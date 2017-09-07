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
package org.onap.portalsdk.core.restful.domain;

public class PortalTimeoutVO implements Comparable<PortalTimeoutVO> {

	private String jSessionId;
	private Long sessionTimOutMilliSec;

	public PortalTimeoutVO() {
		super();
	}

	public PortalTimeoutVO(final String jSessionId, final Long sessionTimOutMilliSec) {
		this.jSessionId = jSessionId;
		this.sessionTimOutMilliSec = sessionTimOutMilliSec;
	}

	public String getjSessionId() {
		return jSessionId;
	}

	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
	}

	public Long getSessionTimOutMilliSec() {
		return sessionTimOutMilliSec;
	}

	public void setSessionTimOutMilliSec(Long sessionTimOutMilliSec) {
		this.sessionTimOutMilliSec = sessionTimOutMilliSec;
	}

	@Override
	public int compareTo(PortalTimeoutVO o) {
		return sessionTimOutMilliSec.compareTo(o.sessionTimOutMilliSec);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof PortalTimeoutVO))
			return false;
		PortalTimeoutVO castOther = (PortalTimeoutVO) other;
		return this.getSessionTimOutMilliSec() == castOther.getSessionTimOutMilliSec();
	}

}
