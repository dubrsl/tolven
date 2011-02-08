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

import java.io.InputStream;

import org.tolven.doc.bean.TolvenMessage;

/**
 * This interface protects the DocBase by handling its encryption and decryption.
 * 
 * @author Joseph Isaac
 * 
 */
public interface AccountProcessingProtectionLocal {

    public void setAsEncryptedContent(byte[] unencryptedContent, TolvenMessage tm);

    /**
     * Encrypt the payload(s) of a TolvenMessage for queuing. 
     * @param tm
     */
    public void encryptTolvenMessage(TolvenMessage tm);

    /**
     * Decrypt TolvenMessage payload(s) in place
     * @param tm
     */
    public void decryptTolvenMessage(TolvenMessage tm);

    public byte[] getDecryptedContent(TolvenMessage tm);

    public InputStream getDecryptedContentAsInputStream(TolvenMessage tm);

}
