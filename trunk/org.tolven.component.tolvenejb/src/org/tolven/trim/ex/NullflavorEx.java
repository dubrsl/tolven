package org.tolven.trim.ex;

import org.tolven.trim.NullFlavor;

public class NullflavorEx extends NullFlavor {
	@Override
	public boolean equals(Object obj) {
    	if (obj==null) return false;
    	if (!(obj instanceof NullFlavor)) return false;
    	NullFlavor nf = (NullFlavor)obj;
    	if (!(nf.getType().equals(getType()))) return false; 
    	return true;
	}

	@Override
	public int hashCode() {
		if (getType() !=null) return getType().hashCode();
		return 0;
	}

	@Override
	public String toString() {
		if (this.getOriginalText()!=null) return this.getOriginalText();
		if (getType()!=null) return getType().name();
		return null;
	}
	
	
}
