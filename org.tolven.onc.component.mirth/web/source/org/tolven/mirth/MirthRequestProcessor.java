package org.tolven.mirth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.tolven.app.MirthOperationsLocal;
import org.tolven.client.TolvenClient;
import org.tolven.client.TolvenClientEx;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.bean.TolvenProperties;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;



/**
 * This class receives the messages from Mirth
 * @author Syed
 * added on 6/21/2010
 */
public class MirthRequestProcessor extends HttpServlet{
	
	
	@EJB
	private MirthOperationsLocal mirthOperationsBean;
	
	@EJB
	private ActivationLocal activationBean;
	
	@EJB
	private TolvenPropertiesLocal propertyBean;
	
	private ResponseMarshaller responseMarshaller;
	
    public MirthOperationsLocal getMirthOperationsBean() {
		return mirthOperationsBean;
    }
    
	 public TolvenPropertiesLocal getPropertyBean() {
			return propertyBean;
	    }
	
    public void init(ServletConfig config) throws ServletException {
            responseMarshaller = new ResponseMarshaller();
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		TolvenClient loginClient = new TolvenClientEx();
		TolvenUser user = getMirthUser(loginClient);
		String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        PrivateKey userPrivateKey = sessionWrapper.getUserPrivateKey(keyAlgorithm);
        
		if(user != null){
			Response responseObj = getPOSTRequestPayload(req);
			if(! responseObj.getMessages().isEmpty()){
				mirthOperationsBean.updateDocument(user, responseObj,userPrivateKey);
			}
			logout(loginClient);
		}	
		
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		System.out.println("Payload: "+req.getParameter("payload"));
//		TolvenClient loginClient = new TolvenClientEx();
//		TolvenUser user = activationBean.loginUser(propertyBean.getProperty("tolven.mirth.user.name"), new Date());
//		String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
//		PrivateKey userPrivateKey = TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm);
//
//		if(user != null){
//			Response responseObj = getGETRequestPayload(req);
//			if(! responseObj.getMessages().isEmpty()){
//				mirthOperationsBean.updateDocument(user, responseObj,userPrivateKey);
//			}
//		}
	}
	
	/**
	 * Extracts the GET request from Mirth and converts it into Response object
	 * @author Syed
	 * @param req
	 * @return response
	 * added on 6/28/2010
	 */
	private Response getGETRequestPayload(HttpServletRequest req){
		//Map<String, Object> reqMap = new HashMap<String, Object>();
		Response response = new Response();
		Message msg = new Message();
		msg.setAccountId(Long.parseLong(req.getParameter("accountId")));
		msg.setEventPath(req.getParameter("eventPath"));
		msg.setProcessed(Boolean.parseBoolean(req.getParameter("processed")));
		response.getMessages().add(msg);
		//reqMap.put("response", response);
		//reqMap.put("now", req.getAttribute("tolvenNow"));
		return response;
	}
	
	/**
	 * Extracts the POST request from Mirth and converts it into Response object
	 * @author Syed
	 * @param req
	 * @return response
	 * added on 6/28/2010
	 */
	private Response getPOSTRequestPayload(HttpServletRequest req){
		//Map<String, Object> reqMap = new HashMap<String, Object>();
		BufferedReader in;
		String inputLine;
		//StringBuffer sb = new StringBuffer();
		//String msg = req.getParameter("payload");
		String msg="MSH|^~\\&|DDTEK LAB|ELAB-1|TOLVEN|BLDG14|200502150930||ORU^R01^ORU_R01|CTRL-9876|" +
				"P|2.5PID|1|723000|010-11-1111||Estherhaus^Eva^E^^^^L|Smith|19720520|F|||256 Sherwood" +
				" Forest Dr.^^Baton Rouge^LA^70809||(225)334-5232|(225)752-1213||||196000||76-B4335^LA^20070520OBR|" +
				"1|948642^DDTEK OE|917363^DDTEK LAB|1554-5^GLUCOSE|||200502150730|||||||||020-22-2222^Levin-" +
				"Epstein^Anna^^^^MD^^Micro-Managed Health Associates|||||||||F|||||||030-33-3333&Honeywell&" +
				"Carson&&&&MDOBX|1|SN|1554-5^GLUCOSE^^^POST 12H CFST:MCNC:PT:SER/PLAS:QN||^175|mg/dl|70_105|H|||F||||||||200502150730";
		System.out.println("Payload: "+req.getParameter("payload"));

		Response response = new Response();
		try {
			in = new BufferedReader(new InputStreamReader(req.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				//sb.append(inputLine+"\n");
			}
			response = responseMarshaller.UnmarshalFromStream(new StreamSource(new StringReader(msg))).getValue();
			//reqMap.put("response", response);
			//reqMap.put("now", req.getAttribute("tolvenNow"));
		} catch (Exception e) {
			e.printStackTrace();
			//reqMap = null;
		}
	   return response;
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
	
	/**
	 * Ends the user session
	 * @author Syed
	 * @param loginClient - the TolvenClient object
	 * added on 6/29/2010
	 */
	public void logout(TolvenClient loginClient){
		loginClient.logout();
	}
	
}
