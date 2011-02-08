package org.tolven.pathology;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.CE;
import org.tolven.trim.INT;
import org.tolven.trim.PQ;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ActRelationshipEx;

public class PathologyReportELUtils {
	public static String getDCISSummary(ActEx act,String laterality) throws Exception{
		String dcisSummary = null;
		
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		String description = null;
		ActRelationshipEx dcisRel = (ActRelationshipEx)ee.evaluate("#{act.relationship['"+laterality+"HistologyDCISDetails']}");;
		if(dcisRel.getEnableRelationship().booleanValue()){
			description = "DCIS: ";
			Object obj = null;
			PQ pq = null;
			ee.addVariable("dcisDetails", dcisRel);
			
			obj = ee.evaluate("#{dcisDetails.act.relationship['dcis'].act.observation.value.PQ}");
			if(obj == null){
				// log here
			}else{
				description += getPQSummary((PQ)obj);
			}
			description = addSeperator(description,',');
			// tumor size
			obj = ee.evaluate("#{dcisDetails.act.relationship['tumorSize'].act.observation.value.PQ}");
			if(obj == null){
				// log here
			}else{
				description += getPQSummary((PQ)obj);
			}
			description = addSeperator(description,',');
			//contiguousSections
			obj = ee.evaluate("#{dcisDetails.act.relationship['contiguousSections']}");
			if(obj != null && ((ActRelationshipEx)obj).getEnabled().booleanValue()){
				Object size = ee.evaluate("#{dcisDetails.act.relationship['contiguousSections'].act.observation.value.PQ}");
				if(size != null)
					description += getPQSummary((PQ)size,"Contiguous sections "); 
			}
			description = addSeperator(description,',');
			//scatteredMicroscopic
			obj = ee.evaluate("#{dcisDetails.act.relationship['scatteredMicroscopic']}");
			if(obj != null && ((ActRelationshipEx)obj).getEnabled().booleanValue()){
				String temp = "";
				Object size = ee.evaluate("#{dcisDetails.act.relationship['scatteredMicroscopic'].act.observation.value.PQ}");
				if(size != null)
					description += getPQSummary((PQ)size,"Scattered foci ");
				Object actualSlides = ee.evaluate("#{dcisDetails.act.relationship['scatteredMicroscopic'].act.relationship['actualInvolved'].act.observation.value.PQ}");
				if(actualSlides != null){
					temp = getPQSummary((PQ)actualSlides);
					if(!StringUtils.isBlank(temp)){
						description += " "+temp+" of ";
					}
				}
				Object totalSlides = ee.evaluate("#{dcisDetails.act.relationship['scatteredMicroscopic'].act.relationship['totalInvolved'].act.observation.value.PQ}");
				if(totalSlides  != null){
					temp = getPQSummary((PQ)totalSlides );
					if(!StringUtils.isBlank(temp)){
						description += temp+" slides";
					}
				}
			}
			description = addSeperator(description,';');
			
			//histologicType
			description += ee.evaluate("#{dcisDetails.act.relationship['histologicType'].act.observation.value.formatted}")+";";
			
			//calcifications
			obj = ee.evaluate("#{dcisDetails.act.relationship['calcification'].act.observation.value.CE}");
			if(obj != null ){
				description += getCESummary((CE)obj," Calcification:");
			}
			obj = ee.evaluate("#{dcisDetails.act.relationship['calcificationYes']}");
			if(obj != null && ((ActRelationshipEx)obj).getEnabled().booleanValue()){
				Object ce = ee.evaluate("#{dcisDetails.act.relationship['calcificationYes'].act.observation.value.CE}");
				if(ce != null)
					description += getCESummary((CE)ce," ")+";"; 
			}
			//Grade
			obj = ee.evaluate("#{dcisDetails.act.relationship['nuclearGrade'].act.observation.value.CE}");
			if(obj != null ){
				description += getCESummary((CE)obj," Grade:")+";";
			}
			description += getMarginSummary((ActEx)ee.evaluate("#{dcisDetails.act}"));
			
		}
		return description;
	}
		
