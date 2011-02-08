/*
 *  Copyright (C) 2008 Tolven Inc 
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
package org.tolven.web.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
/**
 * Util class to modify String literals sending in Http Response
 * 
 * @author Srini Kandula
 */
public class StringUtils {
	
	/**
	   * Escape characters for text appearing in HTML markup.
	   * 
	   * <P>The following characters are replaced with corresponding HTML 
	   * character entities : 
	   * <table border='1' cellpadding='3' cellspacing='0'>
	   * <tr><th> Character </th><th> Encoding </th></tr>
	   * <tr><td> < </td><td> &lt; </td></tr>
	   * <tr><td> > </td><td> &gt; </td></tr>
	   * <tr><td> & </td><td> &amp; </td></tr>
	   * <tr><td> " </td><td> &quot;</td></tr>
	   * <tr><td> ' </td><td> &#039;</td></tr>
	   * <tr><td> ( </td><td> &#040;</td></tr> 
	   * <tr><td> ) </td><td> &#041;</td></tr>
	   * <tr><td> # </td><td> &#035;</td></tr>
	   * <tr><td> % </td><td> &#037;</td></tr>
	   * <tr><td> ; </td><td> &#059;</td></tr>
	   * <tr><td> + </td><td> &#043; </td></tr>
	   * <tr><td> - </td><td> &#045; </td></tr>
	   * </table>
	   * 
	   */
	public static String forHTML(String aText) {
		if(aText == null)
			return null;
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '&') {
				result.append("&amp;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '(') {
				result.append("&#040;");
			} else if (character == ')') {
				result.append("&#041;");
			} else if (character == '#') {
				result.append("&#035;");
			} else if (character == '%') {
				result.append("&#037;");
			} else if (character == ';') {
				result.append("&#059;");
			} else if (character == '+') {
				result.append("&#043;");
			} else if (character == '-') {
				result.append("&#045;");
			} else {
				//the char is not a special one
				//add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}
}
