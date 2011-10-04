package org.tolven.ldap;

import javax.naming.NamingException;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;

import org.tolven.ldap.Ber.DecodeException;
import org.tolven.ldap.Ber.EncodeException;

public class PasswordChangeRequest implements ExtendedRequest {
    public static final String CHANGE_PASSWORD_REQ_OID = "1.3.6.1.4.1.4203.1.11.1";
    String username;
    char[] newPassword;
    PasswordChangeResponse response = null;

    // User-friendly constructor 
    public PasswordChangeRequest(String username, char[] newPassword) {
        this.username = username;
        this.newPassword = newPassword;
    };

    // Methods used by service providers
    public String getID() {
        return CHANGE_PASSWORD_REQ_OID;
    }

    public byte[] getEncodedValue() {

        try {
            BerEncoder encoder = new BerEncoder();

            // Start the sequence.
            encoder.beginSeq(Ber.ASN_SEQUENCE | Ber.ASN_CONSTRUCTOR);

            // Add the data and finish the sequence.
            encoder.encodeString(username, Ber.ASN_CONTEXT | 0, true);
            //                encoder.encodeString(oldPassword, Ber.ASN_CONTEXT | 1, true);
            if (newPassword != null) {
                encoder.encodeString(new String(newPassword), Ber.ASN_CONTEXT | 2, true);
            }
            encoder.endSeq();

            return encoder.getTrimmedBuf();
        } catch (EncodeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public char[] getNewPassword() {
        return newPassword;
    }

    public ExtendedResponse createExtendedResponse(String id, byte[] berValue, int offset, int length) throws NamingException {
        try {
            response = new PasswordChangeResponse(id, berValue, offset, length);
        } catch (DecodeException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    public class PasswordChangeResponse implements ExtendedResponse {
        PasswordChangeResponse(String id, byte[] berValue, int offset, int length) throws NamingException, DecodeException {
            if (berValue != null) {
                BerDecoder decoder = new BerDecoder(berValue, offset, length);
                int rlen[] = new int[10];
                int t = decoder.parseSeq(rlen);
                byte[] np = decoder.parseOctetString(128, null);
                newPassword = new String(np).toCharArray();
            }
        }

        // These are type-safe and user-friendly methods
        public char[] getNewPassword() {
            return newPassword;
        }

        // These are low-level methods
        public byte[] getEncodedValue() {
            return null;
        }

        public String getID() {
            return CHANGE_PASSWORD_REQ_OID;
        }
    }
}
