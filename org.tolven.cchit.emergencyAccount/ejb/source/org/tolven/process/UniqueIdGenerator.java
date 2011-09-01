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
 * This file contains UniqueIdGenerator class used to generate unique id for each patient.
 *
 * @package org.tolven.process
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.process;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.EmergencyAccountLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ST;
import org.tolven.trim.ex.ActEx;

/**
 * This class generates unique id for each patient.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 01/25/2011           	| Valsaraj Viswanathan 	| Initial Version 						
==============================================================================================================================================
*/
public class UniqueIdGenerator extends EmergencyComputeBase {
	private ActEx act;
	private EmergencyAccountLocal emergencyAccountBean;
	private Random random;
	private String randomID;
	private int randomInt; 
	private String studyName;
	private String path;
	private String patientName;
	private int[] currentIds;
	private String[] currentPatients;
	private String[] currentStudys;
	
	private boolean enabled;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * Compute call to generate and set unique id to patient TRIM.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 */
	public void compute() throws Exception {
		if (isEnabled()) {
			act = (ActEx) this.getAct();
			getCurrentList();
			String uniqueId = getUniqueId();
			
			if (uniqueId != null && !uniqueId.isEmpty()) {
				act.getRelationship().get("uniqueId").setEnabled(false);
				setRandomId(uniqueId);
			} else {
				generateRandomId(5);	
				setRandomId(formatRandom(randomInt, 5));
			}
			
			setRandomPatientId();			
			disableCompute();
		}
	}

	/**
	 * Returns unique id.
	 * 
	 * added on 02/04/2010
	 * @author valsaraj
	 */
	public String getUniqueId() {
		String uniqueId = act.getRelationship().get("uniqueId").getAct()
					.getObservation().getValues().get(0).getST().getValue();

		return uniqueId;
	}
	
	/**
	 * This function is used to convert string to ST and returns generated ST.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj  
	 * @param str - string
	 * @return st - ST
	 */
	public ST getST(String str) {
		ST st = new ST();
		st.setValue(str);
		
		return st;
	}

	/**
	 * This function is used to disable compute.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 */
	protected void disableCompute() {
		for (Property property : getComputeElement().getProperties()) {
			if ("enabled".equals(property.getName())) {
				property.setValue(Boolean.FALSE);
			}
		}
	}

	/**
	 * This function is used to compute random id. Checks whether the generated id is already existing.
	 * If the id is already existing a new id is generated.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj  
	 * @param digitCount - digit count
	 * @return void
	 */
	protected void generateRandomId(int digitCount) {
		boolean idDuplication = false;
		random = new Random();
		final long MAX = 999999999, MIN = 999999;
		random.setSeed((long) (Math.floor(Math.random() * (MAX - MIN + 1)) + MIN));
		randomInt = random.nextInt((int) Math.pow(10, digitCount));
		
		if (randomInt == 0) {
			generateRandomId(digitCount);
		}
		
		for (int i = 0; i < currentIds.length; i++) {
			if (randomInt == currentIds[i]) {
				idDuplication = true;
			}
		}
		
		if (idDuplication) {
			generateRandomId(digitCount);
		}
	}
	
	/**
	 * Formats all the random id to letters by padding zeros.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj 
	 * @param id - generated random id
	 * @param digitCount - digit count
	 * @return formatted - random id.
	 */
	protected String formatRandom(int id, int digitCount) {
		String formatted;
		StringBuffer current = new StringBuffer(Integer.toString(id));
		StringBuffer zeros = new StringBuffer();
		int length = current.length();
		
		if (length < digitCount) {
			for (int i = digitCount; i > length; i--) {
				zeros.append("0");
			}
		}
		
		zeros.append(current);
		formatted = new String(zeros);
		
		return formatted;
	}

