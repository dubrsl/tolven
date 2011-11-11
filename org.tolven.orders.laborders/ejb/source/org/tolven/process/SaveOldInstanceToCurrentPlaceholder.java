/*  Copyright (C) 2011 Tolven Inc 
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

package org.tolven.process;

import java.util.List;

import org.tolven.app.entity.MenuData;
import org.tolven.trim.TolvenId;
/*
 * A compute to set the previous menu data to current menu data fields 
 */
public class SaveOldInstanceToCurrentPlaceholder extends ComputeBase{
	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


	@Override
	public void compute() throws Exception {
		// Revise for laborder
		
		List<TolvenId> events = getTrim().getTolvenEventIds();
		if(events.size() > 1){
			//get the first event which would be the current menudata 
			long id = Long.parseLong(events.get(0).getId());
			MenuData md = getMenuBean().findMenuDataItem(id);
			// the event at index 1 would be the previous menu data item that's been removed due to revise
			MenuData oldMd = getMenuBean().findMenuDataItem(new Long(events.get(1).getId()));
			md.setField(getFieldName(), oldMd);
			getMenuBean().persistMenuData(md);
		}
		
		/*Object obj = ((ActEx)wizardTrim.getAct()).getRelationship().get("replaces");
	    	ActRelationship rel = (ActRelationship)obj;
	    	rel.getAct().getId().getIIS().add(0, ii);
	    	try{
	    		creatorBean.marshalToDocument( wizardTrim, docXML );
	    	}catch(Exception e) {
	    		TolvenLogger.debug("Exception during (Marshall Trim) "+ e, getClass());
	    	}
		}
		
*/	}

}
