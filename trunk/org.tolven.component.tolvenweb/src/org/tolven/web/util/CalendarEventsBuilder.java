package org.tolven.web.util;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersion;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.TolvenRequest;
import org.tolven.core.entity.AccountUser;

public class CalendarEventsBuilder {
	private String path = null;
	private MenuLocal menuBean = null;
	private Date fromDate = null;
	private Date toDate = null;
	private String interval = null;
	private String getVersions = null;
	private SimpleDateFormat dateFormat =  new SimpleDateFormat("MMM dd,yyyy hh:mm");
	
	AccountUser getAccountUser() {
	    return TolvenRequest.getInstance().getAccountUser();
	}
	
	public CalendarEventsBuilder(MenuLocal menuBean, HttpServletRequest req) {
		path = req.getParameter("element");
		getVersions = req.getParameter("getVersions");
		this.menuBean = menuBean;
		fromDate = parseDateStr(Integer.parseInt(req.getParameter("fromDate")));
		toDate = parseDateStr(Integer.parseInt(req.getParameter("toDate")));
		interval = req.getParameter("interval");
	}

	public void buildCalendarEvents(Writer writer) throws IOException {
	
		MenuStructure ms = menuBean.findMenuStructure(getAccountUser(),
				new MenuPath(path).getPath());
		List<MenuStructure> subMenus = menuBean.findMenuChildren(
		        getAccountUser(), ms.getAccountMenuStructure());
		MenuPath targetMenuPath = new MenuPath(path);
		//StringBuffer calendarEventData = new StringBuffer("{events:[");
		writer.write("{events:[");
		int i = 0;
		StringBuffer subMenusStr = new StringBuffer();
		for (MenuStructure menu : subMenus) {
		
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setAccountUser(getAccountUser());
			ctrl.setMenuStructure(menu.getAccountMenuStructure());
			//ctrl.setNow((now));
			//ctrl.setFromDate(fromDate);
			//ctrl.setToDate(toDate);
			ctrl.setOffset(0);
			ctrl.setOriginalTargetPath(targetMenuPath);
			ctrl.setRequestedPath(targetMenuPath);
			List<MenuData> dataitems = menuBean.findMenuData(ctrl);
			//				if(dataitems.size()==0)
			//					log.debug("Data not found for "+targetMenuPath+":"+band.getText());
			for (MenuData dataItem : dataitems) {
				if(dataItem.getDate01() == null)
					continue;
				else if(!isDuration(dataItem)){
					if(dataItem.getDate01().after(fromDate) && dataItem.getDate01().before(toDate)){ // non-durable event
						if(i > 0)
							writer.write(",");
						convertToJSON(dataItem, menu.getText(),writer,dataItem.getDate01(),null,interval);
						i++;
					}
				}else{ // Duration item
					//  -----------------------event
					//         ------ 			range
					if(dataItem.getDate01().before(fromDate) && dataItem.getDate02().after(toDate)){ // if the event scope is broder than selected dates 
						if(i > 0)
							writer.write(",");
						convertToJSON(dataItem, menu.getText(),writer,fromDate,toDate,interval);
						i++;
					}
					else if(dataItem.getDate01().before(fromDate) && dataItem.getDate02().before(toDate) && dataItem.getDate02().after(fromDate) ){
						//  -----------------------event
						//         		-------------------------- 			range
						if(i > 0)
							writer.write(",");
						convertToJSON(dataItem, menu.getText(),writer,fromDate,dataItem.getDate02(),interval);
						i++;
					}else if(dataItem.getDate01().after(fromDate) && dataItem.getDate01().before(toDate) && dataItem.getDate02().after(toDate) ){
						// 			 -----------------------event
						//   -------------------------- 			range
						if(i > 0)
							writer.write(",");
						convertToJSON(dataItem, menu.getText(),writer,dataItem.getDate01(),toDate,interval);
						i++;
					}
				}				
			}
			if(menu.getRole().equals("entry") && getVersions != null && getVersions.equals("true")){
				MenuDataVersion mdv = menuBean.findMenuDataVersion(getAccountUser().getAccount().getId(),path+":"+menu.getNode());
				long version = 0;
				if(mdv != null)
					version = mdv.getVersion();
				if(subMenusStr.toString().length() > 0)
					subMenusStr.append(",");
				subMenusStr.append(path+":"+menu.getNode()+"="+version);
			}
			
		}	
		writer.write(String.format(Locale.US, "],menus:'%s'}",subMenusStr.toString()));
		
	}
	private void convertToJSON(MenuData dataitem,String type,Writer writer,Date start,Date end,String interval) throws IOException {
		String ppath = dataitem.getReferencePath();//md.getReferencePath( );
		MenuPath refpath = new MenuPath( ppath );
		MenuStructure refms = menuBean.findMenuStructure(getAccountUser(), refpath.getPath() );//accountId, refpath.getPath()
		if( getAccountUser().getAccount().isEnableBackButton() == true && refms.getDefaultPathSuffix() != null ){
			ppath += refms.getDefaultPathSuffix();
		}
		if(end == null){
			writer.write(String.format(Locale.US, "{start:'%s'",dateFormat.format(dataitem.getDate01())));
			writer.write(String.format(Locale.US, ",title:'%s'",StringUtils.forHTML(dataitem.getString01())));
			writer.write(String.format(Locale.US, ",path:'%s',accountUser:'%s'",ppath,getAccountUser()));
			writer.write(String.format(Locale.US, ",type:'%s'}",type));
		}else{
			int i = 0;
			Calendar _start = Calendar.getInstance();
			Calendar _end = Calendar.getInstance();
			_start.setTime(start);
			_end.setTime(end);
			Calendar _actStart = Calendar.getInstance();
			_actStart.setTime(dataitem.getDate01());
			_start.set(Calendar.HOUR,_actStart.get(Calendar.HOUR));
			do{
				if(i > 0)
					writer.write(",");
				writer.write(String.format(Locale.US, "{start:'%s'",dateFormat.format(_start.getTime())));
				writer.write(String.format(Locale.US, ",end:'%s',isDuration:'true'",dateFormat.format(dataitem.getDate02())));
				writer.write(String.format(Locale.US, ",title:'%s'",StringUtils.forHTML(dataitem.getString01())));
				writer.write(String.format(Locale.US, ",path:'%s',accountUser:'%s'",ppath,getAccountUser()));
				writer.write(String.format(Locale.US, ",type:'%s'}",type));
				i++;
				// go to next sunday
				if(interval.equalsIgnoreCase("DAY"))
					_start.set(Calendar.DATE, _start.get(Calendar.DATE) + 1);
				else
					_start.set(Calendar.DATE, _start.get(Calendar.DATE) + (8 -_start.get(Calendar.DAY_OF_WEEK)));
				}while (_start.before(_end));
			
		}
			
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	private Date parseDateStr(int yearMonthDate){
	 int date = yearMonthDate%100;
	 yearMonthDate =yearMonthDate/100;
	 int month = yearMonthDate%100;
     int year = yearMonthDate/100;
	 Calendar cal = Calendar.getInstance();
	 cal.set(year,month-1,date);
	 return cal.getTime();
	}
	private boolean isDuration(MenuData md){
		if(md.getDate01() != null && md.getDate02() != null)
			return true;
		else
			return false;
	}

}
