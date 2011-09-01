package org.tolven.app.el;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.provider.ProviderLocal;
import org.tolven.provider.entity.Provider;
import org.tolven.security.LDAPLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.trim.ActStatus;
import org.tolven.trim.CE;
import org.tolven.trim.DataType;
import org.tolven.trim.II;
import org.tolven.trim.SETIISlot;
import org.tolven.trim.ex.TrimFactory;

public class ELFunctions {
	private static final TrimFactory factory = new TrimFactory( );
	public static final String MENUDATA_ID = ".1.";
//	public static final String NEED_ID = ".99999.";
	
	public static String computeIDRoot( Account account ) {
		return System.getProperty("tolven.repository.oid")+ MENUDATA_ID + account.getId();
	}

//	public static String computeNeedRoot( Account account ) {
//		return System.getProperty("tolven.repository.oid")+ NEED_ID + account.getId();
//	}
	
	public static ProviderLocal getProviderBean() throws NamingException {
		InitialContext ctx = null;
		ProviderLocal providerBean = null;
		if (ctx==null) {
			ctx =  new InitialContext();
		}
		if (providerBean==null) {
	        providerBean = (ProviderLocal) ctx.lookup("java:global/tolven/tolvenEJB/ProviderBean!org.tolven.provider.ProviderLocal");
		}
		return providerBean;
	}
	
	public static LDAPLocal getLDAPBean() throws NamingException {
		InitialContext ctx = null;
		LDAPLocal ldapBean = null;
		if (ctx==null) {
			ctx =  new InitialContext();
		}
		if (ldapBean==null) {
			ldapBean = (LDAPLocal) ctx.lookup("java:global/tolven/tolvenEJB/LDAPBean!org.tolven.security.LDAPLocal");
		}
		return ldapBean;
	}

	public static MenuLocal getMenuBean() throws NamingException {
		InitialContext ctx = null;
		MenuLocal menuBean = null;
		if (ctx==null) {
			ctx =  new InitialContext();
		}
		if (menuBean==null) {
			menuBean = (MenuLocal) ctx.lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal");
		}
		return menuBean;
	}
	
	public static MenuData placeholder( Account account, String path ) throws NamingException {
		if (path==null) return null;
		return getMenuBean().findMenuDataItem(account.getId(), path);
	}
	
	/**
	 * Return the placeholder identified by the specified ID slot
	 * @param account
	 * @param idSlot
	 * @return placeholder
	 * @throws NamingException
	 */
	public static MenuData placeholder( Account account, SETIISlot idSlot ) throws NamingException {
		if (idSlot==null) return null;
		String path = internalId( account, idSlot);
		return placeholder(account, path);
	}
	
	/**
	 * Find the appropriate internal id for the given account
	 * @param account
	 * @param idSlot A Slot containing a set of IIs
	 * @return A String containing the path, eg echr:patient-123
	 */
	public static String internalId( Account account, SETIISlot idSlot) {
		String root = computeIDRoot(account);
		if (idSlot==null) return null;
		for (II ii : idSlot.getIIS()) {
			if (root.equals(ii.getRoot())) {
				return ii.getExtension();
			}
		}
		return null;
	}
	
	/**
	 * Return the first non-null parameter. The function name "from" is used because it
	 * functions similar to the &lt;from&gt; element in x.application.xml
	 * @param objects
	 * @return
	 */
	public static Object from( Object object1, Object object2 ) {
		if (object1!=null) return object1;
		if (object2!=null) return object2;
//		for (int x = 0; x < objects.length; x++) {
//			if (objects[x]!=null) return objects[x]; 
//		}
		return null;
	}
	
