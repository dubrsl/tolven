package org.tolven.client.example;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.tolven.app.entity.MDQueryResults;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.client.TolvenClient;
import org.tolven.core.entity.Account;
import org.tolven.logging.TolvenLogger;

/**
 * Sample Tolven Client that gets a patient list from the server.
 */
public class SimplePatientList extends TolvenClient {
	private long accountId;
	// Note: The following literal assumes that the selected account is configured 
	// with a patient list by this name. (Tolven does not require such a list by the example
	// application comes with one.
	public static final String ALL_PATIENTS = "echr:patients:all";

	public SimplePatientList() {
		super();
		TolvenLogger.initialize();
	}

	public void findAccount(long accountId) {
		setAccountId( accountId );
		Account account = this.getAccountBean().findAccount(accountId);
		TolvenLogger.info( "Verified Account: " + account.getTitle() + " (" + account.getId() + ")", SimplePatientList.class ); 
		loginToAccount(accountId);
	}
	
	public List<Object[]> findAllPatients() {
		MenuQueryControl ctrl = new MenuQueryControl( getAccountUser(), ALL_PATIENTS ); 
		ctrl.getColumns().add("id");	// id is available by default on all items returned.
		ctrl.getColumns().add("Name");
		ctrl.getColumns().add("DOB");
		ctrl.getColumns().add("Sex");
		ctrl.getColumns().add("string01"); // Internal Names also work
		ctrl.getColumns().add("path"); // Internal Names also work
		return getMenuBean().findMenuDataRows(ctrl);
	}
	
	/**
	 * Another way to do the same thing.
	 */
	public List<Map<String, Object>> findAllPatients2() {
		MenuQueryControl ctrl = new MenuQueryControl( getAccountUser(), ALL_PATIENTS ); 
		ctrl.setLimit( 10 );
		ctrl.setNow( new Date() );
		ctrl.setOffset( 0 );
//		ctrl.setFilter("troyano");
		MDQueryResults  mdQueryResults = getMenuBean().findMenuDataByColumns(ctrl);
		List<Map<String, Object>> rows = mdQueryResults.getResults();
		return rows;
	}
	
	public void printPatients() throws Exception {
		int colwidth[] = {12,35, 25, 10, 20, 35};
		System.out.println( "\nList of " + ALL_PATIENTS + " for account " + this.getAccountId());
		for(Object row[] : findAllPatients()) { 
			StringBuffer sb = new StringBuffer(256);
			int padoffset = 0;
			int colindex = 0;
			for (Object col : row) {
				if (col!=null) {
					sb.append(col);
				}
				padoffset += colwidth[colindex++];
				// Pad out the column
				while (sb.length() < padoffset ) sb.append(" ");
			}
			System.out.println(sb); 
		} 
	}
	
	public void printPatients2() throws Exception {
		System.out.println( "\nSame list but only the first 10 are selected and in name-value pairs");
		for(Map<String, Object> row : findAllPatients2()) { 
			StringBuffer sb = new StringBuffer(256);
			for (String key : row.keySet()) {
				sb.append(key);
				sb.append("=");
				sb.append(row.get(key));
				sb.append("  ");
			}
			System.out.println(sb); 
		} 
	}
	
	/**
	 * Simple test calling Tolven via J2EE remote
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	    if(args.length < 1) {
	        System.out.println("Arguments: userId password accountId");
	        return;
	    }
		SimplePatientList lpl = new SimplePatientList();
		lpl.login(args[0], args[1]);
		lpl.findAccount(Long.parseLong(args[2])); 
//		lpl.printPatients();
		lpl.printPatients2();
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	
}