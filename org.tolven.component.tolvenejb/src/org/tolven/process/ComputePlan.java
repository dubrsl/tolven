package org.tolven.process;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;

import org.tolven.app.el.ELFunctions;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.TrimHeader;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActRelationshipDirection;
import org.tolven.trim.ActRelationshipType;
import org.tolven.trim.IVLTS;
import org.tolven.trim.PIVL;
import org.tolven.trim.PQ;
import org.tolven.trim.PQSlot;
import org.tolven.trim.TS;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TSEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

public class ComputePlan extends ComputeBase{
	private static final TrimFactory trimFactory = new TrimFactory();

	/**
	 * The top-level plan act which we will update effectiveTimeHigh as we compute occurrences.
	 * This attribute is only used within this method, it is not injected by the invoker.
	 */
	private Act planAct;
	
	private String template;
	private String templateBody;
	private String planStatus;

	public void compute( ) throws Exception {
		super.checkProperties();
		planStatus = getPlanStatus();
		TolvenLogger.info( "[ComputePlan, status=" + planStatus + "]", ComputePlan.class);
		// If we have no plan, safe to assume that the first one in is the plan. Otherwise,
		// get busy calculating occurrences for this act.
		if (planAct==null) {
			planAct=getAct();
			initializePlanEndTime();
		} else {
			removeOccurrences();
			initializeStartTime();
			generateOccurrences();
			updatePlanEndTime();
		}
		setTemplate( null );
	}
	
	public String getPlanStatus() {
		ActRelationship ar = ((ActEx)getTrim().getAct()).getRelationship().get("planStatus");
		Act planStatusAct = ar.getAct();
		return planStatusAct.getObservation().getValues().get(0).getCE().getCode();
	}
	
	/**
	 * At the start of a plan, we make the plan end time the same as the plan start time.
	 * This is important because each plan component will use the current plan end time
	 * as its start time and then adjust the end time as it intantiates occurrences.
	 * @throws ParseException 
	 */
	public void initializePlanEndTime() throws ParseException {
		if (getAct().getEffectiveTime()!=null && getAct().getEffectiveTime().getIVLTS()!=null) {
			IVLTS ivlts = getEffectiveTimeIVLTS( getAct() );
			if (ivlts.getLow()!=null) {
				TSEx ts = (TSEx) ivlts.getLow().getTS();
				ivlts.getHigh().setTS(toTS( ts.getDate() ));
			}
		}
	}
	
	/**
	 * the start time of this act is taken from the current end time of the
	 * planAct. This method depends on initializePlanEndTime being called once.
	 * @throws ParseException 
	 */
	public void initializeStartTime() throws ParseException {
		IVLTS ivltsPlan = getEffectiveTimeIVLTS( planAct );
		TSEx tsPlanEnd = (TSEx) ivltsPlan.getHigh().getTS();
		IVLTS ivltsAct = getEffectiveTimeIVLTS( getAct() );
		if (ivltsAct!=null) {
			ivltsAct.getLow().setTS(toTS( tsPlanEnd.getDate() ));
		} else {
			getAct().getEffectiveTime().setTS(toTS( tsPlanEnd.getDate() ));
		}
	}
	
	/**
	 * For a zero with act, we just make width zero and end=start.
	 * @throws ParseException
	 */
	public void zeroWidthAct() throws ParseException {
		IVLTS ivlts = getEffectiveTimeIVLTS( getAct() );
		if (ivlts!=null) {
			TSEx tsStart = (TSEx) ivlts.getLow().getTS();
			ivlts.getHigh().setTS(toTS( tsStart.getDate() ));
			PQSlot slot = trimFactory.createPQSlot();
			PQ pq = trimFactory.createPQ();
			pq.setValue(0.0);
			slot.setPQ(pq);
			ivlts.setWidth(slot);
		}
	}
	
	/**
	 * Use our endTime to update the endTime of the plan
	 * TODO: We should, but don't also compute the duration of the plan. 
	 * @throws ParseException 
	 */
	public void updatePlanEndTime( ) throws ParseException {
		IVLTS ivltsAct = getEffectiveTimeIVLTS( getAct() );
		IVLTS ivltsPlan = getEffectiveTimeIVLTS( planAct );
		if (ivltsAct!=null) {
			TSEx tsEnd = (TSEx) ivltsAct.getHigh().getTS();
			ivltsPlan.getHigh().setTS(toTS( tsEnd.getDate() ));
		}
	}
	
	/**
	 * Generate occurrences according to the PIVL. If no PIVL, then
	 * endTime of this act is same as start time.
	 * @param act
	 * @throws ParseException 
	 * @throws JAXBException 
	 */
	public void generateOccurrences( ) throws ParseException, JAXBException {
		long count = 1;
		PQ period = null;
		TSEx ts = null;
		IVLTS ivlts = null;
		PIVL pivl = getEffectiveTimePIVL( getAct() );
		
		if (pivl!=null) {
			count = pivl.getCount().getINT().getValue();
			period = pivl.getPeriod().getPQ();
			ivlts = getEffectiveTimeIVLTS( getAct() );
			ts = (TSEx) ivlts.getLow().getTS();
		} else {
			ts = getEffectiveTimeTS( getAct() );
		}
		// The time that will become the end time of this act.
		// As it is updated, the movingTime is used as the start time for each occurrence.
		GregorianCalendar movingTime = new GregorianCalendar();
		movingTime.setTime( ts.getDate()); 
		// Loop for each occurrence specified
		for (int c = 0; c < count; c++) {
			// If this isn't a good day, move the time ahead.
			while( movingTime.get(GregorianCalendar.DAY_OF_WEEK)==GregorianCalendar.SATURDAY || 
					movingTime.get(GregorianCalendar.DAY_OF_WEEK)==GregorianCalendar.SUNDAY ) {
				movingTime.add(GregorianCalendar.DAY_OF_WEEK, 1 );
			}
			if (getTemplate()!=null && "active".equals(planStatus)) {
				ActRelationship ar = parseTemplate(getAct(), movingTime.getTime());
				getAct().getRelationships().add(ar);
			}
			if (pivl!=null) {
				// OK, now we need to advance the act end time
				movingTime.setTime(addWidth( movingTime.getTime(), period ));
			}
		}
		if (pivl!=null) {
			// Now set the end time for this act
			ivlts.getHigh().setTS(toTS( movingTime.getTime() ));
		}
	}

