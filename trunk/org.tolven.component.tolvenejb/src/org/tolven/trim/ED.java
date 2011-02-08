
package org.tolven.trim;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ED complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ED">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}DataType">
 *       &lt;sequence>
 *         &lt;element name="representation" type="{urn:tolven-org:trim:4.0}BinaryDataEncoding" minOccurs="0"/>
 *         &lt;element name="mediaType" type="{urn:tolven-org:trim:4.0}cs" minOccurs="0"/>
 *         &lt;element name="charset" type="{urn:tolven-org:trim:4.0}cs" minOccurs="0"/>
 *         &lt;element name="compression" type="{urn:tolven-org:trim:4.0}cs" minOccurs="0"/>
 *         &lt;element name="reference" type="{urn:tolven-org:trim:4.0}TELSlot" minOccurs="0"/>
 *         &lt;element name="integrityCheck" type="{urn:tolven-org:trim:4.0}IntegrityCheck" minOccurs="0"/>
 *         &lt;element name="description" type="{urn:tolven-org:trim:4.0}STSlot" minOccurs="0"/>
 *         &lt;element name="thumbnail" type="{urn:tolven-org:trim:4.0}EDSlot" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="translation" type="{urn:tolven-org:trim:4.0}SET_EDSlot" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="language" type="{urn:tolven-org:trim:4.0}cs" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ED", propOrder = {
    "representation",
    "mediaType",
    "charset",
    "compression",
    "reference",
    "integrityCheck",
    "description",
    "thumbnail",
    "value",
    "translation"
})
public class ED
    extends DataType
    implements Serializable
{

    protected BinaryDataEncoding representation;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mediaType;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String charset;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String compression;
    protected TELSlot reference;
    protected IntegrityCheck integrityCheck;
    protected STSlot description;
    protected EDSlot thumbnail;
    protected byte[] value;
    protected SETEDSlot translation;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String language;

    /**
     * Gets the value of the representation property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryDataEncoding }
     *     
     */
    public BinaryDataEncoding getRepresentation() {
        return representation;
    }

    /**
     * Sets the value of the representation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryDataEncoding }
     *     
     */
    public void setRepresentation(BinaryDataEncoding value) {
        this.representation = value;
    }

    /**
     * Gets the value of the mediaType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Sets the value of the mediaType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMediaType(String value) {
        this.mediaType = value;
    }

    /**
     * Gets the value of the charset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the value of the charset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharset(String value) {
        this.charset = value;
    }

    /**
     * Gets the value of the compression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompression() {
        return compression;
    }

    /**
     * Sets the value of the compression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompression(String value) {
        this.compression = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link TELSlot }
     *     
     */
    public TELSlot getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link TELSlot }
     *     
     */
    public void setReference(TELSlot value) {
        this.reference = value;
    }

    /**
     * Gets the value of the integrityCheck property.
     * 
     * @return
     *     possible object is
     *     {@link IntegrityCheck }
     *     
     */
    public IntegrityCheck getIntegrityCheck() {
        return integrityCheck;
    }

    /**
     * Sets the value of the integrityCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegrityCheck }
     *     
     */
    public void setIntegrityCheck(IntegrityCheck value) {
        this.integrityCheck = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link STSlot }
     *     
     */
    public STSlot getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link STSlot }
     *     
     */
    public void setDescription(STSlot value) {
        this.description = value;
    }

    /**
     * Gets the value of the thumbnail property.
     * 
     * @return
     *     possible object is
     *     {@link EDSlot }
     *     
     */
    public EDSlot getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the value of the thumbnail property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDSlot }
     *     
     */
    public void setThumbnail(EDSlot value) {
        this.thumbnail = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setValue(byte[] value) {
        this.value = ((byte[]) value);
    }

    /**
     * Gets the value of the translation property.
     * 
     * @return
     *     possible object is
     *     {@link SETEDSlot }
     *     
     */
    public SETEDSlot getTranslation() {
        return translation;
    }

    /**
     * Sets the value of the translation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SETEDSlot }
     *     
     */
    public void setTranslation(SETEDSlot value) {
        this.translation = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
