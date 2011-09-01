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
package org.tolven.ws.trim;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.WebServiceContext;

import org.tolven.app.TrimLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.CE;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

/**
* 
* @author Joseph Isaac
*/
@WebService(name = "Trim", serviceName = "TrimService", targetNamespace = "http://tolven.org/trim")
public class TrimServiceImpl {

    private static final String TRIM_NS = "urn:tolven-org:trim:4.0";
    private static final String TRIM_Package = "org.tolven.trim";

    @EJB
    private TrimLocal trimBean;

    @EJB
    private DocumentLocal documentBean;

    @EJB
    private AccountDAOLocal accountBean;

    @Resource
    private WebServiceContext wsContext;

    private static JAXBContext jc;

    public TrimServiceImpl() {
    }

    @WebMethod
    @Oneway
    @RequestWrapper(localName = "submitTrim", targetNamespace = "http://tolven.org/trim", className = "org.tolven.ws.trim.jaxws.SubmitTrimRequest")
    public void submitTrim(@WebParam(name = "webServiceTrim", targetNamespace = "http://tolven.org/trim") WebServiceTrim webServiceTrim) {

        Map<String, Object> sourceMap = new HashMap<String, Object>();
        for (WebServiceField field : webServiceTrim.getFields()) {
            if (field.getValue() instanceof XMLGregorianCalendar) {
                sourceMap.put(field.getName(), ((XMLGregorianCalendar) field.getValue()).toGregorianCalendar().getTime());
            } else {
                sourceMap.put(field.getName(), field.getValue());
            }
        }
        TrimFactory factory = new TrimFactory();
        CE gender = factory.createCE();
        gender.setDisplayName("Male");
        gender.setCode("C0024554");
        // Male
        //      gender.setDisplayName("Female");
        //      gender.setCode("C0015780"); // Female
        gender.setCodeSystem("2.16.840.1.113883.6.56");
        gender.setCodeSystemVersion("2007AA");
        sourceMap.put("gender", gender);
        String principalName = wsContext.getUserPrincipal().getName();
        try {
            AccountUser accountUser = accountBean.findAccountUser(principalName, webServiceTrim.getAccountId());
            TolvenUser tolvenUser = accountUser.getUser();
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("account", accountUser.getAccount());
            variables.put("user", tolvenUser);
            variables.put("now", new Date());
            variables.put("source", sourceMap);
            TrimEx trim = trimBean.evaluateAndParseTrim(webServiceTrim.getName(), variables);
            TolvenMessageWithAttachments tm = createTolvenMessage(TRIM_NS, accountUser.getAccount().getId(), tolvenUser.getId());
            addTrimAsPayload(trim, tm);
            displayInstantiatedTrim(tm);
            submitMessage(tm);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        TolvenLogger.info("Processing WebServiceTrim: " + webServiceTrim.getName(), TrimServiceImpl.class);

    }

    /**
     * Create a TolvenMessage payload wrapper. Notice that the accountId and user id must be supplied in the wrapper.
     * Tolven does not accept anonymous messages.
     * @param ns The namespace that defines the payload 
     * @return
     */

    public TolvenMessageWithAttachments createTolvenMessage(String ns, long accountId, long userId) {
        TolvenMessageWithAttachments tm = new TolvenMessageWithAttachments();
        tm.setAccountId(accountId);
        tm.setAuthorId(userId);
        tm.setXmlNS(ns);
        return tm;
    }

    /**
     * Marshal a Trim object graph to XML and add it to the message. 
     * @param trim
     * @param tm
     */

    public void addTrimAsPayload(Trim trim, TolvenMessage tm) {
        JAXBContext jc = setupJAXBContext();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(trim, output);
        } catch (Exception ex) {
            throw new RuntimeException("Could not marshal trim", ex);
        }
        tm.setPayload(output.toByteArray());
    }

    static JAXBContext setupJAXBContext() {
        if (jc == null) {
            try {
                jc = JAXBContext.newInstance(TRIM_Package);
            } catch (Exception ex) {
                throw new RuntimeException("Could not create JAXBContext", ex);
            }
        }
        return jc;
    }

    /**
     * Given a message, display the XML playload.
     * @param tm
     */
    public void displayInstantiatedTrim(TolvenMessageWithAttachments tm) {
        String x = new String(tm.getPayload());
        TolvenLogger.info("************************** Instantiated Trim ***************************", TrimServiceImpl.class);
        TolvenLogger.info(x, TrimServiceImpl.class);
        TolvenLogger.info("************************************************************************", TrimServiceImpl.class);
    }

    /**
     * Put a message onto the processing queue
     * @param tm
     * @throws Exception
     */
    public void submitMessage(TolvenMessageWithAttachments tm) throws Exception {
        documentBean.queueTolvenMessage(tm);
    }

}
