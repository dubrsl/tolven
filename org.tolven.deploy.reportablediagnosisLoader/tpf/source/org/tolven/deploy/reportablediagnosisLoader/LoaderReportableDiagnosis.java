package org.tolven.deploy.reportablediagnosisLoader;

import java.io.File;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;

public class LoaderReportableDiagnosis extends TolvenCommandPlugin {
protected Logger logger = Logger.getLogger(getClass());
    
    private String getAdminId() {
        return getTolvenConfigWrapper().getAdminId();
    }

    private char[] getAdminPassword() {
        return getTolvenConfigWrapper().getAdminPassword();
    }

    private String getAppRestfulRootURL() {
        return getTolvenConfigWrapper().getApplication().getAppRestfulURL();
    }

    private String getAuthRestfulRootURL() {
        return getTolvenConfigWrapper().getApplication().getAuthRestfulURL();
    }

	@Override
	public void execute(String[] args) throws Exception {
		logger.debug("*** execute ***");
		LoadReportableDiagnosis reportableDiagnosis = new LoadReportableDiagnosis(getAdminId(), getAdminPassword(), getAppRestfulRootURL(), getAuthRestfulRootURL());
   	 	File src = getFilePath("tpf/voc/reportableDiagnosis.txt");
        logger.info("Load ReportableDiagnosis from " + src.getPath() + "...");
        reportableDiagnosis.load(src);
        logger.info("Load ReportableDiagnosis completed");

	}

	@Override
	protected void doStart() throws Exception {
		logger.debug("*** start ***"); 

	}

	@Override
	protected void doStop() throws Exception {
		logger.debug("*** stop ***"); 

	}

}
