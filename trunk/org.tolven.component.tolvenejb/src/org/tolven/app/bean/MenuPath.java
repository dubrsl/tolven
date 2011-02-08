/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.app.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * <p>A MenuPath object parses and manipulates a single path element. The element can either be for
 * metadata: the "path" of a MenuStructure item, or for instance data: the path of a MenuData item.</p>
 * <p>A metadata path has no identifiers.</p>
 * <pre>echr:patient</pre>
 * <p>A MenuData (instance) item typically does have identifiers.</p>
 * <pre>echr:patient-123</pre>
 * <p>The decision is left to the caller as to how any particular MenuPath is used.</p> 
 * <p>Each of the identifiers embedded in a MenuPath item is always a positive long integer (2**64-1) and always corresponds 
 * to the surrogate key of a menuData item (in the app.menu_data table). When a path contains multiple ids, such as for a lab result:</p> 
 * <pre>echr:patient-123:result-456</pre>
 * <p>Each of the ids can be used independently. In the example, the id 456 is the result and will independently have a parent pointing
 * to the patient 123. So, the ids are simply denormalized (duplicated) into the key to improve query time and to reduce the number of round trips
 * needed in the browser. For example, if the showPane javascipt method were called with the result example above, the patient 
 * can also be determined and if not already loaded to the browser, the node can be used to query the patient, without having to
 * wait for the return of the result.</p> 
 * @author John Churin
 *
 */
