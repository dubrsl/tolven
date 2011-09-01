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
 * @version $Id: CDEx.java,v 1.1 2009/11/06 20:06:50 jchurin Exp $
 */  

package org.tolven.trim.ex;

import org.tolven.trim.CD;

public class CDEx extends CD {
	public boolean equals(Object obj) {
    	if (obj==null) return false;
    	if (!(obj instanceof CD)) return false;
    	CD cd = (CD)obj;
    	if (code!=null && cd.getCode()!=null){
    		if (!(code.equals(cd.getCode()))) {
    			return false;
    		}
    	} else {
    		if (code!=cd.getCode()) {
    			return false;
    		}
    	}
    	if (codeSystem!=null && cd.getCodeSystem()!=null){
    		if (!(codeSystem.equals(cd.getCodeSystem()))) {
    			return false;
    		}
    	} else {
    		if (codeSystem!=cd.getCodeSystem()) {
    			return false;
    		}
    	}
    	if (codeSystemName!=null && cd.getCodeSystemName()!=null){
    		if (!(codeSystemName.equals(cd.getCodeSystemName()))) {
    			return false;
    		}
    	} else {
    		if (codeSystemName!=cd.getCodeSystemName()) {
    			return false;
    		}
    	}
    	if (codeSystemVersion!=null && cd.getCodeSystemVersion()!=null){
    		if (!(codeSystemVersion.equals(cd.getCodeSystemVersion()))) {
    			return false;
    		}
    	}
		return true;
	}

	@Override
	public int hashCode() {
		if (code !=null) return code.hashCode();
		return 0;
	}

	@Override
	public String toString() {
		if (getLabel()!=null) return this.getLabel().getValue();
		StringBuffer sb = new StringBuffer();
		sb.append("CD:");
		if (getDisplayName()!=null) {
			sb.append("(");
			sb.append(getDisplayName());
			sb.append(")");
		}
		sb.append( getCodeSystem());
		sb.append("/");
		sb.append(getCode());
		if (getOriginalText()!=null) {
			sb.append("|");
			sb.append(getOriginalText());
			sb.append("|");
		}
		return sb.toString();
	}
	
}
