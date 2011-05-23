package org.tolven.ajax;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tolven.app.entity.MSAction;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.web.util.MiscUtils;


/**
 * Create a data grid based on MenuStructure and MSColumn metadata. This depends on 
 * both Rico LiveGrid and Tolven javascript running in the browser.
 * @author John Churin
 *
 */
public class GridBuilder {
	public static final int DEFAULT_VISIBLE_ROWS = 15;
	public static final float DEFAULT_COL_WIDTH = 10.0f;

	private long rowCount;
	private MenuQueryControl ctrl;
	private Writer writer;
    private TolvenResourceBundle tolvenResourceBundle;
	private List<Map<String, Object>> favorites;
	private String jsMethodName;
	private String jsMethodArgs;
	private String gridId; // to allow setting the grid id other than the menupath
	private String gridType; // represents output format for the grid
	private String favoritesType;
	private Logger logger = Logger.getLogger(GridBuilder.class);
	
	/**
	 * An instance of this class is constructed for the purpose of building a grid 
	 * for a single request for a menu structure entry. 
	 * @param ms
	 */
	public GridBuilder(MenuQueryControl ctrl, long rowCount, TolvenResourceBundle tolvenResourceBundle) {
		this.ctrl = ctrl;
		this.rowCount = rowCount;
		this.writer = new StringWriter();
		this.tolvenResourceBundle = tolvenResourceBundle;
		
	}

    /**
	 * Return the resulting string created by this class.
	 */
	public String toString() {
		return writer.toString();
	}
	
	public String getId( String suffix  ) {
		if(getGridId() != null)
			return getGridId()+suffix;
		else
			return ctrl.getOriginalTargetPath().getPathString() + suffix;
	}
	public String getMenuPath(String suffix) {
		return ctrl.getOriginalTargetPath().getPathString() + suffix;
	}

    public TolvenResourceBundle getAppBundle() {
        return getTolvenResourceBundle();
    }

    public TolvenResourceBundle getGlobalBundle() {
        return getTolvenResourceBundle();
    }

    public TolvenResourceBundle getTolvenResourceBundle() {
        return tolvenResourceBundle;
    }

    public void setupGrid() throws IOException {
		String[] initialSort = getInitialSort();
		writer.write(String.format(Locale.US,
				"<div id=\"%s\" class=\"grid\" totalRows=\"%d\" visibleRows=\"%d\" gridOffset=\"0\" gridSortCol=\"%s\" gridSortDir=\"%s\">\n", 
				getId("-grid"), rowCount, DEFAULT_VISIBLE_ROWS, initialSort[0], initialSort[1]) );
			createFilter();
			createHead();
			createBody();
			createFoot();
			writer.write("</div>\n"); 
    }
    /**
	 * Create the overall grid. Notice that the outer div here contains the defaults for the
	 * root div. the root div is not controlled by this control but holds values should the control be
	 * refreshed such as when new updates arrive and the grid needs to be repositioned to
	 * it's previous scrolling offset. the createGrid js method uses these dafaults to initialize the
	 * root grid when needed.
	 * @param writer
	 * @throws IOException 
	 */
	public void createGrid(  ) throws IOException {
		setupGrid();
		createInit();
	}
	
	/**
	 * The purpose of this method is same as createGrid(). Additional arguments passed will be used to
	 * decide which script method to be invoked when clicked on Grid-rows. Grid posts back jsMethodName and jsFormId
	 * back to AjaxServlet which are then used to build the hyper link.
	 * 
	 * @param jsMethodName - Script method that will be eventually used for on-click of grid rows 
	 * @param jsMethodArgs - Concatenated Arguments Str passed to the above method. 
	 */
	public void createGrid(String jsMethodName, String jsMethodArgs) throws IOException
	{
		setJsMethodName(jsMethodName);
		setJsMethodArgs(jsMethodArgs);
		setupGrid();
		createInit(jsMethodName, jsMethodArgs);
	}
	
	/**
	 * Return initialSort as a string array with the first string being the column and the second string being the direction.
	 * @return
	 */
	protected String[] getInitialSort( ){
		String initialSort = ctrl.getActualMenuStructure().getInitialSort();
		String col;
		String dir;
		if (initialSort==null) {
			List<MSColumn> cols = ctrl.getSortedColumns();
            if(cols.isEmpty()) {
                throw new RuntimeException("MenuStrucure: " + ctrl.getActualMenuStructure().getPath() + " is expected to have at least one associated column");
            }
			initialSort = cols.get(0).getHeading();
		}
		initialSort = initialSort.trim();
		// Separate out just the column
		int spaceCol = initialSort.indexOf(" ");
		if (spaceCol>=0) {
			col = initialSort.substring(0, spaceCol);
			dir = initialSort.substring(spaceCol);
		} else {
			col = initialSort;
			dir = "ASC";
		}
		return new String[] {col, dir};
	}
	
