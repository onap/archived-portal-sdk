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
package org.onap.portalsdk.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.onap.portalsdk.core.domain.support.DomainVo;

@SuppressWarnings("rawtypes")
public interface DataAccessService {

	// generic view, save, delete methods
	DomainVo getDomainObject(Class domainClass, Serializable id, Map additionalParams);

	void deleteDomainObject(DomainVo domainObject, Map additionalParams);

	void deleteDomainObjects(Class domainClass, String whereClause, Map additionalParams);

	void saveDomainObject(DomainVo domainObject, Map additionalParams);

	// generic get list method(s)
	List getList(Class domainClass, Map additionalParams);

	List getList(Class domainClass, String filter, String orderBy, Map additionalParams);

	List getList(Class domainClass, String filter, int fromIndex, int toIndex, String orderBy,
			Map additionalParams);

	List<?> getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList);

	public List<?> getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList, Map<String, FetchMode> fetchModeMap);

	List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy,
			Map additionalParams);

	// generic native-SQL execution methods
	List executeSQLQuery(String sql, Class domainClass, Map additionalParams);

	List executeSQLQuery(String sql, Class domainClass, Integer fromIndex, Integer toIndex, Map additionalParams);

	// generic HQL execution methods
	List executeQuery(String hql, Map additionalParams);

	List executeQuery(String hql, Integer fromIndex, Integer toIndex, Map additionalParams);

	// generic named query execution methods
	List executeNamedQuery(String queryName, Integer fromIndex, Integer toIndex, Map additionalParams);

	List executeNamedQuery(String queryName, Map params, Map additionalParams);

	List executeNamedQuery(String queryName, Map params, Integer fromIndex, Integer toIndex, Map additionalParams);

	// with Where Clause for RAPTOR ZK
	List executeNamedQueryWithOrderBy(Class entity, String queryName, Map params, String orderBy, boolean asc,
			Integer fromIndex, Integer toIndex, Map additionalParams);

	List executeNamedCountQuery(Class entity, String queryName, String whereClause, Map params);

	List executeNamedQuery(Class entity, String queryName, String whereClause, Map params, Integer fromIndex,
			Integer toIndex, Map additionalParams);

	List executeNamedQueryWithOrderBy(Class entity, String queryName, String whereClause, Map params, String orderBy,
			boolean asc, Integer fromIndex, Integer toIndex, Map additionalParams);

	// generic update query execution method
	int executeUpdateQuery(String sql, Map additionalParams);

	// generic named update query execution method
	int executeNamedUpdateQuery(String queryName, Map params, Map additionalParams);

	// synchronizes the local updates with the database (and vice versa)
	void synchronize(Map additionalParams);

}
