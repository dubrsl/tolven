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
package org.tolven.gen;

import java.io.IOException;
import java.util.List;

import org.tolven.gen.entity.FamilyMember;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.gen.entity.VirtualPerson;

/**
 * Services to generate people and related attributes.
 * @author John Churin
 */
public interface PersonGenerator {
    /**
     * Create a simple person object with name, gender, and dob.
     * Do not persist the object.
     * 
     * @return a new VirtualPerson
     * @throws Exception
     */
    VirtualPerson generatePerson() throws Exception;
    
    /**
     * Generate people and add persist them. No mention of which people are generated is returned.
     * @throws Exception 
     */
    public void generatePeople( int count ) throws Exception;
    
    /**
     * Add address information to a family
     * @param family
     * @throws IOException
     */
    public void generateAddress( FamilyUnit family) throws IOException;
    	     
	/**
     * Return a count of the number of virtual persons are in the database.
     */
    public long countPeople( );
    /**
     * Return a list of VirtualPerson objects meeting the query criteria provided
     * @param limit Maximum number fo rows to return
     * @param offset Zero-based offset of the first row to return from the result set
     * @param sortOrder the name of the attribute to order by (first, last, dob, dod, gender)
     * @param sortDirection the sort direction (ASC or DESC)
     * @param nameFilter Filter on last name comma first if contents is non-null and non-blank
     * @return
     */
    public List<VirtualPerson>findPersons( int limit, int offset, String sortOrder, String sortDirection, String nameFilter );

    /**
     * Return a list of Virtual FamilyUnit objects meeting the query criteria provided
     * @param limit Maximum number fo rows to return
     * @param offset Zero-based offset of the first row to return from the result set
     * @param sortOrder the name of the attribute to order by (first, last, dob, dod, gender)
     * @param sortDirection the sort direction (ASC or DESC)
     * @return
     */
    public List<FamilyUnit>findFamilies( int limit, int offset, String sortOrder, String sortDirection, String nameFilter );

    public FamilyUnit findFamily( long id );

    public List<FamilyMember>findFamilyMembers( long id );
    /**
     * Return a count of the number of virtual families are in the database.
     */
    public long countFamily( );

}
