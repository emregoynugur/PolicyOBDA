# A Semantic Policy Framework for IoT

This repository contains sample code and resources to help with the implementation of the policy framework (utilizing ontology based data access) described in our work (cite). 
This implementation is a work in progress and it is missing several features to be used in production. However, all the functionality described in our work (cite) is implemented. Below we provide an overview of our policy framework.

![](/resources/images/obda.png?raw=true)


## Getting Started

These instructions will let you run our project and examples on your local machine for development or testing purposes. 

### Prerequisites

The project is being developed with [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

The easiest way to run the project is installing [Eclipse](https://www.eclipse.org/downloads/) (or your favorite IDE) and import the code as a [Maven](https://maven.apache.org/install.html) project. The IDE should automatically download all the dependencies.

We use the [Fast Downward](http://fast-downward.org/) planner in our tests. We recommend using the source code provided in our repository, as we had to make a slight change (`disabled verify_axiom_predicates function`) in the code. (This issue will be addressed in the future.) See the [instructions](http://www.fast-downward.org/ObtainingAndRunningFastDownward) to compile the planner. 

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

We provide two examples in this project; a smart home example that works with a single in-memory database and a smart mine example that works with data federation.  Furthermore, the second use case *smart mine* allows us to show how our policy framework can be integrated into in-use IoT systems that work with multiple data sources.

### Smart Home

We use this scenario only to demonstrate the basic functionality and to explain how our policy library can be used. This is a toy example and definitely not an attempt towards building a smart home application.

#### Description

We provide the smart home system with only two policies; a prohibition (SoundDisabled) that disables sound notifications, and an obligation (NotifyDoorbell) that forces a doorbell to notify the residents if there is someone at the door. 


* Given policies SoundDisabled and NotifyDoorbell are in conflict.

* The *current state* includes a doorbell, a television, a baby (*John*), an adult (*Bob*), and a doorbell event. 

* The goal is to notify the adult resident *Bob* without violating the sound policy. 

#### Workflow

1. The application copies its **configuration file** to the project's folder, starts an **in-memory H2 database** instance, and populates it. 

2. The application reads the descriptions given in the **policies.xml** file and looks for potential **conflicts**.

3. It updates the **normative state**. e.g. updates active/expired policy instances.

4. The applications creates the **domain**  and **problem** files for the PDDL planner. The domain actions are **hard-coded** into the example.

5. It runs the **fast-downward planner** to find the best plan that minimizes (or avoids) violation costs. In this example, the planner first locates Bob and notifies him using a visual notification action, thus the found plan does not violate the sound policy.

![](/resources/images/smart_home.png?raw=true)


#### How to run

If the dependencies described in the prerequisites section are installed, simply running  `SmartHome.java` file is sufficient to execute this scenario. The resources used in this example can be found under the `/resources/use_cases/smart_home/` folder.