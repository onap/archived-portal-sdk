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
package org.onap.portalsdk.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.portalsdk.core.command.UserRowBean;
import org.onap.portalsdk.core.domain.User;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class UsageUtilsTest {

	@Test
	public void getActiveUsersTest() {
		HttpSession mock1 = Mockito.mock(HttpSession.class);
		HttpSession mock2 = Mockito.mock(HttpSession.class);
		Mockito.when(mock1.getAttribute("user")).thenReturn(null);
		
		User user = new User();
		user.setFirstName("FName");
		user.setLastName("Lname");
		user.setId(123L);
		user.setEmail("Xyz@xyz.com");
		user.setLastLoginDate(new Date());
		
		Mockito.when(mock2.getCreationTime()).thenReturn(new Date().getTime());
		Mockito.when(mock2.getLastAccessedTime()).thenReturn(new Date().getTime());
		Mockito.when(mock2.getMaxInactiveInterval()).thenReturn(1000);
		
		Mockito.when(mock2.getAttribute("user")).thenReturn(user);
		Map<String, HttpSession> activeUsers = new HashMap<>();
		activeUsers.put("1", mock1);
		activeUsers.put("2", mock2);
		List<UserRowBean> beans = UsageUtils.getActiveUsers(activeUsers);
		Assert.assertTrue(beans.size() > 0);
	}
	
	@Test
	public void getActiveUsersAfterDeleteTest(){
		
		HttpSession mock1 = Mockito.mock(HttpSession.class);
		HttpSession mock2 = Mockito.mock(HttpSession.class);
		Mockito.when(mock1.getAttribute("user")).thenReturn(null);
		
		User user = new User();
		user.setFirstName("FName");
		user.setLastName("Lname");
		user.setId(123L);
		user.setEmail("Xyz@xyz.com");
		user.setLastLoginDate(new Date());
		
		Mockito.when(mock2.getCreationTime()).thenReturn(new Date().getTime());
		Mockito.when(mock2.getLastAccessedTime()).thenReturn(new Date().getTime());
		Mockito.when(mock2.getMaxInactiveInterval()).thenReturn(1000);
		
		Mockito.when(mock2.getAttribute("user")).thenReturn(user);
		Map<String, HttpSession> activeUsers = new HashMap<>();
		activeUsers.put("1", mock1);
		activeUsers.put("2", mock2);
		
		UserRowBean bean = new UserRowBean();
		String beanSessionId = "1122";
		bean.setSessionId(beanSessionId);
		HttpSession mock3 = Mockito.mock(HttpSession.class);
		activeUsers.put(beanSessionId, mock3);
		List<UserRowBean> beans = UsageUtils.getActiveUsersAfterDelete(activeUsers, bean);
		Assert.assertTrue(beans.size() > 0);
	}
}
