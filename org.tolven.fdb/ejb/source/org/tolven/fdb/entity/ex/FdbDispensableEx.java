package org.tolven.fdb.entity.ex;

import javax.persistence.EntityManager;

import org.tolven.fdb.entity.FdbDispensable;
import org.tolven.fdb.entity.FdbDoseform;
import org.tolven.fdb.entity.FdbRoute;

public class FdbDispensableEx{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FdbDispensable dispensable;
	private FdbRoute route;
	private FdbDoseform doseform;
	public FdbDispensableEx(FdbDispensable dispensable) {
		this.dispensable = dispensable;
	}
	public FdbDispensableEx() {
		// TODO Auto-generated constructor stub
	}
	
	public FdbDispensable getDispensable() {
		return dispensable;
	}
	public void setDispensable(FdbDispensable dispensable) {
		this.dispensable = dispensable;
	}
	public FdbRoute getRoute() {
		return route;
	}
	public void setRoute(FdbRoute route) {
		this.route = route;
	}
	public FdbDoseform getDoseform() {
		return doseform;
	}
	public void setDoseform(FdbDoseform doseform) {
		this.doseform = doseform;
	}
	public void loadRoute(EntityManager em){
		this.route = em.find(FdbRoute.class, this.getDispensable().getRtid());
	}
	public void loadDoseform(EntityManager em){
		this.doseform = em.find(FdbDoseform.class, this.getDispensable().getDfid());
	}

}
