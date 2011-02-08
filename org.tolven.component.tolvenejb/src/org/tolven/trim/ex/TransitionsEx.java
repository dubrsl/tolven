package org.tolven.trim.ex;

import java.io.Serializable;
import java.util.Map;

import org.tolven.trim.Transition;
import org.tolven.trim.Transitions;

@SuppressWarnings("serial")
public class TransitionsEx extends Transitions implements Serializable {
	private transient Map<String, Transition> transitionsMap = null;
	
	public Map<String, Transition> getTransition() {
        if (transitionsMap == null) {
        	transitionsMap = new TransitionsMap( getTransitions());
        }
        return transitionsMap;
	}

}
