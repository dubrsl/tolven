package org.tolven.trim.scan;

import java.beans.Beans;
import java.beans.Expression;
import java.beans.PropertyDescriptor;
import java.beans.Statement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.security.DocProtectionLocal;
import org.tolven.trim.Act;
import org.tolven.trim.Compute;
import org.tolven.trim.Entity;
import org.tolven.trim.InfrastructureRoot;
import org.tolven.trim.Role;
import org.tolven.trim.Trim;
/**
 * General-purpose mechanism to invoke computations on a trim document prior to submission.
 * Each method may be called more than once and so should be careful to clean up previous runs.
 * The arguments passed to the specified class are dom Node objects so the handler will need to call 
 * JAXB unmarshall to get an object graph (or just navigate the generic nodes).
 * Once instantiated for a specific trim, a single instance of the object specified by type is used.
 * Thus, the instantiated class can retain state between nodes of the trim visited with that type.
 * @param act
 * @throws Exception
 * 
 * @author John Churin
 */
public class ComputeScanner extends Scanner {
	private AccountUser accountUser;
	private Date now;
	private List<MenuPath> menuContext;
	
	private MenuLocal menuBean;
	private TrimLocal trimBean;
	private DocumentLocal documentBean;
	private TolvenPropertiesLocal propertyBean;
	private DocProtectionLocal docProtectionBean;
	private Map<String, Object> computeInstances;

	
	private ResourceBundle globalBundle;
	private List<String> validationMessages = new ArrayList<String>();
	
	/**
	 * Compute objects are instantiated once for a trim, even if the compute occurs in multiple nodes of the
	 * trim graph.
	 */
	@Override
	protected void preProcessTrim(Trim trim) {
		super.preProcessTrim(trim);
		computeInstances = new HashMap<String, Object>(10);
	}

	@Override
	protected void preProcessAct(Act act) {
		super.preProcessAct(act);
		invokeComputes( act, act.getComputes() );
	}

	@Override
	protected void preProcessEntity(Entity entity) {
		super.preProcessEntity(entity);
		invokeComputes( entity, entity.getComputes() );
	}

	@Override
	protected void preProcessRole(Role role) {
		super.preProcessRole(role);
		invokeComputes( role, role.getComputes() );
	}
	