	public ActRelationship parseTemplate(Act act, Date occurrenceTime ) throws JAXBException {
		TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
		ee.addVariable("source", act);
		ee.addVariable("occurrenceTime", occurrenceTime);
		TrimEx templateTrim = getTrimBean().parseTrim( getTemplateBody(), ee );
		ActRelationship ar = trimFactory.createActRelationship();
		ar.setTypeCode(ActRelationshipType.OCCR);
		ar.setDirection(ActRelationshipDirection.IN);
		ar.setName("occurrence");
		ar.setAct(templateTrim.getAct());
		return ar;
	}
	
	/**
	 * Starting with plan start time, compute start times for everything else including the
	 * end time of the plan. 
	 * We use duration (when available) and repeatNumber (when available)
	 * If the plan start time is not available, then we cannot compute. 
	 */
	public void computePlan() throws Exception {
		Act act = getAct();
		computeDuration( act );
		if (act.getEffectiveTime()!=null && act.getEffectiveTime().getIVLTS()!=null) {
			IVLTS ivlts = getEffectiveTimeIVLTS( act );
			if (ivlts.getLow()!=null) {
				TSEx ts = (TSEx) ivlts.getLow().getTS();
				Date endDate = sequencePlan( act, ts.getDate() );
				ivlts.getHigh().setTS(toTS( endDate ));
			}
		}
	}
	
	public static PIVL getEffectiveTimePIVL( Act act ) {
		if (act.getEffectiveTime()!=null) {
			return act.getEffectiveTime().getPIVL();
		}
		return null;
	}

	public static IVLTS getEffectiveTimeIVLTS( Act act ) {
		if (act.getEffectiveTime()!=null) {
			return act.getEffectiveTime().getIVLTS();
		}
		return null;
	}
	
	public static TSEx getEffectiveTimeTS( Act act ) {
		if (act.getEffectiveTime()!=null) {
			return (TSEx) act.getEffectiveTime().getTS();
		}
		return null;
	}
	
	public static void setEffectiveTimeWidth( Act act, long count, PQ period) {
		IVLTS ivlts = getEffectiveTimeIVLTS( act );
		if (ivlts!=null) {
			PQSlot slot = trimFactory.createPQSlot();
			PQ pq = trimFactory.createPQ();
			pq.setUnit(period.getUnit());
			pq.setValue(period.getValue()*count);
			slot.setPQ(pq);
			ivlts.setWidth(slot);
		}
	}

	/**
	 * For our children acts, look for nextSteps containing PIVL and calculate
	 * the width of the effectiveTime.
	 * @param act
	 * @return
	 */
	public static void computeDuration( Act act ) {
		PIVL pivl = getEffectiveTimePIVL( act );
		if (pivl!=null ) {
			setEffectiveTimeWidth( act, pivl.getCount().getINT().getValue(), pivl.getPeriod().getPQ());
		}
		for (ActRelationship ar : act.getRelationships()) {
			if (ar.getAct()!=null) {
				computeDuration( ar.getAct() );
			}
		}
	}
	
	public static TS toTS( Date date ) {
		TSEx ts = (TSEx) trimFactory.createTS();
		ts.setDate(date);
		return ts;
	}
	
	public static Date addWidth( Date from, PQ width ) {
		GregorianCalendar to = new GregorianCalendar(  );
		to.setTime(from);
		int widthValue = new Long(Math.round(width.getValue())).intValue();
		if (width.getUnit().startsWith("day")) {
			to.add( GregorianCalendar.DAY_OF_MONTH, widthValue);
		}
		// TODO: Lots of other units to deal with here
		return to.getTime();
	}
	
	/**
	 * Sequence the plan by setting our end date based on supplied startDate.
	 * @param act
	 * @param startDate set this as start date of this act
	 */
	public static Date sequencePlan( Act act, Date startDate ) {
		IVLTS ivlts = getEffectiveTimeIVLTS( act );
		Date endDate;
		if (ivlts!=null) {
			// Set low interval to start time.
			ivlts.getLow().setTS(toTS( startDate));
			endDate = addWidth(startDate, ivlts.getWidth().getPQ());
			ivlts.getHigh().setTS(toTS( endDate ));
		} else {
			// We have no width
			endDate = startDate;
			// But can store a timestamp for this one
			if (act.getEffectiveTime( )!=null) {
				TSEx ts = (TSEx) trimFactory.createTS();
				ts.setDate(endDate);
				act.getEffectiveTime( ).setTS(ts);
			}
		}
		for (ActRelationship ar : act.getRelationships()) {
			if (ar.getAct()!=null) {
				endDate = sequencePlan( ar.getAct(), endDate );
			}
		}
		return endDate;
	}

	/**
	 * Remove any AR nodes named occurrence in this act.
	 * @param act
	 */
	public void removeOccurrences( ) {
		this.cleanRelationships("occurrence");
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
		templateBody = null;
	}

	public String getTemplateBody() {
		if (templateBody==null) {
			TrimHeader trimHeader = getTrimBean().findTrimHeader(template);
			templateBody = new String(trimHeader.getTrim());
		}
		return templateBody;
	}
	

}
