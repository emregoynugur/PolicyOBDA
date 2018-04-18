package policy;

import java.util.concurrent.TimeUnit;

import org.apache.jena.query.Query;

public class ActivePolicy {
	private String signature;
	private String addressee;
	private String action;
	private String name;
	private double cost;
	private double deadline;
	private TimeUnit deadlineUnit;
	private Query expiration;
	
	public ActivePolicy(String name, String addressee ,String signature, String action, Query expiration, double cost, double deadline,
			TimeUnit deadlineUnit) {
		super();
		
		this.name = name;
		this.addressee = addressee;
		this.signature = signature;
		this.action = action;
		this.expiration = expiration;
		this.cost = cost;
		this.deadline = deadline;
		this.deadlineUnit = deadlineUnit;
	}
	
	public double getCost() {
		return cost;
	}
	
	public String getAddressee() {
		return addressee;
	}
	
	public String getName() {
		return name;
	}

	public String getSignature() {
		return signature;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Query getExpiration() {
		return expiration;
	}

	public void setExpiration(Query expiration) {
		this.expiration = expiration;
	}

	public double getDeadline() {
		return deadline;
	}

	public void setDeadline(double deadline) {
		this.deadline = deadline;
	}

	public TimeUnit getDeadlineUnit() {
		return deadlineUnit;
	}

	public void setDeadlineUnit(TimeUnit deadlineUnit) {
		this.deadlineUnit = deadlineUnit;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivePolicy other = (ActivePolicy) obj;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((signature == null) ? 0 : signature.hashCode());
		return result;
	}
}
