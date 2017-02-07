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
package org.openecomp.portalsdk.analytics.model.runtime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.openecomp.portalsdk.analytics.model.base.IdNameValue;
import org.openecomp.portalsdk.analytics.view.ColumnHeader;

/*class MetaColumn {
    private String tableId;
    private String dbColName;
    private String crossTabValue;
    private String colName;
    private String displayName;
    private Integer displayWidth;
    private String displayWidthInPxls;
    private String pdfDisplayWidthInPxls;
    private String displayAlignment;
    private String displayHeaderAlignment;
    private int orderSeq;
    private boolean visible;
    private boolean calculated;
    private String colType;
    private String hyperlinkURL;
    private String hyperlinkType;
    private String actionImg;
    private Integer groupByPos;
    private String subTotalCustomText;
    private Boolean hideRepeatedKey;
    private String colFormat;
    private boolean groupBreak;
    private Integer orderBySeq;
    private String orderByAscDesc;
    private String displayTotal;
    private String colOnChart;
    private Integer chartSeq;
    private String chartColor;
    private String chartLineType;
    private Boolean chartSeries;
    private Boolean isRangeAxisFilled;
    private Boolean createInNewChart;
    private String drillDownType;
    private Boolean drillinPoPUp;
    private String drillDownURL;
    private String drillDownParams;
    private String comment;
    private ColFilterList colFilterList;
    private String semaphoreId;
    private String dbColType;
    private String chartGroup;
    private String yAxis;
    private String dependsOnFormField;
    private String nowrap;
    private Integer indentation;
    private Boolean enhancedPagination;
    private Integer level;
    private Integer start;
    private Integer colspan;
    private String dataMiningCol;
    private String colId;
    
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	public String getDbColName() {
		return dbColName;
	}
	public void setDbColName(String dbColName) {
		this.dbColName = dbColName;
	}
	public String getCrossTabValue() {
		return crossTabValue;
	}
	public void setCrossTabValue(String crossTabValue) {
		this.crossTabValue = crossTabValue;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Integer getDisplayWidth() {
		return displayWidth;
	}
	public void setDisplayWidth(Integer displayWidth) {
		this.displayWidth = displayWidth;
	}
	public String getDisplayWidthInPxls() {
		return displayWidthInPxls;
	}
	public void setDisplayWidthInPxls(String displayWidthInPxls) {
		this.displayWidthInPxls = displayWidthInPxls;
	}
	public String getPdfDisplayWidthInPxls() {
		return pdfDisplayWidthInPxls;
	}
	public void setPdfDisplayWidthInPxls(String pdfDisplayWidthInPxls) {
		this.pdfDisplayWidthInPxls = pdfDisplayWidthInPxls;
	}
	public String getDisplayAlignment() {
		return displayAlignment;
	}
	public void setDisplayAlignment(String displayAlignment) {
		this.displayAlignment = displayAlignment;
	}
	public String getDisplayHeaderAlignment() {
		return displayHeaderAlignment;
	}
	public void setDisplayHeaderAlignment(String displayHeaderAlignment) {
		this.displayHeaderAlignment = displayHeaderAlignment;
	}
	public int getOrderSeq() {
		return orderSeq;
	}
	public void setOrderSeq(int orderSeq) {
		this.orderSeq = orderSeq;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isCalculated() {
		return calculated;
	}
	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
	}
	public String getHyperlinkURL() {
		return hyperlinkURL;
	}
	public void setHyperlinkURL(String hyperlinkURL) {
		this.hyperlinkURL = hyperlinkURL;
	}
	public String getHyperlinkType() {
		return hyperlinkType;
	}
	public void setHyperlinkType(String hyperlinkType) {
		this.hyperlinkType = hyperlinkType;
	}
	public String getActionImg() {
		return actionImg;
	}
	public void setActionImg(String actionImg) {
		this.actionImg = actionImg;
	}
	public Integer getGroupByPos() {
		return groupByPos;
	}
	public void setGroupByPos(Integer groupByPos) {
		this.groupByPos = groupByPos;
	}
	public String getSubTotalCustomText() {
		return subTotalCustomText;
	}
	public void setSubTotalCustomText(String subTotalCustomText) {
		this.subTotalCustomText = subTotalCustomText;
	}
	public Boolean getHideRepeatedKey() {
		return hideRepeatedKey;
	}
	public void setHideRepeatedKey(Boolean hideRepeatedKey) {
		this.hideRepeatedKey = hideRepeatedKey;
	}
	public String getColFormat() {
		return colFormat;
	}
	public void setColFormat(String colFormat) {
		this.colFormat = colFormat;
	}
	public boolean isGroupBreak() {
		return groupBreak;
	}
	public void setGroupBreak(boolean groupBreak) {
		this.groupBreak = groupBreak;
	}
	public Integer getOrderBySeq() {
		return orderBySeq;
	}
	public void setOrderBySeq(Integer orderBySeq) {
		this.orderBySeq = orderBySeq;
	}
	public String getOrderByAscDesc() {
		return orderByAscDesc;
	}
	public void setOrderByAscDesc(String orderByAscDesc) {
		this.orderByAscDesc = orderByAscDesc;
	}
	public String getDisplayTotal() {
		return displayTotal;
	}
	public void setDisplayTotal(String displayTotal) {
		this.displayTotal = displayTotal;
	}
	public String getColOnChart() {
		return colOnChart;
	}
	public void setColOnChart(String colOnChart) {
		this.colOnChart = colOnChart;
	}
	public Integer getChartSeq() {
		return chartSeq;
	}
	public void setChartSeq(Integer chartSeq) {
		this.chartSeq = chartSeq;
	}
	public String getChartColor() {
		return chartColor;
	}
	public void setChartColor(String chartColor) {
		this.chartColor = chartColor;
	}
	public String getChartLineType() {
		return chartLineType;
	}
	public void setChartLineType(String chartLineType) {
		this.chartLineType = chartLineType;
	}
	public Boolean getChartSeries() {
		return chartSeries;
	}
	public void setChartSeries(Boolean chartSeries) {
		this.chartSeries = chartSeries;
	}
	public Boolean getIsRangeAxisFilled() {
		return isRangeAxisFilled;
	}
	public void setIsRangeAxisFilled(Boolean isRangeAxisFilled) {
		this.isRangeAxisFilled = isRangeAxisFilled;
	}
	public Boolean getCreateInNewChart() {
		return createInNewChart;
	}
	public void setCreateInNewChart(Boolean createInNewChart) {
		this.createInNewChart = createInNewChart;
	}
	public String getDrillDownType() {
		return drillDownType;
	}
	public void setDrillDownType(String drillDownType) {
		this.drillDownType = drillDownType;
	}
	public Boolean getDrillinPoPUp() {
		return drillinPoPUp;
	}
	public void setDrillinPoPUp(Boolean drillinPoPUp) {
		this.drillinPoPUp = drillinPoPUp;
	}
	public String getDrillDownURL() {
		return drillDownURL;
	}
	public void setDrillDownURL(String drillDownURL) {
		this.drillDownURL = drillDownURL;
	}
	public String getDrillDownParams() {
		return drillDownParams;
	}
	public void setDrillDownParams(String drillDownParams) {
		this.drillDownParams = drillDownParams;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public ColFilterList getColFilterList() {
		return colFilterList;
	}
	public void setColFilterList(ColFilterList colFilterList) {
		this.colFilterList = colFilterList;
	}
	public String getSemaphoreId() {
		return semaphoreId;
	}
	public void setSemaphoreId(String semaphoreId) {
		this.semaphoreId = semaphoreId;
	}
	public String getDbColType() {
		return dbColType;
	}
	public void setDbColType(String dbColType) {
		this.dbColType = dbColType;
	}
	public String getChartGroup() {
		return chartGroup;
	}
	public void setChartGroup(String chartGroup) {
		this.chartGroup = chartGroup;
	}
	public String getyAxis() {
		return yAxis;
	}
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}
	public String getDependsOnFormField() {
		return dependsOnFormField;
	}
	public void setDependsOnFormField(String dependsOnFormField) {
		this.dependsOnFormField = dependsOnFormField;
	}
	public String getNowrap() {
		return nowrap;
	}
	public void setNowrap(String nowrap) {
		this.nowrap = nowrap;
	}
	public Integer getIndentation() {
		return indentation;
	}
	public void setIndentation(Integer indentation) {
		this.indentation = indentation;
	}
	public Boolean getEnhancedPagination() {
		return enhancedPagination;
	}
	public void setEnhancedPagination(Boolean enhancedPagination) {
		this.enhancedPagination = enhancedPagination;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getColspan() {
		return colspan;
	}
	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}
	public String getDataMiningCol() {
		return dataMiningCol;
	}
	public void setDataMiningCol(String dataMiningCol) {
		this.dataMiningCol = dataMiningCol;
	}
	public String getColId() {
		return colId;
	}
	public void setColId(String colId) {
		this.colId = colId;
	}  
    
}*/

