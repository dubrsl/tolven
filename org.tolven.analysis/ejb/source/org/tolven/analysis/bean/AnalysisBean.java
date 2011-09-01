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
 * @version $Id: AnalysisBean.java 1085 2011-05-24 03:11:08Z srini.kandula $
 */
package org.tolven.analysis.bean;

import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.analysis.AnalysisLocal;
import org.tolven.core.entity.Account;
import org.tolven.doc.TolvenMessageSchedulerLocal;
import org.tolven.doc.bean.TolvenMessage;

@Stateless()
@Local(AnalysisLocal.class)
public class AnalysisBean implements AnalysisLocal {

    private @EJB
    TolvenMessageSchedulerLocal tmSchedulerBean;
    
    @Override
    public void scheduleAnalysis(Map<String, Object> messageProperties, Date queueOnDate, Account account) {
        try {
            TolvenMessage tm = new TolvenMessage();
            tm.setAccountId(account.getId());
            tm.setFromAccountId(account.getId());
            tm.setMediaType("text/xml");
            tm.setXmlNS("org.tolven.analysis");
            tmSchedulerBean.schedule(tm, messageProperties,queueOnDate );
        } catch (Exception ex) {
            throw new RuntimeException("Could not submit snapshot for queue", ex);
        }
    }

}
