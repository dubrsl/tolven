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
package org.tolven.gatekeeper.entity;

import java.util.Date;

/**
 * 
 * @author Joseph Isaac
 * 
 */
public interface PasswordBackup {

    public Date getCreation();

    public byte[] getEncryptedPassword();

    public int getIterationCount();
    
    public String getPasswordPurpose();
    
    public String getRealm();

    public byte[] getSalt();

    public String getSecurityQuestion();
    
    public String getUserId();
    
    public void setCreation(Date creation);

    public void setEncryptedPassword(byte[] encryptedPassword);
    
    public void setIterationCount(int iterationCount);
    
    public void setPasswordPurpose(String passwordPurpose);

    public void setRealm(String realm);

    public void setSalt(byte[] salt);
    
    public void setSecurityQuestion(String securityQuestion);
    
    public void setUserId(String userId);
    
}
