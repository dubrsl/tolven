package org.tolven.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table
public class MenuLocator implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @Column
    private String path;
    
    @ManyToOne
    private AccountMenuStructure menuStructure;
    
    public MenuLocator() {
    	
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MenuLocator)) return false;
        MenuLocator ml = (MenuLocator) obj;
        return (path.equals(ml.path) && menuStructure==ml.menuStructure);
    }

    public int hashCode() {
    	return path.hashCode();
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public AccountMenuStructure getMenuStructure() {
		return menuStructure;
	}

	public void setMenuStructure(AccountMenuStructure menuStructure) {
		this.menuStructure = menuStructure;
	}

}