	private static String getMarginSummary(ActEx act) {
		ExpressionEvaluator ee = new ExpressionEvaluator();
		StringBuffer pMargins = new StringBuffer(" Positive Margins:");
		StringBuffer nMargins = new StringBuffer(" Negative Margins:");
		StringBuffer marginsStr = new StringBuffer();		
		boolean hasNegetivemargin=false,hasPositiveMargin = false;
		String[] margins = {"deepMargin","medialMargin","lateralMargin","anteriorSuperiorMargin","anteriorInferiorMargin"};
		ee.addVariable("act", act);
		for(int i=0;i<margins.length;i++){
			Object obj = ee.evaluate("#{act.relationship['"+margins[i]+"'].act.observation.value.CE}");
			if(obj != null && ((CE)obj).getDisplayName().equals("Positive")){
				hasPositiveMargin = true;
				pMargins.append(ee.evaluate("#{act.relationship['"+margins[i]+"'].act.title.ST.value} ")+
						getPQSummary((PQ)ee.evaluate("#{act.relationship['"+margins[i]+"Size'].act.observation.value.PQ}"), "(",") "));
			}else if(obj != null && ((CE)obj).getDisplayName().equals("Negative")){
				hasNegetivemargin = true;
				nMargins.append(ee.evaluate("#{act.relationship['"+margins[i]+"'].act.title.ST.value} ")+
						getPQSummary((PQ)ee.evaluate("#{act.relationship['"+margins[i]+"Size'].act.observation.value.PQ}"), "(",") "));
			}
		}
		if(hasPositiveMargin){
			marginsStr.append(pMargins.toString().trim()+";");
		}
		if(hasNegetivemargin){
			marginsStr.append(nMargins.toString().trim()+";");
		}
		return marginsStr.toString();
	}

	//Nodes: <# positive>/<#examined>: <Location>; Size:<size of largest tumor deposit>; If Extranodal extension=true then display
	//â€œExtranodal extensionâ€�> 
	@SuppressWarnings("unchecked")
	public static String getLymphNodeSummary(ActEx act,String laterality) throws Exception{
		StringBuffer nodeSummary = new StringBuffer();
		boolean hasExtraNodalExtn = false;
		
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		List<ActRelationship> nodes = (List<ActRelationship>)ee.evaluate("#{act.relationshipsList['"+laterality+"AdditionalNodes']}");
		Object obj = ee.evaluate("#{act.relationship['"+laterality+"SentinelLymphNodes']}");
		if(obj != null)
			nodes.add((ActRelationship) obj);
		List<ActRelationship> enabledNodes = new ArrayList<ActRelationship>();
		for(ActRelationship node:nodes){
			if(node.isEnabled())
				enabledNodes.add(node);
		}
		if(enabledNodes.size() == 0)
			return nodeSummary.toString();

		nodeSummary.append(laterality+" Nodes: "+ee.evaluate("#{act.relationship['"+laterality+"TotalPositiveNodes'].act.observation.value.INT.value}")); 
		nodeSummary.append("/"+ee.evaluate("#{act.relationship['"+laterality+"TotalExaminedNodes'].act.observation.value.INT.value}"+";")); 
		Object tempObj;
		for(ActRelationship node:enabledNodes){
			ee.addVariable("node", node);
			if(node.isEnabled()){
				tempObj = ee.evaluate("#{node.act.relationship['location'].act.observation.methodCode.values}");
			if(tempObj != null)
					nodeSummary.append(getCEValuesSummary((List<CE>)tempObj));
			// Size:<size of largest tumor deposit>; ??????????
				tempObj = ee.evaluate("#{node.act.relationship['extention'].act.observation.value.CE}");
				if(tempObj != null && getCESummary((CE)tempObj).equalsIgnoreCase("Yes")) hasExtraNodalExtn = true;
			}
		}
		if(hasExtraNodalExtn)
			nodeSummary.append("Extranodal extension;");
		return nodeSummary.toString();
	}
	
