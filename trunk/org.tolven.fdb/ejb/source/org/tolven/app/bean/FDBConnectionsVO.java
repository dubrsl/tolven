/**
 * 
 */
package org.tolven.app.bean;

import java.io.Serializable;

/**
 * @author mohammed
 *
 */
public class FDBConnectionsVO implements Serializable {
	/**
	 * Default Constructor
	 */
	public FDBConnectionsVO(){
		
	}

	/**
	 * Auto Generated Serial Version ID
	 */
	private static final long serialVersionUID = 1433494665394848423L;
	/**
	 * Variable to hold the pooling boolean
	 */
	 private boolean  isPooling;
	 /**
	  * Variable to hold the driver
	  */
	 private String	driverClass;
     
	/**
      * Variable to hold the URL
      */
	 private String dBURL;
     /**
      * Variable to hold the UserName
      */
	 private String username;
     /**
      * Variable to hold the factory Class
      */
	 private String factoryClass;
     /**
      * Variable to hold the Password
      */
	 private String password ;
     /**
      * Boolean to hold the debug flag
      */
	 private boolean  showDebugInfo;
	 /**
	  * Variable to hold the pool size
	  */
     private Integer poolSize;
     /**
      * Variable to hold the load limit
      */
     private Integer loadLimit;
	
	 public boolean isPooling() {
		return isPooling;
	}
	public void setPooling(boolean isPooling) {
		this.isPooling = isPooling;
	}

	public String getFactoryClass() {
		return factoryClass;
	}
	public void setFactoryClass(String factoryClass) {
		this.factoryClass = factoryClass;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isShowDebugInfo() {
		return showDebugInfo;
	}
	public void setShowDebugInfo(boolean showDebugInfo) {
		this.showDebugInfo = showDebugInfo;
	}
	public Integer getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(Integer poolSize) {
		this.poolSize = poolSize;
	}
	public Integer getLoadLimit() {
		return loadLimit;
	}
	public void setLoadLimit(Integer loadLimit) {
		this.loadLimit = loadLimit;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getdBURL() {
		return dBURL;
	}
	public void setdBURL(String dBURL) {
		this.dBURL = dBURL;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
