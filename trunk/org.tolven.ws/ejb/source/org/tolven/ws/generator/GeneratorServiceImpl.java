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
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.jboss.wsf.spi.annotation.WebContext;
import org.tolven.gen.GeneratorLocal;

/**
* Generate patients for a CHR account.
* @author Joseph Isaac
*/
@Stateless
@WebService(name = "Generator", serviceName = "GeneratorService", targetNamespace = "http://tolven.org/generator")
@WebContext(contextRoot="/ws", urlPattern="/generator/*", authMethod="BASIC", transportGuarantee="CONFIDENTIAL", secureWSDLAccess=false)
public class GeneratorServiceImpl {

    @EJB(name = "tolven/GeneratorBean/local")
    private GeneratorLocal generatorLocal;

    public GeneratorServiceImpl() {
    }

    public GeneratorLocal getGeneratorBean() throws NamingException {
        if (generatorLocal == null) {
            InitialContext ctx = new InitialContext();
            generatorLocal = (GeneratorLocal) ctx.lookup("tolven/GeneratorBean/local");
        }
        return generatorLocal;
    }

    @WebMethod(action = "urn:generateCCRXML")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "generateCCRXML", targetNamespace = "http://tolven.org/generator", className = "org.tolven.ws.generator.jaxws.GenerateCCRXMLRequest")
    @ResponseWrapper(localName = "generateCCRXMLResponse", targetNamespace = "http://tolven.org/generator", className = "org.tolven.ws.generator.jaxws.GenerateCCRXMLResponse")
    public byte[] generateCCRXML(@WebParam(name = "startYear", targetNamespace = "") int startYear) {
        try {
            return getGeneratorBean().generateCCRXML(startYear);
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
