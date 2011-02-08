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
 * @version $Id: ProcessMenuDataVersion.java,v 1.2 2009/06/02 03:13:59 jchurin Exp $
 */  

package org.tolven.app.process;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tolven.app.MessageProcessorLocal;
import org.tolven.app.UpdateMenudataVersionLocal;
import org.tolven.app.entity.MenuDataVersionMessage;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
@Stateless
@Local(UpdateMenudataVersionLocal.class)
public class ProcessMenuDataVersion implements MessageProcessorLocal {

	private Logger logger = Logger.getLogger(this.getClass());

	@EJB org.tolven.app.MenuLocal menuBean;
	
	@Override
	public void process(Object message, Date now) {
		if (message instanceof MenuDataVersionMessage) { 
			menuBean.updateMenuDataVersion((MenuDataVersionMessage)message);
		}
	}

}
