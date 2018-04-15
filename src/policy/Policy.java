package policy;

import java.util.concurrent.TimeUnit;

import org.apache.jena.query.Query;

public class Policy {

	private String name;
	private String modality;
	private String addressee;
	private String addresseeRole;
	private String actionVar;

	private Query actionDescription;
	private Query activation;
	private Query expiration;

	private double cost;
	private double deadline;
	private TimeUnit deadlineUnit;

	public String getName() {
		return name;
	}

	public String getModality() {
		return modality;
	}

	public String getAddressee() {
		return addressee;
	}

	public String getAddresseeRole() {
		return addresseeRole;
	}

	public String getActionVar() {
		return actionVar;
	}

	public Query getActionDescription() {
		return actionDescription;
	}

	public Query getActivation() {
		return activation;
	}

	public double getDeadline() {
		return deadline;
	}

	public Query getExpiration() {
		return expiration;
	}

	public double getCost() {
		return cost;
	}

	public TimeUnit getDeadlineUnit() {
		return deadlineUnit;
	}


	public Policy(String name, String modality, String addressee, String addresseeRole, String actionVar,
			Query actionDescription, Query activation, double cost, double deadline, TimeUnit deadlineUnit,
			Query expiration) {
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
		this.expiration = expiration;

		if (this.expiration != null)
			this.expiration.setQueryAskType();
	}

	@Override
	public String toString() {
		return "Policy [name=" + name + ", modality=" + modality + ", addressee=" + addressee + ", addresseeRole="
				+ addresseeRole + ", actionVar=" + actionVar + ", actionDescription="
				+ actionDescription + ", activation=" + activation + ", expiration=" + expiration + ", cost=" + cost
				+ ", deadline=" + deadline + ", deadlineUnit=" + deadlineUnit + "]";
	}

}
