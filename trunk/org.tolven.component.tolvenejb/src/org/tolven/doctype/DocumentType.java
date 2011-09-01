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
 * @version $Id: DocumentType.java,v 1.5.26.2 2010/08/17 08:41:18 joseph_isaac Exp $
 */  

package org.tolven.doctype;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;
import org.tolven.el.ExpressionEvaluator;

public interface DocumentType {
	
	public static final String PARSED = "_parsed";
	public static final String PAYLOAD = "_payload";
	public static final String PLACEHOLDER = "_placeholder";
	public static final String DOCUMENT = "_document";
	public static final String DOCUMENT_ID = "_documentId";
	public static final String DOCUMENT_BEAN = "_documentBean";
	

	/**
	 * Get the parsed content of the document payload. 
	 * @return An object representing the parsed content of the document.
	 */
	public abstract Object getParsed(  );
	
	/**
	 * Get the payload for this document. This method requires
	 * sufficient information, typically AccountUser, to decrypt the
	 * underlying content. 
	 * @param ee ExpressionEvaluator
	 * @return A byte array containing the document payload
	 */
	public abstract byte[] getPayload(PrivateKey userPrivateKey);
	
	public abstract void prepareEvaluator( ExpressionEvaluator ee );
	
	public abstract String getVariableName();
	
	public abstract boolean matchDocumentType( String mediaType, String namespace );
	
	public Long getDocumentId( ExpressionEvaluator ee );
	
	public DocBase getDocument( ExpressionEvaluator ee );
	
	/**
	 * Create a new document appropriate to this documentType
	 * @return The document
	 */
	public DocBase createNewDocument(String mediaType, String namespace);

	/**
	 * Get the document associated with this DocumentType instance.
	 * @return
	 */
	public DocBase getDocument();
	
	/**
	 * Return the list of attachments for this document, if any
	 * @return The list of attachments
	 */
	public List<DocAttachment> getAttachments();
	
	public void setAtachments( List<DocAttachment> attachments );
	
	public void setDocument(DocBase document);
	
	public String getKeyAlgorithm();

	public void setKeyAlgorithm(String keyAlgorithm);
	
	public int getKeyLength();

	public void setKeyLength(int keyLength);

	/**
	 * Generate a new secret key for this document. 
	 */
	public SecretKey generateSecretKey( PublicKey publicKey  );

	/**
	 * Return the plaintext secret key for this document.
	 * @param privateKey
	 * @return 
	 */
	public SecretKey getSecretKey( PrivateKey privateKey );

	/**
	 * Get the Cypher to use for this document
	 * @return
	 */
	public Cipher getCipher( );
	
	/**
	 * Set the encrypted content in the underlying document. 
	 * @param unencryptedContent this byte array will be encrypted.
	 */
	public void setEncryptedContent( byte[] unencryptedContent, PublicKey publicKey );

	/**
	 * Decrypt and return the content of this document as a byte array. 
	 * @param privateKey the private key needed to decrypt the secret key used to encrypt this content.
	 * @return A byte array containing the plain text document contents
	 */
	public byte[] getDecryptedContent( PrivateKey privateKey );
	
	/**
	 * An AccountUser must be associated with a documentType before the document contents (payload) can be decrypted.
	 * @return The AccountUser object or null if no AccountUser has been associated with this document.
	 */
	public AccountUser getAccountUser();
	
	/**
	 * An AccountUser must be associated with a documentType before the document contents (payload) can be decrypted.
	 * @param accountUser
	 */
	public void setAccountUser(AccountUser accountUser);
}
