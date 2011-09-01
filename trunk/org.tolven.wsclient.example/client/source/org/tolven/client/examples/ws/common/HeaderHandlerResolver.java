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
package org.tolven.client.examples.ws.common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;


/**
 *
 * @author Joseph Isaac
 */
public class HeaderHandlerResolver implements HandlerResolver {

    private String username;
    private char[] password;
    private int expiresInSeconds;

    public HeaderHandlerResolver(String username, char[] password, int expiresInSeconds) {
        setUsername(username);
        setPassword(password);
        setExpiresInSeconds(expiresInSeconds);
    }

    private String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private char[] getPassword() {
        return password;
    }

    private void setPassword(char[] password) {
        this.password = password;
    }

    private int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    private void setExpiresInSeconds(int expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    @SuppressWarnings("rawtypes")
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<Handler>();
        HeaderHandler headerHandler = new HeaderHandler(getUsername(), getPassword(), getExpiresInSeconds());
        handlerChain.add(headerHandler);
        return handlerChain;
    }
}