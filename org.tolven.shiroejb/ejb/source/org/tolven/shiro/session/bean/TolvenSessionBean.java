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
 */
package org.tolven.shiro.session.bean;

import java.io.Serializable;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.tolven.session.TolvenSessionState;
import org.tolven.shiro.session.TolvenSessionLocal;
import org.tolven.shiro.session.entity.TolvenSimpleSession;

@Stateless
@Local(TolvenSessionLocal.class)
public class TolvenSessionBean implements TolvenSessionLocal {

    @PersistenceContext
    private EntityManager em;

    private Logger logger = Logger.getLogger(TolvenSessionBean.class);

    @Override
    public TolvenSessionState createSession() {
        return new TolvenSimpleSession();
    }

    @Override
    public void deleteSession(String sessionId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting session: " + sessionId);
        }
        StringBuffer buff = new StringBuffer();
        buff.append("DELETE FROM TolvenSimpleSession session WHERE session.id = :id");
        Query query = em.createQuery(buff.toString());
        query.setParameter("id", sessionId);
        query.executeUpdate();
        if (logger.isDebugEnabled()) {
            logger.debug("Deleted session: " + sessionId);
        }
    }

    @Override
    public TolvenSimpleSession findSession(Serializable sessionId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Finding session: " + sessionId);
        }
        TolvenSimpleSession tolvenSessionState = em.find(TolvenSimpleSession.class, sessionId);
        if (logger.isDebugEnabled()) {
            if (tolvenSessionState == null) {
                logger.debug("Session not found: " + sessionId);
            } else {
                logger.debug("Found session: " + sessionId);
            }
        }
        return tolvenSessionState;
    }

    @Override
    public Serializable persistSession(TolvenSessionState tolvenSessionState) {
        if (logger.isDebugEnabled()) {
            logger.debug("Persisting session: " + tolvenSessionState.getId());
        }
        em.persist(tolvenSessionState);
        if (logger.isDebugEnabled()) {
            logger.debug("Persisted session: " + tolvenSessionState.getId());
        }
        return tolvenSessionState.getId();
    }

    @Override
    public void updateSession(TolvenSessionState tolvenSessionState) {
        if (logger.isDebugEnabled()) {
            logger.debug("Updating session: " + tolvenSessionState.getId());
        }
        em.merge(tolvenSessionState);
        if (logger.isDebugEnabled()) {
            logger.debug("Updated session: " + tolvenSessionState.getId());
        }
    }

}
