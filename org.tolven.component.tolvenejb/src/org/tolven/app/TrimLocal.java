package org.tolven.app;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.TrimHeader;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.DataType;
import org.tolven.trim.Trim;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.TrimEx;
/**
 * Provide parsing and loading services for trims and trimHeaders.
 * @author John Churin
 *
 */
public interface TrimLocal {


	/**
	 * Create a TrimHeader one or more menu-data items referencing a trimHeader.
	 * @param trimXML The XML representation of the string
	 * @param user The tolven user making the change
	 * @param comment A comment about his change (for history)
	 * @param isAutoGenerated If true, indicates that this trim entry is uploaded from an automated source
	 * and there should not be listed in the trimEditor.
	 * @param knownType When specified, the trim is uploaded to this AccountType.
	 * @throws JAXBException 
	 */
//	public void loadTrimXML( String trimXML, String user, String comment, Boolean isAutoGenerated ) throws JAXBException;
	
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
	public String createTrimHeader( String trimXML, String user, String comment, boolean autogenerated );

	/**
	 * Create a batch of trim headers (useful by remote clients)
	 * @param trimXMLs An array of Trims, as XML strings
	 * @param user
	 * @param comment A comment about his change (for history)
	 * @param autogenerated If true, indicates that this trim entry is uploaded from an automated source
	 */
	public void createTrimHeaders( String[] trimXMLs, String user, String comment, boolean autogenerated );

	/**
	 * Any new TrimHeaders (completely new or just a new version) are parsed and added to appropriate menus.
	 * @return true if there's more work to be done
	 */
	public boolean activateNewTrimHeaders( );

	/**
	 * Queue a process to activate new trim headers.
	 * @return true if there's more work to be done
	 */
	public void queueActivateNewTrimHeaders( ) throws JMSException;
	
//	public void loadTrimXML( String trimXML ) throws JAXBException;
	
	/**
	 * Find an account given the accountId.
	 * @param accountId
	 * @return
	 */
	public Account findAccount( long accountId );


	/**
	 * Return a list of all contents of a valueSet (except binds which we dereference here).
	 * @param accountId
	 * @param trim
	 * @return exploded values
	 * @throws Exception 
	 */
	List<DataType> findValueSetContents(long accountId, ValueSet valueSet ) throws Exception;

	/**
	 * Parse the supplied XML into an Object tree, starting with Trim at the top.
	 * @param is InputStream containing menu.xml
	 * @return The resulting Trim object (and its child nodes)
	 * @throws Exception
	 */
	public TrimEx parseTrim( String trimXML, TrimExpressionEvaluator ee ) throws JAXBException;
	
	public TrimEx parseTrim( byte trimXML[], TrimExpressionEvaluator ee ) throws JAXBException;

	/**
	 * Find a trim regardless of the menu or account
	 * @throws JAXBException 
	 */
	public TrimEx findTrim( String name ) throws JAXBException;

	/**
	 * Find a trim XML String regardless of the menu or account
	 * @throws JAXBException 
	 */
	public String findTrimXml(String name ) throws JAXBException;
	/* 
	 * Find trim xml String for the given trimheader. 
	 * Different from findTrimXml(string). Given trimheader could be of any status 
	 * and any version.
	 */
	public String findTrimXml(TrimHeader trimheader);
	
	/**
	 * Find a raw trim header. The trim body is not parsed.
	 * @param name
	 * @return
	 */
	public TrimHeader findTrimHeader( String name );

	/**
	 * Same a findTrimHeader but return null if the header is not found.
	 * @param name
	 * @return
	 */
	public TrimHeader findOptionalTrimHeader( String name );
	
    /**
     * Return all active trim headers
     * @return
     */
	public List<TrimHeader> findActiveTrimHeaders( );
	public List<TrimHeader> findBrowsableTrimHeaders( );
	
	/**
	 * In addition to marshaling the TRIM XML to an Object graph, this method also
	 * evaluates any embedded EL and processes includes. The following variables are
	 * available to expressions embedded in the trim document:
	 * <ul>
	 * <li>now - contains the transaction time</li>
	 * <li>account - the account object processing the trim</li>
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
	public TrimEx parseTrim( byte trimXML[], AccountUser accountUser, String context, Date now, String source ) throws JAXBException;

	/**
	 * Find an ActRelationship node using a path specification, for example:
	 * nextStep,0;nextStep,0
	 * means find the first occurrence of nextStep in the top-level act, and then
	 * find the first occurrence of nextStep in the act found in the first step.
	 * @param trim
	 * @param path
	 * @return
	 */
	public ActRelationship findActRelationship( Trim trim, String path );


	/**
	 * Given a TRIM XML string, evaluate embedded expression language against the supplied map of variables and then parse and return the
	 * Trim object structure.
	 * @param trimXML
	 * @param variables
	 * @return
	 * @throws JAXBException
	 */
	public TrimEx evaluateAndParseTrim( String trimName, Map<String, Object> variables ) throws JAXBException;

}