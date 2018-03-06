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
package org.onap.portalsdk.analytics.xmlobj;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.analytics.xmlobj.PDFAdditionalOptions;

public class CustomReportTypeTest {

	public CustomReportType mockCustomReportType()
	{
		CustomReportType customReportType = new CustomReportType();
		customReportType.setReportName("test");
		customReportType.setReportDescr("desc");
		customReportType.setNumDashCols("testcols");
		customReportType.setDashboardLayoutHTML("layout");
		customReportType.setDbInfo("db");
		customReportType.setDbType("testType");
		customReportType.setChartType("chartType");
		customReportType.setChartTypeFixed("fixed");
		customReportType.setChartMultiSeries("series");
		customReportType.setChartLeftAxisLabel("leftAxis");
		customReportType.setChartRightAxisLabel("rightAxis");
		customReportType.setChartWidth("width");
		customReportType.setChartHeight("height");
		customReportType.setShowChartTitle(false);
		customReportType.setPublic(false);
		customReportType.setHideFormFieldAfterRun(false);
		customReportType.setCreateId("id");
		customReportType.setCreateDate(null);
		customReportType.setReportSQL("report");
		customReportType.setReportTitle("title");
		customReportType.setReportSubTitle("subtitle");
		customReportType.setReportHeader("header");
		customReportType.setFrozenColumns(1);
		customReportType.setPdfImgLogo("logo");
		customReportType.setEmptyMessage("message");
		customReportType.setWidthNoColumn("widthNoColumn");
		customReportType.setDataGridAlign("grid");
		customReportType.setReportFooter("footer");
		customReportType.setNumFormCols("numForm");
		customReportType.setDisplayOptions("dsiplay");
		customReportType.setJumpTo(1);
		customReportType.setSearchPageSize(1);
		customReportType.setNavPosition("position");
		customReportType.setToggleLayout(false);
		customReportType.setPageNav(false);
		customReportType.setShowPageSize(false);
		customReportType.setShowNavPos(false);
		customReportType.setShowGotoOption(false);
		customReportType.setDataContainerHeight("containerHeight");
		customReportType.setDataContainerWidth("containerWidth");
		customReportType.setAllowSchedule("schedule");
		customReportType.setMultiGroupColumn("multiGroups");
		customReportType.setTopDown("topDown");
		customReportType.setSizedByContent("content");
		customReportType.setComment("comment");
		customReportType.setDataSourceList(null);
		customReportType.setFormFieldList(null);
		customReportType.setJavascriptList(null);
		customReportType.setSemaphoreList(null);
		customReportType.setDashboardOptions("dashboardOptions");
		customReportType.setDashboardType(false);
		customReportType.setReportInNewWindow(false);
		customReportType.setDisplayFolderTree(false);
		customReportType.setMaxRowsInExcelDownload(1);
		customReportType.setDashBoardReports(null);
		customReportType.setDashBoardReportsNew(null);
		customReportType.setChartAdditionalOptions(null);
		customReportType.setPdfAdditionalOptions(null);
		customReportType.setChartDrillOptions(null);
		customReportType.setDataminingOptions(null);
		customReportType.setJavascriptElement("js");
		customReportType.setFolderId("folderId");    
	    customReportType.setDrillURLInPoPUpPresent(false);
	    customReportType.setIsOneTimeScheduleAllowed("false");
	    customReportType.setIsHourlyScheduleAllowed("no");
	    customReportType.setIsDailyScheduleAllowed("no");
	    customReportType.setIsDailyMFScheduleAllowed("no");
	    customReportType.setIsWeeklyScheduleAllowed("no");
	    customReportType.setIsMonthlyScheduleAllowed("no");
	    customReportType.setReportMap(null);
	    customReportType.setPageSize(1);
	    customReportType.setReportType("test");
		return customReportType;
	}
	
