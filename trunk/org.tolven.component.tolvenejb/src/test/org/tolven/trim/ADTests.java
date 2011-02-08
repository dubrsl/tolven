package test.org.tolven.trim;

import java.util.List;

import junit.framework.TestCase;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.trim.AD;
import org.tolven.trim.ADXPSlot;
import org.tolven.trim.AddressPartType;
import org.tolven.trim.AddressUse;
import org.tolven.trim.DataType;
import org.tolven.trim.EN;
import org.tolven.trim.ENSlot;
import org.tolven.trim.ENXPSlot;
import org.tolven.trim.EntityNamePartType;
import org.tolven.trim.EntityNameUse;
import org.tolven.trim.ST;
import org.tolven.trim.TEL;
import org.tolven.trim.TELSlot;
import org.tolven.trim.TelecommunicationAddressUse;
import org.tolven.trim.ex.ADEx;
import org.tolven.trim.ex.ADSlotEx;
import org.tolven.trim.ex.TELSlotEx;
import org.tolven.trim.ex.TrimFactory;
/**
 * Test the AD Datatype access via javax.el
 * @author John Churin
 *
 */
public class ADTests extends TestCase {
	private static final TrimFactory factory = new TrimFactory( );
	
	public static final String AD1_VALUE = "123 Elm St";
	public static final String AD1A_VALUE = "Third Floor";
	public static final String AD2_VALUE = "456 Oak Street|";
	public static final String AD3_VALUE = "789 \\Mission Blvd";
	public static final String AD1 = "#{addr.AD['H'].part['AL'].ST.value}";
	public static final String AD1A = "#{addr.AD['H[0]'].part['AL[1]'].ST.value}";
	public static final String AD2 = "#{addr.AD['WP'].part['AL'].ST.value}";
	public static final String AD3 = "#{addr.AD['H[1]'].part['AL'].ST.value}";
	public static final String AD4 = "#{addr.AD['H[1]'].part['AL']}";
	
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
	
	public ADXPSlot setupADXP(AddressPartType type, String value) {
		ADXPSlot adxp1 = factory.createADXPSlot();
		adxp1.setType(type);
		ST st = factory.createST();
		st.setValue(value);
		adxp1.setST(st);
		return adxp1;
	}
	
	public AD setupAD( AddressUse use) {
		AD ad1 = factory.createAD();
		ad1.getUses().add(use);
		return ad1;
	}

	public ADSlotEx setupADSlot( ) {
		ADSlotEx adslot = (ADSlotEx) factory.createADSlot();
		AD ad1 = setupAD(AddressUse.H);
		ad1.getParts().add(setupADXP(AddressPartType.AL, AD1_VALUE));
		ad1.getParts().add(setupADXP(AddressPartType.AL, AD1A_VALUE));
		adslot.getADS().add(ad1);

		AD ad2 = setupAD(AddressUse.WP);
		ad2.getParts().add(setupADXP(AddressPartType.AL, AD2_VALUE));
		adslot.getADS().add(ad2);
		
		AD ad3 = setupAD(AddressUse.H);
		ad3.getParts().add(setupADXP(AddressPartType.AL, AD3_VALUE));
		adslot.getADS().add(ad3);

		return adslot;
	}
	
	/**
	 * Get all TELS we have created, just to make sure we have them
	 */
	public void testADEncode( ) {
		ADSlotEx adslot = setupADSlot();
		int x = 1;
		for (AD ad : adslot.getADS()) {
			System.out.println( "AD" + x++ + ": " + factory.dataTypeToString(ad) );
		}
		System.out.println( );
	}

	public void testADEncodeDecode( ) {
		ADSlotEx adslot = setupADSlot();
		int x = 1;
		for (AD ad : adslot.getADS()) {
			String encoded = factory.dataTypeToString(ad);
			System.out.println( "Encoded AD" + x++ + ": " +  encoded);
			AD ad2 = (AD) factory.stringToDataType(encoded);
			String encoded2 = factory.dataTypeToString(ad2);
			assertEquals( encoded, encoded2 );
		}
		System.out.println( );
	}
	
	public void testGetAD1( ) {
		ADSlotEx adslot = setupADSlot();
		te.addVariable( "addr", adslot);
		Object result = te.evaluate(AD1);
		assertTrue( AD1_VALUE.equals(result) );
		System.out.println( "test (" + AD1 + "): "+ result );
	}
	
	public void testGetAD1A( ) {
		ADSlotEx adslot = setupADSlot();
		te.addVariable( "addr", adslot);
		Object result = te.evaluate(AD1A);
		assertTrue( AD1A_VALUE.equals(result) );
		System.out.println( "test (" + AD1A + "): "+ result );
	}

	public void testGetAD2( ) {
		ADSlotEx adslot = setupADSlot();
		te.addVariable( "addr", adslot);
		Object result = te.evaluate(AD2);
		assertTrue( AD2_VALUE.equals(result) );
		System.out.println( "test (" + AD2 + "): "+ result );
	}

	public void testGetAD3( ) {
		ADSlotEx adslot = setupADSlot();
		te.addVariable( "addr", adslot);
		Object result = te.evaluate(AD3);
		assertTrue( AD3_VALUE.equals(result) );
		System.out.println( "test (" + AD3 + "): "+ result );
	}
	
	public void testGetADSlot( ) {
		ADSlotEx adslot = setupADSlot();
		te.addVariable( "addr", adslot);
		Object result = te.evaluate(AD4);
		System.out.println( "test (" + AD4 + "): "+ result );
//		assertTrue( AD4_VALUE.equals(result) );
	}

}
