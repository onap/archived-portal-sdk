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
package org.onap.portalsdk.rnotebookintegration.domain;

import org.junit.Assert;
import org.junit.Test;

public class RNoteBookCredentialsTest {

	public RNoteBookCredentials mockRNoteBookCredentials(){
		RNoteBookCredentials rNoteBookCredentials = new RNoteBookCredentials();
			    
		rNoteBookCredentials.setUserInfo(null);
		rNoteBookCredentials.setToken("test");     
		rNoteBookCredentials.setCreated(null);
		rNoteBookCredentials.setCreatedDate(null);
		rNoteBookCredentials.setNotebookID("test");
		rNoteBookCredentials.setParameters(null);
		rNoteBookCredentials.setTokenReadDate(null);
		rNoteBookCredentials.setUserString("test");
		rNoteBookCredentials.setParametersString("test");	
	    
		return rNoteBookCredentials;			
	}
	
	@Test
	public void rNoteBookCredentialsTest(){
		RNoteBookCredentials rNoteBookCredentials1 = mockRNoteBookCredentials();
		
		RNoteBookCredentials rNoteBookCredentials = new RNoteBookCredentials();
		rNoteBookCredentials.setUserInfo(null);
		rNoteBookCredentials.setToken("test");     
		rNoteBookCredentials.setCreated(null);
		rNoteBookCredentials.setNotebookID("test");
		rNoteBookCredentials.setParameters(null);
		rNoteBookCredentials.setTokenReadDate(null);
		rNoteBookCredentials.setUserString("test");
		rNoteBookCredentials.setParametersString("test");
		
		Assert.assertEquals(rNoteBookCredentials.getUserInfo(), rNoteBookCredentials1.getUserInfo());
		Assert.assertEquals(rNoteBookCredentials.getToken(), rNoteBookCredentials1.getToken());
		Assert.assertEquals(rNoteBookCredentials.getCreated(), rNoteBookCredentials1.getCreated());
		Assert.assertEquals(rNoteBookCredentials.getCreatedDate(), rNoteBookCredentials1.getCreatedDate());
		Assert.assertEquals(rNoteBookCredentials.getNotebookID(), rNoteBookCredentials1.getNotebookID());
		Assert.assertEquals(rNoteBookCredentials.getParameters(), rNoteBookCredentials1.getParameters());
		Assert.assertEquals(rNoteBookCredentials.getTokenReadDate(), rNoteBookCredentials1.getTokenReadDate());
		Assert.assertEquals(rNoteBookCredentials.getUserString(), rNoteBookCredentials1.getUserString());
		Assert.assertEquals(rNoteBookCredentials.getParametersString(), rNoteBookCredentials1.getParametersString());
	}
}
