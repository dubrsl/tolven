package org.tolven.app.entity;

import javax.persistence.Column;

/*
 * Used to transfer UserMenuStructure data from client/Web to the MenuBean.
 * This contains data fields from UMS.
 */
public class UMSDataTransferObject {
	
    private String visible;

    private Integer sequence;

    private Integer columnNumber;
    
    private String windowstyle;
    
    private Integer numItems;
    
    private String defaultPathSuffix;
    
    private String style;
    
    private Integer timelineOrder;

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(Integer columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getWindowstyle() {
		return windowstyle;
	}

	public void setWindowstyle(String windowstyle) {
		this.windowstyle = windowstyle;
	}

	public Integer getNumItems() {
		return numItems;
	}

	public void setNumItems(Integer numItems) {
		this.numItems = numItems;
	}

	public String getDefaultPathSuffix() {
		return defaultPathSuffix;
	}

	public void setDefaultPathSuffix(String defaultPathSuffix) {
		this.defaultPathSuffix = defaultPathSuffix;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public Integer getTimelineOrder() {
		return timelineOrder;
	}

	public void setTimelineOrder(Integer timelineOrder) {
		this.timelineOrder = timelineOrder;
	}
    
	
}
