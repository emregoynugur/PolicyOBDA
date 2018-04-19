package policy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.xml.sax.SAXException;

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.OntopOWLFactory;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.si.SemanticIndexException;
import utils.Config;

public class PolicyManager {

	private OntopOWLReasoner owlReasoner;
	private PolicyReasoner policyReasoner;
	private ArrayList<Policy> policies;

	// Normative State
	private HashSet<ActivePolicy> prohibitions;
	private HashSet<ActivePolicy> obligations;
	
	public OntopOWLReasoner getOWLReasoner() {
		return owlReasoner;
	}

	public HashSet<ActivePolicy> getProhibitions() {
		return prohibitions;
	}

	public HashSet<ActivePolicy> getObligations() {
		return obligations;
	}

	public PolicyManager() throws IllegalConfigurationException, OWLOntologyCreationException,
			ParserConfigurationException, IOException, SAXException {

		Config pConfig = Config.getInstance();
		OntopOWLFactory factory = OntopOWLFactory.defaultFactory();
		OntopSQLOWLAPIConfiguration config = (OntopSQLOWLAPIConfiguration) OntopSQLOWLAPIConfiguration.defaultBuilder()
	                .nativeOntopMappingFile(pConfig.getObdaFile())
	                .ontologyFile(pConfig.getOntologyFile())
	                .propertyFile(pConfig.getObdaPropertiesFile())
	                .enableTestMode()
	                .build();

		owlReasoner = factory.createReasoner(config);
		policies = new PolicyReader().readPolicies();
		policyReasoner = new PolicyReasoner();
		prohibitions = new HashSet<>();
		obligations = new HashSet<>();
	}

	public void updateActivePolicies() throws Exception {

		for (int i = 0; i < policies.size(); i++) {
			HashSet<ActivePolicy> instances = policyReasoner.createActivePolicies(policies.get(i), owlReasoner);

			// TODO: determine domain actions (of planner) that will get affected
			if (policies.get(i).getModality().contains("Prohibition"))
				prohibitions.addAll(instances);
			else
				obligations.addAll(instances);
		}

		// TODO: check deadlines for expirations of obligations?
		policyReasoner.removeExpiredPolicies(prohibitions, owlReasoner);
		policyReasoner.removeExpiredPolicies(obligations, owlReasoner);
	}

	public HashMap<Policy, ArrayList<Policy>> checkAllConflicts()
			throws IllegalConfigurationException, OWLException, SemanticIndexException {
		HashMap<Policy, ArrayList<Policy>> conflicts = new HashMap<Policy, ArrayList<Policy>>();
		for (int i = 0; i < policies.size(); i++) {

			Policy first = policies.get(i);

			for (int j = i + 1; j < policies.size(); j++) {

				if (policyReasoner.checkConflict(policies.get(i), policies.get(j))) {
					Policy second = policies.get(j);

					System.out.println(first.getName() + " might conflict with " + second.getName());

					if (!conflicts.containsKey(first))
						conflicts.put(first, new ArrayList<Policy>());

					conflicts.get(first).add(second);
				}
			}
		}
		return conflicts;
	}

	public void stop() throws Exception {
		owlReasoner.close();
	}
}
