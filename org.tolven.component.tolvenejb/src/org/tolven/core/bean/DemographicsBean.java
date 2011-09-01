package org.tolven.core.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.app.entity.DosageForm;
import org.tolven.app.entity.DrugQualifier;
import org.tolven.core.DemographicsLocal;
import org.tolven.app.entity.StateNames;

@Stateless
@Local(DemographicsLocal.class)
public class DemographicsBean implements DemographicsLocal{
	@PersistenceContext
    private EntityManager em;
  
	/**
     * Method to retrieve all the states in USA with their codes
     * @return
     */
    @SuppressWarnings("unchecked")
	public Map<String, String> retrieveAllStates(){
    	Map<String, String> usaStates = null;
    	String qs = null;
		Query query = null;
		qs = String.format(Locale.US, "SELECT st FROM StateNames st ORDER BY st.stateName");
		query = em.createQuery( qs );
		if(null != query.getResultList()){
			usaStates = new TreeMap<String, String>();
			ArrayList<StateNames> stateDetailsList = (ArrayList<StateNames>)query.getResultList();
			for(StateNames states : stateDetailsList){
				usaStates.put(states.getStateCode(),states.getStateName());
			}
		}
    	return usaStates;
    }
	/**
	 * Method to find the dosageForm id/desc when desc/id is provided.
	 * @param input
	 * @param isId
	 * @return
	 */
	public String getDosageForm(String input, boolean isId){
		String output = "";
		if (input!=null) {
	    	String qs = null;
			Query query = null;
			if (isId) {
				qs = String.format(Locale.US, "SELECT df FROM DosageForm df WHERE id="+input);
				query = em.createQuery( qs );
			} else {
				qs = String.format(Locale.US, "SELECT df FROM DosageForm df WHERE df.definition =:input");
				query = em.createQuery( qs );
				query.setParameter("input", input);
			}
			if(query.getResultList() != null && query.getResultList().size() > 0){
				if (isId) {
					output = ((DosageForm)query.getResultList().get(0)).getDefinition();
				} else {
					output = String.valueOf(((DosageForm)query.getResultList().get(0)).getId());
				}
			}
		}
    	return output;
	}
	/**
	 * Method to retrieve all the Drug Qualifiers
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<DrugQualifier> retrieveAllDrugQualifiers(){
		ArrayList<DrugQualifier> drugQualifiers = null;
    	String qs = null;
		Query query = null;
		qs = String.format(Locale.US, "SELECT dr FROM DrugQualifier dr");
		query = em.createQuery( qs );
		if(null != query.getResultList()){
			drugQualifiers = (ArrayList<DrugQualifier>)query.getResultList();
		}
    	return drugQualifiers;
	}

/*	@SuppressWarnings("unchecked")
	public List<StateLocation> retrieveAllStates(){
    	String qs =  String.format(Locale.US, "SELECT st FROM StateNames st ORDER BY st.stateName");
		Query query = em.createQuery( qs );
		return (List<StateLocation>)query.getResultList();    	
    }
*/    
}
