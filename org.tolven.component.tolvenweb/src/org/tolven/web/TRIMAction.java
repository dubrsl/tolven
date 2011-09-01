package org.tolven.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MDQueryResults;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Status;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocumentType;
import org.tolven.logging.TolvenLogger;
import org.tolven.process.ValidationException;
import org.tolven.provider.entity.MyProvider;
import org.tolven.provider.entity.PatientLink;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.trim.Act;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Application;
import org.tolven.trim.BindPhase;
import org.tolven.trim.CE;
import org.tolven.trim.CESlot;
import org.tolven.trim.CV;
import org.tolven.trim.Compute;
import org.tolven.trim.CopyTo;
import org.tolven.trim.DataType;
import org.tolven.trim.EN;
import org.tolven.trim.ENXPSlot;
import org.tolven.trim.Entity;
import org.tolven.trim.EntityNamePartType;
import org.tolven.trim.Field;
import org.tolven.trim.LivingSubject;
import org.tolven.trim.NullFlavor;
import org.tolven.trim.ParticipationType;
import org.tolven.trim.Role;
import org.tolven.trim.TSSlot;
import org.tolven.trim.TolvenId;
import org.tolven.trim.Trim;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.CopyToEx;
import org.tolven.trim.ex.PartyEx;
import org.tolven.trim.ex.TSEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

public class TRIMAction extends MenuAction {
	private static final TrimFactory trimFactory = new TrimFactory();
	private TolvenLogger log;
	private TrimEx trim;
	private List<CopyToEx> routing;
    private DataModel outboundPatientLinks;
    private DataModel psychModel1;
    private DataModel psychModel2;
    private DataModel psychScaleModel;
    private int psychCountLeft;
	private MDList mdList = null;
	private PartyList partyList = null;
	private List<HashMap<String, String>> history;

	private List<SelectItem> activeEncounters;
        
	private Map<String, List<SelectItem>> valueSets;
	
//	private CE dummy;
	
	public TRIMAction() {
		super();
		log = TolvenLogger.getLogger(TRIMAction.class);
	}
	
	public TrimEx parseTrim( MenuData md ) throws NamingException, JAXBException {
		if (md.getDocumentId()==0) {
			throw new RuntimeException( "[getTrim] No Document in MenuData " + md.getId());
		}
		log.debug( "[getTrim] MD.id=" + md.getId() + " Path=" + md.getPath());
		DocBase doc = getDocumentLocal().findDocument(md.getDocumentId());
		if (doc==null) {
			throw new RuntimeException( "[getTrim] Document id invalid in MD.id=" + md.getId());
		}
		if (!(doc instanceof DocXML)) {
			throw new RuntimeException( "[getTrim] Document is not XML MD.id=" + md.getId());
		}
		if (!((DocXML)doc).getXmlNS().equals(TRIM_NS) ) {
			throw new RuntimeException( "[getTrim] XML Document is not TRIM MD.id=" + md.getId());
		}
		// Get the object graph
		return (TrimEx) getXMLProtectedBean().unmarshal((DocXML)doc, getTop().getAccountUser(), getUserPrivateKey() );
	}
	
	/**
	 * Get the root act of the trim object
	 * @return
	 * @throws Exception
	 */
	public TrimEx getTrim() throws Exception {
		if (trim==null) {
			MenuData md = getDrilldownItem();
			if (md==null) {
				throw new RuntimeException( "[getTrim] No drilldown Item");
			}
			trim = parseTrim( md );
		}
		return trim;
	}

	/**
	 * Get the raw XML representation of the trim object
	 * @return
	 * @throws Exception
	 */
	public String getTrimXML() throws Exception {
		MenuData md = getDrilldownItem();
		if (md==null) return null;
		if (md.getDocumentId()==0) return null;
		DocBase doc = getDocumentLocal().findDocument(md.getDocumentId());
		if (doc==null) {
			throw new RuntimeException( "Document id invalid in MD " + md.getId());
		}
		if (!(doc instanceof DocXML)) {
			return null;
		}
        AccountUser activeAccountUser = getAccountUser();
        return getDocProtectionBean().getDecryptedContentString(doc, activeAccountUser, getUserPrivateKey());
	}

	public void setTrim(TrimEx trim) {
		this.trim = trim;
	}
	/**
	 * Present a List of SelectItems based on the contents of a list in MenuData
	 * @author John Churin
	 *
	 */
	class MDList extends HashMap<Object, List<SelectItem>> {
		private MenuLocal menuLocal;
		private long accountId;
		private String contextPath;
		public MDList( MenuLocal menuLocal, long accountId, String context ) {
			this.menuLocal = menuLocal;
			this.accountId = accountId;
			this.contextPath = context;
		}
		
