/**
 * @author mohammed
 */
package org.tolven.app.bean;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.tolven.app.AllergyVO;
import org.tolven.app.DrugVO;
import org.tolven.app.FDBInterface;
import org.tolven.app.FDBRemote;
import org.tolven.app.MedicationDrug;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.XMLProtectedLocal;
import org.tolven.fdb.entity.FdbAllergenpicklist;
import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.fdb.entity.FdbDoserangecheck;
import org.tolven.fdb.entity.FdbRoute;
import org.tolven.fdb.entity.FdbSig;
import org.tolven.fdb.entity.FdbVersion;
import org.tolven.fdb.entity.ex.FdbDispensableEx;

import firstdatabank.database.FDBDataManager;
import firstdatabank.database.FDBException;
import firstdatabank.database.FDBSQLException;
import firstdatabank.database.JDBCConnectionFactory;
import firstdatabank.database.JDBCConnectionFactoryImpl;
import firstdatabank.database.prtConnectionSettings;
import firstdatabank.dif.AllergenPicklistItems;
import firstdatabank.dif.Classifications;
import firstdatabank.dif.DAMScreenResult;
import firstdatabank.dif.DAMScreenResults;
import firstdatabank.dif.DDIMScreenResult;
import firstdatabank.dif.DDIMScreenResults;
import firstdatabank.dif.DFIMScreenResult;
import firstdatabank.dif.DFIMScreenResults;
import firstdatabank.dif.DTScreenDrugItem;
import firstdatabank.dif.DTScreenDrugItems;
import firstdatabank.dif.DTScreenResult;
import firstdatabank.dif.DTScreenResults;
import firstdatabank.dif.DispensableDrug;
import firstdatabank.dif.DrugSearchFilter;
import firstdatabank.dif.FDBAllergyType;
import firstdatabank.dif.FDBClassificationsType;
import firstdatabank.dif.FDBDAMAcceptanceLevelFilter;
import firstdatabank.dif.FDBDDIMSeverityFilter;
import firstdatabank.dif.FDBDFIMSignificanceFilter;
import firstdatabank.dif.FDBDispensableDrugLoadType;
import firstdatabank.dif.FDBDrugType;
import firstdatabank.dif.FDBPhoneticSearch;
import firstdatabank.dif.FDBSIDEFreqFilter;
import firstdatabank.dif.FDBSIDESeverityFilter;
import firstdatabank.dif.FDBSearchMethod;
import firstdatabank.dif.Message;
import firstdatabank.dif.Messages;
import firstdatabank.dif.Navigation;
import firstdatabank.dif.PackagedDrugs;
import firstdatabank.dif.ScreenAllergies;
import firstdatabank.dif.ScreenAllergy;
import firstdatabank.dif.ScreenDrug;
import firstdatabank.dif.ScreenDrugs;
import firstdatabank.dif.Screening;
import firstdatabank.dif.SearchFilter;



/**
 * @author mohammed
 * This class is needed to make all the interactions with the FDB. In order to create a new method to do operations
 *  on the FDB, u just need to write the declarations of that method in FDBInterface and FDBRemote and then define
 *  it in this class.
 */

@Stateless()
@Local(FDBInterface.class)
@Remote(FDBRemote.class)
public class FDBBean implements FDBInterface{
	
	/** Entity Manager Variable	 */
	@PersistenceContext private EntityManager em;
	

	/** JDBC Connection Factory Variable */
	private JDBCConnectionFactory factory;
	/** Port Connection Settings */
	private prtConnectionSettings portConn;
	/** The String to hold the rxnorm location in the local	 */
	private String dirString;
	
	/** FDB Data Manager Variable */
	private FDBDataManager fdbManager;
	/** FDBConnectionVO Variable */
	private FDBConnectionsVO fdbConVo;
	/** Variable to hold the document bean */
	@EJB DocumentLocal docBean;
	/** Variable to hold the xmlProtectedbean */
	@EJB XMLProtectedLocal xmlProtectedBean;
	/** Variable to hold the property bean */
	@EJB TolvenPropertiesLocal propertyBean;
	/** Variable to hold the xml bean */
	@EJB XMLLocal xmlBean;
	
	Logger log = Logger.getLogger(this.getClass());
	
	
	public EntityManager getEm() {
		return em;
	}
	public void setEm(EntityManager em) {
		this.em = em;
	}
	/**
	 * Constructor for the FDBBean
	 */
	public FDBBean(FDBConnectionsVO fdbConVo){
		try {
			this.fdbConVo = fdbConVo;
			fdbManager = new FDBDataManager(fdbConVo.getDriverClass(),
					fdbConVo.getdBURL(),fdbConVo.getUsername(),fdbConVo.getPassword(),false , true , getFactory(fdbConVo));
			navigate = new Navigation(fdbManager);
		} catch (Exception e) {
			log.error("Missing properties to create FDBDataManager instance."+ e.getMessage(), e);
		}
	}
	/**
	 * Constructor for the FDBBean
	 */
	public FDBBean(){
		try {
			this.fdbConVo = DataConnectionsParser.dbConnectionSettingsGenerator();
			fdbManager = new FDBDataManager(fdbConVo.getDriverClass(),
					fdbConVo.getdBURL(),fdbConVo.getUsername(),fdbConVo.getPassword(),false , true , getFactory(fdbConVo));
			navigate = new Navigation(fdbManager);
		} catch (Exception e) {
			log.error("Missing properties to create FDBDataManager instance."+ e.getMessage(), e);
		}
	}
	
	private FdbDoserangecheck getDoseInformation(long ageInDays, long medid) throws Exception{
		Query query = getEm().createQuery("Select fd from FdbDispensable fd where fd.medid =:medid");
		query.setParameter("medid", (int)medid);
		FdbDispensable dispensable = (FdbDispensable)query.getSingleResult();
		StringBuffer queryString = new StringBuffer("select fdrc from FdbDoserangecheck fdrc where ");
		queryString.append(" fdrc.id.agelowindays < :ageInDays and fdrc.id.agehighindays > :ageInDays and fdrc.id.gcnseqno=:gcnseqno");
		Query queryNew = getEm().createQuery(queryString.toString());
		queryNew.setParameter("ageInDays", (int)ageInDays);
		queryNew.setParameter("gcnseqno", dispensable.getGcnseqno());
		List<FdbDoserangecheck> doses = (List<FdbDoserangecheck>)queryNew.getResultList();
		if(doses != null && doses.size()>0)  
			return doses.get(0);
		else
			return new FdbDoserangecheck();
	}
	
	/**
	 * Navigation object for the search process
	 */
	private Navigation navigate = null; 
	
