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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.tolven.app.DataExtractLocal;
import org.tolven.app.DataField;
import org.tolven.app.DataQueryResults;
import org.tolven.app.SQLDialectHandler;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;

/**
 * This bean provides functionality to extract data based on menupaths.
 * 
 * @author Joseph Isaac
 *
 */
@Local(DataExtractLocal.class)
public @Stateless
class DataExtractBean implements DataExtractLocal {

    public static final String SQL_DIALECT_HANDLER = "sqlDialectHandler";
    
	@PersistenceContext
	private EntityManager em;

    @EJB
    private TolvenPropertiesLocal propertiesBean;

    private static List<SQLDialectHandler> sqlDialectHandlers;

    private TolvenPropertiesLocal getTolvenPropertiesBean() {
        return propertiesBean;
    }

    /**
     * Perform the query defined by the supplied DataQueryResults object.
     * @param dq
     */
    protected void getMDQueryResults(DataQueryResults dq, boolean countOnly) {
        String selectString;
        String sortString;
    	if (countOnly) {
    		selectString = "count(*)";
    		sortString = null;
    	} else {
            selectString = dq.getSelectString();
            sortString = dq.getSortString();
    	}
        if (selectString==null || selectString.length()==0) {
            throw new RuntimeException("At least one column is required for the query with: " + dq.getPath());
        }
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT ");
		queryString.append(selectString);
		queryString.append(	" FROM MenuData md ");
		Query query;
		// Item query just needs account and path whereas list query needs other stuff.
		if (dq.isItemQuery()) {
			queryString.append(	"WHERE md.account = :account " +
					"AND md.path = :path ");
			try {
			query = em.createQuery( queryString.toString() );
			} catch (Exception e) {
				throw new RuntimeException( "Error constructing item query " + queryString, e );
			}
			query.setParameter("account", dq.getAccount());
			query.setParameter("path", dq.getMenuPath().getPathString());
		} else {
			queryString.append(	"WHERE md.account = :account " +
				"AND md.menuStructure = :ms " +
				"AND (md.deleted is null OR md.deleted = false)");
			// If there is a parent, then limit to children of that parent
			long snk[] = dq.getMenuPath().getSignificantNodeKeys();
			if (snk.length > 0) {
				queryString.append(" AND parent01.id = :parentId ");
			}
			if (sortString!=null) {
				queryString.append(" ORDER BY " + sortString);
			}
			try {
				query = em.createQuery( queryString.toString() );
			} catch (Exception e) {
				throw new RuntimeException( "Error constructing list query " + queryString, e );
			}
			query.setParameter("account", dq.getAccount());
			query.setParameter("ms", dq.getMenuStructure());
			if (snk.length > 0) {
				query.setParameter("parentId",snk[snk.length-1]);
			}
			if (!countOnly) {
				query.setFirstResult(dq.getOffset());
			}
			if (!countOnly && dq.getLimit()>=0) {
				query.setMaxResults(dq.getLimit());
			}
		}
        try {
			List<Object> results = query.getResultList();
			dq.setCount( results.size());
			dq.setIterator(results.iterator() );
		} catch (RuntimeException e) {
			throw new RuntimeException( "Error executing query " + queryString, e );
		}
    }

    @Override
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

        AccountMenuStructure ms = findAccountMenuStructure(account, mp);
        DataQueryResults dq = new DataQueryResults(ms);
        dq.setMenuPath(mp);
        return dq;
    }

    @Override
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
            xmlStreamWriter.writeStartElement(xmlName(field));
            String value = obj.toString();
            xmlStreamWriter.writeCharacters(StringEscapeUtils.escapeXml(value));
            xmlStreamWriter.writeEndElement();
        }
    }
    
    private void addXMLRows(DataQueryResults dq, XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
    	xmlStreamWriter.writeStartElement("rows");
        List<DataField> fields = dq.getSelectedFields();
        while (dq.hasNext()) {
        	xmlStreamWriter.writeStartElement("row");
        	if (fields.size() > 1) {
	        	Object[] row = (Object[]) dq.next();
	            for (int i = 0; i < fields.size(); i++) {
	            	writeXMLColumn(xmlStreamWriter, fields.get(i), row[i] );
	            }
        	} else {
            	writeXMLColumn(xmlStreamWriter, fields.get(0), dq.next() );
        	}
            xmlStreamWriter.writeEndElement();
        }
        xmlStreamWriter.writeEndElement();
    }

    @Override
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

    @Override
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

    @Override
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

}
