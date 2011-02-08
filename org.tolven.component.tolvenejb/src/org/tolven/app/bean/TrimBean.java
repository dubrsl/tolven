package org.tolven.app.bean;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.tolven.app.ActivateNewTrimHeadersMessage;
import org.tolven.app.AdminAppQueueLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimHandler;
import org.tolven.app.TrimLocal;
import org.tolven.app.TrimRemote;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuLocator;
import org.tolven.app.entity.TrimHeader;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.XMLLocal;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.BindTo;
import org.tolven.trim.DataType;
import org.tolven.trim.SearchPhrase;
import org.tolven.trim.Trim;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.ValueSetEx;
import org.tolven.trim.scan.ExtensionScanner;
import org.tolven.trim.scan.IncludeScanner;


@Local(TrimLocal.class)
@Remote(TrimRemote.class)
@Stateless
public class TrimBean implements TrimLocal, TrimRemote {
	@PersistenceContext private EntityManager em;
	@EJB private XMLLocal xmlBean;
	@EJB private AccountDAOLocal accountBean;
	@EJB private ActivationLocal activationBean;
	@EJB private MenuLocal menuBean;
	@Resource EJBContext ctx;
	
    @EJB
    private AdminAppQueueLocal adminAppQueueBean;
	
	private TrimExpressionEvaluator ee = null;

	private static final String TRIMns = "urn:tolven-org:trim:4.0";

	private Logger logger = Logger.getLogger(this.getClass());
	
    /*
     * A set of methods to call when creating or modifying a trim.
     */
    private Set<String> handlers;
    
    public final static String HANDLER_NAME = "handlerJNDI";
    
    /**
     * The most trims to activate at any one time
     */
    public static final int ACTIVATE_LIMIT = 500;
    
