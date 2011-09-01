package test.org.tolven.security.hash;

import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.tolven.logging.TolvenLogger;
import org.tolven.security.hash.SSHA;
public class SSHATest extends TestCase {

	/**
	 * Verify that encoded password validates plaintext password
	 */
	public void testEncodePassword() throws NoSuchAlgorithmException {
		String plain = "abe";
		String encoded = SSHA.encodePassword(plain.getBytes());
		TolvenLogger.info(encoded, SSHATest.class);
		assertTrue( SSHA.checkPassword( plain.getBytes(), encoded) );
	}

	/**
	 * Encoded the same password twice should yield different encodings.
	 * @throws NoSuchAlgorithmException
	 */
	public void testEncodePassword2() throws NoSuchAlgorithmException {
		String plain = "baker";
		String encoded1 = SSHA.encodePassword(plain.getBytes());
		String encoded2 = SSHA.encodePassword(plain.getBytes());
		TolvenLogger.info(encoded1, SSHATest.class);
		TolvenLogger.info(encoded2, SSHATest.class);
		assertFalse( encoded1.equals(encoded2) );
	}

	/**
	 * Verify that encoded password validates plaintext password
	 */
	public void testEncodePassword3() throws NoSuchAlgorithmException {
		String plain1 = "abe";
		String plain2 = "able";
		String encoded = SSHA.encodePassword(plain1.getBytes());
		TolvenLogger.info(encoded, SSHATest.class);
		assertFalse( SSHA.checkPassword( plain2.getBytes(), encoded) );
	}


}
