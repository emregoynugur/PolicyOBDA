package planning;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import utils.Config;

public class Planner {

	// TODO: mock implementation
	public static boolean runPlanner() throws IOException {
		
        String s = null;
		
		Config config = Config.getInstance();

		if (!Files.exists(Paths.get(config.getPlannerDomain())))
			return false;

		if (!Files.exists(Paths.get(config.getPlannerProblem())))
			return false;

		String command = Paths.get(config.getPlanner()) + "/plan " + 
						 Paths.get(config.getPlannerDomain()).toAbsolutePath() + " " + 
						 Paths.get(config.getPlannerProblem()).toAbsolutePath() + " " +
						 Paths.get(config.getPlannerOutput()).toAbsolutePath();

		Process p = Runtime.getRuntime().exec(command, null, new File(config.getPlanner()));

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		// read the output from the command
		System.out.println("Output of the planner:\n");
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}

		// read any errors from the attempted command
		System.out.println("Errors of the planner (if any):\n");
		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}

		return true;
	}
}
