package test.org.tolven.trim;

import java.util.List;

import junit.framework.TestCase;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.trim.DataType;
import org.tolven.trim.EN;
import org.tolven.trim.ENSlot;
import org.tolven.trim.ENXPSlot;
import org.tolven.trim.EntityNamePartType;
import org.tolven.trim.EntityNameUse;
import org.tolven.trim.TEL;
import org.tolven.trim.TELSlot;
import org.tolven.trim.TelecommunicationAddressUse;
import org.tolven.trim.ex.TELSlotEx;
import org.tolven.trim.ex.TrimFactory;
/**
 * Test the TEL Datatype access via javax.el
 * @author John Churin
 *
 */
public class TELTests extends TestCase {
	private static final TrimFactory factory = new TrimFactory( );
	
	public static final String TEL1_VALUE = "1-800-555-1212";
	public static final String TEL2_VALUE = "44-123-12234";
	public static final String TEL3_VALUE = "1-999-999-9999";
	public static final String TEL4_VALUE = "1-111-111-1111";
	public static final String TEL1 = "#{telecom.TEL['H'].value}";
	public static final String TEL1A = "#{telecom.TEL['H[0]'].value}";
	public static final String TEL2 = "#{telecom.TEL['WP'].value}";
	public static final String TEL3 = "#{telecom.TEL['H[1]'].value}";
	public static final String TEL4 = "#{telecom.TEL['H[2]'].value}";
	
	TrimExpressionEvaluator te;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		te = new TrimExpressionEvaluator();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public TELSlotEx setupTELSlot( ) {
		TELSlotEx telslot = (TELSlotEx) factory.createTELSlot();
		TEL tel1 = factory.createTEL();
		tel1.getUses().add(TelecommunicationAddressUse.H);
		tel1.setValue(TEL1_VALUE);
		telslot.getValues().add(tel1);
		TEL tel2 = factory.createTEL();
		tel2.getUses().add(TelecommunicationAddressUse.WP);
		tel2.setValue(TEL2_VALUE);
		telslot.getValues().add(tel2);
		TEL tel3 = factory.createTEL();
		tel3.getUses().add(TelecommunicationAddressUse.H);
		tel3.setValue(TEL3_VALUE);
		telslot.getValues().add(tel3);
		TEL tel4 = factory.createTEL();
		tel4.getUses().add(TelecommunicationAddressUse.H);
		tel4.getUses().add(TelecommunicationAddressUse.WP);
		tel4.setValue(TEL4_VALUE);
		telslot.getValues().add(tel4);
		return telslot;
	}
	
	/**
	 * Get all TELS we have created, just to make sure we have them
	 */
	public void testGetTELs( ) {
		TELSlotEx telslot = setupTELSlot();
		int x = 1;
		for (DataType dt : telslot.getValues()) {
			System.out.println( "TEL" + x++ + ": " + factory.dataTypeToString(dt) );
		}
		System.out.println( );
	}
	public void testGetTEL1( ) {
		TELSlot telslot = setupTELSlot();
		te.addVariable( "telecom", telslot);
		Object result = te.evaluate(TEL1);
		assertTrue( TEL1_VALUE.equals(result) );
		System.out.println( "test (" + TEL1 + "): "+ result );
	}
	public void testGetTEL1A( ) {
		TELSlot telslot = setupTELSlot();
		te.addVariable( "telecom", telslot);
		Object result = te.evaluate(TEL1A);
		assertTrue( TEL1_VALUE.equals(result) );
		System.out.println( "test (" + TEL1A + "): "+ result );
	}
	public void testGetTEL2( ) {
		TELSlot telslot = setupTELSlot();
		te.addVariable( "telecom", telslot);
		Object result = te.evaluate(TEL2);
		assertTrue( TEL2_VALUE.equals(result) );
		System.out.println( "test (" + TEL2 + "): "+ result );
	}
	public void testGetTEL3( ) {
		TELSlot telslot = setupTELSlot();
		te.addVariable( "telecom", telslot);
		Object result = te.evaluate(TEL3);
		assertTrue( TEL3_VALUE.equals(result) );
		System.out.println( "test (" + TEL3 + "): "+ result );
	}
	
	public void testGetTEL4( ) {
		TELSlot telslot = setupTELSlot();
		te.addVariable( "telecom", telslot);
		Object result = te.evaluate(TEL4);
		assertTrue( TEL4_VALUE.equals(result) );
		System.out.println( "test (" + TEL4 + "): "+ result );
	}
}
