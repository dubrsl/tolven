package org.tolven.deploy.growthChart;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.tolven.doc.DocumentRemote;
import org.tolven.growthchart.GrowthChartRemote;
import org.tolven.growthchart.entity.Lenageinf;
import org.tolven.growthchart.entity.Statage;
import org.tolven.growthchart.entity.Wtage;
import org.tolven.growthchart.entity.Wtageinf;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.tolven.restful.client.LoadRESTfulClient;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.tolven.logging.TolvenLogger;


/**
 * To load Growth Chart values
 * 
 * @author Nevin
 * added on 02/04/2011
 */
public class LoaderGrowthChart extends LoadRESTfulClient  {
	
	@SuppressWarnings("unchecked")
	private List sheetData;
	
	StringWriter bos = null;
	XMLOutputFactory factory = null;
	XMLStreamWriter writer = null;

	public LoaderGrowthChart(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }
	
	public void processGrowthChartAction(String xml) {
		
		WebResource webResource = getAppWebResource().path("growthChartLoader/createGrowthChart");
        ClientResponse response = webResource.cookie(getTokenCookie()).type(MediaType.APPLICATION_XML).entity(xml).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
        }
	  }
	
	@SuppressWarnings("unchecked")
	public void createElement(XMLStreamWriter writer, String elementName, String elementValue) throws Exception {
		writer.writeStartElement(elementName);
		writer.writeCharacters(elementValue);
		writer.writeEndElement(); // extends
	}
	
	
	public void cleanAction(String target) throws Exception
	{
		bos = new StringWriter();
		factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(bos);
		writer.writeStartDocument("ISO-8859-1", "1.0" );
		
		writer.writeStartElement("GrowthChart");
    	
    	createElement(writer,"action",target);
    	writer.writeEndElement();
    	writer.writeEndDocument();
		writer.close();
		bos.close();	
		System.out.println(bos.toString());
		processGrowthChartAction(bos.toString());
	}
	
	public void testGrowthChartAction(String xml) {
		WebResource webResource = getAppWebResource().path("growthChartLoader/createGrowthChart");
        ClientResponse response = webResource.cookie(getTokenCookie()).type(MediaType.APPLICATION_XML).entity(xml).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
        }
	  }
	
	@SuppressWarnings("unchecked")
	public void testGrowthChartAction() throws Exception 
	{
		
		bos = new StringWriter();
		factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(bos);
		writer.writeStartDocument("ISO-8859-1", "1.0" );
		
		writer.writeStartElement("GrowthChart");
    	
    	createElement(writer,"action","Cool");
    	writer.writeEndElement();
    	writer.writeEndDocument();
		writer.close();
		bos.close();	
		System.out.println(bos.toString());
			
		// Call API to post data to Restful API
		testGrowthChartAction(bos.toString());
        
	}
	
	public void loadData(File sourceFile,String clearString, String loadString) throws Exception 
	{
		
		if(sourceFile==null || clearString==null || "".equals(clearString) ||  loadString==null || "".equals(loadString) ) 
		{
			TolvenLogger.error("Required Data not found\n Source File:"+sourceFile+"\nclearString:"+clearString+"\nloadString:"+loadString,LoaderGrowthChart.class);
			return;
		}
		
		// Clear the database
		cleanAction(clearString);
	
		sheetData = new ArrayList();
		
		// Process Excel data file 
	    sheetData = getExcelData(sourceFile);
		for (int i = 0; i < sheetData.size(); i++) {
			bos = new StringWriter();
			factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(bos);
			writer.writeStartDocument("ISO-8859-1", "1.0" );
			
            List list = (List) sheetData.get(i);
            if (!list.get(0).toString().equals("Sex")) {
            	writer.writeStartElement("GrowthChart");
            	
            	// Process Attributes
	            createElement(writer,"action",loadString);
	           
	            //Growth Chart Data
	            if(loadString.indexOf("loadWt")==0)
	            {
	            	getWeightElementData(writer,list);
	            }
	            else
	            {
		            getHeightElementData(writer,list);
		            
		            if(loadString.equals("loadLenageinf"))
		            {
		            	//System.out.println("Setting Specific Data");
			            // Get Lenageinf specific data
		            	createElement(writer,"Pub3",""+((HSSFCell) list.get(14)).getNumericCellValue());
		            	createElement(writer,"Pub5",""+((HSSFCell) list.get(15)).getNumericCellValue());
		            	createElement(writer,"Pub10",""+((HSSFCell) list.get(16)).getNumericCellValue());
		            	createElement(writer,"Pub25",""+((HSSFCell) list.get(17)).getNumericCellValue());
		            	createElement(writer,"Pub50",""+((HSSFCell) list.get(18)).getNumericCellValue());
		            	createElement(writer,"Pub75",""+((HSSFCell) list.get(19)).getNumericCellValue());
		            	createElement(writer,"Pub90",""+((HSSFCell) list.get(20)).getNumericCellValue());
		            	createElement(writer,"Pub95",""+((HSSFCell) list.get(21)).getNumericCellValue());
		            	createElement(writer,"Pub97",""+((HSSFCell) list.get(22)).getNumericCellValue());
		            	createElement(writer,"Diff3",""+((HSSFCell) list.get(23)).getNumericCellValue());
		            	createElement(writer,"Diff5",""+((HSSFCell) list.get(24)).getNumericCellValue());
		            	createElement(writer,"Diff10",""+((HSSFCell) list.get(25)).getNumericCellValue());
		            	createElement(writer,"Diff25",""+((HSSFCell) list.get(26)).getNumericCellValue());
		            	createElement(writer,"Diff50",""+((HSSFCell) list.get(27)).getNumericCellValue());
		            	createElement(writer,"Diff75",""+((HSSFCell) list.get(28)).getNumericCellValue());
		            	createElement(writer,"Diff90",""+((HSSFCell) list.get(29)).getNumericCellValue());
		            	createElement(writer,"Diff95",""+((HSSFCell) list.get(30)).getNumericCellValue());
		            	createElement(writer,"Diff97",""+((HSSFCell) list.get(31)).getNumericCellValue());
		            }
	            }
            	writer.writeEndElement();
            	writer.writeEndDocument();
  				writer.close();
  				bos.close();	
  				//System.out.println(bos.toString());
  				
  				// Call API to post data to Restful API
  				processGrowthChartAction(bos.toString());
			} 
        }
	
	}
	
	public void getHeightElementData(XMLStreamWriter writer,List list)  throws Exception{
		createElement(writer,"Sex",""+((int)Double.parseDouble(list.get(0).toString())));
    	createElement(writer,"Agemonth",""+((HSSFCell) list.get(1)).getNumericCellValue());
        createElement(writer,"L",""+((HSSFCell) list.get(2)).getNumericCellValue());
        createElement(writer,"M",""+((HSSFCell) list.get(3)).getNumericCellValue());
        createElement(writer,"S",""+((HSSFCell) list.get(4)).getNumericCellValue());
    	createElement(writer,"P3",""+((HSSFCell) list.get(5)).getNumericCellValue());
    	createElement(writer,"P5",""+((HSSFCell) list.get(6)).getNumericCellValue());
    	createElement(writer,"P10",""+((HSSFCell) list.get(7)).getNumericCellValue());
    	createElement(writer,"P25",""+((HSSFCell) list.get(8)).getNumericCellValue());
    	createElement(writer,"P50",""+((HSSFCell) list.get(9)).getNumericCellValue());
    	createElement(writer,"P75",""+((HSSFCell) list.get(10)).getNumericCellValue());
    	createElement(writer,"P90",""+((HSSFCell) list.get(11)).getNumericCellValue());
    	createElement(writer,"P95",""+((HSSFCell) list.get(12)).getNumericCellValue());
    	createElement(writer,"P97",""+((HSSFCell) list.get(13)).getNumericCellValue());
	}
	
	public void getWeightElementData(XMLStreamWriter writer,List list)  throws Exception{
		createElement(writer,"Sex",""+((int)Double.parseDouble(list.get(1).toString())));
    	createElement(writer,"Agemonth",""+((HSSFCell) list.get(2)).getNumericCellValue());
        createElement(writer,"L",""+((HSSFCell) list.get(3)).getNumericCellValue());
        createElement(writer,"M",""+((HSSFCell) list.get(4)).getNumericCellValue());
        createElement(writer,"S",""+((HSSFCell) list.get(5)).getNumericCellValue());
    	createElement(writer,"P3",""+((HSSFCell) list.get(6)).getNumericCellValue());
    	createElement(writer,"P5",""+((HSSFCell) list.get(7)).getNumericCellValue());
    	createElement(writer,"P10",""+((HSSFCell) list.get(8)).getNumericCellValue());
    	createElement(writer,"P25",""+((HSSFCell) list.get(9)).getNumericCellValue());
    	createElement(writer,"P50",""+((HSSFCell) list.get(10)).getNumericCellValue());
    	createElement(writer,"P75",""+((HSSFCell) list.get(11)).getNumericCellValue());
    	createElement(writer,"P90",""+((HSSFCell) list.get(12)).getNumericCellValue());
    	createElement(writer,"P95",""+((HSSFCell) list.get(13)).getNumericCellValue());
    	createElement(writer,"P97",""+((HSSFCell) list.get(14)).getNumericCellValue());
	}
	
	/**
	 * Read data from Excel sheet
	 * 
	 * @param String fileName
	 */
	@SuppressWarnings("unchecked")
	private List getExcelData(File fileName) {
		FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                HSSFRow row = (HSSFRow) rows.next();
                Iterator cells = row.cellIterator();

                List data = new ArrayList();
                while (cells.hasNext()) {
                    HSSFCell cell = (HSSFCell) cells.next();
                    data.add(cell);
                }

                sheetData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        return sheetData;
	}


	
}
