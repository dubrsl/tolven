/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

/**
 * This class augements IzPack for Tolven installation.
 * 
 * @author Joseph Isaac
 * 
 */
public class TolvenInstaller {

    public static final String CMD_LINE_CONF_OPTION = "conf";
    public static final String ENV_CONF = "TOLVEN_CONFIG_DIR";
    public static final String CMD_LINE_PLUGINSXMLTEMPLATE_OPTION = "pluginsXMLTemplate";

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Starting Tolven installation...");
            CommandLine commandLine = getCommandLine(args);
            String configDirname = getCommandLineConfigDir(commandLine);
            File configDir = new File(configDirname);
            System.out.println("Selected tolven-config Directory:\t" + configDir.getPath());
            File binDirectory = new File(System.getProperty("user.dir"));
            File installDir = binDirectory.getParentFile();
            File pluginsXMLTemplate = getPluginsXMLTemplate(commandLine, installDir);
            if (!pluginsXMLTemplate.exists()) {
                throw new RuntimeException("Template plugins.xml file not found: " + pluginsXMLTemplate.getPath());
            }
            System.out.println("Template plugins XML:\t" + pluginsXMLTemplate.getPath());
            File bootPropertiesTemplate = new File(installDir, "installer/template-tolven-config/boot.properties");
            if (!bootPropertiesTemplate.exists()) {
                throw new RuntimeException("Template boot.properties not found: " + bootPropertiesTemplate.getPath());
            }
            System.out.println("Template boot.properties:\t" + bootPropertiesTemplate.getPath());
            File repositoryLocalTemplate = new File(installDir, "installer/template-tolven-config/repositoryLocal");
            if (!repositoryLocalTemplate.exists()) {
                throw new RuntimeException("Template repositoryLocal not found: " + repositoryLocalTemplate.getPath());
            }
            System.out.println("Template repositoryLocal:\t" + repositoryLocalTemplate.getPath());
            upgradeConfigDir(configDir, installDir, pluginsXMLTemplate, bootPropertiesTemplate, repositoryLocalTemplate);
            File templateBinDir = new File(binDirectory.getParent(), "installer/bin");
            updateScripts(binDirectory, templateBinDir, configDir);
            System.out.println("\n*** Installation successful ***");
        } catch (AbandonInstallationException ex) {
            System.out.println(ex.getMessage());
            return;
        }
    }

    private static CommandLine getCommandLine(String[] args) {
        GnuParser parser = new GnuParser();
        try {
            return parser.parse(getCommandOptions(), args);
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(TolvenInstaller.class.getName(), getCommandOptions());
            throw new RuntimeException("Could not parse command line for: " + TolvenInstaller.class.getName(), ex);
        }
    }

    private static Options getCommandOptions() {
        Options cmdLineOptions = new Options();
        Option confOption = new Option(CMD_LINE_CONF_OPTION, CMD_LINE_CONF_OPTION, true, "configuration directory");
        cmdLineOptions.addOption(confOption);
        Option pluginsXMLSourceOption = new Option(CMD_LINE_PLUGINSXMLTEMPLATE_OPTION, CMD_LINE_PLUGINSXMLTEMPLATE_OPTION, true, "source plugins.xml file");
        cmdLineOptions.addOption(pluginsXMLSourceOption);
        return cmdLineOptions;
    }

    private static String getCommandLineConfigDir(CommandLine commandLine) throws AbandonInstallationException, IOException {
        String configDir = commandLine.getOptionValue(CMD_LINE_CONF_OPTION);
        if (configDir == null) {
            configDir = System.getenv(ENV_CONF);
            if (configDir == null) {
                String configDirPath = null;
                if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
                    configDirPath = "/usr/local/tolven-config";
                } else {
                    configDirPath = "c:\\tolven-config";
                }
                System.out.print("\nPlease enter the Configuration Directory (hit return for the  default:  " + configDirPath + "): ");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
                String promptedConfigDirPath = input.readLine();
                if (promptedConfigDirPath == null) {
                    // Occurs when CTRL-C is used on windows, or something else goes
                    // wrong?
                    throw new AbandonInstallationException("Installation abandoned");
                }
                if (promptedConfigDirPath.length() == 0) {
                    promptedConfigDirPath = configDirPath;
                }
                return promptedConfigDirPath.trim();
            }
        }
        return configDir;
    }

    private static File getPluginsXMLTemplate(CommandLine commandLine, File installDir) {
        String pluginsXMLTemplateFilename = commandLine.getOptionValue(CMD_LINE_PLUGINSXMLTEMPLATE_OPTION);
        File pluginsXMLTemplate = null;
        if (pluginsXMLTemplateFilename == null) {
            pluginsXMLTemplate = new File(installDir, "template-pluginsxml/default-template-plugins.xml");
        } else {
            pluginsXMLTemplate = new File(pluginsXMLTemplateFilename);
            if (!pluginsXMLTemplate.getPath().equals(pluginsXMLTemplate.getAbsolutePath())) {
                pluginsXMLTemplate = new File(installDir, "template-pluginsxml/" + pluginsXMLTemplateFilename);
            }
        }
        return pluginsXMLTemplate;
    }

    private static void updateScripts(File binDirectory, File templateBinDir, File selectedConfigDir) throws IOException {
        String extension = null;
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
            extension = ".sh";
        } else {
            extension = ".bat";
        }
        Collection<File> scripts = FileUtils.listFiles(templateBinDir, new SuffixFileFilter(extension), null);
        for (File script : scripts) {
            File dest = new File(binDirectory, script.getName());
            if (dest.exists()) {
                dest.delete();
            }
            System.out.println("move: " + script.getPath() + " to: " + binDirectory.getPath());
            FileUtils.moveFileToDirectory(script, binDirectory, false);
            if (script.getName().equals("tpfenv" + extension)) {
                List<String> lines = FileUtils.readLines(dest);
                List<String> sub_lines = new ArrayList<String>();
                boolean tpfenvRequiresUpdate = false;
                for (String line : lines) {
                    String replacedLine = line.replace("$TOLVEN_CONFIG", selectedConfigDir.getPath());
                    if (!replacedLine.equals(line)) {
                        tpfenvRequiresUpdate = true;
                    }
                    sub_lines.add(replacedLine);
                }
                if (tpfenvRequiresUpdate) {
                    System.out.println("updated: " + dest.getPath());
                    FileUtils.writeLines(dest, sub_lines);
                }
            }
        }
    }

    private static void upgradeConfigDir(File configDir, File installDir, File pluginsXMLTemplate, File bootPropertiesTemplate, File repositoryLocalTemplate) throws IOException {
        File pluginsXML = new File(configDir, "plugins.xml");
        if (pluginsXML.exists()) {
            System.out.println(pluginsXML + " exists, and will NOT be replaced by: " + pluginsXMLTemplate);
        } else {
            List<String> sub_lines = getPluginsXMLLines(pluginsXMLTemplate, installDir, configDir);
            System.out.println("write plugins template content from: " + pluginsXMLTemplate.getPath() + " to: " + pluginsXML.getPath());
            FileUtils.writeLines(pluginsXML, sub_lines);
        }
        File bootProperties = new File(configDir, bootPropertiesTemplate.getName());
        if (bootProperties.exists()) {
            System.out.println(bootProperties + " exists, and will NOT be replaced by: " + bootPropertiesTemplate);
        } else {
            System.out.println("copy: " + bootPropertiesTemplate.getPath() + " to: " + bootProperties.getPath());
            FileUtils.copyFile(bootPropertiesTemplate, bootProperties);
        }
        File repositoryLocal = new File(configDir, "repositoryLocal");
        if (repositoryLocal.exists()) {
            System.out.println(repositoryLocal + " exists, and will NOT be replaced by: " + repositoryLocalTemplate);
        } else {
            System.out.println("copy: " + repositoryLocalTemplate.getPath() + " to: " + repositoryLocal.getPath());
            FileUtils.copyDirectory(repositoryLocalTemplate, repositoryLocal);
            File repositoryPluginsDir = new File(repositoryLocal, "plugins");
            repositoryPluginsDir.mkdirs();
        }
    }

    private static List<String> getPluginsXMLLines(File defaultPluginsXMLFile, File installDir, File configDir) throws IOException {
        List<String> lines = FileUtils.readLines(defaultPluginsXMLFile);
        List<String> sub_lines = new ArrayList<String>();
        for (String line : lines) {
            String replacedLine = line.replace("your-installationDir", installDir.getPath().replace("\\", "/"));
            replacedLine = replacedLine.replace("your-tolven-configDir", configDir.getPath().replace("\\", "/"));
            sub_lines.add(replacedLine);
        }
        return sub_lines;
    }

}

class AbandonInstallationException extends Exception {

    private static final long serialVersionUID = 1L;

    AbandonInstallationException(Exception ex) {
        super(ex);
    }

    AbandonInstallationException(String ex) {
        super(ex);
    }
}