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
package org.onap.portalsdk.analytics.model.runtime;

import java.util.ArrayList;

class Row {
	private String displayValue;
	private String dataType;
	private String colId;
	//private boolean visible;
	
	
	/*public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}*/
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
	
	
}
class IndexValueJSON {
	private int index;
	private String value;
	private String title;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}

class DomainAxisJSON   		extends IndexValueJSON {}

class ChartColumnJSON  		extends IndexValueJSON {}

class ChartTypeJSON    		extends IndexValueJSON {}

class PieChartOptions {
	
}

public class ChartJSON {
	
	private String reportID;
	private String reportName;
	private String reportDescr;
	private String reportTitle;
	private String reportSubTitle;
	private ArrayList <FormFieldJSON> formFieldList;
	private ArrayList <ChartColumnJSON> chartColumnJSONList;
	private String formfield_comments;
	private int totalRows;
	private String chartSqlWhole;
	private boolean chartAvailable;
	private ChartTypeJSON chartTypeJSON;
	private BarChartOptions barChartOptions;
	private PieChartOptions pieChartOptions;
	private TimeSeriesChartOptions timeSeriesChartOptions;
	private FlexTimeSeriesChartOptions flexTimeSeriesChartOptions;
	private CommonChartOptions commonChartOptions;
	private String width;
	private String height;
	private boolean animation;
	private String rotateLabels;
	private boolean staggerLabels;
	private boolean showTitle;
	private DomainAxisJSON domainAxisJSON;
	private CategoryAxisJSON categoryAxisJSON;
	private boolean hasCategoryAxis;
	
	
	public boolean isHasCategoryAxis() {
		return hasCategoryAxis;
	}
	public void setHasCategoryAxis(boolean hasCategoryAxis) {
		this.hasCategoryAxis = hasCategoryAxis;
	}
	private ArrayList <RangeAxisJSON> rangeAxisList;
	private ArrayList <RangeAxisJSON> rangeAxisRemoveList;
	
	private ArrayList <ArrayList<Row>> wholeList;

	private String primaryAxisLabel;
	private String secondaryAxisLabel;
	private String minRange;
	private String maxRange;
	//private int topMargin;
	//private int bottomMargin;
	//private int leftMargin;
	//private int rightMargin;
	
