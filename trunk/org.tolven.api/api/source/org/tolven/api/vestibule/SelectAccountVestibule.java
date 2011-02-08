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
 * @version $Id$
 */
package org.tolven.api.vestibule;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.tolven.api.security.AbstractVestibule;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.LoginLocal;

@ManagedBean(value = "org.tolven.api.vestibule.SelectAccountVestibule")
public class SelectAccountVestibule extends AbstractVestibule {

    public static String VESTIBULE_NAME = "org.tolven.vestibule.selectaccount";

    @EJB
    private LoginLocal loginBean;

    @Override
    public void abort(ServletRequest servletRequest) {
    }

    @Override
    public void enter(ServletRequest servletRequest) {
    }

    @Override
    public void exit(ServletRequest servletRequest) {
    }

    @Override
    public String getName() {
        return VESTIBULE_NAME;
    }

    @Override
    public String validate(ServletRequest servletRequest) {
        Long proposedAccountId = getSessionProposedAccountUserId(servletRequest);
        if (proposedAccountId == null) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            TolvenUser user = loginBean.findUser(request.getUserPrincipal().getName());
            if (user == null) {
                throw new RuntimeException("Could not find TolvenUser: " + request.getUserPrincipal());
            }
            AccountUser defaultAccountUser = findDefaultAccountUser(user);
            if (defaultAccountUser == null) {
                return "/vestibule/selectAccount.jsf";
            } else {
                setSessionProposedAccountUserId(defaultAccountUser.getId(), servletRequest);
            }
        }
        return null;
    }
}