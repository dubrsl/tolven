package org.tolven.ldap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LdapException extends RuntimeException {
    
	public static final int INVALID_CREDENTIALS =  49;
    public static final int PASSWORD_MUST_CHANGE =  19;
    public static final int PASSWORD_VALIDATION =  53;
	
	private static final long serialVersionUID = 1L;

	public LdapException(String message) {
		super(message);
	}

	public LdapException(String message, Throwable cause) {
		super(message, cause);
	}

	public int getLDAPErrorCode() {
		if (this.getCause()!=null) { 
			 Pattern p = Pattern.compile(".*LDAP\\: error code (\\d+).+");
			 Matcher m = p.matcher(this.getCause().getMessage());
			 if (m.matches()) {
				 return Integer.parseInt(m.group(1));
			 }
		}
		 return 0;
	}
}
