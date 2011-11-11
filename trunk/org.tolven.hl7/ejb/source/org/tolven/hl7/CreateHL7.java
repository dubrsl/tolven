package org.tolven.hl7;

import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.InitialContext;

import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.core.ActivationLocal;
import org.tolven.core.DemographicsLocal;
import org.tolven.trim.ex.HL7DateFormatUtility;

import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;

public abstract class CreateHL7 implements CreateHL7Local {
	
	private Map<String, String> stateCodeMap = null;
	
	@EJB protected MenuLocal menuBean;	
	@EJB private ActivationLocal activationBean;
	@EJB private DemographicsLocal demographicsLocal; 
	
	public CreateHL7(){
        try {
		InitialContext ctx = new InitialContext();
        if (menuBean==null) {
            menuBean = (MenuLocal) ctx.lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal");
		}
		if (activationBean==null) {
            activationBean = (ActivationLocal) ctx.lookup("java:global/tolven/tolvenEJB/ActivationBean!org.tolven.core.ActivationLocal");
		}		
		if (demographicsLocal==null) {
            demographicsLocal = (DemographicsLocal) ctx.lookup("java:global/tolven/tolvenEJB/DemographicsBean!org.tolven.core.DemographicsLocal");
		}			
		this.stateCodeMap = demographicsLocal.retrieveAllStates();
        }catch (Exception e) {
			throw new RuntimeException("Exception in CreateHL7",e);
		}
	}
	protected void getMenuBean() {
		}
	
	/**
	 * MSH Segment has the following elements
	  	1. Field Separator
	  		|
	  	2. Encoding Characters
	  		^~\&
	  	3. Sending Application
	  		3.1 Namespace ID
	  	4. Sending Facility
	  		4.1 Namespace ID
	  	5. Receiving Application
	  		5.1 Namespace ID
	  	6. Receiving Facility
	  		6.1 Namespace ID
	  	7. Date/Time of Message
	  		7.1 Time of an Event
	  	8. Security
	  		8.1 Value
	  	9. Message Type
	  		9.1 Message Type
	  		9.2 Trigger Event
	  		9.3 Message Structure
	  	10.Message Control ID
	  		10.1 Value	  		
	  	11.Processing ID
	  		11.1 Processing ID
	  	12.Version ID
	  		12.1 Version ID
	 	
	 */
	
	public void createMSHSegment(MSH msh,MenuData menuData) {
		try {
			// 1. Set Field Separator
			msh.getFieldSeparator().setValue("|");
			// 2. Set Encoding characters
			msh.getEncodingCharacters().setValue("^~\\&");
			// 3. Set Sending Application
			msh.getSendingApplication().getNamespaceID().setValue("ViSolve");
			// 4. Set Sending Facility
			msh.getSendingFacility().getNamespaceID().setValue("VicarePlus");			
			if(menuData.getPath().contains("diagnosis")){
				// 4.2 Sending Facility ID
				msh.getSendingFacility().getUniversalID().setValue("1164425112");
				// 4.3 Sending Facility Type
				msh.getSendingFacility().getUniversalIDType().setValue("NPI");
			}
			// 7. Set Date/Time of Message - Time of an Event
			msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(HL7DateFormatUtility.formatHL7TSFormatL14Date(new Date()));
			// 8. Set Security
			// 10. Set Message Control ID
			msh.getMessageControlID().setValue("UNIQUE01");
			// 11. Set Processing ID
			msh.getProcessingID().getProcessingID().setValue("P");
			// 12. Set Version ID
			msh.getVersionID().getVersionID().setValue("2.3.1");		
			
		} catch (Exception e) {			
			throw new RuntimeException("Exception in createMSHSegment",e);
		}
	}	
	
	abstract void createMSHSegmentType();

