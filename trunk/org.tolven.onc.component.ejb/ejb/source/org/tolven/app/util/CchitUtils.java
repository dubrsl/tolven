package org.tolven.app.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;

/**
 * This class contains methods that are used commonly in cchit 
 * 
 * @author Nevin <nevin.pathadan@cyrusxp.com>
 * @since File available since Release 0.0.17
 */
/*
=============================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
=============================================================================================================
1    	|  03/14/2011           |    Nevin Pathadan 	|     Initial Version 	
=============================================================================================================
*/
public class CchitUtils {
	/**
     * This function is used to get an initialized MenuQueryControl object.
     * 
     * added on 03/14/2011
	 * @author Nevin
     * @param String menuPath 
     * @param MenuLocal menuBean
     * @param AccountUser accountUser
     * @param List<MenuPath> contextList
     * @return MenuQueryControl - MenuQueryControl object
     */
	public static MenuQueryControl getMenuQueryControl(String menuPath, MenuLocal menuBean, AccountUser accountUser,
			List<MenuPath> contextList) {
		MenuStructure ms = menuBean.findMenuStructure(accountUser.getAccount().getId(), menuPath);
		MenuQueryControl ctrl = new MenuQueryControl();
		ctrl.setMenuStructure(ms);
		ctrl.setNow(new Date());
		ctrl.setAccountUser(accountUser);
		Map<String, Long> nodeValues = new HashMap<String, Long>(10);
		nodeValues = ((MenuPath) contextList.get(0)).getNodeValues();
		ctrl.setOriginalTargetPath(new MenuPath(ms.instancePathFromContext(nodeValues, true)));
		ctrl.setRequestedPath(ctrl.getOriginalTargetPath());
		ctrl.setActualMenuStructure(ms);
		return ctrl;
	}
	/**
     * This function is used to get an initialized MenuQueryControl object.
     * 
     * added on 03/15/2011
	 * @author Nevin
     * @param String menuPath 
     * @param AccountUser accountUser
     * @return MenuQueryControl - MenuQueryControl object
     */
	public static MenuQueryControl getMenuQueryControl(String path, AccountUser accountUser) {
		MenuQueryControl ctrl = new MenuQueryControl();
		try {
			MenuPath menuPath = new MenuPath(path);
			ctrl.setMenuStructurePath(path);		
			ctrl.setOriginalTargetPath(menuPath);	
			ctrl.setAccountUser(accountUser);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ctrl;
	}
}