	/*private boolean showMaxMin;
	private boolean showLegend;
	private boolean showControls;
	private String topMargin;
	private String bottomMargin;
	private String leftMargin;
	private String rightMargin;
	private String subType;
	private boolean stacked;
	private boolean horizontalBar;
	private boolean barRealTimeAxis;
	private boolean barReduceXAxisLabels;
	private boolean timeAxis;*/
	
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
	public ArrayList<FormFieldJSON> getFormFieldList() {
		return formFieldList;
	}
	public void setFormFieldList(ArrayList<FormFieldJSON> formFieldList) {
		this.formFieldList = formFieldList;
	}
	public String getFormfield_comments() {
		return formfield_comments;
	}
	public void setFormfield_comments(String formfield_comments) {
		this.formfield_comments = formfield_comments;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public String getChartSqlWhole() {
		return chartSqlWhole;
	}
	public void setChartSqlWhole(String chartSqlWhole) {
		this.chartSqlWhole = chartSqlWhole;
	}
	public boolean isChartAvailable() {
		return chartAvailable;
	}
	public void setChartAvailable(boolean chartAvailable) {
		this.chartAvailable = chartAvailable;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public boolean isAnimation() {
		return animation;
	}
	public void setAnimation(boolean animation) {
		this.animation = animation;
	}
	public String getRotateLabels() {
		return rotateLabels;
	}
	public void setRotateLabels(String rotateLabels) {
		this.rotateLabels = rotateLabels;
	}
	public boolean isStaggerLabels() {
		return staggerLabels;
	}
	public void setStaggerLabels(boolean staggerLabels) {
		this.staggerLabels = staggerLabels;
	}
	public boolean isShowTitle() {
		return showTitle;
	}
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}	
	/*public boolean isShowMaxMin() {
		return showMaxMin;
	}
	public void setShowMaxMin(boolean showMaxMin) {
		this.showMaxMin = showMaxMin;
	}
	public boolean isShowLegend() {
		return showLegend;
	}
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}
	public boolean isShowControls() {
		return showControls;
	}
	public void setShowControls(boolean showControls) {
		this.showControls = showControls;
	}
	public String getTopMargin() {
		return topMargin;
	}
	public void setTopMargin(String topMargin) {
		this.topMargin = topMargin;
	}
	public String getBottomMargin() {
		return bottomMargin;
	}
	public void setBottomMargin(String bottomMargin) {
		this.bottomMargin = bottomMargin;
	}
	public String getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(String leftMargin) {
		this.leftMargin = leftMargin;
	}
	public String getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(String rightMargin) {
		this.rightMargin = rightMargin;
	}

	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public boolean isStacked() {
		return stacked;
	}
	public void setStacked(boolean stacked) {
		this.stacked = stacked;
	}
	public boolean isHorizontalBar() {
		return horizontalBar;
	}
	public void setHorizontalBar(boolean horizontalBar) {
		this.horizontalBar = horizontalBar;
	}
	public boolean isBarRealTimeAxis() {
		return barRealTimeAxis;
	}
	public void setBarRealTimeAxis(boolean barRealTimeAxis) {
		this.barRealTimeAxis = barRealTimeAxis;
	}
	public boolean isBarReduceXAxisLabels() {
		return barReduceXAxisLabels;
	}
	public void setBarReduceXAxisLabels(boolean barReduceXAxisLabels) {
		this.barReduceXAxisLabels = barReduceXAxisLabels;
	}
	public boolean isTimeAxis() {
		return timeAxis;
	}
	public void setTimeAxis(boolean timeAxis) {
		this.timeAxis = timeAxis;
	}*/
	public ChartTypeJSON getChartTypeJSON() {
		return chartTypeJSON;
	}
	public void setChartTypeJSON(ChartTypeJSON chartTypeJSON) {
		this.chartTypeJSON = chartTypeJSON;
	}
	public String getChartType() {
		return chartTypeJSON.getValue();
	}	
	public DomainAxisJSON getDomainAxisJSON() {
		return domainAxisJSON;
	}
	public void setDomainAxisJSON(DomainAxisJSON domainAxisJSON) {
		this.domainAxisJSON = domainAxisJSON;
	}
	public CategoryAxisJSON getCategoryAxisJSON() {
		return categoryAxisJSON;
	}
	public void setCategoryAxisJSON(CategoryAxisJSON categoryAxisJSON) {
		this.categoryAxisJSON = categoryAxisJSON;
	}
	public ArrayList<RangeAxisJSON> getRangeAxisList() {
		return rangeAxisList;
	}
	public void setRangeAxisList(ArrayList<RangeAxisJSON> rangeAxisList) {
		this.rangeAxisList = rangeAxisList;
	}
	public String getPrimaryAxisLabel() {
		return primaryAxisLabel;
	}
	public void setPrimaryAxisLabel(String primaryAxisLabel) {
		this.primaryAxisLabel = primaryAxisLabel;
	}
	public String getSecondaryAxisLabel() {
		return secondaryAxisLabel;
	}
	public void setSecondaryAxisLabel(String secondaryAxisLabel) {
		this.secondaryAxisLabel = secondaryAxisLabel;
	}
	public String getMinRange() {
		return minRange;
	}
	public void setMinRange(String minRange) {
		this.minRange = minRange;
	}
	public String getMaxRange() {
		return maxRange;
	}
	public void setMaxRange(String maxRange) {
		this.maxRange = maxRange;
	}
	/*public ArrayList<Row> getRowList() {
		return rowList;
	}
	public void setRowList(ArrayList<Row> rowList) {
		this.rowList = rowList;
	}*/
	
	public ArrayList<ArrayList<Row>> getWholeList() {
		return wholeList;
	}
	public void setWholeList(ArrayList<ArrayList<Row>> wholeList) {
		this.wholeList = wholeList;
	}	
	//private ArrayList<ColumnHeader> reportDataColumns;
	//private ArrayList<Map<String,Object>> reportDataRows;
	public ArrayList<ChartColumnJSON> getChartColumnJSONList() {
		return chartColumnJSONList;
	}
	public void setChartColumnJSONList(ArrayList<ChartColumnJSON> chartColumnJSONList) {
		this.chartColumnJSONList = chartColumnJSONList;
	}

	public BarChartOptions getBarChartOptions() {
		return barChartOptions;
	}
	public void setBarChartOptions(BarChartOptions barChartOptions) {
		this.barChartOptions = barChartOptions;
	}
	public PieChartOptions getPieChartOptions() {
		return pieChartOptions;
	}
	public void setPieChartOptions(PieChartOptions pieChartOptions) {
		this.pieChartOptions = pieChartOptions;
	}
	public TimeSeriesChartOptions getTimeSeriesChartOptions() {
		return timeSeriesChartOptions;
	}
	public void setTimeSeriesChartOptions(TimeSeriesChartOptions timeSeriesChartOptions) {
		this.timeSeriesChartOptions = timeSeriesChartOptions;
	}
	public FlexTimeSeriesChartOptions getFlexTimeSeriesChartOptions() {
		return flexTimeSeriesChartOptions;
	}
	public void setFlexTimeSeriesChartOptions(FlexTimeSeriesChartOptions flexTimeSeriesChartOptions) {
		this.flexTimeSeriesChartOptions = flexTimeSeriesChartOptions;
	}
	public CommonChartOptions getCommonChartOptions() {
		return commonChartOptions;
	}
	public void setCommonChartOptions(CommonChartOptions commonChartOptions) {
		this.commonChartOptions = commonChartOptions;
	}
	
	public String getDomainAxis() {
		if(getDomainAxisJSON() !=null)
			return getDomainAxisJSON().getValue();
		else
			return "";
	}

	public String getCategoryAxis() {
		if(getCategoryAxisJSON()!=null)
			return getCategoryAxisJSON().getValue();
		else
			return "";
	}
	public ArrayList<RangeAxisJSON> getRangeAxisRemoveList() {
		return rangeAxisRemoveList;
	}
	public void setRangeAxisRemoveList(ArrayList<RangeAxisJSON> rangeAxisRemoveList) {
		this.rangeAxisRemoveList = rangeAxisRemoveList;
	}
	
	
}
