package org.tolven.app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Hold query results that can be easily returned over a remote link. This object is returned by the
 * findMenuDataByColumns query.
 * @author John Churin
 */
public class MDQueryResults implements Serializable {
	List<Map<String, Object>> results;
	List<MSColumn> mdSortedColumns;

	MenuQueryControl ctrl;
	
	public MDQueryResults(MenuQueryControl ctrl) {
		this.ctrl = ctrl;
		results = new ArrayList<Map<String, Object>>();
	}
	
	public void addRow( Map<String, Object> rowMap ) {
		results.add(rowMap);
	}
	
	public List<Map<String, Object>> getResults() {
		return results;
	}
	
	public int getRowCount() {
		return results.size();
	}

	public MenuQueryControl getCtrl() {
		return ctrl;
	}

	public void setCtrl(MenuQueryControl ctrl) {
		this.ctrl = ctrl;
	}

	public List<MSColumn> getSortedColumns() {
		if (mdSortedColumns==null) {
			mdSortedColumns = getCtrl().getSortedColumns();
		}
		return mdSortedColumns;
	}
}
