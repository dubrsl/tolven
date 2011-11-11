package org.tolven.ajax;

// import org.tolven.app.EPrescriptionLocal;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.tolven.app.AllergyVO;
import org.tolven.app.CCHITLocal;
import org.tolven.app.CreatorLocal;
import org.tolven.app.DrugVO;
import org.tolven.app.FDBInterface;
import org.tolven.app.MedicationDrug;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.entity.DocXML;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

import org.tolven.trim.ActRelationship;
import org.tolven.trim.CE;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ActRelationshipsMap;
import org.tolven.trim.ex.TSEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

/**
 * 
 */

/**
 * @author mohammed
 * 
 */
public class FDBAjaxServlet extends HttpServlet {

	private static final int POPUP_MAX_ROWS = 14;
	/**
	 * Variable to hold the value of trim header
	 */
	private static final String TRIMns = "urn:tolven-org:trim:4.0";
	private static final String DRUG_IN_FORMULARY_LIST = "Yes";
	private static final String DRUG_NOT_IN_FORMULARY_LIST = "No";

	/**
	 * Default Constructor
	 */
	public FDBAjaxServlet() {
	}

	/**
	 * Variable to hold the Fdb Bean
	 */
	private @EJB FDBInterface fdbBean;
	/**
	 * Variable to hold the creator Bean
	 */
	@EJB
	private CreatorLocal creatorBean;
	/**
	 * Variable to hold the menu Local
	 */
	@EJB
	private MenuLocal menuBean;
	/**
	 * Variable to hold the Document Local
	 */
	@EJB
	private DocumentLocal docBean;
	/**
	 * Variable to hold the Doc Protection Local
	 */
	@EJB
	private DocProtectionLocal docProtectionBean;
	/**
	 * Variable to hold the XML Local
	 */
	@EJB
	private XMLLocal xmlBean;
	/**
	 * Variable to hold the Trim Local
	 */
	@EJB
	private TrimLocal trimBean;
	
    @EJB
	private CCHITLocal cchitBean;
	
    @EJB
    private TolvenPropertiesLocal propertyBean;
    
	
	
	// private EPrescriptionLocal epBean;
	/**
	 * Variable to hold the context
	 */
	private String context;
	/**
	 * Init Method
	 */
	public void init(ServletConfig config) throws ServletException {

		
	}
	
	private int calculatePopupRowId(int actionType, int total, int rowId) {
		switch (actionType) {
		case 1: //first
			rowId = 0;
			break;
		case 2: //previous
			if(rowId >=POPUP_MAX_ROWS)
			rowId-= POPUP_MAX_ROWS;
			break;
		case 3: //next
			if(rowId <total && total > POPUP_MAX_ROWS)
			rowId+= POPUP_MAX_ROWS;
			break;
		case 4: //last
			if(total > POPUP_MAX_ROWS)
			rowId= total - POPUP_MAX_ROWS ;
			break;
		default:
			rowId = 0;				
		}
		return rowId;
	}
		
