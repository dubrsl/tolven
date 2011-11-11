package org.tolven.trim.ex;

import java.io.Serializable;
import java.util.List;

import org.tolven.trim.Act;
import org.tolven.trim.BindTo;
import org.tolven.trim.Compute;
import org.tolven.trim.Observation;
import org.tolven.trim.ObservationValueSlot;

public class ObservationEx extends Observation implements Serializable {

	public void blend( Observation obsInclude ) {
		ObservationEx obsIncludeEx = (ObservationEx) obsInclude;

		// Blend all computes

		if (this.getInterpretationCode() == null) this.setInterpretationCode(obsIncludeEx.getInterpretationCode());
		if (this.getMethodCode() == null) this.setMethodCode(obsIncludeEx.getMethodCode());
		if (this.getPublicHealthCase() == null) this.setPublicHealthCase(obsIncludeEx.getPublicHealthCase());
		if (this.getTargetSiteCode() == null) this.setTargetSiteCode(obsIncludeEx.getTargetSiteCode());
		if (this.getValue() == null) this.setValue(obsIncludeEx.getValue());
	}

	/**
	 * Get single value 
	 */
	public ObservationValueSlot getValue() {
		List<ObservationValueSlot> values = getValues();
		if (values.size()==1) return values.get(0);
		return null;
    }

    public void setValue(ObservationValueSlot value) {
		List<ObservationValueSlot> values = getValues();
		if (values.size()==1) {
			values.set(0, value);
		} else {
	        values.add(value);
		}
    }

}
