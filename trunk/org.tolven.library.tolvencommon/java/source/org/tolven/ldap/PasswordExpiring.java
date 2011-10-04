package org.tolven.ldap;

import javax.naming.ldap.Control;

public class PasswordExpiring {
	Control control;
	protected static final int minute = 60;
	protected static final int hour = 60*minute;
	protected static final int day = 24*hour;
	protected static final int month = 30*day;
	protected static final int year = 365*day;
	
	public PasswordExpiring(Control control) {
		this.control = control;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Number of seconds until password expires
	 * 
	 * @return
	 */
	public int getSecondsUntilExpiration() {
		return Integer.parseInt(new String(control.getEncodedValue()));
	}
	
	/**
	 * Return a formatted string indicating the time left before expiration. For example: 1 minute or 7 hours.
	 * @return Formatted string
	 */
	public String getFormattedExpiration( ) {
		int secs = getSecondsUntilExpiration();
		int years = secs/year;
		if (years == 1) return "1 year";
		if (years > 1) return "" + years + " years";
		int months = secs/month;
		if (months == 1) return "1 month";
		if (months > 1) return "" + months + " months";
		int days = secs/day;
		if (days == 1) return "1 day";
		if (days > 1) return "" + days + " days";
		int hours = secs/hour;
		if (hours == 1) return "1 hour";
		if (hours > 1) return "" + hours + " hours";
		int minutes = secs/minute;
		if (minutes == 1) return "1 minute";
		if (minutes > 1) return "" + minutes + " minutes";
		if (secs == 1) return  "1 second";
		return "" + secs + " seconds";
	}
}