		public void buildList(Object path ) {
			MenuQueryControl ctrl = new MenuQueryControl();
			String parsedPath = (String)path;
			String columnsStr;
			if (StringUtils.contains(parsedPath, "~"))
			{
				String[] pathKeys = parsedPath.split("~", 2);
				parsedPath = pathKeys[0];
				columnsStr = pathKeys[1];
			} else {
				// Default layout if none specified
				columnsStr = "string01 string02";
			}
			
			Pattern columnPattern = Pattern.compile("\\w++");
			Matcher columnMatcher = columnPattern.matcher(columnsStr);
			while (columnMatcher.find()) {
				ctrl.getColumns().add(columnMatcher.group());
			}
			MenuStructure ms = menuLocal.findMenuStructure(accountId, parsedPath );
			Map<String, Long> nodeValues = new HashMap<String, Long>(10);
			// If we have context, use it, too
			if (contextPath!=null) {
				nodeValues.putAll(new MenuPath( contextPath ).getNodeValues());
			}
			ctrl.setMenuStructure( ms );
			ctrl.setNow( getNow());
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setOriginalTargetPath( new MenuPath(ms.instancePathFromContext ( nodeValues, true )));
			ctrl.setRequestedPath( ctrl.getOriginalTargetPath() );
			MDQueryResults results = menuLocal.findMenuDataByColumns( ctrl );
			List<SelectItem> list = new ArrayList<SelectItem>(results.getRowCount());
			// Run through each row returned
			for ( Map<String,Object> row : results.getResults()) { 
				StringBuffer sb = new StringBuffer();
				Matcher outputMatcher = columnPattern.matcher(columnsStr);
				// For each column specified in the pattern
				while (outputMatcher.find()) {
					String columnName = outputMatcher.group();
					Object columnValue = row.get(columnName);
					if (columnValue instanceof String) {
						outputMatcher.appendReplacement(sb, (String)columnValue);
					} else if (columnValue==null) {
						outputMatcher.appendReplacement(sb, "");
					} else {
						throw new RuntimeException( "Non-string column value in column [" + columnName + "]  in " + path );
					}
				}
				outputMatcher.appendTail(sb);
				// Add the resulting string to the list of choices - using the MD item path as the key
				Object itemPath = row.get("referencePath");
				if (itemPath==null) {
					itemPath = row.get("path");
				}
				SelectItem e = new SelectItem(itemPath, sb.toString() );
				list.add(e);
			}
			this.put(path, list);
		}
		
		public List<SelectItem> get(Object path) {
			if (!this.containsKey(path)) buildList(path);
			return super.get(path);
		}
	}

	/**
	 * Create a selection list based on the contents of a menuData list.
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public MDList getList() throws Exception {
		if (mdList==null) {
			mdList = new MDList( this.getMenuLocal(), this.getAccountId(), this.getElement() );
		}
		return mdList;
	}
	/**
	 * Present a List of SelectItems<Party,Label> based on the contents of a list in MenuData
	 * @author Srini Kandula
	 *
	 */
	class PartyList extends HashMap<Object, List<SelectItem>> {
		private MenuLocal menuLocal;
		private long accountId;
		private String contextPath;
		public PartyList( MenuLocal menuLocal, long accountId, String context ) {
			this.menuLocal = menuLocal;
			this.accountId = accountId;
			this.contextPath = context;
		}
		
		public void buildList(Object path ) {
			MenuQueryControl ctrl = new MenuQueryControl();
			String parsedPath = (String)path;
			String columnsStr;
			if (StringUtils.contains(parsedPath, "~"))
			{
				String[] pathKeys = parsedPath.split("~", 2);
				parsedPath = pathKeys[0];
				columnsStr = pathKeys[1];
				ctrl.setSortOrder(columnsStr);
			} else {
				// Default layout if none specified
				columnsStr = "string01 string02";
			}
			
			Pattern columnPattern = Pattern.compile("\\w++");
			Matcher columnMatcher = columnPattern.matcher(columnsStr);
			while (columnMatcher.find()) {
				ctrl.getColumns().add(columnMatcher.group());
			}
			MenuStructure ms = menuLocal.findMenuStructure(accountId, parsedPath );
			Map<String, Long> nodeValues = new HashMap<String, Long>(10);
			nodeValues.putAll(new MenuPath( contextPath ).getNodeValues());
			ctrl.setMenuStructure( ms );
			ctrl.setNow( getNow());
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setOriginalTargetPath( new MenuPath(ms.instancePathFromContext ( nodeValues, true )));
			ctrl.setRequestedPath( ctrl.getOriginalTargetPath() );
			MDQueryResults results = menuLocal.findMenuDataByColumns( ctrl );
			List<SelectItem> list = new ArrayList<SelectItem>(results.getRowCount());
			// Run through each row returned
			for ( Map<String,Object> row : results.getResults()) { 
				StringBuffer sb = new StringBuffer();
				Matcher outputMatcher = columnPattern.matcher(columnsStr);
				// For each column specified in the pattern
				while (outputMatcher.find()) {
					String columnName = outputMatcher.group();
					Object columnValue = row.get(columnName);
					if (columnValue instanceof String) {
						outputMatcher.appendReplacement(sb, (String)columnValue);
					} else if (columnValue==null) {
						outputMatcher.appendReplacement(sb, "");
					} else {
						throw new RuntimeException( "Non-string column value in column [" + columnName + "]  in " + path );
					}
				}
				outputMatcher.appendTail(sb);
				// Add the resulting string to the list of choices - using the MD item path as the key
				Object itemPath = row.get("referencePath");
				if (itemPath==null) {
					itemPath = row.get("path");
				}
				String args[]={itemPath.toString(),String.valueOf(getTop().getAccountUser().getAccount().getId()),"0"}; //TODO: getProviderID
				PartyEx p = TrimFactory.createParty(args);
				SelectItem e = new SelectItem(p, sb.toString() );
				list.add(e);
			}
			this.put(path, list);
		}
		
