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

/**
 * This class encapsulates a Private Key
 * 
 * @author Joseph Isaac
 * 
 */

public class UserPrivateKey implements Serializable {

    private static final long serialVersionUID = 2L;
    public static final String USER_PRIVATE_KEY_ALGORITHM_PROP = "tolven.security.user.privateKeyAlgorithm";
    public static final String USER_PRIVATE_KEY_LENGTH_PROP = "tolven.security.user.privateKeyLength";
    public static final String PBE_KEY_ALGORITHM_PROP = "tolven.security.user.pbeKeyAlgorithm";
    public static final String USER_PASSWORD_SALT_LENGTH_PROP = "tolven.security.user.passwordSaltLength";
    public static final String USER_PASSWORD_ITERATION_COUNT_PROP = "tolven.security.user.passwordIterationCount";

}
