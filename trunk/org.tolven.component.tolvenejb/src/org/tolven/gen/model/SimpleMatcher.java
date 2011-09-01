package org.tolven.gen.model;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.math.random.RandomData;

/**
 * This simple matcher always matches (100% probability) with an event time of now as defined in the
 * GenMedical object.
 * @author John Churin
 *
 */
public class SimpleMatcher extends Matcher {

	public SimpleMatcher() {
		super(1.0);
	}

	@Override
	protected Date getEarliest( GenMedical patient ) {
		return patient.getNow();
	}

	@Override
	protected Date getLatest( GenMedical patient ) {
		return patient.getNow();
	}

	@Override
	protected boolean match(GenMedical patient) {
		return true;
	}
	/**
	 * Event time is overridden to be now.
	 */
	@Override
	public Date getEventTime( GenMedical patient, RandomData rng, Date minTime ) {
		return patient.getNow();
	}

}
