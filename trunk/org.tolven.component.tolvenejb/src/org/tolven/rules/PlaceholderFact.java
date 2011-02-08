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
 * @version $Id: PlaceholderFact.java,v 1.5 2010/03/17 20:10:23 jchurin Exp $
 */  

package org.tolven.rules;

import java.io.Serializable;

import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FieldTemplate;
import org.tolven.app.entity.MenuData;
/**
 * This wrapper class holds a placeholder during rule execution.
 * @author John Churin
 *
 */
public class PlaceholderFact implements Fact, Serializable {
	private static final long serialVersionUID = 1L;
	private MenuData md;
	private PlaceholderFactTemplate ft;
	private long id;
	private boolean touching;
	
	PlaceholderFact(FactTemplate ft, long id ) {
		this.ft = (PlaceholderFactTemplate) ft;
		this.id = id;
	}

	public void setPlaceholder(MenuData md) {
		this.md = md;
	}
	
	public MenuData getPlaceholder() {
		return md;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlaceholderFact)) return false;
		PlaceholderFact other = (PlaceholderFact)obj;
		return md.equals(other.md);
	}

	@Override
	public int hashCode() {
		return md.hashCode();
	}

	@Override
	public String toString() {
		return "Fact: " + md.toString();
	}

	@Override
	public long getFactId() {
		return id;
	}

	@Override
	public FactTemplate getFactTemplate() {
		return ft;
	}

	@Override
	public Object getFieldValue(int index) {
		FieldTemplate field = ft.getFieldTemplate(index);
		if ("menuData".equals(field.getName())) {
			return md;
		} else if ("knownType".equals(field.getName())) {
			return getKnownType();
		}	else {
			return md.getField(field.getName());
		}
	}

	@Override
	public Object getFieldValue(String name) {
		FieldTemplate field = ft.getFieldTemplate(name);
		return getFieldValue(field.getIndex());
	}

	@Override
	public void setFieldValue(String name, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFieldValue(int index, Object value) {
		// TODO Auto-generated method stub

	}

	public String getKnownType() {
		MenuData md = getPlaceholder();
		if (md==null) return null;
		return md.getAccount().getAccountType().getKnownType();
	}
	
	/**
	 * When true, indicates that this placeholder is asserted because it has been touched
	 * but that it has not been directly changed by a transaction.
	 * @return
	 */
	public boolean isTouching() {
		return touching;
	}

	public void setTouching(boolean touching) {
		this.touching = touching;
	}

}
