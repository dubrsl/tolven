package org.tolven.deploy.reportablediagnosisLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.LoadRESTfulClient;

public class LoadReportableDiagnosis extends LoadRESTfulClient {
	String fileName;
	
	public static final int SCTID = 0;	
	public static final int FullySpecifiedName = 1;	
	public static final int DateOfStatusChange = 2;	
	public static final int SubsetStatus = 3;	
	public static final int SNOMEDStatus = 4;	
	public static final int BATCH_SIZE = 100;
	List<String> trims = new ArrayList<String>(BATCH_SIZE);    
    
    public LoadReportableDiagnosis(String userId, char[] password, String appRestfulURL, String authRestfulURL){
    	init(userId, password, appRestfulURL, authRestfulURL);
    }
    
	public String generateName( String fields[] ) {
		return  (new StringBuilder()).append("reportablediagnosis/SCTID-").append(fields[0]).toString();
	}
	public void generateTrim( String fields[], XMLStreamWriter writer  ) throws XMLStreamException {
		writer.writeStartElement("trim" );
		writer.writeAttribute("xmlns", "urn:tolven-org:trim:4.0");
		writer.writeAttribute("xmlns", "", "xsi","http://www.w3.org/2001/XMLSchema-instance");
		writer.writeAttribute("xsi", "", "schemaLocation", "urn:tolven-org:trim:4.0 http://tolven.org/xsd/trim4");
		
		writer.writeStartElement("menu");
		writer.writeCharacters("global:reportableDiagnosisMenu");
		writer.writeEndElement();		
		
		writer.writeStartElement("name");
		writer.writeCharacters(generateName( fields ));
		writer.writeEndElement();

		writer.writeStartElement("description");
		writer.writeCharacters(fields[1]);
		writer.writeEndElement();//description
		writeAct( fields, writer );
		writer.writeEndElement();// Trim
		
	}

	public static void writeAct( String fields[], XMLStreamWriter writer ) throws XMLStreamException {
		writer.writeStartElement("act");
		writer.writeAttribute("classCode", "OBS");
		writer.writeAttribute("moodCode", "EVN");
		writeCode( fields, writer);
		writer.writeEndElement();
	}	

	public static void writeCode( String fields[], XMLStreamWriter writer ) throws XMLStreamException {		
		writer.writeStartElement("observation");		
			writer.writeStartElement("value");		
				writer.writeStartElement("CE");		
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
				writer.writeEndElement();		
		     writer.writeEndElement();		
		writer.writeEndElement();
	}
	
	/**
	 * Load Diagnoses. Note: This file is in ISO-8859-1 which is not supported by Tolven's Trim so
	 * the file is converted to UTF-8 on the way in.
	 * @param fileName
	 * @throws Exception 
	 */
	public void load(File fileName ) throws Exception {
		TolvenLogger.info("Uploading UMLS Diagnosis list from: " + fileName.getAbsolutePath(), LoadReportableDiagnosis.class);
		try{
			//login(userId, password, authFile.getPath(), jndiProperties);		
	        BufferedReader reader;
	        //reader = new BufferedReader(new InputStreamReader(new FileInputStream( new File(fileName)),"ISO-8859-1"));
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
	        String record;
	        String fields[];
	        int rowCount = 0;
	        while (reader.ready()) {
	            rowCount++;
	            record = reader.readLine();
	            // Might break out early if requested
	            if (rowCount > getIterationLimit()) {
	        		TolvenLogger.info( "Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadReportableDiagnosis.class);
	        		System.out.println("row :"+ rowCount);
	        		System.out.println("Iteration :"+ getIterationLimit());
	            	break;
	            }
	            // Skip the heading line
	            if (rowCount==1) continue;
	            fields  = record.split("\\t",5);
			   	StringWriter bos = new StringWriter();
				XMLOutputFactory factory = XMLOutputFactory.newInstance();
				XMLStreamWriter writer = factory.createXMLStreamWriter(bos);
				writer.writeStartDocument("UTF-8", "1.0" );
				generateTrim( fields, writer );
				writer.writeEndDocument();
	//			writer.flush();
				writer.close();
				bos.close();
				createTrimHeader(bos.toString());
	        }
	        TolvenLogger.info("Count of ReportableDignosis uploaded: " + (rowCount - 1), LoadReportableDiagnosis.class);
            TolvenLogger.info("Activating headers... ", LoadReportableDiagnosis.class);
            activate();
		}finally{
			logout();
		}

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
		
	}
}
