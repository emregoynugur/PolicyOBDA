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

### Smart Home

We use this scenario only to demonstrate the basic functionality and to explain how our policy library can be used. This is a toy example and definitely not an attempt towards building a smart home application.

#### Description

We provide the smart home system with only two policies; a prohibition (SoundDisabled) that disables sound notifications, and an obligation (NotifyDoorbell) that forces a doorbell to notify the residents if there is someone at the door. 


* Given policies SoundDisabled and NotifyDoorbell are in conflict.

* The *current state* includes a doorbell, a television, a baby (*John*), an adult (*Bob*), and a doorbell event. 

* The goal is to notify the adult resident *Bob* without violating the sound policy. 

#### How to run

If the dependencies described in the prerequisites section are installed, simply running  `SmartHome.java` file is sufficient to execute this scenario. The resources used in this example can be found under the */resources/use_cases/smart_home/* folder.

#### Workflow

1. The application copies its **configuration file** to the project's folder, starts an **in-memory H2 database** instance, and populates it. 

2. The application reads the descriptions given in the **policies.xml** file and looks for potential **conflicts**.

3. It updates the **normative state**. e.g. updates active/expired policy instances.

4. The applications creates the **domain**  and **problem** files for the PDDL planner. The domain actions are **hard-coded** into the example.

5. It runs the **fast-downward planner** to find the best plan that minimizes (or avoids) violation costs. In this example, the planner first locates Bob and notifies him using a visual notification action, thus the found plan does not violate the sound policy.

![](/resources/images/smart_home.png?raw=true)

### Smart Mine

This is a naive implementation of our policy framework utilizing data federation and OBDA with Ontop. We simplified and distributed the data of a real application into two different sources but we did not do it in the most efficient way to show that we can join different data sources to query a concept or property.

#### Prerequisites

* [PostgreSQL](https://www.postgresql.org) or any other SQL database that is compatible with the generated SQL scripts in */resources/use_cases/mine/* directory. In this tutorial, we assume that the target database is PostgreSQL.

* [MongoDB](https://docs.mongodb.com/manual/installation/)

* [Python3](https://www.python.org/downloads/) and install PyMongo. e.g. `pip install pymongo`

* [Teiid Server]((http://teiid.jboss.org)) with Runtime (WildFly/Console) by JBOSS.

#### Setup

After downloading the requirements mentioned above, please follow these instructions to get the data federation solution up and running. We followed [Ontop's tutorial](https://github.com/ontop/ontop/wiki/BookFederationTutorial) to create our example. Similar instructions can be found in their site.

##### Preparing the data sources

###### PostreSQL
1. Create PostgreSQL database by running `CREATE DATABASE smart_mine;` in `psql`
2. Create the tables by running the script located in the example's folder.  `psql smart_mine < ./resources/use_cases/mine/schema.sql`

###### MongoDB


3. Start the MongoDB server. 
4. Connect to your MongoDB server. (On macOS `mongod`command in terminal can be used to start a server and `mongo` command can be used to connect to that instance in another terminal window.
5. Create a new database: `use smart_mine`
6. Create a user for our example. `db.createUser( { user: "iot", pwd: "iot", roles: [ "readWrite"] })`

##### Populate the data sources

1. While both database servers are running, run the Python script.`python populate.py` This will both populate the MongoDB database and generate SQL file for the relational database.
2. Run the generated SQL script to populate the relational database. `psql smart_mine < ./resources/use_cases/mine/instances.sql`

##### Preparing the federation server

These instructions are taken from [Ontop's tutorial](https://github.com/ontop/ontop/wiki/teiid). The following and few more optimization tips can be found in the provided link.

1. Install [Teiid Designer](http://teiiddesigner.jboss.org/designer_summary/downloads.html)
2. Navigate into Teiid server's folder from terminal.
3. Create an admin user: `$TEIID_DIR/bin/add-user.sh`
	* Type: Management User
	* Username: iotadmin
	* Password: iot
	* Realm: ManagementRealm
4. Create an application user: `$TEIID_DIR/bin/add-user.sh`
	* Type: Application User
	* Username: iotuser
	* Password: iot
	* Realm: ApplicationRealm

##### Creating the federated view in Eclipse

First create a Teiid model project for the relational database.

1. Select `File > New > Others` from the menu bar
2. Choose `Teiid Designer > Teiid Model Project` from the menu bar
3. Project name: FederatedMine

After creating the project, we need to create a PostgreSQL connection. 

1. Switch to Teiid Designer perspective.
2. Select `File > Import > Teiid Designer > JDBC Database`
3. Create new *Connection Profile*. Click new.
4. Click *Generic JDBC Connection* and name it PostgreSQL.
5. Go to *Jar List* tab and add PostgreSQL's driver located in */resources/jars*
6. Go to next tab. Browse and select PostgreSQL class driver.
	* Connection Profile: PostgreSQL
	* JDBC Metadata Processor: PostgreSQL
	* Driver: PostgreSQL
	* URL: jdbc:postgresql://localhost:5432/smart_mine
	* Username: iot
	* Password: iot
7. Click next and choose Table.
8. Click next again, select *public schema*, and then click next one more time.
9. Click finish.

Now, create a Teiid Server instance to test if PostgreSQL connection is working.

1. Click on the "No default server defined" link at the bottom of "Connections" tab. A dialog window will pop out to create a new Teiid server connection.
2. Select "WildFly 10.x".
3. Set the server's host name according to your server installation (by default, use localhost for local installation). Accept the other default values by selecting "Next".
4. Set the server's name to IoTServer
5. Click "Next" (twice).
6. Adjust the "Home Directory" to your TEIID_HOME location.
7. Adjust the "Configuration file" to TEIID_HOME/standalone/configuration/**standalone-teiid.xml**.
8. Enter the credentials set in the previous step. Double click on "IoTServer" in the "Servers" tab.
	* Management Login Credentials:
		* Username: iotadmin
		* Password: iot
	* JDBC Connection Credentials in "Teiid Instance" tab:
		* Username: iotuser
		* Password: iot
9. Click on the green button to start the server.
10. Finally, check if both links "Check Administration Button" and "Check JDBC Connection" in *Teiid Instance" tab returns **OK**.

Follow [this tutorial](https://developer.jboss.org/wiki/ConnectToAMongoDBSource)  to connect the MongoDB server. Please read our notes below before you start the tutorial.