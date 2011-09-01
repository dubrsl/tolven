/*
 *  Copyright (C) 2008 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.web.util;

import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSAction;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersion;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.locale.SessionResourceBundleFactory;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.logging.TolvenLogger;
import org.tolven.util.DateExtent;
import org.tolven.web.security.GeneralSecurityFilter;
/**
 * Util class to create JSON data for Simile Timeline
 * 
 * @author Srini Kandula
 */
public class SimileDataBuilder {
	private AccountUser activeAccountUser = null;
	private String path = null;
	private MenuLocal menuBean = null;
	private Map<String,String> iconImages = new HashMap<String, String>();
	private Map<String,String> barColors = new HashMap<String, String>();
	private Date now = null;
	private HttpServletRequest req;
	
	public HttpServletRequest getReq() {
		return req;
	}

	public void setReq(HttpServletRequest req) {
		this.req = req;
	}

	public SimileDataBuilder(MenuLocal menuBean, HttpServletRequest req) {
		path = req.getParameter("element");
		activeAccountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
		this.menuBean = menuBean;
		now = (Date) req.getAttribute("tolvenNow");
		this.req = req;
	}
	
	/**
	 * Translate timeline interval to a code that Simile understands.
	 * @param interval
	 * @return
	 */
	public static int intervalCodeFromString( String interval ) {
		if (interval==null || interval.trim().length()<1) return 6;
		if ("day".equalsIgnoreCase(interval)) return 4;
		if ("week".equalsIgnoreCase(interval)) return 5;
		if ("month".equalsIgnoreCase(interval)) return 6;
		if ("year".equalsIgnoreCase(interval)) return 7;
		return 6;
	}
	/**
	 * Translate timeline interval to a code that Simile understands.
	 * @param interval
	 * @return
	 */
	public static String stringFromIntervalCode( int code ) {
		final String codes[] = {"millisecond", "second","minute","hour","day","week","month","year","decade","century","millennium"};
		return codes[code];
	}
	
