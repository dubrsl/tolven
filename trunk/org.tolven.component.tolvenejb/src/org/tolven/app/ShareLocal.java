package org.tolven.app;

import org.tolven.core.entity.Account;
import org.tolven.trim.Trim;
/**
 * Provide sharing services for inbound and outbound messages.
 * @author John Churin
 *
 */
public interface ShareLocal {

    /**
     * Scan an outbound trim for embedded IDs and create a cross-reference 
     * to that ID if it doesn't already exists.
     * @param trim
     */
    public void outboundScan( Trim trim, Account account );

    /**
     * Scan an inbound trim for embedded IDs and resolve cross-references 
     * @param trim
     * @param account The account receiving the TRIM
     */
    public void inboundScan( Trim trim, Account account );
    
}
