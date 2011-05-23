Generating the webservice class files requires the tolvenWS.war file, which is deployed when org.tolven.ws is chosen as a <root> plugin.
With the application server running, below is an example of Ant build file snippet, where the target generate-ws-clients needs to be executed.

To use the script:

	1. The source files are assumed to be in a directory called client/source.
	2. The com.sun.tools.ws.ant.WsImport used below included a jar path with the following jars from the Metro WebServices project:
		2.1 webservices-api.jar, webservices-extra-api.jar, webservices-extra.jar, webservices-rt.jar, webservices-tools.jar
	3. With the application server running, the WSDLs need to be available.

Once compiled, see the example classes file main method's usage information.

EXAMPLE ANT BUILD SNIPPET:

	<target name="generate-ws-clients" depends="init" description="Generate clients from WSDL locations at localhost webserver">
		<antcall target="generate-EchoWebServiceClient"/>
		<antcall target="generate-GeneratorWebServiceClient"/>
		<antcall target="generate-DocumentWebServiceClient"/>
		<antcall target="generate-TrimWebServiceClient"/>
	</target>
	<target name="generate-EchoWebServiceClient" depends="init" description="EchoWebServiceClient from WSDL at localhost webserver">
		<delete>
			<fileset dir="client/source">
				<include name="**/ws/echo/jaxws/*"/>
			</fileset>
		</delete>
		<wsimport
			wsdl="http://localhost:8080/ws/EchoService?wsdl"
			package="org.tolven.client.examples.ws.echo.jaxws"
			sourcedestdir="client/source"
			xnocompile="true"/>
	</target>
	<target name="generate-GeneratorWebServiceClient" depends="init" description="GeneratorWebServiceClient from WSDL at localhost webserver">
		<delete>
			<fileset dir="client/source">
				<include name="**/ws/generator/jaxws/*"/>
			</fileset>
		</delete>
		<wsimport
			wsdl="http://localhost:8080/ws/GeneratorService?wsdl"
			package="org.tolven.client.examples.ws.generator.jaxws"
			sourcedestdir="client/source"
			xnocompile="true"/>
	</target>
	<target name="generate-DocumentWebServiceClient" depends="init" description="DocumentWebServiceClient from WSDL at localhost webserver">
		<delete>
			<fileset dir="client/source">
				<include name="**/ws/document/jaxws/*"/>
			</fileset>
		</delete>
		<wsimport
			wsdl="http://localhost:8080/ws/DocumentService?wsdl"
			package="org.tolven.client.examples.ws.document.jaxws"
			sourcedestdir="client/source"
			xnocompile="true"/>
	</target>
	<target name="generate-TrimWebServiceClient" depends="init" description="TrimWebServiceClient from WSDL at localhost webserver">
		<delete>
			<fileset dir="client/source">
				<include name="**/ws/trim/jaxws/*"/>
			</fileset>
		</delete>
		<wsimport
			wsdl="http://localhost:8080/ws/TrimService?wsdl"
			package="org.tolven.client.examples.ws.trim.jaxws"
			sourcedestdir="client/source"
			xnocompile="true"/>
	</target>
	<taskdef name="wsimport" classname="com.sun.tools.ws.ant.WsImport">
		<classpath>
			<path>
				<fileset dir="lib">
					<include name="*.jar" />
				</fileset>
			</path>
		</classpath>
	</taskdef>