package org.tolven.trim.scan;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.trim.Act;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Observation;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ObservationEx;
import org.tolven.trim.ex.TrimEx;

/**
 * Process a trim graph during the parse process. This process primarily expands the extends element of the trim
 * and include elements in actRelationships and participations. It also includes valueSet components.
 * @author John Churin
 */
public class ExtensionScanner extends Scanner {
	private TrimEx targetTrim = null;
	private TrimExpressionEvaluator ee;

	@Override
	public void scan() {
		ee = new TrimExpressionEvaluator();
		ee.addVariable("trim", this.getTargetTrim());
		super.scan();
	}

	/**
	 * <p>Extend the supplied target trim by scanning the extendsTrim and offering to supply missing components to the target trim.</p>
	 * <p>Extending is very different from instantiation and binding which is done after this step.</p>
	 */
	@Override
	protected void preProcessTrim(Trim trim) {
		super.preProcessTrim(trim);
		((TrimEx)targetTrim).blend(trim);
	}

	/**
	 * If the act is null in the target, then all we need do is dump the whole base act in 
	 * the target.
	 */
	@Override
	protected void preProcessAct(Act act) {
		super.preProcessAct(act);
		Object target = ee.evaluate("#{" + getLocation(-1) + "}");
		if (target instanceof Trim) {
			Trim targetTrim = (Trim) target;
			if (targetTrim.getAct()==null) {
				targetTrim.setAct(act);
			} else {
				// Can't just insert the act, so blend instead.
				((ActEx)targetTrim.getAct()).blend(act);
			}
		} else if (target instanceof ActRelationship) {
			ActRelationship targetAR = (ActRelationship) target;
			if (targetAR.getAct()==null) targetAR.setAct(act);
			
		}
	}

	@Override
	protected void preProcessObservation(Observation observation) {
		super.preProcessObservation(observation);
		Object target = ee.evaluate("#{" + getLocation(-1) + "}");
		
		if (target instanceof Act) {
			Act targetAct = (Act) target;
			if (targetAct.getObservation()==null) {
				targetAct.setObservation(observation);
			} else {
				// Can't just insert the act, so blend instead.
				((ObservationEx)targetAct.getObservation()).blend(observation);
			}
		}
	}

	public Trim getTargetTrim() {
		return targetTrim;
	}

	public void setTargetTrim(Trim targetTrim) {
		this.targetTrim = (TrimEx) targetTrim;
	}
	
	/**
	 * For each participation we find in the base, see if there is a
	 * corresponding item in the extension and if not, add it.
	 */
	@Override
	protected void processActParticipation(ActParticipation part) {
		super.processActParticipation(part);
		String parentActPath = getLocation(-1);
		ActEx targetAct = (ActEx) ee.evaluate("#{" + parentActPath + "}");
		targetAct.getParticipations().add(part);
//		processActParticipation( part );
		//		if (!targetAct.getParticipation().containsKey(part.getName())) {
//			targetAct.getParticipation().put(part.getName(), part);
//		}
	}
	
	/**
	 * For each relationship we find in the base, see if there is a
	 * corresponding item in the extension and if not, add it.
	 */
	@Override
	protected void processActRelationship(ActRelationship ar) {
		super.processActRelationship( ar );
		String parentActPath = getLocation(-1);
		ActEx targetAct = (ActEx) ee.evaluate("#{" + parentActPath + "}");
		 if ((targetAct) != null && (!parentActPath.contains("relationships"))) {
		targetAct.getRelationships().add(ar);
		 }
//		processActRelationship( ar );
//		if (!targetAct.getRelationship().containsKey(ar.getName())) {
//			targetAct.getRelationship().put(ar.getName(), ar);
//		}
	}
	
}
