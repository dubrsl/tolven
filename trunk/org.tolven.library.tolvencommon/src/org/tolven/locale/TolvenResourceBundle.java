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
package org.tolven.locale;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * TolvenResourceBundle to encapsulate all Tolven Properties
 * @author Joseph Isaac
 *
 */
public class TolvenResourceBundle extends ResourceBundle {

    private Map<String, String> map;
    private Locale locale;

    public TolvenResourceBundle(Map<String, String> map, Locale locale) {
        this.map = map;
        this.locale = locale;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getString(String key, String defaultValue) {
        try {
            return super.getString(key);
        } catch (MissingResourceException ex) {
            return defaultValue;
        }
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    protected Object handleGetObject(String key) {
        return getMap().get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return new Vector<String>(getMap().keySet()).elements();
    }

}
