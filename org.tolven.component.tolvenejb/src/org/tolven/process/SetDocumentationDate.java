package org.tolven.process;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.tolven.trim.ex.TrimFactory;

public class SetDocumentationDate extends ComputeBase {
	TrimFactory factory = new TrimFactory();
	Logger logger = Logger.getLogger(this.getClass());
	@Override
	public void compute() throws Exception {
		logger.info("Start: compute");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
		getTrim().getAct().getAvailabilityTime().setTS(factory.createNewTS(new Date()));
		logger.info("End: compute");
	}

}