	public void buildJSONData(Writer writer) {
		MenuStructure timelineMS = menuBean.findMenuStructure(activeAccountUser, new MenuPath(path).getPath());
		List<MenuStructure> bands = menuBean.findSortedChildren(activeAccountUser, timelineMS.getAccountMenuStructure());
		MenuPath targetMenuPath = new MenuPath(path);
		int bandCount = 0;
		DateExtent dateExtent = new DateExtent();
		try {
		  writer.write("\n {bar_info:[");
		  boolean firstTime = true;
			for (MenuStructure band : bands) {
				if(!band.getVisible().equalsIgnoreCase("true")){ // if the menu's visiblity is false don't display bar for it.
				  continue;
				}
				MenuQueryControl ctrl = new MenuQueryControl();
				ctrl.setAccountUser(activeAccountUser);
				ctrl.setMenuStructure(band.getAccountMenuStructure());
				ctrl.setNow((now) );
				ctrl.setOffset(0);
				ctrl.setOriginalTargetPath(targetMenuPath);
				ctrl.setRequestedPath(targetMenuPath);
				int interval = intervalCodeFromString(band.getInterval());
				long _thisChildCount = menuBean.countMenuData(ctrl); 
				MenuDataVersion mdv = menuBean.findMenuDataVersion(activeAccountUser.getAccount().getId(),path+":"+band.getNode());
				
				if(mdv!=null) {
					if (firstTime) {
						firstTime = false;
					} else {
						writer.write(","); // to separate bands
					}
				writer.write(String.format(Locale.US, "{barPath:'%s',barColor:'%s'",path+":"+band.getNode(),getBarColor(band)));
				writer.write(String.format(Locale.US, ",repeating:'%s',dataVersion:'%s'",band.getRepeating(),((mdv== null)?0:mdv.getVersion())));
				TolvenResourceBundle tolvenResourceBundle = SessionResourceBundleFactory.getBundle();
				writer.write(String.format(Locale.US, ",interval:'%s',barName:'%s'", interval,ResourceBundleHelper.getString(tolvenResourceBundle, band.getRepeating().replaceAll(":","."))));
				if(_thisChildCount > 0 && mdv != null) {// check dates only if the band has data
					dateExtent.applyDate(mdv.getMinDate());
					dateExtent.applyDate(mdv.getMaxDate());
				}
				
				List<MSAction> actions = band.getAccountMenuStructure().getActions();
				writer.write(",actions:[");
				int i=actions.size();
				for(MSAction action:actions){
					MenuPath menuPath = new MenuPath(action.getPath(), new MenuPath(
							path));
					String menuPathStr = menuPath.getPathString();
					AccountMenuStructure actionMS = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), menuPath.getPath() );
					MenuQueryControl actionCtrl = new MenuQueryControl();
					ctrl.setAccountUser(activeAccountUser);
					actionCtrl.setMenuStructure( actionMS);
					actionCtrl.setNow( new Date() );
					actionCtrl.setOriginalTargetPath( menuPath );
					actionCtrl.setRequestedPath( menuPath );
			    	boolean hasItems =  menuBean.countMenuData(ctrl)>0;
					writer.write(String.format(Locale.US, "{actionName:'%s',actionPath:'%s',hasItems:'%s'}",ResourceBundleHelper.getString(tolvenResourceBundle, action.getRepeating().replaceAll(":",".")),menuPathStr,hasItems));
			    	--i;
					if(i>0)
						writer.write(",");
				}
				writer.write("]}"); //actions and band close
				bandCount++;
				}
			}
			writer.write("]"); // band_info close
			SimpleDateFormat dateFormat =  new SimpleDateFormat("MMM dd,yyyy hh:mm");
			writer.write(String.format(Locale.US, ",barcount:'%s'",bandCount));
			if (dateExtent.isValid()) {
				writer.write(String.format(Locale.US, ",minDate:'%s',maxDate:'%s'",dateFormat.format(dateExtent.getMinDate()),dateFormat.format(dateExtent.getMaxDate())));			
			} else {
				writer.write(String.format(Locale.US, ",hasData:'false'"));
			}
			writer.write( "}" );
			//TolvenLogger.info("Done writing timeline data", SimileDataBuilder.class);
	  } catch (Exception e) {
		throw new RuntimeException( "[buildJSONData] problem building timeline band " + path, e);
	 }
	}
	
	private void convertToJSON(MenuData dataitem, String nodeName, Writer writer) throws Exception {
	  try{	
	    String ppath = dataitem.getReferencePath();
		MenuStructure refms = dataitem.getMenuStructure();
		if( activeAccountUser.getAccount().isEnableBackButton() == true && refms.getDefaultPathSuffix() != null ){
			ppath += refms.getDefaultPathSuffix();
		}		
		String image_icon = "../scripts/simile/images/" + getIcon(nodeName);
		writer.write(String.format(Locale.US, "{start:'%s',", dataitem.getDate01().toString()));
		if(dataitem.getDate02() != null){
			writer.write(String.format(Locale.US, "end:'%s',isDuration:'true',", dataitem.getDate02().toString()));							  
		}
		writer.write(String.format(Locale.US, "path:'%s',title:'%s',icon:'%s'}",ppath,StringUtils.forHTML(dataitem.getString01()),image_icon));
		
	  }catch (Exception e) {
		throw e;
	  }
	}
	
	private String getIcon(String nodeName) {
		if (iconImages.isEmpty()) {
			iconImages.put("probsum", "blue-circle.png");
			iconImages.put("resultsum", "dark-green-circle.png");
			iconImages.put("obssum", "dark-red-circle.png");
			iconImages.put("medsum", "dull-blue-circle.png");
			iconImages.put("apptsum", "dull-red-circle.png");
			iconImages.put("remindersum", "gray-circle.png");
			iconImages.put("allergysum", "dull-green-circle.png");
			iconImages.put("dxsum", "red-circle.png");
			iconImages.put("procsum", "dull-black-circle.png");
		}
		return "red-circle.png";
//		return iconImages.get(nodeName).toString();
	}
