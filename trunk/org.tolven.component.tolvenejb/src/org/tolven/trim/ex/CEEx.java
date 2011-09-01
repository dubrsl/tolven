package org.tolven.trim.ex;

import java.io.Serializable;

import org.tolven.trim.CE;

public class CEEx extends CE implements Serializable {
	@Override
	public boolean equals(Object obj) {
    	if (obj==null) return false;
    	if (!(obj instanceof CE)) return false;
    	CE ce = (CE)obj;
    	if (code!=null && ce.getCode()!=null){
    		if (!(code.equals(ce.getCode()))) {
//    			TolvenLogger.info("CE::equals::code " + code + "!=" +ce.getCode(), CEEx.class );
    			return false;
    		}
    	} else {
    		if (code!=ce.getCode()) {
//    			TolvenLogger.info("CE::equals::code " + code + "!=" +ce.getCode(), CEEx.class );
    			return false;
    		}
    	}
    	if (codeSystem!=null && ce.getCodeSystem()!=null){
    		if (!(codeSystem.equals(ce.getCodeSystem()))) {
//    			TolvenLogger.info("CE::equals::codeSystem " + codeSystem + "!=" +ce.getCodeSystem(), CEEx.class );
    			return false;
    		}
    	} else {
    		if (codeSystem!=ce.getCodeSystem()) {
//    			TolvenLogger.info("CE::equals::codeSystem" + codeSystem + "!=" +ce.getCodeSystem(), CEEx.class );
    			return false;
    		}
    	}
    	if (codeSystemName!=null && ce.getCodeSystemName()!=null){
    		if (!(codeSystemName.equals(ce.getCodeSystemName()))) {
//    			TolvenLogger.info("CE::equals::codeSystemName " + codeSystemName + "!=" +ce.getCodeSystemName(), CEEx.class );
    			return false;
    		}
    	} else {
    		if (codeSystemName!=ce.getCodeSystemName()) {
//    			TolvenLogger.info("CE::equals::codeSystemVersion " + codeSystemVersion + "!=" +ce.getCodeSystemVersion(), CEEx.class );
    			return false;
    		}
    	}
    	if (codeSystemVersion!=null && ce.getCodeSystemVersion()!=null){
    		if (!(codeSystemVersion.equals(ce.getCodeSystemVersion()))) {
//    			TolvenLogger.info("CE::equals::codeSystemVersion" + codeSystemVersion + "!=" +ce.getCodeSystemVersion(), CEEx.class );
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
		if (getDisplayName()!=null) return getDisplayName();
		if (getLabel()!=null) return this.getLabel().getValue();
		if (getOriginalText()!=null) return this.getOriginalText();
		return null;
	}
	

}
