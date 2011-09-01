package org.tolven.pathology;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.PQ;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ActRelationshipEx;

public class ELFunctions {
	
	
	public static String bcpsumm(ActEx act) {
		if (act == null)
			return "";
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		//String[] laterality = {"Left","Right"};
		boolean hasRightProcedure = false;
		boolean hasLeftProcedure = false;
		String lateralityStr = null;
		String description = "";
		try {
			List<ActRelationship> procedures = (List<ActRelationship>) ee.evaluate("#{act.relationshipsList['procedure']}");
			ExpressionEvaluator ee1 = new ExpressionEvaluator();
			for (ActRelationship procedure : procedures) {
				ee1.addVariable("procedure",procedure);
				lateralityStr = (String) ee1.evaluate("#{procedure.act.relationship['laterality'].act.observation.value.CE.displayName}");
				if(lateralityStr == null)
					continue;
				if (lateralityStr.equals("Left")) {
					hasLeftProcedure = true;
				} else if (lateralityStr.equals("Right")) {
					hasRightProcedure = true;
				}
				if (hasLeftProcedure && hasRightProcedure)
					break;
			}
			if(hasLeftProcedure || hasRightProcedure){
				String rInvasive = PathologyReportELUtils.getInvasiveSummary(act, "Right");
				if(rInvasive != null)
					description += rInvasive;
				String rdcis = PathologyReportELUtils.getDCISSummary(act, "Right");
				if(rdcis != null)
					description += rdcis;
				String rlcis = PathologyReportELUtils.getLCISSummary(act, "Right");
				if(rlcis != null)
					description += rlcis;
				
				String rlnodes = PathologyReportELUtils.getLymphNodeSummary(act, "Right");
				if(rlnodes != null)
				description += rlnodes;
				description += PathologyReportELUtils.getReceptorSummary(act, "Right");
				
				String invasive = PathologyReportELUtils.getInvasiveSummary(act, "Left");
				if(invasive != null)
					description += invasive;
				String ldcis = PathologyReportELUtils.getDCISSummary(act, "Left");
				if(ldcis != null)
					description += ldcis;
				String llcis = PathologyReportELUtils.getLCISSummary(act, "Left");
				if(llcis != null)
					description += llcis;
				String lnodes = PathologyReportELUtils.getLymphNodeSummary(act, "Left");
				if(lnodes != null)
					description += lnodes;	
				String lreceptor = PathologyReportELUtils.getReceptorSummary(act, "Left");
				if(lreceptor != null)
					description += lreceptor;	
			}
		} catch (Exception e) {
			TolvenLogger.info(" Error in constructing ProcedureGroup summary",ELFunctions.class);
			e.printStackTrace();
		}
		return description;
	}
	public static String bcpProcedureNames(ActEx act,String actName,String laterality){ 
		String procedureNames ="";
		if(act == null)
			return procedureNames;
		for(ActRelationship procedure:act.getRelationshipsList().get(actName)){
			ExpressionEvaluator ee = new ExpressionEvaluator();
			ee.addVariable("act", procedure.getAct());
			if(((String)ee.evaluate("#{act.relationship['laterality'].act.observation.value.CE.displayName}")).equals(laterality))
				procedureNames += (String)ee.evaluate("#{act.relationship['laterality'].act.observation.value.CE} #{act.title.ST.value}")+"\n";			
		}	
		return procedureNames;
	}
	
