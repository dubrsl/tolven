package org.tolven.deploy.pharmacies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.tolven.client.load.TolvenLoadClient;
import org.tolven.core.entity.Property;
import org.tolven.logging.TolvenLogger;
import org.tolven.surescripts.PharmacyRemote;
import org.tolven.surescripts.PharmacyVO;

public class UploadPharmacies extends TolvenLoadClient {
	
	
	private static final String STATUS_NEW = "new";
	private PharmacyRemote pharmBean;
	private String pharmacyId;

	public UploadPharmacies(String configDir) throws IOException {
		super(configDir);
	}
	
	/**
	 * @return the pharmacyId
	 */
	public String getPharmacyId() {
		return pharmacyId;
	}

	/**
	 * @param pharmacyId the pharmacyId to set
	 */
	public void setPharmacyId(String pharmacyId) {
		this.pharmacyId = pharmacyId;
	}

	/**
	 * Upload pharmacies.
	 * @param userId
	 * @param password
	 * @param authFile
	 * @param jndiProperties
	 * @throws Exception
	 */
	public void load(String userId, char[] password, File authFile,
			Properties jndiProperties) throws Exception {
		try {
			login(userId, password, authFile.getPath(), jndiProperties);
			BufferedReader reader;
			String status = "no";
			 TolvenLogger.info("Downloading Weekly Pharmacy Flat File form SureScripts.", LoadPharmacies.class);
			 status= getPharmacyBean().downloadFlatFile();
			 TolvenLogger.info("Download Completed. "+ status, LoadPharmacies.class);

			if (status.equals("yes")) {
				String fileName = getProperty("eprescription.surescripts.download.directory")+ "/weekly_full/FlatFile.txt";
				TolvenLogger.info("Loading. . . .Please Wait ", LoadPharmacies.class);
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(fileName)), "UTF-8"));
				String record;
				int rowCount = 0;
				getPharmacyBean().changeStatusToDeleted();

