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
 * @author John Churin
 * @version $Id$
 */

package org.tolven.restful;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.tolven.app.ApplicationMetadataLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.entity.MSResource;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.doc.RulesLocal;
import org.tolven.report.ReportLocal;
import org.tolven.util.ExceptionFormatter;

import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;

@Path("loader")
@ManagedBean
public class LoadApplicationResources {

    public static final String APPLICATION_EXTENSION = ".application.xml";

    @Context
    private HttpServletRequest request;

    @EJB
    private ApplicationMetadataLocal applicationMetadataBean;

    @EJB
    private ReportLocal reportBean;

    @EJB
    private MenuLocal menuBean;

    @EJB
    private AccountDAOLocal accountBean;

    @EJB
    private RulesLocal rulesBean;

    @EJB
    private TrimLocal trimBean;

    public LoadApplicationResources() {
    }

    protected ApplicationMetadataLocal getApplicationMetadataBean() {
        if (applicationMetadataBean == null) {
            String jndiName = "java:app/tolvenEJB/ApplicationMetadata!org.tolven.app.ApplicationMetadataLocal";
            try {
                InitialContext ctx = new InitialContext();
                applicationMetadataBean = (ApplicationMetadataLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return applicationMetadataBean;
    }

    protected ReportLocal getReportBean() {
        if (reportBean == null) {
            String jndiName = "java:app/tolvenEJB/ReportBean!org.tolven.report.ReportLocal";
            try {
                InitialContext ctx = new InitialContext();
                reportBean = (ReportLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return reportBean;
    }

    protected MenuLocal getMenuBean() {
        if (menuBean == null) {
            String jndiName = "java:app/tolvenEJB/MenuBean!org.tolven.app.MenuLocal";
            try {
                InitialContext ctx = new InitialContext();
                menuBean = (MenuLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return menuBean;
    }

    protected AccountDAOLocal getAccountDAOBean() {
        if (accountBean == null) {
            String jndiName = "java:app/tolvenEJB/AccountDAOBean!org.tolven.core.AccountDAOLocal";
            try {
                InitialContext ctx = new InitialContext();
                accountBean = (AccountDAOLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return accountBean;
    }

    protected RulesLocal getRulesBean() {
        if (rulesBean == null) {
            String jndiName = "java:app/tolvenEJB/RulesBean!org.tolven.doc.RulesLocal";
            try {
                InitialContext ctx = new InitialContext();
                rulesBean = (RulesLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return rulesBean;
    }

    protected TrimLocal getTrimBean() {
        if (trimBean == null) {
            String jndiName = "java:app/tolvenEJB/TrimBean!org.tolven.app.TrimLocal";
            try {
                InitialContext ctx = new InitialContext();
                trimBean = (TrimLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return trimBean;
    }

    /**
     * Load all supplied application metadata. The files have been read to memory on the client and passed here as
     * a list of zero or more mapped strings with the key being the name of the file.
     * @param appFiles
     */

    @Path("loadApplications")
    @POST
    @Consumes("multipart/mixed")
    public Response loadApplications(MultiPart multiPart) {
        try {
            Map<String, String> applications = new HashMap<String, String>();
            for (BodyPart bodyPart : multiPart.getBodyParts()) {
                String filename = bodyPart.getContentDisposition().getFileName();
                if (filename == null) {
                    return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("No filename found in a Content-Disposition header for upload").build();
                }
                if (!MediaType.APPLICATION_XML.equals(bodyPart.getMediaType().toString())) {
                    return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).type(MediaType.TEXT_PLAIN).entity(filename + " has unsupported media type: " + bodyPart.getMediaType()).build();
                }
                String xml = bodyPart.getEntityAs(String.class);
                applications.put(filename, xml);
            }
            getApplicationMetadataBean().loadApplications(applications);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Load all supplied application metadata. The files have been read to memory on the client and passed here as
     * a list of zero or more mapped strings with the key being the name of the file.
     * @param appFiles
     */
    @Path("loadApplication")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response loadApplicationXMLs(@QueryParam("applicationName") String applicationName, String application) {
        if (applicationName == null || applicationName.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("applicationName is missing").build();
        }
        if (application == null || application.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("The application XML is missing").build();
        }
        try {
            Map<String, String> applications = new HashMap<String, String>();
            applications.put(applicationName + APPLICATION_EXTENSION, application);
            getApplicationMetadataBean().loadApplications(applications);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Store the report as a String. An exception will be thrown if the externalRepotName is supplied and not found to
     * be the same as that within the report itself.
     * 
     * @param externalReportName
     * @param reportAsString
     * @param reportType
     */
    @Path("storeReport")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response storeReport(@QueryParam("externalReportName") String externalReportName, @QueryParam("reportType") String reportType, String reportAsString) {
        try {
            // How are dates portably transmitted
            Date date = (Date) request.getAttribute("tolvenNow"); // new Date(Long.parseLong(time));
            if (externalReportName == null) {
                getReportBean().storeReport(reportAsString, reportType, date);
            } else {
                getReportBean().storeReport(externalReportName, reportAsString, reportType, date);
            }
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Store a resource into the database. The resource primary key is Account+name. In other words, resources are specific to an account.
     * @param resource
     */
    @Path("persistResource")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response persistResource(@FormParam("accountId") String accountId, @FormParam("resourceName") String resourceName, @FormParam("resourceValue") String resourceValue) {
        try {
            Account account = getAccountDAOBean().findAccount(Long.parseLong(accountId));
            MSResource msResource = new MSResource();
            msResource.setAccount(account);
            msResource.setName(resourceName);
            // How is byte[] portably transmitted
            msResource.setValue(resourceValue.getBytes());
            getMenuBean().persistResource(msResource);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * A remote-friendly method that creates a new Rule package from source and requires no special classes on the remote-end
     * @param packageBody
     */
    @Path("packageBody")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response loadRulePackage(@QueryParam("packageBodyName") String packageBodyName, String packageBody) {
        if (packageBodyName == null || packageBodyName.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("packageBodyName is missing").build();
        }
        if (packageBody == null || packageBody.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("The packageBody drl is missing").build();
        }
        try {
            getRulesBean().loadRulePackage(packageBody);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Queue a process to activate new trim headers.
     * @return true if there's more work to be done
     */
    @Path("queueActivateNewTrimHeaders")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response queueActivateNewTrimHeaders() {
        try {
            getTrimBean().queueActivateNewTrimHeaders();
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    @Path("createTrimHeader")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    /**
     * Create a TrimHeader.
     * This new style of loading trims does not create MenuData, which will be done later.
     * It makes no change if the XML has not changed (using a straight string compare).
     * @param trimXML The XML representation of the trim as a string
     * @param user
     * @param comment A comment about his change (for history)
     * @param autogenerated If true, indicates that this trim entry is uploaded from an automated source
     * @return The internal name of the trim as extracted from the &lt;name&gt; element.
     */
    public Response createTrimHeader(@QueryParam("trimName") String trimName, String trimXML) {
        if (trimXML == null || trimXML.length() == 0) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("trimXML is missing").build();
        }
        try {
            //trimLocal.createTrimHeader(trimXML, user, comment, Boolean.parseBoolean(autogenerated));
            getTrimBean().createTrimHeader(trimXML, request.getUserPrincipal().getName(), null, false);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity("Trim " + trimName + ": " + ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

}