	/**
	 * doGet Method
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String uri = req.getRequestURI();
			AccountUser activeAccountUser = (AccountUser) req
					.getAttribute("accountUser");
			resp.setContentType("text/xml");
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Cache-Control", "no-cache");
			Writer writer = resp.getWriter();
			Date now = (Date) req.getAttribute("tolvenNow");
	        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
	        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
	        PrivateKey userPrivateKey = sessionWrapper.getUserPrivateKey(keyAlgorithm);
			
			String element = req.getParameter("element");
			
			TrimFactory trimFactory = new TrimFactory();
			if (uri.endsWith("fdbCollect.ajaxfdb")) {
				String isDrugInFormularyList = DRUG_NOT_IN_FORMULARY_LIST;
				MenuData md = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,new StringReader(trimString));
				String drugCode = req.getParameter("drug_code");
				String priorMeds = req.getParameter("priorMed");
				String priorAllergies = req.getParameter("priorAlleg");
				ExpressionEvaluator evaluator = new ExpressionEvaluator();
				evaluator.addVariable("trim", trim);
				String dob = (String)evaluator.evaluate("#{trim.act.participation['subject'].role.player.livingSubject.birthTime.TS.value}");
				//ArrayList<String> resultList = null;
				FdbDispensable dispensable = fdbBean.findFdbDispensable(new Integer(drugCode));
				ArrayList<String> resultList = fdbBean.searchDrugExhaustive(Long.parseLong(drugCode) , priorMeds , priorAllergies , dob);
				List<Map<String, Object>> items = cchitBean.findAllMenuDataList("echr:admin:drugFormulary:all", "echr:drugFormulary", null, activeAccountUser);
				
				if (((ActEx) trim.getAct()).getRelationship().get("isDrugFromulary") != null ) {
					for (ObservationValueSlot ovsStrength :((ActEx) trim.getAct()).getRelationship().get("isDrugFromulary")
							.getAct().getObservation().getValues()) {
						if (ovsStrength.getLabel().getValue().equals("Drug Formulary") && items != null && items.size() > 0) {
							for (Map<String, Object> map : items) {
								// Using medID(drugCode) check whether the drug is in formulary list.
								if (map.get("Code") != null && map.get("Code").equals(drugCode)) {
									isDrugInFormularyList = DRUG_IN_FORMULARY_LIST;
									ovsStrength.getST().setValue(DRUG_IN_FORMULARY_LIST);
									break;
								} else {
									ovsStrength.getST().setValue(DRUG_NOT_IN_FORMULARY_LIST);
								}
							}
						} else {
							ovsStrength.getST().setValue(DRUG_NOT_IN_FORMULARY_LIST);
						}
					}
				}
				// Setting the drug code in the trim.
				if (((ActEx) trim.getAct()).getRelationship().get("medID") != null ) {
					for (ObservationValueSlot ovsMedID :((ActEx) trim.getAct()).getRelationship().get("medID")
							.getAct().getObservation().getValues()) {
						if (ovsMedID.getLabel().getValue().equals("MedID")) {
							ovsMedID.getST().setValue(drugCode);
						}
					}
				}		
				
				if(null != resultList & resultList.size() > 0){
						String fdbResponce = resultList.get(0);
						if(null != fdbResponce && fdbResponce.trim().length() > 0){
						trim.getAct().getRelationships().get(0).getAct()
								.getObservation().getValues().get(20).getST().setValue(
										fdbResponce);
						}else{
						trim.getAct().getRelationships().get(0).getAct()
								.getObservation().getValues().get(20).getST().setValue(
										"No Information found in FDB.");
						}
				}
				
				trim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(21).getST().setValue(dispensable.getDescdisplay()); 
				
				// Adding the Prescribed Medicine to Trim
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(22).getST().setValue(
						resultList.get(1)); // Adding the boolean for Drug Drug
											// Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(23).getST().setValue(
						resultList.get(2)); // Adding the boolean for Drug Food
											// Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(24).getST().setValue(
						resultList.get(3));  // Adding the boolean for
												// Duplicate Therapy
				

				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(25).getST().setValue(
						resultList.get(4));  // Getting the response for Drug Food Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(26).getST().setValue(
						resultList.get(5)); // Getting the response for Drug Drug Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(27).getST().setValue(
						resultList.get(6)); // Getting the response for Duplicate Therapy Interaction
				
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(35).getST().setValue(
						resultList.get(7)); //Adding the boolean for Drug Allergy
				
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(36).getST().setValue(
						resultList.get(8)); // Getting the response for Drug Allergy  Interaction
				
				
				
				trim.getAct().getRelationships().get(1).getAct()
				.getObservation().getValues().get(1).getST().setValue(
						resultList.get(9)); // Inserting the Strength of the medication from the FDB
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(17).getST().setValue(
						resultList.get(10));// Inserting the Dose of the medication from the FDB
				
				trim.getAct().getRelationships().get(1).getAct()
				.getObservation().getValues().get(3).getST().setValue(
						resultList.get(11));// Inserting the Route of the medication from the FDB
				
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(41).getST().setValue(
						resultList.get(13));// Drug Classification
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(42).getST().setValue(
						resultList.get(14));// Prescriber Messages
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(43).getST().setValue(
						resultList.get(15));// Patient Messages
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(44).getST().setValue(
						resultList.get(16));// Common Order
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(45).getST().setValue(
						resultList.get(20));// Action Group Classification
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(46).getST().setValue(
						resultList.get(18));// CTETC Classification
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(47).getST().setValue(
						resultList.get(19));// CTFDB Classification
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(49).getST().setValue(
						resultList.get(14));// Prescriber Instructions
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(48).getST().setValue(
						resultList.get(15));// Patient Instructions
				
				trim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(50).getST().setValue(resultList.get(21));// Controlled Substance
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(58).getST().setValue(
						resultList.get(22));// Dose Form
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(63).getST().setValue(
						resultList.get(23));// Strength Unit
				
				creatorBean.marshalToDocument(trim, docXML);
				writer.write(resultList.get(1));
				writer.write("|");
				writer.write(resultList.get(2));
				writer.write("|");
				writer.write(resultList.get(3));
				writer.write("|");
				writer.write(resultList.get(7));
				writer.write("|");
				writer.write(resultList.get(9));
				writer.write("|");
				writer.write(resultList.get(10));
				writer.write("|");
				writer.write(resultList.get(11));
				writer.write("|");
				writer.write(resultList.get(12));
				writer.write("|");
				writer.write(resultList.get(13));
				writer.write("|");
				writer.write(resultList.get(14));
				writer.write("|");
				writer.write(resultList.get(15));
				writer.write("|");
				writer.write(resultList.get(16));
				writer.write("|");
				writer.write(resultList.get(17));
				writer.write("|");
				writer.write(resultList.get(18));
				writer.write("|");
				writer.write(resultList.get(19));
				writer.write("|");
				writer.write(resultList.get(20));
				writer.write("|");
				writer.write(resultList.get(21));
				writer.write("|");
				writer.write(resultList.get(23));
				writer.write("|");
				writer.write(String.valueOf(isDrugInFormularyList));
				writer.write("|");
				
								
				Map<String, Boolean> preferenceMap = new HashMap<String, Boolean>();
				preferenceMap.put("Drug - Drug", false);
				preferenceMap.put("Drug - Food", false);
				preferenceMap.put("Drug - Allergy", false);
				String assignedStaffPath = null;
				String[] providerSplit =  activeAccountUser.getOpenMeFirst().split("-");
				if(providerSplit.length == 2)
					assignedStaffPath  = providerSplit[1];
				
				if(assignedStaffPath != null){
					MenuData prescriber = menuBean.findMenuDataItem(Long.parseLong(assignedStaffPath));
					evaluator.addVariable("prescriber", prescriber);
					String notificationPreferences = (String)prescriber.getExtendedField("notification");
					if(notificationPreferences != null){
						String[] notificationPreferencesList = notificationPreferences.split(",");
					for (Object object : notificationPreferencesList) {
						String preference = (String) object;
							if (preference.trim().equals("Drug - Drug")) {
							preferenceMap.put("Drug - Drug", true);
						} 
							if (preference.trim().equals("Drug - Food")) {
							preferenceMap.put("Drug - Food", true);
						} 	
							if (preference.trim().equals("Drug - Allergy")) {
							preferenceMap.put("Drug - Allergy", true);
						}
					}
				writer.write("Drug - Drug=" + preferenceMap.get("Drug - Drug") + "|");
				writer.write("Drug - Food=" + preferenceMap.get("Drug - Food") + "|");
				writer.write("Drug - Allergy=" + preferenceMap.get("Drug - Allergy") + "|");
					}				
				}
					/*//String assignedStaffPath = activeAccountUser.getOpenMeFirst().split("-")[1];
				TrimEx assignedStaffTrim = null; // epBean.findTrimData(new Long(assignedStaffPath), activeAccountUser);
				ActRelationship notificationPreferenceRel = ((ActEx)assignedStaffTrim.getAct()).getRelationship().get("notificationPreference");
				if (notificationPreferenceRel!=null){*/
				TolvenLogger.info("Physician preference for Notification", this.getClass());
				TolvenLogger.info("--------------------------------------", this.getClass());
				TolvenLogger.info("Drug - Drug=" + preferenceMap.get("Drug - Drug"), this.getClass());
				TolvenLogger.info("Drug - Food=" + preferenceMap.get("Drug - Food"), this.getClass());
				TolvenLogger.info("Drug - Allergy=" + preferenceMap.get("Drug - Allergy"), this.getClass());
				TolvenLogger.info("Interation of Drug choosed", this.getClass());
				TolvenLogger.info("--------------------------", this.getClass());
				TolvenLogger.info("Drug - Drug=" + resultList.get(1), this.getClass());
				TolvenLogger.info("Drug - Food=" + resultList.get(2), this.getClass());
				TolvenLogger.info("Drug - Allergy=" + resultList.get(7), this.getClass());
				
				creatorBean.marshalToDocument(trim, docXML);
				req.setAttribute("hasInterraction", writer);
				return;
			} else if (uri.endsWith("drugsUpdate.ajaxfdb")) {
				String filterCondition = req.getParameter("filter_condition");
				List<FdbDispensable> drugs =  fdbBean.retrieveDrugInformation(filterCondition, 0);
				for(FdbDispensable drug : drugs){
					writer.write(drug.getMedid()+"$"+drug.getDescdisplay()+"$"+drug.getNametypecode()+"|");
				}
				req.setAttribute("success", writer);
				return;				
			}else if(uri.endsWith("drugsUpdate15.ajaxfdb")){
				String filterCondition = req.getParameter("filter_condition");
				int total = fdbBean.findDrugCount(filterCondition).intValue();
				int rowId  = 0;
				int actionType = 0;
				if(!StringUtils.isNumeric(req.getParameter("rowid")))
					throw new IllegalArgumentException("Invalid offset for FDB drugs");
				else
					rowId =  Integer.parseInt(req.getParameter("rowid"));
				
				if(!StringUtils.isNumeric(req.getParameter("actionType")))
					throw new IllegalArgumentException("Invalid actionType querying FDB Drugs");
				else
					actionType =  Integer.parseInt(req.getParameter("actionType"));
				
				rowId = calculatePopupRowId(actionType, total, rowId);

				TolvenLogger.info("Filter Condition >>>>>>>>>"+filterCondition, this.getClass());
				TolvenLogger.info("Row ID >>>>>>>>>"+rowId, this.getClass());
				List<FdbDispensable> drugs =  fdbBean.retrieveDrugInformation(filterCondition, rowId);
			
				int i = drugs.size();
				writer.write("{drugs:[");
				for(FdbDispensable drug : drugs){
					writer.write(String.format(Locale.US,"{code:'%s',name:'%s',brand:'%s'}",drug.getMedid(),StringEscapeUtils.escapeHtml(drug.getDescdisplay()),drug.getNametypecode()));
					i--;
					if(i>0)
						writer.write(",");
				}
				
				//int end = total < POPUP_MAX_ROWS ? total : POPUP_MAX_ROWS;
				int end = total <= POPUP_MAX_ROWS ? total : (total-rowId <= POPUP_MAX_ROWS ? total-rowId : POPUP_MAX_ROWS);
				writer.write(String.format(Locale.US,"],totalDrugs:'%s',offSet:'%d',start:'%d',end:'%d'}",total,rowId,rowId,rowId+end));
				req.setAttribute("success", writer);
				return;
			}else if (uri.endsWith("otcDrugsUpdate.ajaxfdb")) {
				String filterCondition = req.getParameter("filter_condition");
				List<DrugVO> drugs =  fdbBean.retrieveOTCDrugInformation(0, filterCondition);
				for(DrugVO drug : drugs){
					writer.write(drug.getDrugCode()+"$"+drug.getDrugName()+"$"+drug.getNameType()+"|");
				}
				req.setAttribute("success", writer);
				return;				
			}else if(uri.endsWith("otcDrugsUpdate15.ajaxfdb")){
				String filterCondition = req.getParameter("filter_condition");
				String rowId = req.getParameter("rowid");
				TolvenLogger.info("Filter Condition >>>>>>>>>"+filterCondition, this.getClass());
				TolvenLogger.info("Row ID >>>>>>>>>"+rowId, this.getClass());
				List<DrugVO> drugs =  fdbBean.retrieveOTCDrugInformation(Integer.parseInt(rowId),filterCondition);
				for(DrugVO drug : drugs){
					writer.write(drug.getDrugCode()+"$"+drug.getDrugName()+"$"+drug.getNameType()+"|");
				}
				req.setAttribute("success", writer);
				return;
			}else if (uri.endsWith("fdbMedication.ajaxfdb")) {
				throw new Exception("Inefficient code detected in "+this.getClass()+" at line 443");
				/*MenuData md = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,new StringReader(trimString));
				String drugName = req.getParameter("drug_name");
				String code = req.getParameter("code");
				MedicationDrug drug= fdbBean.getDrugInformationForRxChange(Long.parseLong(code), drugName);
				trim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(2).getST().setValue(drug.getStrength()); // Strength
				trim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(4).getST().setValue(drug.getRoute()); // Route
				trim.getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getST().setValue(drugName);
				
				writer.write(drugName);
				writer.write("|");
				writer.write(drug.getStrength());
				writer.write("|");
				writer.write(drug.getRoute());
				writer.write("|");
				creatorBean.marshalToDocument(trim, docXML);

				req.setAttribute("fdbResponce", writer);
				return;
				 */			
				}else if(uri.endsWith("flushFDBResponse.ajaxfdb")){
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(22).getST().setValue(
						"false"); // Setting the boolean FALSE for Drug Drug
									// Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(23).getST().setValue(
						"false"); // Setting the boolean FALSE for Drug Food
									// Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(24).getST().setValue(
						"false");  // Setting the boolean FALSE for Duplicate
									// Therapy
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(35).getST().setValue(
						"false");  // Setting the boolean FALSE for Drug Allergy
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(33).getST().setValue(""); // Over Rided comments deleted from the trim
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(32).getST().setValue(""); // Over Rided Boolean is made null
				
				creatorBean.marshalToDocument(trim, docXML);
				return;
			}else if(uri.endsWith("getFDBResponseBooleans.ajaxfdb")){
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				
				if(null == trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(25).getST().getValue() ||
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(25).getST().getValue().trim().length() ==0){
					writer.write("false");
					writer.write("|");
				}else if(null != trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(25).getST().getValue()){
					writer.write("true");
					writer.write("|");
				}
				
				if(null == trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(26).getST().getValue() ||
						trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(26).getST().getValue().trim().length() ==0){
					writer.write("false");	
					writer.write("|");
				}else if(null != trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(26).getST().getValue()){
					writer.write("true");
					writer.write("|");
				}
				
				if(null == trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(27).getST().getValue() ||
						trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(27).getST().getValue().trim().length() ==0){
					writer.write("false");
					writer.write("|");
				}else if(null != trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(27).getST().getValue()){
					writer.write("true");
					writer.write("|");
				}
				
				
				req.setAttribute("fullresponse", writer);
				
				return;
			}else if(uri.endsWith("overRideComments.ajaxfdb")){
				String comments = req.getParameter("comments");
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(22).getST().setValue(
						"false"); // Setting the boolean FALSE for Drug Drug
									// Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(23).getST().setValue(
						"false"); // Setting the boolean FALSE for Drug Food
									// Interaction
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(24).getST().setValue(
						"false");  // Setting the boolean FALSE for Duplicate
									// Therapy
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(33).getST().setValue(comments); // Over Rided comments entered in the trim
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(32).getST().setValue("true"); // Over Rided Boolean is made true
				
				creatorBean.marshalToDocument(trim, docXML);
				return;
				
			}else if(uri.endsWith("addMedicineToTrim.ajaxfdb")){
			String code = req.getParameter("code");
			String medicine = req.getParameter("medicine");
			medicine = medicine;
			MenuData md = menuBean.findMenuDataItem(activeAccountUser
					.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(
					md.getDocumentId());
			String trimString = getDocProtectionBean()
					.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
					new StringReader(trimString));
			MedicationDrug drug = fdbBean.getDrugInformationForRxChange(Long.parseLong(code), medicine);
			String fdbResponce = drug.getFdbResponse();
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(0).getST().setValue(
					medicine); 
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(7).getST().setValue(
					fdbResponce); 
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(3).getST().setValue(
					drug.getDrugFoodInteraction()); 
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(4).getST().setValue(
					drug.getDuplicateTherapyInteraction()); 
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(1).getST().setValue(
					drug.getDrugFoodInteraction()); 
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(2).getST().setValue(
					drug.getDuplicateTherapyInteraction()); 
			writer.write("Strength :"+drug.getStrength());
			writer.write("|");
			writer.write("Route :"+drug.getRoute());
			writer.write("|");
			writer.write("FDB Response :"+drug.getFdbResponse());
			writer.write("|");
			req.setAttribute("success", writer);
			creatorBean.marshalToDocument(trim, docXML);
			return;
		}else if(uri.endsWith("overRideCommentsReq.ajaxfdb")){
			String comments = req.getParameter("comments");
			MenuData md = menuBean.findMenuDataItem(activeAccountUser
					.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(
					md.getDocumentId());
			String trimString = getDocProtectionBean()
					.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
					new StringReader(trimString));
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(6).getST().setValue(
					"true"); 
			
			trim.getAct().getRelationships().get(8).getAct()
			.getObservation().getValues().get(5).getST().setValue(
					comments); 					
			
			creatorBean.marshalToDocument(trim, docXML);
			return;
			
		}else if(uri.endsWith("allergyUpdate.ajaxfdb")){
			String filterCondition = req.getParameter("filter_condition");
			List<AllergyVO> allergies =  fdbBean.fetchFilteredAllergies(filterCondition, 0);
			for(AllergyVO allergy : allergies){
				writer.write(allergy.getAllergen()+"$"+allergy.getType()+"|");
			}
			req.setAttribute("success", writer);
			return;				
		}else if(uri.endsWith("addAllergy.ajaxfdb")){
			MenuData md = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader(trimString));
			String allergyName = req.getParameter("allergy_name");
			allergyName = allergyName;
			
			/**
			 * Modified to solve null pointer exception in Patient List Designer on 08/06/2010 by Valsaraj
			 */
			if (! "patientListDesigner".equals(trim.getName())) {
				if(null != trim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST())
				trim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST().setValue(allergyName);
				trim.setDescription(allergyName);
			}
			if(trim.getAct().getRelationships().get(1).getAct().getObservation()!=null && null != trim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST())
			trim.getAct().getRelationships().get(1).getAct().getObservation().getValues().get(0).getST().setValue(allergyName);
			trim.setDescription(allergyName);
			creatorBean.marshalToDocument(trim, docXML);
			req.setAttribute("allergy", writer);
			return;
		}else if (uri.endsWith("allergyUpdate15.ajaxfdb")){
			
			String filterCondition = req.getParameter("filter_condition");
			int total = fdbBean.findDrugAllergyCount(filterCondition).intValue();
			int rowId  = 0;
			int actionType = 0;
			if(!StringUtils.isNumeric(req.getParameter("rowid")))
				throw new IllegalArgumentException("Invalid offset for FDB drug allergies");
			else
				rowId =  Integer.parseInt(req.getParameter("rowid"));
			
			if(!StringUtils.isNumeric(req.getParameter("actionType")))
				throw new IllegalArgumentException("Invalid actionType querying FDB Drug allergies");
			else
				actionType =  Integer.parseInt(req.getParameter("actionType"));

			rowId = calculatePopupRowId(actionType, total, rowId);
			
			TolvenLogger.info("Filter Condition >>>>>>>>>"+filterCondition, this.getClass());
			TolvenLogger.info("Row ID >>>>>>>>>"+rowId, this.getClass());
			List<AllergyVO> allergies =  fdbBean.fetchFilteredAllergies(filterCondition, rowId);
		
			int i = allergies.size();
			writer.write("{allergies:[");
			for(AllergyVO allergy : allergies){
				writer.write(String.format(Locale.US,"{allergen:'%s',type:'%s', code:'%s'}",StringEscapeUtils.escapeHtml(allergy.getAllergen()),allergy.getType(),allergy.getInternalId()));
				i--;
				if(i>0)
					writer.write(",");
			}				
			int end = total <= POPUP_MAX_ROWS ? total : (total-rowId <= POPUP_MAX_ROWS ? total-rowId : POPUP_MAX_ROWS);
			writer.write(String.format(Locale.US,"],totalAllergies:'%s',offSet:'%d',start:'%d',end:'%d'}",total,rowId,rowId,rowId+end));
			req.setAttribute("success", writer);
			return;
		}else if (uri.endsWith("setRequestedMedicineValueSet.ajaxfdb")){
			MenuData md = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader(trimString));
			ArrayList<String> reqMedicines = new ArrayList<String>();
			
			reqMedicines.add(0,trim.getAct().getRelationships().get(5).getAct()
				.getObservation().getValues().get(0).getST().getValue()); //extracted medicine 1
			reqMedicines.add(1 ,trim.getAct().getRelationships().get(6).getAct()
				.getObservation().getValues().get(0).getST().getValue()); //extracted medicine 2
			reqMedicines.add(2, trim.getAct().getRelationships().get(7).getAct()
				.getObservation().getValues().get(0).getST().getValue()); //extracted medicine 3
			
			
			((CE)trim.getValueSets().get(2).getBindsAndADSAndCDS().get(1)).setDisplayName(reqMedicines.get(0));
			((CE)trim.getValueSets().get(2).getBindsAndADSAndCDS().get(2)).setDisplayName(reqMedicines.get(1));
			((CE)trim.getValueSets().get(2).getBindsAndADSAndCDS().get(3)).setDisplayName(reqMedicines.get(2));
			
			creatorBean.marshalToDocument(trim, docXML);
			req.setAttribute("success", writer);
			return;
		}else if (uri.endsWith("gatherRefillQuantity.ajaxfdb")){
			MenuData md = menuBean.findMenuDataItem(activeAccountUser
					.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(
					md.getDocumentId());
			String trimString = getDocProtectionBean()
					.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
					new StringReader(trimString));
			String drugname = req.getParameter("drug_name");
			
			if(null != drugname && drugname.trim().length() > 0 && !drugname.equals("Select")){
					List<ActRelationship> relationships =  trim.getAct().getRelationships();
					for(ActRelationship relationship : relationships){
						if(drugname.equals(relationship.getAct().getObservation().getValues().get(0).getST().getValue().toString())){
							writer.write(relationship.getAct().getObservation().getValues().get(1).getINT().getValue()+"|");
							writer.write(relationship.getAct().getObservation().getValues().get(2).getINT().getValue()+"");
							break;
						}
					}
			}else{
				writer.write(""+"|"+"");
			}
			req.setAttribute("success", writer);
			return;
		}else if(uri.endsWith("setResponseField.ajaxfdb")){
			MenuData md = menuBean.findMenuDataItem(activeAccountUser
					.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(
					md.getDocumentId());
			String trimString = getDocProtectionBean()
					.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			
			/*RefiilRequest Trim*/
			TrimEx refillRequestTrim = (TrimEx) xmlBean.unmarshal(TRIMns,
					new StringReader(trimString));
			
			Long priorRefill = null ;
			String priorRefillQualifier ="";
			/*Obtaining priorRefill*/
			ActRelationship medicationPrescribedRelation = ((ActEx)refillRequestTrim.getAct()).getRelationship().get("MedicationPrescribed");
			if(medicationPrescribedRelation.getAct().getObservation().getValues()!=null){
				List<ObservationValueSlot>  medicationPrescribedValues = medicationPrescribedRelation.getAct().getObservation().getValues();
					for( int i=0;i<medicationPrescribedValues.size();i++){
						if(!medicationPrescribedValues.get(i).getLabel().getValue().equals(null)){
							if(medicationPrescribedValues.get(i).getLabel().getValue().equals("Refills(value)")){
								priorRefill = Long.valueOf(medicationPrescribedValues.get(i).getST().getValue());
							}
							if(medicationPrescribedValues.get(i).getLabel().getValue().equals("Refills(qualifier)")){
								priorRefillQualifier = medicationPrescribedValues.get(i).getST().getValue();
							}
						}//if label is null
					}//for
			}//if medicationPrescribedobservtationslot null
		
			CE responseStatus = ((ActEx)refillRequestTrim.getAct()).getRelationship().get("response").getAct().getObservation().getValues().get(0).getCE();
			ValueSet statusSet = refillRequestTrim.getValueSet().get("response");
			List<Object> values  = statusSet.getBindsAndADSAndCDS();
			if(responseStatus.toString().equals("Approved")){
				/*Obtaining newRefill*/
				Long newRefill =((ActEx)refillRequestTrim.getAct()).getRelationship().get("MedicationRequested")
				.getAct().getObservation().getValues().get(0).getINT().getValue();
				if(priorRefill != newRefill){
					for(Object value : values){
						CE ce = (CE) value;
						if(ce.getDisplayName().equals("Approved with Change")) {
							((ActEx)refillRequestTrim.getAct()).getRelationship()
							.get("response").getAct().getObservation().getValues().get(0).setCE(ce);
						}
					}
				}else if(priorRefill == 0 || priorRefill == null || priorRefill == newRefill || priorRefillQualifier =="PRN"){
					for(Object value : values){
						CE ce = (CE) value;
						if(ce.getDisplayName().equals("Approved")) {
							((ActEx)refillRequestTrim.getAct()).getRelationship()
							.get("response").getAct().getObservation().getValues().get(0).setCE(ce);
						}
					}
				}
				creatorBean.marshalToDocument(refillRequestTrim, docXML);
				return;
			}
			
			
		}else if (uri.endsWith("instantiatePrescriptionTrim.ajaxfdb")) {
				String templateId = req.getParameter("templateId");
				if (templateId == null)
					throw new IllegalArgumentException(
							"Instantiation request has missing templateId");
				// Where called from
				String context = req.getParameter("context");
				if (context == null)
					throw new IllegalArgumentException(
							"Instantiation request has missing context");
				String source = req.getParameter("source");
				if (source != null && source.length() == 0) {
					source = null;
				}
				MenuData md;
				TolvenLogger.info("[FDBAjaxServlet] ms=" + templateId
						+ " context: " + context, FDBAjaxServlet.class);
				md = creatorBean.createTRIMPlaceholder(activeAccountUser,
						templateId, context, now, source);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				trim.getAct().getRelationships().get(0).getAct()
						.getObservation().getValues().get(38).getST().setValue(
								req.getParameter("rxReferenceNumber"));
				creatorBean.marshalToDocument(trim, docXML);
				TolvenLogger.info("Create new event:" + md.getPath(),
						FDBAjaxServlet.class);
				writer.write(md.getPath());
				req.setAttribute("activeWriter", writer);
				return;
	    }else if(uri.endsWith("loadMedicationDetails.ajaxfdb")){
	    	
	    	MenuData md = menuBean.findMenuDataItem(activeAccountUser
					.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(
					md.getDocumentId());
			String trimString = getDocProtectionBean()
					.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
					new StringReader(trimString));
			String knownAccountType = activeAccountUser.getAccount().getAccountType().getKnownType().toString();
			MenuData medication;
			if (knownAccountType.equals("echr")) {
				medication = menuBean.findMenuDataItem(Long.valueOf(element.split("-")[2].split(":")[0]));
			}else{
				medication = menuBean.findMenuDataItem(Long.valueOf(context.split("-")[2].split(":")[0]));
			}

			if(medication != null){
				if(trim.getName().equals("obs/evn/administration")){
						trim.getAct().getRelationships().get(0).getAct().getObservation().
						getValues().get(9).getST().setValue(medication.getString07());
						trim.getAct().getRelationships().get(0).getAct().getObservation().
						getValues().get(0).getST().setValue(medication.getString01());
						
						trim.getAct().getRelationships().get(0).getAct().getObservation().
						getValues().get(1).getST().setValue(medication.getString02());
						trim.getAct().getRelationships().get(0).getAct().getObservation().
						getValues().get(2).getST().setValue(medication.getLong01().toString());
						
						trim.getAct().getRelationships().get(0).getAct().getObservation().
						getValues().get(3).getST().setValue(medication.getString04());
						trim.getAct().getRelationships().get(0).getAct().getObservation().
						getValues().get(4).getST().setValue(medication.getDate03().toString());
						
						trim.getAct().getRelationships().get(0).getAct().getObservation()
						.getValues().get(5).getST().setValue(medication.getDate02().toString());
						
				}else if(trim.getName().equals("obs/evn/dispense")){
						((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
							.getValues().get(1).getST().setValue(medication.getString01());
						if(medication.getString02() != null)
							((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
										.getValues().get(3).getST().setValue(medication.getString02());
						if(medication.getLong01()!= null)
							((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
											.getValues().get(4).getINT().setValue(medication.getLong01());
						((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
									.getValues().get(8).getST().setValue(
											medication.getDate03().toString());
						((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
									.getValues().get(9).getST()
									.setValue(medication.getDate02().toString());
						((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
									.getValues().get(5).getST().setValue(medication.getString04());
						if(medication.getLong02()!= null)
							((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
										.getValues().get(7).getINT().setValue(medication.getLong02());
						if(medication.getLong03()!=null)
							((ActEx)trim.getAct()).getRelationship().get("dispense").getAct().getObservation()
										.getValues().get(6).getINT().setValue(medication.getLong03());
				}else if(trim.getName().equals("obs/evn/medicationRefill")){
					/*Setting Medication Name*/
					if(medication.getString01() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(0).getST().setValue(medication.getString01());
					}
					/*Setting Strength*/
					if(medication.getString02() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(1).getST().setValue(medication.getString02());
					}
					/*Setting Dose*/
					if(medication.getString03() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(2).getST().setValue(medication.getString03());
					}
					/*Setting Frequency*/
					if(medication.getLong01() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(3).getINT().setValue(medication.getLong01());
						
					}
					/*Setting Start Date*/
					if(medication.getDate02() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(4).getST().setValue(medication.getDate02().toString());
						
					}
					/*Setting End Date*/
					if(medication.getDate03() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(5).getST().setValue(medication.getDate03().toString());
					}
					/*Setting Route*/
					if(medication.getString04() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(6).getST().setValue(medication.getString04());
					}
					/*Setting Dispense Amount*/
					if(medication.getLong02() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(7).getINT().setValue(medication.getLong02());
					}
					/*Setting Refills*/
					if(medication.getLong03() != null){
						((ActEx)trim.getAct()).getRelationship().get("medicationDetails").getAct().getObservation()
						.getValues().get(8).getINT().setValue(medication.getLong03());
					}
				}
					
			}
			creatorBean.marshalToDocument(trim, docXML);
			return;
			}else if(uri.endsWith("getTotalDrugCount.ajaxfdb")){
				String filter = req.getParameter("filter");
				Long maxCount = fdbBean.findDrugCount(filter);
				writer.write(String.valueOf(maxCount));
				req.setAttribute("success", writer);
				return;	
			}else if(uri.endsWith("getTotalOTCDrugCount.ajaxfdb")){
				String filter = req.getParameter("filter");
				int maxCount = fdbBean.findDrugCount("OTCDRUGCOUNT").intValue();
				writer.write(String.valueOf(maxCount));
				req.setAttribute("success", writer);
				return;	
			}else if(uri.endsWith("getTotalAllergyCount.ajaxfdb")){
				String filter = req.getParameter("filter");
				int maxCount = fdbBean.findDrugCount(filter).intValue();
				writer.write(String.valueOf(maxCount));
				req.setAttribute("success", writer);
				return;	
			}else if(uri.endsWith("retrieveNDCInformation.ajaxfdb")){
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				String drugCode = req.getParameter("drug_code");
				String drugName = req.getParameter("drug_name");
				Map<String, String> ndcInfo = fdbBean.retrieveNDCInformation(Long.parseLong(drugCode));

				if (((ActEx) trim.getAct()).getRelationship().get("ndcDetails") != null) {
					CE ndcOVS = ((ActEx) trim.getAct()).getRelationship().get("ndcDetails").getAct().getObservation().getValues().get(0).getCE();
					ndcOVS.setDisplayName(drugName);
					ndcOVS.setCode(ndcInfo.get("ndcCode"));
					ndcOVS.setCodeSystem(ndcInfo.get("codeSystem"));
					ndcOVS.setCodeSystemName(ndcInfo.get("ndcCodeQual"));
					ndcOVS.setCodeSystemVersion(ndcInfo.get("ndcCodeSystemVersion"));
					ndcOVS.getTranslations().get(0).setCode(ndcInfo.get("medId"));
				}
				
				if (! "obs/evn/medicationHistory".equals(trim.getName())) {
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(53).getST().setValue(ndcInfo.get("ndcCode"));// NDC ProductCode
				
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(54).getST().setValue(ndcInfo.get("ndcCodeQual"));// NDC ProductCode Qualifier
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(55).getST().setValue(ndcInfo.get("ndcDoseForm"));// NDC DoseForm
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(56).getST().setValue(ndcInfo.get("ndcStrength"));// NDC Strength
				
				trim.getAct().getRelationships().get(0).getAct()
				.getObservation().getValues().get(57).getST().setValue(ndcInfo.get("ndcStrengthUnit"));// NDC Strength Units
				}
				
				creatorBean.marshalToDocument(trim, docXML);
				ndcInfo.get("ndcCode");
				ndcInfo.get("ndcCodeQual");
				ndcInfo.get("ndcStrength");
				ndcInfo.get("ndcDoseForm");
				ndcInfo.get("ndcStrengthUnit");
				ndcInfo.get("ndcRoute");
				ndcInfo.get("ndcDrug");
				ndcInfo.get("ndc");
				writer.write("NDC:"+ndcInfo.get("ndc")+"|NDC CODE:"+ndcInfo.get("ndcCode")+"|NDC CODE QUALIFIER:"+ndcInfo.get("ndcCodeQual"));
				writer.write("|STRENGTH:"+ndcInfo.get("ndcStrength")+"|DOSE FORM:"+ndcInfo.get("ndcDoseForm")+"|STRENGTH UNIT:"+ndcInfo.get("ndcStrengthUnit"));
				writer.write("|ROUTE:"+ndcInfo.get("ndcRoute")+"|PACKAGED DRUG NAME:"+ndcInfo.get("ndcDrug")+"|");
				req.setAttribute("success", writer);
				return;	
			}else if (uri.endsWith("getDrugCodeFromDrugName.ajaxfdb")){
				String drug = req.getParameter("drugName");
				Integer drugCode = fdbBean.retrieveCodeFromDrugName(drug);
				writer.write(drugCode);
				req.setAttribute("success", writer);
				return;
			}else if (uri.endsWith("retrieveDrugValidity.ajaxfdb")){
				String drug = req.getParameter("drugname");
				Integer drugCode = fdbBean.retrieveCodeFromDrugName(drug);
				writer.write(drugCode);
				req.setAttribute("success", writer);
				return;
			}
			else if(uri.endsWith("getMedicationDetails.ajaxfdb")){
				String code = req.getParameter("code");
				String medicine = req.getParameter("medicine");
				
				Map<String,String> resultList = fdbBean.getMedicationDetails(code , medicine);
				//strength
				if (resultList.get("strength")!=null && !resultList.get("strength").equals(""))
					writer.write(resultList.get("strength"));
				writer.write("|");
				//route
				if (resultList.get("route")!=null && !resultList.get("route").equals(""))
					writer.write(resultList.get("route"));
				writer.write("|");
				//doseForm
				if (resultList.get("doseForm")!=null && !resultList.get("doseForm").equals(""))
					writer.write(resultList.get("doseForm"));
				writer.write("|");
				//classification
				if (resultList.get("classification")!=null && !resultList.get("classification").equals(""))
					writer.write(resultList.get("classification"));
				req.setAttribute("success", writer);
				return;	
			}
		    else if (uri.endsWith("manageMedication.ajaxfdb")) { 
		    	String templateName = req.getParameter("template");
		    	
		    	if (templateName == null || templateName.isEmpty() || "undefined".equals(templateName)) {
		    		templateName = "path/tempMedication";
		    	}
		    	
				String medName = req.getParameter("medicationName");
				String medCode = req.getParameter("medicationCode");
				String strength=req.getParameter("_medStrength");
				String form=req.getParameter("_medForm");
				String route=req.getParameter("_medRoute");
				String Dose=req.getParameter("_medDose");
				String frequency=req.getParameter("_medFrequency");
				String start=req.getParameter("_medStart");
				String dc=req.getParameter("_medDC");
				String _class=req.getParameter("_medClass");
				String instruction=req.getParameter("_medInstruction");
				String relName="medication";
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				ActRelationship rel = null;
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				DateFormat df = new SimpleDateFormat("MM/dd/yy");
				ActEx act=((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get(relName+"s").getAct());
				if(actionType == 0) {// create
					System.out.println("create......");
				    TrimEx _trimEx = trimBean.findTrim(templateName);
			    	Object obj = ((ActEx)_trimEx.getAct()).getRelationship().get(relName);
			    	rel = (ActRelationship)obj;
			    	act.getRelationships().add(rel);
			    	//if (!wizardTrim.getName().equals("docclin/evn/medication"))
			    	//	rel.setEnabled(false);
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get(relName).remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1) {// update
						TrimEx _trimEx = trimBean.findTrim(templateName);
				    	Object obj = ((ActEx)_trimEx.getAct()).getRelationship().get(relName);
				    	act.getRelationships().add(widgetIndex,(ActRelationship)obj);
				    	rel = (ActRelationship)obj;
					}
					
				}
				try {
					if(rel!=null) {
						if (medName!=null) {
							rel.getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(medName));
						}						
						if (_class!=null) {
							rel.getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(_class));
						}
						if (strength!=null) {
							rel.getAct().getObservation().getValues().get(2).setST(trimFactory.createNewST(strength));
						}
						if (form!=null) {
							rel.getAct().getObservation().getValues().get(3).setST(trimFactory.createNewST(form));
						}
						if (route!=null) {
							rel.getAct().getObservation().getValues().get(4).setCE((CE)trimFactory.stringToDataType(route));
						}
						if (Dose!=null) {
							rel.getAct().getObservation().getValues().get(5).setST(trimFactory.createNewST(Dose));
						}
						if (frequency!=null) {
							rel.getAct().getObservation().getValues().get(6).setCE((CE)trimFactory.stringToDataType(frequency));
						}
					
						if ("path/tempMedicationHistory".equals(templateName)) {
							if (dc!=null) {
								rel.getAct().getObservation().getValues().get(7).setST(trimFactory.createNewST(dc));
							}
							
							if (instruction!=null) {
								rel.getAct().getObservation().getValues().get(8).setST(trimFactory.createNewST(instruction));
							}
							
							if (medCode!=null) {
								rel.getAct().getObservation().getValues().get(9).setST(trimFactory.createNewST(medCode));
							}
							
							String ndcCode = req.getParameter("ndcCode");
							
							if (ndcCode != null) {
								rel.getAct().getObservation().getValues().get(11).setST(trimFactory.createNewST(ndcCode));
							}
						}
						else {
							if (medCode!=null) {
								rel.getAct().getObservation().getValues().get(10).setST(trimFactory.createNewST(medCode));
							}
							
							//address
							TSEx startDateVal = (TSEx) trimFactory.createTS();
							if (start!=null && df.parse(start)!=null) {
								try{
									startDateVal.setDate(df.parse(start));
								}
								catch (Exception e) {
									// TODO: handle exception
								}
								rel.getAct().getObservation().getValues().get(7).setTS(startDateVal);
							}
							
							if (dc!=null) {
								rel.getAct().getObservation().getValues().get(8).setST(trimFactory.createNewST(dc));
							}
							
							if (instruction!=null) {
								rel.getAct().getObservation().getValues().get(9).setST(trimFactory.createNewST(instruction));
							}
						}
						
					}
					
			    	creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } 
				catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
			   
		} catch (Exception e) {
			throw new ServletException(
					"New Exception thrown in FDBAjaxServlet", e);
		}
	}
	
	/**
	 * @return the docProtectionBean
	 */
	public DocProtectionLocal getDocProtectionBean() {
		return docProtectionBean;
	}

	/**
	 * @return the docBean
	 */
	public DocumentLocal getDocBean() {
		return docBean;
	}
	
}
