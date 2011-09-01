package test.org.tolven.web;

import java.util.Calendar;

import junit.framework.TestCase;

import org.tolven.web.faces.ValidatePast;

public class TestFacesValidators extends TestCase {
	public void testValidatePast(){
		ValidatePast validatePast = new ValidatePast();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, -1);		
		//assertTrue(validatePast.validateDate(c.getTime(),"04/6/1992 11:34 pm"));
	}

}
