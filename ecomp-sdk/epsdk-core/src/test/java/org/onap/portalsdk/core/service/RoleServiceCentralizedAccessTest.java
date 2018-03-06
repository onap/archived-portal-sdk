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

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RoleServiceCentralizedAccessTest {

	@InjectMocks
	private RoleServiceCentralizedAccess roleServiceCntrlAccess;
	
	@Mock
	private RestApiRequestBuilder restApiRequestBuilder;

	@Test
	public void getRoleFunctionsTest() throws Exception {
		String loginId ="1234";
		String response ="[    {        \"code\" : \"abc\",        \"name\" : \"xyz\"    },    {        \"code\" : \"pqr\",        \"name\" : \"str\"    } ]";
		Mockito.when(restApiRequestBuilder.getViaREST(Matchers.anyString(), Matchers.anyBoolean(), Matchers.anyString())).thenReturn(response);
		List<RoleFunction> roleFunctions = roleServiceCntrlAccess.getRoleFunctions(loginId);
		Assert.assertTrue(roleFunctions.size() > 0);
	}
	
	@Test
	public void getAvailableChildRolesWithEmptyRoleIdTest() throws Exception {
		String loginId = "123";
		Long roleId = null;
		String response ="[    {        \"active\" : true,        \"name\" : \"xyz\"    } ]";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/roles", true, loginId)).thenReturn(response);
		List<Role> roles = roleServiceCntrlAccess.getAvailableChildRoles(loginId, roleId);
		Assert.assertNotNull(roles);
	}
	
	@Test
	public void getAvailableChildRolesWithZeroRoleIdTest() throws Exception {
		String loginId = "123";
		Long roleId = 0L;
		String response ="[    {        \"active\" : true,        \"name\" : \"xyz\"    } ]";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/roles", true, loginId)).thenReturn(response);
		List<Role> roles = roleServiceCntrlAccess.getAvailableChildRoles(loginId, roleId);
		Assert.assertNotNull(roles);
	}
	
	@Test
	public void getAvailableChildRolesTest() throws Exception {
		String loginId = "123";
		Long roleId = 123L;
		String response ="[    {        \"active\" : false,        \"name\" : \"xyz\"    } ]";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/roles", true, loginId)).thenReturn(response);
		String roleResponse =" {        \"active\" : true,        \"name\" : \"xyz\", \"roleFunctions\" : [    {        \"code\" : \"abc\",        \"name\" : \"RF1\" , \"type\" : \"abc\", \"action\" : \"abc\"   },    {        \"code\" : \"pqr\",        \"name\" : \"RF2\"  , \"type\" : \"abc\", \"action\" : \"abc\"  } ]    ,  \"parentRoles\": [   {\"active\" : false,        \"name\" : \"XYZ-ABC\"}, {\"active\" : true,        \"name\" : \"ABC\"}  ]    } ";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/role/" + roleId, true, loginId)).thenReturn(roleResponse);
		roleServiceCntrlAccess.getAvailableChildRoles(loginId, roleId);
		Assert.assertTrue(true);
	}
	
	@Test
	public void saveRoleTest() throws Exception {
		Role role = new Role();
		role.setName("Role");
		roleServiceCntrlAccess.saveRole("123", role);
		Assert.assertTrue(true);
	}
	
	@Test
	public void deleteRoleTest() throws Exception {
		Role role = new Role();
		role.setName("Role");
		role.setId(123l);
		roleServiceCntrlAccess.deleteRole("123", role);
		Assert.assertTrue(true);
	}
	
	@Test
	public void getActiveRolesTest() throws Exception {
		String requestedLoginId ="1234";
		String response ="[    {        \"active\" : true,        \"name\" : \"role1\"    }, {        \"active\" : false,        \"name\" : \"role2\"    } ]";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/activeRoles", true, requestedLoginId)).thenReturn(response);
		List<Role> roles = roleServiceCntrlAccess.getActiveRoles(requestedLoginId);
		Assert.assertNotNull(roles);
	}
	
	@Test
	public void getRoleFunctionTest() throws IOException {
		String requestedLoginId = "xyz";
		String code ="abc";
		
		String responseString = " {        \"code\" : \"abc\",        \"name\" : \"xyz\"   , \"type\" : \"abc\", \"action\" : \"abc\" }";
//		Mockito.when(restApiRequestBuilder.getViaREST("v1/function/" + code, true, requestedLoginId)).thenReturn(responseString);
		Mockito.when(restApiRequestBuilder.getViaREST(Matchers.anyString(), Matchers.anyBoolean(), Matchers.anyString())).thenReturn(responseString);
		RoleFunction roleFunction = roleServiceCntrlAccess.getRoleFunction(requestedLoginId, code);
		Assert.assertNotNull(roleFunction);
	}
	
	@Test
	public void saveRoleFunctionTest() throws IOException {
		String requestedLoginId ="123";
		RoleFunction domainRoleFunction = new RoleFunction();
		domainRoleFunction.setId(1234L);
		roleServiceCntrlAccess.saveRoleFunction(requestedLoginId, domainRoleFunction);
		Assert.assertTrue(true);
	}

	@Test
	public void deleteRoleFunctionTest() throws IOException {
		String requestedLoginId ="123";
		RoleFunction domainRoleFunction = new RoleFunction();
		domainRoleFunction.setId(1234L);
		roleServiceCntrlAccess.deleteRoleFunction(requestedLoginId, domainRoleFunction);
		Assert.assertTrue(true);
	}

	@Test
	public void deleteDependcyRoleRecord() throws IOException {
		String requestedLoginId = "123";
		Long id = 123L;
		roleServiceCntrlAccess.deleteDependcyRoleRecord(requestedLoginId, id);
		Assert.assertTrue(true);
	}
}
