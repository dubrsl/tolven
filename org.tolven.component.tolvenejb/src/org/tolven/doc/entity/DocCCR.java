/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.doc.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CCR")
public class DocCCR extends DocXML implements Serializable{
	
    /**
	 * Version number used for serialization
	 */
	private static final long serialVersionUID = 2L;

    //CCHIT merge
    @Column
    private int uniqueIDSeq;

//    @Transient
//    private ContinuityOfCareRecord newCCR;
    
	public DocCCR() {
		super( "ContinuityOfCareRecord", "urn:astm-org:CCR");
	}

//	/**
//	 * The constructor for this class is not sufficient to create a CCR document. This method
//	 * actually create the document.
//	 * This method does the things needed to initialize a new CCR document. Creating a unique ID for the CCR document is
//	 * a bit of a catch 22 since we want to base it on the PK id of this document. We take care of this by persisting
//	 * immediately after the CCR document is created (although it still could be rollled back) but before this method is called. this yields us a valid Id to
//	 * use in order to identify this object.
//	 * We're not going to marshall this graph until we're all done and ready to persist the document. 
//	 * So in the meantime, we remember the
//	 * graph in a a transient variable. We'll add the ID and marshall to XML when persisting.
//	 * @throws CCRException 
//	 * @throws CCRException 
//	 */
//	public void createCCR( String OID ) throws CCRException  {
//		checkEditable();
//		newCCR = new ContinuityOfCareRecord();
//		newCCR.setCCRDocumentObjectID(OID + "."+ Long.toString(getId()) );
//		// We're done with the graph, marshall to XML
//		setXmlNS("urn:astm-org:CCR"); 
//		setXmlName("ContinuityOfCareRecord"); 
//		newCCR.setVersion("V1.0");
//	}

    public void checkEditable() throws CCRException {
		if (!isEditable()) throw new CCRException( "Document must be in an editable state in order to modify its content");
	}


}
