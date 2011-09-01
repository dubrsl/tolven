package org.tolven.trim.ex;

import org.tolven.trim.II;

public class IIEx extends II {

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (!(obj instanceof II)) return false;
		II other = (II)obj;
		if (!(getRoot().equals(other.getRoot()))) return false;
		if (getExtension()==null && other.getExtension()==null) return true;
		if (getExtension()==null) return false;
		if (getExtension().equals(other.getExtension())) return true;
		return false;
	}

	@Override
	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		if (getExtension()!=null) {
			sb.append( getExtension());
		}
		sb.append(getRoot());
		return sb.toString().hashCode();
	}

	public boolean getEditable() {
		return isEditable()==null || isEditable();
	}
	
	public boolean getDisplayable() {
		return isDisplayable()==null || isDisplayable();
	}
}
