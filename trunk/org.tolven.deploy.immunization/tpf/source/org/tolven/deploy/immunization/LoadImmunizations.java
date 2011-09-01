package org.tolven.deploy.immunization;

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

public class LoadImmunizations extends LoadRESTfulClient {
    
	private String fileName;

    public LoadImmunizations(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }
    
	public void generateTrim( String fields[], XMLStreamWriter writer  ) throws XMLStreamException {
		writer.writeStartElement("trim" );
		writer.writeNamespace(null, "urn:tolven-org:trim:4.0");
		
		writer.writeStartElement("extends");
		writer.writeCharacters("sbadm/rqo/immunization");
		writer.writeEndElement(); // extends

		writer.writeStartElement("name");
		writer.writeCharacters("immunization/" + fields[2]);
		writer.writeEndElement(); // name

		writer.writeStartElement("description");
		writer.writeCharacters(fields[1]);
		writer.writeEndElement();//description

		// writeAct( fields, writer );
		writeCode( fields, writer);
		writer.writeEndElement();// Trim
	}
	
	public static void writeAct( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("act");
		writer.writeAttribute("classCode", "SBADM");
		writer.writeAttribute("moodCode", "RQO");
		writeCode( fields, writer);
		writer.writeEndElement();
	}
	
	public static void writeCode( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
    	writer.writeStartElement("valueSet");
		writer.writeAttribute("name", "material");	
		writer.writeStartElement("CE");
			writer.writeStartElement("code");
			writer.writeCharacters(fields[2]);
			writer.writeEndElement();
			writer.writeStartElement("codeSystem");
			writer.writeCharacters("2.16.840.1.113883.6.96");
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
	 * Load Immunizations
	 * @param file
	 * @throws Exception
	 */
	public void load(File file) throws Exception {
        TolvenLogger.info("Uploading immunization list from: " + fileName, LoadImmunizations.class);
        try {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String record;
            String fields[];
            int rowCount = 0;

            while (reader.ready()) {
                rowCount++;
                record = reader.readLine();
                // Might break out early is requested
                if (rowCount > getIterationLimit()) {
                    TolvenLogger.info("Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadImmunizations.class);
                    break;
                }
                // Skip the heading lines
                if (rowCount == 1)
                    continue;
                fields = record.split("\\|", 3);
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
                //			TolvenLogger.info(bos.toString(), LoadImmunizations.class);
            }
            TolvenLogger.info("Count of Immunizations uploaded: " + (rowCount - 1), LoadImmunizations.class);
            TolvenLogger.info("Activating headers... ", LoadImmunizations.class);
            activate();
        } finally {
            logout();
        }
	}
	
	/**
	 * Load Immunizations. 
	 * @param fileName
	 * @throws Exception 
	 */
	public void load(String fileName ) throws Exception {
		//load(new File(fileName));
	}
	
	public static void main( String[] args ) throws Exception {
        if (args.length < 1 ) {
            System.out.println( "Arguments: configDirectory");
            return;
        }
		//LoadImmunizations la = new LoadImmunizations(args[0]);
		//la.login("admin", "sysadmin");
		//la.load(args[1]);
	}

}
