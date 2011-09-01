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
package org.tolven.gatekeeper;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.tolven.naming.TolvenContext;

/**
 * Faces Action bean concerned with the user login process.
 * @author John Churin
 */
public class LoginAction {

    private String realmId;
    private List<SelectItem> realmIds;

    public LoginAction() {
    }

    public String getRealmId() {
        return realmId;
    }

    public List<SelectItem> getRealmIds() {
        if (realmIds == null) {
            TolvenContext tolvenContext;
            try {
                InitialContext ictx = new InitialContext();
                tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup tolvenContext", ex);
            }
            realmIds = new ArrayList<SelectItem>();
            for (String realmId : tolvenContext.getRealmIds()) {
                realmIds.add(new SelectItem(realmId));
            }
        }
        return realmIds;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

}
