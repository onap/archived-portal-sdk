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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.FusionObject.Parameters;
import org.onap.portalsdk.core.domain.support.DomainVo;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class })
public class DataAccessServiceImplTest {

	@Mock
	private SessionFactory sessionFactory;

	@InjectMocks
	private DataAccessServiceImpl dataAccessServiceImpl;

	@Test
	public void getDomainObjectTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Long id = 1L;
		DomainVo domainVo = new DomainVo();
		domainVo.setId(id);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.get(DomainVo.class, id)).thenReturn(domainVo);
		DomainVo returnDomainVo = dataAccessServiceImpl.getDomainObject(DomainVo.class, id, null);
		Assert.assertEquals(id, returnDomainVo.getId());
	}

	@Test
	public void deleteDomainObjectTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		dataAccessServiceImpl.deleteDomainObject(new DomainVo(), new HashMap<>());
		Assert.assertTrue(true);
	}

	@Test
	public void deleteDomainObjectsTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createQuery(Mockito.anyString())).thenReturn(mockQuery);
		String whereClause = "id = 1";
		dataAccessServiceImpl.deleteDomainObjects(DomainVo.class, whereClause, new HashMap<>());
		Assert.assertTrue(true);
	}

	@Test
	public void saveDomainObjectWithNewTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Map params = new HashMap();
		params.put(Parameters.PARAM_USERID, 1);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APPLICATION_USER_ID)).thenReturn("123");
		DomainVo domainVo = new DomainVo();
		domainVo.setId(0L);
		dataAccessServiceImpl.saveDomainObject(domainVo, params);
		Assert.assertTrue(true);
	}

	@Test
	public void saveDomainObjectWithUpdateTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Map params = new HashMap();
		params.put(Parameters.PARAM_USERID, 1L);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APPLICATION_USER_ID)).thenReturn("123");
		DomainVo domainVo = new DomainVo();
		domainVo.setId(10L);
		dataAccessServiceImpl.saveDomainObject(domainVo, params);
		Assert.assertTrue(true);
	}

	@Test
	public void getListWithOnlyClassTest() {

		DomainVo domainVo1 = new DomainVo();
		domainVo1.setId(1L);
		DomainVo domainVo2 = new DomainVo();
		domainVo2.setId(1L);

		List<DomainVo> domainVoList = new ArrayList<>();
		domainVoList.add(domainVo1);
		domainVoList.add(domainVo2);

		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createQuery(Mockito.anyString())).thenReturn(mockQuery);
		Mockito.when(mockQuery.list()).thenReturn(domainVoList);
		List list = dataAccessServiceImpl.getList(DomainVo.class, new HashMap());
		Assert.assertEquals(2, list.size());

	}

	@Test
	public void getListWithOrderAndFilterTest() {

		DomainVo domainVo1 = new DomainVo();
		domainVo1.setId(1L);
		DomainVo domainVo2 = new DomainVo();
		domainVo2.setId(1L);

		List<DomainVo> domainVoList = new ArrayList<>();
		domainVoList.add(domainVo1);
		domainVoList.add(domainVo2);

		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createQuery(Mockito.anyString())).thenReturn(mockQuery);
		Mockito.when(mockQuery.list()).thenReturn(domainVoList);
		List list = dataAccessServiceImpl.getList(DomainVo.class, "", "", new HashMap());
		Assert.assertEquals(2, list.size());

	}

	@Test
	public void getListTest() {
		String filterClause = " where id = '1' ";

		DomainVo domainVo1 = new DomainVo();
		domainVo1.setId(1L);
		DomainVo domainVo2 = new DomainVo();
		domainVo2.setId(1L);

		List<DomainVo> domainVoList = new ArrayList<>();
		domainVoList.add(domainVo1);
		domainVoList.add(domainVo2);

		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createQuery(Mockito.anyString())).thenReturn(mockQuery);
		Mockito.when(mockQuery.list()).thenReturn(domainVoList);
		List list = dataAccessServiceImpl.getList(DomainVo.class, filterClause, "id", new HashMap());
		Assert.assertEquals(2, list.size());
	}

	@Test
	public void getListGenericTest() {

		List<Criterion> restrictionsList = new ArrayList<>();
		List<Order> orderList = new ArrayList<>();
		orderList.add(Order.asc("id"));
		Criterion criterion1 = Restrictions.like("urlsAccessibleKey.url", "URL%");
		restrictionsList.add(criterion1);
		Session mockedSession = Mockito.mock(Session.class);
		Criteria criteria = Mockito.mock(Criteria.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createCriteria(DomainVo.class)).thenReturn(criteria);
		List list = dataAccessServiceImpl.getList(DomainVo.class, null, restrictionsList, orderList);
		Assert.assertNotNull(list);
	}

	@Test
	public void getLookupListTest() {
		Session mockedSession = Mockito.mock(Session.class);
		SQLQuery mockSQLQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createSQLQuery(Mockito.anyString())).thenReturn(mockSQLQuery);
		Mockito.when(mockSQLQuery.list()).thenReturn(new ArrayList());
		List list = dataAccessServiceImpl.getLookupList("User ", "1", "id", "id", "", null);
		Assert.assertNull(list);
	}

	@Test
	public void executeSQLQueryWithoutRangeTest() {
		Session mockedSession = Mockito.mock(Session.class);
		SQLQuery mockSQLQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createSQLQuery(Mockito.anyString())).thenReturn(mockSQLQuery);
		Mockito.when(mockSQLQuery.addEntity("org.onap.portalsdk.core.domain.support.domainvo", DomainVo.class))
				.thenReturn(mockSQLQuery);
		List list = dataAccessServiceImpl.executeSQLQuery("select * ", DomainVo.class, null);
		Assert.assertNotNull(list);
	}

	@Test
	public void executeSQLQueryTest() {
		Session mockedSession = Mockito.mock(Session.class);
		SQLQuery mockSQLQuery = Mockito.mock(SQLQuery.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createSQLQuery(Mockito.anyString())).thenReturn(mockSQLQuery);
		Mockito.when(mockSQLQuery.addEntity("org.onap.portalsdk.core.domain.support.domainvo", DomainVo.class))
				.thenReturn(mockSQLQuery);
		List list = dataAccessServiceImpl.executeSQLQuery("select * ", DomainVo.class, 1, 3, null);
		Assert.assertNotNull(list);
	}

	@Test
	public void executeQueryWithoutRangeTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createQuery(Mockito.anyString())).thenReturn(mockQuery);
		List list = dataAccessServiceImpl.executeQuery("select * ", null);
		Assert.assertNotNull(list);
	}

	@Test
	public void executeQueryTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.createQuery(Mockito.anyString())).thenReturn(mockQuery);
		List list = dataAccessServiceImpl.executeQuery("select * ", 1, 3, null);
		Assert.assertNotNull(list);
	}

	@Test
	public void executeNamedQueryWithoutRangeTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.getNamedQuery(Mockito.anyString())).thenReturn(mockQuery);
		Map params = new HashMap();
		params.put("map", new HashMap());
		params.put("list", new ArrayList());
		String[] args = { "abc" };
		params.put("obj", args);

		List list = dataAccessServiceImpl.executeNamedQuery("select * ", params, null);
		Assert.assertNotNull(list);
	}

	@Test
	public void executeNamedQueryTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.getNamedQuery(Mockito.anyString())).thenReturn(mockQuery);
		Map params = new HashMap();
		params.put("map", new HashMap());
		params.put("list", new ArrayList());
		String[] args = { "abc" };
		params.put("obj", args);

		List list = dataAccessServiceImpl.executeNamedQuery("select * ", params, 1, 3, null);
		Assert.assertNotNull(list);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void executeNamedQueryWithOrderByTest() {
		dataAccessServiceImpl.executeNamedQueryWithOrderBy(DomainVo.class, "", null, "", false, 0, 3, null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void executeNamedCountQueryTest() {
		dataAccessServiceImpl.executeNamedCountQuery(DomainVo.class, "select * ", " ", null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void executeNamedQueryExceptionTest() {
		dataAccessServiceImpl.executeNamedQuery(DomainVo.class, "select * ", "", null, 1, 3, null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void executeNamedQueryWithOrderByExceptionTest() {
		dataAccessServiceImpl.executeNamedQueryWithOrderBy(DomainVo.class, "", "", null, "", false, 0, 3, null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getListExceptionTest() {
		dataAccessServiceImpl.getList(DomainVo.class, null, null, null, null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void executeUpdateQueryTest() {
		dataAccessServiceImpl.executeUpdateQuery("", null);
	}

	@Test
	public void executeNamedUpdateQueryTest() {
		Session mockedSession = Mockito.mock(Session.class);
		Query mockQuery = Mockito.mock(Query.class);
		Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
		Mockito.when(mockedSession.getNamedQuery(Mockito.anyString())).thenReturn(mockQuery);
		Mockito.when(mockQuery.executeUpdate()).thenReturn(1);
		int result = dataAccessServiceImpl.executeNamedUpdateQuery("", null, null);
		Assert.assertEquals(1, result);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void synchronizeTest() {
		dataAccessServiceImpl.synchronize(null);
	}
}
