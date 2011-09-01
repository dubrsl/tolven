package org.tolven.process;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.CCHITLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.II;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.ActEx;

/**
 * Processes the criteria defined by the user and populates list with the matching patient.
 * 
 * @author valsaraj
 * added on 05/04/2010
 */
public class PatientListDesignerOperations extends InsertAct {
	private CCHITLocal cchitBean;
	private MenuLocal menuBean;
	private AppEvalAdaptor app;
	private Trim trim;
	private long patientTrimId;
	
	private static PatientListDesignerOperations pldo = new PatientListDesignerOperations();
	private static final String PATIENT_TRIM_NAME = "reg/evn/patient";
	private static final String DIAGNOSIS_TRIM_NAME = "docclin/evn/diagnosis";
	private static final String PROBLEM_TRIM_NAME = "docclin/evn/problem";
	private static final String MEDICATION_TRIM_NAME = "docclin/evn/medication";
	private static final String FDB_MEDICATION_HISTORY_TRIM_NAME = "obs/evn/fdbMedicationHistory";
	private static final String LABTEST_TRIM_NAME = "obs/evn/labOrder";

	private static final String ALLERGY_TRIM_NAME = "docclin/evn/allergy";
	private static final String PROCEDURE_TRIM_NAME = "pxDoc";
	private static final String DIAGNOSES_PATH = "echr:patient:diagnoses:current";
	private static final String PROBLEMS_PATH = "echr:patient:problems:active";
	private static final String MEDICATIONS_PATH = "echr:patient:medications:active";
	private static final String ALLERGIES_PATH = "echr:patient:allergies:current";
	private static final String PROCEDURES_PATH = "echr:patient:procedures:pxList";
	private static final String LABRESULT_PATH = "echr:patient:results:lab";
	private static final String ANNUAL_PHYSICAL_PATH = "echr:patient:encounters:annualPhysical";
	private static final String ANNUAL_PHYSICAL_RECOMMEND_PATH = "echr:patients:annualPhysicalRecommended";
	private static final String TETANUS_IMMU_PATH="echr:patient:immu:current";
	private static final String TETANUS_IMMU_RECOMMEND_PATH="echr:patients:recommendTetanusImmunization";
	private static final String OBS_PATH="echr:patient:observations:active";
	private static final String BMI_RECOMMEND_PATH="echr:patients:recommendBMI";
	private static final String PATIENT_DOB_FORMAT = "yyyyMMdd";

	/**
	 * Retrieves CCHIT session bean.
	 * 
	 * @author valsaraj
	 * added on 05/04/2010
	 * @return
	 */
	public CCHITLocal getCCHITBean() {
		if (cchitBean == null) {
			try {
				InitialContext ctx = new InitialContext();
				cchitBean = (CCHITLocal) ctx.lookup("java:global/tolven/tolvenEJB/CCHITBean!org.tolven.app.CCHITLocal");
			}
			catch (NamingException ex) {
				TolvenLogger.info(" Failed to look up java:global/tolven/tolvenEJB/CCHITBean!org.tolven.app.CCHITLocal", this.getClass());
				throw new RuntimeException("Failed to look up java:global/tolven/tolvenEJB/CCHITBean!org.tolven.app.CCHITLocal", ex);
			}
		}
		
		return cchitBean;
	}
	
	/**
	 * Retrieves Menu session bean.
	 * 
	 * @author valsaraj
	 * added on 05/04/2010
	 * @return
	 */
	public MenuLocal getMenuBean() {
		if (menuBean == null) {
			try {
				InitialContext ctx = new InitialContext();
				menuBean = (MenuLocal) getCtx().lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal");
			}
			catch (NamingException ex) {
				TolvenLogger.info(" Failed to look up java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal", this.getClass());
				throw new RuntimeException("Failed to look up java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal", ex);
			}
		}
		
		return menuBean;
	}
	
