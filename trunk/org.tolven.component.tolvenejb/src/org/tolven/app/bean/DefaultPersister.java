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
 * @version $Id: DefaultPersister.java,v 1.2 2010/04/24 17:43:40 jchurin Exp $
 */  

package org.tolven.app.bean;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.tolven.app.PersisterLocal;
import org.tolven.app.entity.MenuData;

@Local(PersisterLocal.class)
public @Stateless class DefaultPersister implements PersisterLocal {

	@PersistenceContext private EntityManager em;


	@Override
	public Participation getParticipation(MenuData md, Operation operation) {
		return Participation.SHARED;
	}
	
	/**
	 * Very simple persist by default
	 */
	@Override
	public void persist(MenuData md) {
		em.persist( md );
		md.initPath();
	}

	@Override
	public void delete(MenuData md) {
		md.setDeleted(true);
    	em.merge(md); 
	}

	@Override
	public void update(MenuData md) {
    	em.merge(md); 
	}

}
