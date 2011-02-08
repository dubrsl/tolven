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
package org.tolven.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class exists to handle the fact that JSF's method binding is limited to Map property type behavior.
 * The default values can only be overridden, not removed. New key/value pairs can be added and those removed.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenPropertiesMap implements Map<String, String> {

    private Map<String, String> map;
    private Map<String, String> defaultsMap;

    public TolvenPropertiesMap(Map<String, String> defaultsMap) {
        this.map = new HashMap<String, String>();
        setDefaultsMap(defaultsMap);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    private Map<String, String> getDefaultsMap() {
        return defaultsMap;
    }

    private void setDefaultsMap(Map<String, String> defaultsMap) {
        this.defaultsMap = defaultsMap;
    }

    @Override
    public void clear() {
        getMap().clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return getMap().containsKey(key) || getDefaultsMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getMap().containsValue(value) || getDefaultsMap().containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        Set<java.util.Map.Entry<String, String>> set = new HashSet<java.util.Map.Entry<String, String>>();
        set.addAll(getMap().entrySet());
        set.addAll(getDefaultsMap().entrySet());
        return set;
    }

    @Override
    public String get(Object key) {
        if (getMap().containsKey(key)) {
            return getMap().get((String) key);
        } else {
            return getDefaultsMap().get((String) key);
        }
    }

    @Override
    public boolean isEmpty() {
        return getMap().isEmpty() && getDefaultsMap().isEmpty();
    }

    @Override
    public Set<String> keySet() {
        Set<String> set = new HashSet<String>();
        set.addAll(getDefaultsMap().keySet());
        set.addAll(getMap().keySet());
        return set;
    }

    @Override
    public String put(String key, String value) {
        String oldValue = getMap().get(key);
        getMap().put(key, value);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        getMap().putAll(m);
    }

    /**
     * Only removes objects which were added...not the defaults
     */
    @Override
    public String remove(Object key) {
        String oldValue = getMap().get(key);
        getMap().remove(key);
        return oldValue;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public Collection<String> values() {
        Map<String, String> m = new HashMap<String, String>();
        m.putAll(getDefaultsMap());
        m.putAll(getMap());
        return m.values();
    }

}
