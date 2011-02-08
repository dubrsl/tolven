package org.tolven.app.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.core.entity.Account;
/**
 * <p>This entity contains a denomalized representation of a version number for each "collection"
 * represented in MenuData. This entity is always populated as a side-effect of updating or adding a new
 * MenuData entity by removing the trailing number and dash of the full path of the menuData item.</p>
 * <p>The resulting key should correspond to the "elementId" we maintain in the application for collections.
 * The version number maintained here represents the version of the collection which can be stored in the client and then used to
 * determine if a change has occurred that requires the client to requery the actual collection.</p>
 * <p>Although we maintain this entity transactionally, the data itself should not be used to
 * determine official transaction behavior. For example, the version number itself is not an official indication
 * that the underlying data has actually changed, only that a UI should consider refreshing the query of the underlying
 * data if the version number in the table is newer than what the client has. Think of this table as completely optional. Also, if a list has been defined
 * by MenuStructure but has not been populated with any MenuData, then no entry for it will exists in this table (version 1 is the lowest version
 * in this table). If this entity is eventually expanded to contain an approximate count of the number of rows in the underlying list, 
 * then version 0 might be possible depending on the method of counting. But again, the count is not official and not a substitute for
 * actually counting the rows. Also, the "approximate row count" can be assumed to be zero if no entry exists in this table.</p> 
 * @author John Churin
 *
 */
@Entity
@Table
public class MenuDataVersion implements Serializable {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column
    private String element;
    
    @Column
    private long version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date minDate;  

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date maxDate;
    
    @Column
    private String role;

	public Account getAccount() {
		return account;
	}
	/**
	 * Account that owns the list we are keeping track of.
	 * @param account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	/**
	 * The id of the list. This matches what the UI provides to the server when requesting 
	 * a given list.
	 * @return
	 */
	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * A version number incremented when an item is added to or removed from the list or if any item on the list is modified for any reason.
	 * This is an "unofficial" version number used by UIs, for display control.
	 * @return
	 */
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	
	/**
	 * Set the minimum and maximum date associated with this list. 
	 * If the supplied date is before the current min date, then the minimum is changed.
	 * If the supplied date is after the current max date, then the maximum is changed.
	 * @param date
	 */
	public void setMinMaxDate(Date date) {
		if (date!=null && (this.minDate==null || this.minDate.after(date))) {
			this.minDate = date;
		}
		if (date!=null && (this.maxDate==null || this.maxDate.before(date))) {
			this.maxDate = date;
		}
	}
	
	/**
	 * Get the minimum date
	 * @return
	 */
	public Date getMinDate() {
		return minDate;
	}
	
	/**
	 * Get the maximum date
	 * @return
	 */
	public Date getMaxDate() {
		return minDate;
	}
	
	/**
	 * @param minDate
	 */
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	/**
	 * @param maxDate
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

}
