package org.tolven.gen;

import java.util.Date;

import org.tolven.gen.entity.FamilyUnit;
/**
 * Remote services to generate a family using the data generator.
 * @author John Churin
 *
 */
public interface FamilyGeneratorRemote {

	 /**
	  * Create a family.
	  * If family name is non-null, then we'll take it as the family name of the family otherwise we'll
	  * use a random name.
	  */
	 public FamilyUnit generateFamily( String familyName,  Date now ) throws Exception;

}
