package org.tolven.labresults;

import org.tolven.process.ComputeBase;
import java.util.ArrayList;
import java.util.List;
import org.tolven.trim.Act;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.CD;
import org.tolven.trim.IVLPQ;
import org.tolven.trim.SETCDSlot;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ex.TrimFactory;

public class ComputeInterpretation extends ComputeBase{

	TrimFactory factory = new TrimFactory();

	@Override
	public void compute() throws Exception {
		Act act = getAct();
		SETCDSlot interpretationCodes = act.getObservation().getInterpretationCode();
		CD interpCode = factory.createCD();
		String genderCode = new String();
		ActRelationship selectedRelationship = null;

		// common code system for all interpretatio codes
		interpCode.setCodeSystem("2.16.840.1.113883.5.83");

		List<ObservationValueSlot> currentObservationValues = act.getObservation().getValues();
		
		if (null != currentObservationValues && null != currentObservationValues.get(0).getPQ())
		    {
			Double observationValue = currentObservationValues.get(0).getPQ().getValue();
			
			// from here
			if (null != act)
			    {
				
				// Grab gender from TRIM.
				for (ActParticipation participation : act.getParticipations())
				    {
					if (participation.getName().equals("subject"))
					    {
						genderCode = participation.getRole().getPlayer().getLivingSubject().getAdministrativeGenderCode().getCE().getCode();
					    }
				    }
				
				// Traverse the relationships, looking for preconditions, if there are
				// and one matches, select it.
				for (ActRelationship lRel : act.getRelationships())
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
					selectedRelationship = act.getRelationships().get(0);
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
						Double lowValue = selectedIVLPQ.getLow().getPQ().getValue();
						Double highValue = selectedIVLPQ.getHigh().getPQ().getValue();
						if (observationValue < lowValue)
						    {
							interpCode.setCode("L");
							interpCode.setDisplayName("Low");
						    }
						else if (observationValue > highValue)
						    {
							interpCode.setCode("H");
							interpCode.setDisplayName("High");
						    }
						else 
						    {
							interpCode.setCode("N");
							interpCode.setDisplayName("Normal");
						    }
					    }
					else if (hasLowValue && !hasHighValue)
					    {
						// only low value
						Double lowValue = selectedIVLPQ.getLow().getPQ().getValue();
						if (observationValue < lowValue)
						    {
							interpCode.setCode("L");
							interpCode.setDisplayName("Low");
						    }
					    }
					else if (!hasLowValue && hasHighValue)
					    {
						// only high value
						Double highValue = selectedIVLPQ.getHigh().getPQ().getValue();
						if (observationValue > highValue)
						    {
							interpCode.setCode("H");
						    }
					    }
					else 
					    {
						// somehow we fell through, should not happen.
					    }
					
				    }
			    }
			
			if(null != interpretationCodes && interpretationCodes.getCDS().size() > 0) {
			    interpretationCodes.getCDS().set(0, interpCode);
			} else {
			    interpretationCodes.getCDS().add(interpCode);
			}
		    }
		else
		    {
			// this isn't a PQ, additional logic might be here later.
		    }
	}
}