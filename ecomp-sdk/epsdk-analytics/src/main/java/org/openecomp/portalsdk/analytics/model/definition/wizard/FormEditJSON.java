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
package org.openecomp.portalsdk.analytics.model.definition.wizard;

import java.util.List;

public class FormEditJSON implements WizardJSON {

	private String tabId;
	private String tabName;
	
	private String fieldId;
	private String fieldName;
	private String fieldType;
	private boolean visible;
	private String defaultValue;
	private String fieldDefaultSQL;
	private String fieldSQL;
	private String validationType;
	private List<IdNameBooleanJSON> predefinedValueList;
	
	
	public String getTabId() {
		return tabId;
	}
	public void setTabId(String tabId) {
		this.tabId = tabId;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getFieldDefaultSQL() {
		return fieldDefaultSQL;
	}
	public void setFieldDefaultSQL(String fieldDefaultSQL) {
		this.fieldDefaultSQL = fieldDefaultSQL;
	}
	public String getValidationType() {
		return validationType;
	}
	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}
	public List<IdNameBooleanJSON> getPredefinedValueList() {
		return predefinedValueList;
	}
	public void setPredefinedValueList(List<IdNameBooleanJSON> predefinedValueList) {
		this.predefinedValueList = predefinedValueList;
	}
	public String getFieldSQL() {
		return fieldSQL;
	}
	public void setFieldSQL(String fieldSQL) {
		this.fieldSQL = fieldSQL;
	}
	
	
	
	
}
