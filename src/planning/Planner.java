package planning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import utils.Config;

public class Planner {

	// TODO: mock implementation
	public static boolean runPlanner() throws IOException, InterruptedException {

		Config config = Config.getInstance();

		if (!Files.exists(Paths.get(config.getPlannerDomain())))
			return false;

		if (!Files.exists(Paths.get(config.getPlannerProblem())))
			return false;

		String plannerCmd = Paths.get(config.getPlannerDir()).toAbsolutePath() + "/" + config.getPlannerCommand();
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", plannerCmd);
		pb.directory(Paths.get(config.getPlannerDir()).toAbsolutePath().toFile());

		Process process = pb.start();
		int errCode = process.waitFor();

		System.out.println("\tPlanner command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
		System.out.println("\tPlanner Output:\n\n" + output(process.getInputStream()));

		return true;
	}

	// TODO: this output parser only works for fast-downward
	private static String output(InputStream inputStream) throws IOException {

		boolean solutionFound = false;
		boolean planCost = false;

		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null && !planCost) {
				if (solutionFound) {
					sb.append("\t\t" + line + System.getProperty("line.separator"));
				}
				if (line.contains("Solution found")) {
					solutionFound = true;
				} else if (line.contains("Plan cost")) {
					planCost = true;
				}
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}
}
