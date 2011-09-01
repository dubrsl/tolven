//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.07.22 at 03:20:53 PM PDT 
//


package org.tolven.config.model.config.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DBServerDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DBServerDetail">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:tolven-config:1.0}EntityDetail">
 *       &lt;sequence>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="portNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="databaseName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="driverClass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="connectionString" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="connectionValidString" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="rootPassId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DBServerDetail", propOrder = {
    "user",
    "portNumber",
    "databaseName",
    "driverClass",
    "connectionString",
    "connectionValidString"
})
public class DBServerDetail
    extends EntityDetail
{

    @XmlElement(required = true)
    protected String user;
    @XmlElement(required = true)
    protected String portNumber;
    @XmlElement(required = true)
    protected String databaseName;
    @XmlElement(required = true)
    protected String driverClass;
    @XmlElement(required = true)
    protected String connectionString;
    @XmlElement(required = true)
    protected String connectionValidString;
    @XmlAttribute(required = true)
    protected String rootPassId;

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the portNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortNumber() {
        return portNumber;
    }

    /**
     * Sets the value of the portNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortNumber(String value) {
        this.portNumber = value;
    }

    /**
     * Gets the value of the databaseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the value of the databaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatabaseName(String value) {
        this.databaseName = value;
    }

    /**
     * Gets the value of the driverClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriverClass() {
        return driverClass;
    }

    /**
     * Sets the value of the driverClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriverClass(String value) {
        this.driverClass = value;
    }

    /**
     * Gets the value of the connectionString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectionString() {
        return connectionString;
    }

    /**
     * Sets the value of the connectionString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectionString(String value) {
        this.connectionString = value;
    }

    /**
     * Gets the value of the connectionValidString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectionValidString() {
        return connectionValidString;
    }

    /**
     * Sets the value of the connectionValidString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectionValidString(String value) {
        this.connectionValidString = value;
    }

    /**
     * Gets the value of the rootPassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootPassId() {
        return rootPassId;
    }

    /**
     * Sets the value of the rootPassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootPassId(String value) {
        this.rootPassId = value;
    }

}
