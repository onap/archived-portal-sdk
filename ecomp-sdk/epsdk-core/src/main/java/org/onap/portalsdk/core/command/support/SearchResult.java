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
package org.onap.portalsdk.core.command.support;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class SearchResult extends ArrayList implements java.io.Serializable {

	private static final long serialVersionUID = -451947878984459011L;
	private int pageNo = 0;
	private int pageSize = 50;
	private int dataSize = -1;

	private String accessType = null;

	public SearchResult() {
		super();
	}

	@SuppressWarnings("unchecked")
	public SearchResult(List items) {
		super(items);
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getDataSize() {
		return dataSize;
	}

	public int getSize() {
		return size();
	} // for Struts bean property access

	public String getAccessType() {
		return accessType;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public void setPageSize(int pageSize) {
		this.dataSize = pageSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

}
