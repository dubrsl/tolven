package org.tolven.core.bean;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.apache.commons.lang.StringUtils; 

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.app.MenuLocal;
import org.tolven.app.PersisterLocal;
import org.tolven.app.bean.DefaultPersister;
import org.tolven.app.entity.MenuData;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.PerformanceDataDAO;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.PerformanceData;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.session.TolvenSessionWrapperFactory;
import org.tolven.trim.process.ProcessTrim;

@Local(PersisterLocal.class)
public @Stateless class PerformanceAuditPersister extends DefaultPersister implements PersisterLocal {
	@EJB TolvenPropertiesLocal tolvenPropertiesBean;
	@EJB AccountDAOLocal accountBean;
	@EJB MenuLocal menuBean;
	@Resource EJBContext ejbContext;
	@EJB PerformanceDataDAO performanceDataDAO;
	@EJB ActivationLocal activationBean;
	
	private final String PERFORMANCE_STATS_FALSE = "false";
	
	@Override
	public void delete(MenuData md) {
		Date eventTime = new Date(System.currentTimeMillis());
		double beginNanoTime = System.nanoTime();
		double elapsed = 0.0;
		Exception caught=null;
		try{
			super.delete(md);
		}catch(Exception e){
			caught=e;
				throw new RuntimeException("Exception while deleting menudata:"+md,e);
		}finally{
			elapsed = System.nanoTime() - beginNanoTime;
			persistPerformanceData(md,eventTime, elapsed,caught,PersisterLocal.Method.DELETE.name());
		}
	}

	@Override
	public Participation getParticipation(MenuData md, Operation operation) {
		return super.getParticipation(md, operation);
	}

	@Override
	public void persist(MenuData md) {
		Date eventTime = new Date(System.currentTimeMillis());
		double beginNanoTime = System.nanoTime();
		double elapsed = 0.0;
		Exception caught=null;
		try{
			super.persist(md);
		}catch(Exception e){
			caught=e;
			throw new RuntimeException("Exception while persisting menudata:"+md,e);
		}finally{
			elapsed = System.nanoTime() - beginNanoTime;
			persistPerformanceData(md,eventTime, elapsed,caught,PersisterLocal.Method.POST.name());
		}
	}

	@Override
	public void update(MenuData md) {
		Date eventTime = new Date(System.currentTimeMillis());
		double beginNanoTime = System.nanoTime();
		double elapsed = 0.0;
		Exception caught=null;
		try{
			super.update(md);
		}catch(Exception e){
			caught=e;
			throw new RuntimeException("Exception while updating menudata:"+md,e);
		}finally{
			elapsed = System.nanoTime() - beginNanoTime;
			persistPerformanceData(md,eventTime, elapsed,caught,PersisterLocal.Method.PUT.name());
		}
	}
	
	/**
     * Method to persist performance data. This method is used to create log entries for specific operations
     * ADD,DELETE and UPDATE. Inorder to work with RestFul API's the vcorresponding HTTP methods (POST, DELETE and PUT)
     * are persisted in the DB. On the GUI the values are displayes as "POST (Add)"
     * 
     * @param md
     * @param eventTime
     * @param elapsed
     * @param caught
     * @param method
     */
    private void persistPerformanceData(MenuData md,Date eventTime, double elapsed, Exception caught,String method)
    {
    	//Check if Performance plugin is included
    	String performance_stats_value = tolvenPropertiesBean.getProperty(PerformanceDataDAO.PERFORMANCE_STATS_PROPERTY);
        if (performance_stats_value!=null && !PERFORMANCE_STATS_FALSE.equals(performance_stats_value)) {
        	String path = md.getPath();
    		String userName = getUser();
        	long accountId = md.getAccount().getId();
        	String role =md.getMenuStructure().getRole();
        	String errorString = null;
        	String auditElement="";
        		
			if(caught != null && Boolean.parseBoolean(tolvenPropertiesBean.getProperty("tolven.transaction.log"))) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				caught.printStackTrace(pw);
				errorString = sw.toString();				
			}
		
        	String URI="user/"+userName+"/account/"+accountId+"/"+role+"/"+path;
        	
        	PerformanceData performanceData = new PerformanceData();
    		performanceData.setEventTime(eventTime);
    		performanceData.setAccountUserID(getAccountUserId(userName,accountId));
    		performanceData.setElapsed(elapsed/1000000);
    		performanceData.setExceptions(errorString);
    		performanceData.setRemoteUserName(userName);
    		
    		//Extract patient Name. If the Path element contains "echr:patient-" it specifies that the operation is related 
    		//to a patient and corresponding audit records are persisted. 
    		if (path!=null && path.split("echr:patient-").length>1) 
    		{
    			String[] pathElements=path.split(":");
	        	
	        	if(pathElements.length>2 && pathElements[2].indexOf("-")!=-1)
	        	{
	        		auditElement=pathElements[2].substring(0,pathElements[2].indexOf("-"));
	        	}
	        	else if(pathElements.length>2)
	        	{
	        		auditElement="List "+pathElements[2];
	        	}
    			MenuData patMD = menuBean.findMenuDataItem(Long.parseLong(path.split("echr:patient-")[1].split(":")[0]));
	    		performanceData.setPatientName(String.format("%s, %s %s", patMD.getString01(),patMD.getString02(),patMD.getString03()));
    		}
	    	performanceData.setMethod(method +" "+ StringUtils.capitalize(auditElement));
    		performanceData.setRequestURI(URI);
    		performanceDataDAO.addPerformanceData(performanceData);
        }
    
    }

    /**
     * Method to get the user name. For user initiated operations use the callerPrinciple.
     * For operations initiated by the system (async messages) retrieve the author name from the 
     * TolvenMessage and use it as the userid.
     * 
     * @return String containing the user name
     */
    private String getUser()
    {
    	String userName = null;
    	//Call to thread local variable 
    	TolvenMessage tm=ProcessTrim.get();
    	//If the Tolven Message is not null get the author id and use it to find the user name.
    	if(tm!=null){
        	TolvenUser user=activationBean.findTolvenUser(tm.getAuthorId());
        	if(user!=null){
        		userName = user.getLdapUID();
        	}
    	}else{
    		userName = TolvenSessionWrapperFactory.getInstance().getPrincipal() !=null?(String)TolvenSessionWrapperFactory.getInstance().getPrincipal():null;
    	}
    	return userName;
    }
    /**
     * Method to get the account user id
     * 
     * @param userId
     * @param accountId
     * @return long value containing the account user id.
     */
    private long getAccountUserId(String userId, long accountId)
    {
		AccountUser accountUser=accountBean.findAccountUser(userId, accountId);
		if(accountUser!=null) return accountUser.getId();
		return 0;
    }
    
}
