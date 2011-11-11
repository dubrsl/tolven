package org.tolven.labresults;

import java.util.List;

import org.tolven.process.ComputeBase;
import org.tolven.trim.Act;
import org.tolven.trim.*;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.CD;
import org.tolven.trim.EDSlot;
import org.tolven.trim.IVLPQ;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.PQSlot;
import org.tolven.trim.ST;
import org.tolven.trim.ex.TrimFactory;

public class ComputeReferenceRange extends ComputeBase{

	TrimFactory factory = new TrimFactory();
	
	@Override
	public void compute() throws Exception {
	    Act act=getAct();
	    Act trimAct=getTrim().getAct();
	    ST referenceRangeST = factory.createST();
	    EDSlot ed = factory.createEDSlot();
	    ActRelationship selectedRelationship = null;
	    String genderCode = new String();
	    String returnedRange = new String();
	    if (null != trimAct)
		{

		    if (null != trimAct.getObservation().getValues().get(0).getPQ())
			{
			    // Grab gender from TRIM.
			    for (ActParticipation participation : trimAct.getParticipations())
				{
				    if (participation.getName().equals("subject"))
					{
					    genderCode = participation.getRole().getPlayer().getLivingSubject().getAdministrativeGenderCode().getCE().getCode();
					}
				}
			    
			    // Traverse the relationships, looking for preconditions, if there are
			    // and one matches, select it.
			    for (ActRelationship lRel : trimAct.getRelationships())
				{
				    if (lRel.getName().equals("referenceRange"))
					{
					    if (lRel.getAct().getRelationships().size() > 0)
						{
						    for (ActRelationship pRel : lRel.getAct().getRelationships())
							{
							    if (pRel.getName().equals("precondition"))
								{
								    if (null != pRel.getAct())
									{
									    if (pRel.getAct().getObservation().getValues().get(0).getCE().getDisplayName().equals(genderCode))
										{
										    selectedRelationship = lRel;
										}
									}
								}
							}
						}
					}
				}
			    
			    // if selectedRelationship is still null, there were no precons
			    // and we need to just select the first referenceRange.
			    if (null == selectedRelationship)
				{
				    selectedRelationship = trimAct.getRelationships().get(0);
				}
			    
			    // Found relationship, extract data.
			    if (null != selectedRelationship)
				{
				    IVLPQ selectedIVLPQ = selectedRelationship.getAct().getObservation().getValues().get(0).getIVLPQ();
				    boolean hasLowValue = (null != selectedIVLPQ.getLow().getPQ().getValue());
				    boolean hasHighValue = (null != selectedIVLPQ.getHigh().getPQ().getValue());
				    
				    if (hasLowValue && hasHighValue)
					{
					    // both low and high values
					    String lowValue = Double.toString(selectedIVLPQ.getLow().getPQ().getValue());
					    String highValue = Double.toString(selectedIVLPQ.getHigh().getPQ().getValue());
					    String lowUnit = selectedIVLPQ.getHigh().getPQ().getUnit();
					    String highUnit = selectedIVLPQ.getHigh().getPQ().getUnit();
					    returnedRange = lowValue + " " + lowUnit + " - " + highValue + " " + highUnit;
					}
				    else if (hasLowValue && !hasHighValue)
					{
					    // only low value
					    String lowValue = Double.toString(selectedIVLPQ.getLow().getPQ().getValue());
					    String lowUnit = selectedIVLPQ.getLow().getPQ().getUnit();
					    returnedRange = lowValue + " " + lowUnit;
					}
				    else if (!hasLowValue && hasHighValue)
					{
					    // only high value
					    String highValue = Double.toString(selectedIVLPQ.getHigh().getPQ().getValue());
					    String highUnit = selectedIVLPQ.getHigh().getPQ().getUnit();
					    returnedRange = highValue + " " + highUnit;
					}
				    else 
					{
					    // somehow we fell through, should not happen.
					    returnedRange = "No Range.";
					}
				    
				}
			    
			    ed.setST(referenceRangeST);
			    referenceRangeST.setValue(returnedRange);
			    act.setText(ed);
			}
		}
	    else
		{
		    // just making sure.
		    ed.setST(referenceRangeST);
		    referenceRangeST.setValue("Null Act");
		    act.setText(ed);
		}
	    
	}
    
}