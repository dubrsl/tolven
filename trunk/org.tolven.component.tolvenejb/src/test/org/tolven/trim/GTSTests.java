package test.org.tolven.trim;

import java.text.ParseException;
import java.util.Date;

import junit.framework.TestCase;

import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ex.TSEx;
import org.tolven.trim.ex.TrimFactory;

public class GTSTests extends TestCase {
	private static final TrimFactory trimFactory = new TrimFactory();  

	/**
	 * Generate an HL7 TS string, convert it to a Date, and compare to the original date.
	 * @throws ParseException
	 */
	public void testGTS1() throws ParseException {
		Date now = new Date();
		TSEx ts = (TSEx) trimFactory.createNewTS(now);
		Date now2 = ts.getDate();
		TolvenLogger.info( "TS(now)=" + ts.getValue() + " Date=" + now2, GTSTests.class);
		assertTrue(Math.abs(now2.getTime()-now.getTime()) < 1000);
	}
	
}
