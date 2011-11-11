package org.tolven.trim.scan;

import org.tolven.app.TrimLocal;
import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.core.entity.Status;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.BindAction;
import org.tolven.trim.CE;
import org.tolven.trim.CESlot;
import org.tolven.trim.ConcreteDatatype;
import org.tolven.trim.GTSSlot;
import org.tolven.trim.II;
import org.tolven.trim.InfrastructureRoot;
import org.tolven.trim.NewFacet;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ex.CESlotEx;
import org.tolven.trim.ex.GTSSlotEx;
import org.tolven.trim.ex.SETIISlotEx;
/**
 * Abstract Trim scanner used by the creation process.
 * @author John Churin
 *
 */
public class CreateScanner extends BindScanner {

	private TrimLocal trimBean;
	private MenuData menuDataSource;

    /**
     * Use this method to populate the parents of this menu data item based on available context.
     * For example, if we are asked to create echr:patient:encounter, the encounter will require
     * a parent of patient because the parent of the the encounter placeholder is the patient placeholder.
     * @param context
     */
    public void populateParents( MenuData md ) {
    	MenuData mdPlaceholder = md;
    	if (mdPlaceholder.getMenuStructure()==null) throw new IllegalStateException( "MenuStructure is null in MenuData");
		MenuStructure ms = mdPlaceholder.getMenuStructure().getParent();
		while ( ms != null ) {
			if (MenuStructure.PLACEHOLDER.equals(ms.getRole())) {
				Long parentId = getPlaceholderContext().get(ms.getNode());
				if (parentId==null) throw new RuntimeException( "Missing context for placeholder: " + ms.getNode() );
				MenuData mdParent = getMenuBean().findMenuDataItem(parentId);
				mdPlaceholder.setParent01(mdParent);
				mdPlaceholder = mdParent;
			}
			ms = ms.getParent();
		}
    }

	/**
	 * Create a new MenuData placeholder
	 * @return
	 */
	protected MenuData createNewPlaceholder( MenuStructure msInstance, String variable, InfrastructureRoot rimObject ) {
		MenuData mdPlaceholder = new MenuData();
		mdPlaceholder.setMenuStructure(msInstance.getAccountMenuStructure());
		mdPlaceholder.setAccount(msInstance.getAccount());
		populateParents( mdPlaceholder );
//		Map<String, Object> sourceMap = new HashMap<String, Object>(7);
//		sourceMap.put("account", getAccount());
//		sourceMap.put("trim", getTrim());
//		sourceMap.put("now", getNow());
//		sourceMap.put(variable, rimObject);
//		getMenuBean().populateMenuData( sourceMap, mdPlaceholder );
		mdPlaceholder.setStatus( Status.NEW );
		if (rimObject instanceof Act) {
			Act act = (Act)rimObject;
			if (act.getStatusCode()!=null) {
				mdPlaceholder.setActStatus(act.getStatusCode().value());
			}
		}
		getMenuBean().persistMenuData(mdPlaceholder);
		return mdPlaceholder;
	}

	/**
	 * We have a bind-to-placeholder:
	 * Try to bind this rim object to an existing placeholder. 
	 * This can occur in one of two ways:
	 * a) An ID is already specified in the object,
	 * b) The bind request can be found in the current context. 
	 * Or, if the placeholder is not found, then we may need to create a new placeholder depending on
	 * instructions provided in the binding request.
	 * 
	 * @return The created or found placeholder, or null if none is applicable
	 */
	public MenuData bindToPlaceholder( BindRequest request  ) {
		TolvenLogger.info( "Bind to placeholder at: " + request.location, CreateScanner.class);
		MenuData md = null;
		// Get the id slot setup for our work
		SETIISlotEx slot = computeSlot( request.rimObject );
		// This is the root of the ID we're looking for
		String idRoot = ELFunctions.computeIDRoot( getAccount() );
		// Look for an existing placeholder for this account
		II ii = slot.findII( idRoot );
		MenuStructure msInstance = getMenuBean().findMenuStructure(getAccount().getId(), request.placeholderBind.getPath());
		// If we already have a placeholder, nothing further to do
		if (ii!=null && ii.getExtension()!=null && ii.getExtension().length() > 0) {
			md = getMenuBean().findMenuDataItem(getAccount().getId(), ii.getExtension());
			this.getPlaceholders().add(md);
			return md;
		}
//		// If not found, see if we can deduce the placeholder from context
//		if (ii == null) {
//			String mdPath = msInstance.instancePathFromContext( getPlaceholderContext(), false );
//			if (mdPath!=null) {
//				ii = trimFactory.createII();
//				ii.setRoot( idRoot );
//				ii.setExtension( mdPath );
//				slot.addUniqueII( ii );
//			}
//		}
		// If we have an id with extension, then return the menuData item
		if (ii != null && ii.getExtension()!=null && ii.getExtension().length() > 0) {
			md = getMenuBean().findMenuDataItem(getAccount().getId(), ii.getExtension());
			// While we're at it, grab all of the external id's we know about
			for (PlaceholderID pid : md.getPlaceholderIDs()) {
				ii = trimFactory.createII();
				ii.setRoot( pid.getRoot() );
				ii.setExtension( pid.getExtension() );
				ii.setAssigningAuthorityName(pid.getAssigningAuthority());
				slot.addUniqueII( ii );
			}
			this.getPlaceholders().add(md);
			return md;
		}
		// Look more broadly at placeholderIds: include external ids
		for (II iiExternal : slot.getIIS()) {
			if (iiExternal.getExtension()!=null && iiExternal.getExtension().length()>0) {
				PlaceholderID pid = getMenuBean().findPlaceholderID(getAccount(), iiExternal.getRoot(), iiExternal.getExtension());
				if (pid!=null) {
					md = pid.getPlaceholder();
					ii = trimFactory.createII();
					ii.setRoot( idRoot );
					ii.setExtension( md.getPath() );
					slot.addUniqueII( ii );
					this.getPlaceholders().add(md);
					return md;
				}
			}
		}
		// If still not found, we need to create a placeholder, if allowed. 
		if (BindAction.CREATE == request.placeholderBind.getBindAction()
		|| BindAction.MERGE == request.placeholderBind.getBindAction() ) {
			String variable = computeVariable( request.rimObject );
			md = createNewPlaceholder( msInstance, variable, request.rimObject );
			if(getTrim().getMessage() != null && getTrim().getMessage().getSender() != null){
				md.setSourceAccountId(getTrim().getMessage().getSender().getAccountId());
			}
			md.setDocumentId(this.getDocumentId());
			ii = trimFactory.createII();
			ii.setRoot( idRoot );
			ii.setExtension( md.getPath() );
			slot.addUniqueII( ii );
			this.getPlaceholders().add(md);
			return md;
		}
		if (request.placeholderBind.isOptional()) {
			return null;
		} 
		throw new RuntimeException( "Unable to create placeholder at " +  request.location + " in trim: " + this.getTrim().getName());
	}
	

