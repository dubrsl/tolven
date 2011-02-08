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
package org.tolven.ldapmgr;

/**
 * This class is used to convey ldap entry information.
 * 
 * @author Joseph Isaac
 *
 */
public class LDAPEntryInfo {

    private String baseDN;
    private String name;

    public LDAPEntryInfo(String name, String baseDN) {
        this.name = name;
        this.baseDN = baseDN;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getBaseDN() {
        return baseDN;
    }

    void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getDN() {
        return getName() + "," + getBaseDN();
    }

}
