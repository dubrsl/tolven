/*
 *  Copyright (C) 2006 Tolven Inc 
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
/**
 * This file contains EmergencyAccountAjaxServlet.
 *
 * @package org.tolven.ajax
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.ajax;

import java.io.StringReader;
import java.io.Writer;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.tolven.app.CCHITLocal;
import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimCreatorLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.entity.DocXML;
import org.tolven.process.RetrieveSharingDetails;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.trim.ex.TrimEx;

/**
 * This bean is used to process emergency access account related operations.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 01/21/2011          	| Valsaraj Viswanathan 	| Initial Version 	
2		| 02/09/2011			| Valsaraj Viswanathan	| Added synchronizeWithEmergencyAccount.ajaxea.
==============================================================================================================================================
*/
public class EmergencyAccountAjaxServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String TRIMns = "urn:tolven-org:trim:4.0";
	
	@EJB 
	private CCHITLocal cchitBean;
	@EJB 
	private MenuLocal menuBean;	
	@EJB 
	private CreatorLocal creatorBean;
	@EJB 
	private DocumentLocal docBean;
	@EJB 
	private DocProtectionLocal docProtectionBean;
	@EJB
    private TolvenPropertiesLocal propertyBean;
	@EJB
	private XMLLocal xmlBean;
	@EJB 
	private TrimCreatorLocal trimCreatorBean;

	Logger log = Logger.getLogger(this.getClass());
	public void init(ServletConfig config) throws ServletException {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		try {
			String uri = req.getRequestURI();
			AccountUser activeAccountUser = (AccountUser) req.getAttribute("accountUser");
			resp.setContentType("text/xml");
			resp.setCharacterEncoding("UTF-8");
		    resp.setHeader("Cache-Control", "no-cache");
		    Writer writer=resp.getWriter();
	    	Date now = (Date) req.getAttribute("tolvenNow");
	    	
	    	/**
			 * Retrieves emergency access account email.
			 * 
			 * @author Valsaraj
			 * added on 01/21/2011
			 * @param accountId - account id
			 */
	    	if (uri.endsWith("findEmergencyAccountMail.ajaxea")) {
                String email = TolvenSSO.getInstance().getTolvenPersonList("mail", req).get(0);
                writer.write(email);
	    		req.setAttribute("activeWriter", writer);
	        } 
	    	/**
			 * Synchronizes Procedures, Drug Allergies, Medications, Problems, Lab orders with emergency access account.
			 * 
			 * @author Valsaraj
			 * added on 02/09/2011
			 */
	    	else if (uri.endsWith("synchronizeWithEmergencyAccount.ajaxea")) {
	    		String emergencyAccountDetails  = req.getParameter("emergencyAccountDetails");
				log.debug("Synchronizing Laborders...........");
				List<MenuData> labOrderMdList = getMenuBean().findMenuData(getMenuQueryControl("echr:patient:orders:labResultsList", activeAccountUser));
				for (MenuData mdPrior : labOrderMdList) {
					reviseAndSubmitTrim(mdPrior, activeAccountUser, now, emergencyAccountDetails,req);
				}
				log.debug("Sync Completed for Laborders...........");
			
				log.debug("Synchronizing Drug Allergies...........");
				List<MenuData> allergiesMdList = getMenuBean().findMenuData(getMenuQueryControl("echr:patient:allergies:active", activeAccountUser));
				for (MenuData mdPrior : allergiesMdList) {
					reviseAndSubmitTrim(mdPrior, activeAccountUser, now, emergencyAccountDetails,req);
				}
				log.debug("Synchronizing Completed for Drug Allergies...........");
			
				log.debug("Synchronizing Problems...........");
				List<MenuData> problemsMdList = getMenuBean().findMenuData(getMenuQueryControl("echr:patient:problems:active", activeAccountUser));
				for (MenuData mdPrior : problemsMdList) {
					reviseAndSubmitTrim(mdPrior, activeAccountUser, now, emergencyAccountDetails,req);
				}
				log.debug("Sync Completed for Problems...........");
				
				log.debug("Synchronizing Medications...........");
				List<MenuData> medicationsMdList = getMenuBean().findMenuData(getMenuQueryControl("echr:patient:medications:active", activeAccountUser));
				for (MenuData mdPrior : medicationsMdList) {
					log.debug("~~~~~~~Sharing " + mdPrior.getString01() + " with Emergency Access Account"); 
					reviseAndSubmitTrim(mdPrior, activeAccountUser, now, emergencyAccountDetails,req);
				}
				log.debug("Sync Completed for Medications...........");
				log.debug("Synchronizing Procedures...........");
				List<MenuData> proceduresMdList = getMenuBean().findMenuData(getMenuQueryControl("echr:patient:procedures:pxList", activeAccountUser));
				for (MenuData mdPrior : proceduresMdList) {
					log.debug("~~~~~~~Sharing " + mdPrior.getString01() + " with Emergency Access Account"); 
					reviseAndSubmitTrim(mdPrior, activeAccountUser, now, emergencyAccountDetails,req);
				}
				log.debug("Sync Completed for Procedures...........");
				writer.write("Success");
				return;
			}
		} catch (Exception e) {
			throw new ServletException("Exception thrown in EmergencyAccountAjaxServlet", e);
		}
	}
	
	/**
	 * Revises and resubmits the trim corresponding to MenuData mdPrior
	 * 
	 * @author Nevin
	 * @param MenuData mdPrior
	 * @param AccountUser activeAccountUser
	 * @param Date now
	 * @param String emergencyAccountDetails
	 * added on 02/11/2011
	 * @throws Exception 
	 */
	private void reviseAndSubmitTrim(MenuData mdPrior, AccountUser activeAccountUser, Date now, String emergencyAccountDetails,HttpServletRequest req) throws Exception {
		String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        PrivateKey userPrivateKey = TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm);
	    
		MenuData mdNew = getCreatorBean().createTRIMEvent(mdPrior, activeAccountUser, "reviseActive",now,userPrivateKey);
		DocXML docXML = (DocXML) getDocBean().findDocument(mdNew.getDocumentId());
		String trimString = getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
		TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader(trimString));
		RetrieveSharingDetails.insertEmergencyAccountDetails(trim, activeAccountUser, emergencyAccountDetails);
		getCreatorBean().marshalToDocument(trim, docXML);
		getTrimCreatorBean().submitTrim(trim, mdNew.getPath(),activeAccountUser, now);
	}

	/**
	 * Returns MenuQueryControl
	 * 
	 * @author Nevin
	 * added on 02/11/2011
	 */
	private MenuQueryControl getMenuQueryControl(String path, AccountUser accountUser) {
		MenuQueryControl ctrl = new MenuQueryControl();
		MenuPath menuPath = new MenuPath(path);
		ctrl.setMenuStructurePath(path);		
		ctrl.setOriginalTargetPath(menuPath);	
		ctrl.setAccountUser(accountUser);
		return ctrl;
	}

	/**
	 * Returns CCHITBean instance.
	 * @return cchitBean
	 */
	public CCHITLocal getCchitBean() {
		return cchitBean;
	}

	/**
	 * Returns MenuBean instance.
	 * @return menuBean
	 */
	public MenuLocal getMenuBean() {
		return menuBean;
	}

	/**
	 * Returns CreatorBean instance.
	 * @return creatorBean
	 */
	public CreatorLocal getCreatorBean() {
		return creatorBean;
	}
	/**
	 * @return the docBean
	 */
	public DocumentLocal getDocBean() {
		return docBean;
	}
	/**
	 * @return the docProtectionBean
	 */
	public DocProtectionLocal getDocProtectionBean() {
		return docProtectionBean;
	}
	/**
	 * @return the docProtectionBean
	 */
	public TrimCreatorLocal getTrimCreatorBean() {
		return trimCreatorBean;
	}
}
