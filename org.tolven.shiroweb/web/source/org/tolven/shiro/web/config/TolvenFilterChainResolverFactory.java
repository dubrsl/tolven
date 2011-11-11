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
package org.tolven.shiro.web.config;

import javax.servlet.FilterConfig;

import org.apache.shiro.util.AbstractFactory;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.tolven.shiro.web.filter.mgt.TolvenFilterChainResolver;

public class TolvenFilterChainResolverFactory extends AbstractFactory<FilterChainResolver> {

    private FilterConfig filterConfig;
    
    public TolvenFilterChainResolverFactory() {
    }

    @Override
    protected FilterChainResolver createInstance() {
        FilterConfig filterConfig = getFilterConfig();
        if (filterConfig != null) {
            return new TolvenFilterChainResolver(filterConfig);
        } else {
            return new TolvenFilterChainResolver();
        }
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

}
