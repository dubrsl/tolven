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
package org.tolven.core.bean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Property;

/**
 * Manage all properties used by Tolven. 
 * TODO: This should be an MBEAN, not a stateless session bean.
 * This bean is protected at the class level by a role which only allows tolvenAdmin to execute most of its methods.
 * However, the getProperty method does permit all roles since this method is only available on the local interface.
 * 
 * @author John Churin
 *
 */
@Stateless
@Local(TolvenPropertiesLocal.class)
public class TolvenProperties implements TolvenPropertiesLocal {

    @PersistenceContext
    private EntityManager em;
    private boolean initialized = false;

    /**
     * Reset the runtime properties
     */
    @Override
    public void resetAllProperties() {
        initialized = false;
        setAllProperties();
    }

    class PropertyMap implements Map<String, String> {
        AccountUser accountUser;
        String brand;

        public PropertyMap(String brand) {
            this.brand = brand;
        }

        @Override
        public String get(Object key) {
            return getBrandedProperty(key.toString(), brand);
        }

        @Override
        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        /**
         * Put or update a property in this account's list of properties.
         */
        @Override
        public int size() {
            return accountUser.getAccountUserProperties().size();
        }

        /**
         * Put or update a property in this accountUser's list of properties.
         */
        @Override
        public String put(String key, String value) {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public Set<java.util.Map.Entry<String, String>> entrySet() {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public Set<String> keySet() {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public void putAll(Map<? extends String, ? extends String> m) {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public String remove(Object key) {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

        @Override
        public Collection<String> values() {
            throw new UnsupportedOperationException("Property operation not supported in AccountUser");
        }

    }

    /**
     * Return a map of properties for a brand. The returned map is simply a wrapper around the
     * getBrandedProperty method.
     * @param brand The brand, or null
     * @return A Map of properties
     */
    @PermitAll
    public Map<String, String> getBrandedProperties(String brand) {
        return new PropertyMap(brand);
    }

    /**
     * Get a property with an optional brand-specific override
     * @param name The pure property name
     * @param brand The brand, which may be null
     * @return Value of the property
     */
    @PermitAll
    public String getBrandedProperty(String name, String brand) {
        String property = null;
        if (brand != null) {
            property = getProperty(name + "." + brand);
        }
        if (property == null) {
            property = getProperty(name);
        }
        return property;
    }

    /**
     * Same as doing a System.getProperty but wrapped by our environment.
     * This method should not be exposed to a remote interface in the same way that System.getProperty is not.
     * 
     * @param name Name of the property
     * @return The value of the property
     */
    @Override
    @PermitAll
    public String getProperty(String propertyName) {
        setAllProperties();
        return System.getProperty(propertyName);
    }

    /**
     * Return the tolven persistent properties (from the database, not the running system)
     * @return Map<String, String>
     */
    public Properties findProperties(List<String> propertyNames) {
        Query query = em.createQuery("SELECT p FROM Property p WHERE p.name in (:propertyNames)");
        query.setParameter("propertyNames", propertyNames);
        List<Property> propertiesList = query.getResultList();
        Properties properties = new Properties();
        for (Property property : propertiesList) {
            properties.setProperty(property.getName(), property.getValue());
        }
        return properties;
    }
    
    /**
     * Return the tolven persistent properties (from the database, not the running system)
     * @return Properties
     */
    @Override
    public Properties findProperties() {
        Query query = em.createQuery("SELECT p FROM Property p");
        List<Property> propertiesList = query.getResultList();
        Properties properties = new Properties();
        for (Property property : propertiesList) {
            if (property.getValue() != null) {
                properties.setProperty(property.getName(), property.getValue());
            }
        }
        return properties;
    }

    /**
     * Remove tolven persistent properties (from the database AND the running system)
     */
    @Override
    public void removeProperty(String name) {
        Query query = em.createQuery("SELECT p FROM Property p WHERE p.name = :name");
        query.setParameter("name", name);
        List<Property> properties = query.getResultList();
        for (Property property : properties) {
            em.remove(property);
            System.clearProperty(property.getName());
        }
    }

    /**
     * Set a property in the running system and in the database.
     * @param propertyName Name of the property
     * @param propertyValue Value of the property
     */
    @Override
    public void setProperty(String propertyName, String propertyValue) {
        setAllProperties();
        Query query = em.createQuery("SELECT p FROM Property p where name = :n");
        query.setParameter("n", propertyName);
        List<Property> properties = query.getResultList();
        Property property;
        if (properties.size() == 0) {
            property = new Property();
            property.setName(propertyName);
            em.persist(property);
        } else {
            property = properties.get(0);
        }
        property.setValue(propertyValue);
        System.setProperty(propertyName, propertyValue);
    }

    /**
     * Update (persist or merge) the supplied properties into the database as well as
     * the running properties of the system. 
     */
    @Override
    public void setProperties(Properties properties) {
        for (Object obj : properties.keySet()) {
            String name = (String) obj;
            String value = properties.getProperty(name);
            Property property = new Property(name, value);
            em.merge(property);
            System.setProperty(name, value);
        }
        initialized = true;
    }

    /**
     * Set all of the properties from the database into the
     * running properties of the system. 
     */
    //	@PostConstruct
    public void setAllProperties() {
        if (!initialized) {
            Properties properties = findProperties();
            if (properties.isEmpty()) {
                throw new RuntimeException("Found no server properties in the database");
            }
            for (Object obj : properties.keySet()) {
                String name = (String) obj;
                System.setProperty(name, properties.getProperty(name));
            }
            initialized = true;
        }
    }

    /**
     * Delete properties
     */
    @Override
    public void removeProperties(List<String> properties) {
        for (String name : properties) {
            removeProperty(name);
        }
    }

}
