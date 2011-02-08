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
package org.tolven.ws.echo;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;

/**
 * This class is a Java Service Endpoint which provides an EchoService implementation for testing
 * 
 * @author Joseph Isaac
 *
 */
@WebService(name = "Echo", serviceName = "EchoService", targetNamespace = "http://tolven.org/echo")
public class EchoServiceImpl {

    @Resource
    WebServiceContext wsCtx;

    public EchoServiceImpl() {
    }

    /**
     * 
     * @param string
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "echo")
    @WebResult(targetNamespace = "http://tolven.org/echo")
    @RequestWrapper(localName = "echo", targetNamespace = "http://tolven.org/echo", className = "org.tolven.ws.echo.jaxws.EchoRequest")
    @ResponseWrapper(localName = "echoResponse", targetNamespace = "http://tolven.org/echo", className = "org.tolven.ws.echo.jaxws.EchoResponse")
    public String echo(@WebParam(name = "string", targetNamespace = "http://tolven.org/echo") String string) {
        return wsCtx.getUserPrincipal() + ": " + string;
    }

}
