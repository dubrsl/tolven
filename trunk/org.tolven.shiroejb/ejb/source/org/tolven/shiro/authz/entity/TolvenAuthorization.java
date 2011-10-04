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
package org.tolven.shiro.authz.entity;

public interface TolvenAuthorization {

    public String getContextPath();

    public String getFilters();

    public String getPolicy();

    public String getUrl();

    public String getUrlMethod();

    public void setContextPath(String contextPath);

    public void setFilters(String filters);

    public void setPolicy(String policy);
    
    public void setUrl(String url);

    public void setUrlMethod(String urlMethod);
    
}
