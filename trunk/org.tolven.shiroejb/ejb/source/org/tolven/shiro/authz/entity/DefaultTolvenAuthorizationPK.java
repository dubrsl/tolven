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
 * @version $Id: DefaultTolvenAuthorizationPK.java 2592 2011-08-22 08:44:53Z joe.isaac $
 */
package org.tolven.shiro.authz.entity;

import java.io.Serializable;

/**
 * Primary key class for DefaultTolvenAuthorization entity
 * 
 * @author Joseph Isaac
 *
 */
public class DefaultTolvenAuthorizationPK implements Serializable {

    private String contextPath;
    private String policy;
    private String url;
    private String urlMethod;

    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof DefaultTolvenAuthorizationPK)) {
            return false;
        }
        DefaultTolvenAuthorizationPK other = (DefaultTolvenAuthorizationPK) otherOb;
        return ((contextPath == null ? other.contextPath == null : contextPath.equals(other.contextPath)) &&
                (policy == null ? other.policy == null : policy.equals(other.policy)) &&
                (url == null ? other.url == null : url.equals(other.url)) &&
                (urlMethod == null ? other.urlMethod == null : urlMethod.equals(other.urlMethod)));
    }
    
    public String getContextPath() {
        return contextPath;
    }

    public String getPolicy() {
        return policy;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlMethod() {
        return urlMethod;
    }

    public int hashCode() {
        return ((contextPath == null ? 0 : contextPath.hashCode()) ^
                (policy == null ? 0 : policy.hashCode()) ^
                (url == null ? 0 : url.hashCode()) ^
                (urlMethod == null ? 0 : urlMethod.hashCode()));
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlMethod(String urlMethod) {
        this.urlMethod = urlMethod;
    }

}
