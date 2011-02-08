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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.analysis.CohortAnalysisLocal;
import org.tolven.core.entity.Account;

@Stateless
@Local(CohortAnalysisLocal.class)
public class CohortAnalysisBean implements CohortAnalysisLocal {

    @Override
    public List<String> getIncludeCodes(String cohortType, Account account) {
        String codeString = account.getProperty().get(cohortType + "." + ".includeCodes");
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
        account.getProperty().put(cohortType + "." + ".includeCodes", commaSeparatedCodes(codes));
    }

    @Override
    public void addIncludeCode(String cohortType, String code, Account account) {
        List<String> codes = getIncludeCodes(cohortType, account);
        if (!codes.contains(code)) {
            codes.add(code);
            setIncludesCodes(cohortType, codes, account);
        }
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

}
