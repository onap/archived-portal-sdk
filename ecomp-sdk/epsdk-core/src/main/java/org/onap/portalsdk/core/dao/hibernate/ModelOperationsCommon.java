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
package org.onap.portalsdk.core.dao.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.type.LongType;
import org.onap.portalsdk.core.dao.support.FusionDao;
import org.onap.portalsdk.core.domain.Lookup;
import org.onap.portalsdk.core.domain.support.DomainVo;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.SystemProperties;

public abstract class ModelOperationsCommon extends FusionDao {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ModelOperationsCommon.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List _getList(Class domainClass, String filterClause, Integer fromIndex, Integer toIndex, String orderBy) {
		String className = domainClass.getName();

		Session session = getSessionFactory().getCurrentSession();

		logger.info(EELFLoggerDelegate.debugLogger, "Getting " + className.toLowerCase() + " records"
				+ ((fromIndex != null) ? " from rows " + fromIndex.toString() + " to " + toIndex.toString() : "")
				+ "...");

		if (filterClause != null && filterClause.length() > 0)
			logger.info(EELFLoggerDelegate.debugLogger, "Filtering " + className + " by: " + filterClause);

		List list = session.createQuery("from " + className + Utilities.nvl(filterClause, "")
				+ ((orderBy != null) ? " order by " + orderBy : "")).list();
		list = (fromIndex != null) ? list.subList(fromIndex.intValue() - 1, toIndex.intValue()) : list;

		if (orderBy == null && list != null)
			Collections.sort(list);

		return list;
	}