	public static String fromCE(Object object1, Object object2) {
		String CEcontent = "";
		/*<displayName>Asian</displayName>
		<code>2567338</code>
		<codeSystem>2.16.840.1.113883.3.26.2</codeSystem>
		<codeSystemName>caDSR</codeSystemName>
		<codeSystemVersion>2.1</codeSystemVersion>*/
		//first grab the CE
		CE ce = new CE();
		List<CE> ces = new ArrayList<CE>();
		if(object1 !=null) {
			if(object1 instanceof CE) {
				ce = (CE) object1;
				ces.add(ce);
			} else if (object1 instanceof List<?>) {
				ces = (List<CE>) object1;
			} else {
				ce.setDisplayName(object1.toString());
			}
		} else if (object2 !=null) {
			if(object2 instanceof CE) {
				ce = (CE) object2;
				ces.add(ce);
			} else if (object2 instanceof List<?>) {
				ces = (List<CE>) object2;
			} else {
				ce.setDisplayName(object2.toString());
			}
		}
		int count = 0;
		for (CE ace : ces) {
			if(ces.size() > 1) {
				//This is only when it is a list
				count +=1;
			}
			//Next write out all those fancy values
			if(ace.getDisplayName() != null) {
				CEcontent += "<displayName>" + ace.getDisplayName() + "</displayName>\n";
			}
			if(ace.getCode() != null) {
				CEcontent += "<code>" + ace.getCode() + "</code>\n";
			}
			if(ace.getCodeSystem() != null) {
				CEcontent += "<codeSystem>" + ace.getCodeSystem() + "</codeSystem>\n";
			}
			if (ace.getCodeSystemName() != null) {
				CEcontent += "<codeSystemName>" + ace.getCodeSystemName() + "</codeSystemName>\n";
			}
			if (ace.getCodeSystemVersion() != null) {
				CEcontent += "<codeSystemVersion>" + ace.getCodeSystemVersion() + "</codeSystemVersion>\n";
			}
			if(ces.size() > 1 && count < ces.size()) {
				//This is only when it is a list, don't do this on the last element
				CEcontent +="</CE>";
				CEcontent +="<CE>";
			}
		}
		
				
		return CEcontent;
	}
	
	public static Date TStoDate( String time ) {
		if (time==null || time.length()==0) return null;
		try {
			String sdfString = "yyyyMMddhhmmss".substring(0, Math.min(time.length(),14));
			SimpleDateFormat sdf = new SimpleDateFormat(sdfString);
			Date date = sdf.parse(time);
			return date;
		} catch (Exception e) {
			throw new RuntimeException( "Error parsing date string " + time, e);
		}
	}
	
	/**
	 * Return a proper TS given a Java date object 
	 * @param date
	 * @return TS object
	 */
	public static String TS( Date date ) {
		if (date==null) return null;
		SimpleDateFormat HL7TSformat = new SimpleDateFormat("yyyyMMddHHmmssZZ"); 
		return HL7TSformat.format(date);
	}

	public static Provider findProvider( Long id ) throws NamingException {
		return getProviderBean().findProvider(id);
	}
	
	public static String encode( DataType dataType ) {
		return factory.dataTypeToString(dataType);
	}
	
	public static DataType decode( String encoded) {
		return factory.stringToDataType(encoded);
	}
	
	/**
	 * Given the Date of Birth and AccountUser return the age
	 * @param dob Date of Birth (usually of a patient)
	 * @param accountUser
	 * @return String containing age
	 */
	public static String age( Date dob, AccountUser accountUser) {
	    Calendar b = new GregorianCalendar();
	    b.setTime( dob );
	    Calendar n = new GregorianCalendar();
	    n.setTime( new Date() );
	    Locale locale = accountUser.getLocaleObject();
		return AgeFormat.toAgeString( b, n, locale );
	}
	public static final long SECOND = 1000;
	public static final long MINUTE = 60*SECOND;
	public static final long HOUR = 60*MINUTE;
	public static final long DAY = 24*HOUR;
	public static final long YEAR = 365*DAY+(DAY/4);
	/**
	 * Given the Date of Birth and AccountUser return the age
	 * @param dob Date of Birth (usually of a patient)
	 * @param accountUser
	 * @return String containing age
	 */
	public static long ageInYears( Date dob ) {
		if (dob==null) return 0;
	    return ((new Date().getTime()-dob.getTime())/YEAR);
	}
	
	//CCHIT merge	
	/**
	 * Function to show elipses ("…") to indicate that 
	 * the entire source details could not be displayed.
	 * @author Pinky S
	 * Added on 02/09/11
	 * @param source
	 */
	public static String processSource( String source ) {
		if(source!=null && source.length()>12 )
			return source.substring(0,12).concat("...");
		else
			return source;
	}
	public static String toLowerCaseValue(ActStatus status){
		if(status != null)
			return status.value().toLowerCase();
		else
			return null;
		
	}
	/*public static TolvenPerson tp( String principal ) throws NamingException {
		return getLDAPBean().createTolvenPerson(principal);
	}*/
	public static String transDisplString(String transition){
		if(transition != null && transition.equalsIgnoreCase(ActStatus.ABORTED.value())){
			return "Discontinued";
		}
		if(transition == null)
			return null;
		return transition.toLowerCase();
	}
	public static TolvenPerson tp( String principal ) throws NamingException {
		return getLDAPBean().createTolvenPerson(principal);
	}
	
}