	public List<MSAction> getActions( ) {
		List<MSAction> actions = new ArrayList<MSAction>(ctrl.getMenuStructure().getActions());
		return actions;
	}

	/**
	 * Create the script line that will initialize the table
	 * @throws IOException 
	 */
	public void createInit(String jsMethodName, String jsMethodArgs ) throws IOException {
		writer.write("<script language=\"JavaScript\" type=\"text/javascript\">\n");
		writer.write("// <![CDATA[\n");
		writer.write(String.format(Locale.US, "createGrid( '%s','%s','%s','%s','%s'" + " );\n", 
				getMenuPath(""),getId(""),(jsMethodName!=null?jsMethodName:""), 
				(jsMethodArgs!=null?jsMethodArgs:""),(getGridType()!=null?getGridType():"")));
		writer.write("// ]]>\n");
		writer.write("</script>\n");
	}
	
	
	/**
	 * Create the script line that will initialize the table
	 * @throws IOException 
	 */
	public void createInit( ) throws IOException {
		writer.write("<script language=\"JavaScript\" type=\"text/javascript\">\n");
		writer.write("// <![CDATA[\n");
		writer.write(String.format(Locale.US, "createGrid( '%s' );\n", getId("")));
		writer.write("// ]]>\n");
		writer.write("</script>\n");
	}

	public void createPresets( ) {
		
	}

	/**
	 * Create the table heading which is actually a separate table from the table body that will contain
	 * the data itself.
	 * @param writer
	 * @throws IOException 
	 */
	public  void createHead( ) throws IOException {
		writer.write(String.format(Locale.US, "<table id=\"%s\">\n<thead>\n<tr>\n", getId("-LG_header")));
		// Add the headings
		List<MSColumn> columns = ctrl.getSortedColumns();
		MSColumn col = null;
		for(int i = 0; i < columns.size(); i++) {
		    col = columns.get(i);
			if (!col.isVisible()) {
				continue;
			}
			float width;
			if (col.getWidth()==null) width = DEFAULT_COL_WIDTH; 
			else width = col.getWidth();
			String align = col.getAlign();
			if (align==null) align="left";
			writer.write(String.format(Locale.US, "<th style=\"text-align:%s;width:%.1fem\">%s</th>", 
					align, width+0.5, col.getLocaleText(getTolvenResourceBundle())));
		}
//		writer.write("\n<th> </th>\n");
		writer.write("\n</tr>\n</thead>\n</table>\n");
	}

	/**
	 * Create the filter-bar.
	 * @param writer
	 * @throws IOException 
	 */
	public  void createFilter( ) throws IOException {
	   writer.write(String.format(Locale.US,"<table class=\"filter\"><tr><td class=\"menuActions\" >"+ MiscUtils.createActionButtons(getActions(),getId(""), getTolvenResourceBundle())+" "));
	   String filterLabel = null;
	   filterLabel = ResourceBundleHelper.getString(getTolvenResourceBundle(), "Filter");
	   writer.write(filterLabel);
	   writer.write(String.format(Locale.US,
		" <input id=\"%s\" name=\"%s\" type=\"text\"/></td>"+addFavoritesTabs()+"</tr></table>\n", 
		getId("-filter"), getId("-filter")));
	}
	/*
	 * Method to add favorites items
	 */
	public String addFavoritesTabs(){
		StringBuffer favTabs = new StringBuffer();
		if(getFavorites() != null && getFavorites().size()>0 ){
			favTabs.append("<td class='favorites'><ul class='favoritesTabs'>");
			//String allPath =
			favTabs.append(String.format("<li id='%s_li' class='%s'><a href=\"javascript:showFavoritesGrid('%s','%s','%s','%s')\">" +
					"All</a></li>",getFavoritesType(),getFavoritesType().equals(getId(""))?"active":"",getFavoritesType(),getFavoritesType(),getJsMethodName(),getJsMethodArgs()));			
			for(Map<String,Object> favItem:getFavorites()){
				favTabs.append(String.format("<li id='%s_li' class='%s'><a href=\"javascript:showFavoritesGrid('%s','%s','%s','%s')\">%s</a></li>",
						favItem.get("referencePath")+":favorites",(favItem.get("referencePath")+":favorites").equals(getId(""))?"active":"",favItem.get("referencePath")+":favorites",favItem.get("referencePath")+":favorites",
						getJsMethodName(),getJsMethodArgs(), favItem.get("DisplayName")));
			}
			//Add More^ tab to favorites for the drop down options
			//SK TODO: should we fix the labels to use locale? 
			favTabs.append(String.format("<li id='%s_bar1_showDrpDwn' class='showDrpDwn' > <a href=\"javascript:toggleDrpDwn(\'%s_bar1_dropdown_loc\',\'%s_bar1_drpDwn\')\">", getId(""),getId(""),getId("")));
			favTabs.append(String.format("More <img src='../images/arrow_blue.gif'/></a>"));
			favTabs.append(String.format("<s id='%s_bar1_dropdown_loc' style='height:0px;position:absolute;'></s>",getId("")));
			favTabs.append(String.format("<div id='%s_bar1_drpDwn' class='drpDwn' style='display:none;'>",getId("")));
			favTabs.append(String.format("<ul><li><a href=\"javascript:getPreferencesMenuItems(\'%s\', \'favorites\')\" style='text-decoration:none;'>Customize</a></li></ul></div></li>",getFavoritesType()));
			favTabs.append("</ul></td>");
		}
		return favTabs.toString();
	}

