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
package org.openecomp.portalsdk.core.service;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.openecomp.portalsdk.core.domain.support.DomainVo;

@SuppressWarnings("rawtypes")
public interface DataAccessService {

    // generic view, save, delete methods
    DomainVo getDomainObject(Class domainClass, Serializable id, HashMap additionalParams);
    void     deleteDomainObject(DomainVo domainObject, HashMap additionalParams);
    void     deleteDomainObjects(Class domainClass, String whereClause, HashMap additionalParams);
    void     saveDomainObject(DomainVo domainObject, HashMap additionalParams);

    // generic get list method(s)
    List getList(Class domainClass, HashMap additionalParams);
    List getList(Class domainClass, String filter, String orderBy, HashMap additionalParams);
    List getList(Class domainClass, String filter, int fromIndex, int toIndex, String orderBy, HashMap additionalParams);
    List<?> getList(Class<?> domainClass, ProjectionList projectionsList , List<Criterion> restrictionsList , List<Order> orderByList);
    public List<?> getList(Class<?> domainClass, ProjectionList projectionsList, List<Criterion> restrictionsList, List<Order> orderByList,HashMap<String,FetchMode> fetchModeMap); 
    
    List getLookupList(String dbTable, String dbValueCol, String dbLabelCol, String dbFilter, String dbOrderBy, HashMap additionalParams);
	
    // generic native-SQL execution methods
	List executeSQLQuery(String sql, Class domainClass, HashMap additionalParams);
    List executeSQLQuery(String sql, Class domainClass, Integer fromIndex, Integer toIndex,HashMap additionalParams);

    // generic HQL execution methods
    List executeQuery(String hql, HashMap additionalParams);
    List executeQuery(String hql, Integer fromIndex, Integer toIndex, HashMap additionalParams);

    // generic named query execution methods
    List executeNamedQuery(String queryName, Integer fromIndex, Integer toIndex, HashMap additionalParams);
    List executeNamedQuery(String queryName, Map params, HashMap additionalParams);
    List executeNamedQuery(String queryName, Map params, Integer fromIndex, Integer toIndex, HashMap additionalParams);

    //with Where Clause for RAPTOR ZK
    List executeNamedQueryWithOrderBy(Class entity, String queryName, Map params, String _orderBy, boolean asc, Integer fromIndex, Integer toIndex, HashMap additionalParams);
    List executeNamedCountQuery(Class entity, String queryName, String whereClause, Map params);
    List executeNamedQuery(Class entity, String queryName, String whereClause, Map params, Integer fromIndex, Integer toIndex, HashMap additionalParams);
    List executeNamedQueryWithOrderBy(Class entity, String queryName, String whereClause, Map params, String _orderBy, boolean asc, Integer fromIndex, Integer toIndex, HashMap additionalParams);

    // generic update query execution method
    int executeUpdateQuery(String sql, HashMap additionalParams) throws RuntimeException;

    // generic named update query execution method
    int executeNamedUpdateQuery(String queryName, Map params, HashMap additionalParams) throws RuntimeException;
    
    // synchronizes the local updates with the database (and vice versa)
    void synchronize(HashMap additionalParams);

}
