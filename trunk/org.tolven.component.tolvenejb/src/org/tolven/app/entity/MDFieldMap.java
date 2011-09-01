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
 * @version $Id: MDFieldMap.java,v 1.1 2009/11/06 20:01:10 jchurin Exp $
 */  

package org.tolven.app.entity;

import java.util.Set;


public abstract class MDFieldMap {

		private MenuData md;
		
		public MDFieldMap( MenuData md) {
			this.md = md;
		}

		protected Object getField(String fieldName) {
			return md.getField(fieldName);
		}
		
		protected Object putField(String fieldName, Object value) {
			Object previousValue = md.getField(fieldName);
			md.setField(fieldName, value);
			return previousValue;
		}
		
		public void clear() {
			// TODO Auto-generated method stub

		}

		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}
		
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		public Set<String> keySet() {
			// TODO Auto-generated method stub
			return null;
		}


		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}


}
