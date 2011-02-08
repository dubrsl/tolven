/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author <your name>
 * @version $Id: TELTests2.java,v 1.2 2009/03/24 23:17:32 joseph_isaac Exp $
 */  

package test.org.tolven.trim;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.trim.Entity;
import org.tolven.trim.LabelFacet;
import org.tolven.trim.TEL;
import org.tolven.trim.TELSlot;
import org.tolven.trim.TelecommunicationAddressUse;
import org.tolven.trim.ex.TELSlotEx;
import org.tolven.trim.ex.TrimFactory;

import junit.framework.TestCase;

public class TELTests2 extends TestCase {
	private static final String PHONE_NUMBER = "1-202-555-1212";
	private static final String ORIGINAL_TEXT = "1(202)555-1212";
	private static final String TEL_LABEL = "Home Phone";
	private static final TrimFactory factory = new TrimFactory( );

	/**
	 * Create a minimum telephone number. 
	 */
	public void testTEL1() {
		TEL tel = factory.createTEL();
		tel.setValue(PHONE_NUMBER);
		assertEquals(PHONE_NUMBER, tel.getValue());
	}
	
	/**
	 * Create a full telephone number. 
	 * @return A populated TEL 
	 */
	public TEL createTEL() {
		TEL tel = factory.createTEL();
		tel.setValue(PHONE_NUMBER);
		LabelFacet label = factory.createLabelFacet(); 
		label.setValue(TEL_LABEL);
		tel.setLabel(label);
		tel.setOriginalText(ORIGINAL_TEXT);
		TelecommunicationAddressUse useCode = TelecommunicationAddressUse.HP;
		tel.getUses().add(useCode);
		return tel;
	}

	/**
	 * Test telephone number
	 */
	public void testTEL2() {
		TEL tel = createTEL();
		assertEquals(PHONE_NUMBER, tel.getValue());
		assertEquals(ORIGINAL_TEXT, tel.getOriginalText());
		assertEquals(TelecommunicationAddressUse.HP, tel.getUses().get(0));
		// Also, check the actual string of Use Code
		assertEquals("HP", tel.getUses().get(0).value());
	}
	
	/*
	 * Please see testTELSlot2 for a better way to do this.
	 */
	public void testTELSlot1() {
		TELSlot telSlot = factory.createTELSlot();
		// Note: This method is not terribly friendly
		telSlot.getNullsAndURLSAndTELS().add(createTEL());
		TEL tel = (TEL) telSlot.getNullsAndURLSAndTELS().get(0);
		assertEquals(PHONE_NUMBER, tel.getValue());
	}
	
	/*
	 * See testGetEL and testSetEL for an even better way to access and manipulate TELs.
	 */
	public void testTELSlot2() {
		// We cast to a Tolven subclass
		TELSlotEx telSlot = (TELSlotEx) factory.createTELSlot();
		// Using a cleaner name 
		telSlot.getValues().add(createTEL());
		TEL tel = (TEL) telSlot.getValues().get(0);
		assertEquals(PHONE_NUMBER, tel.getValue());
	}
	/*
	 * A common routine to create a telecom slot
	 * @return
	 */
	public TELSlotEx createTELSlot() {
		TELSlotEx telSlot = (TELSlotEx) factory.createTELSlot();
		telSlot.getValues().add(createTEL());
		return telSlot;
	}
	
	/*
	 * Create an Entity containing a telecom address 
	 */
	public Entity createEntity() {
		Entity	entity = factory.createEntity();
		entity.setTelecom(createTELSlot());
		return entity;
	}
	
	/*
	 * This technique uses EL to get a TEL data type. This is especially
	 * convenient and safer when the TELSlot is buried deep in a RIM object structure. For this test, we just put it
	 * in an entity, but it would normally be much deeper.  
	 */
	public void testGetTELviaEL() {
		TrimExpressionEvaluator te = new TrimExpressionEvaluator();
		te.addVariable( "player", createEntity());
		Object result = te.evaluate("${player.telecom.values[0].value}");
		assertEquals(PHONE_NUMBER, result);
		System.out.println( result );
	}
	
	public void testSetEL() {
		
	}
	
}
