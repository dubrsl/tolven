package org.tolven.trim.scan;

import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.tolven.app.BindProcessor;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.trim.BindPhase;
import org.tolven.trim.Trim;

/**
 * Hold context information during a bind operation 
 * @author John Churin
 */
public class BindContext {
	private Trim trim;
	private BindPhase phase;
	private Account account;
	private List<MenuPath> menuContext;
	private MenuData menuDataSource;
	private Date now;
	
	private Stack<String> location = new Stack<String>();
	public BindContext() {
		
	}
	
	public BindContext(Trim trim, BindPhase phase, Account account,
			List<MenuPath> menuContext, Date now) {
		super();
		this.trim = trim;
		this.phase = phase;
		this.account = account;
		this.menuContext = menuContext;
		this.now = now;
	}
	
	/**
	 * Match the path we're attempting to create to the context that we are in. For example,
	 * we might have a context like echr:patient-123:results and looking for a path of
	 * echr:patient in which case we would return echr:patient-123. If not found, we return null.
	 * @param path
	 * @return if found, we return the path, otherwise null
	 */
	public String matchContext( String path ) {
		String pathPrefix = path + "-";
		for (MenuPath item : getMenuContext() ) {
			if (item.getPath().startsWith(pathPrefix)) {
				MenuPath newPath = new MenuPath( path, item);
				return newPath.getPathString();
			}
		}
		return null;
	}
	public void pushLocation( String location ) {
		this.location.push(location);
	}
	public String popLocation( ) {
		return this.location.pop();
	}
	
	/**
	 * Return a valid Expression Language (EL) location path within the trim. For example, trim.act.participation['subject'].role
	 * @return 
	 */
	public String getLocation( ) {
		StringBuffer sb = new StringBuffer(100);
		boolean first = true;
		for ( int x = location.size(); x > 0; x--) {
			if (first) first=false;
			else sb.append(".");
			sb.append( location.get(x) );
		}
		return sb.toString();
	}
	public String getKnownType() {
		return getAccount().getAccountType().getKnownType();
	}

	public Trim getTrim() {
		return trim;
	}
	public void setTrim(Trim trim) {
		this.trim = trim;
	}
	public BindPhase getPhase() {
		return phase;
	}
	public void setPhase(BindPhase phase) {
		this.phase = phase;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public List<MenuPath> getMenuContext() {
		return menuContext;
	}
	public void setMenuContext(List<MenuPath> menuContext) {
		this.menuContext = menuContext;
	}
	/**
	 * MenuData source is used in cases where some binding information is extracted from
	 * menuData.
	 * @return
	 */
	public MenuData getMenuDataSource() {
		return menuDataSource;
	}
	public void setMenuDataSource(MenuData menuDataSource) {
		this.menuDataSource = menuDataSource;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}
}
