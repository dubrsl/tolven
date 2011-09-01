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
package org.tolven.security;

import java.io.Serializable;
import java.security.Principal;

/**
 * A Tolven specific principal
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = 2L;
    private String name;

    public TolvenPrincipal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object anObject) {
        return anObject instanceof TolvenPrincipal && name.equals(((TolvenPrincipal) anObject).getName());
    }

    public int hashCode() {
        return name.hashCode();
    }
    
    public String toString() {
        return getName();
    }

}
