package org.tolven.app.bean;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.drools.facttemplates.FactTemplate;
import org.drools.rule.Package;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.ApplicationMetadataLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.PatientListDesignerLocal;
import org.tolven.app.PatientListDesignerRemote;
import org.tolven.app.bean.ApplicationMetadata;
import org.tolven.app.bean.ApplicationMetadata.AppExtendsCompare;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountRole;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.AccountUserRole;
import org.tolven.doc.RulesLocal;
import org.tolven.doc.entity.RulePackage;
import org.tolven.menuStructure.Application;
import org.tolven.menuStructure.Extends;
import org.tolven.menuStructure.parse.ParseMenuStructure;
import org.tolven.rules.PackageCompiler;
import org.tolven.rules.PlaceholderFactTemplate;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.ActEx;
import org.tolven.util.ExceptionFormatter;

/**
 * Manage patient lists.
 * 
 * @author valsaraj
 * added on 05/03/2010
 */
@Stateless
@Local(PatientListDesignerLocal.class)
@Remote(PatientListDesignerRemote.class)
public class PatientListDesignerBean implements PatientListDesignerLocal, PatientListDesignerRemote {
	@EJB private RulesLocal ruleBean;
	@EJB private ApplicationMetadataLocal metadataLocal;
	@EJB private AccountDAOLocal accountBean;
	@EJB private MenuLocal menuBean;
	@PersistenceContext EntityManager em;
	
	private Logger logger = Logger.getLogger(this.getClass());
    private String listName = null;
    private String listNameCopy = null;
    private AppEvalAdaptor app;
    private PackageCompiler compiler = new PackageCompiler();
    private String principal;
    private boolean privateList;
    
    private static final String ACTIVE_STATUS = "active";
	
	public static String APPLICATION_EXTENSION = ".application.xml";
	public static String PATIENT_LIST_DESIGNER_TRIM = "patientListDesigner";
	public static String RULE_PACKAGE_NAME = "pld";
	
