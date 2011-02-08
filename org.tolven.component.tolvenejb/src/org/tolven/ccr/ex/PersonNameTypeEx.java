/**
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
 * @author Kul Bhushan
 * @version $Id: PersonNameTypeEx.java,v 1.2 2009/05/19 18:29:20 jchurin Exp $
 */

package org.tolven.ccr.ex;

import java.util.ArrayList;
import java.util.List;

import org.tolven.ccr.PersonNameType;

public class PersonNameTypeEx extends PersonNameType{

	
    public String getFamilyString() {
    	return combine(this.family);
    }

    public String getGivenString() {
        return combine(this.given);
    }
    
    public String getMiddleString() {
        return combine(this.middle);
    }

    public String getTitleString() {
        return combine(this.title);
    }
    
	public static String combine( List<String> list) {
		if (list==null) return "";
		StringBuffer sb = new StringBuffer( 100 );
		for (String s : list) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(s); 
		}
		return sb.toString();
	}
    public static List<String> split( String name ) {
		List<String> components = new ArrayList<String>();
		String n[] = name.trim().split("\\s");
		for ( String s : n) {
			if (s!=null) {
				String st = s.trim();
				if (st.length()>0) components.add(st);
			}
		}
		return components;
    }
    
}
