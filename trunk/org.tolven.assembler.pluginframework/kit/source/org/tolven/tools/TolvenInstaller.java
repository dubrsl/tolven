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
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * This class augements IzPack for Tolven installation.
 * 
 * @author Joseph Isaac
 * 
 */
public class TolvenInstaller {

    public static final String CMD_LINE_CONF_OPTION = "conf";
    public static final String ENV_CONF = "TOLVEN_CONFIG_DIR";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Starting Tolven installation...");
            Option confOption = new Option(CMD_LINE_CONF_OPTION, CMD_LINE_CONF_OPTION, true, "configuration directory");
            Options cmdLineOptions = new Options();
            cmdLineOptions.addOption(confOption);
            File configDir = null;
            try {
                GnuParser parser = new GnuParser();
                CommandLine commandLine = parser.parse(cmdLineOptions, args, true);
                String configDirname = getCommandLineConfigDir(commandLine);
                configDir = new File(configDirname);
            } catch (ParseException ex) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(TolvenInstaller.class.getName(), cmdLineOptions);
                throw new RuntimeException("Could not parse command line for: " + TolvenInstaller.class.getName(), ex);
            }
            System.out.println("Selected tolven-config Directory:\t" + configDir.getPath());
            File binDirectory = new File(System.getProperty("user.dir"));
            File installDir = binDirectory.getParentFile();
            File templateConfigDir = new File(binDirectory.getParent(), "installer/tolven-config");
            upgradeConfigDir(installDir, templateConfigDir, configDir);
            File templateBinDir = new File(binDirectory.getParent(), "installer/bin");
            updateScripts(binDirectory, templateBinDir, configDir);
            System.out.println("\n*** Installation successful ***");
        } catch (AbandonInstallationException ex) {
            System.out.println(ex.getMessage());
            return;
        }
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

    private static void upgradeConfigDir(File installDir, File templateConfigDir, File configDir) throws IOException {
        Collection<File> files = FileUtils.listFiles(templateConfigDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            String relativeTemplateFilename = file.getPath().substring(templateConfigDir.getPath().length());
            File destConfigFile = new File(configDir, relativeTemplateFilename);
            if (!destConfigFile.exists()) {
                destConfigFile.getParentFile().mkdirs();
                if (file.getName().equals("plugins.xml")) {
                    List<String> lines = FileUtils.readLines(file);
                    List<String> sub_lines = new ArrayList<String>();
                    for (String line : lines) {
                        String replacedLine = line.replace("your-installationDir", installDir.getPath().replace("\\", "/"));
                        replacedLine = replacedLine.replace("your-tolven-configDir", configDir.getPath().replace("\\", "/"));
                        sub_lines.add(replacedLine);
                    }
                    FileUtils.writeLines(destConfigFile, sub_lines);
                } else {
                    System.out.println("copy: " + file.getPath() + " to: " + destConfigFile.getPath());
                    FileUtils.copyFile(file, destConfigFile);
                }
            }
        }
        File repositoryLocal = new File(configDir.getPath(), "repositoryLocal");
        File pluginsDir = new File(repositoryLocal, "plugins");
        pluginsDir.mkdirs();
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