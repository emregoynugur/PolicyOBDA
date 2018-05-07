# A Semantic Policy Framework for IoT

This repository contains sample code and resources to help with the implementation of the policy framework (utilizing ontology based data access) described in our work (cite). 
This implementation is a work in progress and it is missing several features to be used in production. However, all the functionality described in our work (cite) is implemented. 

## Overview

This software takes the following as inputs: a policy file that contains all the policies, an ontology that is used to describe those policies, and the database mappings of the ontology. Then, the software can be used to detect conflicts between policies, to keep track of active policy instances, and to generate plans to execute obligation policies by minimizing policy violation costs. We note that converting action descriptions to PDDL is not in the scope of this work and PDDL actions should be provided to the planner. An overview of the architecture is provided below.

![](/resources/images/obda.png?raw=true)


## Getting Started

These instructions will let you run our project and examples on your local machine for development or testing purposes. 

### Prerequisites

The project is being developed with [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

The easiest way to run the project is installing [Eclipse](https://www.eclipse.org/downloads/) (or your favorite IDE) and import the code as a [Maven](https://maven.apache.org/install.html) project. The IDE should automatically download all the dependencies.

We use the [Fast Downward](http://fast-downward.org/) planner in our tests. We recommend using the source code provided in our repository, as we had to make a slight change (disabled `verify_axiom_predicates` function in `/translate/normalize.py` file) in the code. (This issue will be addressed in the future.) See the [instructions](http://www.fast-downward.org/ObtainingAndRunningFastDownward) to compile the planner. 

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

* [Smart Home](smart_home.md)
* [Smart Mine (with federation)](mine_federation.md)

