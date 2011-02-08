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
package org.tolven.web;

import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
/**
 * This writer accumulates output in a string buffer to be used as the body of an email message.
 * In most respects it is similar to a normal HTTP response writer.
 * @author John Churin
 *
 */
public class MailResponseWriter extends ResponseWriter {

	    private final Writer writer;
	    private boolean startOpen;
	    
	    /**
	     * Construct a ResponseWritper around an io.Writer
	     * @param writer
	     */
	    public MailResponseWriter(Writer writer) {
	        this.writer = writer;
	    }

	    public Writer getWrappedWriter() {
	    	return this.writer;
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#cloneWithWriter(java.io.Writer)
	     */
	    public ResponseWriter cloneWithWriter(Writer writer) {
	        return new MailResponseWriter(writer);
	    }

	    /* (non-Javadoc)
	     * @see java.io.Writer#close()
	     */
	    public void close() throws IOException {
	        // TODO Auto-generated method stub
	    	writer.close();
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#getContentType()
	     */
	    public String getContentType() {
	        return "text/html";
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#getCharacterEncoding()
	     */
	    public String getCharacterEncoding() {
	        return "UTF-8";
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#flush()
	     */
	    public void flush() throws IOException {
	        writer.flush();

	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#startDocument()
	     */
	    public void startDocument() throws IOException {
	        // TODO Auto-generated method stub

	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#endDocument()
	     */
	    public void endDocument() throws IOException {
	        // TODO Auto-generated method stub

	    }

	    private void closeStart() throws IOException {
	        if (this.startOpen) {
	            this.writer.write('>');
	        }
	        this.startOpen = false;
	    }
	    
	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#startElement(java.lang.String, javax.faces.component.UIComponent)
	     */
	    public void startElement(String arg0, UIComponent arg1) throws IOException {
	        this.closeStart();
	        this.writer.write('<');
	        this.writer.write(arg0);
	        this.startOpen = true;
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#endElement(java.lang.String)
	     */
	    public void endElement(String arg0) throws IOException {
	        if (this.startOpen) {
	            this.writer.write("/>");
	            this.startOpen = false;
	        } else {
	            this.writer.write("</");
	            this.writer.write(arg0);
	            this.writer.write('>');
	        }

	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#writeAttribute(java.lang.String, java.lang.Object, java.lang.String)
	     */
	    public void writeAttribute(String arg0, Object arg1, String arg2)
	            throws IOException {
	        if (arg1 != null) {
	            writer.write(' ');
	            writer.write(arg0);
	            writer.write("=\"");
	            writer.write(arg1.toString());
	            writer.write('"');
	        }
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#writeURIAttribute(java.lang.String, java.lang.Object, java.lang.String)
	     */
	    public void writeURIAttribute(String arg0, Object arg1, String arg2)
	            throws IOException {
	        this.writeAttribute(arg0, arg1, arg2);
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#writeComment(java.lang.Object)
	     */
	    public void writeComment(Object arg0) throws IOException {
	        this.closeStart();
	        this.writer.write("<!-- ");
	        this.writer.write(arg0 != null ? arg0.toString() : "null");
	        this.writer.write(" -->");
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#writeText(java.lang.Object, java.lang.String)
	     */
	    public void writeText(Object arg0, String arg1) throws IOException {
	        this.closeStart();
	        this.writer.write(arg0 != null ? arg0.toString() : "null");
	    }

	    /* (non-Javadoc)
	     * @see javax.faces.context.ResponseWriter#writeText(char[], int, int)
	     */
	    public void writeText(char[] arg0, int arg1, int arg2) throws IOException {
	        this.closeStart();
	        this.writer.write(arg0, arg1, arg2);
	    }

	    /* (non-Javadoc)
	     * @see java.io.Writer#write(char[], int, int)
	     */
	    public void write(char[] cbuf, int off, int len) throws IOException {
	        this.writer.write(cbuf, off, len);
	    }


	}

