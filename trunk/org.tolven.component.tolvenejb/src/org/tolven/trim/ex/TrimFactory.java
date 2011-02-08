package org.tolven.trim.ex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRegistry;

import org.apache.commons.codec.binary.Base64;
import org.tolven.trim.AD;
import org.tolven.trim.ADSlot;
import org.tolven.trim.ADXPSlot;
import org.tolven.trim.Act;
import org.tolven.trim.ActClass;
import org.tolven.trim.ActMood;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.AddressPartType;
import org.tolven.trim.AddressUse;
import org.tolven.trim.Application;
import org.tolven.trim.CD;
import org.tolven.trim.CDSlot;
import org.tolven.trim.CE;
import org.tolven.trim.CESlot;
import org.tolven.trim.CV;
import org.tolven.trim.Compute;
import org.tolven.trim.CopyTo;
import org.tolven.trim.DataType;
import org.tolven.trim.ED;
import org.tolven.trim.EN;
import org.tolven.trim.ENSlot;
import org.tolven.trim.Entity;
import org.tolven.trim.Field;
import org.tolven.trim.GTSSlot;
import org.tolven.trim.II;
import org.tolven.trim.IISlot;
import org.tolven.trim.INT;
import org.tolven.trim.NullFlavor;
import org.tolven.trim.NullFlavorType;
import org.tolven.trim.ObjectFactory;
import org.tolven.trim.Observation;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.PQ;
import org.tolven.trim.ParticipationType;
import org.tolven.trim.Party;
import org.tolven.trim.REAL;
import org.tolven.trim.Role;
import org.tolven.trim.RoleClass;
import org.tolven.trim.RoleParticipation;
import org.tolven.trim.SETCDSlot;
import org.tolven.trim.SETCESlot;
import org.tolven.trim.SETIISlot;
import org.tolven.trim.ST;
import org.tolven.trim.TEL;
import org.tolven.trim.TELSlot;
import org.tolven.trim.TS;
import org.tolven.trim.TSSlot;
import org.tolven.trim.TelecommunicationAddressUse;
import org.tolven.trim.TolvenId;
import org.tolven.trim.Transitions;
import org.tolven.trim.Trim;
import org.tolven.trim.ValueSet;

@XmlRegistry
public class TrimFactory extends ObjectFactory {

	@Override
	public II createII() {
		return new IIEx();
	}
	
	@Override
	public Transitions createTransitions() {
		return new TransitionsEx();
	}
	
	@Override
	public ValueSet createValueSet() {
		return new ValueSetEx();
	}

	public String dataTypeToString( DataType object ) {

		//		if (object instanceof ST ) {
//			ST st = (ST)object;
//			String encoded = encode("ST", 
//									st.getValue());
//			return encoded;
//		}

		if (object instanceof TS ) {
			TS ts = (TS)object;
			String encoded = encode("TS", 
									ts.getValue(), ts.getOriginalText());
			return encoded;
		}

		if (object instanceof ED ) {
			ED ed = (ED)object;
			String encoded = encode("ED", 
									new String(Base64.encodeBase64(ed.getValue())),
									ed.getOriginalText());
			return encoded;
		}

		if (object instanceof INT ) {
			INT i = (INT)object;
			String encoded = encode("INT", 
									Long.toString(i.getValue()), 
									i.getOriginalText());
			return encoded;
		}
		if (object instanceof PQ ) {
			PQ pq = (PQ)object;
			String encoded = encode("PQ", 
									pq.getValue()+"", 
									pq.getUnit(),
									pq.getOriginalText());
			return encoded;
		}

		if (object instanceof REAL ) {
			REAL real = (REAL)object;
			String encoded = encode("REAL", 
									Double.toString(real.getValue()), 
									real.getOriginalText());
			return encoded;
		}

		if (object instanceof NullFlavor ) {
			NullFlavor nullFlavor = (NullFlavor)object;
			String encoded = encode("NULL", 
									nullFlavor.getType().name(),
									nullFlavor.getOriginalText());
			return encoded;
		}
		
		if (object instanceof CE ) {
			CE ce = (CE)object;
			String encoded = encode("CE", 
									ce.getCode(), 
									ce.getCodeSystem(), 
									ce.getCodeSystemName(),
									ce.getCodeSystemVersion(), 
									ce.getDisplayName());
//			TolvenLogger.info( "Encode CE: " + encoded, TrimFactory.class);
			return encoded;
		}
		
		if (object instanceof CD ) {
			CD cd = (CD)object;
			String encoded = encode( "CD", 
									cd.getCode(), 
									cd.getCodeSystem(), 
									cd.getCodeSystemName(),
									cd.getCodeSystemVersion(), 
									cd.getDisplayName());
//			TolvenLogger.info( "Encode CE: " + encoded, TrimFactory.class);
			return encoded;
		}
		if (object instanceof AD ) {
			AD ad = (AD)object;
			StringBuffer uses = new StringBuffer(15);
			if (ad.getUses()!=null) {
				for (AddressUse use : ad.getUses()) {
					if (uses.length() > 0 ) uses.append(',');
					uses.append(use.value());
				}
			}
			StringBuffer parts = new StringBuffer(100);
			for (ADXPSlot adxp : ad.getParts()) {
				if (parts.length() > 0) parts.append("^");
				parts.append(encode("ADXP", adxp.getType().value(),adxp.getST().getValue()));
			}
			String encoded = encode("AD", 
									parts.toString(),
									uses.toString(),
									ad.getOriginalText() );
			return encoded;
		}

		if (object instanceof TEL ) {
			TEL t = (TEL)object;
			StringBuffer uses = new StringBuffer(15);
			if (t.getUses()!=null) {
				for (TelecommunicationAddressUse use : t.getUses()) {
					if (uses.length() > 0 ) uses.append(',');
					uses.append(use.value());
				}
			}
			
			String encoded = encode("TEL", 
									t.getValue(),
									uses.toString(),
									t.getOriginalText());
			return encoded;
		}
		return null;

	}
	
