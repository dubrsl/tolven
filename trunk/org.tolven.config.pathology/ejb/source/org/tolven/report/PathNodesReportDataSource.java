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

public class PathNodesReportDataSource extends TolvenReportDataSource {
	private String laterality;
	private String menuPath1;
	public PathNodesReportDataSource(String menuPath,String menuPath1, String tolvenQueryParameter,String laterality, AccountUser accountUser) {
		super(menuPath, tolvenQueryParameter, accountUser);
		this.laterality = laterality;
		setSortOrder("date01");
		setSortDirection("asc");
		setMenuPath1(menuPath1);
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
         //ctrl.setFilter("string02='"+laterality+"'");
         /*
          * Retrieve the actual MenuData based on the qualifed query and wrap each MenuData with
          * a TolvenReportMap
          */
         ctrl.setSortOrder(getSortOrder());
         ctrl.setSortDirection(getSortDirection());
         List<MenuData> menuDatas = menuBean.findMenuData(ctrl);
         String groupPath = null;
         if(menuDatas != null && menuDatas.size() > 0){
        	 groupPath = menuDatas.get(0).getReferencePath();
        	 groupPath = groupPath.substring(0, groupPath.lastIndexOf(":"));
         }
         for (MenuData menuData : menuDatas) {
			if (menuData.getString02().equals(laterality) && menuData.getReferencePath().indexOf(groupPath) > -1) {
				TolvenReportMap reportMap = newTolvenReportMap(menuData);
				getReportMaps().add(reportMap);
			}
		}
         MenuStructure cmenuStructure = menuBean.findMenuStructure(getAccountUser(), getMenuPath1());
         MenuPath cmenuPath = new MenuPath(getTolvenQueryParameter());
         MenuQueryControl cctrl = new MenuQueryControl();
         cctrl.setAccountUser(getAccountUser());
         cctrl.setMenuStructure(cmenuStructure.getAccountMenuStructure());
         cctrl.setNow(new java.util.Date());
         cctrl.setOffset(0);
         cctrl.setOriginalTargetPath(cmenuPath);
         cctrl.setRequestedPath(cmenuPath);
         //ctrl.setFilter("string02='"+laterality+"'");
         cctrl.setSortOrder(getSortOrder());
         cctrl.setSortDirection(getSortDirection());
         List<MenuData> cmenuDatas = menuBean.findMenuData(cctrl);
         if(cmenuDatas == null || cmenuDatas.size() == 0 ){
        	 return;
         }
         for (MenuData menuData : cmenuDatas) {
			if (menuData.getString02().equals(laterality)) {
				// if additional nodes found consider sentinal nodes from the same group
				if(groupPath != null && menuData.getReferencePath().indexOf(groupPath) > -1){
					TolvenReportMap reportMap = newTolvenReportMap(menuData);
					getReportMaps().add(reportMap);						
				}else if(groupPath == null ){
					TolvenReportMap reportMap = newTolvenReportMap(menuData);
					getReportMaps().add(reportMap);
				}
				break;
			}
		}
         
    }
	public String getMenuPath1() {
		return menuPath1;
	}
	public void setMenuPath1(String menuPath1) {
		this.menuPath1 = menuPath1;
	}
         
}
