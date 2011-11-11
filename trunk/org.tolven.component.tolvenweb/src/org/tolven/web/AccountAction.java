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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountExchange;
import org.tolven.core.entity.AccountRole;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.AccountUserRole;
import org.tolven.core.entity.TolvenUser;
import org.tolven.util.ExceptionFormatter;
import org.tolven.web.security.GeneralSecurityFilter;

public class AccountAction extends TolvenAction {
    
    private List<AccountUser> accountUsers = null;

	private DataModel accountUsersModel;
	private List<AccountExchange> accountExchangeSend;
	private DataModel accountExchangeSendModel;
	private List<AccountExchange> accountExchangeReceive;
	private DataModel accountExchangeReceiveModel;
	private long sendAccountId;
	private long receiveAccountId;
	private AccountRole newAccountRole;
	private List<String> selectedAccountUserRoleItems;
	
	private AccountUser selectedAccountUser;
	
	// Starting with next login, remember my selection this time
    private boolean rememberDefault;
    
    private Logger logger = Logger.getLogger(AccountAction.class);

    public AccountAction() {
        super();
        // J2EE 1.5 has not yet defined exact XML <ejb-ref> syntax for EJB3 so we'll self-wire
        // I think this is a JBOSS-only scam.
        //TODO Injection does not work for JBoss (v4.0.4GA) web tier (tomcat), but does for GlassFish
    }

    /**
     * <p>The accountUser list is used when a user logging in has more than one account and
     * has not selected one as a default. It is also used when the user wants to change
     * accounts. </p>
     */
    public List<AccountUser> getAccountUsers() {
        if (accountUsers==null) {
            //TODO: This could be more efficient
            TolvenUser user = getActivationBean().findTolvenUser(getSessionTolvenUserId());
            accountUsers = getActivationBean().findUserAccounts(user); 
        }
        return accountUsers;
    }
    
	public DataModel getAccountUsersModel() {
		if (accountUsersModel==null) {
			accountUsersModel = new ListDataModel();
			accountUsersModel.setWrappedData(getAccountUsers());
		}
		return accountUsersModel;
	}

    /**
     */
	public List<AccountExchange> getAccountExchangeSend() {
		if (accountExchangeSend==null) {
            accountExchangeSend = getAccountBean().findActiveEndPoints(getAccountUser().getAccount(), AccountExchange.Direction.SEND, false);
        }
		return accountExchangeSend;
	}

	public DataModel getAccountExchangeSendModel() {
		if (accountExchangeSendModel==null) {
			accountExchangeSendModel = new ListDataModel();
			accountExchangeSendModel.setWrappedData(getAccountExchangeSend());
		}
		return accountExchangeSendModel;
	}

	/**
     */
	public List<AccountExchange> getAccountExchangeReceive() {
		if (accountExchangeReceive==null) {
			accountExchangeReceive = getAccountBean().findActiveEndPoints(getAccountUser().getAccount(), AccountExchange.Direction.RECEIVE, false);
        }
		return accountExchangeReceive;
	}

	public DataModel getAccountExchangeReceiveModel() {
		if (accountExchangeReceiveModel==null) {
			accountExchangeReceiveModel = new ListDataModel();
			accountExchangeReceiveModel.setWrappedData(getAccountExchangeReceive());
		}
		return accountExchangeReceiveModel;
	}

	public boolean isRememberDefault() {
		return rememberDefault;
	}

	public void setRememberDefault(boolean rememberDefault) {
		this.rememberDefault = rememberDefault;
	}

	/**
	 * User has selected an account to log into
	 * @return
	 */
    public String login() {
        AccountUser accountUser = (AccountUser) accountUsersModel.getRowData();
        // Remember default
        // Save accountUserId in session for subsequent request so the security filters can intercept appropriately
        String accountUserIdString = String.valueOf(accountUser.getId());
        try {
            setSessionProperty(GeneralSecurityFilter.PROPOSED_ACCOUNTUSER_ID, accountUserIdString);
            if (isRememberDefault()) {
                setSessionProperty(GeneralSecurityFilter.REMEMBER_DEFAULT_ACCOUNT, "true");
            }
            return "success";
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("accountForm:accounts", new FacesMessage("Failed to log into account: " + accountUserIdString + " " + ExceptionFormatter.toSimpleStringMessage(ex, ",", true)));
            return "fail";
        }
	}

