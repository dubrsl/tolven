package org.tolven.deploy.cvximmunization;

import java.io.File;

import org.apache.log4j.Logger;

import org.tolven.plugin.TolvenCommandPlugin;

public class LoaderImmunization extends TolvenCommandPlugin {
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
        LoadImmunizations lp = new LoadImmunizations(getAdminId(), getAdminPassword(), getAppRestfulRootURL(), getAuthRestfulRootURL());
        File src = getFilePath("tpf/voc/Immunization.txt");
        logger.info("Load Immunizations from " + src.getPath() + "...");
        lp.load(src);
        logger.info("Load Immunizations completed");

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
