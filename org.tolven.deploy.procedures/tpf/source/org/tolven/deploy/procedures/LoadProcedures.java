package org.tolven.deploy.procedures;
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

public class LoadProcedures extends LoadRESTfulClient {
	
    public LoadProcedures(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }
    
	public void generateTrim( String fields[], XMLStreamWriter writer  ) throws XMLStreamException {
		writer.writeStartElement("trim" );
		writer.writeNamespace(null, "urn:tolven-org:trim:4.0");
		
		writer.writeStartElement("extends");
		writer.writeCharacters("px");
		writer.writeEndElement(); // extends

		writer.writeStartElement("name");
		writer.writeCharacters("px/" + fields[0]);
		writer.writeEndElement(); // name

		writer.writeStartElement("description");
		writer.writeCharacters(fields[1]);
		writer.writeEndElement();//description

		writeAct( fields, writer );
		writer.writeEndElement();// Trim
	}
	
	public static void writeAct( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("act");
		writer.writeAttribute("classCode", "PROC");
		writer.writeAttribute("moodCode", "EVN");
		writeCode( fields, writer);
		writer.writeEndElement();
	}
	
	public static void writeCode( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
    	writer.writeStartElement("code");
		// writer.writeAttribute("name", "material");	
		writer.writeStartElement("CE");
			writer.writeStartElement("code");
			writer.writeCharacters(fields[0]);
			writer.writeEndElement();
			writer.writeStartElement("codeSystem");
			writer.writeCharacters("2.16.840.1.113883.6.56");
			writer.writeEndElement();
			writer.writeStartElement("codeSystemName");
			writer.writeCharacters("SNOMED-CT");
			writer.writeEndElement();
			writer.writeStartElement("displayName");
			writer.writeCharacters(fields[1] );
			writer.writeEndElement();
		writer.writeEndElement();	
		writer.writeEndElement();
	}
	/**
	 * Load Immunizations. 
	 * @param fileName
	 * @throws Exception 
	 */
	public void load(String fileName) throws Exception {
        TolvenLogger.info("Uploading procedure list from: " + fileName, LoadProcedures.class);
        try {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
            String record;
            String fields[];
            int rowCount = 0;

            while (reader.ready()) {
                rowCount++;
                record = reader.readLine();
                // Might break out early is requested
                if (rowCount > getIterationLimit()) {
                    TolvenLogger.info("Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadProcedures.class);
                    break;
                }
                // Skip the heading line
                if (rowCount == 1)
                    continue;
                fields = record.split("\\|", 2);
                StringWriter bos = new StringWriter();
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                XMLStreamWriter writer = factory.createXMLStreamWriter(bos);
                writer.writeStartDocument("UTF-8", "1.0");
                generateTrim(fields, writer);
                writer.writeEndDocument();
                //			writer.flush();
                writer.close();
                bos.close();
                createTrimHeader(bos.toString());
                //			TolvenLogger.info(bos.toString(), LoadProcedures.class);
            }
            TolvenLogger.info("Count of Procedures uploaded: " + (rowCount - 1), LoadProcedures.class);
            TolvenLogger.info("Activating headers... ", LoadProcedures.class);
            activate();
        } finally {
            logout();
        }
    }
	
	public static void main( String[] args ) throws Exception {
        if (args.length < 1 ) {
            System.out.println( "Arguments: configDirectory");
            return;
        }
		//LoadProcedures la = new LoadProcedures(args[0]);
		//la.login("admin", "sysadmin");
		//la.load(args[1]);
	}

}
