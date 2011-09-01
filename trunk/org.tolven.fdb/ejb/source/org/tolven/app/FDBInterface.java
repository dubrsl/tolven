/**
 * 
 */
package org.tolven.app;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.fdb.entity.FdbRoute;
import org.tolven.fdb.entity.FdbSig;
import org.tolven.fdb.entity.FdbVersion;
import org.tolven.fdb.entity.ex.FdbDispensableEx;

/**
 * @author mohammed
 * This Class is an interface to make a connection to the FDB DataBase for retrieving the Drugs Details. 
 */
public interface FDBInterface {
	
	//public String findDrugByName(String drugName);
	/**
	 * This method is to check for details of a drug (FDB Response)
	 * @param drugCode
	 * @param drugName
	 * @param priorMedicines
	 * @param priorAllergies 
	 * @param dob
	 * @return ArrayList<String>
	 */
	public  ArrayList<String> searchDrugExhaustive(long drugId ,String priorMedicines , String priorAllergies , String dob)throws Exception;
		/**
	 * This method is to retrieve all the drug information from the FDB database
	 * @param drugName
	 * @param priorMedicines 
	 * @return ArrayList<DrugVO>
	 */
	public List<FdbDispensable> retrieveDrugInformation (String filter ,int offset);
	
	public Long findDrugCount(String filter);
	
	public Long findDrugAllergyCount(String filter);
	
	/**
	 * This method is to retrieve total drug count from the FDB
	 * @param filter
	 * @param rowId 
	 * @return int
	 */
	// should be replaced
	//public int retrieveDrugCount(String filter)throws Exception;
	/**
	 * This method is to fetch all the Routes available with the FDB
	 * List<FdbRoute>
	 * @return ArrayList<String>
	 */
	public List<FdbRoute> fetchAllRoutes();
	/**
	 * This method is to fetch paginated Drug Allergies
	 * @param rowId
	 * @return ArrayList<AllergyVO>
	 */
	public ArrayList<AllergyVO> fetchDrugAllergies(int offset);
	/**
	 * This method is to fetch filtered Allergies
	 * @param filter
	 * @param rowId
	 * @return ArrayList<AllergyVO>
	 */
	public List<AllergyVO> fetchFilteredAllergies(String filter , int offset)throws Exception;
	
	/**
	 * This function retrieves all the SIG Codes from the database.
	 * @return
	 */
	public List<FdbSig> retrieveSIG();
	/**
	 * Method to retrieve drug specific information
	 * @param drugId
	 * @return
	 */
	public FdbDispensableEx getSelectedDrugInformation(Integer medid) throws IllegalAccessException, InvocationTargetException;
	/**
	 * Method to gather drug information for the RxChange Request
	 * @param code
	 * @param medicine
	 * @return
	 */
	@Deprecated
	public MedicationDrug getDrugInformationForRxChange(long code , String medicine)throws Exception;
	public FdbDispensable findFdbDispensable(Integer medid);
	
	public AllergyVO findDrugAllergy(String allergyName);
	
	/**
	 * This method is to retrieve all the drug information from the FDB database
	 * @param drugName
	 * @param priorMedicines 
	 * @return ArrayList<DrugVO>
	 */
	public List<DrugVO> retrieveOTCDrugInformation(int offset,String  filter);
	/**
	 * This method is to retrieve the NDC information from the FDB database
	 * @param drugName
	 * @param drugID
	 * @return Map<String, String>
	 */
	public Map<String,String> retrieveNDCInformation(long drugId)throws Exception;
	/**
	 * This method is to retrieve the corresponding Drug code When we have the Drug Name in hand
	 * @param drugName
	 * @return
	 */
	public Integer retrieveCodeFromDrugName(String drugName);
	/**
	 * This methos is used get Medication details
	 * @param code
	 * @param medicine
	 * @author suja
	 * added on 17/4/2010
	 * @return
	 */
	public Map<String,String>  getMedicationDetails(String code , String medicine)throws Exception;
	
	public FdbVersion getFdbVersion();
	/**
	 * This method is to check if the drug is valid or not
	 * @param rowId
	 * @return String
	 */
	public String checkForDrugValidity(String drugName);
}
