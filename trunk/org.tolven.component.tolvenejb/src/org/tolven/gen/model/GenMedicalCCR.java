package org.tolven.gen.model;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.tolven.ccr.ActorReferenceType;
import org.tolven.ccr.ActorType;
import org.tolven.ccr.CodedDescriptionType;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.ccr.IDType;
import org.tolven.ccr.InstructionType;
import org.tolven.ccr.Location;
import org.tolven.ccr.PersonNameType;
import org.tolven.ccr.StructuredProductType;
import org.tolven.ccr.ActorType.Address;
import org.tolven.ccr.ActorType.Organization;
import org.tolven.ccr.ContinuityOfCareRecord.From;
import org.tolven.ccr.ex.AlertTypeEx;
import org.tolven.ccr.ex.ContinuityOfCareRecordEx;
import org.tolven.ccr.ex.DateTimeTypeEx;
import org.tolven.ccr.ex.EncounterTypeEx;
import org.tolven.ccr.ex.ProblemTypeEx;
import org.tolven.ccr.ex.ResultTypeEx;
import org.tolven.ccr.ex.StructuredProductTypeEx;
import org.tolven.ccr.ex.TestTypeEx;
import org.tolven.doc.entity.CCRException;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.gen.util.DateUtil;

public class GenMedicalCCR extends GenMedical {
	
	private ContinuityOfCareRecordEx ccr;
	//private org.tolven.ccr.ObjectFactory ccrFactory;
	private org.tolven.ccr.ex.CCRFactory ccrFactory;
	private int nextId = 0;
	
	/**
	 * Construct a new medical data generator for a specific patient.
  	 * @param patient Contains the parent menu data (the patient) for the problems we'll be creating
   	 * @param now
	 * @throws CCRException 
	 * @throws JAXBException 
   	 */
	public GenMedicalCCR ( Date now, int startYear) {
		super( now, startYear);
		//this.ccrFactory = org.tolven.ccr.ObjectFactory.getInstance();
		this.ccrFactory = org.tolven.ccr.ex.CCRFactory.getInstance();
	}
	private String[] sources = {"MC-Lab-C35", "Wellington", "City Lab", "MC-Lab-M77"};
    public ContinuityOfCareRecordEx generate( ) {
		ccr = ccrFactory.createContinuityOfCareRecord();
		ccr.setCCRDocumentObjectID( getDocumentId() );
		From from = ccrFactory.createContinuityOfCareRecordFrom();
		ccr.setVersion("V1.0");
		ActorType sourceActor = addActor();
		String sourceSystemName = sources[getRng().nextInt(0, sources.length-1)]; 
		Organization sourceOrg = ccrFactory.createActorTypeOrganization();
		sourceOrg.setName(sourceSystemName);
		sourceActor.setOrganization(sourceOrg);
		ActorReferenceType sourceActorRef = ccrFactory.createActorReferenceType();
		sourceActorRef.setActorID(sourceActor.getActorObjectID());
		from.getActorLink().add(sourceActorRef);
		ccr.setFrom(from);
		
		// We're the callback that the scenarios will call to create ccr data.
		for (GenSource gs: getCriteria()) {
			gs.generate( this );
		}
		return ccr;
    }

    public ContinuityOfCareRecordEx generate( String scenario) {
		ccr = ccrFactory.createContinuityOfCareRecord();
		ccr.setCCRDocumentObjectID( getDocumentId() );
		ccr.setVersion("V1.0");
		From from = ccrFactory.createContinuityOfCareRecordFrom();
		ActorType sourceActor = addActor();
		String sourceSystemName = "MC-Lab-C35"; 
		Organization sourceOrg = ccrFactory.createActorTypeOrganization();
		sourceOrg.setName(sourceSystemName);
		sourceActor.setOrganization(sourceOrg);
		ActorReferenceType sourceActorRef = ccrFactory.createActorReferenceType();
		sourceActorRef.setActorID(sourceActor.getActorObjectID());
		from.getActorLink().add(sourceActorRef);
		ccr.setFrom(from);
		Date startTime = new Date( getNow().getTime()-(5*DateUtil.YEARS));
		new PatientGen(getRng()).apply(this, startTime);
		new DiabetesGen(getRng()).apply(this, startTime);
		return ccr;
    }
    public ActorType addActor() {
		ActorType actor = new ActorType();
		nextId++;
		actor.setActorObjectID(String.format(Locale.US, "AC%06d", nextId));
		// Remember that we're storing actors at the Tolven document level until marshall time
		// when we put it in the CCR document.
		if (ccr.getActors()==null) ccr.setActors(new ContinuityOfCareRecord.Actors());
		ccr.getActors().getActor().add(actor);
		return actor;
	}

