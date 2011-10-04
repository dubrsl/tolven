package org.tolven.ajax;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.tolven.app.CCHITLocal;
import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.TrimCreatorLocal;
import org.tolven.app.TrimLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MDQueryResults;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.TrimHeader;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocXML;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.session.TolvenSessionWrapper;
import org.tolven.session.TolvenSessionWrapperFactory;

import org.tolven.trim.ActRelationship;
import org.tolven.trim.ActRelationshipDirection;
import org.tolven.trim.ActRelationshipType;
import org.tolven.trim.CE;
import org.tolven.trim.RoleClass;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.ActRelationshipsMap;
import org.tolven.trim.ex.CEEx;
import org.tolven.trim.ex.TRIMException;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;
import org.tolven.web.MenuAction;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.util.FormattedOutputWriter;

/**
 * This servlet added for CCHITto process newly added requests.
 * 
 * @author valsaraj
 * added on 05/20/2010
 */
public class CCHITServlet extends HttpServlet {
	
	private static final String TRIMns = "urn:tolven-org:trim:4.0";
	private static final TrimFactory trimFactory = new TrimFactory();
	private static final long serialVersionUID = 1L;
	
	@EJB
    private CCHITLocal cchitBean;
	
	@EJB
	private MenuLocal menuBean;
	
	@EJB
    private DocumentLocal docBean;

    @EJB
    private DocProtectionLocal docProtectionBean;

    @EJB
    private XMLLocal xmlBean;
    
    @EJB
    private TrimLocal trimBean;

	@EJB
    private CreatorLocal creatorBean;
    
	@EJB 
	private AccountDAOLocal accountBean;

	@EJB 
	private TrimCreatorLocal trimCreatorBean;
	
	@EJB 
    private MenuLocal menuLocal;
	
	@EJB
    private TolvenPropertiesLocal propertyBean;
	
	private List<MenuPath> contextList;
	private String templateBody;
	
	public DocumentLocal getDocBean() {
		return docBean;
	}

    public DocProtectionLocal getDocProtectionBean() {
        return docProtectionBean;
    }
	
