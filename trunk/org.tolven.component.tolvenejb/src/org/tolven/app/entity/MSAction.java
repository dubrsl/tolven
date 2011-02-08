package org.tolven.app.entity;

import java.util.Collection;
import java.util.Properties;

import org.tolven.app.AppLocaleText;
import org.tolven.core.entity.Account;

/**
 * An interface that exposes a MenuStructure in the form of an Action
 * @author John Churin
 *
 */
public interface MSAction extends AppLocaleText {

	public String getRepeating();

	public String getVisible();

	public String getText();
 
	public String getNode();

	public AccountMenuStructure getParent();

	public String getMenuTemplate();

	public Account getAccount();

	public Collection<MSColumn> getColumns();

	public AccountMenuStructure getReferenced();
    public String getPath();
    public String getMenuEventHandlerFactory();
    public Properties getMenuEventHandlerDataMap() throws Exception;

}
