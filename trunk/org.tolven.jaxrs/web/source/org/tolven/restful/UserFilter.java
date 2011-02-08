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
 * @version $Id: UserFilter.java,v 1.1.2.2 2010/08/17 08:37:41 joseph_isaac Exp $
 */  

package org.tolven.restful;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

@Deprecated
public class UserFilter implements Filter {

	@Override
	public void destroy() {
		
	}
	class CB implements CallbackHandler {
		private String username;
		private char[] password;
		public CB(String username, char[] password) {
			this.username = username;
			this.password = password;
		}
    	public void handle(Callback[] callbacks) {
            int len = callbacks.length;
            Callback cb;
            for (int i = 0; i < len; i++) {
                cb = callbacks[i];
                if (cb instanceof NameCallback) {
                    NameCallback ncb = (NameCallback) cb;
                    ncb.setName(username);
                } else if (cb instanceof PasswordCallback) {
                    PasswordCallback pcb = (PasswordCallback) cb;
                    pcb.setPassword(password);
                }
            }
        }
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();
//		LoginContext loginContext = (LoginContext) session.getAttribute("loginContext");
		Principal principal = request.getUserPrincipal();
		// If we have a principal, then we can proceed
		if (principal!=null) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		// before we accept a password, must ensure that this is a secure session
		if (!request.isSecure()) {
			response.setStatus(403);
			return;
		}
		String authorizationHeader = request.getHeader("Authorization");
		// See if we have the username/password
		if (authorizationHeader!=null && authorizationHeader.startsWith("Basic ")) {
			Base64 decoder = new Base64();
			byte[] decoded = decoder.decode(authorizationHeader.substring(6).getBytes());
			String[] usernamePassword = new String(decoded).split(":");
        	//WebAuthentication webA = new WebAuthentication();
        	//boolean loginStatus = webA.login(usernamePassword[0], usernamePassword[1]);
        	//if (!loginStatus) {
			//	response.setStatus(403);
			//	System.out.println( "Login for " + usernamePassword[0] + " - failed");
			//	return;
        	//}
			System.out.println( "Login for " + usernamePassword[0] + " - succeeded");
        	//principal = new TolvenPrincipal(usernamePassword[0]);
        	Subject subject = new Subject(); 
        	subject.getPrincipals().add(principal);
//				loginContext = new LoginContext("tolvenLDAP", subject, new CB(usernamePassword[0], usernamePassword[1].toCharArray()));
//				loginContext.login();
			// Success
//			session.setAttribute("loginContext", loginContext);
			chain.doFilter(servletRequest, servletResponse);
		} else {
			// Ask for password now
			response.setStatus(401);
			response.setHeader("WWW-Authenticate", "Basic realm=\"tolvenLDAP\"");
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}

}