		public List<SelectItem> get(Object path) {
			if (!this.containsKey(path)) buildList(path);
			return super.get(path);
		}
	}

	/**
	 * Create a selection(Party,Name) list based on the contents of a menuData list.
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public PartyList getPartyList() throws Exception {
		if (partyList==null) {
			partyList = new PartyList( this.getMenuLocal(), this.getAccountId(), this.getElement() );
		}
		return partyList;
	}

	
	/**
	 * Action method that gets a list of the patient's active encounters.
	 * @return DataModel suitable for Faces selectItems
	 * @throws Exception 
	 */
	public List<SelectItem> getActiveEncounters() throws Exception {
		if (activeEncounters==null) {
			// Get the patient's encounter menuData
			String patientPath = getTargetMenuPath().getSubPath("patient");
//			TolvenLogger.info( "[TRIMAction::getActiveEncounters] patient=" + patientPath, TRIMAction.class );
			MenuStructure patientMS =  getMenuLocal().findMenuStructure( getAccountId(), patientPath );
			MenuStructure encounterMS =  getMenuLocal().findDescendentMenuStructure(getAccountId(), (AccountMenuStructure)patientMS, "encounters:list");
			
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setMenuStructure( encounterMS.getAccountMenuStructure() );
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setNow( getNow());
			ctrl.setRequestedPath( new MenuPath( getTargetMenuPath().getSubPathWithIds("patient")) );
//			TolvenLogger.info( "ActiveEncounter query control: " + ctrl, TRIMAction.class );
			
			List<MenuData> patientEncounters =  getMenuLocal().findMenuData( ctrl );
			activeEncounters = new ArrayList<SelectItem>();
			activeEncounters.add(new SelectItem( null, "None"));
			for (MenuData md : patientEncounters){
				activeEncounters.add(new SelectItem( md.getPath(), md.getString01() + " "+ md.getDate01()));
//				TolvenLogger.info( "Encounter: " + md.getPath(), TRIMAction.class );
			}
		}
		return activeEncounters;
	}

	public void setActiveEncounters(List<SelectItem> activeEncounters) {
		this.activeEncounters = activeEncounters;
	}
	
	/**
	 * Action method that gets a list of the patient's providers (not all providers for the account).
	 * @return
	 * @throws Exception 
	 */
	public DataModel getOutboundPatientLinks() throws Exception {
		if (outboundPatientLinks==null) {
			// Get the patient menuData
			String patientPath = getTargetMenuPath().getSubPath("patient");
			log.debug( "[TRIMAction::getOutboundPatientLinks] patient=" + patientPath );
			MenuStructure patientMS =  getMenuLocal().findMenuStructure( getAccountId(), patientPath );
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setMenuStructure( patientMS.getAccountMenuStructure() );
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setNow( getNow());
			ctrl.setRequestedPath( new MenuPath( getTargetMenuPath().getSubPathWithIds("patient")) );
			MenuData patientMD =  getMenuLocal().findMenuDataItem( ctrl );
			if (patientMD==null) throw new RuntimeException( "Patient not found ");
			log.debug( "[TRIMAction::getOutboundPatientLinks]Patient is: " + patientMD.getPath());
			outboundPatientLinks = new ListDataModel();
			List<PatientLink> patientLinks = getProviderBean().findOutboundPatientLinks( patientMD );
			outboundPatientLinks.setWrappedData( patientLinks );
		}
		return outboundPatientLinks;
	}

	public List<CopyToEx> prepareRouting( boolean patientSide ) throws Exception {
		if (routing==null) {
			routing = new ArrayList<CopyToEx>(10);
			String patientPath = getTargetMenuPath().getSubPath("patient");
//			TolvenLogger.info( "[TRIMAction::prepareRouting] patient=" + patientPath, TRIMAction.class );
			if (patientPath==null) {
				return routing;
			} 
			MenuStructure patientMS =  getMenuLocal().findMenuStructure( getAccountId(), patientPath );
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setMenuStructure( patientMS.getAccountMenuStructure() );
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setNow( getNow());
			ctrl.setRequestedPath( new MenuPath( getTargetMenuPath().getSubPathWithIds("patient")) );
			MenuData patientMD =  getMenuLocal().findMenuDataItem( ctrl );
			if (patientMD==null) throw new RuntimeException( "Patient not found ");
			// Get the list so far from the document
			// Make a list hash set
			Map<Long, CopyToEx> existing = new HashMap<Long, CopyToEx>(20);
			for ( CopyTo copyTo : getTrim().getCopyTos() ) {
				existing.put(copyTo.getPatientLinkId(), (CopyToEx)copyTo);
			}
			List<PatientLink> links;
			if (patientSide) links = getProviderBean().findOutboundPatientLinks(patientMD );
			else links = getProviderBean().findInboundPatientLinks(patientMD );
			// Make sure any copyTo we know about is also in the list.
			for ( PatientLink link : links ) {
				CopyToEx copyTo = existing.get(link.getId());
				if (copyTo==null) {
					copyTo = (CopyToEx) trimFactory.createCopyTo();
					copyTo.setPatientLinkId(link.getId());
					copyTo.setCopy(false);
				}
				if (link.getProvider()!=null) {
					if (link.getProvider().getOwnerAccount()!=null) {
						copyTo.setAccountName(link.getProvider().getOwnerAccount().getTitle());
					}
					copyTo.setProvider(link.getProvider());
					copyTo.setProviderId(link.getProvider().getId());
					copyTo.setPatientLink(link);
					copyTo.setAccountId(link.getProvider().getOwnerAccount().getId());
					routing.add(copyTo);
				}
			}
		}
		return routing;
	}
	
