package org.tolven.trim.ex;

import java.util.ArrayList;
import java.util.List;

import org.tolven.trim.II;
import org.tolven.trim.SETIISlot;

@SuppressWarnings("serial")
public class SETIISlotEx extends SETIISlot {
	
	/**
	 * @return 
	 * A map suitable for lookup of ids by root. For example, 
	 * act.id.for['root1'].extension will return the extension of the id with root "root1".
	 */
	public ForIIMap getFor( ) {
		return new ForIIMap( this );
	}

	@Override
	public String getDatatype() {
		return "SET_II";
	}
	
	public List<II> getDisplayableIDs() {
		List<II> displayableIDs = new ArrayList<II>(getIIS().size());
		for (II ii : getIIS()) {
			if (ii.isDisplayable()==null || ii.isDisplayable()) {
				displayableIDs.add(ii);
			}
		}
		return displayableIDs;
	}
	
	/**
	 * Add a unique II to the LIST of IIs as if it were a set.
	 * Uniqueness is based on root
	 * @param ii
	 * @return
	 */
	public boolean addUniqueII( II ii ) {
		for (II iiExisting : getIIS()) {
			if (ii.getRoot().equals(iiExisting.getRoot())) {
				iiExisting.setExtension(ii.getExtension());
				if (ii.isDisplayable()!=null) {
					iiExisting.setDisplayable(ii.isDisplayable());
				}
				if (ii.getLabel()!=null) {
					iiExisting.setLabel(ii.getLabel());
				}
				if (ii.getOriginalText()!=null) {
					iiExisting.setOriginalText(ii.getOriginalText());
				}
				return false;
			}
		}
		getIIS().add(ii);
		return true;
	}

	public II findII( String root ) {
		List<II> iis = getIIS();
		for (II ii : iis) {
			if (root.equals(ii.getRoot())) {
				return ii;
			}
		}
		return null;
	}

}
