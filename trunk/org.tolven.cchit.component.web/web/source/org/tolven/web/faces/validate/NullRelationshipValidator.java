package org.tolven.web.faces.validate;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * This class validates Component A, problems, diagnoses wizards.
 * 
 * @author Pinky
 * added on 08/10/2010
 */
public class NullRelationshipValidator implements Validator, Serializable {
	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		int number = 0;
		String name = comp.getAttributes().get("name").toString();
    	try {
			if (value instanceof String) {
				number = (Integer.parseInt((String)value));
			}
						
			if (number==0) {
				throw new ValidatorException(new FacesMessage("Enter atleast one "+name));
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage("Enter atleast one "+name));
		}
    }
			
}
