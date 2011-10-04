package org.tolven.ajax;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimCreatorLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.entity.DocXML;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.DocProtectionLocal;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.CE;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ActRelationshipsMap;
import org.tolven.trim.ex.TSEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;
/**
 * This servlet is commonly used for progress note wizard.
 * @author Suja
 * added on 06/22/2010
 */
public class ProgressNoteWizardServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String TRIMns = "urn:tolven-org:trim:4.0";

    @EJB
    private MenuLocal menuBean;
    
    @EJB
    private DocumentLocal docBean;

    @EJB
    private DocProtectionLocal docProtectionBean;

    @EJB
    private TrimLocal trimBean;

    @EJB
    private XMLLocal xmlBean;

	@EJB
    private CreatorLocal creatorBean;
	
	@EJB
	private TrimCreatorLocal trimCreatorBean;
	
    public DocumentLocal getDocBean() {
        return docBean;
    }

    public DocProtectionLocal getDocProtectionBean() {
        return docProtectionBean;
    }


    public void init(ServletConfig config) throws ServletException {
		try
        {
            InitialContext ctx = new InitialContext();
            if (menuBean == null) {
                menuBean = (MenuLocal) ctx.lookup("tolven/MenuBean/local");
            }
            if (docBean == null) {
                docBean = (DocumentLocal) ctx.lookup("tolven/DocumentBean/local");
            }
            if (docProtectionBean == null) {
                docProtectionBean = (DocProtectionLocal) ctx.lookup("tolven/DocProtectionBean/local");
            }
            if (trimBean == null) {
                trimBean = (TrimLocal) ctx.lookup("tolven/TrimBean/local");
            }
            if (xmlBean == null) {
                xmlBean = (XMLLocal) ctx.lookup("tolven/XMLBean/local");
            }
            if (creatorBean == null) {
                creatorBean = (CreatorLocal) ctx.lookup("tolven/CreatorBean/local");
            }
            if (trimCreatorBean == null) {
                trimCreatorBean = (TrimCreatorLocal) ctx.lookup("tolven/TrimCreatorBean/local");
            }
        }
        catch (NamingException e)
        {
           throw new RuntimeException(e);
        }
		
	}
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String uri = req.getRequestURI();
			AccountUser activeAccountUser = (AccountUser) req.getAttribute("accountUser");
			resp.setContentType("text/xml");
			resp.setCharacterEncoding("UTF-8");
		    resp.setHeader("Cache-Control", "no-cache");
		    Writer writer=resp.getWriter();
	    	Date now = (Date) req.getAttribute("tolvenNow");
	    	TrimFactory trimFactory = new TrimFactory();
	    	DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssZ"); 
	    	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");

		    /*
		     * Add, update or delete test orders
		     * Wizard : Progress Note -> Plan tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/22/2010 
		     */
		    if (uri.endsWith("managePNTestOrders.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = req.getParameter("template");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActRelationship testRel = ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("testOrders");
				ActEx act=((ActEx)testRel.getAct());
				ActRelationship rel = trimFactory.createActRelationship();
				rel.setTypeCode(testRel.getTypeCode());
				rel.setDirection(testRel.getDirection());
				rel.setName("testOrder");
				if(actionType == 0) {// create
					System.out.println("create......");
					act.getRelationships().add(rel); 
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get("testOrder").remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1)
						act.getRelationships().add(widgetIndex,rel);
				}
				try {
					if (template!=null) {
						TrimEx templateTrim=trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}

					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    if (uri.endsWith("managePNImageOrders.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = req.getParameter("template");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActRelationship testRel = ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("imageOrders");
				ActEx act=((ActEx)testRel.getAct());
				ActRelationship rel = trimFactory.createActRelationship();
				rel.setTypeCode(testRel.getTypeCode());
				rel.setDirection(testRel.getDirection());
				rel.setName("imageOrder");
				if(actionType == 0) {// create
					System.out.println("create......");
					act.getRelationships().add(rel); 
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get("imageOrder").remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1)
						act.getRelationships().add(widgetIndex,rel);
				}
				try {
					if (template!=null) {
						TrimEx templateTrim=trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}

					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    /*
		     * Add, update or delete follow-up
		     * Wizard : Progress Note -> Plan tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/22/2010 
		     */
		    else if (uri.endsWith("managePNFollowUp.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = "path/progressnoteTemplate";
				String appointmentDate = req.getParameter( "appointmentDate");
				String staff = req.getParameter( "staff");
				String location = req.getParameter( "location");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActEx act=((ActEx)((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("followUp").getAct());
				ActRelationship rel = null;
				String relName = "appointment";
				if(actionType == 0) {// create
					System.out.println("create......");
				    TrimEx _trimEx = trimBean.findTrim(template);
				    rel = ((ActEx)_trimEx.getAct()).getRelationship().get(relName);
			    	act.getRelationships().add(rel); 
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get(relName).remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1) {// update
						TrimEx _trimEx = trimBean.findTrim(template);
						rel = ((ActEx)_trimEx.getAct()).getRelationship().get(relName);
				    	act.getRelationships().add(widgetIndex,rel);
					}
				}
				try {
					if (rel!=null) {
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						//Appointment Date
						TSEx aDate = (TSEx) trimFactory.createTS();
						if (appointmentDate!=null && df.parse(appointmentDate)!=null)
							aDate.setDate(df.parse(appointmentDate));
						rel.getAct().getEffectiveTime().setTS(aDate);
						//Staff
						rel.getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(staff));
						//Location
						rel.getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(location));
					}
					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    /*
		     * Add, update or delete treatments
		     * Wizard : Progress Note -> Plan tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/22/2010 
		     */
		    else if (uri.endsWith("managePNTreatments.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = req.getParameter("template");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActRelationship treRel= ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("treatments");
				ActEx act=((ActEx)treRel.getAct());
				ActRelationship rel = trimFactory.createActRelationship();
				rel.setTypeCode(treRel.getTypeCode());
				rel.setDirection(treRel.getDirection());
				rel.setName("treatment");
				if(actionType == 0) {// create
					System.out.println("create......");
					act.getRelationships().add(rel); 
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get("treatment").remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1)
						act.getRelationships().add(widgetIndex,rel);
				}
				try {
					if (template!=null) {
						TrimEx templateTrim=trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}

					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    /*
		     * Add, update or delete referrals
		     * Wizard : Progress Note -> Plan tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/22/2010 
		     */
		    else if (uri.endsWith("managePNReferrals.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = req.getParameter("template");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActRelationship refRel= ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("referrals");
				ActEx act=((ActEx)refRel.getAct());
				ActRelationship rel = trimFactory.createActRelationship();
				rel.setTypeCode(refRel.getTypeCode());
				rel.setDirection(refRel.getDirection());
				rel.setName("referral");
				if(actionType == 0) {// create
					System.out.println("create......");
					act.getRelationships().add(rel); 
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get("referral").remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1)
						act.getRelationships().add(widgetIndex,rel);
				}
				try {
					if (template!=null) {
						TrimEx templateTrim=trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}

					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    /*
		     * Add, update or delete problems
		     * Wizard : Progress Note -> Subjective tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/24/2010 
		     */
		    else if (uri.endsWith("managePNProblems.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = req.getParameter("template");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActRelationship proRel= ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("subjective").getAct()).getRelationship().get("problems");
				ActEx act=((ActEx)proRel.getAct());
				ActEx act1=((ActEx)((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("subjective").getAct()).getRelationship().get("siteOfProblems").getAct());
				ActRelationship rel = trimFactory.createActRelationship();
				rel.setTypeCode(proRel.getTypeCode());
				rel.setDirection(proRel.getDirection());
				rel.setName("problem");
				ActRelationship rel1 = trimFactory.createActRelationship();
				rel1.setTypeCode(proRel.getTypeCode());
				rel1.setDirection(proRel.getDirection());
				rel1.setName("siteofProblem");
				rel1.setAct(act1);
				rel1.getAct().setClassCode(proRel.getAct().getClassCode());
				rel1.getAct().setMoodCode(proRel.getAct().getMoodCode());
				if(actionType == 0) {// create
					System.out.println("create......");
			    	act.getRelationships().add(rel); 
			    	act.getRelationships().add(rel1);
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get("problem").remove(widgetIndex);
					act.getRelationshipsList().get("siteofProblem").remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1) {
						act.getRelationships().add(widgetIndex,rel);
						act.getRelationships().add(widgetIndex,rel1);
					}	
				}
				try {
					if (template!=null) {
						TrimEx templateTrim=trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.getAct().getTitle().setST(trimFactory.createNewST("Problem"));
						rel.setEnabled(false);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
					if(actionType == 1){
						String onsetDate = req.getParameter( "onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						//Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate!=null && df.parse(onsetDate)!=null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						String addToList = req.getParameter( "addToList");
						if (addToList!=null && !addToList.equals("") && addToList.equals("Yes"))
							rel.setEnabled(true);
						else
							rel.setEnabled(false);
					}

					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    
		    /*
		     * Update problem
		     * Wizard : Progress Note -> Subjective tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/24/2010 
		     */
		    else if (uri.endsWith("updatePNProblem.pnotes")) {
				String element = req.getParameter("element");
				String severity = req.getParameter("severity_save");
				String course = req.getParameter("course_save");
				String outcome = req.getParameter("outcome_save");
				String treatment = req.getParameter("treatment_save");
				String comments = req.getParameter("comments_save");
				int widgetIndex = Integer.parseInt(req
						.getParameter("widgetIndex"));
				int actionType = Integer.parseInt(req
						.getParameter("actionType"));
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = this.getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				ActEx act = ((ActEx) ((ActEx) ((ActEx) wizardTrim.getAct())
						.getRelationship().get("subjective").getAct())
						.getRelationship().get("problems").getAct());
				try {
					ActRelationship rel = act.getRelationshipsList().get(
							"problem").get(widgetIndex);
					if (actionType == 0) {// update onset date
						String onsetDate = req.getParameter("onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						
						String siteofProblem = req
								.getParameter("siteofProblem");
						act.getRelationship().get("siteofProblem").getAct().getTitle().setST(
								trimFactory.createNewST(siteofProblem));

						((ActEx) rel.getAct()).getRelationship()
								.get("severity").getAct().getObservation()
								.getValues().get(0)
								.setCE(getCE("severity", severity, wizardTrim));
						((ActEx) rel.getAct()).getRelationship().get("course")
								.getAct().getObservation().getValues().get(0)
								.setCE(getCE("course", course, wizardTrim));
						((ActEx) rel.getAct()).getRelationship().get("outcome")
								.getAct().getObservation().getValues().get(0)
								.setCE(getCE("outcome", outcome, wizardTrim));
						((ActEx) rel.getAct()).getRelationship().get(
								"treatment").getAct().getText().getST()
								.setValue(treatment);
						((ActEx) rel.getAct()).getRelationship()
								.get("comments").getAct().getText().getST()
								.setValue(comments);

						// Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate != null && df.parse(onsetDate) != null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						rel.getAct().getTitle().setST(
								trimFactory.createNewST(""));
					} else { // update "add to problem list" field
						String addToList = req.getParameter("addToList");
						if (addToList != null && !addToList.equals("")
								&& addToList.equals("Yes"))
							rel.setEnabled(true);
						else
							rel.setEnabled(false);
					}

					creatorBean.marshalToDocument(wizardTrim, docXML);
					writer.write("Success");
					req.setAttribute("activeWriter", writer);
					return;
				} catch (NumberFormatException e) {
					writer.write("Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
			}
		    
		    /*
		     * Update Diagnoses
		     * Wizard : Progress Note -> Assessment tab -> Diagnosis (Patient -> Progress Notes)
		     * @author Pinky
		     * added on 10/19/2010 
		     */
		    else if (uri.endsWith("updatePNDiagnoses.pnotes")) {
				String element = req.getParameter("element");
				int widgetIndex = Integer.parseInt(req
						.getParameter("widgetIndex"));
				int actionType = Integer.parseInt(req
						.getParameter("actionType"));
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = this.getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				
				ActEx act = ((ActEx)((ActEx) ((ActEx) wizardTrim.getAct()).getRelationship().get("assessment").getAct()).getRelationship().get("diagnoses").getAct());
				try {
					ActRelationship rel = act.getRelationshipsList().get(
							"diagnosis").get(widgetIndex);
					if (actionType == 0) {// update onset date
						String onsetDate = req.getParameter("onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						
						// Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate != null && df.parse(onsetDate) != null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						rel.getAct().getTitle().setST(
								trimFactory.createNewST(""));
					}
					creatorBean.marshalToDocument(wizardTrim, docXML);
					writer.write("Success");
					req.setAttribute("activeWriter", writer);
					return;
				} catch (NumberFormatException e) {
					writer.write("Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
			}
		    
		    /*
		     * Update treatments
		     * Wizard : Progress Note -> Plan tab -> Treatments (Patient -> Progress Notes)
		     * @author Pinky
		     * added on 10/19/2010 
		     */
		    else if (uri.endsWith("updatePNTreatments.pnotes")) {
				String element = req.getParameter("element");
				int widgetIndex = Integer.parseInt(req
						.getParameter("widgetIndex"));
				int actionType = Integer.parseInt(req
						.getParameter("actionType"));
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = this.getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				ActEx act= ((ActEx)((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("treatments").getAct());
				try {
					ActRelationship rel = act.getRelationshipsList().get(
							"treatment").get(widgetIndex);
					if (actionType == 0) {// update onset date
						String onsetDate = req.getParameter("onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						
						// Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate != null && df.parse(onsetDate) != null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						rel.getAct().getTitle().setST(
								trimFactory.createNewST(""));
					}
					creatorBean.marshalToDocument(wizardTrim, docXML);
					writer.write("Success");
					req.setAttribute("activeWriter", writer);
					return;
				} catch (NumberFormatException e) {
					writer.write("Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
			}
		    
		    /*
		     * Update Referrals
		     * Wizard : Progress Note -> Plan tab -> Referrals (Patient -> Progress Notes)
		     * @author Pinky
		     * added on 10/20/2010 
		     */
		    else if (uri.endsWith("updatePNReferrals.pnotes")) {
				String element = req.getParameter("element");
				int widgetIndex = Integer.parseInt(req
						.getParameter("widgetIndex"));
				int actionType = Integer.parseInt(req
						.getParameter("actionType"));
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = this.getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				ActEx act = ((ActEx)((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("referrals").getAct());
				try {
					ActRelationship rel = act.getRelationshipsList().get(
							"referral").get(widgetIndex);
					if (actionType == 0) {// update onset date
						String onsetDate = req.getParameter("onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						
						// Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate != null && df.parse(onsetDate) != null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						rel.getAct().getTitle().setST(
								trimFactory.createNewST(""));
					}
					creatorBean.marshalToDocument(wizardTrim, docXML);
					writer.write("Success");
					req.setAttribute("activeWriter", writer);
					return;
				} catch (NumberFormatException e) {
					writer.write("Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
			}
		    
		    /*
		     * Update Image Orders
		     * Wizard : Progress Note -> Plan tab -> Image Orders (Patient -> Progress Notes)
		     * @author Pinky
		     * added on 10/20/2010 
		     */
		    else if (uri.endsWith("updatePNImageOrders.pnotes")) {
				String element = req.getParameter("element");
				int widgetIndex = Integer.parseInt(req
						.getParameter("widgetIndex"));
				int actionType = Integer.parseInt(req
						.getParameter("actionType"));
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = this.getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				ActEx act = ((ActEx)((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("imageOrders").getAct());
				
				try {
					ActRelationship rel = act.getRelationshipsList().get(
							"imageOrder").get(widgetIndex);
					if (actionType == 0) {// update onset date
						String onsetDate = req.getParameter("onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						
						// Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate != null && df.parse(onsetDate) != null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						rel.getAct().getTitle().setST(
								trimFactory.createNewST(""));
					}
					creatorBean.marshalToDocument(wizardTrim, docXML);
					writer.write("Success");
					req.setAttribute("activeWriter", writer);
					return;
				} catch (NumberFormatException e) {
					writer.write("Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
			}
		    
		    /*
		     * Update Lab Orders
		     * Wizard : Progress Note -> Plan tab -> Lab Orders (Patient -> Progress Notes)
		     * @author Pinky
		     * added on 10/20/2010 
		     */
		    else if (uri.endsWith("updatePNLabOrders.pnotes")) {
				String element = req.getParameter("element");
				int widgetIndex = Integer.parseInt(req
						.getParameter("widgetIndex"));
				int actionType = Integer.parseInt(req
						.getParameter("actionType"));
				MenuData md = menuBean.findMenuDataItem(activeAccountUser
						.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(
						md.getDocumentId());
				String trimString = this.getDocProtectionBean()
						.getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns,
						new StringReader(trimString));
				ActEx act = ((ActEx)((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("plan").getAct()).getRelationship().get("testOrders").getAct());
				
				try {
					ActRelationship rel = act.getRelationshipsList().get(
							"testOrder").get(widgetIndex);
					if (actionType == 0) {// update onset date
						String onsetDate = req.getParameter("onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						
						// Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate != null && df.parse(onsetDate) != null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						rel.getAct().getTitle().setST(
								trimFactory.createNewST(""));
					}
					creatorBean.marshalToDocument(wizardTrim, docXML);
					writer.write("Success");
					req.setAttribute("activeWriter", writer);
					return;
				} catch (NumberFormatException e) {
					writer.write("Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
			}
		    
		    /*
		     * Add, update or delete symptoms
		     * Wizard : Progress Note -> Subjective tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/25/2010 
		     */
		    else if (uri.endsWith("managePNSymptoms.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = req.getParameter("template");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActRelationship sysRel=((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("subjective").getAct()).getRelationship().get("symptoms");
				ActEx act=((ActEx)sysRel.getAct());
				ActRelationship rel = trimFactory.createActRelationship();
				rel.setTypeCode(sysRel.getTypeCode());
				rel.setDirection(sysRel.getDirection());
				rel.setName("symptom");
				if(actionType == 0) {// create
					System.out.println("create......");
			    	act.getRelationships().add(rel); 
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationshipsList().get("symptom").remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1)
						act.getRelationships().add(widgetIndex,rel);
				}
				try {
					if (template!=null) {
						TrimEx templateTrim=trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.getAct().getTitle().setST(trimFactory.createNewST("Symptom"));
						rel.setEnabled(false);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
						
					}
					if(actionType == 1){
						String onsetDate = req.getParameter( "onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						//Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate!=null && df.parse(onsetDate)!=null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						String addToList = req.getParameter( "addToList");
						if (addToList!=null && !addToList.equals("") && addToList.equals("Yes"))
							rel.setEnabled(true);
						else
							rel.setEnabled(false);
					}

					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    /*
		     * Update symptom
		     * Wizard : Progress Note -> Subjective tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/25/2010 
		     */
		    else if (uri.endsWith("updatePNSymptom.pnotes")) { 
		    	String element = req.getParameter( "element");
				int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActEx act=((ActEx)((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("subjective").getAct()).getRelationship().get("symptoms").getAct());
				
				try {
					ActRelationship rel = act.getRelationshipsList().get("symptom").get(widgetIndex);
					if(actionType == 0) {// update onset date
						String onsetDate = req.getParameter( "onsetDate");
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						//Appointment Date
						TSEx osDate = (TSEx) trimFactory.createTS();
						if (onsetDate!=null && df.parse(onsetDate)!=null)
							osDate.setDate(df.parse(onsetDate));
						rel.getAct().getEffectiveTime().setTS(osDate);
						rel.getAct().getTitle().setST(trimFactory.createNewST(""));
					}else {  // update "add to problem list" field
						String addToList = req.getParameter( "addToList");
						if (addToList!=null && !addToList.equals("") && addToList.equals("Yes"))
							rel.setEnabled(true);
						else
							rel.setEnabled(false);
					}
					
					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    /*
		     * Add, update or delete diagnoses
		     * Wizard : Progress Note -> Assessment tab (Patient -> Progress Notes)
		     * @author Suja
		     * added on 06/22/2010 
		     */
		    else if (uri.endsWith("managePNDiagnoses.pnotes")) { 
		    	String element = req.getParameter( "element");
				String template = req.getParameter("template");
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				ActRelationship diaRel = ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("assessment").getAct()).getRelationship().get("diagnoses");
				ActEx act=((ActEx)diaRel.getAct());
				ActRelationship rel = trimFactory.createActRelationship();
				rel.setTypeCode(diaRel.getTypeCode());
				rel.setDirection(diaRel.getDirection());
				rel.setName("diagnosis");
				
				if(actionType == 0) {// create
					System.out.println("create......");
					act.getRelationships().add(rel);
				}else {  // update or delete procedure
					int widgetIndex = Integer.parseInt(req.getParameter("widgetIndex"));
					act.getRelationships().remove(widgetIndex);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					if(actionType == 1) 
						act.getRelationships().add(widgetIndex,rel);
				}
				try {
					if (template!=null) {
						TrimEx templateTrim=trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}

					creatorBean.marshalToDocument( wizardTrim, docXML );
					writer.write("Success");
		    		req.setAttribute("activeWriter", writer);
		    		return;
			    } catch (NumberFormatException e) {
					writer.write( "Invalid input entered by user");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    }
		    /* 
		     * Submits Vital signs Temperature, Pulse, Blood Pressure and Respiration Rate.
		     * @author Sandheep
		     * added on 10/18/2010
		     */
		    else if (uri.endsWith("saveObservationTrims.pnotes")) { 
		    	try{
		    		String element=req.getParameter("element");
			    	String path="echr:"+element.split(":")[1].toString()+":observations:all";
			    	MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
					DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
					String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser);
					TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
					String progressDate = null;
					Date tempDate = null;
					String submitDateString = null;
					Date submitDate = null;
					progressDate = ((ActEx)wizardTrim.getAct()).getEffectiveTime().getTS().getValue();
					tempDate = df1.parse(progressDate);
					submitDateString = formatter.format(tempDate);
					submitDate = formatter.parse(submitDateString);
					TSEx ts = (TSEx) trimFactory.createTS();
					ts.setDate(submitDate);
			    	
					//Pulse Trim
			    	TrimEx pulseTrim=trimCreatorBean.createTrim(activeAccountUser, "obs/evn/pulse", path, now);
					if (!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(1).getAct().getObservation().getValues().get(1).getST().getValue().equals(" ")&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(1).getAct().getObservation().getValues().get(1).getST().getValue().equals(""))&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(1).getAct().getObservation().getValues().get(1).getST().getValue().equals(null))) {
						double pulse = Double.parseDouble(((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(1).getAct().getObservation().getValues().get(1).getST().getValue());
						((ActEx)pulseTrim.getAct()).getObservation().getValues().get(0).getPQ().setValue(pulse);
							pulseTrim.getAct().getEffectiveTime().setTS(ts);
							trimCreatorBean.submitTrim(pulseTrim, path, activeAccountUser, now);
					}
			    	
			    	//Temperature Trim
			    	TrimEx temperatureTrim=trimCreatorBean.createTrim(activeAccountUser, "obs/evn/temperature", path, now);
			    	if (!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getPQ().getValue().equals(" ")&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getPQ().getValue().equals(""))&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getPQ().getValue().equals(null))) {
						double temperature = ((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(0).getAct().getObservation().getValues().get(0).getPQ().getValue();
						((ActEx)temperatureTrim.getAct()).getObservation().getValues().get(0).getPQ().setValue(temperature);
						temperatureTrim.getAct().getEffectiveTime().setTS(ts);
						trimCreatorBean.submitTrim(temperatureTrim, path, activeAccountUser, now);
			    	}
			    	
			    	//Blood Pressure Trim
			    	TrimEx bloodPressureTrim=trimCreatorBean.createTrim(activeAccountUser, "obs/evn/bloodPressure", path, now);
			    	int i = 0;
			    	if (!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(2).getAct().getObservation().getValues().get(1).getST().getValue().equals(" ")&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(2).getAct().getObservation().getValues().get(1).getST().getValue().equals(""))&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(2).getAct().getObservation().getValues().get(1).getST().getValue().equals(null))) {
						double systolic = Double.parseDouble(((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(2).getAct().getObservation().getValues().get(1).getST().getValue());
						((ActEx)bloodPressureTrim.getAct()).getRelationship().get("systolic").getAct().getObservation().getValues().get(0).getPQ().setValue(systolic);
						i = 1;
					}
			    	if (!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(3).getAct().getObservation().getValues().get(1).getST().getValue().equals(" ")&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(3).getAct().getObservation().getValues().get(1).getST().getValue().equals(""))&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(3).getAct().getObservation().getValues().get(1).getST().getValue().equals(null))) {
						double diastolic = Double.parseDouble(((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(3).getAct().getObservation().getValues().get(1).getST().getValue());
						((ActEx)bloodPressureTrim.getAct()).getRelationship().get("diastolic").getAct().getObservation().getValues().get(0).getPQ().setValue(diastolic);
						i = 1;
					}
			    	if(i == 1){
			    		bloodPressureTrim.getAct().getEffectiveTime().setTS(ts);
			    		trimCreatorBean.submitTrim(bloodPressureTrim, path, activeAccountUser, now);
			    	}
			    	
			    	//Respiration Trim
			    	TrimEx respirationTrim=trimCreatorBean.createTrim(activeAccountUser, "obs/evn/respiration", path, now);
			    	if (!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(4).getAct().getObservation().getValues().get(0).getPQ().getValue().equals(" ")&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(4).getAct().getObservation().getValues().get(0).getPQ().getValue().equals(""))&&(!((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(4).getAct().getObservation().getValues().get(0).getPQ().getValue().equals(null))) {
						double respiration = ((ActEx)wizardTrim.getAct()).getRelationship().get("objective").getAct().getRelationships().get(4).getAct().getObservation().getValues().get(0).getPQ().getValue();
						((ActEx)respirationTrim.getAct()).getObservation().getValues().get(0).getPQ().setValue(respiration);
						respirationTrim.getAct().getEffectiveTime().setTS(ts);
						trimCreatorBean.submitTrim(respirationTrim, path, activeAccountUser, now);
			    	}
					writer.write( "success");
					req.setAttribute("activeWriter", writer);
					return;
				}
		    	catch (Exception e) {
		    		System.out.println("Observation Trim Error(s):"+e);
		    	}
		    }
		}
		catch (Exception e) {
			throw new ServletException( "Exception thrown in IntantiateServlet", e);
		}
	}
	 
	/**
	 * This function is used to get CE from a valueSet based on CE display name.
	 * @param valueSetName
	 * @param ceDisplayName
	 * @param trim
	 * @return exactCE
	 */
	public CE getCE(String valueSetName, String ceDisplayName,TrimEx trim) {
		CE ce = new CE();
		CE exactCE = new CE();
		Map<String, ValueSet> valueSetMap =new HashMap<String, ValueSet>();
		List<ValueSet> valueSets = trim.getValueSets();
		
		for (Object vs : valueSets) {
			ValueSet valueSet = (ValueSet) vs;
			valueSetMap.put(valueSet.getName(), valueSet);
		}
		
		for(Object obj : valueSetMap.get(valueSetName).getBindsAndADSAndCDS()) {
			try {
				if (obj instanceof CE) {
					ce = (CE) obj;
					
					if (ce.getDisplayName().equals(ceDisplayName)) {    
						exactCE = ce;					
						break;
					}
				}
			}
			catch (Exception e) {
				TolvenLogger.info(e.getMessage(), this.getClass());
				e.printStackTrace();
			}
		}
		
		return exactCE;
	}
}
