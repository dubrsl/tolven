package org.tolven.web;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.tolven.trim.CD;
import org.tolven.trim.CE;
import org.tolven.trim.PQ;
import org.tolven.trim.ex.TrimFactory;

public class CEConverter implements Converter {
	private static final TrimFactory trimFactory = new TrimFactory();  
	
	public Object getAsObject(FacesContext ctx, UIComponent comp, String string) {
		String t[] = string.split("\\|");
		if ("PQ".equals(t[0])) {
			PQ pq = trimFactory.createPQ();
			if (t.length > 1 && t[1].length()>0) pq.setValue(Double.parseDouble(t[1]));
			if (t.length > 2 && t[2].length()>0) pq.setUnit(t[2]);
			if (t.length > 3 && t[3].length()>0) pq.setOriginalText(t[3]);
			return pq;
		}
		if ("CE".equals(t[0])) {
			CE ce = trimFactory.createCE();
			if (t.length > 1 && t[1].length()>0) ce.setCode(t[1]);
			if (t.length > 2 && t[2].length()>0) ce.setCodeSystem(t[2]);
			if (t.length > 3 && t[3].length()>0) ce.setCodeSystemName(t[3]);
			if (t.length > 4 && t[4].length()>0) ce.setCodeSystemVersion(t[4]);
			if (t.length > 5 && t[5].length()>0) ce.setDisplayName(t[5]);
//			TolvenLogger.info( "Recovered CE: " + ce.getCode(), CEConverter.class);
			return ce;
		}
		if ("CD".equals(t[0])) {
			CD cd = trimFactory.createCD();
			if (t.length > 1 && t[1].length()>0) cd.setCode(t[1]);
			if (t.length > 2 && t[2].length()>0) cd.setCodeSystem(t[2]);
			if (t.length > 3 && t[3].length()>0) cd.setCodeSystemName(t[3]);
			if (t.length > 4 && t[4].length()>0) cd.setCodeSystemVersion(t[4]);
			if (t.length > 5 && t[5].length()>0) cd.setDisplayName(t[5]);
//			TolvenLogger.info( "Recovered CE: " + ce.getCode(), CEConverter.class);
			return cd;
		}
		return null;
	}

	private static String encodeString( String value ) {
		if (value==null) return "|";
		return "|" + value;
	}
	public String getAsString(FacesContext ctx, UIComponent comp, Object object) {
		
		if (object instanceof PQ ) {
			PQ pq = (PQ)object;
			String encoded = "PQ" + encodeString(Double.toString(pq.getValue())) + 
									encodeString(pq.getUnit()) + 
									encodeString(pq.getOriginalText());
			return encoded;
		}
		if (object instanceof CE ) {
			CE ce = (CE)object;
			String encoded = "CE" + encodeString(ce.getCode()) + 
									encodeString(ce.getCodeSystem()) + 
									encodeString(ce.getCodeSystemName()) +
									encodeString(ce.getCodeSystemVersion()) + 
									encodeString(ce.getDisplayName());
//			TolvenLogger.info( "Encode CE: " + encoded, CEConverter.class);
			return encoded;
		}
		if (object instanceof CD ) {
			CD cd = (CD)object;
			String encoded = "CD" + encodeString(cd.getCode()) + 
									encodeString(cd.getCodeSystem()) + 
									encodeString(cd.getCodeSystemName()) +
									encodeString(cd.getCodeSystemVersion()) + 
									encodeString(cd.getDisplayName());
//			TolvenLogger.info( "Encode CE: " + encoded, CEConverter.class);
			return encoded;
		}
		return null;
	}

}
