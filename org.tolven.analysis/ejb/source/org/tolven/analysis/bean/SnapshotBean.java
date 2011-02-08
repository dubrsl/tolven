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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.tolven.analysis.AnalysisLocal;
import org.tolven.analysis.CohortSnapshotLocal;
import org.tolven.analysis.SnapshotLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersionMessage;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.bean.TolvenMessage;

@Stateless()
@Local(SnapshotLocal.class)
public class SnapshotBean implements SnapshotLocal {

    @PersistenceContext
    private EntityManager em;

    private @EJB
    MenuLocal menuBean;

    private @EJB
    AnalysisLocal analysisBean;

    private static Properties cohortTypes;
    
    private Logger logger = Logger.getLogger(SnapshotBean.class);

    @Override
    public void snapshotNow(String cohortType, String snapshotType, Account account) {
        Map<String, Object> messageProperties = new HashMap<String, Object>();
        messageProperties.put("command", "snapshot");
        messageProperties.put("cohortType", cohortType);
        messageProperties.put("snapshotType", snapshotType);
        messageProperties.put("snapshotNow", true);
        analysisBean.scheduleAnalysis(messageProperties, null, account);
    }
    
    @Override
    public void scheduleSnapshot(String cohortType, String snapshotType, Account account) {
        Map<String, Object> messageProperties = new HashMap<String, Object>();
        messageProperties.put("command", "snapshot");
        messageProperties.put("cohortType", cohortType);
        messageProperties.put("snapshotType", snapshotType);
        messageProperties.put("schedule", true);
        analysisBean.scheduleAnalysis(messageProperties, null, account);
    }

    @Override
    public Date getNextScheduledDate(String cohortType, String snapshotType, Date now, Account account) {
        TolvenMessage tm = getNextScheduledTolvenMessage(cohortType, snapshotType, now, account);
        if(tm == null) {
            return null;
        } else {
            return tm.getQueueOnDate();
        }
    }

    private TolvenMessage getNextScheduledTolvenMessage(String cohortType, String snapshotType, Date now, Account account) {
        Query query = em.createQuery("SELECT tm FROM TolvenMessage tm WHERE " +
                "tm.accountId = :accountId AND " +
                "tm.xmlNS = :xmlNS AND " +
                "tm.scheduleDate is NOT NULL AND " +
                "tm.queueOnDate is NOT NULL AND " + 
                "tm.queueOnDate > :now AND " +
                "tm.queueDate is NULL AND " +
                "tm.processDate is NULL AND " +
                "(tm.deleted IS NULL OR tm.deleted = false) AND " +
                "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'cohortType' and tmp.string = :cohortType) AND " +
                "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'snapshotType' and tmp.string = :snapshotType) " +
                "order by tm.queueOnDate");
        query.setParameter("now", now);
        query.setParameter("accountId", account.getId());
        query.setParameter("xmlNS", "org.tolven.analysis");
        query.setParameter("cohortType", cohortType);
        query.setParameter("snapshotType", snapshotType);
        query.setMaxResults(1);
        List<TolvenMessage> tms = query.getResultList();
        if(tms.isEmpty()) {
            return null;
        } else {
            return tms.get(0);
        }
    }
    
    @Override
    public void cancelSchedule(String cohortType, String snapshotType, Account account) {
        Map<String, Object> messageProperties = new HashMap<String, Object>();
        messageProperties.put("command", "cancelSchedule");
        messageProperties.put("cohortType", cohortType);
        messageProperties.put("snapshotType", snapshotType);
        analysisBean.scheduleAnalysis(messageProperties, null, account);
    }

    @Override
    public void cancelScheduleNow(String cohortType, String snapshotType, Account account, Date now) {
        Query query = em.createQuery("SELECT tm FROM TolvenMessage tm WHERE " +
                "tm.accountId = :accountId AND " +
                "tm.xmlNS = :xmlNS AND " +
                "tm.scheduleDate is NOT NULL AND " +
                "tm.queueOnDate is NOT NULL AND " + 
                "tm.queueOnDate >= :now AND " +
                "tm.queueDate IS NULL AND " +
                "tm.processDate is NULL AND " +
                "(tm.deleted IS NULL OR tm.deleted = false) AND " +
                "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'cohortType' and tmp.string = :cohortType) AND " +
                "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'snapshotType' and tmp.string = :snapshotType)");
        query.setParameter("now", now);
        query.setParameter("accountId", account.getId());
        query.setParameter("xmlNS", "org.tolven.analysis");
        query.setParameter("cohortType", cohortType);
        query.setParameter("snapshotType", snapshotType);
        List<TolvenMessage> tms = query.getResultList();
        for (TolvenMessage tm : tms) {
            tm.setDeleted(true);
        }
    }

    @Override
    public MenuData createSnapshot(MenuData cohort, Account account) {
        return null;
    }

