package main;

import java.util.ArrayList;

import ch.qos.logback.classic.Level;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.OntopOWLFactory;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLStatement;
import utils.Config;

public class QueryManager {

	public void runQueries() throws Exception {
		Config pConfig = Config.getInstance();
		
		OntopOWLFactory factory = OntopOWLFactory.defaultFactory();
		
		OntopSQLOWLAPIConfiguration config = (OntopSQLOWLAPIConfiguration) OntopSQLOWLAPIConfiguration.defaultBuilder()
				.r2rmlMappingFile(pConfig.getObdaFile())
				.ontologyFile(pConfig.getOntologyFile())
				.propertyFile(pConfig.getObdaPropertiesFile()).enableTestMode().build();
		
		ArrayList<Policy> policies = new PolicyReader().readPolicies();
		try (OntopOWLReasoner reasoner = factory.createReasoner(config);
				OntopOWLConnection conn = reasoner.getConnection();
				OntopOWLStatement st = conn.createStatement();) {

			for (int i = 0; i < policies.size(); i++) {
				st.executeSelectQuery(policies.get(i).getActivation().toString());
			}
		}
	}

	/**
	 * Main client program
	 */
	public static void main(String[] args) {
		try {
			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
					.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.OFF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
