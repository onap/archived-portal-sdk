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

import org.onap.portalsdk.core.domain.support.DomainVo;

public class Menu extends DomainVo {

	private static final long serialVersionUID = 1L;

	private String menuLevel;
	private String label;
	private Long parentId;
	private String action;
	private String functionCd;
	private Short sortOrder;
	private String servlet;
	private String queryString;
	private String externalUrl;
	private String target;
	private boolean active;
	private String menuSetCode;
	private boolean separator;
	private String imageSrc;

	public String getAction() {
		return action;
	}

	public boolean isActive() {
		return active;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public String getFunctionCd() {
		return functionCd;
	}

	public String getLabel() {
		return label;
	}

	public String getMenuLevel() {
		return menuLevel;
	}

	public Long getParentId() {
		return parentId;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getServlet() {
		return servlet;
	}

	public Short getSortOrder() {
		return sortOrder;
	}

	public String getTarget() {
		return target;
	}

	public String getMenuSetCode() {
		return menuSetCode;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public void setFunctionCd(String functionCd) {
		this.functionCd = functionCd;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setMenuLevel(String menuLevel) {
		this.menuLevel = menuLevel;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setServlet(String servlet) {
		this.servlet = servlet;
	}

	public void setSortOrder(Short sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setMenuSetCode(String menuSetCode) {
		this.menuSetCode = menuSetCode;
	}

	public boolean isSeparator() {
		return separator;
	}

	public void setSeparator(boolean separator) {
		this.separator = separator;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

}
