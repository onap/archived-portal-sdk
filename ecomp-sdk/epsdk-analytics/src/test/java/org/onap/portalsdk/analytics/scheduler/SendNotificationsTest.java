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
package org.onap.portalsdk.analytics.scheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.scheduler.SchedulerUtil.Executor;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class, SendNotifications.class, SchedulerUtil.class, SendEmail.class, Globals.class, AppUtils.class,
		DbUtils.class })
public class SendNotificationsTest {

	@Mock
	SchedulerUtil schedulerUtil;
	
	@Mock
	SendEmail sendEmail;

	@Mock
	Connection conn;

	@Mock
	DatabaseMetaData dMData;
	
	@Mock
	Executor executor;

	@Before
	public void init() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);
		DataSource ds = mock(DataSource.class);
		when(ds.getConnection()).thenReturn(conn);
		when(DbUtils.getConnection()).thenReturn(conn);
		MockitoAnnotations.initMocks(this);
		PowerMockito.whenNew(SchedulerUtil.class).withNoArguments().thenReturn(schedulerUtil);
   		PowerMockito.doNothing().when(sendEmail).setSchedulerUtil(schedulerUtil);
	}
	

	@Test
	public void send_notificationTest() throws RaptorException, Exception {
		SendNotifications sendNotifications = new SendNotifications();
		Statement stat = mock(Statement.class);
		ResultSet rset = mock(ResultSet.class);
		when(Globals.getSchedulerInterval()).thenReturn(2147483647);
		when(Globals.getAvailableSchedules()).thenReturn("test");
		when(Globals.getCurrentDateString()).thenReturn("test");
		when(schedulerUtil.getConnection()).thenReturn(conn);
		when(conn.createStatement()).thenReturn(stat);
		when(rset.getInt(Matchers.anyInt())).thenReturn(10);
		when(rset.next()).thenReturn(true).thenReturn(false);
		when(rset.getTimestamp("run_date")).thenReturn(new java.sql.Timestamp(0));
		when(stat.executeQuery(Matchers.anyString())).thenReturn(rset);
		sendNotifications.send_notification("test", "test", "test", "test", 30);
	}
}
