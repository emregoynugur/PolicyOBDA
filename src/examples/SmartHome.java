package examples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.h2.tools.RunScript;

import ch.qos.logback.classic.Level;
import planning.parser.Atom;
import planning.parser.LispExprList;
import policy.ActivePolicy;
import policy.Policy;
import policy.PolicyManager;
import utils.Config;

public class SmartHome {
	
	public static void prepareH2Database() throws ClassNotFoundException, SQLException, FileNotFoundException {
		String driver = "org.h2.Driver";
		String dbConnection = "jdbc:h2:mem:smart_home;DB_CLOSE_DELAY=-1;";
		String user = "iot";
		String password = "iot";
		
		Class.forName(driver);
		
		try (Connection connection = DriverManager.getConnection(dbConnection, user, password);) {
			RunScript.execute(connection, new FileReader(Config.getInstance().getProperty("smartHomeDB")));
		}
	}
	// TODO: actions are currently hardcoded.
	public static List<LispExprList> getPDDLActions() {
		List<LispExprList> actions = new ArrayList<>();
		
		actions.add(getLocateAction());
		actions.add(getSoundAction());
		actions.add(getVisualAction());
		
		return actions;
	}
	
	private static LispExprList getTypePred(String type, String name) {
		LispExprList pred = new LispExprList();
		pred.add(new Atom(type));
		pred.add(new Atom(name));
		return pred;
	}
	
	private static LispExprList getObjectPred(String rel, String subj, String obj) {
		LispExprList pred = getTypePred(rel, subj);
		pred.add(new Atom(obj));
		return pred;
	}
	
	/* This method creates the PDDL definition of the locate-people action 
		(:action locate-people 
			:parameters (?person ?room)
			:precondition (and (Room ?room) (Person ?person)) 
 			:effect (and (inRoom ?person ?room) (increase (total-cost) 1))) */ 
	
	private static LispExprList getLocateAction() {
		/* Locate People */
		LispExprList locate = new LispExprList();
		locate.add(new Atom(":action locate-people"));
		
		locate.add(new Atom(":parameters"));
		LispExprList parameters = new LispExprList();
		parameters.add(new Atom("?person"));
		parameters.add(new Atom("?room"));
		
		locate.add(parameters);
		
		locate.add(new Atom(":precondition"));
		LispExprList preconditions = new LispExprList();
		preconditions.add(new Atom("and"));
		preconditions.add(getTypePred("Room", "?room"));
		preconditions.add(getTypePred("Person", "?person"));
		
		locate.add(preconditions);

		locate.add(new Atom(":effect"));
		LispExprList effects = new LispExprList();
		effects.add(new Atom("and"));
		effects.add(getObjectPred("inRoom", "?person", "?room"));
		effects.add(getObjectPred("increase", "(total-cost)", "1"));
		
		locate.add(effects);
		
		return locate;
	}
	
	/* This method creates the PDDL definition of the notify-with-sound action 
	(:action  notify-with-sound
		:parameters (?person ?room)
		:precondition (?person ?event ?device) 
		:effect (and (gotNotifiedFor ?person ?event) (increase (total-cost) (SleepingBaby ?device)))) */
	
	private static LispExprList getSoundAction() {
		/* Locate People */
		LispExprList notify = new LispExprList();
		notify.add(new Atom(":action notify-with-sound"));
		
		notify.add(new Atom(":parameters"));
		LispExprList parameters = new LispExprList();
		parameters.add(new Atom("?person"));
		parameters.add(new Atom("?event"));
		parameters.add(new Atom("?device"));
		
		notify.add(parameters);
		
		notify.add(new Atom(":precondition"));
		LispExprList preconditions = new LispExprList();
		preconditions.add(new Atom("and"));
		preconditions.add(getTypePred("Person", "?person"));
		preconditions.add(getTypePred("Doorbell", "?device"));

		
		notify.add(preconditions);

		notify.add(new Atom(":effect"));
		LispExprList effects = new LispExprList();
		effects.add(new Atom("and"));
		effects.add(getObjectPred("gotNotifiedFor", "?person", "?event"));
		effects.add(getObjectPred("increase", "(total-cost)", "(SleepingBaby ?device)"));
		
		notify.add(effects);
		
		return notify;
	}
	