    /**
     * Add an ID to actor
     */
    public IDType createIDType( String issuedBy, String type, String id ) {
		IDType idType = new IDType();
		// Add issuer
    	if (issuedBy!=null) {
        	ActorType orgActor = addActor();
    		Organization org = ccrFactory.createActorTypeOrganization();
    		org.setName(issuedBy);
    		orgActor.setOrganization(org);
    		// Refer to the issuing organization
    		ActorReferenceType orgActorRef = ccrFactory.createActorReferenceType();
    		orgActorRef.setActorID(orgActor.getActorObjectID());
    		idType.setIssuedBy(orgActorRef);
    	}
    	// Add type
    	if (type!=null ) {
    		CodedDescriptionType idTypeType = new CodedDescriptionType();
    		idTypeType.setText(type);
    		idType.setType(idTypeType);
    	}
    	// Add id itself
		idType.setID(id);
		return idType;
    }
    
   	/**
   	 * A nominal callback function to create the patient details in our target document.
   	 * When patients are generated, we just make up a random number as a
   	 * patient medicalrecordNumber - there may even be duplicates which we'll chalk up to
   	 * reality.
   	 * @param startYear
   	 */
   	public void generateNewPatient( Date eventTime )  {
		ContinuityOfCareRecord.Patient pat = new ContinuityOfCareRecord.Patient();
   		// Create patient actor
   		ActorType patActor = addActor();

		if (getPatient()==null) {
			// Add a generated MRN and SSN
			patActor.getIDs().add(createIDType( 
					"TolvenGen",
					"MedicalRecordNumber", 
					Long.toString(getRng().nextLong(10000000,99999999)) ));

			patActor.getIDs().add(createIDType( 
					"TolvenGen",
					"SSN", 
						Integer.toString(getRng().nextInt(100,999)) + "-" +
						Integer.toString(getRng().nextInt(10,99)) + "-" +
						Integer.toString(getRng().nextInt(1000,9999)) 
					));
			
			pat.setActorID(patActor.getActorObjectID());
			ccr.getPatient().add(pat);
				
			ActorType.Person person = new ActorType.Person();
			ActorType.Person.Name name = new ActorType.Person.Name();
			PersonNameType pnt = new PersonNameType();
			pnt.getGiven().add(getVp().getFirst());
			pnt.getMiddle().add(getVp().getMiddle());
			pnt.getFamily().add(getVp().getLast());
			name.setCurrentName(pnt);
			person.setName(name);
			patActor.setPerson(person);
			if (getVp().getMaiden()!=null) {
				PersonNameType pntb = new PersonNameType();
				pntb.getGiven().add(getVp().getFirst());
				pntb.getMiddle().add(getVp().getMiddle());
				pntb.getFamily().add(getVp().getMaiden());
				name.setBirthName(pntb);
			}
			FamilyUnit family = getFamily();
			if (family!=null) {
				Address address = new Address();
				address.setLine1(family.getAddress());
				address.setCity(family.getCity());
				address.setState(family.getState());
				address.setPostalCode(family.getZip());
				address.setCountry("USA");
				patActor.getAddress().add(address);
			}
			
			if (getVp().getDob()!=null) {
				DateTimeTypeEx dob = new DateTimeTypeEx();
				dob.setDateValue( getVp().getDob() );
				person.setDateOfBirth(dob);
			}

			if ("M".equals(getVp().getGender())) {
				CodedDescriptionType genderCode = new CodedDescriptionType();
				genderCode.setText("Male");
				person.setGender(genderCode);
				setMale(true);
			}
			if ("F".equals(getVp().getGender())) {
				CodedDescriptionType genderCode = new CodedDescriptionType();
				genderCode.setText("Female");
				person.setGender(genderCode);
				setFemale(true);
			}
		} else {
			// Existing patient from MenuData
			patActor.getIDs().add(createIDType( 
					"Tolven Health",
					"TolvenPatient", 
					getPatient().getPath() ));
			pat.setActorID(patActor.getActorObjectID());
			ccr.getPatient().add(pat);
				
			ActorType.Person person = new ActorType.Person();
			ActorType.Person.Name name = new ActorType.Person.Name();
			PersonNameType pnt = new PersonNameType();
			pnt.getGiven().add(getPatient().getString02());
			pnt.getMiddle().add(getPatient().getString03());
			pnt.getFamily().add(getPatient().getString01());
			name.setCurrentName(pnt);
			person.setName(name);
			patActor.setPerson(person);
			if (getPatient().getDate01()!=null) {
				DateTimeTypeEx dob = new DateTimeTypeEx();
				dob.setDateValue(getPatient().getDate01() );
				person.setDateOfBirth(dob);
			}
			CodedDescriptionType genderCode = new CodedDescriptionType();
			genderCode.setText(getPatient().getString04());
			person.setGender(genderCode);
			if ("Male".equals(getPatient().getString04())) {
				setMale(true);
			} else {
				setFemale(true);
			}
		}
   	}

