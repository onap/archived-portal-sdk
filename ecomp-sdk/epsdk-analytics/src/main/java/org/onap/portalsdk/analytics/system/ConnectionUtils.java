/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.analytics.system;

import java.sql.Connection;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;

public class ConnectionUtils {
	
	public static DataSet getDataSet(String sql, String remoteDbPrefix)
			throws RaptorException {
		return getDataSet(sql, remoteDbPrefix, false);
	}

	public static Connection getConnection(String remoteDbPrefix) throws ReportSQLException {
		if (!isNull(remoteDbPrefix) && (!remoteDbPrefix.equals(AppConstants.DB_LOCAL)) && !Globals.getSystemType().equals(Globals.ST_GENERIC) ) {
			return RemDbUtils.getConnection(remoteDbPrefix);
		} else {
			return DbUtils.getConnection();
		}
	}
	
	public static DataSet getDataSet(String sql, String remoteDbPrefix,
			boolean pagesize) throws ReportSQLException  {
		DataSet ds = null;
		if (!isNull(remoteDbPrefix) && (!remoteDbPrefix.equals(AppConstants.DB_LOCAL)) && !Globals.getSystemType().equals(Globals.ST_GENERIC) ) {
			if (pagesize == false)
				ds = RemDbUtils.executeQuery(sql,remoteDbPrefix);
			else
				ds = RemDbUtils.executeQuery(sql, Globals.getDefaultPageSize() + 1,remoteDbPrefix);
		} else {
			if (pagesize == false)
				ds = DbUtils.executeQuery(sql);
			else
				ds = DbUtils.executeQuery(sql, Globals.getDefaultPageSize() + 1);
		}
		return ds;
	}
	
	public static boolean isNull(String a) {
		if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
			return true;
		else
			return false;
	}


}
