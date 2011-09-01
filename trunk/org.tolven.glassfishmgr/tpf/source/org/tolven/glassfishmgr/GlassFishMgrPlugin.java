/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Joseph Isaac
 * @version $Id$
 */
package org.tolven.glassfishmgr;

import java.awt.Component;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.tolven.glassfishmgr.gui.GlassFishConnectionPanel;
import org.tolven.glassfishmgr.gui.GlassFishMgrPanel;
import org.tolven.glassfishmgr.gui.GlassFishMgrUI;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.plugin.gui.GUIComponent;

/**
 * This plugin provides information about the Admin to GlassFish connection
 * 
 * @author Joseph Isaac
 *
 */
public class GlassFishMgrPlugin extends TolvenCommandPlugin implements GUIComponent {

    public static final String CMD_LINE_TEST_ADMIN_APPSERVER_OPTION = "testAdminAppServer";
    public static final String CMD_LINE_GUI_OPTION = "gui";

    private GlassFishMgr glassfishMgr;
    private GlassFishMgrPanel glassfishMgrPanel;
    private GlassFishMgrUI glassfishMgrUI;

    private Logger logger = Logger.getLogger(GlassFishMgrPlugin.class);

    private GlassFishMgr getGlassFishMgr() {
        if (glassfishMgr == null) {
            String adminId = getTolvenConfigWrapper().getAdminId();
            char[] adminIdPassword = getTolvenConfigWrapper().getPasswordHolder().getPassword(adminId);
            glassfishMgr = new GlassFishMgr(getTolvenConfigWrapper().getApplication().getAppRestfulURL(), getTolvenConfigWrapper().getApplication().getAuthRestfulURL());
            glassfishMgr.setAdminId(adminId);
            glassfishMgr.setAdminIdPassword(adminIdPassword);
            glassfishMgr.setTolvenConfigWrapper(getTolvenConfigWrapper());
        }
        return glassfishMgr;
    }

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    public void execute(String[] args) {
        logger.info("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        if (commandLine.hasOption(CMD_LINE_TEST_ADMIN_APPSERVER_OPTION)) {
            try {
                getGlassFishMgr().testAdminAppServerConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(getNestedMessage(ex));
            }
        } else if (commandLine.hasOption(CMD_LINE_GUI_OPTION)) {
            logger.info("Starting the glassfish manager gui...");
            getGlassFishMgrUI().setVisible(true);
        }
    }

    private GlassFishMgrUI getGlassFishMgrUI() {
        if (glassfishMgrUI == null) {
            glassfishMgrUI = new GlassFishMgrUI(getComponent());
        }
        return glassfishMgrUI;
    }

    private GlassFishMgrPanel getGlassFishMgrPanel() {
        if (glassfishMgrPanel == null) {
            glassfishMgrPanel = new GlassFishMgrPanel();
            glassfishMgrPanel.addTab("Connection", new GlassFishConnectionPanel(getGlassFishMgr()));
        }
        return glassfishMgrPanel;
    }

    private String getNestedMessage(Exception ex) {
        StringBuffer buff = new StringBuffer();
        Throwable throwable = ex;
        do {
            buff.append(throwable.getMessage() + "\n");
            throwable = throwable.getCause();
        } while (throwable != null);
        return buff.toString();
    }

    private CommandLine getCommandLine(String[] args) {
        GnuParser parser = new GnuParser();
        try {
            return parser.parse(getCommandOptions(), args, true);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(getClass().getName(), getCommandOptions());
            throw new RuntimeException("Could not parse command line for: " + getClass().getName(), ex);
        }
    }

    private Options getCommandOptions() {
        Options cmdLineOptions = new Options();
        OptionGroup optionGroup = new OptionGroup();
        Option testAdminAppServerOption = new Option(CMD_LINE_TEST_ADMIN_APPSERVER_OPTION, CMD_LINE_TEST_ADMIN_APPSERVER_OPTION, false, "\"test admin to appserver connection\"");
        optionGroup.addOption(testAdminAppServerOption);
        Option guiOption = new Option(CMD_LINE_GUI_OPTION, CMD_LINE_GUI_OPTION, false, "\"start the glassfish manager gui\"");
        optionGroup.addOption(guiOption);
        optionGroup.setRequired(true);
        cmdLineOptions.addOptionGroup(optionGroup);
        return cmdLineOptions;
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** stop ***");
    }

    @Override
    public String getComponentId() {
        return getDescriptor().getId();
    }

    @Override
    public Component getComponent() {
        return getGlassFishMgrPanel();
    }

    @Override
    public String getComponentName() {
        return "GlassFish";
    }

}