	/**
	 * Processes the user defined criteria and populates user defined patient list based on that.
	 * 
	 * @author valsaraj
	 * added on 05/04/2010
	 * @param app
	 * @param ms
	 * @param list
	 * @param age
	 * @param ageRel
	 * @param gender
	 * @param genderRel
	 * @param zip
	 * @param zipRel
	 * @param diagnosisIncludeCodes
	 * @param diagnosisInclude
	 * @param problemIncludeCodes
	 * @param problemInclude
	 * @param medicationsIncludeCodes
	 * @param medicationsInclude
	 * @param allergiesIncludeCodes
	 * @param allergiesInclude
	 * @param proceduresIncludeCodes
	 * @param proceduresInclude
	 * @param diagnosisExcludeCodes
	 * @param diagnosisExclude
	 * @param problemExcludeCodes
	 * @param problemExclude
	 * @param medicationsExcludeCodes
	 * @param medicationsExclude
	 * @param allergiesExcludeCodes
	 * @param allergiesExclude
	 * @param proceduresExcludeCodes
	 * @param proceduresExclude
	 * @throws Exception
	 */
	public static void process(AppEvalAdaptor app, MenuStructure ms, Trim trim, String list, String ageStr, String ageRel, 
			String gender, String genderRel, String zip, String zipRel, String diagnosesIncludeCodes, 
			String diagnosesInclude, String problemIncludeCodes, String problemInclude, String medicationsIncludeCodes, 
			String medicationsInclude, String allergiesIncludeCodes, String allergiesInclude, 
			String proceduresIncludeCodes, String proceduresInclude, 
			String labtestsIncludeCodes, String labtestsInclude,String diagnosesExcludeCodes, 
			String diagnosesExclude, String problemExcludeCodes, String problemExclude, 
			String medicationsExcludeCodes, String medicationsExclude, String allergiesExcludeCodes, 
			String allergiesExclude, String proceduresExcludeCodes, String proceduresExclude,
			String labtestsExcludeCodes, String labtestsExclude) throws Exception {
		TolvenLogger.info("Evaluates patient information with " + list + " List", PatientListDesignerOperations.class);
		boolean valid = true;
		pldo.setApp(app);
		pldo.setTrim(trim);
		MenuData mdPat = null;
		
		try {
			for (ActParticipation actParticipation : trim.getAct().getParticipations()) {				
				for(II ii : actParticipation.getRole().getId().getIIS()) {
					if (ii.getExtension().startsWith("echr:patient-")) {
						pldo.setPatientTrimId(Long.parseLong(ii.getExtension().split("-")[1]));
						
						break;
					}
				}
			}
			
			mdPat = pldo.getMenuBean().findMenuDataItem(pldo.getPatientTrimId());
			
			if (! pldo.validateAge(mdPat, ageStr, ageRel) ||
					! pldo.validateGender(mdPat, gender, genderRel) ||
					! pldo.validateZip(mdPat, zip, zipRel)) {
				valid = false;
			}
			
			if (valid != false) {
				int dx = pldo.validateDiagnoses(diagnosesIncludeCodes, diagnosesInclude, diagnosesExcludeCodes, diagnosesExclude);
				int pbm = pldo.validateProblems(problemIncludeCodes, problemInclude, problemExcludeCodes, problemExclude);
				int med = pldo.validateMedications(medicationsIncludeCodes, medicationsInclude, medicationsExcludeCodes, medicationsExclude);
				int alg = pldo.validateAllergies(allergiesIncludeCodes, allergiesInclude, allergiesExcludeCodes, allergiesExclude);
				int px = pldo.validateProcedures(proceduresIncludeCodes, proceduresInclude, proceduresExcludeCodes, proceduresExclude);
				int lab = pldo.validateLabTests(labtestsIncludeCodes, labtestsInclude, labtestsExcludeCodes, labtestsExclude);
				
//				if ((dx + pbm + med + alg + px + lab == 12) ||
//					dx == 1 || pbm == 1 || med == 1 || alg == 1 || px == 1 || lab == 1) {
				
				if(dx>0 && pbm>0 && med>0 && alg>0 && px>0 && lab>0 )	
				{
					valid = true;
				}
				else {
					valid = false;
				}
			}
			
			try
			{
				if (valid) {
					if (mdPat != null) {
						app.createReferenceMD(mdPat, ms);
						TolvenLogger.info("Added patient to " + list + " List", PatientListDesignerOperations.class);
					}
					else {
						throw new Exception("Patient menudata is null");
					}
				}
				else {
					throw new Exception("Invalid patient");
				}
			}
			catch(Exception e)
			{
				throw e;
			}
			finally
			{
			
				// If trim is a patient trim check for general rules
				if(trim.getName().equals(PATIENT_TRIM_NAME))
				{
					// Check patient for Annual Physical.
					AccountMenuStructure annPhyMS=pldo.getMenuBean().findAccountMenuStructure(pldo.getApp().getAccount().getId(), ANNUAL_PHYSICAL_RECOMMEND_PATH);
					
					//List<MenuData> menuDataList = pldo.getMenuBean().findReferencingMDs(mdPat, annPhyMS);
					
					if(pldo.needsAnnualPhysical())
					{
						TolvenLogger.info("Patient Needs Annual Physical", PatientListDesignerOperations.class);
						TolvenLogger.info("Menu Structure: "+annPhyMS.getPath(), PatientListDesignerOperations.class);
						TolvenLogger.info("Patient1: "+mdPat.getPath(), PatientListDesignerOperations.class);
						
						app.createReferenceMD(mdPat, annPhyMS);
					}
					
					// Check patient for Tetanus Immunization
					AccountMenuStructure tetImmuMS=pldo.getMenuBean().findAccountMenuStructure(pldo.getApp().getAccount().getId(), TETANUS_IMMU_RECOMMEND_PATH);
					
					if(pldo.needsTetanusImmunization())
					{
						
						TolvenLogger.info("Patient Needs Tetanus Immunization", PatientListDesignerOperations.class);
						TolvenLogger.info("Menu Structure: "+tetImmuMS.getPath(), PatientListDesignerOperations.class);
						TolvenLogger.info("Patient2: "+mdPat.getPath(), PatientListDesignerOperations.class);
					
						app.createReferenceMD(mdPat, tetImmuMS);
					}
					
					// Check patient for BMI
					AccountMenuStructure bmiMS=pldo.getMenuBean().findAccountMenuStructure(pldo.getApp().getAccount().getId(), BMI_RECOMMEND_PATH);
					
					if(pldo.needsBMI())
					{
						TolvenLogger.info("Patient Needs BMI", PatientListDesignerOperations.class);
						TolvenLogger.info("Menu Structure: "+tetImmuMS.getPath(), PatientListDesignerOperations.class);
						TolvenLogger.info("Patient2: "+mdPat.getPath(), PatientListDesignerOperations.class);
					
						app.createReferenceMD(mdPat, bmiMS);
					}
				}
			}
		}
		catch (Exception e) {
			TolvenLogger.info("Patient is not eligible for " + list + " List", PatientListDesignerOperations.class);
		}
	}