	@Override
	public void init(ServletConfig config) throws ServletException {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		AccountUser activeAccountUser = (AccountUser) req.getAttribute("accountUser");
		Date now = (Date) req.getAttribute("tolvenNow");
		TrimFactory trimFactory = new TrimFactory();
		Writer writer = resp.getWriter();
		TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
		String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        TolvenSessionWrapper sessionWrapper = TolvenSessionWrapperFactory.getInstance();
        PrivateKey userPrivateKey = sessionWrapper.getUserPrivateKey(keyAlgorithm);

		/**
		 * To retrieve reference id using string01.
		 * 
		 * @author Valsaraj
		 * added on 05/31/2010
		 */
		if (uri.endsWith("getPlaceholderId.ajaxcchit")) {
			String string01 = req.getParameter("string01");
			
			
			try {
				writer.write(Long.toString(cchitBean.findReferenceId(string01, activeAccountUser.getAccount().getId())));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * To retrieve summary of unique clinical findings.
		 * 
		 * @author Valsaraj
		 * added on 05/31/2010
		 */
		else if (uri.endsWith("getSummary.ajaxcchit")) {
			try {
				// Get path for the parent menu structure
		    	String path = req.getParameter("element");		    	
		    	
		    	String title = "";
		    	Set<String> diagnosesCodeSet = new HashSet<String>();
		    	String diagnosesCodes = "";
		    	Set<String> problemCodeSet = new HashSet<String>();
		    	String problemCodes = "";
		    	Set<String> medicationsCodeSet = new HashSet<String>();
		    	String medicationsCodes = "";
		    	Set<String> allergiesCodeSet = new HashSet<String>();
		    	String allergiesCodes = "";
		    	Set<String> proceduresCodeSet = new HashSet<String>();
		    	String proceduresCodes = "";
		    	String summaryHTML = "";
		    	final String DIAGNOSES_PATH = "echr:patient:diagnoses:current";
	    		final String PROBLEMS_PATH = "echr:patient:problems:current";
	    		final String MEDICATIONS_PATH = "echr:patient:medications:current";
	    		final String ALLERGIES_PATH = "echr:patient:allergies:current";
	    		final String PROCEDURES_PATH = "echr:patient:procedures:pxList";
	    		final String LABTESTS_PATH = "echr:patient:labtests:current";
	    		
		    	try {
		    		title = tolvenResourceBundle.getString("Summary");		    	
		    	}
		    	catch (Exception e) {
			    	title = "Summary";
				}
		    	
		    	try {
		    		String extension = req.getParameter("extension");
		    		String[] tmp = extension.split("-");
		    		MenuData md = menuBean.findMenuDataItem(Long.parseLong(tmp[1]));
		    		
		    		try {		    		
				    	if ("Yes".equals(md.getField("diagnosisSummary").toString())) {
					    	try {		    		
					    		List<Map<String, Object>> menuDataList = cchitBean.findAllMenuDataList(DIAGNOSES_PATH, path + ":diagnoses:current", "DateSort=DESC", activeAccountUser);
					    		
					    		for (Map<String, Object> menuData : menuDataList) {
					    			diagnosesCodeSet.add(menuData.get("Code").toString());
					    		}
					    		
				    			for (String code : diagnosesCodeSet) {
				    				diagnosesCodes += ", " + code;
								}
					    			
					    		summaryHTML += "<tr class=\"odd\"><td><b>Diagnoses</b></td></tr>"
												+ "<tr style=\"background-color:#EEFFFF\"><td>"
												+ (diagnosesCodes.length() > 1 ? diagnosesCodes.substring(2) : "Nil")
												+ "</td></tr>";
					    	}
					    	catch (Exception e) {
								
							}
				    	}
		    		}
		    		catch (Exception e) {
						
					}
		    		
		    		try {
				    	if ("Yes".equals(md.getField("problemSummary").toString())) {
					    	try {		    		
					    		List<Map<String, Object>> menuDataList = cchitBean.findAllMenuDataList(PROBLEMS_PATH, path + ":problems:current", "DateSort=DESC", activeAccountUser);
					    		
					    		for (Map<String, Object> menuData : menuDataList) {
					    			problemCodeSet.add(menuData.get("Code").toString());
					    		}
					    		
					    		for (String code : problemCodeSet) {
					    			problemCodes += ", " + code;
								}
					    		
					    		summaryHTML += "<tr><td><b>Problem</b></td></tr>"
												+ "<tr style=\"background-color:#EEFFFF\"><td>"
												+ (problemCodes.length() > 1 ? problemCodes.substring(2) : "Nil")
												+ "</td></tr>";
					    	}
					    	catch (Exception e) {
								
							}
				    	}
		    		}
		    		catch (Exception e) {
						
					}
		    		
		    		try {
				    	if ("Yes".equals(md.getField("medicationsSummary").toString())) {
					    	try {		    		
					    		List<Map<String, Object>> menuDataList = cchitBean.findAllMenuDataList(MEDICATIONS_PATH, path + ":medications:current", "DateSort=DESC", activeAccountUser);
					    		
					    		for (Map<String, Object> menuData : menuDataList) {
					    			medicationsCodeSet.add(menuData.get("Code").toString());
					    		}
					    		
					    		for (String code : medicationsCodeSet) {
					    			medicationsCodes += ", " + code;
								}
					    		
					    		summaryHTML += "<tr><td><b>Medications</b></td></tr>"
												+ "<tr style=\"background-color:#EEFFFF\"><td>"
												+ (medicationsCodes.length() > 1 ? medicationsCodes.substring(2) : "Nil")
												+ "</td></tr>";
					    	}
					    	catch (Exception e) {
								
							}
				    	}
		    		}
		    		catch (Exception e) {
						
					}
		    		
		    		try {
				    	if ("Yes".equals(md.getField("allergiesSummary").toString())) {
					    	try {		    		
					    		List<Map<String, Object>> menuDataList = cchitBean.findAllMenuDataList(ALLERGIES_PATH, path + ":allergies:current", "DateSort=DESC", activeAccountUser);
					    		
					    		for (Map<String, Object> menuData : menuDataList) {
					    			allergiesCodeSet.add(menuData.get("Code").toString());
					    		}
					    		
					    		for (String code : allergiesCodeSet) {
					    			allergiesCodes += ", " + code;
								}
					    		
					    		summaryHTML += "<tr><td><b>Allergies</b></td></tr>"
												+ "<tr style=\"background-color:#EEFFFF\"><td>"
												+ (allergiesCodes.length() > 1 ? allergiesCodes.substring(2) : "Nil")
												+ "</td></tr>";
					    	}
					    	catch (Exception e) {
								
							}
				    	}
		    		}
		    		catch (Exception e) {
						
					}
		    		
		    		try {
				    	if ("Yes".equals(md.getField("proceduresSummary").toString())) {
					    	try {		    		
					    		List<Map<String, Object>> menuDataList = cchitBean.findAllMenuDataList(PROCEDURES_PATH, path + ":procedures:current", "DateSort=DESC", activeAccountUser);
					    		
					    		for (Map<String, Object> menuData : menuDataList) {
					    			proceduresCodeSet.add(menuData.get("Code").toString());
					    		}
					    		
					    		for (String code : proceduresCodeSet) {
					    			proceduresCodes += ", " + code;
								}
					    		
					    		summaryHTML += "<tr><td><b>Procedures</b></td></tr>"
												+ "<tr style=\"background-color:#EEFFFF\"><td>"
												+ (proceduresCodes.length() > 1 ? proceduresCodes.substring(2) : "Nil")
												+ "</td></tr>";
					    	}
					    	catch (Exception e) {
								
							}
				    	}
		    		}
		    		catch (Exception e) {
						
					}
		    		
//		    		try {
//				    	if ("Yes".equals(md.getField("labTestsSummary").toString())) {
//					    	try {		    		
//					    		List<Map<String, Object>> menuDataList = cchitBean.findAllMenuDataList(PROBLEMS_PATH, path + ":labtests:current", "DateSort=DESC", activeAccountUser);
//					    		
//					    		for (Map<String, Object> menuData : menuDataList) {
//					    			labTestCodeSet.add(menuData.get("Code").toString());
//					    		}
//					    		
//					    		for (String code : labTestCodeSet) {
//					    			labTestCodes += ", " + code;
//								}
//					    		
//					    		summaryHTML += "<tr><td><b>Problem</b></td></tr>"
//												+ "<tr style=\"background-color:#EEFFFF\"><td>"
//												+ (labTestCodes.length() > 1 ? labTestCodes.substring(2) : "Nil")
//												+ "</td></tr>";
//					    	}
//					    	catch (Exception e) {
//								
//							}
//				    	}
//		    		}
//		    		catch (Exception e) {
//						
//					}
		    		
		    	}
		    	catch (Exception e) {
					
				}
		    	
		    	if (summaryHTML.length() > 0) {
		    		writer.write("<table width=\"100%\">" 
			    					+ summaryHTML
									+ "</table>");
		    	}
		    	else {
		    		writer.write("<table width=\"100%\">" 
			    					+ "<tr><td>No items selected</td></tr>"
									+ "</table>");
		    	}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * To enable/disable 'create PHR' option for component S & T,
		 * if 'primary email' is valid user to LDAP.
		 * 
		 * @author Valsaraj
		 * added on 06/15/2010
		 */
		else if (uri.endsWith("enablePHRCreationAndSharing.ajaxcchit")) {
			
				/*String email = req.getParameter("email");
				TolvenUser user = cchitActivationBean.findUser(email);
				
				if (user != null) {
					String title = "ephr-" + user.getId();
					List<Account> accountLists = cchitActivationBean.getAccountsByTitleAndUser(title, user, 3);
					
					if (accountLists.size() > 0) {						
						writer.write(Long.toString(accountLists.get(0).getId()));
					}
				}
			}*/
			throw new ServletException("Fix the commented code in "+this.getClass()+" at line:436");
			
		}
		/**
		 * To export all data associated with a person in either an Excel or CSV format to a remote device.
		 * 
		 * @author Valsaraj
		 * added on 06/28/2010
		 */
		else if (uri.endsWith("exportData.ajaxcchit")) {
			try {
				String path = req.getParameter("path");
				List<ActRelationship> diagnosisList = new ArrayList<ActRelationship>();
				List<ActRelationship> treatmentsList = new ArrayList<ActRelationship>();
				List<ActRelationship> medicationsList = new ArrayList<ActRelationship>();
				List<ActRelationship> referralsList = new ArrayList<ActRelationship>();
				List<ActRelationship> testOrdersList = new ArrayList<ActRelationship>();
				
				try {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("menuPath", "ephr:patient:progressnotes:current");
					params.put("contextPath", path + ":progressnotes:current");
					params.put("conditions", "TitleFilter=Progress Notes:DateSort=DESC");
					params.put("accountUser", activeAccountUser);
					TrimEx trimEx = cchitBean.findTrimData(params,userPrivateKey);
					
					if (trimEx != null) {
						try {
							diagnosisList = ((ActEx) ((ActEx) ((ActEx) trimEx.getAct()).getRelationship().get("assessment").getAct()).getRelationship().get("diagnoses").getAct()).getRelationshipsList().get("diagnosis");
						}
						catch (Exception e) {
							
						}
						
						try {
							ActEx actEx = (ActEx) ((ActEx)trimEx.getAct()).getRelationship().get("plan").getAct();
						
							try {
								treatmentsList = ((ActEx) actEx.getRelationship().get("treatments").getAct()).getRelationshipsList().get("treatment");
							}
							catch (Exception e) {
								
							}

							try {
								referralsList = ((ActEx) actEx.getRelationship().get("referrals").getAct()).getRelationshipsList().get("referral");
							}
							catch (Exception e) {
								
							}
							
							try {
								testOrdersList = ((ActEx) actEx.getRelationship().get("testOrders").getAct()).getRelationshipsList().get("testOrder");
							}
							catch (Exception e) {
								
							}
						}
						catch (Exception e) {
							
						}
						
						try {
							medicationsList = ((ActEx) ((ActEx) trimEx.getAct()).getRelationship().get("medications").getAct()).getRelationshipsList().get("medication");
						}
						catch (Exception e) {
							
						}
					}
				}
				catch (Exception e) {
					
				}
				
				int diagnosisListSize = diagnosisList.size();
				int treatmentsListSize = treatmentsList.size();
				int medicationsListSize = medicationsList.size();
				int referralsListSize = referralsList.size();
				int testOrdersListSize = testOrdersList.size();				
				int maxCount = diagnosisList.size();
				
				if (maxCount < treatmentsListSize) {
					maxCount = treatmentsListSize;
				}
				
				if (maxCount < medicationsListSize) {
					maxCount = medicationsListSize;
				}
				
				if (maxCount < referralsListSize) {
					maxCount = referralsListSize;
				}
				
				if (maxCount < testOrdersListSize) {
					maxCount = testOrdersListSize;
				}
				
				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-Disposition", "attachment; filename=\"patient-data.csv\"");
				StringBuffer buffer = new StringBuffer();
				buffer.append("Diagnosis,Treatments,Medications,Referrals,Test Orders and Results");
				buffer.append("\n");
				buffer.append("\n");
				
				for (int iter = 0; iter < maxCount; iter++) {
					if (iter < diagnosisListSize) {
						buffer.append(diagnosisList.get(iter).getAct().getObservation().getValues().get(0).getCE().getDisplayName());
					}
					
					buffer.append(",");
					
					if (iter < treatmentsListSize) {
						try {
							buffer.append(treatmentsList.get(iter).getAct().getCode().getCE().getDisplayName());
						}
						catch (Exception e) {
							
						}
					}
					
					buffer.append(",");
					
					if (iter < medicationsListSize) {
						try {
							buffer.append(medicationsList.get(iter).getAct().getObservation().getValues().get(0).getST().getValue());
						}
						catch (Exception e) {
							
						}
					}
					
					buffer.append(",");
					
					if (iter < referralsListSize) {
						try {
							buffer.append(referralsList.get(iter).getAct().getCode().getCE().getDisplayName());
						}
						catch (Exception e) {
							
						}
					}
					
					buffer.append(",");
					
					if (iter < testOrdersListSize) {
						try {
							buffer.append(testOrdersList.get(iter).getAct().getCode().getCE().getDisplayName());
						}
						catch (Exception e) {
							
						}
					}
					
					buffer.append("\n");
				}
				
				writer.write(buffer.toString());
			}
			catch (Exception e) {
				
			}
		}
		/**
		 * This function is used to save the relationship for problem in the trim on selecting the problem from the list.
		 * In Associate with problem - drop-down list - Orders,Procedures.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveOrderAssociateProblems.ajaxcchit")) { 
	    	String element = req.getParameter( "element");
			String template = req.getParameter("template");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			ActRelationship proRel= ((ActEx)wizardTrim.getAct()).getRelationship().get("problems");
			ActEx act=((ActEx)proRel.getAct());
			ActRelationship rel = trimFactory.createActRelationship();
			rel.setTypeCode(proRel.getTypeCode());
			rel.setDirection(proRel.getDirection());
			rel.setName("problem");
			System.out.println("create......");
		    act.getRelationships().add(rel); 
			
			try {
				if (template!=null) {
					TrimEx templateTrim=trimBean.findTrim(template);
					rel.setAct(templateTrim.getAct());
					rel.getAct().getTitle().setST(trimFactory.createNewST("Problem"));
					rel.setEnabled(true);
					rel.getAct().getComputes().clear();
					if (rel.getAct().getParticipations().size()>1)
						rel.getAct().getParticipations().remove(1);
				}
				int index = ((ActEx)proRel.getAct()).getRelationships().size();
				creatorBean.marshalToDocument( wizardTrim, docXML );
				writer.write("Success");
				writer.write("|");
				writer.write(""+index);
	    		req.setAttribute("activeWriter", writer);
	    		return;
		    } catch (NumberFormatException e) {
				writer.write( "Invalid input entered by user");
				req.setAttribute("activeWriter", writer);
				return;
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
	    }
		
		/**
		 * This function is used to save the relationship for problem in the trim on selecting the problem from the list.
		 * In Associate with problem - drop-down list - Orders,Procedures.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveOrderAssociateDiagnoses.ajaxcchit")) { 
	    	String element = req.getParameter( "element");
			String template = req.getParameter("template");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			ActRelationship proRel= ((ActEx)wizardTrim.getAct()).getRelationship().get("diagnoses");
			ActEx act=((ActEx)proRel.getAct());
			ActRelationship rel = trimFactory.createActRelationship();
			rel.setTypeCode(proRel.getTypeCode());
			rel.setDirection(proRel.getDirection());
			rel.setName("diagnose");
			System.out.println("create......");
		    act.getRelationships().add(rel); 
			
			try {
				if (template!=null) {
					TrimEx templateTrim=trimBean.findTrim(template);
					rel.setAct(templateTrim.getAct());
					rel.getAct().getComputes().clear();
					if (rel.getAct().getParticipations().size()>1)
						rel.getAct().getParticipations().remove(1);
				}
				int index = ((ActEx)proRel.getAct()).getRelationships().size();
				creatorBean.marshalToDocument( wizardTrim, docXML );
				writer.write("Success");
				writer.write("|");
				writer.write(""+index);
	    		req.setAttribute("activeWriter", writer);
	    		return;
		    } catch (NumberFormatException e) {
				writer.write( "Invalid input entered by user");
				req.setAttribute("activeWriter", writer);
				return;
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
	    }
		/**
		 * This function is used to remove the relationship for problem and diagnoses from the trim if the pop-up is canceled.
		 * In Associate with problem - drop-down list - Orders,Procedures.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("removeUnsaved.ajaxcchit")) {
			String element = req.getParameter( "element");
			int index = Integer.parseInt(req.getParameter("index"));
			String relName = req.getParameter( "relName");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get(relName).getAct()).getRelationships().remove(index-1);
			
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		/**
		 * This function is used to save the details of the problem.
		 * In Associate with problem - drop-down list - Orders,Procedures.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveOrderAssociateProblemDetails.ajaxcchit")) {
			String element = req.getParameter( "element");
			int index = Integer.parseInt(req.getParameter("index"));
			String severity = req.getParameter( "severity");
			String course = req.getParameter( "course");
			String outcome = req.getParameter( "outcome");
			String treatment = req.getParameter( "treatment");
			String comments = req.getParameter( "comments");
			String onset = req.getParameter( "effectiveTime");
			Date time = null;
			if(!onset.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(onset);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			ActRelationship rel = ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("problems").getAct()).getRelationships().get(index-1);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("severity").getAct().getObservation().getValues().get(0).setCE(getCE("severity", severity, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("course").getAct().getObservation().getValues().get(0).setCE(getCE("course", course, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("outcome").getAct().getObservation().getValues().get(0).setCE(getCE("outcome", outcome, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("treatment").getAct().getText().getST().setValue(treatment);
			((ActEx)rel.getAct()).getRelationship().get("comments").getAct().getText().getST().setValue(comments);
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		
		/**
		 * This function is used to save the details of the diagnoses.
		 * In Associate with problem - drop-down list - Orders,Procedures.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveOrderAssociateDiagnosesDetails.ajaxcchit")) {
			String element = req.getParameter( "element");
			int index = Integer.parseInt(req.getParameter("index"));
			String severity = req.getParameter( "severity");
			String onset = req.getParameter( "onset");
			String course = req.getParameter( "course");
			String onsetDate = req.getParameter( "effectiveTime");
			Date time = null;
			if(!onset.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(onsetDate);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			ActRelationship rel = ((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("diagnoses").getAct()).getRelationships().get(index-1);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("severity").getAct().getObservation().getValues().get(0).setCE(getCE("severity", severity, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("course").getAct().getObservation().getValues().get(0).setCE(getCE("course", course, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("onset").getAct().getObservation().getValues().get(0).setCE(getCE("outcome", onset, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("episodicity").getAct().getObservation().getValues().get(0).setCE(getCE("episodicity", onset, wizardTrim));
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		/**
		 * Returns PHR accounts of a user separated by '|'. 
		 * 
		 * @author Valsaraj
		 * added on 07/12/2010
		 */
		else if (uri.endsWith("getUsersPHRAccountIds.ajaxcchit")) {
				String email = req.getParameter("email");
				
				/*TolvenUser user = cchitActivationBean.findUser(email);
				  if (user != null) {		
					List<Account> accountLists = cchitActivationBean.getAccountsByUser(user, accountBean.findAccountTypebyKnownType("ephr").getId());
					String accountStr = "";
					
					for (Account account : accountLists) {
						accountStr += account.getTitle() + " (" + account.getId() + ")|";
					}
					
					int len = accountStr.length();
					
					if (len > 0) {
						accountStr = accountStr.substring(0, len - 1);
					}
					
					writer.write(accountStr);
				}*/
				throw new ServletException("Fix the commented code in "+this.getClass()+" at line:870");
		}
		
		/**
		 * This function is used to retrieve the details of the item, that we select from the tpf pop-up.
		 * In Orders,Procedures,Problems,Diagnosis.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveOrders.ajaxcchit")) { 
			String element = req.getParameter( "element");
			String template = req.getParameter("template");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			try {
				TrimEx templateTrim = null;
				if (template!=null) {
					templateTrim=trimBean.findTrim(template);
				}
				int index = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
				creatorBean.marshalToDocument( wizardTrim, docXML );
				writer.write("Success");
				writer.write("|");
				writer.write(""+(index));
				writer.write("|");
				writer.write(template);
				writer.write("|");
				writer.write(templateTrim.getDescription());
	    		req.setAttribute("activeWriter", writer);
	    		return;
		    } catch (NumberFormatException e) {
				writer.write( "Invalid input entered by user");
				req.setAttribute("activeWriter", writer);
				return;
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
		}
		
		/**
		  * Ajax call to get Diagnosis name
		  * 
		  * added on 29/Dec/2010
		  * @author Khalid
		  * @param template - Diagnosis trim name
		  * @return  templateTrim - diagnosis trim name
		 */
		else if (uri.endsWith("getDiagnosisName.ajaxcchit")) { 
			String template = req.getParameter("template");
			try {
				TrimEx templateTrim = null;
				if (template!=null) {
					templateTrim=trimBean.findTrim(template);
				}	
				writer.write("Success");
				writer.write("|");
				writer.write(templateTrim.getDescription());
	    		req.setAttribute("activeWriter", writer);
	    		return;
		    } catch (NumberFormatException e) {
		    	writer.write("failure");
				req.setAttribute("activeWriter", writer);
				return;
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		
		
		/**
		 * This function is used to remove the relationship.
		 * In Orders,Procedures,Problems,Diagnosis.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("removeTpfRel.ajaxcchit")) {
			String element = req.getParameter( "element");
			int index = Integer.parseInt(req.getParameter("index"));
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}

			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			((ActEx)wizardTrim.getAct()).getRelationship().get("submitStatus").getAct().getObservation().getValues().get(0).
			getINT().setValue(size-1);
			((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").remove(index);
			ActEx act=((ActEx)wizardTrim.getAct());
			((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
					
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
	
		/**
		 * This function is used to save the details of the immunization order.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveTpfRel.ajaxcchit")) { 
			String element = req.getParameter( "element");
			int index = 0;
			if(!req.getParameter("index").equals(""))
				index = Integer.parseInt(req.getParameter("index"));
			String problem = req.getParameter( "problem");
			String diagnosis = req.getParameter( "diagnosis");
			String date = req.getParameter( "date");
			String template = req.getParameter( "template");
			Date time = null;
			if(!date.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(date);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			String reason = req.getParameter("reason");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			ActRelationship rel = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				
				if(req.getParameter("index").equals("")){
					rel = trimFactory.createActRelationship();
					rel.setTypeCode(wizardTrim.getAct().getRelationships().get(0).getTypeCode());
					rel.setDirection(wizardTrim.getAct().getRelationships().get(0).getDirection());
					rel.setName("entry");
					System.out.println("create......");
					wizardTrim.getAct().getRelationships().add(rel);
					
					if (template!=null) {
						TrimEx templateTrim = trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.setEnabled(true);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}

			if(!req.getParameter("index").equals(""))
				rel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(index);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(diagnosis));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(problem));
			((ActEx)rel.getAct()).getRelationship().get("immunizationOrderInfo").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(reason));
			
			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(""+(size-1));
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		
		/**
		 * This function is used to save the details of the Laboratory order.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveLabTpfRel.ajaxcchit")) { 
			String element = req.getParameter( "element");
			int index = 0;
			if(!req.getParameter("index").equals(""))
				index = Integer.parseInt(req.getParameter("index"));
			String problem = req.getParameter( "problem");
			String diagnosis = req.getParameter( "diagnosis");
			String specimenType = req.getParameter( "specimenType");
			String date = req.getParameter( "date");
			String conatiner = req.getParameter( "conatiner");
			String template = req.getParameter( "template");
			Date time = null;
			if(!date.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(date);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			String reason = req.getParameter("reason");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			ActRelationship rel = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				if(req.getParameter("index").equals("")){
					rel = trimFactory.createActRelationship();
					rel.setTypeCode(wizardTrim.getAct().getRelationships().get(0).getTypeCode());
					rel.setDirection(wizardTrim.getAct().getRelationships().get(0).getDirection());
					rel.setName("entry");
					System.out.println("create......");
					wizardTrim.getAct().getRelationships().add(rel);
					
					if (template!=null) {
						TrimEx templateTrim = trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.setEnabled(true);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			if(!req.getParameter("index").equals(""))
				rel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(index);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(diagnosis));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(problem));
			((ActEx)rel.getAct()).getRelationship().get("labOrderInfo").getAct().getObservation().getValues().get(1).setCE((CE) trimFactory.stringToDataType(specimenType));
			((ActEx)rel.getAct()).getRelationship().get("labOrderInfo").getAct().getObservation().getValues().get(2).setCE((CE) trimFactory.stringToDataType(conatiner));
			((ActEx)rel.getAct()).getRelationship().get("labOrderInfo").getAct().getObservation().getValues().get(3).setST(trimFactory.createNewST(reason));
			
			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(""+(size-1));
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		
		/**
		 * This function is used to save the details of the image order.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveImageTpfRel.ajaxcchit")) { 
			String element = req.getParameter( "element");
			int index = 0;
			if(!req.getParameter("index").equals(""))
				index = Integer.parseInt(req.getParameter("index"));
			String problem = req.getParameter( "problem");
			String diagnosis = req.getParameter( "diagnosis");
			String date = req.getParameter( "date");
			String template = req.getParameter( "template");
			Date time = null;
			if(!date.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(date);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			String reason = req.getParameter("reason");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			ActRelationship rel = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				if(req.getParameter("index").equals("")){
					rel = trimFactory.createActRelationship();
					rel.setTypeCode(wizardTrim.getAct().getRelationships().get(0).getTypeCode());
					rel.setDirection(wizardTrim.getAct().getRelationships().get(0).getDirection());
					rel.setName("entry");
					System.out.println("create......");
					wizardTrim.getAct().getRelationships().add(rel);
					
					if (template!=null) {
						TrimEx templateTrim = trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.setEnabled(true);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			if(!req.getParameter("index").equals(""))
				rel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(index);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(diagnosis));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(problem));
			((ActEx)rel.getAct()).getRelationship().get("imageOrderInfo").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(reason));
	
			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(""+(size-1));
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		
		/**
		 * This function is used to save the details of the referral request.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveReferralTpfRel.ajaxcchit")) { 
			String element = req.getParameter( "element");
			int index = 0;
			if(!req.getParameter("index").equals(""))
				index = Integer.parseInt(req.getParameter("index"));
			String problem = req.getParameter( "problem");
			String diagnosis = req.getParameter( "diagnosis");
			String date = req.getParameter( "date");
			String template = req.getParameter( "template");
			Date time = null;
			if(!date.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(date);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			String reason = req.getParameter("reason");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			ActRelationship rel = null;
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				if(req.getParameter("index").equals("")){
					rel = trimFactory.createActRelationship();
					rel.setTypeCode(wizardTrim.getAct().getRelationships().get(0).getTypeCode());
					rel.setDirection(wizardTrim.getAct().getRelationships().get(0).getDirection());
					rel.setName("entry");
					System.out.println("create......");
					wizardTrim.getAct().getRelationships().add(rel);
					
					if (template!=null) {
						TrimEx templateTrim = trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.setEnabled(true);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			if(!req.getParameter("index").equals(""))
				rel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(index);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(diagnosis));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(problem));
			((ActEx)rel.getAct()).getRelationship().get("referralRequestInfo").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(reason));
	
			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(""+(size-1));
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		
		/**
		 * This function is used to save the details of the problem.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveProbRel.ajaxcchit")) { 
			String element = req.getParameter( "element");
			int index = 0;
			if(!req.getParameter("index").equals(""))
				index = Integer.parseInt(req.getParameter("index"));
			String severity = req.getParameter( "severity");
			String course = req.getParameter( "course");
			String outcome = req.getParameter( "outcome");
			String treatment = req.getParameter( "treatment");
			String comments = req.getParameter( "comments");
			String date = req.getParameter( "date");
			String template = req.getParameter( "template");
			
			Date time = null;
			if(!date.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(date);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			ActRelationship rel = null;
			
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				if(req.getParameter("index").equals("")){
					rel = trimFactory.createActRelationship();
					rel.setTypeCode(wizardTrim.getAct().getRelationships().get(0).getTypeCode());
					rel.setDirection(wizardTrim.getAct().getRelationships().get(0).getDirection());
					rel.setName("entry");
					System.out.println("create......");
					wizardTrim.getAct().getRelationships().add(rel);
					
					if (template!=null) {
						TrimEx templateTrim = trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.setEnabled(true);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			if(!req.getParameter("index").equals(""))
				rel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(index);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("severity").getAct().getObservation().getValues().get(0).setCE((CE) trimFactory.stringToDataType(severity));
			((ActEx)rel.getAct()).getRelationship().get("course").getAct().getObservation().getValues().get(0).setCE((CE) trimFactory.stringToDataType(course));
			((ActEx)rel.getAct()).getRelationship().get("outcome").getAct().getObservation().getValues().get(0).setCE((CE) trimFactory.stringToDataType(outcome));
			((ActEx)rel.getAct()).getRelationship().get("treatment").getAct().getText().setST(trimFactory.createNewST(treatment));
			((ActEx)rel.getAct()).getRelationship().get("comments").getAct().getText().setST(trimFactory.createNewST(comments));
			
			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			((ActEx)wizardTrim.getAct()).getRelationship().get("submitStatus").getAct().getObservation().getValues().get(0).getINT().setValue(size);
			
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML);
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(""+(size-1));
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		/**
		 * Used to ad save Drug Allergy details.
		 * @author pnparam
		 * Added on: 06/09/2010 
		 * modified on 02/14/2011 to change the status messages.
		 * @author valsaraj
		 */
		else if (uri.endsWith("saveUpdateAllergyDetail.ajaxcchit")) {
			String message = "initialize";
			try{
				String element = req.getParameter( "element");
				String templateName = req.getParameter( "template");
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String reaction = req.getParameter( "reaction");
				String severity = req.getParameter( "severity");
				
				// Note: We're reading this in from a mutable document.
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				// Unmarshall the trim
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				ActRelationship allergyDetailsRel = null;
				ActEx act = (((ActEx)((ActEx)wizardTrim.getAct()).getRelationship().get("allergyDetailsStore").getAct()));
				
				if (actionType == 0) {
			    	message = "Allergy has been added ...";
					TrimEx _trimEx = trimBean.findTrim(templateName);
			    	Object obj = ((ActEx)_trimEx.getAct()).getRelationship().get("allergyDetail");
			    	allergyDetailsRel = (ActRelationship)obj;
			    	act.getRelationships().add(allergyDetailsRel); // create new lesion
					allergyDetailsRel.setSourceTrim(templateName);
					ActRelationship destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("reaction");
			    	if(reaction !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("reaction", reaction, wizardTrim));
			    	}
			    	destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("severity");
			    	if(severity !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("severity", severity, wizardTrim));
			    	}
				} else if (actionType==1) {
			    	message = "Allergy has been updated ...";
					int index = Integer.parseInt(req.getParameter("index"));
					allergyDetailsRel = act.getRelationshipsList().get("allergyDetail").get(index);
					if (! templateName.equalsIgnoreCase(allergyDetailsRel.getSourceTrim())) {
						act.getRelationshipsList().get("allergyDetail").remove(index);
						((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
						TrimEx _trimEx = trimBean.findTrim(templateName);
				    	Object obj = ((ActEx)_trimEx.getAct()).getRelationship().get("allergyDetail");
				    	act.getRelationships().add(index,(ActRelationship)obj);
				    	allergyDetailsRel = (ActRelationship) obj;
					}
					allergyDetailsRel.setSourceTrim(templateName);
					ActRelationship destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("reaction");
			    	if(reaction !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("reaction", reaction, wizardTrim));
			    	}
			    	destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("severity");
			    	if(severity !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("severity", severity, wizardTrim));
			    	}
				} else if (actionType==2) {
					message = "Allergy has been removed ...";
					int index = Integer.parseInt(req.getParameter("index"));
					allergyDetailsRel = act.getRelationshipsList().get("allergyDetail").get(index);
					act.getRelationshipsList().get("allergyDetail").remove(index);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
				} 
		    	try {
			    	// marshall the trim
					creatorBean.marshalToDocument( wizardTrim, docXML );				
		    		writer.write("{success: true, message: '" + message + "'}");
		    	} catch (Exception e) {
		    		TolvenLogger.debug("Exception during (Marshall Trim) "+message, e, getClass());
		    		writer.write("{success: false, message: '" + message + " failed. Please contact support.', error: '"+e.getMessage().replaceAll("'", "\"")+"'}");
		    	}
			} catch (Exception e) {
	    		TolvenLogger.debug("Exception during " + message, e, getClass());
	    		writer.write("{success: false, message: '" + message + " failed. Please contact support.', error: '"+e.getMessage().replaceAll("'", "\"")+"'}");
			}
			
    		req.setAttribute("activeWriter", writer);
		}
		/**
		 * This function is used to save the details of the procedure order.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveProcedureRel.ajaxcchit")) { 
			String element = req.getParameter( "element");
			int index = 0;
			if(!req.getParameter("index").equals(""))
				index = Integer.parseInt(req.getParameter("index"));
			String problem = req.getParameter( "problem");
			String diagnosis = req.getParameter( "diagnosis");
			String date = req.getParameter( "date");
			String template = req.getParameter( "template");
			Date time = null;
			if(!date.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(date);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			String reason = req.getParameter("reason");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			ActRelationship rel = null;
			
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				if(req.getParameter("index").equals("")){
					rel = trimFactory.createActRelationship();
					rel.setTypeCode(wizardTrim.getAct().getRelationships().get(0).getTypeCode());
					rel.setDirection(wizardTrim.getAct().getRelationships().get(0).getDirection());
					rel.setName("entry");
					System.out.println("create......");
					wizardTrim.getAct().getRelationships().add(rel);
					
					if (template!=null) {
						TrimEx templateTrim = trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.setEnabled(true);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			if(!req.getParameter("index").equals(""))
				rel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(index);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(diagnosis));
			((ActEx)rel.getAct()).getRelationship().get("orderAssociations").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(problem));
			((ActEx)rel.getAct()).getRelationship().get("procedureInfo").getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(reason));
	
			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(""+(size-1));
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		
		/**
		 * This function is used to save the details of the diagnosis.
		 * @author Pinky
		 * added on 07/08/2010
		 */
		else if (uri.endsWith("saveDiagRel.ajaxcchit")) { 
			String element = req.getParameter( "element");
			int index = 0;
			if(!req.getParameter("index").equals(""))
				index = Integer.parseInt(req.getParameter("index"));
			String severity = req.getParameter( "severity");
			String onset = req.getParameter( "onset");
			String course = req.getParameter( "course");
			String episodicity = req.getParameter( "episodicity");
			String date = req.getParameter( "date");
			String template = req.getParameter( "template");
			Date time = null;
			if(!date.equals("")){
				//onset.
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				try {
					time = df.parse(date);
				} catch (ParseException e2) {
					e2.printStackTrace();
				}
			}
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;
			ActRelationship rel = null;
			
			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				if(req.getParameter("index").equals("")){
					rel = trimFactory.createActRelationship();
					rel.setTypeCode(wizardTrim.getAct().getRelationships().get(0).getTypeCode());
					rel.setDirection(wizardTrim.getAct().getRelationships().get(0).getDirection());
					rel.setName("entry");
					System.out.println("create......");
					wizardTrim.getAct().getRelationships().add(rel);
					
					if (template!=null) {
						TrimEx templateTrim = trimBean.findTrim(template);
						rel.setAct(templateTrim.getAct());
						rel.setEnabled(true);
						rel.getAct().getComputes().clear();
						if (rel.getAct().getParticipations().size()>1)
							rel.getAct().getParticipations().remove(1);
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
			
			if(!req.getParameter("index").equals(""))
				rel = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(index);
			rel.getAct().getEffectiveTime().setTS(trimFactory.createNewTS(time));
			((ActEx)rel.getAct()).getRelationship().get("severity").getAct().getObservation().getValues().get(0).setCE(getCE("severity", severity, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("course").getAct().getObservation().getValues().get(0).setCE(getCE("course", course, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("onset").getAct().getObservation().getValues().get(0).setCE(getCE("onset", onset, wizardTrim));
			((ActEx)rel.getAct()).getRelationship().get("episodicity").getAct().getObservation().getValues().get(0).setCE(getCE("episodicity", episodicity, wizardTrim));
	
			int size = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").size();
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(""+(size-1));
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		
		/**
		 * This function is used to get the code of the selected state from the state name.
		 * Patient trim.
		 * @author Pinky
		 * added on 07/22/2010
		 */
		else if (uri.endsWith("saveStateCode.ajaxcchit")) { 
			String element = req.getParameter( "element");
			String name = req.getParameter( "name");
			String originalText = null;
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;

			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				List<Object> stateVS = wizardTrim.getValueSet().get("states").getBindsAndADSAndCDS();
				int items = wizardTrim.getValueSet().get("states").getBindsAndADSAndCDS().size();
				for (int i = 0; i < items; i++) {
					if (((CEEx) stateVS.get(i)).getDisplayName().equals(name)){
						originalText = ((CEEx) stateVS.get(i)).getOriginalText();
						break;
					}
				}
				

			} catch (JAXBException e) {
				e.printStackTrace();
			}
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(originalText);
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}		
		/**
		 * This function is used to get the name of the selected state from the state code.
		 * Patient wizard.
		 * @author Pinky
		 * added on 07/22/2010
		 */
		else if (uri.endsWith("retrieveStateName.ajaxcchit")) { 
			String element = req.getParameter( "element");
			String code = req.getParameter( "code");
			String originalName = null;
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
			String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
			TrimEx wizardTrim = null;

			try {
				wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				List<Object> stateVS = wizardTrim.getValueSet().get("states").getBindsAndADSAndCDS();
				int items = wizardTrim.getValueSet().get("states").getBindsAndADSAndCDS().size();
				for (int i = 0; i < items; i++) {
					if (((CEEx) stateVS.get(i)).getOriginalText().equals(code)){
						originalName = ((CEEx) stateVS.get(i)).getDisplayName();
						break;
					}
				}
				

			} catch (JAXBException e) {
				e.printStackTrace();
			}
			try {
				creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (TRIMException e) {
				e.printStackTrace();
			}
			writer.write("Success");
			writer.write("|");
			writer.write(originalName);
	    	req.setAttribute("activeWriter", writer);
	    	return;
		}
		/**
		 * This function is used to refresh a list by submitting all trims related to that list.
		 * @author valsaraj
		 * added on 08/03/2010
		 */
		else if (uri.endsWith("refreshList.ajaxcchit")) {
			String listpath = req.getParameter("listpath");
			
			try {
				List<Map<String, Object>> patientMenuDataList = cchitBean.findAllMenuDataList(listpath, listpath, "DateSort=DESC", activeAccountUser);
	    		
	    		for (Map<String, Object> patientDataMap : patientMenuDataList) {
	    			try {
		    			String menuElement = patientDataMap.get("path").toString();
						
		    			if (menuElement == null) {
							throw new IllegalArgumentException("Missing element in TRIM request");
		    			}
		    			
						TolvenLogger.info("[instantiateTrim] account=" + activeAccountUser.getAccount().getId() + ", element=" + menuElement+ ", action: reviseActive", this.getClass());
						MenuData mdPrior = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), menuElement);
						
						//if(mdPrior.getString01().startsWith("kp6"))
						//{
						
							long docId=mdPrior.getDocumentId();
							if(docId>0)
							{
								DocBase doc=docBean.findDocument(docId);
								
								if(!(doc instanceof org.tolven.doc.entity.DocCCR) )
								{
									MenuData mdNew = creatorBean.createTRIMEvent(mdPrior, activeAccountUser, "reviseActive", now,userPrivateKey);
									creatorBean.submit(mdNew, activeAccountUser,userPrivateKey);
								}
							}
						//}
		    		} 
					catch (Exception e) {
						e.printStackTrace();
					}
	    		}
			}
    		catch (Exception e) {
    			writer.write("Fail");
			}
	    		
			writer.write("Success");
	    	return;
		}
		/**
		 * saves the selected FDB medication to trim
		 * @author Nevin
		 * added on 12/03/2010 
		 */
		else if (uri.endsWith("saveFDBMedication.ajaxcchit")) {
			String element = req.getParameter("element");
			String medName = req.getParameter("medicationName");
			String medCode = req.getParameter("medicationCode");
			String drugNameType = req.getParameter("drugNameType");
			String template = "accountListItemTemplate";
			try {
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
		     	TrimEx trimEx = trimBean.findTrim(template);
		     	ActRelationship ar = ((ActEx)trimEx.getAct()).getRelationship().get("favoriteItem");
		     	ar.getAct().getTitle().setST(trimFactory.createNewST(medName));
		     	ar.getAct().getObservation().getValues().get(0).setST(trimFactory.createNewST(medCode));
		     	ar.getAct().getObservation().getValues().get(1).setST(trimFactory.createNewST(drugNameType));
		     	wizardTrim.getAct().getRelationships().add(ar);		 
		     	creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (Exception e) {
				e.printStackTrace();
				writer.write("Fail");
			}
			writer.write("Success");
	    	return;
		}
		/**
		 * removes the selected FDB medication to trim
		 * @author Nevin
		 * added on 12/03/2010 
		 */
		else if (uri.endsWith("removeFDBMedication.ajaxcchit")) {
			String element = req.getParameter("element");
			String medCode = req.getParameter("medicationCode");
			try {
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				List<ActRelationship> favouritesList = ((ActEx)wizardTrim.getAct()).getRelationshipsList().get("favoriteItem");
				for (ActRelationship medRelation : favouritesList) {
					if (medRelation.getAct().getObservation().getValues().get(0).getST().getValue().equals(medCode)) {
						wizardTrim.getAct().getRelationships().remove(medRelation);
						break;
					}
				}
		     	creatorBean.marshalToDocument( wizardTrim, docXML );
			} catch (Exception e) {
				e.printStackTrace();
				writer.write("Fail");
			}
			writer.write("Success");
	    	return;
		}
		/**
		 * to get the Favorite medications under a Favorite medication list
		 * @author Nevin
		 * added on 12/03/2010 
		 */
		else if (uri.endsWith("getFavouriteMedications.ajaxcchit")) {
			String path = req.getParameter("path");
			String element = req.getParameter("element");
			String formId = req.getParameter("formId");
			String popupSource = req.getParameter("popupSource");
			Long id = Long.parseLong(path.split("-")[1]);
			String classType = "odd";
			try {
				TrimEx trimEx = cchitBean.findTrimData(id, activeAccountUser,userPrivateKey);
				List<ActRelationship> favoriteItemsList = ((ActEx)trimEx.getAct()).getRelationshipsList().get("favoriteItem");
				StringBuffer sBuffer = new StringBuffer();
				
				if (popupSource.equals("MedicationHistory")) {
					sBuffer.append("<table width=\"100%\" class=\"gridBody\" id=\""+element+":drugTable\"><tbody>");
					for (ActRelationship item : favoriteItemsList) {
						try {
							String medicationName = item.getAct().getTitle().getST().getValue();
							String medicationCode = item.getAct().getObservation().getValues().get(0).getST().getValue();
							sBuffer.append("<tr class=\""+classType+"\">");
							sBuffer.append("<td>"+medicationCode+"</td>");
							sBuffer.append("<td><span style=\"text-decoration: underline; cursor: pointer;\" onclick=\"loadFDBMedicationDetails('"+medicationName+"','"+medicationCode+"')\">"+medicationName+"</span></td>");
							sBuffer.append("</tr>");
							if (classType.equals("odd")) {
								classType = "even";
							} else {
								classType = "odd";
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					sBuffer.append("</tbody></table>");
				} else if (popupSource.equals("NewPrescription")) {
					sBuffer.append("<table width=\"100%\" class=\"gridBody\" id=\""+element+":drugTable\"><tbody>");
					for (ActRelationship item : favoriteItemsList) {
						try {
							String medicationName = item.getAct().getTitle().getST().getValue();
							String medicationCode = item.getAct().getObservation().getValues().get(0).getST().getValue();
							String drugNameType = "";
							if (item.getAct().getObservation().getValues().get(1) != null && item.getAct().getObservation().getValues().get(1).getST() != null) {
								drugNameType = item.getAct().getObservation().getValues().get(1).getST().getValue();
							}
							sBuffer.append("<tr class=\""+classType+"\">");
							sBuffer.append("<td><span style=\"cursor: pointer; text-decoration: underline;\" onclick=\"saveCode(this , '"+formId+"'  ,");
							sBuffer.append("'"+element+"')\">"+medicationCode+"</span></td>");
							sBuffer.append("<td><span style=\"cursor: pointer; text-decoration: underline;\" onclick=\"saveDrug(this , '"+formId+"'  ,");
							sBuffer.append("'"+element+"')\">"+medicationName+"</span></td>");
							sBuffer.append("<td>"+drugNameType+"</td>");
							sBuffer.append("</tr>");
							if (classType.equals("odd")) {
								classType = "even";
							} else {
								classType = "odd";
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					sBuffer.append("</tbody></table>");
				}
				
				writer.write(sBuffer.toString());
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		/**
		 * On Vital Signs'Revise' action - 
		 * setting the current trim as nullified and 
		 * created a new trim with active status. 
		 * @author Vineetha
		 * added on 12/28/2010
		 * modified on 02/17/2011 
		 * Cannot revise observations, fixed the bug.
		 * @author valsaraj
		 */		
		else if(uri.endsWith("reviseVitalSign.ajaxcchit")){
			try{
				String action = req.getParameter( "action");
				String element = req.getParameter("element");
				String patientId = element.split("-")[1].split(":")[0]; 
				String path="echr:patient-" + patientId + ":observations:active";
				if (element == null ) throw new IllegalArgumentException( "Missing element in TRIM request");
				if (action == null ) throw new IllegalArgumentException( "Missing action in TRIM transition request");
				TolvenLogger.info( "[reviseVitalSign] account=" + activeAccountUser.getAccount().getId() + ", element=" + element + ", action: " + action, CCHITServlet.class);
				// Setting the currrent trim as nullified
				MenuData mdPrior  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				MenuData mdNew = creatorBean.createTRIMEvent(mdPrior, activeAccountUser, action, now,userPrivateKey);
				creatorBean.submit(mdNew, activeAccountUser,userPrivateKey);
				// creating new trim with all data from nullified trim
				if (element.split("-").length>=2){
					TrimEx nulltrim = cchitBean.findTrimData(new Long(mdNew.getPath().split("-")[2]), activeAccountUser,userPrivateKey);
			    	TrimEx newtrim=trimCreatorBean.createTrim(activeAccountUser, nulltrim.getName(), path, now);
			    	//Setting parent path
			    	
			    	if (newtrim.getAct().getText() != null) {
			    		newtrim.getAct().getText().setST(trimFactory.createNewST(element));
			    	}
			    	
			    	//effectiveTime
			    	newtrim.getAct().getEffectiveTime().setTS(nulltrim.getAct().getEffectiveTime().getTS());
			    	((ActEx) newtrim.getAct()).setObservation(((ActEx) nulltrim.getAct()).getObservation());
			    	List<ActRelationship> lst = (nulltrim.getAct()).getRelationships();
	                for (ActRelationship rel : lst) {
	                        ((ActEx) newtrim.getAct()).getRelationship().get(rel.getName()).setAct(rel.getAct());
	                    }
			    	String newPath = trimCreatorBean.addTrimToActivityList(newtrim, path, activeAccountUser, now);
			    	if (newPath!=null)
			    		writer.write(newPath);
				}	
				req.setAttribute("activeWriter", writer);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			return;
		}
		else if(uri.endsWith("cancelVitalSign.ajaxcchit")){
			try{
				String action = req.getParameter( "action");
				String cancelElement = req.getParameter("element");
				TrimEx canceltrim = cchitBean.findTrimData(new Long(cancelElement.split("-")[2]), activeAccountUser,userPrivateKey);
				String element = canceltrim.getAct().getText().getST().getValue();
				String patientId = element.split("-")[1].split(":")[0]; 
				String path="echr:patient-" + patientId + ":observations:active";
				if (element == null ) throw new IllegalArgumentException( "Missing element in TRIM request");
				if (action == null ) throw new IllegalArgumentException( "Missing action in TRIM transition request");
				TolvenLogger.info( "[reviseVitalSign] account=" + activeAccountUser.getAccount().getId() + ", element=" + element + ", action: " + action, CCHITServlet.class);
				// Setting the current trim as nullified
				MenuData mdPrior  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				MenuData mdNew = creatorBean.createTRIMEvent(mdPrior, activeAccountUser, action, now,userPrivateKey);
				creatorBean.submit(mdNew, activeAccountUser,userPrivateKey);
				req.setAttribute("activeWriter", writer);
			} 
			catch (Exception e) {
				throw new ServletException(e);
			}
			return;
		}
		 /**
	     * To set the EA account email ID for the selected account;
	     * @author vineetha
	     */
		 else if (uri.endsWith("findEAAccountEMail.ajaxcchit")) {
		        String accountID  = req.getParameter("accountId");
		        try {
		            EAMail eaEmail = new EAMail();
		            String email = eaEmail.getEaEmail(accountID);
		            writer.write(email);
		            req.setAttribute("activeWriter", writer);
		        } catch (Exception e) {
		            throw new ServletException(e);
		        }
		    }

		/**
		 * Used to add, update and remove reaction for a non-drug Allergy
		 * @author Suja
		 * Added on: 12/20/2010 
		 */
		else if (uri.endsWith("processNonDrugAllergy.ajaxcchit")) {
			String message = "initialize";
			try{
				String element = req.getParameter( "element");
				String templateName = req.getParameter( "template");
				MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
				DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
				String reaction = req.getParameter( "reaction");
				String severity = req.getParameter( "severity");
				String sequence = req.getParameter( "sequence");
				if(StringUtils.isBlank(sequence)){
					sequence ="0";
				}
				// Note: We're reading this in from a mutable document.
				String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
				// Unmarshall the trim
				TrimEx wizardTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader( trimString) );
				int actionType = Integer.parseInt(req.getParameter("actionType"));
				ActRelationship allergyDetailsRel = null;
				ActEx allergyEntry = (ActEx)((ActEx)wizardTrim.getAct()).getRelationshipsList().get("entry").get(Integer.parseInt(sequence)).getAct(); 
				ActEx act = (ActEx) allergyEntry.getRelationship().get("allergyDetail").getAct();
				
				if (actionType==0) {
			    	message = "Allergy detail add";
					TrimEx _trimEx = trimBean.findTrim(templateName);
			    	Object obj = ((ActEx)_trimEx.getAct()).getRelationship().get("allergy");
			    	allergyDetailsRel = (ActRelationship)obj;
			    	act.getRelationships().add(allergyDetailsRel); // create new lesion
					allergyDetailsRel.setSourceTrim(templateName);
					ActRelationship destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("reaction");
			    	if(reaction !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("reaction", reaction, wizardTrim));
			    	}
			    	destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("severity");
			    	if(severity !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("severity", severity, wizardTrim));
			    	}
				} else if (actionType==1) {
			    	message = "Allergy detail update";
					int index = Integer.parseInt(req.getParameter("index"));
					allergyDetailsRel = act.getRelationshipsList().get("allergy").get(index);
					allergyDetailsRel.setSourceTrim(templateName);
					ActRelationship destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("reaction");
			    	if(reaction !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("reaction", reaction, wizardTrim));
			    	}
			    	destRel =  ((ActEx)allergyDetailsRel.getAct()).getRelationship().get("severity");
			    	if(severity !=null && destRel != null) {
			    		destRel.getAct().getObservation().getValues().get(0).setCE(getCE("severity", severity, wizardTrim));
			    	}
				} else if (actionType==2) {
					message = "Allergy detail remove";
					int index = Integer.parseInt(req.getParameter("index"));
					allergyDetailsRel = act.getRelationshipsList().get("allergy").get(index);
					act.getRelationshipsList().get("allergy").remove(index);
					((ActRelationshipsMap)act.getRelationshipsList()).refreshList();
				} 
		    	try {
			    	// marshall the trim
					creatorBean.marshalToDocument( wizardTrim, docXML );				
		    		writer.write("{success: true, message: '"+message+" success'}");
		    	} catch (Exception e) {
		    		TolvenLogger.debug("Exception during (Marshall Trim) "+message, e, getClass());
		    		writer.write("{success: false, message: '"+message+" failed. Please contact support.', error: '"+e.getMessage().replaceAll("'", "\"")+"'}");
		    	}
			} catch (Exception e) {
	    		TolvenLogger.debug("Exception during "+message, e, getClass());
	    		writer.write("{success: false, message: '"+message+" failed. Please contact support.', error: '"+e.getMessage().replaceAll("'", "\"")+"'}");
			}
    		req.setAttribute("activeWriter", writer);
		}

		/**
		 * Modified createGrid.ajaxf to filter the pop-ups in the analysis
		 * screens based on the type of analysis
		 * @author Pinky S 
		 * Added on: 01/17/2011
		 */
		else if(uri.endsWith("createGridWithFilteredItems.ajaxcchit")){
	    	String path = req.getParameter("element");
	    	String gridId = req.getParameter("gridId");	    	
	    	String gridType = req.getParameter("gridType");	
	    	String popupType = req.getParameter("popupType");	
			MenuPath menuPath = new MenuPath(path);	
			MenuStructure ms =  menuBean.findMenuStructure( activeAccountUser, menuPath.getPath() );
			// java script method for grid rows on-click
			String scriptMethodName = req.getParameter("methodName");
			// Form id for .   
			String scriptMethodArgs = req.getParameter("methodArgs");
			
			// Setup a query control
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setLimit( 0 );
			ctrl.setAccountUser(activeAccountUser);
			ctrl.setMenuStructure( ms.getAccountMenuStructure() );
			ctrl.setNow( new Date());
			ctrl.setOffset( 0 );
			ctrl.setOriginalTargetPath(menuPath);
			ctrl.setRequestedPath(menuPath);
			ctrl.setSortDirection( "ASC");
			ctrl.setSortOrder( "" );
			if(popupType.equalsIgnoreCase("diabetes")){
				ctrl.setFilter("diabetes");
			}else if(popupType.equalsIgnoreCase("Hypertension")){
				ctrl.setFilter("hypertension");
			}else if(popupType.equalsIgnoreCase("Cholesterol")){
				ctrl.setFilter("cholesterol");
			}else if(popupType.equalsIgnoreCase("Mammography")){
				ctrl.setFilter("mammography");
			}else if(popupType.equalsIgnoreCase("Influenza Vaccine")){
				ctrl.setFilter("influenza vaccination");
				popupType = "Influenza";
			}
			// Get the number of rows
			Long menuDataCount;
			if(popupType.equalsIgnoreCase("Colorectal")){
				long no = 2;
				menuDataCount = no;
			}
			else{
				menuDataCount = new Long(menuBean.countMenuData( ctrl ) );
			}
			GridBuilder grid = new GridBuilder(ctrl, menuDataCount);
			menuBean.prepareMQC( ctrl );
			if(gridId != null)
				grid.setGridId(gridId);
			if(gridType != null)
				grid.setGridType(gridType);
			
			grid.createGrid(scriptMethodName, scriptMethodArgs );
			StringBuffer sb;
			int start = 0;		
			int end = 0;
			sb = new StringBuffer(grid.toString());
			start = grid.toString().indexOf("createGrid");
			end = grid.toString().indexOf("(", start);
			sb.replace(start, end, "createGridWithFiltered"+popupType);
			writer.write(sb.toString());
		}
		
		/**
		 * Modified createGrid.ajaxf to generate image order pop-ups 
		 * with a tool-tip to show the long name of orders.
		 * @author Pinky S 
		 * Added on: 02/03/2011
		 */
		if( uri.endsWith( "createGridWithToolTip.ajaxcchit" )){
	    	String path = req.getParameter("element");
	    	String gridId = req.getParameter("gridId");	    	
	    	String gridType = req.getParameter("gridType");	 
	    	// incase if we don't need to show the favorites
	    	String disableFavorites = req.getParameter("disableFavorites");
			MenuPath menuPath = new MenuPath(path);	
			MenuStructure ms =  menuBean.findMenuStructure( activeAccountUser, menuPath.getPath() );
			// java script method for grid rows on-click
			String scriptMethodName = req.getParameter("methodName");
			// Form id for .   
			String scriptMethodArgs = req.getParameter("methodArgs");
			
			// Setup a query control
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setLimit( 0 );
			ctrl.setAccountUser(activeAccountUser);
			ctrl.setMenuStructure( ms.getAccountMenuStructure() );
			ctrl.setNow( new Date());
			ctrl.setOffset( 0 );
			ctrl.setOriginalTargetPath(menuPath);
			ctrl.setRequestedPath(menuPath);
			ctrl.setSortDirection( "ASC");
			ctrl.setSortOrder( "" );
			// Get the number of rows
			Long menuDataCount = new Long(menuBean.countMenuData( ctrl ) );
			GridBuilder grid = new GridBuilder(ctrl, menuDataCount);
			menuBean.prepareMQC( ctrl );
			if(gridId != null)
				grid.setGridId(gridId);
			if(gridType != null)
				grid.setGridType(gridType);
		
			if(null == disableFavorites || disableFavorites.equals("false")){
    			grid.setFavorites(findFavorites(path, activeAccountUser, menuBean, menuPath));
    			// need this to add 'All' on the favorites tabs
    			if(grid.getFavorites().size() > 0)
    				grid.setFavoritesType(activeAccountUser.getAccount().getAccountType().getKnownType()+
    						":"+(String)grid.getFavorites().get(0).get("Type"));
			}
			grid.createGrid(scriptMethodName, scriptMethodArgs );
			StringBuffer sb;
			int start = 0;		
			int end = 0;
			sb = new StringBuffer(grid.toString());
			start = grid.toString().indexOf("createGrid");
			end = grid.toString().indexOf("(", start);
			sb.replace(start, end, "createGridWithToolTip");
			writer.write(sb.toString());
	    }
		
		/**
		 * Modified menuData.ajax to generate image order pop-ups 
		 * with a tool-tip to show the long name of orders.
		 * @author Pinky S 
		 * Added on: 02/03/2011
		 */
		if (uri.endsWith("menuDataWithToolTip.ajaxcchit")) {
			writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			writer.write( "<ajax-response>\n" );
			int offset = Integer.parseInt( req.getParameter( "offset"));
			int pageSize = Integer.parseInt( req.getParameter( "page_size"));
			String sortCol = req.getParameter( "sort_col");
			String sortDir = req.getParameter( "sort_dir");
			// Figure out timeZone
			AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
			String tableId = req.getParameter( "id");
			writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
			writer.write( "<rows update_ui='true'>\n" );
			
			String element = req.getParameter( "element");

			MenuPath path = new MenuPath( element );
			long accountId = accountUser.getAccount().getId();
			MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
			boolean addPath = false;
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setAccountUser(accountUser);
			ctrl.setLimit( pageSize );
			ctrl.setMenuStructure( ms );
			ctrl.setNow( new Date() );
			ctrl.setOffset( offset );
			ctrl.setOriginalTargetPath( path );
			ctrl.setRequestedPath( path );
			ctrl.setSortDirection( sortDir);
			ctrl.setSortOrder( sortCol );

			// Any attribute name that ends with "Filter" (exactly) is taken as a filter parameter (but we don't store the 'filter suffix')
			Enumeration<String> params = req.getParameterNames();
			
			while (params.hasMoreElements() ) {
				String param = params.nextElement();
				// Simple catch-all filter is just "filter"
				// named filter is name+Filter
				if (param.equals("filter")) {
					ctrl.setFilter(req.getParameter(param));
				} else if (param.endsWith("Filter")) {
					ctrl.getFilters().put(param.substring(0, param.length()-6), req.getParameter(param));
				}else if(param.equals("methodArgs")){
					
				}
			}
			String gridType = req.getParameter( "gridType");
			if (gridType==null || gridType.length()==0) {
				gridType = "_default";
			}
			FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
			ExpressionEvaluator ee = fow.getExpressionEvaluator();
			ee.pushContext();
			// Make all request parameters available to the expression evaluator as variables.
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				ee.addVariable(key, req.getParameter(key));
			}
			 // Do the query
			MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
		    int line = 0;
		    // Prepare the list of columns we're going to populate
			for( Map<String, Object>  rowData : mdQueryResults.getResults()) {
		    	line++;
		    	writeMenuDataRowWithToolTip( fow, mdQueryResults, rowData, menuLocal, req);
			}
			ee.popContext();
			
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			
			req.setAttribute("activeWriter", writer);
		}
		/**
		 * Modified menuData.ajax to filter the pop-ups in the Diabetes -analysis screen.
		 * @author Pinky S 
		 * Added on: 01/17/2011
		 */
		else if(uri.endsWith("menuDataWithFilteredDiabetes.ajaxcchit")){
			writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			writer.write( "<ajax-response>\n" );
			int offset = Integer.parseInt( req.getParameter( "offset"));
			int pageSize = Integer.parseInt( req.getParameter( "page_size"));
			String sortCol = req.getParameter( "sort_col");
			String sortDir = req.getParameter( "sort_dir");
			// Figure out timeZone
			AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
			String tableId = req.getParameter( "id");
			writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
			writer.write( "<rows update_ui='true'>\n" );
			
			String element = req.getParameter( "element");
			MenuPath path = new MenuPath( element );
			long accountId = accountUser.getAccount().getId();
			MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
			MenuQueryControl ctrl = new MenuQueryControl();
			
			ctrl.setLimit( pageSize );
			ctrl.setAccountUser(accountUser);
			ctrl.setMenuStructure( ms );
			ctrl.setNow( new Date() );
			ctrl.setOffset( offset );
			ctrl.setOriginalTargetPath( path );
			ctrl.setRequestedPath( path );
			ctrl.setSortDirection( sortDir);
			ctrl.setSortOrder( sortCol );
			ctrl.setFilter("diabetes");
			
			// Any attribute name that ends with "Filter" (exactly) is taken as a filter parameter (but we don't store the 'filter suffix')
			Enumeration<String> params = req.getParameterNames();
			if((req.getParameter("filter")!=null)&&(!req.getParameter("filter").equals(""))){
				while (params.hasMoreElements() ) {
					String param = params.nextElement();
					// Simple catch-all filter is just "filter"
					// named filter is name+Filter
					if (param.equals("filter")) {
						ctrl.setFilter(req.getParameter(param));
					} else if (param.endsWith("Filter")) {
						ctrl.getFilters().put(param.substring(0, param.length()-6), req.getParameter(param));
					}else if(param.equals("methodArgs")){
						
					}
				}
			}
			
			String gridType = req.getParameter( "gridType");
			if (gridType==null || gridType.length()==0) {
				gridType = "_default";
			}
			FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
			ExpressionEvaluator ee = fow.getExpressionEvaluator();
			ee.pushContext();
			// Make all request parameters available to the expression evaluator as variables.
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				ee.addVariable(key, req.getParameter(key));
			}
			 // Do the query
			MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
		    int line = 0;
		    // Prepare the list of columns we're going to populate
			for( Map<String, Object>  rowData : mdQueryResults.getResults()) {
				if((rowData.get("Problem").toString().indexOf("diabetes")!=-1)||
						(rowData.get("Problem").toString().indexOf("Diabetes")!=-1)){
					line++;
					writeMenuDataRow( fow, mdQueryResults, rowData, menuLocal, req);
				}
			}
			
			ee.popContext();
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			
			req.setAttribute("activeWriter", writer);
		}
		
		/**
		 * Modified menuData.ajax to filter the pop-ups in the Influenza -analysis screen.
		 * @author Pinky S 
		 * Added on: 01/17/2011
		 */
		else if(uri.endsWith("menuDataWithFilteredInfluenza.ajaxcchit")){
			writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			writer.write( "<ajax-response>\n" );
			int offset = Integer.parseInt( req.getParameter( "offset"));
			int pageSize = Integer.parseInt( req.getParameter( "page_size"));
			String sortCol = req.getParameter( "sort_col");
			String sortDir = req.getParameter( "sort_dir");
			// Figure out timeZone
			AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
			String tableId = req.getParameter( "id");
			writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
			writer.write( "<rows update_ui='true'>\n" );
			
			String element = req.getParameter( "element");
			MenuPath path = new MenuPath( element );
			long accountId = accountUser.getAccount().getId();
			MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
			boolean addPath = false;
			MenuQueryControl ctrl = new MenuQueryControl();
			
			ctrl.setLimit( pageSize );
			ctrl.setAccountUser(accountUser);
			ctrl.setMenuStructure( ms );
			ctrl.setNow( new Date() );
			ctrl.setOffset( offset );
			ctrl.setOriginalTargetPath( path );
			ctrl.setRequestedPath( path );
			ctrl.setSortDirection( sortDir);
			ctrl.setSortOrder( sortCol );
			ctrl.setFilter("influenza vaccination");
			
			// Any attribute name that ends with "Filter" (exactly) is taken as a filter parameter (but we don't store the 'filter suffix')
			Enumeration<String> params = req.getParameterNames();
			if((req.getParameter("filter")!=null)&&(!req.getParameter("filter").equals(""))){
				while (params.hasMoreElements() ) {
					String param = params.nextElement();
					// Simple catch-all filter is just "filter"
					// named filter is name+Filter
					if (param.equals("filter")) {
						ctrl.setFilter(req.getParameter(param));
					} else if (param.endsWith("Filter")) {
						ctrl.getFilters().put(param.substring(0, param.length()-6), req.getParameter(param));
					}else if(param.equals("methodArgs")){
						
					}
				}
			}
			
			String gridType = req.getParameter( "gridType");
			if (gridType==null || gridType.length()==0) {
				gridType = "_default";
			}
			FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
			ExpressionEvaluator ee = fow.getExpressionEvaluator();
			ee.pushContext();
			// Make all request parameters available to the expression evaluator as variables.
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				ee.addVariable(key, req.getParameter(key));
			}
			 // Do the query
			MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
		    int line = 0;
		    // Prepare the list of columns we're going to populate
			for( Map<String, Object>  rowData : mdQueryResults.getResults()) {
				if((rowData.get("immunization").toString().indexOf("influenza vaccination")!=-1)||
						(rowData.get("immunization").toString().indexOf("Influenza Vaccination")!=-1)||
						(rowData.get("immunization").toString().indexOf("Influenza vaccination")!=-1)){
					line++;
					writeMenuDataRow( fow, mdQueryResults, rowData, menuLocal, req);
				}
			}
			
			ee.popContext();
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			
			req.setAttribute("activeWriter", writer);
		}
		
		/**
		 * Modified menuData.ajax to filter the pop-ups in the Hypertension -analysis screen.
		 * @author Pinky S 
		 * Added on: 01/17/2011
		 */
		else if(uri.endsWith("menuDataWithFilteredHypertension.ajaxcchit")){
			writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			writer.write( "<ajax-response>\n" );
			int offset = Integer.parseInt( req.getParameter( "offset"));
			int pageSize = Integer.parseInt( req.getParameter( "page_size"));
			String sortCol = req.getParameter( "sort_col");
			String sortDir = req.getParameter( "sort_dir");
			// Figure out timeZone
			AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
			String tableId = req.getParameter( "id");
			writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
			writer.write( "<rows update_ui='true'>\n" );
			
			String element = req.getParameter( "element");
			MenuPath path = new MenuPath( element );
			long accountId = accountUser.getAccount().getId();
			MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
			boolean addPath = false;
			MenuQueryControl ctrl = new MenuQueryControl();
			
			ctrl.setLimit( pageSize );
			ctrl.setAccountUser(accountUser);
			ctrl.setMenuStructure( ms );
			ctrl.setNow( new Date() );
			ctrl.setOffset( offset );
			ctrl.setOriginalTargetPath( path );
			ctrl.setRequestedPath( path );
			ctrl.setSortDirection( sortDir);
			ctrl.setSortOrder( sortCol );
			ctrl.setFilter("hypertension");
			
			// Any attribute name that ends with "Filter" (exactly) is taken as a filter parameter (but we don't store the 'filter suffix')
			Enumeration<String> params = req.getParameterNames();
			if((req.getParameter("filter")!=null)&&(!req.getParameter("filter").equals(""))){
				while (params.hasMoreElements() ) {
					String param = params.nextElement();
					// Simple catch-all filter is just "filter"
					// named filter is name+Filter
					if (param.equals("filter")) {
						ctrl.setFilter(req.getParameter(param));
					} else if (param.endsWith("Filter")) {
						ctrl.getFilters().put(param.substring(0, param.length()-6), req.getParameter(param));
					}else if(param.equals("methodArgs")){
						
					}
				}
			}
			
			String gridType = req.getParameter( "gridType");
			if (gridType==null || gridType.length()==0) {
				gridType = "_default";
			}
			FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
			ExpressionEvaluator ee = fow.getExpressionEvaluator();
			ee.pushContext();
			// Make all request parameters available to the expression evaluator as variables.
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				ee.addVariable(key, req.getParameter(key));
			}
			 // Do the query
			MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
		    int line = 0;
		    // Prepare the list of columns we're going to populate
			for( Map<String, Object>  rowData : mdQueryResults.getResults()) {
				if((rowData.get("Problem").toString().indexOf("hypertension")!=-1)||
						(rowData.get("Problem").toString().indexOf("Hypertension")!=-1)){
					line++;
					writeMenuDataRow( fow, mdQueryResults, rowData, menuLocal, req);
				}
			}
			ee.popContext();
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			req.setAttribute("activeWriter", writer);
		}
		
		/**
		 * Modified menuData.ajax to filter the pop-ups in the Cholesterol -analysis screen.
		 * @author Pinky S 
		 * Added on: 01/17/2011
		 */
		else if(uri.endsWith("menuDataWithFilteredCholesterol.ajaxcchit")){
			writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			writer.write( "<ajax-response>\n" );
			int offset = Integer.parseInt( req.getParameter( "offset"));
			int pageSize = Integer.parseInt( req.getParameter( "page_size"));
			String sortCol = req.getParameter( "sort_col");
			String sortDir = req.getParameter( "sort_dir");
			// Figure out timeZone
			AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
			String tableId = req.getParameter( "id");
			writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
			writer.write( "<rows update_ui='true'>\n" );
			
			String element = req.getParameter( "element");
			MenuPath path = new MenuPath( element );
			long accountId = accountUser.getAccount().getId();
			MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
			boolean addPath = false;
			MenuQueryControl ctrl = new MenuQueryControl();
			
			ctrl.setLimit( pageSize );
			ctrl.setAccountUser(accountUser);
			ctrl.setMenuStructure( ms );
			ctrl.setNow( new Date() );
			ctrl.setOffset( offset );
			ctrl.setOriginalTargetPath( path );
			ctrl.setRequestedPath( path );
			ctrl.setSortDirection( sortDir);
			ctrl.setSortOrder( sortCol );
			ctrl.setFilter("cholesterol");
			
			// Any attribute name that ends with "Filter" (exactly) is taken as a filter parameter (but we don't store the 'filter suffix')
			Enumeration<String> params = req.getParameterNames();
			if((req.getParameter("filter")!=null)&&(!req.getParameter("filter").equals(""))){
				while (params.hasMoreElements() ) {
					String param = params.nextElement();
					// Simple catch-all filter is just "filter"
					// named filter is name+Filter
					if (param.equals("filter")) {
						ctrl.setFilter(req.getParameter(param));
					} else if (param.endsWith("Filter")) {
						ctrl.getFilters().put(param.substring(0, param.length()-6), req.getParameter(param));
					}else if(param.equals("methodArgs")){
						
					}
				}
			}
			
			String gridType = req.getParameter( "gridType");
			if (gridType==null || gridType.length()==0) {
				gridType = "_default";
			}
			FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
			ExpressionEvaluator ee = fow.getExpressionEvaluator();
			ee.pushContext();
			// Make all request parameters available to the expression evaluator as variables.
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				ee.addVariable(key, req.getParameter(key));
			}
			 // Do the query
			MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
		    int line = 0;
		    // Prepare the list of columns we're going to populate
			for( Map<String, Object>  rowData : mdQueryResults.getResults()) {
				if((rowData.get("Problem").toString().indexOf("cholesterol")!=-1)||
						(rowData.get("Problem").toString().indexOf("Cholesterol")!=-1)){
					line++;
					writeMenuDataRow( fow, mdQueryResults, rowData, menuLocal, req);
				}
			}
			ee.popContext();
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			req.setAttribute("activeWriter", writer);
		}
		
		/**
		 * Modified menuData.ajax to filter the pop-ups in the Colorectal -analysis screen.
		 * @author Pinky S 
		 * Added on: 01/17/2011
		 */
		else if(uri.endsWith("menuDataWithFilteredColorectal.ajaxcchit")){
			writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			writer.write( "<ajax-response>\n" );
			int offset = Integer.parseInt( req.getParameter( "offset"));
			int pageSize = Integer.parseInt( req.getParameter( "page_size"));
			String sortCol = req.getParameter( "sort_col");
			String sortDir = req.getParameter( "sort_dir");
			// Figure out timeZone
			AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
			String tableId = req.getParameter( "id");
			writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
			writer.write( "<rows update_ui='true'>\n" );
			
			String element = req.getParameter( "element");
			MenuPath path = new MenuPath( element );
			long accountId = accountUser.getAccount().getId();
			MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
			boolean addPath = false;
			MenuQueryControl ctrl = new MenuQueryControl();
			
			ctrl.setLimit( pageSize );
			ctrl.setAccountUser(accountUser);
			ctrl.setMenuStructure( ms );
			ctrl.setNow( new Date() );
			ctrl.setOffset( offset );
			ctrl.setOriginalTargetPath( path );
			ctrl.setRequestedPath( path );
			ctrl.setSortDirection( sortDir);
			ctrl.setSortOrder( sortCol );
			
			String gridType = req.getParameter( "gridType");
			if (gridType==null || gridType.length()==0) {
				gridType = "_default";
			}
			FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
			ExpressionEvaluator ee = fow.getExpressionEvaluator();
			ee.pushContext();
			// Make all request parameters available to the expression evaluator as variables.
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				ee.addVariable(key, req.getParameter(key));
			}
			 // Do the query
			MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
		    int line = 0;
		   
		    //Setting the values 'Colonoscopy' and 'Digital Rectal Exam' in the pop-ups.
		    Map<Integer, Object>  rowDatas = new HashMap<Integer, Object>();
			
			Map<String, Object>  rowData0 = new HashMap<String, Object>();
			Long id = new Long(234563);
			rowData0.put("id", id);
			rowData0.put("string01", "DIGITAL RECTAL EXAM");
			rowData0.put("string02", "px/UMLS-C1384593-89.34");
			rowData0.put("referencePath", null);
			rowData0.put("sourceAccountId", null);
			rowData0.put("path", "icd9ProcedureMenu-234563");
			rowData0.put("heading", "px/UMLS-C1384593-89.34");
			rowData0.put("procedure", "DIGITAL RECTAL EXAM");
			rowDatas.put(0, rowData0);
			
			Map<String, Object>  rowData1 = new HashMap<String, Object>();
			Long id1 = new Long(225079);
			rowData1.put("id", id1);
			rowData1.put("string01", "COLONOSCOPY");
			rowData1.put("string02", "px/UMLS-C0009378-45.23");
			rowData1.put("referencePath", null);
			rowData1.put("sourceAccountId", null);
			rowData1.put("path", "icd9ProcedureMenu-225079");
			rowData1.put("heading", "px/UMLS-C0009378-45.23");
			rowData1.put("procedure", "COLONOSCOPY");
			rowDatas.put(1, rowData1);
			for(int i = 0;i<2;i++){
				line++;
				writeMenuDataRow( fow, mdQueryResults, (Map<String, Object>) rowDatas.get(i), menuLocal, req);
			}
			ee.popContext();
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			req.setAttribute("activeWriter", writer);
		}
		
		/**
		 * Modified menuData.ajax to filter the pop-ups in the Mammography -analysis screen.
		 * @author Pinky S 
		 * Added on: 01/17/2011
		 */
		else if(uri.endsWith("menuDataWithFilteredMammography.ajaxcchit")){
			writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			writer.write( "<ajax-response>\n" );
			int offset = Integer.parseInt( req.getParameter( "offset"));
			int pageSize = Integer.parseInt( req.getParameter( "page_size"));
			String sortCol = req.getParameter( "sort_col");
			String sortDir = req.getParameter( "sort_dir");
			// Figure out timeZone
			AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
			String tableId = req.getParameter( "id");
			writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
			writer.write( "<rows update_ui='true'>\n" );
			
			String element = req.getParameter( "element");
			MenuPath path = new MenuPath( element );
			long accountId = accountUser.getAccount().getId();
			MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
			boolean addPath = false;
			MenuQueryControl ctrl = new MenuQueryControl();
			
			ctrl.setLimit( pageSize );
			ctrl.setAccountUser(accountUser);
			ctrl.setMenuStructure( ms );
			ctrl.setNow( new Date() );
			ctrl.setOffset( offset );
			ctrl.setOriginalTargetPath( path );
			ctrl.setRequestedPath( path );
			ctrl.setSortDirection( sortDir);
			ctrl.setSortOrder( sortCol );
			ctrl.setFilter("mammography");
			
			// Any attribute name that ends with "Filter" (exactly) is taken as a filter parameter (but we don't store the 'filter suffix')
			Enumeration<String> params = req.getParameterNames();
			if((req.getParameter("filter")!=null)&&(!req.getParameter("filter").equals(""))){
				while (params.hasMoreElements() ) {
					String param = params.nextElement();
					// Simple catch-all filter is just "filter"
					// named filter is name+Filter
					if (param.equals("filter")) {
						ctrl.setFilter(req.getParameter(param));
					} else if (param.endsWith("Filter")) {
						ctrl.getFilters().put(param.substring(0, param.length()-6), req.getParameter(param));
					}else if(param.equals("methodArgs")){
						
					}
				}
			}
			
			String gridType = req.getParameter( "gridType");
			if (gridType==null || gridType.length()==0) {
				gridType = "_default";
			}
			FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
			ExpressionEvaluator ee = fow.getExpressionEvaluator();
			ee.pushContext();
			// Make all request parameters available to the expression evaluator as variables.
			Enumeration names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();
				ee.addVariable(key, req.getParameter(key));
			}
			 // Do the query
			MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
		    int line = 0;
		    // Prepare the list of columns we're going to populate
			for( Map<String, Object>  rowData : mdQueryResults.getResults()) {
				if(rowData.get("procedure").toString().indexOf("MAMMOGRAPHY")!=-1){
					line++;
					writeMenuDataRow( fow, mdQueryResults, rowData, menuLocal, req);
				}
			}
			ee.popContext();
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			req.setAttribute("activeWriter", writer);
		}
		
		/**
		 * This function is used to retrieve the code of the pop-up, that we select from the tpf pop-up.
		 * In Analysis Screens.
		 * @author Pinky S
		 * added on 01/12/2011
		 */
		else if (uri.endsWith("getCodeFromPopUp.ajaxcchit")) { 
			String element = req.getParameter( "element");
			String template = req.getParameter("template");
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
			try {
				String code = null;
				TrimEx templateTrim = null;
				if (template!=null) {
					templateTrim=trimBean.findTrim(template);
					if(templateTrim.getExtends().equals("obs/evn/problem")){
						code = templateTrim.getAct().getObservation().getValues().get(0).getCE().getCode();
					}
					else if(templateTrim.getExtends().equals("icd9Procedure")){
						code = templateTrim.getAct().getCode().getCE().getTranslations().get(0).getCode();
					}
					else if(templateTrim.getExtends().equals("sbadm/rqo/immunization")){
						code = templateTrim.getName().split("/")[1];
					}
					else{
						code = templateTrim.getAct().getCode().getCE().getCode();
					}
				}
				writer.write("Success");
				writer.write("|");
				writer.write(code);
				req.setAttribute("activeWriter", writer);
				return;
		    }catch (Exception e) {
		    	writer.write("Failure");
				req.setAttribute("activeWriter", writer);
				return;
			}
		} else if(uri.endsWith("submitUniqueTrim.ajaxcchit")) {
			final String trimName = req.getParameter("templateID");
			String element = req.getParameter("element");
			String pathPrefix = null;
			
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
            DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
            String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
            try {
				TrimEx multipleDataTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader(trimString));
				if (((ActEx)multipleDataTrim.getAct()).getParticipation().get("subject").getRole().getClassCode().equals(RoleClass.PAT)) {
					pathPrefix = ((ActEx)multipleDataTrim.getAct()).getParticipation().get("subject").getRole().getId().getIIS().get(0).getExtension().trim();
				}
				if (multipleDataTrim.getName().equals(trimName) && ((ActEx)multipleDataTrim.getAct()).getRelationshipsList().get("entry").size() > 1 ) {
					for (ActRelationship rel: multipleDataTrim.getAct().getRelationships()) {
						if (rel.getName().equals("entry")) {
							submitUniqueProblemTrim(activeAccountUser, rel, pathPrefix, now);
						}
					}
				} 
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			writer.write("Success");
			return;
		} else if (uri.endsWith("submitProblem.ajaxcchit")) {
			final String progressNotesTrimName = "docclin/evn/progressnotes";
			final String labOrderTrimName = "labOrderDoc";
			final String imageOrderTrimName = "imageOrderDoc";
			String element = req.getParameter("element");
			String pathPrefix = null;
			
			MenuData md  = menuBean.findMenuDataItem(activeAccountUser.getAccount().getId(), element);
            DocXML docXML = (DocXML) getDocBean().findDocument(md.getDocumentId());
            String trimString = this.getDocProtectionBean().getDecryptedContentString(docXML, activeAccountUser,userPrivateKey);
            try {
				TrimEx incomingTrim = (TrimEx) xmlBean.unmarshal(TRIMns, new StringReader(trimString));
				if (((ActEx)incomingTrim.getAct()).getParticipation().get("subject").getRole().getClassCode().equals(RoleClass.PAT)) {
					pathPrefix = ((ActEx)incomingTrim.getAct()).getParticipation().get("subject").getRole().getId().getIIS().get(0).getExtension().trim();
				}
				
				if (incomingTrim.getName().equals(progressNotesTrimName)) {
					// Checking whether problems are there in pgNotes Trim
					for (ActRelationship rel : ((ActEx) incomingTrim.getAct()).getRelationship().get("subjective").getAct().getRelationships()) {
						if (rel.getName().equals("problems")) {
							for (ActRelationship problemRel : rel.getAct().getRelationships()) {
								// Only checked problems will be submitted in the problem list.
								if (problemRel.getName().equals("problem") && problemRel.isEnabled()) {
									submitUniqueProblemTrim(activeAccountUser, problemRel, pathPrefix, now);
								}
							}
						}
					}
				} else if ((incomingTrim.getName().equals(labOrderTrimName) || incomingTrim.getName().equals(imageOrderTrimName)) &&
						((ActEx) incomingTrim.getAct()).getRelationship().get("problems") != null) {
					// Checking whether problems are there in labOrder Trim
					for (ActRelationship problemRel : ((ActEx) incomingTrim.getAct()).getRelationship().get("problems").getAct().getRelationships()) {
						if (problemRel.getName().equals("problem") && problemRel.isEnabled()) {
							submitUniqueProblemTrim(activeAccountUser, problemRel, pathPrefix, now);
						}
					}
				}
            } catch (JAXBException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			writer.write("Success");
			return;
	    }
		
		/**
		 * This function is used to retrieve the last snapshot date.
		 * In Analysis Screens.
		 * @author Pinky S
		 * added on 02/02/2011
		 */
		else if (uri.endsWith("findLastSnapShotDate.ajaxcchit")){
			String element = req.getParameter( "element");
			try {
				List<MenuData> snapShotMenudata = cchitBean.findLatestSnapshotDate(activeAccountUser.getAccount(), element);
				writer.write("Success");
				writer.write("|");
				writer.write(snapShotMenudata.get(0).getDate01().toString());
				req.setAttribute("activeWriter", writer);
				return;
		    }catch (Exception e) {
		    	writer.write("Failure");
				req.setAttribute("activeWriter", writer);
				return;
			}
		}
	}
	
	/**
	 * Submits a single problem trim.
	 * @param activeAccountUser
	 * @param problemRel
	 * @param patientPath
	 * @param now
	 * @author <unni.s@cyrusxp.com>
	 * @since v0.0.21
	 */
	private void submitUniqueProblemTrim(AccountUser activeAccountUser, ActRelationship problemRel, 
			String patientPath, Date now) {
		try {
			final String problemTrimName = "docclin/evn/problem";
			TolvenLogger.info("Problem trim exists for submission in Problems List.", CCHITServlet.class);
			TrimEx templateTrim = trimCreatorBean.createTrim(activeAccountUser, problemTrimName, patientPath+":problems:all", now);
			StringBuffer templateName = new StringBuffer("problem/SNOMED_CID-");
			templateName.append(problemRel.getAct().getObservation().getValues().get(0).getCE().getCode().trim());
			ActRelationship entryRel = parseTemplate(activeAccountUser, templateName);
			
			for (ActRelationship problemDet : problemRel.getAct().getRelationships()) {
				String relName = problemDet.getName();
				if (problemDet.getName().equals(relName)) {
					((ActEx)entryRel.getAct()).getRelationship().get(relName).setAct(
							problemDet.getAct());
				}
			}
			entryRel.getAct().setEffectiveTime(problemRel.getAct().getEffectiveTime());
			entryRel.getAct().setObservation(problemRel.getAct().getObservation());
			if (entryRel.getAct().getParticipations().size() > 1) { //Removed as placeholder bind cannot be created.
				entryRel.getAct().getParticipations().remove(1);
			}
			templateTrim.getAct().getRelationships().add(entryRel);
			((ActEx)templateTrim.getAct()).getRelationship().get("submitStatus").
				getAct().getObservation().getValues().get(0).getINT().setValue(1);
			((ActEx)templateTrim.getAct()).getRelationship().get("submitStatus").
				getAct().getObservation().getValues().get(1).getST().setValue("Yes");
			
			trimCreatorBean.submitTrim(templateTrim, patientPath+":problems:all", activeAccountUser, now);
			TolvenLogger.info("Unique problem submitted successfully.", CCHITServlet.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (TRIMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<MenuPath> getContextList() {
		if (contextList==null) {
			contextList = new ArrayList<MenuPath>();
		}
		return contextList;
	}
	
	public String getTemplateBody(String template) {
		if (templateBody==null) {
			System.out.println(template);
			TrimHeader trimHeader = trimBean.findTrimHeader(template);
			templateBody = new String(trimHeader.getTrim());
		}
		return templateBody;
	}
	
	/**
	 * Generates an ActRelationship for problem.
	 * @param activeAccountUser
	 * @param templateName
	 * @return ActRelationship
	 * @throws JAXBException
	 * @author <unni.s@cyrusxp.com>
	 * @since v0.0.21
	 */
	private ActRelationship parseTemplate(AccountUser activeAccountUser, StringBuffer templateName) throws JAXBException {
		TrimExpressionEvaluator ee = new TrimExpressionEvaluator();
		Map<String, Object> variables = new HashMap<String, Object>(10);
		variables.put("account", activeAccountUser.getAccount());
		variables.put("accountUser", activeAccountUser);
		variables.put("user", activeAccountUser.getUser());
		variables.put("knownType", activeAccountUser.getAccount().getAccountType().getKnownType());
		variables.put("now", new Date());
		for (MenuPath contextPath : getContextList()) {
			long keys[] = contextPath.getNodeKeys();
			for (int n = 0; n < keys.length; n++) {
				if (keys[n]!=0) {
					MenuData md = menuBean.findMenuDataItem(keys[n]);
					variables.put( contextPath.getNode(n), md);
				}
			}
		}
		ee.addVariables(variables);
		// Make our functions available - anything else to pass to the act at this point?
		TrimEx templateTrim = trimBean.parseTrim(getTemplateBody(templateName.toString()), ee );
		ActRelationship ar = trimFactory.createActRelationship();
		ar.setTypeCode(ActRelationshipType.valueOf("COMP"));
		ar.setDirection(ActRelationshipDirection.valueOf("OUT"));
		ar.setName("entry");
		ar.setAct(templateTrim.getAct());
		return ar;
	}
	
	/**
	 * Function to write the results in rows. 
	 * @author Pinky S 
	 * Added on: 01/17/2011
	 */
	public static void writeMenuDataRow( FormattedOutputWriter fow, MDQueryResults mdQueryResults,
			Map<String, Object> rowData, MenuLocal menuLocal, HttpServletRequest req ) throws IOException {
		List<MSColumn> msColumns = mdQueryResults.getSortedColumns();
		fow.writeVerbatim( "<tr "+(req.getParameter("methodArgs").contains("addId")?"id='"+(String)rowData.get( "path" )+"'":"")+">");
		ExpressionEvaluator ee = fow.getExpressionEvaluator();
		ee.pushContext();
		ee.addVariables(rowData);
		for (MSColumn msColumn : msColumns) {
			if (msColumn.isVisible()) {
				fow.writeVerbatim( "<td>");
				String outputExpression = null;
				if (fow.getType()!=null) {
					outputExpression = msColumn.getOutputFormatMap().get(fow.getType());
				}
				if (outputExpression!=null) {
					writeFormattedColumn(rowData, msColumn, fow, menuLocal, req, outputExpression);
				} else {
					writeColumn( rowData, msColumn, fow, menuLocal, req);
				}
				fow.writeVerbatim("</td>");
			}
		}
		fow.writeVerbatim( "</tr>\n");
		ee.popContext();
	}
	
	/**
	 * Function to write the results in rows. 
	 * Modified to show the long name of the items as a tool-tip. 
	 * @author Pinky S 
	 * Added on: 02/03/2011
	 */
	public static void writeMenuDataRowWithToolTip( FormattedOutputWriter fow, MDQueryResults mdQueryResults,
			Map<String, Object> rowData, MenuLocal menuLocal, HttpServletRequest req ) throws IOException {
		List<MSColumn> msColumns = mdQueryResults.getSortedColumns();
		fow.writeVerbatim( "<tr "+(req.getParameter("methodArgs").contains("addId")?"id='"+(String)rowData.get( "path" )+"'":"")+">");
		ExpressionEvaluator ee = fow.getExpressionEvaluator();
		ee.pushContext();
		ee.addVariables(rowData);
		for (MSColumn msColumn : msColumns) {
			if (msColumn.isVisible()) {
				fow.writeVerbatim( "<td>");
				String outputExpression = null;
				if (fow.getType()!=null) {
					outputExpression = msColumn.getOutputFormatMap().get(fow.getType());
				}
				if (outputExpression!=null) {
					writeFormattedColumn(rowData, msColumn, fow, menuLocal, req, outputExpression);
				} else {
					writeColumnWithToolTip( rowData, msColumn, fow, menuLocal, req);
				}
				fow.writeVerbatim("</td>");
			}
		}
		fow.writeVerbatim( "</tr>\n");
		ee.popContext();
	}
	
	/**
	 * Function to format the columns in result rows. 
	 * @author Pinky S 
	 * Added on: 01/17/2011
	 */
	private static void writeFormattedColumn(Map<String, Object> rowData, MSColumn msColumn, FormattedOutputWriter fow, MenuLocal menuLocal, HttpServletRequest req, String outputExpression ) {
		try {
			fow.writeExpression(outputExpression);
		} catch (Exception e) {
			throw new RuntimeException( "Error formatting column " + msColumn.getHeading(),e);
		}
	}
	
	/**
	 * Function to write the columns in the rows of result. 
	 * @author Pinky S 
	 * Added on: 01/17/2011
	 */
	private static void writeColumn(Map<String, Object> rowData, MSColumn msColumn, FormattedOutputWriter fow, MenuLocal menuLocal, HttpServletRequest req ) {
	    try {
			String lMethodArgs = req.getParameter("methodArgs");
			String lMethodName = req.getParameter("methodName");
			// Get the column value to display
			Object value = rowData.get(msColumn.getHeading());
			if (value==null) {
				fow.writeVerbatim(" "); // NB Space
			} else {
				String stringValue = value.toString().trim();
				if (stringValue.length()==0) {
					fow.writeVerbatim(" ");	// NB Space
				}else if (lMethodName.equals("patientPopupRefReq")) {	//For patient popup in refillReq wizard. Modified by uks.
					fow.writeVerbatim( "<a href=\"javascript:patientPopupRefReq('" + rowData.get("referencePath") +
							"','"+req.getParameter("element")+"')\">" );
					fow.writeEscape( stringValue );
					fow.writeVerbatim("</a>");
					if(null != rowData.get("sourceAccountId")) {
						AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
						Long accountId = accountUser.getAccount().getId();
						if(Long.parseLong(rowData.get("sourceAccountId").toString()) != (accountId)) {
							fow.writeVerbatim(" <img src='../images/vcard.gif' class='shareInfo' title='Data shared from account:"+rowData.get("sourceAccountId")+"'/>");						
						}
					}
				}
				
				else if (msColumn.isReference()) {
					fow.writeVerbatim( "<a href=\"javascript:showPane('" + rowData.get("referencePath") +
							"',false,'"+req.getParameter("element")+"')\">" );
					fow.writeEscape( stringValue );
					fow.writeVerbatim("</a>");
					if(null != rowData.get("sourceAccountId")) {
						AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
						Long accountId = accountUser.getAccount().getId();
						if(Long.parseLong(rowData.get("sourceAccountId").toString()) != (accountId)) {
							fow.writeVerbatim(" <img src='../images/vcard.gif' class='shareInfo' title='Data shared from account:"+rowData.get("sourceAccountId")+"'/>");						
						}
					}
				} else if (msColumn.isInstantiate() && !lMethodArgs.contains("enableCopy")) {
					String instantiateValue="";
					
					String path=(String)rowData.get( "path" );
					String element = req.getParameter("element");
					
					if (lMethodName != null && lMethodName.length() > 0)
					{
						long id;
						id = (Long)rowData.get( "id" );
						MenuData md1 = menuLocal.findMenuDataItem(id);
						if (lMethodArgs.contains("withCode")) 
							instantiateValue =  "<span style='float:left;width:100px;'>"+md1.getString02().split("-")[1]+"</span><span style='float:left;'>";
						instantiateValue +=  "<a href=\"#\" onclick=\"" + lMethodName + "('" + md1.getString02() + "','"+ element+ "','" + lMethodArgs + "')\">";
					}else{
						instantiateValue =  "<a href=\"#\" onclick=\"instantiate('" + path + "','"+ element+ "')\">";	
					}		
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
					if (lMethodArgs.contains("withCode")) 
						fow.writeVerbatim("</span>");
				} else if(lMethodArgs != null && lMethodArgs.contains("enableCopy")){
					/* this to populate a grid which will be used to create favorites lists
					 basically on clicking an item in the grid a custom script function will be called */
					
					//chop off the argument 'enableCopy' and replace , with ','
					lMethodArgs = lMethodArgs.replaceAll("enableCopy,","").replaceAll(",","','");
					
					String path=(String)rowData.get( "path" );
					String instantiateValue =  "<a id='"+path+"' href=\"javascript:" + lMethodName + "('" + path + "','"+lMethodArgs+"')\">";
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
				} else  if(lMethodName != null && lMethodName.length() > 0){ // may be this should be combined with the above if
					String instantiateValue="";
					String element = req.getParameter("element");
					long id = (Long)rowData.get( "id" );
					MenuData md1 = menuLocal.findMenuDataItem(id);
					instantiateValue =  "<a href=\"#\" onclick=\"" + lMethodName + "('" + md1.getString02() + "','"+ element+ "','" + lMethodArgs + "')\">";
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
				}else{
					fow.writeEscape( stringValue );
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error writing column " + msColumn, e);
		}
	}
	
	/**
	 * Function to write the columns in the rows of result.
	 * Modified to show the long name of the items as a tool-tip. 
	 * @author Pinky S 
	 * Added on: 02/03/2011
	 */
	private static void writeColumnWithToolTip(Map<String, Object> rowData, MSColumn msColumn, FormattedOutputWriter fow, MenuLocal menuLocal, HttpServletRequest req ) {
	    try {
			String lMethodArgs = req.getParameter("methodArgs");
			String lMethodName = req.getParameter("methodName");
			// Get the column value to display
			Object value = rowData.get(msColumn.getHeading());
			if (value==null) {
				fow.writeVerbatim(" "); // NB Space
			} else {
				String stringValue = value.toString().trim();
				if (stringValue.length()==0) {
					fow.writeVerbatim(" ");	// NB Space
				}else if (lMethodName.equals("patientPopupRefReq")) {	//For patient popup in refillReq wizard. Modified by uks.
					fow.writeVerbatim( "<a href=\"javascript:patientPopupRefReq('" + rowData.get("referencePath") +
							"','"+req.getParameter("element")+"')\">" );
					fow.writeEscape( stringValue );
					fow.writeVerbatim("</a>");
					if(null != rowData.get("sourceAccountId")) {
						AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
						Long accountId = accountUser.getAccount().getId();
						if(Long.parseLong(rowData.get("sourceAccountId").toString()) != (accountId)) {
							fow.writeVerbatim(" <img src='../images/vcard.gif' class='shareInfo' title='Data shared from account:"+rowData.get("sourceAccountId")+"'/>");						
						}
					}
				}
				
				else if (msColumn.isReference()) {
					fow.writeVerbatim( "<a href=\"javascript:showPane('" + rowData.get("referencePath") +
							"',false,'"+req.getParameter("element")+"')\">" );
					fow.writeEscape( stringValue );
					fow.writeVerbatim("</a>");
					if(null != rowData.get("sourceAccountId")) {
						AccountUser accountUser = (AccountUser) req.getAttribute("accountUser");
						Long accountId = accountUser.getAccount().getId();
						if(Long.parseLong(rowData.get("sourceAccountId").toString()) != (accountId)) {
							fow.writeVerbatim(" <img src='../images/vcard.gif' class='shareInfo' title='Data shared from account:"+rowData.get("sourceAccountId")+"'/>");						
						}
					}
				} else if (msColumn.isInstantiate() && !lMethodArgs.contains("enableCopy")) {
					String instantiateValue="";
					
					String path=(String)rowData.get( "path" );
					String element = req.getParameter("element");
					
					if (lMethodName != null && lMethodName.length() > 0)
					{
						long id;
						id = (Long)rowData.get( "id" );
						MenuData md1 = menuLocal.findMenuDataItem(id);
						if (lMethodArgs.contains("withCode")) 
							instantiateValue =  "<span style='float:left;width:100px;'>"+md1.getString02().split("-")[1]+"</span><span style='float:left;'>";
						instantiateValue +=  "<a title='"+md1.getString06()+"' href=\"#\" onclick=\"" + lMethodName + "('" + md1.getString02() + "','"+ element+ "','" + lMethodArgs + "')\">";
					}else{
						instantiateValue =  "<a href=\"#\" onclick=\"instantiate('" + path + "','"+ element+ "')\">";	
					}		
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
					if (lMethodArgs.contains("withCode")) 
						fow.writeVerbatim("</span>");
				} else if(lMethodArgs != null && lMethodArgs.contains("enableCopy")){
					/* this to populate a grid which will be used to create favorites lists
					 basically on clicking an item in the grid a custom script function will be called */
					
					//chop off the argument 'enableCopy' and replace , with ','
					lMethodArgs = lMethodArgs.replaceAll("enableCopy,","").replaceAll(",","','");
					
					String path=(String)rowData.get( "path" );
					String instantiateValue =  "<a id='"+path+"' href=\"javascript:" + lMethodName + "('" + path + "','"+lMethodArgs+"')\">";
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
				} else  if(lMethodName != null && lMethodName.length() > 0){ // may be this should be combined with the above if
					String instantiateValue="";
					String element = req.getParameter("element");
					long id = (Long)rowData.get( "id" );
					MenuData md1 = menuLocal.findMenuDataItem(id);
					instantiateValue =  "<a href=\"#\" onclick=\"" + lMethodName + "('" + md1.getString02() + "','"+ element+ "','" + lMethodArgs + "')\">";
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
				}else{
					fow.writeEscape( stringValue );
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error writing column " + msColumn, e);
		}
	}
	
	public class HTMLOutputWriter extends FormattedOutputWriter {
		public HTMLOutputWriter(Writer writer, String type, AccountUser accountUser, Date now) {
			super(writer, type, accountUser, now);
		}

	/**
	 * Write a character adding escape characters for things like < >
	 * @param writer
	 * @param val
	 * @throws IOException 
	 */
	@Override
	public void writeEscape( char c ) throws IOException {
	    	switch ( c ) {
	    	case '<' : writer.write( "&lt;");break;
	    	case '>' : writer.write( "&gt;");break;
	    	case '&' : writer.write( "&amp;");break;
	    	case '"' : writer.write( "&quot;");break;
	    	default : writer.write(c);
		}
	}


	}
	
	/** Method to find the favorites Lists
	 * @param path - the menuStructure path of the favorites
	 * @return
	 */
	public List<Map<String, Object>> findFavorites(String elment,AccountUser accountUser,MenuLocal menuLocal,MenuPath targetMenuPath) {		
		List<Map<String, Object>> favorites = new ArrayList<Map<String, Object>>();
		
		String favoritePathString = accountUser.getProperty().get("favoritePath");
		if(favoritePathString != null){
			String[] favoritesPaths = favoritePathString.split(",") ; 
			for(int i=0;i<favoritesPaths.length;i++){
				MenuPath favPath = new MenuPath(favoritesPaths[i]);
				MenuStructure ms;
				String menuPath = targetMenuPath.getPath();
				String node = menuPath.substring(menuPath.lastIndexOf(':')+1);
				
				try {
					ms = menuLocal.findMenuStructure( accountUser.getAccount().getId(), favPath.getPath() );
					MenuQueryControl ctrl = new MenuQueryControl();
					ctrl.setMenuStructure( ms.getAccountMenuStructure() );
					ctrl.setAccountUser(accountUser);
					ctrl.setNow( new Date() );
					ctrl.setOriginalTargetPath( favPath);
					ctrl.setRequestedPath( favPath );
					MDQueryResults data= menuLocal.findMenuDataByColumns(ctrl); 
					
					if(data != null){
						//identify the favorites type
						for(Map<String, Object> result:data.getResults()){
							if(elment.indexOf((String)result.get("referencePath"))> -1){
								node = (String)result.get("Type");
								break;
							}
						}				
						// pick only the elligible favorites
						for(Map<String, Object> result:data.getResults()){
							if(result.get("Type").equals(node) )
								favorites.add(result);
						}
					}
				} catch (Exception e) {
					TolvenLogger.info( "Error finding favorties for "+targetMenuPath.getPath(), MenuAction.class);
					e.printStackTrace();
				}
			}
			
		}		
		return favorites;
	}
	/**
	 * This function is used to get CE from a valueSet based on CE display name.
	 * @param valueSetName
	 * @param ceDisplayName
	 * @param trim
	 * @return exactCE
	 */
	public CE getCE(String valueSetName, String ceDisplayName,TrimEx trim) {
		CE ce = new CE();
		CE exactCE = new CE();
		Map<String, ValueSet> valueSetMap =new HashMap<String, ValueSet>();
		List<ValueSet> valueSets = trim.getValueSets();
		
		for (Object vs : valueSets) {
			ValueSet valueSet = (ValueSet) vs;
			valueSetMap.put(valueSet.getName(), valueSet);
		}
		
		for(Object obj : valueSetMap.get(valueSetName).getBindsAndADSAndCDS()) {
			try {
				if (obj instanceof CE) {
					ce = (CE) obj;
					
					if (ce.getDisplayName().equals(ceDisplayName)) {    
						exactCE = ce;					
						break;
					}
				}
			}
			catch (Exception e) {
				TolvenLogger.info(e.getMessage(), this.getClass());
				e.printStackTrace();
			}
		}
		
		return exactCE;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
   
}
