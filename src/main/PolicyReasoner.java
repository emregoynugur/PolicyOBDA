package main;

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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.xml.sax.SAXException;


import it.unibz.inf.ontop.owlapi.connection.OntopOWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLStatement;
import it.unibz.inf.ontop.owlapi.impl.QuestOWL;
import it.unibz.inf.ontop.owlapi.impl.QuestOWLFactory;
import it.unibz.inf.ontop.owlapi.resultset.BooleanOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import it.unibz.inf.ontop.si.OntopSemanticIndexLoader;
import it.unibz.inf.ontop.si.SemanticIndexException;



public class PolicyReasoner {
	private final String owlFile = System.getProperty("user.dir") + "/resources/use_cases/smart_home/sspn-ql-rdf.owl";

	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLOntology ontology = null;
	private OWLDataFactory factory = manager.getOWLDataFactory();
	
	QuestOWL reasoner = null;


	// private String ontologyIRI =
	// "https://faculty.ozyegin.edu.tr/muratsensoy/mine-ontology#";
	private final static String ontologyIRI = "http://cs.ozyegin.edu.tr/muratsensoy/2015/03/sspn-ql#";

	private static int indCounter = 0;

	private String getIndividual() {
		return "ind" + Integer.toString(indCounter++);
	}

	private void resetIndividuals() {
		indCounter = 0;
	}

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

			// Assuming individual is a variable. TODO support individuals in policy
			// descriptions
			OWLNamedIndividual subjectInd = getOrCreateIndividual(subject, cache);

			if (predicate.contains("#type")) {
				OWLClass owlClass = factory.getOWLClass(IRI.create(object));
				OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(owlClass, subjectInd);
				//manager.applyChange(new AddAxiom(ontology, axiom));
				manager.addAxiom(ontology, axiom);
				axioms.add(axiom);
			} else {
				if (object.charAt(0) == '?') {
					OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(predicate));
					OWLIndividual objectInd = getOrCreateIndividual(object, cache);
					OWLObjectPropertyAssertionAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(property,
							subjectInd, objectInd);
					//manager.applyChange(new AddAxiom(ontology, axiom));
					manager.addAxiom(ontology, axiom);
					axioms.add(axiom);
				} else {
					// Data Property
				}
			}
		}
		return axioms;
	}

	private void loadOntology() {
		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File(owlFile));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void startReasoner() throws SemanticIndexException, IllegalConfigurationException, OWLOntologyCreationException {
		Properties properties = new Properties();
		try (OntopSemanticIndexLoader siLoader = OntopSemanticIndexLoader.loadOntologyIndividuals(ontology, properties)) {
			QuestOWLFactory questFactory = new QuestOWLFactory();
			reasoner = (QuestOWL) questFactory.createReasoner(siLoader.getConfiguration());
		}
	}

	public boolean checkConflict(Policy pA, Policy pB) throws OWLException, IllegalConfigurationException, SemanticIndexException {

		if (!checkModalityConflict(pA, pB))
			return false;

		resetIndividuals();

		loadOntology();

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

			startReasoner();
			OntopOWLConnection conn = reasoner.getConnection();
			OntopOWLStatement st = conn.createStatement();
			
			String prefix = "PREFIX : <http://cs.ozyegin.edu.tr/muratsensoy/2015/03/sspn-ql#> \n PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";

			try {
				TupleOWLResultSet rs = st.executeSelectQuery(p2.getActionDescription().toString());
				while (rs.hasNext()) {
					OWLBindingSet result = rs.next();
					HashMap<String, OWLNamedIndividual> map = new HashMap<String, OWLNamedIndividual>();
					for (Object var : p2.getActionDescription().getResultVars()) {
						System.out.println(var);
						//OWLNamedIndividual binding = (OWLNamedIndividual) result.getOWLObject(var);
						//map.put("?" + var, binding);
					}
					results.add(map);
				}
				rs.close();
			} catch (Exception e) {
				System.err.println("PolicyReasoner Conflict Detection SQL Error: " + e.getMessage());
				return false;
			}

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

			startReasoner();
			OntopOWLConnection conn = reasoner.getConnection();
			OntopOWLStatement st = conn.createStatement();

			TupleOWLResultSet rs = st.executeSelectQuery(p1.getActivation().toString());
			p1.createInstancesWithBindings(rs);
			rs.close();

			HashSet<ActivePolicy> activeA = new HashSet<ActivePolicy>(p1.getInstances());

			rs = st.executeSelectQuery(p2.getActivation().toString());
			p2.createInstancesWithBindings(rs);
			rs.close();

			HashSet<ActivePolicy> activeB = new HashSet<ActivePolicy>(p2.getInstances());

			// check if policies expire
			for (Iterator<ActivePolicy> curr = activeA.iterator(); curr.hasNext();) {
				ActivePolicy p = curr.next();
				// Check deadline too?
				BooleanOWLResultSet res = st.executeAskQuery(p.getExpiration());
				if (res.getValue())
					curr.remove();
			}

			for (Iterator<ActivePolicy> curr = activeB.iterator(); curr.hasNext();) {
				ActivePolicy p = curr.next();
				// Check deadline too?
				BooleanOWLResultSet res = st.executeAskQuery(p.getExpiration());
				if (res.getValue())
					curr.remove();
			}

			// Does not conflict
			if (activeA.size() == 0 || activeB.size() == 0)
				continue;

			if (reasoner.isConsistent()) {
				return true;
			}
		}

		return false;
	}

	private OWLNamedIndividual getOrCreateIndividual(String var, Map<String, OWLNamedIndividual> cache) {
		if (cache.containsKey(var))
			return cache.get(var);
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + getIndividual()));
		cache.put(var, ind);
		return ind;
	}

	public static void main(String[] args)
			throws ParserConfigurationException, IOException, SAXException, OWLException, IllegalConfigurationException, SemanticIndexException {
		PolicyReader reader = new PolicyReader();
		ArrayList<Policy> readPolicies = reader.readPolicies();

		PolicyReasoner r = new PolicyReasoner();
		System.out.println(r.checkConflict(readPolicies.get(0), readPolicies.get(1)));

		System.out.println(readPolicies);
	}
}
