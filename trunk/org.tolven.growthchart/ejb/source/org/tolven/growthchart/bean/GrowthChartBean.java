package org.tolven.growthchart.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.growthchart.GrowthChartLocal;
import org.tolven.growthchart.GrowthChartRemote;
import org.tolven.growthchart.entity.Lenageinf;
import org.tolven.growthchart.entity.Statage;
import org.tolven.growthchart.entity.Wtage;
import org.tolven.growthchart.entity.Wtageinf;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * To manage Growth Chart
 * 
 * @author Suja
 * added on 01/20/2011
 */
@Stateless()
@Local(GrowthChartLocal.class) 
@Remote(GrowthChartRemote.class)
public class GrowthChartBean implements GrowthChartLocal, GrowthChartRemote {

	@PersistenceContext
	private EntityManager em;
	
	// XPath variable
	XPath xpath = XPathFactory.newInstance().newXPath();
	
	/**
	 * Add an entry in Lenageinf table
	 * @param Lenageinf
	 * @return 
	 */
	public void addLenageinf(Lenageinf lenageinf) {
		em.merge(lenageinf);
	}
	
	/**
	 * Add an entry in Statage table
	 * @param Statage
	 * @return 
	 */
	public void addStatage(Statage statage) {
		em.flush();
		em.merge(statage);
	}
	
	/**
	 * Add an entry in Wtage table
	 * @param Wtage
	 * @return 
	 */
	public void addWtage(Wtage wtage) {
		em.merge(wtage);
	}
	
	/**
	 * Add an entry in Wtageinf table
	 * @param Wtageinf
	 * @return 
	 */
	public void addWtageinf(Wtageinf wtageinf) {
		em.merge(wtageinf);
	}

	@Override
	public List<Wtage> findWtageData(int gender) {
		Query query = em.createQuery("SELECT obj FROM Wtage obj WHERE obj.sex = :gender ORDER BY obj.agemonth");
		query.setParameter("gender", gender);
		return query.getResultList();
	}

	@Override
	public List<Wtageinf> findWtageinfData(int gender) {
		Query query = em.createQuery("SELECT obj FROM Wtageinf obj WHERE obj.sex = :gender ORDER BY obj.agemonth");
		query.setParameter("gender", gender);
		return query.getResultList();
	}

	@Override
	public List<Lenageinf> findLenageinfData(int gender) {
		Query query = em.createQuery("SELECT obj FROM Lenageinf obj WHERE obj.sex = :gender ORDER BY obj.agemonth");
		query.setParameter("gender", gender);
		return query.getResultList();
	}

	@Override
	public List<Statage> findStatageData(int gender) {
		Query query = em.createQuery("SELECT obj FROM Statage obj WHERE obj.sex = :gender ORDER BY obj.agemonth");
		query.setParameter("gender", gender);
		return query.getResultList();
	}

	/**
	 * Remove all entries from Lenageinf table
	 * @param 
	 * @return 
	 */
	@Override
	public void clearLenageinf() {
		Query query = em.createQuery("delete from Lenageinf");
		query.executeUpdate();
	}

	/**
	 * Remove all entries from Statage table
	 * @param 
	 * @return 
	 */
	@Override
	public void clearStatage() {
		Query query = em.createQuery("delete from Statage");
		query.executeUpdate();
	}

	/**
	 * Remove all entries from Wtage table
	 * @param 
	 * @return 
	 */
	@Override
	public void clearWtage() {
		Query query = em.createQuery("delete from Wtage");
		query.executeUpdate();
	}

	/**
	 * Remove all entries from Wtageinf table
	 * @param 
	 * @return 
	 */
	@Override
	public void clearWtageinf() {
		Query query = em.createQuery("delete from Wtageinf");
		query.executeUpdate();
	}
	
