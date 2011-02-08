package org.tolven.app.bean;

public class StopList {

//	static final String EN_WORDS 
	
	public static boolean ignore( String word, String language ) {
		if (language==null) return ignoreEN( word );
		if ("en_US".equals(language)) return ignoreEN( word );
		if ("en".equals(language)) return ignoreEN( word );
		return false;
	}
	
	public static boolean ignoreEN( String word ) {
		if (word.length()<1) {
			return true;
		}
		return false;
	}
}
