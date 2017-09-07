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
package org.onap.portalsdk.core.domain;

import java.util.Date;

import org.onap.portalsdk.core.domain.support.DomainVo;

public class BroadcastMessage extends DomainVo {

	private static final long serialVersionUID = 1L;

	public static final String ID_MESSAGE_LOCATION_LOGIN = "10";
	public static final String ID_MESSAGE_LOCATION_WELCOME = "20";

	private String messageText;
	private Integer locationId;
	private Date startDate;
	private Date endDate;
	private Integer sortOrder;
	private Boolean active;
	private String siteCd;

	public Boolean getActive() {
		return active;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public String getMessageText() {
		return messageText;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getSiteCd() {
		return siteCd;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setSiteCd(String siteCd) {
		this.siteCd = siteCd;
	}

	@Override
	public int compareTo(Object obj) {
		Integer c1 = getLocationId();
		Integer c2 = ((BroadcastMessage) obj).getLocationId();

		if (c1.compareTo(c2) == 0) {
			c1 = getSortOrder();
			c2 = ((BroadcastMessage) obj).getSortOrder();

			if (c1.compareTo(c2) == 0) {
				Long c3 = getId();
				Long c4 = ((BroadcastMessage) obj).getId();

				return c3.compareTo(c4);
			}
		}

		return c1.compareTo(c2);
	}

}
