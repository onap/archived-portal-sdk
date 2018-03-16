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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class,  SchedulerUtil.class,  SendEmail.class, Globals.class, AppUtils.class, DbUtils.class })
public class SendEmailTest {

	@InjectMocks
	SendEmail sendEmail;

	@Mock 
	SchedulerUtil schedulerUtil;
	
	@Mock
	Connection conn;
	
	@Mock
	DatabaseMetaData  dMData;
	
	String sql = "select * 1";

	@Before
	public void init() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);
		DataSource ds = mock(DataSource.class);
		when(ds.getConnection()).thenReturn(conn);
		when(DbUtils.getConnection()).thenReturn(conn);
		MockitoAnnotations.initMocks(this);
	}

	
	@Test 
	public void sendEmailTest() throws Exception {
		FileOutputStream fileOut = mock(FileOutputStream.class);
		FileInputStream fileIn = mock(FileInputStream.class);
		PowerMockito.whenNew(FileInputStream.class).withArguments(Matchers.anyString()).thenReturn(fileIn);
		PowerMockito.whenNew(FileOutputStream.class).withArguments(Matchers.anyString()).thenReturn(fileOut);
		String obj = "test";
		when(Globals.getSchedulerUserEmails()).thenReturn("test");
		when(schedulerUtil.getSingleResult("select email from fn_user au, cr_report_schedule crs where CRS.SCHED_USER_ID = AU.USER_ID and CRS.SCHEDULE_ID = 1", "email")).thenReturn(obj);
		when(schedulerUtil.getSingleResult("SELECT rep_id FROM cr_report_schedule WHERE schedule_id =1","rep_id")).thenReturn(BigDecimal.valueOf(12));
		when(Globals.getSequenceNextVal()).thenReturn("[sequenceName]");
		when(schedulerUtil.getSingleResult("[sequenceName]".replace("[sequenceName]", "seq_cr_report_file_history"),"ID")).thenReturn(Long.valueOf(10));
		when(Globals.getCurrentDateString()).thenReturn("test");
		when(schedulerUtil.getSingleResult("select translate(title||to_char( "+ Globals.getCurrentDateString() + ",'MM-dd-yyyyHH24:mm:ss'), "
			+ "'0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'||'():;.-`~^\\|'||chr(34)||chr(39)||chr(9)||' ', "
			+ "'0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')|| "+ 10 +" as title FROM cr_report WHERE rep_id = "+null, "title")).thenReturn("Test_file");
		URL u = PowerMockito.mock(URL.class);
		String url = "http://test.com";
		when(Globals.getProjectFolder()).thenReturn("test_project");
		when(Globals.getOutputFolder()).thenReturn("test_output_folder");
		PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(u);
		HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
		PowerMockito.when(u.openConnection()).thenReturn(huc);
		PowerMockito.when(huc.getResponseCode()).thenReturn(200);
		byte []  inBytes = "test_Input_Stream".getBytes();
		InputStream in = new ByteArrayInputStream(inBytes);
		PowerMockito.when(huc.getInputStream()).thenReturn(in);
		sendEmail.sendEmail("test", "test", "test", "test", "http://test.com", 2, 1, 1, false, 10);
	}

}
