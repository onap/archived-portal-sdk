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

import java.io.Serializable;
import java.util.Objects;

public class UrlsAccessible extends FusionVo implements Serializable {

	private static final long serialVersionUID = 1L;
	private UrlsAccessibleKey urlsAccessibleKey = new UrlsAccessibleKey();

	public UrlsAccessible() {
		super();
	}

	public UrlsAccessible(String url, String functionCd) {
		this();
		setUrl(url);
		setFunctionCd(functionCd);
	}

	public String getUrl() {
		return getUrlsAccessibleKey().getUrl();
	}

	public String getFunctionCd() {
		return getUrlsAccessibleKey().getFunctionCd();
	}

	public void setUrl(String url) {
		getUrlsAccessibleKey().setUrl(url);
	}

	public void setFunctionCd(String functionCd) {
		getUrlsAccessibleKey().setFunctionCd(functionCd);
	}

	public UrlsAccessibleKey getUrlsAccessibleKey() {
		return urlsAccessibleKey;
	}

	public void setUrlsAccessibleKey(UrlsAccessibleKey urlsAccessibleKey) {
		this.urlsAccessibleKey = urlsAccessibleKey;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUrl(), getFunctionCd());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof UrlsAccessible))
			return false;
		UrlsAccessible ua = (UrlsAccessible) obj;
		return Objects.equals(ua.getUrl(), getUrl()) && Objects.equals(ua.getFunctionCd(), getFunctionCd());
	}

}
