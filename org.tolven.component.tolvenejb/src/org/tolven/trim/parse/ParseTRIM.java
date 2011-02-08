package org.tolven.trim.parse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;
import org.tolven.xml.ParseXML;

public class ParseTRIM extends ParseXML {
	public static String EXTENSION = ".trim.xml";
	public String baseDir;
	
	@Override
	protected String getParsePackageName() {
		return "org.tolven.trim";
	}
/*
	private static class TrimFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.endsWith(EXTENSION)) return true;
			return false;
		}
    }
	*/
	/**
	 * Return the directory containing Trim instances
	 * @return
	 */
	public String getTrimDir() {
		return baseDir + File.separator + "trim";
	}

	/**
	 * Trim instances in sam directory as trim
	 * @return
	 */
	public String getTrimInstanceDir() {
		return baseDir + File.separator + "trim";
	}

	/**
	 * Given an input stream, parse and upload the contents of the application file 
	 * @param is InputStream containing .application.xml
	 * @return The resulting Application object
	 * @throws Exception 
	 */
	public Trim parseStream( InputStream input ) throws Exception {
		Trim trim = (Trim) getUnmarshaller().unmarshal( input);
//		TolvenLogger.info( "Parsing trim: " + trim.getName(), ParseTRIM.class);
		return trim;
	}
	
	/**
	 * Given an application.xml file, create an input stream to the file and load the resulting stream
	 * @param file
	 * @throws Exception
	 */
	public Trim parseFile( File file ) throws Exception {
		TolvenLogger.info( "Processing file: " + file, ParseTRIM.class);
		InputStream input = new BufferedInputStream( new FileInputStream(file));
//		String fileName = file.getName().substring(0, file.getName().length()-EXTENSION.length());
		Trim trim = parseStream( input );
		input.close();
		return trim;
	}
	@Override
	protected Unmarshaller getUnmarshaller() throws JAXBException {
		Unmarshaller u = super.getUnmarshaller();
    	u.setProperty("com.sun.xml.bind.ObjectFactory", new TrimFactory());
		return u;
	}

}
