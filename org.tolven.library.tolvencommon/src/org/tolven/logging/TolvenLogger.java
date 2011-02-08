package org.tolven.logging;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * <p>Provide Tolven-specific log4j behavior. There are three ways to use TolvenLogger:</p>
 * <ol>
 * <li>Do not call the <code>initialize()</code> and do not call <code>defaultInitialize()</code>: 
 * This approach assumes that LOG4J has been initialized elsewhere. In that case, calls to TolvenLogger will use the
 * configuration that is in place.
 * If LOG4J has in fact not been initialized, then a warning message will be output by the log4j 
 * system and no further log output will be produced when TolvenLogger methods are called.</li>
 * <li>Call <code>TolvenLogger.defaultInitialize()</code>: 
 * This approach uses the Basic log4j configuration. If there happens to be a log4j.xml file in the classpath, then LOG4J will use it.
 * Otherwise, LOG4J defaults to a simple console appender.</li>
 * <li>Call TolvenLogger.initialize() with properties (or parameters) that specify the configuration file and/or log file to use.
 * This is the approach used by the Tolven configuration manager although if the properties are missing, behavior defaults to the next approach.</li>
 * <li>Call TolvenLogger.initialize() with one or both properties missing (or with null parameters) and defaults will be assigned as described in the method description. </li>
 * </ol>
 * <p>In any case, you can then make calls to TolvenLogger.info, TolvenLogger.error, TolvenLogger.debug and the output will
 * be directed and formatted as specified in the log4j configuration.</p>
 * <p>A reminder that the standard LOG4J log-level priority is: DEBUG < INFO < WARN < ERROR < FATAL. For example, if the logging level is WARN, then .warn, .error, and .fatal messages will be displayed.
 * The tolven default logging level is INFO</p>
 * @author Anil and John Churin
 *
 */
public class TolvenLogger {
	public static final String TOLVEN_LOG4J = "tolven-log4j.xml";
	public static final String DEFAULT_LOG_FILE = System.getProperty("user.dir") + "/tolven.log";
	public static final String LOG_FILE_PROPERTY = "tolven.log.file";
    private Logger logger;
    
    /**
     * <p>Initialize log4j logging using the Tolven appender specification.</p>
     * <p>Note: This method should <i>not</i> be called within an environment such as JBoss that has a separate
     * log4j configuration. In that case, the TolvenLogger will use the appender configuration supplied by that
     * environment.</p>
     * <p>Two properties control the behavior of this method.
     * <ul>
     * <li><code>log4j.configuration</code> contains the name of the file containing the log4j configuration. If not specified, the
     * a file named tolven-log4j.xml on the classpath will be used.</li>
     * <li><code>tolven.log.file</code> contains the name of the log file. This file will be created if it does not already exist. If the property is not found, 
     * a default file named <code>${user.dir}/tolven.log</code> will be used.</li>
     * </ul>
     * </p>
     */
    public static void initialize() {
	        String log4jConfiguration = System.getProperty("log4j.configuration");
    		if (log4jConfiguration == null) {
    			log4jConfiguration = TOLVEN_LOG4J;
    		}
	        String logFilename = System.getProperty(LOG_FILE_PROPERTY);
	        initialize( log4jConfiguration, logFilename );
    }
    
    /**
     * <p>Initialize log4j logging using the Tolven appender specification.</p>
     * <p>Note: This method should <i>not</i> be called within an environment such as JBoss that has a separate
     * log4j configuration. In that case, the TolvenLogger will use the appender configuration supplied by that
     * environment.</p>
     * @param log4jConfiguration The name of the file containing the log4j configuration. If null, the
     * a file named tolven-log4j.xml on the classpath will be used.
     * @param logFilename The name of the log file. This file will be created if it does not already exist. If null, 
     * a default file named <code>${user.dir}/tolven.log</code> will be used.
     */
    public static void initialize(String log4jConfiguration, String logFilename ) {
    	try {
            File logFile;
    		if (logFilename != null) {
    			logFile = new File(logFilename);
	        } else {
	        	logFile = new File(DEFAULT_LOG_FILE);
	        }
    		System.setProperty(LOG_FILE_PROPERTY, logFile.getAbsolutePath());
            logFile.getParentFile().mkdirs();
            logFile.createNewFile();
            defaultInitialize();
            Logger.getRootLogger().info("Start log4j - Configuration: " + log4jConfiguration + ", logFileName: " + logFile.getAbsolutePath());
			BasicConfigurator.resetConfiguration();
			URL configURL;
			try {
				configURL = new URL( log4jConfiguration );
			} catch (Exception e) {
				configURL = Loader.getResource(log4jConfiguration);
			}
            DOMConfigurator.configure(configURL);
		} catch (Exception e) {
			throw new RuntimeException( "Exception while initializing Tolven log4j. log4jConfiguration: " + log4jConfiguration + " logFilename: " + logFilename, e);
		}
    }

