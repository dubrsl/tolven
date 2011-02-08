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

import org.tolven.app.el.GlobalLocaleMap;

/**
 * Allows code in JSF to dynamically supply a key as access to a mesage bundle
 * @author Joseph Isaac
 *
 */
public interface GlobalLocaleText {

    public String getLocaleText(String key);
    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as item.localeText[aResourceBundle] and returns the textOverride of the item,
     * if defined, or the value found in the resource bundle
     * @return
     */
    public GlobalLocaleMap getLocaleText();

}
