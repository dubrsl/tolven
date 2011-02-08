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
 * @author John Churin
 * @version $Id: WMLogger.java,v 1.2 2010/03/17 20:10:23 jchurin Exp $
 */  

package org.tolven.rules;

import org.apache.log4j.Logger;
import org.drools.WorkingMemoryEventManager;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.ActivationLogEvent;
import org.drools.audit.event.ILogEventFilter;
import org.drools.audit.event.LogEvent;
/**
 * This class is used to log debug output from rule execution. 
 * @author John Churin
 *
 */
public class WMLogger extends WorkingMemoryLogger {
	private Logger logger = Logger.getLogger(this.getClass());
	
	
	public WMLogger(WorkingMemoryEventManager workingMemory) {
		super(workingMemory);
	    addFilter(new WMLogFilter());
	}
	
	@Override
	public void logEventCreated(LogEvent event) {
		if (event instanceof ActivationLogEvent) {
			ActivationLogEvent ale = (ActivationLogEvent) event;
			logger.debug( "Fire rule '" + ale.getRule() + "' using: " + ale.getDeclarations() );
		} else {
			logger.debug( event.toString());
		}
	}
}

class WMLogFilter implements ILogEventFilter {

	@Override
	public boolean acceptEvent(LogEvent event) {
		if (event.getType()==LogEvent.AFTER_ACTIVATION_FIRE) return true;
//		if (event.getType()==LogEvent.ACTIVATION_CREATED) return true;
		return false;
	}
}
