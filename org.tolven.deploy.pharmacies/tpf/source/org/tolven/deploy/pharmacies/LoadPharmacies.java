package org.tolven.deploy.pharmacies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.client.load.TolvenLoadClient;
import org.tolven.core.entity.Property;
import org.tolven.logging.TolvenLogger;
import org.tolven.surescripts.PharmacyRemote;

public class LoadPharmacies extends TolvenLoadClient {
	public static final int NCPDPID = 0;
	public static final int StoreName = 1;
	public static final int AddressLine1 = 2;
	public static final int City = 3;
	public static final int State = 4;
	public static final int Zip = 5;
	public static final int PrimaryPhone = 6;
	public static final int Fax = 7;
	public static final int Email = 8;
	public static final int PhoneAlt1 = 9;
	public static final int PhoneAlt1Qualifier = 10;
	public static final int ActiveStartTime = 11;
	public static final int ActiveEndTime = 12;
	public static final int ServiceLevel = 13;
	public static final int PartnerAccount = 14;
	public static final int LastModifedDate = 15;
	public static final int NPI= 16;
	
	List<String> trims = new ArrayList<String>();
	private PharmacyRemote pharmBean;

	public LoadPharmacies(String configDir) throws IOException {
		super(configDir);
	}

	public void generateTrim(String fields[], XMLStreamWriter writer)
			throws XMLStreamException {
		writer.writeStartElement("trim");
		writer.writeNamespace(null, "urn:tolven-org:trim:4.0");

		writer.writeStartElement("extends");
		writer.writeCharacters("obs/evn/pharmacy");
		writer.writeEndElement(); // extends

		writer.writeStartElement("name");
		writer.writeCharacters("pharmacy/" + fields[NCPDPID]);
		writer.writeEndElement(); // name

		writer.writeStartElement("description");
		writer.writeCharacters(fields[StoreName]);
		writer.writeEndElement();// description

		writeAct(fields, writer);

		writer.writeEndElement();// Trim
	}

	public static void writeAct(String fields[], XMLStreamWriter writer)
			throws XMLStreamException {
		writer.writeStartElement("act");
		writer.writeAttribute("classCode", "OBS");
		writer.writeAttribute("modeCode", "EVN");
		writeCode(fields, writer);
		writer.writeEndElement();
	}

