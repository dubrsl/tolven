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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.util.IoUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @version $Id$
 */
public class ManifestParser {
    static Log log = LogFactory.getLog(ManifestParser.class);

    static final String TPFXSD = loadTPFXSD();

    private static String loadTPFXSD() {
        try {
            Reader in = new InputStreamReader(PluginRegistryImpl.class.getResourceAsStream("tpf.xsd"), Charset.forName("UTF-8"));
            try {
                StringBuilder sBuf = new StringBuilder();
                char[] cBuf = new char[64];
                int read;
                while ((read = in.read(cBuf)) != -1) {
                    sBuf.append(cBuf, 0, read);
                }
                return sBuf.toString();
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not read tpf.xsd", ex);
        }
    }

    private static EntityResolver getXSDEntityResolver() {
        EntityResolver e =  new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (TPFXSD == null) {
                    return null;
                } else {
                    return new InputSource(new StringReader(TPFXSD));
                }
            }
        };
        return e;
    }

    private SAXParserFactory parserFactory;
    private EntityResolver entityResolver;

    public ManifestParser(boolean isValidating) {
        parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(isValidating);
        entityResolver = isValidating ? getXSDEntityResolver() : null;
        //log.info("got SAX parser factory - " + parserFactory);
    }

    public ModelPluginManifest parseManifest(URL url) throws ParserConfigurationException, SAXException, IOException {
        ManifestHandler handler = new ManifestHandler(entityResolver);
        InputStream strm = IoUtil.getResourceInputStream(url);
        try {
            parserFactory.setFeature("http://xml.org/sax/features/validation", false);
            parserFactory.newSAXParser().parse(strm, handler);
        } finally {
            strm.close();
        }
        ModelPluginManifest result = handler.getResult();
        result.setLocation(url);
        return result;
    }

    public ModelManifestInfo parseManifestInfo(URL url) throws ParserConfigurationException, SAXException, IOException {
        ManifestInfoHandler handler = new ManifestInfoHandler(entityResolver);
        InputStream strm = IoUtil.getResourceInputStream(url);
        try {
            parserFactory.setFeature("http://xml.org/sax/features/validation", false);
            parserFactory.newSAXParser().parse(strm, handler);
        } finally {
            strm.close();
        }
        return handler.getResult();
    }
}
