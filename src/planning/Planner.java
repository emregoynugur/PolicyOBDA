package planning;

import java.io.BufferedReader;
import java.io.File;
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

		ProcessBuilder pb = new ProcessBuilder(Paths.get(config.getPlanner()).toAbsolutePath().toString(),
				Paths.get(config.getPlannerDomain()).toAbsolutePath().toString(),
				Paths.get(config.getPlannerProblem()).toAbsolutePath().toString(), config.getPlannerCommand());
        pb.directory(Paths.get(config.getPlanner()).toAbsolutePath().getParent().toFile());
        
        String f = Paths.get(config.getPlanner()).toAbsolutePath().getParent().toString();
        
        Files.createFile(Paths.get(f + "/output.txt"));
        
        pb.redirectErrorStream(true);
        pb.redirectError(new File(f + "/output.txt"));
        pb.redirectOutput(new File(f + "/output.txt"));
        pb.redirectInput(new File(f + "/output.txt"));

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
