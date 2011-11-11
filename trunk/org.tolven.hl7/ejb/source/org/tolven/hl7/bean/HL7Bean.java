package org.tolven.hl7.bean;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.hl7.HL7Local;
import org.tolven.hl7.Immunization;
import org.tolven.hl7.PHS;
import org.tolven.hl7.entity.HL7Message;

@Local(HL7Local.class)
@Stateless
public class HL7Bean implements HL7Local {
	@PersistenceContext
	private EntityManager em;
	@EJB
	private MenuLocal menubean;
	
	public MenuLocal getMenubean() {
		return menubean;
	}

	public void setMenubean(MenuLocal menubean) {
		this.menubean = menubean;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public boolean saveHL7Message(MenuData md) {
		try {

			MenuData recevier = (MenuData) md.getField("receiverBy");
			HL7Message hl7Message = new HL7Message();
			hl7Message.setPlaceholderPath(md.getPath());
			if (md != null && md.getPath().contains("immunization")) {
				Immunization imm = new Immunization(md, recevier);
				hl7Message.setXml01(imm.createHL7().getBytes());
				em.persist(hl7Message);
			} else {
				boolean flag = Boolean.parseBoolean(md.getField("isReportable").toString());
				if (flag) {
					PHS phs = new PHS(md, recevier, "Inpatient");
					hl7Message.setXml01(phs.createHL7().getBytes());
					em.persist(hl7Message);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error saving HL7Message for "
					+ md.getPath(), e);
		}
		return true;
	}

	@Override
	public String findHL7Message(String placeholderPath) {
		String hl7Message = "";
        try {
    		Query query = em.createQuery("SELECT m FROM HL7Message m WHERE m.placeholderPath = :path");
            query.setParameter("path", placeholderPath);
        	Object obj = query.getSingleResult();
        	if(obj != null){
        		HL7Message message = (HL7Message)obj;
        		hl7Message = new String(message.getXml01()); 
        	}
        } catch (NoResultException ex) {
        	hl7Message = "Message not found for "+placeholderPath;
        }
        return hl7Message;
	}

}
