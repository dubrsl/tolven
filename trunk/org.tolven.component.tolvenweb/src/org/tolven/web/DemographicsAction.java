package org.tolven.web;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.tolven.core.DemographicsLocal;
import org.tolven.core.TolvenPropertiesLocal;

public class DemographicsAction extends TolvenAction {

    @EJB
    private DemographicsLocal demographicsLocal;

	public DemographicsLocal getDemographicsLocal() {
		return demographicsLocal;
	}
	public void setDemographicsLocal(DemographicsLocal demographicsLocal) {
		this.demographicsLocal = demographicsLocal;
	}
	private ArrayList<SelectItem> statesUsa;

	/**
	 * @return the statesUsa
	 */
	public ArrayList<SelectItem> getStatesUsa() {
		if(null == statesUsa){
			statesUsa = new ArrayList<SelectItem>();
			Map<String, String> map = getDemographicsLocal().retrieveAllStates();
			Set<String> mapKeys = map.keySet();
			for(String key : mapKeys){
				SelectItem item = new SelectItem();
				item.setValue(key);
				item.setLabel(map.get(key));
				statesUsa.add(item);
			}
		}
		return statesUsa;
	}
	/**
	 * @param statesUsa the statesUsa to set
	 */
	public void setStatesUsa(ArrayList<SelectItem> statesUsa) {
		this.statesUsa = statesUsa;
	}
	/**
	 * @return the sureBean
	 */

}
