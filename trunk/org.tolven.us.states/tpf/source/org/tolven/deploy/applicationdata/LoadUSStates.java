package org.tolven.deploy.applicationdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.ws.rs.core.MediaType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.LoadRESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class LoadUSStates extends LoadRESTfulClient {
    String fileName;

    public LoadUSStates(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }

    /**
     * Load Application Data. 
     * @param fileName
     * @throws Exception 
     */
    public void load(String fileName) throws Exception {
        TolvenLogger.info("Uploading State Names from: " + fileName, LoadUSStates.class);
        try {
            BufferedReader reader;
            String fields[];
            
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), "ISO-8859-1"));
            String nextLine=reader.readLine();
            
            while (nextLine!=null) {
                
                fields = nextLine.split(",");
                StringWriter bos = new StringWriter();
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                XMLStreamWriter writer = factory.createXMLStreamWriter(bos);
                
                /*
                <?xml version='1.0' encoding='ISO-8859-1'?>
                <StateNames>
                	<StateCode></StateCode>
                	<StateName></StateName>
                </StateNames>	
                */
                
                writer.writeStartDocument("ISO-8859-1", "1.0");
                writer.writeStartElement("StateNames");
                writer.writeStartElement("StateCode");
        		writer.writeCharacters(fields[0].trim());
        		writer.writeEndElement();
        		writer.writeStartElement("StateName");
        		writer.writeCharacters(fields[1].trim());
        		writer.writeEndElement();
                writer.writeEndElement();
                writer.writeEndDocument();
                //			writer.flush();
                writer.close();
                bos.close();

                loadStateNames(bos.toString());
                
                nextLine=reader.readLine();

            }
        } finally {
            logout();
        }
    }
    
    public void loadStateNames(String xml) {
		WebResource webResource = getAppWebResource().path("loader/loadStateNames");
        ClientResponse response = webResource.cookie(getTokenCookie()).type(MediaType.APPLICATION_XML).entity(xml).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
        }
	  }

}
