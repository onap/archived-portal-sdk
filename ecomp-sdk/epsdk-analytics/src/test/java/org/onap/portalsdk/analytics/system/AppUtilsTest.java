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
package org.onap.portalsdk.analytics.system;

import static org.junit.Assert.*;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.xmlobj.MockitoTestSuite;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.owasp.esapi.ESAPI;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.codecs.Codec;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Globals.class, ESAPI.class, Codec.class, SecurityCodecUtil.class })
public class AppUtilsTest {

	@InjectMocks
	AppUtils appUtils;

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	@SuppressWarnings("static-access")
	@Test
	public void generateFileNameTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserID(mockedRequest)).thenReturn("test12");
		assertEquals(appUtils.generateFileName(mockedRequest, "test"), "cr_test12test");
	}

	@SuppressWarnings("static-access")
	@Test
	public void generateUniqueFileNameTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserID(mockedRequest)).thenReturn("test12");
		assertEquals(appUtils.generateUniqueFileName(mockedRequest, "test", "test").getClass(), String.class);
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRequestValueTest() {
		Mockito.when(mockedRequest.getAttribute(Matchers.anyString())).thenReturn(null);
		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		assertEquals(appUtils.getRequestValue(mockedRequest, "test"), "select *");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRequestNvlValueTest() {
		Mockito.when(mockedRequest.getAttribute(Matchers.anyString())).thenReturn(null);
		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		assertEquals(appUtils.getRequestNvlValue(mockedRequest, "test"), "select *");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRequestFlagTest() {
		Mockito.when(mockedRequest.getAttribute(Matchers.anyString())).thenReturn(null);
		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("yes");
		assertTrue(appUtils.getRequestFlag(mockedRequest, "test"));
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRequestFlag1Test() {
		Mockito.when(mockedRequest.getAttribute(Matchers.anyString())).thenReturn(null);
		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("true");
		assertTrue(appUtils.getRequestFlag(mockedRequest, "test"));
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRequestFlag2Test() {
		Mockito.when(mockedRequest.getAttribute(Matchers.anyString())).thenReturn(null);
		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		assertFalse(appUtils.getRequestFlag(mockedRequest, "test"));
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserIDTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserID(mockedRequest)).thenReturn("test12");
		assertEquals(appUtils.getUserID(mockedRequest), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserNameest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserName(mockedRequest)).thenReturn("test12");
		assertEquals(appUtils.getUserName(mockedRequest), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserName1Test() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserName("test12")).thenReturn("test12");
		assertEquals(appUtils.getUserName("test12"), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserEmailTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserEmail("test12")).thenReturn("test12@test.com");
		assertEquals(appUtils.getUserEmail("test12"), "test12@test.com");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserEmail1est() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserEmail(mockedRequest)).thenReturn("test12@test.com");
		assertEquals(appUtils.getUserEmail(mockedRequest), "test12@test.com");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserLoginIdTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserLoginId(mockedRequest)).thenReturn("test12");
		assertEquals(appUtils.getUserLoginId(mockedRequest), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserLoginId1Test() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserLoginId("test12")).thenReturn("test12");
		assertEquals(appUtils.getUserLoginId("test12"), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserBackdoorLoginIdTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserBackdoorLoginId(mockedRequest)).thenReturn("test12");
		assertEquals(appUtils.getUserBackdoorLoginId(mockedRequest), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getAllUsersTest() {
		Vector vc = new Vector<>();
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getAllUsers("test", "test", false)).thenReturn(vc);
		appUtils.getAllUsers("test", "test", false);
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRoleNameTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getRoleName("test12")).thenReturn("test12");
		assertEquals(appUtils.getRoleName("test12"), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getAllRolesTest() {
		Vector vc = new Vector<>();
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getAllRoles("test", "test", false)).thenReturn(vc);
		appUtils.getAllRoles("test", "test", false);
	}

	@SuppressWarnings("static-access")
	@Test
	public void isUserInRoleTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.isUserInRole(mockedRequest, "1")).thenReturn(false);
		assertFalse(appUtils.isUserInRole(mockedRequest, "1"));
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUserRolesTest() throws Exception {
		Vector vc = new Vector<>();
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUserRoles("test")).thenReturn(vc);
		appUtils.getUserRoles("test");
	}

	@SuppressWarnings("static-access")
	@Test
	public void resetUserCacheTest() throws Exception {
		Vector vc = new Vector<>();
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.doNothing().when(iAppUtils).resetUserCache();
		appUtils.resetUserCache();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getSuperRoleIDTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getSuperRoleID()).thenReturn("test");
		assertEquals(appUtils.getSuperRoleID(), "test");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getAdminRoleIDsTest() throws Exception {
		Vector vc = new Vector<>();
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getAdminRoleIDs()).thenReturn(vc);
		appUtils.getAdminRoleIDs();
	}

	@SuppressWarnings("static-access")
	@Test
	public void isSuperUserTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isAdminRoleEquivalenttoSuperRole()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getSuperRoleID()).thenReturn("test");
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.isUserInRole(mockedRequest, "1")).thenReturn(false);
		assertFalse(appUtils.isSuperUser(mockedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void isAdminUserTst() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isAdminRoleEquivalenttoSuperRole()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getSuperRoleID()).thenReturn("test");
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.isUserInRole(mockedRequest, "1")).thenReturn(true);

		Vector vc = new Vector<>();
		Mockito.when(iAppUtils.getAdminRoleIDs()).thenReturn(vc);

		assertFalse(appUtils.isAdminUser(mockedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void isAdminUser1Tst() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isAdminRoleEquivalenttoSuperRole()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getSuperRoleID()).thenReturn("test");
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.isUserInRole(mockedRequest, "1")).thenReturn(true);

		Vector vc = new Vector<>();
		vc.add("test");
		Mockito.when(iAppUtils.getAdminRoleIDs()).thenReturn(vc);
		Mockito.when(iAppUtils.isUserInRole(Matchers.any(HttpServletRequest.class), Matchers.anyString()))
				.thenReturn(true);

		assertTrue(appUtils.isAdminUser(mockedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void isAdminUser2Tst() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isAdminRoleEquivalenttoSuperRole()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getSuperRoleID()).thenReturn("test");
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.isUserInRole(mockedRequest, "1")).thenReturn(true);

		Vector vc = new Vector<>();
		vc.add("test");
		Mockito.when(iAppUtils.getAdminRoleIDs()).thenReturn(vc);
		Mockito.when(iAppUtils.isUserInRole(mockedRequest, "test")).thenReturn(false);

		assertFalse(appUtils.isAdminUser(mockedRequest));
	}

	@SuppressWarnings("static-access")
	@Test
	public void getTempFolderPathTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("test12");
		appUtils.getTempFolderPath();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getTempFolderPath1Test() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("test12\\");
		appUtils.getTempFolderPath();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getTempFolderPath2Test() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("test12/");
		appUtils.getTempFolderPath();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUploadFolderPathTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUploadFolderPath()).thenReturn("test12");
		appUtils.getUploadFolderPath();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUploadFolderPath1Test() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUploadFolderPath()).thenReturn("test12\\");
		appUtils.getUploadFolderPath();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getTempFolderURLTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getTempFolderURL()).thenReturn("test12");
		assertEquals(appUtils.getTempFolderURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getUploadFolderURLTest() {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getUploadFolderURL()).thenReturn("test12");
		assertEquals(appUtils.getUploadFolderURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getSMTPServerTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getSMTPServer()).thenReturn("test12");
		assertEquals(appUtils.getSMTPServer(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getDefaultEmailSenderTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getDefaultEmailSender()).thenReturn("test12");
		assertEquals(appUtils.getDefaultEmailSender(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getErrorPageTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getErrorPage()).thenReturn("test12");

		Mockito.when(iAppUtils.getJspContextPath()).thenReturn("test12");
		assertEquals(appUtils.getErrorPage(), "test12test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getJspContextPathTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getJspContextPath()).thenReturn("test12");
		assertEquals(appUtils.getJspContextPath(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getImgFolderURLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getImgFolderURL()).thenReturn("test12");
		assertEquals(appUtils.getImgFolderURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getBaseFolderURLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseFolderURL()).thenReturn("test12");
		assertEquals(appUtils.getBaseFolderURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getChartScriptsPathTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseFolderURL()).thenReturn("test12");
		Mockito.when(iAppUtils.getFolderPathAdj()).thenReturn("test12");
		assertEquals(appUtils.getChartScriptsPath(), "test12test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getChartScriptsPath1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseFolderURL()).thenReturn("test12");
		assertEquals(appUtils.getChartScriptsPath("test"), "testtest12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getFolderPathAdjTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getFolderPathAdj()).thenReturn("test12");
		assertEquals(appUtils.getFolderPathAdj(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getDirectAccessURLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getDirectAccessURL()).thenReturn("test12");
		assertEquals(appUtils.getDirectAccessURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getBaseURLLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionURL()).thenReturn("test12");
		assertEquals(appUtils.getBaseURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getBaseURLL1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionURL()).thenReturn("test12?");
		assertEquals(appUtils.getBaseURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getBaseActionURLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionURL()).thenReturn("test12");
		assertEquals(appUtils.getBaseActionURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getDrillActionURLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getDrillActionURL()).thenReturn("test12");
		assertEquals(appUtils.getDrillActionURL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRaptorActionURLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionURL()).thenReturn("test12");
		assertEquals(appUtils.getRaptorActionURL(), "test12raptor&r_action=");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getRaptorActionURLNGTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionURLNG()).thenReturn("test12");
		assertEquals(appUtils.getRaptorActionURLNG(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getReportExecuteActionURLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionURLNG()).thenReturn("test12");
		assertEquals(appUtils.getReportExecuteActionURL(), "nullraptor&r_action=report.run.container&c_master=");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getReportExecuteActionURLNGTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionURLNG()).thenReturn("test12");
		assertEquals(appUtils.getReportExecuteActionURLNG(), "test12report_run/");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getBaseActionParamTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getBaseActionParam()).thenReturn("test12");
		assertEquals(appUtils.getBaseActionParam(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getQuickLinksMenuIDsTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Vector vc = new Vector<>();
		Mockito.when(iAppUtils.getQuickLinksMenuIDs()).thenReturn(vc);
		appUtils.getQuickLinksMenuIDs();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getMenuLabelTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getMenuLabel("test")).thenReturn("test12");
		assertEquals(appUtils.getMenuLabel("test"), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getReportDbColsMaskSQLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getReportDbColsMaskSQL()).thenReturn("test12");
		assertEquals(appUtils.getReportDbColsMaskSQL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getReportDbLookupsSQLTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getReportDbLookupsSQL()).thenReturn("test12");
		assertEquals(appUtils.getReportDbLookupsSQL(), "test12");
	}

	@SuppressWarnings("static-access")
	@Test
	public void processErrorNotificationTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.doNothing().when(iAppUtils).processErrorNotification(Matchers.any(HttpServletRequest.class),
				Matchers.any(RaptorException.class));
		appUtils.processErrorNotification(mockedRequest, new RaptorException("test"));
	}

	@SuppressWarnings("static-access")
	@Test
	public void getExcelTemplatePathTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getExcelTemplatePath()).thenReturn("test12");
		appUtils.getExcelTemplatePath();
	}

	@SuppressWarnings("static-access")
	@Test
	public void getExcelTemplatePath1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		IAppUtils iAppUtils = PowerMockito.mock(IAppUtils.class);
		Mockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		Mockito.when(iAppUtils.getExcelTemplatePath()).thenReturn("test12\\");
		appUtils.getExcelTemplatePath();
	}

	@SuppressWarnings("static-access")
	@Test
	public void nvlTest() {
		assertEquals(appUtils.nvl(null), "");
		assertEquals(appUtils.nvl("test"), "test");
		assertEquals(appUtils.isNotEmpty(""), false);
		assertEquals(appUtils.isNotEmpty("test"), true);
		assertEquals(appUtils.nvls("test", "test1"), "test");
		assertEquals(appUtils.nvls(null, "test1"), "test1");
	}
}
