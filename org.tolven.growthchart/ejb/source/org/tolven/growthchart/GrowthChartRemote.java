package org.tolven.growthchart;

import org.tolven.growthchart.entity.Lenageinf;
import org.tolven.growthchart.entity.Statage;
import org.tolven.growthchart.entity.Wtage;
import org.tolven.growthchart.entity.Wtageinf;

/**
 * To manage Growth Chart
 * 
 * @author Nevin
 * added on 02/04/2011
 */
public interface GrowthChartRemote {
	/**
	 * Add an entry in Lenageinf table
	 * @param Lenageinf
	 * @return 
	 */
	public void addLenageinf(Lenageinf lenageinf);
	
	/**
	 * Add an entry in Statage table
	 * @param Statage
	 * @return 
	 */
	public void addStatage(Statage statage);
	
	/**
	 * Add an entry in Wtage table
	 * @param Wtage
	 * @return 
	 */
	public void addWtage(Wtage wtage);
	
	/**
	 * Add an entry in Wtageinf table
	 * @param Wtageinf
	 * @return 
	 */
	public void addWtageinf(Wtageinf wtageinf);
	
	/**
	 * Remove all entries from Lenageinf table
	 * @param 
	 * @return 
	 */
	public void clearLenageinf();
	
	/**
	 * Remove all entries from Statage table
	 * @param 
	 * @return 
	 */
	public void clearStatage();
	
	/**
	 * Remove all entries from Wtage table
	 * @param 
	 * @return 
	 */
	public void clearWtage();
	
	/**
	 * Remove all entries from Wtageinf table
	 * @param 
	 * @return 
	 */
	public void clearWtageinf();
	
	/**
	 * Load Growth Chart Entries
	 * @param xml - XML Data from the restful api
	 * @return 
	 */
	public void loadGrowthChart(String xml);

}
