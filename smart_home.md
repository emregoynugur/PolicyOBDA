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

