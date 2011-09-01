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
 * @version $Id: DateExtent.java 1790 2011-07-22 20:42:40Z joe.isaac $
 */  

package org.tolven.util;

import java.io.Serializable;
import java.util.Date;

/**
 * Holds a date extent. Each call to applyDate may extend the min and/or max date which can then be returned.
 * @author John Churin
 *
 */
@SuppressWarnings("serial")
public class DateExtent implements Serializable {
		private Date minDate;
		private Date maxDate;
		public DateExtent() {
			minDate = null;
			maxDate = null;
		}
		/**
		 * If a date is less than the minimum, extend it. If a date is greater than the max, extend it
		 */
		public boolean applyDate(Date date) {
			if (date==null) return false;
			if (minDate==null) {
				minDate = date;
				maxDate = date;
				return true;
			} 
			boolean rslt = false;
			if (date.before(minDate)) {
				minDate = date;
				rslt = true;
			}
			if (date.after(maxDate)) {
				maxDate = date;
				rslt = true;
			}
			return rslt;
		}
		
		/**
		 * Return true if the dataExtent is valid, that is, apply has been called at least once with a non-null date.
		 * @return
		 */
		public boolean isValid( ) {
			return (minDate!=null && maxDate!=null); 
		}
		
		public Date getMinDate() {
			return minDate;
		}
		public Date getMaxDate() {
			return maxDate;
		}
		
}
