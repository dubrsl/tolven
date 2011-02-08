package org.tolven.core;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.security.PermitAll;
/**
 * Service that manage properties stored in the database and made available to the application
 * @author John Churin
 *
 */
public interface TolvenPropertiesLocal {

	/**
	 * Return the tolven persistent properties (from the database, not the running system
	 * @return Properties collection
	 */
	public Properties findProperties();
	
    /**
     * Delete tolven property from database (not running system)
     * @param name
     */
    public void removeProperty(String propertyName);
	
    /**
     * Set a property in the running system and in the database.
     * @param name Name of the property
     * @param value Value of the property
     */
    public void setProperty(String propertyName, String propertyValue);

    /**
     * Same as doing a System.getProperty but wrapped by our environment.
     * @param name Name of the property
     * @return The value of the property
     */
    public String getProperty(String propertyName);

    /**
     * Return the tolven persistent properties (from the database, not the running system)
     * @param name A list of property names
     * @return Properties of the propertyNames/propertyValues
     */
    public Properties findProperties(List<String> propertyNames);
  
    /**
     * Get a property with an optional brand-specific override
     * @param name The pure property name
     * @param brand The brand, which may be null
     * @return Value of the property
     */
     public String getBrandedProperty(String name, String brand);
     
    /**
     * Return a map of properties for a brand. The returned map is simply a wrapper around the
     * getBrandedProperty method.
     * @param brand The brand, or null
     * @return A Map of properties
     */
    @PermitAll
    public Map<String,String> getBrandedProperties(String brand);
        
    /**
     * Set all of the properties from the database into the
     * running properties of the system. 
     */
    public void setAllProperties( );
    
    /**
     * Reset the runtime properties
     */
    public void resetAllProperties();

    /**
     * Add properties or set the value if the property already exists
     * @param properties
     */
    public void setProperties(Properties properties);

    /**
     * Delete properties 
     * @param properties
     */
    public void removeProperties(List<String> properties);

}