	/**
	 * Create the table body which includes a table with the number of rows to be shown.
	 * @param writer
	 * @throws IOException 
	 */
	public  void createBody(  ) throws IOException {
		writer.write(String.format(Locale.US, "<table id=\"%s\">\n<tbody>\n", getId("-LG")));
		for (int x = 0; x < (DEFAULT_VISIBLE_ROWS+1); x++ ) {
			if ((x&1)==0) createRowTemplate( "odd" );
			else createRowTemplate( "even");
		}
		writer.write("</tbody>\n</table>\n");
	}

	/**
	 * Create the table heading which is actually a separate table from the table body that will contain
	 * the data itself.
	 * @param writer
	 * @throws IOException 
	 */
	public void createFoot() throws IOException {
        String formattedString = null;
        String pattern = null;
        if (rowCount == 1) {
            pattern = ResourceBundleHelper.getString(getTolvenResourceBundle(), "item1");
        } else {
            pattern = ResourceBundleHelper.getString(getTolvenResourceBundle(), "itemsN");
        }
        MessageFormat formatter = new MessageFormat(pattern, getTolvenResourceBundle().getLocale());
        Object[] messageArgs = { rowCount };
        formattedString = formatter.format(messageArgs);
        writer.write(String.format(Locale.US, "<div class=\"foot\">" + formattedString + "<span id=\"%s\"> </span></div>\n", getId("-foot")));
    }

	/**
	 * Create one row in the actual table. The browser will call back to populate the
	 * actual instance data in the row. 
	 * @param writer
	 * @throws IOException 
	 */
	public void createRowTemplate( String rowClass ) throws IOException {
		writer.write("<tr class=\""); writer.write(rowClass); writer.write("\">");
		for( MSColumn col : ctrl.getSortedColumns()) {
			createColumnTemplate( col );
		}
		writer.write("</tr>\n");
	}

	public void createColumnTemplate( MSColumn col  ) throws IOException {
		if (!col.isVisible()) {
			return;
		}
		String align = col.getAlign();
		if (align==null) {
			align="left";
		}
		float width;
		if (col.getWidth()==null) {
			width = 10.0f;
		} else {
			width = col.getWidth()+0.5f;
		}
		writer.write( String.format(Locale.US, "<td style=\"text-align:%s;width:%.1fem\">--</td>", align, width) );
	}

	public List<Map<String, Object>> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Map<String, Object>> favorites) {
		this.favorites = favorites;
	}

	public String getJsMethodName() {
		return jsMethodName;
	}

	public void setJsMethodName(String jsMethodName) {
		this.jsMethodName = jsMethodName;
	}

	public String getJsMethodArgs() {
		return jsMethodArgs;
	}

	public void setJsMethodArgs(String jsMethodArgs) {
		this.jsMethodArgs = jsMethodArgs;
	}

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	public String getFavoritesType() {
		return favoritesType;
	}

	public void setFavoritesType(String favoritesType) {
		this.favoritesType = favoritesType;
	}

	public String getGridType() {
		return gridType;
	}

	public void setGridType(String gridType) {
		this.gridType = gridType;
	}
}
