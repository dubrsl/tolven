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
 * @author Joseph Isaac
 * @version $Id: RuleQueueKeyLocal.java 1658 2011-07-12 11:17:41Z joe.isaac $
 */
package org.tolven.queue;

import javax.crypto.SecretKey;

public interface RuleQueueKeyLocal {

    /**
     * Only a ruleQueue user can successfully decrypt ruleQueue messages using this method
     */
    public byte[] decryptMessage(byte[] message, byte[] messageSecretKey, String messageSecretKeyAlgorithm, long accountId);

    public byte[] encryptMessageSecretKey(SecretKey secretKey, long accountId);

    public SecretKey generateMessageSecretKey(long accountId);
    
    public byte[] encryptMessage(byte[] message, SecretKey secretKey);

}
