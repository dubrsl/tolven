package org.tolven.app.bean;

import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.Account;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocCCR;

/**
 * <p>Instances of this class wrap a MenuData item while in memory providing a context-specific personality to the data index data and the underlying document.
 * This wrapper is typically used when creating and manipulating MenuData items. Usually not used in 
 * AJAX lists which map directly to the MenuData object and for read only. The only exception is
 * when drilling down to a specific object since this is the way we get to the encrypted document
 * underlying the MenuData item.</p>
 * <p>A subclass must ensure that the wrapper contains no data that can't be recreated from the underlying
 * MenuData item or the document, if any, underlying the MenuData item.</p>
 * @author John Churin
 *
 */
public abstract class MenuDataWrapper {
	private transient MenuData menuData;
    private transient DocBase unlockedDocument;
    private transient boolean documentUnlocked = false;
    /**
     * Get the generic path to the MenuStructure defining this MenuDataItem. This method is not sufficient to get a particular instance of a menu data item.
     * @return A path such as "echr:patient"
     */
	public abstract String getMSPath();

	public MenuData getMenuData() {
		return menuData;
	}

	protected void setMenuData(MenuData menuData) {
		this.menuData = menuData;
	}
	/**
	 * Get the unlocked document associated with this MenuData item.
	 * @return
	 * @throws IllegalArgumentException if the document has not been unlocked.
	 */
	protected DocBase getUnlockedDocument() {
		if (!isDocumentUnlocked()) throw new IllegalArgumentException( "Document is not unlocked"); 
		return unlockedDocument;
	}

	protected void setUnlockedDocument(DocBase unlockedDocument) {
		this.unlockedDocument = unlockedDocument;
	}

	protected boolean isDocumentUnlocked() {
		return documentUnlocked;
	}

	protected void setDocumentUnlocked(boolean documentUnlocked) {
		this.documentUnlocked = documentUnlocked;
	}
	
	/**
	 * Get the current CCR document or null if it isn't CCR
	 * @return
	 */
	protected DocCCR getCCR( ) {
		DocBase doc = getUnlockedDocument();
		if (doc instanceof DocCCR) return (DocCCR) doc;
		return null;
	}

}