	@Test
	public void customReportTypeTest()
	{
		CustomReportType customReportType = mockCustomReportType();

       assertEquals(customReportType.getReportName(),"test");
       assertEquals(customReportType.getReportDescr(),"desc");
       assertEquals(customReportType.getNumDashCols(),"testcols");
       assertEquals(customReportType.getDashboardLayoutHTML(),"layout");
       assertEquals(customReportType.getDbInfo(),"db");
       assertEquals(customReportType.getDbType(),"testType");
       assertEquals(customReportType.getChartType(),"chartType");
       assertEquals(customReportType.getChartTypeFixed(),"fixed");
       assertEquals(customReportType.getChartMultiSeries(),"series");
       assertEquals(customReportType.getChartLeftAxisLabel(),"leftAxis");
       assertEquals(customReportType.getChartRightAxisLabel(),"rightAxis");
       assertEquals(customReportType.getChartWidth(),"width");
       assertEquals(customReportType.getChartHeight(),"height");
       assertEquals(customReportType.isShowChartTitle(), false);
       assertEquals(customReportType.isPublic(),false);
       assertEquals(customReportType.isHideFormFieldAfterRun(),false);
       assertEquals(customReportType.getCreateId(),"id");
       assertEquals(customReportType.getCreateDate(),null);
       assertEquals(customReportType.getReportSQL(),"report");
       assertEquals(customReportType.getReportTitle(),"title");
       assertEquals(customReportType.getReportSubTitle(),"subtitle");
       assertEquals(customReportType.getReportHeader(),"header");
       assertTrue(customReportType.getFrozenColumns() == 1);
       assertEquals(customReportType.getPdfImgLogo(),"logo");
       assertEquals(customReportType.getEmptyMessage(),"message");
       assertEquals(customReportType.getWidthNoColumn(),"widthNoColumn");
       assertEquals(customReportType.getDataGridAlign(),"grid");
       assertEquals(customReportType.getReportFooter(),"footer");
       assertEquals(customReportType.getNumFormCols(),"numForm");
       assertEquals(customReportType.getDisplayOptions(),"dsiplay");
       assertTrue(customReportType.getJumpTo()==1);
       assertTrue(customReportType.getSearchPageSize()==1);
       assertEquals(customReportType.getNavPosition(),"position");
       assertEquals(customReportType.isToggleLayout(),false);
       assertEquals(customReportType.isPageNav(),false);
       assertEquals(customReportType.isShowPageSize(),false);
       assertEquals(customReportType.isShowNavPos(),false);
       assertEquals(customReportType.isShowGotoOption(),false);
       assertEquals(customReportType.getDataContainerHeight(),"containerHeight");
       assertEquals(customReportType.getDataContainerWidth(),"containerWidth");
       assertEquals(customReportType.getAllowSchedule(),"schedule");
       assertEquals(customReportType.getMultiGroupColumn(),"multiGroups");
       assertEquals(customReportType.getTopDown(),"topDown");
       assertEquals(customReportType.getSizedByContent(),"content");
       assertEquals(customReportType.getComment(),"comment");
       assertEquals(customReportType.getDataSourceList(),null);
       assertEquals(customReportType.getFormFieldList(),null);
       assertEquals(customReportType.getJavascriptList(),null);
       assertEquals(customReportType.getSemaphoreList(),null);
       assertEquals(customReportType.getDashboardOptions(),"dashboardOptions");
       assertEquals(customReportType.isDashboardType(),false);
       assertEquals(customReportType.isReportInNewWindow(),false);
       assertEquals(customReportType.isDisplayFolderTree(),false);
       assertTrue(customReportType.getMaxRowsInExcelDownload() ==1);
       assertEquals(customReportType.getDashBoardReports(),null);
       assertEquals(customReportType.getDashBoardReportsNew(),null);
       assertEquals(customReportType.getChartAdditionalOptions(),null);
       assertEquals(customReportType.getChartDrillOptions(),null);
       assertEquals(customReportType.getDataminingOptions(),null);
       assertEquals(customReportType.getJavascriptElement(),"js");
       assertEquals(customReportType.getFolderId(),"folderId");    
       assertEquals(customReportType.isDrillURLInPoPUpPresent(),false);
       assertEquals(customReportType.getIsOneTimeScheduleAllowed(),"false");
       assertEquals(customReportType.getIsHourlyScheduleAllowed(),"no");
       assertEquals(customReportType.getIsDailyScheduleAllowed(),"no");
       assertEquals(customReportType.getIsDailyMFScheduleAllowed(),"no");
       assertEquals(customReportType.getIsWeeklyScheduleAllowed(),"no");
       assertEquals(customReportType.getIsMonthlyScheduleAllowed(),"no");
       assertEquals(customReportType.getReportMap(),null);
       assertTrue(customReportType.getPageSize()==1);
       assertEquals(customReportType.getReportType(),"test");
       PDFAdditionalOptions  pDFAdditionalOptions = customReportType.getPdfAdditionalOptions();
       assertNull(pDFAdditionalOptions);
	}

}
