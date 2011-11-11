package org.tolven.process;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.doc.DocumentLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.el.ELFunctions;
import org.tolven.core.entity.AccountUser;
import org.tolven.doctype.DocumentType;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.security.DocProtectionLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Compute;
import org.tolven.trim.Entity;
import org.tolven.trim.II;
import org.tolven.trim.InfrastructureRoot;
import org.tolven.trim.Role;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;

public abstract class ComputeBase {
    private InitialContext ctx;
	private TrimLocal trimBean;
	public MenuLocal menuBean;
	private DocumentLocal documentBean;
	private DocProtectionLocal docProtectionBean;
	private TolvenPropertiesLocal propertyBean;	
	private Date now;
	private AccountUser accountUser;
	private Compute computeElement;
	private List<MenuPath> contextList; 
	private String location;
	private ResourceBundle globalBundle;
	
	
	/**
	 * Result of Validation Messages.
	 */
	private List<String> validationMessages = new ArrayList<String>();
	
	private static final TrimFactory trimFactory = new TrimFactory();
	
	private DocumentType documentType;

	/**
	 * The trim that we're working
	 */
	private Trim trim;
	/**
	 * The current act being visited
	 */
	private InfrastructureRoot node;

	public ComputeBase() {
		
	}
	public void checkProperties() {
		if (trim==null) throw new IllegalStateException( "Missing TRIM");
		if (node==null) throw new IllegalStateException( "Missing Node (Act, Role, or Entity) specified");
	}
	
	public abstract void compute() throws Exception; 

	public List<ActRelationship> findRelationships(String relationship) {
		List<ActRelationship> arList = new ArrayList<ActRelationship>(); 
		// Make a list of the nodes to remove.
		for (ActRelationship ar : getAct().getRelationships()) {
			if (relationship.equals(ar.getName())) {
				arList.add(ar);
			}
		}
		return arList;
	}
	
	/**
	 * Simply remove matching relationships from current act in trim
	 * @param relationship
	 */
	public void removeRelationships(String relationship) {
		List<ActRelationship> arRemoveList = findRelationships( relationship); 
		getAct().getRelationships().removeAll(arRemoveList);
	}
	
	/**
	 * Clean out the current relationships since we will be replacing them (potentially)
	 * However, we don't actually remove the IDs yet, instead we put them in a hold area.
	 */
	public void cleanRelationships(String relationship) {
		List<ActRelationship> arRemoveList = findRelationships( relationship); 
 		String idRoot = ELFunctions.computeIDRoot(getAccountUser().getAccount());
		// Add embedded IDs that we own to free list
		for (ActRelationship ar : arRemoveList) {
			for( II ii : ar.getAct().getId().getIIS()) {
				if (idRoot.equals(ii.getRoot())) {
					if (getTrim().getUnused()==null) {
						getTrim().setUnused( trimFactory.createUnused());
					}
					getTrim().getUnused().getIIS().add(ii);
				}
			}
		}
		// Now remove the ARs
		getAct().getRelationships().removeAll(arRemoveList);
	}
	
	/**
	 * Get the TrimBean from which we were called (this is the bean itself, not a proxy).
	 * @return
	 */
	public TrimLocal getTrimBean() {
		return trimBean;
	}

	public void setTrimBean(TrimLocal trimBean) {
		this.trimBean = trimBean;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public AccountUser getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
	}

	public Trim getTrim() {
		return trim;
	}

	public void setTrim(Trim trim) {
		this.trim = trim;
	}

	public Act getAct() {
		if (getNode() instanceof Act) {
			return (Act) getNode();
		}
		return null;
	}

	public Role getRole() {
		if (getNode() instanceof Role) {
			return (Role) getNode();
		}
		return null;
	}

	public Entity getEntity() {
		if (getNode() instanceof Entity) {
			return (Entity) getNode();
		}
		return null;
	}

	public InitialContext getCtx() throws NamingException {
    	if (ctx==null) {
    		ctx = new InitialContext();
    	}
    	return ctx;
	}

	public void setCtx(InitialContext ctx) {
		this.ctx = ctx;
	}
	public InfrastructureRoot getNode() {
		return node;
	}
	public void setNode(InfrastructureRoot node) {
		this.node = node;
	}
	public Compute getComputeElement() {
		return computeElement;
	}
	public void setComputeElement(Compute computeElement) {
		this.computeElement = computeElement;
	}
	
	public List<MenuPath> getContextList() {
		if (contextList==null) {
			contextList = new ArrayList<MenuPath>();
		}
		return contextList;
	}
	
	public void setContextList(List<MenuPath> contextList) {
		this.contextList = contextList;
	}
	public void setDocProtectionBean(DocProtectionLocal documentBean) {
		this.docProtectionBean = documentBean;
	}
	public DocProtectionLocal getDocProtectionBean() {
		return docProtectionBean;
	}

	public void setDocumentBean(DocumentLocal documentBean) {
		this.documentBean = documentBean;
	}
	public DocumentLocal getDocumentBean() {
		return documentBean;
	}
	public TolvenPropertiesLocal getPropertyBean() {
		return propertyBean;
	}
	public void setPropertyBean(TolvenPropertiesLocal propertyBean) {
		this.propertyBean = propertyBean;
	}	
	
	public void setMenuBean(MenuLocal menuBean) {
		this.menuBean = menuBean;
	}
	public MenuLocal getMenuBean() {
		return menuBean;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public void addValidationMessage(String aMessage) {
		this.validationMessages.add(aMessage);
	}
	public List<String> getValidationMessages() {
		return this.validationMessages;
	}

	public void setGlobalBundle(ResourceBundle aBundle) {
		this.globalBundle = aBundle;
	}

	public ResourceBundle getGlobalBundle() {
		if (globalBundle==null) {
	    	AccountUser accountUser = getAccountUser();
	    	String accountType = accountUser.getAccount().getAccountType().getKnownType();
	    	String appGlobalBundleName = ResourceBundleHelper.getAppGlobalBundleName(accountType);
            Locale locale = ResourceBundleHelper.getLocale(accountUser.getLocale());
	    	globalBundle = ResourceBundle.getBundle(appGlobalBundleName, locale);
		}
		return this.globalBundle;
	}

	protected void buildMessage(String messageKey, String... substitueParams) {
		String messagePattern = ResourceBundleHelper.getString(getGlobalBundle(), messageKey);
		MessageFormat formatter = new MessageFormat(messagePattern, getGlobalBundle().getLocale());
		String formattedString = formatter.format(substitueParams);
		getValidationMessages().add(formattedString);
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
}
