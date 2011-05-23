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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * TolvenResourceBundle to encapsulate all Tolven Properties
 * @author Joseph Isaac
 *
 */
public class TolvenResourceBundle extends ResourceBundle {

    private String baseName;
    private Locale locale;

    public TolvenResourceBundle(String baseName, Locale locale) {
        this(locale, new ArrayList<TolvenResourceBundle>());
        this.baseName = baseName;
    }

    public TolvenResourceBundle(Locale locale, List<TolvenResourceBundle> parentBundles) {
        this.locale = locale;
        TolvenResourceBundle bundle = this;
        for (TolvenResourceBundle b : parentBundles) {
            bundle.setParent(b);
            bundle = b;
        }
    }

    private String getBaseName() {
        return baseName;
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
        if (getBaseName() == null) {
            return null;
        } else {
            try {
                return ResourceBundle.getBundle(getBaseName(), getLocale()).getString(key);
            } catch (MissingResourceException ex) {
                return null;
            }
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        Vector<String> keys = new Vector<String>();
        if (getBaseName() != null) {
            keys.addAll(ResourceBundle.getBundle(getBaseName(), getLocale()).keySet());
        }
        return keys.elements();
    }

}
