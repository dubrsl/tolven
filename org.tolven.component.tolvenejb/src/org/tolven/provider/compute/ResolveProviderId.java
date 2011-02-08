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
 * @version $Id: ResolveProviderId.java,v 1.1 2009/12/13 06:49:53 jchurin Exp $
 */  

package org.tolven.provider.compute;

import org.tolven.process.ComputeBase;
import org.tolven.trim.ex.TrimEx;

public class ResolveProviderId extends ComputeBase {
	@Override
	public void compute() throws Exception {
		TrimEx trim = (TrimEx)this.getTrim();
		Long providerId;
		providerId = (Long) trim.getField().get("system");
		Boolean selectSpecialty = (Boolean) trim.getField().get("selectSpecialty");
		Boolean selectProvider = (Boolean) trim.getField().get("selectProvider");
		if (selectSpecialty!=null && selectSpecialty) {
			providerId = (Long) trim.getField().get("specialty");
		}
		if (selectProvider!=null && selectProvider) {
			providerId = (Long) trim.getField().get("provider");
		}
		if (providerId==null || providerId==0) {
			buildMessage("invalidProvider");
		} else {
			// Store the resulting providerId
			trim.getField().put("providerId", providerId);
		}
	}

}
