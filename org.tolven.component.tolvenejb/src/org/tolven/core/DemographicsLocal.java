package org.tolven.core;

import java.util.ArrayList;
import java.util.Map;

import org.tolven.app.entity.DrugQualifier;


public interface DemographicsLocal {
	public Map<String, String> retrieveAllStates();
	public String getDosageForm(String input, boolean isId);
	public ArrayList<DrugQualifier> retrieveAllDrugQualifiers();

}
