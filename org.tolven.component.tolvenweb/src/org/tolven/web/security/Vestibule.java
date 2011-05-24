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
 * @version $Id: Vestibule.java 915 2011-05-18 05:32:10Z joe.isaac $
 */
package org.tolven.web.security;

import javax.servlet.ServletRequest;

public interface Vestibule {

    public String getName();

    public void enter(ServletRequest servletRequest) throws VestibuleException;

    public String validate(ServletRequest servletRequest) throws VestibuleException;

    public void exit(ServletRequest servletRequest) throws VestibuleException;

    public void abort(ServletRequest servletRequest) throws VestibuleException;

}
