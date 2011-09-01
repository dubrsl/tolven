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
 * @version $Id: UserKeyBean.java 1671 2011-07-13 04:57:59Z joe.isaac $
 */
package org.tolven.key.bean;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Base64;
import org.tolven.gatekeeper.RSGatekeeperClientLocal;
import org.tolven.key.UserKeyLocal;
import org.tolven.session.TolvenSessionWrapperFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Stateless
@Local(UserKeyLocal.class)
public class UserKeyBean implements UserKeyLocal {

    @EJB
    private RSGatekeeperClientLocal rsGatekeeperClientBean;

    private static CertificateFactory certificateFactory;

    public UserKeyBean() {
    }

    private CertificateFactory getCertificateFactory() {
        if (certificateFactory == null) {
            try {
                certificateFactory = CertificateFactory.getInstance("X509");
            } catch (CertificateException ex) {
                throw new RuntimeException("Could not get instance of CertificateFactory", ex);
            }
        }
        return certificateFactory;
    }

    @Override
    public PublicKey getUserPublicKey() {
        return TolvenSessionWrapperFactory.getInstance().getUserPublicKey();
    }

    @Override
    public PublicKey getUserPublicKey(String uid, String realm) {
        X509Certificate x509Certificate = getUserX509Certificate(uid, realm);
        if (x509Certificate == null) {
            return null;
        } else {
            return x509Certificate.getPublicKey();
        }
    }

    @Override
    public X509Certificate getUserX509Certificate() {
        return TolvenSessionWrapperFactory.getInstance().getUserX509Certificate();
    }

    @Override
    public X509Certificate getUserX509Certificate(String uid, String realm) {
        String principal = (String) TolvenSessionWrapperFactory.getInstance().getPrincipal();
        WebResource webResource = rsGatekeeperClientBean.getWebResource("user/" + principal + "/user/" + uid);
        ClientResponse response = webResource.queryParam("realm", realm).queryParam("attributes", "userCertificate").type(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " " + principal + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        MultivaluedMap<String, String> resultMap = response.getEntity(MultivaluedMap.class);
        byte[] userX509CertificateBytes = null;
        try {
            String encodedUserCertificate = resultMap.getFirst("userCertificate");
            if (encodedUserCertificate != null) {
                String urlDecodedUserCertificate = URLDecoder.decode(encodedUserCertificate, "UTF-8");
                userX509CertificateBytes = Base64.decodeBase64(urlDecodedUserCertificate.getBytes("UTF-8"));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not convert userCertificate to bytes using UTF-8", ex);
        }
        if (userX509CertificateBytes == null) {
            return null;
        } else {
            try {
                return (X509Certificate) getCertificateFactory().generateCertificate(new ByteArrayInputStream(userX509CertificateBytes));
            } catch (Exception ex) {
                throw new RuntimeException("Could not get X509Certificate from SSO userCertificate", ex);
            }
        }
    }

    @Override
    public String getUserX509CertificateString() {
        return TolvenSessionWrapperFactory.getInstance().getUserX509CertificateString();
    }

    @Override
    public String getUserX509CertificateString(String uid, String realm) {
        X509Certificate x509Certificate = getUserX509Certificate(uid, realm);
        if (x509Certificate == null) {
            return null;
        }
        try {
            StringBuffer buff = new StringBuffer();
            buff.append("-----BEGIN CERTIFICATE-----");
            buff.append("\n");
            String pemFormat = new String(Base64.encodeBase64Chunked(x509Certificate.getEncoded()));
            buff.append(pemFormat);
            buff.append("\n");
            buff.append("-----END CERTIFICATE-----");
            buff.append("\n");
            return buff.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not convert X509Certificate into a String", ex);
        }
    }

}
