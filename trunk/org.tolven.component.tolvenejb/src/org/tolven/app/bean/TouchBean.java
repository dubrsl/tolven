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
 * @version $Id: TouchBean.java,v 1.2 2010/03/13 00:57:01 jchurin Exp $
 */  

package org.tolven.app.bean;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.tolven.app.TouchLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.Touch;

@Stateless
@Local(TouchLocal.class )
public class TouchBean implements TouchLocal  {
	@PersistenceContext	private EntityManager em;

	private Logger logger = Logger.getLogger(this.getClass());
	
    /**
     * Return a list of touches that would cause a specific placeholder 
     * to be updated (regardless of reason - focal placeholder). 
     * @param mdPlaceholder
     * @return
     */
    public List<Touch> findTouches( MenuData mdPlaceholder ) {
		Query query = em.createQuery("SELECT t FROM Touch t " +
				"WHERE t.account.id = :accountId " +
				"AND (t.deleted IS NULL OR t.deleted = false) " +
				"AND t.updatePlaceholder = :u" );
		query.setParameter("accountId", mdPlaceholder.getAccount().getId());
		query.setParameter("u", mdPlaceholder);
		return query.getResultList();
    }

    /**
     * Return a list of placeholder that need to be updated because the focal placeholder 
     * has changed.
     * @param mdPlaceholder Focal placeholder
     * @return The list of touches relating to the specified focal placeholder
     */
    public List<Touch> findTouched( MenuData mdPlaceholder ) {
		Query query = em.createQuery("SELECT t FROM Touch t " +
				"WHERE t.account.id = :accountId " +
				"AND (t.deleted IS NULL OR t.deleted = false) " +
				"AND t.focalPlaceholder = :f" );
		query.setParameter("accountId", mdPlaceholder.getAccount().getId());
		query.setParameter("f", mdPlaceholder);
		return query.getResultList();
    }
    
    /**
     * Persist a new touch entry
     * @param touch
     */
    public void persistTouch(Touch touch) {
    	em.persist(touch);
    }
    
    /**
     * Deferred touches are processed here.
     */
	@Override
	public void process(Object message, Date now) {
		// TODO Auto-generated method stub
		
	}

}
