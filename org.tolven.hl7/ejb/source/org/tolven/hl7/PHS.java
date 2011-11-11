package org.tolven.hl7;

import java.util.Date;

import org.tolven.app.entity.MenuData;
import org.tolven.trim.ex.HL7DateFormatUtility;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.message.ADT_A01;
import ca.uhn.hl7v2.model.v231.message.ADT_A04;
import ca.uhn.hl7v2.model.v231.message.ADT_A08;
import ca.uhn.hl7v2.model.v231.segment.DG1;
import ca.uhn.hl7v2.model.v231.segment.EVN;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.PV1;
import ca.uhn.hl7v2.model.v231.segment.PV2;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class PHS extends CreateHL7 {
	
	private ADT_A01 adt_a01;
	private ADT_A04 adt_a04;
	private ADT_A08 adt_a08;
	private String adtType;
	private MenuData md,mdReceiver,parent;
	
	private MSH msh;
	private PID pid;
	private EVN evn;
	private PV1 pv1;
	private PV2 pv2;
	private DG1 dg1;
	
	public PHS(MenuData md,MenuData mdReceiver, String adtType) {
		super();
		try {
			this.adtType = adtType;
			this.md = md;
			this.mdReceiver = mdReceiver;
			if(adtType.equals("Inpatient")) {
				adt_a01 = new ADT_A01();
				msh = adt_a01.getMSH();
				pid = adt_a01.getPID();
				evn = adt_a01.getEVN();
				pv1 = adt_a01.getPV1();
				this.pv2 = adt_a01.getPV2();
				dg1 = adt_a01.getDG1();
			} else if(adtType.equals("Emergency")) {
				adt_a04 = new ADT_A04();
				msh = adt_a04.getMSH();
				pid = adt_a04.getPID();
				evn = adt_a04.getEVN();
				pv1 = adt_a04.getPV1();
				pv2 = adt_a04.getPV2();
				dg1 = adt_a04.getDG1();				
			} else if(adtType.equals("Update")) {
				adt_a08 = new ADT_A08();
				msh = adt_a08.getMSH();
				pid = adt_a08.getPID();
				evn = adt_a08.getEVN();
				pv1 = adt_a08.getPV1();
				pv2 = adt_a08.getPV2();
				dg1 = adt_a08.getDG1();				
			}			
		} catch (Exception e) {
		}
	}
	
	public String createHL7() {
		getMenuBean();			
		createMSHSegmentType();
		parent = md.getParent01();
		//mdPatient = menuBean.findMenuDataItem(md.getAccount().getId(),parent.getPath());
		createEVNSegment();
		createPIDSegment(pid,parent);		
		createPV1Segment();
		createPV2Segment();		
		createDG1Segment();
		return encodeToXML();
	}
	

	/**
	 * MSH Segment has the following elements

	  	5. Receiving Application
	  		5.1 Namespace ID
	  	6. Receiving Facility
	  		6.1 Namespace ID

	  	9. Message Type
	  		9.1 Message Type
	  		9.2 Trigger Event
	  		9.3 Message Structure
	 	
	 */
	
	public void createMSHSegmentType() {
		try {
			createMSHSegment(msh,md);
			// 5. Set Receiving Application
			String receivingApplication = mdReceiver.getString02();
			msh.getReceivingApplication().getNamespaceID().setValue(receivingApplication);
			// 6. Set Receiving Facility
			String receivingFacility = mdReceiver.getString01();
			msh.getReceivingFacility().getNamespaceID().setValue(receivingFacility);
			
			// 9. Set Message Type 
			// 9.1 Message Type
			msh.getMessageType().getMessageType().setValue("ADT");
			// 9.2 Trigger Event
			String messageType="";
			if(adtType.equals("Inpatient")) {
				messageType = "A01";
			} else if(adtType.equals("Emergency")) {
				messageType = "A04";
			} else if(adtType.equals("Update")) {
				messageType = "A08";
			}
			msh.getMessageType().getTriggerEvent().setValue(messageType);
			// 9.3 Message Structure
			// msh.getMessageType().getMessageStructure().setValue("ADT_A04");
			// 10. Set Message Control ID
		} catch (DataTypeException e) {
			throw new RuntimeException("Exception in createMSHSegmentType",e);
		}
	}	
	
	/**
	 * EVN Segment has the following elements
	  	1. Event Type Code
	  		1.1 Value
	  	2. Recorded Date/Time
	  		2.1 Time of An Event
	 */	
	private void createEVNSegment() {
		try {
			// 1.Event Type Code
			String typeCode;
			if(adtType.equals("Inpatient")){
				typeCode = "A01";
			}else if(adtType.equals("Emergency")){
				typeCode = "A04";
			}else{
				typeCode = "A08";
			}
			evn.getEventTypeCode().setValue(typeCode);
			// 2. Recorded Date/Time
			Date effectiveDateTime = (Date)md.getField("effectiveTime");
			
			if(effectiveDateTime != null ){
				evn.getRecordedDateTime().getTimeOfAnEvent().setValue(HL7DateFormatUtility.formatHL7TSFormatL14Date(effectiveDateTime));
			}
		} catch(DataTypeException e) {
			throw new RuntimeException("Exception in createEVNSegment",e);
		}
	}
	
	/**
	 * PV1 Segment has the following elements
	  	1. Set ID
	  		1.1 Value
	  	2. Patient Class
	  		2.1 Value
	  	4. Admission Type
	  		4.1 Value
	  	14. Admit Source
	  		14.1 Value
	  	19. Visit Number
	  		19.1 ID
	  	44. Admin Date/Time
	  		44.1 Time of An Event
	 */
	private void createPV1Segment() {
		try {
			//Set ID
			pv1.getSetIDPV1().setValue("1");
			
			// 2. Patient Class
			if(adtType.equals("Inpatient")){
				pv1.getPatientClass().setValue("I");
			}else if(adtType.equals("Emergency")){
				pv1.getPatientClass().setValue("E");
			}else{
				pv1.getPatientClass().setValue("U");
			}
			// 4. Admission Type
			pv1.getAdmissionType().setValue("R");
			
			// 14. Admit Source
			pv1.getAdmitSource().setValue("9");
			
			// 19. Visit Number
			//19.1 Visit Identifier
			Object encounter = md.getField("encounter");
			if(encounter != null){
				MenuData encounterMd = (MenuData) encounter;
				Object visitID = encounterMd.getId();
				if(visitID != null){
					pv1.getVisitNumber().getID().setValue(visitID.toString());
				}
				// 44. Admit Date/Time
				Date admitdate = (Date)encounterMd.getField("effectiveTimeLow");
				if(admitdate != null){
					//DateFormat format = sdf;					
					pv1.getAdmitDateTime().getTimeOfAnEvent().setValue(HL7DateFormatUtility.formatHL7TSFormatL14Date(admitdate));
				}		
			}
				
		} catch(Exception e) {
			throw new RuntimeException("Error in createPV1Segment ",e);
		}
	}
	
	/**
	 * PV2 Segment has the following elements
	  	3. Admit Reason
	  		3.1 Identifier
	  		3.2 Text
	  		3.3 Name of Coding System
	 */	
	private void createPV2Segment() {
		try {

		} catch(Exception e) {
			
		}
	}
	/**
	 * DG1 has the following elements
	   		1.SetID
	   		2.Diagnosis Coding Method
	   		3.Diagnosis Code
	   			3.1 Identifier
	   			3.2 Text
	   			3.3 Coding System
	   		4.Diagnosis Description
	   		6.Diagnosis Type	   			
	 */
	private void createDG1Segment() {
		try {
			//1.SetID
			//First Occurrence
			dg1.getSetIDDG1().setValue("1");
			//2.Diagnosis Coding Method
			dg1.getDg12_DiagnosisCodingMethod().setValue("SNOMED-CT");
			//3.1 Identifier
			Object identifier = md.getField("code");
			if(identifier != null){
				dg1.getDiagnosisCodeDG1().getIdentifier().setValue(identifier.toString());
			}
			//3.2 Text
			Object diagnosis = md.getField("title");
			if(diagnosis != null){
				dg1.getDiagnosisCodeDG1().getText().setValue(diagnosis.toString());
		    }
			//3.3 Coding System
			dg1.getDiagnosisCodeDG1().getNameOfCodingSystem().setValue("SNOMED-CT");			
			//6.Diagnosis Type
			dg1.getDiagnosisType().setValue("W");
		} catch(Exception e) {		
			throw new RuntimeException("Error in createDG1Segment ",e);
		}
	}	
	
	public String encodeToPipeStream() {
		try {
			String hl7Message=null;
			Parser parser = new PipeParser();
			if(adtType.equals("Inpatient")){
				hl7Message = parser.encode(adt_a01);
			}else if(adtType.equals("Emergency")){
				hl7Message = parser.encode(adt_a04);
			}else{
				hl7Message = parser.encode(adt_a08);
			}
			return hl7Message;			
		} catch (Exception e) {
			throw new RuntimeException("Error in encodeToHL7 ",e);
		}
	}
	
	public String encodeToXML() {
		try {
			String hl7Message=null;
			Parser parser = new DefaultXMLParser();
			if(adtType.equals("Inpatient")){
				hl7Message = parser.encode(adt_a01);
			}else if(adtType.equals("Emergency")){
				hl7Message = parser.encode(adt_a04);
			}else{
				hl7Message = parser.encode(adt_a08);
			}
			System.out.println("Created HL7 Message");
			return hl7Message;	
		} catch (HL7Exception e) {
			throw new RuntimeException("Error in encodeToXML ",e);
		}	
	}

}
