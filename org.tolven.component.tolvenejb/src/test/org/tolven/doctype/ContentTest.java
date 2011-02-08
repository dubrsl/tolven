/*
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
 * @author <your name>
 * @version $Id: ContentTest.java,v 1.1 2009/03/29 05:32:58 jchurin Exp $
 */  

package test.org.tolven.doctype;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Arrays;

import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocTypeFactory;
import org.tolven.doctype.DocumentType;
import org.tolven.security.key.AccountPrivateKey;
import org.tolven.security.key.AccountSecretKey;

import junit.framework.TestCase;

public class ContentTest extends TestCase {

    private static final String PRIVATE_KEY_ALGORITHM = "RSA";
    private static final int PRIVATE_KEY_LENGTH = 1024;

    private static final String KBE_KEY_ALGORITHM = "DESede";
    private static final int KBE_KEY_LENGTH = 112;
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    
    private KeyPair keyPair;
    
    private DocTypeFactory factory;
    private byte[] unencryptedContent;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = new DocTypeFactory();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(PRIVATE_KEY_ALGORITHM);
        keyPairGenerator.initialize(PRIVATE_KEY_LENGTH);
        keyPair = keyPairGenerator.genKeyPair();
        unencryptedContent = new byte[1000];
        for (int x = 0; x < unencryptedContent.length; x++) {
        	unencryptedContent[x] =  (byte) (x & 255);
        }
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		factory = null;
	}
	
	public void testEncryptedContent() {
		DocBase doc = factory.createNewDocument("text/xml", "urn:tolven-org:trim:4.0" );
		DocumentType documentType = doc.getDocumentType();
		// Setup secret key preferences
		documentType.setKeyAlgorithm(KBE_KEY_ALGORITHM);
		documentType.setKeyLength(KBE_KEY_LENGTH);
		documentType.setEncryptedContent(unencryptedContent, keyPair.getPublic());
		byte[] decryptedContent = documentType.getDecryptedContent(keyPair.getPrivate());
		assertTrue( Arrays.equals(unencryptedContent, decryptedContent));
	}
}
