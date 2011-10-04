package org.tolven.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The DateUtils provide method for converting type of date from string to Date.
 * 
 */
public class DateUtils {
	/**
	 * Converts type of date.
	 * 
	 * @param strDate
	 * @param format
	 * @return Date
	 */
	public static Date stringToDate(String strDate, String format) {
		
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = null;
		
		try {
			date = formatter.parse(strDate);
		} catch (Exception e) {
			new Exception("The type of date cant be converted");
		}
		
		return date;
	}
}