    public static void initialize(String log4jConfiguration, File logFile) {
        try {
            File configFile = new File(log4jConfiguration );
            initialize(configFile.toURI().toURL().toExternalForm(), logFile.getPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not convert logFile: '" + logFile.getPath() + "' to a URI");
        }
    }
    
    public static void defaultInitialize() {
    	  BasicConfigurator.configure();
    }
    
    public TolvenLogger(Class<?> c) {
        logger = Logger.getLogger(c);
    }

    public TolvenLogger(String name) {
        logger = Logger.getLogger(name);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public static TolvenLogger getLogger(Class<?> aClass) {
        return new TolvenLogger(aClass);
    }

    public static void setLevel(Level level, Class<?> aClass) {
    	Logger log = Logger.getLogger(aClass);
        log.setLevel(level);
    }
    
    public static void setLevel(Level level) {
    	Logger log = Logger.getLogger(TolvenLogger.class);
        log.setLevel(level);
    }

    public void debug(Object message, java.lang.Throwable t) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(message, t);
        }
    }

    public void debug(Object message) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(message);
        }
    }
    
    public void info(Object message, java.lang.Throwable t) {
        if (getLogger().isInfoEnabled()) {
            getLogger().info(message, t);
        }
    }

    public void info(Object message) {
        if (getLogger().isInfoEnabled()) {
            getLogger().info(message);
        }
    }

    public void warn(Object message, java.lang.Throwable t) {
        getLogger().warn(message, t);
    }

    public void warn(Object message) {
        getLogger().warn(message);
    }

    public void error(Object message, java.lang.Throwable t) {
        getLogger().error(message, t);
    }

    public void error(Object message) {
        getLogger().error(message);
    }

    public static void debug(Object message, java.lang.Throwable t, Class<?> aClass) {
        Logger log = Logger.getLogger(aClass);
        if (log.isDebugEnabled()) {
            log.debug(message, t);
        }
    }

    public static void debug(Object message, Class<?> aClass) {
        Logger log = Logger.getLogger(aClass);
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
    }
    
    public static void info(Object message, java.lang.Throwable t, Class<?> aClass) {
        Logger log = Logger.getLogger(aClass);
        if (log.isInfoEnabled()) {
            log.info(message, t);
        }
    }

    public static void info(Object message, Class<?> aClass) {
        Logger log = Logger.getLogger(aClass);
        if (log.isInfoEnabled()) {
            log.info(message);
        }
    }

    public static void warn(Object message, java.lang.Throwable t, Class<?> aClass) {
        Logger.getLogger(aClass).warn(message, t);
    }

    public static void warn(Object message, Class<?> aClass) {
        Logger.getLogger(aClass).warn(message);
    }

    public static void error(Object message, java.lang.Throwable t, Class<?> aClass) {
        Logger.getLogger(aClass).error(message, t);
    }

    public static void error(Object message, Class<?> aClass) {
        Logger.getLogger(aClass).error(message);
    }

    public static TolvenLogger getLogger(String classname) {
        return new TolvenLogger(classname);
    }
    
    public static void debug(Object message, java.lang.Throwable t, String classname) {
        Logger log = Logger.getLogger(classname);
        if (log.isDebugEnabled()) {
            log.debug(message, t);
        }
    }

    public static void debug(Object message, String classname) {
        Logger log = Logger.getLogger(classname);
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
    }
    
    public static void info(Object message, java.lang.Throwable t, String classname) {
        Logger log = Logger.getLogger(classname);
        if (log.isInfoEnabled()) {
            log.info(message, t);
        }
    }

    public static void info(Object message, String classname) {
        Logger log = Logger.getLogger(classname);
        if (log.isInfoEnabled()) {
            log.info(message);
        }
    }

    public static void warn(Object message, java.lang.Throwable t, String classname) {
        Logger.getLogger(classname).warn(message, t);
    }

    public static void warn(Object message, String classname) {
        Logger.getLogger(classname).warn(message);
    }

    public static void error(Object message, java.lang.Throwable t, String classname) {
        Logger.getLogger(classname).error(message, t);
    }

    public static void error(Object message, String classname) {
        Logger.getLogger(classname).error(message);
    }

}
