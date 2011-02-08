/*
 * Copyright (C) 2010 Tolven Inc

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
 * @author <your name>
 * @version $Id$
 */

package org.tolven.restful;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.ProcessLocal;
import org.tolven.doc.entity.DocBase;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.util.ExceptionFormatter;

@Path("document")
@ManagedBean
public class DocumentResources {

    @EJB
    private DocumentLocal documentBean;

    @EJB
    private DocProtectionLocal docProtectionBean;

    @EJB
    private TolvenPropertiesLocal propertyBean;

    @EJB
    private ProcessLocal processLocal;

    @Context
    UriInfo uriInfo;

    @Context
    HttpServletRequest request;

    @Path("body")
    @GET
    public Response getDocumentBody(@QueryParam("id") String id) throws Exception {
        AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        DocBase doc = documentBean.findDocument(Long.parseLong(id));
        if (doc.getAccount().getId() != accountUser.getAccount().getId()) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Document not found in this account").build();
        }
        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        String body = docProtectionBean.getDecryptedContentString(doc, accountUser, TolvenSSO.getInstance().getUserPrivateKey(request, keyAlgorithm));
        Response response = Response.ok().type(doc.getMediaType()).entity(body).build();
        return response;
    }

    @Path("header")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getDocumentHeader(@QueryParam("id") String id) throws Exception {
        AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        DocBase doc = documentBean.findDocument(Long.parseLong(id));
        if (doc.getAccount().getId() != accountUser.getAccount().getId()) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Document not found in this account").build();
        }
        Response response = Response.ok().entity(doc).build();
        return response;
    }

    @Path("signature")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDocumentSignature(@QueryParam("id") String id) throws Exception {
        AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        DocBase doc = documentBean.findDocument(Long.parseLong(id));
        if (doc.getAccount().getId() != accountUser.getAccount().getId()) {
            return Response.status(Status.FORBIDDEN).type(MediaType.TEXT_PLAIN).entity("Document not found in this account").build();
        }
        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        String signature = docProtectionBean.getDocumentSignaturesString(doc, accountUser, TolvenSSO.getInstance().getUserPrivateKey(request, keyAlgorithm));
        if (signature == null || signature.length() == 0) {
            return Response.noContent().build();
        }
        Response response = Response.ok().entity(signature).build();
        return response;
    }

    /**
     * Submit a document for processing
     * @return response
     */
    @Path("submit")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitDocument(@DefaultValue("urn:astm-org:CCR") @FormParam("xmlns") String xmlns, @FormParam("payload") String payload) throws Exception {
        AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        documentBean.queueWSMessage(payload.getBytes(), xmlns, accountUser.getAccount().getId(), accountUser.getUser().getId());
        Response response = Response.ok().entity("Document submitted").build();
        return response;
    }

    /**
    * Process a document (synchronously)
    * @return response
    */
    @Path("process")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response processDocument(@DefaultValue("urn:astm-org:CCR") @FormParam("xmlns") String xmlns, @FormParam("payload") String payload) throws Exception {
        AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
        if (accountUser == null) {
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("AccountUser not found").build();
        }
        long documentId = processLocal.processMessage(payload.getBytes(), "text/xml", xmlns, accountUser.getAccount().getId(), accountUser.getUser().getId(), new Date());
        URI uri = null;
        try {
            uri = new URI(URLEncoder.encode(Long.toString(documentId), "UTF-8"));
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(e, "\\n")).build();
        }
        Response response = Response.created(uri).entity(String.valueOf(documentId)).build();
        return response;
    }

}
