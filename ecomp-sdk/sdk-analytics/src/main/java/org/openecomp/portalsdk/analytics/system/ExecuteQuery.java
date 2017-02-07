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
package org.openecomp.portalsdk.analytics.system;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

public class ExecuteQuery implements Callable<ResultSet>{

	private Statement stmt;
	private String sql = "";
	private int maxRowLimit;
	private ResultSet resultSet;
	
	public ResultSet getResultSet() {
		return resultSet;
	}
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

    public ExecuteQuery(Statement stmt, String sql, int maxRowLimit) {
		super();
		this.stmt = stmt;
		this.sql = sql;
		this.maxRowLimit = maxRowLimit;
	}
	
	public ResultSet call() throws SQLException {
		try {
		    System.out.println("Query Started" + new java.util.Date());
		    resultSet = stmt.executeQuery(sql);
			    System.out.println("Query End" + new java.util.Date());
		} catch (SQLException ex) {
		    System.out.println("Query Exception" + new java.util.Date());
			ex.printStackTrace();
			throw ex;
		}
		return resultSet;
	}
}
