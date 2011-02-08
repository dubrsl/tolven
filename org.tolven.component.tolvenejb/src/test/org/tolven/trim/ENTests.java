package test.org.tolven.trim;

import java.util.List;

import junit.framework.TestCase;

import org.tolven.trim.EN;
import org.tolven.trim.ENSlot;
import org.tolven.trim.ENXPSlot;
import org.tolven.trim.EntityNamePartType;
import org.tolven.trim.EntityNameUse;
import org.tolven.trim.ex.TrimFactory;

public class ENTests extends TestCase {
	private static final TrimFactory factory = new TrimFactory( );

	public static final String FIRST = "My First Name";
	public static final String LAST = "My Last Name";
	
	public void testEN1( ) {
		ENSlot enslot = factory.createENSlot();
		EN en = factory.createEN();
		en.getUses().add(EntityNameUse.L);
		List<ENXPSlot> parts = en.getParts();
		ENXPSlot first = factory.createENXPSlot();
		first.setST(factory.createNewST(FIRST));
		first.setType(EntityNamePartType.GIV);
		parts.add(first);
		ENXPSlot last = factory.createENXPSlot();
		last.setST(factory.createNewST(LAST));
		last.setType(EntityNamePartType.FAM);
		parts.add(last);
		enslot.getENS().add(en);
	}
}
