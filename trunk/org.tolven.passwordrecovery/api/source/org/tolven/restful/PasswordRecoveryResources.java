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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.tolven.security.PasswordRecoveryLocal;
import org.tolven.security.entity.SecurityQuestion;
import org.tolven.util.ExceptionFormatter;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@Path("passwordRecovery")
@ManagedBean
//@RolesAllowed("tolvenAdmin")
public class PasswordRecoveryResources {

    @EJB
    private PasswordRecoveryLocal passwordRecoveryLocal;

    /**
     * Return the tolven persistent securityQuestions. A purpose (e.g. SecurityQuestionPurpose Enumerations) must be supplied. If
     * the question is also supplied, then only one or no security questions will be returned
     * @param purpose
     * @return
     */
    @Path("getSecurityQuestions")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getSecurityQuestions(@FormParam("question") String question, @FormParam("purpose") String purpose) {
        try {
            if (purpose == null) {
                throw new RuntimeException("A purpose needs to be supplied");
            }
            List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
            if (question == null) {
                securityQuestions = passwordRecoveryLocal.getSecurityQuestions(purpose);
            } else {
                securityQuestions = passwordRecoveryLocal.getSecurityQuestions(question, purpose);
            }
            MultivaluedMap<String, String> map = new MultivaluedMapImpl();
            for (SecurityQuestion securityQuestion : securityQuestions) {
                map.putSingle(securityQuestion.getQuestion(), securityQuestion.getPurpose());
            }
            return Response.ok(map).build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Add a List of SecurityQuestions
     * 
     * @param question
     * @param purpose
     * @return
     */
    @Path("addSecurityQuestions")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addSecurityQuestions(MultivaluedMap<String, String> map) {
        try {
            List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
            if (map == null) {
                throw new RuntimeException("No security questions supplied");
            } else {
                for (String question : map.keySet()) {
                    SecurityQuestion securityQuestion = new SecurityQuestion(question, map.getFirst(question));
                    securityQuestions.add(securityQuestion);
                }
            }
            passwordRecoveryLocal.addSecurityQuestions(securityQuestions);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Change a SecurityQuestion
     * 
     * @param currentQuestion
     * @param purpose
     * @return
     */
    @Path("changeSecurityQuestion")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changeSecurityQuestion(@FormParam("currentSecurityQuestion") String currentQuestion, @FormParam("purpose") String purpose, @FormParam("newSecurityQuestion") String newQuestion) {
        try {
            if (currentQuestion == null) {
                throw new RuntimeException("No currentQuestion supplied");
            } else if (purpose == null) {
                throw new RuntimeException("No purpose supplied");
            } else if (newQuestion == null) {
                throw new RuntimeException("No newQuestion supplied");
            } else {
                SecurityQuestion securityQuestion = new SecurityQuestion(currentQuestion, purpose);
                passwordRecoveryLocal.changeSecurityQuestion(securityQuestion, newQuestion);
                return Response.ok().build();
            }
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

    /**
     * Remove SecurityQuestoins
     * 
     * @param map
     * @return
     */
    @Path("removeSecurityQuestions")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response removeSecurityQuestions(MultivaluedMap<String, String> map) {
        try {
            List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
            if (map == null) {
                throw new RuntimeException("No security questions supplied");
            } else {
                for (String question : map.keySet()) {
                    SecurityQuestion securityQuestion = new SecurityQuestion(question, map.getFirst(question));
                    securityQuestions.add(securityQuestion);
                }
            }
            passwordRecoveryLocal.removeSecurityQuestions(securityQuestions);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(500).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
        }
    }

}
