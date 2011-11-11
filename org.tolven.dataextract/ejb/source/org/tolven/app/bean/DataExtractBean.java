/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.tolven.app.DataExtractLocal;
import org.tolven.app.DataExtractRemote;
import org.tolven.app.DataField;
import org.tolven.app.DataQueryResults;
import org.tolven.app.MenuLocal;
import org.tolven.app.SQLDialectHandler;
import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuLocator;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.TolvenRequest;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;

/**
 * This bean provides functionality to extract data based on menupaths.
 * 
 * @author Joseph Isaac
 *
 */
@Local(DataExtractLocal.class)
@Remote(DataExtractRemote.class)
public @Stateless
class DataExtractBean implements DataExtractLocal, DataExtractRemote {
	Logger logger = Logger.getLogger(this.getClass());

    public static final String SQL_DIALECT_HANDLER = "sqlDialectHandler";
	@PersistenceContext
	private EntityManager em;

    @EJB
    private TolvenPropertiesLocal propertiesBean;
    
    @EJB
    private AccountDAOLocal accountBean;
    @EJB
    private MenuLocal menuBean;    

    private static List<SQLDialectHandler> sqlDialectHandlers;

    private TolvenPropertiesLocal getTolvenPropertiesBean() {
        return propertiesBean;
    }
	/**
	 * Given a path to a global menu, such as TrimList, return
	 * the menuLocator that tells us where the list actually is.
	 * A RuntimeException is thrown if the item is not found.
	 * @param path
	 * @return MenuLocator for the given path
	 */
	public MenuLocator findMenuLocator( String path) {
		Query query = em.createQuery("SELECT md FROM MenuLocator md " +
				"WHERE md.path = :p");
		query.setParameter("p", path);
		MenuLocator menuLocator = (MenuLocator)query.getSingleResult();
		return menuLocator;
	}
	/**
	 * Setup the filter criteria, if any, for this query
	 * @param dq
	 * @return SQL string (additional where clauses)
	 */
	/*protected void setupFilterCriteria( DataQueryResults dq, StringBuffer fromString, StringBuffer whereString, Map<String, Object> params ) {
		String filter = dq.getFilter();
		if (filter==null) return;
		//filter = filter.trim().toLowerCase();
		filter = filter.replaceAll("[^a-zA-Z0-9:=]", " ");
		if (filter.length()==0) return;
        FilterLexer lex = new FilterLexer(new ANTLRStringStream(filter));
       	CommonTokenStream tokens = new CommonTokenStream(lex);

       	// Parse
        FilterParser parser = new FilterParser(tokens);
        RuleReturnScope result;
		try {
			result = parser.root();
		} catch (Exception e) {
			throw new RuntimeException( "Error parsing filter expression: " + filter, e);
		}
		Tree tree = (Tree)result.getTree();
		if (tree==null) return;

		logger.debug(tree.toStringTree());
    	Filter f = new Filter(fromString, whereString, params);
		whereString.append( String.format(Locale.US, "\n AND ("));
        f.doNode( tree );
        whereString.append( ")");
		
	}*/

