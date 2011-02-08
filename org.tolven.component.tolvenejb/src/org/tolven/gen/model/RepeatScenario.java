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

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.math.random.RandomData;

/**
 * Repeat the supplied a scenario periodically
 * @author John Churin
 *
 */
public class RepeatScenario extends Scenario {
	private Scenario scenarioToRepeat;
	private int intervalType;
	private int intervalValue;
	private double deviation;
	private boolean resetClock;
	private int startOffset;
	private int maxCount;
	private Scenario futureScenario;
	private int futureCount;
	
	/**
	 * Because this class uses date arithmatic, it's possible that it could look out of control so
	 * we have an intrinsic maximum. If we hit the maximum, throw an IllegalArgumentException suggesting that
	 * the repeat interval must be wrong.
	 */
	public static int MAX_REPEAT = 10000;

	/**
	 * Repeating a scenario on a periodic basis
	 * @param intervalType From GregorianCalendar (YEAR, MONTH, DAY, etc)
	 * @param intervalValue How many calendar units is the interval (eg 12 months, 1 year, etc)
	 * @param deviation Standard deviation. 0.0 means the scenario is repeated right on schedule each time. The deviation is relative to the
	 * interval so. So the actual applied deviation is multiplied by the number of milliseconds in the specified intervalType (eg months*ms*deviation).
	 * @param resetClock If true, the next instance will be based on the actual time of the current instance. If false, then each instance will
	 * be based on the interval, exactly. For example, the patient might actually show up a few days early for an annual exam. If true, then
	 * the next exam will be 12 months from the visit. If false, it will be 12 months from when the visit SHOULD have occured.  
	 * @param start how many calendar periods to wait before the first repeat (zero means the first repeat occurs at the start time exactly)
	 * @param maxCount If non-zero, means number of times to run the scenario. The end time is alway limited by patient death or the current date
	 * regardless of this value. If specified as zero means to repeat until one of the other reasons to end the repeat.
	 * @param scenarioToRepeat The scenario to be repeated
	 */
	public RepeatScenario( int intervalType, int intervalValue, double deviation, boolean resetClock, int startOffset, int maxCount, Scenario scenarioToRepeat ) {
		this( intervalType, intervalValue, deviation, resetClock, startOffset, maxCount, scenarioToRepeat, null, 0);
	}
	/**
	 * This scenario is a bit fancier because it also defines a future scenario.
	 * Typically used to include appointments in addition to the events of the past.
	 * @param intervalType
	 * @param intervalValue
	 * @param deviation
	 * @param resetClock
	 * @param startOffset
	 * @param maxCount
	 * @param scenarioToRepeat The scenario to be repeated
	 * @param futureScenario An alternate scenario to repeated in the future
	 * @param futureCount How many times to repeat the scenario in the future (over and above maxCount)
	 */
	public RepeatScenario( int intervalType, int intervalValue, double deviation, boolean resetClock, int startOffset, int maxCount, Scenario scenarioToRepeat, Scenario futureScenario, int futureCount ) {
		this.intervalType = intervalType;
		this.intervalValue = intervalValue;
		this.setDeviation(deviation);
		this.resetClock = resetClock;
		this.scenarioToRepeat = scenarioToRepeat;
		this.startOffset = startOffset;
		if (maxCount == 0) this.maxCount = MAX_REPEAT;
		else this.maxCount = maxCount;
		this.setTitle("Repeat (" + intervalType + "*" + intervalValue + " " + deviation + ")" + scenarioToRepeat.getTitle());
		this.futureScenario = futureScenario;
		this.futureCount = futureCount;
	}

