package org.tolven.app;

import org.tolven.trim.scan.BindContext;

/**
 * An implementation of this interface will be called each time a matching bind node is found
 * in a given trim.
 * @author John Churin
 *
 */
public interface BindProcessor {

	public void bindNode( BindContext ctx );
	
}