	public DataType stringToDataType( String encoded ) {
		String t[] = decode( encoded );
		String dataType = t[0];
		
		if ("AD".equals(dataType)) {
			AD ad = createAD();
			String parts[] = t[1].split("\\^");
			for ( String part : parts ) {
				String partParts[] = decode( part );
				ADXPSlot adxp = createADXPSlot();
				adxp.setType(AddressPartType.fromValue(partParts[1]));
				ST st = createST();
				st.setValue(partParts[2]);
				adxp.setST(st);
				ad.getParts().add(adxp);
			}
			String uses[] = t[2].split(",");
			for (String use : uses) {
				ad.getUses().add(AddressUse.fromValue(use));
			}
			ad.setOriginalText(t[3]);
			return ad;
		}
		
		if ("PQ".equals(dataType)) {
			PQ pq = createPQ();
			if (t[1].length()>0) pq.setValue(Double.parseDouble(t[1]));
			if (t[2].length()>0) pq.setUnit(t[2]);
			if (t[3].length()>0) pq.setOriginalText(t[3]);
			return pq;
		}
		
		if ("INT".equals(dataType)) {
			INT i = createINT();
			if (t[1].length()>0) i.setValue(Long.parseLong(t[1]));
			if (t[2].length()>0) i.setOriginalText(t[2]);
			return i;
		}
		
		if ("REAL".equals(dataType)) {
			REAL real = createREAL();
			if (t[1].length()>0) real.setValue(Double.parseDouble(t[1]));
			if (t[2].length()>0) real.setOriginalText(t[2]);
			return real;
		}

		if ("CE".equals(dataType)) {
			CE ce = createCE();
			if (t[1].length()>0) ce.setCode(t[1]);
			if (t[2].length()>0) ce.setCodeSystem(t[2]);
			if (t[3].length()>0) ce.setCodeSystemName(t[3]);
			if (t[4].length()>0) ce.setCodeSystemVersion(t[4]);
			if (t[5].length()>0) ce.setDisplayName(t[5]);
			return ce;
		}
		
		if ("CD".equals(dataType)) {
			CD cd = createCD();
			if (t[1].length()>0) cd.setCode(t[1]);
			if (t[2].length()>0) cd.setCodeSystem(t[2]);
			if (t[3].length()>0) cd.setCodeSystemName(t[3]);
			if (t[4].length()>0) cd.setCodeSystemVersion(t[4]);
			if (t[5].length()>0) cd.setDisplayName(t[5]);
			return cd;
		}
		
		if ("NULL".equals(dataType)) {
			NullFlavor nf = createNullFlavor();
			if (t[1].length()>0) nf.setType(NullFlavorType.fromValue(t[1]));
			if (t[2].length()>0) nf.setOriginalText(t[2]);
			return nf;
		}
		
//		if ("ST".equals(dataType)) {
//			ST st = createST();
//			if (t[1].length()>0) st.setValue(t[1]);
//			return st;
//		}

		if ("TS".equals(dataType)) {
			TS ts = createTS();
			if (t[1].length()>0) ts.setValue(t[1]);
			if (t[2].length()>0) ts.setOriginalText(t[2]);
			return ts;
		}

		if ("ED".equals(dataType)) {
			ED ed = createED();
			if (t[1].length()>0) ed.setValue(Base64.decodeBase64(t[1].getBytes()));
			if (t[2].length()>0) ed.setOriginalText(t[2]);
			return ed;
		}
		return null;

	}
	/**
	 * Return a String array after parsing the encoded string 
	 * @return
	 */
	static String[] decode( String encoded ) {
		List<String> rslt = new ArrayList<String>(20);
		StringBuffer sb = new StringBuffer(500);
		boolean escape = false;
		for (int x=0; x < encoded.length();x++) {
			char c = encoded.charAt(x);
			// If escaped, we let this one through without checking for delimiters.
			if (escape) {
				escape = false;
				sb.append(c);
				continue;
			}
			// If this is an escape char, next char goes through without comparison
			if  (c=='\\') {
				escape = true;
				continue;
			}
			if (c=='|') {
				rslt.add(sb.toString());
				sb = new StringBuffer(500);
				continue;
			}
			sb.append(c);
		}
		if (sb.length()>0) {
			rslt.add(sb.toString());
		} else {
			rslt.add("");
		}
		return rslt.toArray(new String[rslt.size()]);
	}
	
