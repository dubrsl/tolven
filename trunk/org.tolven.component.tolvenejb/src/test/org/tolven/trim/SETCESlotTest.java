package test.org.tolven.trim;

import org.tolven.trim.CE;
import org.tolven.trim.SETCESlot;
import org.tolven.trim.ex.TrimFactory;

import junit.framework.TestCase;

public class SETCESlotTest extends TestCase {
	private static final TrimFactory factory = new TrimFactory( );
	public void testSETCESerialization(){
		try{
			SETCESlot slot = factory.createSETCESlot();
			CE ce0 = new CE();
			ce0.setDisplayName("one");
			CE ce1 = new CE();
			ce1.setDisplayName("two");
			CE ce2 = new CE();
			ce2.setDisplayName("three");
			slot.getCES().add(ce0);
			slot.getCES().add(ce1);
			slot.getCES().add(ce2);
			String xml = factory.toXML(slot);
			System.out.println(xml);
			SETCESlot newSlot = factory.fromXML(xml);
			int i =0;
			for(CE ce:newSlot.getCES()){
				if(i== 0){
					if(!ce.getDisplayName().equalsIgnoreCase("one")){
						fail("one is wrong");
					}
				}else if(i == 1){
					if(!ce.getDisplayName().equalsIgnoreCase("two")){
						fail("two is wrong");
					}
				}else if(i == 2){
					if(!ce.getDisplayName().equalsIgnoreCase("three")){
						fail("three is wrong");
					}
				}
				i++;
			}
		}catch(Exception ex){
			fail(ex.getMessage());
		}
	}
}
