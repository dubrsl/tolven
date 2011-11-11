package org.tolven.hl7;

import org.tolven.app.entity.MenuData;

public interface HL7Local {
	public boolean saveHL7Message(MenuData md);
	public String findHL7Message(String placeholderPath);
}