	//ER: <ER overall result, for example “Positive”> + (<proportion score>) +
	//Intensity: <intensity score> Total:(<total score>; PR: <PR overall result, for example “Positive”> +
	//(<proportion score>) + Intensity: <intensity score> Total:(<total score>; HER2/neu: <HER2 overall
	//result, for example “Positive”> + (IHC: <IHC score>) + (FISH: <FISH score> (<FISH test type>)
	public static String getReceptorSummary(ActEx act,String laterality) throws Exception{
		StringBuffer receptorSummary = new StringBuffer();
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		Object obj = ee.evaluate("#{act.relationshipsList['"+laterality+"Receptor']}");
		if(obj != null){
			for(ActRelationshipEx receptor:(List<ActRelationshipEx>)obj){
				ee.addVariable("receptor", receptor);
				Object tempObj = ee.evaluate("#{receptor.act.observation.value.CE}");
				if(tempObj == null)
					continue;
				receptorSummary.append((String)ee.evaluate("#{receptor.act.text.ST.value}")+
						": "+ee.evaluate("#{receptor.act.observation.value.CE} "));
				Object scores = ee.evaluate("#{receptor.act.relationshipsList['score']}");
				if(scores != null){
					for(ActRelationshipEx score:(List<ActRelationshipEx>)scores){
						ee.addVariable("score", score);
						receptorSummary.append((String)ee.evaluate("#{score.act.text.ST.value}")+":"
								+(String)ee.evaluate("#{score.act.observation.value.CE.displayName}")+",");						
					}
					receptorSummary.append(';');
				}
				Object total = ee.evaluate("#{receptor.act.relationship['total']}");
				if(total != null){
					receptorSummary.append(getPQSummary((PQ)ee.evaluate("#{receptor.act.relationship['total'].act.observation.value.PQ}"),
							(String)ee.evaluate("#{receptor.act.relationship['total'].act.text.ST.value} ")));
				}
			}
		}
		Object receptorMarker = ee.evaluate("#{act.relationship['"+laterality+"ReceptorMarker']}");
		if(receptorMarker != null && ee.evaluate("#{act.relationship['"+laterality+"ReceptorMarker'].act.observation.value.CE}") != null ){
			receptorSummary.append(ee.evaluate(" #{act.relationship['"+laterality+"ReceptorMarker'].act.text.ST.value}")+":");
			receptorSummary.append(ee.evaluate("#{act.relationship['"+laterality+"ReceptorMarker'].act.observation.value.CE}"));
		}
		return receptorSummary.toString();
	}
	/*Invasive: <semi-colon concatenated string of Invasive histology types.> <tumor size> cm <if multifocal
	tumor=true then “, Multifocal”>; <if calcifications then “Calcification: <calcification type>>;SBR Grade:
	<SBR grade> (Nuclear grade <nuclear grade>; Mitotic count <mitotic count>; Tubule/papilla formation
	<tubule pappilla formation>); <if Lymphatic-vascular invasion = true then “Lymphatic-vascular
	invasion”>; “Skin involvement:<skin involvement> + <type> (i.e. Paget disease); <if any Invasive
	Margins = Positive, display Positive, else Negative>;
	*/
	public static String getInvasiveSummary(ActEx act,String laterality) throws Exception{
		String invaiseSumm = "";
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		Object obj = ee.evaluate("#{act.relationship['"+laterality+"HistologyInvasiveTumorDetails']}");
		if(obj != null && ((ActRelationshipEx)obj).getEnableRelationship() != null && ((ActRelationshipEx)obj).getEnableRelationship().booleanValue()){
			//histologicType
			ee.addVariable("invasive", ((ActRelationshipEx)obj));
			invaiseSumm = "Invasive: ";
			//Invasive: <semi-colon concatenated string of Invasive histology types.>
			invaiseSumm += ee.evaluate("#{invasive.act.relationship['invasiveHistology'].act.observation.value.formatted}")+";";
			// tumor size
			obj = ee.evaluate("#{invasive.act.relationship['tumorSize'].act.observation.value.PQ}");
			if(obj == null){
				// log here
			}else{
				invaiseSumm += getPQSummary((PQ)obj);
			}
			//<if multifocal tumor=true then “, Multifocal”>;
			
			//calcifications
			obj = ee.evaluate("#{invasive.act.relationship['calcification'].act.observation.value.CE}");
			if(obj != null ){
				invaiseSumm += getCESummary((CE)obj," Calcification:");
			}
			obj = ee.evaluate("#{invasive.act.relationship['calcificationYes']}");
			if(obj != null && ((ActRelationshipEx)obj).getEnabled().booleanValue()){
				Object ce = ee.evaluate("#{invasive.act.relationship['calcificationYes'].act.observation.value.CE}");
				if(ce != null)
					invaiseSumm += getCESummary((CE)ce," "); 
			}
			invaiseSumm = addSeperator(invaiseSumm,';');
			
			//SBR Grade:<SBR grade> 
			obj = ee.evaluate("#{invasive.act.relationship['sbrGrade'].act.observation.value.originalText}");
			if(obj != null ){
				invaiseSumm += " SBR Grade:"+(String)obj;
			}
			String subStr ="";
			//(Nuclear grade <nuclear grade>; 
			obj = ee.evaluate("#{invasive.act.relationship['nuclearGrade'].act.observation.value.CE}");
			if(obj != null ){
				subStr += getCESummary((CE)obj," Nuclear grade ")+";";
			}
			//Mitotic count <mitotic count>; 
			obj = ee.evaluate("#{invasive.act.relationship['mitoticCount'].act.observation.value.CE}");
			if(obj != null ){
				subStr += getCESummary((CE)obj," Mitotic count ")+";";
			}
			
			//Tubule/papilla formation  <tubule pappilla formation>); 
			obj = ee.evaluate("#{invasive.act.relationship['tubeleFormation'].act.observation.value.CE}");
			if(obj != null ){
				subStr += getCESummary((CE)obj," Tubule/papilla formation ")+";";
			}
			if(!StringUtils.isBlank(subStr)){
				invaiseSumm += "("+subStr+")";
			}
			
			//Mitotic count <mitotic count>; 
			obj = ee.evaluate("#{invasive.act.relationship['lymphaticVascularInvasion'].act.observation.value.CE}");
			if(obj != null && getCESummary((CE)obj).equals("Present")){
				invaiseSumm += " Lymphatic-vascular;";
			}
			/*   “Skin involvement:<skin involvement> + <type> (i.e. Paget disease); */
			obj = ee.evaluate("#{invasive.act.relationship['skinInvolvement'].act.observation.value.CE}");
			if(obj != null ){
				invaiseSumm += getCESummary((CE)obj," Skin involvement:")+";";
			}
		
			invaiseSumm += getMarginSummary((ActEx)ee.evaluate("#{invasive.act}"));
		}
		return invaiseSumm;		
	}
	
