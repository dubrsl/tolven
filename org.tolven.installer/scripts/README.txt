Short Instructions:

As it stands, this is a one time install script. It will not work if it fails halfway through and those components are not removed. Remove and start again,
as suggested by the clean up section at the end. Ensure also that at the very least, the following ports are free (if accepting all the defaults):
	OpenDS: 389, 4444, 5555, 1636
	Tomcat: 8444
	OpenAM: 7777, 50389
	Glassfish: 8080, 8443
You may need to consult the OpenDS, Tomcat, OpenAM and Glassfish documentation to determine typical ports they use when in default mode i.e. before Tolven customization

See the Tolven Wiki Installation Guide for installation of the following components:

	Install Ant. Set ANT_HOME and add to the execution path.

	Install JDK. Set JAVA_HOME and add to the execution path.

	Install database as per database documentation.
	If using PostgreSQL, add the PostgreSQL schemas manually using a tool like pgAdminIII or psql (schemas are in wiki)

	Create keystore.jks/cacerts.jks/tolvendev-mdbuser.p12 keystore

	Add domain to hosts file

Installer files:

Look at build-v1.properties. Don't change this file, use the empty build-v2-override.properties to override these properties.

select a root.dir (e.g. c: directory), place the entry in the build-v2-override.properties file (create the directory manually if it does not exist).

create initial-tolven-components directory under the ${root.dir} directory created above, where all the starting components will be gathered.

Place keystore.jks/cacerts.jks/tolvendev-mdbuser.p12 created in the wiki instructions to the directory initial-tolven-components.

Gather the other components and use overrides if the versions are slightly different
	Ensure that the apache tomcat zip file matches the name of the directory in the zip itself (with ".zip" as the extension)
	If you move any ${} parameters on the value side of entries in the overrides file, the actual property definition must also be copied over.
	Two of the plugins mentioned in this section are located in the Tolven catalog. Use the zip link: http://tolven.org/download/v2/catalog/plugins
	Download the new kit 2.0.26 from: http://tolven.org/download/v2/tpf/tolven-V2.0.26.zip (leave as a zip, and do not expand) 
	If using Oracle, get its driver for the Oracle website, and override the jdbc.driver property

POSTGRESQL
	Verify the following database specific locations and override in build-v2-overrides.properties where necessary
		Check properties in build-v2-legacypostgresql.properties
		Check jdbc.driver in build-v2.properties
		Check rootdb.password in build-v2-password.properties
		Since PostgreSQL is the default database, there is no need to add the following to the build-v2-overrides.properties file, but the default is:
			tolven.database.code=legacypostgresql
	(NB: legacy here simply means that postgresql uses schemas to separate table spaces)

ORACLE
	Verify the following database specific locations and override in build-v2-overrides.properties where necessary
		Check properties in build-v2-oracle.properties
		Check jdbc.driver in build-v2.properties
		Check rootdb.password in build-v2-password.properties
		Since Oracle is not the default database, add the following to the build-v2-overrides.properties file:
			tolven.database.code=oracle

From the scripts directory:
	Execute: ant install
		This will install all components in the ${root.dir}
	
	On completion, lines like the following will appear in the output
	     [echo] NOW SAVE MASTER PASSWORD USING FOLLOWING COMMAND
	     [echo] c:/tolven/tolven-glassfish3/bin/asadmin change-master-password --savemasterpassword=true tolven
	
	Execute the command (switch to backslash for windows once in your command window), and set the password (e.g: tolven) when prompted
	
	Finally execute: ant config-tolven
		This should configure and start all servers which then leads to a Tolven login after setting up a user in OpenAM

In addition to the standard out, an install.log is created in the intial-components-directory with annotated information about the installation process

TROUBLESHOOTING AND CLEANUP

If something goes wrong, determine the cause by checking the tolven kit, opends, openam, and/or appserver logs.

Clean up can be done in one of the following ways:
	1. Ensure no servers are running (usually java executatbles in the Task manager or use linux ps command to determine what processes are running)
	2. Remove all directories from ${root.dir}, except initial-tolven-components and tolven-config. And in tolven-config remove the credentials directory.
	3. Or you can use the exclude flags as described in the Exclude Flags section
	
Exclude Flags
The following exclude flags can be used to restart then install and config stages, only if preceding components are known to have installed correctly.
Previous servers must be started if they are already installed, so that they are ready for downstream use. All the flags are listed here:
	exclude.tolven.kit=
	exclude.tolven.opends=
	exclude.openam=
	exclude.appserver=
	
	The normal order of installation is: tolven.kit -> opends -> openam -> appserver
	
They can be added to the build-v2-overrides.properties file, along with just their equeals sign, e.g.
Example:
If you get to the openam stage during config-tolven and failed at that point, then:
	a. you stop openam
	b. remove the openam directory
	c. you exclude tolven.kit, opends, assuming they installed correctly and opends is running
	d. you run ant install and ant config-tolven again
If you get to the appserver stage during config-tolven and failed, then:
	a. you stop appserver
	b. remove appserver directory
	c. you exclude tolven.kit, opends, openam
	d. you run ant install and ant config-tolven again