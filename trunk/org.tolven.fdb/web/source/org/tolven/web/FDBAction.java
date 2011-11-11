/**
 * 
 */
package org.tolven.web;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.tolven.app.AllergyVO;
import org.tolven.app.DrugVO;
import org.tolven.app.FDBInterface;
import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.fdb.entity.FdbRoute;
import org.tolven.fdb.entity.FdbSig;

/**
 * @author mohammed
 * 
 */
public class FDBAction extends TolvenAction {

	/**
	 * Default Constructor
	 */
	public FDBAction(){
		super();		
	}
	/**
	 * Parameterized Constructor
	 * @param filterCondition
	 */
	public FDBAction( String filterCondition) {
		super();
		this.filterValue = filterCondition;		
	}
	private String rowId = "0";
	
	/**
	 * Variable to hold the hidden String
	 */
	private String hidden;
	
	/**
	 * FDBBean variable
	 */
	@EJB protected FDBInterface fdbBean;
	/**
	 * Variable to bind the list
	 */
	private List<FdbDispensable> drugList = null;
	/**
	 * Variable to hold the OTC Drugs
	 */
	private List<DrugVO> otcDrugList = null;
	/**
	 * Variable to bind the allergy List
	 */
	private List<AllergyVO> allergyList = null;
	/**
	 * Variable to hold the total drug count
	 */
	private int totalDrugCount;
	/**
	 * Variable to hold the OTC Drug Count
	 */
	private int otcCount;
	/**
	 * Variable to hold the total Allergy Count
	 */
	private int totalAllergyCount;
	/**
	 * Variable to hold the filter value
	 */
	private String filterValue;
	/**
	 * Variable to hold the selected Drug Name
	 */
	private String selectedDrug;
	/**
	 * Variable to hold the FdbStorageAction
	 */
	private List<SelectItem> routes;
	
	/**
	 * List of SIG from FDB
	 */
	private List<SelectItem> sigValues;
	
	public List<FdbDispensable> getDrugList() throws Exception  {
		if (null == this.drugList ) {
			this.drugList = new ArrayList<FdbDispensable>();
			this.drugList = getFdbBean().retrieveDrugInformation(null,0);
		}	
		totalDrugCount = getFdbBean().findDrugCount("").intValue();
		return drugList;
	}
	

	/**
	 * @param drugList
	 *            the drugList to set
	 */
	public void setDrugList(List<FdbDispensable> drugList) {
		this.drugList = drugList;
	}

	/**
	 * @return the totalDrugCount
	 * @throws Exception 
	 */
	public int getTotalDrugCount() throws Exception {
		if(this.filterValue == null){
			this.filterValue = "";
		}
		if(getFdbBean() != null)
			this.totalDrugCount = getFdbBean().findDrugCount(this.filterValue).intValue();		
		return totalDrugCount;
	}

	/**
	 * @param totalDrugCount
	 *            the totalDrugCount to set
	 */
	public void setTotalDrugCount(int totalDrugCount) {
		this.totalDrugCount = totalDrugCount;
	}

	/**
	 * @return the filterValue
	 */
	public String getFilterValue() {
		return filterValue;
	}

	/**
	 * @param filterValue
	 *            the filterValue to set
	 */
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	/**
	 * @return the selectedDrug
	 */
	public String getSelectedDrug() {
		return selectedDrug;
	}

	/**
	 * @param selectedDrug
	 *            the selectedDrug to set
	 */
	public void setSelectedDrug(String selectedDrug) {
		this.selectedDrug = selectedDrug;
	}

	/**
	 * @return the allergyList
	 * @throws Exception 
	 */
	public List<AllergyVO> getAllergyList() throws Exception {
		if (null == this.allergyList) {
			this.allergyList = getFdbBean().fetchDrugAllergies(0);
			this.totalAllergyCount = getFdbBean().findDrugAllergyCount("").intValue();
		}	
		return allergyList;
	}
	/**
	 * @param allergyList the allergyList to set
	 */
	public void setAllergyList(List<AllergyVO> allergyList) {
		this.allergyList = allergyList;
	}
	
	/**
	 * @return the totalAllergyCount
	 * @throws Exception 
	 */
	public int getTotalAllergyCount() throws Exception {
			if(this.filterValue == null){
				this.filterValue = "";
			}
			if(getFdbBean() != null)
				this.totalAllergyCount = getFdbBean().findDrugAllergyCount(this.filterValue).intValue();
		return totalAllergyCount;
	}
	/**
	 * @param totalAllergyCount the totalAllergyCount to set
	 */
	public void setTotalAllergyCount(int totalAllergyCount) {
		this.totalAllergyCount = totalAllergyCount;
	}
	/**
	 * @return the fdbBean
	 */
	public FDBInterface getFdbBean() {
		return fdbBean;
	}
	/**
	 * @param fdbBean the fdbBean to set
	 */
	public void setFdbBean(FDBInterface fdbBean) {
		this.fdbBean = fdbBean;
	}
	/**
	 * @return the otcCount
	 * @throws Exception 
	 */
	public int getOtcCount() throws Exception {
		if(otcCount == 0){
			otcCount = getFdbBean().findDrugCount("OTCDRUGCOUNT").intValue();
			}
		return otcCount;
	}
	/**
	 * @param otcCount the otcCount to set
	 */
	public void setOtcCount(int otcCount) {
		this.otcCount = otcCount;
	}
	/**
	 * @return the otcDrugList
	 * @throws Exception 
	 */
	public List<DrugVO> getOtcDrugList() {
		if(otcDrugList == null){
			otcDrugList = getFdbBean().retrieveOTCDrugInformation(0,null);
		}
		return otcDrugList;
	}
	/**
	 * @param otcDrugList the otcDrugList to set
	 */
	public void setOtcDrugList(List<DrugVO> otcDrugList) {
		this.otcDrugList = otcDrugList;
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	public String getHidden() {
		return hidden;
	}
	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	/**
	 * @return the routes
	 * @throws Exception 
	 */
	public List<SelectItem> getRoutes() throws Exception {
		if(null == this.routes){
			this.routes = convertRoutesToSelectItems(fdbBean.fetchAllRoutes());
		}
		return this.routes;
	}
	/**
	 * @param routes the routes to set
	 */
	public void setRoutes(List<SelectItem> routes) {
		this.routes = routes;
	}
		
	private List<SelectItem> convertRoutesToSelectItems (List<FdbRoute> routes){
		List<SelectItem> finalRoutes = new ArrayList<SelectItem>();
		for(FdbRoute route : routes){
			SelectItem r = new SelectItem(route.getRtid(),route.getDescription1());
			finalRoutes.add(r);
		}
		return finalRoutes;
	}
	private List<SelectItem> convertSigToSelectItems (List<FdbSig> sigs){
		List<SelectItem> finalRoutes = new ArrayList<SelectItem>();
		for(FdbSig sig : sigs){
			SelectItem r = new SelectItem(sig.getId().getSigcode(),sig.getId().getSigdesc());
			finalRoutes.add(r);
		}
		return finalRoutes;
	}
	/**
	 * Getter for SigValues
	 * @return
	 * @throws Exception 
	 */
	public List<SelectItem> getSigValues() throws Exception {
		if(null == this.sigValues){
			this.sigValues = convertSigToSelectItems(getFdbBean().retrieveSIG());
		}
		return sigValues;
	}
	/**
	 * Setter for SigValues
	 * @param sigValues
	 */
	public void setSigValues(List<SelectItem> sigValues) {
		this.sigValues = sigValues;
	}
		
}
