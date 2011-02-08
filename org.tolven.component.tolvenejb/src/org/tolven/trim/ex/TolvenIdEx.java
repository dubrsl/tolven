package org.tolven.trim.ex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.tolven.trim.TolvenId;

public class TolvenIdEx extends TolvenId {
	private static final long serialVersionUID = 1L;
	
	public Long getIdAsLong() {
		if (getId()==null) return null;
		String value = getId().trim();
		if (value.length()==0) return null;
		Long id = Long.parseLong(value);
		if (id==0) return null;
		return id;
	}
	
	public Long getAccountIdAsLong() {
		if (getAccountId()==null) return null;
		String value = getAccountId().trim();
		if (value.length()==0) return null;
		Long accountId = Long.parseLong(value);
		if (accountId==0) return null;
		return accountId;
	}
	
	/**
	 * Set the transaction time in the tolvenId - always GMT.
	 * @param date
	 */
	public void setDate(Date date) {
		if (date==null) {
			setTimestamp("");
		} else {
			SimpleDateFormat HL7TSformat = new SimpleDateFormat("yyyyMMddHHmmssZZ"); 
			HL7TSformat.setTimeZone(TimeZone.getTimeZone("GMT"));
			setTimestamp(HL7TSformat.format(date));
		}
	}
	
	/**
	 * Return the stored timestamp as a Java date. The timezone is stored in HL7 format (yyymmddhhmmss).
	 * @return The date of the event (in the case of an event)
	 */
	public Date getDate() {
		try {
			SimpleDateFormat HL7TSformat = new SimpleDateFormat("yyyyMMddHHmmssZZ");
			HL7TSformat.setTimeZone(TimeZone.getTimeZone("GMT"));
			if (getTimestamp() == null || getTimestamp().length() == 0)
				return null;
			return HL7TSformat.parse(getTimestamp());
		} catch (Exception e) {
			throw new IllegalStateException( "Invalid date format in tolvenId");
		}
	}

	
}
