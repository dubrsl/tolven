package org.tolven.process;

import java.util.List;

import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.TrimHeader;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActRelationshipDirection;
import org.tolven.trim.ActRelationshipType;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;
/**
 * Find selected items to include in a share message to another account. This is called from trim "compute".
 * @author John Churin
 */
public class Harvest extends ComputeBase {
	private MenuData mdPlaceholder;
	private String pathContext;
	private String path;
	private boolean enabled;
	private String title;
	private String relationship;
	private String participation;
	private String template;

	private static final TrimFactory trimFactory = new TrimFactory();

	public Harvest() {
		super();
	}
	
	/**
	 * Paths can start with : which means the path assumes knownType is prepended.
	 * @param path
	 * @return
	 */
	public String buildFullPath( String path ) {
		if (path==null || path.length() == 0 || ':'==path.charAt(0)) {
			return getAccountUser().getAccount().getAccountType().getKnownType() + path;
		}
		return path;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<MenuData> getUnsharedMenuData() throws Exception{
		MenuQueryControl ctrl = new MenuQueryControl();
    	MenuPath pathContext = null;
    	if (this.getPathContext() != null)
    	    pathContext = new MenuPath( this.getPathContext() );
    	String menuStructurePath = buildFullPath(this.getPath());
    	MenuPath requestPath = null;
    	if (pathContext != null)
		   requestPath = new MenuPath( menuStructurePath, pathContext);
    	else
    	   requestPath = new MenuPath( menuStructurePath );
		ctrl.setAccountUser(getAccountUser());
		ctrl.setMenuStructurePath(menuStructurePath);
		ctrl.setRequestedPath( requestPath );
		ctrl.setNow( getNow());
		ctrl.setAccountUser(getAccountUser());
		List<MenuData> rslt =  getMenuBean().findMenuData( ctrl );
		return rslt;
	}
	
	/**
	 * Find a trim and get it's act. It will pull information from the placeholder.
	 * @param placeholder
	 * @return
	 * @throws JAXBException
	 */
	public Act parseTemplate(MenuData placeholder ) throws JAXBException {
		String context = placeholder.getPath();
		TrimHeader trimHeader = getTrimBean().findTrimHeader(getTemplate());
		TrimEx templateTrim = getTrimBean().parseTrim(trimHeader.getTrim(), getAccountUser(), context, getNow(), null );
		return templateTrim.getAct();
	}

	public ActRelationship createActRelationship(Act act ) {
		ActRelationship ar = trimFactory.createActRelationship();
		ar.setTypeCode(ActRelationshipType.COMP);
		ar.setDirection(ActRelationshipDirection.OUT);
		ar.setName(getRelationship());
		ar.setAct(act);
		return ar;
	}
	
	public void compute( ) throws Exception {
		TolvenLogger.info( "Compute", this.getClass());
		super.checkProperties();
		if (this.isEnabled()) {
			removeRelationships(getRelationship());			
			List<MenuData> items = getUnsharedMenuData( );
			for (MenuData item : items) {
				MenuData placeholder = item.getReference();
				Act newAct = parseTemplate(placeholder);
				ActRelationship ar = createActRelationship( newAct );
				getAct().getRelationships().add(ar);
			}
		}
		// Disable the Compute since its job is done.
    	for (Property property : getComputeElement().getProperties()) {
			if (property.getName().equals("enabled")) {
				property.setValue(Boolean.FALSE);
				break;
			}
		} 
	}
	
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public MenuData getMdPlaceholder() {
		return mdPlaceholder;
	}

	public void setMdPlaceholder(MenuData mdPlaceholder) {
		this.mdPlaceholder = mdPlaceholder;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getParticipation() {
		return participation;
	}

	public void setParticipation(String participation) {
		this.participation = participation;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPathContext() {
		return pathContext;
	}

	public void setPathContext(String pathContext) {
		this.pathContext = pathContext;
	}

}