	public static String largestTumorSize(List<ActRelationshipEx> tumors){
		String largeTumor = "";
		for(ActRelationshipEx sizeAct:tumors){
			ExpressionEvaluator ee = new ExpressionEvaluator();
			ee.addVariable("relation", sizeAct);
			Object obj = ee.evaluate("#{relation.act.observation.value.PQ.originalText}");
			if(obj != null && obj.toString().length() > 0){
				if(StringUtils.isBlank(largeTumor)){
					largeTumor = String.valueOf(Double.parseDouble(obj.toString()));
					continue;
				}
				if(Double.parseDouble(obj.toString()) > Double.parseDouble(largeTumor))
					largeTumor = String.valueOf(Double.parseDouble(obj.toString())); 
			}
		}
		return largeTumor;
	}
	public static String  histologySize(ActEx act){
		String size=null;
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		Object typeStr = ee.evaluate("#{act.relationship['type'].act.observation.value.originalText}");
		Object obj = null;
		if(typeStr != null){
			switch (Integer.parseInt(typeStr.toString())) {
				case 1: // Invasive
					/*<tumor size> cm <if multifocal tumor=true then “, Multifocal”>*/
					obj = ee.evaluate("#{act.relationship['tumorSize'].act.observation.value.PQ}");
					if(obj != null)
						size = PathologyReportELUtils.getPQSummary((PQ)obj);
				break;
				case 2: // DCIS
					/*<tumor size> cm <if contiguous sections=true
					then “, Contiguous sections” + <contiguous
					section size> + “cm”> <if scattered
					microscopic foci=true then “, Scattered foci” +
					<size> + “cm”> */
					obj = ee.evaluate("#{act.relationship['tumorSize'].act.observation.value.PQ}");
					if(obj != null)
						size = PathologyReportELUtils.getPQSummary((PQ)obj);
					obj = ee.evaluate("#{act.relationship['contiguousSections']}");
					if(obj != null){
						if(((ActRelationshipEx)obj).getEnabled().booleanValue()){
							size+= " Contiguous sections "+
							PathologyReportELUtils.getPQSummary((PQ)ee.evaluate("#{act.relationship['contiguousSections'].act.observation.value.PQ}"));							
						}
					}
					obj = ee.evaluate("#{act.relationship['scatteredMicroscopic']}");
					if(obj != null){
						if(((ActRelationshipEx)obj).getEnabled().booleanValue()){
							size+= " Scattered foci "+
							PathologyReportELUtils.getPQSummary((PQ)ee.evaluate("#{act.relationship['scatteredMicroscopic'].act.observation.value.PQ}"));							
						}
					}			
				break;
				case 3: // LCIS
					
				break;
				case 4: // Benign
					
				break;

				default:
				break;
			}
		}
			
		return size;		
	}
	public static String  histologyGrade(ActEx act){
		String grade=null;
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		Object typeStr = ee.evaluate("#{act.relationship['type'].act.observation.value.originalText}");
		Object obj = null;
		if(typeStr != null){
			switch (Integer.parseInt(typeStr.toString())) {
				case 1: // Invasive <SBR grade>
					obj = ee.evaluate("#{act.relationship['sbrGrade'].act.observation.value.originalText}");
					if(obj != null)
						grade = obj.toString();
				break;
				case 2: // DCIS <Nuclear grade>
					obj = ee.evaluate("#{act.relationship['nuclearGrade'].act.observation.value.CE.displayName}");
					if(obj != null)
						grade = obj.toString();
				break;
				case 3: // LCIS
					
				break;
				case 4: // Benign
					
				break;

				default:
				break;
			}
		}
			
		return grade;		
	}
	public static String  histologyMargins(ActEx act){
		String margins=null;
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		Object typeStr = ee.evaluate("#{act.relationship['type'].act.observation.value.originalText}");
		
		if(typeStr != null){
			switch (Integer.parseInt(typeStr.toString())) {
				case 1: // Invasive
					/*<if any Invasive Margins = Positive, display
					Positive, else Negative> (<minimum margin
					size> mm)*/
					margins = histologyMarginsResult(act) +" "+ histologyMarginsResultSize(act);
				break;
				case 2: // DCIS
					margins = histologyMarginsResult(act) +" "+ histologyMarginsResultSize(act);
				break;
				case 3: // LCIS
					margins = histologyMarginsResult(act) +" "+ histologyMarginsResultSize(act);
				break;
				case 4: // Benign
					
				break;

				default:
				break;
			}
		}
			
		return margins;		
	}
	public static String  histologyClosestMargin(ActEx act){
		String margins=null;
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		Object typeStr = ee.evaluate("#{act.relationship['type'].act.observation.value.originalText}");
		
		if(typeStr != null){
			switch (Integer.parseInt(typeStr.toString())) {
				case 1: // Invasive
					/*<Display label for margin with smallest margin size>*/
					margins = closestHistologyMargin(act);
				break;
				case 2: // DCIS
					margins = closestHistologyMargin(act);
				break;
				case 3: // LCIS
					
					margins = closestHistologyMargin(act);
				break;
				case 4: // Benign
					
				break;

				default:
				break;
			}
		}
			
		return margins;		
	}
	private static String histologyMarginsResult(ActEx act){
		String result = "Negative";
		String[] margins = {"deepMargin","medialMargin","lateralMargin" ,
							"anteriorSuperiorMargin","anteriorInferiorMargin"};
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		for(int i=0;i<margins.length;i++){
			Object obj = ee.evaluate("#{act.relationship['"+margins[i]+"'].act.observation.value.CE.displayName}");
			if(obj != null && !result.equals(obj.toString())){
				result = obj.toString();
				return result;
			}
		}
		return result;
	}
	private static String histologyMarginsResultSize(ActEx act){
		String size = "";
		String marginType;
		String[] margins = {"deepMargin","medialMargin","lateralMargin" ,
							"anteriorSuperiorMargin","anteriorInferiorMargin"};
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		for(int i=0;i<margins.length;i++){
			String obj = (String)ee.evaluate("#{act.relationship['"+margins[i]+"Size'].act.observation.value.PQ.originalText}");
			if(!StringUtils.isBlank(obj)){
				if(StringUtils.isBlank(size)){ size = obj; continue;}
				if(Double.parseDouble(obj) < Double.parseDouble(size))
					size = obj;
			}
		}
		if(size.length() == 0)
			return size;
		else
			return size+ee.evaluate("#{act.relationship['deepMarginSize'].act.observation.value.PQ.unit}");
	}
	private static String closestHistologyMargin(ActEx act){
		String size = "";
		String label = "";
		String marginType;
		String[] margins = {"deepMargin","medialMargin","lateralMargin" ,
							"anteriorSuperiorMargin","anteriorInferiorMargin"};
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.addVariable("act", act);
		for(int i=0;i<margins.length;i++){
			String obj = (String)ee.evaluate("#{act.relationship['"+margins[i]+"Size'].act.observation.value.PQ.originalText}");
			if(!StringUtils.isBlank(obj)){
				if(StringUtils.isBlank(size)){ 
					size = obj;
					label = (String) ee.evaluate("#{act.relationship['"+margins[i]+"'].act.title.ST.value} ");
					continue;
				}
				if(Double.parseDouble(obj) < Double.parseDouble(size))
					size = obj;
					label = (String) ee.evaluate("#{act.relationship['"+margins[i]+"'].act.title.ST.value} ");
			}
		}
		return label;
	}
	
}