   	/**
   	 * Generate one problem. CCR supports episodes, we could but don't take that into account. Herpes
   	 * began on a certain date and then episodes periodically from then on out.
   	 */
   	public void generateProblem(  Date onset, String problem, String status ) {
   		ProblemTypeEx pt = ccrFactory.createProblemType();
   		// Set the type
   		pt.setTypeText(ProblemTypeEx.PROBLEM_TYPE);
   		pt.setDateTimeType(ProblemTypeEx.DATE_OF_ONSET, onset);
   		pt.setStatusText(status);
   		pt.setDescriptionText(problem);
   		if (ccr.getBody()==null) ccr.setBody( ccrFactory.createContinuityOfCareRecordBody());
   		if (ccr.getBody().getProblems()==null) ccr.getBody().setProblems(ccrFactory.createContinuityOfCareRecordBodyProblems());
   		ccr.getBody().getProblems().getProblem().add(pt);
   	}

   	/**
   	 * Generate one allergy.
   	 */
   	public void generateAllergy(  Date onset, String allergy, String status ) {
   		AlertTypeEx alert = ccrFactory.createAlertType();
   		// Set the type
   		alert.setTypeText("Allergy");
   		alert.setDateTimeType("Start Date", onset);
   		alert.setStatusText(status);
   		alert.setDescriptionText(allergy);
   		if (ccr.getBody()==null) ccr.setBody( ccrFactory.createContinuityOfCareRecordBody());
   		if (ccr.getBody().getAlerts()==null) ccr.getBody().setAlerts(ccrFactory.createContinuityOfCareRecordBodyAlerts());
   		ccr.getBody().getAlerts().getAlert().add(alert);
   	}

   	/**
   	 * Generate a lab result. We ignore requests before the start date presuming that if the practice wasn't in
   	 * business, then the results wouldn't be in this record.
   	 */
   	public void generateLabResult( Date testDate, String battery, List<LabTest> tests) {
   		// Anything before the start time we ignore.
   		GregorianCalendar effectiveCal = new GregorianCalendar(  );
   		effectiveCal.setTime( testDate );
   		if (effectiveCal.get(GregorianCalendar.YEAR) < this.getStartYear()) return;

   		// Create the overall result
   		ResultTypeEx rt = ccrFactory.createResultType();
   		// Set collection time
   		rt.setDateTimeType(ResultTypeEx.COLLECTION_DATE, testDate);
   		// Set the type
   		rt.setTypeText(ResultTypeEx.CHEMISTRY_TYPE);
   		// Name of the battery (result)
   		rt.setDescriptionText(battery);
   		
   		CodedDescriptionType typeCD = ccrFactory.createCodedDescriptionType();
   		typeCD.setText(ResultTypeEx.CHEMISTRY_TYPE);
   		rt.setType(typeCD);

   		// Individual results
   		for (LabTest test : tests) {
   	   		TestTypeEx tt = ccrFactory.createTestType();
   	   		// Type = result
   	   		tt.setTypeText(TestTypeEx.RESULT_TYPE);
   	   		// Test name
   	   		tt.setDescriptionText(test.getTestName());
   	   		// ResultType - containing the value + unit
   	   		org.tolven.ccr.TestResultType testResultTypeCCR = ccrFactory.createTestResultType();
   	   		//org.tolven.ccr.RateType.Units unitsCCR = ccrFactory.createRateTypeUnits();
   	   		org.tolven.ccr.NormalType.Units unitsCCR = ccrFactory.createNormalTypeUnits(); 	   		   	   		
   	   		unitsCCR.setUnit(test.getUnits());
   	   		testResultTypeCCR.setValue(test.getStringValue());
   	   		testResultTypeCCR.setUnits(unitsCCR);
   	   		// Add result type to test
   	   		tt.setTestResult(testResultTypeCCR);
   	   		// Add test to overall result
   	   		rt.getTest().add(tt);
   		}
   		if (ccr.getBody()==null) ccr.setBody( ccrFactory.createContinuityOfCareRecordBody());
   		if (ccr.getBody().getResults()==null) ccr.getBody().setResults(ccrFactory.createContinuityOfCareRecordBodyResults());
   		// add result to CCR body
   		ccr.getBody().getResults().getResult().add(rt);
   	}

