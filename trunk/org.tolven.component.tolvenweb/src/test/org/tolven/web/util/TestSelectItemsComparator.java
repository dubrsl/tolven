package test.org.tolven.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import org.tolven.web.util.SelectItemsComparator;

import junit.framework.TestCase;

public class TestSelectItemsComparator extends TestCase {

	public void testComparator(){
		SelectItem item1 = new SelectItem("America","America");
		SelectItem item2 = new SelectItem("India","India");
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(item2);
		items.add(item1);
		Collections.sort(items,new SelectItemsComparator());
		assertEquals(items.get(0).getLabel(), "America");
		assertNotSame(items.get(1).getLabel(), "America");
	}
}
