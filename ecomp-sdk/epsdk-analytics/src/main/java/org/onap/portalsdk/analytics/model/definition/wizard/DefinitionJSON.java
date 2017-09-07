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
package org.onap.portalsdk.analytics.model.definition.wizard;

import java.util.List;

public class DefinitionJSON implements WizardJSON {

	private String tabName;
	private String tabId;
	
	private String reportId;
	private String reportName;
    private String reportDescr;
    private String reportType;
    private String dbInfo;
    private String formHelpText;
    private Integer pageSize;
    private List<IdNameBooleanJSON> displayArea = null;
    private Boolean hideFormFieldsAfterRun;
    private Integer maxRowsInExcelCSVDownload;
    private Integer frozenColumns;
    private String dataGridAlign;
    private String emptyMessage;
    private String dataContainerHeight;
    private String dataContainerWidth;
    private List<NameBooleanJSON> displayOptions = null;
    private Boolean runtimeColSortDisabled;
    private Integer numFormCols;
    private String reportTitle;
    private String reportSubTitle;
    
	@Override
	public String getTabName() {
		return tabName;
	}

	@Override
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	@Override
	public String getTabId() {
		return tabId;
	}

	@Override
	public void setTabId(String tabId) {
		this.tabId = tabId;
	}
	
	

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportDescr() {
		return reportDescr;
	}

	public void setReportDescr(String reportDescr) {
		this.reportDescr = reportDescr;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getDbInfo() {
		return dbInfo;
	}

	public void setDbInfo(String dbInfo) {
		this.dbInfo = dbInfo;
	}

	public String getFormHelpText() {
		return formHelpText;
	}

	public void setFormHelpText(String formHelpText) {
		this.formHelpText = formHelpText;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public List<IdNameBooleanJSON> getDisplayArea() {
		return displayArea;
	}

	public void setDisplayArea(List<IdNameBooleanJSON> displayArea) {
		this.displayArea = displayArea;
	}

	public Boolean getHideFormFieldsAfterRun() {
		return hideFormFieldsAfterRun;
	}

	public void setHideFormFieldsAfterRun(Boolean hideFormFieldsAfterRun) {
		this.hideFormFieldsAfterRun = hideFormFieldsAfterRun;
	}

	public Integer getMaxRowsInExcelCSVDownload() {
		return maxRowsInExcelCSVDownload;
	}

	public void setMaxRowsInExcelCSVDownload(Integer maxRowsInExcelCSVDownload) {
		this.maxRowsInExcelCSVDownload = maxRowsInExcelCSVDownload;
	}

	public Integer getFrozenColumns() {
		return frozenColumns;
	}

	public void setFrozenColumns(Integer frozenColumns) {
		this.frozenColumns = frozenColumns;
	}

	public String getDataGridAlign() {
		return dataGridAlign;
	}

	public void setDataGridAlign(String dataGridAlign) {
		this.dataGridAlign = dataGridAlign;
	}

	public String getEmptyMessage() {
		return emptyMessage;
	}

	public void setEmptyMessage(String emptyMessage) {
		this.emptyMessage = emptyMessage;
	}

	public String getDataContainerHeight() {
		return dataContainerHeight;
	}

	public void setDataContainerHeight(String dataContainerHeight) {
		this.dataContainerHeight = dataContainerHeight;
	}

	public String getDataContainerWidth() {
		return dataContainerWidth;
	}

	public void setDataContainerWidth(String dataContainerWidth) {
		this.dataContainerWidth = dataContainerWidth;
	}

	public List<NameBooleanJSON> getDisplayOptions() {
		return displayOptions;
	}

	public void setDisplayOptions(List<NameBooleanJSON> displayOptions) {
		this.displayOptions = displayOptions;
	}

	public Boolean getRuntimeColSortDisabled() {
		return runtimeColSortDisabled;
	}

	public void setRuntimeColSortDisabled(Boolean runtimeColSortDisabled) {
		this.runtimeColSortDisabled = runtimeColSortDisabled;
	}

	public Integer getNumFormCols() {
		return numFormCols;
	}

	public void setNumFormCols(Integer numFormCols) {
		this.numFormCols = numFormCols;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportSubTitle() {
		return reportSubTitle;
	}

	public void setReportSubTitle(String reportSubTitle) {
		this.reportSubTitle = reportSubTitle;
	}
	
	

}
