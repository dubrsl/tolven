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
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA 02111-1307 USA 
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.report;

import java.util.Map;

import org.tolven.app.entity.MenuData;

/**
 * This is a wrapper for a MenuData item, which allows nesting to take place using an internal parameterMap.
 * @author Joseph Isaac
 *
 */
public class TolvenReportMap {

    private MenuData menuData;
    private Map<String, Object> parameterMap;

    public TolvenReportMap(MenuData menuData, Map<String, Object> parameterMap) {
        this.menuData = menuData;
        this.parameterMap = parameterMap;
    }

    public MenuData getMenuData() {
        return menuData;
    }

    public void setMenuData(MenuData menuData) {
        this.menuData = menuData;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> map) {
        this.parameterMap = map;
    }

    /**
     * First lookup the supplied key in the parameterMap, and if not there, then expect the key to be actual MenuData.
     * The current order was chosen, since an exception is thrown, whenever a key is not found in a MenuData.
     * @param key
     * @return
     */
    public Object get(String key) {
        Object obj = null;
        if (getParameterMap() != null) {
            obj = getParameterMap().get(key);
        }
        if (obj == null) {
            if (getMenuData() == null) {
                return null;
            } else {
                return getMenuData().getField(key);
            }
        } else {
            return obj;
        }
    }

}
