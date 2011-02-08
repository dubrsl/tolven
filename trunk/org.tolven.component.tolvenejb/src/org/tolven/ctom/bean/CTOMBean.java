package org.tolven.ctom.bean;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.math.random.RandomData;
import org.tolven.ctom.CTOMLocal;
import org.tolven.ctom.CTOMRemote;
import org.tolven.ctom.entity.Study;
import org.tolven.ctom.entity.Subject;
import org.tolven.gen.PersonGenerator;

@Stateless()
@Local(CTOMLocal.class)
@Remote(CTOMRemote.class)
public class CTOMBean implements org.tolven.ctom.CTOMLocal {

	@PersistenceContext
	private EntityManager em;

	@EJB
	private PersonGenerator personGen;
	
    private RandomData rng;
	private static SimpleDateFormat iso8601d = new SimpleDateFormat("yyyy-MM-dd"); 

	/**
	 * Persist a new study
	 * @param study
	 * @return the id of the new Study
	 */
	public long persist( Study study ) {
		em.persist(study);
		return study.getId();
	}

	/**
	 * Persist a new subject
	 * @param subject
	 * @return the id of the new Subject
	 */
	public long persist( Subject subject ) {
		em.persist(subject);
		return subject.getId();
	}

	/**
	 * Return a list of all subject known to the system (limited to 100)
	 * @return
	 */
	public List<Subject> findSubjects() {
		Query query = em.createQuery("SELECT s FROM Subject s");
		query.setMaxResults(100);
		return query.getResultList();
	}
	
	
}
