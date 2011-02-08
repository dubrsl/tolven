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
 * @author <your name>
 * @version $Id: PlaceholderFactTemplate.java,v 1.4 2010/03/17 20:10:23 jchurin Exp $
 */  

package org.tolven.rules;

import java.io.Serializable;
import java.util.ArrayList;

import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FieldTemplate;
import org.drools.rule.Package;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;

/**
 * This wrapper class references a placeholder in a rule. While a placeholder is pure metadata,
 * a PlaceholderFactTemplate is represented as a concrete fact type in rules.
 * @author John Churin
 *
 */
public class PlaceholderFactTemplate implements FactTemplate, Serializable {
	private static final long serialVersionUID = 1L;
//	private transient MenuStructure ms;
	private String name;
	private transient Package pkg;
	private java.util.List<FieldTemplate> fieldTemplates = new ArrayList<FieldTemplate>();
	
	public PlaceholderFactTemplate(Package pkg, MenuStructure ms) {
		this.pkg = pkg;
//		this.ms = ms;
		this.name = ms.getNode();
		int x = 0;
		for (MSColumn column: ms.getColumns()) {
			FieldTemplate fieldTemplate = new SimpleFieldTemplate(column.getHeading(), x, column.getJavaClass());
			fieldTemplates.add(fieldTemplate);
			x++;
		}
		FieldTemplate mdFieldTemplate;
		mdFieldTemplate = new SimpleFieldTemplate("menuData", x, MenuData.class);
		fieldTemplates.add(mdFieldTemplate);
		x++;
		mdFieldTemplate = new SimpleFieldTemplate("actStatus", x, String.class);
		fieldTemplates.add(mdFieldTemplate);
		x++;
		mdFieldTemplate = new SimpleFieldTemplate("status", x, String.class);
		fieldTemplates.add(mdFieldTemplate);
		x++;
		mdFieldTemplate = new SimpleFieldTemplate("id", x, Long.class);
		fieldTemplates.add(mdFieldTemplate);
		x++;
		mdFieldTemplate = new SimpleFieldTemplate("knownType", x, String.class);
		fieldTemplates.add(mdFieldTemplate);
		x++;
		
	}
	
	@Override
	public Fact createFact(long id) {
        return new PlaceholderFact( this, id );
	}

	@Override
	public FieldTemplate[] getAllFieldTemplates() {
		return fieldTemplates.toArray(new FieldTemplate[fieldTemplates.size()]);
	}

	protected FieldTemplate findFieldTemplate( String name ) {
		for (FieldTemplate ft : fieldTemplates) {
			if (name.equals(ft.getName())) {
				return ft;
			}
		}
		return null;
	}

	@Override
	public FieldTemplate getFieldTemplate(String name) {
		// First, look for the whole field as specified
		FieldTemplate ft = findFieldTemplate(name);
		if (ft!=null) {
			return ft;
		}
		// We may need to add a new field if this is a qualified identifier (with dots)
		int dotOffset = name.indexOf('.');
		if (dotOffset<0) return null;
		String prefix = name.substring(0,dotOffset);
		ft = findFieldTemplate(prefix);
		if (ft==null) return null;
		Class clazz = ft.getValueType().getClassType();
		if (clazz.equals(Object.class)) {
			clazz = MenuData.class;
		}
		// For example, patient.lastName:
		// 1. make sure patient is menuData
		// 2. Find the factTemplate of prefix (patient) 
		// 3. Look up the suffix (lastName) in that factTemplate
		// 4. Create an indirect FieldTemplate entry which we'll use later.
		if (MenuData.class.isAssignableFrom(clazz)) {
			FactTemplate ft2 = pkg.getFactTemplate(prefix);
			String suffix = name.substring(dotOffset+1);
			FieldTemplate fieldTemplate = ft2.getFieldTemplate(suffix);
			if (fieldTemplate==null) return null;
			FieldTemplate newFieldTemplate = new SimpleFieldTemplate( name, fieldTemplates.size(), (SimpleFieldTemplate)fieldTemplate );
			fieldTemplates.add(newFieldTemplate);
			return newFieldTemplate;
		}
		return null;
	}

	@Override
	public FieldTemplate getFieldTemplate(int index) {
		return fieldTemplates.get(index);
	}

	@Override
	public int getFieldTemplateIndex(String name) {
		FieldTemplate fieldTemplate = getFieldTemplate(name);
		return fieldTemplate.getIndex();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getNumberOfFields() {
		return fieldTemplates.size();
	}

	@Override
	public Package getPackage() {
		return pkg;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlaceholderFactTemplate)) return false;
		return name.equals(((PlaceholderFactTemplate)obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "PlaceholderFactTemplate: " + getName();
	}

}
