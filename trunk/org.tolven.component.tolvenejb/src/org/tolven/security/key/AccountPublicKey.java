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
package org.tolven.security.key;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * This class encapsulates an x509EncodedKeySpec for a Public Key
 * 
 * @author Joseph Isaac
 * 
 */
@Embeddable
public class AccountPublicKey extends TolvenPublicKey implements Serializable {

	private static final long serialVersionUID = 2L;

	protected static final String NOT_INITIALIZED = "AccountPublicKey not initialized";

    /**
     * Return an instance of UserPublicKey
     * 
     * @return
     */
    public static AccountPublicKey getInstance() {
        return new AccountPublicKey();
    }

}
