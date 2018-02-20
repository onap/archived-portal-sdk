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
package org.onap.portalsdk.core.restful.client;

import org.junit.Test;

import junit.framework.Assert;

public class HttpStatusAndResponseTest {

	public HttpStatusAndResponse mockHttpStatusAndResponse(){
		HttpStatusAndResponse httpStatusAndResponse = new HttpStatusAndResponse(0, null);
				
		return httpStatusAndResponse;
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void httpStatusAndResponseTest(){
		HttpStatusAndResponse httpStatusAndResponse1 = mockHttpStatusAndResponse();
		
		HttpStatusAndResponse httpStatusAndResponse = new HttpStatusAndResponse(0, null);
		
		Assert.assertEquals(httpStatusAndResponse.getResponse(), httpStatusAndResponse1.getResponse());
		Assert.assertEquals(httpStatusAndResponse.getStatusCode(), httpStatusAndResponse1.getStatusCode());
	}
}
