package org.tolven.process;

import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimFactory;

/**
 * This class is used for fetching values for progress note wizard
 * @author Suja
 * added on 06/22/2010
 */
public class ProgressNote extends InsertAct {	
	private static final TrimFactory trimFactory = new TrimFactory();
	private ActEx act;
	
	public void compute( ) throws Exception {
		TolvenLogger.info( "Compute enabled=" + isEnabled(), InsertAct.class);
		super.checkProperties();
		
		if (isEnabled()) {
			act = (ActEx)this.getAct();
		}		
	}
	
}
