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
package org.onap.portalsdk.analytics.system.fusion.service;

import java.util.List;
import java.util.Map;

import org.onap.portalsdk.analytics.system.fusion.domain.CR_Report;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.service.support.FusionService;
import org.springframework.context.ApplicationContext;

public class RaptorServiceImpl extends FusionService implements RaptorService {

	private DataAccessService dataAccessService;
	
	private int totalSize;

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
	private ApplicationContext applicationContext;

    //raptorSearchAllReportsCount
	
	public int executeCountQuery(Class entity, String query, Map params, String whereClause) {
		List l  = getDataAccessService().executeNamedCountQuery(entity, query, whereClause, params);
		//List l  = getDataAccessService().executeNamedQuery(query, params, null);
		int searchCount = 0;
		if(l != null) {
			if (!l.isEmpty()) {
				searchCount = (((Long) l.get(0))).intValue();
			}
		}
		totalSize = searchCount;
		return totalSize;
	}
	
	//raptorSearchAllReports
	public List executeGridQuery(String query, Map params, Integer fromIndex, Integer toIndex) {
	  return getDataAccessService().executeNamedQuery(query, params, fromIndex, toIndex, null);
	}

	public List executeGridQueryOrderByWithLimit(Class entity, String query, Map params, String _orderBy, boolean asc, Integer fromIndex, Integer toIndex) {
		return getDataAccessService().executeNamedQueryWithOrderBy(entity, query, params, _orderBy, asc, fromIndex, toIndex, null);
	}
	
    //with where clause
	public List executeGridQuery(Class entity, String query, String whereClause, Map params, Integer fromIndex, Integer toIndex) {
		  return getDataAccessService().executeNamedQuery(entity, query, whereClause, params, fromIndex, toIndex, null);
	}

	public List executeGridQueryOrderByWithLimit(Class entity, String query, String whereClause, Map params, String _orderBy, boolean asc, Integer fromIndex, Integer toIndex) {
			return getDataAccessService().executeNamedQueryWithOrderBy(entity, query, whereClause, params, _orderBy, asc, fromIndex, toIndex, null);
	}	
	
	/*
	public int getUserReportsCount(Map params) {
		List l  = getDataAccessService().executeNamedQuery("raptorSearchUserReportsCount", params, null);
		int count = 0;
		if(l != null) {
			if (!l.isEmpty()) {
				Object[] result = (Object[]) l.get(0);
				count = ((Long)result[0]).intValue();
			}
		}
		totalSize = count;
		return totalSize;
	}
	
	public List getUserReports(Map params, Integer fromIndex, Integer toIndex) {
	  return getDataAccessService().executeNamedQuery("raptorSearchUserReports", params, fromIndex, toIndex, null);
	}

	public int getPublicReportsCount(Map params) {
		List l  = getDataAccessService().executeNamedQuery("raptorPublicUserReportsCount", params, null);
		int count = 0;
		if(l != null) {
			if (!l.isEmpty()) {
				Object[] result = (Object[]) l.get(0);
				count = ((Long)result[0]).intValue();
			}
		}
		totalSize = count;
		return totalSize;
	}
	
	public List getPublicReports(Map params, Integer fromIndex, Integer toIndex) {
	  return getDataAccessService().executeNamedQuery("raptorPublicUserReports", params, fromIndex, toIndex, null);
	}

	public int getFavReportsCount(Map params) {
		List l  = getDataAccessService().executeNamedQuery("raptorSearchFavReportsCount", params, null);
		int count = 0;
		if(l != null) {
			if (!l.isEmpty()) {
				Object[] result = (Object[]) l.get(0);
				count = ((Long)result[0]).intValue();
			}
		}
		totalSize = count;
		return totalSize;
	}
	
	public List getFavReports(Map params, Integer fromIndex, Integer toIndex) {
	  return getDataAccessService().executeNamedQuery("raptorSearchFavReports", params, fromIndex, toIndex, null);
	}
	*/	
	public int getTotalSize() {
		return totalSize;
	}
	
	public void deleteReport(Long reportId) {
		getDataAccessService().deleteDomainObject(getDataAccessService().getDomainObject(CR_Report.class, reportId, null), null);
	}
	
	public List getReportInfo(Map params) {
		return getDataAccessService().executeNamedQuery("raptorInfoQuery", params, null);
	}
	 
/*	public List executeGridQueryWithOrderBy(String query, Map params, String _orderBy, boolean asc, Integer fromIndex, Integer toIndex) {
		query = String.format(query, _orderBy, asc ? "ASC" : "DESC", fromIndex, toIndex);
		DataSource ds = (DataSource)getApplicationContext().getBean(searchBean.getDatasourceName());
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(ds);
	}*/

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @param applicationContext the applicationContext to set
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
