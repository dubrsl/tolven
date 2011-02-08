/*****************************************************************************
 * Java Plug-in Framework (JPF)
 * Copyright (C) 2004-2007 Dmitry Olshansky
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *****************************************************************************/
package org.tolven.plugin.registry.xml;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @version $Id$
 */
public abstract class BaseHandler extends DefaultHandler {
    protected final Log log = LogFactory.getLog(getClass());
    protected final EntityResolver entityResolver;

    public BaseHandler(EntityResolver anEntityResolver) {
        entityResolver = anEntityResolver;
    }

    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        if (entityResolver != null) {
            try {
                return entityResolver.resolveEntity(publicId, systemId);
            } catch (SAXException se) {
                throw se;
            } catch (IOException ioe) {
                throw new SAXException("I/O error has occurred - " + ioe, ioe);
            }
        }
        //log.warn("ignoring publicId=" + publicId + " and systemId=" + systemId);
        return null;
    }

    /**
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    @Override
    public void warning(SAXParseException e) {
        //log.warn("non-fatal error while parsing XML document", e);
    }

    /**
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        if (entityResolver != null) {
            // we are in "validating" mode
            log.error("failed parsing XML resource in validating mode", e);
            throw e;
        }
        //log.warn("ignoring parse error", e);
    }

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        log.fatal("failed parsing XML resource", e);
        throw e;
    }
}

class SimpleStack<T> {
    private LinkedList<T> data;

    SimpleStack() {
        data = new LinkedList<T>();
    }

    T pop() {
        return data.isEmpty() ? null : data.removeLast();
    }

    void push(T obj) {
        data.addLast(obj);
    }

    int size() {
        return data.size();
    }
}
