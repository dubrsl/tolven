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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.tolven.analysis.SnapshotLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuDataVersion;
import org.tolven.web.TolvenAction;

/**
 * Faces Action bean for handling analysis requests
 * 
 * @author Joseph Isaac
 */
public class SnapshotAction extends TolvenAction {

    @EJB
    private SnapshotLocal snapshotBean;

    @EJB
    private MenuLocal menuBean;

    private String cohortType;
    private String snapshotType;

    private Map<String, String> accountProperties;
    
    public SnapshotAction() {
    }

    protected SnapshotLocal getSnapshotBean() {
        if (snapshotBean == null) {
            try {
                snapshotBean = (SnapshotLocal) getContext().lookup("tolven/SnapshotBean/local");
            } catch (NamingException ex) {
                throw new RuntimeException("Could not lookup tolven/SnapshotBean/local", ex);
            }
        }
        return snapshotBean;
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

    public String getSnapshotType() {
        if (snapshotType == null) {
            snapshotType = (String) getRequestParameter("snapshotType");
        }
        return snapshotType;
    }

    public void setSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
    }

    public Map<String, String> getProperties() {
        if (accountProperties == null) {
            accountProperties = getAccountUser().getProperty();
        }
        return accountProperties;
    }

    public String snapshotNow() {
        getSnapshotBean().snapshotNow(getCohortType(), getSnapshotType(), getTop().getAccountUser().getAccount());
        return "success";
    }

    public String startSchedule() {
        getSnapshotBean().scheduleSnapshot(getCohortType(), getSnapshotType(), getTop().getAccountUser().getAccount());
        return "success";
    }

    public String cancelSchedule() {
        getSnapshotBean().cancelSchedule(getCohortType(), getSnapshotType(), getTop().getAccountUser().getAccount());
        return "success";
    }

    public Date getNextScheduledDate() {
        return getSnapshotBean().getNextScheduledDate(getCohortType(), getSnapshotType(), getNow(), getTop().getAccountUser().getAccount());
    }

    public String getNextScheduledDateString() {
        Date date = getNextScheduledDate();
        if(date == null) {
            return null;
        } else {
            return new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);
        }
    }

    public Map<String, Long> getSnapshotVersion() {
        return new SnapshotVersionMap(this);
    }

    public Long getMenuDataVersion(String path) {
        MenuDataVersion mdv = menuBean.findMenuDataVersion(getTop().getAccountUser().getAccount().getId(), path);
        if (mdv == null) {
            return 0L;
        } else {
            return mdv.getVersion();
        }
    }

}
