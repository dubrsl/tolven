package org.tolven.ldap;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;

import org.tolven.ldap.Ber.DecodeException;
import org.tolven.ldap.Ber.EncodeException;

public class PasswordPolicy {

	protected Control control;
	protected BerDecoder decoder;
	
	// Wrap the real control
	public PasswordPolicy(Control control) {
		this.control = control;
		decoder = new BerDecoder(control.getEncodedValue(), 0, control.getEncodedValue().length);
	}

	public String getFormattedReason() {
		StringBuffer sb = new StringBuffer();
		try {
			decoder.parseSeq(null);
			int len = decoder.parseLength();
			for (int l = 0; l < len; l++) {
				int error = decoder.parseByte();
				sb.append(error);
			}
		} catch (DecodeException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}
