# A Semantic Policy Framework for IoT

This repository contains sample code and resources to help with the implementation of the policy framework (utilizing ontology based data access) described in our work (cite). 
This implementation is a work in progress and it is missing several features to be used in production. However, all the functionality described in our work (cite) is implemented.

## Getting Started

These instructions will let you run our project and examples on your local machine for development or testing purposes. 

### Prerequisites

The project is being developed with [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

The easiest way to run the project is installing [Eclipse](https://www.eclipse.org/downloads/) (or your favorite IDE) and import the code as a [Maven](https://maven.apache.org/install.html) project. The IDE should automatically download all the dependencies.

We use the [Fast Downward](http://fast-downward.org/) planner in our tests. We recommend using the source code provided in our repository, as we had to make a slight change (`disabled verify_axiom_predicates function`) in the code. (This *hack* will be addressed in the future.) See the [instructions](http://www.fast-downward.org/ObtainingAndRunningFastDownward) to compile the planner. 

### Configuration File

The configuration file contains the following parameters that can be changed. All the paths written in this file should be relative paths. Below is the configuration file we use for our smart home example: 

```
owlFile=resources/use_cases/smart_home/sspn-ql-rdf.owl
obdaFile=resources/use_cases/smart_home/sspn-ql-rdf.obda
obdaPropertiesFile=resources/use_cases/smart_home/sspn-ql-rdf.properties
ontologyIri=http://cs.ozyegin.edu.tr/muratsensoy/2015/03/sspn-ql#
policyFile=resources/use_cases/smart_home/policies.xml
plannerDomain=planner/fastdownward/domain.pddl
plannerProblem=planner/fastdownward/problem.pddl
plannerOutput=planner/fastdownward/sas_plan
plannerDir=planner/fastdownward/
plannerCommand=fast-downward.py domain.pddl problem.pddl --search "lazy_greedy([ff()], preferred=[ff()])"
smartHomeDB=resources/use_cases/smart_home/h2.sql
```

## Use Cases

We provide two examples in this project; a smart home example that works with a single in-memory database and a smart mine example that works with data federation. 

### Smart Home

A relational database may not be a good choice for such an application. However we use this example, as it is easy to understand. The implementation demonstrates the following scenario:

* The used ontology, the mappings, and the policy file can be found under the */resources/use_cases/smart_home/* folder.

* There are only two policies given to the smart home system; a prohibition (SoundDisabled) that disables sound notifications, and an obligation (NotifyDoorbell) that forces a doorbell to notify the residents if there is someone at the door. 

* SoundDisabled and NotifyDoorbell policies are in conflict.

* The *current state* and the schema of a very simple smart home database (H2 in-memory database) is provided in */resources/use_cases	/smart_home/h2.sql*.

* The *current state* includes a doorbell, a television, a baby (John), and an adult (Bob). 

* The goal of the demo is finding a way to notify *Bob* without violating the sound policy. 

The demo application first reads the provided policies and finds the conflict. Then, it updates the normative state (finds active policy instances). Finally, it runs the planner to execute obligated actions by minimizing (or avoiding) violation costs.






