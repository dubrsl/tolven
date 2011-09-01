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
 * This file contains RetrieveSharingDetails class used to 
 * set sharing information.
 *
 * @package org.tolven.process
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.process;

import java.security.Principal;
import java.security.acl.Group;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.xml.bind.JAXBException;

import org.tolven.app.CCHITLocal;
import org.tolven.app.TrimLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.TolvenPerson;
import org.tolven.trim.ADXPSlot;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.AddressPartType;
import org.tolven.trim.CE;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ENXPSlot;
import org.tolven.trim.Entity;
import org.tolven.trim.LivingSubject;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimEx;

/**
 * This class sets sharing information.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 01/27/2011           	| Valsaraj Viswanathan 	| Initial Version 						
==============================================================================================================================================
*/
public class RetrieveSharingDetails extends EmergencyComputeBase {
	public static String ACCOUNT_EMAIL = "org.tolven.account.email";
	
	private static String EMERGENCY_ACCOUNT_LIST = "echr:admin:emergencyAccounts:account";
	private ActEx act;
	private TrimEx trim;
	
	private boolean enabled;
	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * Compute call to set sharing information to TRIM.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj
	 */
	public void compute() throws Exception {
		TolvenLogger.info("Compute enabled for RetrieveSharingDetails = " + isEnabled(), this.getClass());
		super.checkProperties();
		
		if (isEnabled()) {
			act = (ActEx) this.getAct();

			try {
				TrimEx patientInfoTemplateTrim = getTrimBean().findTrim(
						"obs/evn/SharePatientInfo");
				if (setEmergencyAccountDetails(patientInfoTemplateTrim) && setPatientDetails(patientInfoTemplateTrim)) {
					setPatientAccountDetails(patientInfoTemplateTrim);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			disableCompute();
		}
	}
	
	/**
	 * To insert Emergency account related values to trim while synchronizing with Emergency account
	 * 
	 * added on 02/18/2011
	 * @author Nevin
	 */
	public static void insertEmergencyAccountDetails(TrimEx trim, AccountUser accountUser, String emergencyAccountDetails) {
		try {
			TolvenLogger.info("insertEmergencyAccountDetails enabled for RetrieveSharingDetails", RetrieveSharingDetails.class);
			RetrieveSharingDetails r = new RetrieveSharingDetails();
			r.setAccountUser(accountUser);
			r.trim = (TrimEx) trim;
			r.act = (ActEx)trim.getAct();
			TrimEx patientInfoTemplateTrim = r.getTrimBean().findTrim("obs/evn/SharePatientInfo");
			if (r.setEmergencyAccountDetails(patientInfoTemplateTrim, emergencyAccountDetails) && r.setPatientDetails(patientInfoTemplateTrim)) {
				r.setPatientAccountDetails(patientInfoTemplateTrim);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	/**
	 * Sets patient details.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param patientInfoTemplateTrim - patient information template trim
	 * @return boolean status of operation
	 * @throws JAXBException 
	 */
	public boolean setPatientDetails(TrimEx patientInfoTemplateTrim) throws JAXBException {
		Object patDoc = null;
		if (getContextList().size() > 0) {
			patDoc = getCCHITBean().findDoc(getContextList().get(0).getNodeKeys()[1], getAccountUser(),getPrivateKey());
		} else {
			Long id = new Long(trim.getTolvenEventIds().get(0).getPath().toString().split("-")[1].split(":")[0]);
			patDoc = getCCHITBean().findDoc(id, getAccountUser(),getPrivateKey());
		}
		
		if (patDoc instanceof TrimEx) {
			TrimEx patientTrim = (TrimEx) patDoc;
			
			if (act.getRelationship().get("patientInfo") != null) {
				act.getRelationship().remove("patientInfo");
			}

			ActEx patientAct = (ActEx) patientTrim.getAct();
			ActRelationship patDetailsRelation = ((ActEx) patientInfoTemplateTrim
					.getAct()).getRelationship().get("patientInfo");
			List<ObservationValueSlot> patientDetailsSlot = patDetailsRelation
					.getAct().getObservation().getValues();
			patientDetailsSlot.get(0).setST(
					patientAct.getRelationship().get("uniqueId").getAct()
							.getObservation().getValues().get(0).getST());
			Entity patPlayer = ((ActEx) patientTrim.getAct()).getParticipation()
					.get("subject").getRole().getPlayer();
			setPatientName(patPlayer, patientDetailsSlot.get(1));
			LivingSubject patLivingSubject = patPlayer.getLivingSubject();
			setBirthTime(patLivingSubject, patientDetailsSlot.get(2));
			setGender(patLivingSubject, patientDetailsSlot.get(3));
			setAddress(patPlayer, patientDetailsSlot);
			act.getRelationships().add(patDetailsRelation);
			
			return true;
		}
		
		return false;
	}

	/**
	 * Sets patient's name.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param patPlayer - patient player entity
	 * @param patientNameSlot - patient name slot
	 */
	public void setPatientName(Entity patPlayer, ObservationValueSlot patientNameSlot) {
		try {
			List<ENXPSlot> nameParts = patPlayer.getName().getENS().get(0)
					.getParts();
			patientNameSlot.getST().setValue(
					(nameParts.get(2).getST() != null ? nameParts.get(2).getST()
							.getValue() : "")
							+ ", "
							+ (nameParts.get(0).getST() != null ? nameParts.get(0)
									.getST().getValue() : "")
							+ " "
							+ (nameParts.get(1).getST() != null ? nameParts.get(1)
									.getST().getValue() : ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets patient's gender.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param patLivingSubject - patient living subject
	 * @param genderSlot - gender slot
	 */
	public void setGender(LivingSubject patLivingSubject, ObservationValueSlot genderSlot) {
		try {
			CE genderCE = patLivingSubject.getAdministrativeGenderCode().getCE();
			String gender = "";
			
			if (genderCE != null) {
				gender = genderCE.getDisplayName();
			}
			
			genderSlot.getST().setValue(gender);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets patient's birth time.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param patLivingSubject - patient living subject
	 * @param birthTimeSlot - birth time slot
	 */
	public void setBirthTime(LivingSubject patLivingSubject, ObservationValueSlot birthTimeSlot) {
		try {
			birthTimeSlot.setTS(patLivingSubject.getBirthTime().getTS());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets patient's address.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param patPlayer - patient player entity
	 * @param patientDetailsSlot - patient details slot
	 */
	public void setAddress(Entity patPlayer,
			List<ObservationValueSlot> patientDetailsSlot) {
		List<ADXPSlot> addressParts = patPlayer.getPerson().getAddr().getADS()
				.get(0).getParts();

		for (ADXPSlot adxpSlot : addressParts) {
			if (adxpSlot.getType().compareTo(AddressPartType.AL) == 0
					&& "Address Line 1".equals(adxpSlot.getLabel())) {
				patientDetailsSlot.get(4).setST(adxpSlot.getST());
			} else if (adxpSlot.getType().compareTo(AddressPartType.AL) == 0
					&& "Address Line 2".equals(adxpSlot.getLabel())) {
				patientDetailsSlot.get(5).setST(adxpSlot.getST());
			} else if (adxpSlot.getType().compareTo(AddressPartType.CTY) == 0) {
				patientDetailsSlot.get(6).setST(adxpSlot.getST());
			} else if (adxpSlot.getType().compareTo(AddressPartType.STA) == 0) {
				patientDetailsSlot.get(7).setST(adxpSlot.getST());
			} else if (adxpSlot.getType().compareTo(AddressPartType.ZIP) == 0) {
				patientDetailsSlot.get(8).setST(adxpSlot.getST());
			} else if (adxpSlot.getType().compareTo(AddressPartType.CNT) == 0) {
				patientDetailsSlot.get(9).setST(adxpSlot.getST());
			}
		}
	}
	
	/**
	 * Sets emergency account details.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param patientInfoTemplateTrim - patient information template trim
	 * @return boolean status of operation
	 */
	public boolean setEmergencyAccountDetails(TrimEx patientInfoTemplateTrim) {
		TrimEx eaTrim = getLatestTrim(EMERGENCY_ACCOUNT_LIST,
				"DateSort=DESC");
		String accountId;
		String accountName;
		String accountEmail;

		if (eaTrim != null) {
			try {
				accountId = ((ActEx) eaTrim.getAct()).getRelationship().get(
						"accountDetails").getAct().getObservation().getValues()
						.get(0).getST().toString();
			} catch (Exception e) {
				TolvenLogger.info("Error-----> Exception in setting EA account Id. ID set to 0",
								this.getClass());
				accountId = "0";
			}

			try {
				accountName = ((ActEx) eaTrim.getAct()).getRelationship().get(
						"accountDetails").getAct().getObservation().getValues()
						.get(1).getST().toString();
			} catch (Exception e) {
				TolvenLogger.info("Error-----> Exception in setting DCC account name. Name set to 0",
								this.getClass());
				accountName = "0";
			}

			try {
				accountEmail = getCCHITBean().getAccountEmail(
						Long.parseLong(accountId), ACCOUNT_EMAIL);
	
				if (accountEmail == null || accountEmail.isEmpty())
					accountEmail = ((ActEx) eaTrim.getAct()).getRelationship().get(
							"accountDetails").getAct().getObservation().getValues()
							.get(2).getST().toString();
			} catch (Exception e) {
				TolvenLogger.info("Error-----> Exception in setting DCC account email. Email set to ''",
								this.getClass());
				accountEmail = "";
			}

			setEmergencyAccountRelation(patientInfoTemplateTrim, accountId, accountName, accountEmail);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets emergency account details.
	 * 
	 * added on 02/18/2011
	 * @author valsaraj 
	 * @param patientInfoTemplateTrim - patient information template trim
	 * @param emergencyAccountDetails - emergency account details
	 * @return boolean status of operation
	 */
	public boolean setEmergencyAccountDetails(TrimEx patientInfoTemplateTrim, String emergencyAccountDetails) {
		String accountId;
		String accountName;
		String accountEmail;
		
		if (emergencyAccountDetails != null) {
			String[] emergencyAccountDetailsArray = emergencyAccountDetails.split("/");
			
			try {
				accountId = emergencyAccountDetailsArray[0].trim();
			} catch (Exception e) {
				TolvenLogger.info("Error-----> Exception in setting EA account Id. ID set to 0",
								this.getClass());
				accountId = "0";
			}

			try {
				accountName = emergencyAccountDetailsArray[1].trim();
			} catch (Exception e) {
				TolvenLogger.info("Error-----> Exception in setting DCC account name. Name set to 0",
								this.getClass());
				accountName = "0";
			}

			try {
				accountEmail = getCCHITBean().getAccountEmail(
						Long.parseLong(accountId), ACCOUNT_EMAIL);
			} catch (Exception e) {
				TolvenLogger.info("Error-----> Exception in setting DCC account email. Email set to ''",
								this.getClass());
				accountEmail = "";
			}

			setEmergencyAccountRelation(patientInfoTemplateTrim, accountId, accountName, accountEmail);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets emergency account details in ActRelationship.
	 * 
	 * added on 02/18/2011
	 * @author valsaraj 
	 * @param patientInfoTemplateTrim - patient information template trim
	 * @param accountId - account id
	 * @param accountName - account name
	 * @param accountEmail - account email
	 */
	public void setEmergencyAccountRelation(TrimEx patientInfoTemplateTrim,
			String accountId, String accountName, String accountEmail) {
		ActRelationship emergencyAccountRelation = ((ActEx) patientInfoTemplateTrim
				.getAct()).getRelationship().get("emergencyAccount");

		if (act.getRelationship().get("emergencyAccount") != null) {
			act.getRelationship().remove("emergencyAccount");
		}

		ActEx emergencyAccountInfo = (ActEx) emergencyAccountRelation.getAct();
		emergencyAccountInfo.getObservation().getValues().get(0).getST()
				.setValue(accountId);
		emergencyAccountInfo.getObservation().getValues().get(1).getST()
				.setValue(accountName);
		emergencyAccountInfo.getObservation().getValues().get(2).getST()
				.setValue(accountEmail);
		act.getRelationships().add(emergencyAccountRelation);
	}
	
	/**
	 * Sets patient account details.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param patientInfoTemplateTrim - patient information template trim
	 */
	public void setPatientAccountDetails(TrimEx patientInfoTemplateTrim) {
		ActRelationship patientAccountRelation = ((ActEx) patientInfoTemplateTrim
				.getAct()).getRelationship().get("patientAccount");

		if (act.getRelationship().get("patientAccount") != null) {
			act.getRelationship().remove("patientAccount");
		}

		ActEx patientAccountInfo = (ActEx) ((ActEx) patientInfoTemplateTrim
				.getAct()).getRelationship().get("patientAccount")
				.getAct();
		List<ObservationValueSlot> obsVal = patientAccountInfo
				.getObservation().getValues();
		//obsVal.get(0).getST().setValue(getEmailAddress());
		//TolvenPerson user = ldapBean.findTolvenPerson(getAccountUser().getUser().getLdapUID());
		//obsVal.get(1).getST().setValue(user.getGivenName());
		act.getRelationships().add(patientAccountRelation);
	}
		
	/**
	 * Returns latest trim in the given path, wizard name not required.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj 
	 * @param path - list path
	 * @param conditions - conditions
	 * @return trimEx - trim
	 */
	private TrimEx getLatestTrim(String path, String conditions) {
		TrimEx trimEx = null;
	
		try {
			List<Map<String, Object>> list = getCCHITBean().findAllMenuDataList(
					path, getContextList().size() > 0 ? getContextList().get(0).getPathString() : path, conditions,
					getAccountUser());
			Long id = new Long(0);
			
			if(! list.isEmpty()) {
				Map<String, Object> mapList = list.get(0);
				 id = new Long(mapList.get("id").toString());
			}
			
			trimEx = getCCHITBean().findTrimData(id, getAccountUser(),getPrivateKey());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return trimEx;
	}

	
	 
	 /**
	 * This function is used to disable compute.
	 * 
	 * added on 01/27/2011
	 * @author valsaraj
	 */
	private void disableCompute() {
		for (Property property : getComputeElement().getProperties()) {
			if ("enabled".equals(property.getName())) {
				property.setValue(Boolean.FALSE);
			}
		}
	}

	

	
}
