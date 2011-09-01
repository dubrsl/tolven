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
package org.tolven.web;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import org.tolven.app.DataExtractLocal;
import org.tolven.app.DataField;
import org.tolven.app.DataQueryResults;
import org.tolven.core.entity.AccountUser;

/**
 * Handles data extraction based on menu paths
 * 
 * @author Joseph Isaac
 */
public class DataExtractAction extends TolvenAction {

    private String menuPath;

    @EJB(name = "tolven/DataExtractBean/local")
    private DataExtractLocal dataExtractBean;

    private List<SelectItem> availableColumnHeadings;
    private List<String> selectedColumnHeadings;
    private List<SelectItem> availableSQLDialects;
    private String selectedSQLDialect;
    private String tableName;
    private String whatToExtract;
    private DataQueryResults dq;
    public static final String WHAT_TO_EXTRACT = "org.tolven.web.dataextract.whattoextract";
    
    /** Creates a new instance of DataExtractAction */
    public DataExtractAction() {
    }

    public String getMenuPath() {
        if (menuPath == null) {
            menuPath = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("menuPath");
        }
        return menuPath;
    }

    public void setMenuPath(String menupath) {
        this.menuPath = menupath;
    }

    public DataQueryResults getDQ() {
    	if (dq==null) {
        	dq = getDataExtractBean().setupQuery(getMenuPath(), getAccountUser());
    	}
    	return dq;
    }
    
    /**
     * Return a structure suitable for faces <f:selectItems>
     * @return
     */
    public List<SelectItem> getAvailableColumnHeadings() {
    	if (availableColumnHeadings==null) {
            availableColumnHeadings = new ArrayList<SelectItem>();
            for (DataField field : getDQ().getFields()) {
            	SelectItem item = new SelectItem(field.getLabel(), field.getLabel());
                availableColumnHeadings.add(item);
            }
    	}
        return availableColumnHeadings;
    }

    public List<String> getSelectedColumnHeadings() {
    	if (selectedColumnHeadings==null) {
        	selectedColumnHeadings = new ArrayList<String>();
            for (DataField field : getDQ().getFields()) {
            	if (field.isEnabled()) {
                	selectedColumnHeadings.add(field.getLabel());
            	}
            }
    	}
        return selectedColumnHeadings;
    }

    public void setSelectedColumnHeadings(List<String> selectedColumns) {
    	getDQ().setSelectedFields(selectedColumns);
    }

    public List<String> getSelectedColumns() {
        return selectedColumnHeadings;
    }

    public List<SelectItem> getAvailableSQLDialects() {
        if (availableSQLDialects == null) {
            availableSQLDialects = new ArrayList<SelectItem>();
            for (String heading : getDataExtractBean().getSQLDialects()) {
                availableSQLDialects.add(new SelectItem(heading, heading));
            }
        }
        return availableSQLDialects;
    }

    public void setAvailableSQLDialects(List<SelectItem> availableDialects) {
        this.availableSQLDialects = availableDialects;
    }

    public String getSelectedSQLDialect() {
        return selectedSQLDialect;
    }

    public void setSelectedSQLDialect(String selectedDialect) {
        this.selectedSQLDialect = selectedDialect;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DataExtractLocal getDataExtractBean() {
        if (dataExtractBean == null) {
            try {
                dataExtractBean = (DataExtractLocal) getContext().lookup("tolven/DataExtractBean/local");
            } catch (NamingException ex) {
                throw new RuntimeException("Failed to look up tolven/DataExtractBean/local", ex);
            }
        }
        return dataExtractBean;
    }

    public AccountUser getAccountUser() {
        return getActivationBean().findAccountUser(getSessionAccountUserId());
    }

    public void xmlDataExtract() throws IOException {
        FacesContext faces = FacesContext.getCurrentInstance();
        Writer out = header(faces, "text/xml", "data-extract.xml");
        getDataExtractBean().streamResultsXML(out, dq);
        faces.responseComplete();
    }

    public void csvDataExtract() throws IOException {
        FacesContext faces = FacesContext.getCurrentInstance();
        Writer out = header(faces, "text/csv", "data-extract.csv");
        getDataExtractBean().streamResultsCSV(out, dq);
        faces.responseComplete();
    }

    public void sqlDataExtract() throws IOException {
        FacesContext faces = FacesContext.getCurrentInstance();
        getAccountUser().getProperty().put(WHAT_TO_EXTRACT, getWhatToExtract());
        if (getTableName() == null || getTableName().trim().length() == 0) {
            faces.addMessage("dataExtractForm:tableName", new FacesMessage("A table name must be supplied"));
            return;
        }
        Writer out = header(faces, "text/sql", "data-extract.sql");
        getDataExtractBean().streamResultsSQL(out, dq, getTableName(), getSelectedSQLDialect());
        faces.responseComplete();
    }

    private Writer header(FacesContext faces, String contentType, String filename) throws IOException {
//        byte[] bytes = output.getBytes(Charset.forName("UTF-8"));
        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType(contentType);
//        response.setContentLength(bytes.length);
        return response.getWriter();
    }

	public String getWhatToExtract() {
		if (whatToExtract==null) {
			whatToExtract = getAccountUser().getProperty().get(WHAT_TO_EXTRACT);
		}
		return whatToExtract;
	}

	public void setWhatToExtract(String whatToExtract) {
		this.whatToExtract = whatToExtract;
	}

}
