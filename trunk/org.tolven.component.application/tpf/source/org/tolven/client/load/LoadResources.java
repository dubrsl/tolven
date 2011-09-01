package org.tolven.client.load;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.tolven.core.entity.Account;
import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
/**
 * Load resources from a directory and identity by a particular account
 * The baseDirectory supplied is for one application (accountType) such as c:/tolven-config/applications/echr.
 * All files in all subdirectories will be loaded. Directory paths are normalized to forward slash.
 * Therefore, the "name" of a resource might be something like: "rest:/wizard/observation.xhtml"
 * @author John Churin
 *
 */
public class LoadResources extends RESTfulClient {
	private String baseDir;	// What we're asked to load 
	/*
	private static class ResourceFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (dir.getName().equals("CVS")) return false;
			return true;
		}
    }
*/
	public LoadResources(String userId, char[] password, String appRestfulURL, String authRestfulURL, String baseDir) {
		this.baseDir = baseDir;
        init(userId, password, appRestfulURL, authRestfulURL);
	}
	
	/**
	 * Given an input stream, parse and upload the contents of the application file 
	 * @throws Exception 
	 */
	public byte[] loadStream( InputStream inputStream ) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(  ); 
		int b;
		while( (b = inputStream.read()) >=0 ) {
			bos.write(b);
		}
		byte[] ba = bos.toByteArray();
		inputStream.close();
		bos.close();
		return ba;
	}
	
	/**
	 * Given a file, create an input stream to the file and load the resulting stream as a resource.
	 * @param file
	 * @throws Exception
	 */
	public void loadFile( String subDir, File file, Account account ) throws Exception {
		String resourceName = subDir+ "/" + file.getName();
		InputStream inputStream = new FileInputStream( file );
		inputStream.close();
		TolvenLogger.info( "name: " + resourceName + " File: " + file.getPath(), LoadResources.class);
        WebResource webResource = getAppWebResource().path("loader/persistResource");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.putSingle("accountId", String.valueOf(account.getId()));
        formData.putSingle("resourceName", resourceName);
        // How is byte[] portably transmitted
        formData.putSingle("reportType", new String(loadStream(inputStream)));
        ClientResponse response = webResource.cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error uploading applications " + "Error " + response.getStatus());
        }
//		InputStream input = new BufferedInputStream( new FileInputStream(file));
//		String fileName = file.getName().substring(0, file.getName().length()-EXTENSION.length());
//		loadStream( fileName, input );
	}

	/**
	 * Scan a directory for files with menu.xml extension and cal loadFile for each file found.
	 * @param directory
	 * @throws Exception
	 */
	public void loadFromDirectory( String subDir, Account account ) throws Exception {
//		beginTransaction();
//		TolvenLogger.info( "[loadFromDirectory]", LoadResources.class);
		if ("/CVS".equals(subDir)) return;
		File dir = new File( baseDir+subDir );
		File files[] = dir.listFiles();
		if (files==null) throw new IllegalArgumentException( "Invalid applications directory: " + baseDir);
		for (File file : files) {
			if (file.isDirectory()) {
				loadFromDirectory(subDir + "/" + file.getName(), account);
			} else {
				loadFile( subDir, file, account );
			}
		}
//		commitTransaction();
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println( "LoadResources <config-directory> <application-directory>" );
			return;
		}
		Account account = new Account();
		// Phase I create accountTypes and Menustructure
		LoadResources lr = new LoadResources(args[0], args[1].toCharArray(), args[2], args[3], args[4]);
		account.setId(1234);
		lr.loadFromDirectory( "echr", account );
		TolvenLogger.info( "Loaded resources from " + lr.getBaseDir() + " for account " + account.getId(), LoadResources.class);
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}


}