				while (reader.ready()) {
					rowCount++;
					record = reader.readLine();
					
					PharmacyVO pharmacyVO = new PharmacyVO();					
					setPharmacyData(pharmacyVO, record);
					setServiceLevel(pharmacyVO);
					pharmacyVO.setStatus(STATUS_NEW);
					getPharmacyBean().addPharmacy(pharmacyVO);
				}
				TolvenLogger.info("Count of Pharmacies Uploaded: "+ rowCount, LoadPharmacies.class);
			} else {
				TolvenLogger.info("No Updates Available in SureScripts.", LoadPharmacies.class);
				status = "no";
			}
		} catch (Exception e) {
			TolvenLogger.error("Exception occured while adding pharmacy. "+ getPharmacyId(), LoadPharmacies.class);
			e.printStackTrace();
		} finally {
			logout();
		}
	}

	/**
	 * Sets the service level of pharmacy.
	 * @param pharmacy
	 */
	private void setServiceLevel(PharmacyVO pharmacyVO) {
		
		Long serviceCode = pharmacyVO.getServiceLevel();
		pharmacyVO.setNewRx(false);
		pharmacyVO.setRefReq(false);
		pharmacyVO.setRxChg(false);
		pharmacyVO.setRxFill(false);
		pharmacyVO.setCanRx(false);
		pharmacyVO.setMedicatioHistory(false);
		pharmacyVO.setEligibility(false);

		if ((serviceCode & 1) == 1) {
			pharmacyVO.setNewRx(true);
		}
		if ((serviceCode & 2) == 2) {
			pharmacyVO.setRefReq(true);
		}
		if ((serviceCode & 4) == 4) {
			pharmacyVO.setRxChg(true);
		}
		if ((serviceCode & 8) == 8) {
			pharmacyVO.setRxFill(true);
		}
		if ((serviceCode & 16) == 16) {
			pharmacyVO.setCanRx(true);
		}
		if ((serviceCode & 32) == 32) {
			pharmacyVO.setMedicatioHistory(true);
		}
		if ((serviceCode & 64) == 64) {
			pharmacyVO.setEligibility(true);
		}
	}
	
	/**
	 * Sets the data from flat file to Pharmacy object.
	 * @param pharmacyVO
	 * @param record
	 */
	private void setPharmacyData(PharmacyVO pharmacyVO, String record) {
		
		setPharmacyId(record.substring(0, 7).trim());
		pharmacyVO.setNcpdpid(record.substring(0, 7).trim());

		if (record.substring(7, 42).trim().length() > 0) {
			pharmacyVO.setStoreNumber(record.substring(7, 42).trim());
		}
		if (record.substring(42, 77).trim().length() > 0) {
			pharmacyVO.setReferenceNumberAlt1(Long.parseLong(record
					.substring(42, 77).trim()));
		}
		if (record.substring(77, 80).trim().length() > 0) {
			pharmacyVO.setReferenceNumberAlt1Qualifier(record
					.substring(77, 80).trim());
		}
		if (record.substring(80, 115).trim().length() > 0) {
			pharmacyVO.setStoreName(record.substring(80, 115).trim());
		}
		if (record.substring(115, 150).trim().length() > 0) {
			pharmacyVO.setAddressLine1(record.substring(115, 150)
					.trim());
		}
		if (record.substring(150, 185).trim().length() > 0) {
			pharmacyVO.setAddressLine2(record.substring(150, 185)
					.trim());
		}
		if (record.substring(185, 220).trim().length() > 0) {
			pharmacyVO.setCity(record.substring(185, 220).trim());
		}
		if (record.substring(220, 222).trim().length() > 0) {
			pharmacyVO.setState(record.substring(220, 222).trim());
		}
		if (record.substring(222, 233).trim().length() > 0) {
			pharmacyVO.setZip(Long.parseLong(record.substring(222,
					233).trim()));
		}
		if (record.substring(233, 258).trim().length() > 0) {
			pharmacyVO.setPhonePrimary(record.substring(233, 258)
					.trim());
		}
		if (record.substring(258, 283).trim().length() > 0) {
			pharmacyVO.setFax(record.substring(258, 283).trim());
		}
		if (record.substring(283, 363).trim().length() > 0) {
			pharmacyVO.setEmail(record.substring(283, 363).trim());
		}
		if (record.substring(363, 388).trim().length() > 0) {
			pharmacyVO
					.setPhoneAlt1(record.substring(363, 388).trim());
		}
		if (record.substring(388, 391).trim().length() > 0) {
			pharmacyVO.setPhoneAlt1Qualifier(record.substring(388,
					391).trim());
		}
		if (record.substring(391, 416).trim().length() > 0) {
			pharmacyVO.setPhoneAlt2(record.substring(391, 416).trim());
		}
		if (record.substring(416, 419).trim().length() > 0) {
			pharmacyVO.setPhoneAlt2Qualifier(record.substring(416,
					419).trim());
		}
		if (record.substring(419, 444).trim().length() > 0) {
			pharmacyVO.setPhoneAlt3(record.substring(419, 444).trim());
		}
		if (record.substring(444, 447).trim().length() > 0) {
			pharmacyVO.setPhoneAlt3Qualifier(record.substring(444,
					447).trim());
		}
		if (record.substring(447, 472).trim().length() > 0) {
			pharmacyVO.setPhoneAlt4(record.substring(447, 472).trim());
		}
		if (record.substring(472, 475).trim().length() > 0) {
			pharmacyVO.setPhoneAlt4Qualifier(record.substring(472,
					475).trim());
		}
		if (record.substring(475, 500).trim().length() > 0) {
			pharmacyVO.setPhoneAlt5(record.substring(475, 500).trim());
		}
		if (record.substring(500, 503).trim().length() > 0) {
			pharmacyVO.setPhoneAlt5Qualifier(record.substring(500,
					503).trim());
		}
		if (record.substring(503, 525).trim().length() > 0) {
			pharmacyVO.setActiveStartTime(parseDate(record.substring(
					503, 525).trim()));
		}
		if (record.substring(525, 547).trim().length() > 0) {
			pharmacyVO.setActiveEndTime(parseDate(record.substring(
					525, 547).trim()));
		}
		if (record.substring(547, 552).trim().length() > 0) {
			pharmacyVO.setServiceLevel(Long.parseLong(record
					.substring(547, 552).trim()));
		}
		if (record.substring(552, 587).trim().length() > 0) {
			pharmacyVO.setPartnerAccount(record.substring(552, 587)
					.trim());
		}
		if (record.substring(587, 609).trim().length() > 0) {
			pharmacyVO.setLastModifiedDate(parseDate(record
					.substring(587, 609).trim()));
		}
		if (record.substring(609, 610).trim().length() > 0) {
			pharmacyVO.setTwentyFourHourFlag(record.substring(609,
					610).trim());
		}
		if (record.substring(610, 645).trim().length() > 0) {
			pharmacyVO.setCrossStreet(record.substring(610, 645)
					.trim());
		}
		if (record.substring(645, 646).trim().length() > 0) {
			pharmacyVO.setRecordChange(record.substring(645, 646)
					.trim());
		}
		if (record.substring(646, 651).trim().length() > 0) {
			pharmacyVO.setOldServiceLevel(Long.parseLong(record
					.substring(646, 651).trim()));
		}
		if (record.substring(651, 751).trim().length() > 0) {
			pharmacyVO.setTextServiceLevel(record.substring(651, 751)
					.trim());
		}
		if (record.substring(751, 851).trim().length() > 0) {
			pharmacyVO.setTextServiceLevelChange(record.substring(
					751, 851).trim());
		}
		if (record.substring(851, 856).trim().length() > 0) {
			pharmacyVO.setVersion(record.substring(851, 856).trim());
		}
		if (record.substring(856, 866).trim().length() > 0) {
			pharmacyVO.setNpi(Long.parseLong(record.substring(856,
					866).trim()));
		}
	}

	/**
	 * Converts string to Date object. 
	 * @param string
	 * @return
	 */
	private Date parseDate(String date) {
		StringBuffer inputDate = new StringBuffer(date);
		if (inputDate.charAt(10) == 'T') {
			inputDate.replace(10, 11, "");
		}
		if (inputDate.charAt(inputDate.length() - 1) == 'Z') {
			inputDate.replace(inputDate.length() - 1, inputDate.length(), "");
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.F");
		Date newDate = null;
		try {
			newDate = df.parse(inputDate.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
	}

	private List<Property> findProperties() {
		try {
			List<Property> properties = null;
			properties = getPropertyBean().findProperties();
			return properties;
		} catch (Exception ex) {
			throw new RuntimeException("Could not find server properties", ex);
		}
	}

	public String getProperty(String name) {
		String propertyValue = null;
		for (Property property : findProperties()) {
			if (property.getName().equals(name)) {
				propertyValue = property.getValue();
				return propertyValue;
			}
		}
		return propertyValue;
	}

	public PharmacyRemote getPharmacyBean() {
		if (pharmBean == null) {
			try {
				pharmBean = (PharmacyRemote) getCtx().lookup(
						"tolven/PharmacyBean/remote");
			} catch (NamingException ex) {
				throw new RuntimeException(
						"Failed to look up tolven/PharmacyBean/remote", ex);
			}
		}
		return pharmBean;
	}
}
