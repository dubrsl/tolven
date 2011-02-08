/**
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Kul Bhushan
 * @version $Id: CCRFactory.java,v 1.2 2009/05/19 18:29:20 jchurin Exp $
 */

package org.tolven.ccr.ex;

import javax.xml.bind.annotation.XmlRegistry;

import org.tolven.ccr.ObjectFactory;

@XmlRegistry
public class CCRFactory extends ObjectFactory {
	
    public static CCRFactory getInstance() {
    	return new CCRFactory();
    }
	
	@Override
	public DateTimeTypeEx createDateTimeType() {
		return new DateTimeTypeEx();
	}
	
	@Override
    public CCRCodedDataObjectTypeEx createCCRCodedDataObjectType() {
        return new CCRCodedDataObjectTypeEx();
    }
	
	@Override
    public ProblemTypeEx createProblemType() {
        return new ProblemTypeEx();
    }
	
	@Override
	public ContinuityOfCareRecordEx createContinuityOfCareRecord() {
		return new ContinuityOfCareRecordEx();
	}
	
	@Override
    public TestTypeEx createTestType() {
        return new TestTypeEx();
    }
	
	@Override
    public AlertTypeEx createAlertType() {
        return new AlertTypeEx();
    }

	@Override
    public ResultTypeEx createResultType() {
        return new ResultTypeEx();
    }
	
	@Override
    public PlanTypeEx createPlanType() {
        return new PlanTypeEx();
    }

	@Override
    public ProcedureTypeEx createProcedureType() {
        return new ProcedureTypeEx();
    }    
	
	@Override
    public EncounterTypeEx createEncounterType() {
        return new EncounterTypeEx();
    }	

	@Override
    public StructuredProductTypeEx createStructuredProductType() {
        return new StructuredProductTypeEx();
    }
	
	@Override
    public PersonNameTypeEx createPersonNameType() {
        return new PersonNameTypeEx();
    }	
}