	protected String setupQueryString( DataQueryResults dq, boolean countOnly, Map<String, Object>params ) {
        StringBuffer selectString = new StringBuffer();
		StringBuffer whereString = new StringBuffer();
		StringBuffer fromString = new StringBuffer();
		StringBuffer sortString = new StringBuffer();
		selectString.append("SELECT ");
    	if (countOnly) {
    		selectString.append( "count(DISTINCT md) ");
    	} else {
/*            selectString.append( "DISTINCT md.id as pk" );
            if (dq.getSelectString()!=null && dq.getSelectString().length()>0) {
            	selectString.append(", ");
            }*/
            selectString.append( dq.getSelectString() );
            selectString.append(" ");
          	dq.getSortString(sortString);
    	}
        if (selectString==null || selectString.length()==0) {
            throw new RuntimeException("At least one column is required for the query with: " + dq.getPath());
        }
		fromString.append(	"FROM MenuData md ");
		whereString.append(	"WHERE md.account = :account ");
		params.put("account", dq.getAccount());
		whereString.append(	"AND (md.deleted is null OR md.deleted = false) ");

		if(!countOnly) {
			MenuStructure msParent = dq.getMenuStructure();
			int level = 1;
			int count = 1;
			do
			{
				if (MenuStructure.PLACEHOLDER.equals(msParent.getRole()) && count!=1)
				{
					if(level==1)
						fromString.append("join md.parent01 p"+level+" ");
					else
						fromString.append("join p"+(level-1)+".parent01 p"+level+" ");
					level++;
				}
				msParent = msParent.getParent();
				count++;
			}
			while (msParent!=null);
		}
		
		// Item query just needs account and path whereas list query needs other stuff.
		if (dq.isItemQuery()) {
			// Filter and order by make no sense for an item query
			if (dq.getFilter()!=null || dq.getOrder()!=null) {
				throw new RuntimeException( "Filter and Order parameters do not apply to an item query" );
			}
			whereString.append(	"AND md.path = :path ");
			params.put("path", dq.getMenuPath().getPathString());
		} else {
			whereString.append(	"AND md.menuStructure = :ms ");
				params.put("ms", dq.getMenuStructure());
			// If there is a parent, then limit to children of that parent
			long snk[] = dq.getMenuPath().getSignificantNodeKeys();
			if (snk.length > 0) {
				whereString.append(" AND md.parent01.id = :parentId ");
				params.put("parentId", snk[snk.length-1]);
			}
			// Add filter criteria if provided
			/*if (dq.getFilter()!=null) {
				setupFilterCriteria(dq, fromString, whereString, params);
			}*/
		}
		String queryString = null;
		queryString = selectString.toString() + "\n" + fromString.toString()+ "\n" + whereString.toString() +"\n" + sortString.toString();
		return queryString;

	}
    /**
     * Perform the query defined by the supplied DataQueryResults object.
     * @param dq
     */
    protected void getMDQueryResults(DataQueryResults dq, boolean countOnly) {
    	Map<String, Object>params = new HashMap<String, Object>();
		String queryString = setupQueryString( dq, countOnly, params);
		try {
    		Query query;
			try {
				query = em.createQuery( queryString );
				logger.debug( queryString );
			} catch (Exception e) {
				throw new RuntimeException( "Error constructing item query " + queryString, e );
			}
			if (!countOnly) {
				query.setFirstResult(dq.getOffset());
			}
			if (!countOnly && dq.getLimit()>=0) {
				query.setMaxResults(dq.getLimit());
			}
            for (Map.Entry<String,Object> entry : params.entrySet()) {
                logger.debug(entry.getKey() + "=" + entry.getValue());
                query.setParameter(entry.getKey(), entry.getValue());
            }
			List<Object> results = query.getResultList();
			dq.setCount( results.size());
			dq.setIterator(results.iterator() );
		} catch (RuntimeException e) {
			throw new RuntimeException( "Error executing query " + queryString, e );
		}
    }

	public AccountMenuStructure findAccountMenuStructure( Account account, MenuPath mp) {
		try {
			AccountMenuStructure ms;
			Query query = em.createQuery("SELECT ams FROM AccountMenuStructure ams " +
					"WHERE ams.account = :account and ams.path = :path");
			query.setParameter("account", account );
			query.setParameter("path", mp.getPath());
			ms = (AccountMenuStructure)query.getSingleResult();
			return ms;
		} catch (Exception e) {
			throw new RuntimeException( "Error finding menustructure path " + mp.getPath() + " in account " + account.getId(), e);
		}
	}

    @Override
    public DataQueryResults setupQuery(String menupath, AccountUser accountUser) {
        return internalSetupQuery(menupath, accountUser.getAccount());
    }

    /**
     * This method can only be used with Accounts which are template Accounts
     * 
     * @param menupath
     * @param account
     * @return
     */
    @Override
    public DataQueryResults setupQuery(String menupath, Account account) {
        if (account.getAccountTemplate() != null) {
            throw new RuntimeException("Not a template Account: " + account.getId());
        }
        return internalSetupQuery(menupath, account);
    }