	@Override
	public  ArrayList<String> searchDrugExhaustive(long drugId ,String priorMedicines , String priorAllergies , String dob)throws Exception{
		ArrayList<String> responseArray = null;
		StringBuffer fdbResponseBuffer = new StringBuffer();
		StringBuffer drugDrugResponse = new StringBuffer();
		StringBuffer drugFoodResponse = new StringBuffer();
		StringBuffer duplicateTherapyResponse = new StringBuffer();
		StringBuffer drugAllergyResponse = new StringBuffer();
		String fdbResponse = new String("No Results found in FDB.");
		String hasDrugDrugInterraction = "false";
		String hasDrugFoodInterraction = "false";
		String hasDuplicateTherapy = "false";
		String hasDrugAllergy = "false";
		String strength = "";
		String dose = "";
		String route = "";
		long ageInDays = toAgeString(dob);
		long maxDose;
		String AHFSClassification = "No Classifications in FDB. ";
		String CTETCClassification = "No Classifications in FDB. ";
		String CTFDBClassification = "No Classifications in FDB. ";
		String CTActionGroupClassification = "No Classifications in FDB. ";
		StringBuffer prescriberInstructions = new StringBuffer();
		StringBuffer patientInstructions = new StringBuffer();
		StringBuffer commonOrders = new StringBuffer();
		StringBuffer poem = new StringBuffer();
		String doseForm="";
		try{
			FdbDispensable dispensable = findFdbDispensable(new Integer((int)drugId));
			FdbDoserangecheck doserangecheck = getDoseInformation(ageInDays,drugId);
			if(null != doserangecheck){
				BigDecimal doseLow = doserangecheck.getDoselow();
				String doseLowUnit = doserangecheck.getDoselowunits();
				BigDecimal doseHigh = doserangecheck.getDosehigh();
				String doseHighUnit = doserangecheck.getDosehighunits();
				 if(null!= doseLow  && null!= doseLowUnit && !doseLowUnit.equals("")
			        				&& null!= doseHigh && null!= doseHighUnit && !doseHighUnit.equals("")){
						if(!(doseHigh.intValue() == 0)){
								dose = "("+doseLow+doseLowUnit+" - "+doseHigh+doseHighUnit+")";
								maxDose = doseHigh.intValue();
						}else{
							dose="No dose suggestions from FDB";
							maxDose = 0;
						}
		    		}else{
		    			dose="No dose suggestions from FDB";
		    			if(null !=doseHigh )
		    				maxDose = doseHigh.intValue();
		    			else
		    				maxDose = 0;
		    		}
		        }else{
		        	dose= "No dose suggestions from FDB";
		        	maxDose = 0;
		        }
	    	DispensableDrug currentDrug = new DispensableDrug(fdbManager);
	    	currentDrug.load(drugId,FDBDispensableDrugLoadType.fdbDDLTMedID , dispensable.getDescdisplay(), "", "");
	        	
    		if(null != currentDrug.getDoseForm())
    			doseForm = currentDrug.getDoseForm();
    		
			if(null != currentDrug.getStrength() && null != currentDrug.getStrengthUnit()
					&& !currentDrug.getStrength().equals("") && !currentDrug.getStrengthUnit().equals(""))
						strength = currentDrug.getStrength()+" "+currentDrug.getStrengthUnit();
			
			if(null != currentDrug.getRoute() && !currentDrug.getRoute().equals(""))
				route = currentDrug.getRoute();
			
			ScreenDrug scDrug = new ScreenDrug(fdbManager);
			scDrug.load(String.valueOf(drugId),FDBDrugType.fdbDTDispensableDrug );
			scDrug.setDescription(dispensable.getDescdisplay());
			ScreenDrugs scDrugs = null;
			ArrayList<String> drugDrug = new ArrayList<String>();
			if(!priorMedicines.equals("") && null != priorMedicines){
			 scDrugs = generateScreenDrugList(priorMedicines);
			 scDrugs.addItem(scDrug);
			 
			// Drug / Drug Interaction
				drugDrug = doScreenDDIM(scDrugs, new Screening(fdbManager));
			}
			
			// Drug / Food Interaction
			ArrayList<String> drugFood = doScreenDFIM(scDrug , new Screening(fdbManager));
			
			// Duplicate Therapy Checking
			ArrayList<String> duplicateTherapy = doScreenDT(scDrug , new Screening(fdbManager));
			
			//Drug Allergy Interaction
			ArrayList<String> drugAllergy = new ArrayList<String>();
			if(null != priorAllergies && priorAllergies.trim().length() > 0){
				drugAllergy = doScreenDAI(scDrug ,new Screening(fdbManager),  priorAllergies);
			}
			
	        
	        if(currentDrug.getName().equals("Replaced/Retired Drug"))
	        	fdbResponseBuffer = fdbResponseBuffer.append("CURRENT STATUS :" +currentDrug.getName());
	        
	        if(null != currentDrug.getName() && !currentDrug.getName().equals("Replaced/Retired Drug"))
	        	fdbResponseBuffer = fdbResponseBuffer.append("DRUG NAME : ").append(currentDrug.getName()).append(" | "); 
	        
	        if(null != currentDrug.getDoseForm()  )
	        	fdbResponseBuffer = fdbResponseBuffer.append("DOSE FORM : ").append(currentDrug.getDoseForm()).append(" | "); 
	        
	        
	        if(null != currentDrug.getConfusionGroupDescription() && !currentDrug.getConfusionGroupDescription().equals("") ){
	        	fdbResponseBuffer = fdbResponseBuffer.append("CONFUSION DESCRIPTION : ").append(currentDrug.getConfusionGroupDescription()).append(" | "); 
	        }
	        
	        if(0L != currentDrug.getConfusionGroupID()){
	        	fdbResponseBuffer = fdbResponseBuffer.append("CONFUSION GROUP ID : ").append(currentDrug.getConfusionGroupID()).append(" | "); 
	        }
       		String isControlled="false";
       		if(currentDrug.getFederalDEAClassCode().equals("4") || currentDrug.getFederalDEAClassCode().equals("3")
	    		   || currentDrug.getFederalDEAClassCode().equals("2") || currentDrug.getFederalDEAClassCode().equals("5")){
	    		isControlled = "true";
	    	}
    	    if(currentDrug.getStatusCode().equals("0")){
		    	if(null != currentDrug.getClassifications(FDBClassificationsType.fdbCTActionGroup) &&
		    			currentDrug.getClassifications(FDBClassificationsType.fdbCTActionGroup).count() > 0){
		    		fdbResponseBuffer.append("Action Group CLASSIFICATIONS FOR THE DRUG : ");
		    		Classifications classifications =  currentDrug.getClassifications(FDBClassificationsType.fdbCTActionGroup);
		    		CTActionGroupClassification ="";
		    		for(int i=0; i< classifications.count(); i++){
		    			CTActionGroupClassification = CTActionGroupClassification +classifications.item(i).getDescription() + ",";
		    		}
		    		if(CTActionGroupClassification.endsWith(",")){
		    			CTActionGroupClassification = CTActionGroupClassification.substring(0, CTActionGroupClassification.length() - 1)+". ";
		    		}
		    		fdbResponseBuffer.append(CTActionGroupClassification).append("|");		    		
		    	}
		    	if(null != currentDrug.getClassifications(FDBClassificationsType.fdbCTETC) &&
		    			currentDrug.getClassifications(FDBClassificationsType.fdbCTETC).count() > 0){
		    		fdbResponseBuffer.append("CTETC CLASSIFICATIONS FOR THE DRUG : ");
		    		CTETCClassification = "";
		    		Classifications classifications =  currentDrug.getClassifications(FDBClassificationsType.fdbCTETC);
		    		for(int i=0; i< classifications.count(); i++){
		    			CTETCClassification = CTETCClassification +classifications.item(i).getDescription() + ",";
		    		}
		    		if(CTFDBClassification.endsWith(",")){
		    			CTETCClassification = CTETCClassification.substring(0, CTETCClassification.length() - 1)+". ";
		    		}
		    		fdbResponseBuffer.append(CTETCClassification).append("|");
		    	}
		    	if(null != currentDrug.getClassifications(FDBClassificationsType.fdbCTFDB) &&
		    			currentDrug.getClassifications(FDBClassificationsType.fdbCTFDB).count() > 0){
		    		fdbResponseBuffer.append("CTFDB CLASSIFICATIONS FOR THE DRUG : ");
		    		CTFDBClassification = "";
		    		Classifications classifications =  currentDrug.getClassifications(FDBClassificationsType.fdbCTFDB);
		    		for(int i=0; i< classifications.count(); i++){
		    			CTFDBClassification = CTFDBClassification +classifications.item(i).getDescription() + ",";
		    		}
		    		if(CTFDBClassification.endsWith(",")){
		    			CTFDBClassification = CTFDBClassification.substring(0, CTFDBClassification.length() - 1)+". ";
		    		}
		    		fdbResponseBuffer.append(CTFDBClassification).append("|");
		    	}
		    	if(null != currentDrug.getClassifications(FDBClassificationsType.fdbCTAHFS) &&
		    			currentDrug.getClassifications(FDBClassificationsType.fdbCTAHFS).count() > 0){
		    		fdbResponseBuffer.append("AHFS CLASSIFICATIONS FOR THE DRUG : ");
		    		AHFSClassification = "";
		    		Classifications classifications =  currentDrug.getClassifications(FDBClassificationsType.fdbCTAHFS);
		    		for(int i=0; i< classifications.count(); i++){
		    			AHFSClassification = AHFSClassification + classifications.item(i).getDescription() + ",";
		    		}
		    		if(AHFSClassification.endsWith(",")){
		    			AHFSClassification = AHFSClassification.substring(0, AHFSClassification.length() - 1)+". ";
		    		}
		    		fdbResponseBuffer.append(AHFSClassification).append("|");
		    	}
		    	if(null != currentDrug.getFDBClassifications() && currentDrug.getFDBClassifications().count() > 0 )
		    		fdbResponseBuffer.append("FDB CLASSIFICATION ID : ").append(currentDrug.getFDBClassifications().item(0).getID()).append(" | ");
		    	
		    	if(null != currentDrug.getDoseRoutes() && currentDrug.getDoseRoutes().count() > 0 
						&&  null != currentDrug.getDoseRoutes().item(0).getDescription())
				fdbResponseBuffer.append("FDB DOSE ROUTE DESCRIPTION : ").append(currentDrug.getDoseRoutes().item(0).getDescription()).append(" | ");
		
		    	if(null != currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2))
						&& currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).count() > 0
						&& null != currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).item(0).getConditionDescription())
					fdbResponseBuffer = fdbResponseBuffer.append("SIDE EFFECTS : ").append(currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).item(0).getConditionDescription()).append(" | ");
		    	
		    	
		    	 /* Counseling Messages for the Prescriber to prescriber a drug */ 
		    	if(null !=  currentDrug.getCounselingMessages("FDB-PE") && currentDrug.getCounselingMessages("FDB-PE").count() > 0){
					for(int i=0; i< currentDrug.getCounselingMessages("FDB-PE").count(); i++){
						if(null != currentDrug.getCounselingMessages("FDB-PE").item(i).getProfessionalText1()){
							prescriberInstructions.append(currentDrug.getCounselingMessages("FDB-PE").
									item(i).getProfessionalText1()).append(" ");
						}
						if(null != currentDrug.getCounselingMessages("FDB-PE").item(i).getProfessionalText2()){
							prescriberInstructions.append(currentDrug.getCounselingMessages("FDB-PE").item(i).getProfessionalText2()).append(". ");		  
						}
						if(null != currentDrug.getCounselingMessages("FDB-PE").item(i).getPatientText1()){
							patientInstructions.append(currentDrug.getCounselingMessages("FDB-PE").item(i).getPatientText1()).append(" ");
						}
						if(null != currentDrug.getCounselingMessages("FDB-PE").item(i).getPatientText2()){
							patientInstructions.append(currentDrug.getCounselingMessages("FDB-PE").item(i).getPatientText2()).append(". ");
						}
					}	
		    	}
		    	try{
        		if(null != currentDrug.getDefaultOrder() && null != currentDrug.getDefaultOrder().getFullOrderText()){
					commonOrders.append(currentDrug.getDefaultOrder().getFullOrderText()).append(". ");
				}
		    	}catch (Exception e) {
					//ignore this error
				}
			}					
			if(drugDrug.size() > 0){
				hasDrugDrugInterraction = "true";
				for(int i = 0; i< drugDrug.size(); i++){
					fdbResponseBuffer.append(drugDrug.get(i));
					fdbResponseBuffer.append("|");
					drugDrugResponse.append(drugDrug.get(i));
					drugDrugResponse.append("|");
				}
			}
			if(drugFood.size() > 0){
				hasDrugFoodInterraction = "true";
				for(int i = 0; i< drugFood.size(); i++){
					fdbResponseBuffer.append(drugFood.get(i));
					fdbResponseBuffer.append("|");
					drugFoodResponse.append(drugFood.get(i));
					drugFoodResponse.append("|");
				}
			}
			if(duplicateTherapy.size() > 0){
				hasDuplicateTherapy = "true";
				for(int i = 0; i< duplicateTherapy.size(); i++){
					fdbResponseBuffer.append(duplicateTherapy.get(i));
					fdbResponseBuffer.append("|");
					duplicateTherapyResponse.append(duplicateTherapy.get(i));
					duplicateTherapyResponse.append("|");
				}
			}
					
			if(drugAllergy.size() > 0){
				hasDrugAllergy = "true";
				for(int i = 0; i< drugAllergy.size(); i++){
					fdbResponseBuffer.append(drugAllergy.get(i));
					fdbResponseBuffer.append("|");
					drugAllergyResponse.append(drugAllergy.get(i));
					drugAllergyResponse.append("|");
				}
			}       
	     
			fdbResponse = fdbResponseBuffer.toString();
			responseArray = new ArrayList<String>();
			responseArray.add(0, fdbResponse);
			responseArray.add(1,hasDrugDrugInterraction);
			responseArray.add(2,hasDrugFoodInterraction);
			responseArray.add(3,hasDuplicateTherapy);
			responseArray.add(4, drugDrugResponse.toString());
			responseArray.add(5, drugFoodResponse.toString());
			responseArray.add(6, duplicateTherapyResponse.toString());
			responseArray.add(7, hasDrugAllergy);
			responseArray.add(8, drugAllergyResponse.toString());
			responseArray.add(9, strength);
			responseArray.add(10, dose);
			responseArray.add(11, route);
			responseArray.add(12, String.valueOf(maxDose));
			responseArray.add(13, AHFSClassification);
			responseArray.add(14, prescriberInstructions.toString());
			responseArray.add(15, patientInstructions.toString());
			responseArray.add(16, commonOrders.toString());
			responseArray.add(17, poem.toString());
			responseArray.add(18, CTETCClassification);
			responseArray.add(19, CTFDBClassification);
			responseArray.add(20, CTActionGroupClassification);
			responseArray.add(21, isControlled);
			responseArray.add(22, doseForm);
			if (currentDrug != null && currentDrug.getStrengthUnit()!=null)
				//responseArray.add(23, getStrengthUnit(Long.parseLong(currentDrug.getStrengthUnit())).getFdbUnit());
				responseArray.add(23, currentDrug.getStrengthUnit());
			return responseArray;
					
		} catch (Exception e) {
			throw e;
		}		
	}
	
	
	/**mdEvent
	 * Method to generate the List of ScreenDrugs From Prior Medication
	 * @param priorMedicines
	 * @return
	 */
	
	private ScreenDrugs generateScreenDrugList(String priorMedicines) throws Exception{
		ScreenDrugs drugsList = new ScreenDrugs(fdbManager);
		Navigation navigate = new Navigation(fdbManager);
		if(null != priorMedicines && priorMedicines.trim().length() > 0){
			String[] drugs = null;
			if(priorMedicines.contains(",")){
				drugs = priorMedicines.split(",");
			}else{
				drugs = new String[1];
				drugs[0] = priorMedicines;
			}
			SearchFilter sf = new SearchFilter();
			FDBSearchMethod searchMethod = FDBSearchMethod.fdbSMExhaustive;
			FDBPhoneticSearch searchType = FDBPhoneticSearch.fdbPSAfterEmpty ;
			sf.setPhoneticSearch(searchType);
			sf.setSearchMethod(searchMethod);
			DispensableDrug currentDrug = null;
			try {
				for(int i=0; i<drugs.length; i++){
					if(navigate.dispensableDrugSearch(drugs[i], sf, new DrugSearchFilter()).count() > 0){
						currentDrug = navigate.dispensableDrugSearch(drugs[i], sf, new DrugSearchFilter()).item(0);
						ScreenDrug scDrug = new ScreenDrug(fdbManager);
						scDrug.load(Long.toString(currentDrug.getID()),
								FDBDrugType.fdbDTDispensableDrug );
						scDrug.setDescription(currentDrug.getDescription());
						drugsList.addItem(scDrug);
					}	
				}
			} catch (FDBException fdbException) {
				throw fdbException;
			}
		}	
		return drugsList;
	}
	/**
	 * Method to Screen the selected Drug for Drug/Drug Screening
	 * @param scDrug
	 * @param screening
	 * @throws FDBSQLException 
	 */
	private ArrayList<String> doScreenDDIM(ScreenDrugs drugs, Screening objScreen) throws FDBSQLException {
		 Messages objMessages =null;
        DDIMScreenResults objResultsDDIM = null;
        long lngCnt;
        FDBDDIMSeverityFilter sev = FDBDDIMSeverityFilter.fdbDDIMSFModerate ;
     	 objResultsDDIM = objScreen.DDIMScreen(drugs,false,sev, false, false);   
		
        // Retrieve any of the messages generated by this screening
        objMessages=objResultsDDIM.getMessages ();
        String warning = getMessages(objMessages);

        // See if the screening produced any results
        lngCnt = objResultsDDIM.count();
        ArrayList<String> messagesList = null;
        if (!(lngCnt >= 0)){
        	messagesList = new ArrayList<String>();
        	messagesList.add("No Drug - Drug Interractions Found.");
        } else {
            DDIMScreenResult objDDIMRes =null;
            messagesList = new ArrayList<String>();
           // messagesList.add("The following Drug - Drug Interractions were Found.");
            for (int intInd = 0; intInd <= (int)lngCnt - 1; intInd++){
            	objDDIMRes = objResultsDDIM.item(intInd);
            	 // Add an entry to the screen results listview control.
              log.debug("DDIM : "+objDDIMRes.getDrug1Description() + " and " + objDDIMRes.getDrug2Description());
                // Add a description of the result to the screen results text box.
              log.debug("    DDIM Result #" + Integer.toString(intInd + 1));
              log.debug("    " + objDDIMRes.getDrug1Description() + " and " + objDDIMRes.getDrug2Description());
              log.debug("    Interaction Description: " + objDDIMRes.getInteractionDescription());
              log.debug("    Message: " + objDDIMRes.getScreenMessage());
              messagesList.add("DDIM : "+objDDIMRes.getDrug1Description() + " and " + objDDIMRes.getDrug2Description());
              messagesList.add("		DDIM Result #" + Integer.toString(intInd + 1));
              messagesList.add("		" + objDDIMRes.getDrug1Description() + " and " + objDDIMRes.getDrug2Description());
              messagesList.add("		Interaction Description: " + objDDIMRes.getInteractionDescription());
              messagesList.add("		Message: " + objDDIMRes.getScreenMessage());             
            }
        }
        return messagesList;
	}
	/**
	 * Method to Screen the selected Drug for Drug/Food Screening
	 * @param drugID
	  * @param screening 
	 * @throws FDBSQLException 
	 */
	private ArrayList<String> doScreenDFIM(ScreenDrug scrnDrug, Screening objScreen) throws FDBSQLException {
	    Messages objMessages =null;
        DFIMScreenResults objResultsDFIM = null;
        long lngCnt;
        ScreenDrugs drugs = new ScreenDrugs(fdbManager);
        drugs.addItem(scrnDrug);
        FDBDFIMSignificanceFilter sig = FDBDFIMSignificanceFilter.fdbDFIMSFMore ;
        objResultsDFIM = objScreen.DFIMScreen(drugs,  false, sig, false);			
       
        // Retrieve any of the messages generated by this screening
        objMessages=objResultsDFIM.getMessages ();
        String warning = getMessages(objMessages);

        // See if the screening produced any results
        lngCnt = objResultsDFIM.count();
        ArrayList<String> messagesList = null;
        if (!(lngCnt >= 0)){
        	messagesList = new ArrayList<String>();
        	 if(!warning.equals(""))
            	  messagesList.add("Warning Message :"+warning);
              
        	messagesList.add("No Drug - Food Interractions Found.");
        } else {
            DFIMScreenResult objDFIMRes =null;
            messagesList = new ArrayList<String>();
            if(!warning.equals(""))
            	  messagesList.add("Warning Message :"+warning);
           // messagesList.add("The following Drug - Food Interractions were Found.");
            for (int intInd = 0; intInd <= (int)lngCnt - 1; intInd++){
                objDFIMRes = objResultsDFIM.item(intInd);
                // Add an entry to the screen results listview control.
                log.debug("DFIM : " +objDFIMRes.getDrugDescription());
                // Add a description of the result to the screen results text box.
                log.debug("    DFIM Result #" + Integer.toString(intInd + 1));
                log.debug("    Drug: " + objDFIMRes.getDrugDescription());
                log.debug("    Advice: " + objDFIMRes.getAdviceMessage());
                log.debug("    Clinical result: " + objDFIMRes.getClinicalResult());
                log.debug("    Message: " + objDFIMRes.getScreenMessage());
                messagesList.add("DFIM : " +objDFIMRes.getDrugDescription());
                messagesList.add("		DFIM Result #" + Integer.toString(intInd + 1));
                messagesList.add("		Drug: " + objDFIMRes.getDrugDescription());
                messagesList.add("		Advice: " + objDFIMRes.getAdviceMessage());
                messagesList.add("		Clinical result: " + objDFIMRes.getClinicalResult());
                messagesList.add("		Message: " + objDFIMRes.getScreenMessage());
            }
        }
        return messagesList;
	}
	/**
	 * Method to screen Selected Drug on Duplicate Therapy
	 * @param scDrug
	 * @param screening
	 * @throws FDBSQLException 
	 */
	private ArrayList<String> doScreenDT(ScreenDrug scDrug, Screening objScreen) throws FDBSQLException {
		Messages objMessages =null;
        DTScreenResults objResultsDT = null;
        long lngCnt;
        ScreenDrugs drugs = new ScreenDrugs(fdbManager);
        drugs.addItem(scDrug);
        objResultsDT =objScreen.DTScreen(drugs, false, true );
		   
        // Retrieve any of the messages generated by this screening
        objMessages=objResultsDT.getMessages ();
        String warning = getMessages(objMessages);

        // See if the screening produced any results
        lngCnt = objResultsDT.count();
        ArrayList<String> messagesList = null;
        if (!(lngCnt >= 0)){
        	 messagesList = new ArrayList<String>();
        	 messagesList.add("No Duplicate Therapy Found.");
        } else {
        	 DTScreenResult objDTRes = null;
        	 messagesList = new ArrayList<String>();
            for (int intInd = 0; intInd <= (int)lngCnt - 1; intInd++){
            	objDTRes = objResultsDT.item(intInd);
            	// Add an entry to the screen results listview control
                log.debug("DT : "+ objDTRes.getClassDescription());
                // Add a description of the result to the screen results text box
                log.debug("    DT Result #" + Integer.toString(intInd + 1));
                log.debug("    Class: " + objDTRes.getClassDescription());
                
                messagesList.add("DT : "+ objDTRes.getClassDescription());
                messagesList.add("    DT Result #" + Integer.toString(intInd + 1));
                messagesList.add("    Class: " + objDTRes.getClassDescription());
            
                
                // Create a list of the duplicated drugs reported by this result and
                // add it to the screen results text box
                DTScreenDrugItems objDrugItems = null;
                DTScreenDrugItem objDrugItem = null;
                long lngCnt2;
                int intInd2;
                String strDrugs = new String("");

                // Get the duplicated drugs collection object
                objDrugItems = objDTRes.getDrugItems();
                lngCnt2 = objDrugItems.count();
                for (intInd2 = 0; intInd2 < lngCnt2 - 1; intInd2++)
                {
                    objDrugItem = objDrugItems.item(intInd2);
                    if (intInd2 > 0)
                    {
                        strDrugs = strDrugs + ", ";
                    }
                    strDrugs = strDrugs + objDrugItem.getDrugDescription();
                }
                log.debug("    Drugs: " + strDrugs);
                log.debug("    Message: " + objDTRes.getScreenMessage());
                messagesList.add("    Drugs: " + strDrugs);
                messagesList.add("    Message: " + objDTRes.getScreenMessage());
              
            }
        }
        return messagesList;
	}
	/**
	 * Method to Generate the Messages
	 * @param objMsgs
	 */
	private String getMessages(Messages objMsgs) {
		String strM = new String("");
        long lngCnt;
        lngCnt = objMsgs.count();
        if (lngCnt < 1){
           log.debug("No Messages !!!!");
        } else{
            	Message objMsg;
            for (int intInd = 0; intInd <= (int)lngCnt - 1; intInd++){
                objMsg = objMsgs.item(intInd);
                strM = new String("Message #" + Integer.toString(intInd)+ "|" + " Drug: " + objMsg.getDrugDescription() + "|" + "  Text: " + objMsg.getMessageText());
               log.debug(strM);
            }    
        }
        return strM;
    }

	/**
	 * Retrieve All Drugs Allergy Information
	 * @throws FDBSQLException 
	 *//*
	private Ingredients  retrieveInformationOnIngredients(String drugID) throws FDBSQLException{
		SearchFilter sf = new SearchFilter();
		FDBSearchMethod searchMethod = FDBSearchMethod.fdbSMExhaustive;
		sf.setSearchMethod(searchMethod);
		Ingredients ing = navigate.ingredientSearch(drugID, sf);
		for (int i=0; i < ing.count() - 1; i++){
            Ingredient ingredient = ing.item(i);
            log.debug("The Ingredients of the selected Drugs are:" +ingredient.getDescription());
        }
		return ing;
	}
*/
	/**
	 * @return the dirString
	 */
	public String getDirString() {
		return dirString;
	}
	/**
	 * @param dirString the dirString to set
	 */
	public void setDirString(String dirString) {
		this.dirString = dirString;
	}
	
	/**
	 * @param fdbConVo2 
	 * @return the factory
	 */
	public JDBCConnectionFactory getFactory(FDBConnectionsVO fdbConVo) {
		if(null == this.factory){
		this.factory = (JDBCConnectionFactory) (new JDBCConnectionFactoryImpl(getPortConn(fdbConVo)));
		}
		return factory;
	}
	/**
	 * @param factory the factory to set
	 */
	public void setFactory(JDBCConnectionFactory factory) {
		this.factory = factory;
	}
	/**
	 * @param fdbConVo 
	 * @return the portConn
	 */
	public prtConnectionSettings getPortConn(FDBConnectionsVO fdbConVo) {
		if(portConn == null){
			portConn =new prtConnectionSettings(fdbConVo.getDriverClass(),fdbConVo.getdBURL(), 
						fdbConVo.getUsername(), fdbConVo.getPassword(), false, true, fdbConVo.getPoolSize(),
						fdbConVo.getLoadLimit(), null);
		}
		return portConn;
	}
	/**
	 * @param portConn the portConn to set
	 */
	public void setPortConn(prtConnectionSettings portConn) {
		this.portConn = portConn;
	}
	
	/**
	 * Method to fetch all the drug alllergies from the fdb
	 * @param rowId
	 * @return ArrayList<AllergyVO>
	 * @throws Exception 
	 */
	public ArrayList<AllergyVO> fetchDrugAllergies(int offset){
		ArrayList<AllergyVO> arrayList= null;
		Query query = getEm().createQuery("SELECT allgpcick FROM FdbAllergenpicklist allgpcick");
		query.setFirstResult(offset+14);
		query.setMaxResults(14);
		List<FdbAllergenpicklist> allergenpicklists = query.getResultList();
		arrayList = new ArrayList<AllergyVO>();
        for(FdbAllergenpicklist allergenpick:allergenpicklists){
        	AllergyVO allerVO = new AllergyVO();
        	allerVO.setAllergen(allergenpick.getDescription1());
        	if(allergenpick.getId().getConcepttype() == 1){
        		allerVO.setType("Allergen Group");
        	}else if(allergenpick.getId().getConcepttype() == 2){
        		allerVO.setType("Drug Name");
        	}else if(allergenpick.getId().getConcepttype() == 6){
        		allerVO.setType("Ingredient");
        	}
        	arrayList.add(allerVO);
        }
        return arrayList;
	}
	
	/**
	 * Method to fetch all the routes from the FDB
	 * @return List<FdbRoute> 
	 */
	public List<FdbRoute> fetchAllRoutes(){
		Query query = getEm().createQuery("SELECT route FROM FdbRoute route ORDER BY route.description1");
		return (List<FdbRoute>)query.getResultList();
	}
	/**
	 * This method is to fetch filtered Allergies
	 * @param filter
	 * @param rowId
	 * @return ArrayList<AllergyVO>
	 * @throws Exception 
	 */
	public List<AllergyVO> fetchFilteredAllergies(String filter , int offset) throws Exception{
		List<AllergyVO> list = null;
		SearchFilter sf = new SearchFilter();
		FDBSearchMethod searchMethod = FDBSearchMethod.fdbSMSimple;
		FDBPhoneticSearch searchType = FDBPhoneticSearch.fdbPSNoPhonetic;
		sf.setPhoneticSearch(searchType);
		sf.setSearchMethod(searchMethod);
		AllergenPicklistItems allergens = null;
		list = new ArrayList<AllergyVO>();
		if(null != filter && !filter.equals("")){
			allergens = navigate.allergenPicklistSearch(filter, sf, false);
			for(int i=offset; i< allergens.count(); i++){
				AllergyVO allergVO = new AllergyVO();
				allergVO.setAllergen(allergens.item(i).getDescription());
				if(allergens.item(i).getIDType().equals(FDBAllergyType.fdbATAllergenGroup))
					allergVO.setType("Allergen Group");
				else if(allergens.item(i).getIDType().equals(FDBAllergyType.fdbATDrugName))
					allergVO.setType("Drug Name");
				else if(allergens.item(i).getIDType().equals(FDBAllergyType.fdbATIngredient))	
					allergVO.setType("Ingredient");
				list.add(allergVO);
				if (list.size() == 14) {
					break;
				}
			}
			//use the offset to filter out the allergies
			return list;	
		}else{
			return fetchDrugAllergies(offset);				
		} 
        
	}
	
	
	/**
	 * Method to capture the Drug Allergy Interactions
	 * @param scDrug
	 * @param screening
	 * @param priorAllergies
	 * @return
	 * @throws Exception 
	 */
	private ArrayList<String> doScreenDAI(ScreenDrug scDrug,Screening screening, String priorAllergies) throws Exception {
		Messages objMessages =null;
        DAMScreenResults objResultsDAM = null;
        long lngCnt;
        FDBDAMAcceptanceLevelFilter sev = FDBDAMAcceptanceLevelFilter.fdbDAMALFIngredient ;
        ScreenAllergies allergens = createAllergens(priorAllergies);
        ScreenDrugs scDrugs = new ScreenDrugs(fdbManager);
        scDrugs.addItem(scDrug);
        ArrayList<String> messagesList = null;
        try {
        	objResultsDAM = screening.DAMScreen(scDrugs, allergens, false , true);
		} catch (FDBSQLException e) {
			messagesList = new ArrayList<String>();
        	messagesList.add("No Drug - Allergy Interractions Found.");
		}
	       
        // Retrieve any of the messages generated by this screening
        objMessages=objResultsDAM.getMessages ();
        String warning = getMessages(objMessages);

        // See if the screening produced any results
        lngCnt = objResultsDAM.count();
        if (!(lngCnt >= 0)){
        	messagesList = new ArrayList<String>();
        	messagesList.add("No Drug - Allergy Interractions Found.");
        } else {
        	DAMScreenResult objDAMMRes =null;
            messagesList = new ArrayList<String>();
           // messagesList.add("The following Drug - Drug Interractions were Found.");
            for (int intInd = 0; intInd <= (int)lngCnt - 1; intInd++){
            	objDAMMRes = objResultsDAM.item(intInd);
            	 // Add an entry to the screen results listview control.
              log.debug("DAM : "+objDAMMRes.getDrugDescription());
                // Add a description of the result to the screen results text box.
              log.debug("    DAM Result #" + Integer.toString(intInd + 1));
              log.debug("    Allergen Name : " + objDAMMRes.getAllergenDescription());
              log.debug("    Reaction : " + objDAMMRes.getScreenMessage());
              log.debug("    Match Description : " + objDAMMRes.getMatchDescription());
              messagesList.add("DAM : "+objDAMMRes.getDrugDescription());
              messagesList.add("    DAM Result #" + Integer.toString(intInd + 1));
              messagesList.add("    Allergen Name : " + objDAMMRes.getAllergenDescription());
              messagesList.add("    Reaction : " + objDAMMRes.getScreenMessage());
              messagesList.add("    Match Description : " + objDAMMRes.getMatchDescription());
            }
        }
        return messagesList;
	}
	
	/**
	 * Method to create the ScreenAllergies out of String
	 * @param priorAllergies
	 * @return
	 * @throws Exception 
	 */
	private ScreenAllergies createAllergens(String priorAllergies) throws Exception {
		String[] allergies = null;
		if(priorAllergies.contains(",")){
			allergies = priorAllergies.split(",");
		}
		SearchFilter sf = new SearchFilter();
		FDBSearchMethod searchMethod = FDBSearchMethod.fdbSMSimple;
		FDBPhoneticSearch searchType = FDBPhoneticSearch.fdbPSOnly ;
		sf.setPhoneticSearch(searchType);
		sf.setSearchMethod(searchMethod);
		AllergenPicklistItems allergenItems = null;
		ScreenAllergies allergens = new ScreenAllergies(fdbManager);
		ScreenAllergy allergy = new ScreenAllergy(fdbManager);
		if(null !=allergies){
			for(int i=0; i<allergies.length; i++){
				allergenItems =navigate.allergenPicklistSearch(allergies[i], sf, false);
				for(int j=0; j< allergenItems.count(); j++){
					allergy.load(allergenItems.item(j).getID(), allergenItems.item(j).getIDType());
					allergy.setDescription(allergenItems.item(j).getDescription());
					allergens.addItem(allergy);
				}				
			}			
		}else{
			allergenItems =navigate.allergenPicklistSearch(priorAllergies, sf, false);
			for(int j=0; j< allergenItems.count(); j++){
				allergy.load(allergenItems.item(j).getID(), allergenItems.item(j).getIDType());
				allergy.setDescription(allergenItems.item(j).getDescription());
				allergens.addItem(allergy);
			}
		}
		return allergens;
	}
	
	/**
	 * This function retrieves all the SIG Codes from the database.
	 * @return sigValues
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<FdbSig> retrieveSIG(){
		Query query = getEm().createQuery("SELECT sig FROM FdbSig sig");
		return (List<FdbSig>)query.getResultList();	 	
		
	}
	
	
	/**
	 * Method to get the Drug Specific Information for CAREPLAN
	 * @param medid
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	public FdbDispensableEx getSelectedDrugInformation(Integer medid) throws IllegalAccessException, InvocationTargetException{
		FdbDispensable dispensable =  getEm().find(FdbDispensable.class,medid);
		if(dispensable == null)
			return null;
		FdbDispensableEx dispensableEx = new FdbDispensableEx(dispensable);
		//load dose form
		dispensableEx.loadDoseform(getEm());
		//load route
		dispensableEx.loadRoute(getEm());
		return dispensableEx;
	}
	
	/**
	 * Method to convert the DOB to days
	 * @param dob
	 * @param now
	 * @param locale
	 * @return
	 */
	public long toAgeString( String dob) {
		Date now = new Date();
		Date dateOfBirth = new Date(Integer.parseInt(dob.substring(0,4)), Integer.parseInt(dob.substring(5,7)),Integer.parseInt(dob.substring(8,10)));
        // Get age in years
        int years = (now.getYear()+1900) - dateOfBirth.getYear();
        long days = now.getDate() - dateOfBirth.getDate();
        if (days < 0)
          {
            years--;
            days = days + 365;
          }
        if (years == 0 && days < 30)
         {
            return  days ;
         }
        return  (years * 365) + days ;
    }

	/**
	 * Method to gather drug information for the RxChange Request
	 * @param medicine
	 * @return
	 * @throws Exception 
	 */
