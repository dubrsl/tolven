/**
 * 
 */
package org.tolven.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.tolven.app.CCHITLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocXML;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.TolvenId;
import org.tolven.trim.ex.TrimEx;

/**
 * @author root
 * 
 */
public class VitalSignsHistoryAction extends TolvenAction {

	private AccountUser activeAccountUser;
	private MenuData activeMenuData;
	private DocXML activeDocument;
	private TrimEx activeTrim;
	private List<HashMap<String, String>> history;
	@EJB
	private CCHITLocal cchitBean;
	
	public CCHITLocal getCchitBean() {
		return cchitBean;
	}
	public VitalSignsHistoryAction() throws ServletException {
		/*super();
		
		try {
			activeAccountUser = (AccountUser) getRequestAttribute("accountUser");
			activeMenuData = getMenuLocal().findMenuDataItem(
					activeAccountUser.getAccount().getId(),
					(String) getRequestParameter("element"));
			activeDocument = (DocXML) getDocBean().findDocument(
					activeMenuData.getDocumentId());
			activeTrim = (TrimEx) getXMLProtectedBean().unmarshal(
					activeDocument, activeAccountUser);
			

		} catch (Exception e) {
			TolvenLogger.error(
					"Error while initialising Vital Signs History", e,
					VitalSignsHistoryAction.class);
		}
		
		
		if (activeTrim != null) {
			try {
				long id;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssZ");
				SimpleDateFormat sdfOut = new SimpleDateFormat(
						"MM/dd/yyyy hh:mm:ss");
				history = new ArrayList<HashMap<String, String>>();
				TrimEx trim = null; 
				int i = 0;
				String val = null;
				String units = null;
				for (TolvenId item : activeTrim.getTolvenEventIds()) {
					HashMap<String, String> tempHistory = new HashMap<String, String>();
					tempHistory.put("user", getLDAPLocal().createTolvenPerson(item.getPrincipal()).getGivenName());
					tempHistory.put("timestamp", sdfOut.format(sdf.parse(item.getTimestamp())));
					if(i!=0){
					id = Long.parseLong(item.getId());
					trim = getCchitBean().findTrimData(id, activeAccountUser);
						if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Blood Pressure")){
							val = trim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getST().getValue()+"/"+trim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST().getValue();
							units = activeTrim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Temperature")){
							val = (trim.getAct().getObservation().getValues().get(0).getPQ().getValue()).toString();
							units = activeTrim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Pulse")){
							val = trim.getAct().getObservation().getValues().get(0).getST().getValue();
							units = activeTrim.getAct().getObservation().getValues().get(0).getLabel().getValue();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Weight")){
							val = (trim.getAct().getObservation().getValues().get(0).getPQ().getValue()).toString();
							units =  trim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Height")){
							val = trim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getST().getValue()+"'"+trim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST().getValue()+"''";
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Weight")){
							val = (trim.getAct().getObservation().getValues().get(0).getPQ().getValue()).toString();
							units =  trim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
					}
					else{
						if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Blood Pressure")){
							val = activeTrim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getST().getValue()+"/"+activeTrim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST().getValue();
							units = activeTrim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Temperature")){
							val = (activeTrim.getAct().getObservation().getValues().get(0).getPQ().getValue()).toString();
							units = activeTrim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Pulse")){
							val = activeTrim.getAct().getObservation().getValues().get(0).getST().getValue();
							units = activeTrim.getAct().getObservation().getValues().get(0).getLabel().getValue();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Weight")){
							val = (activeTrim.getAct().getObservation().getValues().get(0).getPQ().getValue()).toString();
							units =  activeTrim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Height")){
							val = activeTrim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getST().getValue()+"'"+activeTrim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST().getValue()+"''";
						}
						else if ((activeTrim.getAct().getTitle().getST().getValue()).equals("Weight")){
							val = (activeTrim.getAct().getObservation().getValues().get(0).getPQ().getValue()).toString();
							units =  activeTrim.getAct().getObservation().getValues().get(0).getPQ().getUnit();
						}
					}
					tempHistory.put("value", val);
					tempHistory.put("units", units);
					tempHistory.put("status", item.getStatus().toUpperCase());
					history.add(tempHistory);
					i++;
				}

			} catch (Exception e) {
				TolvenLogger.error(
						"Error while populating Vital Signs History", e,
						VitalSignsHistoryAction.class);
			}
		} else {
			TolvenLogger.error("Current trim is not initialized.",
					VitalSignsHistoryAction.class);
		}
*/
		throw new ServletException("Fix the commented code in "+this.getClass()+" at line:146");
	}

	public AccountUser getActiveAccountUser() {
		return activeAccountUser;
	}

	public void setActiveAccountUser(AccountUser activeAccountUser) {
		this.activeAccountUser = activeAccountUser;
	}

	public MenuData getActiveMenuData() {
		return activeMenuData;
	}

	public void setActiveMenuData(MenuData activeMenuData) {
		this.activeMenuData = activeMenuData;
	}

	public DocXML getActiveDocument() {
		return activeDocument;
	}

	public void setActiveDocument(DocXML activeDocument) {
		this.activeDocument = activeDocument;
	}

	public TrimEx getActiveTrim() {
		return activeTrim;
	}

	public void setActiveTrim(TrimEx activeTrim) {
		this.activeTrim = activeTrim;
	}

	public List<HashMap<String, String>> getHistory() {
		return history;
	}

	public void setHistory(List<HashMap<String, String>> history) {
		this.history = history;
	}
}
