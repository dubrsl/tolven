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
package org.tolven.app.el;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import org.tolven.locale.ResourceBundleHelper;

public class AgeFormat {
	/**
	 * Using "now" and "date of birth", compute a readable age string.
	 * STILL NEEDS I18N support.
	 * @param dob
	 * @param now
	 * @return
	 */
	public static String toAgeString( Calendar dob, Calendar now, Locale locale ) {
		ResourceBundle bundle;
		if (locale==null) {
			bundle = ResourceBundle.getBundle(ResourceBundleHelper.GLOBALBUNDLENAME, Locale.getDefault());
		} else {
			bundle = ResourceBundle.getBundle(ResourceBundleHelper.GLOBALBUNDLENAME, locale);
			
		}
	    if (dob.after(now)) return bundle.getString("ageUnborn");
	    // Get age in years
	    int years = now.get(Calendar.YEAR)- dob.get(Calendar.YEAR);
	    int days = now.get( Calendar.DAY_OF_YEAR ) - dob.get( Calendar.DAY_OF_YEAR );
	    if (days < 0) 
	      {
	        years--;
	        days = days + 365;
	      }
	    if (years > 1) return Integer.toString( years ) + bundle.getString("ageInYears");
	    if (years == 0 && days < 30) 
	     {
	        return Integer.toString( days ) + bundle.getString("ageInDays");
	     }
	    return Integer.toString( years * 12 + (days/30) ) + bundle.getString("ageInMonths");
	}
	
	/**
	 * Simple version that does not contain timezone awareness
	 * @param dob
	 * @param now
	 * @return
	 */
	public static String toAgeString( Date dob, Date now ) {
	    Calendar n = new GregorianCalendar();
	    n.setTime( now );
	    Calendar b = new GregorianCalendar();
	    b.setTime( dob );
	    return toAgeString( b, n, null);
	}
}
