

package org.tolven.deploy.SubstanceManufacturers;



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

public class LoadSubstanceManufacturer extends LoadRESTfulClient {
	String fileName;
	public static final int MVX_CODE=0;
	public static final int Manufacturer_name=1;
	public static final int Notes =2;
	public static final int Status=3;
	public static final int Mdate=4;
	public static final int Manufacturer_id=5;
	public static final int BATCH_SIZE = 100;
	List<String> trims = new ArrayList<String>(BATCH_SIZE);
	
	public LoadSubstanceManufacturer(String userId,char[] password,String appRestfulURL,String authRestfulURL){
		init(userId,password,appRestfulURL,authRestfulURL);
	}	
	
	public String generateName( String fields[] ) {
		return  (new StringBuilder()).append("substanceManufacturer/-").append(fields[5]).toString();
	}
	
	public void generateTrim(String fields[], XMLStreamWriter writer) throws XMLStreamException{
		writer.writeStartElement("trim" );
		writer.writeAttribute("xmlns", "urn:tolven-org:trim:4.0");
		writer.writeAttribute("xmlns", "", "xsi","http://www.w3.org/2001/XMLSchema-instance");
		writer.writeAttribute("xsi", "", "schemaLocation", "urn:tolven-org:trim:4.0 http://tolven.org/xsd/trim4");
		
		writer.writeStartElement("menu");
		writer.writeCharacters("global:manufacturerMenu");
		writer.writeEndElement();
		
		writer.writeStartElement("name");
		writer.writeCharacters(generateName( fields ));
		writer.writeEndElement();
		
		writer.writeStartElement("description");
		writer.writeCharacters(fields[2]);
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
	
	public static void writeCode( String fields[], XMLStreamWriter writer) throws XMLStreamException{		
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
					writer.writeCharacters(fields[5]);		
					writer.writeEndElement();
	
					writer.writeStartElement("codeSystemName");		
					writer.writeCharacters("MVX");						
					writer.writeEndElement();		
			writer.writeEndElement();		
	     writer.writeEndElement();		
	    writer.writeEndElement();	
	}
	
	public void load(File file) throws Exception{
		TolvenLogger.info("Uploading substanceManufacturer list from: " + file.getAbsolutePath(), LoadSubstanceManufacturer.class);
		try{
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
	                    TolvenLogger.info("Upload stopped early due to " + UPLOAD_LIMIT + " property being set", LoadSubstanceManufacturer.class);
	                    break;
	                }
	                // Skip the heading lines
	                if (rowCount == 1)
	                    continue;
	                fields = record.split("\\|",6);
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
	            }
	            TolvenLogger.info("Count of substanceManufacturers uploaded: " + (rowCount - 1), LoadSubstanceManufacturer.class);
	            TolvenLogger.info("Activating headers... ", LoadSubstanceManufacturer.class);
	            activate();			
		}finally{
			logout();
		}
		
	}	

}
