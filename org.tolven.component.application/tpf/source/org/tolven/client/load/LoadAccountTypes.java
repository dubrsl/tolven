package org.tolven.client.load;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;

import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class LoadAccountTypes extends RESTfulClient{
	private Map<String, String> appFiles; 
	
	/**
	 * Constructor
	 * @param configDir
	 */
	public LoadAccountTypes(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        appFiles = new HashMap<String, String>();
        init(userId, password, appRestfulURL, authRestfulURL);
    }
    
	
    /**
     * Load up a file into a string
     * @param filename The filename of the file to read
     * @return A String containing the contents of the file
     */
    public String loadFile( File file ) {
		FileReader reader = null;
		StringWriter writer = null;
		try {
			reader = new FileReader(file);
			writer = new StringWriter( );
			while ( true ) {
				int c = reader.read();
				if (c < 0) break;
				writer.write( c );
			}
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException( "Error reading file " + file, e);
		} finally {
			if (reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
    }
    
	/**
	 * Scan a directory for files with application.xml extensions each file found.
	 * @param directory in which to look for application.xml files
	 */
	public void addDirectory( File dir ) {
		File files[] = dir.listFiles(); 
		for (File file : files) {
			if (file.isDirectory()) {
				addDirectory( file );
			} else {
				String normalPath = file.getPath().replace('\\', '/');
				appFiles.put(normalPath, loadFile( file));
			}
		}
	}
	
	/**
     * Upload the accumulated list of application files.
     */
    public void uploadApplications(String userId) throws Exception {
        try {
            if (!appFiles.keySet().isEmpty()) {
                WebResource webResource = getAppWebResource().path("loader/loadApplications");
                Multipart multipart = new MimeMultipart();
                for (String key : appFiles.keySet()) {
                    String xml = appFiles.get(key);
                    MimeBodyPart bodyPart = new MimeBodyPart();
                    bodyPart.setText(xml, "UTF-8", "xml");
                    bodyPart.setFileName(key);
                    bodyPart.setHeader("Content-Type", MediaType.TEXT_XML);
                    multipart.addBodyPart(bodyPart);
                }
                ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA).cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, multipart);
                if (response.getStatus() != 200) {
                    throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
                }
            }
       } catch (Exception ex) {
                //TolvenLogger.info("Rolling back transaction : upload applications for: " + userId, LoadAccountTypes.class);
                throw new RuntimeException("Error uploading applications for: " + userId, ex);
        }
    }

}
