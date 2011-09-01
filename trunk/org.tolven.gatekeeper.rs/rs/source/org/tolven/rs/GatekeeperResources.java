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
 */
package org.tolven.rs;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.tolven.shiro.authc.UsernamePasswordRealmToken;

/**
 * A class to support RESTful API for managing logins
 * 
 * @author Joseph Isaac
 *
 */
@Path("authenticate")
public class GatekeeperResources {

    public GatekeeperResources() {
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("uid") String uid, @FormParam("password") String password, @FormParam("realm") String realm) {
        try {
            if (uid == null) {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("uid parameter missing").build();
            }
            if (password == null) {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("password parameter missing").build();
            }
            if (realm == null) {
                return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity("realm parameter missing").build();
            }
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordRealmToken token = new UsernamePasswordRealmToken(uid, password.toCharArray(), realm);
            subject.login(token);
            if (subject.isAuthenticated()) {
                return Response.ok().build();
            } else {
                return Response.status(Status.UNAUTHORIZED).build();
            }
        } catch (AuthenticationException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("logout")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {

        }
        try {
            subject.logout();
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
