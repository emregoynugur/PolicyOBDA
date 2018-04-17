package policy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.xml.sax.SAXException;

import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLStatement;
import it.unibz.inf.ontop.owlapi.impl.QuestOWL;
import it.unibz.inf.ontop.owlapi.impl.QuestOWLFactory;
import it.unibz.inf.ontop.owlapi.resultset.BooleanOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import it.unibz.inf.ontop.si.OntopSemanticIndexLoader;
import it.unibz.inf.ontop.si.SemanticIndexException;
import utils.Config;

public class PolicyReasoner {

	private String ontologyIri = Config.getInstance().getOntologyIri();

	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLOntology ontology = null;
	private OWLDataFactory factory = manager.getOWLDataFactory();

	private int indCounter = 0;

	private boolean checkModalityConflict(Policy a, Policy b) {
		/* Goal based policies also make modality check easier */
		if ((a.getModality().contains("Obligation") && b.getModality().contains("Prohibition"))
				|| (b.getModality().contains("Obligation") && a.getModality().contains("Prohibition"))) {
			return true;
		}

		return false;
	}

	private Set<OWLAxiom> freezeQuery(Query query, Map<String, OWLNamedIndividual> cache) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		ElementTriplesBlock conditions = (ElementTriplesBlock) query.getQueryPattern();
		for (Iterator<Triple> it = conditions.patternElts(); it.hasNext();) {
			Triple condition = it.next();

			String predicate = condition.getPredicate().toString();
			String subject = condition.getSubject().toString();
			String object = condition.getObject().toString();

			// TODO: implement individuals in policy descriptions
			OWLNamedIndividual subjectInd = getOrCreateIndividual(subject, cache);

			if (predicate.contains("#type")) {
				OWLClass owlClass = factory.getOWLClass(IRI.create(object));
				OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(owlClass, subjectInd);
				manager.addAxiom(ontology, axiom);
				axioms.add(axiom);
			} else {
				if (object.charAt(0) == '?') {
					OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(predicate));
					OWLIndividual objectInd = getOrCreateIndividual(object, cache);
					OWLObjectPropertyAssertionAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(property,
							subjectInd, objectInd);
					manager.addAxiom(ontology, axiom);
					axioms.add(axiom);
				} else {
					// TODO: Data Property
				}
			}
		}
		return axioms;
	}

	private void loadOntology() {
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File(Config.getInstance().getOntologyFile()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private QuestOWL startQuestReasoner()
			throws SemanticIndexException, IllegalConfigurationException, OWLOntologyCreationException {
		QuestOWL reasoner = null;
		Properties properties = new Properties();
		try (OntopSemanticIndexLoader siLoader = OntopSemanticIndexLoader.loadOntologyIndividuals(ontology,
				properties)) {
			reasoner = (QuestOWL) new QuestOWLFactory().createReasoner(siLoader.getConfiguration());
		}
		return reasoner;
	}

	public boolean checkConflict(Policy pA, Policy pB)
			throws OWLException, IllegalConfigurationException, SemanticIndexException {

		if (!checkModalityConflict(pA, pB))
			return false;

		loadOntology();

		indCounter = 0;
		HashMap<String, OWLNamedIndividual> varIndMap = new HashMap<String, OWLNamedIndividual>();

		Policy p1 = null;
		Policy p2 = null;

		Set<OWLAxiom> createdAxioms = new HashSet<OWLAxiom>();
		List<Map<String, OWLNamedIndividual>> results = new ArrayList<Map<String, OWLNamedIndividual>>();
		for (int i = 0; i < 2; i++) {
			p1 = i == 0 ? pA : pB;
			p2 = i == 0 ? pB : pA;

			if (i == 1) {
				manager.removeAxioms(ontology, createdAxioms);
			}

			createdAxioms.addAll(freezeQuery(p1.getActivation(), varIndMap));
			createdAxioms.addAll(freezeQuery(p1.getActionDescription(), varIndMap));

			QuestOWL reasoner = startQuestReasoner();

			try (OntopOWLConnection conn = reasoner.getConnection();
					OntopOWLStatement st = conn.createStatement();
					TupleOWLResultSet rs = st.executeSelectQuery(p2.getActionDescription().toString());) {

				while (rs.hasNext()) {
					OWLBindingSet result = rs.next();
					HashMap<String, OWLNamedIndividual> map = new HashMap<String, OWLNamedIndividual>();
					for (String var : p2.getActionDescription().getResultVars()) {
						OWLNamedIndividual binding = (OWLNamedIndividual) result.getOWLObject(var);
						map.put("?" + var, binding);
					}
					results.add(map);
				}
			} catch (Exception e) {
				reasoner.close();
				System.err.println("PolicyReasoner Conflict Detection SQL Error: " + e.getMessage());
				return false;
			}
			
			reasoner.close();

			if (!results.isEmpty())
				break;
		}

		if (results == null || results.isEmpty())
			return false;

		OWLNamedIndividual addressee = varIndMap.get(p1.getAddressee());
		for (Map<String, OWLNamedIndividual> result : results) {

			if (result.containsKey(p2.getAddressee())
					&& !result.get(p2.getAddressee()).toString().equals(addressee.toString()))
				continue;

			result.put(p2.getAddressee(), addressee);
			freezeQuery(p2.getActivation(), result);

			QuestOWL reasoner = startQuestReasoner();

			HashSet<ActivePolicy> activeA = createActivePolicies(p1, reasoner);
			HashSet<ActivePolicy> activeB = createActivePolicies(p2, reasoner);

			removeExpiredPolicies(activeA, reasoner);
			removeExpiredPolicies(activeB, reasoner);
			
			reasoner.close();

			if (activeA.size() == 0 || activeB.size() == 0)
				continue;

			if (reasoner.isConsistent()) {
				return true;
			}
		}
		
		return false;
	}

	public HashSet<ActivePolicy> createActivePolicies(Policy p, OntopOWLReasoner reasoner) throws OWLException {
		HashSet<ActivePolicy> instances = new HashSet<>();

		try (OntopOWLConnection conn = reasoner.getConnection();
				OntopOWLStatement st = conn.createStatement();
				TupleOWLResultSet rs = st.executeSelectQuery(p.getActivation().toString());) {
			
			if (!rs.hasNext())
				return instances;

			ParameterizedSparqlString pss = new ParameterizedSparqlString();
			if (p.getExpiration() != null)
				pss.setCommandText(p.getExpiration().toString());

			List<String> resultVars = p.getActivation().getResultVars();
			while (rs.hasNext()) {
				OWLBindingSet result = rs.next();

				String signature = p.getName();
				for (String v : resultVars) {
					OWLObject binding = result.getOWLObject(v);
					String iri = binding.toString();

					if (iri.charAt(0) == '<')
						iri = iri.substring(1, iri.length() - 1);

					if (p.getExpiration() != null) {
						if (binding instanceof OWLLiteral)
							pss.setLiteral(v, ((OWLLiteral) binding).getLiteral());
						else
							pss.setIri(v, iri);
					}

					signature += ";" + v + "," + iri.substring(iri.indexOf("#") + 1);
				}

				String expQuery = null;
				if (p.getExpiration() != null)
					expQuery = pss.asQuery().toString();

				instances.add(new ActivePolicy(signature, p.getActionDescription().toString(), expQuery,
						p.getDeadline(), p.getDeadlineUnit()));
			}
		}
		return instances;
	}

	public void removeExpiredPolicies(HashSet<ActivePolicy> policies, OntopOWLReasoner reasoner) throws OWLException {
		try (OntopOWLConnection conn = reasoner.getConnection(); OntopOWLStatement st = conn.createStatement();) {
			for (Iterator<ActivePolicy> curr = policies.iterator(); curr.hasNext();) {
				ActivePolicy p = curr.next();
				// TODO: Check deadline
				if (p.getExpiration() != null) {
					try (BooleanOWLResultSet res = st.executeAskQuery(p.getExpiration())) {
						if (res.getValue())
							curr.remove();
					}
				}
			}
		}
	}

	private OWLNamedIndividual getOrCreateIndividual(String var, Map<String, OWLNamedIndividual> cache) {
		if (cache.containsKey(var))
			return cache.get(var);
		OWLNamedIndividual ind = factory
				.getOWLNamedIndividual(IRI.create(ontologyIri + "ind" + Integer.toString(indCounter++)));
		cache.put(var, ind);
		return ind;
	}

	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, OWLException,
			IllegalConfigurationException, SemanticIndexException {

		PolicyReader reader = new PolicyReader();
		ArrayList<Policy> readPolicies = reader.readPolicies();

		PolicyReasoner r = new PolicyReasoner();
		System.out.println(r.checkConflict(readPolicies.get(0), readPolicies.get(1)));
	}
}