	/**
	 * Creates patient list.
	 * 
	 * @author valsaraj
	 * added on 05/03/2010
	 * @param trim
	 * @param app
	 */
	public void createPatientList(Trim trim, AppEvalAdaptor app) {
		try {
			if (PATIENT_LIST_DESIGNER_TRIM.equals(trim.getName())) {
				setApp(app);
				setPrincipal(trim.getTolvenEventIds().get(0).getPrincipal());
				
				try {
					setPrivateList("Yes".equals(((ActEx) trim.getAct()).getRelationship().get("generalDetail").getAct().getObservation().getValues().get(1).getCE().getDisplayName()) ? true : false);
				}
				catch (Exception e) {
					setPrivateList(false);
				}
				
				String listName = ((ActEx) trim.getAct()).getRelationship().get("generalDetail").getAct().getObservation().getValues().get(0).getST().getValue();
				setListName(toCamelCase(removeSpecialCharacters(listName)));
				listNameCopy = ((ActEx) trim.getAct()).getRelationship().get("generalDetail").getAct().getObservation().getValues().get(2).getST().getValue();
				
				if (! "".equals(listNameCopy)) {
					setListNameCopy(toCamelCase(removeSpecialCharacters(listNameCopy)));
				}
				else {
					loadApplications(createApplications(trim));	
				}
				
				loadRulePackage(RULE_PACKAGE_NAME, createRule(trim));
			}
			else {
				logger.info("Invalid TRIM");
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/**
	 * Loads application metadata to DB.
	 * 
	 * @author valsaraj
	 * added on 05/03/2010
	 * @param appFiles
	 */
	public void loadApplications(Map<String, String> appFiles) {
        java.util.List<Application> extendApplications = new ArrayList<Application>();
        ParseMenuStructure pa = new ParseMenuStructure();
        
		for (Map.Entry<String, String> entry : appFiles.entrySet()) {
			if (entry.getKey().endsWith(APPLICATION_EXTENSION)) {
				try {
					StringReader input = new StringReader(entry.getValue());
					Application application = pa.parseReader(input);
					
		    		if (application.getName() == null) {
		    			logger.info("Extending application(s) from " + entry.getKey());
		    			extendApplications.add(application);
		    		}
				}
				catch (Exception e) {
					throw new RuntimeException("Error parsing file " + entry.getKey(), e);
				}
			}
		}
		
        // Extensions
		processExtends(extendApplications);
        
        // Now go back for properties
        for (Application application : extendApplications) {
        	metadataLocal.uploadProperties(application, appFiles);
        }
        
		logger.info("Finished creating application metadata");
	}
	
	/**
	 * Individual updates to an account type begin with an extends node which specifies the path
	 * being updated.
	 * @param app
	 */
	public void processExtends( java.util.List<Application> extendApplications ) {
		java.util.List<Extends> extensions = new ArrayList<Extends>();
		// Pull all extensions from each application file into one list
        // Now pick up the extensions to established applications
        for (Application application : extendApplications) {
    		if (application.getMenu()!=null) {
    			throw new RuntimeException( "<menu> element not allowed in unnamed application " + application.getName() );
    		}
    		if (application.getDepends().size()>0) {
    			throw new RuntimeException( "<depends> element not allowed in unnamed application " + application.getName() );
    		}
    		if (application.getLogo()!=null) {
    			throw new RuntimeException( "logo attribute not allowed in unnamed application " + application.getName() );
    		}
    		if (application.getTitle()!=null) {
    			throw new RuntimeException( "title attribute not allowed in unnamed application " + application.getName() );
    		}
    		metadataLocal.processTrimMenus( application.getTrimMenus() );
    		for (Extends ext : application.getExtends()) {
    			extensions.add( ext );
    		}
        }
        // Part 2: Sort the applications
        Collections.sort(extensions, new ApplicationMetadata().new AppExtendsCompare());
        
		// This entire application.xml contains extensions to an existing templateAccount
		for (Extends ext : extensions) {
			processExtend( ext );
		}
	}
	
	/**
	 * Process the extend element
	 * @param ext
	 */
	public void processExtend( Extends ext ) {
		if (ext.getPath()==null | ext.getPath().length()==0) {
			throw new RuntimeException( "Missing path attribute in application extension at " /* + ext.sourceLocation()*/);
		}
		String paths[] = ext.getPath().split("\\,");
		for (String path : paths) {
			// Look up this path
			java.util.List<AccountMenuStructure> amss = metadataLocal.getMatchingMenuStructures( path );
			// If not found but required, error
			if (amss.size()==0 && !ext.isOptional()) {
				throw new RuntimeException( "Required path (" + path + ") in extends element not found at " /* + ext.sourceLocation()*/);
			}
			// Process each matching item
			for (AccountMenuStructure ams : amss) {
				processExtend( ext, ams.getAccount().getAccountType().getKnownType(), ams.getPath());
			}
		}
	}
	
	
	/**
	 * Creates application file from trim.
	 * 
	 * @author valsaraj
	 * added on 05/03/2010
	 * @param trim
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> createApplications(Trim trim) throws Exception {
		Map<String, String> appFile = new HashMap<String, String>();
		String appXMLFilename = "pld.application.xml";
		String appXMLContent = "<application xmlns=\"urn:tolven-org:menuStructure:1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:tolven-org:menuStructure:1.0 http://tolven.org/xsd/menuStructure.xsd\">" +
								"<extends path=\"echr\">" +
								"<menu name=\"patients\" sequence=\"30\" title=\"Patients\" page=\"/five/patients.xhtml\" visible=\"true\">";
		try {
			String listName = ((ActEx) trim.getAct()).getRelationship().get("generalDetail").getAct().getObservation().getValues().get(0).getST().getValue();
			String listNameCopy = ((ActEx) trim.getAct()).getRelationship().get("generalDetail").getAct().getObservation().getValues().get(2).getST().getValue();
			
			try {
				if (! "".equals(listNameCopy)) {					
					listName = listNameCopy;
				}
			}
			catch (Exception e) {
				
			}
			
			appXMLContent += "<list name=\"" + toCamelCase(removeSpecialCharacters(listName)) + "\" title=\"" + toTitleCase(listName)+ "\" page=\"/five/list.xhtml\" visible=\"true\" drilldown=\"echr:patient\">";
			String nameCol = "<column name=\"Patient Name\" internal=\"string01,string02\" format=\"%s, %s\" reference=\"true\" width=\"20.0\"/>" +
								"<column name=\"Last\" internal=\"string01\" visible=\"false\">" 
								+ "<from>#{patient.lastName}</from>" 
								+ "</column>" 
								+ "<column name=\"First\" internal=\"string02\" visible=\"false\">" 
								+ "<from>#{patient.firstName}</from>" 
								+"</column>";	
			String ageCol = "<column name=\"DOB\" internal=\"date01\" format=\"d MMM yyyy\" width=\"7.0\" visible=\"false\">" 
							+ "<from>#{patient.dob}</from>" 
							+ "</column>" 
							+ "<column name=\"Age\" internal=\"date01\" format=\"age\" width=\"4.5\"/>";
			String raceCol = "<column name=\"Race\" internal=\"string03\" width=\"10.0\">" 
								+ "<from>#{patient.race}</from>" 
								+ "</column>";
			String genderCol = "<column name=\"Gender\" internal=\"string04\" width=\"6.0\">" 
								+ "<from>#{patient.sex}</from>" 
								+ "</column>";
			String zipCol = "<column name=\"Zip Code\" internal=\"string05\" width=\"10.0\">" 
							+ "<from>#{patient.homeZip}</from>" 
							+ "</column>";						
			int namePos = 0;
			String nameStaus = "";				
			int agePos = 0;
			String ageStatus = "";
			int racePos = 0;
			String raceStatus = "";
			int genderPos = 0;
			String genderStatus = "";
			int zipPos = 0;
			String zipStatus = "";
			String extension = "";
			
			try {
				ActEx listDisplay = (ActEx)((ActEx)trim.getAct()).getRelationship().get("listDisplay").getAct();
				
				try {
					namePos = Integer.parseInt(listDisplay.getRelationship().get("patientName").getAct().getObservation().getValues().get(0).getST().getValue());
					nameStaus = listDisplay.getRelationship().get("patientName").getAct().getObservation().getValues().get(1).getST().getValue();
				}
				catch (Exception e) {
					
				}
				
				try {
					agePos = Integer.parseInt(listDisplay.getRelationship().get("patientAge").getAct().getObservation().getValues().get(0).getST().getValue());
					ageStatus = listDisplay.getRelationship().get("patientAge").getAct().getObservation().getValues().get(1).getST().getValue();
				}
				catch (Exception e) {
					
				}
				
				try {
					racePos = Integer.parseInt(listDisplay.getRelationship().get("patientRace").getAct().getObservation().getValues().get(0).getST().getValue());
					raceStatus = listDisplay.getRelationship().get("patientRace").getAct().getObservation().getValues().get(1).getST().getValue();
				}
				catch (Exception e) {
					
				}
				
				try {
					genderPos = Integer.parseInt(listDisplay.getRelationship().get("patientGender").getAct().getObservation().getValues().get(0).getST().getValue());
					genderStatus = listDisplay.getRelationship().get("patientGender").getAct().getObservation().getValues().get(1).getST().getValue();
				}
				catch (Exception e) {
					
				}
				
				try {
					zipPos = Integer.parseInt(listDisplay.getRelationship().get("zipCode").getAct().getObservation().getValues().get(0).getST().getValue());
					zipStatus = listDisplay.getRelationship().get("zipCode").getAct().getObservation().getValues().get(1).getST().getValue();
				}
				catch (Exception e) {
					
				}
				
				for (int pos = 1; pos < 6; pos++) {
					if (namePos == pos && "Yes".equals(nameStaus)) {
						appXMLContent += nameCol;
					}
					else if (agePos == pos && "Yes".equals(ageStatus)) {
						appXMLContent += ageCol;
					}
					else if (racePos == pos && "Yes".equals(raceStatus)) {
						appXMLContent += raceCol;		
					}
					else if (genderPos == pos && "Yes".equals(genderStatus)) {
						appXMLContent += genderCol;
					}
					else if (zipPos == pos && "Yes".equals(zipStatus)) {
						appXMLContent += zipCol;
					}
				}
			
				try {
					extension = trim.getAct().getId().getIIS().get(0).getExtension();
				}
				catch (Exception e) {
					
				}
			}
			catch (Exception e) {
				logger.info(e.getMessage());
			}
			
			appXMLContent += "<column name=\"Summary\" internal=\"string06\" width=\"20.0\">" 
								+ "<output><a href=\"javascript:showSummary('#{patient.path}','" + extension + "');\">Summary</a></output>"
								+ "</column>";
			
			if (namePos == 0 && "Yes".equals(nameStaus)) {
				appXMLContent += nameCol;
			}
			
			if (agePos == 0 && "Yes".equals(ageStatus)) {
				appXMLContent += ageCol;
			}
			
			if (racePos == 0 && "Yes".equals(raceStatus)) {
				appXMLContent += raceCol;		
			}
			
			if (genderPos == 0 && "Yes".equals(genderStatus)) {
				appXMLContent += genderCol;
			}
			
			if (zipPos == 0 && "Yes".equals(zipStatus)) {
				appXMLContent += zipCol;
			}
		
			appXMLContent += "</list></menu></extends></application>";
			appFile.put(appXMLFilename, appXMLContent);
		}
		catch (Exception e) {
			throw new Exception("Invalid Data");
		}
		
		return appFile;
	}

	/**
	 * A remote-friendly method that creates a new Rule package from source and requires no special classes on the remote-end
	 * 
	 * @author valsaraj
	 * added on 05/17/2010
	 * @param packageName
	 * @param packageBody
	 * @return
	 * @throws RuntimeException
	 */
	public void loadRulePackage(String packageName, String packageBody) {
		try {
			createRulePackage(packageName, packageBody);
			getRuleBean().setSavedTime(null);
		}
		catch (Exception e) {
			String details = ExceptionFormatter.toSimpleString(e, "\n");
			throw new RuntimeException("Error loading rule package\n" + details );
		}
	}
	
	/**
	 * Create or update a Rule package. If the content is unchanged from the previous version, then
	 * a new package is not created.
	 * 
	 * @author valsaraj
	 * added on 05/17/2010
	 * @param packageName
	 * @param rule
	 * @return The RulePackage entity
	 */
	public RulePackage createRulePackage(String packageName, String rule) {
		logger.info("Compile rule package: " + packageName);		
		RulePackage rulePackage = getRuleBean().findActivePackage(packageName);
		String packageBody = null;
		
		if (rulePackage != null) {			
			packageBody = rulePackage.getPackageBody();
			String[] arr = rule.split("\n");
			
			if (getListNameCopy() != null) {
				arr[0] = arr[0].replace(getListName(), getListNameCopy());
			}
			
			String regExp = arr[0].replace("(", "\\(").replace(")", "\\)") + "\nsalience -5\nwhen\nMode\\( \\$thisAccount: thisAccount \\)\n(.*)\n(.*)\nthen\n(.*)\nend";
			packageBody = Pattern.compile(regExp).matcher(packageBody).replaceAll("") + '\n' + rule;
			rulePackage.setPackageVersion(rulePackage.getPackageVersion() + 1);
		}
		else {
			packageBody = "package pld\n" 
				+ "import java.util.*;\n"
				+ "import org.tolven.trim.*;\n"
				+ "import org.tolven.trim.ex.*;\n" 
				+ "import org.tolven.doc.entity.*;\n" 
				+ "import java.lang.Integer;\n" 
				+ "import org.tolven.app.entity.MenuStructure;\n" 
				+ "import org.tolven.app.entity.MenuData;\n" 
				+ "import org.tolven.app.bean.Mode;\n" 
				+ "import org.tolven.app.bean.Plan;\n" 
				+ "import org.tolven.core.entity.Status;\n" 
				+ "import org.tolven.model.Patient;\n" 
				+ "import org.tolven.trim.Observation;\n" 
				+ "import org.tolven.app.bean.Mode;\n"
				+ "import org.tolven.process.PatientListDesignerOperations;\n" 
				+ "global org.tolven.app.AppEvalAdaptor app;\n" 
				+ "global Date now;\n" 
				+ "import function org.tolven.trim.contstants.PatientImportance.isVIP;\n"
				+ rule;
			
			rulePackage = new RulePackage();
			rulePackage.setPackageName(packageName);
			rulePackage.setPackageVersion(1);
			rulePackage.setPackageStatus(ACTIVE_STATUS);
		}
		
		rulePackage.setPackageBody(packageBody);
		rulePackage.setTimestamp(app.getNow());
		Package initialPackage = new Package(packageName);	
		
		for (String knownType : compiler.extractPlaholderAccountType( packageBody )) {
			logger.info("Add " + knownType + " placeholders to " + packageName + " package");
			addKnownTypeToPackage(initialPackage, knownType);
		}
		
		Package pkg = compiler.compile(packageBody, initialPackage);
		byte[] serialized = compiler.serializePackage(pkg);		
		rulePackage.setCompiledPackage(serialized);
		em.persist(rulePackage);
		
		return rulePackage;
	}
	
	/**
	 * Creates rule package from trim.
	 * 
	 * @author valsaraj
	 * added on 05/03/2010
	 * @param trim
	 * @return
	 */
	public String createRule(Trim trim) throws Exception {
		String rule = null;
		String listName = getListName();
		
		if (listName != null) {
			List<ObservationValueSlot> patientAgeObs = ((ActEx) ((ActEx) trim.getAct()).getRelationship().get("patientAttributes").getAct()).getRelationship().get("patientAge").getAct().getObservation().getValues();
			String age = null;
			
				age = patientAgeObs.get(0).getST().getValue();
				String ageRel = null;
				ageRel = patientAgeObs.get(1).getCE().getCode();
			
			List<ObservationValueSlot> patientGenderObs = ((ActEx) ((ActEx) trim.getAct()).getRelationship().get("patientAttributes").getAct()).getRelationship().get("patientGender").getAct().getObservation().getValues();
			String gender = null;
			
				gender = patientGenderObs.get(0).getCE().getCode();
			
			String genderRel = null;
			
				genderRel = patientGenderObs.get(1).getCE().getCode();
			
			List<ObservationValueSlot> patientZipObs = ((ActEx) ((ActEx) trim.getAct()).getRelationship().get("patientAttributes").getAct()).getRelationship().get("zipCode").getAct().getObservation().getValues();
			
			String zip = null;
			
				zip = patientZipObs.get(0).getST().getValue();
			
			String zipRel = null;
			
				zipRel = patientZipObs.get(1).getCE().getCode();
			
			ActEx diagnosticCodesAct = (ActEx) ((ActEx) trim.getAct()).getRelationship().get("diagnosticCodes").getAct();
			String diagnosisIncludeCodes = null;
			
				diagnosisIncludeCodes = diagnosticCodesAct.getRelationship().get("diagnosisInclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String diagnosisInclude = diagnosticCodesAct.getRelationship().get("diagnosisInclude").isEnabled() ? "1" : "0";
			String problemIncludeCodes = null;
			
				problemIncludeCodes = diagnosticCodesAct.getRelationship().get("problemInclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String problemInclude = diagnosticCodesAct.getRelationship().get("problemInclude").isEnabled() ? "1" : "0";
			String medicationsIncludeCodes = null;
			
				medicationsIncludeCodes = diagnosticCodesAct.getRelationship().get("medicationsInclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String medicationsInclude = diagnosticCodesAct.getRelationship().get("medicationsInclude").isEnabled() ? "1" : "0";
			String allergiesIncludeCodes = null;
			
				allergiesIncludeCodes = diagnosticCodesAct.getRelationship().get("allergiesInclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String allergiesInclude = diagnosticCodesAct.getRelationship().get("allergiesInclude").isEnabled() ? "1" : "0";
			
			String proceduresIncludeCodes = null;
			proceduresIncludeCodes = diagnosticCodesAct.getRelationship().get("proceduresInclude").getAct().getObservation().getValues().get(0).getST().getValue();
			String proceduresInclude = diagnosticCodesAct.getRelationship().get("proceduresInclude").isEnabled() ? "1" : "0";
			
			String labtestsIncludeCodes = null;
			labtestsIncludeCodes = diagnosticCodesAct.getRelationship().get("labtestsInclude").getAct().getObservation().getValues().get(0).getST().getValue();
			String labtestsInclude = diagnosticCodesAct.getRelationship().get("labtestsInclude").isEnabled() ? "1" : "0";
			
			String diagnosisExcludeCodes = null;
			
				diagnosisExcludeCodes = diagnosticCodesAct.getRelationship().get("diagnosisExclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String diagnosisExclude = diagnosticCodesAct.getRelationship().get("diagnosisExclude").isEnabled() ? "1" : "0";
			String problemExcludeCodes = null;
			
				problemExcludeCodes = diagnosticCodesAct.getRelationship().get("problemExclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String problemExclude = diagnosticCodesAct.getRelationship().get("problemExclude").isEnabled() ? "1" : "0";
			String medicationsExcludeCodes = null;
			
				medicationsExcludeCodes = diagnosticCodesAct.getRelationship().get("medicationsExclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String medicationsExclude = diagnosticCodesAct.getRelationship().get("medicationsExclude").isEnabled() ? "1" : "0";
			String allergiesExcludeCodes = null;
			
				allergiesExcludeCodes = diagnosticCodesAct.getRelationship().get("allergiesExclude").getAct().getObservation().getValues().get(0).getST().getValue();
			
			String allergiesExclude = diagnosticCodesAct.getRelationship().get("allergiesExclude").isEnabled() ? "1" : "0";
			
			String proceduresExcludeCodes = null;
			proceduresExcludeCodes = diagnosticCodesAct.getRelationship().get("proceduresExclude").getAct().getObservation().getValues().get(0).getST().getValue();
			String proceduresExclude = diagnosticCodesAct.getRelationship().get("proceduresExclude").isEnabled() ? "1" : "0";
			
			String labtestsExcludeCodes = null;
			labtestsExcludeCodes = diagnosticCodesAct.getRelationship().get("labtestsExclude").getAct().getObservation().getValues().get(0).getST().getValue();
			String labtestsExclude = diagnosticCodesAct.getRelationship().get("labtestsExclude").isEnabled() ? "1" : "0";
			
			String accId = Long.toString(getApp().getAccount().getId());
			rule = "rule \"Add patients to " + listName + " (" + accId + ") List\"\n" +
					"salience -5\n" +
					"when\n" +
					"Mode( $thisAccount: thisAccount )\n" + 
					"$msList: MenuStructure( path == \"echr:patients:" + listName + "\" )\n" + 
					"$trim: Trim( name == \"reg/evn/patient\" || name == \"docclin/evn/diagnosis\" || name == \"docclin/evn/problem\" || name == \"docclin/evn/medication\" || name == \"obs/evn/fdbMedicationHistory\" || name == \"docclin/evn/allergy\" || name == \"pxDoc\" || name == \"labResultDoc\")\n"+
					"then\n" + 
					"if($thisAccount.getId()==" + accId + ") PatientListDesignerOperations.process(app,$msList,$trim,\"" + listName + "\",\"" + age + "\",\"" + ageRel + "\",\"" + gender + "\",\"" + genderRel + "\",\"" + zip + "\",\"" + zipRel + "\",\"" + diagnosisIncludeCodes + "\",\"" + diagnosisInclude + "\",\"" + problemIncludeCodes + "\",\"" + problemInclude + "\",\"" + medicationsIncludeCodes + "\",\"" + medicationsInclude + "\",\"" + allergiesIncludeCodes + "\",\"" + allergiesInclude + "\",\"" + proceduresIncludeCodes + "\",\"" + proceduresInclude + "\",\"" + labtestsIncludeCodes + "\",\"" + labtestsInclude + "\",\"" + diagnosisExcludeCodes + "\",\"" + diagnosisExclude + "\",\"" + problemExcludeCodes + "\",\"" + problemExclude + "\",\"" + medicationsExcludeCodes + "\",\"" + medicationsExclude + "\",\"" + allergiesExcludeCodes + "\",\"" + allergiesExclude + "\",\"" + proceduresExcludeCodes + "\",\"" + proceduresExclude + "\",\"" + labtestsExcludeCodes + "\",\"" + labtestsExclude + "\");\n" +
					"end";
			
			//"$msPlaceHolder: MenuStructure( role == \"placeholder\", (path == \"echr:patients:all\" ||  path == \"echr:patient:diagnosis\" || path == \"echr:patient:problem\" || path == \"echr:patient:medication\" || path == \"echr:patient:allergy\" || path == \"echr:patient:px\"))\n" +
			//"$md: MenuData( menuStructure == $msPlaceHolder, (string02=='active' || string02=='ACTIVE' || string03=='active' || string03=='ACTIVE' || actStatus=='active')  )\n" +

		}
		
		
		
		
		
		
		
		return rule;		
	}

	/**
	 * To add known types.
	 * 
	 * @param pkg
	 * @param knownType
	 */
	public void addKnownTypeToPackage(Package pkg, String knownType) {
		Account account = accountBean.findAccountTemplate( knownType );		
    	List<MenuStructure> menus = menuBean.findFullMenuStructure( account.getId());
    
    	for (MenuStructure ms : menus) {
    		if (MenuStructure.PLACEHOLDER.equals(ms.getRole())) {
    			FactTemplate ft = new PlaceholderFactTemplate(pkg,  ms); 
        		pkg.addFactTemplate(ft);
    		}
    	}
	}
	
	/**
	 * Process one resolved extend element. The account type and path are known and valid.
	 * Overrided method to add list dynamically and set scope.
	 * 
	 * @author valsaraj
	 * added on 05/03/2010
	 * modified on 05/18/2010 to set scope to list.
	 * @param ext
	 * @param knownType
	 * @param account
	 */
	protected void processExtend(Extends ext, String knownType, String path) {
		Account account;
		
		try {
			String principal = getPrincipal();
			long accountId = getApp().getAccount().getId();
			account = getAccountBean().findAccount(accountId);
			AccountRole ar = new AccountRole();
			ar.setAccount(account);			
			ar.setRole(principal);
			Set<AccountRole> aRoles = account.getAccountRoles();
			aRoles.add(ar);
			account.setAccountRoles(aRoles);
			AccountUser au = getAccountBean().findAccountUser(principal, accountId);
			Set<AccountUserRole> auRoles = au.getRoles();
			AccountUserRole aur = new AccountUserRole();
			aur.setAccountUser(au);
			aur.setRole(principal);
			auRoles.add(aur);
			au.setRoles(auRoles);
		} 
		catch (Exception e1) {
			throw new RuntimeException( "Unknown Account Type (" + knownType+ ") in extends path " + path, e1);
		}
		
		AccountMenuStructure msParent;
		
		try {
			MenuStructure ms = getMenuBean().findMenuStructure(account, path);
			msParent = ms.getAccountMenuStructure();
		} 
		catch (Exception e1) {
			throw new RuntimeException( "Unable to extend " + path, e1);
		}
		
		AccountMenuStructure msPlaceholder = msParent;
		
		// Find immediate placeholder ancestor, if any
		while (msPlaceholder != null) {
			if (MenuStructure.PLACEHOLDER.equals(msPlaceholder.getRole())) {
				break;
			}
			
			msPlaceholder = msPlaceholder.getParent();
		}
		
		if (ext.getBand() != null) {
			metadataLocal.processBand(account, ext.getBand(), msParent, msPlaceholder);
		} 
		else if (ext.getCalendar() != null) {
			metadataLocal.processCalendar(account, ext.getCalendar(), msParent, msPlaceholder);
		} 
		else if (ext.getInstance()!=null) {
			metadataLocal.processInstance(account, ext.getInstance(), msParent);
		} 
		else if (ext.getList() != null) {
			processList(account, ext.getList(), msParent, msPlaceholder);
		} 
		else if (ext.getMenu() != null) {
			metadataLocal.processMenu(account, ext.getMenu(), msParent, msPlaceholder);			
		} 
		else if (ext.getPlaceholder() != null) {
			try {
				MenuStructure msMenuRoot = getMenuBean().findMenuStructure(account, knownType);
				metadataLocal.processPlaceholder(msMenuRoot.getAccountMenuStructure(), ext.getPlaceholder(), msParent);
			} 
			catch (Exception e) {
				String placeholderName = ext.getPlaceholder().getName();
				throw new RuntimeException( "Unable to create placeholder " + placeholderName + " at " + path /* + " at "  + ext.sourceLocation()*/, e);
			}
		} 
		else if (ext.getPortal() != null) {
			metadataLocal.processPortal(account, ext.getPortal(), msParent, msPlaceholder);
		} 
		else if (ext.getPortlet() != null) {
			metadataLocal.processPortlet(account, ext.getPortlet(), msParent, msPlaceholder);
		} 
		else if (ext.getTimeline() != null) {
			metadataLocal.processTimeline(account, ext.getTimeline(), msParent, msPlaceholder);
		} 
		else if (ext.getTrimList() != null) {
			metadataLocal.processTrimList(account, ext.getTrimList(), msParent);
		} 
		else if (ext.getField() != null) {
			if (msParent.getRole().equals(MenuStructure.PLACEHOLDER)) {
				metadataLocal.processPlaceholderField(msParent, ext.getField());
			} 
			else {
				throw new RuntimeException("Field extension ("+ path + ") must be for a placeholder");
			}
		} 
		else if (ext.getColumn() != null) {
			if (msParent.getRole().equals(MenuStructure.LIST) ||
					msParent.getRole().equals(MenuStructure.PORTLET) ||
					msParent.getRole().equals(MenuStructure.ACTION) ||
					msParent.getRole().equals(MenuStructure.TRIMLIST) ||
					msParent.getRole().equals(MenuStructure.BAND) ||
					msParent.getRole().equals(MenuStructure.ENTRY)) {
				metadataLocal.processColumn(ext.getColumn(), msParent );
			} 
			else {
				throw new RuntimeException("Column extension ("+ path + ") must to a list, portlet, action, trimlist, ban, or entry");
			}
		}
	}

	/**
	 * Process a list pane from the supplied XML sub-tree.
	 * Overrided method to set scope.
	 * 
	 * @author valsaraj
	 * added on 05/18/2010
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return The created or update MenuStructure object
	 *  
	 */
	public AccountMenuStructure processList( Account account, org.tolven.menuStructure.List list, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder ) {
		AccountMenuStructure ms = metadataLocal.resolveMenuStructure(account, list, MenuStructure.LIST, msParent);
	
		if (list.getPage()!=null) {
			ms.setTemplate(list.getPage());
		}
		if (list.getQuery()!=null) {
			ms.setQuery( list.getQuery() );
		}
		if (list.getFilter()!=null) {
			ms.setFilter( list.getFilter() );
		}
		if (list.getUniqueKey()!=null) {
			ms.setUniqueKey( list.getUniqueKey());
		}
		if (list.getInitialSort()!=null) {
			ms.setInitialSort( list.getInitialSort() );
		}
		if (list.getTitle()!=null) {
			ms.setText(list.getTitle());
		}
		if (list.getDrilldown()!=null) {
			if (list.getDrilldown().startsWith(":")) {
				if (msPlaceholder!=null) {
					ms.setRepeating(metadataLocal.fullPath(msPlaceholder) + list.getDrilldown());
				} else {
					throw new IllegalStateException( "Missing parent placeholder for relative drilldown path" + list.getDrilldown());
				}
			} else {
				ms.setRepeating(list.getDrilldown());
			}
		}
		if (list.getVisible()!=null) {
			ms.setVisible(list.getVisible());
		}
		
		metadataLocal.processColumns( ms, list.getColumns());
		metadataLocal.processActions( account, ms, list.getActions() );
		metadataLocal.nominateDefaultSuffix( ms );
		boolean persistFlag = false;
		
		try {
			String lsName = list.getName();
			
			if (lsName != null && ! lsName.equals(getListName())) {
				if (! getListName().equals(getListNameCopy())) {
					if (msParent != null) {
						ms.setPath(msParent.getPath() + ":" + getListName());
					}
					else {
						ms.setPath(lsName);
					}
					
					ms.setNode(getListName());
					ms.setText(toTitleCase(getListName()));
					persistFlag = true;
				}
			}
		}
		catch (Exception e) {
			
		}
		
		try {
			String allowRoles;
			
			if (isPrivateList() && list.getName().equals(getListName())) {
				allowRoles = ms.getAllowRoles();
				allowRoles = (allowRoles != null ? allowRoles + "," + principal : principal);
				ms.setAllowRoles(allowRoles);
				persistFlag = true;
			}
			else {
				allowRoles = ms.getAllowRoles();
				
				if (allowRoles.contains(principal)) {
					allowRoles = allowRoles.replace(principal, "");
					ms.setAllowRoles(allowRoles);
					persistFlag = true;
				}
			}
		}
		catch (Exception e) {
			
		}
		
		if (persistFlag != false) {
			em.persist(ms);
		}
		
		return ms;
	}
	
	/**
	 * Convertes to title case.
	 * 
	 * @author valsaraj
	 * added on 05/03/2010
	 * @param str
	 * @return
	 */
	public String toTitleCase(String str){
		StringBuffer sb = new StringBuffer();     
		str = str.toLowerCase();
		StringTokenizer strTitleCase = new StringTokenizer(str);
		
		while(strTitleCase.hasMoreTokens()){
			String s = strTitleCase.nextToken();
			
			if (sb.length() > 0) {
				sb.append(" ");
			}
			
			sb.append(s.replaceFirst(s.substring(0,1), s.substring(0,1).toUpperCase()));
		}
		
		return sb.toString();
	}
	
	/**
	 * Removes special characters.
	 * 
	 * @author valsaraj
	 * added on 07/08/2010
	 * @param str
	 * @return
	 */
	public String removeSpecialCharacters(String str) {
		String regExp = "[^a-zA-Z0-9]+";

		return Pattern.compile(regExp).matcher(str).replaceAll("");
	}

	/**
	 * Converts to camel case.
	 * 
	 * @author valsaraj
	 * added on 05/03/2010
	 * @param str
	 * @return
	 */
	public String toCamelCase(String str){
		StringBuffer sb = new StringBuffer();     
		str = str.toLowerCase();
		StringTokenizer strTitleCase = new StringTokenizer(str);
		boolean skipFirst = true;
		
		while(strTitleCase.hasMoreTokens()) {
			String s = strTitleCase.nextToken();
			
			if (! skipFirst) {				
				sb.append(s.replaceFirst(s.substring(0,1), s.substring(0,1).toUpperCase()));
			}
			else {
				sb.append(s);
				skipFirst = false;
			}
		}
		
		return sb.toString();
	}
	
	public RulesLocal getRuleBean() {
		return ruleBean;
	}
	
	public void setRuleBean(RulesLocal ruleBean) {
		this.ruleBean = ruleBean;
	}
	
	public String getListName() {
		return listName;
	}
	
	public void setListName(String listPath) {
		this.listName = listPath;
	}

	public AppEvalAdaptor getApp() {
		return app;
	}

	public void setApp(AppEvalAdaptor app) {
		this.app = app;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public boolean isPrivateList() {
		return privateList;
	}

	public void setPrivateList(boolean privateList) {
		this.privateList = privateList;
	}

	public String getListNameCopy() {
		return listNameCopy;
	}

	public void setListNameCopy(String listNameCopy) {
		this.listNameCopy = listNameCopy;
	}
	public ApplicationMetadataLocal getMetadataLocal() {
		return metadataLocal;
	}

	public void setMetadataLocal(ApplicationMetadataLocal metadataLocal) {
		this.metadataLocal = metadataLocal;
	}

	public AccountDAOLocal getAccountBean() {
		return accountBean;
	}

	public void setAccountBean(AccountDAOLocal accountBean) {
		this.accountBean = accountBean;
	}

	public MenuLocal getMenuBean() {
		return menuBean;
	}

	public void setMenuBean(MenuLocal menuBean) {
		this.menuBean = menuBean;
	}
}