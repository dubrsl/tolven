package org.tolven.trim.scan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.MenuData;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActStatus;
import org.tolven.trim.BindAction;
import org.tolven.trim.Entity;
import org.tolven.trim.EntityStatus;
import org.tolven.trim.II;
import org.tolven.trim.Role;
import org.tolven.trim.RoleStatus;
import org.tolven.trim.ex.SETIISlotEx;
/**
 * Trim scanner that will populate placeholders according to bind instructions. We also
 * add the documentId to any placeholder we create thus providing a cross-reference to
 * the document containing the trim we are processing.
 * 
 * @author John Churin
 *
 */
public class PopulateScanner extends BindScanner {
    /**
     * When we bind to a menuData item, we generally want to store all IDs in the 
     * underlying object as placeholder cross-references to the menuData item
     * TBD: A placeholder id references the menu data as a whole. 
     * We store location (EL path) to describe the exact location in the Trim
     * which defines this id. 
     * @param iis
     * @param md
     */
    public void populatePlaceholderIDs(List<II> iis, MenuData md) {
    	for (II ii : iis) {
    		if(ii.getExtension()!=null && ii.getExtension().length()>0) {
        		md.addPlaceholderID(ii.getRoot(), ii.getExtension(), ii.getAssigningAuthorityName());
    		}
    	}
    }

	/**
	 * Populate MenuData from trim when we find an element with placeholder bind information.
	 * @return The created or found placeholder, or null if none is applicable
	 */
	public MenuData bindToPlaceholder( BindRequest request ) {
		TolvenLogger.info( "Populate placeholder at: " + request.location, PopulateScanner.class);
		// Get the id slot setup for our work
		SETIISlotEx slot = computeSlot( request.rimObject );
		String variable = computeVariable( request.rimObject );
		// This is the root of the ID we're looking for
		String idRoot = ELFunctions.computeIDRoot( getAccount() );
		// Look for an existing placeholder for this account
		II ii = slot.findII( idRoot );
		// If not found, we're done.
		if (ii == null || ii.getExtension() == null || ii.getExtension().length()==0) {
			return null;
//			throw new RuntimeException( "Placeholder ID expected but not found at: " + request.location + " of " + getTrim().getName());
		}
		MenuData md = getMenuBean().findMenuDataItem(getAccount().getId(), ii.getExtension());
		// Only populate placeholder if bindAction is MERGE, UPDATE, or CREATE 
		if (request.placeholderBind.getBindAction()== BindAction.MERGE
			||	request.placeholderBind.getBindAction()== BindAction.CREATE
			||	request.placeholderBind.getBindAction()== BindAction.UPDATE) {
			// Get the menuData item
			md.setDocumentId(getDocumentId());
			// Populate MenuData from trim
			Map<String, Object> sourceMap = new HashMap<String, Object>(5); 
			sourceMap.put(variable, request.rimObject);
			sourceMap.put("trim", getTrim());
			sourceMap.put("now", getNow());
			sourceMap.put("bindAction", request.placeholderBind.getBindAction());
			for (MenuData mdPlaceholder : getPlaceholders()) {
				sourceMap.put(mdPlaceholder.getMenuStructure().getNode(), mdPlaceholder);
			}
			getMenuBean().populateMenuData(sourceMap, md);
			populatePlaceholderIDs(slot.getIIS(), md);
			String status = null;
			if (request.rimObject instanceof Act ) {
				md.setDocumentPathVariable("act");
				ActStatus actStatus = ((Act)request.rimObject).getStatusCode();
				if (actStatus!=null) {
					status = actStatus.value();
				}
			}
			if (request.rimObject instanceof Role ) {
				md.setDocumentPathVariable("role");
				RoleStatus roleStatus = ((Role)request.rimObject).getStatusCode();
				if (roleStatus!=null) {
					status = roleStatus.value();
				}
			}
			if (request.rimObject instanceof Entity ) {
				md.setDocumentPathVariable("entity");
				EntityStatus entityStatus = ((Entity)request.rimObject).getStatusCode();
				if (entityStatus!=null) {
					status = entityStatus.value();
				}
			}
	  		md.setDocumentPath(request.location);
			if (status!=null) {
				md.setActStatus( status );
			}
			getChangedPlaceholders().add( md );
		}
		return md;
	}

}
