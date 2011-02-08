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
package org.tolven.gen.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.gen.PersonGenerator;
import org.tolven.gen.PersonGeneratorRemote;
import org.tolven.gen.entity.FamilyMember;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.gen.entity.VirtualPerson;
import org.tolven.gen.entity.FamilyMember.FamilyMemberRole;
import org.tolven.gen.util.GenerateCauseOfDeath;
import org.tolven.gen.util.GeneratePlace;
import org.tolven.gen.util.LifeSpan;
import org.tolven.gen.util.RandomNameGenerator;

/**
 * The bulk of this class is now obsolete. The most important method of this class is
 * generateFamily which is used by the data generator. There's really no need to keep a
 * persistent list of people or families any longer.
 * @author John Churin
 */
//@DeclareRoles({"gen","provider"})
@Stateless
@Local(PersonGenerator.class)
@Remote(PersonGeneratorRemote.class)
public class PersonGeneratorDAO implements PersonGenerator {

    	@PersistenceContext
	private EntityManager em;

    	private RandomNameGenerator nameGenerator;
    	private LifeSpan lifeSpan;
    	private GeneratePlace placeGenerator;
    	private GenerateCauseOfDeath grimReaper;
        private Random rng;
    	
    /** Creates a new instance of PersonGenerator */
    public PersonGeneratorDAO() {
    	nameGenerator = new RandomNameGenerator();
    	lifeSpan = new LifeSpan();
    	placeGenerator = new GeneratePlace();
    	grimReaper = new  GenerateCauseOfDeath();
        rng = new Random();
    	
    }
    /**
     * Given a mother, find a suitable father in the list of available father, if any.
     */
    public static VirtualPerson findSuitableFather( Date now, VirtualPerson mother, ArrayList<VirtualPerson> fathers ) {
    	for (VirtualPerson father : fathers ) {
    		int ageDifference = father.getAgeInYears( now )-mother.getAgeInYears( now );
    		if (Math.abs( ageDifference) < 10 ) {
    			fathers.remove( father );
    			return father;
    		}
    	}
    	// No suitable father available.
    	return null;
    }
    /**
     * Given a mother, and maybe a father, find a suitable child in the list of available kids, if any.
     */
    public static VirtualPerson findSuitableChild( Date now, VirtualPerson mother, VirtualPerson father, ArrayList<VirtualPerson> kids ) {
    	for (VirtualPerson child : kids ) {
    		boolean fatherOK = (father==null || father.getAgeInYears(now) > (child.getAgeInYears( now) + 6));
    		boolean motherOK = (mother==null || mother.getAgeInYears(now) > (child.getAgeInYears( now) + 6));
    		if ( fatherOK && motherOK) { 
    			kids.remove( child );
    			return child;
    		}
    	}
    	// No suitable child available.
    	return null;
    }

    /**
     * Add address information to a family
     * @param family
     * @throws IOException
     */
    public void generateAddress( FamilyUnit family) throws IOException {
        placeGenerator.generateZipCode(family);
        placeGenerator.generateAddress(family);
    }
    
    /**
     * Generate people and persist them. No mention of which people are generated is returned.
     * Also generate family units for these new people.
     * We assume an average of 2.0 children per family with mother and father.
     * We'll try to make all children part of some family and that all children in a family are younger than bother parents. 
     * But not all adults will be part of a family. And not all families will have a father.
     * Statistics: 75 percent of child-bearing adults, 18-55 are part of a family. TODO: Needs verification.
     * Families will have zero to four children (TODO: flat average, no distribution)
     * 
     * @throws Exception 
     */
    public void generatePeople( int count ) throws Exception {
    	Date now = new Date();
    	ArrayList<VirtualPerson> mothers = new ArrayList<VirtualPerson>( count/2 ); 
    	ArrayList<VirtualPerson> fathers = new ArrayList<VirtualPerson>( count/2 ); 
    	ArrayList<VirtualPerson> kids = new ArrayList<VirtualPerson>( count ); 
    	ArrayList<VirtualPerson> others = new ArrayList<VirtualPerson>( count ); 

    	// Generate a bunch of people in proportion to US demographics
    	for (int c = 0; c < count; c++) {
    		VirtualPerson person = generatePerson();
        	em.persist( person );
    		int age = person.getAgeInYears( now );
    		if ( age > 18 && age < 55 ) {
    			if (rng.nextInt( 100 ) < 75) {
    				if ("M".equals(person.getGender())) {
    					fathers.add( person );
    				} else mothers.add( person );
    			} else others.add( person );
    		} else if (age <= 18) {
    			kids.add( person );
    		} else others.add( person );
    	}
    	// Now run back through the list to create family units
    	for (VirtualPerson mother : mothers ) {
    		FamilyUnit family = new FamilyUnit();
            placeGenerator.generateZipCode(family);
            placeGenerator.generateAddress(family);
    		em.persist( family );
    		em.persist( family.addMember( mother, FamilyMember.FamilyMemberRole.MOTHER ));
    		family.setFamilyName( mother.getLast() ); // Temporary if/until we get a father
    		// Pick off an available dad
    		VirtualPerson father = findSuitableFather( now, mother, fathers);
    		if (father!=null) {
        		em.persist(family.addMember( father, FamilyMember.FamilyMemberRole.FATHER ));
        		// And in this case, mom's name changes to dad's (just imply marriage)
        		mother.setLast( father.getLast() );
        		family.setFamilyName( father.getLast() );
    		}
    		// And add some random number of kids (TODO: This has to be much fancier)
    		for (int k = 0; k < rng.nextInt(4); k++) {
	    		VirtualPerson child = findSuitableChild( now, mother, father, kids);
	    		if (child == null) break;	// No need to continue if there are no more kids available.
    			child.setLast( mother.getLast());
    			child.setFather( father ); // May be null
    			child.setMother( mother );
        		em.persist(family.addMember( child, FamilyMember.FamilyMemberRole.CHILD ));
    		}
    	}
    	// Finally, anyone left over is not part of a family.
    }

