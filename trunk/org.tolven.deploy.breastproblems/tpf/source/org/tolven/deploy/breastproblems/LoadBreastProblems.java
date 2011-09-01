package org.tolven.deploy.breastproblems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.LoadRESTfulClient;

public class LoadBreastProblems extends LoadRESTfulClient {

	String fileName;
	public static final int SCTID = 0;
	public static final int FullySpecifiedName = 1;
	public static final int DateOfStatusChange = 2;
	public static final int SubsetStatus = 3;
	public static final int SNOMEDStatus = 4;
	public static final int BATCH_SIZE = 100;
	
	public LoadBreastProblems(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
	}
	
	
	public String generateName( String fields[] ) {
		return "problem/SCTID-B" + fields[SCTID];
	}
	public void generateTrim( String fields[], XMLStreamWriter writer  ) throws XMLStreamException {
		writer.writeStartElement("trim" );
		writer.writeNamespace(null, "urn:tolven-org:trim:4.0");
//		sb.append("<trim xmlns=\"urn:tolven-org:trim:4.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
//				" xsi:schemaLocation=\"urn:tolven-org:trim:4.0 http://tolven.org/xsd/trim4\">");sb.append("\n");
//
		
		writer.writeStartElement("extends");
		writer.writeCharacters("obs/evn/breastdiagnosis");
		writer.writeEndElement(); // extends

		writer.writeStartElement("name");
		writer.writeCharacters(generateName( fields ));
		writer.writeEndElement(); // name

		writer.writeStartElement("description");
		writer.writeCharacters(fields[FullySpecifiedName]);
		writer.writeEndElement();//description
		writeAct( fields, writer );
		writer.writeEndElement();// Trim
	}

	public static void writeAct( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("act");
		writer.writeAttribute("classCode", "OBS");
		writer.writeAttribute("modeCode", "EVN");
		writeCode( fields, writer);
		writer.writeEndElement();
	}
	
/*
 		<observation>
			<value>
				<label>Diagnosis</label>
				<CE>
					<code>@string02@</code>
					<codeSystemName>UMLS</codeSystemName>
					<codeSystemVersion>2007AA</codeSystemVersion>
					<displayName>@string01@</displayName>
				</CE>
			</value>
		</observation>
 */
	public static void writeCode( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("observation");
		{
			writer.writeStartElement("value");
			{
				writer.writeStartElement("CE");
				{
					writer.writeStartElement("displayName");
					writer.writeCharacters( fields[FullySpecifiedName] );
					writer.writeEndElement();
					writer.writeStartElement("code");
					writer.writeCharacters(fields[SCTID]);
					writer.writeEndElement();
					writer.writeStartElement("codeSystem");
					writer.writeCharacters("2.16.840.1.113883.6.96");
					writer.writeEndElement();
					writer.writeStartElement("codeSystemName");
					writer.writeCharacters("SNOMED-CT");
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	
	/**
	 * Load problems. Note: This file is in ISO-8859-1 which is not supported by Tolven's Trim so
	 * the file is converted to UTF-8 on the way in.
	 * @param fileName
	 * @throws Exception 
	 */
	public void load(String fileName) throws Exception {
		TolvenLogger.info("Uploading UMLS problem list from: " + fileName, LoadBreastProblems.class);
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(new FileInputStream( new File(fileName)),"ISO-8859-1"));
        String record;
        String fields[];
        int rowCount = 0;
        while (reader.ready()) {
            rowCount++;
            record = reader.readLine();
            // Might break out early if requested
            if (rowCount > getIterationLimit()) {
        		TolvenLogger.info( "Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadBreastProblems.class);
            	break;
            }
            // Skip the heading line
            if (rowCount==1) continue;
            fields  = record.split("\\t",5);
		   	StringWriter bos = new StringWriter();
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = factory.createXMLStreamWriter(bos);
			writer.writeStartDocument("ISO-8859-1", "1.0" );
			generateTrim( fields, writer );
			writer.writeEndDocument();
//			writer.flush();
			writer.close();
			bos.close();
			/*String trimName = */generateName( fields);
//			TolvenLogger.info( "Load Trim: " + trimName, LoadBreastProblems.class );
            createTrimHeader(bos.toString());
			TolvenLogger.info(bos.toString(), LoadBreastProblems.class);
        }
		TolvenLogger.info( "Count of Problems uploaded: " + (rowCount-1), LoadBreastProblems.class);
		TolvenLogger.info( "Activating headers... ", LoadBreastProblems.class);
		activate();

	}

	/*
	<?xml version='1.0' encoding='ISO-8859-1'?>
	<trim xmlns="urn:tolven-org:trim:4.0">
		<extends>obs/evn/diagnosis</extends>
		<name>problem/SCTID-274945004</name>
		<description>AA amyloidosis (disorder)</description>
		<act classCode="OBS" modeCode="EVN">
			<observation>
				<value>
					<CE>
						<displayName>AA amyloidosis (disorder)</displayName>
						<code>274945004</code>
						<codeSystem>2.16.840.1.113883.6.96</codeSystem>
						<codeSystemName>SNOMED-CT</codeSystemName>
					</CE>
				</value>
			</observation>
		</act>
	</trim>
	*/
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
        if (args.length < 1 ) {
            System.out.println( "Arguments: configDirectory");
            return;
        }
//		LoadBreastProblems ld = new LoadBreastProblems(args[0]);
//		ld.login("admin", "sysadmin");
//		ld.load(args[0]);
	}	
}
