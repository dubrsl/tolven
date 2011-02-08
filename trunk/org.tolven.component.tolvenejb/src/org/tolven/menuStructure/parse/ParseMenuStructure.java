/*
 *  Copyright (C) 2008 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.menuStructure.parse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.tolven.logging.TolvenLogger;
import org.tolven.menuStructure.Application;
import org.tolven.trim.ex.TRIMException;
import org.tolven.xml.ParseXML;

/**
 * Common methods for parsing Tolven application.xml files.
 * Ultimately, these XML application specifications end up in the database as metadata (rows and columns).
 * @author John Churin
 */
public class ParseMenuStructure extends ParseXML {
	public static String EXTENSION = ".application.xml";
	
	/**
	 * Construct a Tolven application XML parser
	 * @throws Exception
	 */
	public ParseMenuStructure() {
	}

	protected String getParsePackageName() {
		return 	"org.tolven.menuStructure";
	}
	
	/**
	 * Given an input stream, parse and upload the contents of the application file 
	 * @param is InputStream containing .application.xml
	 * @return The resulting Application object
	 * @throws JAXBException 
	 * @throws Exception 
	 */
	public Application parseStream( InputStream input ) throws JAXBException {
        Application application = (Application) getUnmarshaller().unmarshal( input);
		return application;
	}
	
	/**
	 * Given a reader, parse and upload the contents of the application file 
	 * @param A reader containing .application.xml
	 * @return The resulting Application object
	 * @throws JAXBException 
	 * @throws Exception 
	 */
	public Application parseReader( Reader reader ) throws JAXBException {
        Application application = (Application) getUnmarshaller().unmarshal( reader );
		return application;
	}
	
	/**
	 * Given an application.xml file, create an input stream to the file and load the resulting stream
	 * @param file
	 * @throws IOException 
	 * @throws JAXBException 
	 * @throws Exception
	 */
	public Application parseFile( File file ) throws IOException, JAXBException {
		TolvenLogger.info( "Reading file: " + file, ParseMenuStructure.class);
		InputStream input = new BufferedInputStream( new FileInputStream(file));
//		String fileName = file.getName().substring(0, file.getName().length()-EXTENSION.length());
		Application application = parseStream( input );
		input.close();
		return application;
	}
	
	private static class MenuFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.endsWith(EXTENSION)) return true;
			return false;
		}
    }
	
	/**
	 * Scan a directory for files with application.xml extensions each file found.
	 * @param directory in which to look for application.xml files
	 * @return A list of application objects corresponding to the application.xml files found in the supplied directory 
	 * @throws JAXBException 
	 * @throws IOException 
	 * @throws Exception
	 */
	public List<Application> parseFromDirectory( String directory) throws IOException, JAXBException {
//		TolvenLogger.info( "[loadFromDirectory]", ParseApplication.class);
		File dir = new File( directory );
		File files[] = dir.listFiles(new MenuFilter()); 
		List<Application> applications = new ArrayList<Application>();
		if (files==null) throw new IllegalArgumentException( "Invalid applications directory: " + directory);
		for (File file : files) {
			applications.add(parseFile( file ));
		}
		return applications;
	}
	
	/**
	 * Marshal (serialize) an application tree to XML.
	 * @param application
	 * @param output
	 * @throws JAXBException
	 * @throws TRIMException
	 */
	public void marshalToStream( Application application, OutputStream output ) throws JAXBException {
        getMarshaller().marshal( application, output );
	}
	
	/**
	 * Marshal an application to a file.
	 * @throws IOException 
	 * @throws JAXBException 
	 */
	public void marshalToFile( Application application, File file ) throws IOException, JAXBException {
		OutputStream stream = new FileOutputStream( file );
		marshalToStream(application, stream );
		stream.close();
	}

	/**
	 * Marshal a list of application files to a directory. the output files are named <application-name>.application.xml.
	 * We also rename the original to application.xml.backup
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	public void marshalToDirectory( List<Application> applications, String directory ) throws IOException, JAXBException {
		for (Application application : applications ) {
			File of = new File( directory+ "/"+ application.getName()+EXTENSION);
			
			if (of.exists()) {
				String backupName = directory+ "/"+ application.getName()+EXTENSION+".bak";
				File backupFile = new File(backupName );
				of.renameTo(backupFile);
			}
			marshalToFile( application, of);
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println( "parseApplication <application-directory>" );
			System.out.println( " application-directory contains *.application.xml" );
			return;
		}
		ParseMenuStructure pa = new ParseMenuStructure();
		List<Application> applications = pa.parseFromDirectory( args[0] );
		for (Application application : applications ) {
			TolvenLogger.info( "Application: " + application, ParseMenuStructure.class);
//			pa.marshalToStream( application, System.out);
		}
		pa.marshalToDirectory( applications, args[0]);
	}
	
}
