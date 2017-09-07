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

import java.util.HashMap;

import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;

public class RemDbInfo {
	private HashMap remDbMap 		= null;
	private HashMap remDbTypeMap 	= null;

	public RemDbInfo() throws Exception {
		if (remDbMap == null) {
			load();
		}
	}

	public void load() throws Exception {
        remDbMap 		= new HashMap();
        remDbTypeMap 	= new HashMap();
        try {
		//String query = " SELECT a.SCHEMA_ID, a.SCHEMA_DESC, DATASOURCE_TYPE, rownum id  FROM SCHEMA_INFO a " +
		//		       " where schema_id = 'local' union " +
        //               " SELECT a.SCHEMA_ID, a.SCHEMA_DESC, DATASOURCE_TYPE, (rownum+1) id  FROM SCHEMA_INFO a " +
        //               " where schema_id <> 'local' order by id ";
        String query = Globals.getRemoteDbSchemaSql();
		DataSet ds = null;
		Globals.getDbUtils();
		ds = DbUtils.executeQuery(query);

		String prefix = "", desc = "", dbType = "";
		
		if(ds.getRowCount() > 0) {
			for (int i = 0; i < ds.getRowCount(); i++) {
				prefix = ds.getItem(i, 0);
				desc   = ds.getItem(i, 1);
				dbType = ds.getItem(i, 2);
				
				remDbMap.put(prefix, desc);
				remDbTypeMap.put(prefix, dbType);
			}
		} else {
			remDbMap.put("local", "local");
			remDbTypeMap.put("local", Globals.getDBType());
		}
        }
        catch (Exception e) {}
        
	}

	public String getDesc(String prefix) {
		if ((remDbMap != null) && (remDbMap.containsKey(prefix))) {
			return (String) remDbMap.get(prefix);
		}

		return "";
	}
	
	public String getDBType(String prefix) {
		if ((remDbTypeMap != null) && (remDbTypeMap.containsKey(prefix))) {
			return (String) remDbTypeMap.get(prefix);
		}

		return "";
	}	

	public HashMap getDbHash() {
		return remDbMap;
	}
	
	public HashMap getDbTypeHash() {
		return remDbTypeMap;
	}	
}
