package org.tolven.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This entity provides a cross reference from a word to a menu data item.
 * Words are normalized: lower case, special characters and numbers are removed.
 * 
 * @author John Churin
 *
 */
@Entity
@Table
public class MenuDataWord implements Serializable {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountMenuStructure menuStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData menuData;

    @Column
    private String language;

    @Column
    private String field;

    @Column
    private int position;

    @Column
    private String word;
	
    /**
     * Compare two MenuDataWord objects for equality. This test is based on the field the word occurs in, language, 
     * the word and the menu data item in which the word occurs. 
     * MenuStructure is not included in the test because MenuData is more selective and implies MenuStructure.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof MenuDataWord)) return false;
        if (!this.getMenuData().equals(((MenuDataWord)obj).getMenuData())) return false;
        if (!this.getLanguage().equals(((MenuDataWord)obj).getLanguage())) return false;
        if (!this.getWord().equals(((MenuDataWord)obj).getWord())) return false;
        if (this.getPosition()!=((MenuDataWord)obj).getPosition()) return false;
        if (!this.getField().equals(((MenuDataWord)obj).getField())) return false;
        return true;
    }

    /**
     * Return a hash code for this object. Note: The hashCode is based on the the word which is 
     * adequate for a hash.
     */
    public int hashCode() {
        return getWord().hashCode();
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * The MenuData item containing this word.
	 * @return
	 */
	public MenuData getMenuData() {
		return menuData;
	}

	public void setMenuData(MenuData menuData) {
		this.menuData = menuData;
	}

	public AccountMenuStructure getMenuStructure() {
		return menuStructure;
	}

	public void setMenuStructure(AccountMenuStructure menuStructure) {
		this.menuStructure = menuStructure;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * The field the word is found in. This doubles as a relationship type.
	 * For example, when extracted from a diagnosis, this could contain
	 * category thus distinguishing the word from words from the name of the
	 * diagnoisis itself.
	 * @return
	 */
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
	/**
	 * The position of the word in the field it appears in. one-origin so the first position is one. The position ignores ignores. 
	 * stop-words. For example, my name is bill, name is position 1 and bill is position 2. 
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
