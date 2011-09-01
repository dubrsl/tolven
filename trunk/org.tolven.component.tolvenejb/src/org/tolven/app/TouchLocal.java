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
 * @version $Id: TouchLocal.java,v 1.2 2010/03/17 20:10:22 jchurin Exp $
 */  

package org.tolven.app;

import java.util.List;

import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.Touch;

/**
 * Services related to Placeholder Touching which is used to rerun rules for some data when other, indirect data (placeholders) are updated.
 * @author John Churin
 *
 */
public interface TouchLocal extends MessageProcessorLocal {
	
    /**
     * Return a list of all touchIfs which is the reverse of touches. 
     * @param mdPlaceholder
     * @return
     */
    public List<Touch> findTouches( MenuData mdPlaceholder );
    
    /**
     * Return a list of placeholder that need to be updated because the focal placeholder 
     * has changed.
     * @param mdPlaceholder Focal placeholder
     * @return The list of touches relating to the specified focal placeholder
     */
    public List<Touch> findTouched( MenuData mdPlaceholder );
    
    /**
     * Persist a new touch entry
     * @param touch
     */
    public void persistTouch(Touch touch);

}
