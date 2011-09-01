/*
 *  Copyright (C) 2006 Tolven Inc 
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
package org.tolven.security;

import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocumentSignature;

/**
 * This interface protects the DocBase by handling its encryption and decryption.
 * 
 * @author Joseph Isaac
 * 
 */
public interface DocProtectionLocal {

    /**
     * Currently assumes all content is encrypted and only the authorized loggedInUser will succeed in getting the readable content
     * This method calls decryption each time it is called.
     * Decryption takes CPU time and it requires access to security policy which means
     * the caller must have permission to call this method.
     * @param doc
     * @param activeAccountUser
     * @return
     */
    public byte[] getDecryptedContent(DocContentSecurity doc, AccountUser activeAccountUser, PrivateKey userPrivateKey);

    /**
     * Stream content to the specified output stream
     * @return
     */
    public void streamContent(DocContentSecurity doc, OutputStream stream, AccountUser activeAccountUser, PrivateKey userPrivateKey);

    /**
     * Return the contents of the document as base64 encoded.
     * This method calls decryption each time it is called.
     * Decryption takes CPU time and it requires access to security policy which means
     * the caller must have permission to call this method.
     * @param doc
     * @param activeAccountUser
     * @return
     */
    public String getDecryptedContentB64(DocContentSecurity doc, AccountUser activeAccountUser, PrivateKey userPrivateKey);

    /**
     * Return the content as a string. This method calls decryption each time it is called.
     * Decryption takes CPU time and it requires access to security policy which means
     * the caller must have permission to call this method.
     * @return
     */
    public String getDecryptedContentString(DocContentSecurity doc, AccountUser activeAccountUser, PrivateKey userPrivateKey);

    public void streamJPEGThumbnail(DocContentSecurity doc, int targetWidth, int targetHeight, OutputStream stream, AccountUser activeAccountUser, PrivateKey userPrivateKey);

    /**
     * Sign the clear text content of DocContentSecurity and return a DocumentSignatute
     * @param doc
     * @param activeAccountUser
     * @return
     */
    public DocumentSignature sign(DocBase doc, AccountUser activeAccountUser, PrivateKey privateKey, X509Certificate x509Certificate);

    /**
     * Verify the document signature belongs to aPublicKey using aDecryptionKey
     * to decrypt the document
     * @param aPublicKey
     * @param aDecryptionKey
     * @return
     */
    public boolean verify(DocumentSignature documentSignature, X509Certificate x509Certificate, AccountUser activeAccountUser, PrivateKey userPrivateKey);
    

    public String getDocumentSignaturesString(DocBase doc, AccountUser activeAccountUser, PrivateKey userPrivateKey);
}
