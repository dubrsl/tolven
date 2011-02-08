package org.tolven.process;

import org.tolven.app.el.ELFunctions;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.DataType;
import org.tolven.trim.Entity;
import org.tolven.trim.Role;
import org.tolven.trim.Slot;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ex.GTSSlotEx;
import org.tolven.trim.ex.ObservationValueSlotEx;

public class DataTypeSelect extends ComputeBase {

	private boolean enabled;
	private String dataTypeDestination;
	private String dataTypeSource;
	
	@Override
	public void compute() throws Exception {
		if (isEnabled())
		{
			TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
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

			Object source = (Object)ee.evaluate(getDataTypeSource());
			
 			Object destination = ee.evaluate(getDataTypeDestination());
 			if (destination instanceof ObservationValueSlotEx)
 			{
 				((ObservationValueSlotEx)destination).setValue((DataType)source) ;
 			}
 			else  
 			{
 				// 	Assuming that else will be GTSSlotEx. Modify if more cases needs to be handled.
 				((GTSSlotEx)destination).setValue((DataType)source) ;
 			}
			TolvenLogger.info("Selecting Dattype = " + source , DataTypeSelect.class);
			
            // Disable the Compute since its job is done.
	    	for (Property property : getComputeElement().getProperties()) {
				if (property.getName().equals("enabled")) {
					property.setValue(Boolean.FALSE);
					break;
				}
			}			
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setDataTypeDestination(String aDestination)
	{
		this.dataTypeDestination = aDestination;
	}
	public String getDataTypeDestination()
	{
		return this.dataTypeDestination;
	}
	
	public void setDataTypeSource(String aSourceDataType)
	{
		this.dataTypeSource = aSourceDataType; 
	}
	
	public String getDataTypeSource() 
	{
		return dataTypeSource;
	}
	


}