/*	public ArrayList<String>  getDrugInformationForRxChange(String code , String medicine) throws Exception{
		ArrayList<String> drugInfo = new ArrayList<String>();
		String strength = "";
		String route = "";
		String drugFoodBoolean = "false" ;
		String duplicateTherapyBoolean = "false";
		StringBuffer drugFoodInteraction = new StringBuffer();
		StringBuffer duplicateTherapyInteraction = new StringBuffer();
		DispensableDrug currentDrug = new DispensableDrug(fdbManager);
		StringBuffer fdbResponseBuffer = new StringBuffer("No Details found in FDB.");
		String classification = "No Classifications in FDB. ";
    	try {
			currentDrug.load(Long.parseLong(code),FDBDispensableDrugLoadType.fdbDDLTMedID , medicine, "", "");
			
			if(null != currentDrug.getStrength() && null != currentDrug.getStrengthUnit()
					&& !currentDrug.getStrength().equals("") && !currentDrug.getStrengthUnit().equals(""))
						strength = currentDrug.getStrength()+" "+currentDrug.getStrengthUnit();
			
			if(null != currentDrug.getRoute() && !currentDrug.getRoute().equals(""))
				route = currentDrug.getRoute();
			
			ScreenDrug scDrug = new ScreenDrug(fdbManager);
			scDrug.load(code,FDBDrugType.fdbDTDispensableDrug );
			scDrug.setDescription(medicine);
			
			ArrayList<String> drugFood = doScreenDFIM(scDrug , new Screening(fdbManager));
			if(drugFood.size() > 0)
				drugFoodBoolean = "true";
				
			for(String foodInter : drugFood){
				drugFoodInteraction.append(foodInter).append("|");
			}
			
			
			ArrayList<String> duplicateTherapy = doScreenDT(scDrug , new Screening(fdbManager));
			if(duplicateTherapy.size() > 0)
				duplicateTherapyBoolean = "true";
				
			for(String dupInter : duplicateTherapy){
				duplicateTherapyInteraction.append(dupInter).append("|");
			}
			
			if(currentDrug.getName().equals("Replaced/Retired Drug"))
	        	fdbResponseBuffer = fdbResponseBuffer.append("CURRENT STATUS :" +currentDrug.getName());
	        
	        if(null != currentDrug.getName() && !currentDrug.getName().equals("Replaced/Retired Drug"))
	        	fdbResponseBuffer = fdbResponseBuffer.append("DRUG NAME : ").append(currentDrug.getName()).append(" | "); 
	        
	        if(null != currentDrug.getDoseForm()  )
	        	fdbResponseBuffer = fdbResponseBuffer.append("DOSE FORM : ").append(currentDrug.getDoseForm()).append(" | "); 
	        
	        
	        if(null != currentDrug.getConfusionGroupDescription() && !currentDrug.getConfusionGroupDescription().equals("") ){
	        	fdbResponseBuffer = fdbResponseBuffer.append("CONFUSION DESCRIPTION : ").append(currentDrug.getConfusionGroupDescription()).append(" | "); 
	        }
	        
	        if(0L != currentDrug.getConfusionGroupID()){
	        	fdbResponseBuffer = fdbResponseBuffer.append("CONFUSION GROUP ID : ").append(currentDrug.getConfusionGroupID()).append(" | "); 
	        }
	        if(currentDrug.getStatusCode().equals("0")){
		    	if(currentDrug.getAHFSClassifications() != null && currentDrug.getAHFSClassifications().count() > 0){
		    		fdbResponseBuffer.append("CLASSIFICATIONS FOR THE DRUG : ");
		    		classification="";
		    		for(int i=0; i< currentDrug.getAHFSClassifications().count(); i++){
		    			classification = classification + currentDrug.getAHFSClassifications().item(i).getDescription() + ",";
		    		}
		    		if(classification.endsWith(",")){
		    			classification = classification.substring(0, classification.length() - 1)+". ";
		    		}
		    		fdbResponseBuffer.append(classification).append("|");
		    		
		    	}
		    	if(null != currentDrug.getFDBClassifications() && currentDrug.getFDBClassifications().count() > 0 )
		    		fdbResponseBuffer.append("FDB CLASSIFICATION ID : ").append(currentDrug.getFDBClassifications().item(0).getID()).append(" | ");
		    	if(null != currentDrug.getDoseRoutes() && currentDrug.getDoseRoutes().count() > 0 
						&&  null != currentDrug.getDoseRoutes().item(0).getDescription())
				fdbResponseBuffer.append("FDB DOSE ROUTE DESCRIPTION : ").append(currentDrug.getDoseRoutes().item(0).getDescription()).append(" | ");
		    	if(null != currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2))
						&& currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).count() > 0
						&& null != currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).item(0).getConditionDescription())
					fdbResponseBuffer = fdbResponseBuffer.append("SIDE EFFECTS : ").append(currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).item(0).getConditionDescription()).append(" | ");
		    }
			drugInfo.add(0, strength);
			drugInfo.add(1, route);
			drugInfo.add(2 , drugFoodBoolean);
			drugInfo.add(3 , drugFoodInteraction.toString());
			drugInfo.add(4, duplicateTherapyBoolean);
			drugInfo.add(5 , duplicateTherapyInteraction.toString());
			drugInfo.add(6 , fdbResponseBuffer.toString());
    	} catch (Exception e) {
			throw e;
		}		
		return drugInfo;
	}*/
	/**
	 * This method is to fetch all drugs from the FDB
	 * @param rowId
	 * @return ArrayList<DrugVO>
	 * @throws Exception 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<DrugVO> retrieveOTCDrugInformation(int offset,String filter){
		List<DrugVO> list = new ArrayList<DrugVO>();
		StringBuffer buffer = new StringBuffer("SELECT m from FdbDispensable m WHERE m.statuscode='0' AND m.reffederallegendcode = '2'");
		if(!StringUtils.isBlank(filter)){
			buffer.append(" and upper(m.descdisplay) LIKE '"+filter.toUpperCase()+"%' "); 
		}
		buffer.append(" ORDER BY m.descdisplay");
		Query query = getEm().createQuery(buffer.toString());
		query.setFirstResult(offset);
		query.setMaxResults(14);		
		List<FdbDispensable> dispensables = query.getResultList();
        for(FdbDispensable dispensable:dispensables){
    		DrugVO drugVO = new DrugVO();
   			drugVO.setDrugCode(dispensable.getMedid().toString());
    		drugVO.setDrugName(dispensable.getDescdisplay());
    		if(dispensable.getNametypecode()=="1"){
    			drugVO.setNameType("Brand Name");
    		}else if(dispensable.getNametypecode()=="2"){
    			drugVO.setNameType("Generic Name");
    		}        		
    		list.add(drugVO);
        }           
        return list;	
	}
	
	@Override
	public Map<String, String> retrieveNDCInformation(long drugId) throws Exception {
		DispensableDrug currentDrug = new DispensableDrug(fdbManager);
		FdbDispensable dispensable = findFdbDispensable((int)drugId);
		if(dispensable == null){
			throw new Exception("Dispensible drug is not found");
		}
		Map<String,String> ndcDetails = new HashMap<String, String>();
//		FWStatus fs =  new FWStatus(this.fdbManager);
//		fs.getDatabaseIssueDate();
		try {
			currentDrug.load(drugId,FDBDispensableDrugLoadType.fdbDDLTMedID , dispensable.getDescdisplay(), "", "");
			if(currentDrug.getStatusCode().equals("0")){
				PackagedDrugs pack = currentDrug.getPackagedDrugs(null);
		    	for(int i=0; i< pack.count(); i++){
		    		ndcDetails.put("medId", String.valueOf( currentDrug.getGenericMedID()));
		    		/*if(pack.item(i).getRepackager() == true || 
		    				(null != pack.item(i).getObsoleteDate() && pack.item(i).getObsoleteDate() != "")
		    				|| pack.item(i).getPrivLabeledProd() == true){
		    		}else{*/
		    			ndcDetails.put("drugCode", String.valueOf(drugId));
		    			ndcDetails.put("drugName", String.valueOf(dispensable.getDescdisplay()));
		    			ndcDetails.put("ndcCode", pack.item(i).getID());
		    			ndcDetails.put("ndcCodeQual", "ND");
		    			ndcDetails.put("codeSystem", "FDB");
		    			ndcDetails.put("ndcCodeSystemVersion", getFdbVersion().getIssuedate());
		    			ndcDetails.put("ndcStrength", pack.item(i).getStrength());
	    				ndcDetails.put("ndcDoseForm", String.valueOf(pack.item(i).getDoseFormID()));
		    			ndcDetails.put("ndcStrengthUnit", pack.item(i).getStrengthUnit());
		    			ndcDetails.put("ndcRoute", pack.item(i).getRoute());
		    			ndcDetails.put("ndcDrug", pack.item(i).getDescription());
		    			ndcDetails.put("ndc", pack.item(i).getNDC());
		    			log.debug("NDC Code-"+i+":"+pack.item(i).getID());
		    			break;
		    		//}
	    		}
	    	}
		} catch (Exception e) {
			throw e;
		}
		return ndcDetails;
	}
	
	/**
	 * This method is to retrieve the corresponding Drug code When we have the Drug Name in hand
	 * @param drugName
	 * @return
	 * @throws Exception 
	 */
	public Integer retrieveCodeFromDrugName(String drugName){
		Query query = getEm().createQuery("SELECT m.medid  from FdbDispensable m WHERE m.descdisplay = :drugName");
		query.setParameter("drugName", drugName);
		Integer drugCode = (Integer)query.getSingleResult();
		return drugCode;
	}
	/**
	 * Method to check the drug validity
	 * @throws Exception 
	 */
	public String findDrugByName(String drugName) {
		Query query = getEm().createQuery("SELECT m.reffederaldeaclasscode as code  from FdbDispensable WHERE m.descdisplay = :drugname");
		return (String)query.getSingleResult();		
	}
	
	/**
	 * Method to get the issueDate from fdb_version.
	 * @throws Exception 
	 */
	public FdbVersion getFdbVersion() {
		Query query = getEm().createQuery("SELECT m FROM FdbVersion m");
		FdbVersion fdbVersion = (FdbVersion)query.getSingleResult();
		return fdbVersion;
	}

	/*private StrengthUnits getStrengthUnit(long id){
		return getEm().find(StrengthUnits.class,id);			
	}*/
	/**
	 * This methos is used get Medication details
	 * @param code
	 * @param medicine
	 * @author suja
	 * added on 17/4/2010
	 * @return
	 * @throws Exception 
	 */
	public Map<String,String>  getMedicationDetails(String code , String medicine) throws Exception{
		Map<String,String> drugInfo = new HashMap<String,String>();
		String strength = "";
		String route = "";
		String doseForm = "";
		String classification ="";
		DispensableDrug currentDrug = new DispensableDrug(fdbManager);		
    	try {
			currentDrug.load(Long.parseLong(code),FDBDispensableDrugLoadType.fdbDDLTMedID , medicine, "", "");
			
			if(null != currentDrug.getStrength() && null != currentDrug.getStrengthUnit()
					&& !currentDrug.getStrength().equals("") && !currentDrug.getStrengthUnit().equals("")){
				strength = currentDrug.getStrength() + " " + currentDrug.getStrengthUnit();
			}
			if(null != currentDrug.getRoute() && !currentDrug.getRoute().equals(""))
				route = currentDrug.getRoute();
			if(null != currentDrug.getDoseForm() && !currentDrug.getDoseForm().equals(""))
				doseForm = currentDrug.getDoseForm();
			
			if(currentDrug.getStatusCode().equals("0")){
		    	if(currentDrug.getAHFSClassifications() != null && currentDrug.getAHFSClassifications().count() > 0){
		    		classification="";
		    		for(int i=0; i< currentDrug.getAHFSClassifications().count(); i++){
		    			classification = classification + currentDrug.getAHFSClassifications().item(i).getDescription() + ",";
		    		}
		    		if(classification.endsWith(",")){
		    			classification = classification.substring(0, classification.length() - 1)+". ";
		    		}
		    	}
		    }		
		drugInfo.put("strength", strength);
		drugInfo.put("route", route);
		drugInfo.put("doseForm", doseForm);
		drugInfo.put("classification", classification);
    	} catch (Exception e) {
			throw e;
		}
	return drugInfo;
	}
	//Re-factored methods
	/**
	 * Method to find findFdbDispensable
	 * @param medicine
	 * @return
	 * @throws Exception 
	 */
	public FdbDispensable  findFdbDispensable(Integer medid){
		return getEm().find(FdbDispensable.class, medid);
	}
	
	public AllergyVO findDrugAllergy(String allergyName){
		StringBuffer queryString = new StringBuffer("SELECT allgpcick FROM FdbAllergenpicklist allgpcick");
		queryString.append(" WHERE upper(allgpcick.description1) like'"+allergyName.toUpperCase()+"'");
		Query query = em.createQuery(queryString.toString());
		FdbAllergenpicklist allergy = (FdbAllergenpicklist) query.getSingleResult();
		AllergyVO allerVO = new AllergyVO();
		if(allergy != null) {
			
			allerVO.setAllergen(allergy.getDescription1());
			if(allergy.getId().getConcepttype() == 1){
	    		allerVO.setType("Allergen Group");
	    	}else if(allergy.getId().getConcepttype() == 2){
	    		allerVO.setType("Drug Name");
	    	}else if(allergy.getId().getConcepttype() == 6){
	    		allerVO.setType("Ingredient");
	    	}
			allerVO.setInternalId(Long.valueOf(allergy.getId().getConceptid()));
		}
		return allerVO;
	}
	
	/**
	 * Method to gather drug information for the RxChange Request
	 * @param medicine
	 * @return
	 * @throws Exception 
	 */
	public MedicationDrug  getDrugInformationForRxChange(long drugCode , String medicine) throws Exception{
		String strength = null;
		String route = null;
		StringBuffer drugFoodInteraction = new StringBuffer();
		StringBuffer duplicateTherapyInteraction = new StringBuffer();
		DispensableDrug currentDrug = new DispensableDrug(fdbManager);
		StringBuffer fdbResponseBuffer = new StringBuffer("No Details found in FDB.");
		String classification = "No Classifications in FDB. ";
    	currentDrug.load(drugCode,FDBDispensableDrugLoadType.fdbDDLTMedID , medicine, "", "");
		if(!StringUtils.isBlank(currentDrug.getStrength()) && !StringUtils.isBlank(currentDrug.getStrengthUnit()))
			strength = currentDrug.getStrength()+" "+currentDrug.getStrengthUnit();
		
		if(!StringUtils.isBlank(currentDrug.getRoute()))
			route = currentDrug.getRoute();
		
		ScreenDrug scDrug = new ScreenDrug(fdbManager);
		scDrug.load(String.valueOf(drugCode),FDBDrugType.fdbDTDispensableDrug );
		scDrug.setDescription(medicine);
		
		ArrayList<String> drugFood = doScreenDFIM(scDrug , new Screening(fdbManager));
			
		for(String foodInter : drugFood){
			drugFoodInteraction.append(foodInter).append("|");
		}	
		
		ArrayList<String> duplicateTherapy = doScreenDT(scDrug , new Screening(fdbManager));
		for(String dupInter : duplicateTherapy){
			duplicateTherapyInteraction.append(dupInter).append("|");
		}
		
		if(currentDrug.getName().equals("Replaced/Retired Drug"))
        	fdbResponseBuffer = fdbResponseBuffer.append("CURRENT STATUS :" +currentDrug.getName());
		else
        	fdbResponseBuffer = fdbResponseBuffer.append("DRUG NAME : ").append(currentDrug.getName()).append(" | "); 
        
        if(null != currentDrug.getDoseForm()  )
        	fdbResponseBuffer = fdbResponseBuffer.append("DOSE FORM : ").append(currentDrug.getDoseForm()).append(" | "); 
        
        if(StringUtils.isBlank(currentDrug.getConfusionGroupDescription())){
        	fdbResponseBuffer = fdbResponseBuffer.append("CONFUSION DESCRIPTION : ").append(currentDrug.getConfusionGroupDescription()).append(" | "); 
        }
        
        if(0L != currentDrug.getConfusionGroupID()){
        	fdbResponseBuffer = fdbResponseBuffer.append("CONFUSION GROUP ID : ").append(currentDrug.getConfusionGroupID()).append(" | "); 
        }
        if(currentDrug.getStatusCode().equals("0")){
	    	if(currentDrug.getAHFSClassifications() != null && currentDrug.getAHFSClassifications().count() > 0){
	    		fdbResponseBuffer.append("CLASSIFICATIONS FOR THE DRUG : ");
	    		classification="";
	    		for(int i=0; i< currentDrug.getAHFSClassifications().count(); i++){
	    			classification = classification + currentDrug.getAHFSClassifications().item(i).getDescription() + ",";
	    		}
	    		if(classification.endsWith(",")){
	    			classification = classification.substring(0, classification.length() - 1)+". ";
	    		}
	    		fdbResponseBuffer.append(classification).append("|");	    		
	    	}
	    	if(null != currentDrug.getFDBClassifications() && currentDrug.getFDBClassifications().count() > 0 )
	    		fdbResponseBuffer.append("FDB CLASSIFICATION ID : ").append(currentDrug.getFDBClassifications().item(0).getID()).append(" | ");
	    	if(null != currentDrug.getDoseRoutes() && currentDrug.getDoseRoutes().count() > 0 
					&&  null != currentDrug.getDoseRoutes().item(0).getDescription())
			fdbResponseBuffer.append("FDB DOSE ROUTE DESCRIPTION : ").append(currentDrug.getDoseRoutes().item(0).getDescription()).append(" | ");
	    	if(null != currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2))
					&& currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).count() > 0
					&& null != currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).item(0).getConditionDescription())
				fdbResponseBuffer = fdbResponseBuffer.append("SIDE EFFECTS : ").append(currentDrug.getSideEffects(FDBSIDESeverityFilter.getInstanceFromInt(2) , FDBSIDEFreqFilter.getInstanceFromInt(2)).item(0).getConditionDescription()).append(" | ");
	    }
        MedicationDrug drug = new MedicationDrug();
        drug.setStrength(strength);
        drug.setRoute(route);
        drug.setDrugFoodInteraction(drugFoodInteraction.toString());
        drug.setDrugCode(drugCode);
        drug.setDrugName(medicine);
        drug.setDuplicateTherapyInteraction(duplicateTherapyInteraction.toString());
        drug.setFdbResponse(fdbResponseBuffer.toString());
        drug.setFdbVersion(getFdbVersion().getDbversion());
        return drug;
        
		/*drugInfo.put("strength", strength);
		drugInfo.put("route", route);
		drugInfo.put("drugFood" , drugFoodBoolean);
		drugInfo.put("drugFoodInteraction" , drugFoodInteraction.toString());
		drugInfo.put("duplicateTherapy", duplicateTherapyBoolean);
		drugInfo.put("duplicateTherapyInteraction" , duplicateTherapyInteraction.toString());
		drugInfo.put("fdbResponse" , fdbResponseBuffer.toString());    			
		return drugInfo;*/
	}
	
	/**
	 * Method to get all data from the FDB
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<FdbDispensable> retrieveDrugInformation(String filter,int rowId){
		StringBuffer queryString = new StringBuffer("SELECT fd FROM FdbDispensable fd WHERE ");
        if(!StringUtils.isBlank(filter)){
        	queryString.append(" upper(fd.descdisplay) LIKE '"+filter.toUpperCase()+"%' AND ");
		}    		
        queryString.append(" fd.statuscode='0' ORDER BY fd.descdisplay");
		
		Query query = em.createQuery(queryString.toString());
		query.setFirstResult(rowId);
		query.setMaxResults(14);
		return (List<FdbDispensable>)query.getResultList();             
	}
	@SuppressWarnings("unchecked")
	public Long findDrugCount(String filter){
		StringBuffer queryString = new StringBuffer("SELECT COUNT(fd.medid) FROM FdbDispensable fd WHERE fd.statuscode='0' ");
        
		if(null != filter && filter.trim().length() > 0){
			 filter = filter.trim();
			 if(filter.equals("OTCDRUGCOUNT")){
				 queryString.append(" AND fd.reffederallegendcode = '2' ");
			 }else{
				 queryString.append(" AND upper(fd.descdisplay) like '"+filter.toUpperCase()+"%'");
			 }	 
		 }
		Query query = em.createQuery(queryString.toString());
		return (Long)query.getSingleResult();             
	}
	
	@SuppressWarnings("unchecked")
	public Long findDrugAllergyCount(String filter){
		StringBuffer queryString = new StringBuffer("SELECT COUNT(allgpcick.description1) FROM FdbAllergenpicklist allgpcick");
        
		if(null != filter && filter.trim().length() > 0){
			 filter = filter.trim();
			 queryString.append(" WHERE upper(allgpcick.description1) like '"+filter.toUpperCase()+"%'");
			 	 
		 }
		Query query = em.createQuery(queryString.toString());
		return (Long)query.getSingleResult();             
	}
	
	/**
	 * Method to check the drug validity
	 */
	public String checkForDrugValidity(String drugName) {
		Query query = getEm().createQuery("SELECT fd.reffederaldeaclasscode as code FROM FdbDispensable fd " +
				"WHERE fd.descdisplay = :drugname");
		query.setParameter("drugname", drugName.trim());
		String valid = (String)query.getSingleResult();
		return valid;
	}

}
