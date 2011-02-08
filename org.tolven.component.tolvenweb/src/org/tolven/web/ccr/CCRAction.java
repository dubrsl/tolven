package org.tolven.web.ccr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELException;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.tolven.ccr.ActorType;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.ccr.PersonNameType;
import org.tolven.ccr.ContinuityOfCareRecord.Patient;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.CCRException;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocCCR;
import org.tolven.logging.TolvenLogger;
import org.tolven.web.DocAction;


public class CCRAction extends DocAction {

	private ActorType actor;
	private List<SelectItem> ccrPathItems = null;
	private ContinuityOfCareRecord ccr;
	
	private boolean debug;

	public CCRAction() throws NamingException {
		super();
	}
	
	protected void addCCRPath( String path ){
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		String pathValue;
		try {
			pathValue = (String) app.evaluateExpressionGet(ctx, path, String.class );
		} catch (ELException e) {
			pathValue = e.getMessage();
		}
		ccrPathItems.add( new SelectItem(pathValue, path ));
	}

	/**
     * A shortcut to return a list of the actors playing patients.
     * These actors may be playing other roles as well.
     * There is usually only one patient, sometimes two (conjoined twins)
     * @return A list of ActorTypes corresponding to the list of Patients in this document
	 * @throws Exception 
	 * @throws JAXBException 
     */
    public List<ActorType> getPatientActor( ) throws JAXBException, Exception {
    	int patCount = getCCR().getPatient().size();
    	List<ActorType> patientActors = new ArrayList<ActorType>(patCount);
    	for(Patient p : getCCR().getPatient()) {
	    	for ( ActorType a : getCCR().getActors().getActor()) {
	    		if (a.getActorObjectID().equals(p.getActorID())) {
	    			patientActors.add(a);
	    		}
	    	}
    	}
    	return patientActors;
    }
    
	public List<SelectItem> getccrPaths() throws JAXBException, Exception {
		if (ccrPathItems==null) {
			ccrPathItems = new ArrayList<SelectItem>( 30 );
			if (getCCR()==null) return ccrPathItems; 
			addCCRPath( "#{ccr.CCR.CCRDocumentObjectID}" );
			addCCRPath( "#{ccr.CCR.version}" );
			addCCRPath( "#{ccr.CCR.patient[0].actorID}" );
			addCCRPath( "#{ccr.CCR.patient[1].actorID}" );
			addCCRPath( "#{ccr.patientActor[0].person.name.currentName.given[0]}" );
			addCCRPath( "#{ccr.CCR.actors.actor[0].person.name.currentName.family[0]}" );
			addCCRPath( "#{ccr.patientActor[0].person.dateOfBirth}" );
			addCCRPath( "#{ccr.CCR.actors.actor[0].person.gender.text}" );
			addCCRPath( "#{ccr.patientActor[0].address[0].line1}" );
			addCCRPath( "#{ccr.CCR.actors.actor[0].address[0].line2}" );
			addCCRPath( "#{ccr.patientActor[0].address[0].city}" );
			addCCRPath( "#{ccr.CCR.actors.actor[0].address[0].state}" );
			addCCRPath( "#{ccr.patientActor[0].address[0].postalCode}" );
			addCCRPath( "#{ccr.CCR.actors.actor[0].address[0].country}" );
			addCCRPath( "#{ccr.patientActor[0].address[0].county}" );
			addCCRPath( "#{ccr.CCR.body.problems.problem[0].description.text}");
		}
		return ccrPathItems;
	}

	/**
	 * Create a new instance of a CCR document.
	 * @return
	 * @throws CCRException 
	 * @throws IOException 
	 * @throws JAXBException 
	 */
	public String newCCR() throws CCRException, NamingException {
		TolvenLogger.info( "Create new document...", CCRAction.class);
		// Create a new CCR document.
		DocCCR docCCR = getDocBean().createCCRDocument(getSessionTolvenUserId(), getSessionAccountId());
		setDoc( docCCR );
		TolvenLogger.info( "...created id " + getDoc().getId(), CCRAction.class);
		getDocBean().saveDocument(docCCR);
		return "success";
	}

//	public String addPatient( ) throws Exception {
//		getDocCCR().addNewPatient();
//		TolvenLogger.info( "Patient added to " + getDocCCR().getId(), CCRAction.class);
//		docBean.persistCCRDocument(getDocCCR(), false);
////		docBean.submitDocument(getDocCCR());
//		return "success";
//	}
	
	/**
	 * Return the unmarshalled (or new) CCR object graph for this document. 
	 * @return root of ContinuityOfCareRecord
	 * @throws JAXBException
	 * @throws Exception
	 */
	public ContinuityOfCareRecord getCCR() throws Exception {
		if (ccr==null) {
			if (getDocXML()==null) return null;
			if (!(getDocXML() instanceof DocCCR)) return null;
            AccountUser activeAccountUser = getActivationBean().findAccountUser(this.getSessionAccountUserId());
			ccr = (ContinuityOfCareRecord) getXMLProtectedBean().unmarshal(getDocXML(), activeAccountUser, getUserPrivateKey());
		}
		return ccr;
	}

	/**
	 * Type-safe accessor for document that is a CCR.
	 * @return
	 */
	public DocCCR getDocCCR() throws NamingException {
		DocBase doc = getDoc();
		if (doc==null) return null;
		if (doc instanceof DocCCR) return (DocCCR) doc;
		return null;
	}

	/**
	 * @return
	 */
	public ActorType getActor() {
		if (actor==null) {
			actor = new ActorType();
			ActorType.Person person = new ActorType.Person(); 
			PersonNameType personName = new PersonNameType();
			personName.getGiven().add("a given name 3");
			ActorType.Person.Name name = new ActorType.Person.Name();
			name.setCurrentName(personName);
			person.setName(name);
			actor.setPerson(person);
		}
		return actor;
	}

	public void setActor(ActorType actor) {
		this.actor = actor;
	}
	
	public String submit() {
		TolvenLogger.info( "CCR Submit ", CCRAction.class  );
//		docBean.persistCCRDocument(getDocCCR(), true);
		ActorType actor = getActor();
		if (actor==null) return "success";
		ActorType.Person person = actor.getPerson();
		if (person==null) return "success";
		ActorType.Person.Name name = person.getName();
		if (name==null) return "success";
		PersonNameType pnt = name.getCurrentName();
		if (pnt==null) return "success";
		TolvenLogger.info( "PersonNameType: " + pnt.getFamily() + "," + pnt.getGiven(), CCRAction.class );
		return "success";
	}

	public String reset() {
		TolvenLogger.info( "CCR Reset ", CCRAction.class  );
		actor = null;
		return "success";
	}

	public String add() {
		Object type = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("type");
		Object parent = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("parent");
		TolvenLogger.info( "Add a " + type + " to " + parent, CCRAction.class  );
//		if (parent instanceof ActorType.Person.Name) {
			ActorType.Person.Name lp = (ActorType.Person.Name)parent;
			lp.setBirthName(new PersonNameType ());
			TolvenLogger.info( "Added birth Name", CCRAction.class);
//		}
		return "success";
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
