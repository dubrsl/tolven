package org.tolven.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.tolven.app.CCHITLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.util.CchitUtils;
import org.tolven.app.util.DateUtils;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.CE;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.ValueSet;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.ValueSetEx;

public class CCHITOrderCompute extends CCHITComputeBase {

	private ActEx act;
	private TrimEx trim;
	private ValueSetEx encounterTemplate,contactTemplate;
	private ValueSetEx encounter, contact;
	private List<String> trimPaths = new ArrayList<String>(1);
	private List<String> trimTimestamps = new ArrayList<String>(1);

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	private boolean enabled;
	private String action;


	public void compute() throws Exception {
		
		if (isEnabled()) {
			TolvenLogger.info("-------------> Compute enabled for CCHITOrders:",CCHITOrderCompute.class);
			setTrim((TrimEx) this.getTrim());
			
			try {
				if (getAction().equals("labOrder")
						|| getAction().equals("imageOrder")
						|| getAction().equals("referralRequest")
						|| getAction().equals("pxDoc")
						|| getAction().equals("immunizationOrder")
						|| getAction().equals("Problems")
						|| getAction().equals("dxDoc")) {

					encounterTemplate = getValueSetEx(trim, "encounterTemplate");
					encounter = getValueSetEx(trim, "encounter");
					insertPriorData("echr:patient:encounters:active","DateSort=DESC", "Encounter", encounterTemplate,encounter);
				}

				if (getAction().equals("smokingAssmt")|| getAction().equals("vitalAssessment")) {
					encounterTemplate = getValueSetEx(trim, "encounterTemplate");
					encounter = getValueSetEx(trim, "encounter");
					insertPriorData("echr:patient:encounters:active","DateSort=DESC", "Encounter", encounterTemplate,encounter);
				}

				if (getAction().equals("Progressnotes")) {
					checkAndSetSharingInfo();
					encounterTemplate = getValueSetEx(trim, "encounterTemplate");
					encounter = getValueSetEx(trim, "encounter");
					insertPriorData("echr:patient:encounters:active","DateSort=DESC", "Encounter", encounterTemplate,encounter);

					MenuQueryControl ctrl = CchitUtils.getMenuQueryControl("echr:patient:observations:all", getMenuBean(), getAccountUser(), getContextList());
					ctrl.setSortOrder("date01");
					ctrl.setSortDirection("DESC");
					List<MenuData> items = getMenuBean().findMenuData(ctrl);
					String contextPath = getContextList().get(0).getPathString();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("menuPath", "echr:patient:observations:all");
					params.put("contextPath", contextPath);
					params.put("conditions","TestFilter=Temperature:DateSort=DESC");
					params.put("accountUser", getAccountUser());
					// the following call has to be modified once we figure out how to send
					// userprivatekey
					TrimEx trimEx = getCCHITBean().findTrimData(params, null);
					if (trimEx != null) {
						trimEx.getAct();
						Double PQ = trimEx.getAct().getObservation().getValues().get(0).getPQ().getValue();
						String date = trimEx.getAct().getEffectiveTime().getTS().getValue();
						Calendar cal = Calendar.getInstance();
						String dateFormat = "yyyyMMdd";
						SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
						Date compareDate = DateUtils.stringToDate(date,"yyyyMMddhhmm");
						String dateFormatnew = "yyyyMMdd";
						SimpleDateFormat sdfnew = new SimpleDateFormat(dateFormatnew);
						/*
						 * Temperature data will be pre-populated from data
						 * captured in the patient intake assessment if one was
						 * undertaken on the same day.
						 */
						if (items != null && items.size() > 0 && ((sdf.format(cal.getTime())).equals(sdfnew.format(compareDate)))) {
							MenuData item = items.get(0);
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("temperature")
									.setEnabled(true);
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("temperature")
									.getAct().getObservation().getValues().get(
											0).getPQ().setValue(PQ);
						} else {
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("temperature")
									.setEnabled(false);
						}
					} else {
						((ActEx) ((ActEx) this.getAct()).getRelationship().get(
								"objective").getAct()).getRelationship().get(
								"temperature").setEnabled(false);
					}
					Map<String, Object> params1 = new HashMap<String, Object>();
					params1.put("menuPath", "echr:patient:observations:all");
					params1.put("contextPath", contextPath);
					params1.put("conditions", "TestFilter=Pulse:DateSort=DESC");
					params1.put("accountUser", getAccountUser());
					TrimEx trimEx1 = getCCHITBean().findTrimData(params1, null);
					if (trimEx1 != null) {
						trimEx1.getAct();
						Double PQ1 = trimEx1.getAct().getObservation()
								.getValues().get(0).getPQ().getValue();
						String date1 = trimEx1.getAct().getEffectiveTime()
								.getTS().getValue();
						Calendar cal1 = Calendar.getInstance();
						String dateFormat1 = "yyyyMMdd";
						SimpleDateFormat sdf1 = new SimpleDateFormat(
								dateFormat1);
						Date compareDate1 = DateUtils.stringToDate(date1,
								"yyyyMMddhhmm");
						String dateFormatnew1 = "yyyyMMdd";
						SimpleDateFormat sdfnew1 = new SimpleDateFormat(
								dateFormatnew1);
						/*
						 * Pulse data will be pre-populated from data captured
						 * in the patient intake assessment if one was
						 * undertaken on the same day.
						 */
						if (items != null
								&& items.size() > 0
								&& ((sdf1.format(cal1.getTime()))
										.equals(sdfnew1.format(compareDate1)))) {
							MenuData item = items.get(0);
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("pulse").setEnabled(
											true);
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("pulse").getAct()
									.getObservation().getValues().get(1)
									.getST().setValue(PQ1.toString());
						} else {
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("pulse").setEnabled(
											false);
						}
					} else {
						((ActEx) ((ActEx) this.getAct()).getRelationship().get(
								"objective").getAct()).getRelationship().get(
								"pulse").setEnabled(false);
					}
					Map<String, Object> params2 = new HashMap<String, Object>();
					params2.put("menuPath", "echr:patient:observations:all");
					params2.put("contextPath", contextPath);
					params2.put("conditions",
							"TestFilter=Respiration:DateSort=DESC");
					params2.put("accountUser", getAccountUser());
					TrimEx trimEx2 = getCCHITBean().findTrimData(params2,null);
					if (trimEx2 != null) {
						trimEx2.getAct();
						Double PQ2 = trimEx2.getAct().getObservation()
								.getValues().get(0).getPQ().getValue();
						String date2 = trimEx2.getAct().getEffectiveTime()
								.getTS().getValue();
						Calendar cal2 = Calendar.getInstance();
						String dateFormat2 = "yyyyMMdd";
						SimpleDateFormat sdf2 = new SimpleDateFormat(
								dateFormat2);
						Date compareDate2 = DateUtils.stringToDate(date2,
								"yyyyMMddhhmm");
						String dateFormatnew2 = "yyyyMMdd";
						SimpleDateFormat sdfnew2 = new SimpleDateFormat(
								dateFormatnew2);
						/*
						 * Respiration Rate data will be pre-populated from data
						 * captured in the patient intake assessment if one was
						 * undertaken on the same day.
						 */
						if (items != null
								&& items.size() > 0
								&& ((sdf2.format(cal2.getTime()))
										.equals(sdfnew2.format(compareDate2)))) {
							MenuData item = items.get(0);
							act = (ActEx) this.getAct();
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("respirationrate")
									.setEnabled(true);
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("respirationrate")
									.getAct().getObservation().getValues().get(
											0).getPQ().setValue(PQ2);
						} else {
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("respirationrate")
									.setEnabled(false);
						}
					} else {
						((ActEx) ((ActEx) this.getAct()).getRelationship().get(
								"objective").getAct()).getRelationship().get(
								"respirationrate").setEnabled(false);
					}
					Map<String, Object> params3 = new HashMap<String, Object>();
					params3.put("menuPath", "echr:patient:observations:all");
					params3.put("contextPath", contextPath);
					params3.put("conditions", "TestFilter=Blood:DateSort=DESC");
					params3.put("accountUser", getAccountUser());
					TrimEx trimEx3 = getCCHITBean().findTrimData(params3,null);
					if (trimEx3 != null) {
						trimEx3.getAct();
						Double PQ3 = ((ActEx) trimEx3.getAct())
								.getRelationship().get("systolic").getAct()
								.getObservation().getValues().get(0).getPQ()
								.getValue();
						String date3 = trimEx3.getAct().getEffectiveTime()
								.getTS().getValue();
						Calendar cal3 = Calendar.getInstance();
						String dateFormat3 = "yyyyMMdd";
						SimpleDateFormat sdf3 = new SimpleDateFormat(
								dateFormat3);
						Date compareDate3 = DateUtils.stringToDate(date3,
								"yyyyMMddhhmm");
						String dateFormatnew3 = "yyyyMMdd";
						SimpleDateFormat sdfnew3 = new SimpleDateFormat(
								dateFormatnew3);
						/*
						 * Blood Pressure-Systolic data will be pre-populated
						 * from data captured in the patient intake assessment
						 * if one was undertaken on the same day.
						 */
						if (items != null
								&& items.size() > 0
								&& ((sdf3.format(cal3.getTime()))
										.equals(sdfnew3.format(compareDate3)))) {
							MenuData item = items.get(0);
							act = (ActEx) this.getAct();
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("systolic")
									.setEnabled(true);
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("systolic").getAct()
									.getObservation().getValues().get(1)
									.getST().setValue(PQ3.toString());
						} else {
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("systolic")
									.setEnabled(false);
						}
					} else {
						((ActEx) ((ActEx) this.getAct()).getRelationship().get(
								"objective").getAct()).getRelationship().get(
								"systolic").setEnabled(false);
					}
					Map<String, Object> params4 = new HashMap<String, Object>();
					params4.put("menuPath", "echr:patient:observations:all");
					params4.put("contextPath", contextPath);
					params4.put("conditions", "TestFilter=Blood:DateSort=DESC");
					params4.put("accountUser", getAccountUser());
					TrimEx trimEx4 = getCCHITBean().findTrimData(params4, null);
					if (trimEx4 != null) {
						trimEx4.getAct();
						Double PQ4 = ((ActEx) trimEx4.getAct())
								.getRelationship().get("diastolic").getAct()
								.getObservation().getValues().get(0).getPQ()
								.getValue();
						String date4 = trimEx4.getAct().getEffectiveTime()
								.getTS().getValue();
						Calendar cal4 = Calendar.getInstance();
						String dateFormat4 = "yyyyMMdd";
						SimpleDateFormat sdf4 = new SimpleDateFormat(
								dateFormat4);
						Date compareDate4 = DateUtils.stringToDate(date4,
								"yyyyMMddhhmm");
						String dateFormatnew4 = "yyyyMMdd";
						SimpleDateFormat sdfnew4 = new SimpleDateFormat(
								dateFormatnew4);
						/*
						 * Blood Pressure-Diastolic data will be pre-populated
						 * from data captured in the patient intake assessment
						 * if one was undertaken on the same day.
						 */
						if (items != null
								&& items.size() > 0
								&& ((sdf4.format(cal4.getTime()))
										.equals(sdfnew4.format(compareDate4)))) {
							MenuData item = items.get(0);
							act = (ActEx) this.getAct();
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("diastolic")
									.setEnabled(true);
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("diastolic")
									.getAct().getObservation().getValues().get(
											1).getST().setValue(PQ4.toString());
						} else {
							((ActEx) ((ActEx) this.getAct()).getRelationship()
									.get("objective").getAct())
									.getRelationship().get("diastolic")
									.setEnabled(false);
						}
					} else {
						((ActEx) ((ActEx) this.getAct()).getRelationship().get(
								"objective").getAct()).getRelationship().get(
								"diastolic").setEnabled(false);
					}
				}

				if (getAction().equals("AdvanceDirectives")) {

					// To populate contacts to the wizard in a drop-down in the
					// ephr account.
					contactTemplate = getValueSetEx(trim, "contactTemplate");
					contact = getValueSetEx(trim, "contact");
					insertPriorData("ephr:patient:personal:contacts",
							"DateSort=DESC", "Contact", contactTemplate,
							contact);

					// To get the patient trim.
					String patientId = (trim.getAct().getParticipations()
							.get(0).getRole().getId().getIIS().get(0)
							.getExtension()).split(":")[1].split("-")[1];
					TrimEx priorPatientTrim = null;
					Long longPatientId = new Long(0);
					longPatientId = Long.parseLong(patientId);
					priorPatientTrim = getCCHITBean().findTrimData(
							longPatientId, getAccountUser(),null);

					// To show the patient name on advance directive wizard.
					String name = priorPatientTrim.getAct().getParticipations()
							.get(0).getRole().getPlayer().getName().getENS()
							.get(0).getParts().get(2).getST().getValue()
							+ ", "
							+ priorPatientTrim.getAct().getParticipations()
									.get(0).getRole().getPlayer().getName()
									.getENS().get(0).getParts().get(0).getST()
									.getValue()
							+ " "
							+ priorPatientTrim.getAct().getParticipations()
									.get(0).getRole().getPlayer().getName()
									.getENS().get(0).getParts().get(1).getST()
									.getValue();
					((ActEx) trim.getAct()).getRelationship().get(
							"powerOfAtterny").getAct().getObservation()
							.getValues().get(0).getST().setValue(name);

					// To show the patient's county on advance directive wizard.
					String county = priorPatientTrim.getAct()
							.getParticipations().get(0).getRole().getPlayer()
							.getPerson().getAddr().getADS().get(0).getParts()
							.get(2).getST().getValue();
					((ActEx) trim.getAct()).getRelationship().get(
							"powerOfAtterny").getAct().getObservation()
							.getValues().get(1).getST().setValue(county);

				}
			} catch (Exception e) {
				TolvenLogger.error(
						"-------------> Error in CCHIT compute. Error message: "
								+ e.getMessage(), CCHITOrderCompute.class);
				TolvenLogger.error(e.getMessage() + "<-->" + e.toString(),
						CCHITOrderCompute.class);
			}

			TolvenLogger.info(
					"-------------> Disabling compute for CCHITOrders:",
					CCHITOrderCompute.class);
		}
		disableCompute();
	}
	/**
	 * To write each ce to valueset
	 * @param newCode
	 * @param newDisplayName
	 * @param codeSystem
	 * @param codeSystemVersion
	 * @param vSet
	 */
	void writeCE(String newCode, String newDisplayName, String codeSystem, String codeSystemVersion,ValueSet vSet) {
		CE newCE = new CE();
		newCE.setCode(newCode);
		newCE.setDisplayName(newDisplayName);
		newCE.setCodeSystem(codeSystem);
		newCE.setCodeSystemVersion(codeSystemVersion);
		vSet.getBindsAndADSAndCDS().add(newCE);
		newCE = null;
	}

