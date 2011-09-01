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
 * @version $Id: TolvenWSAgentFilter.java 467 2011-03-27 00:42:37Z joe.isaac $
 */
package org.tolven.openamclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import com.sun.identity.agents.common.IHttpServletRequestHelper;
import com.sun.identity.agents.filter.AmAgentServletRequest;
import com.sun.identity.agents.filter.AmFilterResult;
import com.sun.identity.agents.filter.OpenSSOHttpServletResponse;
import com.sun.identity.wss.security.handler.SOAPRequestHandler;

public class TolvenWSAgentFilter extends TolvenAgentFilter {

    protected void sendData(HttpServletResponse response, AmFilterResult result) throws IOException {
        PrintWriter out = null;
        try {
            response.setContentType("text/xml");
            out = response.getWriter();
            String respContent = result.getDataToServe();
            boolean needProcessResponse = result.getProcessResponseFlag();
            if (needProcessResponse) {
                String processedRespContent = process(result.getRequestURL(), respContent);
                out.print(processedRespContent);
            } else {
                out.print(respContent);
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Processes the response
     */
    private String process(String providerName, String respContent) {
        String processedResponseContent = respContent;
        try {
            // Constrcut the SOAP Message
            MimeHeaders mimeHeader = new MimeHeaders();
            mimeHeader.addHeader("Content-Type", "text/xml");
            MessageFactory msgFactory = MessageFactory.newInstance();
            // Construct Access Manager's SOAPRquestHandler to
            // secure the SOAP message
            SOAPRequestHandler handler = new SOAPRequestHandler();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("providername", providerName);
            handler.init(params);

            SOAPMessage respMessage = msgFactory.createMessage(mimeHeader, new ByteArrayInputStream(respContent.getBytes()));

            // Secure the SOAP message
            SOAPMessage encMessage = handler.secureResponse(respMessage, Collections.EMPTY_MAP);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            encMessage.writeTo(bao);
            processedResponseContent = bao.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processedResponseContent;
    }

    public void allowRequestToContinue(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, AmFilterResult result) throws IOException, ServletException {
        IHttpServletRequestHelper helper = result.getRequestHelper();
        HttpServletRequest outgoingRequest = null;
        if (helper != null) {
            outgoingRequest = new AmAgentServletRequest(request, helper);
        } else {
            outgoingRequest = request;
        }
        boolean needProcessResponse = result.getProcessResponseFlag();
        if (needProcessResponse) {
            OpenSSOHttpServletResponse outgoingResponse = new OpenSSOHttpServletResponse((HttpServletResponse) response);
            filterChain.doFilter(outgoingRequest, outgoingResponse);
            processResponse(outgoingRequest, outgoingResponse);
        } else {
            filterChain.doFilter(outgoingRequest, response);
        }

    }

    private void processResponse(HttpServletRequest request, OpenSSOHttpServletResponse response) throws IOException {
        String respContent = response.getContents();
        String processedRespContent = process(request.getRequestURL().toString(), respContent);
        PrintWriter out = response.getWriter();
        out.println(processedRespContent);
    }

}