    /**
     * Return a count of the number of virtual persons are in the database.
     */
    public long countPeople( ) {
        Query query = em.createQuery( "SELECT COUNT(c) FROM VirtualPerson c");
        Long rslt = (Long) query.getSingleResult();
        return rslt;
    }

    /**
     * Return a count of the number of virtual families are in the database.
     */
    public long countFamily( ) {
        Query query = em.createQuery( "SELECT COUNT(f) FROM FamilyUnit f");
        Long rslt = (Long) query.getSingleResult();
        return rslt;
    }
    
    /**
     * Create a simple person object with name, gender, and dob
     * Do not persist the object (yet).
     * @return A new VirtualPerson object
     * @throws Exception
     */
    public VirtualPerson generatePerson( ) throws Exception
    {
//        if (!ctx.isCallerInRole("gen")) { 
//            throw new SecurityException( "User [" + ctx.getCallerPrincipal() + "] must be in gen role to call GeneratePerson");
//        }
        // A virtual person object
        VirtualPerson vPerson = new VirtualPerson();
        nameGenerator.generateNewName(vPerson);
        grimReaper.generateCauseOfDeath( vPerson );
        lifeSpan.setLifeSpan( vPerson );
        
        return vPerson;
    }

    /**
     * @see PersonGenerator
     */
    public List<VirtualPerson>findPersons( int limit, int offset, String sortOrder, String sortDirection, String nameFilter ) {
    	StringBuffer queryString = new StringBuffer(500);
    	queryString.append("SELECT vp FROM VirtualPerson vp ");
    	String filterFirst = "";
        String filterLast = ""; 
        if (nameFilter!=null) {
	        String nameParts[] = nameFilter.split("\\,", 2);
	    	if (nameParts.length > 0) {
	    		filterLast = nameParts[0].trim().toLowerCase();
	    	}
	    	if (nameParts.length > 1) {
	    		filterFirst = nameParts[1].trim().toLowerCase();
	    	}
	    	if (filterLast.length() > 0 || filterFirst.length() > 0) {
	    		queryString.append( "where " );
	    	}
	    	if (filterLast.length() > 0) {
	    		queryString.append( "LOWER(vp.last) LIKE :filterLast ");
	    		if (filterFirst.length() > 0) queryString.append("and ");
	    	}
	    	if (filterFirst.length() > 0) {
	    		queryString.append( "LOWER(vp.first) LIKE :filterFirst ");
	    	}
        }
    	queryString.append( "ORDER BY vp." );
    	queryString.append( sortOrder );
    	queryString.append( " " );
    	queryString.append( sortDirection);
    	Query query = em.createQuery( queryString.toString() );
		query.setMaxResults(limit);
		query.setFirstResult(offset);
    	if (filterLast.length() > 0) {
    		query.setParameter("filterLast", filterLast+"%");
    	}
    	if (filterFirst.length() > 0) {
    		query.setParameter("filterFirst", filterFirst+"%");
    	}
		List<VirtualPerson> items = query.getResultList();
		return items;
   	}

    /**
     * Return a list of Virtual FamilyUnit objects meeting the query criteria provided
     * @param limit Maximum number fo rows to return
     * @param offset Zero-based offset of the first row to return from the result set
     * @param sortOrder the name of the attribute to order by (first, last, dob, dod, gender)
     * @param sortDirection the sort direction (ASC or DESC)
     * @return
     */
    public List<FamilyUnit>findFamilies( int limit, int offset, String sortOrder, String sortDirection, String nameFilter ) {
    	StringBuffer queryString = new StringBuffer(500);
    	queryString.append( "SELECT fu FROM FamilyUnit fu " );
        String filterLast = ""; 
    	if (nameFilter!=null) {
	    		filterLast = nameFilter.trim().toLowerCase();
    	}
    	if (filterLast.length() > 0) {
    		queryString.append( "where LOWER(fu.familyName) LIKE :filterLast ");
    	}
    	queryString.append( "ORDER BY fu." );
    	queryString.append( sortOrder );
    	queryString.append( " " );
    	queryString.append( sortDirection);
    	Query query = em.createQuery( queryString.toString() );
    	if (filterLast.length() > 0) {
    		query.setParameter("filterLast", filterLast+"%");
    	}
		query.setMaxResults(limit);
		query.setFirstResult(offset);
		List<FamilyUnit> items = query.getResultList();
		return items;
   	}

    public FamilyUnit findFamily( long id ) {
    	return em.find( FamilyUnit.class, id );
    }

   	public List<FamilyMember>findFamilyMembers( long id ) {
        Query query = em.createQuery( "SELECT fm FROM FamilyMember fm WHERE fm.familyUnit.id = :id ");
		query.setParameter( "id", id );
		List<FamilyMember> items = query.getResultList();
		return items;
    }

}
