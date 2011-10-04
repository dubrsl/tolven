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
 * @version $Id: TolvenWebSecurityManager.java 1777 2011-07-21 23:13:45Z joe.isaac $
 */
package org.tolven.shiro.web.mgt;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.Nameable;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.tolven.naming.RealmContext;
import org.tolven.naming.TolvenContext;
import org.tolven.shiro.web.session.mgt.TolvenWebSessionManager;

public class TolvenWebSecurityManager extends DefaultWebSecurityManager {

    public TolvenWebSecurityManager() {
        setSessionMode(NATIVE_SESSION_MODE);
        setRememberMeManager(null);
        setSessionManager(new TolvenWebSessionManager());
        updateRealms();
    }

    protected void updateRealms() {
        List<Realm> realms = new ArrayList<Realm>();
        TolvenContext tolvenContext;
        try {
            InitialContext ictx = new InitialContext();
            tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
        } catch (Exception ex) {
            throw new RuntimeException("Could not lookup tolvenContext", ex);
        }
        for (String realmId : tolvenContext.getRealmIds()) {
            RealmContext realmContext = (RealmContext) tolvenContext.getRealmContext(realmId);
            Realm realm = (Realm) realmContext.getRealmClass();
            ((Nameable) realm).setName(realmId);
            realms.add(realm);
        }
        setRealms(realms);
    }


}
