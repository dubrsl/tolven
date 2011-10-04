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
/**
 * This file contains TrimCreatorLocal interface.
 *
 * @package org.tolven.app
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app;

import java.security.PrivateKey;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.AccountUser;
import org.tolven.trim.ex.TRIMException;
import org.tolven.trim.ex.TrimEx;

/**
 * This interface is used to create new trim, submit and for other trim related operations.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
public interface TrimCreatorLocal  {
	/**
	 * Creates trim.
	 * 
	 * added on 12/12/2009
	 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
     * @param accountUser - account user
     * @param trimPath - trim path
     * @param context - context
     * @param now - current date
	 */
	public TrimEx createTrim(AccountUser accountUser, String trimPath, String context, Date now) 
			throws JAXBException, TRIMException;

	/**
	 * Submits the trim.
	 * 
	 * added on 12/12/2009
	 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
	 * @param trim - trim object
	 * @param context - context path
     * @param accountUser - account user
     * @param now - current date
	 */
	public void submitTrim(TrimEx trim, String context, AccountUser accountUser, Date now) throws Exception;

	public String addTrimToActivityList(TrimEx trim, String context,
			AccountUser accountUser, Date now) throws Exception;
	
	/**
     * Returns the Trim for the MenuData which can be revised.
     * 
     * added on 02/21/2011
     * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
     * @param mdPlaceholder - place holder
     * @param accountUser - account user
     * @return TrimEx - trim object
     * @throws JAXBException
     */
    public TrimEx getTrimForRevision(MenuData mdPlaceholder, AccountUser accountUser, PrivateKey userPrivateKey) throws JAXBException;
    
    /**
     * Returns the MenuData corresponding to the Trim provided.
     * 
     * added on 02/21/2011
     * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
     * @param trim - trim object
     * @param mdPlaceholder - place holder
     * @param accountUser - account user
     * @param transitionName - transition name
     * @param now - current date
     * @return MenuData - menudata
     * @throws JAXBException
     * @throws TRIMException
     */
    public MenuData reviseTrimEvent(TrimEx trim, MenuData mdPlaceholder, AccountUser accountUser, String transitionName, Date now) throws JAXBException, TRIMException;
}