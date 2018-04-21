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
		ProcessBuilder pb = new ProcessBuilder("bash","-c", plannerCmd);
		pb.directory(Paths.get(config.getPlannerDir()).toAbsolutePath().toFile());

		Process process = pb.start();
		int errCode = process.waitFor();

		System.out.println("Planner command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
		System.out.println("Planner Output:\n" + output(process.getInputStream()));

		return true;
	}

	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}
}
