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
 * @version $Id: ExceptionFormatter.java 1790 2011-07-22 20:42:40Z joe.isaac $
 */  

package org.tolven.util;

import java.util.ArrayList;
import java.util.List;

public class ExceptionFormatter {
	public static final String exclusions[] = {"org.jboss.ejb3.tx.Ejb3TxPolicy"};  
	
	/**
	 * Allows us to ignore annoying traces that don't usually provide any useful trace information.
	 * @param stackTrace
	 * @return false if this stack frame should be ignored in the display output
	 */
	private static boolean isIncluded( StackTraceElement[] stackTrace ) {
		if (stackTrace.length < 1) return true;
		String className = stackTrace[0].getClassName();
		for (String exclusion : exclusions) {
			if (className.equals(exclusion)) {
				return false;
			}
		}
		return true;
	}
    /**
     * Format an exception to one-line per "caused by" stack frame.
     * @param exception Any Throwable exception
     * @param newLine The string to be used between lines
     * @return A formatted string containing the exception
     */
    public static String toSimpleString( Throwable exception, String newLine) {
        StringBuffer sb = new StringBuffer(1000);
        Throwable e = exception;
        while (e !=null) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace!=null && stackTrace.length > 0 && isIncluded( stackTrace)) {
                StackTraceElement topOfStack = stackTrace[0];
                if (sb.length()>0) {
                    sb.append(newLine);
                    sb.append("Caused by: ");
                }
                sb.append(e.getMessage());
                sb.append( " at ");
                sb.append(topOfStack.getClassName());
                sb.append( " (line ");
                sb.append(topOfStack.getLineNumber());
                sb.append( ")");
            }
            e = e.getCause();
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Format an exception to one-line per "caused by" stack frame
     * @param exception Any Throwable exception
     * @param newLine The string to be used between lines
     * @return A formatted string containing the exception
     */
    public static String toSimpleStringMessage(Throwable exception, String newLine) {
        return toSimpleStringMessage(exception, newLine, false);
    }
    
    /**
     * Format an exception to one-line per "caused by" stack frame (or "led to" for reverse)
     * @param exception Any Throwable exception
     * @param newLine The string to be used between lines
     * @return A formatted string containing the exception
     */
    public static String toSimpleStringMessage(Throwable exception, String newLine, boolean reverse) {
        List<Throwable> throwables = new ArrayList<Throwable>();
        Throwable e = exception;
        while (e != null) {
            throwables.add(e);
            e = e.getCause();
        }
        List<Throwable> sortedThrowables = new ArrayList<Throwable>();
        String prefix = null;
        if (reverse) {
            for (int i = throwables.size() - 1; i >= 0; i--) {
                sortedThrowables.add(throwables.get(i));
            }
            prefix = "Led to: ";
        } else {
            sortedThrowables = throwables;
            prefix = "Caused by: ";
        }
        StringBuffer sb = new StringBuffer(1000);
        for (Throwable t : sortedThrowables) {
            StackTraceElement[] stackTrace = t.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0 && isIncluded(stackTrace)) {
                String message = t.getMessage();
                if (message != null) {
                    if (sb.length() > 0) {
                        sb.append(newLine);
                        sb.append(prefix);
                    }
                    sb.append(message);
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }
    
}
