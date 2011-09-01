package org.tolven.trim.scan;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.tolven.doctype.DocumentType;
import org.tolven.trim.Act;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.BindPhase;
import org.tolven.trim.BindTo;
import org.tolven.trim.CESlot;
import org.tolven.trim.Entity;
import org.tolven.trim.GTSSlot;
import org.tolven.trim.InfrastructureRoot;
import org.tolven.trim.LivingSubject;
import org.tolven.trim.Observation;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.Patient;
import org.tolven.trim.Person;
import org.tolven.trim.Role;
import org.tolven.trim.RoleParticipation;
import org.tolven.trim.SETCESlot;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;

/**
 * Concrete subclasses of this scanner visit various nodes of a trim graph.
 * @author John Churin
 *
 */
public abstract class Scanner {
	protected static final TrimFactory trimFactory = new TrimFactory();
	private Trim trim;
	private BindPhase phase;
	private String knownType;
	private DocumentType documentType;
	
	
	
	private Stack<String> location = new Stack<String>();
	private Stack<InfrastructureRoot> rimObjectPath = new Stack<InfrastructureRoot>();

	/**
	 * The scan method sets about scanning the provided trim. This is, effectively the main method.
	 * Prior to calling this method, several properties should be set. Most significantly is the
	 * trim object. The account, BindPhase and account object should almost always be provided as well.
	 */
	public void scan( ) {
		if (getTrim()==null) throw new RuntimeException( "Scanner: Missing Trim");
		scanTrim();
	}
	
	/**
	 * Override this method in order to see a trim prior to any further processing on the trim object.
	 * @param act
	 */
	protected void preProcessTrim( Trim trim ) { }
	
	/**
	 * Override this method in order to see a Trim object after other processing has been performed n that object.
	 * @param act
	 */
	protected void postProcessTrim( Trim trim ) { }
	/**
	 * Override this method in order to see an act prior to any further processing on the act.
	 * @param act
	 */
	protected void preProcessAct( Act act ) { }
	/**
	 * Override this method in order to see an act after other processing has been performed.
	 * 
	 * @param act
	 */
	protected void postProcessAct( Act act ) { }
	/**
	 * Override this method in order to see a Role prior to any further processing on the Role.
	 * @param role
	 */
	protected void preProcessRole( Role role ) { }
	/**
	 * Override this method in order to see a role after other processing has been performed on that Role.
	 * @param role
	 */
	protected void postProcessRole( Role role ) { }
	
	/**
	 * Override this method in order to see an Entity prior to any further processing on the Entity.
	 * @param entity
	 */
	protected void preProcessEntity( Entity entity ) { }

	/**
	 * Override this method in order to see an Entity after other processing has been performed on that Entity.
	 * @param entity
	 */
	protected void postProcessEntity( Entity entity ) { }

	/**
	 * Override this method to process non-null GTS slots, regardless of where the slot occurs.
	 * @param GTSSlot
	 */
	protected void processGTSSlot( String fieldName, GTSSlot slot ) { }

	/**
	 * Override this method to process CE slots, regardless of where the slot occurs.
	 * @param CESlot
	 */
	protected void processCESlot( String fieldName, CESlot slot ) { }

	/**
	 * Override this method to process SETCE slots, regardless of where the slot occurs.
	 * @param SETCESlot
	 */
	protected void processSETCESlot( String fieldName, SETCESlot slot ) { }
	
	/**
	 * Override this method to process a matching bind-to request. Both the application and phase
	 * must match, otherwise this method is not called for the specified bind request.
	 * @param rimObject
	 * @param bindTo
	 */
	protected void processBindTo( InfrastructureRoot rimObject, BindTo bindTo  ) { }
	protected void processObservationValue( ObservationValueSlot value ) { }
	protected void processActRelationship( ActRelationship ar ) { }
	protected void processActParticipation( ActParticipation part ) { }
	protected void processRoleParticipation( RoleParticipation part ) { }
	protected void preProcessLivingSubject(LivingSubject ls) { }
	protected void postProcessLivingSubject(LivingSubject ls) {	}
	protected void preProcessPerson(Person person) { }
	protected void postProcessPerson(Person person) { }
	protected void preProcessPatient(Patient patient) { }
	protected void postProcessPatient(Patient patient) { }
	protected void preProcessObservation(Observation observation) {	}
	protected void postProcessObservation(Observation observation) { }
	
