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

package org.tolven.core;

import java.util.Date;
import java.util.List;

import org.tolven.core.entity.Notice;


/**
 * Services to manage system-side notices, typically displayed on a login page.
 */
public interface NoticeDAOLocal {
	
    /**
     * Create a new notice.
     * @param strNotice, bActive
     * @return A new Notice object
     */
	public Notice createNotice( String strNotice, Date dateShowFrom, Date dateShowTo, Date dateEffectiveDate, boolean bActive );

    public void updateNotice( Notice notice );
	
	/**
     * Find a notice for the given noticeid.
     * @param noticeId
     * @return the notice object
     */
    public Notice findNotice(long noticeId );
    

    public List<Notice> findActiveNotices ( );
    
} // interface NoticeDAOLocal