	public static void writeCode(String fields[], XMLStreamWriter writer)
			throws XMLStreamException {
		writer.writeStartElement("observation");
		{
			writer.writeStartElement("value");
			{
				writer.writeStartElement("CE");
				{
					writer.writeStartElement("displayName");
					writer.writeCharacters(fields[StoreName]);
					writer.writeEndElement();
					writer.writeStartElement("code");
					writer.writeCharacters(fields[NCPDPID]);
					writer.writeEndElement();
					writer.writeStartElement("codeSystem");
					writer.writeCharacters("2.16.840.1.113883.6.96");
					writer.writeEndElement();
					writer.writeStartElement("codeSystemName");
					writer.writeCharacters("SNOMED-CT");
					writer.writeEndElement();
				}
				writer.writeEndElement();// ce
			}
			writer.writeEndElement();// value

			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("AddressLine1");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[AddressLine1]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// value

			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("City");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[City]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// value

			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("State");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[State]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// value

			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("Zip");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[Zip]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// value
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("PrimaryPhone");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[PrimaryPhone]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// value
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("Fax");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[Fax]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// value
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("Email");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[Email]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// Email
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("PhoneAlt1");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[PhoneAlt1]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// PhoneAlt1
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("PhoneAlt1Qualifier");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[PhoneAlt1Qualifier]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// PhoneAlt1Qualifier
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("ActiveStartTime");
				writer.writeEndElement();
				writer.writeStartElement("TS");
				writer.writeStartElement("value");
				writer.writeCharacters(fields[ActiveStartTime]);
				writer.writeEndElement();
				writer.writeEndElement();
			}
			writer.writeEndElement();// ActiveStartTime
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("ActiveEndTime");
				writer.writeEndElement();
				writer.writeStartElement("TS");
				writer.writeStartElement("value");
				writer.writeCharacters(fields[ActiveEndTime]);
				writer.writeEndElement();
				writer.writeEndElement();

			}
			writer.writeEndElement();// ActiveEndTime
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("ServiceLevel");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[ServiceLevel]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// ServiceLevel
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("PartnerAccount");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[PartnerAccount]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// PartnerAccount
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("LastModifedDate");
				writer.writeEndElement();
				writer.writeStartElement("TS");
				writer.writeStartElement("value");
				writer.writeCharacters(fields[LastModifedDate]);
				writer.writeEndElement();
				writer.writeEndElement();
			}
			writer.writeEndElement();// LastModifedDate
			
			writer.writeStartElement("value");
			{
				writer.writeStartElement("label");
				writer.writeCharacters("NPI");
				writer.writeEndElement();
				writer.writeStartElement("ST");
				writer.writeCharacters(fields[NPI]);
				writer.writeEndElement();
			}
			writer.writeEndElement();// NPI
		}
		writer.writeEndElement();// observation
	}

	/**
	 * Load pharmacies.
	 * 
	 * @param userId
	 * @param password
	 * @param authFile
	 * @param jndiProperties
	 * @throws Exception
	 */
	public void load(String userId, char[] password,File authFile, Properties jndiProperties) throws Exception {
		try {
			login(userId, password, authFile.getPath(), jndiProperties);
			BufferedReader reader;
			String status="no";
			TolvenLogger.info("*****DOWNLOADING WEEKLY PHARMACY DOWNLOADS FROM SURESCRIPTS*****", LoadPharmacies.class);
			status= getPharmacyBean().downloadFlatFile();
			TolvenLogger.info("*****DOWNLOAD ACTION COMPLETED FOR WEEKLY PHARMACY DOWNLOADS*****" + status, LoadPharmacies.class);
				
			if(status.equals("yes")){
				
				/* Code for Weekly Pharmacy Downloads */
				String fileName = getProperty("eprescription.surescripts.download.directory")+"/weekly_full/FlatFile.txt"; 
				TolvenLogger.info("*****LOADING WEEKLY UPDATES FOR PHARMACIES FROM :"+ fileName, LoadPharmacies.class);
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(fileName)), "UTF-8"));
				String record;
				String fields[] = new String[17];
				int rowCount = 0;
				while (reader.ready()) {
					rowCount++;
					record = reader.readLine();
					// Might break out early is requested
					if (rowCount > getIterationLimit()) {
						TolvenLogger.info("*****UPLOAD STOPPED EARLY DUE TO "+ UPLOAD_LIMIT + " PROPERTY BEING SET*****",LoadPharmacies.class);
						break;
					}
//					 Skip the heading line
					if (rowCount == 1)
						continue;
					int i;
					for (i = 0; i < record.length(); i++) {
						fields[NCPDPID] = record.substring(0, 7).trim();
						fields[StoreName] = record.substring(80, 115).trim();
						fields[AddressLine1] = record.substring(115, 150).trim();
						fields[City] = record.substring(185, 220).trim();
						fields[State] = record.substring(220, 222).trim();
						fields[Zip] = record.substring(222, 233).trim();
						fields[PrimaryPhone] = record.substring(233, 258); 
						fields[Fax] = record.substring(258, 283).trim();
						
						fields[Email] = record.substring(283, 363).trim();
						fields[PhoneAlt1] = record.substring(363, 388).trim();
						fields[PhoneAlt1Qualifier] = record.substring(388, 391).trim();
						fields[ActiveStartTime] = record.substring(503, 525).trim();
						fields[ActiveEndTime] = record.substring(525, 547).trim();
						fields[ServiceLevel] = record.substring(547, 552).trim();
						fields[PartnerAccount] = record.substring(552, 587).trim();
						fields[LastModifedDate] = record.substring(587, 609).trim();
						fields[NPI] = record.substring(856, 866).trim();
					}
	
					StringWriter bos = new StringWriter();
					XMLOutputFactory factory = XMLOutputFactory.newInstance();
					XMLStreamWriter writer = factory.createXMLStreamWriter(bos);
					writer.writeStartDocument("UTF-8", "1.0");
					generateTrim(fields, writer);
					writer.writeEndDocument();
					// writer.flush();
					writer.close();
					bos.close();
					trims.add(bos.toString());
					if (trims.size() >= 10) {
						getTrimBean().createTrimHeaders(
								trims.toArray(new String[0]), null,
								"LoadPharmacies", true);
						trims.clear();
					}
					TolvenLogger.info(bos.toString(), LoadPharmacies.class);
				}
				// Send unfinished batch, if any.
				if (trims.size() > 0) {
					getTrimBean().createTrimHeaders(trims.toArray(new String[0]),
							null, "LoadPharmacies", true);
				}
				TolvenLogger.info("*****COUNT OF PHARMACIES UPLOADED: " + (rowCount - 1),LoadPharmacies.class);
				TolvenLogger.info("*****ACTIVATING TRIMHEADERS*****", LoadPharmacies.class);
				getTrimBean().queueActivateNewTrimHeaders();
			}else{
				TolvenLogger.info("*****NO UPDATES AVAILABLE IN SURESCRIPTS*****", LoadPharmacies.class);
				status="no";
			}
		} finally {
			logout();
		}
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
        	if(property.getName().equals(name)){
        		propertyValue = property.getValue();
        		return propertyValue;
        	}
        }
		return propertyValue;
	}
	
	public PharmacyRemote getPharmacyBean() {
        if (pharmBean == null) {
            try {
            	pharmBean = (PharmacyRemote) getCtx().lookup("tolven/PharmacyBean/remote");
            } catch (NamingException ex) {
                throw new RuntimeException("Failed to look up tolven/PharmacyBean/remote", ex);
            }
        }
        return pharmBean;
    }
	
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Arguments: configDirectory");
			return;
		}
		LoadPharmacies la = new LoadPharmacies(args[0]);
		la.login("admin", "sysadmin");
		//la.load(args[0]);
	}
}
