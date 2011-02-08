package org.tolven.deploy.allergies;

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

public class LoadAllergies extends LoadRESTfulClient {
    
    public LoadAllergies(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }
    
    public void generateTrim(String fields[], XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("trim");
        writer.writeNamespace(null, "urn:tolven-org:trim:4.0");

        writer.writeStartElement("extends");
        writer.writeCharacters("obs/evn/allergy");
        writer.writeEndElement(); // extends

        writer.writeStartElement("name");
        writer.writeCharacters("allergy/" + fields[0]);
        writer.writeEndElement(); // name

        writer.writeStartElement("description");
        writer.writeCharacters(fields[1]);
        writer.writeEndElement();//description

        writeAct(fields, writer);

        writer.writeEndElement();// Trim
    }

    public static void writeAct(String fields[], XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("act");
        writer.writeAttribute("classCode", "OBS");
        writer.writeAttribute("modeCode", "EVN");
        writeCode(fields, writer);
        writer.writeEndElement();
    }

    public static void writeCode(String fields[], XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("observation");
        {
            writer.writeStartElement("value");
            {
                writer.writeStartElement("CE");
                {
                    writer.writeStartElement("displayName");
                    writer.writeCharacters(fields[1]);
                    writer.writeEndElement();
                    writer.writeStartElement("code");
                    writer.writeCharacters(fields[0]);
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
     * Load allergies. 
     * @param fileName
     * @throws Exception 
     */
    public void load(String fileName) throws Exception {
        TolvenLogger.info("Uploading allergy list from: " + fileName, LoadAllergies.class);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
        String record;
        String fields[];
        int rowCount = 0;
        while (reader.ready()) {
            rowCount++;
            record = reader.readLine();
            // Might break out early is requested
            if (rowCount > getIterationLimit()) {
                TolvenLogger.info("Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadAllergies.class);
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
            TolvenLogger.info(bos.toString(), LoadAllergies.class);
        }
        TolvenLogger.info("Count of allergies uploaded: " + (rowCount - 1), LoadAllergies.class);
        TolvenLogger.info("Activating headers... ", LoadAllergies.class);
        activate();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Arguments: configDirectory");
            return;
        }
        //LoadAllergies la = new LoadAllergies();
        //la.login("admin", "sysadmin");
        //la.load(args[0]);
    }
}
