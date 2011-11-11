package org.tolven.hl7;

import org.tolven.app.entity.MenuData;

import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;

public interface CreateHL7Local {

	String createHL7();
	
	void createMSHSegment(MSH msh,MenuData md);
	
	void createPIDSegment(PID pid, MenuData mdPatient);
	
	String encodeToPipeStream();
	
	String encodeToXML();

}
