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
 * @version $Id: DocumentTypeAbstract.java,v 1.5.22.2 2010/08/17 08:41:18 joseph_isaac Exp $
 */  

package org.tolven.doctype;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.tolven.core.KeyUtility;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.bean.DocumentBean;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.key.DocumentSecretKey;

public abstract class DocumentTypeAbstract implements DocumentType {
	
	private DocBase document;
	private String keyAlgorithm;
	private int keyLength;
	private List<DocAttachment> attachments;
	private AccountUser accountUser;
	
	public DocumentTypeAbstract() {
	}
	
	/**
	 * Get the parsed content of the document payload. 
	 * @param ee ExpressionEvaluator
	 * @return An object representing the parsed content of the document.
	 */
	public abstract Object getParsed(  );
	
	public abstract void prepareEvaluator( ExpressionEvaluator ee );
	
	public abstract String getVariableName();
	
	public abstract boolean matchDocumentType( String mediaType, String namespace );
	
	public Long getDocumentId( ExpressionEvaluator ee ) {
		Long documentId = (Long) ee.get(DOCUMENT_ID);
		return documentId;
	}
	
	public DocBase getDocument( ExpressionEvaluator ee ) {
		DocumentBean docBean = (DocumentBean) ee.get(DOCUMENT_BEAN );
		DocBase document = (DocBase) ee.get(DOCUMENT );
		if (document==null) {
			Object docId = ee.get(DocumentTypeAbstract.DOCUMENT_ID);
			if (docId!=null) {
				// We'll need an accountUser to do this
				AccountUser accountUser = (AccountUser) ee.get("accountUser");
				if (accountUser==null) {
					throw new RuntimeException( "accountUser must be specified in ExpressionEvaluator in order to acess the underlying document" );
				}
			}
			document = docBean.findDocument((Long)docId);
			ee.addVariable(DocumentTypeAbstract.DOCUMENT, document);
		}
		return document;
	}

	public DocBase getDocument() {
		return document;
	}

	public void setDocument(DocBase document) {
		this.document = document;
	}

	@Override
	public List<DocAttachment> getAttachments() {
		return attachments;
	}

	@Override
	public void setAtachments(List<DocAttachment> attachments) {
		this.attachments = attachments;
		
	}

	/**
	 * Generate a new secret key for this document. 
	 */
	@Override
	public SecretKey generateSecretKey( PublicKey publicKey ) {
		DocumentSecretKey documentSecretKey = DocumentSecretKey.getInstance();
    	getDocument().setDocumentSecretKey(documentSecretKey);
        try {
			SecretKey secretKey = documentSecretKey.init(publicKey, getKeyAlgorithm(), getKeyLength());
			return secretKey;
		} catch (GeneralSecurityException e) {
			throw new RuntimeException( "Error creating a document secret key in " + this, e );
		}
	}
	/**
	 * Return the plaintext secret key for this document. 
	 * @param privateKey
	 * @return If no key exists, return null. This facilitates cases where unencrypted content may be stored.
	 */
	@Override
	public SecretKey getSecretKey( PrivateKey privateKey ) {
        try {
			DocumentSecretKey documentSecretKey = getDocument().getDocumentSecretKey();
	        if (documentSecretKey == null) {
	            return null;
	        }
			SecretKey secretKey = documentSecretKey.getSecretKey(privateKey);
			return secretKey;
		} catch (Exception e) {
			throw new RuntimeException("Error getting document secret key from " + this, e );
		}
	}
	
	@Override
	public Cipher getCipher( ) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(getDocument().getDocumentSecretKey().getAlgorithm());
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining Cipher in " + this, e);
		}	
		return cipher;
	}
	
	/**
	 * Set the encrypted content in the underlying document
	 * @param unencryptedContent
	 */
	public void setEncryptedContent( byte[] unencryptedContent, PublicKey publicKey ) {
	    if (!getDocument().isEditable())
	        throw new RuntimeException("Document is not editable");
	    if (publicKey == null) {
	        //TODO: No accountPublicKey means the content cannot be encrypted....for now backward compatibility and demo set content unencrypted
	        TolvenLogger.info(getClass() + " No AccountPublicKey found in " + this, DocBase.class);
	        getDocument().setContent(unencryptedContent);
	    } else {
	    	// Generate a new secret key
	    	SecretKey secretKey = generateSecretKey(publicKey);
	    	// Encrypt the content
	        Cipher cipher = getCipher();
	        try {
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		        getDocument().setContent(cipher.doFinal(unencryptedContent));
			} catch (Exception e) {
				throw new RuntimeException("Error encrypting content in " + this, e);
			}
	    }
	}
	
	/**
	 * Decrypt and return the content of this document as a byte array. 
	 * @param privateKey the private key needed to decrypt the secret key used to encrypt this content.
	 * @return A byte array containing the plain text document contents
	 */
	public byte[] getDecryptedContent( PrivateKey privateKey ) {
        SecretKey secretKey = getSecretKey(privateKey);
        if (secretKey==null) {
        	 return getDocument().getContent();
        }
        Cipher cipher;
		try {
			cipher = Cipher.getInstance(secretKey.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        return cipher.doFinal(getDocument().getContent());
		} catch (Exception e) {
			throw new RuntimeException( "Error decrypting contents of document.id " + getDocument().getId(), e);
		}
		
	}
	
	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	@Override
	public byte[] getPayload(PrivateKey userPrivateKey) {
		// Get the account private key
		PrivateKey privateKey = KeyUtility.getAccountPrivateKey( getAccountUser(), userPrivateKey);
		// And now decrypt
		return getDecryptedContent(privateKey);
	}
	
	/**
	 * An AccountUser must be associated with a documentType before the document contents (payload) can be decrypted.
	 * @return The AccountUser object or null if no AccountUser has been associated with this document.
	 */
	public AccountUser getAccountUser() {
		return accountUser;
	}
	
	/**
	 * An AccountUser must be associated with a documentType before the document contents (payload) can be decrypted.
	 * @param accountUser
	 */
	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
	}
	
	
}
