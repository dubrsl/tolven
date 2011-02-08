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
 * @author <your name>
 * @version $Id: FormattedOutputWriter.java,v 1.2.4.2 2010/11/07 03:21:35 joseph_isaac Exp $
 */  

package org.tolven.web.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.core.entity.AccountUser;

public abstract class FormattedOutputWriter {
	protected Writer writer;
	protected String type;
	protected Date now;
	protected AccountUser accountUser;
	
	private TrimExpressionEvaluator ee;
        		
	public FormattedOutputWriter(Writer writer, String type, AccountUser accountUser, Date now) {
		this.writer = writer;
		this.type = type;
		this.accountUser = accountUser;
		this.now = now;
		ee = new TrimExpressionEvaluator();
		ee.addVariable("now", now);
		ee.addVariable("account", accountUser.getAccount());
		ee.addVariable("accountUser", accountUser);
	}
	
	/**
	 * Add escape characters for things like < >
	 * @param writer
	 * @param val
	 * @throws IOException 
	 */
	public void writeEscape( String val ) throws IOException {
		for ( char c : val.toCharArray()) {
			writeEscape( c );
		}
	}
	public abstract void writeEscape( char val ) throws IOException;
	public void writeVerbatim( String val ) throws IOException {
		writer.write(val);
	}
	
	public void writeVerbatim( char val ) throws IOException {
		writer.write(val);
	}
	
	public void writeExpression( String expression ) throws IOException {
		int segmentBegin = -1;
		boolean escape = false;
		char e[] = expression.toCharArray();
		for (int c = 0; c < e.length; c++ ) {
			if (escape) {
				escape=false;
				writeVerbatim( e[c]);
				continue;
			}
			if ( '\\'==e[c] ) {
				escape = true;
				continue;
			}
			if (c < e.length-3) {
				if ('#'==e[c] || '$'==e[c]) {
					if ('{'==e[c+1]) {
						segmentBegin=c;
						continue;
					}
				}
			}
			// If we're in an el segment and see the end, do the evaluation
			if ('}'==e[c] && segmentBegin>=0) {
				String segment = expression.substring(segmentBegin, c+1);
				writeEscape((String) ee.evaluate(segment, String.class));
				segmentBegin=-1;
				continue;
			}
			if (segmentBegin < 0) {
				writeVerbatim( e[c]);
			}
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TrimExpressionEvaluator getExpressionEvaluator() {
		return ee;
	}

	public void setExpressionEvaluator(TrimExpressionEvaluator ee) {
		this.ee = ee;
	}
	
}