	public List<?> _getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList) {
		return _getList(domainClass, projectionsList, restrictionsList, orderByList, null);
	}

	public List<?> _getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList, HashMap<String, FetchMode> fetchModeMap) {

		Session session = getSessionFactory().getCurrentSession();

		Criteria criteria = session.createCriteria(domainClass);

		if (projectionsList != null) {
			criteria.setProjection(projectionsList);
		}

		if (restrictionsList != null && !restrictionsList.isEmpty()) {
			for (Criterion criterion : restrictionsList)
				criteria.add(criterion);
		}

		if (orderByList != null && !orderByList.isEmpty()) {
			for (Order order : orderByList)
				criteria.addOrder(order);
		}

		if (fetchModeMap != null) {
			Iterator<String> itr = fetchModeMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				criteria.setFetchMode(key, fetchModeMap.get(key));
			}

		}
		return criteria.list();
	}

	@SuppressWarnings("rawtypes")
	public DomainVo _get(Class domainClass, Serializable id) {

		Session session = getSessionFactory().getCurrentSession();

		logger.info(EELFLoggerDelegate.debugLogger,
				"Getting " + domainClass.getName() + " record for id - " + id.toString());

		DomainVo vo = (DomainVo) session.get(domainClass, id);

		if (vo == null) {
			try {
				vo = (DomainVo) domainClass.newInstance();
			} catch (Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"Failed while instantiating a class of " + domainClass.getName(), e);
			}
		}

		return vo;
	}

	@SuppressWarnings("rawtypes")
	public List _getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy,
			Map additionalParams) {
		logger.info(EELFLoggerDelegate.debugLogger, "Retrieving " + dbTable + " lookup list...");

		List list = null;
		String dbOrderByCol = dbOrderBy;

		Session session = getSessionFactory().getCurrentSession();

		// default the orderBy if null
		if (Utilities.nvl(dbOrderBy).length() == 0) {
			dbOrderByCol = dbLabelCol;
			dbOrderBy = dbLabelCol;
		} else {
			if (dbOrderBy.lastIndexOf(" ") > -1) {
				dbOrderByCol = dbOrderBy.substring(0, dbOrderBy.lastIndexOf(" "));
			}
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct ").append(dbLabelCol).append(" as lab, ").append(dbValueCol).append(" as val, ")
				.append(dbOrderByCol).append(" as sortOrder ").append("from ").append(dbTable).append(" ")
				.append((Utilities.nvl(dbFilter).length() == 0) ? "" : (" where " + dbFilter)).append(" order by ")
				.append(dbOrderBy);

		try {
			list = session.createSQLQuery(sql.toString()).addEntity(Lookup.class).list();
		} catch (Exception e) {
			list = null;
			logger.error(EELFLoggerDelegate.errorLogger, "_getLookupList failed on SQL: [" + sql + "]", e);
		}

		return list;
	} // getLookupList

	/**
	 * This method is used to execute SQL queries
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeSQLQuery(String sql, Class domainClass) {
		return _executeSQLQuery(sql, domainClass, null, null);
	}

	/**
	 * This method is used to execute SQL queries with paging
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeSQLQuery(String sql, Class domainClass, Integer fromIndex, Integer toIndex) {
		Session session = getSessionFactory().getCurrentSession();

		SQLQuery query = session.createSQLQuery(sql).addEntity(domainClass.getName().toLowerCase(), domainClass);

		if (fromIndex != null && toIndex != null) {
			query.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			query.setMaxResults(pageSize);
		}

		return query.list();
	}

	/**
	 * This method is used to execute HQL queries
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeQuery(String sql) {
		return _executeQuery(sql, null, null);
	}

	/**
	 * This method is used to execute HQL queries with paging
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeQuery(String sql, Integer fromIndex, Integer toIndex) {
		Session session = getSessionFactory().getCurrentSession();

		Query query = session.createQuery(sql);

		if (fromIndex != null && toIndex != null) {
			query.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			query.setMaxResults(pageSize);
		}

		return query.list();
	}

	/**
	 * This method can be used to execute both HQL or SQL named queries. The
	 * distinction will come in the hbm.xml mapping file defining the named query.
	 * Named HQL queries use the <query> tag while named SQL queries use the
	 * <sql-query> tag.
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeNamedQuery(String queryName, Map params) {
		return _executeNamedQuery(queryName, params, null, null);
	}

	/**
	 * This method can be used to execute both HQL or SQL named queries with paging.
	 * The distinction will come in the hbm.xml mapping file defining the named
	 * query. Named HQL queries use the <query> tag while named SQL queries use the
	 * <sql-query> tag.
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeNamedQuery(String queryName, Map params, Integer fromIndex, Integer toIndex) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(queryName);
		bindQueryParameters(query, params);
		if (fromIndex != null && toIndex != null) {
			query.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			query.setMaxResults(pageSize);
		}
		return query.list();
	}

	// RAPTOR ZK

	/**
	 * This method can be used to execute both HQL or SQL named queries with paging.
	 * The distinction will come in the hbm.xml mapping file defining the named
	 * query. Named HQL queries use the <query> tag while named SQL queries use the
	 * <sql-query> tag.
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeNamedCountQuery(Class entity, String queryName, String whereClause, Map params) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(queryName);
		String queryStr = query.getQueryString();
		StringBuilder modifiedSql = new StringBuilder("select count(*) as countRows from (" + queryStr + " ) al ");
		if (whereClause != null && whereClause.length() > 0)
			modifiedSql.append("where " + whereClause);
		SQLQuery sqlQuery = session.createSQLQuery(modifiedSql.toString());
		bindQueryParameters(sqlQuery, params);
		sqlQuery.addScalar("countRows", LongType.INSTANCE);
		return sqlQuery.list();
	}

	/**
	 * This method can be used to execute both HQL or SQL named queries with paging.
	 * The distinction will come in the hbm.xml mapping file defining the named
	 * query. Named HQL queries use the <query> tag while named SQL queries use the
	 * <sql-query> tag. It is modified to test ZK filter.
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeNamedQuery(Class entity, String queryName, String whereClause, Map params,
			Integer fromIndex, Integer toIndex) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(queryName);
		bindQueryParameters(query, params);
		String queryStr = query.getQueryString();
		StringBuilder modifiedSql = new StringBuilder(" select * from (" + queryStr + " ) al ");
		if (whereClause != null && whereClause.length() > 0)
			modifiedSql.append("where " + whereClause);

		SQLQuery sqlQuery = session.createSQLQuery(modifiedSql.toString());
		bindQueryParameters(sqlQuery, params);
		// why is reportSearch hardcoded here?
		sqlQuery.addEntity("reportSearch", entity);

		if (fromIndex != null && toIndex != null) {
			sqlQuery.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			sqlQuery.setMaxResults(pageSize);
		}
		return sqlQuery.list();
	}

	/**
	 * x This method can be used to execute both HQL or SQL named queries with
	 * paging. The distinction will come in the hbm.xml mapping file defining the
	 * named query. Named HQL queries use the <query> tag while named SQL queries
	 * use the <sql-query> tag.
	 */
	@SuppressWarnings("rawtypes")
	protected final List _executeNamedQueryWithOrderBy(Class entity, String queryName, Map params, String _orderBy,
			boolean asc, Integer fromIndex, Integer toIndex) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(queryName);
		bindQueryParameters(query, params);
		String queryStr = query.getQueryString();
		queryStr = String.format(queryStr, _orderBy, asc ? "ASC" : "DESC");
		SQLQuery sqlQuery = session.createSQLQuery(queryStr);
		bindQueryParameters(sqlQuery, params);
		sqlQuery.addEntity("reportSearch", entity);
		if (fromIndex != null && toIndex != null) {
			sqlQuery.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			sqlQuery.setMaxResults(pageSize);
		}
		return sqlQuery.list();
	}

	@SuppressWarnings("rawtypes")
	protected final List _executeNamedQueryWithOrderBy(Class entity, String queryName, String whereClause, Map params,
			String _orderBy, boolean asc, Integer fromIndex, Integer toIndex) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(queryName);
		bindQueryParameters(query, params);
		String queryStr = query.getQueryString();
		queryStr = String.format(queryStr, _orderBy, asc ? "ASC" : "DESC");
		StringBuilder modifiedSql = new StringBuilder(" select * from (" + queryStr + " ) al ");
		if (whereClause != null && whereClause.length() > 0)
			modifiedSql.append("where " + whereClause);
		SQLQuery sqlQuery = session.createSQLQuery(modifiedSql.toString());
		bindQueryParameters(sqlQuery, params);
		sqlQuery.addEntity("reportSearch", entity);
		if (fromIndex != null && toIndex != null) {
			sqlQuery.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			sqlQuery.setMaxResults(pageSize);
		}
		return sqlQuery.list();
	}

	// RAPTOR ZK END

	/* Processes custom Insert/Update/Delete SQL statements */
	protected final int _executeUpdateQuery(String sql) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sql);
		return query.executeUpdate();
	}

	/* Processes Insert/Update/Delete Named SQL statements */
	@SuppressWarnings("rawtypes")
	protected final int _executeNamedUpdateQuery(String queryName, Map params) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(queryName);
		bindQueryParameters(query, params);
		return query.executeUpdate();
	}

	protected final void _update(DomainVo vo, Integer userId) {
		_update(vo, (userId != null) ? userId.intValue() : 0);
	}

	protected final void _update(DomainVo vo, int userId) {
		Date timestamp = new Date();

		Session session = getSessionFactory().getCurrentSession();

		if (vo.getId() == null || vo.getId().intValue() == 0) { // add new
			vo.setCreated(timestamp);
			vo.setModified(timestamp);

			if (userId != 0
					&& userId != Integer.parseInt(SystemProperties.getProperty(SystemProperties.APPLICATION_USER_ID))) {
				vo.setCreatedId(new Long(userId));
				vo.setModifiedId(new Long(userId));
			}
		} else { // update existing
			vo.setModified(timestamp);

			if (userId != 0
					&& userId != Integer.parseInt(SystemProperties.getProperty(SystemProperties.APPLICATION_USER_ID))) {
				vo.setModifiedId(new Long(userId));
			}
		}

		session.saveOrUpdate(vo);
	}

	protected final void _remove(DomainVo vo) {
		Session session = getSessionFactory().getCurrentSession();
		session.delete(vo);
	}

	@SuppressWarnings("rawtypes")
	protected final int _remove(Class domainClass, String whereClause) {
		Session session = getSessionFactory().getCurrentSession();
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(domainClass.getName()).append(" where ").append(whereClause);
		int rowsAffected = session.createQuery(sql.toString()).executeUpdate();
		return rowsAffected;
	}

	protected final void _flush() {
		Session session = getSessionFactory().getCurrentSession();
		session.flush();
	}

	@SuppressWarnings("rawtypes")
	private void bindQueryParameters(Query query, Map params) {
		if (params != null) {
			for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();

				Object parameterValue = entry.getValue();
				if (!(parameterValue instanceof Collection) && !(parameterValue instanceof Object[])) {
					query.setParameter((String) entry.getKey(), parameterValue);
				} else if (parameterValue instanceof Collection) {
					query.setParameterList((String) entry.getKey(), (Collection) parameterValue);
				} else if (parameterValue instanceof Object[]) {
					query.setParameterList((String) entry.getKey(), (Object[]) parameterValue);
				} else {
					logger.warn("bindQueryParameters: unimplemented case for {}", parameterValue);
				}
			}
		}
	}

}
