/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author <your name>
 * @version $Id: ApplicationMetadataRemote.java 2890 2011-08-24 22:39:19Z admin $
 */  

package org.tolven.app;

import java.util.Map;

/**
 * Application metadata is loaded to database. 
 * @author John Churin
 *
 */
public interface ApplicationMetadataRemote {
	/**
	 * Load all supplied application metadata. The files have been read to memory on the client and passed here as
	 * a list of zero or more mapped string with the key being the name of the file.
	 * @param appFiles
	 */
	public void loadApplications( Map<String, String> appFiles );

}
