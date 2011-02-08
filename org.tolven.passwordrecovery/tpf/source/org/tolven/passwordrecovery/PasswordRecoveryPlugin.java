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
package org.tolven.passwordrecovery;

import java.awt.Component;
import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.java.plugin.registry.ExtensionPoint;
import org.tolven.passwordrecovery.api.SecurityQuestionsImpl;
import org.tolven.passwordrecovery.gui.LoginPasswordRecoveryQuestionPanel;
import org.tolven.passwordrecovery.gui.LoginPasswordRecoveryQuestionUI;
import org.tolven.plugin.TolvenCommandPlugin;
import org.tolven.plugin.gui.GUIComponent;

/**
 * This plugin updates the appserver with security questions
 * 
 * @author Joseph Isaac
 *
 */
public class PasswordRecoveryPlugin extends TolvenCommandPlugin implements GUIComponent {

    public static final String EXTENSIONPOINT_SECURITY_QUESTIONS = "securityQuestions";

    public static final String CMD_LINE_LOAD_OPTION = "load";
    public static final String CMD_LINE_DISPLAY_OPTION = "display";
    public static final String CMD_LINE_ADD_OPTION = "add";
    public static final String CMD_LINE_CHANGE_OPTION = "change";
    public static final String CMD_LINE_NEW_OPTION = "new";
    public static final String CMD_LINE_REMOVE_OPTION = "remove";
    public static final String CMD_LINE_GUI_OPTION = "gui";

    private LoginPasswordRecoveryQuestionUI loginPasswordRecoveryQuestionUI;

    private Logger logger = Logger.getLogger(PasswordRecoveryPlugin.class);