	/**
	 * PID segment has the following elements
	  	1. Set ID
	  		1.1 Value
		2. Patient ID
			2.1 ID
		3. Patient Identifier List
			3.1 ID
			3.2 Check Digit
			3.3 Code Identifying the Check Digit Scheme Employed
			3.4 Assigning Authority
			3.5 Identifier Type Code			
		4. Alternate Patient ID
			4.1 ID
		5. Patient Name
			5.1 Family Last Name
			5.2 Given Name
		6. Mother's Maiden Name
			6.1 Family Last Name
		7. Date/Time of Birth
			7.1 Time of an Event
		8. Sex
			8.1 Value
		9. Patient Alias
			9.1 Family Last Name
		10.Race
			10.1 Identifier 
			10.2 Text
			10.3 Name of Coding System
		11.Patient Address
			11.1 Street Address
			11.2 Other Designation
			11.3 City
			11.4 State or Province
			11.5 Zip or Postal Code
			11.6 Country
			11.7 Address Type
		12.County Code
			12.1 Value
		13.Phone Number - Home
			13.1 Any Text 
			13.2 Telecommunication Use Code
			13.3 Telecommunication Equipment Type
			13.4 Email Address
			13.5 Country Code
			13.6 Area/City Code
			13.7 Phone Number
		14.Phone Number - Business
			14.1 Any Text
		15.Primary Language
			15.1 Identifier
		16.Marital Status
			16.1 Identifier
		17.Religion
			17.1 Identifier
		18.Patient Account Number
			18.1 ID
		19.SSN Number - Patient
			19.1 Value
		20.Driver's License Number - Patient
			20.1 Driver's License Number
		21.Mother's Identifier
			21.1 ID
		22.Ethnic Group
			22.1 Identifier
			22.2 Text
			22.3 Name of Coding System
	 */
	
	
	public void createPIDSegment(PID pid, MenuData mdPatient) {
		try {
		// 1. Set ID
			  pid.getSetIDPID().setValue("1");	
		//3. Patient Identifier List
			//3.1 ID
			pid.getPatientIdentifierList(0).getID().setValue(((Long)mdPatient.getId()).toString());
			//3.5 Identifier Type Code
			Object identifierList = mdPatient.getField("mrn");
			if(identifierList != null){
				pid.getPatientIdentifierList(0).getIdentifierTypeCode().setValue(identifierList.toString());
			}
		// 4. Alternate Patient ID
		// 5. Patient Name
			Object firstName = mdPatient.getField("firstName");
			if(firstName != null){
				pid.getPatientName(0).getGivenName().setValue(firstName.toString());
			}
			Object middleName = mdPatient.getField("middleName");
			if(middleName!=null) {
				pid.getPatientName(0).getMiddleInitialOrName().setValue(middleName.toString());
			}
			Object lastName = mdPatient.getField("lastName");
			if(lastName!=null) {
				pid.getPatientName(0).getFamilyLastName().getFamilyName().setValue(lastName.toString());
			}
		// 6. Mother's Maiden Name
		// 7. Date/Time of Birth
			Date dob = (Date)mdPatient.getField("dob");
			if(dob != null){
				pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue(HL7DateFormatUtility.formatHL7TSFormatL14Date(dob));
			}
		// 8. Sex
			Object sex = mdPatient.getField("sex");
			if(sex!=null) {
				if(sex.toString().equalsIgnoreCase("Male")){
					pid.getSex().setValue("M");
				}else if(sex.toString().equalsIgnoreCase("Female")){
					pid.getSex().setValue("F");	
				}
			}
		// 9. Patient Alias			
		// 10.Race
			Object race = mdPatient.getExtendedField("race");
			String raceIdentifier,raceText;
			if(race!=null) {
				if(race.toString().equalsIgnoreCase("[White]")){				
					raceIdentifier = "W";
					raceText = "White";
				} else if(race.toString().equalsIgnoreCase("[Black or African American]")){
					raceIdentifier = "B";
					raceText = "Black or African American";
				} else if(race.toString().equalsIgnoreCase("[Asian]")){
					raceIdentifier = "A";
					raceText = "Asian or Pacific Islander";
				} else if(race.toString().equalsIgnoreCase("[American Indian or Alaska Native]")){
					raceIdentifier = "I";
					raceText = "American Indian or Alaska Native";
				} else{
					raceIdentifier = "O";
					raceText = "Other";
				}
			} else {
				raceIdentifier = "U";
				raceText = "Unknown";
			}
			pid.getRace(0).getIdentifier().setValue(raceIdentifier);
			pid.getRace(0).getText().setValue(raceText);
			pid.getRace(0).getNameOfCodingSystem().setValue("hl70005");
		/* 	11.Patient Address
				11.1 Street Address
				11.2 Other Designation
				11.3 City
				11.4 State or Province
				11.5 Zip or Postal Code
				11.6 Country
				11.7 Address Type	*/
			Object address = mdPatient.getField("homeAddr1");
			String homeAddr="";
			if(address!=null) {
				homeAddr = address.toString()+" ";
			}
			address = mdPatient.getField("homeAddr2");
			if(address!=null) {
				homeAddr += address.toString();
			}
			pid.getPatientAddress(0).getStreetAddress().setValue(homeAddr);
			Object city = mdPatient.getField("homeCity");
			if(city!=null) {
				pid.getPatientAddress(0).getCity().setValue(city.toString());	
			}
			Object state = mdPatient.getField("homeState");
			if(state!=null) {
				pid.getPatientAddress(0).getStateOrProvince().setValue(stateCodeMap.get(state.toString()));	
			}
			Object zip = mdPatient.getField("homeZip");
			if(zip!=null) {
				pid.getPatientAddress(0).getZipOrPostalCode().setValue(zip.toString());
			}
			Object country = mdPatient.getField("homeCountry");
			if(country!=null) {
				pid.getPatientAddress(0).getCountry().setValue(country.toString());	
			}
			pid.getPatientAddress(0).getAddressType().setValue("H");
			
		// 12.County Code
			Object county = mdPatient.getField("homeCounty");
			if(county!=null) {
				pid.getCountyCode().setValue(county.toString());	
			}
		/*	13.Phone Number - Home
			13.1 Any Text 
			13.2 Telecommunication Use Code
			13.3 Telecommunication Equipment Type
			13.4 Email Address
			13.5 Country Code
			13.6 Area/City Code
			13.7 Phone Number	*/
			Object phoneHome = mdPatient.getField("homeTelecom");
			if(phoneHome!=null) {
				pid.getPhoneNumberHome(0).getTelecommunicationUseCode().setValue("PRN");
				pid.getPhoneNumberHome(0).getPhoneNumber().setValue(phoneHome.toString().substring(3));
				pid.getPhoneNumberHome(0).getAreaCityCode().setValue(phoneHome.toString().substring(0,3));
			}
		// 14.Phone Number - Business
			Object workHome = mdPatient.getField("workTelecom");
			if(workHome!=null) {
				pid.getPhoneNumberBusiness(0).getAnyText().setValue(workHome.toString());
			}
		// 15.Primary Language
		// 16.Marital Status
		// 17.Religion			
		// 18.Patient Account Number			
		// 19.SSN Number - Patient
			Object ssn = mdPatient.getField("ssn");
			if(ssn!=null) {
				pid.getSSNNumberPatient().setValue(ssn.toString());
			}
		// 20.Driver's License Number - Patient
		// 21.Mother's Identifier
		// 22.Ethnic Group
			Object ethnicityObject = mdPatient.getField("ethnicity");
			String ethnicIdentifier="",ethnicText="";
			if(ethnicityObject!=null) {
				String ethnicity = ethnicityObject.toString();
				if(ethnicity.equalsIgnoreCase("Hispanic or Latino")) {
					ethnicIdentifier = "H";
					ethnicText = "Hispanic";
				} else if(ethnicity.toString().equalsIgnoreCase("Not Hispanic or Latino")){
					ethnicIdentifier = "N";
					ethnicText = "Non-Hispanic";
				}
			} else {
				ethnicIdentifier = "U";
				ethnicText = "Unknown";
			}
			pid.getEthnicGroup(0).getIdentifier().setValue(ethnicIdentifier);
			pid.getEthnicGroup(0).getText().setValue(ethnicText);
			pid.getEthnicGroup(0).getNameOfCodingSystem().setValue("hl70189");
			
			// 29. Date/Time of Death
			/*Object deathDate = mdPatient.getField("deathDate");				
			if(deathDate!=null) {
				Date dod = (Date)deathDate;
				pid.getPatientDeathDateAndTime().getTimeOfAnEvent().setValue(HL7DateFormatUtility.formatHL7TSFormatL14Date(dod));
				// 30. Death Indicator to YES
				pid.getPatientDeathIndicator().setValue("Y");
			} else {
				// 30. Death Indicator to NO
				pid.getPatientDeathIndicator().setValue("N");
			}*/
		}catch(Exception e){
			throw new RuntimeException("Exception in createPIDSegment"+e);
		}
	}	
}