/*class Row {
	private String displayValue;
	private String dataType;
	private String colId;
	private boolean visible;
	
	
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getDisplayValue() {
		return displayValue;
	}
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getColId() {
		return colId;
	}
	public void setColId(String colId) {
		this.colId = colId;
	}
	
	
}*/
class FormFieldJSON {
	private String fieldId;
	private String fieldDisplayName;
	private String fieldType;
	private String validationType;
	private boolean required;
	//private String defaultValue;
	
	private Calendar rangeStartDate;
	private Calendar rangeEndDate;
	private String multiSelectListSize;
	private String helpText;
	private boolean visible;
	private boolean triggerOtherFormFields;
	private ArrayList<IdNameValue> formFieldValues;
	
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldDisplayName() {
		return fieldDisplayName;
	}
	public void setFieldDisplayName(String fieldDisplayName) {
		this.fieldDisplayName = fieldDisplayName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getValidationType() {
		return validationType;
	}
	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public Calendar getRangeStartDate() {
		return rangeStartDate;
	}
	public void setRangeStartDate(Calendar rangeStartDate) {
		this.rangeStartDate = rangeStartDate;
	}
	public Calendar getRangeEndDate() {
		return rangeEndDate;
	}
	public void setRangeEndDate(Calendar rangeEndDate) {
		this.rangeEndDate = rangeEndDate;
	}
	public String getMultiSelectListSize() {
		return multiSelectListSize;
	}
	public void setMultiSelectListSize(String multiSelectListSize) {
		this.multiSelectListSize = multiSelectListSize;
	}
	public String getHelpText() {
		return helpText;
	}
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isTriggerOtherFormFields() {
		return triggerOtherFormFields;
	}
	public void setTriggerOtherFormFields(boolean triggerOtherFormFields) {
		this.triggerOtherFormFields = triggerOtherFormFields;
	}
	public ArrayList<IdNameValue> getFormFieldValues() {
		return formFieldValues;
	}
	public void setFormFieldValues(ArrayList<IdNameValue> formFieldValues) {
		this.formFieldValues = formFieldValues;
	}

	
}
public class ReportJSONRuntime {

