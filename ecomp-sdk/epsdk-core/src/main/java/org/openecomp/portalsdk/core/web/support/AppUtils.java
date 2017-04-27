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
package org.openecomp.portalsdk.core.web.support;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.openecomp.portalsdk.core.exception.SessionExpiredException;
import org.openecomp.portalsdk.core.objectcache.AbstractCacheManager;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


public class AppUtils {
    
	
	
	private static DataAccessService  dataAccessService;
	
    private static AbstractCacheManager cacheManager;

    private static boolean applicationLocked;

    private static Hashtable feedback = new Hashtable();
    
    private static DataSource datasource;

    public static DataSource getDatasource() {
		return datasource;
	}
    
    @Autowired
	public void setDatasource(DataSource datasource) {
		AppUtils.datasource = datasource;
	}
    
    public AppUtils() {
    }

    public static HttpSession getSession(HttpServletRequest request) {
        HttpSession session = null;
        if (request != null) {
            session = request.getSession(false);
            if (session == null) {
                throw new SessionExpiredException();
            }
        } else {
            throw new SessionExpiredException();
        }
        return session;
    }
    
    public static List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy) {
        return getLookupList(dbTable, dbValueCol, dbLabelCol, dbFilter, dbOrderBy, null);
    } // getLookupList
    
    public static List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy, Session session) {
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
        return (List)getObjectFromCache(key);
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
        return (getCacheManager() != null);
    }
    
    public void setFeedback(Hashtable feedback) {
        this.feedback = feedback;
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

        List<org.openecomp.portalsdk.core.domain.Lookup> lstResult = getLookupListNoCache(dbTable, dbValueCol, dbLabelCol, dbLabelCol + "='" + label.replaceAll("'", "''") + "'", "");
        if (lstResult == null) {
            return "";
        }
        if (lstResult.size() > 0) {
            return ((org.openecomp.portalsdk.core.domain.Lookup)lstResult.toArray()[0]).getValue();
        } else {
            return "";
        }
    }

    public static String getLookupValueByLabel(String label, List lookupList) {
      Iterator i = null;

      if (label == null || label.equalsIgnoreCase("")) {
          return "";
      }

      if (lookupList == null || lookupList.size() == 0) {
          return "";
      }

      i = lookupList.iterator();
      while (i.hasNext()) {
        org.openecomp.portalsdk.core.domain.Lookup lookup = (org.openecomp.portalsdk.core.domain.Lookup)i.next();

        if (lookup.getLabel().equals(label)) {
            return lookup.getValue();
        }
      }

      return "";
}
 public static List getLookupListNoCache(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy) {
        return getLookupListNoCache(dbTable, dbValueCol, dbLabelCol, dbFilter, dbOrderBy, null);
    } // getLookupListNoCache


    public static List getLookupListNoCache(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy, Session session) {
        return getDataAccessService().getLookupList(dbTable, dbValueCol, dbLabelCol, dbFilter, dbOrderBy, null);
    } // getLookupListNoCache
    
	

} // AppUtils
