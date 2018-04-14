package main;

import static java.util.stream.Collectors.joining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import ch.qos.logback.classic.Level;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.OntopOWLFactory;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLStatement;

public class QueryManager {
	final int policyCount = 8;

	final String owlFile = System.getProperty("user.dir") + "/resources/mine-federation.owl";
	final String obdaFile = System.getProperty("user.dir") + "/resources/mine-federation.obda";
	final String propertiesFile = System.getProperty("user.dir") + "/resources/mine-federation.properties";

	final String sparqlFile = System.getProperty("user.dir") + "/resources/policies/policy";

	final static String resultFile = System.getProperty("user.dir") + "/resources/policies/federation/results.txt";
	final static String labelFile = System.getProperty("user.dir") + "/resources/policies/federation/labels.txt";

	final static String IN_MEMORY_RESULT = System.getProperty("user.dir") + "/resources/federation/results.txt";
	final static String IN_MEMORY_OBJECT_CREATION = System.getProperty("user.dir")
			+ "/resources/inmemory/policy_creation_runtimes.txt";
	final static String IN_MEMORY_ROW_COUNTS = System.getProperty("user.dir")
			+ "/resources/inmemory/policy_row_counts.txt";

	final static String IN_MEMORY_QUERY_SINGLE_RUN = System.getProperty("user.dir")
			+ "/resources/inmemory/queries_single_run.txt";
	final static String IN_MEMORY_OBJECT_CREATION_10 = System.getProperty("user.dir")
			+ "/resources/inmemory/process_runtime_10.txt";
	final static String IN_MEMORY_OBJECT_QUERY_COMBINED = System.getProperty("user.dir")
			+ "/resources/inmemory/process_query_runtime.txt";

	public ArrayList<String> readQueries() throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= policyCount; i++) {
			String sparqlQuery = Files.lines(Paths.get(sparqlFile + i + ".sparql")).collect(joining("\n"));
			list.add(sparqlQuery);
		}
		return list;
	}

	public void appendToResults(String file, String result) {
		BufferedWriter bw = null;
		try {
			// APPEND MODE SET HERE
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(result);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally

	}

	public void runQueries() throws Exception {
		OntopOWLFactory factory = OntopOWLFactory.defaultFactory();

		OntopSQLOWLAPIConfiguration config = (OntopSQLOWLAPIConfiguration) OntopSQLOWLAPIConfiguration.defaultBuilder()
				.r2rmlMappingFile(obdaFile).ontologyFile(owlFile).propertyFile(propertiesFile).enableTestMode().build();

		// ArrayList<String> queries = readQueries();
		ArrayList<Policy> policies = new PolicyReader().readPolicies();
		ArrayList<Double> averageObjectTimes = new ArrayList<Double>();
		ArrayList<Double> averageQueryTimes = new ArrayList<Double>();
		ArrayList<Double> averageCombinedTimes = new ArrayList<Double>();

		try (OntopOWLReasoner reasoner = factory.createReasoner(config);
				OntopOWLConnection conn = reasoner.getConnection();
				OntopOWLStatement st = conn.createStatement();) {

			for (int i = 0; i < policies.size(); i++) {
				// st.executeTuple(policies.get(i).getActivation().toString());
			}

			/*
			 * for (int i = 0; i < policies.size(); i++) { // ArrayList<Double> runTimes =
			 * new ArrayList<Double>(); long start_time = System.nanoTime();
			 * TupleOWLResultSet rs =
			 * st.executeTuple(policies.get(i).getActivation().toString()); long q_end_time
			 * = System.nanoTime(); policies.get(i).createInstancesWithBindings(rs); long
			 * o_end_time = System.nanoTime();
			 * 
			 * rs.close(); double difference = (q_end_time - start_time) / 1e6;
			 * averageQueryTimes.add(difference);
			 * 
			 * difference = (o_end_time - q_end_time) / 1e6;
			 * averageObjectTimes.add(difference);
			 * 
			 * difference = (o_end_time - start_time) / 1e6;
			 * averageCombinedTimes.add(difference); }
			 */

			System.out.println(averageCombinedTimes);
			System.out.println(averageQueryTimes);
			System.out.println(averageObjectTimes);

			String result = "";
			for (int i = 0; i < policyCount - 1; i++) {
				result += averageObjectTimes.get(i).toString() + ",";
			}
			result += averageObjectTimes.get(policyCount - 1).toString();
			appendToResults(IN_MEMORY_OBJECT_CREATION_10, result);

			result = "";
			for (int i = 0; i < policyCount - 1; i++) {
				result += averageQueryTimes.get(i).toString() + ",";
			}
			result += averageQueryTimes.get(policyCount - 1).toString();
			appendToResults(IN_MEMORY_QUERY_SINGLE_RUN, result);
			System.out.println(result);

			result = "";
			for (int i = 0; i < policyCount - 1; i++) {
				result += averageCombinedTimes.get(i).toString() + ",";
			}
			result += averageCombinedTimes.get(policyCount - 1).toString();
			appendToResults(IN_MEMORY_OBJECT_QUERY_COMBINED, result);
			System.out.println(result);

		}

	}

	/*
	 * OWL2QLProfile profile = new OWL2QLProfile(); OWLProfileReport report =
	 * profile.checkOntology(ontology); if (report.isInProfile()) {
	 * System.out.println("OK"); } else {
	 * System.out.println(report.getViolations().size() + " violations"); }
	 * 
	 * 
	 */

	/**
	 * Main client program
	 */
	public static void main(String[] args) {
		try {
			// InMemoryDatabase mdb = new InMemoryDatabase();
			// mdb.startServer();

			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
					.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.OFF);

			QueryManager manager = new QueryManager();

			manager.runQueries();
			/*
			 * do { manager.runQueries(); } while (mdb.loadNextBatch());
			 */

			// String dir = "/Users/emre/Desktop/mine/data/";
			// File file = new File(dir);
			// String[] names = file.list();

			// QueryManager example = new QueryManager();
			// example.runQueries();
			/*
			 * for (String name : names) { if (new File(dir + name).isDirectory()) {
			 * System.out.println(name); DatabaseManager.recreateDatabase();
			 * DatabaseManager.executeScript(dir + name + "/data.sql");
			 * DatabaseManager.executeScript(dir + name + "/sensor.sql");
			 * System.out.println(name); example.runQueries(); System.out.println(name); } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