	private void processObservationValues( List<ObservationValueSlot> values ) {
		for (ObservationValueSlot value : values) {
			processObservationValue( value);
		}
	}

	
	/**
	 * Handle binding instructions. Binding instructions are perpetual in a trim: Whenever we process a trim, we
	 * seek to bind to underlying placeholders. 
	 * Only consider binding instructions that match application (if supplied). And only IDs
	 * relating to this account, and only of the rim object is enabled in this trim
	 * Only process nodes that are enabled.
	 * @param rimObject
	 */
	private void bindTo( InfrastructureRoot rimObject ) {
		if (isPathEnabled()) {
			for (BindTo bindTo : rimObject.getBinds() ) {
				// Only consider binding instructions that match phase and application (if supplied)
				// If not supplied, then match unconditionally.
				if (getKnownType()!=null && bindTo.getApplication()!= null) {
					if (!getKnownType().equals(bindTo.getApplication())) continue;
				}
				if (getPhase()!=null && bindTo.getPhase()!=null) {
					if (!getPhase().equals(bindTo.getPhase())) continue;
				}
				processBindTo( rimObject, bindTo );
			}
		}
	}

	private void scanTrim() {
		pushLocation( "trim");
		preProcessTrim(getTrim());
		if (getTrim().getAct()!=null) scanAct( getTrim().getAct() );
		postProcessTrim(getTrim());
		popLocation();
	}
	
	/**
	 * Look for binding requests in a newly instantiated Act template. For example, 
	 * an observation act will probably ask to be bound to some patient so it will 
	 * get bound to the actual patient now.
	 * @param ms 
	 * @param act
	 * @param context a list of parent nodes such as patient that may be needed to help
	 * complete the binding.
	 */
	private void scanAct( Act act ) {
		rimObjectPath.push(act);
		pushLocation( "act");
		preProcessAct( act );
		bindTo( act );
		if (act.getEffectiveTime()!=null) processGTSSlot("effectiveTime", act.getEffectiveTime());
		if( act.getActivityTime()!=null) processGTSSlot("activityTime", act.getActivityTime());
		if (act.getObservation()!=null  ) {
			pushLocation( "observation" );
			preProcessObservation(act.getObservation());
			if (act.getObservation().getValues()!=null) {
				processObservationValues( act.getObservation().getValues() );
			}
			postProcessObservation(act.getObservation());
			popLocation();
		}
		// Make a copy of the list in case the list is modified by the callback
		int partIndex = 0;
		List<ActParticipation> parts = new ArrayList<ActParticipation>( act.getParticipations() );
		for (ActParticipation part : parts) {
			rimObjectPath.push(part);
			pushLocation( "participations[" + partIndex++ + "]" );
//			pushLocation( "participation['" + part.getName() + "']" );
			bindTo( part );
			processActParticipation( part );
			if (part.getRole()!=null) {
				scanRole( (Role)part.getRole() );
			}
			popLocation();
			rimObjectPath.pop();
		}
		int relIndex = 0;
		List<ActRelationship> ars = new ArrayList<ActRelationship>( act.getRelationships() );
		for (ActRelationship ar : ars ) {
			rimObjectPath.push(ar);
			pushLocation( "relationships[" + relIndex++ + "]");
//				pushLocation( "relationship['" + ar.getName() + "']");
			bindTo( ar );
			processActRelationship( ar );
			if (ar.getAct()!=null) {
				scanAct( ar.getAct() );
			}
			popLocation();
			rimObjectPath.pop();
		}
		postProcessAct( act );
		popLocation();
		rimObjectPath.pop();
	}
	
	/**
	 * Scan an entity for create-time behavior
	 * @param ms
	 * @param entity
	 * @param context
	 * @param mdSource
	 */
	private void scanEntity( Entity entity ) {
		rimObjectPath.push(entity);
		pushLocation( "entity" );
		preProcessEntity( entity );
		bindTo( entity );
		// TODO: add all attributes, not just a few CESlots (also, all subclasses)
		if (entity.getCode()!=null) processCESlot("code", entity.getCode());
		if (entity.getHandlingCode()!=null) processCESlot("handlingCode", entity.getHandlingCode());
		if (entity.getRiskCode()!=null) processCESlot("riskCode", entity.getRiskCode());
		if (entity.getLivingSubject()!=null) {
			scanLivingSubject( entity.getLivingSubject() );
		}
		if (entity.getPerson()!=null) {
			scanPerson( entity.getPerson() );
		}
		for ( Role role : entity.getScopedRoles()) {
			scanRole( role);
		}
		for ( Role role : entity.getPlayedRoles()) {
			scanRole( role);
		}
		postProcessEntity( entity );
		popLocation();
		rimObjectPath.pop();
	}
	private void scanLivingSubject( LivingSubject ls ) {
		pushLocation( "livingSubject" );
		preProcessLivingSubject(ls);
		if (ls.getAdministrativeGenderCode()!=null) processCESlot( "administrativeGenderCode", ls.getAdministrativeGenderCode() );
		postProcessLivingSubject(ls);
		popLocation();
	}
	
