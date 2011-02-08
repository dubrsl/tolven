
package org.tolven.trim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ObservationValueSlot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObservationValueSlot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:tolven-org:trim:4.0}Slot">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="null" type="{urn:tolven-org:trim:4.0}NullFlavor"/>
 *         &lt;element name="AD" type="{urn:tolven-org:trim:4.0}AD"/>
 *         &lt;element name="SETAD" type="{urn:tolven-org:trim:4.0}AD" maxOccurs="unbounded"/>
 *         &lt;element name="BL" type="{urn:tolven-org:trim:4.0}BL"/>
 *         &lt;element name="CD" type="{urn:tolven-org:trim:4.0}CD"/>
 *         &lt;element name="SETCD" type="{urn:tolven-org:trim:4.0}CD" maxOccurs="unbounded"/>
 *         &lt;element name="CE" type="{urn:tolven-org:trim:4.0}CE"/>
 *         &lt;element name="SETCE" type="{urn:tolven-org:trim:4.0}CE" maxOccurs="unbounded"/>
 *         &lt;element name="ED" type="{urn:tolven-org:trim:4.0}ED"/>
 *         &lt;element name="SETED" type="{urn:tolven-org:trim:4.0}ED" maxOccurs="unbounded"/>
 *         &lt;element name="EN" type="{urn:tolven-org:trim:4.0}EN"/>
 *         &lt;element name="SETEN" type="{urn:tolven-org:trim:4.0}EN" maxOccurs="unbounded"/>
 *         &lt;element name="II" type="{urn:tolven-org:trim:4.0}II"/>
 *         &lt;element name="SETII" type="{urn:tolven-org:trim:4.0}II" maxOccurs="unbounded"/>
 *         &lt;element name="INT" type="{urn:tolven-org:trim:4.0}INT"/>
 *         &lt;element name="SETINT" type="{urn:tolven-org:trim:4.0}INT" maxOccurs="unbounded"/>
 *         &lt;element name="PQ" type="{urn:tolven-org:trim:4.0}PQ"/>
 *         &lt;element name="REAL" type="{urn:tolven-org:trim:4.0}REAL"/>
 *         &lt;element name="RTO" type="{urn:tolven-org:trim:4.0}RTO"/>
 *         &lt;element name="SETPQ" type="{urn:tolven-org:trim:4.0}PQ" maxOccurs="unbounded"/>
 *         &lt;element name="IVLPQ" type="{urn:tolven-org:trim:4.0}IVL_PQ"/>
 *         &lt;element name="SETIVLPQ" type="{urn:tolven-org:trim:4.0}IVL_PQ" maxOccurs="unbounded"/>
 *         &lt;element name="ST" type="{urn:tolven-org:trim:4.0}ST"/>
 *         &lt;element name="SETST" type="{urn:tolven-org:trim:4.0}ST" maxOccurs="unbounded"/>
 *         &lt;element name="TS" type="{urn:tolven-org:trim:4.0}TS"/>
 *         &lt;element name="SETTS" type="{urn:tolven-org:trim:4.0}TS" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationValueSlot", propOrder = {
    "setts",
    "ts",
    "setsts",
    "st",
    "setivlpqs",
    "ivlpq",
    "setpqs",
    "rto",
    "real",
    "pq",
    "setints",
    "_int",
    "setiis",
    "ii",
    "setens",
    "en",
    "seteds",
    "ed",
    "setces",
    "ce",
    "setcds",
    "cd",
    "bl",
    "setads",
    "ad",
    "_null"
})
public class ObservationValueSlot
    extends Slot
    implements Serializable
{

    @XmlElement(name = "SETTS")
    protected List<TS> setts;
    @XmlElement(name = "TS")
    protected TS ts;
    @XmlElement(name = "SETST")
    protected List<ST> setsts;
    @XmlElement(name = "ST")
    protected ST st;
    @XmlElement(name = "SETIVLPQ")
    protected List<IVLPQ> setivlpqs;
    @XmlElement(name = "IVLPQ")
    protected IVLPQ ivlpq;
    @XmlElement(name = "SETPQ")
    protected List<PQ> setpqs;
    @XmlElement(name = "RTO")
    protected RTO rto;
    @XmlElement(name = "REAL")
    protected REAL real;
    @XmlElement(name = "PQ")
    protected PQ pq;
    @XmlElement(name = "SETINT")
    protected List<INT> setints;
    @XmlElement(name = "INT")
    protected INT _int;
    @XmlElement(name = "SETII")
    protected List<II> setiis;
    @XmlElement(name = "II")
    protected II ii;
    @XmlElement(name = "SETEN")
    protected List<EN> setens;
    @XmlElement(name = "EN")
    protected EN en;
    @XmlElement(name = "SETED")
    protected List<ED> seteds;
    @XmlElement(name = "ED")
    protected ED ed;
    @XmlElement(name = "SETCE")
    protected List<CE> setces;
    @XmlElement(name = "CE")
    protected CE ce;
    @XmlElement(name = "SETCD")
    protected List<CD> setcds;
    @XmlElement(name = "CD")
    protected CD cd;
    @XmlElement(name = "BL")
    protected BL bl;
    @XmlElement(name = "SETAD")
    protected List<AD> setads;
    @XmlElement(name = "AD")
    protected AD ad;
    @XmlElement(name = "null")
    protected NullFlavor _null;

    /**
     * Gets the value of the setts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TS }
     * 
     * 
     */
    public List<TS> getSETTS() {
        if (setts == null) {
            setts = new ArrayList<TS>();
        }
        return this.setts;
    }

    /**
     * Gets the value of the ts property.
     * 
     * @return
     *     possible object is
     *     {@link TS }
     *     
     */
    public TS getTS() {
        return ts;
    }

    /**
     * Sets the value of the ts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TS }
     *     
     */
    public void setTS(TS value) {
        this.ts = value;
    }

    /**
     * Gets the value of the setsts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setsts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETSTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ST }
     * 
     * 
     */
    public List<ST> getSETSTS() {
        if (setsts == null) {
            setsts = new ArrayList<ST>();
        }
        return this.setsts;
    }

    /**
     * Gets the value of the st property.
     * 
     * @return
     *     possible object is
     *     {@link ST }
     *     
     */
    public ST getST() {
        return st;
    }

    /**
     * Sets the value of the st property.
     * 
     * @param value
     *     allowed object is
     *     {@link ST }
     *     
     */
    public void setST(ST value) {
        this.st = value;
    }

    /**
     * Gets the value of the setivlpqs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setivlpqs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETIVLPQS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IVLPQ }
     * 
     * 
     */
    public List<IVLPQ> getSETIVLPQS() {
        if (setivlpqs == null) {
            setivlpqs = new ArrayList<IVLPQ>();
        }
        return this.setivlpqs;
    }

    /**
     * Gets the value of the ivlpq property.
     * 
     * @return
     *     possible object is
     *     {@link IVLPQ }
     *     
     */
    public IVLPQ getIVLPQ() {
        return ivlpq;
    }

    /**
     * Sets the value of the ivlpq property.
     * 
     * @param value
     *     allowed object is
     *     {@link IVLPQ }
     *     
     */
    public void setIVLPQ(IVLPQ value) {
        this.ivlpq = value;
    }

    /**
     * Gets the value of the setpqs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setpqs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETPQS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PQ }
     * 
     * 
     */
    public List<PQ> getSETPQS() {
        if (setpqs == null) {
            setpqs = new ArrayList<PQ>();
        }
        return this.setpqs;
    }

    /**
     * Gets the value of the rto property.
     * 
     * @return
     *     possible object is
     *     {@link RTO }
     *     
     */
    public RTO getRTO() {
        return rto;
    }

    /**
     * Sets the value of the rto property.
     * 
     * @param value
     *     allowed object is
     *     {@link RTO }
     *     
     */
    public void setRTO(RTO value) {
        this.rto = value;
    }

    /**
     * Gets the value of the real property.
     * 
     * @return
     *     possible object is
     *     {@link REAL }
     *     
     */
    public REAL getREAL() {
        return real;
    }

    /**
     * Sets the value of the real property.
     * 
     * @param value
     *     allowed object is
     *     {@link REAL }
     *     
     */
    public void setREAL(REAL value) {
        this.real = value;
    }

    /**
     * Gets the value of the pq property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getPQ() {
        return pq;
    }

    /**
     * Sets the value of the pq property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setPQ(PQ value) {
        this.pq = value;
    }

    /**
     * Gets the value of the setints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETINTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link INT }
     * 
     * 
     */
    public List<INT> getSETINTS() {
        if (setints == null) {
            setints = new ArrayList<INT>();
        }
        return this.setints;
    }

    /**
     * Gets the value of the int property.
     * 
     * @return
     *     possible object is
     *     {@link INT }
     *     
     */
    public INT getINT() {
        return _int;
    }

    /**
     * Sets the value of the int property.
     * 
     * @param value
     *     allowed object is
     *     {@link INT }
     *     
     */
    public void setINT(INT value) {
        this._int = value;
    }

    /**
     * Gets the value of the setiis property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setiis property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETIIS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link II }
     * 
     * 
     */
    public List<II> getSETIIS() {
        if (setiis == null) {
            setiis = new ArrayList<II>();
        }
        return this.setiis;
    }

    /**
     * Gets the value of the ii property.
     * 
     * @return
     *     possible object is
     *     {@link II }
     *     
     */
    public II getII() {
        return ii;
    }

    /**
     * Sets the value of the ii property.
     * 
     * @param value
     *     allowed object is
     *     {@link II }
     *     
     */
    public void setII(II value) {
        this.ii = value;
    }

    /**
     * Gets the value of the setens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETENS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EN }
     * 
     * 
     */
    public List<EN> getSETENS() {
        if (setens == null) {
            setens = new ArrayList<EN>();
        }
        return this.setens;
    }

    /**
     * Gets the value of the en property.
     * 
     * @return
     *     possible object is
     *     {@link EN }
     *     
     */
    public EN getEN() {
        return en;
    }

    /**
     * Sets the value of the en property.
     * 
     * @param value
     *     allowed object is
     *     {@link EN }
     *     
     */
    public void setEN(EN value) {
        this.en = value;
    }

    /**
     * Gets the value of the seteds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the seteds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETEDS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ED }
     * 
     * 
     */
    public List<ED> getSETEDS() {
        if (seteds == null) {
            seteds = new ArrayList<ED>();
        }
        return this.seteds;
    }

    /**
     * Gets the value of the ed property.
     * 
     * @return
     *     possible object is
     *     {@link ED }
     *     
     */
    public ED getED() {
        return ed;
    }

    /**
     * Sets the value of the ed property.
     * 
     * @param value
     *     allowed object is
     *     {@link ED }
     *     
     */
    public void setED(ED value) {
        this.ed = value;
    }

    /**
     * Gets the value of the setces property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setces property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETCES().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CE }
     * 
     * 
     */
    public List<CE> getSETCES() {
        if (setces == null) {
            setces = new ArrayList<CE>();
        }
        return this.setces;
    }

    /**
     * Gets the value of the ce property.
     * 
     * @return
     *     possible object is
     *     {@link CE }
     *     
     */
    public CE getCE() {
        return ce;
    }

    /**
     * Sets the value of the ce property.
     * 
     * @param value
     *     allowed object is
     *     {@link CE }
     *     
     */
    public void setCE(CE value) {
        this.ce = value;
    }

    /**
     * Gets the value of the setcds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setcds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETCDS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CD }
     * 
     * 
     */
    public List<CD> getSETCDS() {
        if (setcds == null) {
            setcds = new ArrayList<CD>();
        }
        return this.setcds;
    }

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link CD }
     *     
     */
    public CD getCD() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CD }
     *     
     */
    public void setCD(CD value) {
        this.cd = value;
    }

    /**
     * Gets the value of the bl property.
     * 
     * @return
     *     possible object is
     *     {@link BL }
     *     
     */
    public BL getBL() {
        return bl;
    }

    /**
     * Sets the value of the bl property.
     * 
     * @param value
     *     allowed object is
     *     {@link BL }
     *     
     */
    public void setBL(BL value) {
        this.bl = value;
    }

    /**
     * Gets the value of the setads property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setads property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSETADS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AD }
     * 
     * 
     */
    public List<AD> getSETADS() {
        if (setads == null) {
            setads = new ArrayList<AD>();
        }
        return this.setads;
    }

    /**
     * Gets the value of the ad property.
     * 
     * @return
     *     possible object is
     *     {@link AD }
     *     
     */
    public AD getAD() {
        return ad;
    }

    /**
     * Sets the value of the ad property.
     * 
     * @param value
     *     allowed object is
     *     {@link AD }
     *     
     */
    public void setAD(AD value) {
        this.ad = value;
    }

    /**
     * Gets the value of the null property.
     * 
     * @return
     *     possible object is
     *     {@link NullFlavor }
     *     
     */
    public NullFlavor getNull() {
        return _null;
    }

    /**
     * Sets the value of the null property.
     * 
     * @param value
     *     allowed object is
     *     {@link NullFlavor }
     *     
     */
    public void setNull(NullFlavor value) {
        this._null = value;
    }

}