	/**
	 * Return the number of accountUser records for this user
	 * @return
	 */
	public int getAccountUserCount() {
		return getAccountUsers().size();
	}

	public long getReceiveAccountId() {
		return receiveAccountId;
	}

	public void setReceiveAccountId(long receiveAccountId) {
		this.receiveAccountId = receiveAccountId;
	}

	public long getSendAccountId() {
		return sendAccountId;
	}

	public void setSendAccountId(long sendAccountId) {
		this.sendAccountId = sendAccountId;
	}
	
	public String addSendAccount() {
	    logger.info( "Adding account: " + getSendAccountId());
		Account otherAccount = getAccountBean().findAccount(getSendAccountId());
		if (otherAccount==null) {
			FacesContext.getCurrentInstance().addMessage("exchangeSend:sendAccount", new FacesMessage("Invalid Account"));
			return "fail";
		}
		AccountExchange aes = getAccountBean().newAccountExchange(getAccountUser().getAccount(), otherAccount, AccountExchange.Direction.SEND);
    	aes.setEffectiveTime(getNow());
    	accountExchangeSend = null;
    	accountExchangeSendModel = null;
		return "success";
	}

	public String addReceiveAccount() {
		logger.info( "Adding account: " + getReceiveAccountId());
		Account otherAccount = getAccountBean().findAccount(getReceiveAccountId());
		if (otherAccount==null) {
			FacesContext.getCurrentInstance().addMessage("exchangeReceive:receiveAccount", new FacesMessage("Invalid Account"));
			return "fail";
		}
		AccountExchange aer = getAccountBean().newAccountExchange(getAccountUser().getAccount(), otherAccount, AccountExchange.Direction.RECEIVE);
    	aer.setEffectiveTime(getNow());
    	accountExchangeReceive = null;
    	accountExchangeReceiveModel = null;
		return "success";
	}

	/**
	 * Return a list of all roles supported by this account
	 * @return List of accountRoles
	 */
	public List<AccountRole> getAccountRoles() {
		Set<AccountRole> roleSet = getAccountUser().getAccount().getAccountRoles();
		List<AccountRole> roles = new ArrayList<AccountRole>(roleSet.size());
		roles.addAll(roleSet);
		return roles;
	}

