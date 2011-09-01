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
 */
package org.tolven.shiro.authz.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.tolven.shiro.authz.TolvenAuthorizationLocal;
import org.tolven.shiro.authz.entity.DefaultTolvenAuthorization;
import org.tolven.shiro.authz.entity.TolvenAuthorization;

@Stateless
@Local(TolvenAuthorizationLocal.class)
public class TolvenAuthorizationBean implements TolvenAuthorizationLocal {

    public static final String DEFAULT_POLICY = "default";
    private Comparator<TolvenAuthorization> comparator;

    @PersistenceContext
    private EntityManager em;

    private Logger logger = Logger.getLogger(TolvenAuthorizationBean.class);

    @Override
    public TolvenAuthorization getAuthorization(String urlMethod, String contextPath, String url) {
        return getAuthorization(DEFAULT_POLICY, urlMethod, contextPath, url);
    }

    @Override
    public TolvenAuthorization getAuthorization(String policy, String urlMethod, String contextPath, String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("Search for authorization for: " + urlMethod + " " + url);
        }
        String path = null;
        if (url.endsWith("/")) {
            path = url + "*";
            if (logger.isDebugEnabled()) {
                logger.debug("Search for authorization reduced to: " + urlMethod + " " + path);
            }
        } else {
            path = url;
        }
        Query query = em.createQuery("SELECT authz FROM DefaultTolvenAuthorization authz" +
                " WHERE authz.policy = :policy" +
                " AND authz.urlMethod = :urlMethod" +
                " AND authz.contextPath = :contextPath" +
                " AND authz.url IN (:urls)");
        query.setParameter("policy", policy);
        query.setParameter("urlMethod", urlMethod);
        query.setParameter("contextPath", contextPath);
        query.setParameter("urls", getURLList(path));
        List<TolvenAuthorization> results = query.getResultList();
        if (results.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found no matches");
            }
            return null;
        } else {
            //Comparator sorts them with shortest URLs towards end of list
            Collections.sort(results, getComparator());
            if (results.size() == 1) {
                //Normally only one should match
                TolvenAuthorization authz = results.get(0);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found one match: " + authz);
                }
                return authz;
            } else {
                // Normally if two match, then it's because one is a wild card
                // If more than one matches e.g. url=/a while database contains /* and /a, then /a is the match
                if (logger.isDebugEnabled()) {
                    logger.debug("Found matches: " + results.size());
                    logger.debug("Searching for longest matching url");
                }
                TolvenAuthorization authz0 = results.get(0);
                TolvenAuthorization authz1 = results.get(1);
                if (authz0.getUrl().length() == authz1.getUrl().length()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Checking first two longest matching urls for one which does not contain a wild card");
                    }
                    if (!authz0.getUrl().contains("*")) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Found one match: " + authz0);
                        }
                        return authz0;
                    } else if (!authz1.getUrl().contains("*")) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Found one match: " + authz1);
                        }
                        return authz1;
                    } else {
                        // If both contain an asterisk throw an exception
                        throw new RuntimeException("More than one URL was found when searching for filters: " + path);
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found one match: " + authz0);
                    }
                    return authz0;
                }
            }
        }
    }

    private Comparator<TolvenAuthorization> getComparator() {
        if (comparator == null) {
            comparator = new Comparator<TolvenAuthorization>() {
                @Override
                public int compare(TolvenAuthorization authz1, TolvenAuthorization authz2) {
                    return -1 * new Integer(authz1.getUrl().length()).compareTo(new Integer(authz2.getUrl().length()));
                }
            };
        }
        return comparator;
    }

    private List<String> getURLList(String url) {
        List<String> urlList = new ArrayList<String>();
        urlList.add(url);
        do {
            if (url.endsWith("/**")) {
                url = url.substring(0, url.lastIndexOf("/"));
            }
            int index = url.lastIndexOf("/");
            if (index >= 0 && url.length() > 1) {
                url = url.substring(0, url.lastIndexOf("/"));
                urlList.add(url + "/**");
            }
        } while (url.length() > 1);
        if (logger.isDebugEnabled()) {
            StringBuffer buff = new StringBuffer();
            buff.append("[");
            Iterator<String> it = urlList.iterator();
            while (it.hasNext()) {
                buff.append(it.next());
                if (it.hasNext()) {
                    buff.append(",");
                }
            }
            buff.append("]");
            logger.debug("Search urls which might match: " + url + " " + buff.toString());
        }
        return urlList;
    }

}