	/**
	 * Invoke the computes but only if path is enabled
	 * @param node
	 * @param computes
	 */
	public void invokeComputes( InfrastructureRoot node, List<Compute> computes ) {
		if (isPathEnabled()) {
			try {
				Statement statement = null;
				for (Compute compute : computes) {
					// If this compute is not for the current trim name, ignore it
					if (compute.getForTrimName()!=null && !(compute.getForTrimName().equals(getTrim().getName()) )) continue;
					if (compute.getForAccountType()!=null && !(compute.getForAccountType().equals(getAccountUser().getAccount().getAccountType().getKnownType()) )) continue;
					// If we already have an instance, use it, otherwise, create one
					Object target = computeInstances.get(compute.getType());
					if (target==null) {
						target = Beans.instantiate(this.getClass().getClassLoader(), compute.getType());
						computeInstances.put(compute.getType(), target);
					}
					statement = new Statement(target, "setTrim", new Object[]{getTrim()});
					statement.execute();
					statement = new Statement(target, "setContextList", new Object[]{getMenuContext()});
					statement.execute();
					statement = new Statement(target, "setTrimBean", new Object[]{getTrimBean()});
					statement.execute();
					statement = new Statement(target, "setMenuBean", new Object[]{getMenuBean()});
					statement.execute();
					statement = new Statement(target, "setDocumentBean", new Object[]{getDocumentBean()});
					statement.execute();
					statement = new Statement(target, "setDocProtectionBean", new Object[]{getDocProtectionBean()});
					statement.execute();
					statement = new Statement(target, "setPropertyBean", new Object[]{getPropertyBean()});
					statement.execute();										
					statement = new Statement(target, "setNow", new Object[]{getNow()});
					statement.execute();
					statement = new Statement(target, "setComputeElement", new Object[]{compute});
					statement.execute();
					statement = new Statement(target, "setAccountUser", new Object[]{getAccountUser()});
					statement.execute();
					statement = new Statement(target, "setLocation", new Object[]{getLocation()});
					statement.execute();
					statement = new Statement(target, "setNode", new Object[]{node});
					statement.execute();
					statement = new Statement(target, "setDocumentType", new Object[]{getDocumentType()});
					statement.execute();
					
					// Pass any properties specified in the XML to the compute class
					// meaning the class must have appropriate set methods.
					for (Compute.Property property : compute.getProperties()) {
						PropertyDescriptor desc = new PropertyDescriptor(property.getName(), target.getClass());
						Method method = desc.getWriteMethod();
						Object[] arg;
						Class<?> t = desc.getPropertyType();
						if ( "boolean".equals(t.getName()) ) {
							boolean boolValue = (Boolean)property.getValue();
							arg = new Object[]{boolValue};
						} else {
							arg = new Object[]{property.getValue()};
						}
						method.invoke(target, arg);
					}
					// Pass any attributes specified in the XML to the compute class
					// meaning the class must have appropriate set methods.
					for (Compute.Attribute attribute : compute.getAttributes()) {
						PropertyDescriptor property = new PropertyDescriptor(attribute.getName(), target.getClass());
						Method method = property.getWriteMethod();
						method.invoke(target, new Object[]{attribute.getAny()});
					}
				  
				   statement = new Statement( target, "compute", null );
				   statement.execute();
				   
				   Expression expression = new Expression(target, "getValidationMessages", null);
				   expression.execute();
				   getValidationMessages().addAll((List<String>)expression.getValue());
				}
			} catch (Exception e) {
				throw new RuntimeException("Error processing compute from " + getLocation(), e);
			}
		}
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public List<MenuPath> getMenuContext() {
		return menuContext;
	}

	public void setMenuContext(List<MenuPath> menuContext) {
		this.menuContext = menuContext;
	}

	public MenuLocal getMenuBean() {
		return menuBean;
	}

	public void setMenuBean(MenuLocal menuBean) {
		this.menuBean = menuBean;
	}

	public TrimLocal getTrimBean() {
		return trimBean;
	}

	public void setDocumentBean(DocumentLocal documentBean) {
		this.documentBean = documentBean;
	}

	public DocumentLocal getDocumentBean() {
		return documentBean;
	}

	public void setDocProtectionBean(DocProtectionLocal docProtectionBean) {
		this.docProtectionBean = docProtectionBean;
	}

	public DocProtectionLocal getDocProtectionBean() {
		return docProtectionBean;
	}

	public void setPropertyBean(TolvenPropertiesLocal propertyBean) {
		this.propertyBean = propertyBean;
	}

	public TolvenPropertiesLocal getPropertyBean() {
		return propertyBean;
	}
	
	public void setTrimBean(TrimLocal trimBean) {
		this.trimBean = trimBean;
	}

	public AccountUser getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
		setGlobalBundle(accountUser);
	}
	
	public void setGlobalBundle(ResourceBundle globalBundle) {
		this.globalBundle = globalBundle;
	}
	
    public void setGlobalBundle(AccountUser accountUser) {
        String accountType = accountUser.getAccount().getAccountType().getKnownType();
        String appGlobalBundleName = ResourceBundleHelper.getAppGlobalBundleName(accountType);
        Locale locale = ResourceBundleHelper.getLocale(accountUser.getUser().getLocale(), accountUser.getAccount().getLocale());
        this.globalBundle = ResourceBundle.getBundle(appGlobalBundleName, locale);
    }
    
	public ResourceBundle getGlobalBundle(){
		return this.globalBundle;
	}
	
	public void setValidationMessages(List<String> messages) {
		this.validationMessages = messages;
	}
	public List<String> getValidationMessages() {
		return this.validationMessages;
	}
}