	/**
	 * Put together a list of accounts we could send this item to based on the providers selected by
	 * the patient. The patient link must already be established and active. Use this for data contained by the patient.
	 * @return
	 * @throws Exception
	 */
	public List<CopyToEx> getOutboundRouting() throws Exception {
		return prepareRouting( true );
	}
	
	/**
	 * Put together a list of accounts we could send this item to. This is used on the provider side to 
	 * to get a list of patient records we've received and thus can return information to.
	 * @return
	 * @throws Exception
	 */
	public List<CopyToEx> getInboundRouting() throws Exception {
		return prepareRouting( false );
	}
	
	/**
	 * A shortcut method so faces page does not have to figure out which "side" (ephr or echr) we're on. 
	 * @return
	 * @throws Exception
	 */
	public List<CopyToEx> getRouting( )  throws Exception{
		String knownType = getTop().getAccountUser().getAccount().getAccountType().getKnownType();
		if ("echr".equals(knownType)) return getInboundRouting();
		if ("ephr".equals(knownType)) return getOutboundRouting();
		return null;
	}

	/**
	 * Return a list of possible providers for a new patient. 
	 */
	public  List<CopyToEx> getNewPatientRouting( ) throws Exception {
		if (routing==null) {
			routing = new ArrayList<CopyToEx>(10);
			List<MyProvider> myProviders = getProviderBean().findMyActiveProviders( this.getAccountId() );
//			TolvenLogger.info( "TrimAction::getNewPatientRouting provider count: " + myProviders.size(), TRIMAction.class);
			// This is similar but not identical to the code in prepare routing
			Map<Long, CopyToEx> existing = new HashMap<Long, CopyToEx>(20);
			for ( CopyTo copyTo : getTrim().getCopyTos() ) {
				existing.put(copyTo.getProviderId(), (CopyToEx)copyTo);
			}
			for ( MyProvider myProvider : myProviders ) {
				CopyToEx copyTo = existing.get(myProvider.getProvider().getId());
				if (copyTo==null) {
					copyTo = (CopyToEx) trimFactory.createCopyTo();
					copyTo.setProviderId(myProvider.getProvider().getId());
					copyTo.setCopy(false);
				}
				copyTo.setProvider(myProvider.getProvider());
				copyTo.setAccountId(myProvider.getProvider().getOwnerAccount().getId());
				copyTo.setAccountName(myProvider.getProvider().getName());
				routing.add(copyTo);
			}
		}
		return routing;
	}
	
	public List<MenuData> getDupeList() throws Exception {
		MenuStructure ms;
		if ("echr".equals(getTop().getAccountType())) {
			ms = getMenuLocal().findMenuStructure(getAccountId(), "echr:patients:all");
		} else if ("ephr".equals(getTop().getAccountType())) {
			ms = getMenuLocal().findMenuStructure(getAccountId(), "ephr:patients:all");
		} else return null;
		// Populate criteria (this is the same logic as in the rules that populate the MenuData item for real.
		MenuData criteria = new MenuData();
		Act act = (Act)getTrim().getAct();
		Role role = (Role) act.getParticipations().get(0).getRole();
		Entity entity = (Entity) role.getPlayer();
		EN en = entity.getName().getENS().get(0);
		for (ENXPSlot enxp : en.getParts()) {
			if (enxp.getType() == EntityNamePartType.FAM) {
				criteria.setString01(enxp.getST().getValue()); 
			}
			if (enxp.getType() == EntityNamePartType.GIV) {
				if (criteria.getString02()==null) {
					criteria.setString02(enxp.getST().getValue()); 
				} else {
					if (criteria.getString03()==null) {
						criteria.setString03(enxp.getST().getValue()); 
					} else {
						criteria.setString03(criteria.getString03() + " " + enxp.getST().getValue()); 
					}
				}
			}
		}
		LivingSubject liv = entity.getLivingSubject();
		if (liv!=null) {
			TSSlot birthTimeSlot = liv.getBirthTime();
			if (birthTimeSlot!=null) {
				if (birthTimeSlot.getTS()!=null) {
					criteria.setDate01(((TSEx)birthTimeSlot.getTS()).getDate());
				}
			}
			CESlot genderSlot = liv.getAdministrativeGenderCode();
			if (genderSlot!=null) {
//				TolvenLogger.info( "Got Gender slot", TRIMAction.class); 
				CE ce = genderSlot.getCE();
				if (ce!=null) {
//					TolvenLogger.info( "Got Gender CE", TRIMAction.class); 
					if ("C0015780".equals( ce.getCode() )) {
						criteria.setString04("Female");
					}
					if ("C0024554".equals( ce.getCode() )) {
						criteria.setString04("Male");
					}
				}
			}
		}
		return getMatchBean().match(ms, 30, criteria);
	}

