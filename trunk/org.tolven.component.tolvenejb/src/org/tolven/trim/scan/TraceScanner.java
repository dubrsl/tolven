package org.tolven.trim.scan;

import java.util.ArrayList;
import java.util.List;

import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.CESlot;
import org.tolven.trim.Entity;
import org.tolven.trim.GTSSlot;
import org.tolven.trim.LivingSubject;
import org.tolven.trim.Observation;
import org.tolven.trim.Person;
import org.tolven.trim.Role;
import org.tolven.trim.SETCESlot;
import org.tolven.trim.Trim;

/**
 * Scanner subclass that simply displays the path to each node in the Trim graph provides.
 * Use by unit tests.
 * @author John Churin
 *
 */
public class TraceScanner extends Scanner {

	private List<String> nodePaths = new ArrayList<String>(100);
	
	public void addNodePath( ) {
		nodePaths.add(getLocation());
	}
	
	public void printNodePaths( ) {
		for (String path : getNodePaths()) {
			TolvenLogger.info( path, TraceScanner.class );
		}
	}
	
	@Override
	protected void preProcessAct(Act act) {
		super.preProcessAct(act);
		addNodePath();
	}

	@Override
	protected void preProcessObservation(Observation observation) {
		super.preProcessObservation(observation);
		addNodePath();
	}

	@Override
	protected void preProcessEntity(Entity entity) {
		super.preProcessEntity(entity);
		addNodePath();
	}
	@Override
	protected void preProcessLivingSubject(LivingSubject ls) {
		super.preProcessLivingSubject(ls);
		addNodePath();
	}

	@Override
	protected void preProcessPerson(Person person) {
		super.preProcessPerson(person);
		addNodePath();
	}

	@Override
	protected void preProcessRole(Role role) {
		super.preProcessRole(role);
		addNodePath();
	}

	@Override
	protected void preProcessTrim(Trim trim) {
		super.preProcessTrim(trim);
		addNodePath();
	}

	@Override
	protected void processActRelationship(ActRelationship ar) {
		super.processActRelationship(ar);
		addNodePath();
	}

	@Override
	protected void processActParticipation(ActParticipation part) {
		super.processActParticipation(part);
		addNodePath();
	}

	@Override
	protected void processCESlot(String fieldName, CESlot slot) {
		super.processCESlot(fieldName, slot);
		nodePaths.add(getLocation()+ "." + fieldName);
	}

	@Override
	protected void processGTSSlot(String fieldName, GTSSlot slot) {
		super.processGTSSlot(fieldName, slot);
		nodePaths.add(getLocation()+ "." + fieldName);
	}

	@Override
	protected void processSETCESlot(String fieldName, SETCESlot slot) {
		super.processSETCESlot(fieldName, slot);
		nodePaths.add(getLocation()+ "." + fieldName);
	}

	public List<String> getNodePaths() {
		return nodePaths;
	}

	public void setNodePaths(List<String> nodePaths) {
		this.nodePaths = nodePaths;
	}

	
}
