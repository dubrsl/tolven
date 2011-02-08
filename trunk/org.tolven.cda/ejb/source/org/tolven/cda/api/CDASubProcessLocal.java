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
 * @version $Id: CDASubProcessLocal.java,v 1.1 2009/07/14 13:44:45 jchurin Exp $
 */  

package org.tolven.cda.api;

import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.entity.MenuData;
import org.w3c.dom.Document;

/**
 * Each implementation of this interface is an EJB stateless session bean that can process
 * a CDA message. The patient will already be determined, but can still be changed before it is
 * inserted into the rules for evaluation. 
 * @author John Churin
 *
 */
public interface CDASubProcessLocal {
	/**
	 * This method should determine if it has any interest in further processing of a CDA document.
	 * @param cda The CDA document as a dom object
	 * @param mdPatient The Patient as a tolven placeholder
	 * @param workingMemmory - insert any new facts, typically placeholders, into rule working memory.
	 */
	public void process( Document cda, MenuData mdPatient, AppEvalAdaptor app);
}