	/**
	 * Encode a datatype as a string
	 * @param datatype
	 * @param value
	 * @return
	 */
	static String encode( String datatype, String ... value  ) {
		StringBuffer sb = new StringBuffer( 100 );
		sb.append(datatype);
		for (int x = 0; x < value.length; x++) {
			sb.append("|");
			if (value[x]!=null) {
				 for (int c = 0; c < value[x].length(); c++ ) {
					 if ('|'==value[x].charAt(c)
					 || '\\'==value[x].charAt(c)) {
						 sb.append('\\');
					 }
					 sb.append(value[x].charAt(c));
				 }
			}
		}
		return sb.toString();
	}
	
	@Override
	public Application createApplication() {
		return new ApplicationEx();
	}

	@Override
	public PQ createPQ() {
		return new PQEx();
	}
	
	@Override
	public Field createField() {
		return new FieldEx();
	}

	@Override
	public NullFlavor createNullFlavor() {
		return new NullflavorEx();
	}

	@Override
	public ObservationValueSlot createObservationValueSlot() {
		return new ObservationValueSlotEx();
	}

	@Override
	public Compute createCompute() {
		return new ComputeEx();
	}

	@Override
	public ActRelationship createActRelationship() {
    	ActRelationship ar = new ActRelationshipEx();
    	return ar;
	}

	@Override
	public ActParticipation createActParticipation() {
		return new ActParticipationEx();
	}

	@Override
	public RoleParticipation createRoleParticipation() {
		return new RoleParticipationEx();
	}

	@Override
	public TolvenId createTolvenId() {
		return new TolvenIdEx();
	}

	@Override
	public CESlot createCESlot() {
		return new CESlotEx();
	}

	@Override
	public AD createAD() {
		return new ADEx();
	}

	@Override
	public ADSlot createADSlot() {
		return new ADSlotEx();
	}

	@Override
	public ENSlot createENSlot() {
		// TODO Auto-generated method stub
		return new ENSlotEx();
	}

	/**
     * Create an instance of {@link CE }
     * 
     */
    @Override
    public CE createCE() {
        return new CEEx();
    }

	/**
     * Create an instance of {@link CD }
     * 
     */
    @Override
    public CD createCD() {
        return new CDEx();
    }

    @Override
	public CopyTo createCopyTo() {
		return new CopyToEx();
	}

	/**
     * Create an instance of {@link ED }
     * 
     */
    @Override
    public ED createED() {
        return new EDEx();
    }

	/**
     * Create a Tolven-specific instance of {@link Observation }
     * 
     */
	@Override
	public Observation createObservation() {
		return new ObservationEx();
	}

