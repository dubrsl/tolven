package org.tolven.trim.ex;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.tolven.trim.TS;

public class TSEx extends TS implements Serializable {
	
	public void setDate( Date time) {
		if (time==null) {
			setValue("");
		} else {
			setValue( HL7DateFormatUtility.formatHL7TSFormatL16Date(time));
		}
	}
	public Date getDate( ) throws ParseException {
		return HL7DateFormatUtility.parseDate(getValue());
	}
	
}
