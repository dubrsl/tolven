package org.tolven.glassfishmgr;

import org.apache.log4j.Logger;
import org.tolven.config.model.TolvenConfigWrapper;
import org.tolven.glassfishmgr.api.GlassFishModel;

public class GlassFishMgr {

    public static String ABOUT_TO_TEST_MESSAGE = "About To Test";
    public static String SUCCESSFUL_CONNECTION_MESSAGE = "Connection Successful";

    private String adminId;
    private char[] adminPassword;
    private TolvenConfigWrapper tolvenConfigWrapper;
    private GlassFishModel glassFishModel;

    private Logger logger = Logger.getLogger(GlassFishMgr.class);

    public GlassFishMgr(String appRestfulURL, String authRestfulURL) {
        setGlassFishModel(new GlassFishModel(getAdminId(), getAdminPassword(), appRestfulURL, authRestfulURL));
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public char[] getAdminPassword() {
        return adminPassword;
    }

    public void setAdminIdPassword(char[] adminIdPassword) {
        this.adminPassword = adminIdPassword;
    }

    public TolvenConfigWrapper getTolvenConfigWrapper() {
        return tolvenConfigWrapper;
    }

    public void setTolvenConfigWrapper(TolvenConfigWrapper tolvenConfigWrapper) {
        this.tolvenConfigWrapper = tolvenConfigWrapper;
    }

    private GlassFishModel getGlassFishModel() {
        return glassFishModel;
    }

    private void setGlassFishModel(GlassFishModel glassFishModel) {
        this.glassFishModel = glassFishModel;
    }

    public String testAdminAppServerConnection() {
        String messageSuffix = " for: " + getAdminId() + " at: " + getTolvenConfigWrapper().getApplication().getAppRestfulURL();
        try {
            String preTestString = "\n" + ABOUT_TO_TEST_MESSAGE + messageSuffix;
            logger.debug(preTestString);
            System.out.println(preTestString);
            getGlassFishModel().test();
            String result = "\n" + SUCCESSFUL_CONNECTION_MESSAGE + messageSuffix + "\n";
            logger.debug(result);
            System.out.println(result);
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Failed: " + messageSuffix + getNestedMessage(ex));
        }
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

}