	/*
	LCIS: <semi-colon concatenated string of LCIS histology types.> ; <If LCIS histology type includes
	“Pleomorphic” then display: <if any LCIS Margins = Positive, display Positive, else Negative> (<minimum
	margin size> mm)>;
	*/
	public static String getLCISSummary(ActEx act,String laterality) throws Exception{
		String lcisSumm = "";
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		Object obj = ee.evaluate("#{act.relationship['"+laterality+"HistologyLCISDetails']}");
		if(obj != null && ((ActRelationshipEx)obj).getEnableRelationship() != null && ((ActRelationshipEx)obj).getEnableRelationship().booleanValue()){
			lcisSumm = "LCIS:";
			ee.addVariable("lcis", obj);
			Object tempObj = ee.evaluate("#{lcis.act.relationship['lcisType'].act.observation.value.CE}");
			if(obj != null && getCESummary((CE)tempObj).equals("Pleomorphic")){
			    lcisSumm +=getMarginSummary((ActEx)ee.evaluate("#{lcis.act}"));
			}
		}
		return lcisSumm;
	}
	
	public static String getPQSummary(PQ pq,String prefix){
		String pqSum = "";
		if(pq == null){
			return pqSum;
		}
		pqSum = prefix + getPQSummary(pq);
		return pqSum;
	}
	public static String getPQSummary(PQ pq,String prefix,String suffix){
		StringBuffer pqSum = new StringBuffer();
		if(pq == null){
			return "";
		}
		pqSum.append(getPQSummary(pq));
		if(StringUtils.isBlank(pqSum.toString()))
			return "";
		else
			return prefix+pqSum.toString()+suffix;		
	}
	
	public static String getPQSummary(PQ pq){
		StringBuffer pqSum = new StringBuffer();
		if(pq == null){
			return "";
		}
		//if(stringNotEmpty(pq.getValue())){//origin
			if(StringUtils.isBlank(pq.getOriginalText()))
				return "";
			pqSum.append(pq.getOriginalText());
			if(!pq.getUnit().equals("1")){
				pqSum.append(pq.getUnit()); 
			}
		//}
		return pqSum.toString();
	}
	/* CE with prefix*/
	public static String getCESummary(CE ce,String prefix){
		String ceSumm = "";
		if(ce == null){
			return ceSumm;
		}
		if(!StringUtils.isBlank(ce.getDisplayName())){
			ceSumm = prefix + getCESummary(ce);
		}
		return ceSumm;
	}
	/* CE with no prefix*/
	public static String getCESummary(CE ce){
		String ceSumm = "";
		if(ce == null){
			return ceSumm;
		}
		if(!StringUtils.isBlank(ce.getDisplayName())){
			ceSumm = ce.getDisplayName();
		}
		return ceSumm;
	}
	/* INT with no prefix*/
	public static long getINTSummary(INT _int){
		long intSumm = 0;
		if(_int == null){
			return intSumm;
		}
		else return _int.getValue();
	}
	
	public static String getCEValuesSummary(List<CE> values){
		String valuesSumm = "";
		for(CE ce:values){
			valuesSumm += ce.getDisplayName()+";";
		}
		return valuesSumm;
	}
	
	// add seperator 
	public static String addSeperator(String actual,char seperator){
		if(!StringUtils.isBlank(actual) && actual.charAt(actual.length()-1) != seperator)
			actual +=seperator +" ";
		return actual;	
	}	
	
	
}