	/**
	 * Load Growth Chart Entries
	 * @param xml - XML Data from the restful api
	 * @return 
	 */
	public void loadGrowthChart(String xml)
	{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			
			String action=(String)path("/GrowthChart/action/text()").evaluate(doc, XPathConstants.STRING );
			
			//If the request is a clear request, call appropriate methods to clear data 
			if(action!=null && action.indexOf("clear")==0)
			{
				processClear(action);
			}
			else if("loadLenageinf".equalsIgnoreCase(action))
			{
				Lenageinf lenageinf = new Lenageinf();
				lenageinf.setSex((int)getDoubleValue(doc,"Sex"));
				lenageinf.setAgemonth(getDoubleValue(doc,"Agemonth"));
			    lenageinf.setL(getDoubleValue(doc,"L"));
			    lenageinf.setM(getDoubleValue(doc,"M"));
			    lenageinf.setS(getDoubleValue(doc,"S"));
				lenageinf.setP3(getDoubleValue(doc,"P3"));
				lenageinf.setP5(getDoubleValue(doc,"P5"));
				lenageinf.setP10(getDoubleValue(doc,"P10"));
				lenageinf.setP25(getDoubleValue(doc,"P25"));
				lenageinf.setP50(getDoubleValue(doc,"P50"));
				lenageinf.setP75(getDoubleValue(doc,"P75"));
				lenageinf.setP90(getDoubleValue(doc,"P90"));
				lenageinf.setP95(getDoubleValue(doc,"P95"));
				lenageinf.setP97(getDoubleValue(doc,"P97"));
				lenageinf.setPub3(getDoubleValue(doc,"Pub3"));
				lenageinf.setPub5(getDoubleValue(doc,"Pub5"));
				lenageinf.setPub10(getDoubleValue(doc,"Pub10"));
				lenageinf.setPub25(getDoubleValue(doc,"Pub25"));
				lenageinf.setPub50(getDoubleValue(doc,"Pub50"));
				lenageinf.setPub75(getDoubleValue(doc,"Pub75"));
				lenageinf.setPub90(getDoubleValue(doc,"Pub90"));
				lenageinf.setPub95(getDoubleValue(doc,"Pub95"));
				lenageinf.setPub97(getDoubleValue(doc,"Pub97"));
				lenageinf.setDiff3(getDoubleValue(doc,"Diff3"));
				lenageinf.setDiff5(getDoubleValue(doc,"Diff5"));
				lenageinf.setDiff10(getDoubleValue(doc,"Diff10"));
				lenageinf.setDiff25(getDoubleValue(doc,"Diff25"));
				lenageinf.setDiff50(getDoubleValue(doc,"Diff50"));
				lenageinf.setDiff75(getDoubleValue(doc,"Diff75"));
				lenageinf.setDiff90(getDoubleValue(doc,"Diff90"));
				lenageinf.setDiff95(getDoubleValue(doc,"Diff95"));
				lenageinf.setDiff97(getDoubleValue(doc,"Diff97"));
				addLenageinf(lenageinf);
			}
			else if("loadStatage".equalsIgnoreCase(action))
			{
				Statage statage = new Statage();
				statage.setSex((int)getDoubleValue(doc,"Sex"));
				statage.setAgemonth(getDoubleValue(doc,"Agemonth"));
			    statage.setL(getDoubleValue(doc,"L"));
			    statage.setM(getDoubleValue(doc,"M"));
			    statage.setS(getDoubleValue(doc,"S"));
				statage.setP3(getDoubleValue(doc,"P3"));
				statage.setP5(getDoubleValue(doc,"P5"));
				statage.setP10(getDoubleValue(doc,"P10"));
				statage.setP25(getDoubleValue(doc,"P25"));
				statage.setP50(getDoubleValue(doc,"P50"));
				statage.setP75(getDoubleValue(doc,"P75"));
				statage.setP90(getDoubleValue(doc,"P90"));
				statage.setP95(getDoubleValue(doc,"P95"));
				statage.setP97(getDoubleValue(doc,"P97"));
				addStatage(statage);
			}
			else if("loadWtage".equalsIgnoreCase(action))
			{
				Wtage wtage = new Wtage();
				wtage.setSex((int)getDoubleValue(doc,"Sex"));
				wtage.setAgemonth(getDoubleValue(doc,"Agemonth"));
			    wtage.setL(getDoubleValue(doc,"L"));
			    wtage.setM(getDoubleValue(doc,"M"));
			    wtage.setS(getDoubleValue(doc,"S"));
				wtage.setP3(getDoubleValue(doc,"P3"));
				wtage.setP5(getDoubleValue(doc,"P5"));
				wtage.setP10(getDoubleValue(doc,"P10"));
				wtage.setP25(getDoubleValue(doc,"P25"));
				wtage.setP50(getDoubleValue(doc,"P50"));
				wtage.setP75(getDoubleValue(doc,"P75"));
				wtage.setP90(getDoubleValue(doc,"P90"));
				wtage.setP95(getDoubleValue(doc,"P95"));
				wtage.setP97(getDoubleValue(doc,"P97"));
				addWtage(wtage);
			}
			else if("loadWtageinf".equalsIgnoreCase(action))
			{
				Wtageinf wtageinf = new Wtageinf();
				wtageinf.setSex((int)getDoubleValue(doc,"Sex"));
				wtageinf.setAgemonth(getDoubleValue(doc,"Agemonth"));
			    wtageinf.setL(getDoubleValue(doc,"L"));
			    wtageinf.setM(getDoubleValue(doc,"M"));
			    wtageinf.setS(getDoubleValue(doc,"S"));
				wtageinf.setP3(getDoubleValue(doc,"P3"));
				wtageinf.setP5(getDoubleValue(doc,"P5"));
				wtageinf.setP10(getDoubleValue(doc,"P10"));
				wtageinf.setP25(getDoubleValue(doc,"P25"));
				wtageinf.setP50(getDoubleValue(doc,"P50"));
				wtageinf.setP75(getDoubleValue(doc,"P75"));
				wtageinf.setP90(getDoubleValue(doc,"P90"));
				wtageinf.setP95(getDoubleValue(doc,"P95"));
				wtageinf.setP97(getDoubleValue(doc,"P97"));
				addWtageinf(wtageinf);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
 	}
	
	/**
	 * Utility method to parse value to a double value
	 * @param doc - Document representation of the input xml
	 * @param element - The element to process
	 * @return double value
	 */
	protected double getDoubleValue(Document doc,String element) throws Exception
	{
		String elementValue=(String)path("/GrowthChart/"+element+"/text()").evaluate(doc, XPathConstants.STRING );
		
		if(elementValue!=null && !"".equals(elementValue))
		{
			return Double.parseDouble(elementValue);
		}
		else
		{
			//TODO Check if error condition or to assign a default value
			return 0.0;
		}
	}
	
	/**
	 * Method to identify/process clear request
	 * @param action - ientifier for the content to clear
	 * @return 
	 */
	protected void processClear(String action)
	{
		
		if("clearLenageinf".equalsIgnoreCase(action))
		{
			clearLenageinf();
		}
		else if("clearStatage".equalsIgnoreCase(action))
		{
			clearStatage();
		}
		else if("clearWtage".equalsIgnoreCase(action))
		{
			clearWtage();
		}
		else if("clearWtageinf".equalsIgnoreCase(action))
		{
			clearWtageinf();
		}
	}
	
	
	/**
	 * Method to compile xpath expression 
	 * @param expression - String representing the expression
	 * @return 
	 */
	protected XPathExpression path( String expression ) {
		try {
			return xpath.compile(expression);
		} catch (XPathExpressionException e) {
			throw new RuntimeException( "Invalue XPath Expression", e );
		}
	}
}
