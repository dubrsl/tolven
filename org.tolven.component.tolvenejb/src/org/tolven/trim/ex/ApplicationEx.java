package org.tolven.trim.ex;

import org.tolven.trim.Application;

@SuppressWarnings("serial")
public class ApplicationEx extends Application {
    
    /**
     * Compare to see if two 
     * Application objects are equal by comparing their names
     */
    public boolean equals(Object aObject)
    {
    	if (!(aObject instanceof Application)) return false;

    	Application app = (Application)aObject;
    	if (!app.getName().equals(app.getName())) return false;
    	
    	return true;
    }
    
    public int hashCode()
    {
    	return getName().hashCode();
    }
}
