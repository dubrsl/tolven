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
package org.tolven.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActClass;
import org.tolven.trim.ActMood;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActRelationshipDirection;
import org.tolven.trim.ActRelationshipType;
import org.tolven.trim.CE;
import org.tolven.trim.LabelFacet;
import org.tolven.trim.Observation;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ex.ActEx;

/**
 * Retrieves Physician specific data and saves in the trim.
  * @since v0.0.1
 */
public class ComputePrescriber extends ComputeBase {
	private ActEx act;
	private String knownType = null;
	private ArrayList<String> spiNumbersInAccount;
	private String spiInAccount;
	private int serviceLevel = 0;
	private String action;
	private boolean enabled;

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

	public ComputePrescriber(){
		super();
	}
	
	public void compute() throws Exception {
		super.checkProperties();
		if (isEnabled()) {
			knownType = getAccountUser().getAccount().getAccountType().getKnownType();
			act = (ActEx) this.getAct();
			if (knownType.equals("echr")) {
				if (getAction().equals("prescriberDetails")) {
					
					List<ObservationValueSlot> prescriberOVS = act.getRelationship().get("prescriber").getAct().getObservation().getValues();
					String updateStatus = "";
					
					if (act.getRelationship().get("serviceLevel") != null) {
						List<ObservationValueSlot> serviceLevelOVS = act.getRelationship().get("serviceLevel").getAct().getObservation().getValues();
						if (serviceLevelOVS.get(0).getSETCES().size() > 0) {
							serviceLevel = computeServiceLevel(serviceLevelOVS.get(0).getSETCES());
							prescriberOVS.get(3).setST(	new ComputeMessageID().getST(String.valueOf(serviceLevel)));
						}
						if (prescriberOVS.get(3).getST().getValue().trim().length() == 0) {
							prescriberOVS.get(3).setST(	new ComputeMessageID().getST(String.valueOf(serviceLevel)));
						}
					}
					
					if (act.getRelationship().get("updateStatus") != null) {
						 updateStatus = act.getRelationship().get("updateStatus").getAct().getObservation().getValues().get(0).getST().getValue();
					}
					
					String addPrescStatus = null;
					addPrescStatus = prescriberOVS.get(9).getST().getValue();
					prescriberOVS.get(9).setST(	new ComputeMessageID().getST("Pending"));
					spiInAccount="";
					for(String spi : getSpiNumbersInAccount()){
						spiInAccount = spiInAccount + spi + "|";
					}
					prescriberOVS.get(19).getST().setValue(spiInAccount);
					
					if ((addPrescStatus.equals("Error") && updateStatus.length() > 0) ||
							addPrescStatus.equals("Active") && act.getRelationship().get("updateStatus") == null) {
						act.getRelationships().add(createUpdateStatusRelationship());
					}
				}
			}
		}
	}
	
	/**
	 * Method to compute the serviceLevel
	 * @param setCE
	 * @return
	 */
	private int computeServiceLevel(List<CE> setCE) {
		final double binary = 2;
		int serviceLevel = 0;
		
		for(CE level : setCE) {
			if (level.getDisplayName().equals("NewRx")) {
				serviceLevel = serviceLevel + (int)Math.pow(binary, 0);
			}
			if (level.getDisplayName().equals("RefillResponse")) {
				serviceLevel = serviceLevel + (int)Math.pow(binary, 1);
			}
			if (level.getDisplayName().equals("RxChangeResponse")) {
				serviceLevel = serviceLevel + (int)Math.pow(binary, 2);
			}
			if (level.getDisplayName().equals("RxFill")) {
				serviceLevel = serviceLevel + (int)Math.pow(binary, 3);
			}
			if (level.getDisplayName().equals("CancelRx")) {
				serviceLevel = serviceLevel + (int)Math.pow(binary, 4);
			}
			if (level.getDisplayName().equals("Medication History")) {
				serviceLevel = serviceLevel + (int)Math.pow(binary, 5);
			}
			if (level.getDisplayName().equals("Eligibility")) {
				serviceLevel = serviceLevel + (int)Math.pow(binary, 6);
			}
		}
		return serviceLevel;
	}

	/**
	 * Creates an ActRelationship named updateStatus.
	 * @author unni.s@cyrusxp.com
	 * @return ActRelationship
	 */
	private ActRelationship createUpdateStatusRelationship() {
		ActRelationship relUpdate = new ActRelationship();
		relUpdate.setTypeCode(ActRelationshipType.NAME);
		relUpdate.setDirection(ActRelationshipDirection.OUT);
		relUpdate.setName("updateStatus");
		
		Act actUpdate = new Act();
		actUpdate.setMoodCode(ActMood.EVN);
		actUpdate.setClassCode(ActClass.ENTRY);
		
		Observation observation = new Observation();
		ObservationValueSlot ovs = new ObservationValueSlot();
		LabelFacet labelUpdate =  new LabelFacet();
		labelUpdate.setValue("Update Status");
		ovs.setLabel(labelUpdate);
		ovs.setST(new ComputeMessageID().getST("Updating"));
		observation.getValues().add(ovs);
		actUpdate.setObservation(observation );
		relUpdate.setAct(actUpdate);
		return relUpdate;
	}

	/**
	 * The method is used to retrieve all the SPIs in the admin:staff:all list
	 * @param path
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getSPINumbersInAccount(String path) {
		ArrayList<String> result = null;
		try {
			MenuStructure ms = getMenuBean().findMenuStructure(
					getAccountUser().getAccount().getId(), path);
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setMenuStructure(ms);
			ctrl.setNow(new Date());
			ctrl.setAccountUser(getAccountUser());
			Map<String, Long> nodeValues = new HashMap<String, Long>(10);
			nodeValues = getContextList().get(0).getNodeValues();
			ctrl.setOriginalTargetPath(new MenuPath(ms.instancePathFromContext(
					nodeValues, true)));
			ctrl.setRequestedPath(ctrl.getOriginalTargetPath());
			ctrl.setActualMenuStructure(ms);
			List<MenuData> items = getMenuBean().findMenuData(ctrl);
			result = new ArrayList<String>();
			for (MenuData item : items) {
				if(null != item.getReference() && null != item.getReference().getPqUnits03())
					result.add(item.getReference().getPqUnits03().substring(0,10));
			}
		} catch (Exception e) {
			TolvenLogger.info(e.getMessage(), InsertAct.class);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return the spiNumbersInAccount
	 */
	public ArrayList<String> getSpiNumbersInAccount() {
		if(null == this.spiNumbersInAccount){
			this.spiNumbersInAccount = getSPINumbersInAccount("echr:admin:staff:all");
		}
		return spiNumbersInAccount;
	}

	/**
	 * @param spiNumbersInAccount the spiNumbersInAccount to set
	 */
	public void setSpiNumbersInAccount(ArrayList<String> spiNumbersInAccount) {
		this.spiNumbersInAccount = spiNumbersInAccount;
	}
}