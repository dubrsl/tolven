Tolven JMeter Test Setup

To understand the JMeter tests requires reading the JMeter User Guide.

In general, there are three main components:

1. ThreadGroups
	These equate to threads for users
	They are added to Test Plans (e.g. defaultTestPlan has many by default), and are locatd in the threads directory
2. Simple Controller Configs (*SCCConfig.jmx files)
	These are what are added to ThreadGroups.
	They contain UserParameter nodes used to configure SimpleControllers
	They are located in the simple-controller-configs directory
3. Simple Controllers (*SCC.jmx files)
	These are reusable components, which get included into Simple Controller Configs
	These should be changed only with care, since they are used in many ThreadGroups and test plans
	Configuration should be done via a Simple Controller Config
	They are located in the simple-controllers directory

*******
Install
*******
Download and unzip JMeter
	Copy the tolven-jmeter-test.jar to the JMeter's lib\ext directory

*****************
GUI Configuration
*****************
Open org.tolven.jmetertests/defaultTestPlan.jmx
Save as org.tolven.jmetertests/myTestPlan.jmx
Select root node called defaultTestPlan
	Change root node name from defaultTestPlan to myTestPlan
Select globalVariables node. It is recommended not to change these, but override as per next steps
Select root node called defaultTestPlan
Right click: Add/ConfigElement/UserDefinedVariables
	Click the new node and change its name to myVariables
	This are added to end of the node tree, where it works fine (it can be dragged and dropped after globleVariables for ease of view)
Add variables to myVaribles to override the ones in globleVariables...check all, but in particular:
	DATA_DIR: Absolute path to org.tolven.jmetertests/data
	RESULTS_DIR: Absolute path to org.tolven.jmetertests/results
		Create the directory in the filesystem
SAVE myTestPlan
In addition save myVariables separately
	Select the myVariables node
	Right click: Save SelectionAs...
	SAVE as myVariables.jmx


*********
Run Tests
*********
Ensure that the Tolven environment is up and running
In JMeter:
	Select the View Results Tree node to watch results created
	Select Run/Start
	A count down of remaining tests shows in the top right corner (some take a while to execute, by GUI navigation is fine)
	Green ticked triangles represent successful steps, red are failures
	Results  can also be viewed via the View Results Tree
	Via the Simple Data Wrieter node, results are also written to the results directory
