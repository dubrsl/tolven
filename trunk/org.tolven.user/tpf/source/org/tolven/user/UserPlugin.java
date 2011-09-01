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
 * @version $Id: UserPlugin.java 1870 2011-07-27 07:21:54Z joe.isaac $
 */
package org.tolven.user;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin creates users via RESTful API
 * 
 * @author Joseph Isaac
 *
 */
public class UserPlugin extends TolvenCommandPlugin {

    public static final String CMD_COMMONNAME_OPTION = "commonName";

    public static final String CMD_COUNTRY_OPTION = "country";
    public static final String CMD_CREATE_OPTION = "create";
    public static final String CMD_EMAILS_OPTION = "emails";
    public static final String CMD_ORGANIZATION_OPTION = "organization";
    public static final String CMD_ORGANIZATION_UNIT_OPTION = "organizationUnit";
    public static final String CMD_REALM_OPTION = "realm";
    public static final String CMD_STATE_OR_PROVINCE_OPTION = "stateOrProvince";
    public static final String CMD_SURNAME_OPTION = "surname";
    public static final String CMD_UID_OPTION = "uid";
    public static final String CMD_USERPASSWORD_OPTION = "userPassword";
    public static final String CMD_USERPCKS12_OPTION = "userPKCS12";
    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";

    private Logger logger = Logger.getLogger(UserPlugin.class);

    @Override
    protected void doStart() throws Exception {
        logger.info("*** start ***");
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** end ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.info("*** execute ***");
        CommandLine commandLine = getCommandLine(args);
        Properties props = new Properties();
        String uid = commandLine.getOptionValue(CMD_UID_OPTION);
        props.setProperty("uid", uid);
        String commonName = commandLine.getOptionValue(CMD_COMMONNAME_OPTION);
        props.setProperty("commonName", commonName);
        String surname = commandLine.getOptionValue(CMD_SURNAME_OPTION);
        props.setProperty("surname", surname);
        String organizationUnit = commandLine.getOptionValue(CMD_ORGANIZATION_UNIT_OPTION);
        if (organizationUnit != null) {
            props.setProperty("organizationUnit", organizationUnit);
        }
        String organization = commandLine.getOptionValue(CMD_ORGANIZATION_OPTION);
        if (organization != null) {
            props.setProperty("organization", organization);
        }
        String stateOrProvince = commandLine.getOptionValue(CMD_STATE_OR_PROVINCE_OPTION);
        if (stateOrProvince != null) {
            props.setProperty("stateOrProvince", stateOrProvince);
        }
        String country = commandLine.getOptionValue(CMD_COUNTRY_OPTION);
        if (country != null) {
            props.setProperty("country", country);
        }
        String emails = commandLine.getOptionValue(CMD_EMAILS_OPTION);
        if (emails != null) {
            props.setProperty("emails", emails);
        }
        String userPKCS12 = commandLine.getOptionValue(CMD_USERPCKS12_OPTION);
        String keyStorePassword = commandLine.getOptionValue(CMD_USERPASSWORD_OPTION);
        char[] uidPassword = keyStorePassword.toCharArray();
        props.setProperty("uidPassword", new String(uidPassword));
        if (userPKCS12 != null) {
            String encodedUserPkCS12 = getBase64EnocdedKeyStore(userPKCS12, uidPassword);
            props.setProperty("userPKCS12", encodedUserPkCS12);
        }
        String realm = commandLine.getOptionValue(CMD_REALM_OPTION);
        props.setProperty("realm", realm);
        String userId = getTolvenConfigWrapper().getAdminId();
        props.setProperty("userId", new String(userId));
        char[] userIdPassword = getTolvenConfigWrapper().getAdminPassword();
        props.setProperty("userIdPassword", new String(userIdPassword));
        UserLoader userLoader = new DefaultUserLoader(userId, userIdPassword);
        userLoader.createUser(uid, props, userId);
    }

    private String getBase64EnocdedKeyStore(String filename, char[] password) {
        byte[] bytes = null;
        try {
            bytes = FileUtils.readFileToByteArray(new File(filename));
        } catch (Exception e) {
            throw new RuntimeException("Could not load keyStore bytes from: " + filename);
        }
        vaildateKeyStore(bytes, password);
        try {
            String encodedKeyStore = new String(Base64.encodeBase64(bytes), "UTF-8");
            return encodedKeyStore;
        } catch (Exception ex) {
            throw new RuntimeException("Could not base64 encode keyStore", ex);
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
        Option createOption = new Option(CMD_CREATE_OPTION, CMD_CREATE_OPTION, false, "\"create user\"");
        createOption.setRequired(true);
        cmdLineOptions.addOption(createOption);
        Option uidOption = new Option(CMD_UID_OPTION, CMD_UID_OPTION, true, "\"uid\"");
        uidOption.setRequired(true);
        cmdLineOptions.addOption(uidOption);
        Option commonNameOption = new Option(CMD_COMMONNAME_OPTION, CMD_COMMONNAME_OPTION, true, "\"commonName\"");
        commonNameOption.setRequired(true);
        cmdLineOptions.addOption(commonNameOption);
        Option surnameOption = new Option(CMD_SURNAME_OPTION, CMD_SURNAME_OPTION, true, "\"surname\"");
        surnameOption.setRequired(true);
        cmdLineOptions.addOption(surnameOption);
        Option organizationUnitOption = new Option(CMD_ORGANIZATION_UNIT_OPTION, CMD_ORGANIZATION_UNIT_OPTION, true, "\"organizationUnit\"");
        cmdLineOptions.addOption(organizationUnitOption);
        Option organizationOption = new Option(CMD_ORGANIZATION_OPTION, CMD_ORGANIZATION_OPTION, true, "\"organization\"");
        cmdLineOptions.addOption(organizationOption);
        Option stateOption = new Option(CMD_STATE_OR_PROVINCE_OPTION, CMD_STATE_OR_PROVINCE_OPTION, true, "\"stateOrProvince\"");
        cmdLineOptions.addOption(stateOption);
        Option countryOption = new Option(CMD_COUNTRY_OPTION, CMD_COUNTRY_OPTION, true, "\"country\"");
        cmdLineOptions.addOption(countryOption);
        Option emailsOption = new Option(CMD_EMAILS_OPTION, CMD_EMAILS_OPTION, true, "\"" + CMD_EMAILS_OPTION + "\"");
        cmdLineOptions.addOption(emailsOption);
        Option keystoreOption = new Option(CMD_USERPCKS12_OPTION, CMD_USERPCKS12_OPTION, true, "\"userPKCS12 file\"");
        cmdLineOptions.addOption(keystoreOption);
        Option realmOption = new Option(CMD_REALM_OPTION, CMD_REALM_OPTION, true, "\"realm\"");
        realmOption.setRequired(true);
        cmdLineOptions.addOption(realmOption);
        Option keystorePasswordOption = new Option(CMD_USERPASSWORD_OPTION, CMD_USERPASSWORD_OPTION, true, "\"user password\"");
        keystorePasswordOption.setRequired(true);
        cmdLineOptions.addOption(keystorePasswordOption);
        return cmdLineOptions;
    }

    private void vaildateKeyStore(byte[] bytes, char[] password) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            try {
                KeyStore keyStore = KeyStore.getInstance(TOLVEN_CREDENTIAL_FORMAT_PKCS12);
                keyStore.load(bais, password);
            } catch (Exception ex) {
                throw new RuntimeException("Could not load keyStore", ex);
            }
        } finally {
            if (bais != null)
                try {
                    bais.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Could not close bytearrayinputstream after loading keyStore", ex);
                }
        }
    }

}
