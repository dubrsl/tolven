package org.tolven.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.tolven.logging.TolvenLogger;

public class ConfigureListener implements ServletContextListener {
    
    private boolean initialized = false;

    public void contextInitialized(ServletContextEvent event) {
        TolvenLogger.info("### INFO: WEB TIER LISTENING ###", ConfigureListener.class);
    }

    /**
     * Return true if the system appears to not have been initiallized
     * @return
     */
    public boolean isInitialized() {
        return initialized;
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

}
