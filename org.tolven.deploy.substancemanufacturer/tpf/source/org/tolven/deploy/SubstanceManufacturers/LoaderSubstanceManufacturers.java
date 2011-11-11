package org.tolven.deploy.SubstanceManufacturers;

import java.io.File;

import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;

public class LoaderSubstanceManufacturers extends TolvenCommandPlugin {
	protected Logger logger = Logger.getLogger(getClass());
	
	private String getAdminId(){
		return getTolvenConfigWrapper().getAdminId();
	}
	
	private char[] getAdminPassword(){
		return getTolvenConfigWrapper().getAdminPassword();
	}
	
	private String getAppsRestfulRootURL(){
		return getTolvenConfigWrapper().getApplication().getAppRestfulURL();
	}
	
	private String getAuthRestfulRootURL(){
		return getTolvenConfigWrapper().getApplication().getAuthRestfulURL();
	}

	@Override
	public void execute(String[] args) throws Exception {
		logger.debug("********execute*******");
		LoadSubstanceManufacturer sm = new LoadSubstanceManufacturer(getAdminId(), getAdminPassword(), getAppsRestfulRootURL(), getAuthRestfulRootURL());
		File source = getFilePath("tpf/voc/SubstanceManufacturerList.txt");
		logger.info("Load SubstanceManufacturer from " + source.getPath() + "...");
		sm.load(source);
		logger.info("Load SubstanceManufacturer completed");

	}

	@Override
	protected void doStart() throws Exception {
		logger.debug("***********Start*******");

	}

	@Override
	protected void doStop() throws Exception {
		logger.debug("**********Stop********");

	}

}
