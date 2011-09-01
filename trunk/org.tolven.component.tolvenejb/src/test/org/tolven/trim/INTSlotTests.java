package test.org.tolven.trim;

import org.tolven.trim.INT;
import org.tolven.trim.INTSlot;
import org.tolven.trim.ex.TrimFactory;

import junit.framework.TestCase;

public class INTSlotTests extends TestCase {
	static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	private static final TrimFactory factory = new TrimFactory( );

	public void testCreateINTSlotWithINT() {
		// Traditional create
		INTSlot intSlot = factory.createINTSlot();
		INT intValue = factory.createINT();
		intValue.setValue(1234);
		intSlot.setINT(intValue);
		// Also keep what the user might have typed
		intSlot.setOriginalText("1234");
	}
}
