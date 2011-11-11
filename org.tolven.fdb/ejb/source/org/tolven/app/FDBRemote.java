/**
 * 
 */
package org.tolven.app;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author mohammed
 *
 */
public interface FDBRemote {

	/**
	 * This method is to fetch all drugs from the FDB
	 * @param rowId
	 * @return ArrayList<DrugVO>
	 */
	public ArrayList<DrugVO> retrieveDrugInformation (String rowId);
	/**
	 * This method is to check if the drug is valid or not
	 * @param rowId
	 * @return String
	 */
	public String checkForDrugValidity(String drugName);
	/**
	 * This method is to check for details of a drug (FDB Response)
	 * @param drugCode
	 * @param drugName
	 * @param priorMedicines
	 * @param priorAllergies 
	 * @param dob
	 * @return ArrayList<String>
	 */
	public ArrayList<String> searchDrugExhaustive(String drugCode , String drugName , String priorMedicines , String priorAllergies , String dob);
	/**
	 * This method is to retrieve all the drug information from the FDB database
	 * @param drugName
	 * @param priorMedicines 
	 * @return ArrayList<DrugVO>
	 */
	public ArrayList<DrugVO> retrieveDrugInformationByRowId (String filter ,String rowId);
	/**
	 * This method is to retrieve total drug count from the FDB
	 * @param filter
	 * @param rowId 
	 * @return int
	 */
	public int retrieveDrugCount(String filter);
	/**
	 * This method is to fetch all the Routes available with the FDB
	 * @param filter
	 * @return ArrayList<String>
	 */
	public ArrayList<String> fetchAllRoutes();
	/**
	 * This method is to fetch paginated Drug Allergies
	 * @param rowId
	 * @return ArrayList<AllergyVO>
	 */
	public ArrayList<AllergyVO> fetchDrugAllergies(String rowId);
	/**
	 * This method is to fetch filtered Allergies
	 * @param filter
	 * @param rowId
	 * @return ArrayList<AllergyVO>
	 */
	public ArrayList<AllergyVO> fetchFilteredAllergies(String filter , String rowId);
	/**
	 * This method is to fetch the total number of allergies from the fdb
	 * @param filter
	 * @return int
	 */
	public int retrieveAllergyCount(String filter);	
	/**
	 * This function retrieves all the SIG Codes from the database.
	 * @return
	 */
	public ArrayList<String> retrieveSIG();
	/**
	 * Method to retrieve drug specific information
	 * @param drugId
	 * @return
	 */
	public Map<String, String> getSelectedDrugInformation(String drugId);
	/**
	 * Method to gather drug information for the RxChange Request
	 * @param code
	 * @param medicine
	 * @return
	 */
	public ArrayList<String> getDrugInformationForRxChange(String code ,String medicine);
	/**
	 * This method is to fetch all drugs from the FDB
	 * @param rowId
	 * @return ArrayList<DrugVO>
	 */
	public ArrayList<DrugVO> retrieveOTCDrugInformation (String rowId);
	/**
	 * This method is to retrieve all the drug information from the FDB database
	 * @param drugName
	 * @param priorMedicines 
	 * @return ArrayList<DrugVO>
	 */
	public ArrayList<DrugVO> retrieveOTCDrugInformationByRowId (String filter ,String rowId);
	/**
	 * This method is to retrieve the NDC information from the FDB database
	 * @param drugName
	 * @param drugID
	 * @return Map<String, String>
	 */
	public Map<String,String> retrieveNDCInformation(String drugId, String drugName);
	/**
	 * This method is to retrieve the corresponding Drug code When we have the Drug Name in hand
	 * @param drugName
	 * @return
	 */
	public String retrieveCodeFromDrugName(String drugName);
	/**
	 * This methos is used get Medication details
	 * @param code
	 * @param medicine
	 * @author suja
	 * added on 17/4/2010
	 * @return
	 */
	public Map<String,String>  getMedicationDetails(String code , String medicine);
}
