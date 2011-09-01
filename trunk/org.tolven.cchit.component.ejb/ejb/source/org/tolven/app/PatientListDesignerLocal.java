/**
 * 
 */
package org.tolven.app;

import java.util.Map;

import org.tolven.trim.Trim;


/**
 * PatientListDesigner local interface.
 * 
 * @author valsaraj
 */
public interface PatientListDesignerLocal {
	public void createPatientList(Trim trim, AppEvalAdaptor app);
	
	/**
	 * Load all supplied application metadata. The files have been read to memory on the client and passed here as
	 * a list of zero or more mapped string with the key being the name of the file.
	 * @param appFiles
	 */
	public void loadApplications(Map<String, String> appFiles);
	
	/**
	 * Load rules.
	 * @param pakageName
	 * @param packageBody
	 */
	public void loadRulePackage(String pakageName, String packageBody);
}
