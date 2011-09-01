package org.tolven.web.faces.validate;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.naming.InitialContext;

import org.tolven.app.MenuLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.web.CCHITMenuAction;

import java.util.Map;

/**
 * This class validates patient list name, checks whether list already exists or not.
 * 
 * @author Valsaraj
 * added on 07/13/2010
 */
public class PatientListDuplicateValidator implements Validator, Serializable {
	private static final String ERROR_MESSAGE = "Validation Error: Patient list already exists.";
	
	@Override
    public void validate(FacesContext ctx, UIComponent comp, Object value) throws ValidatorException {
		try {
			String name = null;
			
			if (value instanceof String) {
				name = (String) value;
			}

			if (name != null) {
				if (! dupCheck(name, ctx)) {
					throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
				}
			}
    	}
    	catch (Exception e) {
    		throw new ValidatorException(new FacesMessage(ERROR_MESSAGE));
		}
    }
		
	/**
	 * Checks for duplicate list.
	 * @param name - the name to validate
	 * @param ctx
	 * @return true or false
	 */
	 public boolean dupCheck(String name, FacesContext ctx) {
		 CCHITMenuAction cchitMenuAction = (CCHITMenuAction) ctx.getCurrentInstance().getExternalContext().getRequestMap().get("cchitMenu");
		 Map<String, String> menuItemMap = cchitMenuAction.getMenuItemMap("echr:patients", "tabs");
		 name = name.replaceAll(" ", "");
		 String path = "echr:patients:" + name;

		 for (String pathStr : menuItemMap.values()) {
			 if (path.equalsIgnoreCase(pathStr)) {
				 return false;
			 }
		 }
		 
		 for (String title : menuItemMap.keySet()) {
			 if (name.equalsIgnoreCase(title.replaceAll(" ", ""))) {
				 return false;
			 }
		 }
		 
		 
		 
		 return true;
	 }
}
