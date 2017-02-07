/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.core.domain;


import org.openecomp.portalsdk.core.domain.support.DomainVo;

public class Menu extends DomainVo {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Menu() {}

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

	private String  menuLevel;
    private String  label;
    private Long    parentId;
	private String  action;
    private String  functionCd;
    private Short   sortOrder;
    private String  servlet;
    private String  queryString;
    private String  externalUrl;
    private String  target;
    private boolean active;
    private String  menuSetCode;
    private boolean separator;
    private String  imageSrc;

}
