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
 * @version $Id: QueueKeyBean.java 1817 2011-07-23 05:46:53Z joe.isaac $
 */
package org.tolven.key.bean;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.key.QueueKeyLocal;
import org.tolven.key.UserKeyLocal;
import org.tolven.naming.QueueContext;
import org.tolven.naming.TolvenContext;

@Stateless
@Local(QueueKeyLocal.class)
public class QueueKeyBean implements QueueKeyLocal {

    @EJB
    private UserKeyLocal userKeyBean;

    public PublicKey getUserPublicKey(String queueId) {
        QueueContext queueContext = getQueueContext(queueId);
        String user = queueContext.getUser();
        String realm = queueContext.getRealm();
        return userKeyBean.getUserPublicKey(user, realm);
    }

    public X509Certificate getUserX509Certificate(String queueId) {
        QueueContext queueContext = getQueueContext(queueId);
        String user = queueContext.getUser();
        String realm = queueContext.getRealm();
        return userKeyBean.getUserX509Certificate(user, realm);
    }

    public String getUserX509CertificateString(String queueId) {
        QueueContext queueContext = getQueueContext(queueId);
        String user = queueContext.getUser();
        String realm = queueContext.getRealm();
        return userKeyBean.getUserX509CertificateString(user, realm);
    }

    private QueueContext getQueueContext(String queueId) {
        TolvenContext tolvenContext = null;
        try {
            InitialContext ictx = new InitialContext();
            tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
        } catch (NamingException ex) {
            throw new RuntimeException("Could not lookup tolvenContext", ex);
        }
        return (QueueContext) tolvenContext.getQueueContext(queueId);
    }

}
