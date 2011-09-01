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
package org.tolven.web.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.tolven.analysis.CohortAnalysisLocal;
import org.tolven.analysis.SnapshotLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.web.TolvenAction;

/**
 * Faces Action bean for handling analysis requests
 * 
 * @author Joseph Isaac
 */
public class CohortAnalysisAction extends TolvenAction {

    @EJB
    private CohortAnalysisLocal cohortAnalysisBean;

    @EJB
    private SnapshotLocal snapshotBean;
    private AccountDAOLocal accountBean;

    private String cohortType;
    private String gender;

    private Integer lowAge;
    private String lowAgeUnit;
    private Integer highAge;
    private String highAgeUnit;
    
    private String includeCode;
    private String excludeCode;
    private ArrayList<String> ageRanges;

    private Map<String, String> accountProperties;

    public CohortAnalysisAction() {
    }

    protected CohortAnalysisLocal getCohortAnalysisBean() {
        if (cohortAnalysisBean == null) {
            try {
                cohortAnalysisBean = (CohortAnalysisLocal) getContext().lookup("tolven/CohortAnalysisBean/local");
            } catch (NamingException ex) {
                throw new RuntimeException("Could not lookup tolven/CohortAnalysisBean/local", ex);
            }
        }
        return cohortAnalysisBean;
    }
    
    public String getCohortType() {
    	if (cohortType == null) {
            cohortType = (String) getRequestParameter("cohortType");
        }
        return cohortType;
    }

    public void setCohortType(String cohortType) {
        this.cohortType = cohortType;
    }

    public String getIncludeCode() {
        return includeCode;
    }

    public void setIncludeCode(String includeCode) {
        this.includeCode = includeCode;
    }

    public String getExcludeCode() {
        return excludeCode;
    }

    public void setExcludeCode(String excludeCode) {
        this.excludeCode = excludeCode;
    }

    public Integer getLowAge() {
        return lowAge;
    }

    public void setLowAge(Integer lowAge) {
        this.lowAge = lowAge;
    }

    public String getLowAgeUnit() {
        return lowAgeUnit;
    }

    public void setLowAgeUnit(String lowAgeUnit) {
        this.lowAgeUnit = lowAgeUnit;
    }

    public Integer getHighAge() {
        return highAge;
    }

    public void setHighAge(Integer highAge) {
        this.highAge = highAge;
    }