		public boolean needsAnnualPhysical() 
		{
			if(getLatestDate(ANNUAL_PHYSICAL_PATH,"")<((new Date()).getTime())-(new Long("31557600000")))
			{
				return true;
			}
			return false;
		}
		
		public boolean needsTetanusImmunization() 
		{
			if(getLatestDate(TETANUS_IMMU_PATH,"")<((new Date()).getTime())-(new Long("315576000000")))
			{
				return true;
			}
			return false;
		}
		
		public boolean needsBMI() 
		{
			if(getLatestDate(OBS_PATH,"string08=BMI")<((new Date()).getTime())-(new Long("315576000000")))
			{
				return true;
			}
			return false;
		}

		private Long getLatestDate(String path,String condition)
		{
			try {
				List<Map<String, Object>> menuDataList = getCCHITBean().findAllMenuDataList(path, path.replace("patient", "patient-" + getPatientTrimId()), "DateSort=DESC", getApp().getAccountUser());
				
				Long latestDate=new Long(0); 
				for (Map<String, Object> menuData : menuDataList) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					String date01=menuData.get("date01").toString();
					TolvenLogger.info("Date in Loop: "+date01,PatientListDesignerOperations.class);
					
					if(evaluateConditions(menuData,condition) && date01!=null && !date01.equals(""))
					{
						Date date = sdf.parse(date01);
						
						if(date.getTime()>latestDate)
						{
							latestDate=date.getTime();
						}
					}
				}
				return latestDate;
			}
			catch (Exception e) {
				return new Long(0);
			}
		}
		
		private boolean evaluateConditions(Map<String, Object> menuData, String condition)
		{
			String splitChar = ":";
			
			if (condition != null && condition != "") {
				String[] condnParams=condition.split(splitChar);
				String[] param;
				String value="";
				
				for (int i = 0; i < condnParams.length; i++) {
					param=condnParams[i].split("=");
				
					if (param.length > 1){
						value=menuData.get(param[0]).toString();
						if(value==null || !value.equalsIgnoreCase(param[01])) return false;
					}
				}
			}
			return true;
		}
	/**
	 * Validates patient age.
	 * 
	 * @author valsaraj
	 * added on 05/04/2010
	 * @param patTrim
	 * @param ageStr
	 * @param ageRel
	 * @return
	 */
	public boolean validateAge(MenuData mdPat, String ageStr, String ageRel) {
		final String AGE_LESS_THAN = "C0183108";
		final String AGE_GREATER_THAN = "C0183107";
		final String AGE_EQUAL = "C0183106";		
		boolean valid = true;
		
		if (! "null".equals(ageRel)) {
			try {
				int patAge = -1;
				
				try {
					if (PATIENT_TRIM_NAME.equals(pldo.getTrim().getName())) {
						String strDOB = pldo.getTrim().getAct().getParticipations().get(0).getRole().getPlayer().getLivingSubject().getBirthTime().getTS().getValue();
						DateFormat df = new SimpleDateFormat(PATIENT_DOB_FORMAT);
						patAge = convertToAge(df.parse(strDOB), PATIENT_DOB_FORMAT);
					}
					else {
						patAge = convertToAge(mdPat.getDate01(), PATIENT_DOB_FORMAT);
					}					
				}
				catch (Exception e) {
					valid = false;
				}
				
				if (patAge > -1) { 
					int age = Integer.parseInt(ageStr);
					
					if (AGE_LESS_THAN.equals(ageRel)) {
						if (patAge >= age) {
							valid = false;
						}
					}
					else if (AGE_GREATER_THAN.equals(ageRel)) {
						if (patAge <= age) {
							valid = false;
						}
					}
					else if (AGE_EQUAL.equals(ageRel)) {
						if (age != patAge) {
							valid = false;
						}
					}
				}
			}
			catch (Exception e) {
				
			}
		}
		
		return valid;
	}

	/**
	 * Validates gender.
	 * 
	 * @author valsaraj
	 * added on 05/04/2010
	 * @param patTrim
	 * @param gender
	 * @param genderRel
	 * @return
	 */
	public boolean validateGender(MenuData mdPat, String gender, String genderRel) {
		final String MALE = "C0183108";
		final String FEMALE = "C0183107";
		final String INTERMEDIATE_OR_BOTH = "C0183106";
		final String INCLUDE = "C0183108";
		final String EXCLUDE = "C0183107";
		boolean valid = true;
		String patGender;
		
		if (! "null".equals(genderRel)) {
			try {
				if (PATIENT_TRIM_NAME.equals(pldo.getTrim().getName())) {
					patGender = pldo.getTrim().getAct().getParticipations().get(0).getRole().getPlayer().getLivingSubject().getAdministrativeGenderCode().getCE().getDisplayName();
				}
				else {
					patGender = mdPat.getString04();
				}
				
				if (INCLUDE.equals(genderRel)) {
					if (MALE.equals(gender)) {
						if (! "Male".equals(patGender)) {
							valid = false;
						}
					}
					else if (FEMALE.equals(gender)) {
						if (! "Female".equals(patGender)) {
							valid = false;
						}
					}
					else if (INTERMEDIATE_OR_BOTH.equals(genderRel)) {
						
					}
				}
				else if (EXCLUDE.equals(genderRel)) {
					if (MALE.equals(gender)) {
						if ("Male".equals(patGender)) {
							valid = false;
						}
					}
					else if (FEMALE.equals(gender)) {
						if ("Female".equals(patGender)) {
							valid = false;
						}
					}
					else if (INTERMEDIATE_OR_BOTH.equals(genderRel)) {
						
					}
				}
			}
			catch (Exception e) {
				valid = false;
			}
		}
		
		return valid;
	}
	
	/**
	 * Validates zip code.
	 * 
	 * @author valsaraj
	 * added on 05/04/2010
	 * @param patTrim
	 * @param zip
	 * @param zipRel
	 * @return
	 */
	public boolean validateZip(MenuData mdPat, String zip, String zipRel) {
		final String ZIP_NOT_EQUAL = "C0183108";
		final String ZIP_EQUAL = "C0183107";
		boolean valid = true;
		String homeZip;
		
		if (! "null".equals(zipRel)) {
			try {
				if (PATIENT_TRIM_NAME.equals(pldo.getTrim().getName())) {
					homeZip = pldo.getTrim().getAct().getParticipations().get(0).getRole().getPlayer().getPerson().getAddr().getADS().get(0).getParts().get(5).getST().getValue();
				}
				else {
					homeZip = mdPat.getExtendedField("homeZip").toString();
				}
				
				if (ZIP_EQUAL.equals(zipRel)) {
					if (! zip.equals(homeZip)) {
						valid = false;
					}
				}
				else if (ZIP_NOT_EQUAL.equals(zipRel)) {
					if (zip.equals(homeZip)) {
						valid = false;
					}
				}
			}
			catch (Exception e) {
				valid = false;
			}
		}
		
		return valid;
	}
	
	/**
	 * Validates diagnoses.
	 * 
	 * @author valsaraj
	 * added on 05/12/2010
	 * @param diagnosesIncludeCodes
	 * @param diagnosesInclude
	 * @param diagnosesExcludeCodes
	 * @param diagnosesExclude
	 * @return
	 */
	public int validateDiagnoses(String diagnosesIncludeCodes, String diagnosesInclude, String diagnosesExcludeCodes, String diagnosesExclude) {
		return validate(DIAGNOSES_PATH, diagnosesIncludeCodes, diagnosesInclude, diagnosesExcludeCodes, diagnosesExclude);
	}
	
	/**
	 * Validates problems
	 * 
	 * @author valsaraj
	 * added on 05/12/2010
	 * @param problemIncludeCodes
	 * @param problemInclude
	 * @param problemExcludeCodes
	 * @param problemExclude
	 * @return
	 */
	public int validateProblems(String problemIncludeCodes, String problemInclude, String problemExcludeCodes, String problemExclude) {
		return validate(PROBLEMS_PATH, problemIncludeCodes, problemInclude, problemExcludeCodes, problemExclude);
	}
	
	/**
	 * Validates medications.
	 * 
	 * @author valsaraj
	 * added on 05/12/2010
	 * @param medicationsIncludeCodes
	 * @param medicationsInclude
	 * @param medicationsExcludeCodes
	 * @param medicationsExclude
	 * @return
	 */
	public int validateMedications(String medicationsIncludeCodes, String medicationsInclude, String medicationsExcludeCodes, String medicationsExclude) {
		return validate(MEDICATIONS_PATH, medicationsIncludeCodes, medicationsInclude, medicationsExcludeCodes, medicationsExclude);
	}
	
	/**
	 * Validates allergies.
	 * 
	 * @author valsaraj
	 * added on 05/12/2010
	 * @param allergiesIncludeCodes
	 * @param allergiesInclude
	 * @param allergiesExcludeCodes
	 * @param allergiesExclude
	 * @return
	 */
	public int validateAllergies(String allergiesIncludeCodes, String allergiesInclude, String allergiesExcludeCodes, String allergiesExclude) {
		return validate(ALLERGIES_PATH, allergiesIncludeCodes, allergiesInclude, allergiesExcludeCodes, allergiesExclude);
	}
	
	/**
	 * Validates procedures.
	 * 
	 * @author valsaraj
	 * added on 05/12/2010
	 * @param proceduresIncludeCodes
	 * @param proceduresInclude
	 * @param proceduresExcludeCodes
	 * @param proceduresExclude
	 * @return
	 */
	public int validateProcedures(String proceduresIncludeCodes, String proceduresInclude, String proceduresExcludeCodes, String proceduresExclude) {
		return validate(PROCEDURES_PATH, proceduresIncludeCodes, proceduresInclude, proceduresExcludeCodes, proceduresExclude);
	}

	/**
	 * Validates Lab Tests.
	 * 
	 * @author Kanag Kuttiannan
	 * added on 7/7/2011
	 * @param labtestsIncludeCodes
	 * @param labtestsInclude
	 * @param labtestsExcludeCodes
	 * @param labtestsExclude
	 * @return
	 */
	public int validateLabTests(String labtestsIncludeCodes, String labtestsInclude, String labtestsExcludeCodes, String labtestsExclude) {
		
		try {
			if ("0".equals(labtestsInclude) && "0".equals(labtestsExclude)) {
				return 2;
			}
			
			int valid = 1;
			
			HashMap<String,String> codeValueMap=new HashMap<String,String>();
			HashMap<String,Long> codeTimestampMap=new HashMap<String,Long>();
			try {
				List<Map<String, Object>> menuDataList = getCCHITBean().findAllMenuDataList(LABRESULT_PATH, LABRESULT_PATH.replace("patient", "patient-" + getPatientTrimId()), "DateSort=DESC", getApp().getAccountUser());
				
				for (Map<String, Object> menuData : menuDataList) {
					String loincCode=menuData.get("loincCode").toString();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					String date01=menuData.get("date01").toString();
					
					
					String value=menuData.get("Value").toString();
					
					if(value.endsWith(".0")) value=value.substring(0,value.length()-2);
					
					Long currentTime=codeTimestampMap.get(loincCode);
					
					if(date01!=null && !date01.equals(""))
					{
						Date date = sdf.parse(date01);
						if(date.getTime()>((new Date()).getTime())-(new Long("31557600000")))
						{
							if(currentTime==null || currentTime.longValue()<date.getTime())
							{
								codeValueMap.put(loincCode, value);
								codeTimestampMap.put(loincCode, date.getTime());
							}
						}
					}
				}
			}
			catch (Exception e) {
				
			}
			
			if ("1".equals(labtestsInclude)) {
				if(checkLabCodes(codeValueMap,labtestsIncludeCodes,false)==0) return 0;
			}
			
			if ("1".equals(labtestsExclude)) {			
				if(checkLabCodes(codeValueMap,labtestsExcludeCodes,true)==0) return 0;
			}
			
			
			return valid;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
		
		
		
	}
	
	private int checkLabCodes(HashMap<String,String> codeValueMap, String codes, boolean exclude)
	{
		List<String> codeValues = Arrays.asList(Pattern.compile(",").split(codes));
		
		for (String codeValue : codeValues) {
			
			String[] codeValuesArr = codeValue.split(":");
			
			if(codeValuesArr.length>2 && codeValueMap.containsKey(codeValuesArr[0]))
			{
				if(exclude)
				{
					if (checkValue(codeValuesArr[1],codeValueMap.get(codeValuesArr[0]),codeValuesArr[2],(codeValuesArr.length>3?codeValuesArr[3]:""))) {
						return 0;
					}
				}
				else
				{
					if (!checkValue(codeValuesArr[1],codeValueMap.get(codeValuesArr[0]),codeValuesArr[2],(codeValuesArr.length>3?codeValuesArr[3]:""))) {
						return 0;
					}
				}
			}
		}
		
		return 1;
		
	}
	
	
	private boolean checkValue(String operation, String resultValueStr, String checkValueStartStr,String checkValueEndStr)
	{
		if(operation.equals("eq"))
		{
			return resultValueStr.equalsIgnoreCase(checkValueStartStr);
		}
		else 
		{
			Double resultValue=new Double(resultValueStr);
			Double checkValueStart=new Double(checkValueStartStr);
			Double checkValueEnd=new Double("".equals(checkValueEndStr)?"0":checkValueEndStr);
			
			if(operation.equals("neq"))
			{
				return !(resultValue.doubleValue()==checkValueStart.doubleValue());
			}
			else if(operation.equals("gt"))
			{
				return resultValue.doubleValue()>checkValueStart.doubleValue();
			}
			else if(operation.equals("lt"))
			{
				return resultValue.doubleValue()<checkValueStart.doubleValue();
			}
			else if(operation.equals("bet"))
			{
				return (resultValue.doubleValue()>=checkValueStart.doubleValue() &&
						resultValue.doubleValue()<=checkValueEnd.doubleValue()
				);
			}
		}
		
		return false;
	}
	
	

	/**
	 * Utility function to perform clinical finding validation.
	 * 
	 * @author valsaraj
	 * added on 05/04/2010
	 * @param menuPath
	 * @param includeCodes
	 * @param include
	 * @param excludeCodes
	 * @param exclude
	 * @return
	 */
	public int validate(String menuPath, String includeCodes, String include, String excludeCodes, String exclude) {
		try {
			if ("0".equals(include) && "0".equals(exclude)) {
				return 2;
			}
			
			int valid = 1;
			Set<String> codes = getClinicalFindingCodes(menuPath);
			
			if ("1".equals(include)) {
				if (! checkCodes(codes, includeCodes, true)) {
					return 0;
				}
			}
			
			if ("1".equals(exclude)) {			
				if (checkCodes(codes, excludeCodes, false)) {
					return 0;
				}
			}
			
			return valid;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	/**
	 * Returns code of clinical finding in menu path..
	 * 
	 * @author valsaraj
	 * added on 05/12/2010
	 * @param menuPath
	 * @return
	 * @throws ParseException 
	 */
	public Set<String> getClinicalFindingCodes(String menuPath) {
		Set<String> codes = new HashSet<String>();
		
		try {
			List<Map<String, Object>> menuDataList = getCCHITBean().findAllMenuDataList(menuPath, menuPath.replace("patient", "patient-" + getPatientTrimId()), "DateSort=DESC", getApp().getAccountUser());
			
			for (Map<String, Object> menuData : menuDataList) {
				String tempStr=(String)menuData.get("Code");
				if(tempStr==null || "".equals(tempStr)) tempStr=(String)menuData.get("code");
				codes.add(tempStr.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Trim trim = pldo.getTrim();
			
			if (DIAGNOSES_PATH.equals(menuPath)) {
				if (trim.getName().equals(DIAGNOSIS_TRIM_NAME)) {
					List<ActRelationship> entries = ((ActEx) trim.getAct()).getRelationshipsList().get("entry");
					
					for (ActRelationship actRelationship : entries) {
						codes.add(actRelationship.getAct().getObservation().getValues().get(0).getCE().getTranslations().get(0).getCode());
					}
				}
			}
			else if (PROBLEMS_PATH.equals(menuPath)) {
				if (trim.getName().equals(PROBLEM_TRIM_NAME)) {
					List<ActRelationship> entries = ((ActEx) trim.getAct()).getRelationshipsList().get("entry");
					
					for (ActRelationship actRelationship : entries) {
						codes.add(actRelationship.getAct().getObservation().getValues().get(0).getCE().getCode());
					}
				}
			}
			else if (MEDICATIONS_PATH.equals(menuPath)) {
				if (trim.getName().equals(MEDICATION_TRIM_NAME) || trim.getName().equals(FDB_MEDICATION_HISTORY_TRIM_NAME)) {
					List<ActRelationship> entries = ((ActEx) ((ActEx) trim.getAct()).getRelationship().get("medications").getAct()).getRelationshipsList().get("medication");
					
					for (ActRelationship actRelationship : entries) {
						codes.add(actRelationship.getAct().getObservation().getValues().get(10).getCE().getCode());
					}
				}
			}
			else if (ALLERGIES_PATH.equals(menuPath)) {
				if (trim.getName().equals(ALLERGY_TRIM_NAME)) {
					List<ActRelationship> entries = ((ActEx) trim.getAct()).getRelationshipsList().get("entry");
					
					for (ActRelationship actRelationship : entries) {
						codes.add(actRelationship.getAct().getObservation().getValues().get(0).getCE().getCode());
					}
				}
			}
			else if (PROCEDURES_PATH.equals(menuPath)) {
				if (trim.getName().equals(PROCEDURE_TRIM_NAME)) {
					List<ActRelationship> entries = ((ActEx) trim.getAct()).getRelationshipsList().get("entry");
					
					for (ActRelationship actRelationship : entries) {
						codes.add(actRelationship.getAct().getObservation().getValues().get(0).getCE().getTranslations().get(0).getCode());
					}
				}
			}
		}
		catch (Exception e) {
			
		}
		
		return codes;
	}

	/**
	 * Performs code checking using the codes in clinical finding and the codes defined.
	 * 
	 * @author valsaraj
	 * added on 05/12/2010
	 * @param codes
	 * @param codesStrToCheck
	 * @return
	 */
	public boolean checkCodes(Set<String> codes, String codesStrToCheck, boolean flag) {
		boolean checkFlag = flag;
		List<String> checkList = Arrays.asList(Pattern.compile(",").split(codesStrToCheck));
		
		for (String check : checkList) {
			checkFlag = false;
			
			if (codes.contains(check)) {
				checkFlag = true;
				
				break;
			}
		}
		
		return checkFlag;
	}

	/**
	 * This function is used to convert date to age.
	 * 
	 * @author Valsaraj
	 * added on 07/16/09
	 * @param dob
	 * @param dateFormat
	 * @return age
	 */
	public Integer convertToAge(Object dob, String dateFormat) {
		Integer age = -1;
		DateFormat df = new SimpleDateFormat(dateFormat);
		String ageStr = "";
			
		try {
			if (dob instanceof Date) {
				ageStr = toAgeString((Date) dob, new Date(), null);
			}
			else if (dob != null && dob.toString().length() > 9) {
				ageStr = toAgeString(df.parse(dob.toString().substring(0, 11)), new Date(), null);
			}
			else if (dob.toString().length() > 7) {
				ageStr = toAgeString(df.parse(dob.toString()), new Date(), null);
			}
			
			
			age = ageStr.contains("y") ? Integer.parseInt(ageStr.replace("y", "")) : 0;
		}
		catch (Exception e) {
			TolvenLogger.info(e.getMessage(), InsertAct.class);
			e.printStackTrace();
		}
		
		return age;
	}
	
	/**
	 * Using "now" and "date of birth", compute a readable age string.
	 * STILL NEEDS I18N support. Also, fixed the bug which occurs when age is 1 year.
	 * 
	 * @author valsaraj
	 * added on 07/16/09
	 * @param dob
	 * @param now
	 * @return
	 */
	public static String toAgeString(Date dob1, Date now1, Locale locale) {
		ResourceBundle bundle;
		
		if (locale == null) {
			bundle = ResourceBundle.getBundle(ResourceBundleHelper.GLOBALBUNDLENAME, Locale.getDefault());
		}
		else {
			bundle = ResourceBundle.getBundle(ResourceBundleHelper.GLOBALBUNDLENAME, locale);
		}
		
		Calendar now = new GregorianCalendar();
		now.setTime(now1);
	    Calendar dob = new GregorianCalendar();
	    dob.setTime(dob1);
	    
	    if (dob.after(now)) {
	    	return bundle.getString("ageUnborn");
	    }
	    
	    int years = now.get(Calendar.YEAR)- dob.get(Calendar.YEAR);
	    int days = now.get( Calendar.DAY_OF_YEAR ) - dob.get( Calendar.DAY_OF_YEAR );
	   
	    if (days < 0) {
	        years--;
	        days = days + 365;
	    }
	    
	    if (years > 0) {
	    	return Integer.toString( years ) + bundle.getString("ageInYears");
	    }
	    
	    if (years == 0 && days < 30) {
	        return Integer.toString( days ) + bundle.getString("ageInDays");
	    }
	    
	    return Integer.toString( years * 12 + (days/30) ) + bundle.getString("ageInMonths");
	}
	
	public AppEvalAdaptor getApp() {
		return app;
	}

	public void setApp(AppEvalAdaptor app) {
		this.app = app;
	}

	public long getPatientTrimId() {
		return patientTrimId;
	}

	public void setPatientTrimId(long patientTrimId) {
		this.patientTrimId = patientTrimId;
	}
}