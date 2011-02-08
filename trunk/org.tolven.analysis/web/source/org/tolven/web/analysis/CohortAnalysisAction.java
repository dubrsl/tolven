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

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.tolven.analysis.CohortAnalysisLocal;
import org.tolven.util.TolvenPropertiesMap;
import org.tolven.web.TolvenAction;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Faces Action bean for handling analysis requests
 * 
 * @author Joseph Isaac
 */
public class CohortAnalysisAction extends TolvenAction {

    @EJB
    private CohortAnalysisLocal cohortAnalysisBean;

    private String cohortType;

    private Double lowAge;
    private String lowAgeUnit;
    private Double highAge;
    private String highAgeUnit;

    private String includeCode;
    private String excludeCode;
    
    private TolvenPropertiesMap propertiesMap;

    public CohortAnalysisAction() {
    }

    protected CohortAnalysisLocal getCohortAnalysisBean() {
        return cohortAnalysisBean;
    }

    public String getCohortType() {
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

    public Double getLowAge() {
        return lowAge;
    }

    public void setLowAge(Double lowAge) {
        this.lowAge = lowAge;
    }

    public String getLowAgeUnit() {
        return lowAgeUnit;
    }

    public void setLowAgeUnit(String lowAgeUnit) {
        this.lowAgeUnit = lowAgeUnit;
    }

    public Double getHighAge() {
        return highAge;
    }

    public void setHighAge(Double highAge) {
        this.highAge = highAge;
    }

    public String getHighAgeUnit() {
        return highAgeUnit;
    }

    public void setHighAgeUnit(String highAgeUnit) {
        this.highAgeUnit = highAgeUnit;
    }

    public String updateProperties() {
        WebResource webResource = getAppWebResource().path("account/properties/set");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        for (String name : getProperties().getMap().keySet()) {
            formData.putSingle(name, (String) getProperties().get(name));
        }
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).cookie(getSSOCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " POST " + getRequest().getUserPrincipal() + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        return "success";
    }

    public TolvenPropertiesMap getProperties() {
        if (propertiesMap == null) {
            propertiesMap = new TolvenPropertiesMap(getTolvenResourceBundle().getMap());
        }
        return propertiesMap;
    }

    public List<String> getAgeRanges() {
        return new ArrayList<String>();
    }

    public List<String> getIncludeCodes() {
        return getCohortAnalysisBean().getIncludeCodes(getCohortType(), getTop().getAccountUser().getAccount());
    }

    public String addIncludeCode() {
        if (getIncludeCode() == null || getIncludeCode().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("A code must be supplied"));
            return "error";
        } else if (getIncludeCode().indexOf(",") != -1) {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("Commas are not permitted"));
            return "error";
        } else if (getIncludeCodes().contains(getIncludeCode())) {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("Code is already present"));
        } else if (getCohortType() == null) {
            throw new RuntimeException("Cohort Type cannot be null");
        } else {
            getCohortAnalysisBean().addIncludeCode(getCohortType(), getIncludeCode(), getTop().getAccountUser().getAccount());
        }
        return "success";
    }

    public String removeIncludeCode() {
        if (getIncludeCodes().contains(getIncludeCode())) {
            getCohortAnalysisBean().removeIncludeCode(getCohortType(), getIncludeCode(), getTop().getAccountUser().getAccount());
            return "success";
        } else {
            FacesContext.getCurrentInstance().addMessage("codesForm:includeCode", new FacesMessage("Code is not present"));
            return "error";
        }
    }

    public List<String> getExcludeCodes() {
        return getCohortAnalysisBean().getExcludeCodes(getCohortType(), getTop().getAccountUser().getAccount());
    }

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
        } else {
            getCohortAnalysisBean().addExcludeCode(getCohortType(), getExcludeCode(), getTop().getAccountUser().getAccount());
        }
        return "success";
    }

    public String removeExcludeCode() {
        if (getExcludeCodes().contains(getExcludeCode())) {
            getCohortAnalysisBean().removeExcludeCode(getCohortType(), getExcludeCode(), getTop().getAccountUser().getAccount());
            return "success";
        } else {
            FacesContext.getCurrentInstance().addMessage("codesForm:excludeCode", new FacesMessage("Code is not present"));
            return "error";
        }
    }

}
