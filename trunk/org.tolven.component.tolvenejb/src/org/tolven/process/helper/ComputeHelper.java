package org.tolven.process.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.tolven.app.MenuLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.el.ELFunctions;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.TrimHeader;
import org.tolven.core.entity.AccountUser;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActRelationshipDirection;
import org.tolven.trim.ActRelationshipType;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

// Helper class to hold common methods used during compute.
public class ComputeHelper {
	
	/** Mehtod to load,parse and create a respective TrimEx.
	 * @param trimName
	 * @param accountUser
	 * @param now
	 * @param contextList
	 * @param menuBean
	 * @param trimBean
	 * @return TrimEx
	 * @throws JAXBException
	 */
	public static TrimEx getTrim(String trimName,Map<String,Object> trimEnv) throws JAXBException{
		TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
		AccountUser accountUser = (AccountUser) trimEnv.get("accountUser");
		Map<String, Object> variables = new HashMap<String, Object>(10);
		variables.put("account", accountUser.getAccount());
		variables.put("accountUser", accountUser);
		variables.put("user", accountUser.getUser());
		variables.put("knownType", accountUser.getAccount().getAccountType().getKnownType());
		variables.put("now", (Date)trimEnv.get("now"));
		for (MenuPath contextPath : ((List<MenuPath>)trimEnv.get("contextList"))) {
			long keys[] = contextPath.getNodeKeys();
			for (int n = 0; n < keys.length; n++) {
				if (keys[n]!=0) {
					MenuData md = ((MenuLocal)trimEnv.get("menuBean")).findMenuDataItem(keys[n]);
					variables.put( contextPath.getNode(n), md);
				}
			}
		}
		ee.addVariables(variables);
		// Make our functions available - anything else to pass to the act at this point?
		TrimHeader trimHeader = ((TrimLocal)trimEnv.get("trimBean")).findTrimHeader(trimName);
		String templateBody = new String(trimHeader.getTrim());
		TrimEx templateTrim = ((TrimLocal)trimEnv.get("trimBean")).parseTrim( templateBody, ee );
		return templateTrim;
	}
	
	public static ActRelationship getRelation(String trimName,List<String> options,Map<String,Object> trimEnv) throws JAXBException {
		Map<String,Object> relationsAndValueSets = new HashMap();
		List<ActRelationship> retRelations = new ArrayList();

		TrimEx templateTrim = getTrim(trimName, trimEnv);
		List<ActRelationship> relations =  templateTrim.getAct().getRelationships();
		List<ActRelationship> retVal = new ArrayList();
		
		ActRelationship _ar = ((TrimFactory)trimEnv.get("trimFactory")).createActRelationship();
		_ar.setTypeCode(ActRelationshipType.PERT);
		_ar.setDirection(ActRelationshipDirection.OUT);
		_ar.setName(trimName.substring(trimName.lastIndexOf("/")+1));
		_ar.setEnabled(false);
		//_ar.setAct(templateTrim.getAct());
		Act _act = ((TrimFactory)trimEnv.get("trimFactory")).createAct();
		_act.setTitle(templateTrim.getAct().getTitle());
		_act.setClassCode(templateTrim.getAct().getClassCode());
		_act.setMoodCode(templateTrim.getAct().getMoodCode());
		_ar.setAct(_act);
		Map<String, ActRelationship> tempMap = new HashMap<String, ActRelationship>();
		for(ActRelationship _rel: templateTrim.getAct().getRelationships()){
			if(options.contains((_rel.getName()))){
				tempMap.put(_rel.getName(),_rel);
				continue;
			}			
		}
		for(String option:options){  // load the relations in the order defined in the options String.
			_act.getRelationships().add(tempMap.get(option));
		}
		return _ar;
	}
	public static List<ActRelationship> getAllRelations(String trimName,Map<String,Object> trimEnv) throws JAXBException {
	
		TrimEx templateTrim = getTrim(trimName, trimEnv);
		if(templateTrim != null)
			return templateTrim.getAct().getRelationships();
		else
			return new ArrayList<ActRelationship>();
		
	}
	
	
	public static Act getAct(Act _act,String relationName){
		ActRelationship _rel = getRelationFromAct(_act,relationName);
		if(_rel != null)
			return _rel.getAct();
		return null;
	}
	public static ActRelationship getRelationFromAct(Act _act,String relationName){
		List<ActRelationship> _relList = _act.getRelationships();
		for(ActRelationship _rel: _relList)
			if(_rel.getName().equalsIgnoreCase(relationName))
				return _rel;
		
		return null;
	}

	
	
}