	/**
	 * Return a list of all roles supported by this account
	 * @return List of accountRoles
	 */
	public List<SelectItem> getAccountRoleItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (AccountRole accountRole : getAccountRoles()) {
			items.add(new SelectItem( accountRole.getRole()));
		}
		return items;
	}

	public AccountRole getNewAccountRole() {
		if (newAccountRole==null) {
			newAccountRole = new AccountRole();
			newAccountRole.setAccount(getAccountUser().getAccount());
			newAccountRole.setId(0);
		}
		return newAccountRole;
	}
	
	/**
	 * Select an existing account role
	 * @return
	 */
	public String selectAccountRole( ) {
		String accountRoleIdStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("accountRoleId");
//		logger.info( "SelectAccountRole: " + accountRoleIdStr);
		long accountRoleId = Long.parseLong(accountRoleIdStr);
		for (AccountRole accountRole : getAccountUser().getAccount().getAccountRoles()) {
			if (accountRoleId == accountRole.getId()) {
				newAccountRole = new AccountRole();
				newAccountRole.setAccount(getAccountUser().getAccount());
				newAccountRole.setId(accountRole.getId());
				newAccountRole.setRole(accountRole.getRole());
				newAccountRole.setTitle(accountRole.getTitle());
				return "success";
			}
		}
		return "error";
	}

	/**
	 * Update (or add) an account role
	 * @return
	 */
	public String updateAccountRole( ) {
		long accountRoleId = getNewAccountRole().getId();
		if (accountRoleId==0) {
		    getAccountUser().getAccount().getAccountRoles().add(getNewAccountRole());
			return "success";
		} else {
			for (AccountRole accountRole : getAccountUser().getAccount().getAccountRoles()) {
				if (accountRoleId == accountRole.getId()) {
					accountRole.setTitle(getNewAccountRole().getTitle());
					return "success";
				}
			}
		}
		return "error";
	}
	
	/**
	 * Select an AccountUser for editing
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public String selectAccountUser( ) {
		String accountUserIdStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedAccountUserId");
//		logger.info( "SelectAccountUser: " + accountUserIdStr);
		long accountUserId = Long.parseLong(accountUserIdStr);
		AccountUser accountUser = getActivationBean().findAccountUser(accountUserId);
		try {
            selectedAccountUser = (AccountUser)accountUser.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Could not clone accountUser: " + accountUser.getId(), ex);
        }
		return "success";
	}
	
	public String updateAccountUser( ) {
		AccountUser sau = getSelectedAccountUser();
		AccountUser accountUser = getActivationBean().findAccountUser(sau.getId());
		for (AccountUserRole aur : accountUser.getRoles()) {
			aur.getId();
			logger.info( "Existing role: " + aur.getRole());
		}
		if (sau.getId()!=getAccountUser().getId()) {
			accountUser.setAccountPermission(sau.isAccountPermission());
			accountUser.setStatus(sau.getStatus());
		}
//		accountUser.setEffectiveDate(sau.getEffectiveDate());
		accountUser.setExpirationDate(sau.getExpirationDate());
		accountUser.setOpenMeFirst(sau.getOpenMeFirst());
		Set<String> selectedRoles = new HashSet<String>();
		// Add anything selected, but not currently in the role list, to the role list
		for (String role : getSelectedAccountUserRoleItems()) {
			logger.info( "Checking for add: " + role);
			AccountUserRole aur = new AccountUserRole();
			aur.setAccountUser(accountUser);
			aur.setRole(role);
			if (!accountUser.getRoles().contains(aur)) {
//				logger.info( "Add role: " + aur.getRole());
				accountUser.getRoles().add(aur);
			}
			selectedRoles.add(role);
		}
		List<AccountUserRole> removeList = new ArrayList<AccountUserRole>();
		// Remove anything in the role list that isn't selected.
		for (AccountUserRole aur : accountUser.getRoles()) {
//			logger.info( "Checking for removal: " + aur.getRole());
			if (!selectedRoles.contains(aur.getRole())) {
//				logger.info( "Removing: " + aur.getRole());
//				aur.setAccountUser(null);
				removeList.add(aur);
			}
		}
//		logger.info( "Removed: " + removeList);
		getActivationBean().removeAccountUserRoles( removeList);
		accountUser.getRoles().removeAll(removeList);
//		logger.info( "What's left: " + accountUser.getRoles());
		for (AccountUserRole aur : removeList) {
			
		}
		return "success";
	}
	
	public AccountUser getSelectedAccountUser() {
		if (selectedAccountUser==null) selectedAccountUser = new AccountUser();
		return selectedAccountUser;
	}

	public void setSelectedAccountUser(AccountUser selectedAccountUser) {
		this.selectedAccountUser = selectedAccountUser;
	}

	public List<String> getSelectedAccountUserRoleItems() {
		if (selectedAccountUserRoleItems==null) {
			AccountUser accountUser = getSelectedAccountUser();
			selectedAccountUserRoleItems = new ArrayList<String>();
			for (AccountUserRole aur : accountUser.getRoleList() ) {
				selectedAccountUserRoleItems.add(aur.getRole());
			}
		}
		return selectedAccountUserRoleItems;
	}


	public void setSelectedAccountUserRoleItems(
			List<String> selectedAccountUserRoleItems) {
		this.selectedAccountUserRoleItems = selectedAccountUserRoleItems;
	}

}
