package org.tolven.process;

import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Role;
import org.tolven.trim.Act;
import org.tolven.trim.Entity;
import org.tolven.trim.II;
import org.tolven.trim.ex.SETIISlotEx;

public class DupeCheck extends ComputeBase {

	private String root;
	
	@Override
	public void compute() throws Exception {
		TolvenLogger.info( "[MRNCheck] root=" + getRoot() + " location=" + getLocation(), DupeCheck.class);
		SETIISlotEx iislot = null;
		// Get the appropriate ID slot
		if (this.getNode() instanceof Role) {
			iislot = (SETIISlotEx) ((Role)this.getNode()).getId();
		}
		if (this.getNode() instanceof Act) {
			iislot = (SETIISlotEx) ((Act)this.getNode()).getId();
		}
		if (this.getNode() instanceof Entity) {
			iislot = (SETIISlotEx) ((Entity)this.getNode()).getId();
		}
		II thisII = iislot.findII(ELFunctions.computeIDRoot(getAccountUser().getAccount()));
		MenuData placeholder = null;
		if (thisII!=null) {
			placeholder = this.getMenuBean().findMenuDataItem(getAccountUser().getAccount().getId(), thisII.getExtension());
		}
		// If we have a slot, get the specified id based on the root
		if (iislot!=null) {
			II item = iislot.findII(getRoot());
			// If the extension is specified, then see if it's already known as an id, we've got a dupe
			if (item!=null && item.getExtension()!=null && item.getExtension().length()>0) {
				PlaceholderID placeholderId = getMenuBean().findPlaceholderID(getAccountUser().getAccount(), item.getRoot(), item.getExtension());
				if (placeholderId!=null && placeholderId.getPlaceholder()!=placeholder) {
 					throw new ValidationException(getLocation(), "MRN Already exists");
				}
			}
		}
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

}
