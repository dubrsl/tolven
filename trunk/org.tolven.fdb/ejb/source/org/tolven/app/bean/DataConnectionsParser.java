/**
 * 
 */
package org.tolven.app.bean;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.logging.TolvenLogger;
// import org.tolven.surescripts.SureScriptsCommunicator;

/**
 * @author mohammed
 *
 */
public class DataConnectionsParser {
	
	private TolvenPropertiesLocal tproperties;
	
	public DataConnectionsParser(){
		try {
			InitialContext ctx = new InitialContext();
			if(tproperties == null){
				tproperties = (TolvenPropertiesLocal) ctx.lookup("java:global/tolven/tolvenEJB/TolvenProperties!org.tolven.core.TolvenPropertiesLocal");
			}
		}catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static FDBConnectionsVO dbConnectionSettingsGenerator(){
        FDBConnectionsVO fdbConnVO = new FDBConnectionsVO();
        DataConnectionsParser dcp = new DataConnectionsParser();
       
    	if (dcp.tproperties.getProperty("fdb.driver") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.driver- .", DataConnectionsParser.class);
			return null;
		} else if (dcp.tproperties.getProperty("fdb.url") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.url- .", DataConnectionsParser.class);
			return null;
		} else if (dcp.tproperties.getProperty("fdb.username") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.username- .", DataConnectionsParser.class);
			return null;
		} else if (dcp.tproperties.getProperty("fdb.password") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.password- .", DataConnectionsParser.class);
			return null;
		} else if (dcp.tproperties.getProperty("fdb.showDebugInfo") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.showDebugInfo- .", DataConnectionsParser.class);
			return null;
		} else if (dcp.tproperties.getProperty("fdb.usePooling") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.usePooling- .", DataConnectionsParser.class);
			return null;
		} else if (dcp.tproperties.getProperty("fdb.poolSize") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.poolSize- .", DataConnectionsParser.class);
			return null;
		} else if (dcp.tproperties.getProperty("fdb.loadLimit") == null) {
			TolvenLogger.error("PROPERTY MISSING : -fdb.loadLimit- .", DataConnectionsParser.class);
			return null;
		}
    	
    	fdbConnVO.setDriverClass(dcp.tproperties.getProperty("fdb.driver"));
    	fdbConnVO.setdBURL(dcp.tproperties.getProperty("fdb.url"));
    	fdbConnVO.setUsername(dcp.tproperties.getProperty("fdb.username"));
    	fdbConnVO.setPassword(dcp.tproperties.getProperty("fdb.password"));
    	if (dcp.tproperties.getProperty("fdb.showDebugInfo") == "false") {
    		fdbConnVO.setShowDebugInfo(false);
		} else {
			fdbConnVO.setShowDebugInfo(true);
		}
    	if (dcp.tproperties.getProperty("fdb.usePooling") == "false") {
    		fdbConnVO.setPooling(false);
		} else {
			fdbConnVO.setPooling(true);
		}
    	fdbConnVO.setPoolSize(Integer.parseInt(dcp.tproperties.getProperty("fdb.poolSize")));
    	fdbConnVO.setLoadLimit(Integer.parseInt(dcp.tproperties.getProperty("fdb.loadLimit")));
    	
    	return fdbConnVO;
	}   
	public static void main(String[] hh){
		DataConnectionsParser.dbConnectionSettingsGenerator();
	}

}
