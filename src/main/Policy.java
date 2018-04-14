package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;

import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;

public class Policy {

	private String name;
	private String modality;
	private String addressee;
	private String addresseeRole;
	private String actionVar;
	private String goal;

	private Query actionDescription;
	private Query activation;
	private Query expiration;

	private double cost;
	private double deadline;
	private TimeUnit deadlineUnit;

	private List<ActivePolicy> instances = new ArrayList<ActivePolicy>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public String getAddresseeRole() {
		return addresseeRole;
	}

	public void setAddresseeRole(String addresseeRole) {
		this.addresseeRole = addresseeRole;
	}

	public String getActionVar() {
		return actionVar;
	}

	public void setActionVar(String actionVar) {
		this.actionVar = actionVar;
	}

	public Query getActionDescription() {
		return actionDescription;
	}

	public void setActionDescription(Query actionDescription) {
		this.actionDescription = actionDescription;
	}

	public Query getActivation() {
		return activation;
	}

	public void setActivation(Query activation) {
		this.activation = activation;
	}

	public double getDeadline() {
		return deadline;
	}

	public void setDeadline(double deadline) {
		this.deadline = deadline;
	}

	public Query getExpiration() {
		return expiration;
	}

	public void setExpiration(Query expiration) {
		this.expiration = expiration;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public TimeUnit getDeadlineUnit() {
		return deadlineUnit;
	}

	public void setDeadlineUnit(TimeUnit deadlineUnit) {
		this.deadlineUnit = deadlineUnit;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public Policy(String name, String modality, String addressee, String addresseeRole, String actionVar,
			Query actionDescription, Query activation, double cost, double deadline, TimeUnit deadlineUnit,
			Query expiration, String goal) {
		super();
		this.name = name;
		this.modality = modality;
		this.addressee = addressee;
		this.addresseeRole = addresseeRole;
		this.actionVar = actionVar;
		this.actionDescription = actionDescription;
		this.activation = activation;
		this.cost = cost;
		this.deadline = deadline;
		this.deadlineUnit = deadlineUnit;
		this.expiration = expiration;
		this.goal = goal;
		this.expiration = expiration;

		if (this.expiration != null)
			this.expiration.setQueryAskType();
	}

	public void createInstancesWithBindings(TupleOWLResultSet rs) throws OWLException {
		List<String> resultVars = activation.getResultVars();
		ParameterizedSparqlString pss = new ParameterizedSparqlString();

		if (expiration != null)
			pss.setCommandText(expiration.toString());

		while (rs.hasNext()) {
			OWLBindingSet result = rs.next();

			HashMap<String, String> bindings = new HashMap<String, String>();
			String activeGoal = goal;

			for (String v : resultVars) {
				OWLObject binding = result.getOWLObject(v);
				String iri = binding.toString();

				if (iri.charAt(0) == '<')
					iri = iri.substring(1, iri.length() - 1);

				activeGoal = activeGoal.replace("?" + v, binding.toString());

				if (expiration != null) {
					if (binding instanceof OWLLiteral)
						pss.setLiteral(v, ((OWLLiteral) binding).getLiteral());
					else
						pss.setIri(v, iri);
				}

				bindings.put(v, iri);
			}

			String expQuery = null;
			if (expiration != null)
				expQuery = pss.asQuery().toString();

			instances.add(new ActivePolicy(name, activeGoal, actionDescription.toString(), expQuery, deadline,
					deadlineUnit, bindings));
		}
	}

	public List<ActivePolicy> getInstances() {
		return instances;
	}

	public void setInstances(List<ActivePolicy> instances) {
		this.instances = instances;
	}

}
