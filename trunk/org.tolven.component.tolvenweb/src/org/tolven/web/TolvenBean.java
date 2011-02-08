/*
 *  Copyright (C) 2008 Tolven Inc 
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

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Set;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;

import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.PersonMatchLocal;
import org.tolven.app.TrimLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.InvitationLocal;
import org.tolven.core.NoticeDAOLocal;
import org.tolven.core.QuestionAnswerDAOLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.XMLProtectedLocal;
import org.tolven.gen.GeneratorLocal;
import org.tolven.gen.PersonGenerator;
import org.tolven.provider.ProviderLocal;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.LDAPLocal;
import org.tolven.security.LoginLocal;
import org.tolven.security.key.UserPrivateKey;
/**
 * Base bean used by faces backing beans or JSP beans. This module provides access to
 * EJBs in the tolven middle/business tier. It also provides access to the userPrivateKey which is needed
 * for operations involving encrypted documents.
 * 
 * @author John Churin, Joe Isaac
 *
 */
public class TolvenBean {
    @EJB
    private DocumentLocal documentLocal;
    
    @EJB
    private XMLProtectedLocal xmlProtectedLocal;
    
    @EJB
    private XMLLocal xmlLocal;
    
    @EJB
    private CreatorLocal creatorBean;
        
    @EJB
    private DocumentLocal DocumentLocal;

    @EJB
    private DocProtectionLocal docProtectionBean;
    
    @EJB
    private LoginLocal loginBean;

    @EJB
    private LDAPLocal ldap;

    @EJB
    private InvitationLocal invitationBean;

    @EJB
    private GeneratorLocal generatorLocal;

    @EJB
    private MenuLocal menuLocal;
        
    @EJB
	private NoticeDAOLocal noticeDAO;

    @EJB
    private AccountDAOLocal accountBean;
    
    @EJB
    private ActivationLocal activationBean;
    
    @EJB
    private ProviderLocal providerBean;
    
    @EJB
    private PersonMatchLocal matchBean;

    @EJB
    private TrimLocal trimBean;
    
    @EJB
    private QuestionAnswerDAOLocal questionAnswerBean;

    @EJB
    private PersonGenerator personGen;

    @EJB
    private TolvenPropertiesLocal propertiesBean;
    
    private InitialContext ctx;

    protected InitialContext getContext() {
        if (ctx == null) {
            try {
                ctx = new InitialContext();
            } catch (NamingException ex) {
                throw new RuntimeException("Could not create an initial context", ex);
            }
        }
        return ctx;
    }
    
    protected DocumentLocal getDocumentLocal() {
        return documentLocal;
    }

    protected XMLProtectedLocal getXMLProtectedBean() {
        return xmlProtectedLocal;
    }

    protected XMLLocal getXmlLocal() {
        return xmlLocal;
    }

    protected CreatorLocal getCreatorBean() {
        return creatorBean;
    }

    protected DocumentLocal getDocBean() {
        return DocumentLocal;
    }


    protected DocProtectionLocal getDocProtectionBean() {
        return docProtectionBean;
    }

    protected MenuLocal getMenuLocal() {
        return menuLocal;
    }

    protected NoticeDAOLocal getNoticeLocal() {
        return noticeDAO;
    }

    protected AccountDAOLocal getAccountBean() {
        return accountBean;
    }

    protected ActivationLocal getActivationBean() {
        return activationBean;
    }
    
    protected GeneratorLocal getGeneratorBean() {
        return generatorLocal;
    }

    protected LDAPLocal getLDAPLocal() {
        return ldap;
    }

    protected InvitationLocal getInvitationLocal() {
        return invitationBean;
    }

    protected LoginLocal getLoginBean() {
        return loginBean;
    }

    protected ProviderLocal getProviderBean() {
        return providerBean;
    }

    protected PersonMatchLocal getMatchBean() {
        return matchBean;
    }

    protected TrimLocal getTrimBean() {
        return trimBean;
    }

    protected QuestionAnswerDAOLocal getQuestionAnswerBean() {
        return questionAnswerBean;
    }

    protected PersonGenerator getPersonGen() {
        return personGen;
    }
    
    protected TolvenPropertiesLocal getTolvenPropertiesBean() {
        return propertiesBean;
    }

    /**
     * Return the UserPrivateKey for the user
     * @return
     * @throws PolicyContextException
     * @throws GeneralSecurityException
     */
    public UserPrivateKey getSubjectUserPrivateKey() throws PolicyContextException, GeneralSecurityException {
        Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        if (subject == null)
            throw new GeneralSecurityException("No Subject found in PolicyContext");
        Set<UserPrivateKey> privateCredentials = subject.getPrivateCredentials(UserPrivateKey.class);
        if (privateCredentials.isEmpty()) {
            Principal principal = null;
            Object obj = null;
            for (java.util.Iterator<Principal> iter = subject.getPrincipals().iterator(); iter.hasNext();) {
                obj = iter.next();
                if (obj instanceof Principal && !(obj instanceof Group)) {
                    principal = (Principal) obj;
                    break;
                }
            }
            if (principal == null)
                    throw new GeneralSecurityException("No Principal found in PolicyContext Subject");
            String principalName = principal.getName();
            throw new GeneralSecurityException(": No UserPrivateKey found for " + principalName);
        }
        return (UserPrivateKey) privateCredentials.iterator().next();
    }
    
}
