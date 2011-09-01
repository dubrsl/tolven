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
 */
package org.tolven.naming;

/**
 * This class allows the parameters to be configured externally via JNDI
 * 
 * @author Joseph Isaac
 *
 */
public interface WebContext extends TolvenJndiContext {

    public String getContextPath();

    public String getHtmlLoginPath();

    public String getHtmlLoginUrl();

    public String getRsInterface();

    public String getRsLoginPath();

    public String getRsLoginUrl();

    public String getSslPort();

    public String getWsLoginPath();
    
    public String getWsLoginUrl();

}
