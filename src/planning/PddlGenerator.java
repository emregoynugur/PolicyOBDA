package planning;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import planning.parser.Atom;
import planning.parser.LispExprList;
import policy.ActivePolicy;
import utils.Config;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class PddlGenerator {

	private OntopOWLReasoner owlReasoner;

	public PddlGenerator(OntopOWLReasoner owlReasoner) {
		this.owlReasoner = owlReasoner;
	}

	private ArrayList<LispExprList> derivedAxioms = new ArrayList<LispExprList>();

	/* Probably do not need all these different maps and sets */
	private HashSet<String> costFunctions = new HashSet<String>();
	private HashMap<String, ArrayList<LispExprList>> indCosts = new HashMap<String, ArrayList<LispExprList>>();
	private HashMap<String, Set<String>> indCostSet = new HashMap<String, Set<String>>();
	private HashMap<Integer, Set<String>> actionProhibitions = new HashMap<Integer, Set<String>>();
	private HashMap<Integer, String> actionNames = new HashMap<Integer, String>();

	public boolean generateDomainFile() {
		LispExprList definition = new LispExprList();
		definition.add(new Atom("define"));

		LispExprList domain = new LispExprList();
		domain.add(new Atom("domain"));
		domain.add(new Atom("iot"));

		LispExprList requirements = new LispExprList();
		requirements.add(new Atom(":requirements"));
		requirements.add(new Atom(":adl"));
		requirements.add(new Atom(":derived-predicates"));

		/* TODO: Data properties */
		definition.add(domain);
		definition.add(requirements);
		definition.add(getPredicates());

		for (LispExprList derived : derivedAxioms)
			definition.add(derived);

		/*
		 * findProhibitedActions();
		 * 
		 * for (LispExprList action : getActions()) definition.add(action);
		 * 
		 * for (LispExprList derived : derivedAxioms) definition.add(derived);
		 */

		System.out.println(definition);

		return true;
	}

	/*
	 * public boolean generateProblemFile() throws OWLOntologyCreationException {
	 * LispExprList definition = new LispExprList(); definition.add(new
	 * Atom("define"));
	 * 
	 * LispExprList problem = new LispExprList(); problem.add(new Atom("problem"));
	 * problem.add(new Atom("iot"));
	 * 
	 * LispExprList domain = new LispExprList(); domain.add(new Atom(":domain"));
	 * domain.add(new Atom("iot"));
	 * 
	 * definition.add(problem); definition.add(domain);
	 * 
	 * definition.add(getObjects()); definition.add(getInitialState());
	 * definition.add(getGoalState()); definition.add(getMinimizeMetric());
	 * 
	 * System.out.println(definition);
	 * 
	 * return true; }
	 * 
	 * private LispExprList getMinimizeMetric() { LispExprList minimize = new
	 * LispExprList(); LispExprList cost = new LispExprList(); cost.add(new
	 * Atom("total-cost"));
	 * 
	 * minimize.add(new Atom(":metric")); minimize.add(new Atom("minimize"));
	 * minimize.add(cost);
	 * 
	 * return minimize; }
	 * 
	 * private LispExprList getGoalState() { LispExprList goal = new LispExprList();
	 * goal.add(new Atom(":goal"));
	 * 
	 * LispExprList preds = new LispExprList(); preds.add(new Atom("and"));
	 * 
	 * PriorityBlockingQueue<ActivePolicy> obligations =
	 * normativeState.getObligations(); ActivePolicy mainGoal = null; try { mainGoal
	 * = obligations.take(); } catch (InterruptedException e) { e.printStackTrace();
	 * }
	 * 
	 * // TODO find a better way! Instances should be stored together like //
	 * prohibitions. ArrayList<ActivePolicy> instances = new
	 * ArrayList<ActivePolicy>(); for (Iterator<ActivePolicy> i =
	 * obligations.iterator(); i.hasNext();) { ActivePolicy next = i.next(); if
	 * (next.getName().equals(mainGoal.getName())) { instances.add(next);
	 * i.remove(); } }
	 * 
	 * if (instances.isEmpty()) { for (LispExprList p :
	 * getGoalStateFromPolicy(mainGoal)) preds.add(p); } else { LispExprList
	 * disjunction = new LispExprList(); disjunction.add(new Atom("or"));
	 * 
	 * LispExprList mainCondition = new LispExprList(); for (LispExprList p :
	 * getGoalStateFromPolicy(mainGoal)) mainCondition.add(p);
	 * 
	 * preds.add(mainCondition);
	 * 
	 * for (ActivePolicy inst : instances) { LispExprList newCondition = new
	 * LispExprList(); for (LispExprList p : getGoalStateFromPolicy(inst))
	 * newCondition.add(p); preds.add(newCondition); } }
	 * 
	 * goal.add(preds); return goal; }
	 * 
	 * private ArrayList<LispExprList> getGoalStateFromPolicy(ActivePolicy policy) {
	 * ArrayList<LispExprList> list = new ArrayList<LispExprList>();
	 * 
	 * List<String> predicates = policy.getGoalState(); for (String p : predicates)
	 * { int start = p.indexOf("(");
	 * 
	 * String pName = p.substring(0, start); String[] pArgs = p.substring(start + 1,
	 * p.length() - 1).split(",");
	 * 
	 * LispExprList predicate = new LispExprList(); predicate.add(new Atom(pName));
	 * for (int i = 0; i < pArgs.length; i++) predicate.add(new Atom(pArgs[i]));
	 * 
	 * list.add(predicate); }
	 * 
	 * return list; }
	 * 
	 * private LispExprList getObjects() { LispExprList objects = new
	 * LispExprList(); objects.add(new Atom(":objects")); List<String> individuals =
	 * Thing.getIndividualFromThing(); for (String ind : individuals) {
	 * objects.add(new Atom(ind.replaceAll("[^A-Za-z0-9 ]", ""))); } return objects;
	 * }
	 * 
	 * private LispExprList getInitialState() { LispExprList initialState = new
	 * LispExprList(); initialState.add(new Atom(":init"));
	 * 
	 * LispExprList isConsistent = new LispExprList(); isConsistent.add(new
	 * Atom("isConsistent"));
	 * 
	 * Set<OWLClass> classes = ontology.getClassesInSignature(); for (OWLClass
	 * owlClass : classes) { // TODO do not insert all individuals into OWLThing if
	 * (owlClass.isOWLThing()) continue;
	 * 
	 * List<String> individuals =
	 * Thing.getIndividualNames(owlClass.getIRI().getShortForm()); for (String ind :
	 * individuals) { LispExprList individual = new LispExprList();
	 * individual.add(new Atom(owlClass.getIRI().getShortForm()));
	 * individual.add(new Atom(ind.replaceAll("[^A-Za-z0-9 ]", "")));
	 * initialState.add(individual); } }
	 * 
	 * Set<OWLObjectProperty> properties =
	 * ontology.getObjectPropertiesInSignature(); for (OWLObjectProperty property :
	 * properties) { List<Object[]> objectProperties =
	 * ObjectProperty.getObjectPropertiesFrom(property.getIRI().getShortForm()); for
	 * (Object[] entry : objectProperties) { LispExprList objProperty = new
	 * LispExprList(); objProperty.add(new Atom(property.getIRI().getShortForm()));
	 * objProperty.add(new Atom(Thing.getIndividualName((Integer)
	 * entry[0]).replaceAll("[^A-Za-z0-9 ]", ""))); objProperty.add(new
	 * Atom(Thing.getIndividualName((Integer) entry[1]).replaceAll("[^A-Za-z0-9 ]",
	 * ""))); initialState.add(objProperty); } }
	 * 
	 * LispExprList initCost = new LispExprList(); LispExprList totalCost = new
	 * LispExprList(); totalCost.add(new Atom("total-cost"));
	 * 
	 * initCost.add(new Atom("=")); initCost.add(totalCost); initCost.add(new
	 * Atom("0"));
	 * 
	 * initialState.add(initCost); for (String key : indCosts.keySet()) {
	 * ArrayList<LispExprList> costList = indCosts.get(key); for (LispExprList cost
	 * : costList) initialState.add(cost); }
	 * 
	 * // TODO this could also be very costly for large intances of data for (String
	 * device : getDeviceNames()) { for (String function : costFunctions) { if
	 * (indCostSet.containsKey(device) &&
	 * (indCostSet.get(device).contains(function))) continue;
	 * 
	 * LispExprList initFunction = new LispExprList(); LispExprList costFunction =
	 * new LispExprList();
	 * 
	 * costFunction.add(new Atom(function)); costFunction.add(new Atom(device));
	 * 
	 * initFunction.add(new Atom("=")); initFunction.add(costFunction);
	 * initFunction.add(new Atom("0"));
	 * 
	 * initialState.add(initFunction); } }
	 * 
	 * return initialState; }
	 * 
	 * 
	 * /* private void findProhibitedActions() { HashMap<String,
	 * HashSet<ActivePolicy>> prohibitions = normativeState.getProhibitions();
	 * 
	 * for (String key : prohibitions.keySet()) { for (ActivePolicy policy :
	 * prohibitions.get(key)) { costFunctions.add(policy.getName());
	 * 
	 * LispExprList costFunction = new LispExprList(); costFunction.add(new
	 * Atom("="));
	 * 
	 * LispExprList pFunction = new LispExprList(); pFunction.add(new
	 * Atom(policy.getName())); pFunction.add(new
	 * Atom(policy.getAddresseeIndividual()));
	 * 
	 * costFunction.add(pFunction); costFunction.add(new
	 * Atom(Double.toString(policy.getCost())));
	 * 
	 * String addressee = policy.getAddresseeIndividual(); if
	 * (!indCosts.containsKey(addressee)) { indCosts.put(addressee, new
	 * ArrayList<LispExprList>()); indCostSet.put(addressee, new HashSet<String>());
	 * } indCosts.get(addressee).add(costFunction);
	 * indCostSet.get(addressee).add(policy.getName());
	 * 
	 * Session session = null; try { session =
	 * HibernateUtil.getSessionFactory().openSession();
	 * 
	 * List<Integer> results =
	 * session.createSQLQuery(policy.getAction().toString()).list(); for (Integer id
	 * : results) { if (!actionProhibitions.containsKey(id))
	 * actionProhibitions.put(id, new HashSet<String>());
	 * actionProhibitions.get(id).add(policy.getName()); } } catch (Exception e) {
	 * System.err.println("SQL Activation Condition Error: " + e.getMessage()); }
	 * finally { if (session != null) session.close(); } } } }
	 * 
	 * private ArrayList<LispExprList> getActions() { ArrayList<LispExprList>
	 * actions = new ArrayList<LispExprList>();
	 * 
	 * actionNames = getActionNames();
	 * 
	 * for (Integer id : actionNames.keySet()) { LispExprList action = new
	 * LispExprList(); action.add(new Atom(":action")); action.add(new
	 * Atom(actionNames.get(id))); action.add(new Atom(":parameters"));
	 * 
	 * List<String> _parameters = null; List<String> _preconditions = null;
	 * List<String> _effects = null; String _subject = null;
	 * 
	 * Session session = null; try { session =
	 * HibernateUtil.getSessionFactory().openSession();
	 * 
	 * _parameters = DataProperty.getDataValues(id, "hasParameter", session);
	 * _preconditions = DataProperty.getDataValues(id, "hasPrecondition", session);
	 * _effects = DataProperty.getDataValues(id, "hasEffect", session); _subject =
	 * DataProperty.getDataValue(id, "hasSubject", session);
	 * 
	 * } catch (Exception e) { System.err.println("SQL getActions Error: " +
	 * e.getMessage()); } finally { if (session != null) session.close(); }
	 * 
	 * LispExprList parameters = new LispExprList(); for (String p : _parameters) {
	 * parameters.add(new Atom(p)); } action.add(parameters);
	 * 
	 * action.add(new Atom(":precondition"));
	 * action.add(mergePredicatesFromStrings(_preconditions));
	 * 
	 * action.add(new Atom(":effect")); LispExprList effects =
	 * mergePredicatesFromStrings(_effects);
	 * 
	 * Set<String> prohibitions = actionProhibitions.get(id); LispExprList totalCost
	 * = new LispExprList(); totalCost.add(new Atom("total-cost")); for (String p :
	 * prohibitions) { LispExprList increase = new LispExprList(); increase.add(new
	 * Atom("increase")); increase.add(totalCost);
	 * 
	 * LispExprList pCost = new LispExprList(); pCost.add(new Atom(p));
	 * pCost.add(new Atom(_subject));
	 * 
	 * increase.add(pCost); effects.add(increase); }
	 * 
	 * action.add(effects); actions.add(action); }
	 * 
	 * System.out.println("All actions");
	 * 
	 * return actions; }
	 
	private LispExprList mergePredicatesFromStrings(List<String> list) {
		LispExprList pddl = new LispExprList();
		pddl.add(new Atom("and"));
		for (String p : list) {
			String[] terms = p.substring(1, p.length() - 1).split(" ");

			LispExprList pred = new LispExprList();
			for (int i = 0; i < terms.length; i++)
				pred.add(new Atom(terms[i]));
			pddl.add(pred);
		}
		return pddl;
	}*/

	private LispExprList getPredicates() {
		LispExprList predicates = new LispExprList();
		predicates.add(new Atom(":predicates"));

		LispExprList isConsistent = new LispExprList();
		isConsistent.add(new Atom("isConsistent"));

		predicates.add(isConsistent);

		Set<OWLClass> classes = owlReasoner.getRootOntology().getClassesInSignature();
		for (OWLClass owlClass : classes) {
			LispExprList classPred = new LispExprList();

			String name = owlClass.getIRI().getShortForm();
			String var = ("" + owlClass.getIRI().getShortForm().charAt(0)).toLowerCase();

			classPred.add(new Atom(name));
			classPred.add(new Atom("?" + var));
			predicates.add(classPred);

			generateDomainAxioms(name, var, "", false);
		}

		Set<OWLObjectProperty> properties = owlReasoner.getRootOntology().getObjectPropertiesInSignature();
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

		/*
		 * if (isProperty) nqs = qr.rewriteQuery(qr.getPropertyQuery(pred, subj, obj));
		 * else nqs = qr.rewriteQuery(qr.getMembershipQuery(pred, subj));
		 */

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

			Set<OWLObjectPropertyExpression> subProps = owlReasoner.getSubObjectProperties(ObjectProperty(IRI.create(iri + pred)), true).getFlattened();

			if (subProps.size() > 1)
				conditions.add(new Atom("or"));

			// TODO: add support for custom rules. i.e. hasSpeaker & hasDisplay ->
			// Television
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
			Set<OWLClass> subClasses = owlReasoner.getSubClasses(Class(IRI.create(iri + pred)), true).getFlattened();

			if (subClasses.size() > 1)
				conditions.add(new Atom("or"));

			// TODO: add support for custom rules. i.e. hasSpeaker & hasDisplay ->
			// Television
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
}