	/**
	 * Gets all the patients information from the StudyIDs list.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 */
	protected void getCurrentList() {
		MenuStructure ms = getMenuBean().findMenuStructure(
				getAccountUser().getAccount().getId(), getPath());
		MenuQueryControl ctrl = new MenuQueryControl();
		ctrl.setMenuStructure(ms);
		ctrl.setNow(new Date());
		ctrl.setAccountUser(getAccountUser());
		ctrl.setActualMenuStructure(ms);
		List<MenuData> items = getMenuBean().findMenuData(ctrl);
		int count = 0;
		currentStudys = new String[items.size()];
		currentPatients = new String[items.size()];
		
		for (MenuData item : items) {
			currentStudys[count] = item.getString02();
			currentPatients[count++] = item.getString03();
		}
		
		List<String> allIds = getEmergencyAccountBean().getAllStudyIds(getStudyName());
		currentIds = new int[allIds.size()];
		
		if (! allIds.isEmpty()) {
			count = 0;
			
			for(String id : allIds) {
				currentIds[count++] = Integer.parseInt(id);
			}
		}		
	}

	/**
	 * Gets StudyID of the patient.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 * @return id - StudyID of the patient
	 */
	protected String findId() {
		String id = null;
		
		try {			
			MenuStructure ms = getMenuBean().findMenuStructure(
					getAccountUser().getAccount().getId(), getPath());
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setMenuStructure(ms);
			ctrl.setNow(new Date());
			ctrl.setAccountUser(getAccountUser());
			ctrl.setActualMenuStructure(ms);
			List<MenuData> items = getMenuBean().findMenuData(ctrl);
			
			for (MenuData item : items) {
				if (patientName.equalsIgnoreCase(item.getString03())
						&& getStudyName().equalsIgnoreCase(item.getString02())) {
					id = item.getString01();
				}
			}
		} catch (Exception e) {
			TolvenLogger.info(e.getMessage(), InsertAct.class);
			e.printStackTrace();
		}
		
		return id;
	}

	/**
	 * Sets RandomPatientID fields.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 */
	public void setRandomPatientId() {
		List<ObservationValueSlot> uniqueIdValueSlot = act.getRelationship()
				.get("uniqueId").getAct().getObservation().getValues();
		uniqueIdValueSlot.get(0).setST(getST(randomID));
		uniqueIdValueSlot.get(1).setST(getST(getStudyName()));
		uniqueIdValueSlot.get(2).setST(getST(patientName));
	}
	    
    /**
     * This function is used to get an initialized MenuQueryControl object.
     * 
     * added on 01/25/2011
	 * @author valsaraj
     * @param menuPath - menu path
     * @return MenuQueryControl - MenuQueryControl object
     */
    public MenuQueryControl getMenuQueryControl(String menuPath){
            MenuStructure ms = getMenuBean().findMenuStructure(getAccountUser().getAccount().getId(), menuPath );
            MenuQueryControl ctrl = new MenuQueryControl();
            ctrl.setMenuStructure(ms);
            ctrl.setNow(new Date());
            ctrl.setAccountUser(getAccountUser());
            Map<String, Long> nodeValues = new HashMap<String, Long>(10);
            nodeValues=getContextList().get(0).getNodeValues();
            ctrl.setOriginalTargetPath(new MenuPath(ms.instancePathFromContext(nodeValues, true)));
            ctrl.setRequestedPath(ctrl.getOriginalTargetPath());
            ctrl.setActualMenuStructure(ms);        
            
            return ctrl;
    }
    
    
    /**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random
	 *            the random to set
	 */
	public void setRandom(Random random) {
		this.random = random;
	}

	/**
	 * @return the studyName
	 */
	public String getStudyName() {
		if (studyName == null) {
			setStudyName("cchit");
		}		
		return studyName;
	}

	/**
	 * @param studyName
	 *            the studyName to set
	 */
	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param setPath
	 *            the setPath to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public int getRandomInt() {
		return randomInt;
	}

	public void setRandomInt(int randomInt) {
		this.randomInt = randomInt;
	}

	public int[] getCurrentIds() {
		return currentIds;
	}

	public void setCurrentIds(int[] currentIds) {
		this.currentIds = currentIds;
	}

	public String getPatientName() {
		return patientName;
	}
	
	public void setPatientName(String name) {
		patientName = name;
	}
	
	public String getRandomId() {
		return randomID;
	}

	public void setRandomId(String id) {
		this.randomID = id;
	}
}
