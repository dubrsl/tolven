/**
 * 
 */
package org.tolven.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class ProblemDocumentHistoryAction extends TRIMAction {

	private AccountUser activeAccountUser;
	private MenuData activeMenuData;
	private DocXML activeDocument;
	private TrimEx activeTrim;
	private List<HashMap<String, String>> history;

	public ProblemDocumentHistoryAction() throws Exception {
		super();
		if (getTrim() != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssZ");
				SimpleDateFormat sdfOut = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
				history = new ArrayList<HashMap<String, String>>();
				for (TolvenId item : getTrim().getTolvenEventIds()){
					HashMap<String, String> tempHistory = new HashMap<String, String>();
					//tempHistory.put("user", getLDAPLocal().createTolvenPerson(item.getPrincipal()).getGivenName());
					tempHistory.put("user", item.getPrincipal());
					tempHistory.put("timestamp", sdfOut.format(sdf.parse(item.getTimestamp())));
					tempHistory.put("status", item.getStatus().toUpperCase());
					history.add(tempHistory);
				}

			} catch (Exception e) {
				TolvenLogger.error("Error while populating Problem Document History", e,ProblemDocumentHistoryAction.class);
				throw e;
			}
		} else {
			TolvenLogger.error("Current trim is not initialized.",ProblemDocumentHistoryAction.class);
		}
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
