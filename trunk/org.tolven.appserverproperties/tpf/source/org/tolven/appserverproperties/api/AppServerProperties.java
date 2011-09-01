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
package org.tolven.appserverproperties.api;

import java.io.File;
import java.util.List;

import org.tolven.appserverproperties.model.LoadServerProperties;

public class AppServerProperties {

    private LoadServerProperties loadServerProperties;

    public AppServerProperties(String appRestfulURL, String authRestfulURL, String userId, char[] password) {
        setLoadServerProperties(new LoadServerProperties(userId, password, appRestfulURL, authRestfulURL));
    }

    private LoadServerProperties getLoadServerProperties() {
        return loadServerProperties;
    }

    private void setLoadServerProperties(LoadServerProperties loadServerProperties) {
        this.loadServerProperties = loadServerProperties;
    }

    public void displayProperties() {
        try {
            getLoadServerProperties().displayProperties();
        } catch (Exception ex) {
            throw new RuntimeException("Could not display server properties", ex);
        } finally {
            getLoadServerProperties().logout();
        }
    }

    public void uploadPropertiesFiles(List<File> propertiesFiles) {
        try {
            getLoadServerProperties().uploadPropertiesFiles(propertiesFiles);
        } catch (Exception ex) {
            throw new RuntimeException("Could not import and upload server properties", ex);
        } finally {
            getLoadServerProperties().logout();
        }
    }

    public String findProperty(String propertyName) {
        try {
            return getLoadServerProperties().findProperty(propertyName);
        } catch (Exception ex) {
            throw new RuntimeException("Could not find property: " + propertyName + " for: ", ex);
        } finally {
            getLoadServerProperties().logout();
        }
    }

    public void setProperty(String propertyName, String propertyValue) {
        try {
            getLoadServerProperties().setProperty(propertyName, propertyValue);
        } catch (Exception ex) {
            throw new RuntimeException("Could not set property: " + propertyName + " for: ", ex);
        } finally {
            getLoadServerProperties().logout();
        }
    }

    public void removeProperty(String propertyName) {
        try {
            getLoadServerProperties().removeProperty(propertyName);
        } catch (Exception ex) {
            throw new RuntimeException("Could not remove property: " + propertyName + " for: ", ex);
        } finally {
            getLoadServerProperties().logout();
        }
    }

}