	/**
	 * To apply this scenario, we will modify the start time of the scenario to repeat to
	 * be the selected event time. For example, if the repeat interval is one year (in milliseconds), 
	 * and apply is called with a start time around 1992, then the child scenario is called
	 * with a start time of 1977, 1978, to the end-time (present day or death being most common
	 * for chronic diseases or the duration of the disease for acute diseases).
	 * Since most repeat scenarios represent followup activity, the start date is often offset 
	 * by some interval. Thus, with a startOffset of 12 (months), and the 
	 * startDate were to be 1976, then the first "repeat" will be around 1977. 
	 */
	public boolean apply(GenMedical patient, Date startTime, Date endTime, RandomData rng ) {
		GregorianCalendar clock = new GregorianCalendar();
		clock.setTime(startTime);
		boolean rslt = false;
		int count = -startOffset; 
//		TolvenLogger.info( getTitle() + " of " + getScenarioToRepeat().getTitle() + " Ranging from " + clock.getTime() + " to " + endTime, RepeatScenario.class); 
		while ( true ) {
			if (count++ >= MAX_REPEAT) throw new IllegalArgumentException( "Maximum repeat for scenario " + getTitle());
			if (count > maxCount) break;
			// Now pick a random time around that clock setting unless this is the first instance or there is no deviation
			Date instanceTime;
			if (getDeviation()==0.0 || count <= 0) {
				instanceTime =  new Date( clock.getTimeInMillis());
			} else {
				instanceTime = new Date( ((long)rng.nextGaussian(clock.getTimeInMillis(), getDeviationMs())));
			}
			// Needs to be before the end time, otherwise, we're done.
			if (instanceTime.after(endTime)) break;
			// Now apply the instance (if we're past the startOffset)
			if (count>0) {
				getScenarioToRepeat().apply(patient, instanceTime, endTime, rng );
			}
			// Reset the clock if requested
			if (resetClock) clock.setTime(instanceTime);
			// Advance the clock with no randomness
			clock.add(intervalType, intervalValue);
			rslt = true;
		}
		// If there's a future count, then we keep going by calling the futureScenario
		// Note: Logic is similar but not identical to above.
		for ( int x = 0; x < getFutureCount(); x++) {
			Date instanceTime;
			if (getDeviation()==0.0 ) {
				instanceTime =  new Date( clock.getTimeInMillis());
			} else {
				instanceTime = new Date( ((long)rng.nextGaussian(clock.getTimeInMillis(), getDeviationMs())));
			}
			// 
			// In the future, start and end time are the same.
			getFutureScenario().apply(patient, instanceTime, instanceTime, rng );
			// Reset the clock if requested
			if (resetClock) clock.setTime(instanceTime);
			// Advance the clock with no randomness
			clock.add(intervalType, intervalValue);
		}
		return rslt;
	}

	public Scenario getScenarioToRepeat() {
		return scenarioToRepeat;
	}

	public void setScenarioToRepeat(Scenario scenarioToRepeat) {
		this.scenarioToRepeat = scenarioToRepeat;
	}

	public double getDeviation() {
		return deviation;
	}
	public double getDeviationMs() {
		long units;
		if (intervalType == GregorianCalendar.MINUTE) units = 60L*1000L;
		else if (intervalType == GregorianCalendar.HOUR) units = 60L*60L*1000L;
		else if (intervalType == GregorianCalendar.DAY_OF_WEEK || intervalType == GregorianCalendar.DAY_OF_MONTH) units = 24L*60L*60L*1000L;
		else if (intervalType == GregorianCalendar.MONTH) units = 30L*24L*60L*60L*1000L;
		else if (intervalType == GregorianCalendar.YEAR) units = 365L*24L*60L*60L*1000L;
		else units = 1000L;
		// Calculate it in ms
		return deviation*units;
	}

	public void setDeviation(double deviation) {
		this.deviation = deviation;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	public int getFutureCount() {
		return futureCount;
	}
	public void setFutureCount(int futureCount) {
		this.futureCount = futureCount;
	}
	public Scenario getFutureScenario() {
		return futureScenario;
	}
	public void setFutureScenario(Scenario futureScenario) {
		this.futureScenario = futureScenario;
	}

}
