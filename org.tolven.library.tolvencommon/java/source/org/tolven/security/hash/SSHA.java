package org.tolven.security.hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
public class SSHA {

    /**
     * Convert password char[] to byte[] using UTF-8, then encode the password using SSHA
     * 
     * @param password
     * @return
     */
      public static String encodePassword(char[] password) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          OutputStreamWriter outputStreamWriter = null;
          try {
            try {
                  outputStreamWriter = new OutputStreamWriter(baos, Charset.forName("UTF-8"));
                  outputStreamWriter.write(password, 0, password.length);
              } finally {
                  outputStreamWriter.close();
              }
        } catch (IOException ex) {
            throw new RuntimeException("Could not encode password to an OutputStreamWriter", ex);
        }
          return encodePassword(baos.toByteArray());
      }
	 /**
	   * Encode the provided password as an SSHA password
	   * @param  password  The plain-text password.
	   * @return  The SSHA-encoded password string.
	   * @throws  NoSuchAlgorithmException SHA-1 digest doesn't exist.
	   */
	  public static String encodePassword(byte[] password) {
	    // Generate a 64-bit salt
	    byte[] salt = new byte[8];
	    new SecureRandom().nextBytes(salt);

	    // Append the salt bytes to the password.
	    byte[] passwordPlusSalt = new byte[password.length + salt.length];
	    System.arraycopy(password, 0, passwordPlusSalt, 0, password.length);
	    System.arraycopy(salt, 0, passwordPlusSalt, password.length, salt.length);

	    // Use SHA-1 to digest the password plus salt
	    MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Could not get an SHA-1 MessageDigest instance to encode a password", ex);
        }
	    byte[] passwordPlusSaltHash = sha1.digest(passwordPlusSalt);

	    // Create a byte array that appends the salt bytes to the SHA-1 digest.
	    byte[] digestPlusSalt = new byte[passwordPlusSaltHash.length + salt.length];
	    System.arraycopy(passwordPlusSaltHash, 0, digestPlusSalt, 0, passwordPlusSaltHash.length);
	    System.arraycopy(salt, 0, digestPlusSalt, passwordPlusSaltHash.length, salt.length);

	    // Base64-encode the resulting array, and append to hash identifier
	    return "{SSHA}" + new String(Base64.encodeBase64(digestPlusSalt));
	  }
	  
	  /**
	   * Check the supplied plaintext password against the supplied hashed SSHA password.
	   * @param password
	   * @param sshaPasswordString
	   * @return true if the password compares true
	   */
	  public static boolean checkPassword(byte[] password, String sshaPasswordString) {

		if (!sshaPasswordString.startsWith("{SSHA}")) return false;
		byte[] digestPlusSalt = Base64.decodeBase64(sshaPasswordString.substring(6).getBytes());
		byte[] salt = new byte[8];
		byte[] digestBytes = new byte[digestPlusSalt.length - 8];

		System.arraycopy(digestPlusSalt, 0, digestBytes, 0, digestBytes.length);
		System.arraycopy(digestPlusSalt, digestBytes.length, salt, 0, salt.length);

		byte[] passwordPlusSalt = new byte[password.length + salt.length];
		System.arraycopy(password, 0, passwordPlusSalt, 0, password.length);
		System.arraycopy(salt, 0, passwordPlusSalt, password.length, salt.length);

		MessageDigest sha1Digest = null;
        try {
            sha1Digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Could not get an SHA-1 MessageDigest instance to encode a password", ex);
        }
		byte[] passwordPlusSaltHash = sha1Digest.digest(passwordPlusSalt);

		return Arrays.equals(digestBytes, passwordPlusSaltHash);
	}

    public static boolean checkPassword(char[] password, byte[] sshaPassword) {
        String sshaPasswordString = null;
        try {
            sshaPasswordString = new String(sshaPassword, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Could not convert byte[] to char[]", ex);
        }
        return checkPassword(password, sshaPasswordString);
    }

    public static boolean checkPassword(char[] password, String sshaPasswordString) {
        byte[] passwordBytes = getBytes(password);
        return checkPassword(passwordBytes, sshaPasswordString);
    }

    public static byte[] getBytes(char[] charArr) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(baos);
                outputStreamWriter.write(charArr, 0, charArr.length);
            } finally {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            }
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Could not convert char[] to byte[]", ex);
        }
    }

}
