import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

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
 * @version $Id: JerseyTestClient.java,v 1.3.2.1 2010/08/12 06:59:54 joseph_isaac Exp $
 */

public class JerseyTestClient extends RESTfulClient {
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Format generic XML
	 * @param source
	 * @param out
	 * @throws Exception
	 */
	public static void formatXML( StreamSource source, OutputStream out ) throws Exception {
		StreamResult result = new StreamResult( out);
		javax.xml.transform.Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(source, result);
	}
	
	/**
	 * Use formatter to print generic XML
	 * @param out
	 * @param xml
	 */
	public static void prettyPrint(PrintStream out, String xml) {
		try {
			// Use Transform
			StringReader reader = new StringReader(xml); 
			formatXML(new StreamSource(reader), out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		out.println(xml);
	}

	public ClientResponse login(String username, String password) {
		WebResource login = getRoot().path("authentication/login");
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("username", username);
		formData.add("password", password);
		ClientResponse response = login.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
		return response;
	}
	
	public ClientResponse userAccountList() {
		WebResource accountList = getRoot().path("vestibule/accountList");
		ClientResponse response = accountList.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		return response;
	}

	public ClientResponse userAccountList(String username) {
		WebResource accountList = getRoot().path("vestibule/accountList").path(username);
		ClientResponse response = accountList.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		return response;
	}
	
	public ClientResponse selectAccount(long accountId) {
		WebResource selectAccount = getRoot().path("vestibule/selectAccount");
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("accountId", Long.toString(accountId));
		ClientResponse response = selectAccount.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
		return response;
	}
	
	public ClientResponse applicationList(String path, String[] fields, String sort[], int offset, int limit) {
		WebResource list = getRoot().path("application/list").queryParam("path", path);
		if (fields!=null) {
			StringBuffer sbFields = new StringBuffer();
			for (String field : fields) {
				if (sbFields.length()>0) {
					sbFields.append(',');
				}
				sbFields.append(field);
			}
			list = list.queryParam("fields", sbFields.toString());
		}
		if (sort!=null) {
			StringBuffer sbSort = new StringBuffer();
			for (String sortField : sort) {
				if (sbSort.length()>0) {
					sbSort.append(',');
				}
				sbSort.append(sortField);
			}
			list = list.queryParam("order", sbSort.toString());
		}
		list = list.queryParam("offset", Integer.toString(offset)).queryParam("limit", Integer.toString(limit));
		ClientResponse response = list.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		return response;
	}
	
	/**
	 * Test RESTful APIs
	 * Must be called with four arguments:
	 * 1. URL, such as: https://localhost:8443/Tolven/api, SSL is required
	 * 2. username
	 * 3. Password
	 * 4. AccountId to which the user has access (must be echr account type)
	 * 
	 * Note: The caller must have a property defined that provides access to a keystore containing 
	 * the public keys of the server(s) that this client recognizes.
	 * -Djavax.net.ssl.trustStore=cacerts.jks -Djavax.net.ssl.trustStorePassword=tolven
	 * 
	 * Jar files required: jsr311-api-1.1.1.jar, jersey-client-1.1.5.1.jar, jersey-core.1.1.5.1.jar
	 * 
	 */
	public static void main(String[] args) throws Exception {
		String url = args[0]; // "https://localhost:8443/Tolven/api";
		String username = args[1]; // "pat";
		String password = args[2]; // "pat";
		long accountId = Long.parseLong(args[3]); // 2486000;
		// Create a client
		JerseyTestClient jtc = new JerseyTestClient();
		// Connect
		//jtc.initSSL(url);
//		c.addFilter(new HTTPBasicAuthFilter(username, password));
		ClientResponse response = null;
		// Display the WADL (generated automatically)
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET application.wadl" );
			WebResource wadl = jtc.getRoot().path("application.wadl");
			String response1 = wadl.accept(MediaType.APPLICATION_XML).get(String.class);
			System.out.println( response1);
		}
		
		// Get the server time
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET guest/time" );
			WebResource guestTime = jtc.getRoot().path("guest/time");
			String response1 = guestTime.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
			System.out.println( response1);
		}
		
		// Get HTTP headers for this request (not very useful)
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET guest/headers" );
			WebResource guestHeaders = jtc.getRoot().path("guest/headers");
			String response1 = guestHeaders.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
			System.out.println( response1);
		}
		
