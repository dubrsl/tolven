package org.tolven.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.tolven.logging.TolvenLogger;
import org.tolven.process.helper.ComputeHelper;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Choice;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ValueSet;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;
import org.tolven.trim.ex.ValueSetEx;
import org.tolven.trim.CE;

public class ExpandChoices extends ComputeBase {
	private static final TrimFactory trimFactory = new TrimFactory();
	private boolean loadChildTrims;
	private boolean enabled;
	private boolean enableChoice;
	private String choice;
	private String procName;


	private static TolvenLogger logger = new TolvenLogger("InsertChoiceProcedures.java");
	
	@Override
	public void compute() throws Exception {
		logger.info( "compute() START- loadChildTrims =" + isLoadChildTrims(),this.getClass());
		super.checkProperties();
		Map<String, Object> trimEnv = prepareTrimEnv();
		ActEx _templAct = (ActEx)ComputeHelper.getTrim("procedures/procedureTemplateForCopy", trimEnv).getAct();
		ByteArrayOutputStream bos = pushToStream(_templAct);
		ValueSet _valueSet = null;
		try{			
			if(isLoadChildTrims()){
				List<ActRelationship> _relations = getAct().getRelationships();
				List<ActRelationship> _newRelations = new ArrayList<ActRelationship>();
				
				for(ActRelationship _relation:_relations){
					List<Choice> _choices = _relation.getChoices();
					if(_choices.size() == 0){						
						_templAct = (ActEx)getCopy(bos); // get cloned deep copy of the act.
						ActRelationship _rel = _templAct.getRelationship().get("procedure");
						_rel.getAct().setTitle(_relation.getAct().getTitle());
						_rel.setName(_relation.getName());
						ActRelationship _specRel = ((ActEx)_rel.getAct()).getRelationship().get("specimen"); 
						_rel.getAct().getRelationships().clear();
						_rel.getAct().getRelationships().add(_specRel);
						_rel.setEnabled(false);
						_newRelations.add(_rel);
						continue;
					}
					for(Choice _choice: _choices){
						_valueSet = ((TrimEx)getTrim()).getValueSet().get(_choice.getInclude());
						if(_valueSet == null){
							_templAct = (ActEx)getCopy(bos); // get cloned deep copy of the act.
							ActRelationship _rel = _templAct.getRelationship().get("procedure");
							_rel.getAct().setTitle(_relation.getAct().getTitle());
							_rel.setName(_relation.getName()+_choice.getTitle());
							ActRelationship _laterality = ((ActEx)_rel.getAct()).getRelationship().get("laterality");
							_laterality.getAct().getObservation().getValues().get(0).getLabel().setValue(_choice.getTitle());
							_laterality.getAct().getRelationships().clear();
							_rel.setEnabled(false);
							_newRelations.add(_rel);
							
						}else{
							for(Object _dataType:((ValueSetEx)_valueSet).getValues()){
								CE _ce = (CE)_dataType;
								_templAct = (ActEx)getCopy(bos); // get cloned deep copy of the act.
								ActRelationship _rel = _templAct.getRelationship().get("procedure");
								_rel.getAct().setTitle(_relation.getAct().getTitle());
								_rel.setName(_relation.getName()+_choice.getTitle()+_ce.getLabel().getValue());
								ActRelationship _laterality = ((ActEx)_rel.getAct()).getRelationship().get("laterality");
								_laterality.getAct().getObservation().getValues().get(0).getLabel().setValue(_choice.getTitle());
								ActRelationship _location = ((ActEx)_laterality.getAct()).getRelationship().get("location");
								_location.getAct().getObservation().getValues().get(0).getLabel().setValue(_ce.getLabel().getValue());
								_location.getAct().getObservation().getValues().get(0).setCE(_ce);	
								_rel.setEnabled(false);
								_newRelations.add(_rel);							
							}
						}
					}	
				}
				_relations.clear();
				_relations.addAll(_newRelations);
				
				// Disable the Compute since its job is done.
		    	for (Property property : getComputeElement().getProperties()) {
					if (property.getName().equals("loadChildTrims")) {
						property.setValue(Boolean.FALSE);
						break;
					}
				} 
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		logger.info( "compute() END- loadChildTrims =" + isLoadChildTrims(),this.getClass());
	}

	public boolean isLoadChildTrims() {
		return loadChildTrims;
	}

	public void setLoadChildTrims(boolean loadChildTrims) {
		this.loadChildTrims = loadChildTrims;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnableChoice() {
		return enableChoice;
	}

	public void setEnableChoice(boolean enableChoice) {
		this.enableChoice = enableChoice;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getProcName() {
		return procName;
	}

	public void setProcName(String procName) {
		this.procName = procName;
	}	
	private Map<String,Object> prepareTrimEnv(){
		Map<String, Object> trimEnv = new HashMap<String, Object>();
		trimEnv.put("accountUser", getAccountUser());
		trimEnv.put("now", getNow());
		trimEnv.put("menuBean", getMenuBean());
		trimEnv.put("trimBean", getTrimBean());
		trimEnv.put("contextList", getContextList());
		trimEnv.put("trimFactory", trimFactory);		
		return trimEnv;
	}

	public ByteArrayOutputStream pushToStream(Object orig) {
		ByteArrayOutputStream bos = null;
        try {
            // Write the object out to a byte array
            bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
       
        return bos;
    }
	public Object getCopy(ByteArrayOutputStream bos){
		Object obj = null;
		try{
        ObjectInputStream in = new ObjectInputStream(
            new ByteArrayInputStream(bos.toByteArray()));
        obj = in.readObject();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

}
