/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.analytics.util;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class DataSet extends Vector implements Serializable {
	private Vector columnNames = null;

	private Vector columnTypes = null;

	public DataSet(ResultSet rs) throws SQLException {
		this(rs, Integer.MAX_VALUE);
	} // DataSet

	public DataSet(ResultSet rs, int maxRowLimit) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();

		int colCount = rsmd.getColumnCount();
		columnNames = new Vector(colCount);
		columnTypes = new Vector(colCount);
		for (int i = 1; i <= colCount; i++) {
			columnNames.add(rsmd.getColumnLabel(i)); // getColumnLabel ??
			columnTypes.add(rsmd.getColumnTypeName(i));
		} // for

		while (rs.next() && size() < maxRowLimit) {
//   			if(runtime.freeMemory()/mb <= ((runtime.maxMemory()/mb)*(Globals.getMemoryThreshold()*2)/100) ) {
//   				System.out.println("freeMemory " + runtime.freeMemory());
//   				System.out.println("Max Memory " + runtime.maxMemory());
//   				System.out.println("If Logic " + (runtime.freeMemory()/mb <= ((runtime.maxMemory()/mb)*(Globals.getMemoryThreshold()*2)/100)));
//   				break;
//   			}
			
			Vector v = new Vector(colCount);
			for (int i = 1; i <= colCount; i++)
				v.add(rs.getString(i));
			add(v);
		} // while
        
		   if(rs!=null) 
				rs.close();
	} // DataSet

	public DataSet() {
		columnNames = new Vector();
		columnTypes = new Vector();
	} // DataSet

	public void insertRow(int rowIdx) {
		if (rowIdx > size())
			rowIdx = size();

		Vector v = new Vector(columnNames.size());
		for (int i = 0; i < columnNames.size(); i++)
			v.add("");
		add(rowIdx, v);
	} // insertRow

	public void insertColumn(int colIdx, String colName) {
		insertColumn(colIdx, colName, "VARCHAR2");
	} // insertColumn

	public void insertColumn(int colIdx, String colName, String colType) {
		if (colIdx > columnNames.size())
			colIdx = columnNames.size();

		columnNames.add(colIdx, colName);
		columnTypes.add(colIdx, colType);

		for (int i = 0; i < size(); i++)
			((Vector) get(i)).add(colIdx, "");
	} // insertColumn

	public void setValue(int rowIdx, int colIdx, String value) {
		((Vector) get(rowIdx)).set(colIdx, value);
	} // setValue

	public void setValue(int rowIdx, String colName, String value) {
		((Vector) get(rowIdx)).set(getColumnIndex(colName), value);
	} // setValue

	public void setString(int rowIdx, int colIdx, String value) {
		setValue(rowIdx, colIdx, value);
	} // setString

	public void setString(int rowIdx, String colName, String value) {
		setValue(rowIdx, colName, value);
	} // setString

	public int getRowCount() {
		return size();
	} // getRowCount()

	public int getColumnCount() {
		return columnNames.size();
	} // getColumnCount

	public String getColumnName(int colIdx) {
		return ((String) columnNames.get(colIdx));
	} // getColumnName

	public String getColumnType(int colIdx) {
		return ((String) columnTypes.get(colIdx));
	} // getColumnType

	public String getColumnType(String colName) {
		return getColumnType(getColumnIndex(colName));
	} // getColumnType

	public int getColumnIndex(String colName) {
		for (int i = 0; i < columnNames.size(); i++)
			if (colName.equalsIgnoreCase((String) columnNames.get(i)))
				return i;

		return -1;
	} // getColumnIndex

	public String getString(int rowIdx, int colIdx) {
		return nvl((String) ((Vector) get(rowIdx)).get(colIdx));
	} // getString

	public String getString(int rowIdx, String colName) {
		return getString(rowIdx, getColumnIndex(colName));
	} // getString

	public int getInt(int rowIdx, int colIdx) {
		return Integer.parseInt(getString(rowIdx, colIdx));
	} // getString

	public int getInt(int rowIdx, String colName) {
		return getInt(rowIdx, getColumnIndex(colName));
	} // getString

	public String getItem(int rowIdx, int colIdx) {
		return getString(rowIdx, colIdx);
	} // getItem

	public String getItem(int rowIdx, String colName) {
		return getString(rowIdx, colName);
	} // getItem

	/** *********************************************************************** */

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}

} // DataSet