   	/**
   	 * Generate one rad result, but nothing before we started the practice
   	 * @return
   	 */
   	public void generateRadResult( Date effective, String test, String result, String interpretation ) {
   		// Anything before the start time we ignore.
   		GregorianCalendar effectiveCal = new GregorianCalendar(  );
   		effectiveCal.setTime( effective );
   		if (effectiveCal.get(GregorianCalendar.YEAR) < this.getStartYear()) return;

   		// Create the overall result
   		ResultTypeEx rt = ccrFactory.createResultType();
   		// Set collection time
   		rt.setDateTimeType(ResultTypeEx.COLLECTION_DATE, effective);
   		// Name of the result
   		rt.setDescriptionText(test);
   		
   		CodedDescriptionType typeCD = ccrFactory.createCodedDescriptionType();
   		typeCD.setText(ResultTypeEx.IMAGING_X_RAY_TYPE);
   		rt.setType(typeCD);
   		
   		// Just one test for radiology
   		TestTypeEx tt = ccrFactory.createTestType();
   		// Type = result
   		tt.setTypeText(TestTypeEx.RESULT_TYPE);
   		org.tolven.ccr.TestResultType testResultTypeCCR = ccrFactory.createTestResultType();
   		org.tolven.ccr.CodedDescriptionType testDescriptionCCR = ccrFactory.createCodedDescriptionType();
   		testDescriptionCCR.setText(result);
   		testResultTypeCCR.getDescription().add(testDescriptionCCR);
   		// Add test result to the test
   		tt.setTestResult(testResultTypeCCR);
   		// Add the test to the result
   		rt.getTest().add(tt);
   		if (ccr.getBody()==null) ccr.setBody( ccrFactory.createContinuityOfCareRecordBody());
   		if (ccr.getBody().getResults()==null) ccr.getBody().setResults(ccrFactory.createContinuityOfCareRecordBodyResults());
   		// Add result to the CCR Body
   		ccr.getBody().getResults().getResult().add(rt);
   	}

   	/**
   	 * Generate one appointment (typically in the future)
   	 */
   	public void generateAppointment( Date effective, String purpose, String location, String practitioner, String instructions ) {
   		if (ccr.getBody()==null) ccr.setBody( ccrFactory.createContinuityOfCareRecordBody());
   		if (ccr.getBody().getEncounters()==null) ccr.getBody().setEncounters(ccrFactory.createContinuityOfCareRecordBodyEncounters());
   		// Add encounter to the CCR Body
   		EncounterTypeEx enc = ccrFactory.createEncounterType();
   		// Type = result
   		enc.setTypeText("Physician Office Visit");
   		// Set collection time
   		enc.setDateTimeType("Encounter DateTime", effective);
   		enc.setDescriptionText(purpose);
   		if (location!=null) {
   			if (enc.getLocations()==null) enc.setLocations(ccrFactory.createLocations());
   			Location loc = ccrFactory.createLocation();
   			CodedDescriptionType locDesc = ccrFactory.createCodedDescriptionType();
   			locDesc.setText(location);
   			loc.setDescription(locDesc);
   			enc.getLocations().getLocation().add(loc);
   		}
   		ccr.getBody().getEncounters().getEncounter().add(enc);
   	}

   	/**
   	 * Generate a medication
   	 * @return
   	 */
   	public void generateMedication( Date effective, String name, String dispense, String instuctions, String refills ) {
   		if (ccr.getBody()==null) ccr.setBody( ccrFactory.createContinuityOfCareRecordBody());
   		if (ccr.getBody().getMedications()==null) ccr.getBody().setMedications(ccrFactory.createContinuityOfCareRecordBodyMedications());
   		// Add medication to the CCR Body
   		StructuredProductTypeEx med = ccrFactory.createStructuredProductType();
   		med.setDateTimeType("Start Date", effective);
   		med.setTypeText("Medication");
   		//med.setDescriptionText(name);
		med.setProductBrandNameText(name,"Longs Drugs");
   		med.setStatusText("Active");
   		// Instructions
   		CodedDescriptionType descriptionField = ccrFactory.createCodedDescriptionType();
   		descriptionField.setText(instuctions);
   		InstructionType instruction = ccrFactory.createInstructionType();
   		instruction.setText(instuctions);
   		StructuredProductType.PatientInstructions instructions = ccrFactory.createStructuredProductTypePatientInstructions();
   		instructions.getInstruction().add(instruction);
   		med.setPatientInstructions(instructions);
   		// Refills
   		StructuredProductType.Refills.Refill refill = ccrFactory.createStructuredProductTypeRefillsRefill();
   		refill.getNumber().add(new BigInteger( refills));
   		StructuredProductType.Refills refillsField = ccrFactory.createStructuredProductTypeRefills();
   		refillsField.getRefill().add(refill);
   		med.setRefills(refillsField);
   		ccr.getBody().getMedications().getMedication().add(med);
   	}

}
