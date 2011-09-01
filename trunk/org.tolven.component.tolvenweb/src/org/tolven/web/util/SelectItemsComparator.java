package org.tolven.web.util;

import java.util.Comparator;

import javax.faces.model.SelectItem;

public class SelectItemsComparator implements Comparator<SelectItem> {

	@Override
	public int compare(SelectItem arg0, SelectItem arg1) {
		return arg0.getLabel().compareTo(arg1.getLabel());
	}

}
