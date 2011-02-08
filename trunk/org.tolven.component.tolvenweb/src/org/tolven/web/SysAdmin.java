package org.tolven.web;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.Status;
import org.tolven.logging.TolvenLogger;

/**
 * SystAdmin - allow users to enter static data such as AccountTypes, needed
 * to support Account and MenuStructure organization.  Any user who can log in
 * to the application has access to sys admin functions
 * @author person1
 *
 */
public class SysAdmin extends TolvenAction {

    
	private DataModel accountTypesModel;
	private AccountType currentAccountType;
	private String knownType;
	private String homePage;
	private String longDesc;
	private String status = Status.ACTIVE.value();
	private boolean readOnly = false;
	private boolean checkbox;
	protected String [] changestatus = new String[0];
	private List<SelectItem> accTypes;
	
	/** Creates a new instance of RegisterAction */
    public SysAdmin() {
    }


    public DataModel getAccountTypesModel() throws NamingException {
    	if (this.accountTypesModel==null) {
    		accountTypesModel = new ListDataModel();
    		accountTypesModel.setWrappedData(getAccountBean().findAllAccountTypes());
    	}
		return accountTypesModel;
    }

    /**
     * find out how many accountTypes exist
     * @return int 
     */
	public int getAccountTypeCount() throws NamingException {
		return getAccountTypesModel().getRowCount();
	}
	
	public List<SelectItem> getAccTypes() throws NamingException {
		if (accTypes== null){
			List<AccountType>accountTypes = getAccountBean().findAllAccountTypes();
			Iterator<AccountType> iter = accountTypes.iterator();
			if (accountTypes.isEmpty() == false){
				int count = accountTypes.size();
				accTypes = new ArrayList<SelectItem>( count );
				for (int x=0; x < count; x++){
					AccountType aty = (AccountType)iter.next();
					accTypes.add(new SelectItem( Long.toString(aty.getId()), aty.getStatus() ));
				}
			} else { TolvenLogger.info("No account types defined", SysAdmin.class); }
		}
		 return accTypes;
	}
	
	public void setChangestatus(String changestatus[] ){
		this.changestatus = changestatus;
	}
	public String[] getChangestatus(){
		return this.changestatus;
	}
	
	public boolean isCheckbox(){
		return this.checkbox;
	}
	public void setCheckbox(boolean value){
		this.checkbox = value;
	}
	public String getKnownType() {
		return knownType;
	}
	public void setKnownType(String knownType) {
		this.knownType = knownType;
	}
	public String getHomePage() {
		return homePage;
	}
	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
	public String getLongDesc() {
		return longDesc;
	}
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	public boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	/**
	 * Update the current AccountType
	 *
	 */
    public String updateAccountType() throws Exception {
    	TolvenLogger.info( "Updating AccountType ID: " + getCurrentAccountType().getId(), SysAdmin.class);
		getAccountBean().updateAccountType( getCurrentAccountType() );
		return "success";
    }

    /**
     * User selected a specific row, select the account type at that row so it can be edited if needed.
     * @return
     */
    public String selectAccountType( ) {
    	AccountType accountType = (AccountType) accountTypesModel.getRowData();
    	if (accountType==null) {
    		return "error";
    	}
    	setCurrentAccountType( accountType );
    	return "success";
    }
    /**
     * Add a new AccountType. The knownType must not already exist as an active AccountType.
     * @return outcome used by faces-config.xml
     * @throws Exception
     */
    public String addAccountType() throws Exception {
    	AccountType accountType = getAccountBean().findAccountTypebyKnownType( getKnownType());
    	if (accountType!=null ) {
    		FacesContext.getCurrentInstance().addMessage( null, new FacesMessage("Known type already exists"));
			return "fail";
    	} else {
			AccountType newAccountType = getAccountBean().createAccountType(
				this.getKnownType(),this.getHomePage(),this.getLongDesc(),
				this.getReadOnly(), this.getStatus(), true );
			if (newAccountType == null) {
				TolvenLogger.info("AccountType not created", SysAdmin.class);
				return "fail";
			} else {
				// Clear the accountTypes list so we requery to get the current state after this change
				accountTypesModel = null;
				// Tell faces we were successful and to go to the next page.
				return "success";
	    	}
    	}
    }

    public String getStatus() {
		return status;
	}

    public void setStatus(String status) {
		this.status = status;
	}

	public AccountType getCurrentAccountType() {
		if (currentAccountType==null) currentAccountType = new AccountType();
		return currentAccountType;
	}

	public void setCurrentAccountType(AccountType currentAccountType) {
		this.currentAccountType = currentAccountType;
	}

}
