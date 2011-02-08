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
package org.tolven.security.acl;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A Tolven specific Group
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenGroup implements Group, Serializable {

    private static final long serialVersionUID = 2L;
    private String name;
    private HashSet<Principal> members;

    public TolvenGroup(String name) {
        this.name = name;
        members = new HashSet<Principal>();
    }

    public boolean addMember(Principal user) {
        return members.add(user);
    }

    public boolean removeMember(Principal user) {
        return members.remove(user);
    }

    public boolean isMember(Principal member) {
        for (Principal myPrincipal : members) {
            if (myPrincipal instanceof Group) {
                return ((Group) myPrincipal).isMember(member);
            } else {
                if (myPrincipal.getName() != null && myPrincipal.getName().equals(member.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(members);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer(getName());
        buff.append("(members:");
        Iterator<Principal> iter = members.iterator();
        while (iter.hasNext()) {
            buff.append(iter.next().toString());
            if (iter.hasNext()) {
                buff.append(',');
            }
        }
        buff.append(")");
        return buff.toString();
    }

}