	/**
	 * To set receiver account information if the patient wants to receive
	 * information.
	 * 
	 * @author valsaraj added on 06/18/2010
	 * @param void
	 * @return void
	 */
	public void checkAndSetSharingInfo() {
		try {
			Object patDoc = getCCHITBean().findDoc(
					getContextList().get(0).getNodeKeys()[1], getAccountUser(),null);

			if (patDoc instanceof TrimEx) {
				TrimEx patTrim = (TrimEx) patDoc;

				if (((ActEx) ((ActEx) patTrim.getAct()).getRelationship().get(
						"preference").getAct()).getRelationship().get(
						"sendEncounter").isEnabled()) {
					List<ObservationValueSlot> obsSource = ((ActEx) patTrim
							.getAct()).getRelationship().get("preference")
							.getAct().getRelationships().get(0).getAct()
							.getRelationships().get(2).getAct()
							.getObservation().getValues();
					List<ObservationValueSlot> obsTarget = ((ActEx) getTrim()
							.getAct()).getRelationship().get("receiverInfo")
							.getAct().getObservation().getValues();
					obsTarget.get(0).setST(obsSource.get(0).getST());
					obsTarget.get(1).setST(obsSource.get(1).getST());
					((ActEx) getTrim().getAct()).getRelationship().get(
							"shareFields").getAct().getObservation()
							.getValues().get(0).getST()
							.setValue(getShareInfo());
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Returns share information.
	 * 
	 * @author valsaraj added on 06/22/2010
	 * @param void
	 * @return String
	 */
	public String getShareInfo() {
		String shareInfo = "";

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("menuPath", "echr:admin:providers:providerSettings");
			params.put("contextPath", "echr:admin:providers:providerSettings");
			params.put("conditions",
					"TitleFilter=Provider Settings:DateSort=DESC");
			params.put("accountUser", getAccountUser());
			TrimEx trimEx = getCCHITBean().findTrimData(params,null);

			try {
				shareInfo += ((ActEx) trimEx.getAct()).getRelationship().get(
						"diagnosis").isEnabled() != false ? "1" : "0";
			} catch (Exception e) {
				shareInfo += "1";
			}

			try {
				shareInfo += ((ActEx) trimEx.getAct()).getRelationship().get(
						"treatments").isEnabled() != false ? "1" : "0";
			} catch (Exception e) {
				shareInfo += "1";
			}

			try {
				shareInfo += ((ActEx) trimEx.getAct()).getRelationship().get(
						"medications").isEnabled() != false ? "1" : "0";
			} catch (Exception e) {
				shareInfo += "1";
			}

			try {
				shareInfo += ((ActEx) trimEx.getAct()).getRelationship().get(
						"referrals").isEnabled() != false ? "1" : "0";
			} catch (Exception e) {
				shareInfo += "1";
			}

			try {
				shareInfo += ((ActEx) trimEx.getAct()).getRelationship().get(
						"testOrdersAndResults").isEnabled() != false ? "1"
						: "0";
			} catch (Exception e) {
				shareInfo += "1";
			}
		} catch (Exception e) {

		}

		return shareInfo;
	}

	private ValueSetEx getValueSetEx(TrimEx trim, String name) {
		return (ValueSetEx) trim.getValueSet().get(name);
	}

	/**
	 * Returns latest trim in the given path with the given wizard name
	 * 
	 * @param path
	 * @param conditions
	 * @param wizardName
	 * @return
	 */
	private List<Map<String, Object>> getAllTrimData(String path,
			String conditions, String wizardName) {
		List<Map<String, Object>> list = null;
		try {
			list = getCCHITBean().findAllMenuDataList(path,
					getContextList().get(0).getPathString(), conditions,
					getAccountUser());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	private void insertPriorData(String path, String conditions,
			String wizardName, ValueSetEx template, ValueSetEx target) {
		List<Map<String, Object>> priorTrims = null;
		TrimEx priorTrim = null;
		priorTrims = getAllTrimData(path, conditions, wizardName);
		String displayName = null;
		if(priorTrims.size()==0 && !wizardName.equals("Contact")){
			displayName = "None";
			int cnt = 0;
			addToValueSet(template, target, displayName, cnt);
		}
		Long id = new Long(0);
		int count = 0;
		try {
			for (Map<String, Object> map : priorTrims) {
				id = new Long(map.get("id").toString());
				priorTrim = getCCHITBean().findTrimData(id, getAccountUser(),null);
				if (validateTrims(priorTrim)) {
					getCEDisplayName(priorTrim, wizardName, count, template,
							target);
					count++;
				}
				
			}
		} catch (Exception e) {
			TolvenLogger
					.error(
							"-------------> Error in loading prior datas for "
									+ wizardName + ". Error message: "
									+ e.getMessage(), CCHITOrderCompute.class);
			TolvenLogger.error(e.getMessage() + "<-->" + e.toString(),
					CCHITOrderCompute.class);
		}

	}

	/**
	 * Returns name to be displayed in valuesets in trim.
	 * 
	 * @param trim
	 * @param wizardName
	 * @return
	 */
	private String getCEDisplayName(TrimEx trim, String wizardName, int count,
			ValueSetEx template, ValueSetEx target) {
		String displayName = null;
		try {
			if (wizardName.equals("Encounter")) {
				String encYear = null;
				String encMonth = null;
				String encDate = null;
				if(trim.getDescription().equals("Ambulatory Encounter")){
					encYear = trim.getAct().getEffectiveTime().getTS().getValue().substring(2, 4);
					encMonth = trim.getAct().getEffectiveTime().getTS().getValue().substring(4, 6);
					encDate = trim.getAct().getEffectiveTime().getTS().getValue().substring(6,8);
				}
				else if(trim.getDescription().equals("Inpatient Encounter")){
					encYear = trim.getAct().getEffectiveTime().getIVLTS().getLow().getTS().getValue().substring(2, 4);
					encMonth = trim.getAct().getEffectiveTime().getIVLTS().getLow().getTS().getValue().substring(4, 6);
					encDate = trim.getAct().getEffectiveTime().getIVLTS().getLow().getTS().getValue().substring(6,8);
				}
				String time = encMonth+"/"+encDate+"/"+encYear;
				String encounter = time
						+ " "
						+ trim.getAct().getParticipations().get(1).getRole()
								.getPlayer().getName().getENS().get(0)
								.getParts().get(0).getST().getValue()
						+ " "
						+ trim.getAct().getParticipations().get(2).getRole()
								.getPlayer().getName().getENS().get(0)
								.getParts().get(0).getST().getValue();
				
				displayName = encounter;
				addToValueSet(template, target, displayName, count);
			} else if (wizardName.equals("Contact")) {
				String contact = trim.getAct().getRelationships().get(0)
						.getAct().getObservation().getValues().get(0).getST()
						.toString();
				displayName = contact;
				addToValueSet(template, target, displayName, count);
			}
		} catch (Exception e) {
			TolvenLogger.error(
					"-------------> Error in getting prior values for "
							+ wizardName, CCHITOrderCompute.class);
			TolvenLogger.error(e.getMessage() + "<-->" + e.toString(),
					CCHITOrderCompute.class);
		}
		return displayName;
	}

	/**
	 * 
	 * @param template
	 * @param destination
	 * @param displayName
	 * @param count
	 */
	private void addToValueSet(ValueSetEx template, ValueSetEx destination,
			String displayName, int count) {

		CE tempCE = (CE) template.getBindsAndADSAndCDS().get(0);
		String codeSystem = tempCE.getCodeSystem();
		String codeSystemVersion = tempCE.getCodeSystemVersion();
		String code = tempCE.getCode();
		String sCode = code.substring(0, 2);
		String iCode = code.substring(2, code.length());
		tempCE = null;

		String newCode = sCode + (Integer.parseInt(iCode) + count);

		CE destinationCE = new CE();
		destinationCE.setCode(newCode);
		destinationCE.setDisplayName(displayName);
		destinationCE.setCodeSystem(codeSystem);
		destinationCE.setCodeSystemVersion(codeSystemVersion);
		destination.getBindsAndADSAndCDS().add(destinationCE);
		destinationCE = null;
	}

	/**
	 * Checks weather the trim has already been processed
	 * 
	 * @param priorTrim
	 * @return
	 */
	private boolean validateTrims(TrimEx priorTrim) {
		boolean repeat = false;
		String trimPath = priorTrim.getTolvenEventIds().get(0).getId();
		String trimTimestamp = priorTrim.getTolvenEventIds().get(0)
				.getTimestamp();
		if (!trimPaths.isEmpty()) {
			for (String path : trimPaths) {
				if (path.equalsIgnoreCase(trimPath)) {
					for (String timeStamp : trimTimestamps) {
						if (timeStamp.equalsIgnoreCase(trimTimestamp)) {
							repeat = true;
						}
					}
				}
			}
		}
		if (repeat) {
			return false;
		} else {
			trimPaths.add(trimPath);
			trimTimestamps.add(trimTimestamp);
			return true;
		}
	}

	/**
	 * This function is used to disable compute.
	 */
	private void disableCompute() {
		for (Property property : getComputeElement().getProperties()) {
			if ("enabled".equals(property.getName())) {
				property.setValue(Boolean.FALSE);
			}
		}
	}

	public TrimEx getTrim() {
		return trim;
	}

	public void setTrim(TrimEx trim) {
		this.trim = trim;
	}

}
