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
 * @author <your name>
 * @version $Id: TolvenServlet.java,v 1.2.24.3 2010/05/03 19:10:24 joseph_isaac Exp $
 */  

package org.tolven.web.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.tolven.core.TolvenPropertiesLocal;

public abstract class TolvenServlet extends HttpServlet {
	protected static final String RESOURCE_OVERRIDE = "tolven.web.resources";
	private static final int BUFFER_SIZE = 4096;
	
	@EJB
	private TolvenPropertiesLocal propertiesBean;
	
    public TolvenPropertiesLocal getPropertiesBean() {
        return propertiesBean;
    }
    
    protected void copyStream( InputStream istream, OutputStream ostream ) throws IOException {
		BufferedInputStream bis = new BufferedInputStream( istream, BUFFER_SIZE );
		byte buffer[] = new byte[2048];
		int len = buffer.length;
		while (true) {
			len = bis.read(buffer);
			if (len == -1) break;
			ostream.write(buffer, 0, len);
		}
    }
    /**
     * Combine the contextPath with the specified path. For this purpose, we
     * remove the leading "/" to make the supplied path relative to the context path.
     * @param ContextPath From "tolven.web.resources" property
     * @param path Must begin with "/"
     * @return
     * @throws MalformedURLException 
     */
    protected static InputStream tryURL( String contextPath, String path ) {
    	try {
			URL ctx = new URL( contextPath );
			URL url = new URL( ctx, path.substring(1) );
			// See if that yields a file
			InputStream is = url.openStream();
			return is;
		} catch (IOException e) {
			return null;
		}
    	
    }
    
    /**
     * Open a resource from a branded location, general resource override location, or a normal location within war.
     * @param resourceName
     * @param localAddr
     * @param servletContext
     * @return An input stream if open successfully.
     */
    protected InputStream openResourceAsStream( String resourceName, String localAddr, ServletContext servletContext ) {
    	InputStream stream = null;
		String path = getPropertiesBean().getProperty(RESOURCE_OVERRIDE + "." + localAddr);
		if (path !=null ) {
			// Make sure path ends with "/"
			if (!path.endsWith("/")) {
				path = path + "/";
			}
			stream = tryURL( path, resourceName);
		}
		if (stream==null) {
			path = getPropertiesBean().getProperty(RESOURCE_OVERRIDE );
			if (path !=null ) {
				// Make sure path ends with "/"
		    	if (!path.endsWith("/")) {
		    		path = path + "/";
		    	}
		    	stream = tryURL( path, resourceName);
			}
		}
		if (stream==null) {
			stream = servletContext.getResourceAsStream(resourceName);
		}
    	return stream;
    }
}
