package org.tolven.web;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

import org.tolven.trim.CE;
import org.tolven.trim.ValueSet;

public class ValueSetLookupConverter implements Converter {
	
	private List<SelectItem> valueSet;
	
	public void setValueSet(List<SelectItem> valueSet) {
		this.valueSet = valueSet;
	}
	
	public Object getAsObject(FacesContext ctx, UIComponent comp, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAsString(FacesContext ctx, UIComponent comp, Object object) {
		if (valueSet==null) throw new ConverterException("Missing valueSet argument in " + comp.getId() + " valueSetLookupConverter");
		if (object instanceof CE ) {
			CE ce = (CE)object;
			for ( SelectItem item : valueSet) {
				if (ce.equals(item.getValue())) return item.getLabel();
			}
			throw new ConverterException("Value not found in valueSet in valueSetLookup");
		}
		throw new ConverterException("Invalid value type in valueSetLookup");
	}

}
