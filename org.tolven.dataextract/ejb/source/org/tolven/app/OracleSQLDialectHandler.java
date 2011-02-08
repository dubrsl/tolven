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

import java.util.Date;

public class OracleSQLDialectHandler extends AbstractSQLDialectHandler implements SQLDialectHandler {

    private String[][] columnTypeDefinitions;

    private String[][] getColumnTypeDefinitions() {
        if (columnTypeDefinitions == null) {
            columnTypeDefinitions = new String[][] {
                    { "account", "number(19,0)" },
                    { "actstatus", "varchar2(255)" },
                    { "date01", "timestamp(6)" },
                    { "date02", "timestamp(6)" },
                    { "date03", "timestamp(6)" },
                    { "date04", "timestamp(6)" },
                    { "date01string", "varchar2(255)" },
                    { "date02string", "varchar2(255)" },
                    { "date03string", "varchar2(255)" },
                    { "date04string", "varchar2(255)" },
                    { "dateType", "varchar2(255)" },
                    { "deleted", "number(1)" },
                    { "document", "number(19,0)" },
                    { "documentpath", "varchar2(255)" },
                    { "documentpathvariable", "varchar2(255)" },
                    { "expiration_time", "timestamp(6)" },
                    { "id", "number(19,0)" },
                    { "long01", "number(19,0)" },
                    { "long02", "number(19,0)" },
                    { "long03", "number(19,0)" },
                    { "long04", "number(19,0)" },
                    { "menu_path", "varchar2(255)" },
                    { "menustructure", "number(19,0)" },
                    { "parent_path01", "varchar2(255)" },
                    { "parent_path02", "varchar2(255)" },
                    { "parent_path03", "varchar2(255)" },
                    { "parent_path04", "varchar2(255)" },
                    { "parent01", "number(19,0)" },
                    { "parent02", "number(19,0)" },
                    { "parent03", "number(19,0)" },
                    { "parent04", "number(19,0)" },
                    { "pqvalue01", "float" },
                    { "pqsval01", "varchar2(255)" },
                    { "pqunits01", "varchar2(255)" },
                    { "pqvalue02", "float" },
                    { "pqsval02", "varchar2(255)" },
                    { "pqunits02", "varchar2(255)" },
                    { "pqvalue03", "float" },
                    { "pqsval03", "varchar2(255)" },
                    { "pqunits03", "varchar2(255)" },
                    { "pqvalue04", "float" },
                    { "pqsval04", "varchar2(255)" },
                    { "pqunits04", "varchar2(255)" },
                    { "reference", "number(19,0)" },
                    { "reference_path", "varchar2(255)" },
                    { "sourceaccountid", "varchar2(255)" },
                    { "status", "varchar2(255)" },
                    { "string01", "varchar2(255)" },
                    { "string02", "varchar2(255)" },
                    { "string03", "varchar2(255)" },
                    { "string04", "varchar2(255)" },
                    { "string05", "varchar2(255)" },
                    { "string06", "varchar2(255)" },
                    { "string07", "varchar2(255)" },
                    { "string08", "varchar2(255)" },
                    { "trimheader", "number(19,0)" },
                    { "xml01", "blob" } };
        }
        return columnTypeDefinitions;
    }

    @Override
    public String getDialect() {
        return "Oracle";
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
