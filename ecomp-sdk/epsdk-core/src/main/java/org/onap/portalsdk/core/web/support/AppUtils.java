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
package org.onap.portalsdk.core.web.support;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.onap.portalsdk.core.exception.SessionExpiredException;
import org.onap.portalsdk.core.objectcache.AbstractCacheManager;
import org.onap.portalsdk.core.service.DataAccessService;
import org.springframework.beans.factory.annotation.Autowired;

public class AppUtils {

	private static DataAccessService dataAccessService;

	private static AbstractCacheManager cacheManager;

	private static boolean applicationLocked;

	private static DataSource datasource;

	public static DataSource getDatasource() {
		return datasource;
	}

	public AppUtils() {
		super();
	}

	@Autowired
	public void setDatasource(DataSource datasource) {
		AppUtils.datasource = datasource;
	}

	public static HttpSession getSession(HttpServletRequest request) {
		if (request != null) {
			HttpSession session = request.getSession(false);
			if (session == null)
				throw new SessionExpiredException();
			else
				return session;
		} else {
			throw new SessionExpiredException();
		}
	}

	public static List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter,
			String dbOrderBy) {
		return getLookupList(dbTable, dbValueCol, dbLabelCol, dbFilter, dbOrderBy, null);
	} // getLookupList

	public static List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter,
			String dbOrderBy, Session session) {
		String cacheKey = dbTable + "|" + dbValueCol + "|" + dbLabelCol + "|" + dbFilter + "|" + dbOrderBy;
		List list = getLookupListFromCache(cacheKey);
		if (list == null) {
			list = getDataAccessService().getLookupList(dbTable, dbValueCol, dbLabelCol, dbFilter, dbOrderBy, null);
			if (list != null) {
				addLookupListToCache(cacheKey, list);
			}
		} // if
		return list;
	} // getLookupList

	private static List getLookupListFromCache(String key) {
		return (List) getObjectFromCache(key);
	} // getLookupListFromCache

	public static Object getObjectFromCache(String key) {
		if (isCacheManagerAvailable()) {
			return getCacheManager().getObject(key);
		} else {
			return null;
		}
	} // getObjectFromCache

	private static void addLookupListToCache(String key, List list) {
		addObjectToCache(key, list);
	} // addLookupListToCache

	public static void addObjectToCache(String key, Object o) {
		if (isCacheManagerAvailable()) {
			getCacheManager().putObject(key, o);
		}
	} // addObjectToCache

	@Autowired
	public void setCacheManager(AbstractCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public static AbstractCacheManager getCacheManager() {
		return cacheManager;
	}

	public static boolean isCacheManagerAvailable() {
		return getCacheManager() != null;
	}

	public static boolean isApplicationLocked() {
		return applicationLocked;
	}

	public static DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	@Autowired
	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	public static void setApplicationLocked(boolean locked) {
		applicationLocked = locked;
	}

	public static String getLookupValueByLabel(String label, String dbTable, String dbValueCol, String dbLabelCol) {
		if (label == null || label.equals("")) {
			return "";
		}

		List<org.onap.portalsdk.core.domain.Lookup> lstResult = getLookupListNoCache(dbTable, dbValueCol, dbLabelCol,
				dbLabelCol + "='" + label.replaceAll("'", "''") + "'", "");
		if (lstResult == null) {
			return "";
		}
		if (!lstResult.isEmpty()) {	
			return ((org.onap.portalsdk.core.domain.Lookup) lstResult.toArray()[0]).getValue();
		} else {
			return "";
		}
	}

	public static String getLookupValueByLabel(String label, List lookupList) {
		if (label == null || "".equals(label)) 
			return "";
		if (lookupList == null || lookupList.size() == 0)
			return "";

		Iterator i = lookupList.iterator();
		while (i.hasNext()) {
			org.onap.portalsdk.core.domain.Lookup lookup = (org.onap.portalsdk.core.domain.Lookup) i.next();
			if (lookup.getLabel().equals(label)) {
				return lookup.getValue();
			}
		}
		return "";
	}

	public static List getLookupListNoCache(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter,
			String dbOrderBy) {
		return getLookupListNoCache(dbTable, dbValueCol, dbLabelCol, dbFilter, dbOrderBy, null);
	}

	public static List getLookupListNoCache(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter,
			String dbOrderBy, Session session) {
		return getDataAccessService().getLookupList(dbTable, dbValueCol, dbLabelCol, dbFilter, dbOrderBy, null);
	}

}
