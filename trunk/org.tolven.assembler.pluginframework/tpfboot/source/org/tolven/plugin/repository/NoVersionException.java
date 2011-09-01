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
 * @version $Id$
 */
package org.tolven.plugin.repository;

import java.io.Serializable;

import org.tolven.plugin.repository.bean.PluginDetail;

/**
 * This exception occurs when there are no PluginVersionDetails within a PluginDetail
 * 
 * @author Joseph Isaac
 *
 */
public class NoVersionException extends RuntimeException implements Serializable {
    
    private PluginDetail plugin;
    
    public NoVersionException(String message) {
        super(message);
    }

    public PluginDetail getPlugin() {
        return plugin;
    }

    public void setPlugin(PluginDetail plugin) {
        this.plugin = plugin;
    }

}
