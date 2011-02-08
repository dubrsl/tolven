package org.tolven.report;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;

public class PathHistologyReportDataSource extends TolvenReportDataSource {
	private String laterality;

	public PathHistologyReportDataSource(String menuPath, String tolvenQueryParameter,String laterality, AccountUser accountUser) {
		super(menuPath, tolvenQueryParameter, accountUser);
		this.laterality = laterality;
		setSortOrder("long01"); // 1:Invasive 2:DCIS 3:LCIS 4:BENIGN 
		setSortDirection("asc");
		
		// TODO Auto-generated constructor stub
	}
	 /**
     * 
     * @throws NamingException
     */
    protected void fetchTolvenReportMaps() throws NamingException {
    	 InitialContext ctx = new InitialContext();
         setReportMaps(new ArrayList<TolvenReportMap>());
         MenuLocal menuBean = (MenuLocal) ctx.lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal");
         
         /*
          * Find the menustructure represented by the tolvenQuery and place it in a MenuQueryControl
          */
         MenuStructure menuStructure = menuBean.findMenuStructure(getAccountUser(), getTolvenQuery());
         MenuPath menuPath = new MenuPath(getTolvenQueryParameter());
         MenuQueryControl ctrl = new MenuQueryControl();
         ctrl.setAccountUser(getAccountUser());
         ctrl.setMenuStructure(menuStructure.getAccountMenuStructure());
         ctrl.setNow(new java.util.Date());
         ctrl.setOffset(0);
         ctrl.setLimit(3);
         ctrl.setOriginalTargetPath(menuPath);
         ctrl.setRequestedPath(menuPath);
        // ctrl.setFilter("string06='"+laterality+"'");
         ctrl.setSortOrder(getSortOrder());
         ctrl.setSortDirection(getSortDirection());
         List<MenuData> menuDatas = menuBean.findMenuData(ctrl);
         if(menuDatas == null || menuDatas.size() == 0 ){
        	 return;
         }
         for(MenuData md:menuDatas){
        	 if(md.getString06().equals(laterality)){
        		TolvenReportMap reportMap = newTolvenReportMap(md);
             	getReportMaps().add(reportMap);
             	break;
        	 }
         }
        // }
         
    }
         
}
