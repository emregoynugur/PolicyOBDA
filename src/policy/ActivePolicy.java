package policy;

import java.util.concurrent.TimeUnit;

public class ActivePolicy {
	private String signature;
	private String action;
	private String expiration;
	private double deadline;
	private TimeUnit deadlineUnit;

	public ActivePolicy(String signature, String action, String expiration, double deadline,
			TimeUnit deadlineUnit) {
		super();
		
		this.signature = signature;
		this.action = action;
		this.expiration = expiration;
		this.deadline = deadline;
		this.deadlineUnit = deadlineUnit;
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

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
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
