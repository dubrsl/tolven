
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LanguageCommunication complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LanguageCommunication">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="languageCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="modeCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="proficiencyLevelCode" type="{urn:tolven-org:trim:4.0}CESlot" minOccurs="0"/>
 *         &lt;element name="preferenceInd" type="{urn:tolven-org:trim:4.0}BLSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LanguageCommunication", propOrder = {
    "languageCode",
    "modeCode",
    "proficiencyLevelCode",
    "preferenceInd"
})
public class LanguageCommunication
    implements Serializable
{

    protected CESlot languageCode;
    protected CESlot modeCode;
    protected CESlot proficiencyLevelCode;
    protected BLSlot preferenceInd;

    /**
     * Gets the value of the languageCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the value of the languageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setLanguageCode(CESlot value) {
        this.languageCode = value;
    }

    /**
     * Gets the value of the modeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getModeCode() {
        return modeCode;
    }

    /**
     * Sets the value of the modeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setModeCode(CESlot value) {
        this.modeCode = value;
    }

    /**
     * Gets the value of the proficiencyLevelCode property.
     * 
     * @return
     *     possible object is
     *     {@link CESlot }
     *     
     */
    public CESlot getProficiencyLevelCode() {
        return proficiencyLevelCode;
    }

    /**
     * Sets the value of the proficiencyLevelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CESlot }
     *     
     */
    public void setProficiencyLevelCode(CESlot value) {
        this.proficiencyLevelCode = value;
    }

    /**
     * Gets the value of the preferenceInd property.
     * 
     * @return
     *     possible object is
     *     {@link BLSlot }
     *     
     */
    public BLSlot getPreferenceInd() {
        return preferenceInd;
    }

    /**
     * Sets the value of the preferenceInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BLSlot }
     *     
     */
    public void setPreferenceInd(BLSlot value) {
        this.preferenceInd = value;
    }

}
