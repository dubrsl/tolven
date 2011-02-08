/*
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
 * @author anil
 * @version $Id: CardinalityValidator.java,v 1.5 2009/10/28 05:42:53 jchurin Exp $
 */

package org.tolven.process;

import java.util.List;

import org.tolven.app.el.ELFunctions;
import org.tolven.app.el.GeneralExpressionEvaluator;
import org.tolven.trim.Act;
import org.tolven.trim.Entity;
import org.tolven.trim.Role;

/**
 * To make sure that certain Relations are added and enabled in the trim.
 * 
 * @author Anil
 *
 */
public class CardinalityValidator extends ComputeBase {

	private Integer minCardinality;
	private String targetPath;
	private String validationMsgKey;
	
	@Override
	public void compute() throws Exception {
		
		GeneralExpressionEvaluator ee = new GeneralExpressionEvaluator();
		ee.addVariable("trim", getTrim());
		ee.addVariable("account", getAccountUser().getAccount());
		if (getNode() instanceof Act) {
			ee.addVariable("act", getNode());
		}
		if (getNode() instanceof Role) {
			ee.addVariable("role", getNode());
		}
		if (getNode() instanceof Entity) {
			ee.addVariable("entity", getNode());
		}

		Object targetObject = ee.evaluate(getTargetPath()) ;
		
		if (targetObject instanceof List)
		{
			List<Object> targetObjList = (List<Object>)targetObject;
			if(targetObjList.size() < getMinCardinality())
			{
				buildMessage(getValidationMsgKey());
			}
		}
		else
		{
			throw new RuntimeException("Unable to obtain expression for " + getTargetPath());
		}
	}
	
	public void setMinCardinality(Integer aMinCardinlaity) {
		this.minCardinality = aMinCardinlaity;
	}
	public Integer getMinCardinality(){
		return this.minCardinality;
	}
	
	public void setTargetPath(String aPath){
		this.targetPath = aPath;
	}
	public String getTargetPath(){
		return this.targetPath;
	}
	
	public void setValidationMsgKey(String aMsgKey){
		this.validationMsgKey = aMsgKey;
	}
	public String getValidationMsgKey(){
		return this.validationMsgKey;
	}

}