    /**
     * This method is private since callers should be passing AccountUser, unless dealing with template Accounts
     * @param menupath
     * @param account
     * @return
     */
    private DataQueryResults internalSetupQuery(String menupath, Account account) {
        String path = null;
        if (menupath.startsWith(":")) {
            path = account.getAccountType().getKnownType() + menupath;
        } else {
            path = menupath;
        }
        // Remove ids from path, we just want metadata at this point.
        MenuPath mp = new MenuPath(path);

        AccountMenuStructure ms;
		if (menupath.startsWith("global:")) {
			MenuLocator menuLocator = findMenuLocator(menupath);
			ms = menuLocator.getMenuStructure();
		} else {
	        ms = findAccountMenuStructure(account, mp);
		}
        DataQueryResults dq = new DataQueryResults(ms);
        dq.setMenuPath(mp);
        return dq;
    }
	
	public DataQueryResults setupQueryById(long id, AccountUser accountUser)
	{
		Query query = em.createQuery("SELECT md FROM MenuData md WHERE md.id = :id and md.account = :account");
		query.setParameter("id", id);
		query.setParameter("account", accountUser.getAccount());
		MenuData md = (MenuData)query.getSingleResult();
		DataQueryResults dq = null;
		if(md!=null) {
			dq = new DataQueryResults(md.getMenuStructure());
			dq.setMenuPath(new MenuPath(md.getPath()));
		}
		return dq;
	}

