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

public class PathReceptorsReportDataSource extends TolvenReportDataSource {
	private String laterality;

	public PathReceptorsReportDataSource(String menuPath, String tolvenQueryParameter,String laterality, AccountUser accountUser) {
		super(menuPath, tolvenQueryParameter, accountUser);
		this.laterality = laterality;
		setSortOrder("date01");
		setSortDirection("desc");
		
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
         ctrl.setOriginalTargetPath(menuPath);
         ctrl.setRequestedPath(menuPath);
         //ctrl.setFilter("string03='"+laterality+"'");
         /*
          * Retrieve the actual MenuData based on the qualifed query and wrap each MenuData with
          * a TolvenReportMap
          */
         ctrl.setSortOrder(getSortOrder());
         ctrl.setSortDirection(getSortDirection());
         List<MenuData> menuDatas = menuBean.findMenuData(ctrl);
         String groupPath = null;
         if(menuDatas == null || menuDatas.size() == 0){
        	 return;
         }else{
        	 groupPath = menuDatas.get(0).getReferencePath();
        	 groupPath = groupPath.substring(0, groupPath.lastIndexOf(":"));
         }
         for (MenuData md : menuDatas) {
			if (md.getString03().equals(laterality) && md.getReferencePath().indexOf(groupPath) != -1) {
				TolvenReportMap reportMap = newTolvenReportMap(md);
				getReportMaps().add(reportMap);				
			}
		}
		// }
         
    }
         
}
