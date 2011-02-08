package org.tolven.app;

import java.security.PrivateKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocumentType;
import org.tolven.trim.BindPhase;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TRIMException;
import org.tolven.trim.ex.TrimEx;

/**
 * APIs in this module are used to create new instances of TRIM placeholders.
 * @author John Churin
 *
 */
public interface CreatorLocal {

	/**
	 * Instantiate a new document and new Menudata item pointing to it.
	 * @param accountId
	 * @param ms
	 * @param parent
	 * @return MenuData
	 * @throws JAXBException
	 * @throws TRIMException
	 */
	public MenuData createTRIMPlaceholder( AccountUser accountUser, String trimPath, String context, Date now, String alias ) throws JAXBException, TRIMException;

	public MenuData createTRIMEvent( MenuData mdPrior, AccountUser activeAccountUser, String transitionName, Date now, PrivateKey userPrivateKey ) throws JAXBException, TRIMException;
	public void addToWIP( MenuData mdEvent, TrimEx trim, Date now, Map<String, Object> variables );
	/**
	 * Create an event entry
	 * that represents this transaction in the database. If the trim does not indicate
	 * that an event is needed (specified as instance path in application element), then we return null.
	 * @param account
	 * @param trim
	 * @param mdTrim - The "trimList" menuData item where this trim came from, if any. (This is largely obsolete and normally should be null.) 
	 * @param contextList - A list of context elements which can be used to establish certain placeholders. For example, echr:patient-123:diagnosis is
	 * enough to determine that a new problem trim is to be associated with patient-123.
	 * @param now - Transaction Time
	 * @return New event or null
	 */
	public MenuData establishEvent( Account account, TrimEx trim, Date now, Map<String, Object> variables );

	/**
	 * Perform create scan on this trim looking for unsatisfied placeholder bind requests.
	 * @param account
	 * @param trim
	 * @param mdTrim - The "trimList" menuData item where this trim came from, if any. (This is largely obsolete and normally should be null.) 
	 * @param contextList - A list of context elements which can be used to establish certain placeholders. For example, echr:patient-123:diagnosis is
	 * enough to determine that a new problem trim is to be associated with patient-123.
	 * @param now - Transaction Time
	 * @param bindPhase - Which phase 
	 * @return A list of all placeholders with applicable binding instructions
	 */
	public List<MenuData> placeholderBindScan( Account account, TrimEx trim, MenuData mdTrim, MenuPath context, Date now, BindPhase bindPhase, DocBase doc );
	
	public List<MenuData> placeholderBindScan( AccountUser accountUser, TrimEx trim, MenuData mdTrim, MenuPath context, Date now, BindPhase bindPhase, DocBase doc );
	
	public void computeScan( Trim trim, AccountUser accountUser, MenuPath contextPath, Date now, DocumentType documentType );

	/**
	 * Returns Validation Error Messages if any.
	 * 
	 * @param trim
	 * @param accountUser
	 * @param contextPath
	 * @param now
	 * @param documentType The documentType instance for this document
	 * @return
	 */
	public List<String> computeScanWithResults( Trim trim, AccountUser accountUser, MenuPath contextPath, Date now, DocumentType documentType ) ;

	/**
	 * Submit the document associated with this menuData item
	 * @throws Exception 
	 */
	public void submit(MenuData md, AccountUser activeAccountUser, PrivateKey userPrivateKey) throws Exception;

	/**
	 * Submit the document associated with this menuData item
	 * @throws Exception 
	 */
	public void submitNow(MenuData md, AccountUser activeAccountUser, Date now, PrivateKey userPrivateKey) throws Exception;
	
    /**
     * Submit the document associated with this event to be queued for processing on queueOnDate
     * @throws Exception 
     */
    public void submit(MenuData mdEvent, AccountUser activeAccountUser, Date queueOnDate, PrivateKey userPrivateKey) throws Exception;
    
	/**
	 * Submit from a finished CCR graph but no document
	 * @param accountId
	 * @param userId
	 * @param ccr
	 * @throws Exception
	 */
	public void submit( long accountId, long userId, ContinuityOfCareRecord ccr ) throws Exception;

	/**
	 * Send a copy of an existing document to another account
	 * @throws Exception 
	 */
	public void sendCopyTo( AccountUser accountUser, long documentId, long otherAccountId, PrivateKey userPrivateKey ) throws Exception;

	
	/**
	 * Marshal the specified Trim object graph back to the specified document.
	 * @param trim
	 * @param docXML
	 * @throws JAXBException
	 * @throws TRIMException
	 */
	public void marshalToDocument( Trim trim, DocXML docXML ) throws JAXBException, TRIMException;

}