    private LoginPasswordRecoveryQuestionUI getLoginPasswordRecoveryQuestionUI() {
        if (loginPasswordRecoveryQuestionUI == null) {
            loginPasswordRecoveryQuestionUI = new LoginPasswordRecoveryQuestionUI(getComponent());
        }
        return loginPasswordRecoveryQuestionUI;
    }

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    public void execute(String[] args) {
        logger.info("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        SecurityQuestionsImpl securityQuestionsImpl = getSecurityQuestionsImpl();
        if (commandLine.hasOption(CMD_LINE_DISPLAY_OPTION)) {
            logger.info("Displaying security questions...");
            securityQuestionsImpl.displaySecurityQuestions();
        } else if (commandLine.hasOption(CMD_LINE_LOAD_OPTION)) {
            File securityQuestionFile = getSecurityQuestionFile();
            if (securityQuestionFile != null) {
                logger.info("Loading security questions from: " + securityQuestionFile.getPath());
                securityQuestionsImpl.importSecurityQuestions(securityQuestionFile);
            }
        } else if (commandLine.hasOption(CMD_LINE_ADD_OPTION)) {
            String securityQuestion = commandLine.getOptionValue(CMD_LINE_ADD_OPTION);
            logger.info("Adding security question: " + securityQuestion);
            securityQuestionsImpl.addSecurityQuestion(securityQuestion);
        } else if (commandLine.hasOption(CMD_LINE_CHANGE_OPTION)) {
            if (!commandLine.hasOption(CMD_LINE_NEW_OPTION)) {
                throw new RuntimeException(CMD_LINE_NEW_OPTION + " option is required with option " + CMD_LINE_CHANGE_OPTION);
            }
            String changedQuestion = commandLine.getOptionValue(CMD_LINE_CHANGE_OPTION);
            String newQuestion = commandLine.getOptionValue(CMD_LINE_NEW_OPTION);
            logger.info("Changing security question FROM: " + changedQuestion + " TO: " + newQuestion);
            securityQuestionsImpl.changeSecurityQuestion(changedQuestion, newQuestion);
        } else if (commandLine.hasOption(CMD_LINE_REMOVE_OPTION)) {
            String securityQuestion = commandLine.getOptionValue(CMD_LINE_REMOVE_OPTION);
            logger.info("Removing security question: " + securityQuestion);
            securityQuestionsImpl.removeSecurityQuestion(securityQuestion);
        } else if (commandLine.hasOption(CMD_LINE_GUI_OPTION)) {
            logger.info("Starting the password recovery gui...");
            getLoginPasswordRecoveryQuestionUI().setVisible(true);
        }
        logger.info("security questions completed");
    }

    private File getSecurityQuestionFile() {
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_SECURITY_QUESTIONS);
        String securityQuestionFilename = extensionPoint.getParameterDefinition("login.security.questions").getDefaultValue();
        String eval_securityQuestionFilename = (String) evaluate(securityQuestionFilename, getDescriptor());
        if (eval_securityQuestionFilename == null) {
            return null;
        } else {
            File eval_securityQuestionFile = new File(eval_securityQuestionFilename);
            File securityQuestionsFile = null;
            if (isAbsoluteFilePath(eval_securityQuestionFile)) {
                securityQuestionsFile = eval_securityQuestionFile;
            } else {
                securityQuestionsFile = getFilePath(getDescriptor(), eval_securityQuestionFile.getName());
            }
            if (!securityQuestionsFile.exists()) {
                throw new RuntimeException("Missing securityQuestions file: " + (isAbsoluteFilePath(eval_securityQuestionFile) ? securityQuestionsFile.getPath() : getPluginZip(getDescriptor()) + "!/" + eval_securityQuestionFile));
            }
            return securityQuestionsFile;
        }
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
        Option dispalyOption = new Option(CMD_LINE_DISPLAY_OPTION, CMD_LINE_DISPLAY_OPTION, false, "\"display security questions from repositoryRuntime\"");
        optionGroup.addOption(dispalyOption);
        Option importOption = new Option(CMD_LINE_LOAD_OPTION, CMD_LINE_LOAD_OPTION, false, "\"load security questions from repositoryRuntime\"");
        optionGroup.addOption(importOption);
        Option addOption = new Option(CMD_LINE_ADD_OPTION, CMD_LINE_ADD_OPTION, true, "\"add a security question\"");
        optionGroup.addOption(addOption);
        Option changeOption = new Option(CMD_LINE_CHANGE_OPTION, CMD_LINE_CHANGE_OPTION, true, "\"change a security question\"");
        optionGroup.addOption(changeOption);
        Option removeOption = new Option(CMD_LINE_REMOVE_OPTION, CMD_LINE_REMOVE_OPTION, true, "\"remove a security question\"");
        optionGroup.addOption(removeOption);
        Option guiOption = new Option(CMD_LINE_GUI_OPTION, CMD_LINE_GUI_OPTION, false, "\"start the password recovery gui\"");
        optionGroup.addOption(guiOption);
        optionGroup.setRequired(true);
        cmdLineOptions.addOptionGroup(optionGroup);
        Option newValueOption = new Option(CMD_LINE_NEW_OPTION, CMD_LINE_NEW_OPTION, true, "\"supplied with -" + CMD_LINE_CHANGE_OPTION + " option\"");
        cmdLineOptions.addOption(newValueOption);
        return cmdLineOptions;
    }

    private SecurityQuestionsImpl getSecurityQuestionsImpl() {
        String adminId = getTolvenConfigWrapper().getAdminId();
        char[] adminPassword = getTolvenConfigWrapper().getPasswordHolder().getPassword(adminId);
        String appRestfulURL = getTolvenConfigWrapper().getApplication().getAppRestfulURL();
        String authRestfulURL = getTolvenConfigWrapper().getApplication().getAuthRestfulURL();
        return new SecurityQuestionsImpl(appRestfulURL, authRestfulURL, adminId, adminPassword);
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
        return new LoginPasswordRecoveryQuestionPanel(getSecurityQuestionsImpl());
    }

    @Override
    public String getComponentName() {
        return "Password Recovery";
    }

}
