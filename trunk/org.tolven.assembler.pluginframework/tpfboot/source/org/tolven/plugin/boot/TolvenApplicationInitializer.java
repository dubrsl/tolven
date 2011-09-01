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
 * @author Joseph Isaac
 * @version $Id$
 */
package org.tolven.plugin.boot;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.java.plugin.boot.DefaultApplicationInitializer;
import org.java.plugin.registry.IntegrityCheckReport;

/**
 * Initialize the runtime by setting up access to the extended boot.properties.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenApplicationInitializer extends DefaultApplicationInitializer {

    private Logger logger = Logger.getLogger(TolvenApplicationInitializer.class);
    
    protected String integrityCheckReport2str(IntegrityCheckReport report) {
        StringBuilder buf = new StringBuilder();
        BasicConfigurator.configure();
        for (IntegrityCheckReport.ReportItem item : report.getItems()) {
            if (!"NO_ERROR".equals(item.getCode().toString())) {
                StringBuffer buff = new StringBuffer();
                buff.append("code=");
                buff.append(item.getCode());
                buff.append("; ");
                buff.append("message=");
                buff.append(item.getMessage());
                buff.append("; ");
                buff.append("source=");
                buff.append(item.getSource());
                logger.error(buff.toString());
            }
        }
        logger.info("Integrity check done. Errors: " + report.countErrors() + ". Warnings: " + report.countWarnings() + ".");
        return buf.toString();
    }

}
