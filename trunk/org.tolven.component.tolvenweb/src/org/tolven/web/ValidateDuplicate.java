package org.tolven.web;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class ValidateDuplicate implements Validator, Serializable {

	public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {

	}

}
