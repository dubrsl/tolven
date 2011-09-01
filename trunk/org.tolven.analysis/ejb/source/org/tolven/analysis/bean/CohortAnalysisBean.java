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
 * @version $Id: CohortAnalysisBean.java 486 2011-03-29 00:51:12Z kanag.kuttiannan $
 */
package org.tolven.analysis.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.analysis.AnalysisLocal;
import org.tolven.analysis.CohortAnalysisLocal;
import org.tolven.core.entity.Account;

@Stateless
@Local(CohortAnalysisLocal.class)
public class CohortAnalysisBean implements CohortAnalysisLocal {

	private @EJB
	AnalysisLocal analysisBean;
	 
    @Override
    public List<String> getIncludeCodes(String cohortType, Account account) {
        String codeString = account.getProperty().get(cohortType +  ".includeCodes");
        if (codeString == null) {
            codeString = "";
        }
        List<String> codes = new ArrayList<String>();
        for (String code : codeString.split(",")) {
            codes.add(code);
        }
        return codes;
    }

    private void setIncludesCodes(String cohortType, List<String> codes, Account account) {
        Collections.sort(codes);
        account.getProperty().put(cohortType + ".includeCodes", commaSeparatedCodes(codes));
    }

    @Override
    public void addIncludeCode(String cohortType, String code, Account account) {
		List<String> codes = getIncludeCodes(cohortType, account);
		if (!codes.contains(code)) {
			codes.add(code);
			setIncludesCodes(cohortType, codes, account);
		}
	}

	/**
	 * Find the gender for the specified cohort type
	 * 
	 * @param cohortType the type of cohort
	 * @param account the account of the user who logged into the system
	 * @return the gender for the cohort type
	 */
	@Override
	public String findGender(String cohortType, Account account) {
		return account.getProperty().get(cohortType + ".gender");
	}
	
	/**
	 * Set the gender for a cohort type 
	 * 
	 * @param cohortType the type of cohort
	 * @param account the account of the user who logged into the system
	 * @return the gender for the cohort type
	 */
    @Override
    public void setGender(String cohortType, String gender, Account account) {
    	 account.getProperty().put(cohortType + ".gender", gender);
    }
    
    @Override
    public void removeIncludeCode(String cohortType, String code, Account account) {
        List<String> codes = getIncludeCodes(cohortType, account);
        if (codes.contains(code)) {
            codes.remove(code);
            setIncludesCodes(cohortType, codes, account);
        }
    }

    @Override
    public List<String> getExcludeCodes(String cohortType, Account account) {
        String codeString = account.getProperty().get(cohortType + "." + ".excludeCodes");
        if (codeString == null) {
            codeString = "";
        }
        List<String> codes = new ArrayList<String>();
        for (String code : codeString.split(",")) {
            codes.add(code);
        }
        return codes;
    }

    private void setExcludesCodes(String cohortType, List<String> codes, Account account) {
        Collections.sort(codes);
        account.getProperty().put(cohortType + "." + ".excludeCodes", commaSeparatedCodes(codes));
    }

    @Override
    public void addExcludeCode(String cohortType, String code, Account account) {
        List<String> codes = getExcludeCodes(cohortType, account);
        if (!codes.contains(code)) {
            codes.add(code);
            setExcludesCodes(cohortType, codes, account);
        }
    }

    @Override
    public void removeExcludeCode(String cohortType, String code, Account account) {
        List<String> codes = getExcludeCodes(cohortType, account);
        if (codes.contains(code)) {
            codes.remove(code);
            setExcludesCodes(cohortType, codes, account);
        }
    }

    private String commaSeparatedCodes(List<String> codes) {
        Iterator<String> it = codes.iterator();
        StringBuffer buff = new StringBuffer();
        while (it.hasNext()) {
            String code = it.next();
            buff.append(code);
            if (it.hasNext()) {
                buff.append(",");
            }
        }
        return buff.toString();
    }
    
    @Override
    public List<String> getAgeRanges(String cohortType, Account account) {
        String codeString = account.getProperty().get(cohortType +  ".ageRangeCodes");
        if (codeString == null) {
            codeString = "";
        }
        List<String> codes = new ArrayList<String>();
        for (String code : codeString.split(",")) {
            codes.add(code);
        }
        return codes;
    }
    
    @Override
    public void addAgeRange(String cohortType, String code, Account account) {
        List<String> codes = getAgeRanges(cohortType, account);
        if (!codes.contains(code)) {
            codes.add(code);
            setAgeRanges(cohortType, codes, account);
        }
    }
    
    private void setAgeRanges(String cohortType, List<String> codes, Account account) {
        Collections.sort(codes);
        account.getProperty().put(cohortType + ".ageRangeCodes", commaSeparatedCodes(codes));
    }

	@Override
	public void removeAgeRange(String cohortType, String code, Account account) {
		List<String> codes = getAgeRanges(cohortType, account);
        if (codes.contains(code)) {
            codes.remove(code);
            setAgeRanges(cohortType, codes, account);
        }		
	}

	@Override
	/**
	 * Method to refresh the cohort lists on a change in the cohort properties
	 * @author Pinky
	 * Created on 09/17/2010
	 */
	public void refreshCohortList(String cohortType, String snapshotType,
			Account account) {
		Map<String, Object> messageProperties = new HashMap<String, Object>();
        messageProperties.put("command", "update");
        messageProperties.put("cohortType", cohortType);
		analysisBean.scheduleAnalysis(messageProperties, null, account);
		
	}
}
