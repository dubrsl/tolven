package org.tolven.locale;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleHelper {

    public static final String GLOBALBUNDLENAME = "messages.GlobalBundle";
    public static final String APPGLOBALBUNDLEKEY = "appGlobalBundle";
    public static final String APPBUNDLEKEY = "appBundle";
    public static final String USER_LOCALE = "accountUserLocale";
    public static final String ACCOUNT_LOCALE = "accountLocale";

    private static final String APPBUNDLESUFFIX = ".AppBundle";
    private static final String APPGLOBALBUNDLESUFFIX = ".GlobalBundle";
    private static final String MISSING_DELIMETER = "???";
    
    /**
     * Get the locale object for a given localString. 
     * @param localeString
     * @return Locale object for the specified locale name
     */
    public static Locale getLocale(String localeString) {
        Locale locale = null;
        if (localeString == null) {
            locale =  Locale.getDefault();
        } else {
            String[] localeComponents = localeString.split("_");
            if (localeComponents.length == 2) {
                locale = new Locale(localeComponents[0], localeComponents[1]);
            } else {
                locale = new Locale(localeComponents[0]);
            }
        }
        return locale;
    }

    public static String getAppBundleName(String application) {
        return application + APPBUNDLESUFFIX;
    }

    public static String getAppGlobalBundleName(String application) {
        return application + APPGLOBALBUNDLESUFFIX;
    }

    /**
     * Logic which determines the user locale/account locale in effect at any given time
     * @param userLocale
     * @param accountLocale
     * @return
     */
    public static Locale getLocale(String userLocale, String accountLocale) {
        if (userLocale == null) {
            if (accountLocale == null) {
                return Locale.getDefault();
            } else {
                return getLocale(accountLocale);
            }
        } else {
            return getLocale(userLocale);
        }
    }

    /**
     * A wrapper of the getString method on ResourceBundle, which provides for a easily identifiable missing key e.g. ???MissingKey???
     * This method also makes it easier to determine which parts of the code are using message bundles
     * @param messagesBundle
     * @param key
     * @return
     */
    public static String getString(ResourceBundle messagesBundle, String key) {
        try {
            return messagesBundle.getString(key);
        } catch (MissingResourceException ex) {
            return MISSING_DELIMETER + key + MISSING_DELIMETER;
        }
    }

}
