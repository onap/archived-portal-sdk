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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.onap.portalsdk.core.domain.Lookup;
import org.onap.portalsdk.core.domain.support.DomainVo;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.support.FusionService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides implementations of methods in {@link DataAccessService}.
 */
@Transactional
public class DataAccessServiceImpl extends FusionService implements DataAccessService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(DataAccessServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public DomainVo getDomainObject(Class domainClass, Serializable id, Map additionalParams) {
		Session session = sessionFactory.getCurrentSession();
		logger.info(EELFLoggerDelegate.debugLogger,
				"Getting " + domainClass.getName() + " record for id - " + id.toString());
		DomainVo vo = (DomainVo) session.get(domainClass, id);
		if (vo == null) {
			try {
				vo = (DomainVo) domainClass.newInstance();
			} catch (Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"getDomainObject failed while instantiating class " + domainClass.getName(), e);
			}
		}
		return vo;
	}

	@Override
	public void deleteDomainObject(DomainVo domainObject, Map additionalParams) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(domainObject);
	}

	@Override
	public void deleteDomainObjects(Class domainClass, String whereClause, Map additionalParams) {
		Session session = sessionFactory.getCurrentSession();
		StringBuffer sql = new StringBuffer("delete from ");
		sql.append(domainClass.getName()).append(" where ").append(whereClause);
		Query query = session.createQuery(sql.toString());
		query.executeUpdate();
	}

	@Override
	public void saveDomainObject(DomainVo vo, Map additionalParams) {
		Integer userId = 1;
		if (additionalParams != null) {
			Object uid = additionalParams.get(Parameters.PARAM_USERID);
			if (uid instanceof Integer) {
				userId = (Integer) uid;
			} else if (uid instanceof Long) {
				userId = ((Long) uid).intValue();
			}
		}
		_update(vo, userId);
	}

	/**
	 * Creates or updates the specified virtual object. Uses the specified user ID
	 * as the creator and modifier if a new object is created; uses ID only as
	 * modifier if an object already exists.
	 * 
	 * @param vo
	 * @param userId
	 *            Ignored if value is zero.
	 */
	protected final void _update(DomainVo vo, int userId) {
		Date timestamp = new Date();

		Session session = sessionFactory.getCurrentSession();

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

	/**
	 * generic get list method
	 * 
         * @deprecated
         * This method may be vulnerable to SQL Injection attacks depending on the usage and is being deprecated. Please use
         * getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList) method instead
	 * 
	 * @param domainClass
	 * @param filterClause
	 * @param fromIndex
	 * @param toIndex
	 * @param orderBy
	 * @return
	 */
	@Deprecated
	private List getListCommon(Class domainClass, String filterClause, Integer fromIndex, Integer toIndex,
			String orderBy) {
		String className = domainClass.getName();
		Session session = sessionFactory.getCurrentSession();

		if (logger.isInfoEnabled()) {
			logger.info(EELFLoggerDelegate.debugLogger, "Getting " + className.toLowerCase() + " records"
					+ ((fromIndex != null) ? " from rows " + fromIndex.toString() + " to " + toIndex.toString() : "")
					+ "...");
			if (filterClause != null && filterClause.length() > 0)
				logger.info(EELFLoggerDelegate.debugLogger, "Filtering " + className + " by: " + filterClause);
		}

		List list = session.createQuery("from " + className + Utilities.nvl(filterClause, "")
				+ ((orderBy != null) ? " order by " + orderBy : "")).list();
		list = (fromIndex != null) ? list.subList(fromIndex.intValue() - 1, toIndex.intValue()) : list;
		if (orderBy == null && list != null)
			Collections.sort(list);

		return list;
	}

	/**
          * @deprecated
          * This method may be vulnerable to SQL Injection attacks depending on the usage and is being deprecated. Please use
          * getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList) method instead
        */
	@Override
	@Deprecated
	public List getList(Class domainClass, Map additionalParams) {
		return getListCommon(domainClass, null, null, null, null);
	}

	 /**
     * @deprecated
     * This method may be vulnerable to SQL Injection attacks depending on the usage and is being deprecated. Please use
     * getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList) method instead
     */
	@Override
	@Deprecated
	public List getList(Class domainClass, String filter, String orderBy, Map additionalParams) {
		return getListCommon(domainClass, filter, null, null, orderBy);
	}
	
	/**
     * @deprecated
     * This method may be vulnerable to SQL Injection attacks depending on the usage and is being deprecated. Please use
     * getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList) method instead
     */
	@Override
	@Deprecated
	public List getList(Class domainClass, String filter, int fromIndex, int toIndex, String orderBy,
			Map additionalParams) {
		return getListCommon(domainClass, filter, new Integer(fromIndex), new Integer(toIndex), orderBy);
	}

	@Override
	public List<?> getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList) {

		Session session = sessionFactory.getCurrentSession();

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

		return criteria.list();
	}

	@Override
	public List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy,
			Map additionalParams) {
		if (logger.isInfoEnabled())
			logger.info(EELFLoggerDelegate.debugLogger, "Retrieving " + dbTable + " lookup list...");
		String dbOrderByCol = dbOrderBy;

		Session session = sessionFactory.getCurrentSession();

		// default the orderBy if null
		if (Utilities.nvl(dbOrderBy).length() == 0) {
			dbOrderByCol = dbLabelCol;
			dbOrderBy = dbLabelCol;
		} else if (dbOrderBy.lastIndexOf(" ") > -1) {
			dbOrderByCol = dbOrderBy.substring(0, dbOrderBy.lastIndexOf(" "));
		}

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct ").append(dbLabelCol).append(" as lab, ").append(dbValueCol).append(" as val, ")
				.append(dbOrderByCol).append(" as sortOrder ").append("from ").append(dbTable).append(" ")
				.append((Utilities.nvl(dbFilter).length() == 0) ? "" : (" where " + dbFilter)).append(" order by ")
				.append(dbOrderBy);

		List list = null;
		try {
			list = session.createSQLQuery(sql.toString()).addEntity(Lookup.class).list();
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getLookupList failed on query query [" + sql + "]", e);
		}
		return list;
	}

	/*
	 * methods accepting a Map of additional params to passed to the DAO (for
	 * extensibility, just in case)
	 */

	@Override
	public List executeSQLQuery(String sql, Class domainClass, Map additionalParams) {
		return executeSQLQuery(sql, domainClass, null, null, additionalParams);
	}

	@Override
	public List executeSQLQuery(String sql, Class domainClass, Integer fromIndex, Integer toIndex,
			Map additionalParams) {
		Session session = sessionFactory.getCurrentSession();

		SQLQuery query = session.createSQLQuery(sql).addEntity(domainClass.getName().toLowerCase(), domainClass);

		if (fromIndex != null && toIndex != null) {
			query.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			query.setMaxResults(pageSize);
		}

		return query.list();
	}

	@Override
	public List executeQuery(String sql, Map additionalParams) {
		return executeQuery(sql, null, null, additionalParams);
	}

	@Override
	public List executeQuery(String sql, Integer fromIndex, Integer toIndex, Map additionalParams) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(sql);

		if (fromIndex != null && toIndex != null) {
			query.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			query.setMaxResults(pageSize);
		}

		return query.list();
	}

	@Override
	public List executeNamedQuery(String queryName, Integer fromIndex, Integer toIndex, Map additionalParams) {
		return executeNamedQuery(queryName, null, fromIndex, toIndex, additionalParams);
	}

	@Override
	public List executeNamedQuery(String queryName, Map params, Map additionalParams) {
		return executeNamedQuery(queryName, params, null, null, additionalParams);
	}

	@Override
	public List executeNamedQuery(String queryName, Map params, Integer fromIndex, Integer toIndex,
			Map additionalParams) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.getNamedQuery(queryName);
		bindQueryParameters(query, params);
		if (fromIndex != null && toIndex != null) {
			query.setFirstResult(fromIndex.intValue());
			int pageSize = (toIndex.intValue() - fromIndex.intValue()) + 1;
			query.setMaxResults(pageSize);
		}
		return query.list();
	}

	/**
	 * Stores parameters into the query using String keys from the map. Gives
	 * special treatment to map values of Collection and array type.
	 * 
	 * @param query
	 *            Query with parameters
	 * @param params
	 *            Map of String to Object.
	 */
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
					logger.error(EELFLoggerDelegate.errorLogger, "bindQueryParameters: no match for value {}",
							parameterValue);
				}
			}
		}
	}

	// With Where Clause & RAPTOR's ZK

	@Override
	public List executeNamedQueryWithOrderBy(Class entity, String queryName, Map params, String orderBy, boolean asc,
			Integer fromIndex, Integer toIndex, Map additionalParams) {
		logger.error(EELFLoggerDelegate.errorLogger, "Not implemented");
		throw new UnsupportedOperationException();
	}

	@Override
	public List executeNamedCountQuery(Class entity, String queryName, String whereClause, Map params) {
		logger.error(EELFLoggerDelegate.errorLogger, "Not implemented");
		throw new UnsupportedOperationException();
	}

	@Override
	public List executeNamedQuery(Class entity, String queryName, String whereClause, Map params, Integer fromIndex,
			Integer toIndex, Map additionalParams) {
		logger.error(EELFLoggerDelegate.errorLogger, "Not implemented");
		throw new UnsupportedOperationException();
	}

	@Override
	public List executeNamedQueryWithOrderBy(Class entity, String queryName, String whereClause, Map params,
			String orderBy, boolean asc, Integer fromIndex, Integer toIndex, Map additionalParams) {
		logger.error(EELFLoggerDelegate.errorLogger, "Not implemented");
		throw new UnsupportedOperationException();
	}

	@Override
	public List<?> getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList, Map<String, FetchMode> fetchModeMap) {
		logger.error(EELFLoggerDelegate.errorLogger, "Not implemented");
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdateQuery(String sql, Map additionalParams) {
		logger.error(EELFLoggerDelegate.errorLogger, "Not implemented");
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeNamedUpdateQuery(String queryName, Map params, Map additionalParams) {
		Session session = sessionFactory.getCurrentSession();
	    Query query = session.getNamedQuery(queryName);    
	    bindQueryParameters(query,params);
	    return query.executeUpdate();
	}

	@Override
	public void synchronize(Map additionalParams) {
		logger.error(EELFLoggerDelegate.errorLogger, "Not implemented");
		throw new UnsupportedOperationException();
	}

}
