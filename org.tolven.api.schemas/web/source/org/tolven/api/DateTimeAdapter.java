package org.tolven.api;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

public class DateTimeAdapter {
	 public static Date parseDateTime(String s) {
		    return DatatypeConverter.parseDateTime(s).getTime();
	  }
	 
	  public static String printDateTime(Date dt) {
		  if (dt==null) {
			  return null;
		  } else {
		    Calendar cal = new GregorianCalendar();
		    cal.setTime(dt);
		    return DatatypeConverter.printDateTime(cal);
		  }
	  }

}