	public void initializeTrimHandlers() {
    	try {
			if (handlers==null) {
				Properties properties = new Properties();
				String propertyFileName = this.getClass().getSimpleName()+".properties"; 
				InputStream is = this.getClass().getResourceAsStream(propertyFileName);
				String handlerNames = null;
				if (is!=null) {
					properties.load(is);
					handlerNames = properties.getProperty(HANDLER_NAME);
					is.close();
				}
				handlers = new HashSet<String>();
				if (handlerNames!=null && handlerNames.length()>0) {
					String names[] = handlerNames.split(",");
					// Ignore duplicates
                    for (String name : names) {
                        if (name.startsWith("java:comp/env/")) {
                            handlers.add(name);
                        } else {
                            handlers.add("java:comp/env/" + name);
                        }
                    }
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error initializing MenuData Persisters", e);
		}
		
	}
	
	/**
	 * Parse the supplied XML into an Object tree, starting with Trim at the top.
	 * The resulting trim will also be expanded if it has an extends element.
	 * <p>Note: This method checks for cycles on the extends element and throws a runtime exception if a cycle is detected</p>
	 * @param is InputStream containing menu.xml
	 * @return The resulting Trim object (and its child nodes)
	 * @throws Exception
	 */
	public TrimEx parseTrim( Reader input, TrimExpressionEvaluator ee ) throws JAXBException {
		TrimEx trim = (TrimEx) xmlBean.unmarshal(TRIMns, input);
		String extend = trim.getExtends();
		if (extend!=null) {
			TrimHeader trimHeader = this.findTrimHeader(extend);
			Trim baseTrim = parseTrim(trimHeader.getTrim(), ee);
			ExtensionScanner scanner = new ExtensionScanner();
			scanner.setTrim( baseTrim );
			scanner.setTargetTrim( trim );
			scanner.scan();
		}
		// Resolve includes
		IncludeScanner scanner = new IncludeScanner();
		scanner.setTrim( trim );
		scanner.setEvaluator(ee);
		scanner.setTrimBean(this);
		scanner.scan( );
    	try {
    		initializeTrimHandlers();
			// See if anyone needs to adjust the trim after it has been parsed and before it is persisted.
	    	InitialContext ctx = new InitialContext();
	    	for (String handler : handlers ) {
	        	try {
	    		Object po = ctx.lookup(handler);
	    		TrimHandler trimHandler = (TrimHandler) po;
	    		// If this is for real, call instantiate handler, otherwise, loader
	    		if (ee!=null) {
	    			trimHandler.instantiate(trim, ee);
	    		} else {
	    			trimHandler.load(trim);
	    		}
	    		} catch (Exception e) {
	    			throw new RuntimeException( "Exception dispatching to Trim handler (" + handler + ")", e);
	    		}
	    	}
		} catch (Exception e) {
			throw new RuntimeException( "Exception in Trim instantiate handler", e);
		}
		return trim;
	}
	
	/**
	 * Evaluate supplied trim substituting Expression language as needed then marshal the
	 * resulting string the an object graph.
	 */
	public TrimEx parseTrim( String trimXML, TrimExpressionEvaluator ee ) throws JAXBException {
		String evaluatedTrimXML;
		if (ee!=null) {
			evaluatedTrimXML = (String) ee.evaluate(trimXML);
		} else {
			evaluatedTrimXML = trimXML;
		}
		StringReader is = new StringReader( evaluatedTrimXML );
		return parseTrim( is, ee );
	}

	public TrimEx parseTrim( byte trimXML[], TrimExpressionEvaluator ee ) throws JAXBException {
		return parseTrim( new String( trimXML), ee);
	}

	public void loadContext( Map <String, Object> map, String context ) {
		// load up each context item.
		MenuPath path = new MenuPath( context );
		long keys[] = path.getNodeKeys();
		for (int n = 0; n < keys.length; n++) {
			if (keys[n]!=0) {
				MenuData md = menuBean.findMenuDataItem(keys[n]);
				map.put( path.getNode(n), md);
			}
		}
	}
	
	/**
	 * In addition to marshaling the TRIM XML to an Object graph, this method also
	 * evaluates any embedded EL and processes includes. The following variables are
	 * available to expressions embedded in the trim document:
	 * <ul>
	 * <li>now - contains the transaction time</li>
	 * <li>account - the account object processing the trim</li>
	 * <li>knownType - the name of the accountType, eg echr</li>
	 * <li>user - The TolvenUser object</li>
	 * <li>accountUser - The accountUser object</li>
	 * <li>Any objects mentioned in the context supplied. For example, if the context is: echr:patient-123:encounter-456
	 * then a patient variable and an encounter variable are also available to the context.</li>
	 * <li>Any registered functions will also be available.</li>
	 * </ul>
	 * @param trimXML
	 * @param accountUser
	 * @param context
	 * @param now
	 * @return Trim The parsed object graph
	 * @throws JAXBException
	 */
	public TrimEx parseTrim( byte trimXML[], AccountUser accountUser, String context, Date now, String source ) throws JAXBException {
		Map <String, Object> map = new HashMap<String, Object>();
		// Load up variables for the evaluator to use
		map.put("now", now);
		map.put("account", accountUser.getAccount());
		map.put("knownType", accountUser.getAccount().getAccountType().getKnownType());
		map.put("user", accountUser.getUser());
		map.put("accountUser", accountUser);
		loadContext( map, context );
		if (source!=null) {
			MenuData md = menuBean.findMenuDataItem(accountUser.getAccount().getId(), source);
			/*String attributeName = */ md.getMenuStructure().getNode();
			map.put("source", md);
		}
		return evaluateAndParseTrim( trimXML, map );
	}
public static final String ACCOUNT_ID = "account.id";	
public static final String USER_ID = "user.id";	
public static final String PLACEHOLDER_ID = "placeholder.id";	
	/**
	 * Given a TRIM XML string, evaluate embedded expression language against the supplied map of variables and then parse and return the
	 * Trim object structure.
	 * @param trimXML
	 * @param variables
	 * @return
	 * @throws JAXBException
	 */
	public TrimEx evaluateAndParseTrim( byte trim[], Map<String, Object> variables ) throws JAXBException {
		
		if (ee==null ) {
			ee = new TrimExpressionEvaluator();
		} else {
			ee.clearVariables( );
		}
		ee.addVariables(variables);
		Object account_id = variables.get(ACCOUNT_ID);
		Account account = null;
		if (account_id!=null ) {
			if (!(account_id instanceof Long)) throw new RuntimeException( "account.id must be Long" );
			account = accountBean.findAccount((Long)account_id);
			ee.addVariable("account", account);
		}
		Object user_id = variables.get(USER_ID);
		TolvenUser user = null;
		if (user_id!=null ) {
			if (!(user_id instanceof Long)) throw new RuntimeException( "user.id must be Long" );
			user = activationBean.findTolvenUser((Long)user_id);
			ee.addVariable("user", user);
		}
		// Find placeholder(s) - they start with PLACEHOLDER_ID but may also have a :name
		for (Map.Entry<String, Object> entry : variables.entrySet()) {
			if (entry.getKey().startsWith(PLACEHOLDER_ID)) {
				if (!(entry.getValue() instanceof Long)) throw new RuntimeException( "placeholder.id must be Long" );
				Long id = (Long)entry.getValue();
				MenuData md = menuBean.findMenuDataItem(id);
				if (md == null) {
					throw new RuntimeException( "Placeholder not found for id: " + id);
				}
				if (md.getAccount() != account) {
					throw new RuntimeException( "Placeholder id " + id + " not in account: "  + account_id );
				}
				String node = md.getMenuStructure().getNode();
				if (entry.getKey().length() > PLACEHOLDER_ID.length()) {
					node = entry.getKey().substring(PLACEHOLDER_ID.length()+1);
				}
				ee.addVariable(node, md);
			}
		}
//		String expandedTrim = (String) ee.evaluate(new String( trim ));
 		return parseTrim( new String( trim ), ee );
	}
	
	/**
	 * Find an account given the accountId.
	 * @param accountId
	 * @return
	 */
	public Account findAccount( long accountId ) {
		return accountBean.findAccount(accountId);
	}
	
	/**
	 * Given a TRIM XML string, evaluate embedded expression language against the supplied map of variables and then parse and return the
	 * Trim object structure.
	 * @param trimXML
	 * @param variables
	 * @return
	 * @throws JAXBException
	 */
	public TrimEx evaluateAndParseTrim( String trimName, Map<String, Object> variables ) throws JAXBException {
		TrimHeader trimHeader = findTrimHeader( trimName );
		return evaluateAndParseTrim( trimHeader.getTrim(), variables );
	}

	public long persistTrimHeader( TrimHeader trimHeader ) {
		Date updateTime = new Date(System.currentTimeMillis());
		trimHeader.setLastUpdated( updateTime );
		em.persist(trimHeader);
		return trimHeader.getId();
	}
	
	/**
	 * Find the specified TrimHeader and throw an exception if it is not found.
	 * @param name
	 * @return
	 */
	public List<TrimHeader> findTrimHeaders( String name ) {
		Query query = em.createQuery("SELECT t FROM TrimHeader t WHERE t.name = :name");
		query.setParameter("name", name );
		List<TrimHeader> items = query.getResultList();
		return items;
    }

	public List<TrimHeader> findBrowsableTrimHeaders( ) {
		Query query = em.createQuery("SELECT t FROM TrimHeader t where t.status = :status AND " +
				" t.autogenerated=:auto");
		query.setParameter("status", "active");
		query.setParameter("auto", false);
		List<TrimHeader> items = query.getResultList();
		return items;
    }
	
    /**
     * Return all active trim headers
     * @return
     */
	public List<TrimHeader> findActiveTrimHeaders( ) {
		Query query = em.createQuery("SELECT t FROM TrimHeader t where t.status = :status");
		query.setParameter("status", "active");
		List<TrimHeader> items = query.getResultList();
		return items;
    }
	
	public Collection<String> findActiveTrimHeaderNames( ) {
		Query query = em.createQuery("SELECT t.name FROM TrimHeader t where t.status = :status");
		query.setParameter("status", "active");
		List<String> items = query.getResultList();
		return items;
    }

	/**
	 * This method should return either zero or one item in the list. Unlike findTrimHeader, it does not
	 * throw an exception if there is no result found. 
	 * @param name
	 * @return
	 */
	public List<TrimHeader> findNewOrActiveTrimHeaders( String name ) {
		Query query = em.createQuery("SELECT t FROM TrimHeader t WHERE t.name = :name and (t.status = :status1 OR t.status = :status2)");
		query.setParameter("name", name );
		query.setParameter("status1", "active");
		query.setParameter("status2", "new");
		List<TrimHeader> items = query.getResultList();
		return items;
    }

	public TrimHeader findTrimHeader( String name ) {
		TrimHeader trimHeader = findOptionalTrimHeader( name);
		if (trimHeader==null) {
			throw new RuntimeException( "Required trim " + name + " not found in Trim Header table");
		}
		return trimHeader;
    }
	
	/**
	 * Same as findTrimHeader but return null if the header is not found.
	 * @param name
	 * @return
	 */
	public TrimHeader findOptionalTrimHeader( String name ) {
		try {
			Query query = em.createQuery("SELECT t FROM TrimHeader t WHERE t.name = :name and t.status = :status");
			query.setParameter("name", name );
			query.setParameter("status", Status.ACTIVE.value());
			return (TrimHeader) query.getSingleResult();
		} catch (NoResultException e ) {
			return null;
		}
    }
	
	/*
	 * Find all active TrimHeaders that were created manually[ ie autogenerated=false]
	 */
	public java.util.List<String> findAllActiveTrimHeaderNames(){
		Query query = em.createQuery("SELECT t from TrimHeader t WHERE t.status = :status AND t.autogenerated = :autogenerated");
		query.setParameter("status", Status.ACTIVE.value());
		query.setParameter("autogenerated", false);
		
		List<TrimHeader> trimHeaders = query.getResultList();
		List<String> trimHeaderNames = new ArrayList<String>();
		for( TrimHeader th : trimHeaders ){
			trimHeaderNames.add( th.getName());
		}
		return trimHeaderNames;
	}
		
	/**
	 * Find a trim regardless of the menu or account
	 * @throws JAXBException 
	 */
	public TrimEx findTrim( String name ) throws JAXBException {
		TrimHeader trimHeader = findTrimHeader(name );
		TrimEx trim = parseTrim( trimHeader.getTrim(), null );
		return trim; 
	}
	
	/*
	 * Get the TrimHeader using the id. 
	 * Helps in retrieving trimheader of a previous version.
	 */
	public String findTrimXML(Long id) throws JAXBException{
		TrimHeader th = em.find(TrimHeader.class, id);
		return new String(th.getTrim());
	}

	/**
	 * Find a trim XML String regardless of the menu or account
	 * @throws JAXBException 
	 */
	public String findTrimXml(String name ) throws JAXBException {
		
		TrimHeader trimHeader = findTrimHeader(name );
		return new String(trimHeader.getTrim());
		
	}
	
	/* 
	 * Find trim xml String for the given trimheader. 
	 * Different from findTrimXml(string). Given trimheader could be of any status 
	 * and any version.
	 */
	public String findTrimXml(TrimHeader trimheader){
		trimheader = em.find(TrimHeader.class, trimheader.getId());
		return new String(trimheader.getTrim());
	}
		
	/**
	 * Get the valueSet from a given Trim
	 * @param accountId
	 * @param bindTo
	 * @param valueSetName
	 * @return
	 * @throws Exception
	 */
	public ValueSet findValueSet(long accountId, BindTo bindTo, String valueSetName ) throws Exception {
		Trim trim = findTrim(bindTo.getInclude());
		for( ValueSet valueSet : trim.getValueSets()) {
			if (valueSet.getName().equals(valueSetName)) return valueSet;
		}
		return null;
	}
	
	/**
	 * Recursive method to iterate a value set and populate a passed-in list of values.
	 * @param accountId
	 * @param valueSet a ValueSet to  
	 * @param values the resulting list of values
	 * @throws Exception 
	 */
	void populateValueSetList(long accountId, ValueSet valueSet, List<DataType> values, Set<String> included ) throws Exception {
		for (Object item : ((ValueSetEx)valueSet).getValues()) {
			if (item instanceof DataType ) {
				values.add((DataType) item);
			} else if (item instanceof BindTo ){
				BindTo bindTo = (BindTo) item;
				ValueSet embeddedValueSet = findValueSet( accountId, bindTo, valueSet.getName());
				if (!included.contains(bindTo.getValueSet())) {
					included.add(bindTo.getValueSet());
					populateValueSetList( accountId, embeddedValueSet, values, included);
				}
			}
		}
	}
	
	/**
	 * Return a list of all contents of a valueSet (except binds which we dereference here).
	 * @param accountId
	 * @param trim
	 * @return exploded values
	 * @throws Exception 
	 */
	public List<DataType> findValueSetContents(long accountId, ValueSet valueSet ) throws Exception {
		List<DataType> values = new ArrayList<DataType>();
		Set<String> included = new HashSet<String>();
		populateValueSetList( accountId, valueSet, values, included);
		return values;
	}
	
	/**
	 * Simple method to extract only certain attributes from a trim XML string.
	 * @param trimXML
	 * @return Trim
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	private String getTrimName( String trimXML ) throws XMLStreamException, IOException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(trimXML));
		Stack<String> path = new Stack<String>(); 
		String trimName = null;
		while ( reader.hasNext()) {
			reader.next();
			if (reader.getEventType()==XMLStreamReader.END_ELEMENT) {
				path.pop();
				continue;
			}
			if (reader.getEventType()!=XMLStreamReader.START_ELEMENT) continue;
			String prefix;
			if (path.size() > 0) {
				prefix = path.peek()+".";
			} else {
				prefix = "";
			}
			path.push( prefix + reader.getName().getLocalPart());
			if ("trim.name".equals(path.peek())) { 
				trimName = reader.getElementText();
				path.pop();
				break;
//				continue;
			} 
		}
		reader.close();
		return trimName;
	}

	/**
	 * Add a TrimHeader to the specified menu
	 * @param account Typically a templateAccount specified by the AccountType but can be an individual account 
	 * @param trim Trim object structure
	 * @param menuPath Example: echr:observationMenu
	 */
	public MenuData addTrimHeaderToMenu( Trim trim, TrimHeader trimHeader, String menuPath) {
		try {
			// The menu must exist as a menuStructure and be identified by MenuLocator (a global menu)
			MenuLocator ml = menuBean.findMenuLocator(menuPath);
			AccountMenuStructure ams = ml.getMenuStructure();
			// Remove duplicates, if any.
			MenuData md = removeTrimMenu( trimHeader, ams );
			if (md==null) {
				md = new MenuData();
				md.setMenuStructure(ams);
				md.setAccount(ams.getAccount());
				md.setTrimHeader(trimHeader);
				if (trimHeader.getAutogenerated()==false) {
					logger.info("Add " + ams + " for trim " + trimHeader.getName());
				}
			} else {
				if (trimHeader.getAutogenerated()==false) {
					logger.info("Update " + md + " for trim " + trimHeader.getName());
				}
			}
			Map<String, Object> sourceMap = new HashMap<String, Object>(10);
			sourceMap.put("trim", trim);
			//////////////  Populate menuData here
			menuBean.populateMenuData(sourceMap, md);
			em.persist(md);
			md.initPath();
			// A little legacy code, just in case name and description are not covered by application.xml (usually global.application.xml).
			if (md.getString01()==null) {
				md.setString01(trim.getDescription());
			}
			if (md.getString02()==null) {
				md.setString02(trim.getName());
			}
			// Allow search by name, again, in case not covered in application.xml
			md.addPhrase(md.getString01(), "title", "en_US");
			for (SearchPhrase searchPhrase : trim.getSearchPhrases()) {
				md.addPhrase(searchPhrase.getValue(), searchPhrase.getType(), "en_US");
			}
			return md;
		} catch (RuntimeException e) {
			throw new RuntimeException( "Error adding trim " + trim.getName() + " to menuPath " + menuPath, e);
		}
	}
	/**
	 * Find and remove duplicate menu data referencing a trimHEader. Return at most one
	 * menu data item, if found. 
	 */
	public MenuData removeTrimMenu(TrimHeader trimHeader, AccountMenuStructure ams) {
		// Remove any menu data entries previously associated with this trim header
		Query delQuery = em.createQuery("SELECT md FROM MenuData md JOIN md.trimHeader th " +
				"WHERE th.name = :trimName " +
				"AND md.menuStructure = :ams " +
				"AND (md.deleted is null OR md.deleted = false)");
		delQuery.setParameter("trimName", trimHeader.getName());
		delQuery.setParameter("ams", ams);
		List<MenuData> mdObsolete = delQuery.getResultList();
		if (mdObsolete.size() == 0) {
			return null;
		}
		MenuData result = null;
		// Delete all but the last one we found
		for ( int x = mdObsolete.size()-1; x >= 0 ; x--) {
			TrimHeader th = mdObsolete.get(x).getTrimHeader();
			if ( result!=null || th.getStatus().equals("obsolete")) {
				mdObsolete.get(x).setDeleted(true);
			} else {
				result = mdObsolete.get(x);
			}
			// Mark as deleted
		}
		// Return the last one we found
		return result;
	}
	
	/**
	 * Using a trim that already exists in TrimHeader, add it to menus according to its instructions (per the application element).
	 * @param trimName
	 * @throws JAXBException 
	 */
	public void addTrimToMenus( TrimHeader trimHeader ) throws JAXBException {
		// Load trim considering includes and extends but ignore substitution
		Trim trim = parseTrim( trimHeader.getTrim(), null );
		if (trim.isAbstract()==null || !trim.isAbstract()) {
			// Create menuData for this trim
			for (String menuPath : trim.getMenus()) {
				/*MenuData md = */ addTrimHeaderToMenu( trim, trimHeader, menuPath);
			}
		}
	}
	
	public List<TrimHeader> findNewTrimHeaders(int limit) {
		Query query = em.createQuery("SELECT t FROM TrimHeader t where upper(t.status) = :status");
		query.setParameter("status", "NEW");
		query.setMaxResults(limit);
		List<TrimHeader> items = query.getResultList();
		return items;
	}
	class TrimHeaderCompare implements Comparator<TrimHeader> {

		public int compare(TrimHeader o1, TrimHeader o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
	
	protected void obsoletePreviousTrimHeaders( TrimHeader trimHeader) {
		Query query1 = em.createQuery("UPDATE TrimHeader SET status = 'obsolete' " +
			"WHERE  name = :name " +
			"AND status = 'active'");
		query1.setParameter("name", trimHeader.getName());
		int results = query1.executeUpdate();
	}

	/**
	 * Any new TrimHeaders (completely new or just a new version) are parsed and added to appropriate menus.
	 * @return true if there's more work to be done
	 */
	public boolean activateNewTrimHeaders( ) {
		logger.info("Activating Trim Headers in the background ...");
			// Anything that is currently active that has a new version now needs to be removed from MenuData
//			Query query1 = em.createQuery("UPDATE MenuData SET deleted = true " +
//					"WHERE (deleted IS NULL OR deleted = false) AND trimHeader.id IN " + 
//						"(SELECT h1.id FROM TrimHeader h1, TrimHeader h2 " +
//						"WHERE h1.status='active' AND h2.status='new' AND h1.name = h2.name )");
//			int results = query1.executeUpdate();
//			if (results > 0 ) {
//				logger.info("Delete menuData for " + results + " changed TrimHeaders ");
//				return true;
//			}

			// Next, get the new headers and activate each one
			// and setup the menus
			List<TrimHeader> trimHeaders = findNewTrimHeaders(ACTIVATE_LIMIT);
			
//			// All new trim headers now need to be marked active.
//			Query query3 = em.createQuery("UPDATE TrimHeader SET status ='active' WHERE status='new'");
//			results = query3.executeUpdate();

			Collections.sort(trimHeaders, new TrimHeaderCompare());
			// All new trim headers we are about to process need to be marked active (this is because one trim might depend on
			// another trim not-yet processed. So this is the easiest way to handle "forward references"
			for (TrimHeader trimHeader : trimHeaders) {
				obsoletePreviousTrimHeaders( trimHeader );
				trimHeader.setStatus(Status.ACTIVE.value());
			}
			em.flush();
			// Now build the menus
			for (TrimHeader trimHeader : trimHeaders) {
				try {
					addTrimToMenus( trimHeader );
				} catch (JAXBException e) {
					throw new RuntimeException( "Error activating Trim " + trimHeader.getName(), e);
				}
			}
			logger.info("Update menus for batch of  " + trimHeaders.size() + " TrimHeaders");
			if (trimHeaders.size() >= ACTIVATE_LIMIT) {
				return true;
			}
			return false;
	}

	public void queueActivateNewTrimHeaders( ) throws JMSException {
		ActivateNewTrimHeadersMessage msg = new ActivateNewTrimHeadersMessage();
		adminAppQueueBean.send(msg);
	}
	
	/**
	 * Create a batch of trim headers
	 * @param trimXMLs An array of Trims, as XML strings
	 * @param user
	 * @param comment A comment about his change (for history)
	 * @param autogenerated If true, indicates that this trim entry is uploaded from an automated source
	 */
	public void createTrimHeaders( String[] trimXMLs, String user, String comment, boolean autogenerated ) {
		for (String trimXML : trimXMLs) {
			createTrimHeader( trimXML, user, comment, autogenerated);
		}
	}
	
	/**
	 * Create a TrimHeader.
	 * This new style of loading trims does not create MenuData, which will be done later.
	 * It makes no change if the XML has not changed (using a straight string compare).
	 * @param trimXML The XML representation of the trim as a string
	 * @param user
	 * @param comment A comment about his change (for history)
	 * @param autogenerated If true, indicates that this trim entry is uploaded from an automated source
	 * @return The internal name of the trim as extracted from the &lt;name&gt; element.
	 */
	public String createTrimHeader( String trimXML, String user, String comment, boolean autogenerated ) {
		String name;
		try {
			name = getTrimName( trimXML );
		} catch (Exception e) {
			throw new RuntimeException( "Unable to parse XML", e);
		}
		Date updateTime = new Date();
		List<TrimHeader> trimHeaders = findNewOrActiveTrimHeaders(name);
		// This will normally loop zero or one times since it would be an error for two or more headers with the same name to be active at the same time.
		TrimHeader prevTrimHeader = null;
		TrimHeader matchedTrimHeader = null;
		for (TrimHeader trimHeader : trimHeaders) {
			if (trimHeader.getStatus().equals(Status.ACTIVE.value())) {
				prevTrimHeader = trimHeader;
			}
			// If no change, we're done
			if (new String(trimHeader.getTrim()).equals(trimXML)) {
//				logger.info("TrimHeader unchanged: " + name);
				matchedTrimHeader = trimHeader;
				break;
			}
		}
		// If any extra new trimHeaders, remove them. Only need one new
		for (TrimHeader trimHeader : trimHeaders) {
			if (trimHeader.getStatus().equals(Status.NEW.value()) && trimHeader!=matchedTrimHeader) {
				trimHeader.setStatus(Status.OBSOLETE.value());
				continue;
			}
		}
		// If we had a match, mark is as new so we review it and we're done
		if (matchedTrimHeader!=null) {
			matchedTrimHeader.setStatus(Status.NEW.value());
			return name;
		}
//		logger.info("New TrimHeader: " + name);
		TrimHeader trimHeader = new TrimHeader();
		trimHeader.setName(name);
		trimHeader.setStatus(Status.NEW.value());
		trimHeader.setTrim(trimXML.getBytes());
		trimHeader.setComment(comment);
		trimHeader.setAutogenerated(autogenerated);
		trimHeader.setLastUpdated( updateTime );
		Integer version;
		if (prevTrimHeader!=null) {
			Integer prevVersion;
			if (prevTrimHeader.getVersion()!=null) {
				prevVersion = prevTrimHeader.getVersion();
			} else {
				prevVersion = 1;
			}
			version = prevVersion + 1;
		} else {
			version = 1;
		}
		trimHeader.setVersion(version);
		persistTrimHeader( trimHeader );
		return name;
	}
	
	/*
	 * Find all versions of the trim header by name(active and inactive).
	 */
	public java.util.List<TrimHeader> findTrimHeaderVersions(String trimHeaderName){
		Query query = em.createQuery("SELECT t FROM TrimHeader t WHERE t.name = :name");
		query.setParameter("name", trimHeaderName );
		List<TrimHeader> items = query.getResultList();
		return items;
	}
	
	/*
	 * Add comment to a trim header
	 */
	public void setComment(TrimHeader trimheader, String comment){
		TrimHeader th = em.find(TrimHeader.class, trimheader.getId());
		if(th != null){
			th.setComment( comment);
		}
	}
	/**
	 * Create a menu-data item based on a trim.
	 * @param msId
	 * @param trimHeaderId
	 * @param code
	 * @param description
	 */
	public void shortTrim( long msId, long trimHeaderId, String code, String description ) {
		// Create a new MenuData item pointing to this template
		MenuData md = new MenuData( );
		AccountMenuStructure ms = em.find(AccountMenuStructure.class, msId);
		md.setMenuStructure(ms);
		md.setAccount(ms.getAccount());
		TrimHeader trimHeader = em.find(TrimHeader.class, trimHeaderId);
		md.setTrimHeader(trimHeader);
		md.setString01(description);
		md.setString02(code);
		// TODO: Should get language 
		md.addPhrase(description, "title", "en_US");
//		logger.info( "Adding phrase " + act.getTitle().getST().getValue());
		em.persist(md);
		md.initPath();
	}
	
	private static String EXTENSION = ".trim.xml";
	
	static class TrimFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.endsWith(EXTENSION)) return true;
			return false;
		}
    }
	
	public ActRelationship findActRelationship( Act act, String[] nodes, int index ) {
		if (act==null) return null;
		if (index > nodes.length) return null;
		String node[] = nodes[index].split("\\,");
		int nodeIndex = Integer.parseInt(node[1]);
		int nodeMatchCount = 0;
		for (ActRelationship ar : act.getRelationships()) {
			if (node[0].equals(ar.getName())) {
				if (nodeIndex==nodeMatchCount) {
					if ((index+1)==nodes.length) return ar;
					return findActRelationship( ar.getAct(), nodes, index+1);
				}
				nodeMatchCount++;
			}
		}
		return null;
	}

	/**
	 * Find an ActRelationship node using a path specification, for example:
	 * nextStep,0;nextStep,0
	 * means find the first occurrence of nextStep in the top-level act, and then
	 * find the first occurrence of nextStep in the act found in the first step.
	 * @param trim
	 * @param path
	 * @return
	 */
	public ActRelationship findActRelationship( Trim trim, String path ) {
		String nodes[] = path.split("\\;");
		return findActRelationship(trim.getAct(), nodes, 0);
	}

}
