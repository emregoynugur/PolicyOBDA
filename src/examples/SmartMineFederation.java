package examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import planning.Planner;
import policy.Policy;
import policy.PolicyManager;
import utils.Config;

public class SmartMineFederation {

	private static void copy(String source, String destination) throws IOException {
		Path src = Paths.get(source);
		Path dst = Paths.get(destination);

		if (Files.exists(dst)) {
			Files.delete(dst);
		}

		if (Files.exists(src)) {
			Files.copy(src, dst);
		}
	}

	public static void copyConfigFile() throws IOException {
		copy("resources/use_cases/mine/config.properties", "config.properties");
	}

	public static void copyPlannerFiles() throws IOException {
		copy("resources/use_cases/mine/domain.pddl", Config.getInstance().getPlannerDomain());
		copy("resources/use_cases/mine/problem.pddl", Config.getInstance().getPlannerProblem());
	}

	public static void main(String[] args) {
		try {

			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
					.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.OFF);

			SmartMineFederation.copyConfigFile();

			PolicyManager manager = new PolicyManager();

			System.out.println("Checking potential conflicts between all policies ...");
			System.out.println("");

			// Potential conflicts can be found to take precautions.
			HashMap<Policy, ArrayList<Policy>> conflicts = manager.checkAllConflicts();
			for (Policy p1 : conflicts.keySet()) {
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
			for (String instance : prohibitions) {
				System.out.println("\t\tPolicy: " + instance.replace("-", "\tAddressee: "));
			}

			System.out.println("");
			System.out.println("\tObligations:");
			System.out.println("");

			ArrayList<String> obligations = new ArrayList<String>(manager.getObligations().keySet());
			for (String instance : obligations) {
				System.out.println("\t\tPolicy: " + instance.replace("-", "\tAddressee: "));
			}

			System.out.println("");
			System.out.println("Running planner to show the example scenario!");
			System.out.println("");

			SmartMineFederation.copyPlannerFiles();
			Planner.runPlanner();

			manager.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