    @Override
    public MenuData findMenuData(long id, Account account) {
        Query query = em.createQuery("SELECT md FROM MenuData md WHERE " +
                "md.id = :id AND " +
                "md.account.id = :account");
        query.setParameter("id", id);
        query.setParameter("account", account.getId());
        query.setMaxResults(1);
        List<MenuData> results = query.getResultList();
        if(results.size() == 0) {
            return null;
        } else if(results.size() == 1) {
            return results.get(0);
        } else {
            throw new RuntimeException("Found more than one active MenuData with id: " + id + " in account: " + account.getId());
        }
    }

    @Override
    public void addCohortPlaceholderID(String cohortName, MenuData cohort) {
        cohort.addPlaceholderID("org.tolven.cohort", cohortName, "org.tolven.analysis");
    }

    @Override
    public MenuData findCohort(String cohortName, Account account) {
        List<MenuData> cohorts = menuBean.findMenuDataById(account, "org.tolven.cohort", cohortName);
        if(cohorts.size() == 0) {
            return null;
        } else if(cohorts.size() == 1){
            return cohorts.get(0);
        } else {
            throw new RuntimeException("Found more than one cohort with placeholder ID: " + cohortName);
        }
    }

    @Override
    public void addPatientcohortPlaceholderID(String cohortType, MenuData patientcohort) {
        patientcohort.addPlaceholderID("org.tolven.patientcohort", cohortType + "-" + patientcohort.getParent01().getId(), "org.tolven.analysis");
    }

    @Override
    public MenuData findPatientcohort(String cohortType, MenuData patient, Account account) {
        return findPatientcohort(cohortType, patient.getId(), account);
    }

    @Override
    public MenuData findPatientcohort(MenuData patient, MenuData cohort, Account account) {
        return findPatientcohort(cohort.getString02(), patient.getId(), account);
    }

    @Override
    public MenuData findPatientcohort(String cohortType, long patientId, Account account) {
        List<MenuData> patientcohorts = menuBean.findMenuDataById(account, "org.tolven.patientcohort", cohortType + "-" + patientId);
        if(patientcohorts.size() == 0) {
            return null;
        } else if(patientcohorts.size() == 1){
            return patientcohorts.get(0);
        } else {
            throw new RuntimeException("Found more than one patientcohort with placeholder ID: " + cohortType + "-" + patientId);
        }
    }

    @Override
    public List<MenuData> findCohortPatients(MenuData cohort, Account account) {
        return findCohortPatients(cohort.getString02(), account);
    }

    @Override
    public List<MenuData> findCohortPatients(String cohortType, Account account) {
        Query query = em.createQuery("SELECT md FROM MenuData md WHERE " +
                "md.account.id = :account AND " +
                "md.parent02.string02 = :code AND " +
                "md.menuStructure.role = :role AND " +
                "md.menuStructure.path = :path AND " +
                "(md.deleted IS NULL OR md.deleted = false)");
        query.setParameter("account", account.getId());
        query.setParameter("path", "echr:patient:patientcohort");
        query.setParameter("role", "placeholder");
        query.setParameter("code", cohortType);
        List<MenuData> patientcohorts = query.getResultList();
        logger.debug("Found " + patientcohorts.size() + " patientcohorts in " + cohortType + " ");
        return patientcohorts;
    }

    @Override
    public JFreeChart getChart(String cohortType, String snapshotType, String chartType, AccountUser accountUser, Date now) {
        CohortSnapshotLocal cohortSnapshotBean = getCohortBean(cohortType);
        return cohortSnapshotBean.getChart(cohortType, snapshotType, chartType, accountUser, now);
    }

    private CohortSnapshotLocal getCohortBean(String cohortType) {
        try {
            String bean = cohortTypes.getProperty(cohortType);
            if (bean == null) {
                throw new RuntimeException("Could not locate bean for cohort code '" + cohortType);
            } else {
                InitialContext ctx = new InitialContext();
                return (CohortSnapshotLocal) ctx.lookup(bean);
            }
        } catch (NamingException ex) {
            throw new RuntimeException("Could not locate bean for cohort code '" + cohortType + "'", ex);
        }
    }
    
    public void upateMenuDataVersion(String path, Account account, Date now) {
        Map<String, MenuDataVersionMessage> mdvs = new HashMap<String, MenuDataVersionMessage>();
        MenuDataVersionMessage mdv = new MenuDataVersionMessage(path, account.getId());
        mdvs.put(path, mdv);
        menuBean.queueDeferredMDVs(mdvs);
    }

    static {
        String propertyFileName = SnapshotBean.class.getSimpleName() + ".properties";
        try {
            cohortTypes = new Properties();
            InputStream in = SnapshotBean.class.getResourceAsStream(propertyFileName);
            if (in != null) {
                cohortTypes.load(in);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load cohort types from: " + propertyFileName, ex);
        }
    }

}
