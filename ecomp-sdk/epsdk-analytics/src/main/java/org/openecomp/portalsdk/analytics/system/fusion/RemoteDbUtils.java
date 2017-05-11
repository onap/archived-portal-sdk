/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalsdk.analytics.system.fusion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;

import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.system.RDbUtils;
import org.openecomp.portalsdk.analytics.system.fusion.adapter.RaptorAdapter;
import org.openecomp.portalsdk.analytics.system.fusion.adapter.SpringContext;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.Log;
import org.springframework.beans.factory.annotation.Autowired;



public class RemoteDbUtils implements RDbUtils{

	private RaptorAdapter raptorAdapter;

	
	public void initializeDbUtils(ServletContext servletContext) {
		raptorAdapter = (RaptorAdapter)SpringContext.getApplicationContext().getBean("raptorAdapter");
	} // initializeDbUtils


    public Connection getRemoteConnection(String dbKey) {
		return raptorAdapter.getConnection(dbKey);
	} 

	public void clearConnection(Connection conn) {
		raptorAdapter.releaseConnection(conn);
	} 

}
