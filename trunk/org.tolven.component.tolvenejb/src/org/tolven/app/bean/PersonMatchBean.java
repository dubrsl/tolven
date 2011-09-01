package org.tolven.app.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.tolven.app.PersonMatchLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;

@Stateless()
@Local(PersonMatchLocal.class)
public class PersonMatchBean implements PersonMatchLocal {
	@PersistenceContext private EntityManager em;

	/**
	 * Return a list of possible matches 
	 * Insufficient criteria returns an empty list.
	 * @param ms The MenuStructure defining the list to query such as echr:patients:all
	 * @param limit maximum number of hits to return
	 * @param criteria A prototype MenuData item containing search criteria (the rest of the entity can be left null)
	 * @return A list of matching MenuData items
	 * 
	 * Modiifed by adding first name and DOB to duplicate check on 08/04/2010 by Valsaraj.
	 */
	public List<MenuData> match( MenuStructure ms, int limit, MenuData criteria ) {
		if (StringUtils.isEmpty(criteria.getString01()) 
				|| StringUtils.isEmpty(criteria.getString04())
				|| criteria.getDate01()==null) 
			return new ArrayList<MenuData>(); 
		Query query = em.createQuery("SELECT md FROM MenuData md WHERE " +
				"md.menuStructure = :ms AND " +
				"(md.deleted is null OR md.deleted = false) AND " +
				"lower(md.string01) BETWEEN :s01a  AND :s01b AND " +
				"lower(md.string02) BETWEEN :f01a  AND :f01b AND " +
				"md.date01 = :date01 AND " +
				"md.string04 = :s04");
		String fc01 = criteria.getString01().substring(0, 1).toLowerCase();
		query.setParameter("ms", ms );
		query.setParameter("s01a", fc01 );
		query.setParameter("s01b", fc01+"zzzzzzzzzzzzzzzzzzzz" );
		query.setParameter("s04", criteria.getString04() );
//CCHIT merge
		String fc00 = criteria.getString02().substring(0, 1).toLowerCase();
		query.setParameter("f01a", fc00 );
		query.setParameter("f01b", fc00 + "zzzzzzzzzzzzzzzzzzzz");
		query.setParameter("date01", criteria.getDate01());
		query.setMaxResults(limit);
		return query.getResultList();
	}
}
