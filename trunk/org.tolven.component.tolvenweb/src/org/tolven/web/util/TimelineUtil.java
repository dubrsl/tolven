package org.tolven.web.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimelineUtil {
	private Map<String,List<Date>> dateRangeMap = null;
	
	public TimelineUtil() {
		dateRangeMap = new HashMap<String, List<Date>>();
	}
	public static TimelineUtil getInstance(){
		return new TimelineUtil();
	}
	
	public void setDatesRange(String timelineId,List<Date> dates){
		dateRangeMap.put(timelineId, dates);
	}
	public List<Date> getDatesRange(String timelineId){
		return dateRangeMap.get(timelineId);
	}
}