		// Login
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "POST login user " + username);
			// Login
			response = jtc.login(username,password);
			if (response.getStatus()>=300) {
			    System.out.println("Error logging in");
		        return;
			}
		}
		// Get a list of all accounts available to this user
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET vestibule/accountList" );
			// Display list of accounts
			response = jtc.userAccountList();
			if (response.getStatus()!=200) {
			     System.out.println( "Error " + response.getStatus() + " returned");
			     return;
			} else {
				prettyPrint(System.out, response.getEntity(String.class));
			}
		}
		// get a list of accounts available to an arbitrary user (calling user must have tolvenAdmin role)
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET vestibule/accountList/{username}" );
			// Display list of accounts
			response = jtc.userAccountList(username);
			if (response.getStatus()!=200) {
			     System.out.println( "Error " + response.getStatus() + " returned");
			} else {
				prettyPrint(System.out, response.getEntity(String.class));
			}
		}
		// Get accumulated server elapsed time since last call to this API
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET guest/milliseconds" );
			WebResource milliseconds = jtc.getRoot().path("guest/milliseconds");
			String response1;
			try {
				response1 = milliseconds.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
				System.out.println( response1);
			} catch (UniformInterfaceException e) {
				System.out.println( e.getMessage());
			}
		}
		// Select an account to which this user will login
		// This account is retained until logout or changed.
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "POST vestibule/selectAccount" );
			// Select an account
			response = jtc.selectAccount(accountId);
			if (response.getStatus()>=300) {
			    System.out.println("Error selecting account");
		        return;
			}
		}
		// Print some information about the logged in user
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET user/info" );
			WebResource userInfo = jtc.getRoot().path("user/info");
			String response1 = userInfo.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
			System.out.println( response1);
		//	     String response = r.accept(MediaType.APPLICATION_XML_TYPE).get(String.class);
		}
		// Print some information about the selected account
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET account/info" );
			WebResource accountInfo = jtc.getRoot().path("account/info");
			String response1 = accountInfo.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
			System.out.println( response1);
		}
		// Upload a trim
		// (requires user to have tolvenAdmin role)
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "POST trim/upload" );
			File trimFile = new File("test.trim.xml");
			WebResource trimUpload = jtc.getRoot().path("trim/upload");
			String trimId = trimUpload.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN_TYPE).post(String.class, trimFile);
			System.out.println( trimId);
		}
		// Cause previously uploaded trims to be activated, synchronously
		// (requires user to have tolvenAdmin role)
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "POST trim/activate" );
			WebResource trimActivate = jtc.getRoot().path("trim/activate");
			trimActivate.accept(MediaType.TEXT_PLAIN_TYPE).post(String.class);
		}
		// Return a trim (which must be active)
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET trim/get" );
			WebResource trimGet = jtc.getRoot().path("trim/get");
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("name", "test");
			ClientResponse response1 = trimGet.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
			System.out.println( response1);
		}
		// Get number of users known to system (requires user to have tolvenAdmin role)
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET user/count" );
			WebResource userCount = jtc.getRoot().path("user/count");
			String response1;
			try {
				response1 = userCount.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
				System.out.println( response1);
			} catch (UniformInterfaceException e) {
				System.out.println( e.getMessage());
			}
		}
		// Get list of columns available to display on patient list
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET application/list/echr:patients:all (column metadata only)" );
			response = jtc.applicationList("echr:patients:all", 
					null, null, 0, 0);
			if (response.getStatus()!=200) {
			     System.out.println( "Error " + response.getStatus() + " returned");
			     return;
			} else {
				prettyPrint(System.out, response.getEntity(String.class));
			}
		}
		// Get list of patients with selected columns
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET application/list/echr:patients:all (selected fields)" );
			response = jtc.applicationList("echr:patients:all", new String[]{"First","Last","id"},
					new String[]{"Last asc"}, 0, 10);
			if (response.getStatus()!=200) {
			     System.out.println( "Error " + response.getStatus() + " returned");
			     return;
			} else {
				prettyPrint(System.out, response.getEntity(String.class));
			}
		}
		// Get list of patients
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET application/list/echr:patients:all (All fields)" );
			response = jtc.applicationList("echr:patients:all", null,
					new String[]{"First desc"}, 10, 10);
			if (response.getStatus()!=200) {
			     System.out.println( "Error " + response.getStatus() + " returned");
			     return;
			} else {
				prettyPrint(System.out, response.getEntity(String.class));
			}
		}
//		if (true) {
//			System.out.println( "\n****************" );
//			System.out.println( "GET application/list/echr:patient-19645019:allergies:current (All fields)" );
//			response = jtc.applicationList("echr:patient-19645019:allergies:current", null,
//					new String[]{"id asc"}, 0, 10);
//			if (response.getStatus()!=200) {
//			     System.out.println( "Error " + response.getStatus() + " returned");
//			     System.out.println( "More: " + response.getEntity(String.class));
//			     return;
//			} else {
//				prettyPrint(System.out, response.getEntity(String.class));
//			}
//		}
		// Get accumulated server elapsed time since last call to this method
		if (true) {
			System.out.println( "\n****************" );
			System.out.println( "GET guest/milliseconds" );
			WebResource milliseconds = jtc.getRoot().path("guest/milliseconds");
			String response1;
			try {
				response1 = milliseconds.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
				System.out.println( response1);
			} catch (UniformInterfaceException e) {
				System.out.println( e.getMessage());
			}
		}
	}

}
