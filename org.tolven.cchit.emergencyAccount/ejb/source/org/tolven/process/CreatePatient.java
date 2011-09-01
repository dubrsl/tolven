package org.tolven.process;

import java.util.List;

import org.tolven.app.entity.MenuData;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.II;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.SETIISlotEx;


public class CreatePatient extends ComputeBase {
	
	private ActEx act;
	private boolean enabled;
	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	public void compute() throws Exception {
		TolvenLogger.info("Compute enabled for CreatePatient=" + isEnabled(),
				this.getClass());
		super.checkProperties();

		if (isEnabled()) {
			TolvenLogger.info("Inside CreatePatient compute", this.getClass());
			act = (ActEx) this.getAct();
			String uniqueId = act.getRelationship().get("patientInfo").getAct()
					.getObservation().getValues().get(0).getST().toString();
			II trimII = ((SETIISlotEx) act.getParticipation().get("subject")
					.getRole().getId()).getFor().get(
					getAccountUser().getAccount());
			String root = trimII.getRoot();
			String extension = uniqueId;
			MenuData mdPatient = null;
			List<MenuData> mdPatients = getMenuBean().findMenuDataById(
					getAccountUser().getAccount(), root, extension);

			if (mdPatients.size() > 0) {
				mdPatient = mdPatients.get(0);
				trimII.setExtension(mdPatient.getPath());
			}

			disableCompute();
		}
	}
	
	private void disableCompute() {
		for (Property property : getComputeElement().getProperties()) {
			if ("enabled".equals(property.getName())) {
				property.setValue(Boolean.FALSE);
			}
		}
	}
}
