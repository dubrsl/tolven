/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Joseph Isaac
 * @version $Id$
 */
package org.tolven.app;


public class PostgresqlSQLDialectHandler extends AbstractSQLDialectHandler implements SQLDialectHandler {

    private String[][] columnTypeDefinitions;

    private String[][] getColumnTypeDefinitions() {
        if (columnTypeDefinitions == null) {
            columnTypeDefinitions = new String[][] {
                    { "account", "bigint" },
                    { "actstatus", "varchar(255)" },
                    { "date01", "timestamp without time zone" },
                    { "date02", "timestamp without time zone" },
                    { "date03", "timestamp without time zone" },
                    { "date04", "timestamp without time zone" },
                    { "date01string", "varchar(255)" },
                    { "date02string", "varchar(255)" },
                    { "date03string", "varchar(255)" },
                    { "date04string", "varchar(255)" },
                    { "dateType", "varchar(255)" },
                    { "deleted", "boolean" },
                    { "document", "bigint" },
                    { "documentpath", "varchar(255)" },
                    { "documentpathvariable", "varchar(255)" },
                    { "expiration_time", "timestamp without time zone" },
                    { "id", "bigint" },
                    { "long01", "bigint" },
                    { "long02", "bigint" },
                    { "long03", "bigint" },
                    { "long04", "bigint" },
                    { "menu_path", "varchar(255)" },
                    { "menustructure", "bigint" },
                    { "parent_path01", "varchar(255)" },
                    { "parent_path02", "varchar(255)" },
                    { "parent_path03", "varchar(255)" },
                    { "parent_path04", "varchar(255)" },
                    { "parent01", "bigint" },
                    { "parent02", "bigint" },
                    { "parent03", "bigint" },
                    { "parent04", "bigint" },
                    { "pqvalue01", "double precision" },
                    { "pqsval01", "varchar(255)" },
                    { "pqunits01", "varchar(255)" },
                    { "pqvalue02", "double precision" },
                    { "pqsval02", "varchar(255)" },
                    { "pqunits02", "varchar(255)" },
                    { "pqvalue03", "double precision" },
                    { "pqsval03", "varchar(255)" },
                    { "pqunits03", "varchar(255)" },
                    { "pqvalue04", "double precision" },
                    { "pqsval04", "varchar(255)" },
                    { "pqunits04", "varchar(255)" },
                    { "reference", "bigint" },
                    { "reference_path", "varchar(255)" },
                    { "sourceaccountid", "varchar(255)" },
                    { "status", "varchar(255)" },
                    { "string01", "varchar(255)" },
                    { "string02", "varchar(255)" },
                    { "string03", "varchar(255)" },
                    { "string04", "varchar(255)" },
                    { "string05", "varchar(255)" },
                    { "string06", "varchar(255)" },
                    { "string07", "varchar(255)" },
                    { "string08", "varchar(255)" },
                    { "trimheader", "bigint" },
                    { "xml01", "oid" } };
        }
        return columnTypeDefinitions;
    }

    @Override
    public String getDialect() {
        return "Postgresql";
    }

    @Override
    public String getColumnType(String internalId) {
        for (String[] createColumnExpressionArr : getColumnTypeDefinitions()) {
            String createColumnId = createColumnExpressionArr[0];
            String createColumnExpression = createColumnExpressionArr[1];
            if (createColumnId.equals(internalId)) {
                return createColumnExpression;
            }
        }
        throw new RuntimeException("Could not find a create column expression for internal type: " + internalId);
    }

}