// Use style in metadata (menuStructure) <band> under <timeline> otherwise, we have some old defaults laying around.
	private String getBarColor(MenuStructure ms ) {
		if (ms.getStyle()!=null) {
			return ms.getStyle();
		}
		if (barColors.isEmpty()) {
			barColors.put("probsum", "#99FFCC");
			barColors.put("resultsum", "#BDFFBD");
			barColors.put("obssum", "#FFCC33");
			barColors.put("medsum", "#CCFFFF");
			barColors.put("apptsum", "#D4FFD4");
			barColors.put("remindersum", "#FF99CC");
			barColors.put("allergysum", "#FFFFAD");
			barColors.put("dxsum", "#99FFFF");
			barColors.put("procsum", "#E0E0E0");
		}
		Object colorObj = barColors.get(ms.getNode()); 
		if(colorObj != null)
			return colorObj.toString();
		else	
		return "#FF99CC"; // default to white if the style is not found
	}
	public void buildTimlePreferences(Writer writer){
		
		MenuStructure ms = menuBean.findMenuStructure(activeAccountUser, new MenuPath(path).getPath());
		List<MenuStructure> menus = menuBean.findSortedChildren(
		        activeAccountUser, ms.getAccountMenuStructure());
		MenuPath targetMenuPath = new MenuPath(path);
		int bandCount = 0;
		try{
		writer.write("{settings:[");	
		for (MenuStructure band : menus) {
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setAccountUser(activeAccountUser);
			ctrl.setMenuStructure(band.getAccountMenuStructure());
			ctrl.setNow((now) );
			ctrl.setOffset(0);
			ctrl.setOriginalTargetPath(targetMenuPath);
			ctrl.setRequestedPath(targetMenuPath);
			ctrl.setSortDirection("DESC");
			ctrl.setSortOrder("date01");
			int interval = intervalCodeFromString(band.getInterval());
			if(bandCount > 0)
				writer.write(",");
			TolvenResourceBundle tolvenResourceBundle = SessionResourceBundleFactory.getBundle();
			writer.write(String.format(Locale.US, "{barName:'%s',barPath:'%s',",ResourceBundleHelper.getString(tolvenResourceBundle, band.getRepeating().replaceAll(":",".")),band.getNode()));
			writer.write(String.format(Locale.US, "barColor:'%s',visible:'%s',interval:'%s'}",getBarColor(band),band.getVisible(),interval));
			bandCount++;
		}
		writer.write(String.format(Locale.US, "],barCount:'%s'}",bandCount));
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void getBandEvents(Writer writer) {
		MenuPath targetMenuPath = new MenuPath(path);
		MenuStructure bandMS = menuBean.findMenuStructure(activeAccountUser, targetMenuPath.getPath());
		DateExtent originalExtent = new DateExtent();
		DateExtent currentExtent = new DateExtent();
		try {
			MenuDataVersion bandMDV = menuBean.findMenuDataVersion(activeAccountUser.getAccount().getId(),path);
			
			SimpleDateFormat smf = new SimpleDateFormat("MM/dd/yyyy");
			//get the dates for the band on the browser
			String bandMinDateStr = getReq().getParameter("minDate");
			String bandMaxDateStr = getReq().getParameter("maxDate");
			boolean extended = false;
			//refresh band request will have dates passed from browser
			//check if the dates range have been changed
			if(isValidDate(bandMinDateStr) && isValidDate(bandMaxDateStr)){
				currentExtent.applyDate(smf.parse(bandMinDateStr));
				currentExtent.applyDate(smf.parse(bandMaxDateStr));
				extended = currentExtent.applyDate(bandMDV.getMinDate()); 
				extended |= currentExtent.applyDate(bandMDV.getMaxDate());
			}else{
				currentExtent.applyDate(bandMDV.getMinDate());
				currentExtent.applyDate(bandMDV.getMaxDate());
			}
			//check if the dates for the band have been extended
			if (extended) {
					TolvenLogger.info("Timeline needs to be refreshed due to path " + path, SimileDataBuilder.class);
					writer.write("refreshTimeline");
				} else if (!currentExtent.isValid()) {
					TolvenLogger.info("Timeline date range not found in getBandEvents() for band " + path, SimileDataBuilder.class);
					writer.write("NoData");
				} else {
					writer.write(String.format(Locale.US, "{dateTimeFormat:'iso8601',barPath:'%s',barColor:'%s'",path+":"+bandMS.getNode(),getBarColor(bandMS)));
					writer.write(String.format(Locale.US, ",repeating:'%s'",bandMS.getRepeating()));
					TolvenResourceBundle tolvenResourceBundle = SessionResourceBundleFactory.getBundle();
					writer.write(String.format(Locale.US, ",barName:'%s'", ResourceBundleHelper.getString(tolvenResourceBundle, bandMS.getRepeating())));
					writer.write(String.format(Locale.US, ",minDate:'%s',maxDate:'%s'",
						smf.format(currentExtent.getMinDate()),
						smf.format(currentExtent.getMaxDate()) ));
					writer.write(",events:[");				
				   	MenuQueryControl ctrl = new MenuQueryControl();
					ctrl.setAccountUser(activeAccountUser);
					ctrl.setMenuStructure(bandMS.getAccountMenuStructure());
					ctrl.setNow((now) );
					ctrl.setOffset(0);
					ctrl.setOriginalTargetPath(targetMenuPath);
					ctrl.setRequestedPath(targetMenuPath);
					ctrl.setSortDirection("DESC");
					ctrl.setSortOrder("date01");
					List<MenuData> dataitems = menuBean.findMenuData(ctrl);
					boolean firstTime = true;
					for (MenuData md : dataitems) {					
						if (firstTime) {
							firstTime = false;
						} else {
							writer.write(",");
						}
						if(md.getDate01()!= null){
							convertToJSON(md, bandMS.getNode(), writer);
						}			
					}
					writer.write("]}");// close events	
			}
		}catch (Exception e) {
			throw new RuntimeException( "Error building timeline band for " +  path, e );
		}
	}
	public boolean isValidDate(String inDate) {

	    if (inDate == null)
	      return false;

	    //set the format to use as a constructor argument
	    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	    
	    if (inDate.trim().length() != dateFormat.toPattern().length())
	      return false;

	    dateFormat.setLenient(false);
	    
	    try {
	      //parse the inDate parameter
	      dateFormat.parse(inDate.trim());
	    }
	    catch (ParseException pe) {
	      return false;
	    }
	    return true;
	  }
}