public class MenuPath implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String pathString;
    // Nodes extracted from the requested element
    transient private String nodes[] = null;
    // Key (or zero) for any node specifying an id
    transient private long nodeKeys[] = null;

    transient private String label;

	public MenuPath() {
		super();
	}

	/**
	 * Create a new menu path with the specified path
	 * @param path A path such as "echr:pat-123:results:lab"
	 */
	public MenuPath(String path) {
		super();
		if (path==null) throw new IllegalArgumentException( "MenuPath cannot be null");
		this.pathString = path;
	}
	
	/**
	 * Create a new path using a supplied set of values. The first path is a template of the desired path
	 * and the srcPath is an existing element from which instance values can be harvested to 
	 * populate the resulting path. For example, 
	 * "echr:pat-123:doc" is the result of  
	 * <pre>
	 * MenuPath("echr:pat:doc", "echr:pat-123:results:lab");
	 * </pre>
	 */
	public MenuPath(String path, MenuPath srcPath) {
		if (srcPath != null && srcPath.getPathString()!=null) {
	    	Map<String, Long> nodeValues = srcPath.getNodeValues();
			String nodes[] = path.split("\\:");
			StringBuffer newPath = new StringBuffer( 100 ); 
			for (int x = 0;x < nodes.length; x++) {
				if (newPath.length()>0) newPath.append(":");
				newPath.append(nodes[x]);
				// A match and the value needs to be non-zero, then we copy it
				if (nodeValues.containsKey(nodes[x]) && nodeValues.get(nodes[x]).longValue()!=0) {
					newPath.append("-");
					newPath.append(nodeValues.get(nodes[x]).longValue());
				}
			}
			this.pathString = newPath.toString();
		} else {
			this.pathString = path;
		}
	}

	/**
	 * Return a unique but shorter version of this path string without special characters.
	 * @return
	 */
	public String getLabel() {
		if (label==null) {
			StringBuffer sb = new StringBuffer( pathString.length() );
			for ( char c : pathString.toCharArray()) {
				if (Character.isDigit(c) || Character.isLetter(c)) {
					sb.append(c);
				}
			}
		label= sb.toString();
		}
		return label;
	}
	
	/**
	 * Parse an element id such as <pre>echr:patient-123:results:lab:act-767</pre>. The element is acquired from the requestParameter <italic>element</italic>. An array with each node is created
	 * and when a key value is present, it is parsed and stored in a parallel array of key values. Any node without a value
	 * gets a keyValue of zero. (All keysValues are assumed to be longs, appropriate for surrogate primary key values.
	 * This funtion can be called many times but will only parse once.
	 */
	public void initNodes( ) {
		if (nodes!=null) return;
		String element = pathString;
        if (element!=null) {
			nodes = element.split("\\:");
			nodeKeys = new long[nodes.length];
			for (int x = 0;x < nodes.length; x++) {
				int dash = nodes[x].indexOf("-");
				if (dash < 0) nodeKeys[x] = 0;
				else {
					nodeKeys[x] = Long.parseLong(nodes[x].substring(dash+1));
					nodes[x] = nodes[x].substring( 0, dash );
				}
			}
        }
	}

	/**
	 * Get the node for this element in the menu which is the final node in the path
	 * @return The last node name in the path
	 */
	public String getNode( ) {
		initNodes();
		return nodes[nodes.length-1];
	}
	
	/**
	 * Return the list of nodes in the path (contains only the node names, not
	 * values.
	 * @return
	 */
	public String[] getNodes() {
		initNodes();
		return nodes;
	}
	/**
	 * Get the specified node for this element. An error will be thrown if the index is
	 * beyond the number of nodes in the path.
	 * @return The last node name in the path
	 */
	public String getNode( int index) {
		initNodes();
		return nodes[index];
	}


	/**
	 * Return the node values as a map
	 * @return
	 */
	public Map<String, Long> getNodeValues() {
		this.initNodes();
		Map<String, Long> values = new HashMap<String, Long>( 10 );
		for (int x = 0; x < nodes.length; x++ ) {
			values.put( nodes[x], new Long(nodeKeys[x]) );
		}
		return values;
	}

	/**
	 *  Get path string. Object ids, if any, are not included in the path but are available in a parallel array.
	 * This is used to find MenuStructure nodes (not MenuData)/
	 */
	public String getPath( ) {
		initNodes();
		StringBuffer sb = new StringBuffer( 100 );
		for (int x = 0; x < nodes.length; x++ ) {
			if (sb.length()>0) sb.append(":");
			sb.append( nodes[x] );
		}
		return sb.toString();
	}
	
	/**
	 * Construct a path to a sub node. The node name of the desired leaf node is specified.
	 * For example, if the full path says: "echr:patient-123:results:lab" and the requested node
	 * is "patient", then "echr:patient" is returned (object Ids omitted).
	 * This method is used to find MenuStructure nodes (not MenuData).
	 * Null is returned if the named node is not found.
	 */
	public String getSubPath(String node) {
		initNodes();
		StringBuffer sb = new StringBuffer( 100 );
		for (int x = 0; x < nodes.length; x++ ) {
			if (sb.length()>0) sb.append(":");
			sb.append( nodes[x] );
			if (node.equals(nodes[x])) return sb.toString(); 
		}
		return null;
	}
	/**
	 * Return the path to the owner of this path which is the previous placeholder node, if any.
	 * For example, the owner path of "echr:patient-123:allergy-456" is "echr:patient-123" 
	 * @return owner path or null if no owner.
	 */
	public String getOwnerPath() {
		initNodes();
		StringBuffer sb = new StringBuffer( 100 );
		int end = nodes.length-1;
		// Reduce the end until we find a placeholder (non-zero id)
		while (end > 0 && nodeKeys[end]==0) {
			end--;
		}
		if (end==0) return null;
		for (int x=0; x <= end; x++ ) {
			if (sb.length()>0) sb.append(":");
			sb.append( nodes[x] );
			if (nodeKeys[x]!=0) {
				sb.append( "-" );
				sb.append( nodeKeys[x] );
			}
		}
		return sb.toString();
	}
	
	/**
	 * Construct a path to a sub node with Ids. The node name of the desired leaf node is specified.
	 * For example, if the full path says: "echr:patient-123:results:lab" and the requested node
	 * is "patient", then "echr:patient-123" is returned (object Ids omitted).
	 * This method is used to find MenuStructure nodes (not MenuData).
	 * Null is returned if the named node is not found.
	 */
	public String getSubPathWithIds(String node) {
		initNodes();
		StringBuffer sb = new StringBuffer( 100 );
		for (int x = 0; x < nodes.length; x++ ) {
			if (sb.length()>0) sb.append(":");
			sb.append( nodes[x] );
			if (nodeKeys[x]!=0) {
				sb.append("-");
				sb.append(nodeKeys[x]);
			}
			if (node.equals(nodes[x])) return sb.toString(); 
		}
		return null;
	}
	/**
	 *  Get path string to the parent of the requested node
	 */
	public String getParentPath( ) {
		initNodes();
		if (nodes.length == 0) return "";
		StringBuffer sb = new StringBuffer( 100 );
		for (int x = 0; x < nodes.length-1; x++ ) {
			if (sb.length()>0) sb.append(":");
			sb.append( nodes[x] );
		}
		return sb.toString();
	}

	public long[] getNodeKeys() {
		this.initNodes();
		return nodeKeys;
	}
	
	public void setNodeKeys(long[] nodeKeys) {
		this.nodeKeys = nodeKeys;
	}


	public String getPathString() {
		return pathString;
	}

	public void setPathString(String pathString) {
		this.pathString = pathString;
	}
	/**
	 * Get the key value of the most specific node, the one on the right.
	 * For example, 
	 * in "echr:patient-123:lab:result-456"
	 * return 456.
	 * @return
	 */
	public long getLeafNodeKey( ) {
		initNodes();
		return nodeKeys[nodes.length-1];
	}

	public long [] getSignificantNodeKeys( ) {
		initNodes();
		int snkCount = 0;
		for ( int x = 0; x < nodeKeys.length; x++) {
				if (nodeKeys[x]!=0)	snkCount++;
		}
		long sigNodeKeys[] = new long[ snkCount ];
		int z = 0;
		for ( int y = 0; y < nodeKeys.length; y++) {
			if (nodeKeys[y]!=0) {
				sigNodeKeys[z++] = nodeKeys[y];
			}
		}
		return sigNodeKeys;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer( 200 );
		sb.append( "path: "); sb.append(getPath());
		sb.append(" Values: ");sb.append(getNodeValues());
		return sb.toString();
	}

}
