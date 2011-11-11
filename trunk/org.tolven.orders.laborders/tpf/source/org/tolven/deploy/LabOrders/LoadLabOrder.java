package org.tolven.deploy.LabOrders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.restful.client.LoadRESTfulClient;
import org.tolven.logging.TolvenLogger;

public class LoadLabOrder extends LoadRESTfulClient {
	String fileName;
	public static final int CONVENIENCE_NAME = 5;
	public static final int LOINC_VER = 2;
	public static final int LOINC_CODE = 3;
	public static final int LOINC_LONG_COMMON_NAME = 6;
	public static final int BATCH_SIZE = 100;
	List<String> trims = new ArrayList<String>(BATCH_SIZE);

    public LoadLabOrder(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }	
	
	public String generateName( String fields[] ) {
		return "LabOrders/LOINC-"+fields[LOINC_CODE];
	}
	
	public void generateTrim( String fields[], XMLStreamWriter writer  ) throws XMLStreamException {
		writer.writeStartElement("trim" );
		writer.writeNamespace(null, "urn:tolven-org:trim:4.0");
//		sb.append("<trim xmlns=\"urn:tolven-org:trim:4.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
//				" xsi:schemaLocation=\"urn:tolven-org:trim:4.0 http://tolven.org/xsd/trim4\">");sb.append("\n");
//
		writer.writeStartElement("extends");
		writer.writeCharacters("obs/evn/labOrder");
		//writer.writeCharacters("labOrderDoc");
		writer.writeEndElement(); // extends

		writer.writeStartElement("name");
		writer.writeCharacters(generateName(fields));
		writer.writeEndElement(); // name

		writer.writeStartElement("description");
		writer.writeCharacters(fields[CONVENIENCE_NAME]);
		writer.writeEndElement();//description
		writeAct(fields, writer);
		writer.writeEndElement();// Trim
	}

	public static void writeAct( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("act");
		writer.writeAttribute("classCode", "OBS");
		writer.writeAttribute("moodCode", "RQO");
		writeCode(fields, writer);
		writer.writeEndElement();
	}
	
	public static void writeCode( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("code");
		{
			writer.writeStartElement("CD");
			{
				writer.writeStartElement("displayName");
				writer.writeCharacters(fields[CONVENIENCE_NAME]);
				writer.writeEndElement();
				writer.writeStartElement("code");
				writer.writeCharacters(fields[LOINC_CODE]);
				writer.writeEndElement();
				writer.writeStartElement("codeSystemName");
				writer.writeCharacters("LOINC");
				writer.writeEndElement();
				writer.writeStartElement("codeSystemVersion");
				writer.writeCharacters(fields[LOINC_VER]);
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		writer.writeEndElement();
		writer.writeStartElement("text");
		{
			writer.writeStartElement("ST");
			writer.writeCharacters(fields[LOINC_LONG_COMMON_NAME]);
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	
	/**
	 * Load LabOrders. Note: This file is in ISO-8859-1 which is not supported by Tolven's Trim so
	 * the file is converted to UTF-8 on the way in.
	 * @param fileName
	 * @throws Exception 
	 */
	public void load(String fileName) throws Exception {
		TolvenLogger.info("Uploading Lab Orders list from: " + fileName, LoadLabOrder.class);
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
        		TolvenLogger.info( "Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadLabOrder.class);
            	break;
            }
            // Skip the heading line
            if (rowCount==1) continue;
            fields  = record.split("\\t",13);
		   	StringWriter bos = new StringWriter();
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = factory.createXMLStreamWriter(bos);
			writer.writeStartDocument("ISO-8859-1", "1.0" );
			generateTrim( fields, writer );
			writer.writeEndDocument();
//			writer.flush();
			writer.close();
			bos.close();
			/*String trimName = */generateName(fields);
			//TolvenLogger.info("Lab Order Trim: " + bos.toString(), LoadLabOrder.class);
			createTrimHeader(bos.toString());
			/*
			trims.add(bos.toString());
			if (trims.size() >= BATCH_SIZE ) {
				TolvenLogger.info( "Load batch", LoadLabOrder.class );
				getTrimBean().createTrimHeaders(trims.toArray(new String[0]), null, "LoadLabOrders", true);
				trims.clear();
			}
			*/			
        }
        /*
        // Send unfinished batch, if any.
		if (trims.size() > 0 ) {
			getTrimBean().createTrimHeaders(trims.toArray(new String[0]), null, "LoadLabOrders", true);
		}
		*/
		TolvenLogger.info( "Count of Lab Orders uploaded: " + (rowCount-1), LoadLabOrder.class);
		TolvenLogger.info( "Activating headers... ", LoadLabOrder.class);
		// getTrimBean().queueActivateNewTrimHeaders();
		activate();        
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
        if (args.length < 1 ) {
            System.out.println( "Arguments: configDirectory");
            return;
        }
		//LoadLabOrder ld = new LoadLabOrder(args[0]);
		//ld.login("admin", "sysadmin");
        //ld.load(args[1]);
	}
}
