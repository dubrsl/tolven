package org.tolven.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tolven.logging.TolvenLogger;
import org.tolven.process.helper.ComputeHelper;
import org.tolven.trim.Act;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Choice;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ex.TrimFactory;


public class ToggleRelation extends ComputeBase {
	private static final TrimFactory trimFactory = new TrimFactory();
	private boolean loadChildTrims;
	private String choice;
	private boolean enabled;
	private String procName;
	private boolean enableChoice;
	private String templateBody;
	private String optionsStr;
	private boolean disableSiblings = false;
	private Map<String,List<String>> parameters = null; 
	private Map<String, Object> trimEnv = new HashMap<String, Object>();
	private static TolvenLogger logger = new TolvenLogger("ToggleRelation.java");
	
	@Override
	public void compute() throws Exception {
		logger.info( "compute() START- enabled =" + isEnabled());
		super.checkProperties();
		try{
			trimEnv = prepareTrimEnv(trimEnv);
			parameters = parserParams(getOptionsStr());
			if(isLoadChildTrims()){
				//List<ActRelationship> relations = getTrim().getAct().getRelationships(); getNode();
				List<ActRelationship> relations = ((Act)getNode()).getRelationships(); //getNode();
				for(ActRelationship procGroup:relations){
					List<Choice> choices = procGroup.getChoices();
					for(Choice choice:choices){
						if(parameters.get(procGroup.getName()) != null){
							List<String> options = parameters.get(procGroup.getName());
							if(options.size() == 1 && options.get(0).equalsIgnoreCase("*"))
								procGroup.getAct().getRelationships().addAll(ComputeHelper.getAllRelations(choice.getInclude(),trimEnv));
							else
								procGroup.getAct().getRelationships().add(ComputeHelper.getRelation(choice.getInclude(),options,trimEnv));
						}else
							logger.info("option not found for "+procGroup.getName());
					}
				}			
			} else	if (isEnabled()) {
				
				Act _procAct = ComputeHelper.getAct(this.getAct(),getProcName()); // procedure relation
				ActRelationship _choiceRelation = ComputeHelper.getRelationFromAct(_procAct,getChoice()); // choice relation
				if(_choiceRelation != null)
					_choiceRelation.setEnabled(enableChoice);  //TODO: should we clear the values when the relation is disabled?
				if(isDisableSiblings()){
					for(ActRelationship _rel:_procAct.getRelationships()){
						if(_rel.getName().equalsIgnoreCase(getChoice())){
							_rel.setEnabled(true);							
						}
						else{
							_rel.setEnabled(false);
						}
							
					}
				}
			}
			// Disable the Compute since its job is done.
	    	for (Property property : getComputeElement().getProperties()) {
				if (property.getName().equals("loadChildTrims")) {
					property.setValue(Boolean.FALSE);
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		logger.info( "compute() END- enabled =" + isEnabled());
	}	
	
	
	/*@Override
	public void compute() throws Exception {
		System.out.println( "[ToggleRelation.compute() - enabled =]" + isEnabled());
		super.checkProperties();
		try{
			if (isEnabled()) {
				parameters = parserParams(getOptionsStr());
				Act _procAct = getAct(this.getAct(),getProcedure()); // procedure relation
				ActRelationship _choiceRelation = (_procAct,getTemplate());
				if(_act == null)
					throw new Exception("Invalid procName was requested: "+procName);
				if (getAction().equals("enable"))
				{
					ActRelationship _relation = getRelation();	
					_act.getRelationships().add(_relation);
					System.out.println( "InsertTrim.compute() - adding relation " +_relation.getName() +" to procedure" +getProcName());	          
				}
				else // Remove
				{
					for(int i=0; i<_act.getRelationships().size();i++){
						if(_act.getRelationships().get(i).getName().equalsIgnoreCase(getTemplateName())){
							System.out.println( "InsertTrim.compute() - removing relation " + _act.getRelationships().get(i).getName() +" from "+getProcName());
							_act.getRelationships().remove(i);
							break;
						}
					}				
				}
		       
		        // Disable the Compute since its job is done.
		    	for (Property property : getComputeElement().getProperties()) {
					if (property.getName().equals("enabled")) {
						property.setValue(Boolean.FALSE);
						break;
					}
				}
				System.out.println( "[InsertTrim.compute() - enabled reset =]" + isEnabled());
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}*/


	public String getTemplateBody() {
		if (templateBody==null) {
			
		}
		return templateBody;
	}

	public void setTemplateBody(String templateBody) {
		this.templateBody = templateBody;
	}

	public String getProcName() {
		return procName;
	}

	public void setProcName(String procName) {
		this.procName = procName;
	}

	public String getTemplateName(){
		return choice.substring(choice.lastIndexOf('/')+1);		
	}
	private List<String> getValueset(String _procName){
		return getParameters().get(_procName);
	}

	public Map<String,List<String>> getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public String getOptionsStr() {
		return optionsStr;
	}

	public void setOptionsStr(String optionsStr) {
		this.optionsStr = optionsStr;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public boolean isEnableChoice() {
		return enableChoice;
	}


	public void setEnableChoice(boolean enableChoice) {
		this.enableChoice = enableChoice;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isLoadChildTrims() {
		return loadChildTrims;
	}

	public void setLoadChildTrims(boolean loadChildTrims) {
		this.loadChildTrims = loadChildTrims;
	}
	
	private Map<String,List<String>> parserParams(String paramsStr) throws Exception{
		Map<String,List<String>> paramsMap = new HashMap();
		if(paramsStr == null)
			return paramsMap;
		String[] params = paramsStr.split(";");
		for(int i=0;i<params.length;i++){
			String[] _temp = params[i].split(",");
			String[] _template = _temp[0].split("=");
			String[] _options = _temp[1].split("=");
			if(_template[0].equalsIgnoreCase("template") && _options[0].equalsIgnoreCase("options")){
				paramsMap.put(_template[1],Arrays.asList(_options[1].split(":")));
			}else
				throw new Exception("Invalid parameter string "+paramsStr);
			
		}
		return paramsMap;
	}
	private Map<String,Object> prepareTrimEnv(Map<String,Object> trimEnv){
		trimEnv.put("accountUser", getAccountUser());
		trimEnv.put("now", getNow());
		trimEnv.put("menuBean", getMenuBean());
		trimEnv.put("trimBean", getTrimBean());
		trimEnv.put("contextList", getContextList());
		trimEnv.put("trimFactory", trimFactory);
		
		return trimEnv;
	}


	public boolean isDisableSiblings() {
		return disableSiblings;
	}


	public void setDisableSiblings(boolean disableSiblings) {
		this.disableSiblings = disableSiblings;
	}
	

	
}
