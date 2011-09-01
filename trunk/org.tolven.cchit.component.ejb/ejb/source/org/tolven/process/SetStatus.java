/*
 *  Copyright (C) 2011 Tolven Inc
 *
 * This library is free software; you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * Contact: info@tolvenhealth.com
 */
/**
 * This file contains SetStatus class.
 *
 * @package org.tolven.app.bean
 * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.process;

import java.util.Date;
import java.util.List;

import org.tolven.trim.CE;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimFactory;

/**
 * <p>The class sets a status flag to check whether the patient is submitted
 * through the reminder scheduler or not. It also set the notification preference to No by default.</p>
 * 
 * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
==========================================================================================
No:  	|Created/Updated Date   |Created/Updated By      |Method name/Comments            |
==========================================================================================
1    	| 06/22/2010           	|Unnikrishnan Skandappan |Initial Version 	              |
==========================================================================================
*/
public class SetStatus extends CCHITComputeBase {
	private ActEx act;
	private TrimFactory trimFactory;
	private boolean enabled;
	private String action;
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * Gets the trimFactory.
	 * @return trimFactory - trim factory
	 */
	public TrimFactory getTrimFactory() {
		trimFactory = new TrimFactory();
		
		return trimFactory;
	}
	
	/**
	 * This method processes the compute call and sets status in trim.
	 * 
	 * added on 06/22/2010
	 * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
	 */
	@Override
	public void compute() throws Exception {
		super.checkProperties();
		
		if (isEnabled()) {
			act = (ActEx) this.getAct();
			
			if (getAction().equals("setStatusForScheduler")) {
				List<ObservationValueSlot> notificationStatusOVS = act
						.getRelationship().get("notification").getAct()
						.getObservation().getValues();
				String status = notificationStatusOVS.get(8).getST().getValue();
				List<ObservationValueSlot> preferenceStatusOVS = act
						.getRelationship().get("preference").getAct()
						.getObservation().getValues();
				
				if (preferenceStatusOVS.get(1).getTS().getValue().isEmpty()) {
					/*Date of Creation of Patient.*/
					preferenceStatusOVS.get(1).setTS(getTrimFactory().createNewTS(new Date()));
				}				
				
				for (ObservationValueSlot ovs : act.getRelationship().get(
						"notification").getAct().getObservation().getValues()) {
					if (ovs.getCE() != null) {
						CE _ce = new CE();
						
						if (ovs.getCE().getDisplayName().isEmpty()) {
							_ce.setDisplayName("No");
							_ce.setCode("C0580798");
							_ce.setCodeSystem("2.16.840.1.113883.6.56");
							_ce.setCodeSystemVersion("2007AA");
							ovs.setCE(_ce);
						}
					}
				}
				
				if (status.isEmpty() || status.equals("true")) {
					notificationStatusOVS.get(8).getST().setValue("false");
					disableCompute();
				}
			}
		}
	}
	
	/**
	 * Method used to disable the compute function.
	 * 
	 * added on 06/22/2010
	 * @author Unnikrishnan Skandappan <unni.s@cyrusxp.com>
	 */
	private void disableCompute() {
		for (Property property : getComputeElement().getProperties()) {
			if ("enabled".equals(property.getName())) {
				property.setValue(Boolean.FALSE);
			}
		}
	}
}