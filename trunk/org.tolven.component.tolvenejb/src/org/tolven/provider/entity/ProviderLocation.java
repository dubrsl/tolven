package org.tolven.provider.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class ProviderLocation  implements Serializable  {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PROVIDER_SEQ_GEN")
    private long id;

    /**
     * The provider that owns this provider location entry.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Provider provider;

    @Column
    private String address1;

    @Column
    private String address2;

    @Column
    private String address3;
    
    @Column
    private String city;

    @Column
    private String state;
    
    @Column
    private String zip;

    @Column
    private String hours;

    @Column
    private String phone;

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProviderLocation)) return false;
		return ((ProviderLocation)obj).id==id;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

}
