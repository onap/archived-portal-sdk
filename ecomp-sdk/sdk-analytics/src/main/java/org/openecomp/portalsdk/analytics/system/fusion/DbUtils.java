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
package org.openecomp.portalsdk.analytics.system.fusion;

import java.sql.Connection;

import javax.servlet.ServletContext;

import org.openecomp.portalsdk.analytics.system.IDbUtils;
import org.openecomp.portalsdk.analytics.system.fusion.adapter.FusionAdapter;
import org.openecomp.portalsdk.analytics.system.fusion.adapter.RaptorAdapter;
import org.openecomp.portalsdk.analytics.system.fusion.adapter.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;



public class DbUtils implements IDbUtils {
	
	@Autowired
	public RaptorAdapter raptorAdapter;
	@Autowired
	public FusionAdapter fusionAdapter;

	public DbUtils() {}

	public void initializeDbUtils(ServletContext servletContext) {
		raptorAdapter = (RaptorAdapter)SpringContext.getApplicationContext().getBean("raptorAdapter");
	}   // initializeDbUtils

	public Connection getConnection()  {
		return raptorAdapter.getConnection();
	}   // getConnection

	public void clearConnection(Connection con) {
		raptorAdapter.releaseConnection(con);
	}   // clearConnection

	public RaptorAdapter getRaptorAdapter() {
		return raptorAdapter;
	}

	public void setRaptorAdapter(RaptorAdapter raptorAdapter) {
		this.raptorAdapter = raptorAdapter;
	}

	public FusionAdapter getFusionAdapter() {
		return fusionAdapter;
	}

	public void setFusionAdapter(FusionAdapter fusionAdapter) {
		this.fusionAdapter = fusionAdapter;
	}

	
	

	
}   // DbUtils
