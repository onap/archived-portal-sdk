/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.analytics.model.base;

import java.util.*;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.model.*;
import org.openecomp.portalsdk.analytics.model.runtime.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;

public class IdNameLookup extends IdNameSql {
	private String dbTableName = null;

	private String dbIdField = null;

	private String dbNameField = null;

	private String dbSortByField = null;

	private String searchString = "";

	public IdNameLookup(int pageNo, String dbTableName, String dbIdField, String dbNameField) {
		this(dbTableName, dbIdField, dbNameField);

		this.pageNo = pageNo;
	} // IdNameLookup

	public IdNameLookup(String dbTableName, String dbIdField, String dbNameField) {
		this(dbTableName, dbIdField, dbNameField, null, "", false);
	} // IdNameLookup

/*    public IdNameLookup(String dbTableName, String dbIdField, String dbNameField,
            String dbSortByField) {
        super();

        setDbTableName(dbTableName);
        setDbIdField(dbIdField);
        setDbNameField(dbNameField);
        setDbSortByField(dbSortByField);
        updateParentSQL();
    } // IdNameLookup

    public IdNameLookup(String dbTableName, String dbIdField, String dbNameField,
			String dbSortByField, String defaultSQL) {
		super();

		setDbTableName(dbTableName);
		setDbIdField(dbIdField);
		setDbNameField(dbNameField);
		setDbSortByField(dbSortByField);
        setDefaultSQL(defaultSQL);        
		updateParentSQL();
	} // IdNameLookup
*/
    public IdNameLookup(String dbTableName, String dbIdField, String dbNameField,
            String dbSortByField, boolean textField) {
        super();
        setDbTableName(dbTableName);
        setDbIdField(dbIdField);
        setDbNameField(dbNameField);
        setDbSortByField(dbSortByField);
        if(!textField)
        	updateParentSQL();
    } // IdNameLookup

    public IdNameLookup(String dbTableName, String dbIdField, String dbNameField,
			String dbSortByField, String defaultSQL, boolean textField) {
		super();

		setDbTableName(dbTableName);
		setDbIdField(dbIdField);
		setDbNameField(dbNameField);
		setDbSortByField(dbSortByField);
        setDefaultSQL(defaultSQL);  
        if(!textField)
        	updateParentSQL();
	} // IdNameLookup

    public String getDbTableName() {
		return dbTableName;
	}

	public String getDbIdField() {
		return dbIdField;
	}

	public String getDbNameField() {
		return dbNameField;
	}

	public String getDbSortByField() {
		return dbSortByField;
	}

	public void setDbTableName(String dbTableName) {
		this.dbTableName = dbTableName;
	}

	public void setDbIdField(String dbIdField) {
		this.dbIdField = dbIdField;
	}

	public void setDbNameField(String dbNameField) {
		this.dbNameField = dbNameField;
	}


	public void setDbSortByField(String dbSortByField) {
		this.dbSortByField = dbSortByField;
	}

	private void updateParentSQL() {
		String sql_start = "SELECT DISTINCT " + dbIdField + " id, " + dbNameField + " name";
		String sql_end = " FROM " + dbTableName + " WHERE " + dbIdField + " IS NOT NULL";
		if (searchString.length() > 0)
			sql_end += " AND UPPER(" + dbNameField + ") LIKE UPPER('" + searchString + "')";

		String sql_middle = "";
		if (dbSortByField != null && (!dbSortByField.equals(dbNameField)) && (!dbSortByField.trim().startsWith("TO_DATE")))
			sql_middle = ", "
					+ ((dbSortByField.indexOf(' ') > 0) ? dbSortByField.substring(0,
							dbSortByField.indexOf(' ')) : dbSortByField) + " sort";

		setSqlNoOrderBy(sql_start + sql_middle + sql_end);
//        System.out.println("SQL Start " + sql_start);
//        System.out.println("SQL Middle " + sql_middle);
//        System.out.println("SQL End " + sql_end);
//        System.out.println("DbSortByField " + dbSortByField);
        
		setSql(sql_start + sql_middle + sql_end + " ORDER BY " + nvl(dbSortByField, "2"));
	} // updateParentSQL

	public boolean canUseSearchString() {
		return true;
	}

	public String getBaseSQL() {
		return "SELECT " + dbIdField + " FROM " + dbTableName;
	} // getBaseSQL

	public String getBaseWholeSQL() {
		return "SELECT " + dbIdField + " FROM " + dbTableName;
	} // getBaseSQL
	
	/*
	public void loadData(int pageNo) throws RaptorException {
		loadData(pageNo, "");
	} // loadData

	public void loadData(String pageNo) throws RaptorException {
		loadData(pageNo, "");
	} // loadData
*/
	
	public void loadData(String pageNo, String searchString, String dbInfo) throws RaptorException {
		int iPageNo = 0;

		if (pageNo != null)
			try {
				iPageNo = Integer.parseInt(pageNo);
			} catch (NumberFormatException e) {
			}

		loadData(iPageNo, searchString, dbInfo);
	} // loadData

	private void loadData(int pageNo, String searchString, String dbInfo) throws RaptorException {
		boolean dataAlreadyLoaded = (this.pageNo == pageNo)
				&& (this.searchString.equals(searchString));

		if (dataAlreadyLoaded)
			return;

		if (!this.searchString.equals(searchString)) {
			dataSize = -1;
			pageNo = 0;
		} // if

		this.pageNo = pageNo;
		this.searchString = searchString;
		updateParentSQL();
		performLoadData(searchString,dbInfo);
	} // loadData

} // IdNameLookup
