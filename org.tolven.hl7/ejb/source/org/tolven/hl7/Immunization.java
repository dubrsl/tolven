package org.tolven.hl7;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.tolven.app.entity.MenuData;
import ca.uhn.hl7v2.model.v231.group.VXU_V04_ORCRXARXROBXNTE;
import ca.uhn.hl7v2.model.v231.message.VXU_V04;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.RXA;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;

public class Immunization extends CreateHL7 {
	
	private VXU_V04 vxu;
	private String uniqueIdentifier;
	private MenuData md,mdReceiver;
	
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}


	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}
	
	
	public Immunization(MenuData md,MenuData mdReceiver) {
		super();
		this.md = md;
		this.uniqueIdentifier="UNIQUE02";
		this.mdReceiver = mdReceiver;
		this.vxu = new VXU_V04();			
	}
	
	
	public String createHL7() {
		getMenuBean();		
		createMSHSegmentType();
		MenuData parent = md.getParent01();
		//MenuData mdPatient = menuBean.findMenuDataItem(md.getAccount().getId(), parent.getPath());
		PID pid = vxu.getPID();
		createPIDSegment(pid,parent);		
		createRXASegment(vxu,md);		
		return encodeToXML();
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
	
	public void createMSHSegmentType() {
		try {
			MSH msh = vxu.getMSH();
			createMSHSegment(msh,md);
			// 5. Set Receiving Application
			msh.getReceivingApplication().getNamespaceID().setValue((String)mdReceiver.getField("receiver"));
			// 6. Set Receiving Facility
			msh.getReceivingFacility().getNamespaceID().setValue((String)mdReceiver.getField("receiverBy"));
			
			// 9. Set Message Type 
			// 9.1 Message Type
			msh.getMessageType().getMessageType().setValue("VXU");
			// 9.2 Trigger Event			
			msh.getMessageType().getTriggerEvent().setValue("V04");
			// 9.3 Message Structure
			msh.getMessageType().getMessageStructure().setValue("VXU_V04");
			// 10. Set Message Control ID
		} catch (Exception e) {			
		}
	}

	
	/**
	 * RXA Segment has the following elements
	 * ( Pharmacy / Treatment Administration ) 
	 	1.  Give Sub-Id Counter
	 		1.1 Value
	 	2.  Administration Sub-Id Counter
	 		2.1 Value
	 	3.  Date/Time Start of Administration
	 		3.1 Time of an Event
	 	4.  Date/Time End of Administration
	 		4.1 Time of an Event
	 	5.  Administered Code
	 		5.1 Identifier
	 		5.2 Text
	 		5.3 Name of Coding System
	 	6.  Administered Amount
	 		6.1 Value
	 	7.  Administered Units
	 		7.1 Identifier
	 		7.2 Text
	 		7.3 Name of Coding System
	 	8.  Administered Dosage Form
	 		8.1 Identifier
	 	9.  Administration Notes
	 		9.1 Identifier
	 	10. Administering Provider
	 		10.1 ID Number (ST)
	 	11. Administered-at Location
	 		11.1 Point of Care (IS)
	 	12. Administered Per (time Unit)
	 		12.1 Value
	 	13. Administered Strength
	 		13.1 Value
	 	14. Administered Strength Units
	 		14.1 Identifier
	 	15. Substance Lot Number
	 		15.1 Value
	 	16. Substance Expiration Date
	 		16.1 Time of an Event
	 	17. Substance Manufacturer Name
	 		17.1 Identifier
	 		17.2 Text
	 		17.3 Name of Coding System
	 	18. Substance Refusal Reason
	 		18.1 Identifier
	 	19. Indication
	 		19.1 Identifier
	 	20. Completion Status
	 		20.1 Value
	 	21.	Action Code
	 		21.1 Value
	 */
	
	public void createRXASegment(VXU_V04 vx,MenuData md) {
		try {
			VXU_V04_ORCRXARXROBXNTE rxaGroup = vxu.getORCRXARXROBXNTE();
			RXA rxa = rxaGroup.getRXA();			
			//1. Give Sub-Id Counter-Fixed
			rxa.getGiveSubIDCounter().setValue("0");
			
			//2. Administration Sub-Id Counter-Fixed
			rxa.getAdministrationSubIDCounter().setValue("1");
			
			//3. Date/Time Start of Administration
			Date doe = (Date)md.getField("date01");
			Format format = new SimpleDateFormat("yyyyMMddhhmmss");			
			String formattedDOE = format.format(doe);	
			rxa.getDateTimeStartOfAdministration().getTimeOfAnEvent().setValue(formattedDOE);
			
			//4.  Date/Time End of Administration
			rxa.getDateTimeEndOfAdministration().getTimeOfAnEvent().setValue(formattedDOE);
			
			//5. Administered Code 
			//5.1 Identifier
			Object administerCode = md.getField("code");			
			if(administerCode != null){				
				rxa.getAdministeredCode().getIdentifier().setValue(administerCode.toString());
			}
			//5.2 Text
			Object serviceName = md.getField("title");
			rxa.getAdministeredCode().getText().setValue(serviceName.toString());
			//5.3 Name of Coding System			
			rxa.getAdministeredCode().getNameOfCodingSystem().setValue("CVX");

			//6.  Administered Amount
	 		//6.1 Value
			Object materialAmount = md.getField("consumableMaterial");
			if(materialAmount !=null){
				rxa.getAdministeredAmount().setValue(materialAmount.toString());
			}
			
			//7.  Administered Units
	 		//7.1 Identifier
	 		//7.2 Text
	 		//7.3 Name of Coding System
			/*Object units = md.getField("dosagequantityUnit");
			if(units != null){
				//rxa.getAdministeredUnits().getText().setValue(units.toString());
				rxa.getAdministeredUnits().getIdentifier().setValue(units.toString());
			}*/
			Object unitText = md.getField("consumableMaterialUnit");
			if(unitText != null){
				rxa.getAdministeredUnits().getIdentifier().setValue("mg");
				rxa.getAdministeredUnits().getText().setValue(unitText.toString());
			}
			rxa.getAdministeredUnits().getNameOfCodingSystem().setValue("ISO+");
			
			
			//8.  Administered Dosage Form
	 		//8.1 Identifier
			Object dosage = md.getField("dosageQuantity");
			if(dosage != null){
				rxa.getAdministeredDosageForm().getIdentifier().setValue(dosage.toString());
			}
			
			//9.  Administration Notes
	 		//9.1 Identifier
			//10. Administering Provider
	 		//10.1 ID Number (ST)
			//11. Administered-at Location
	 		//11.1 Point of Care (IS)
			//12. Administered Per (time Unit)
	 		//12.1 Value
			//13. Administered Strength
	 		//13.1 Value
			//14. Administered Strength Units
	 		//14.1 Identifier
			
			//15. Substance Lot Number
	 		//15.1 Value
			Object lotNumber = md.getField("lotNumber");
			if(lotNumber != null){
				rxa.getSubstanceLotNumber(0).setValue(lotNumber.toString());
			}
					
			//16. Substance Expiration Date
	 		//16.1 Time of an Event
			
			//17. Substance Manufacturer Name
	 		//17.1 Identifier
	 		//17.2 Text
	 		//17.3 Name of Coding System
			Object substanceManufacturer = md.getField("manufacturerName");
			if(substanceManufacturer != null){
				String[] manufacturerInfo = substanceManufacturer.toString().split("~");
				String manufacturerName = manufacturerInfo[0];
				String manufacturerId = manufacturerInfo[1];
				rxa.getSubstanceManufacturerName(0).getIdentifier().setValue(manufacturerId);
				rxa.getSubstanceManufacturerName(0).getText().setValue(manufacturerName);	
				rxa.getSubstanceManufacturerName(0).getNameOfCodingSystem().setValue("MVX");
			}
			
			//18. Substance Refusal Reason
	 		//18.1 Identifier
     	 	//19. Indication
			//19.1 Identifier
			
			//20. Completion Status
	 		//20.1 Value
			
			//21 Actioncode-Fixed
			rxa.getActionCodeRXA().setValue("A");			
			
		} catch (Exception e) {			
		}
	}
	
	public String encodeToPipeStream() {
		try {
			return new PipeParser().encode(this.vxu);
		} catch (Exception e) {
			throw new RuntimeException("error encoding HL7 to pipped stream",e);
		}
	}
	
	public String encodeToXML() {
		try {
			return new DefaultXMLParser().encode(this.vxu);
		} catch (Exception e) {
			throw new RuntimeException("error encoding HL7 to xml",e);
		}	
	}
}
