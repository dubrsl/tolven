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

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.tolven.core.entity.TolvenUser;
import org.tolven.gen.bean.GenControlCHRAccount;
import org.tolven.gen.entity.FamilyMember;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.TolvenPerson;

/**
 * Backing bean concerned with internally generated (simulated) people. Create an individual person or a whole family. As with all tolven Action beans,
 * the actualy functional work, including persistence and query, is done by separate EJBs.
 * @author John Churin
 */
public class PersonGenAction extends TolvenAction  {

    private int numberToGenerate;

    private int generateHistoryFrom = 2007;

    private String accountType;
    
    private String title;

    private String firstName;
    private String lastName;
    private String password;
    private String uid;
    

    public String generatePeople() throws Exception {
    	getPersonGen().generatePeople( numberToGenerate );
        FacesContext.getCurrentInstance().addMessage( null, new FacesMessage("Generated " + numberToGenerate + " new virtual people"));
    	return "success";
    }
    /** Creates a new instance of PersonGenAction */
    public PersonGenAction()  {
        numberToGenerate = 10;
    }

    public int getPersonCount() throws NamingException {
    	return (int) getPersonGen().countPeople();
    }

    public int getFamilyCount() throws NamingException {
    	return (int) getPersonGen().countFamily();
    }

    /**
     * Action: Generate a new person
     */
    public String newPerson()  throws Exception {
        if (getPersonGen()==null) 
        {
            TolvenLogger.info( "MISSING PersonGenerator bean", PersonGenAction.class);
            return "error";
        }
        TolvenPerson tp = new TolvenPerson(getPersonGen().generatePerson());
        this.setFirstName( tp.getGivenName());
        this.setLastName( tp.getSn());
        this.setPassword( tp.getUserPassword());
        this.setUid( tp.getUid());
        return "success";
    }
    
    public long getCurrentFamilyId( ) {
    	Map<String,Object> reqMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
    	MenuAction menuAction = (MenuAction) reqMap.get( "menu");
//    	if (menuAction==null) menuAction = new MenuAction();
    	Map<String, Long> keys = menuAction.getTargetMenuPath().getNodeValues();
    	long id = keys.get("detail");
    	return id;
    }
    /**
     * Generate new demo patients in this account 
     * @return
     * @throws Exception
     */
    public String createCHRPatients( ) throws Exception {
    	GenControlCHRAccount control = new GenControlCHRAccount();
    	control.setUserId( getSessionTolvenUserId());
    	control.setChrAccountId(getSessionAccountId());
    	control.setCount( getNumberToGenerate() );
    	control.setNow(getNow());
    	control.setStartYear(this.getGenerateHistoryFrom() );
    	getGeneratorBean().queueGeneration(control);
        FacesContext.getCurrentInstance().addMessage( "patGen", new FacesMessage( Integer.toString(getNumberToGenerate()) + " patients are being created"));
        return "success";
    }

    /**
     * Return the specified family member
     */
    public FamilyUnit getFamily() throws NamingException {
    	// Get the familt we're looking for
//    	long id = Long.parseLong((String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("familyId"));
    	long id = getCurrentFamilyId();
    	return getPersonGen().findFamily( id );
    }
    /**
     * Return the specified family members
     */
    public List<FamilyMember> getFamilyMembers() throws NamingException {
    	// Get the familt we're looking for
//    	long id = Long.parseLong((String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("familyId"));
    	long id = getCurrentFamilyId();
    	return getPersonGen().findFamilyMembers( id );
    }
    /**
     * Action: Create a new TolvenUser
     */
    public String newUser()  throws Exception {
        // Verify email address is unique in LDAP
        if (getLDAPLocal().entryExists( getUid())) {
           FacesContext.getCurrentInstance().addMessage( null, new FacesMessage("This user id is already in use"));
           return "error";
        }
        TolvenPerson tp = new TolvenPerson();
        tp.setGivenName( this.getFirstName() );
        tp.setSn( this.getLastName() );
        tp.setUid( this.getUid() );
        tp.setUserPassword( this.getPassword());
        tp.setCn( this.getFirstName() + " " + this.getLastName());
		TolvenUser user = getLoginBean().registerAndActivate( tp, getNow() );
		 /*AccountUser  au = */ getAccountBean().addAccountUser(getAccountBean().createAccount( accountType ), user, getNow(), true, getUserPublicKey() ); 
        FacesContext.getCurrentInstance().addMessage( null, new FacesMessage("User " + this.getUid() +" created"));
        return "success";
    }

	public int getNumberToGenerate() {
		return numberToGenerate;
	}

	public void setNumberToGenerate(int numberToGenerate) {
		this.numberToGenerate = numberToGenerate;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * The title to be used on the account
	 * @return
	 */
	public String getTitle() {
		if (title==null) title = "Account generated " + getNow().toString();
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getGenerateHistoryFrom() {
		return generateHistoryFrom;
	}
	public void setGenerateHistoryFrom(int generateHistoryFrom) {
		this.generateHistoryFrom = generateHistoryFrom;
	}

}
