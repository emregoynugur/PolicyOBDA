package main;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class ActivePolicy {
	private String name;
	private String goalState;
	private String action;
	private String expiration;
	private double deadline;
	private TimeUnit deadlineUnit;

	private HashMap<String, String> bindings;
	
	public ActivePolicy(String name, String goalState, String action, String expiration, double deadline,
			TimeUnit deadlineUnit, HashMap<String, String> bindings) {
		super();
		this.name = name;
		this.goalState = goalState;
		this.action = action;
		this.expiration = expiration;
		this.deadline = deadline;
		this.deadlineUnit = deadlineUnit;
		this.bindings = bindings;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGoalState() {
		return goalState;
	}

	public void setGoalState(String goalState) {
		this.goalState = goalState;
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

	public HashMap<String, String> getBindings() {
		return bindings;
	}

	public void setBindings(HashMap<String, String> bindings) {
		this.bindings = bindings;
	}

}
