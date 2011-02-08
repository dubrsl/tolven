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
package org.tolven.security;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.image.codec.jpeg.ImageFormatException;
/**
 * This interface indicates the methods needed to decrypt image content securely.
 * 
 * @author Joseph Isaac
 * 
 */
public interface ImageDocContentSecurity extends DocContentSecurity {
    public void streamJPEGThumbnail(byte[] unencryptedContent, int targetWidth, int targetHeight, OutputStream stream) throws ImageFormatException, IOException;
}
