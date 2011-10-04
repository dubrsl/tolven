package org.tolven.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.web.MenuAction;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.util.MiscUtils;

import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;

/**
 * Handles data extraction methods for various requirements.
 * 
 * @author valsaraj
 * added on 07/13/2010
 */
public class CCHITMenuAction extends MenuAction {
	private int init = 1;
	
	private MenuDataList menuDataList = null;
	
	public CCHITMenuAction() {
		super();
	}
	
	public List<Map<String, Object>> getFavouriteMedicationsList() {
		List<Map<String, Object>> favouriteMedicationList = new ArrayList<Map<String,Object>>();

		try {
			List<Map<String, Object>> favouriteList = getAllMenuData().get("echr:admin:lists:accountLists");
			for (Map<String, Object> map : favouriteList) {
				if (map.get("Type").equals("medications")) {
					favouriteMedicationList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return favouriteMedicationList;
	}
	
	/**
	 * Returns a map of menu paths under speciifed menu.
	 * 
	 * @author valsaraj
	 * added on 07/13/2010
	 * @param path
	 * @param role
	 * @return menuItemMap
	 */
	public Map<String, String> getMenuItemMap(String path, String role) {
		Map<String, String> menuItemMap = new HashMap<String, String>();
		
		// get menu structure
    	AccountMenuStructure ams = getMenuLocal().findAccountMenuStructure(getTop().getAccountUser().getAccount().getId(), path );
    	MenuStructure ms = getMenuLocal().findMenuStructure(getTop().getAccountUser(), ams);
    	// get its preferred children
    	List<MenuStructure> children = getMenuLocal().findSortedChildren(getTop().getAccountUser(), ams);
    	HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	//ResourceBundle bundle_app = MiscUtils.getResourceBundle(req, ResourceBundleHelper.APPBUNDLEKEY);
    	TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
    	for( MenuStructure item : children ) {
    		try {
	    		if(! "placeholder".equalsIgnoreCase(item.getRole()) && item.getSequence() > 0 ) {
	    			String txt = StringEscapeUtils.escapeHtml(item.getLocaleText(tolvenResourceBundle));
		    		String regExp = "^\\?{3}(.*)\\?{3}$";
		    		
		    		if (Pattern.compile(regExp).matcher(txt).matches()) {
		    			txt = item.getText();
		    		}
		    		
	    			menuItemMap.put(txt, item.getPath());
	    		}
    		}
    		catch (Exception e) {
				
			}
    	}
    	
    	return menuItemMap;
	}
	
	public int getInit() {
		return init;
	}
	
	public void setInit(int init) {
		this.init = init;
	}
	/**
	 * This class used to retrieve MenuData List.
	 * @author Suja
	 * added on 5/29/09
	 */
	class MenuDataList extends HashMap<Object, List<Map<String, Object>>> {
		private MenuLocal menuLocal;
		private long accountId;
		private String contextPath;
		private Date dateNow;
		private AccountUser accountUser;
		public MenuDataList( MenuLocal menuLocal, long accountId, String context, Date dateNow, AccountUser accountUser) {
			this.menuLocal = menuLocal;
			this.accountId = accountId;
			this.contextPath = context;
			this.dateNow = dateNow;
			this.accountUser = accountUser;
		}		
		/**
		 * This function prepare the menudata list and add it to 'MenuDataList'
		 * @param path
		 */ 
		public void buildList(Object path ) {
			try{
				MenuQueryControl ctrl = new MenuQueryControl();
				DateFormat df = new SimpleDateFormat("MM/dd/yy");
				String splitChar =",";
				String parsedPath = path.toString().split(splitChar)[0];			
				
				MenuStructure ms = menuLocal.findMenuStructure(accountId, parsedPath );
				Map<String, Long> nodeValues = new HashMap<String, Long>(10);
				nodeValues.putAll(new MenuPath( contextPath ).getNodeValues());
				ctrl.setMenuStructure( ms );
				ctrl.setNow(dateNow);
				ctrl.setAccountUser(accountUser);
				ctrl.setOriginalTargetPath( new MenuPath(ms.instancePathFromContext ( nodeValues, true )));
				ctrl.setRequestedPath( ctrl.getOriginalTargetPath() );
				if (path.toString().split(splitChar).length>1 && path.toString().split(splitChar)[1]!=""){
					String[] params=path.toString().split(splitChar)[1].split(":");
					String[] param;
					String paramStr;
					long t;
					for (int i = 0; i < params.length; i++) {
						param=params[i].split("=");
						if (param.length>1){
							if (param[0].equals("filter"))
								ctrl.setFilter(param[1]);
							else if (param[0].equals("fromDate"))
								ctrl.setFromDate(df.parse(param[1]));
							else if (param[0].equals("toDate"))
								ctrl.setToDate(df.parse(param[1]));
							else if (param[0].equals("DateFilter")){
								if (param[1].contains("/")){
									ctrl.setFromDate(df.parse(param[1]));
									t = df.parse(param[1]).getTime();
									t += 24 * 60 * 60 * 1000;
									ctrl.setToDate(new Date(t));
								}
								else if (param[1].length()==8){
									paramStr=param[1].substring(4, 6)+"/"+param[1].substring(6, 8)+"/"+param[1].substring(2, 4);
									ctrl.setFromDate(df.parse(paramStr));
									t = df.parse(paramStr).getTime();
									t += 24 * 60 * 60 * 1000;
									ctrl.setToDate(new Date(t));
								}
								
							}else if (param[0].endsWith("Filter")) {
								ctrl.getFilters().put(param[0].substring(0, param[0].length()-6), param[1]);
							}else if (param[0].endsWith("Sort")) {
								ctrl.setSortOrder(param[0].substring(0, param[0].length()-4));
								ctrl.setSortDirection(param[1]);
							}else if (param[0].toLowerCase().equals("limit"))
								ctrl.setLimit(Integer.parseInt(param[1]));
						}
					}
				}
				
				List<Map<String, Object>> list =  menuLocal.findMenuDataByColumns(ctrl).getResults();			
				this.put(path, list);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		public List<Map<String, Object>> get(Object path) {
			if (!this.containsKey(path)) buildList(path);
			return super.get(path);
		}
	}
	
	/**
	 * This function is used to retrieve MenuData List.
	 * Format - <path>,<fieldName1>Filter=<fieldValue1>:<fieldName2>Filter=<fieldValue2>:
	 * 			:fromDate=<FROMDATE>:toDate=<TODATE>:<fieldName>Sort=<sortOrder>:limit=<value>
	 * eg1: echr:patient:specimens:active
	 * eg2: echr:patient:specimens:active,titleFilter=Desc:nameSort=ASC:limit=10
	 * @author Suja
	 * added on 5/29/09
	 * @return MenuDataList
	 * @throws Exception
	 */
	public MenuDataList getAllMenuData() throws Exception {
		if (menuDataList==null) {
			menuDataList = new MenuDataList( this.getMenuLocal(), this.getAccountId(), 
					this.getElement(), getNow(), getTop().getAccountUser());
		}
		return menuDataList;
	}
}
