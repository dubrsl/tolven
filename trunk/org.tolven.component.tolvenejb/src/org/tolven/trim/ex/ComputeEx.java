package org.tolven.trim.ex;

import java.util.Map;

import org.tolven.trim.Compute;

@SuppressWarnings("serial")
public class ComputeEx extends Compute {
	private transient PropertyMap propertyMap;
	
	public Map<String, Object> getProperty( ) {
		if (propertyMap==null) {
			propertyMap = new PropertyMap( this );
		}
		return propertyMap;
	}

	public Object getPropertyValue( String propertyName ) {
		for (Property property : getProperties()) {
			if (property.getName().equals(propertyName)) {
				return property.getValue( );
			}
		}
		return null;
	}
}
