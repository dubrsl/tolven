package org.tolven.trim.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.trim.Act;
import org.tolven.trim.BindTo;
import org.tolven.trim.Entity;
import org.tolven.trim.InfrastructureRoot;
import org.tolven.trim.PlaceholderBind;
import org.tolven.trim.Role;
import org.tolven.trim.SETIISlot;
import org.tolven.trim.ex.SETIISlotEx;
/**
* Abstract Trim scanner that handles the basics of placeholder binding.
* The binding is done in two phases. The first phase just collects a list of nodes in the
* trim that need binding. We sort the list by location and then do the binding. This allows
* local context to be applied. 
* For example, echr:patient should be bound before echr:patient:encounter, etc. 
 * @author John Churin
 *
 */
public abstract class BindScanner extends Scanner {

	private List<MenuData> placeholders;
	private List<MenuData> changedPlaceholders;
	private Map<String, Long> placeholderContext = new HashMap<String, Long>(10);
	private Account account;
	private Date now;
	private long documentId;
	private MenuLocal menuBean;
	private List<BindRequest> requests = new ArrayList<BindRequest>(10);

	@Override
	public void scan() {
		// Collect placeholder binding requests
		super.scan();
		// Sort the requests by path name
		Collections.sort(requests);
		// We'll collect the finished placeholder
		placeholders = new ArrayList<MenuData>( 10 );
		changedPlaceholders = new ArrayList<MenuData>( 2 );
		// Now do the binding
		for (BindRequest request : requests ) {
			MenuData md = bindToPlaceholder(request);
			if (md!=null) {
				placeholders.add(md);
				// Add this placeholder to context (or replace context)
				placeholderContext.put(md.getMenuStructure().getNode(), md.getId());
			}
		}
	}
	
	protected class BindRequest implements Comparable<BindRequest> {
		String location;
		InfrastructureRoot rimObject; 
		PlaceholderBind placeholderBind;
		
		public BindRequest( String location, InfrastructureRoot rimObject, PlaceholderBind placeholderBind ) {
			this.location = location;
			this.rimObject = rimObject;
			this.placeholderBind = placeholderBind;
		}
		
		/**
		 * Sort by the Path length (number of nodes) and then by path name specified in the bind request.
		 * Therefore, echr:diagnosis should sort before echr:patient:allergy
		 */
		@Override
		public int compareTo(BindRequest arg0) {
			MenuPath myPath = new MenuPath(placeholderBind.getPath());
			MenuPath otherPath = new MenuPath(arg0.placeholderBind.getPath());
			if (myPath.getNodeValues().size() < otherPath.getNodeValues().size()) return -1;
			if (myPath.getNodeValues().size() > otherPath.getNodeValues().size()) return 1;
			return myPath.getPathString().compareTo(otherPath.getPathString());
		}

		@Override
		public boolean equals(Object arg0) {
			MenuPath myPath = new MenuPath(placeholderBind.getPath());
			MenuPath otherPath = new MenuPath(((BindRequest)arg0).placeholderBind.getPath());
			if (myPath.getNodeValues().size() != otherPath.getNodeValues().size()) return false;
			return myPath.getPathString().equals(otherPath.getPathString());
		}
	}
	
    /**
     * Find the id that is internal to this account, if any, and return the corresponding placeholder.
     * @param idSlot
     * @return Placeholder or null
     */
    protected MenuData findInternalPlaceholder( SETIISlot idSlot ) {
    	String path = ELFunctions.internalId(account, idSlot);
    	if ( path == null ) return null;
    	MenuData mdPlaceholder = menuBean.findMenuDataItem(account.getId(), path);
    	return mdPlaceholder;
    }

    @Override
	protected void processBindTo(InfrastructureRoot rimObject, BindTo bindTo) {
		super.processBindTo(rimObject, bindTo);
		// Get the type of placeholder we need
		PlaceholderBind placeholderBind = bindTo.getPlaceholder();
		// If no placeholder bind, forget it
		if (placeholderBind!=null) {
			BindRequest request = new BindRequest(getLocation(), rimObject, placeholderBind);
			requests.add(request);
		}
	}

	protected static String computeVariable( InfrastructureRoot rimObject ) {
		if (rimObject instanceof Act) return "act";
		if (rimObject instanceof Role) return "role";
		if (rimObject instanceof Entity) return "entity";
		return null;
	}
	
	protected static SETIISlotEx computeSlot( InfrastructureRoot rimObject ) {
		SETIISlotEx slot = null;
		if (rimObject instanceof Act) {
			Act act = (Act)rimObject;
			if (act.getId()==null) {
				act.setId(trimFactory.createSETIISlot());
			}
			slot = (SETIISlotEx) act.getId();
		} else if (rimObject instanceof Role) {
			Role role = (Role)rimObject;
			if (role.getId()==null) {
				role.setId(trimFactory.createSETIISlot());
			}
			slot = (SETIISlotEx) role.getId();
		} else if (rimObject instanceof Entity) {
			Entity entity = (Entity)rimObject;
			if (entity.getId()==null) {
				entity.setId(trimFactory.createSETIISlot());
			}
			slot = (SETIISlotEx) entity.getId();
		}
		return slot;
	}
	
	/**
	 * Given a list of MenuPaths, add them to placeholder we know about
	 * @param menuContext
	 */
	public void addMenuContext(List<MenuPath> menuContext) {
		if (menuContext!=null) {
			for (MenuPath contextPath : menuContext) {
				placeholderContext.putAll(contextPath.getNodeValues());
			}
		}
	}

	/**
	 * Given a MenuPath, add it to the list of placeholder we know about
	 * @param menuContext
	 */
	public void addMenuContext(MenuPath menuContext) {
		if (menuContext!=null) {
			placeholderContext.putAll(menuContext.getNodeValues());
		}
	}
	public void addContextVariables(String name, Long variable) {
		placeholderContext.put(name,variable);
	}
	/**
	 * We have a bind-to-placeholder:
	 * Try to bind this rim object to an existing placeholder. 
	 * This can occur in one of two ways:
	 * a) An ID is already specified in the object,
	 * b) The bind request can be found in the current context. 
	 * See MatchContext for an explanation.
	 * Or, if the placeholder is not found, then we may need to create a new placeholder depending on
	 * instructions provided in the binding request.
	 * 
	 * @return The created or found placeholder, or null if none is applicable
	 */
	public abstract MenuData bindToPlaceholder( BindRequest request );
	

	public List<MenuData> getPlaceholders() {
		return placeholders;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}
	
	public MenuLocal getMenuBean() {
		return menuBean;
	}

	public void setMenuBean(MenuLocal menuBean) {
		this.menuBean = menuBean;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public Map<String, Long> getPlaceholderContext() {
		return placeholderContext;
	}

	public List<MenuData> getChangedPlaceholders() {
		return changedPlaceholders;
	}
	

	
}