    public void streamResultsXML(Writer out, DataQueryResults dq) {
    	Long totalCount = null;
    	if (dq.isReturnTotalCount()) {
        	getMDQueryResults(dq, true);
        	totalCount = (Long) dq.getIterator().next();
    	}
    	
    	if (dq.getLimit()!=0) {
        	getMDQueryResults(dq, false);
    	}
    	try {
            XMLStreamWriter xmlStreamWriter = null;
            try {
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                xmlStreamWriter = factory.createXMLStreamWriter(out);
                xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
                xmlStreamWriter.writeStartElement("results");
                xmlStreamWriter.writeAttribute("path", dq.getPath());
                xmlStreamWriter.writeAttribute("account", String.valueOf(dq.getAccount().getId()));
                xmlStreamWriter.writeAttribute("database", getTolvenPropertiesBean().getProperty("tolven.repository.oid"));
				GregorianCalendar nowCal = new GregorianCalendar();
				nowCal.setTime(dq.getNow());
				DatatypeFactory xmlFactory = DatatypeFactory.newInstance();
				XMLGregorianCalendar ts = xmlFactory.newXMLGregorianCalendar(nowCal);
				xmlStreamWriter.writeAttribute("timestamp", ts.toXMLFormat());
				if (!dq.isItemQuery()) {
					xmlStreamWriter.writeAttribute("offset", Long.toString(dq.getOffset()));
					xmlStreamWriter.writeAttribute("limit", Long.toString(dq.getLimit())); 
					xmlStreamWriter.writeAttribute("count", Long.toString(dq.getCount())); 
				}
				if (!dq.isItemQuery() && totalCount !=null) {
					xmlStreamWriter.writeAttribute("totalCount", totalCount.toString());
				}
				addExtendedFields(dq);
				//addPlaceholderIds(dq);
				if(MenuStructure.PLACEHOLDER.equals(dq.getMenuStructure().getRole())) {
					addParentFields(dq);
				}
				addSimpleXMLColumnHeadings(dq, xmlStreamWriter);
            	if (dq.getLimit()!=0) {
            		addXMLRows(dq, xmlStreamWriter);
            	}
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndDocument();
            } finally {
                if (xmlStreamWriter != null) {
                    xmlStreamWriter.close();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not obtain XML results for menupath: " + dq.getPath() + " in account: " + dq.getAccount().getId(), ex);
        }
    }
    protected String xmlName(DataField field) {
    	// ************************** FINISH ************************
    	return field.getExternal();
    }
    
    protected void addXMLColumnHeadings(DataQueryResults dq, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
    	xmlStreamWriter.writeStartElement("columns");
        for (DataField field : dq.getSelectedFields()) {
            xmlStreamWriter.writeStartElement("col");
            xmlStreamWriter.writeAttribute("name", xmlName(field));
            xmlStreamWriter.writeEndElement();
        }
        xmlStreamWriter.writeEndElement();
    }
    
    protected void addSimpleXMLColumnHeadings(DataQueryResults dq, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
    	xmlStreamWriter.writeStartElement("fields");
    	boolean firstTime = true;
        for (DataField field : dq.getSelectedFields()) {
        	if (firstTime) {
        		firstTime = false;
        	} else {
                xmlStreamWriter.writeCharacters(",");
        	}
            xmlStreamWriter.writeCharacters(xmlName(field));
        }
        xmlStreamWriter.writeEndElement();
    }
    
    protected void writeXMLColumn(XMLStreamWriter xmlStreamWriter, DataField field, Object obj ) throws XMLStreamException {
        if (obj != null) {
        	String value;
            xmlStreamWriter.writeStartElement(xmlName(field));
            if(field.getExternal().equalsIgnoreCase("placeholderIds")) {
            	//fill me in with a for loop over the placeholder ids
            	Object[] plist = (Object[]) obj;
            	for (Object pobj : plist) {
            		String[] pentry = pobj.toString().split(",");
            		String proot = pentry[0].toString();
            		String pext = pentry[1].toString();
            		xmlStreamWriter.writeStartElement("id");
            		xmlStreamWriter.writeAttribute("root", StringEscapeUtils.escapeXml(proot));
            		xmlStreamWriter.writeAttribute("extension", StringEscapeUtils.escapeXml(pext));            		
            		xmlStreamWriter.writeEndElement();
            	}
            } else {
	            value = obj.toString();
	            String df = field.getDisplayFunction();
				if(df != null && "age".equalsIgnoreCase(df)) {
	            	value = ELFunctions.age((Date) obj, TolvenRequest.getInstance().getAccountUser());
	            }
				xmlStreamWriter.writeCharacters(StringEscapeUtils.escapeXml(value));
            }            
            xmlStreamWriter.writeEndElement();
        }
    }
    protected Object generateGenericMedication(List<DataField> fieldName,Object[] fieldVal){
    	Object gm=null; 
    	HashMap hashMap= new HashMap();    	
    	for(int i=0;i<fieldName.size();i++){    		
    		if(fieldName.get(i).getExternal().equals("genericName") ||
    				(fieldName.get(i).getExternal().equals("genericQualifier")) || 
    					(fieldName.get(i).getExternal().equals("genericForm")) ||
    						(fieldName.get(i).getExternal().equals("genericStrength"))){    			
    			hashMap.put(fieldName.get(i).getExternal(), fieldVal[i]);  
       		}    		
    	}   	
    	gm = hashMap.get("genericName") + " " +hashMap.get("genericQualifier") + " "+hashMap.get("genericForm") + " " +hashMap.get("genericStrength");
    	return gm;
    }
    private void addXMLRows(DataQueryResults dq, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
    	xmlStreamWriter.writeStartElement("rows");
        List<DataField> fields = dq.getSelectedFields();
        while (dq.hasNext()) {
        	xmlStreamWriter.writeStartElement("row");
        	if (fields.size() > 1) {
	        	Object[] row = (Object[]) dq.next();
	        	if(dq.getMenuPath().getPath().startsWith("global:genericMedicationMenu")) {  //TODO: FIXME
	        		for (int j = 0; j < fields.size(); j++) {
	        			Object genericMedication=null;
	        			if(fields.get(j).getLabel().startsWith("genericmedication")) {
	        				genericMedication = generateGenericMedication(fields,row);
	        				row[j]=genericMedication;
	        			}
	        			writeXMLColumn(xmlStreamWriter, fields.get(j), row[j] );
	        		}
	        	} else {
		        	for (int i = 0; i < fields.size(); i++) {
		        		writeXMLColumn(xmlStreamWriter, fields.get(i), row[i] );
		            }
	        	}
        	} else {
            	writeXMLColumn(xmlStreamWriter, fields.get(0), dq.next() );
        	}
            xmlStreamWriter.writeEndElement();
        }
        xmlStreamWriter.writeEndElement();
    }

    public void streamResultsCSV(Writer out, DataQueryResults dq) throws IOException {
    	getMDQueryResults(dq, false);
        addCSVColumnHeadings(dq, out);
        out.write("\n");
        addCSVRows(dq, out);
    }

    private void addCSVColumnHeadings(DataQueryResults dq, Writer out) throws IOException {
    	boolean firstTime = true;
    	for (DataField field : dq.getSelectedFields()) {
    		if (firstTime) {
    			firstTime = false;
    		} else {
    			out.write(',');
    		}
    		out.write(field.getExternal());
        }
    }

    private void addCSVRows(DataQueryResults dq,  Writer out) throws IOException {
        while (dq.hasNext()) {
            Object[] row = (Object[]) dq.next();
            for (int i = 0; i < row.length; i++) {
                if (i > 0) {
                	out.write(',');
                }
                Object obj = row[i];
                if (obj != null) {
                    String value = obj.toString().replace("\"", "\"\"");
                	out.write('"');
                	out.write(value);
                	out.write('"');
                } else {
                	out.write("");
                }
            }
            out.write("\n");
        }
    }

    public void streamResultsSQL(Writer out, DataQueryResults dq, String table, String dialect) throws IOException {
        SQLDialectHandler sqlDialectHandler = getSQLDialectHandler(dialect);
    	getMDQueryResults(dq, false);
        if (table == null || table.trim().length() == 0) {
            throw new RuntimeException("Table name must be supplied");
        }
        if (dialect == null || dialect.trim().length() == 0) {
            throw new RuntimeException("A SQL dialect must be supplied");
        }
        createTableStatement(out, table, dq, sqlDialectHandler);
        out.write("\n");
        createSQLRowStatements(out, table, dq, sqlDialectHandler);
    }

    private void createTableStatement(Writer out, String table, DataQueryResults dq, SQLDialectHandler sqlDialectHandler) throws IOException {
        out.write("CREATE TABLE");
        out.write(" ");
        out.write(table);
        out.write("(");
        boolean firstTime = true;
        for (DataField field : dq.getSelectedFields()) {
        	if (firstTime) {
        		firstTime = false;
        	} else {
                out.append(",\n");
        	}
            out.write(sqlDialectHandler.encodeIdentifier(field.getExternal()));
            out.write(' ');
            String columnType = sqlDialectHandler.getColumnType(field.getInternalField());
            out.write(columnType);
            if ("id".equals(field.getInternal())) {
            	out.append(" PRIMARY KEY");
            } else if (field.getInternal().endsWith(".id")){
            	out.append(" REFERENCES ");
            	out.append(sqlDialectHandler.encodeIdentifier(field.getExternalSegments()[field.getExternalSegments().length-2]));
            	out.append(" (id)");
            }
        }
        out.write(");");
    }

    private SQLDialectHandler getSQLDialectHandler(String dialect) {
        for (SQLDialectHandler sqlDialectHandler : sqlDialectHandlers) {
            if (dialect != null && dialect.equals(sqlDialectHandler.getDialect())) {
                return sqlDialectHandler;
            }
        }
        throw new RuntimeException("Could not locate SQL dialect handler for: " + dialect);
    }

    public List<String> getSQLDialects() {
        List<String> sqlDialects = new ArrayList<String>();
        for (SQLDialectHandler sqlDialectHandler : sqlDialectHandlers) {
            sqlDialects.add(sqlDialectHandler.getDialect());
        }
        return sqlDialects;
    }

    private void createSQLRowStatements(Writer out, String table, DataQueryResults dq, SQLDialectHandler sqlDialectHandler) throws IOException {
        while (dq.hasNext()) {
            Object[] row = (Object[]) dq.next();
            String sqlRowStatement = createSQLRowStatement(table, dq.getSelectedFields(), row, sqlDialectHandler);
            out.write(sqlRowStatement);
            out.write("\n");
        }
    }

    private String createSQLRowStatement(String table, List<DataField> selectedFields, Object[] row, SQLDialectHandler sqlDialectHandler) {
        StringBuffer buff = new StringBuffer();
        StringBuffer columnsBuff = new StringBuffer();
        StringBuffer rowsBuff = new StringBuffer();
        boolean firstTime = true;
        int i = 0;
        for (DataField field : selectedFields) {
            if (firstTime) {
            	firstTime = false; 
            } else {
                columnsBuff.append(',');
                rowsBuff.append(',');
            }
            columnsBuff.append(sqlDialectHandler.encodeIdentifier(field.getExternal()));
            Object obj = row[i++];
            sqlDialectHandler.formatValue( rowsBuff, obj );
        }
        buff.append("INSERT INTO ");
        buff.append(table);
        buff.append(" (");
        buff.append(columnsBuff.toString());
        buff.append(")  VALUES (");
        buff.append(rowsBuff.toString());
        buff.append(");");
        return buff.toString();
    }

    static {
        String propertyFileName = DataExtractBean.class.getSimpleName() + ".properties";
        try {
            Properties filterProperties = new Properties();
            InputStream in = DataExtractBean.class.getResourceAsStream(propertyFileName);
            sqlDialectHandlers = new ArrayList<SQLDialectHandler>();
            if (in != null) {
                filterProperties.load(in);
                String value = filterProperties.getProperty(SQL_DIALECT_HANDLER);
                if (value != null) {
                    for (String classname : Arrays.asList(value.split(","))) {
                        Class<?> clazz = null;
                        try {
                            clazz = Class.forName(classname);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException("Could not find class: " + classname + " in file: " + propertyFileName, ex);
                        }
                        try {
                            SQLDialectHandler sqlDialectHandler = (SQLDialectHandler) clazz.newInstance();
                            if (sqlDialectHandler.getDialect() == null) {
                                throw new RuntimeException("sqlDialectHandlers from file " + propertyFileName + " must return a value for the dialect");
                            }
                            sqlDialectHandlers.add(sqlDialectHandler);
                        } catch (Exception ex) {
                            throw new RuntimeException("RuntimeException", ex);
                        }
                    }
                }
            }
            Comparator<SQLDialectHandler> comparator = new Comparator<SQLDialectHandler>() {
                public int compare(SQLDialectHandler obj1, SQLDialectHandler obj2) {
                    return obj1.getDialect().compareTo(obj2.getDialect());
                };
            };
            Collections.sort(sqlDialectHandlers, comparator);
        } catch (IOException e) {
            throw new RuntimeException("Could not load filter properties from: " + propertyFileName, e);
        }
    }
    
    
	public List<MenuData> findMenuDataByDocumentId( Account account, long documentId) {
		try {
			Query query = em.createQuery("SELECT md FROM MenuData md " +
					"WHERE md.account = :account and md.documentId = :document");
			query.setParameter("account", account );
			query.setParameter("document", documentId);
			List<MenuData> results = query.getResultList();
			return results;
		} catch (Exception e) {
			throw new RuntimeException( "Error finding menudata in account " + account.getId(), e);
		}
	}

	public void addExtendedFields(DataQueryResults dq)
	{
		List<Object> rows = new ArrayList<Object> ();	
		long id;
		while(dq.hasNext())
		{
			List<DataField> fields = dq.getSelectedFields();
			Object[] row = null;
			Object[] newRow = new Object[fields.size()];			
			int findExtendedCount = fields.size();
			
			for(int i=0;i<fields.size();i++)
			{
				if(fields.get(i).getInternal().contains("_extended"))
					findExtendedCount--;
			}
		
			if(findExtendedCount>1)
			{
				row = (Object[]) dq.next();
				id = Long.parseLong(row[0].toString());
				newRow[0] = row[0];
			}
			else
			{
				newRow[0] = dq.next();
				id = Long.parseLong(newRow[0].toString());
				if(fields.size()==1)
				{
					rows.add(newRow[0]);
					break;
				}
			}
			try 
			{
				MenuData md;
				Query query = em.createQuery("SELECT md FROM MenuData md WHERE md.id = :id");
				query.setParameter("id", id);
				md = (MenuData)query.getSingleResult();
				if(md!=null)
				{
					for(int i=1,j=1;i<fields.size();i++)
					{
						if(fields.get(i).getInternal().contains("_extended")) {
							newRow[i] = md.getExtendedField(fields.get(i).getExternal());
						} else if (fields.get(i).getInternal().contains("placeholderIds")) {
							//grab the placeholderIds							
							newRow[i] = addPlaceholderIds(md);
						}
						else
						{
							newRow[i] = row[j++];
						}						
					}
					rows.add(newRow);
				}
			} catch (Exception e) {
				logger.info(e);
			}
			
		}
		dq.setIterator(rows.iterator());
	}
	
	public Object[] addPlaceholderIds(MenuData md)
	{
		//grab the placeholderIds
		Object[] plist = new Object[md.getPlaceholderIDs().size()];
		int x = 0;
		for(PlaceholderID pid : md.getPlaceholderIDArray())
		{			
			String placeholderIdXML = pid.getRoot().toString() + "," + pid.getExtension().toString();
			plist[x] = placeholderIdXML;
			x++;
		}
		return plist;
	}
	
	/**
	 * Add the parent menudata fields and it's corresponding values
	 * @param dq
	 */
	public void addParentFields(DataQueryResults dq) {
		
		// Hold original values
		Iterator<Object> iter = dq.getIterator();
		// Hold original fields
		List<DataField> fieldsAr = dq.getFields();
		// To hold new fields concatenation with old fields
		List<DataField> newFields = new ArrayList<DataField>();
		// To hold new values concatenation with old values. Will hold an Array of Object in each of its element
		List<Object> values = new ArrayList<Object>();
		// To hold fields to retrieve specific parent fields
		List<DataField> parentFilterFields = dq.getParentFilterFields();
		
		//Flag to update the fields only ones even though the values will be fetched number of times.
		boolean fieldFlag = true;
		//Flag to add the parent ID field only once.
		boolean addIdFlag = true;
		// Store all the old fields
		newFields.addAll(fieldsAr);
		
		// Check for the number of enabled fields
		int enabledFields = fieldsAr.size();
		for(DataField df : fieldsAr) {
			if(!df.isEnabled()) {
				enabledFields--;
			}
		}
		Object[] rows = new Object[fieldsAr.size()];	

		while(iter.hasNext()) {
			//Array list to hold new values
			List<Object> newRows = new ArrayList<Object>();
			String refPath = null;
			Long id = null;
			
			//Retrieve old values
			//If the enabled fields > 1, use array of objects
			if(enabledFields > 1) {
				rows = (Object [])iter.next();
				for(int i=0,j=0;i<fieldsAr.size();i++) {
					if(fieldsAr.get(i).isEnabled()) {
						//If the current menudata is a list, reference path needs to be used to get the original menudata.
						//In case of a placeholder, ID of the menudata should be used.
						if(fieldsAr.get(i).getInternalSegments()[0].equals("referencePath")) {
							refPath = rows[j].toString();
						} else if (fieldsAr.get(i).getInternalSegments()[0].equals("id")) {
							if(dq.getMenuStructure().getRole().equals("placeholder")) {
								id = (Long)rows[j];
							}
						}
						newRows.add((Object)rows[j]);
						j++;
					}
				}
			} else {
				rows[0] = iter.next();
				newRows.add(rows[0]);
				id = Long.parseLong(rows[0].toString());
			}
			MenuData md = null;
			String externalPrefix02 = "", externalPrefix03 = "", externalPrefix04 = "" ;
			// Get the menu data of a list or a placeholder based on reference path or ID respectively.
			if(refPath!=null) {
				md = menuBean.findMenuDataItem(dq.getAccount().getId(),  refPath);
			} else if(id!=null) {
				md = menuBean.findMenuDataItem(id);
				addIdFlag = false;
			}
			if(md!=null) {
				//Find the external names for the parent fields
				for(MSColumn col : md.getMenuStructure().getColumns()) {
					if(col.getInternal().equals("parent02"))
						externalPrefix02 = col.getHeading();
					else if(col.getInternal().equals("parent03"))
						externalPrefix03 = col.getHeading();
					else if(col.getInternal().equals("parent04"))
						externalPrefix04 = col.getHeading();		
				}
			
				String path=null;
				//Check for the parent existance. If yes, update the fields and values for the parent.
				boolean filterParent = false;
				if(parentFilterFields!=null) {
					filterParent = true;
				}
				if(md.getParent02()!=null) {
					path=md.getParent02().getPath();
					DataQueryResults newDq = this.setupQuery(path, 
							accountBean.getSystemAccountUser(md.getAccount(),true, new Date()));
					List<DataField> newParentFilterFields = new ArrayList<DataField>();
					if(parentFilterFields!=null) {
						for(DataField df : parentFilterFields) {
							if(Arrays.asList(df.getExternalSegments()).contains(externalPrefix02)) {
								newParentFilterFields.add(df);
							}
						}
					}
					newDq.setParentFilterFields(newParentFilterFields);
					updateParentFields(newDq, newFields, newRows, fieldFlag, externalPrefix02,addIdFlag,filterParent);					
				}
				if(md.getParent03()!=null) {
					path=md.getParent03().getPath();
					DataQueryResults newDq = this.setupQuery(path, 
							accountBean.getSystemAccountUser(md.getAccount(),true, new Date()));
					List<DataField> newParentFilterFields = new ArrayList<DataField>();
					if(parentFilterFields!=null) {
						for(DataField df : parentFilterFields) {
							if(Arrays.asList(df.getExternalSegments()).contains(externalPrefix03)) {
								newParentFilterFields.add(df);
							}
						}
					}
					newDq.setParentFilterFields(newParentFilterFields);
					updateParentFields(newDq, newFields, newRows, fieldFlag, externalPrefix03,addIdFlag,filterParent);					
				}
				if(md.getParent04()!=null) {
					path=md.getParent04().getPath();
					DataQueryResults newDq = this.setupQuery(path, 
							accountBean.getSystemAccountUser(md.getAccount(),true, new Date()));
					List<DataField> newParentFilterFields = new ArrayList<DataField>();
					if(parentFilterFields!=null) {
						for(DataField df : parentFilterFields) {
							if(Arrays.asList(df.getExternalSegments()).contains(externalPrefix04)) {
								newParentFilterFields.add(df);
							}
						}
					}
					newDq.setParentFilterFields(newParentFilterFields);
					updateParentFields(newDq, newFields, newRows, fieldFlag, externalPrefix04,addIdFlag,filterParent);							
				}
			}
			if(newRows.size() > 1) {
				values.add(newRows.toArray(new Object[newRows.size()]));
			} else {
				values.add(Long.parseLong(rows[0].toString()));
			}
			// No need to add the fields again in newFields
			fieldFlag = false;
		}
		dq.setFields(newFields);
		dq.setCount(values.size());
		dq.setIterator(values.iterator());
	}
	
	/**
	 * 
	 * @param newDq
	 * @param newFields
	 * @param newRows
	 * @param fieldFlag
	 * @param externalPrefix
	 * @param addIdFlag
	 */
	private void updateParentFields(DataQueryResults newDq, List<DataField> newFields, List<Object> newRows, boolean fieldFlag, String externalPrefix, boolean addIdFlag, boolean filterParent) {
		newDq.setItemQuery(true);
		newDq.setLimit(1);
		getMDQueryResults(newDq, false);
		addExtendedFields(newDq);
		List<DataField> parentFilterFields = newDq.getParentFilterFields();
		List<String> finalFields = new ArrayList<String>();
		
		// Find the fields of the parent to be listed, 
		// by removing the external prefix of the parent
		if(parentFilterFields!=null) {
			for (DataField df : parentFilterFields) {
				String temp[] = df.getExternalSegments();
				String external = "";
				for(int j=1;j<df.getExternalSegments().length-1;j++) {
					external += temp[j]+".";
				}
				external += temp[df.getExternalSegments().length-1];
				finalFields.add(external);
			}
		}

		//Need to skip the value, if the corresponding ID was skipped.
		List<Integer> flag = new ArrayList<Integer>();
		// If false, no need to update the fields. All the required fields will be already fetched.
		if(fieldFlag) {
			int k=-1;
			for(DataField df : newDq.getFields()) {
				if(df.isEnabled()) {
					k++;
					String external = df.getExternal();
					//Skip the ID, if the menudata is for the placeholder
					if(((external.equals("id") && !addIdFlag)) || (filterParent && !finalFields.contains(external))) {
						flag.add(k);
						continue;
					}
					newFields.add(new DataField( externalPrefix +"."+df.getExternal() , df.getInternal(), true, null));
				}
			}
		}
		
		Iterator<Object> newIter = newDq.getIterator();
		while(newIter.hasNext()) {
			Object[] againNewRows = (Object [])newIter.next();
			for(int k=0;k<againNewRows.length;k++) {
				//If the ID is skipped, as in the previous loop, skip the value for the same.
				if(flag.contains(k)) {
					continue;
				}
				newRows.add(againNewRows[k]);
			}
		}
	}
}