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
 * @author <your name>
 * @version $Id: VersionNumber.java,v 1.1 2009/06/13 02:19:30 jchurin Exp $
 */  

package org.tolven.util;

public class VersionNumber {
	/**
	 * Break up a version string into an array of integers.
	 * @param versionString
	 * @return
	 */
	public static int[] getVersionArray( String versionString ) {
        String[] parts = versionString.split("\\.");
        int values[] = new int[parts.length];
        for ( int x = 0; x < parts.length; x++) {
        	int part = Integer.parseInt(parts[x]);
        	values[x] = part;
        }
        return values;
	}
	
	/**
	 * Create a normalized version string from an array of integers.
	 * @param versionArray
	 */
	public static String toVersionString( int[] versionArray ) {
		StringBuffer sb = new StringBuffer( );
		for (int version : versionArray) {
			if (sb.length()>0) sb.append(".");
			sb.append(version);
		}
		return sb.toString();
	}

	/**
	 * Compare two version number strings. The strings can have dots to separate number groups.
	 * For example: 
	 * <ul>
	 * <li>1.1.0 &gt; 1.1</li> 
	 * <li>1.01 = 1.1</li> 
	 * <li>0.1.1 &lt; 1.1</li> 
	 * <li>0.01 = 1.1</li> 
	 * <li>1.1. Not allowed</li> 
	 * <li>1..1 Not allowed</li> 
	 * <li>.1.1 Not allowed</li> 
	 * </ul>
	 * @param arg0
	 * @param arg1
	 * @return a value less than zero if arg0 is less than arg1, a value greater than zero if arg0 is greater than arg1, otherwise zero.
	 */
	public static int compare(String arg0, String arg1) {
		int argInt0[] = getVersionArray( arg0 );
		int argInt1[] = getVersionArray( arg1 );
		for (int x = 0; x < Math.min( argInt0.length, argInt1.length); x++ ) {
			int rslt = argInt0[x] - argInt1[x];
			if (rslt==0) continue;
    		return rslt;
		}
		// Length will break the tie
		return argInt0.length - argInt1.length;
	}
}
