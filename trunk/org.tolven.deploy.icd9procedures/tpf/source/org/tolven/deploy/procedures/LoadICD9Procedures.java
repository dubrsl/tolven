package org.tolven.deploy.procedures;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.LoadRESTfulClient;

public class LoadICD9Procedures extends LoadRESTfulClient {
	protected Logger logger = Logger.getLogger(getClass());
	
    public LoadICD9Procedures(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }
    
	public void generateTrim( String procId,String procName, XMLStreamWriter writer  ) throws XMLStreamException {
		writer.writeStartElement("trim" );
		writer.writeNamespace(null, "urn:tolven-org:trim:4.0");
		
		writer.writeStartElement("extends");
		writer.writeCharacters("icd9procedure");
		writer.writeEndElement(); // extends

		writer.writeStartElement("name");
		writer.writeCharacters("icd9procedure/" + procId);
		writer.writeEndElement(); // name

		writer.writeStartElement("description");
		writer.writeCharacters(procName);
		writer.writeEndElement();//description

		writeAct( procId,procName, writer );
		writer.writeEndElement();// Trim
	}
	
	public static void writeAct( String procId,String procName, XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("act");
		writer.writeAttribute("classCode", "PROC");
		writer.writeAttribute("moodCode", "EVN");
		writeCode( procId,procName, writer);
		writer.writeEndElement();
	}
	
	public static void writeCode( String procId,String procName, XMLStreamWriter writer ) throws XMLStreamException {
    	writer.writeStartElement("code");
		// writer.writeAttribute("name", "material");	
		writer.writeStartElement("CE");
			writer.writeStartElement("code");
			writer.writeCharacters(procId);
			writer.writeEndElement();
			writer.writeStartElement("codeSystem");
			writer.writeCharacters("2.16.840.1.113883.6.104");
			writer.writeEndElement();
			writer.writeStartElement("codeSystemName");
			writer.writeCharacters("ICD-9CM");
			writer.writeEndElement();
			writer.writeStartElement("displayName");
			writer.writeCharacters(procName );
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
        logger.info("Uploading procedure list from: " + fileName);
        try {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
            String record;
            int rowCount = 0;

            while (reader.ready()) {
                rowCount++;
                record = reader.readLine();
                // Might break out early is requested
                if (rowCount > getIterationLimit()) {
                    logger.info("Upload stopped early due to " + UPLOAD_LIMIT + " property being set");
                    break;
                }
                // Skip the heading line
                if (rowCount == 1)
                    continue;
                String procId = record.substring(0,record.indexOf(" "));
                String procName = record.substring(record.indexOf(" ")+1);
                StringWriter bos = new StringWriter();
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                XMLStreamWriter writer = factory.createXMLStreamWriter(bos);
                writer.writeStartDocument("UTF-8", "1.0");
                generateTrim(procId,procName.trim(), writer);
                writer.writeEndDocument();
                //			writer.flush();
                writer.close();
                bos.close();
                createTrimHeader(bos.toString());
                //			TolvenLogger.info(bos.toString(), LoadProcedures.class);
            }
            logger.info("Count of Procedures uploaded: " + (rowCount - 1));
            logger.info("Activating headers... ");
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
