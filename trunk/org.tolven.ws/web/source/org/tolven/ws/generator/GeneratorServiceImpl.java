/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.ws.generator;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.tolven.gen.GeneratorLocal;

/**
* Generate patients for a CHR account.
* @author Joseph Isaac
*/
@WebService(name = "Generator", serviceName = "GeneratorService", targetNamespace = "http://tolven.org/generator")
public class GeneratorServiceImpl {

    @EJB
    private GeneratorLocal generatorBean;

    public GeneratorServiceImpl() {
    }

    @WebMethod(action = "urn:generateCCRXML")
    @WebResult(targetNamespace = "http://tolven.org/generator")
    @RequestWrapper(localName = "generateCCRXML", targetNamespace = "http://tolven.org/generator", className = "org.tolven.ws.generator.jaxws.GenerateCCRXMLRequest")
    @ResponseWrapper(localName = "generateCCRXMLResponse", targetNamespace = "http://tolven.org/generator", className = "org.tolven.ws.generator.jaxws.GenerateCCRXMLResponse")
    public String generateCCRXML(@WebParam(name = "startYear", targetNamespace = "http://tolven.org/generator") int startYear) {
        byte[] bytes = generatorBean.generateCCRXML(startYear);
        return new String(bytes);
    }

}