	private void scanPerson( Person person ) {
		pushLocation( "person" );
		preProcessPerson(person);
		if (person.getDisabilityCode()!=null) processSETCESlot("disabilityCode", person.getDisabilityCode());
		if (person.getEthnicGroupCode()!=null) processSETCESlot("ethnicGroupCode", person.getEthnicGroupCode());
		if (person.getRaceCode()!=null) processSETCESlot("raceCode", person.getRaceCode());
		if (person.getEducationLevelCode()!=null) processCESlot("educationLevelCode", person.getEducationLevelCode());
		if (person.getLivingArrangementCode()!=null) processCESlot("livingArrangementCode", person.getLivingArrangementCode() );
		if (person.getMaritalStatusCode()!=null) processCESlot("maritalStatusCode", person.getMaritalStatusCode() );
		if (person.getReligiousAffiliationCode()!=null) processCESlot("ReligiousAffiliationCode", person.getReligiousAffiliationCode() );
		postProcessPerson(person);
		popLocation();
	}
	
	private void scanRole( Role role ) {
		rimObjectPath.push(role);
		pushLocation( "role" );
		preProcessRole( role );
		bindTo( role );
		if (role.getCode()!=null) processCESlot( "code", role.getCode());
		if (role.getPatient()!=null) {
			scanPatient( role.getPatient() );
		}

		if (role.getScoper()!=null) {
			pushLocation( "scoper" );
			scanEntity( role.getScoper() );
			popLocation();
		}
		if (role.getPlayer()!=null) {
			pushLocation( "player" );
			scanEntity( role.getPlayer() );
			popLocation();
		}
		for ( RoleParticipation part : role.getParticipations()) {
			rimObjectPath.push(part);
			pushLocation( "participation['" + part.getName() + "']" );
			processRoleParticipation( part );
			if (part.getAct()!=null) {
				scanAct( part.getAct() );
			}
			popLocation();
			rimObjectPath.pop();
		}
		postProcessRole( role );
		popLocation();
		rimObjectPath.pop();
	}
	private void scanPatient( Patient patient ) {
		pushLocation( "patient" );
		preProcessPatient(patient);
		if (patient.getVeryImportantPersonCode()!=null) processCESlot("veryImportantPersonCode", patient.getVeryImportantPersonCode());
		postProcessPatient(patient);
		popLocation();
	}
	
	
	
	public void pushLocation( String location ) {
		this.location.push(location);
	}
	public String popLocation( ) {
		return this.location.pop();
	}
	
	/**
	 * Determine if the current node is in an ancestor path (including itself) that is enabled.
	 * @return true if no node in the current path is enabled=false
	 */
	public boolean isPathEnabled() {
		for ( int x = 0; x < rimObjectPath.size();  x++) {
			if (!rimObjectPath.get(x).isEnabled() ) return false;
		}
		return true;
	}
	
	public String getLocation( int offset ) {
		StringBuffer sb = new StringBuffer(100);
		boolean first = true;
		for ( int x = 0; x < location.size()+offset;  x++) {
			if (first) first=false;
			else sb.append(".");
			sb.append( location.get(x) );
		}
		return sb.toString();
	}
	
	/**
	 * Return a valid Expression Language (EL) path within the trim. For example, trim.act.participation['subject'].role
	 * @return 
	 */
	public String getLocation( ) {
		return getLocation( 0 );
	}

	public void setKnownType(String knownType) {
		this.knownType = knownType;
	}

	public String getKnownType() {
		return knownType;
	}

	public Trim getTrim() {
		return trim;
	}
	public void setTrim(Trim trim) {
		this.trim = trim;
	}
	public BindPhase getPhase() {
		return phase;
	}
	public void setPhase(BindPhase phase) {
		this.phase = phase;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

}
