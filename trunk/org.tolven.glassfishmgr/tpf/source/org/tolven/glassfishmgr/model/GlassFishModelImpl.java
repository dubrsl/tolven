package org.tolven.glassfishmgr.model;

import org.tolven.restful.client.RESTfulClient;

public class GlassFishModelImpl extends RESTfulClient {

    public GlassFishModelImpl(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }

    public void test() {
        getTokenCookie();
        logout();
    }

}