    public String getHighAgeUnit() {
        return highAgeUnit;
    }
    public void setHighAgeUnit(String highAgeUnit) {
        this.highAgeUnit = highAgeUnit;
    }
    public String getGender() {
    	if(gender==null) {
    		gender=getCohortAnalysisBean().findGender(getCohortType(), getTop().getAccountUser().getAccount());
    	}
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

    public String updateProperties() {
        Properties properties = new Properties();
        for (String name : getProperties().keySet()) {
            properties.setProperty(name, (String) getProperties().get(name));
        }
        accountBean.putAccountProperties(getAccountUser().getAccount().getId(), properties);
        return "success";
    }

    public Map<String, String> getProperties() {
        if (accountProperties == null) {
            accountProperties = getAccountUser().getProperty();
        }
        return accountProperties;
    }
/*
    public List<String> getAgeRanges() {
        return new ArrayList<String>();
    }
*/
    public List<String> getIncludeCodes() {
        return getCohortAnalysisBean().getIncludeCodes(getCohortType(), getTop().getAccountUser().getAccount());
    }

    /**
     * Method used to add included codes.
     * Modified to add validation that a code cannot be both included and excluded?.
     * @author Pinky S
     * Modified on 31/01/11
     * @return
     */
    public String addIncludeCode() {
        if (getIncludeCode() == null || getIncludeCode().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("A code must be supplied"));
            return "error";
        } else if (getIncludeCode().indexOf(",") != -1) {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("Commas are not permitted"));
            return "error";
        } else if (getIncludeCodes().contains(getIncludeCode())) {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("Code is already present"));
        } else if ((getExcludeCodes()!=null)&&(getExcludeCodes().contains(getIncludeCode()))) {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("Code is present in Excluded Codes list"));
        } else if (getCohortType() == null) {
            throw new RuntimeException("Cohort Type cannot be null");
        } else {
            getCohortAnalysisBean().addIncludeCode(getCohortType(), getIncludeCode(), getTop().getAccountUser().getAccount());
            getCohortAnalysisBean().refreshCohortList(getCohortType(), "refreshCohort", getTop().getAccountUser().getAccount());
        }
        return "success";
    }

    public String removeIncludeCode() {
    	String code = (String) getRequestParameter("code");
        if (getIncludeCodes().contains(code)) {
            getCohortAnalysisBean().removeIncludeCode(getCohortType(), code, getTop().getAccountUser().getAccount());
            getCohortAnalysisBean().refreshCohortList(getCohortType(), "refreshCohort", getTop().getAccountUser().getAccount());
            return "success";
        } else {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("Code is not present"));
            return "error";
        }
    }

    public List<String> getExcludeCodes() {
        return getCohortAnalysisBean().getExcludeCodes(getCohortType(), getTop().getAccountUser().getAccount());
    }

    /**
     * Method used to add excluded codes.
     * Modified to add validation that a code cannot be both included and excluded?.
     * @author Pinky S
     * Modified on 31/01/11
     * @return
     */
    public String addExcludeCode() {
        if (getExcludeCode() == null || getExcludeCode().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("codesForm:excludeCode", new FacesMessage("A code must be supplied"));
            return "error";
        } else if (getExcludeCode().indexOf(",") != -1) {
            FacesContext.getCurrentInstance().addMessage("codesForm:excludeCode", new FacesMessage("Commas are not permitted"));
            return "error";
        } else if (getExcludeCodes().contains(getExcludeCode())) {
            FacesContext.getCurrentInstance().addMessage("codesForm:excludeCode", new FacesMessage("Code is already present"));
            return "error";
        } else if ((getIncludeCodes()!=null)&&(getIncludeCodes().contains(getExcludeCode()))){
        	FacesContext.getCurrentInstance().addMessage("codesForm:excludeCode", new FacesMessage("Code is present in Included Codes list"));
            return "error";
        }else {
            getCohortAnalysisBean().addExcludeCode(getCohortType(), getExcludeCode(), getTop().getAccountUser().getAccount());
            getCohortAnalysisBean().refreshCohortList(getCohortType(), "refreshCohort", getTop().getAccountUser().getAccount());
        }
        return "success";
    }
    
    public String addGender() {
    	getCohortAnalysisBean().setGender(getCohortType(),getGender(), getTop().getAccountUser().getAccount());
    	getCohortAnalysisBean().refreshCohortList(getCohortType(), "refreshCohort", getTop().getAccountUser().getAccount());
    	return "success";
    }
    
    public String findGender() {
    	return getCohortAnalysisBean().findGender(getCohortType(), getTop().getAccountUser().getAccount());    	
    }
    

    public String removeExcludeCode() {
    	String code = (String) getRequestParameter("code");
        if (getExcludeCodes().contains(code)) {
            getCohortAnalysisBean().removeExcludeCode(getCohortType(), code, getTop().getAccountUser().getAccount());
            getCohortAnalysisBean().refreshCohortList(getCohortType(), "refreshCohort", getTop().getAccountUser().getAccount());
            return "success";
        } else {
            FacesContext.getCurrentInstance().addMessage("codesForm:excludeCode", new FacesMessage("Code is not present"));
            return "error";
        }
    }

    public List<String> getAgeRanges() {
        return getCohortAnalysisBean().getAgeRanges(getCohortType(), getTop().getAccountUser().getAccount());
    }

    /**
     * Method to add the age ranges added on preference screen.
     * @return a String which indicates the process is either success or failure.
     * @author Pinky
     * Created on 09/03/2010
     */
    public String addAgeRange() {
        if (getLowAge() == null || getHighAge() == null || getLowAgeUnit() == null|| getHighAgeUnit() == null ) {
            FacesContext.getCurrentInstance().addMessage("codesForm:ageRangeTable", new FacesMessage("A code must be supplied"));
            return "error";
        }else if (getCohortType() == null) {
            throw new RuntimeException("Cohort Type cannot be null");
        }else if (getAgeRanges().contains(getLowAge().toString()+"~"+getLowAgeUnit()+"~"+getHighAge().toString()+"~"+getHighAgeUnit())) {
            FacesContext.getCurrentInstance().addMessage("codesForm:ageRangeTable", new FacesMessage("Code is already present"));
            return "error";
        }else {
            getCohortAnalysisBean().addAgeRange(getCohortType(), getLowAge().toString()+"~"+getLowAgeUnit()+"~"+getHighAge().toString()+"~"+getHighAgeUnit(), getTop().getAccountUser().getAccount());
            getCohortAnalysisBean().refreshCohortList(getCohortType(), "refreshCohort", getTop().getAccountUser().getAccount());
        }
        return "success";
    }
    
    /**
     * Method to remove the age ranges added on preference screen.
     * @return a String which indicates the process is either success or failure.
     * @author Pinky
     * Created on 09/03/2010
     */
    public String removeAgeRange() {
    	String ageRange = (String) getRequestParameter("ageRange");
        if (getAgeRanges().contains(ageRange)) {
            getCohortAnalysisBean().removeAgeRange(getCohortType(), ageRange, getTop().getAccountUser().getAccount());
            getCohortAnalysisBean().refreshCohortList(getCohortType(), "refreshCohort", getTop().getAccountUser().getAccount());
            return "success";
        } else {
            FacesContext.getCurrentInstance().addMessage("codesForm:ageRangeTable", new FacesMessage("Code is not present"));
            return "error";
        }
    }

}
