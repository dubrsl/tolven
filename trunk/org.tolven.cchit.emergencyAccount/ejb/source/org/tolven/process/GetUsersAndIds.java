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
 * This file contains GetUsersAndIds class used to 
 * retrieve all accounts and their ids for a tolven user.
 *
 * @package org.tolven.process
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.process;

import java.util.List;
import java.util.Map;

import org.tolven.app.CCHITLocal;
import org.tolven.app.EmergencyAccountLocal;
import org.tolven.trim.CE;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimEx;

/**
 * This class retrieves all accounts and their ids for a tolven user.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 01/25/2011           	| Valsaraj Viswanathan 	| Initial Version 	
2		| 02/07/2011			| Valsaraj Viswanathan 	| Added getPriorEmergencyAccount() and getLatestTrim() modified compute() and writeCe()
 															to set prior emergency account.
==============================================================================================================================================
*/
public class GetUsersAndIds extends EmergencyComputeBase {
	private CCHITLocal cchitBean;
	private EmergencyAccountLocal emergencyAccountBean;
	private TrimEx trim;
	private boolean enabled;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	private static String EMERGENCY_ACCOUNT_LIST = "echr:admin:emergencyAccounts:account";
	
	/**
	 * Compute call to retrieve all accounts and their ids for a tolven user.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 */
	public void compute() throws Exception {
		if (isEnabled()) {
			trim = (TrimEx) this.getTrim();
			ValueSet vSet = trim.getValueSet().get("accounts");
			ValueSet tempVSet = trim.getValueSet().get("template");
			CE tempCE = (CE)tempVSet.getBindsAndADSAndCDS().get(0);
			String code = tempCE.getCode();
			String codeSystem = tempCE.getCodeSystem();
			String codeSystemVersion = tempCE.getCodeSystemVersion();
			String sCode = code.substring(0, 2);
			String iCode = code.substring(2, code.length());
			List<Object[]> accounts = getEmergencyAccountBean().getAllEmergencyAccountIds();
			String priorEmergencyAccount = getPriorEmergencyAccount();
			CE priorCe = null;
			CE ce;
			
			for (Object[] account : accounts) {
		        int val = Integer.parseInt(iCode);
				iCode = Integer.toString(++val);
				String newCode = new String(sCode+iCode);
				String newDisplayName = new String((Long)account[0] + " / " + (String)account[1]);
				ce = writeCe(newCode, newDisplayName, codeSystem, codeSystemVersion, vSet);
				
				if (newDisplayName.equals(priorEmergencyAccount)) {
					priorCe = ce;
				}
		    }
			
			if (priorCe != null) {
				((ActEx) trim.getAct()).getRelationship().get("accountDetails").getAct().getObservation().getValues().get(3).setCE(priorCe);
			}
			
			disableCompute();
		}
	}
	
	/**
	 * Returns prior emergency account.
	 * 
	 * added on 02/07/2011
	 * @author valsaraj
	 */
	public String getPriorEmergencyAccount() {
		TrimEx eaTrim = getLatestTrim(EMERGENCY_ACCOUNT_LIST, "DateSort=DESC");
		String account = null;

		try {
			List<ObservationValueSlot> accountValues = ((ActEx) eaTrim.getAct())
					.getRelationship().get("accountDetails").getAct()
					.getObservation().getValues();
			account = accountValues.get(0).getST().toString() + " / "
					+ accountValues.get(1).getST().toString();
		} catch (Exception e) {

		}

		return account;
	}
	
	/**
	 * Returns latest trim in the given path, wizard name not required.
	 * 
	 * added on 02/07/2011
	 * @author valsaraj 
	 * @param path - list path
	 * @param conditions - conditions
	 * @return trimEx - trim
	 */
	private TrimEx getLatestTrim(String path, String conditions) {
		TrimEx trimEx = null;
	
		try {
			List<Map<String, Object>> list = getCCHITBean().findAllMenuDataList(
					path, getContextList().get(0).getPathString(), conditions,
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
	 * To write each CE to valueset.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 * @param newCode - new code
	 * @param newDisplayName - new display name
	 * @param codeSystem - code system
	 * @param codeSystemVersion - code system version
	 * @param vSet - valueset
	 */
	CE writeCe(String newCode, String newDisplayName, String codeSystem, String codeSystemVersion,ValueSet vSet) {
		CE newCE = new CE();
		newCE.setCode(newCode);
		newCE.setDisplayName(newDisplayName);
		newCE.setCodeSystem(codeSystem);
		newCE.setCodeSystemVersion(codeSystemVersion);
		vSet.getBindsAndADSAndCDS().add(newCE);
		
		return newCE;
	}
		
	/**
	 * This function is used to disable compute.
	 * 
	 * added on 01/25/2011
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
