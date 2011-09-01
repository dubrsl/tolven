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
 * @version $Id: VestibuleException.java 1009 2011-05-18 22:37:37Z joe.isaac $
 */
package org.tolven.web.security;

import java.io.Serializable;

public class VestibuleException extends Exception implements Serializable {

    private String redirect;

    public VestibuleException() {
    }

    public VestibuleException(String message) {
        super(message);
    }

    public VestibuleException(String message, String redirect) {
        super(message);
        this.redirect = redirect;
    }

    public VestibuleException(Throwable cause) {
        super(cause);
    }

    public VestibuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public VestibuleException(String message, String redirect, Throwable cause) {
        super(message, cause);
        this.redirect = redirect;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

}
