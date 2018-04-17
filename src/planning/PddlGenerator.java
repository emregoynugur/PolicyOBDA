package planning;

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import planning.parser.Atom;
import planning.parser.LispExprList;
import utils.Config;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class PddlGenerator {

	private OntopOWLReasoner owlReasoner;

	public PddlGenerator(OntopOWLReasoner owlReasoner) {
		this.owlReasoner = owlReasoner;
	}

	private ArrayList<LispExprList> derivedAxioms = new ArrayList<LispExprList>();

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
		 * for (LispExprList action : getActions()) definition.add(action);
		 * 
		 */

		System.out.println(definition);

		return true;
	}

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
