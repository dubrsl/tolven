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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(DefaultTolvenAuthorizationPK.class)
public class DefaultTolvenAuthorization implements TolvenAuthorization, Serializable {

    @Id
    @Column
    private String contextPath;

    @Column
    private String filters;

    @Id
    @Column
    private String policy;

    @Id
    @Column
    private String url;

    @Id
    @Column
    private String urlMethod;

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getFilters() {
        return filters;
    }

    @Override
    public String getPolicy() {
        return policy;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUrlMethod() {
        return urlMethod;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public void setFilters(String filters) {
        this.filters = filters;
    }

    @Override
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setUrlMethod(String urlMethod) {
        this.urlMethod = urlMethod;
    }

    public String toString() {
        return DefaultTolvenAuthorization.class + "(" + getPolicy() + ": " + getUrlMethod() + " " + getContextPath() + getUrl() + " [" + getFilters() + "])";
    }

}
