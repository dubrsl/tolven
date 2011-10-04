package org.tolven.cchit.component.hl7;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tolven.app.MirthOperationsLocal;
import org.tolven.client.TolvenClient;
import org.tolven.client.TolvenClientEx;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.mirth.Message;
import org.tolven.mirth.ResponseMarshaller;
import org.tolven.mirth.api.ProcessLabResultsLocal;
import org.tolven.security.LDAPLocal;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;

/**
 * This class receives the messages from HL7 v2.5
 * 
 * @author Param added on 7/27/2010
 */
public class Hl7MessageProcessor extends HttpServlet {

	/**
	 * Added default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	@EJB
	private MirthOperationsLocal mirthOperationsBean;
	
	@EJB
	private TolvenPropertiesLocal propertyBean;
	
	@EJB
	private LDAPLocal ldapBean;
	
	@EJB
	private ActivationLocal activationBean;
	
	@EJB
	private ProcessLabResultsLocal labProcessor;
	
	
    public MirthOperationsLocal getMirthOperationsBean() {
		return mirthOperationsBean;
    }
    
	 public TolvenPropertiesLocal getPropertyBean() {
			return propertyBean;
	    }
	
    public void init(ServletConfig config) throws ServletException {
		
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			//ORU_R01 oruR01Message = (ORU_R01) Hl7MessageHandler.getMessage(req.getParameter("message"));
			ORU_R01 oruR01Message = (ORU_R01) Hl7MessageHandler.getMessageFromFilePath(req.getParameter("file"));
			String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
	        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
	        PrivateKey userPrivateKey = sessionWrapper.getUserPrivateKey(keyAlgorithm);
		        
			long accountId = Long.parseLong(oruR01Message.getPATIENT_RESULT().getPATIENT().getPID().getPatientAccountNumber().getIDNumber().getValue());
			TolvenClient loginClient = new TolvenClientEx();
			if(oruR01Message != null){
				TolvenUser user = activationBean.loginUser(propertyBean.getProperty("tolven.mirth.user.name"), new Date());
				if(user != null){
					List<AccountUser> accountUsers = activationBean.findUserAccounts(user);
					for(AccountUser accountUser: accountUsers) {
						if (accountUser.getAccount().getId()== accountId){
							labProcessor.saveLabResults(oruR01Message, accountUser, new Date(),userPrivateKey);
						}
					}
				}
			}
				
			System.out.println(oruR01Message.getPATIENT_RESULT().getPATIENT().getNK1().getAddress(0).getCity().getValue());
		} catch (EncodingNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Starts a new session for Mirth user
	 * @author Syed
	 * @param loginClient - the TolvenClient object
	 * added on 6/29/2010
	 */
	public TolvenUser getMirthUser(TolvenClient loginClient){
		String uid = propertyBean.getProperty("tolven.mirth.user.name");
		String password = propertyBean.getProperty("tolven.mirth.user.password");
		return loginClient.login(uid, password);
	}

}
