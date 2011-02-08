package org.tolven.trim.ex;

import java.io.Serializable;
import java.util.List;

import org.tolven.trim.Observation;
import org.tolven.trim.ObservationValueSlot;

public class ObservationEx extends Observation implements Serializable {

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
