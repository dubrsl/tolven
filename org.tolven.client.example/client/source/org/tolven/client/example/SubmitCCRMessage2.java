/**
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Kul Bhushan
 * @version $Id$
 */

package org.tolven.client.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import javax.naming.NamingException;

import org.tolven.client.TolvenClient;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.logging.TolvenLogger;

/**
 * This class reads the CCR xml file and create/update the patient.
 */
public class SubmitCCRMessage2 extends TolvenClient {
	
	private TolvenUser tolvenUser;
	private static final String CCRns = "urn:astm-org:CCR";	
	private static File	ccrFile = null;	
	private static String ccrFileName= "";
	private static String ccrInDirPath = "";
	private static String ccrOutDirPath = "";
	private static File ccrInDir = null;
	private static File ccrOutDir = null;
	
	private static int SLEEP_INTERVAL = 5000;
	
	public SubmitCCRMessage2(String configDir) throws IOException {
		super(configDir);
	}
	
	/**
	 * Log in as the Tolven user that will be what HL7 calls the dataEnterer. 
	 * @param uid The user name (principal)
	 * @param password The user password
	 * @throws NamingException
	 * @throws GeneralSecurityException 
	 */
	public void setupUser( String uid, String password ) throws NamingException, GeneralSecurityException {
		tolvenUser = login(uid, password);
	}
	
	/**
	 * <p>Specify the account that the already-logged-in user will be submitting to.
	 * The user must already be a member of the specified account.
	 * This method actually finds the a Tolven accountUser record which represents this
	 * user's membership in an account.</p> 
	 * <p>A method is provided to access the AccountUser object
	 * (getAccountUser( )) and from this object, the Account and TolvenUser objects can be acquired.</p>
	 * @param accountId
	 * @throws NamingException
	 */
	public void setupAccount(long accountId) throws NamingException {
		loginToAccount(accountId);
	}	
	
	/**
	 * Create a TolvenMessage payload wrapper. Notice that the accountId and user id must be supplied in the wrapper.
	 * Tolven does not accept anonymous messages.
	 * @param ns The namespace that defines the payload 
	 * @return
	 */
	public TolvenMessageWithAttachments createTolvenMessage ( String ns ) throws Exception {
		TolvenMessageWithAttachments tm = new TolvenMessageWithAttachments();
		tm.setAccountId(getAccountUser().getAccount().getId());
		tm.setAuthorId(tolvenUser.getId());
		tm.setXmlNS( ns );
		getCCRXML(tm);

		return tm;
	}

	private void getCCRXML(TolvenMessageWithAttachments tm)
			throws NamingException, Exception {
		
		if (ccrFile == null) {
			ccrFile = new File(ccrFileName);
		}
		InputStream inputStream = new FileInputStream( ccrFile );
		if (inputStream==null) throw new IllegalArgumentException( "CCRXML file " + ccrFile + " cannot be opened ");
		ByteArrayOutputStream bos = new ByteArrayOutputStream(  ); 
		int b;
		while( (b = inputStream.read()) >=0 ) {
			bos.write(b);
		}
		byte[] payload = bos.toByteArray();
		inputStream.close();
		bos.close();
		// Display the CCR
		TolvenLogger.info( new String(payload), SubmitCCRMessage2.class );
		tm.setPayload(payload);
	}
	
	
	/**
	 * Put a message onto the processing queue
	 * @param tm
	 * @throws Exception
	 */
	public void submitMessage( TolvenMessageWithAttachments tm ) throws Exception {
		getDocBean().queueTolvenMessage( tm );
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		boolean automation = false;
		if (args.length < 5 ) {
			System.out.println( "(For One time ccr processing) Arguments: UserId password accountId configDirectory filename");
			System.out.println( "(For Automated ccr processing) Arguments: UserId password accountId configDirectory ccrInDirPath ccrOutDirPath");
			return;
		}
		SubmitCCRMessage2 submitCCR = new SubmitCCRMessage2(args[3]);
		submitCCR.setupUser(args[0], args[1] );
		submitCCR.setupAccount(Long.parseLong(args[2]));
		
		if (args.length == 5) {
			ccrFileName = args[4];
			processCCR(submitCCR);
		} else if (args.length == 6) {
			automation = true;
			ccrInDirPath = args[4];
			ccrOutDirPath = args[5];
			ccrInDir = new File(ccrInDirPath);
			ccrOutDir = new File(ccrInDirPath);
			if (! ccrInDir.exists()) {
				TolvenLogger.info("CCR Input directory does not exist.", SubmitCCRMessage2.class);
				return;
			}
			if (! ccrOutDir.exists()) {
				TolvenLogger.info("CCR Output directory does not exist.", SubmitCCRMessage2.class);
				return;
			}
		}
		
		//Keep checking the Infolder to read the ccr file
		TolvenLogger.info("All processed files will be moved to " + ccrOutDirPath, SubmitCCRMessage2.class);
		while(automation) {
			File[] ccrInDirList = ccrInDir.listFiles();
			for(int i=0;i<ccrInDirList.length;i++){
				ccrFileName = ccrInDirList[i].getAbsolutePath();
				ccrFile = new File(ccrFileName);				
				if (ccrFile.isDirectory()) continue;
				
				if (ccrFile.exists()) {
					//If file exists then process it
					TolvenLogger.info("Processing" + ccrFile.getName(), SubmitCCRMessage2.class);					
					processCCR(submitCCR);					
					//Now move the file to processed directory
				    boolean success = ccrFile.renameTo(new File(ccrOutDirPath, ccrFile.getName()));
				    if (!success) {
				    	TolvenLogger.info(ccrFile.getName() + " was not moved to " + ccrOutDirPath, SubmitCCRMessage2.class);
				    }
				}			
			}
			Thread.sleep(SLEEP_INTERVAL);
		}
	}

	private static void processCCR(SubmitCCRMessage2 submitCCR)
			throws Exception {
		submitCCR.beginTransaction();
		TolvenMessageWithAttachments tm = submitCCR.createTolvenMessage( CCRns );
		submitCCR.submitMessage( tm );
		submitCCR.commitTransaction();
	}
}
