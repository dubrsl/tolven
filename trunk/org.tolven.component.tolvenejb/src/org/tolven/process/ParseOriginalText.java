package org.tolven.process;

import org.tolven.app.el.ELFunctions;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.Entity;
import org.tolven.trim.INT;
import org.tolven.trim.INTSlot;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.PQ;
import org.tolven.trim.PQSlot;
import org.tolven.trim.Role;
import org.tolven.trim.ex.ObservationValueSlotEx;

public class ParseOriginalText extends ComputeBase {

	private String slot;
	
	@Override
	public void compute() throws Exception {
		TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
		ee.addVariable("trim", getTrim());
		ee.addVariable("account", getAccountUser().getAccount());
		if (getNode() instanceof Act) {
			ee.addVariable("act", getNode());
		}
		if (getNode() instanceof Role) {
			ee.addVariable("role", getNode());
		}
		if (getNode() instanceof Entity) {
			ee.addVariable("entity", getNode());
		}
		Object slotNode = ee.evaluate(getSlot());
		if (slotNode instanceof ObservationValueSlotEx) {
			slotNode = ((ObservationValueSlotEx)slotNode).getValue();
		}
		if (slotNode instanceof PQSlot) {
			slotNode = ((PQSlot)slotNode).getPQ();
			if (slotNode==null) {
				slotNode = ((PQSlot)slotNode).getINT();
			}
		}
		if (slotNode instanceof INTSlot) {
			slotNode = ((INTSlot)slotNode).getINT();
		}
		if (slotNode instanceof PQ) {
			PQ pq = (PQ) slotNode;
			if (pq!=null) {
				String originalText = pq.getOriginalText();
				if (originalText==null || originalText.length()==0) {
					pq.setValue(0.0);
				} else {
					pq.setValue(Double.parseDouble(originalText));
				}
			}
		} else if (slotNode instanceof INT) {
			INT intVal = (INT)slotNode;
			if (intVal!=null) {
				String originalText = intVal.getOriginalText();
				if (originalText==null || originalText.length()==0) {
					intVal.setValue(0);
				} else {
					intVal.setValue(Long.parseLong(originalText));
				}
			}
		}
		
//		TolvenLogger.info( "" , Copy.class);
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}


}
