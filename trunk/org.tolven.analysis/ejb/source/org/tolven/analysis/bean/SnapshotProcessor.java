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
package org.tolven.analysis.bean;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.drools.StatefulSession;
import org.tolven.analysis.AnalysisEvent;
import org.tolven.analysis.SnapshotLocal;
import org.tolven.analysis.SnapshotProcessorLocal;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.MenuLocal;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.doc.TolvenMessageSchedulerLocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocumentType;
import org.tolven.el.ExpressionEvaluator;

@Stateless
@Local(SnapshotProcessorLocal.class)
public class SnapshotProcessor extends AppEvalAdaptor implements SnapshotProcessorLocal {

    public static final String ANALYSIS_NS = "org.tolven.analysis";

    protected @EJB
    MenuLocal menuBean;

    protected @EJB
    SnapshotLocal snapshotBean;

    protected @EJB
    TolvenMessageSchedulerLocal tmSchedulerBean;

    private AnalysisEvent analysisEvent;
    private TrimExpressionEvaluator trimee;

    private Logger logger = Logger.getLogger(SnapshotProcessor.class);

    public AnalysisEvent getAnalysisEvent() {
        return analysisEvent;
    }

    public void setAnalysisEvent(AnalysisEvent analysisEvent) {
        this.analysisEvent = analysisEvent;
    }

    @Override
    protected ExpressionEvaluator getExpressionEvaluator() {
        if (trimee == null) {
            trimee = new TrimExpressionEvaluator();
            trimee.addVariable("now", getNow());
            trimee.addVariable("doc", getDocument());
            trimee.addVariable(TrimExpressionEvaluator.ACCOUNT, getAccount());
            trimee.addVariable(DocumentType.DOCUMENT, getDocument());
        }
        return trimee;
    }

    @Override
    protected void loadWorkingMemory(StatefulSession workingMemory) throws Exception {
        workingMemory.insert(getAnalysisEvent());
    }

    @Override
    protected DocBase scanInboundDocument(DocBase doc) throws Exception {
        return doc;
    }

    @Override
    public void process(Object obj, Date now) {
        try {
            if (obj instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) obj;
                TolvenMessage tm = tmSchedulerBean.unwrapTolvenMessage(objectMessage);
                if (tm != null) {
                    if (ANALYSIS_NS.equals(tm.getXmlNS())) {
                        setAnalysisEvent(new AnalysisEvent(objectMessage));
                        this.trimee = null;
                        associateDocument(tm, now);
                        logger.debug("Executing snapshot rules...");
                        runRules();
                        logger.debug("SnapshotProcessor complete");
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Exception in Snapshot processor", ex);
        }
    }

}
