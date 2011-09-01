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
package org.tolven.web.analysis;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This class exists to handle the fact that JSF's method binding is limited to Map property type behavior
 * @author Joseph Isaac
 *
 */
public class SnapshotVersionMap implements Map<String, Long> {

    private SnapshotAction snapshotAction;

    public SnapshotVersionMap(SnapshotAction snapshotAction) {
        this.snapshotAction = snapshotAction;
    }

    public SnapshotAction getSnapshotAction() {
        return snapshotAction;
    }

    public void setSnapshotAction(SnapshotAction snapshotAction) {
        this.snapshotAction = snapshotAction;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean containsKey(Object key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<String, Long>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long get(Object key) {
        return getSnapshotAction().getMenuDataVersion((String)key);
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<String> keySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long put(String key, Long value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Long> m) {
        // TODO Auto-generated method stub

    }

    @Override
    public Long remove(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Collection<Long> values() {
        // TODO Auto-generated method stub
        return null;
    }

}
