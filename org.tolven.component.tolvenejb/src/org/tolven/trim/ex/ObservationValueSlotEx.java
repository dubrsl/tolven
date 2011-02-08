package org.tolven.trim.ex;

import java.util.List;

import org.tolven.trim.AD;
import org.tolven.trim.BL;
import org.tolven.trim.CD;
import org.tolven.trim.CE;
import org.tolven.trim.DataType;
import org.tolven.trim.ED;
import org.tolven.trim.EN;
import org.tolven.trim.II;
import org.tolven.trim.INT;
import org.tolven.trim.IVLPQ;
import org.tolven.trim.NullFlavor;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.PQ;
import org.tolven.trim.REAL;
import org.tolven.trim.RTO;
import org.tolven.trim.TS;

public class ObservationValueSlotEx extends ObservationValueSlot {
	
	private static final long serialVersionUID = 1L;

	public DataType getValue() {
		if (this.getAD()!=null) {
			return getAD();
		}
		if (this.getBL()!=null) {
			return getBL();
		}
		if (this.getCD()!=null) {
			return getCD();
		}
		if (this.getCE()!=null) {
			return getCE();
		}
		if (this.getED()!=null) {
			return getED();
		}
		if (this.getEN()!=null) {
			return getEN();
		}
		if (this.getII()!=null) {
			return getII();
		}
		if (this.getINT()!=null) {
			return getINT();
		}
		if (this.getIVLPQ()!=null) {
			return getIVLPQ();
		}
		if (this.getNull()!=null) {
			return getNull();
		}
		if (this.getPQ()!=null) {
			return getPQ();
		}
		if (this.getREAL()!=null) {
			return getREAL();
		}
		if (this.getRTO()!=null) {
			return getRTO();
		}
		if (this.getTS()!=null) {
			return getTS();
		}
		return null;
	}
	
	public void setValue(DataType value) {
		setAD( null );
		setBL( null );
		setCD( null );
		setCE( null );
		setED( null );
		setEN( null );
		setII( null );
		setINT( null );
		setIVLPQ( null );
		setNull( null );
		setPQ( null );
		setREAL( null );
		setRTO( null );
		setTS( null );
		
		if (value == null){
			return;
		}
		
		// Note : The sequence in which these expression are placed for each of the Datatype objects is Very Important.
		// The child Datatype object should always be before parent . For ex. expression for CD appears before CD, because CE is parent of CD. 
		
		if (value instanceof AD) {
			setAD( (AD) value );
			return;
		}
		if (value instanceof BL) {
			setBL( (BL) value );
			return;
		}
		if (value instanceof CD) {
			setCD((CD) value);
			return;			
		}
		if (value instanceof CE) {
			setCE((CE) value);
			return;			
		}				
		if (value instanceof ED) {
			setED((ED) value);
			return;			
		}
		if (value instanceof EN) {
			setEN((EN) value);
			return;			
		}
		if (value instanceof II) {
			setII((II) value);
			return;			
		}
		if (value instanceof INT) {
			setINT((INT) value);
			return;			
		}
		if (value instanceof IVLPQ) {
			setIVLPQ((IVLPQ) value);
			return;			
		}
		if (value instanceof NullFlavor) {
			setNull((NullFlavor) value);
			return;			
		}
		if (value instanceof PQ) {
			setPQ((PQ) value);
			return;			
		}
		if (value instanceof REAL) {
			setREAL((REAL) value);
			return;			
		}
		if (value instanceof RTO) {
			setRTO((RTO) value);
			return;			
		}
		if (value instanceof TS) {
			setTS((TS) value);
			return;			
		}
	}

	@Override
	public String getDatatype() {
		return "OBSVALUE";
	}

	@Override
	public String toString() {
		DataType value = getValue();
		if (value!=null) return value.toString();
		return null;
	}

	public List<CE> getCEValues(){
		if(getSETCES().size() > 0)
		return getSETCES();
		return null;
	}
	public List<INT> getINTValues(){
		if(getSETINTS().size() > 0)
		return getSETINTS();
		return null;
	}
	
	/**
	 * Formatted output of Observation values 
	 * @return comma-separated list of value
	 */
	public String getFormatted(){
    	StringBuffer sb = new StringBuffer( 100 );
    	for (CE ce : getSETCES() ) {
    		if (sb.length() > 0) sb.append( ", " );
    		sb.append( ce.toString());
    	}
    	for (INT i : getSETINTS() ) {
    		if (sb.length() > 0) sb.append( ", " );
    		sb.append( i.getValue() );
    	}
        if(getValue() != null) {
    		if (sb.length() > 0) sb.append( ", " );
        	sb.append( getValue().getOriginalText());
        }
        return sb.toString();
    }

	public void setCEValues( List<CE> values ) {
		getSETCES().clear();
		getSETCES().addAll(values);		
	}
	
	public NullFlavor getNullValue() {
		return getNull();
	}
	
}
