package org.tolven.trim.ex;

import java.io.Serializable;
import java.util.Locale;

import org.tolven.trim.DataType;
import org.tolven.trim.LabelFacet;

public class DataTypeEx extends DataType implements Serializable {

	// Obsolete method.
//	/**
//	 * Using the supplied Locale, find an appropriate matching label. There are four possible matches, listed in order of precedence:
//	 * <ol>
//	 * <li>match on language, country, and variant</li>
//	 * <li>match on language and country</li>
//	 * <li>match on language</li>
//	 * <li>A label with no language</li>
//	 * </ol>
//	 * @param locale
//	 * @return
//	 */
//	public static String getLocaleLabel( DataType dataType, Locale locale) {
//		// Look through each of the labels in the list
//		String label0 = null;
//		String label1 = null;
//		String label2 = null;
//		String label3 = null;
//		for (LabelFacet labelFacet : dataType.getLabels()) {
//			if (labelFacet.getLanguage()==null ) {
//				label0 = labelFacet.getValue();
//				continue;
//			}
//			String components[] = labelFacet.getLanguage().split("_");
//			if (components.length == 1 && new Locale(components[0]).equals(locale)){
//				label1 = labelFacet.getValue();
//				continue;
//			}
//			if (components.length == 2 && new Locale(components[0], components[1]).equals(locale)) {
//				label2 = labelFacet.getValue();
//				continue;
//			}
//			if (components.length == 3 && new Locale(components[0], components[1], components[2]).equals(locale)) {
//				label3 = labelFacet.getValue();
//			}
//		}
//		// Return the most specific match
//		if (label3!=null) return label3;
//		if (label2!=null) return label2;
//		if (label1!=null) return label1;
//		return label0;
//	}

}
