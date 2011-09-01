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
package org.tolven.core.bean;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.core.NoticeDAOLocal;
import org.tolven.core.entity.Notice;

/**
 * This is the bean class for the Notice enterprise bean.
 * Created May 6, 2007
 * @author Sashikanth Vema
 */
@Stateless()
@Local(NoticeDAOLocal.class)
public class NoticeDAOBean implements NoticeDAOLocal {
	@PersistenceContext private EntityManager em;

    @Resource EJBContext ejbContext;

    /**
     * Create a new notice.
     * @param strNotice, bActive
     * @return A new Notice object
     */
    public Notice createNotice( String strNotice, Date dateShowFrom, Date dateShowTo, Date dateEffectiveDate, boolean bActive ) {
    	
        Notice notice = new Notice();
        notice.setNotice(strNotice);
        notice.setShowFrom(dateShowFrom);
        notice.setShowTo(dateShowTo);
        notice.setEffectiveDate(dateEffectiveDate);
        notice.setNoticeActive(bActive);
        em.persist( notice );

        return notice;
        
    } // createNotice()

    /**
     * Stick the updated notice back in the database.
     * @param account
     */
    public void updateNotice( Notice notice ) {
    	em.merge(notice);
    } // updateNotice()


    /**
     * Find a notice given the notice id.
     * @param noticeId
     * @return the notice object
     */
    public Notice findNotice(long noticeId ) {
    	return em.find( Notice.class, noticeId );
    }

     /**
      * Return all active notices if available.
      * If showFrom and showTo are set for notices, only those notices where current system time 
      * falls between showFrom and showTo are retrieved.
      * Notices are ordered (descending) by effectiveDate if set.   
      * @return List<Notice>
      */
     public List<Notice> findActiveNotices ( ) {
    	 
    	 String strSelectQuery = "SELECT n FROM Notice n " +
    	 	"WHERE n.noticeActive = TRUE " +    
	    	"AND CURRENT_TIMESTAMP BETWEEN n.showFrom AND n.showTo " +     
	    	"ORDER BY n.effectiveDate DESC";
    	 Query query = em.createQuery(strSelectQuery);
    	 return query.getResultList();

     } // findActiveNotices()

 } // class NoticeDAOBean
 	

   
