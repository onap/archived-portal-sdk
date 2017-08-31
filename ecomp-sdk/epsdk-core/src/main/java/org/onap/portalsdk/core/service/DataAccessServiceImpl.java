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
package org.onap.portalsdk.core.service;

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
	public DomainVo getDomainObject(Class domainClass, Serializable id, HashMap additionalParams) {
		DomainVo vo = null;
		Session session = sessionFactory.getCurrentSession();
		logger.info(EELFLoggerDelegate.debugLogger, "Getting " + domainClass.getName() + " record for id - " + id.toString());
		vo = (DomainVo) session.get(domainClass, id);

		if (vo == null) {
			try {
				vo = (DomainVo) domainClass.newInstance();
			} catch (Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger, "An error occured while instantiating a class of " + domainClass.getName() + e.getMessage());
			}
		}
		return vo;
	}

	@Override
	public void deleteDomainObject(DomainVo domainObject, HashMap additionalParams) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(domainObject);
	}

	@Override
	public void deleteDomainObjects(Class domainClass, String whereClause, HashMap additionalParams) {
		int rowsAffected = 0;
		Session session = sessionFactory.getCurrentSession();

		StringBuffer sql = new StringBuffer("delete from ");

		sql.append(domainClass.getName()).append(" where ").append(whereClause);

		rowsAffected = session.createQuery(sql.toString()).executeUpdate();
		/* return rowsAffected; */
	}

	@Override
	public void saveDomainObject(DomainVo vo, HashMap additionalParams) {
		Integer userId = 1;
		if (additionalParams != null) {
			// look for a passed user id
			// userId = (Integer)additionalParams.get(Parameters.PARAM_USERID);
			Object uid = additionalParams.get(Parameters.PARAM_USERID);
			if (uid instanceof Integer) {
				userId = (Integer) uid;
			} else if (uid instanceof Long) {
				userId = ((Long) uid).intValue();
			}
			// if (userId == null) {
			// look for a passed request to get the user id from
			// userId = new
			// Integer(UserUtils.getUserId((HttpServletRequest)additionalParams.get(Parameters.PARAM_HTTP_REQUEST)));
			// }
		}
		_update(vo, userId);
	}

	/**
	 * Creates or updates the specified virtual object. Uses the specified user
	 * ID as the creator and modifier if a new object is created; uses ID only
	 * as modifier if an object already exists.
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
	 * @param domainClass
	 * @param filterClause
	 * @param fromIndex
	 * @param toIndex
	 * @param orderBy
	 * @return
	 */
	private List getListCommon(Class domainClass, String filterClause, Integer fromIndex, Integer toIndex,
			String orderBy) {
		List list = null;
		String className = domainClass.getName();
		Session session = sessionFactory.getCurrentSession();

		if (logger.isInfoEnabled()) {
			logger.info(EELFLoggerDelegate.debugLogger, "Getting " + className.toLowerCase() + " records"
					+ ((fromIndex != null) ? " from rows " + fromIndex.toString() + " to " + toIndex.toString() : "")
					+ "...");
			if (filterClause != null && filterClause.length() > 0)
			    logger.info(EELFLoggerDelegate.debugLogger, "Filtering " + className + " by: " + filterClause);
		}

		list = session.createQuery("from " + className + Utilities.nvl(filterClause, "")
				+ ((orderBy != null) ? " order by " + orderBy : "")).list();
		list = (fromIndex != null) ? list.subList(fromIndex.intValue() - 1, toIndex.intValue()) : list;

		if (orderBy == null && list != null)
			Collections.sort(list);

		return list;
	}

	@Override
	public List getList(Class domainClass, HashMap additionalParams) {
		return getListCommon(domainClass, null, null, null, null);
	}

	@Override
	public List getList(Class domainClass, String filter, String orderBy, HashMap additionalParams) {
		return getListCommon(domainClass, filter, null, null, orderBy);
	}

	@Override
	public List getList(Class domainClass, String filter, int fromIndex, int toIndex, String orderBy,
			HashMap additionalParams) {
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
		/*
		 * if(fetchModeMap!=null){ Iterator<String> itr =
		 * fetchModeMap.keySet().iterator(); String key=null;
		 * while(itr.hasNext()){ key = itr.next();
		 * criteria.setFetchMode(key,fetchModeMap.get(key)); } }
		 */
		return criteria.list();
	}

	@Override
	public List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy,
			HashMap additionalParams) {
		if (logger.isInfoEnabled())
		    logger.info(EELFLoggerDelegate.debugLogger, "Retrieving " + dbTable + " lookup list...");
		String dbOrderByCol = dbOrderBy;

		Session session = sessionFactory.getCurrentSession();

		// default the orderBy if null;
		if (Utilities.nvl(dbOrderBy).length() == 0) {
			dbOrderByCol = dbLabelCol;
			dbOrderBy = dbLabelCol;
		} else {
			if (dbOrderBy.lastIndexOf(" ") > -1) {
				dbOrderByCol = dbOrderBy.substring(0, dbOrderBy.lastIndexOf(" "));
			}
		}

		StringBuffer sql = new StringBuffer();

		sql.append("select distinct ").append(dbLabelCol).append(" as lab, ").append(dbValueCol).append(" as val, ")
				.append(dbOrderByCol).append(" as sortOrder ").append("from ").append(dbTable).append(" ")
				.append((Utilities.nvl(dbFilter).length() == 0) ? "" : (" where " + dbFilter)).append(" order by ")
				.append(dbOrderBy);

		List list = null;
		try {
			list = session.createSQLQuery(sql.toString()).addEntity(Lookup.class).list();
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, "Failed to create SQL lookup query for [" + sql + "]" + e.getMessage());
		}
		return list;
	}

	/*
	 * methods accepting a Map of additional params to passed to the DAO (for
	 * extensibility, just in case)
	 */

	@Override
	public List executeSQLQuery(String sql, Class domainClass, HashMap additionalParams) {
		return executeSQLQuery(sql, domainClass, null, null, additionalParams);
	}

	@Override
	public List executeSQLQuery(String sql, Class domainClass, Integer fromIndex, Integer toIndex,
			HashMap additionalParams) {
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
	public List executeQuery(String sql, HashMap additionalParams) {
		return executeQuery(sql, null, null, additionalParams);
	}

	@Override
	public List executeQuery(String sql, Integer fromIndex, Integer toIndex, HashMap additionalParams) {
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
	public List executeNamedQuery(String queryName, Integer fromIndex, Integer toIndex, HashMap additionalParams) {
		return executeNamedQuery(queryName, null, fromIndex, toIndex, additionalParams);
	}

	@Override
	public List executeNamedQuery(String queryName, Map params, HashMap additionalParams) {
		return executeNamedQuery(queryName, params, null, null, additionalParams);
	}

	@Override
	public List executeNamedQuery(String queryName, Map params, Integer fromIndex, Integer toIndex,
			HashMap additionalParams) {
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
				}
			}
		}
	}

	// With Where Clause & RAPTOR's ZK

	@Override
	public List executeNamedQueryWithOrderBy(Class entity, String queryName, Map params, String _orderBy, boolean asc,
			Integer fromIndex, Integer toIndex, HashMap additionalParams) {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
		return null;
	}

	@Override
	public List executeNamedCountQuery(Class entity, String queryName, String whereClause, Map params) {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
		return null;
	}

	@Override
	public List executeNamedQuery(Class entity, String queryName, String whereClause, Map params, Integer fromIndex,
			Integer toIndex, HashMap additionalParams) {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
		return null;
	}

	@Override
	public List executeNamedQueryWithOrderBy(Class entity, String queryName, String whereClause, Map params,
			String _orderBy, boolean asc, Integer fromIndex, Integer toIndex, HashMap additionalParams) {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
		return null;
	}

	@Override
	public List<?> getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList,
			List<Order> orderByList, HashMap<String, FetchMode> fetchModeMap) {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
		return null;
	}

	@Override
	public int executeUpdateQuery(String sql, HashMap additionalParams) throws RuntimeException {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
		return 0;
	}

	@Override
	public int executeNamedUpdateQuery(String queryName, Map params, HashMap additionalParams) throws RuntimeException {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
		return 0;
	}

	@Override
	public void synchronize(HashMap additionalParams) {
		// TODO Auto-generated method stub
		logger.info(EELFLoggerDelegate.debugLogger, "Not implemented");
	}

}