	/**
	 * Return a list of only the selected copyTo entries for this item
	 * @return 
	 * @throws Exception
	 */
	public List<CopyToEx> getSelectedRouting() throws Exception {
		List<CopyToEx> rslt = new ArrayList<CopyToEx>( getTrim().getCopyTos().size() );
		for ( CopyTo copyTo : getTrim().getCopyTos() ) {
				if (copyTo.isCopy()) rslt.add( (CopyToEx) copyTo);
		}
		return rslt;
	}

	public boolean isShowSelectedRouting() throws Exception {
		for ( CopyTo copyTo : getTrim().getCopyTos() ) {
			if (copyTo.isCopy()) return true;
		}
		return false;
	}

	public boolean isSignatureRequired() throws Exception {
        AccountUser accountUser = getAccountUser();
        String knownType = accountUser.getAccount().getAccountType().getKnownType();
        for (Application application : getTrim().getApplications()) {
            if (knownType.equals(application.getName())) {
                return application.isSignatureRequired();
            }
        }
	    return false;
	}
	
	public String getTrimSignatures() throws Exception {
        MenuData md = getMenuDataItem();
        if (md == null)
            return null;
        if (md.getDocumentId() == 0)
            return null;
        DocBase doc = getDocumentLocal().findDocument(md.getDocumentId());
        if (doc == null) {
            throw new RuntimeException("Document id invalid in MD " + md.getId());
        }
        return getDocProtectionBean().getDocumentSignaturesString(doc, getTop().getAccountUser(), getUserPrivateKey());
    }
	
	public void setRouting( List<CopyToEx> routing) {
		log.debug( "[TrimAction.setRouting]");
		this.routing = routing;
	}
	
	public Act findEncounterAct( ) throws Exception {
		Act act = (Act) getTrim().getAct();
		for (ActRelationship rel : act.getRelationships()) {
			if ("encounter".equals(rel.getName())) {
				return (Act) rel.getAct();
			}
		}
		return null;
	}
	
	// If we're talking to the patient, need to get the patientLink.
	public void setupPatientLink( ) throws Exception {
		// Assume that we have a patient
		MenuData mdPatient = getMenuDataItem().getParent01();
		log.debug("setupPatientLink for patient: " + mdPatient.getPath());
		List<PatientLink> patientLinks = getProviderBean().findInboundPatientLinks(mdPatient );
		Trim trim = getTrim();
		for (PatientLink patientLink : patientLinks) {
			TolvenLogger.info("setupPatientLink found patient link: " + patientLink.getId() + " " + patientLink.getRequest(), TRIMAction.class);
			trim.setPatientLinkId(patientLink.getId());
			CopyTo newCopyTo = null;
			for ( CopyTo copyTo : trim.getCopyTos() ) {
				if (copyTo.getPatientLinkId()==patientLink.getId()) {
					newCopyTo = copyTo;
					TolvenLogger.info("Found existing copyTo", TRIMAction.class);
					break;
				}
			}
			if (newCopyTo==null) {
				TolvenLogger.info("Creating new copyTo", TRIMAction.class);
				newCopyTo = trimFactory.createCopyTo();
				newCopyTo.setPatientLinkId(trim.getPatientLinkId());
				trim.getCopyTos().add(newCopyTo);
				TolvenLogger.info("Added copyTo to trim", TRIMAction.class);
			}
			newCopyTo.setAccountId(patientLink.getMyPatient().getAccount().getId());
			newCopyTo.setComment("Send encounter to patient");
			newCopyTo.setProviderId(patientLink.getProvider().getId());
			newCopyTo.setCopy(true);
			break;
		}
	}
	
	/**
	 * Remove subject(s) from this act and it's child acts, if any.
	 * @param trim
	 */
	public void removeSubject(Act act ) {
//		TolvenLogger.info( " Remove participations from act: " + act.getTitle(), TRIMAction.class);
		List<ActParticipation> removeList = new ArrayList<ActParticipation>();
		
		for ( ActParticipation participation : act.getParticipations()) {
//			TolvenLogger.info( "Marking participation to be deleted: " + participation.getName(), TRIMAction.class);
			if (ParticipationType.SBJ==participation.getTypeCode() || ParticipationType.RCT==participation.getTypeCode()) {
				removeList.add(participation);
			}
		}
		// Now remove the subjects we found.
//		TolvenLogger.info( Integer.toString(removeList.size()) + " participations deleted", TRIMAction.class);
		act.getParticipations().removeAll(removeList);

		// And look through children as well
		for ( ActRelationship ar : act.getRelationships()) {
			if (ar.getAct() instanceof Act)
			removeSubject((Act)ar.getAct());
		}
	}

