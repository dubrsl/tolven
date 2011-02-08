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
package org.tolven.app;

import java.util.ResourceBundle;

import org.tolven.app.el.AppLocaleMap;

/**
 * Provides specialized access to application-specific objects (MenuStructure, MSColumn etc) which require locale translations
 * @author Joseph Isaac
 *
 */
public interface AppLocaleText {

    public String getLocaleText(ResourceBundle bundle);

    public String getDefaultLocaleText(ResourceBundle bundle);
    
    public String getLocaleTextKey();

    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as item.localeText[aResourceBundle] and returns the textOverride of the item,
     * if defined, or the value found in the resource bundle
     * @return
     */
    public AppLocaleMap getLocaleText();

    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as item.localeText[aResourceBundle] and ignores the textOverride of the item,
     * in order to return the value found in the resource bundle
     * @return
     */
    public AppLocaleMap getDefaultLocaleText();

}
