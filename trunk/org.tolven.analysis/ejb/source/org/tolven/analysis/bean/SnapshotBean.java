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
 * @version $Id: SnapshotBean.java 486 2011-03-29 00:51:12Z kanag.kuttiannan $
 */
package org.tolven.analysis.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.CCHITBean;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersionMessage;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
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
	/**
     * Method to create snapshots.
     * Modified to make the include code field mandatory.
     * @author Pinky S
     * Modified on 31/01/11
     * @return
	 */
	public void snapshotNow(String cohortType, String snapshotType,
			Account account) {
		String incCodes = account.getProperty().get(cohortType + ".includeCodes");
		if((incCodes.equals(""))&&
				(!cohortType.equals("org.tolven.cohort.smk")&&
						(!cohortType.equals("org.tolven.cohort.obst")))){
			 FacesContext.getCurrentInstance().addMessage("snapshotForm:snapShotButton", new FacesMessage("Included codes are mandatory to do a snapshot."));
		}
		else{
			Map<String, Object> messageProperties = new HashMap<String, Object>();
			messageProperties.put("command", "snapshot");
			messageProperties.put("cohortType", cohortType);
			messageProperties.put("snapshotType", snapshotType);
			messageProperties.put("snapshotNow", true);
			analysisBean.scheduleAnalysis(messageProperties, null, account);
		}
	}

	@Override
	public void scheduleSnapshot(String cohortType, String snapshotType,
			Account account) {
		Map<String, Object> messageProperties = new HashMap<String, Object>();
		messageProperties.put("command", "snapshot");
		messageProperties.put("cohortType", cohortType);
		messageProperties.put("snapshotType", snapshotType);
		messageProperties.put("schedule", true);
		analysisBean.scheduleAnalysis(messageProperties, null, account);
	}

	@Override
	public Date getNextScheduledDate(String cohortType, String snapshotType,
			Date now, Account account) {
		TolvenMessage tm = getNextScheduledTolvenMessage(cohortType,
				snapshotType, now, account);
		if (tm == null) {
			return null;
		} else {
			return tm.getQueueOnDate();
		}
	}

	private TolvenMessage getNextScheduledTolvenMessage(String cohortType,
			String snapshotType, Date now, Account account) {
		Query query = em
				.createQuery("SELECT tm FROM TolvenMessage tm WHERE "
						+ "tm.accountId = :accountId AND "
						+ "tm.xmlNS = :xmlNS AND "
						+ "tm.scheduleDate is NOT NULL AND "
						+ "tm.queueOnDate is NOT NULL AND "
						+ "tm.queueOnDate > :now AND "
						+ "tm.queueDate is NULL AND "
						+ "tm.processDate is NULL AND "
						+ "(tm.deleted IS NULL OR tm.deleted = false) AND "
						+ "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'cohortType' and tmp.string = :cohortType) AND "
						+ "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'snapshotType' and tmp.string = :snapshotType) "
						+ "order by tm.queueOnDate");
		query.setParameter("now", now);
		query.setParameter("accountId", account.getId());
		query.setParameter("xmlNS", "org.tolven.analysis");
		query.setParameter("cohortType", cohortType);
		query.setParameter("snapshotType", snapshotType);
		query.setMaxResults(1);
		List<TolvenMessage> tms = query.getResultList();
		if (tms.isEmpty()) {
			return null;
		} else {
			return tms.get(0);
		}
	}

	@Override
	public void cancelSchedule(String cohortType, String snapshotType,
			Account account) {
		Map<String, Object> messageProperties = new HashMap<String, Object>();
		messageProperties.put("command", "cancelSchedule");
		messageProperties.put("cohortType", cohortType);
		messageProperties.put("snapshotType", snapshotType);
		analysisBean.scheduleAnalysis(messageProperties, null, account);
	}

	@Override
	public void cancelScheduleNow(String cohortType, String snapshotType,
			Account account, Date now) {
		Query query = em
				.createQuery("SELECT tm FROM TolvenMessage tm WHERE "
						+ "tm.accountId = :accountId AND "
						+ "tm.xmlNS = :xmlNS AND "
						+ "tm.scheduleDate is NOT NULL AND "
						+ "tm.queueOnDate is NOT NULL AND "
						+ "tm.queueOnDate >= :now AND "
						+ "tm.queueDate IS NULL AND "
						+ "tm.processDate is NULL AND "
						+ "(tm.deleted IS NULL OR tm.deleted = false) AND "
						+ "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'cohortType' and tmp.string = :cohortType) AND "
						+ "tm in (SELECT tmp.tolvenMessage from TolvenMessageProperty tmp where tmp.propertyName = 'snapshotType' and tmp.string = :snapshotType)");
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
		Query query = em.createQuery("SELECT md FROM MenuData md WHERE "
				+ "md.id = :id AND " + "md.account.id = :account");
		query.setParameter("id", id);
		query.setParameter("account", account.getId());
		query.setMaxResults(1);
		List<MenuData> results = query.getResultList();
		if (results.size() == 0) {
			return null;
		} else if (results.size() == 1) {
			return results.get(0);
		} else {
			throw new RuntimeException(
					"Found more than one active MenuData with id: " + id
							+ " in account: " + account.getId());
		}
	}

	@Override
	public void addCohortPlaceholderID(String cohortName, MenuData cohort) {
		cohort.addPlaceholderID("org.tolven.cohort", cohortName,
				"org.tolven.analysis");
	}

	@Override
	public MenuData findCohort(String cohortName, Account account) {
		List<MenuData> cohorts = menuBean.findMenuDataById(account,
				"org.tolven.cohort", cohortName);
		if (cohorts.size() == 0) {
			return null;
		} else if (cohorts.size() == 1) {
			return cohorts.get(0);
		} else {
			throw new RuntimeException(
					"Found more than one cohort with placeholder ID: "
							+ cohortName);
		}
	}

	@Override
	public void addPatientcohortPlaceholderID(String cohortType,
			MenuData patientcohort) {
		patientcohort.addPlaceholderID("org.tolven.patientcohort", cohortType
				+ "-" + patientcohort.getParent01().getId(),
				"org.tolven.analysis");
	}

	@Override
	public MenuData findPatientcohort(String cohortType, MenuData patient,
			Account account) {
		return findPatientcohort(cohortType, patient.getId(), account);
	}

	@Override
	public MenuData findPatientcohort(MenuData patient, MenuData cohort,
			Account account) {
		return findPatientcohort(cohort.getString02(), patient.getId(), account);
	}

	@Override
	public MenuData findPatientcohort(String cohortType, long patientId,
			Account account) {
		List<MenuData> patientcohorts = menuBean.findMenuDataById(account,
				"org.tolven.patientcohort", cohortType + "-" + patientId);
		if (patientcohorts.size() == 0) {
			return null;
		} else if (patientcohorts.size() == 1) {
			return patientcohorts.get(0);
		} else {
			throw new RuntimeException(
					"Found more than one patientcohort with placeholder ID: "
							+ cohortType + "-" + patientId);
		}
	}

	@Override
	public List<MenuData> findCohortPatients(MenuData cohort, Account account) {
		return findCohortPatients(cohort.getString02(), account);
	}

	@Override
	public List<MenuData> findCohortPatients(String cohortType, Account account) {
		Query query = em.createQuery("SELECT md FROM MenuData md WHERE "
				+ "md.account.id = :account AND "
				+ "md.parent02.string02 = :code AND "
				+ "md.menuStructure.role = :role AND "
				+ "md.menuStructure.path = :path AND "
				+ "(md.deleted IS NULL OR md.deleted = false)");
		query.setParameter("account", account.getId());
		query.setParameter("path", "echr:patient:patientcohort");
		query.setParameter("role", "placeholder");
		query.setParameter("code", cohortType);
		List<MenuData> patientcohorts = query.getResultList();
		logger.debug("Found " + patientcohorts.size() + " patientcohorts in "
				+ cohortType + " ");
		return patientcohorts;
	}

	@Override
	public JFreeChart getChart(String cohortType, String snapshotType,
			String chartType, AccountUser accountUser, Date now) {
		CohortSnapshotLocal cohortSnapshotBean = getCohortBean(cohortType);
		return cohortSnapshotBean.getChart(cohortType, snapshotType, chartType,
				accountUser, now);
	}

	private CohortSnapshotLocal getCohortBean(String cohortType) {
		try {
			String bean = cohortTypes.getProperty(cohortType);
			if (bean == null) {
				throw new RuntimeException(
						"Could not locate bean for cohort code '" + cohortType);
			} else {
				InitialContext ctx = new InitialContext();
				return (CohortSnapshotLocal) ctx.lookup(bean);
			}
		} catch (NamingException ex) {
			throw new RuntimeException(
					"Could not locate bean for cohort code '" + cohortType
							+ "'", ex);
		}
	}

	public void upateMenuDataVersion(String path, Account account, Date now) {
		Map<String, MenuDataVersionMessage> mdvs = new HashMap<String, MenuDataVersionMessage>();
		MenuDataVersionMessage mdv = new MenuDataVersionMessage(path, account
				.getId());
		mdvs.put(path, mdv);
		menuBean.queueDeferredMDVs(mdvs);
	}

	/**
	 * This function deletes all previous entries in the snapshot lists,
	 * while taking a new snapshot.
	 */
	@Override
	public void deleteAnalysisList(Account account, String snapshotType) {
		Query query = em
				.createQuery("update  MenuData md set md.deleted=TRUE WHERE "
						+ "md.account.id = :account and  md.path LIKE '%"
						+ snapshotType + "%' ");
		query.setParameter("account", account.getId());
		query.executeUpdate();
	}

	/**
	 * This function deletes all patients in the echr:patients:cohorts:current list,
	 * on any change in the cohort properties.
	 */
	@Override
	public void deletePatientCohortList(Account account, String analysisType) {
		Query query = em
				.createQuery("update  MenuData md set md.deleted=TRUE WHERE "
						+ "md.account.id = :account and  md.path LIKE '%:cohorts:current%' and md.string01 = :analysisType");
		query.setParameter("account", account.getId());
		query.setParameter("analysisType", analysisType);
		query.executeUpdate();
	}

	/**
	 * This function deletes all cohort placeholders,
	 * on any change in the cohort properties.
	 */
	@Override
	public void deleteCohortPlaceholder(Account account, String cohortType) {
		Query query = em.createQuery("delete PlaceholderID pi WHERE "
				+ "pi.account.id = :account and  pi.extension LIKE '" + cohortType
				+ "%'");
		query.setParameter("account", account.getId());
		query.executeUpdate();
	}

	/**
	 * This function deletes all patients in the echr:cohorts list,
	 * on any change in the cohort properties.
	 */
	@Override
	public void deleteCohortList(Account account, String analysisType, String cohortType) {
		Query query = em
				.createQuery("update  MenuData md set md.deleted=TRUE WHERE "
						+ "md.account.id = :account and  md.string01 =:analysisType and md.string02 = :cohortType and md.path LIKE '%echr:cohort-%'");
		query.setParameter("account", account.getId());
		query.setParameter("analysisType", analysisType);
		query.setParameter("cohortType", cohortType);
		query.executeUpdate();
	}
	
	/**
	 * This function deletes the patients (It is generated manually when there is null cohort)
	 * in the echr:cohorts list.
	 * @author Pinky
	 * Created on 09/28/2010
	 */
	@Override
	public void deleteFalseCohortList(Account account, String analysisType, String cohortType) {
		Query query = em
				.createQuery("update  MenuData md set md.deleted=TRUE WHERE "
						+ "md.account.id = :account and  md.string08 =:analysisType and md.string02 = :cohortType and md.path LIKE '%echr:cohort-%'");
		query.setParameter("account", account.getId());
		query.setParameter("analysisType", analysisType);
		query.setParameter("cohortType", cohortType);
		query.executeUpdate();
	}
	

	/**
	 * This function deletes all patients in the echr:analysis:cohorts:all list,
	 * on any change in the cohort properties.
	 */
	@Override
	public void deleteAnalysisCohortList(Account account, String analysisType) {
		Query query = em
				.createQuery("update  MenuData md set md.deleted=TRUE WHERE "
						+ "md.account.id = :account and  md.string01 =:analysisType and md.path LIKE '%echr:analysis:cohorts:all-%'");
		query.setParameter("account", account.getId());
		query.setParameter("analysisType", analysisType);
		query.executeUpdate();
	}
	
	/**
	 * This function deletes the patients (It is generated manually when there is null cohort)
	 * in the echr:analysis:cohorts:all list.
	 * @author Pinky
	 * Created on 09/28/2010
	 */
	@Override
	public void deleteFalseAnalysisCohortList(Account account, String analysisType) {
		Query query = em
				.createQuery("update  MenuData md set md.deleted=TRUE WHERE "
						+ "md.account.id = :account and  md.string08 =:analysisType and md.path LIKE '%echr:analysis:cohorts:all-%'");
		query.setParameter("account", account.getId());
		query.setParameter("analysisType", analysisType);
		query.executeUpdate();
	}

	static {
		String propertyFileName = SnapshotBean.class.getSimpleName()
				+ ".properties";
		try {
			cohortTypes = new Properties();
			InputStream in = SnapshotBean.class
					.getResourceAsStream(propertyFileName);
			if (in != null) {
				cohortTypes.load(in);
			}
		} catch (IOException ex) {
			throw new RuntimeException("Could not load cohort types from: "
					+ propertyFileName, ex);
		}
	}

	/**
	 * Based on the age and gender of the patient, this function validated the patients who satisfies
	 * the two cohort properties - Age Ranges and gender.Return either true or false.
	 */
	@Override
	public Boolean validateCohortProperties(String type, MenuData patient,
			AppEvalAdaptor app) {
		Boolean genderCondition = false;
		Boolean ageCondition = false;
		String lowRangeType = "";
		String highRangeType = "";
		String calcType = "";
		int lowRange = 0;
		int highRange = 0;
		int ageY = 0;
		int ageM = 0;
		int ageD = 0;
		int ageW = 0;
		int calcAge = 0;

		//gender Condition
		if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + ".gender") == null) {
			genderCondition = true;
		} else if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + ".gender").equals("")) {
			genderCondition = true;
		} else if ((app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + ".gender").equals(patient
				.getString04().trim()))
				|| (app.getAccount().getProperty().get(
						"org.tolven.cohort." + type + ".gender").equals("Both"))) {
			genderCondition = true;
		}
		
		//age Condition
		if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + ".ageRangeCodes") == null) {
			ageCondition = true;
		} else if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + ".ageRangeCodes").equals("")) {
			ageCondition = true;
		} else {
			for (int i = 1; i < app.getAccount().getProperty().get(
					"org.tolven.cohort." + type + ".ageRangeCodes").split(",").length; i++) {
				ageCondition = false;

				lowRange = Integer.parseInt(app.getAccount().getProperty().get(
						"org.tolven.cohort." + type + ".ageRangeCodes").split(
						",")[i].split("~")[0]);
				lowRangeType = app.getAccount().getProperty().get(
						"org.tolven.cohort." + type + ".ageRangeCodes").split(
						",")[i].split("~")[1];
				highRange = Integer.parseInt(app.getAccount().getProperty()
						.get("org.tolven.cohort." + type + ".ageRangeCodes")
						.split(",")[i].split("~")[2]);
				highRangeType = app.getAccount().getProperty().get(
						"org.tolven.cohort." + type + ".ageRangeCodes").split(
						",")[i].split("~")[3];

				GregorianCalendar n = new GregorianCalendar();
				n.setTime(new Date());
				GregorianCalendar b = new GregorianCalendar();
				b.setTime(patient.getDate01());

				int years = n.get(Calendar.YEAR) - b.get(Calendar.YEAR);
				int days = n.get(Calendar.DAY_OF_YEAR)
						- b.get(Calendar.DAY_OF_YEAR);

				if (days < 0) {
					years--;
					days = days + 365;
				}
				if (years > 1) {
					calcAge = years;
					calcType = "year";
				} else if (years == 0 && days < 30) {
					calcAge = days;
					calcType = "days";
				} else {
					calcAge = years * 12 + (days / 30);
					calcType = "months";
				}

				if (calcType.equals("year")) {
					ageD = calcAge * 365;
					ageM = calcAge * 12;
					ageY = calcAge;
					ageW = calcAge * 52;
				}
				if (calcType.equals("months")) {
					ageD = calcAge * 30;
					ageM = calcAge;
					ageY = 0;
					ageW = calcAge / 7;
				}
				if (calcType.equals("days")) {
					ageD = calcAge;
					ageM = 0;
					ageY = 0;
					ageW = calcAge / 7;
				}

				if ((lowRangeType.equals("year"))
						&& (highRangeType.equals("year"))) {
					if ((ageY >= lowRange) && (ageY <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("year"))
						&& (highRangeType.equals("month"))) {
					if ((ageY >= lowRange) && (ageM <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("year"))
						&& (highRangeType.equals("week"))) {
					if ((ageY >= lowRange) && (ageW <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("year"))
						&& (highRangeType.equals("day"))) {
					if ((ageY >= lowRange) && (ageD <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("month"))
						&& (highRangeType.equals("year"))) {
					if ((ageM >= lowRange) && (ageY <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("month"))
						&& (highRangeType.equals("month"))) {
					if ((ageM >= lowRange) && (ageM <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("month"))
						&& (highRangeType.equals("week"))) {
					if ((ageM >= lowRange) && (ageW <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("month"))
						&& (highRangeType.equals("day"))) {
					if ((ageM >= lowRange) && (ageD <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("week"))
						&& (highRangeType.equals("year"))) {
					if ((ageW >= lowRange) && (ageY <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("week"))
						&& (highRangeType.equals("month"))) {
					if ((ageW >= lowRange) && (ageM <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("week"))
						&& (highRangeType.equals("week"))) {
					if ((ageW >= lowRange) && (ageW <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("week"))
						&& (highRangeType.equals("day"))) {
					if ((ageW >= lowRange) && (ageD <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("day"))
						&& (highRangeType.equals("year"))) {
					if ((ageD >= lowRange) && (ageY <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("day"))
						&& (highRangeType.equals("month"))) {
					if ((ageD >= lowRange) && (ageM <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("day"))
						&& (highRangeType.equals("week"))) {
					if ((ageD >= lowRange) && (ageW <= highRange)) {
						ageCondition = true;
					}
				}
				if ((lowRangeType.equals("day"))
						&& (highRangeType.equals("day"))) {
					if ((ageD >= lowRange) && (ageD <= highRange)) {
						ageCondition = true;
					}
				}
				if (ageCondition == true)
					break;
			}
		}
				
		if (genderCondition == true && ageCondition == true ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This function validated the patients who satisfies
	 * the two cohort properties - Included codes and Excluded codes. 
	 * Returns either true or false.
	 */
	@Override
	public Boolean validateCodeStatusCohortProperties(String type, MenuData patient,
			AppEvalAdaptor app, String code) {
		
		Boolean inculdeCodeCondition = false;
		Boolean excludeCodeCondition = false;
		
		//includeCode Condition
		if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + ".includeCodes") == null) {
			inculdeCodeCondition = false;
		} else if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + ".includeCodes").equals("")) {
			inculdeCodeCondition = false;
		} else {
			for (int i = 1; i < app.getAccount().getProperty().get(
					"org.tolven.cohort." + type + ".includeCodes").split(",").length; i++) {
				inculdeCodeCondition = false;
				if(app.getAccount().getProperty().get(
					"org.tolven.cohort." + type + ".includeCodes").split(",")[i].equals(code)){
					inculdeCodeCondition = true;
					break;
				}
			}
		}
		
		//excludeCode Condition
		if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + "..excludeCodes") == null) {
			excludeCodeCondition = true;
		} else if (app.getAccount().getProperty().get(
				"org.tolven.cohort." + type + "..excludeCodes").equals("")) {
			excludeCodeCondition = true;
		} else {
			for (int i = 1; i < app.getAccount().getProperty().get(
					"org.tolven.cohort." + type + "..excludeCodes").split(",").length; i++) {
				excludeCodeCondition = true;
				if(app.getAccount().getProperty().get(
						"org.tolven.cohort." + type + "..excludeCodes").split(",")[i].equals(code)){
					excludeCodeCondition = false;
					break;
					}
			}
		}
		
		if(inculdeCodeCondition == true && excludeCodeCondition == true){
			 return true;
		 }
		 else
		 	return false;
	}
	 
}