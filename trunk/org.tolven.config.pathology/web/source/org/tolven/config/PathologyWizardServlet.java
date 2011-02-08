package org.tolven.config;
import java.io.IOException;
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

import org.tolven.ajax.InstantiateServlet;
import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MenuData;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.entity.DocXML;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.LDAPLocal;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.CE;
import org.tolven.trim.Choice;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ActRelationshipsMap;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;
import org.tolven.trim.ex.ValueSetEx;

public class PathologyWizardServlet extends HttpServlet {
	private static final String TRIMns = "urn:tolven-org:trim:4.0";
	@EJB
    private CreatorLocal creatorBean;

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
    private TolvenPropertiesLocal propertyBean;
    
    public DocumentLocal getDocBean() {
        return docBean;
    }

    public DocProtectionLocal getDocProtectionBean() {
        return docProtectionBean;
    }
    
    public void init(ServletConfig config) throws ServletException {
	/*	try
        {
            InitialContext ctx = new InitialContext();
            //TODO Injection does not work for JBoss (v4.0.4GA) web tier (tomcat), but does for GlassFish
            if (creatorBean == null) {
                creatorBean = (CreatorLocal) ctx.lookup("tolven/CreatorBean/local");
            }
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
            if (ldapBean == null) {
                ldapBean = (LDAPLocal) ctx.lookup("tolven/LDAPBean/local");
            }
            if (xmlBean == null) {
                xmlBean = (XMLLocal) ctx.lookup("tolven/XMLBean/local");
            }
        }
        catch (NamingException e)
        {
           throw new RuntimeException(e);
        }*/
		
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try{
		String uri = req.getRequestURI();
		AccountUser activeAccountUser = (AccountUser) req.getAttribute("accountUser");
		resp.setContentType("text/xml");
		resp.setCharacterEncoding("UTF-8");
	    resp.setHeader("Cache-Control", "no-cache");
	    
    	Date now = (Date) req.getAttribute("tolvenNow");
    	String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        PrivateKey userPrivateKey = TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm);
    	TrimFactory trimFactory = new TrimFactory(); 
    	if (uri.endsWith("modifySpecimen.ajaxp")) {   // will be removed once we decide on query mechanism for trim
			String element = req.getParameter( "element");
			int procedureIndex = Integer.parseInt(req.getParameter( "procedureIndex"));
			int actionType = Integer.parseInt(req.getParameter( "actionType"));
			if (element == null ) throw new IllegalArgumentException( "Missing element in removeSpecimen");			
			TolvenLogger.info( "[modifySpecimen] account=" + activeAccountUser.getAccount().getId() + ", procIndex=" + procedureIndex, InstantiateServlet.class);
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			ActRelationship ar = ((ActEx)trim.getAct()).getRelationshipsList().get("procedure").get(procedureIndex);
			if(actionType == 0){	// create specimen
				String template = req.getParameter( "template");
				TrimEx _trimEx = trimBean.findTrim(template);
				Act _act = ((ActEx)_trimEx.getAct()).getRelationship().get("procedure").getAct();
				ar.getAct().getRelationships().add(((ActEx)_act).getRelationship().get("specimen"));
			}else if(actionType == 1){  // delete specimen
				int specimenIndex = Integer.parseInt(req.getParameter( "specimenIndex"));
				((ActEx)ar.getAct()).getRelationshipsList().get("specimen").remove(specimenIndex);
				((ActRelationshipsMap)((ActEx)ar.getAct()).getRelationshipsList()).refreshList();
			}
			// Write it back and we're done.
			creatorBean.marshalToDocument( trim, docXML );
			//writer.close();
			return;
	    }else if (uri.endsWith("getTrimOptions.ajaxp")) {
	    	String trimName = req.getParameter("trimName");
	    	TrimEx _trimEx = trimBean.findTrim(trimName);
	    	Act _actToCopy = ((ActEx)_trimEx.getAct()).getRelationship().get("procedure").getAct();
	    	String _text = "{";
	    	if(_actToCopy != null && _actToCopy.getObservation() != null 
	    			&& _actToCopy.getObservation().getMethodCode() !=null){
	    		_text +="methodCode:\""+ _actToCopy.getObservation().getMethodCode().getValueSet()+"\",";
	    		_text +="methodCodeTitle:\""+ _actToCopy.getObservation().getMethodCode().getLabel().getValue()+"\",";
	    		
	    	}
	    	int v = _trimEx.getValueSets().size();
	    	for(ValueSet vs : _trimEx.getValueSets()){
	    		ValueSetEx vsx = (ValueSetEx)vs;
	    		_text += vs.getName()+":\"";
	    		int c = vsx.getValues().size();
	    		for(Object _obj : vsx.getValues()){
	    			CE _ce = (CE)_obj;
	    			_text +=trimFactory.dataTypeToString(_ce);
	    			
	    			if(c > 1)
	    				_text += ",";
	    			c--;
	    		}
	    		_text += "\"";
	    		if(v > 1)
	    			_text += ",";
	    		v--;	    		
	    	}
	    	_text += "}";
	    	Writer writer = resp.getWriter();
	    	writer.write(_text);
    		req.setAttribute("activeWriter", writer);
    		//writer.close();
			return;
	    }else if (uri.endsWith("saveUpdateProcedure.ajaxp")) {
	    	String element = req.getParameter( "element");
			String templateName = req.getParameter( "template");
			String laterality = req.getParameter("laterality");
			String location = req.getParameter("location");
			String methodCodes = req.getParameter("methodCode");
			int actionType = Integer.parseInt(req.getParameter("actionType"));
			CE locationCE = location!=null?(CE)trimFactory.stringToDataType(location):null;
			CE lateralityCE = laterality!=null?(CE)trimFactory.stringToDataType(laterality):null;
			 TolvenLogger.info("Action "+actionType+" element "+element, InstantiateServlet.class);	
			ActRelationship procedureRel = null;
			// SK: load the trim for current wizard
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			// Note: We're reading this in from a mutable document.
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			// Unmarshall
			TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			TrimExpressionEvaluator evaluator = new TrimExpressionEvaluator();
			evaluator.addVariable("wizardTrim", wizardTrim);
			if(actionType == 0) {// create
			    TrimEx _trimEx = trimBean.findTrim(templateName);
			    evaluator.addVariable("trim", _trimEx);
			    procedureRel = (ActRelationship)evaluator.evaluate("#{trim.act.relationship['procedure']}");
			    wizardTrim.getAct().getRelationships().add(procedureRel); // create new
			    TolvenLogger.info("Adding procedure "+req.getParameter("procTitle"), InstantiateServlet.class);
		 	}else if(actionType == 1){  // update/recreate procedure  
				int procedureIndex = Integer.parseInt(req.getParameter("procedureIndex"));
				procedureRel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("procedure").get(procedureIndex);
				TolvenLogger.info("Updating procedure "+req.getParameter("procTitle"), InstantiateServlet.class);
			}else if(actionType == 2){ // delete procedure
				int procedureIndex = Integer.parseInt(req.getParameter("procedureIndex"));
				ActRelationship deleteAct = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("procedure").remove(procedureIndex);
				((ActRelationshipsMap)((ActEx)wizardTrim.getAct()).getRelationshipsList()).refreshList();
				/*evaluator.addVariable("deleteAct", deleteAct);
				deleteMenuDataItem(deleteAct.getAct(),activeAccountUser);
				deleteMenuDataItem((Act)evaluator.evaluate("#{deleteAct.act.relationship['specimen'].act}"),activeAccountUser);
				 */
				TolvenLogger.info("Deleting procedure "+req.getParameter("procTitle"), InstantiateServlet.class);
				//writer.write("deleted procedure with index "+procedureIndex);
				creatorBean.marshalToDocument( wizardTrim, docXML );
				//writer.write("Success");
				
				return;
			}

	    	//add all the act from choices to the base trim 
	    	List<Choice> choiceList = procedureRel.getChoices();
	    	for(Choice choice:choiceList){
	    		TrimEx _choiceTrim = trimBean.findTrim(choice.getInclude());
	    		evaluator.addVariable("choiceTrim", _choiceTrim);
	    		//check if the choice is already added
	    		ActRelationship choiceAct = null;
	    		if(evaluator.evaluate("#{wizardTrim.act.relationship['"+lateralityCE.getDisplayName()+choice.getTitle()+"']}") != null)
	    			choiceAct = (ActRelationship)evaluator.evaluate("#{wizardTrim.act.relationship['"+lateralityCE.getDisplayName()+choice.getTitle()+"']}");
	    		else
	    			choiceAct = (ActRelationship) evaluator.evaluate("#{choiceTrim.act.relationship['"+lateralityCE.getDisplayName()+choice.getTitle()+"']}");
	    		if(choiceAct == null)
	    			continue;
	    		evaluator.addVariable("choiceAct", choiceAct);
	    		evaluator.setValue("#{choiceAct.act.relationship['location'].act.observation.value.CE}", (CE)trimFactory.stringToDataType(location));
	    		wizardTrim.getAct().getRelationships().add(choiceAct);
	    	}
	    	
			evaluator.addVariable("procedureRel", procedureRel);
			evaluator.setValue("#{procedureRel.act.title.ST.value}",req.getParameter("procTitle"));
			procedureRel.setSourceTrim(templateName);
		    if(methodCodes != null && methodCodes.trim().length() > 0){
		    	String[] _methods = methodCodes.split(",");
		    	List<CE> methodCodeList = (List<CE>) evaluator.evaluate("#{procedureRel.act.observation.methodCode.CES}");//procedureRel.getAct().getObservation().getMethodCode().getCES();
		    	methodCodeList.clear();
		    	for(int i=0;i<_methods.length;i++){
		    		methodCodeList.add((CE)trimFactory.stringToDataType(_methods[i]));
		    	}		    	
		    }
		    if(lateralityCE != null)
		    evaluator.setValue("#{procedureRel.act.relationship['laterality'].act.observation.value.CE}",lateralityCE);
		    if(locationCE != null)				   
		    evaluator.setValue("#{procedureRel.act.relationship['laterality']" +
		    		".act.relationship['location'].act.observation.value.CE}",locationCE);
			creatorBean.marshalToDocument( wizardTrim, docXML );
			//writer.write("Success");
    		
    		TolvenLogger.info("has procedures "+((ActEx)wizardTrim.getAct()).getRelationshipsList().get("procedure").size(), InstantiateServlet.class);
			//writer.close();
			//return;
	    }
	}catch (Exception e) {
		throw new ServletException(e.getMessage());
	}
	}
}

