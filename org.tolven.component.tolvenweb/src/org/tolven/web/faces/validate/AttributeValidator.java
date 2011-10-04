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
 * @version $Id$
 */
package org.tolven.web.faces.validate;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

/**
 * This class validates Directory Service attributes in a Faces UI
 * 
 * @author Joseph Isaac
 *
 */
@SuppressWarnings("serial")
public class AttributeValidator implements Validator, Serializable {

    @Override
    public void validate(FacesContext arg0, UIComponent ui, Object object) throws ValidatorException {
        Attribute attribute = (Attribute) object;
        if(attribute != null) {
            Object attributeObject = null;
            try {
                attributeObject = attribute.get();
            } catch (NamingException ex) {
                throw new RuntimeException("Could not get the attribute: " + attribute.getID(), ex);
            }
            if(attributeObject == null || attributeObject.toString().trim().isEmpty()) {
                throw new ValidatorException(new FacesMessage("Validation Error: Value is required."));
            }
        }
    }
}
