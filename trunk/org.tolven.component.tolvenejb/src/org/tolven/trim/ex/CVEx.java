package org.tolven.trim.ex;

import java.io.Serializable;

import org.tolven.trim.CV;

@SuppressWarnings("serial")
public class CVEx extends CV implements Serializable {

	@Override
	public boolean equals(Object obj) {
    	if (!(obj instanceof CV)) return false;
    	if (!(code.equals(((CV)obj).getCode()))) return false;
    	if (!(codeSystem.equals(((CV)obj).getCodeSystem()))) return false;
		return true;
	}

}