	private String reportID;
	private String reportName;
	private String reportDescr;
	private String reportTitle;
	private String reportSubTitle;
	private boolean allowSchedule;
	private boolean allowEdit;
	private ArrayList <FormFieldJSON> formFieldList;
	private String formfield_comments;
	private ArrayList<ColumnHeader> reportDataColumns;
	private ArrayList<Map<String,Object>> reportDataRows;
	private int totalRows;
	private int pageSize;
	private String sqlWhole;
	private boolean chartAvailable;
	private boolean chartWizardAvailable;
	private boolean displayData;
	private boolean displayForm;
	private boolean displayExcel;
	private boolean displayPDF;
	private String backBtnURL;
	private String colIdxTobeFreezed;
	private int numFormCols;
	private String message;
	
	
	public boolean isChartAvailable() {
		return chartAvailable;
	}
	public void setChartAvailable(boolean chartAvailable) {
		this.chartAvailable = chartAvailable;
	}
	public ArrayList<Map<String,Object>> getReportDataRows() {
		return reportDataRows;
	}
	public void setReportDataRows(ArrayList<Map<String,Object>> reportDataRows) {
		this.reportDataRows = reportDataRows;
	}
	public String getReportID() {
		return reportID;
	}
	public void setReportID(String reportID) {
		this.reportID = reportID;
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
	public boolean isAllowSchedule() {
		return allowSchedule;
	}
	public void setAllowSchedule(boolean allowSchedule) {
		this.allowSchedule = allowSchedule;
	}
	public ArrayList getFormFieldList() {
		return formFieldList;
	}
	public void setFormFieldList(ArrayList formFieldList) {
		this.formFieldList = formFieldList;
	}
	public String getFormfield_comments() {
		return formfield_comments;
	}
	public void setFormfield_comments(String formfield_comments) {
		this.formfield_comments = formfield_comments;
	}
	public ArrayList<ColumnHeader> getReportDataColumns() {
		return reportDataColumns;
	}
	public void setReportDataColumns(ArrayList<ColumnHeader> reportDataColumns) {
		this.reportDataColumns = reportDataColumns;
	}

	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getSqlWhole() {
		return sqlWhole;
	}
	public void setSqlWhole(String sqlWhole) {
		this.sqlWhole = sqlWhole;
	}
	
	public boolean isAllowEdit() {
		return allowEdit;
	}
	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}
	public String getBackBtnURL() {
		return backBtnURL;
	}
	public void setBackBtnURL(String backBtnURL) {
		this.backBtnURL = backBtnURL;
	}
	public String getColIdxTobeFreezed() {
		return colIdxTobeFreezed;
	}
	public void setColIdxTobeFreezed(String colIdxTobeFreezed) {
		this.colIdxTobeFreezed = colIdxTobeFreezed;
	}
	public int getNumFormCols() {
		return numFormCols;
	}
	public void setNumFormCols(int numFormCols) {
		this.numFormCols = numFormCols;
	}
	public boolean isDisplayData() {
		return displayData;
	}
	public void setDisplayData(boolean displayData) {
		this.displayData = displayData;
	}
	public boolean isDisplayForm() {
		return displayForm;
	}
	public void setDisplayForm(boolean displayForm) {
		this.displayForm = displayForm;
	}
	public boolean isDisplayExcel() {
		return displayExcel;
	}
	public void setDisplayExcel(boolean displayExcel) {
		this.displayExcel = displayExcel;
	}
	public boolean isDisplayPDF() {
		return displayPDF;
	}
	public void setDisplayPDF(boolean displayPDF) {
		this.displayPDF = displayPDF;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isChartWizardAvailable() {
		return chartWizardAvailable;
	}
	public void setChartWizardAvailable(boolean chartWizardAvilable) {
		this.chartWizardAvailable = chartWizardAvilable;
	}
	
   

	
}
