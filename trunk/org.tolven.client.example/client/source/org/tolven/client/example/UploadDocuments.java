package org.tolven.client.example;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Trim;


public class UploadDocuments extends SendTolvenMessage {

	public UploadDocuments(String configDir) throws IOException {
		super(configDir);
		TolvenLogger.initialize();
	}
	
	private static class DocFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.endsWith(".png")) return true;
			if (name.endsWith(".gif")) return true;
			if (name.endsWith(".jpg")) return true;
			if (name.endsWith(".jpeg")) return true;
			return false;
		}
    }
	
	/**
	 * Create a new Trim based on a trim template
	 * @return
	 * @throws Exception 
	 */
	public void createFaxTrim( File file ) throws Exception {
		Trim trim = getTrimBean().findTrim( "doc/fax");
		sendMessage( trim, file );
	}
	
	/**
	 * Upload all available trim in the named directory to server
	 * @param directory from which trim is uploaded
	 * @throws Exception
	 */
	public void uploadFromDirectory( String directory ) throws Exception {
		File dir = new File( directory );
		File files[] = dir.listFiles(new DocFilter()); 
		if (files!=null ) {
			for (File file : files) {
				beginTransaction();
				createFaxTrim( file );
				commitTransaction();
			}
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 5 ) {
			System.out.println( "Arguments: UserId password accountId docDirectory configDirectory");
			return;
		}
        UploadDocuments ud = new UploadDocuments(args[4]);
		ud.setupUser(args[0], args[1] );
		ud.setAccountId(Long.parseLong(args[2]));
		ud.uploadFromDirectory(args[3]);

	}

}
