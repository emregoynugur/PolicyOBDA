package policy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.xml.sax.SAXException;

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.OntopOWLFactory;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.si.SemanticIndexException;
import planning.PddlGenerator;
import planning.Planner;
import planning.parser.LispExprList;
import utils.Config;

public class PolicyManager {

	private OntopOWLReasoner owlReasoner;
	private PolicyReasoner policyReasoner;
	private ArrayList<Policy> policies;

	// Normative State
	private HashMap<String, HashSet<ActivePolicy>> prohibitions;
	private HashMap<String, HashSet<ActivePolicy>> obligations;

	public OntopOWLReasoner getOWLReasoner() {
		return owlReasoner;
	}

	public HashMap<String, HashSet<ActivePolicy>> getProhibitions() {
		return prohibitions;
	}

	public HashMap<String, HashSet<ActivePolicy>> getObligations() {
		return obligations;
	}

	public PolicyManager() throws IllegalConfigurationException, OWLOntologyCreationException,
			ParserConfigurationException, IOException, SAXException {

		Config pConfig = Config.getInstance();
		OntopOWLFactory factory = OntopOWLFactory.defaultFactory();
		OntopSQLOWLAPIConfiguration config = (OntopSQLOWLAPIConfiguration) OntopSQLOWLAPIConfiguration.defaultBuilder()
				.nativeOntopMappingFile(pConfig.getObdaFile()).ontologyFile(pConfig.getOntologyFile())
				.propertyFile(pConfig.getObdaPropertiesFile()).enableTestMode().build();

		owlReasoner = factory.createReasoner(config);
		policies = new PolicyReader().readPolicies();
		policyReasoner = new PolicyReasoner();
		prohibitions = new HashMap<String, HashSet<ActivePolicy>>();
		obligations = new HashMap<String, HashSet<ActivePolicy>>();
	}

	public void updateNormativeState() throws Exception {

		for (int i = 0; i < policies.size(); i++) {

			HashSet<ActivePolicy> instances = policyReasoner.createActivePolicies(policies.get(i), owlReasoner);

			boolean isProhibition = policies.get(i).getModality().contains("Prohibition");
			HashMap<String, HashSet<ActivePolicy>> set = (isProhibition) ? prohibitions : obligations;

			for (ActivePolicy instance : instances) {
				String key = instance.getName() + "-" + instance.getAddressee();
				if (!set.containsKey(key)) {
					set.put(key, new HashSet<ActivePolicy>());
				}
				set.get(key).add(instance);
			}
		}

		// TODO: check deadlines for expirations of obligations?
		HashSet<String> expiredPolicies = new HashSet<>();

		// check expired prohibitions
		for (String key : prohibitions.keySet()) {
			policyReasoner.removeExpiredPolicies(prohibitions.get(key), owlReasoner);
			if (prohibitions.get(key).size() == 0)
				expiredPolicies.add(key);
		}

		// check expired obligations
		for (String key : obligations.keySet()) {
			policyReasoner.removeExpiredPolicies(obligations.get(key), owlReasoner);
			if (obligations.get(key).size() == 0)
				expiredPolicies.add(key);
		}

		for (String key : expiredPolicies) {
			if (prohibitions.containsKey(key))
				prohibitions.remove(key);
			else
				obligations.remove(key);
		}
	}

	public HashMap<Policy, ArrayList<Policy>> checkAllConflicts()
			throws IllegalConfigurationException, OWLException, SemanticIndexException {
		HashMap<Policy, ArrayList<Policy>> conflicts = new HashMap<Policy, ArrayList<Policy>>();
		for (int i = 0; i < policies.size(); i++) {

			Policy first = policies.get(i);

			for (int j = i + 1; j < policies.size(); j++) {

				if (policyReasoner.checkConflict(policies.get(i), policies.get(j))) {
					Policy second = policies.get(j);

					if (!conflicts.containsKey(first))
						conflicts.put(first, new ArrayList<Policy>());

					conflicts.get(first).add(second);
				}
			}
		}
		return conflicts;
	}

	public void executeObligations(List<LispExprList> actions)
			throws IOException, InterruptedException, ReasonerInternalException, OWLException {

		if (obligations.size() == 0)
			return;

		PddlGenerator pddl = new PddlGenerator(this);

		pddl.generateDomainFile(actions);

		// TODO: implement a proper mechanism to consume obligations
		// TODO: just change the goal state instead of re-generating the entire problem
		// file.
		for (String obligation : obligations.keySet()) {
			HashSet<ActivePolicy> instances = obligations.get(obligation);
			pddl.generateProblemFile(instances);
			Planner.runPlanner();
		}
	}

	public void stop() throws Exception {
		owlReasoner.close();
	}
}