	/**
     * Create an instance of {@link CE }
     * 
     */
    @Override
    public CV createCV() {
        return new CVEx();
    }

	public Act createNewAct( ActClass classCode, ActMood moodCode) {
		Act act = createAct();
		act.setClassCode(classCode);
		act.setMoodCode(moodCode);
		return act;
	}
	/**
	 * A convenience method to create an ST with the passed-in string as the value
	 * @param value
	 * @return
	 */
	public ST createNewST( String value ) {
		ST st = createST();
		st.setValue(value);
		return st;
	}

	@Override
	public GTSSlot createGTSSlot() {
		return new GTSSlotEx();
	}

	/**
	 * A convenience method to create an INT with the passed-in long as the value
	 * @param INT
	 * @return
	 */
	public INT createNewINT( long value ) {
		INT i = createINT();
		i.setValue(value);
		return i;
	}

	@Override
	public TS createTS() {
		return new TSEx();
	}

	public TS createNewTS( Date time ) {
		TSEx ts = (TSEx) createTS();
		ts.setDate(time);
		return ts;
	}

	public void setCodeAsCD( Act act, String code, String codeSystem) {
		CD cd = createCD();
		cd.setCode(code);
		cd.setCodeSystem(codeSystem);
		if (null==act.getCode()) act.setCode(createCDSlot());
		act.getCode().setCE(null);
		act.getCode().setFlavor(null);
		act.getCode().setCD(cd);
	}

	public void setCodeAsCE( Act act, String code, String codeSystem) {
		CE ce = createCE();
		ce.setCode(code);
		ce.setCodeSystem(codeSystem);
		if (null==act.getCode()) act.setCode(createCDSlot());
		act.getCode().setCD(null);
		act.getCode().setFlavor(null);
		act.getCode().setCE(ce);
	}

	public void setCodeAsNull( Act act, NullFlavor nullFlavor) {
		if (null==act.getCode()) act.setCode(createCDSlot());
		act.getCode().setCD(null);
		act.getCode().setFlavor(nullFlavor);
		act.getCode().setCE(null);
	}

	public Role createRole( RoleClass roleClass ) {
		Role role = createRole();
		role.setClassCode(roleClass);
		return role;
	}

	@Override
	public Role createRole() {
		return new RoleEx();
	}

	public II createNewII(String root, String extension ) {
		II ii = createII();
		ii.setRoot(root);
		ii.setExtension(extension);
		return ii;
	}

	public IISlot createNewIISlot(String root, String extension ) {
		II ii = createNewII(root, extension);
		IISlot iiSlot = createIISlot();
		iiSlot.setII(ii);
		return iiSlot;
	}


	public ActParticipation createActParticipation( ParticipationType type, Role role ) {
		ActParticipation participation = createActParticipation();
		participation.setTypeCode(type);
		participation.setRole(role);
		return participation;
	}
	
	public void addParticipation( Act act, ActParticipation participation ) {
		act.getParticipations().add(participation);
	}

	public void addActRelationship( Act act, ActRelationship ar ) {
		act.getRelationships().add(ar);
	}

	@Override
	public Act createAct() {
		// TODO Auto-generated method stub
		return new ActEx();
	}

	@Override
	public DataType createDataType() {
		return new DataTypeEx();
	}

	@Override
	public Entity createEntity() {
		return new EntityEx();
	}

	@Override
	public ST createST() {
		return new STEx();
	}

	@Override
	public TEL createTEL() {
		// TODO Auto-generated method stub
		return new TELEx();
	}

	@Override
	public TELSlot createTELSlot() {
		return new TELSlotEx();
	}

	@Override
	public EN createEN() {
		return new ENEx();
	}

	@Override
	public Trim createTrim() {
		// TODO Auto-generated method stub
		return new TrimEx();
	}

	@Override
	public TSSlot createTSSlot() {
		return new TSSlotEx();
	}

	@Override
	public CDSlot createCDSlot() {
		return new CDSlotEx();
	}

	@Override
	public SETCDSlot createSETCDSlot() {
		return new SETCDSlotEx();
	}

	@Override
	public SETIISlot createSETIISlot() {
		return new SETIISlotEx();
	}
	
	@Override
	public SETCESlot createSETCESlot() {
		return new SETCESlotEx();
	}
	
	public static PartyEx createParty(String[] args){
		return new PartyEx(args);
	}	
}