	/**
	 * Remove subject(s) from this trim
	 * @param trim
	 */
	public void removeSubject(Trim trim ) {
		removeSubject((Act)trim.getAct());
	}
	/**
	 * Return list of computes that apply to the current AccountType and trim 
	 * @return
	 * @throws Exception 
	 */
	public List<Compute> getActiveComputes() throws Exception {
		String knownType = getTop().getAccountUser().getAccount().getAccountType().getKnownType();
		List<Compute> computes = new ArrayList<Compute>();
		for (Compute compute : getTrim().getAct().getComputes()) {
			if (compute.getForTrimName()!=null && !(compute.getForTrimName().equals(getTrim().getName()) )) continue;
			if (compute.getForAccountType()!=null && !(compute.getForAccountType().equals(knownType) )) continue;
			computes.add(compute);
		}
		return computes;
	}
	/**
	 * Marshal updated trim back into the document.
	 * Like submit but just store in TRIM and return 
	 * @throws Exception 
	 */
	public String upload( ) throws Exception {
		
		try {
			MenuData md = getMenuDataItem();
			if (md==null) {
				throw new RuntimeException( "upload requested but no menuDataItem specified");
			}
			DocBase doc = getDocumentLocal().findDocument(md.getDocumentId());
			if (doc==null) {
				throw new RuntimeException( "Document id invalid in MD " + md.getId());
			}
			if (!(doc instanceof DocXML)) {
				throw new RuntimeException( "Document is not XML " + doc.getId() + " Class: " + doc.getClass().getName());
			}
			if (!(Status.NEW.value().equals( doc.getStatus()))) {
				throw new RuntimeException( "Document is no longer editable " );
			}
			log.debug( "[Upload]" + trim.getPage());
			// Temp
			if ("iip-32.xhtml".equals(trim.getPage())) {
				savePsych();
			}
			
			// Scan for binding requirements in TRIM - request-phase
			MenuPath contextPath = new MenuPath(getElement());
			List<MenuPath> contextList = new ArrayList<MenuPath>();
			contextList.add(contextPath);
			DocumentType documentType = doc.getDocumentType();
			documentType.setAtachments(getDocumentLocal().findAttachments(doc));
			List<String> computeMsgs = getCreatorBean().computeScanWithResults(trim, getTop().getAccountUser(), contextPath, getNow(), documentType);
			// Convert Scan Results into FacetMessages.
			for (String msg : computeMsgs)
			{
				FacesContext.getCurrentInstance().addMessage("", new FacesMessage(msg));
			}

//			getCreatorBean().computeScan( trim, getTop().getAccountUser(), contextPath, getNow());
			// Re-check creations
			getCreatorBean().placeholderBindScan(getTop().getAccountUser(), trim, null, null, getNow(), BindPhase.CREATE, this.getDrilldownItemDoc());
			// Save the trim in the output
			ByteArrayOutputStream trimXML = new ByteArrayOutputStream() ;
			getXmlLocal().marshalTRIM(trim, trimXML);
	        String kbeKeyAlgorithm = getTolvenPropertiesBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
	        int kbeKeyLength = Integer.parseInt(getTolvenPropertiesBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
			doc.setAsEncryptedContent(trimXML.toByteArray(), kbeKeyAlgorithm, kbeKeyLength);
//			// Now update the event fields in the menuData if necessary.
//			// We do this after serialization just to ensure that nothing in this step goes backwards into the document
//			// since this is unidirectional process - from document to menudata.
//			getMenuLocal().populateMenuData( trim, "trim", md);
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String isRefresh = request.getParameter("refreshWizard");
			if (isRefresh != null && isRefresh.equals("true"))
			{
				return "refresh";
			}
			return "success";
		} catch (ValidationException e) {
//			log.error( "Validation Error: ", e);
			FacesContext.getCurrentInstance().addMessage( 
					null, 
					new FacesMessage("Validation: " +  e.toString()));
		} catch (RuntimeException e) {
			log.error( "Upload Error: ", e);
			FacesContext.getCurrentInstance().addMessage( 
					null, 
					new FacesMessage("Upload Error: " +  e.toString()));
		}
		return "fail";
	}

	
	public static class Scale implements Serializable {
		private static final long serialVersionUID = 1L;
		private String title;
		private int scale;
		private int score;

		public Scale( ) {
			scale = 0;
			score = 0;
		}

		public Scale( int scale, String title ) {
			this.scale = scale;
			this.title = title;
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getScale() {
			return scale;
		}
		public void setScale(int scale) {
			this.scale = scale;
		}
	}

	public static class MultipleChoice implements Serializable {
		private static final long serialVersionUID = 1L;
		private int id;
		private String question;
		private Integer answer;
		private int scale;
		
		public MultipleChoice( int id, String question, int scale ) {
			this.id = id;
			this.question = question;
			this.answer = null;
			this.scale = scale;
		}
		public Integer getAnswer() {
			return answer;
		}
		public void setAnswer(Integer answer) {
			this.answer = answer;
		}
		
		public String getQuestion() {
			return question;
		}
		public void setQuestion(String question) {
			this.question = question;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getScale() {
			return scale;
		}
		public void setScale(int scale) {
			this.scale = scale;
		}
	}
	public ArrayList<MultipleChoice> newPsych1( ) {
		ArrayList<MultipleChoice> psych = new ArrayList<MultipleChoice>(20);
		psych.add(new MultipleChoice( 1, "Say \"no\" to other people", 6));
		psych.add(new MultipleChoice( 2, "Join in on groups", 4));
		psych.add(new MultipleChoice( 3, "Keep things private from other people", 8));
		psych.add(new MultipleChoice( 4, "Tell a person to stop bothering me", 5));
		psych.add(new MultipleChoice( 5, "Introduce myself to new people", 4));
		psych.add(new MultipleChoice( 6, "Confront people with problems that come up", 5));
		psych.add(new MultipleChoice( 7, "Be assertive with another person", 5));
		psych.add(new MultipleChoice( 8, "Let other people know when I am angry", 6));
		psych.add(new MultipleChoice( 9, "Socialize with other people", 4));
		psych.add(new MultipleChoice( 10, "Show affection to people", 3));
		psych.add(new MultipleChoice( 11, "Get along with people", 3));
		psych.add(new MultipleChoice( 12, "Be firm when I need to be", 5));
		psych.add(new MultipleChoice( 13, "Experience a feeling of love for another person", 3));
		psych.add(new MultipleChoice( 14, "Be supportive of another person's goals in life", 2));
		psych.add(new MultipleChoice( 15, "Feel close to other people", 3));
		psych.add(new MultipleChoice( 16, "Really care about other people's problems", 2));
		psych.add(new MultipleChoice( 17, "Put somebody else's needs before my own", 2));
		psych.add(new MultipleChoice( 18, "Feel good about another person's happiness", 2));
		psych.add(new MultipleChoice( 19, "Ask other people to get together socially with me", 4));
		psych.add(new MultipleChoice( 20, "Be assertive without worrying about hurting the other person's feelings", 6));
		return psych;
	}

	public ArrayList<MultipleChoice> newPsych2( ) {
		ArrayList<MultipleChoice> psych = new ArrayList<MultipleChoice>(12);
		psych.add(new MultipleChoice( 21, "I open up to people too much", 8));
		psych.add(new MultipleChoice( 22, "I am too aggressive toward other people", 1));
		psych.add(new MultipleChoice( 23, "I try to please other people too much", 7));
		psych.add(new MultipleChoice( 24, "I want to be noticed too much", 8));
		psych.add(new MultipleChoice( 25, "I try to control other people too much", 1));
		psych.add(new MultipleChoice( 26, "I put other people's needs before my own too much", 7));
		psych.add(new MultipleChoice( 27, "I am overly generous to other people", 7));
		psych.add(new MultipleChoice( 28, "I manipulate other people too much to get what I want", 1));
		psych.add(new MultipleChoice( 29, "I tell personal things to other people too much", 8));
		psych.add(new MultipleChoice( 30, "I argue with other people too much", 1));
		psych.add(new MultipleChoice( 31, "I let other people take advantage of me too much", 6));
		psych.add(new MultipleChoice( 32, "I am affected by another person's misery too much", 7));
		return psych;
	}

	public ArrayList<Scale> newScales() {
		ArrayList<Scale> scales;
		scales = new ArrayList<Scale>(8);
		scales.add(new Scale( 1, "Domineering/Controlling"));
		scales.add(new Scale( 2, "Vindictive/Self-Centered"));
		scales.add(new Scale( 3, "Cold/Distant"));
		scales.add(new Scale( 4, "Socially Inhibited"));
		scales.add(new Scale( 5, "Nonassertive"));
		scales.add(new Scale( 6, "Overly Accommodating"));
		scales.add(new Scale( 7, "Self-Sacrificing"));
		scales.add(new Scale( 8, "Intrusive/Needy"));
		return scales;
	}
	/**
	 * Clear the current scores so we can recalculate
	 */
	public void clearPsychScales() {
		ArrayList<Scale> scales = (ArrayList<Scale>) psychScaleModel.getWrappedData();
		for (Scale scale : scales ) {
			scale.setScore(0);
		}
	}
	
	public void bumpScale(int scale, int answer) {
		ArrayList<Scale> scales = (ArrayList<Scale>) psychScaleModel.getWrappedData();
		for (Scale s : scales ) {
			if (s.getScale()==scale) {
				s.setScore(s.getScore()+answer);
				break;
			}
		}
	}
	/**
	 * Score the psych test, return the number of incomplete answers
	 * @return
	 */
	public int scorePsych( ) {
		clearPsychScales();
		int count = 32;
		ArrayList<MultipleChoice> psych1 = (ArrayList<MultipleChoice>) psychModel1.getWrappedData();
		for ( MultipleChoice choice : psych1 ) {
			if (choice.getAnswer()!=null && choice.getAnswer() >= 0) {
				count--;
				bumpScale( choice.getScale(), choice.getAnswer());
			} else {
				FacesContext.getCurrentInstance().addMessage( 
						getElementLabel() + ":" + choice.getId(), 
						new FacesMessage("Question " + choice.getId() + " not answered"));
			}
		}
		ArrayList<MultipleChoice> psych2 = (ArrayList<MultipleChoice>) psychModel2.getWrappedData();
		for ( MultipleChoice choice : psych2 ) {
			if (choice.getAnswer()!=null && choice.getAnswer() >= 0) {
				count--;
				bumpScale( choice.getScale(), choice.getAnswer());
			} else {
				FacesContext.getCurrentInstance().addMessage( 
						getElementLabel() + ":" + choice.getId(), 
						new FacesMessage("Question " + choice.getId() + " not answered"));
			}
		}
		return count;
	}
	/**
	 * [temporary code]
	 * Save psych data
	 */
	public void savePsych() throws Exception {
		if (psychModel1==null) return;	// Nothing to do.
		// Score the test if possible
		psychCountLeft = scorePsych( );
		if (psychCountLeft>0) {
			FacesContext.getCurrentInstance().addMessage( null, 
					new FacesMessage(psychCountLeft + " questions to complete"));
		}
		// 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(psychModel1.getWrappedData());
		oos.writeObject(psychModel2.getWrappedData());
		oos.writeObject(psychScaleModel.getWrappedData());
		oos.writeObject(new Integer(psychCountLeft));
		baos.flush();
//		TolvenLogger.info( "[SavePsych] baos size=" + baos.size(), TRIMAction.class);
//		ed.setValue(baos.toByteArray());
		trim.setFormData(baos.toByteArray());
		oos.close();
		baos.close();
	}
	/**
	 * [Temporary code]
	 * If the psych data isn't in the act, create it now.
	 * @throws Exception
	 */
	public void initPsych() throws Exception {
		if (psychModel1!=null) return;	// Already done
		// See if something in act already
		Act act = (Act) getTrim().getAct();
		psychModel1 = new ListDataModel();
		psychModel2 = new ListDataModel();
		psychScaleModel = new ListDataModel();
		if (act.getText()==null) {
			psychModel1.setWrappedData(newPsych1());
			psychModel2.setWrappedData(newPsych2());
			psychScaleModel.setWrappedData(newScales());
			psychCountLeft = 32;
			return;
		}
		// Get psych data from trim
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(getTrim().getFormData()));
		ArrayList<MultipleChoice> psych1 = (ArrayList<MultipleChoice>) ois.readObject();
		psychModel1.setWrappedData(psych1);
		ArrayList<MultipleChoice> psych2 = (ArrayList<MultipleChoice>) ois.readObject();
		psychModel2.setWrappedData(psych2);
		ArrayList<Scale> psychScale = (ArrayList<Scale>) ois.readObject();
		psychScaleModel.setWrappedData(psychScale);
		psychCountLeft = (Integer) ois.readObject();
		ois.close();
	}

	/**
	 * Sort and return the full list of answers
	 * @return
	 * @throws Exception
	 */
	public ArrayList<MultipleChoice> getPsychAnswers() throws Exception {
		initPsych();
		ArrayList<MultipleChoice> psych = new ArrayList<MultipleChoice>(32);
		psych.addAll((ArrayList<MultipleChoice>) getPsychModel1().getWrappedData());
		psych.addAll((ArrayList<MultipleChoice>) getPsychModel2().getWrappedData());
		return psych;
	}

	public DataModel getPsychModel1() throws Exception {
		initPsych();
		return psychModel1;
	}

	public DataModel getPsychModel2() throws Exception {
		initPsych();
		return psychModel2;
	}

	public DataModel getPsychScaleModel() throws Exception {
		initPsych();
		return psychScaleModel;
	}

	public int getPsychCountLeft() throws Exception {
		initPsych();
		return psychCountLeft;
	}

	public Map<String, Object> getFieldMap() throws Exception {
		return getTrim().getField();
	}
	
	public static class KVsort implements Comparator<Field> {


		public int compare(Field o1, Field o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	/**
	 * Return the field map as a list suitable for iteration and display
	 * @return
	 * @throws Exception
	 */
	public List<Field> getFieldList() throws Exception {
		// Allocate a new list
		List<Field> list = getTrim().getFields();
		// Sort the list
		Collections.sort( list, new KVsort());
		return list;
	}
	
	/**
	 * The first time we're asked for the map of value sets, populate it from the current trim object
	 * @return
	 * @throws Exception 
	 */
	public Map<String, List<SelectItem>> getValueSets() throws Exception {
		if (valueSets==null) {
			valueSets = new HashMap<String, List<SelectItem>>();
			for (ValueSet vs : getTrim().getValueSets()) {
				List<SelectItem> items = new ArrayList<SelectItem>();
				for ( DataType vsItem : getTrimBean().findValueSetContents(this.getAccountId(), vs)) {
					String displayName;
//					Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
//					displayName = DataTypeEx.getLocaleLabel(vsItem, locale);
					if (vsItem.getLabel()!=null) {
						displayName = vsItem.getLabel().getValue();
					} else {
						displayName = null;
					}
					if (displayName == null) {
						if (vsItem instanceof CV) {
							displayName = ((CV)vsItem).getDisplayName();
							if (displayName==null) displayName =  ((CV)vsItem).getCode();
						}
					}
					if (displayName == null && vsItem instanceof NullFlavor ) {
						displayName = ((NullFlavor)vsItem).getType().toString();
					}
					items.add( new SelectItem( vsItem, displayName));
				}
				valueSets.put(vs.getName(), items);
			}
		}
		return valueSets;
	}

	public void setValueSets(Map<String, List<SelectItem>> valueSets) {
		this.valueSets = valueSets;
	}

	public String getReference() throws Exception {
        return StringEscapeUtils.escapeXml(getTrim().getReference());
    }
	public List<HashMap<String, String>> getHistory() throws ParseException, Exception {
		if(history == null){
			history = new ArrayList<HashMap<String,String>>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssZ");
			SimpleDateFormat sdfOut = new SimpleDateFormat(
					"MM/dd/yyyy hh:mm:ss");
			history = new ArrayList<HashMap<String, String>>();
			for (TolvenId item : getTrim().getTolvenEventIds()) {
				HashMap<String, String> tempHistory = new HashMap<String, String>();
				tempHistory.put("user", item.getPrincipal());
				tempHistory.put("timestamp", sdfOut.format(sdf.parse(item.getTimestamp())));
				tempHistory.put("status", item.getStatus() != null ? item.getStatus().toUpperCase():"NEW");
				history.add(tempHistory);
			}
		}
		return history;
	}

	public void setHistory(List<HashMap<String, String>> history) {
		this.history = history;
	}
}