	/* This method creates the PDDL definition of the notify-with-visual action 
	(:action  notify-with-visual
		:parameters (?person ?room)
		:precondition (and (Person ?person) (Television ?device) (inRoom ?device ?room) (inRoom ?person ?room)) 
		:effect (and (gotNotifiedFor ?person ?event) (increase (total-cost) 1))) */
	
	private static LispExprList getVisualAction() {
		/* Locate People */
		LispExprList notify = new LispExprList();
		notify.add(new Atom(":action notify-with-visual"));
		
		notify.add(new Atom(":parameters"));
		LispExprList parameters = new LispExprList();
		parameters.add(new Atom("?person"));
		parameters.add(new Atom("?event"));
		parameters.add(new Atom("?device"));
		parameters.add(new Atom("?room"));
		
		notify.add(parameters);
		
		notify.add(new Atom(":precondition"));
		LispExprList preconditions = new LispExprList();
		preconditions.add(new Atom("and"));
		preconditions.add(getTypePred("Person", "?person"));
		preconditions.add(getTypePred("Television", "?device"));
		preconditions.add(getObjectPred("inRoom", "?device", "?room"));
		preconditions.add(getObjectPred("inRoom", "?person", "?room"));
		
		notify.add(preconditions);

		notify.add(new Atom(":effect"));
		LispExprList effects = new LispExprList();
		effects.add(new Atom("and"));
		effects.add(getObjectPred("gotNotifiedFor", "?person", "?event"));
		effects.add(getObjectPred("increase", "(total-cost)", "1"));
		
		notify.add(effects);
		
		return notify;
	}
	
	/* Copies the configuration file required to run this example */
	public static void copyConfigFile() throws IOException {
		Path src = Paths.get("resources/use_cases/smart_home/config.properties");
		Path dst = Paths.get("config.properties");
		
		if(Files.exists(dst)) {
			Files.delete(dst);
		}
		
		if(Files.exists(src)) {
			Files.copy(src, dst);
		}
	}
	
	public static void main(String[] args) {
		try {
			
			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
					.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.OFF);
			
			
			SmartHome.copyConfigFile();
			
			SmartHome.prepareH2Database();
			
			PolicyManager manager = new PolicyManager();
			
			System.out.println("Checking potential conflicts between all policies ...");
			System.out.println("");
			
			// Potential conflicts can be found to take precautions.
			HashMap<Policy, ArrayList<Policy>> conflicts = manager.checkAllConflicts();
			for(Policy p1 : conflicts.keySet()) {
				String list = conflicts.get(p1).stream().map(Policy::getName).collect(Collectors.joining(","));
				System.out.println("\t" + p1.getName() + " might conflict with the following policies: " + list);
			}
			
			System.out.println("");
			System.out.println("Updating normative state ...");
			System.out.println("");
			
			manager.updateNormativeState();
			
			System.out.println("\tProhibitions:");
			System.out.println("");

			ArrayList<String> prohibitions = new ArrayList<String>(manager.getProhibitions().keySet());
			for(String instance : prohibitions) {
				System.out.println("\t\tPolicy: " + instance.replace("-", "\tAddressee: "));
			}
			
			System.out.println("");
			System.out.println("\tObligations:");
			System.out.println("");
			
			ArrayList<String> obligations = new ArrayList<String>(manager.getObligations().keySet());
			for(String instance : obligations) {
				System.out.println("\t\tPolicy: " + instance.replace("-", "\tAddressee: "));
			}
			
			System.out.println("");
			System.out.println("Running planner for obligations");
			System.out.println("");
			
			for (String obligation : manager.getObligations().keySet()) {
				HashSet<ActivePolicy> instances = manager.getObligations().get(obligation);
				manager.executeObligations(SmartHome.getPDDLActions(), instances);
			}
			
			manager.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
