package org.tolven.app;

import java.util.List;

import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
/**
 * An implementation of this interface provides EMPI matching services.
 * @author John Churin
 *
 */
public interface PersonMatchLocal {

	/**
	 * Return a list of possible matches 
	 * @param ms The MenuStructure defining the list to query such as echr:patients:all
	 * @param limit maximum number of hits to return
	 * @param criteria A map containing as much criteria as possible
	 * @return A list of matching MenuData items
	 */
	public List<MenuData>match( MenuStructure ms, int limit, MenuData criteria );

}
