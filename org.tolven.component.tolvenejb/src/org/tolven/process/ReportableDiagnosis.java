/**
 * 
 */
package org.tolven.process;

import java.util.Iterator;
import java.util.List;

import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuLocator;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ST;
import org.tolven.trim.ex.ActEx;

/**
 * @author root
 *
 */
public class ReportableDiagnosis extends ComputeBase {
	
	String path;

	/**
	 * 
	 */
	public ReportableDiagnosis() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.tolven.process.ComputeBase#compute()
	 */
	@Override
	public void compute() throws Exception {
		// TODO Auto-generated method stub
		String code = getMdCode();
		boolean reportable = false;
		String selectedDiagnosisPath = getPath();
		MenuQueryControl ctrl = new MenuQueryControl();
		MenuPath reqPath = new MenuPath(selectedDiagnosisPath);
		ctrl.setAccountUser(getAccountUser());
		ctrl.setMenuStructurePath(selectedDiagnosisPath);
		ctrl.setRequestedPath(reqPath);
		MenuLocator menuLocator = menuBean.findMenuLocator(selectedDiagnosisPath);
		ctrl.setMenuStructure( menuLocator.getMenuStructure());		
		ctrl.getMenuStructure().getAccountMenuStructure().setQuery(selectedDiagnosisPath);	
		List<MenuData>selectedDiagnosisList = menuBean.findMenuData(ctrl);		
		if(selectedDiagnosisList.size() > 0){
			Iterator<MenuData> diagItr = selectedDiagnosisList.iterator();
			while(diagItr.hasNext()){
				MenuData reportableMenuData = diagItr.next();
				int reporatbleCode=Integer.parseInt(reportableMenuData.getField("Code").toString());
				int mdCode = Integer.parseInt(code);				
				if(reporatbleCode == mdCode){
					reportable=true;
					break;
				}
			}
		}
		//return reportable;
		ActRelationship ar = ((ActEx)this.getAct()).getRelationship().get("isReportable");
		ST st = new ST();
		st.setValue(reportable?"true":"false");
		ObservationValueSlot element = new ObservationValueSlot();
		element.setST(st);
		ar.getAct().getObservation().getValues().set(0, element);
	}
	
	private String getMdCode() {
		return this.getAct().getObservation().getValues().get(0).getCE().getCode();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}