	@Override
	protected void processCESlot(String fieldName, CESlot slot) {
		super.processCESlot(fieldName, slot);
		CESlotEx slotEx = (CESlotEx) slot;
		// New processing, if any
		NewFacet nf = slot.getNew();
		if (nf!=null && slotEx.getValue()==null) {
			if (nf.getEncoded()!=null) {
				slotEx.setValue(trimFactory.stringToDataType(nf.getEncoded()));
			}
		}
	}

	@Override
	protected void processGTSSlot(String fieldName, GTSSlot slot) {
		super.processGTSSlot(fieldName, slot);
		GTSSlotEx slotEx = (GTSSlotEx) slot; 
		NewFacet nf = slot.getNew();
		if (nf!=null && slotEx.getValue()==null) {
			if (nf.getDatatype().equals(ConcreteDatatype.TS)) {
				if ("now".equals(nf.getFunction())) {
					slot.setTS(trimFactory.createNewTS(getNow()));
				}
			}
		}

	}
	
	/**
	 * JMC: This should be obsolete but I'm not sure yet.
	 */
	@Override
	protected void processObservationValue(ObservationValueSlot value) {
		super.processObservationValue(value);
		if (value != null && value.getCE()!=null && this.getMenuDataSource()!=null) {
			CE ce = value.getCE();
			MenuData mdSource = getMenuDataSource();
			if ("@string01@".equals(ce.getCode())) ce.setCode(mdSource.getString01());
			if ("@string02@".equals(ce.getCode())) ce.setCode(mdSource.getString02());
			if ("@string03@".equals(ce.getCode())) ce.setCode(mdSource.getString03());
			if ("@string04@".equals(ce.getCode())) ce.setCode(mdSource.getString04());
			if ("@string05@".equals(ce.getCode())) ce.setCode(mdSource.getString05());
			if ("@string06@".equals(ce.getCode())) ce.setCode(mdSource.getString06());
			if ("@string01@".equals(ce.getDisplayName())) ce.setDisplayName(mdSource.getString01());
			if ("@string02@".equals(ce.getDisplayName())) ce.setDisplayName(mdSource.getString02());
			if ("@string03@".equals(ce.getDisplayName())) ce.setDisplayName(mdSource.getString03());
			if ("@string04@".equals(ce.getDisplayName())) ce.setDisplayName(mdSource.getString04());
			if ("@string05@".equals(ce.getDisplayName())) ce.setDisplayName(mdSource.getString05());
			if ("@string06@".equals(ce.getDisplayName())) ce.setDisplayName(mdSource.getString06());
		}
	}

	public TrimLocal getTrimBean() {
		return trimBean;
	}

	public void setTrimBean(TrimLocal trimBean) {
		this.trimBean = trimBean;
	}
	
	/**
	 * MenuData source is used in cases where some binding information is extracted from
	 * menuData.
	 * @return
	 */
	public MenuData getMenuDataSource() {
		return menuDataSource;
	}
	public void setMenuDataSource(MenuData menuDataSource) {
		this.menuDataSource = menuDataSource;
	}

	
}
