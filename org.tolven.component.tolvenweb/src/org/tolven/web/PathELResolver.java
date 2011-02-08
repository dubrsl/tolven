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
package org.tolven.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;

import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;


public class PathELResolver extends javax.el.ELResolver {

	public PathELResolver() {
		super();
	}
	/**
	 * Transient object used to hold EL evaluation results. Ultimately, this object will construct
	 * a full path object to the selected MenuStructure using the values specificed in the menuData item.
	 * @author John Churin
	 *
	 */
	public class MenuStructurePath {
		MenuData md = null;
		List<MenuStructure> msList = null;
		MenuStructure root = null;
		/**
		 * Construct a new MenuStructurePath relating to the MenuData object supplied
		 * @param md
		 */
		public MenuStructurePath(MenuData md ) {
			this.md = md;
			
			msList = new ArrayList<MenuStructure>( 10 );
			// Find our root
			root = md.getMenuStructure().getRoot();
		}
		
		public void addNode( String node ) {
			MenuStructure ms;
			// Verify that the first node is the root
			if (msList.size()==0 ) {
				if (!root.getNode().equals( node ))	throw new PropertyNotFoundException ( "First node in path (" + node + ") is not the root of the MenuStructure (" + root.getNode() + ")" );
				ms = root;
			} else {
				ms = msList.get(msList.size()-1).findChild( node );
				if ( ms==null ) throw new PropertyNotFoundException ( "Child node " + node + " not found in MenuStructure (" + msList.get(msList.size()-1).getPath() + ")" );
			}
			msList.add( ms );
		}
		
		/**
		 * Contruct a path to the specified MenuStructure relating, if approppriate, to the specified MenuData item.
		 * For example: Seeking MenuStructure 
		 * <pre>		echr:patient:results:lab </pre>
		 * might look like this from el:
		 * 	<pre>	#{md.pathTo.echr.patient.results.lab.fullPath} </pre>
		 * If the id of the menuData item we're on is 123, then the resulting path should be:
		 * <pre>		echr:patient-123:results:lab </pre>
		 * Knowing where to place the MD id in this case is simply a matter of finding which MenuStructure
		 * node (or nodes) are placeholders and pulling the corresponding node from the MenuDetail item. 
		 * The Path for the detail item in this case might be:
		 * <pre>		echr:patients:patient-123 </pre>
		 * There is no requirement that the MenuData id actually be used in the target path. this would happen
		 * if the intended target is not directly related to the menu data item. For example, a link on
		 * a patient entry might be "make an appointment" which brings the user to the appointment pane so that
		 * the user can create a new appointment. So the resulting path might be:
		 * <pre>		echr:calendar </pre>
		 * @return
		 */
		public String getFullPath() {
			StringBuffer sb = new StringBuffer( 150 ); 
			// For each node in the target, see if we need an id.
			for ( MenuStructure m : msList ) {
				if( sb.length()>0) sb.append(":");
				sb.append( m.getNode() );
				String rep = m.getRepeating();
				if (rep!=null) {
					sb.append( "-" );
					// Get requtested key id from md and add it to the path
					sb.append(md.getKey( rep ));
				}
			}
			return sb.toString();
		}
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) throws ELException {
		if(context == null) throw new NullPointerException();
        // This is what we're here to resolve
		if ( base instanceof MenuData && property instanceof String && "pathTo".equals( property ) ) {
            context.setPropertyResolved(true);
			return new MenuStructurePath( (MenuData)base );
		}
		// Add property to the path
		if (base instanceof MenuStructurePath) {
			MenuStructurePath msp = (MenuStructurePath)base;
			if ("fullPath".equals( property )) {
	            context.setPropertyResolved(true);
	            return msp.getFullPath();
			} else {
			msp.addNode( (String) property );
            context.setPropertyResolved(true);
			return msp;
			}
		}
		// Nothing we can resolve so let someone else do it.
		return null;
	}

	@Override
	public Class getType(ELContext context, Object base, Object property) throws ELException {
		return null;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) throws ELException {
		
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) throws ELException {
        if(context == null) throw new NullPointerException();
        
		if ( base instanceof MenuData && property instanceof String && "pathTo".equals( property ) ) {
            context.setPropertyResolved(true);
			return true;
		}
        if(base instanceof MenuStructurePath) {
            context.setPropertyResolved(true);
            return true;
        }
		return false;
	}

	@Override
	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	@Override
	public Class getCommonPropertyType(ELContext context, Object base) {
		return null;
	}

}
