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
package org.tolven.web;

import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.core.NoticeDAOLocal;
import org.tolven.core.entity.Notice;

/**
 * Backing bean for the login activities. This is a request scoped bean.
 * @author Sashikanth Vema
 */
public class LoginAction extends TolvenAction {

    
    private List<Notice> listNotices;
        
    
    /** Creates a new instance of LoginAction 
     * @throws NamingException */
    public LoginAction(){
    }
    
    public List<Notice> getNotices() throws NamingException {
        if (listNotices == null) {
            listNotices = getNoticeLocal().findActiveNotices();
        }
        return listNotices;

    } //getNotices()

} // class LoginAction
