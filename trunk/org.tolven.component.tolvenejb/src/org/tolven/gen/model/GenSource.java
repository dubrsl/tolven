/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.gen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math.random.RandomData;

/**
 * A source of some kind of medical data to be generated. A source will have an affinity for a certain kind of 
 * patient and certain information about that patient. Typically, an individual source will only apply
 * to a certain age range or gender and perhaps patients with, or without, a specific problem.
 * Sources can be arranged in a hierarchy with the top-level looking for the broadest matching criteria
 * and then lower-levels looking for detailed matches.
 * @author John Churin
 */
public abstract class GenSource {
	private List<Matcher> matchers;
	private Scenario scenario;
	protected long matchCount = 0;
	protected long applyCount = 0; 
	protected RandomData rng;
	private long minimumSpacing = 0;
	private List<Double> repeatProbability;
	
	/**
	 * See if the specified patient should get data generated for this source and then do so.
	 * @param patient
	 */
	public void generate( GenMedical patient) {
		Matcher m = matchFirst(patient);
		if (m==null) return;
		// Ask the matcher to supply a reasonable event time
		Date eventTime = m.getEventTime(patient, rng, null);
		// At this point, we are 100% likely to create this event.
		apply( patient, eventTime );
		// Now check the repeats completely ignoring match criteria since we've already got
		// a match. 
		// TODO: There's a possible flaw here: Since a recurrance is independent of the original match
		// criteria, a disease that can recur is not limited by age ranges. But there may be no such disease.
		// It would be a disease that can recur but only within a certain age range, once the patient
		// is older, the recurrance probability, like the initial disease, drops to zero.
		for (Double p : getRepeatProbability()) {
			if (rng.nextUniform(0.0, 1.0) > p.doubleValue()) break;
			long next;
			if (eventTime.getTime()+getMinimumSpacing() >= patient.getNow().getTime()) break;
			next = rng.nextLong(eventTime.getTime()+getMinimumSpacing(), patient.getNow().getTime());
			eventTime = m.getEventTime(patient, rng, new Date(next));
			apply( patient, eventTime );
		}
	}

	public GenSource() {
	};
	
	/**
	 * Call each matcher looking for a match. The first Matcher to match is returned.
	 * A probability will be extrated from the matching entry and used to determine if the patient actually
	 * will have this item triggered.
	 * @return a matching child, or null
	 */
	protected Matcher matchFirst( GenMedical patient ) {
		for (Matcher m : getMatchers()) {
			if (m.match(patient) && fire( m)) return m;
		}
		return null;
	}
	
	/**
	 * This method determines if we should actually fire the trigger, which should have already been determined to match this
	 * sources criteria, should actually apply or not. For example, it 20% of males of a certain age will get
	 * X disease, then this method determines the 20%.
	 * @param matcher
	 * @return True if this patient is actually chosen to get the source. 
	 */
	protected boolean fire( Matcher matcher ) {
		return (rng.nextUniform(0.0, 1.0) < matcher.getProbability());
	}

	/**
	 * This method generates the actual data based on matching
	 * criteria. For example, if a patient qualifies for both match() and next() then apply()
	 * will generate one or more medical events appropriate to this source. 
	 * For the default behavior, we simply look for a match among our children, if any.
	 * Careful: at this point, we have 100% certainty that the parent matches. Thus,
	 * all probabilities below this level are relative to the parent. For example, if 20% of
	 * people get diabetes, then a child probability of 25% for 40-60 year olds means 25% of 
	 * that 20% or a net of 5% probability. the child probabilities do not need to add to 100%.
	 * For example, if nothing happens to a certain portion of the matching population, then it can 
	 * simply be omitted, or if you prefer, implement that fraction with nothing in apply function.
	 * You may or may not call super.apply(). If, for example, something happens for ALL diabetics,
	 * then this should be included in the parent level. The method can then call super.apply() which
	 * will apply additional medical data only to that matching group.
	 * @param patient
	 */
	protected void apply(GenMedical patient, Date eventTime) {
		// Now carry out the appropriate scenario
		// Compute scenario endTime (dod or now)
		long msDOD = patient.getDateOfDeath( ).getTime();
		long msNow = patient.getNow().getTime();
		Date endTime;
		if (msDOD < msNow) endTime = new Date( msDOD );
		else endTime = new Date( msNow );
		if (getScenario()!=null) getScenario().apply(patient, eventTime, endTime, rng);
	}
		
		
	/**
	 * Return the actual list of children, creating one if needed. A child is typically restricted by the parent.
	 * For example, the parent can determine that the patient is diabetic and then each child is
	 * only concerned with the age of the patient. In these cases, the child can delegate apply method to the parent
	 * if nothing is different among the different age groups except probability. But be careful: the
	 * course of a disease is often different based on patient age and other factors.
	 * @return
	 */
	public List<Matcher> getMatchers() {
		if (matchers==null) matchers = new ArrayList<Matcher>( 10 );
		return matchers;
	}

	public void setChildren(List<Matcher> matchers) {
		this.matchers = matchers;
	}

	/**
	 * Add a Matcher
	 * @param matcher
	 */
	public void addMatcher( Matcher matcher) {
		getMatchers().add( matcher );
	}

	/**
	 * Minimum spacing between occurances of this event.
	 * @return
	 */
	public long getMinimumSpacing() {
		return minimumSpacing;
	}

	public void setMinimumSpacing(long minimumSpacing) {
		this.minimumSpacing = minimumSpacing;
	}
	/**
	 * Each entry in this list is the probability that this same source will occur again for the nth time.
	 * If this list is empty, then the problem only occurs once (per lifetime). If the problem can
	 * repeat, then spacing should indicate the minimum. For example, pregnancy would have a spacing of, say, 10 months.
	 * Each probability in this list is based on the previous event actually occuring (100% probability).
	 * If breast cancer has an initial occurance of, say, 0.01 it might have a recurrance rate of say 0.20. 
	 * Pregnancy (in women) may occur 0.30 with a recurrance of 0.75, 0.20, etc. meaning that 20% of
	 * women with two pregnancies will have a third (it does not mean that 20% of all women will have three pregnancies).
	 * @return
	 */
	public List<Double> getRepeatProbability() {
		if (repeatProbability==null) repeatProbability = new ArrayList<Double>( 10 );
		return repeatProbability;
	}

	public void setRepeatProbability(List<Double> repeatProbability) {
		this.repeatProbability = repeatProbability;
	}

	public void addRepeatProbability( double probability) {
		getRepeatProbability().add(new Double( probability ));
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
}
