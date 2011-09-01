/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.analysis;

import java.util.List;

import org.tolven.core.entity.Account;

/**
 * @author Joseph Isaac
 */

public interface CohortAnalysisLocal {
	public String findGender(String cohortType, Account account);
	
	public void setGender(String cohortType, String gender, Account account);

    public List<String> getIncludeCodes(String cohortType, Account account);

    public void addIncludeCode(String cohortType, String code, Account account);

    public void removeIncludeCode(String cohortType, String code, Account account);

    public List<String> getExcludeCodes(String cohortType, Account account);

    public void addExcludeCode(String cohortType, String code, Account account);

    public void removeExcludeCode(String cohortType, String code, Account account);
    
    public void addAgeRange(String cohortType, String code, Account account);
    
    public List<String> getAgeRanges(String cohortType, Account account);
    
    public void removeAgeRange(String cohortType, String code, Account account);
    
    public void refreshCohortList(String cohortType, String snapshotType, Account account);

}
