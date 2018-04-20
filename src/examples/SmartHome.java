package examples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.tools.RunScript;

import ch.qos.logback.classic.Level;
import planning.PddlGenerator;
import planning.Planner;
import planning.parser.Atom;
import planning.parser.LispExprList;
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
		effects.add(getObjectPred("increase", "(total-cost)", "(SoundDisabled ?device)"));
		
		notify.add(effects);
		
		return notify;
	}
	
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
	
	public static void main(String[] args) {
		try {
			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
					.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.OFF);
			
			SmartHome.prepareH2Database();
			
			PolicyManager manager = new PolicyManager();
			manager.updateActivePolicies();
			
			PddlGenerator pddl = new PddlGenerator(manager);
			pddl.generateDomainFile(SmartHome.getPDDLActions());
			pddl.generateProblemFile();
			
			Planner.runPlanner();
			
			manager.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
