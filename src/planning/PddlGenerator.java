package planning;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import it.unibz.inf.ontop.owlapi.connection.OntopOWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLStatement;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import planning.parser.Atom;
import planning.parser.LispExprList;
import policy.ActivePolicy;
import policy.PolicyManager;
import utils.Config;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class PddlGenerator {

	private PolicyManager manager;

	public PddlGenerator(PolicyManager manager) {
		this.manager = manager;
	}

	private ArrayList<LispExprList> derivedAxioms = new ArrayList<LispExprList>();

	public boolean generateDomainFile(List<LispExprList> actions) throws IOException {
		LispExprList definition = new LispExprList();
		definition.add(new Atom("define"));

		LispExprList domain = new LispExprList();
		domain.add(new Atom("domain"));
		domain.add(new Atom("iot"));

		LispExprList requirements = new LispExprList();
		requirements.add(new Atom(":requirements"));
		requirements.add(new Atom(":adl"));
		requirements.add(new Atom(":derived-predicates"));
		requirements.add(new Atom(":action-costs"));

		/* TODO: Data properties */
		definition.add(domain);
		definition.add(requirements);
		definition.add(getPredicates());
		definition.add(getFunctions());

		for (LispExprList action : actions)
			definition.add(action);

		for (LispExprList derived : derivedAxioms)
			definition.add(derived);

		Files.write(Paths.get(Config.getInstance().getPlannerDomain()), definition.toString().getBytes());

		return true;
	}

	private LispExprList getFunctions() {
		LispExprList functions = new LispExprList();
		functions.add(new Atom(":functions"));

		LispExprList totalcost = new LispExprList();
		totalcost.add(new Atom("total-cost"));

		functions.add(totalcost);

		HashMap<String, HashSet<ActivePolicy>> prohibitions = manager.getProhibitions();
		for (String key : prohibitions.keySet()) {
			ActivePolicy policy = prohibitions.get(key).iterator().next();

			LispExprList cost = new LispExprList();
			cost.add(new Atom(policy.getName()));
			cost.add(new Atom("?device"));

			functions.add(cost);
		}

		return functions;
	}

	private LispExprList getPredicates() {
		LispExprList predicates = new LispExprList();
		predicates.add(new Atom(":predicates"));

		Set<OWLClass> classes = manager.getOWLReasoner().getRootOntology().getClassesInSignature();
		for (OWLClass owlClass : classes) {
			LispExprList classPred = new LispExprList();

			String name = owlClass.getIRI().getShortForm();
			String var = ("" + owlClass.getIRI().getShortForm().charAt(0)).toLowerCase();

			classPred.add(new Atom(name));
			classPred.add(new Atom("?" + var));
			predicates.add(classPred);

			generateDomainAxioms(name, var, "", false);
		}

		Set<OWLObjectProperty> properties = manager.getOWLReasoner().getRootOntology().getObjectPropertiesInSignature();
		for (OWLObjectProperty property : properties) {
			LispExprList propPred = new LispExprList();
			propPred.add(new Atom(property.getIRI().getShortForm()));
			propPred.add(new Atom("?s"));
			propPred.add(new Atom("?o"));
			predicates.add(propPred);

			generateDomainAxioms(property.getIRI().getShortForm(), "s", "o", true);
		}

		return predicates;
	}

	private void generateDomainAxioms(String pred, String subj, String obj, boolean isProperty) {

		String iri = Config.getInstance().getOntologyIri();

		LispExprList derived = new LispExprList();
		derived.add(new Atom(":derived"));

		LispExprList axiom = new LispExprList();
		axiom.add(new Atom(pred));
		axiom.add(new Atom("?" + subj));

		if (isProperty)
			axiom.add(new Atom("?" + obj));

		derived.add(axiom);

		LispExprList conditions = new LispExprList();

		if (isProperty) {

			Set<OWLObjectPropertyExpression> subProps = manager.getOWLReasoner()
					.getSubObjectProperties(ObjectProperty(IRI.create(iri + pred)), true).getFlattened();

			if (subProps.size() > 1)
				conditions.add(new Atom("or"));

			// TODO: add support for custom rules.
			// i.e. hasSpeaker & hasDisplay -> Television
			for (OWLObjectPropertyExpression prop : subProps) {
				LispExprList rule = new LispExprList();

				if (prop.isOWLBottomObjectProperty())
					break;

				rule.add(new Atom(prop.getNamedProperty().getIRI().getShortForm().toString()));
				rule.add(new Atom("?" + subj));
				rule.add(new Atom("?" + obj));

				if (subProps.size() > 1)
					conditions.add(rule);
				else
					conditions = rule;
			}

		} else {
			Set<OWLClass> subClasses = manager.getOWLReasoner().getSubClasses(Class(IRI.create(iri + pred)), true)
					.getFlattened();

			if (subClasses.size() > 1)
				conditions.add(new Atom("or"));

			// TODO: add support for custom rules.
			// i.e. hasSpeaker & hasDisplay -> Television
			for (OWLClass subClass : subClasses) {
				LispExprList rule = new LispExprList();

				if (subClass.isBottomEntity())
					break;

				rule.add(new Atom(subClass.getIRI().getShortForm().toString()));
				rule.add(new Atom("?" + subj));

				if (subClasses.size() > 1)
					conditions.add(rule);
				else
					conditions = rule;
			}
		}

		derived.add(conditions);

		if (conditions.size() > 0)
			derivedAxioms.add(derived);
	}

	public boolean generateProblemFile(HashSet<ActivePolicy> obligations)
			throws ReasonerInternalException, OWLException, IOException {
		LispExprList definition = new LispExprList();
		definition.add(new Atom("define"));

		LispExprList problem = new LispExprList();
		problem.add(new Atom("problem"));
		problem.add(new Atom("iot"));

		LispExprList domain = new LispExprList();
		domain.add(new Atom(":domain"));
		domain.add(new Atom("iot"));

		definition.add(problem);
		definition.add(domain);

		addObjectsAndInitialState(definition);

		definition.add(getGoalState(obligations));
		definition.add(getMinimizeMetric());

		Files.write(Paths.get(Config.getInstance().getPlannerProblem()), definition.toString().getBytes());

		return true;
	}

	private LispExprList getGoalState(HashSet<ActivePolicy> obligations) {
		LispExprList goal = new LispExprList();
		goal.add(new Atom(":goal"));

		List<LispExprList> conditions = new ArrayList<>();
		for (ActivePolicy obligation : obligations) {

			LispExprList cond = new LispExprList();
			cond.add(new Atom("and"));

			// TODO: watch out for unbound variables in the expiration query
			// TODO: below implementation is not robust
			ElementGroup query = (ElementGroup) obligation.getExpiration().getQueryPattern();
			for (Element elem : query.getElements()) {
				TriplePath triple = ((ElementPathBlock) elem).getPattern().getList().get(0);

				LispExprList pred = new LispExprList();

				if (triple.getPredicate().getLocalName().contains("type")
						|| triple.getPredicate().getLocalName().contains("a")) {
					pred.add(new Atom(triple.getObject().getLocalName()));
					pred.add(new Atom(triple.getSubject().getLocalName()));
				} else {
					pred.add(new Atom(triple.getPredicate().getLocalName()));
					pred.add(new Atom(triple.getSubject().getLocalName()));
					pred.add(new Atom(triple.getObject().getLocalName()));
				}

				cond.add(pred);
			}

			conditions.add(cond);
		}

		if (obligations.size() > 1) {
			LispExprList preds = new LispExprList();
			preds.add(new Atom("or"));
			for (LispExprList cond : conditions)
				preds.add(cond);
			goal.add(preds);
		} else {
			goal.add(conditions.get(0));
		}

		return goal;
	}

	private LispExprList addObjectsAndInitialState(LispExprList definition)
			throws ReasonerInternalException, OWLException {

		LispExprList initialState = new LispExprList();
		initialState.add(new Atom(":init"));

		LispExprList isConsistent = new LispExprList();
		isConsistent.add(new Atom("isConsistent"));

		try (OntopOWLConnection conn = manager.getOWLReasoner().getConnection();
				OntopOWLStatement st = conn.createStatement();) {

			HashSet<String> individuals = new HashSet<>();
			Set<OWLClass> classes = manager.getOWLReasoner().getRootOntology().getClassesInSignature();
			for (OWLClass owlClass : classes) {

				if (owlClass.isOWLThing())
					continue;

				// TODO: if possible, use Ontop API to get individuals without query re-writing.
				// i.e. use derived-predicates for inference.
				String q = "SELECT DISTINCT ?i WHERE { ?i a <" + owlClass.getIRI() + "> }";
				try (TupleOWLResultSet rs = st.executeSelectQuery(q);) {

					while (rs.hasNext()) {
						OWLBindingSet result = rs.next();

						String iri = ((OWLNamedIndividual) result.getOWLObject("i")).getIRI().getShortForm();
						individuals.add(iri);

						LispExprList individual = new LispExprList();
						individual.add(new Atom(owlClass.getIRI().getShortForm()));
						individual.add(new Atom(iri));

						initialState.add(individual);
					}

				}
			}

			Set<OWLObjectProperty> properties = manager.getOWLReasoner().getRootOntology()
					.getObjectPropertiesInSignature();
			for (OWLObjectProperty property : properties) {
				String q = "SELECT DISTINCT ?s ?o WHERE { ?s <" + property.getIRI() + "> ?o }";
				try (TupleOWLResultSet rs = st.executeSelectQuery(q);) {

					while (rs.hasNext()) {

						OWLBindingSet result = rs.next();

						String subj = ((OWLNamedIndividual) result.getOWLObject("s")).getIRI().getShortForm();
						String obj = ((OWLNamedIndividual) result.getOWLObject("o")).getIRI().getShortForm();

						LispExprList objProperty = new LispExprList();
						objProperty.add(new Atom(property.getIRI().getShortForm()));
						objProperty.add(new Atom(subj));
						objProperty.add(new Atom(obj));

						individuals.add(subj);
						individuals.add(obj);

						initialState.add(objProperty);
					}
				}
			}

			definition.add(getObjects(individuals));

			LispExprList initCost = new LispExprList();
			LispExprList totalCost = new LispExprList();
			totalCost.add(new Atom("total-cost"));
			initCost.add(new Atom("="));
			initCost.add(totalCost);
			initCost.add(new Atom("0"));

			initialState.add(initCost);

			HashMap<String, HashSet<ActivePolicy>> prohibitions = manager.getProhibitions();
			for (String key : prohibitions.keySet()) {
				ActivePolicy prohibition = prohibitions.get(key).iterator().next();

				LispExprList initFunction = new LispExprList();
				LispExprList costFunction = new LispExprList();

				costFunction.add(new Atom(prohibition.getName()));
				costFunction.add(new Atom(prohibition.getAddressee()));

				initFunction.add(new Atom("="));
				initFunction.add(costFunction);
				initFunction.add(new Atom(Integer.toString((int) prohibition.getCost())));

				initialState.add(initFunction);
			}
		}

		definition.add(initialState);

		return initialState;
	}

	private LispExprList getObjects(Set<String> individuals) {
		LispExprList objects = new LispExprList();
		objects.add(new Atom(":objects"));
		for (String ind : individuals) {
			objects.add(new Atom(ind.replaceAll("[^A-Za-z0-9 ]", "")));
		}
		return objects;
	}

	private LispExprList getMinimizeMetric() {
		LispExprList minimize = new LispExprList();
		LispExprList cost = new LispExprList();
		cost.add(new Atom("total-cost"));

		minimize.add(new Atom(":metric"));
		minimize.add(new Atom("minimize"));
		minimize.add(cost);

		return minimize;
	}
}
