/**
 * 
 */
package org.tolven.process;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.tolven.core.entity.Account;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimEx;

/**
 * @author 
 *
 */
public class FindEmergencyAccount extends EmergencyComputeBase {
	private ActEx act;
	private String dccAccountId = "";
	private String currentAccountId;
	private boolean isDCC = false;
	private boolean enabled;
	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Checks weather the current account is the emergency account.
	 */
	public void compute() throws Exception {
		
		if(isEnabled()) {
			act = (ActEx) this.getAct();
			TrimEx dccTrim = getLatestTrim("echr:admin:dccAccounts:account","DateSort=DESC");
			currentAccountId = Long.toString(getAccountUser().getAccount().getId());
			
			try {
				dccAccountId = ((ActEx)dccTrim.getAct()).getRelationship().get("accountDetails").getAct().getObservation().getValues().get(0).getST().toString();
			} catch(NullPointerException np) {
				isDCC = false;
			}
			
			if(dccAccountId.equalsIgnoreCase(currentAccountId)) {
				isDCC = true;
			}
			act.getRelationship().get("isDCC").setEnabled(isDCC);
			String studyName = getPropertyBean().getProperty("catissue.collection.protocol");
			
			if(isDCC) {
				String iSpy2Id = act.getParticipations().get(0).getRole().getPlayer().getName().getENS().get(0).getParts().get(2).getST().getValue();
				List<Object[]> targetPatients = getEmergencyAccountBean().getPatientIdByStudyId(iSpy2Id, studyName);
				
				if(! targetPatients.isEmpty()) {
					act.getRelationship().get("isDCC").getAct().getObservation().getValues().get(0).getST().setValue((String)targetPatients.get(0)[0]);
					act.getRelationship().get("isDCC").getAct().getObservation().getValues().get(1).getST().setValue(((Account)targetPatients.get(0)[1]).getId()+"");
				} else {
					TolvenLogger.info("ERROR------------------> : No patient found with same last name as the patient", this.getClass());
					TolvenLogger.info("ERROR------------------> : This crf cannot be submitted", this.getClass());
					act.getRelationship().get("isDCC").getAct().getObservation().getValues().get(0).getST().setValue("");
					act.getRelationship().get("isDCC").getAct().getObservation().getValues().get(1).getST().setValue("");
				}
			}
			disableCompute();
		}
	}
	
	/**
	 *  Returns latest trim in the given path, wizard name not required.
	 * @param path
	 * @param conditions
	 * @return
	 * @throws ParseException 
	 * @throws JAXBException 
	 */
	private TrimEx getLatestTrim(String path, String conditions) throws ParseException, JAXBException {
		TrimEx _trimEx = null;
		List<Map<String, Object>> list = getCCHITBean().findAllMenuDataList(path,getContextList().get(0).getPathString(),
							conditions, getAccountUser());
		Long id = new Long(0);
		if (! list.isEmpty()) {
			Map<String, Object> mapList = list.get(0);
			id = new Long(mapList.get("id").toString());
		}
		_trimEx = getCCHITBean().findTrimData(id, getAccountUser(),getPrivateKey());
		return _trimEx;
	}
	
	 /**
	 * This function is used to disable compute.
	 * @author 
	 * @param void
	 * @return void
	 */
	private void disableCompute() {
		for (Property property : getComputeElement().getProperties()) {
			if ("enabled".equals(property.getName())) {
				property.setValue(Boolean.FALSE);
			}
		}
	}

	
}
