package org.tolven.deploy.ImageOrders;

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

public class LoadImageOrder extends LoadRESTfulClient {
	String fileName;
	public static final int OrderNo = 0;
	public static final int Proc_Short_Name = 6;
	public static final int Code = 7;
	public static final int CodeSystemName = 3;
	public static final int CodeSystemVersion = 8;
	public static final int Proc_Name = 5;
	public static final int BATCH_SIZE = 100;
	List<String> trims = new ArrayList<String>(BATCH_SIZE);

    public LoadImageOrder(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }	
    
	public String generateName( String fields[] ) {
		return "ImageOrders/CPT-" + fields[Code];
	}
	
	public void generateTrim( String fields[], XMLStreamWriter writer  ) throws XMLStreamException {
		writer.writeStartElement("trim" );
		writer.writeNamespace(null, "urn:tolven-org:trim:4.0");
//		sb.append("<trim xmlns=\"urn:tolven-org:trim:4.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
//				" xsi:schemaLocation=\"urn:tolven-org:trim:4.0 http://tolven.org/xsd/trim4\">");sb.append("\n");
//
		writer.writeStartElement("extends");
		writer.writeCharacters("obs/evn/imageOrder");
		//writer.writeCharacters("imageOrderDoc");
		writer.writeEndElement(); // extends

		writer.writeStartElement("name");
		writer.writeCharacters(generateName(fields));
		writer.writeEndElement(); // name

		writer.writeStartElement("description");
		writer.writeCharacters(fields[Proc_Short_Name]);
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
				writer.writeCharacters(fields[Proc_Short_Name]);
				writer.writeEndElement();
				writer.writeStartElement("code");
				writer.writeCharacters(fields[Code]);
				writer.writeEndElement();
				writer.writeStartElement("codeSystemName");
				writer.writeCharacters("CPT");
				writer.writeEndElement();
				writer.writeStartElement("codeSystemVersion");
				writer.writeCharacters(fields[CodeSystemVersion]);
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		writer.writeEndElement();
		writer.writeStartElement("text");
		{
			writer.writeStartElement("ST");
			writer.writeCharacters(fields[Proc_Name]);
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	
	/**
	 * Load ImageOrders. Note: This file is in ISO-8859-1 which is not supported by Tolven's Trim so
	 * the file is converted to UTF-8 on the way in.
	 * @param fileName
	 * @throws Exception 
	 */
	public void load(String fileName) throws Exception {
		TolvenLogger.info("Uploading Image Orders list from: " + fileName, LoadImageOrder.class);
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
        		TolvenLogger.info( "Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadImageOrder.class);
            	break;
            }
            // Skip the heading line
            if (rowCount==1) continue;
            fields  = record.split("\\t", 9);
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
			createTrimHeader(bos.toString());
			/*
			trims.add(bos.toString());
			if (trims.size() >= BATCH_SIZE ) {
				TolvenLogger.info( "Load batch", LoadImageOrder.class );
				getTrimBean().createTrimHeaders(trims.toArray(new String[0]), null, "LoadImageOrders", true);
				trims.clear();
			}
			*/
        }
        // Send unfinished batch, if any.
        /*
		if (trims.size() > 0 ) {
			getTrimBean().createTrimHeaders(trims.toArray(new String[0]), null, "LoadImageOrders", true);
		}
		TolvenLogger.info( "Count of Image Orders uploaded: " + (rowCount-1), LoadImageOrder.class);
		TolvenLogger.info( "Activating headers... ", LoadImageOrder.class);
		getTrimBean().queueActivateNewTrimHeaders();
        */
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
		//LoadImageOrder ld = new LoadImageOrder(args[0]);
		//ld.login("admin", "sysadmin");
		//ld.load(args[1]);
	}
}
