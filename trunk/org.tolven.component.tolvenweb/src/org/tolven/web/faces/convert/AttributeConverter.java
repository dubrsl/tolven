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
 * @author Joseph Isaac
 * @version $Id$
 */
package org.tolven.web.faces.convert;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

public class AttributeConverter implements Converter, Serializable {
    
    public AttributeConverter() {
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent ui, String attributeValue) {
        if(ui.getId() == null) {
            throw new RuntimeException("attribute converter Id cannot be null");
        }
        Attribute attribute = new BasicAttribute(ui.getId());
        attribute.add(attributeValue);
        return attribute;
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent ui, Object object) {
        Attribute attribute = (Attribute) object;
        try {
            if(attribute == null) {
                return "";
            } else {
                Object attributeObject = attribute.get();
                if(attributeObject == null) {
                    return "";
                } else {
                    return attributeObject.toString();
                }
            }
        } catch (NamingException ex) {
            throw new RuntimeException("Could not get() for '" + ui.getId() + "' attribute", ex);
        }
    }

}
