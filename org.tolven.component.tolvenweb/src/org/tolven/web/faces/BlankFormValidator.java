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
 * @author Anil
 * @version $Id: BlankFormValidator.java,v 1.2 2009/04/06 00:51:59 jchurin Exp $
 */
package org.tolven.web.faces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.tolven.web.TopAction;

/**
 * To make sure Form is filled before submission. The criteria to decide if a form is filled
 * based on the size of collection passed.
 * 
 *  Used in preventEmptyForm.xhtml.
 * 
 * @author Anil
 *
 */
public class BlankFormValidator implements Validator, Serializable {

	@Override
	public void validate(FacesContext cont, UIComponent arg1, Object arg2) throws ValidatorException {
		

		// Do not validate when wizard is refreshed.
		Map lParams = cont.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String lRefresh = (String)lParams.get("refreshWizard");
		
 		if (lRefresh == null ){
 			int size = (Integer)arg1.getAttributes().get("length");
 			String lkey = (String)arg1.getAttributes().get("messageKey");
 			
			if (size == 0)
			{
				TopAction top = (TopAction) cont.getCurrentInstance().getExternalContext().getSessionMap().get("top");
				String lLocalText = top.getLocaleText(lkey);
				throw new ValidatorException(new FacesMessage(lLocalText ));	
			}
		}
	}
}
