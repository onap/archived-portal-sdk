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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.onap.portalsdk.analytics.config.ConfigLoader;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.model.runtime.ReportFormFields;
import org.onap.portalsdk.analytics.model.runtime.ReportParamValues;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.core.web.support.UserUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigLoader.class, AppUtils.class })
public class GlobalsTest {

	Properties properties = PowerMockito.mock(Properties.class);

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	@Test(expected = java.lang.RuntimeException.class)
	public void initializeSystemTest() throws Exception {
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		PowerMockito.mockStatic(ConfigLoader.class);
		Properties sqlProperty = PowerMockito.mock(Properties.class);
		Mockito.when(ConfigLoader.getProperties(Matchers.any(ServletContext.class), Matchers.anyString()))
				.thenReturn(sqlProperty);
		Globals.initializeSystem(servletContext);
	}

	@Test
	public void initializeRaptorPropertiesTest() throws Exception {
		PowerMockito.mockStatic(ConfigLoader.class);
		Properties sqlProperty = PowerMockito.mock(Properties.class);
		Mockito.when(ConfigLoader.getProperties(Matchers.any(ServletContext.class), Matchers.anyString()))
				.thenReturn(sqlProperty);
		Globals.initializeRaptorProperties(sqlProperty);
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void getRaptorActionMappingTest() {
		Globals.getRaptorActionMapping();
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void getSystemTypeTest() {
		Globals.getSystemType();
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void getAppUtilsTest() {
		Globals.getAppUtils();
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void getDbUtilsTest() {
		Globals.getDbUtils();
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void getRDbUtilsTest() {
		Globals.getRDbUtils();
	}

	@Test
	public void getDebugLevelTest() {
		assertEquals(Globals.getDebugLevel(), 5);
	}

	@Test
	public void getDownloadLimitTest() {
		assertEquals(Globals.getDownloadLimit(), 65000);
	}

	// @Test(expected = java.lang.NullPointerException.class)
	// public void getCSVDownloadLimitTest() {
	// assertEquals(Globals.getCSVDownloadLimit(),65000);
	// }
	//

	@Test
	public void getDownloadLimitAsTextTest() {
		assertEquals(Globals.getDownloadLimitAsText(),
				java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(65000));
	}

	@Test
	public void getDefaultPageSizeTest() {
		assertEquals(Globals.getDefaultPageSize(), 50);
	}

	@Test
	public void getFormFieldsListSizeTest() {
		assertEquals(Globals.getFormFieldsListSize(), 50);
	}

	@Test
	public void getSchedulerIntervalTest() {
		assertEquals(Globals.getSchedulerInterval(), 0);
	}

	@Test
	public void getBaseTitleTest() {
		assertEquals(Globals.getBaseTitle(), "ANALYSIS");
	}

	@Test
	public void getSystemNameTest() {
		assertEquals(Globals.getSystemName(), "MSA Databank");
	}

	@Test
	public void getAllowSQLBasedReportsTest() {
		assertTrue(Globals.getAllowSQLBasedReports());
	}

	@Test
	public void getShowDisclaimerTest() {
		assertTrue(Globals.getShowDisclaimer());
	}

	@Test
	public void getDisplayFormBeforeRunTest() {
		assertTrue(Globals.getDisplayFormBeforeRun());
	}

	@Test
	public void getIncludeFormWithDataTest() {
		assertTrue(Globals.getIncludeFormWithData());
	}

	@Test
	public void getCacheChartDataTest() {
		assertTrue(Globals.getCacheChartData());
	}

	@Test
	public void getCacheCurPageDataTest() {
		assertTrue(Globals.getCacheCurPageData());
	}

	@Test
	public void getDeleteOnlyByOwnerTest() {
		assertTrue(Globals.getDeleteOnlyByOwner());
	}

	@Test
	public void getEnableReportLogTest() {
		assertTrue(Globals.getEnableReportLog());
	}

	@Test
	public void getCacheUserRolesest() {
		assertTrue(Globals.getCacheUserRoles());
	}

	@Test
	public void getMonthFormatUseLastDayTest() {
		assertTrue(Globals.getMonthFormatUseLastDay());
	}

	@Test
	public void getPrintTitleInDownloadTest() {
		assertFalse(Globals.getPrintTitleInDownload());
	}

	@Test
	public void getShowDescrAtRuntimeTest() {
		assertFalse(Globals.getShowDescrAtRuntime());
	}

	@Test
	public void getShowNonstandardChartsTest() {
		assertFalse(Globals.getShowNonstandardCharts());
	}

	@Test
	public void getAllowRuntimeChartSelTest() {
		assertTrue(Globals.getAllowRuntimeChartSel());
	}

	@Test
	public void getDisplayChartTitleTest() {
		assertFalse(Globals.getDisplayChartTitle());
	}

	@Test
	public void getMergeCrosstabRowHeadingsTest() {
		assertTrue(Globals.getMergeCrosstabRowHeadings());
	}

	@Test
	public void getPrintParamsInDownloadTest() {
		assertFalse(Globals.getPrintParamsInDownload());
	}

	@Test
	public void getCanCopyOnReadOnlyTest() {
		assertTrue(Globals.getCanCopyOnReadOnly());
	}

	@Test
	public void getMaxDecimalsOnTotalsTest() {
		assertEquals(Globals.getMaxDecimalsOnTotals(), 2);
	}

	@Test
	public void getDefaultChartWidthTest() {
		assertEquals(Globals.getDefaultChartWidth(), 700);
	}

	@Test
	public void getDefaultChartHeightTest() {
		assertEquals(Globals.getDefaultChartHeight(), 420);
	}

	@Test
	public void getSkipChartLabelsLimitTest() {
		assertEquals(Globals.getSkipChartLabelsLimit(), 30);
	}

	@Test
	public void getRestrictTablesByRoleTest() {
		assertTrue(Globals.getRestrictTablesByRole());
	}

	@Test
	public void getJavaTimeFormatTest() {
		assertEquals(Globals.getJavaTimeFormat(), "MM/dd/yyyy h:m:s a");
	}

	@Test
	public void getRaptorVersionTest() {
		assertEquals(Globals.getRaptorVersion(), "10.5.1");
	}

	@Test
	public void getDisplayAllUsersTest() {
		assertTrue(Globals.getDisplayAllUsers());
	}

	@Test
	public void getUserColDefTest() {
		Globals.getUserColDef();
	}

	@Test
	public void getSheetNameTest() {
		assertEquals(Globals.getSheetName(), "Raptor Reports");
	}

	@Test
	public void getFlatFileLowerLimitTest() {
		assertEquals(Globals.getFlatFileLowerLimit(), 0);
	}

	@Test
	public void getFlatFileUpperLimitTest() {
		assertEquals(Globals.getFlatFileUpperLimit(), 0);
	}

	@Test
	public void getShellScriptDirest() {
		assertEquals(Globals.getShellScriptDir(), "");
	}

	@Test
	public void getQueryFolderTest() {
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getTempFolderPath()).thenReturn("test");
		Globals.getQueryFolder();
	}

	@Test
	public void getRequestParamsTest() {
		assertEquals(Globals.getRequestParams(), "");
	}

	@Test
	public void getSessionParamsTest() {
		assertEquals(Globals.getSessionParams(), "");
	}

	@Test
	public void getPrintFooterInDownloadTest() {
		Globals.getPrintFooterInDownload();
	}

	@Test
	public void getFooterFirstLineTest() {
		Globals.getFooterFirstLine();
	}

	@Test
	public void getFooterSecondLineTest() {
		Globals.getFooterSecondLine();
	}

	@Test
	public void getReportsInPoPUpWindowTest() {
		assertEquals(Globals.getReportsInPoPUpWindow(), false);
	}

	@Test
	public void getPoPUpInNewWindowTest() {
		assertEquals(Globals.getPoPUpInNewWindow(), false);
	}

	@Test
	public void getPassRequestParamInDrilldownTest() {
		Globals.getPassRequestParamInDrilldown();
	}

	@Test
	public void getDataFontSizeTest() {
		assertEquals(Globals.getDataFontSize(), 10, 0);
	}

	@Test
	public void getDataFontSizeOffsetTest() {
		assertEquals(Globals.getDataFontSizeOffset(), 9, 0);
	}

	@Test
	public void getFooterFontSizeTest() {
		assertEquals(Globals.getFooterFontSize(), 9, 0);
	}

	@Test
	public void getPageNumberPositionTest() {
		assertEquals(Globals.getPageNumberPosition(), 1);
	}

	@Test
	public void getDataFontFamilyTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_font_family")).thenReturn(null);
		assertEquals(Globals.getDataFontFamily(), "Arial");
	}

	@Test
	public void getDataFontFamily1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_font_family")).thenReturn("test");
		assertEquals(Globals.getDataFontFamily(), "test");
	}

	@Test
	public void getFooterFontFamilyTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_footer_font_family")).thenReturn(null);
		assertEquals(Globals.getFooterFontFamily(), "Arial");
	}

	@Test
	public void getFooterFontFamily1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_footer_font_family")).thenReturn("test");
		assertEquals(Globals.getFooterFontFamily(), "test");
	}

	@Test
	public void isCoverPageNeededTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("display_cover_page")).thenReturn(null);
		assertTrue(Globals.isCoverPageNeeded());
	}

	@Test
	public void isCoverPageNeeded1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("display_cover_page")).thenReturn("test");
		assertFalse(Globals.isCoverPageNeeded());
	}

	@Test
	public void isDataAlternateColorTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_alternate_color")).thenReturn(null);
		assertTrue(Globals.isDataAlternateColor());
	}

	@Test
	public void isDataAlternateColor1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_alternate_color")).thenReturn("test");
		assertFalse(Globals.isDataAlternateColor());
	}

	@Test
	public void getPDFFooterTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_footer")).thenReturn(null);
		assertEquals(Globals.getPDFFooter(), "");
	}

	@Test
	public void getPDFFooter1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_footer")).thenReturn("test");
		assertEquals(Globals.getPDFFooter(), "test");
	}

	@Test
	public void isCreatedOwnerInfoNeededTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("display_create_owner_info")).thenReturn(null);
		assertTrue(Globals.isCreatedOwnerInfoNeeded());
	}

	@Test
	public void isCreatedOwnerInfoNeeded1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("display_create_owner_info")).thenReturn("test");
		assertFalse(Globals.isCreatedOwnerInfoNeeded());
	}

	// @Test
	// public void displayFormFieldInfoTest() {
	//
	// Properties properties= PowerMockito.mock(Properties.class);
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("display_formfield_info")).thenReturn(null);
	// Globals.displayFormFieldInfo();
	// }
	//
	// @Test
	// public void displayFormFieldInfo1Test() {
	//
	// Properties properties= PowerMockito.mock(Properties.class);
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("display_formfield_info")).thenReturn("test");
	// Globals.displayFormFieldInfo();
	// }
	//
	//
	// @Test
	// public void customizeFormFieldInfoTest() {
	//
	// Properties properties= PowerMockito.mock(Properties.class);
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("customize_formfield_info")).thenReturn(null);
	// assertFalse(Globals.customizeFormFieldInfo());
	// }
	//
	// @Test
	// public void customizeFormFieldInfo1Test() {
	//
	// Properties properties= PowerMockito.mock(Properties.class);
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("customize_formfield_info")).thenReturn("test");
	// assertFalse(Globals.customizeFormFieldInfo());
	// }

	@Test
	public void getSessionInfoForTheCoverPageTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("session_info")).thenReturn(null);
		assertEquals(Globals.getSessionInfoForTheCoverPage(), "");
	}

	@Test
	public void getSessionInfoForTheCoverPage1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("session_info")).thenReturn("test");
		assertEquals(Globals.getSessionInfoForTheCoverPage(), "test");
	}

	@Test
	public void getDatePatternTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_date_pattern")).thenReturn(null);
		assertEquals(Globals.getDatePattern(), "MM/dd/yyyy hh:mm:ss a");
	}

	@Test
	public void getDatePattern1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_date_pattern")).thenReturn("test");
		assertEquals(Globals.getDatePattern(), "test");
	}

	@Test
	public void getTimeZoneTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_date_timezone")).thenReturn(null);
		assertEquals(Globals.getTimeZone(), "EST");
	}

	@Test
	public void getTimeZone1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_date_timezone")).thenReturn("test");
		assertEquals(Globals.getTimeZone(), "test");
	}

	@Test
	public void getWordBeforePageNumberTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_word_before_page_number")).thenReturn(null);
		assertEquals(Globals.getWordBeforePageNumber(), "");
	}

	@Test
	public void getWordBeforePageNumber1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_word_before_page_number")).thenReturn("test");
		assertEquals(Globals.getWordBeforePageNumber(), "test");
	}

	@Test
	public void getWordAfterPageNumberTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_word_after_page_number")).thenReturn(null);
		assertEquals(Globals.getWordAfterPageNumber(), "");
	}

	@Test
	public void getWordAfterPageNumber1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_word_after_page_number")).thenReturn("test");
		assertEquals(Globals.getWordAfterPageNumber(), "test");
	}

	@Test
	public void getPDFFooterFontSizeTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_footer_font_size")).thenReturn("1");
		assertEquals(Globals.getPDFFooterFontSize(), 1, 0);
	}

	@Test
	public void getPDFFooterFontSize1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_footer_font_size")).thenReturn(null);
		assertEquals(Globals.getPDFFooterFontSize(), 7, 0);
	}

	@Test
	public void getDataBackgroundAlternateHexCodeTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_background_alternate_hex_code")).thenReturn(null);
		assertEquals(Globals.getDataBackgroundAlternateHexCode(), "#FFFFFF");
	}

	@Test
	public void getDataBackgroundAlternateHexCode1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_background_alternate_hex_code")).thenReturn("test");
		assertEquals(Globals.getDataBackgroundAlternateHexCode(), "test");
	}

	@Test
	public void getDataDefaultBackgroundHexCodeTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_default_background_hex_code")).thenReturn(null);
		assertEquals(Globals.getDataDefaultBackgroundHexCode(), "#FFFFFF");
	}

	@Test
	public void getDataDefaultBackgroundHexCode1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_default_background_hex_code")).thenReturn("test");
		assertEquals(Globals.getDataDefaultBackgroundHexCode(), "test");
	}

	@Test
	public void getDataTableHeaderFontColorTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_table_header_font_hex_code")).thenReturn(null);
		assertEquals(Globals.getDataTableHeaderFontColor(), "#FFFFFF");
	}

	@Test
	public void getDataTableHeaderFontColor1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_table_header_font_hex_code")).thenReturn("test");
		assertEquals(Globals.getDataTableHeaderFontColor(), "test");
	}

	@Test
	public void getDataTableHeaderBackgroundFontColorTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_table_header_background_hex_code")).thenReturn(null);
		assertEquals(Globals.getDataTableHeaderBackgroundFontColor(), "#8A9BB3");
	}

	@Test
	public void getDataTableHeaderBackgroundFontColor1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_data_table_header_background_hex_code")).thenReturn("test");
		assertEquals(Globals.getDataTableHeaderBackgroundFontColor(), "test");
	}

	// @Test
	// public void isFolderTreeAllowedTest() {
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("show_folder_tree")).thenReturn(null);
	// assertEquals(Globals.isFolderTreeAllowed(), true);
	// }
	//
	// @Test
	// public void isFolderTreeAllowed1Test() {
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("show_folder_tree")).thenReturn("test");
	// assertEquals(Globals.isFolderTreeAllowed(), true);
	// }
	//
	// @Test
	// public void isFolderTreeAllowed2Test() {
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("show_folder_tree")).thenReturn("YES");
	// assertEquals(Globals.isFolderTreeAllowed(), true);
	// }
	@Test
	public void getCoverPageFirstColumnSizeTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_coverpage_firstcolumn_size")).thenReturn("1");
		assertEquals(Globals.getCoverPageFirstColumnSize(), 1, 0);
	}

	@Test
	public void getCoverPageFirstColumnSize1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_coverpage_firstcolumn_size")).thenReturn(null);
		assertEquals(Globals.getCoverPageFirstColumnSize(), 0, 3);
	}

	@Test
	public void isImageAutoRotateTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_image_auto_rotate")).thenReturn(null);
		assertFalse(Globals.isImageAutoRotate());
	}

	@Test
	public void isImageAutoRotate1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("pdf_image_auto_rotate")).thenReturn("true");
		assertTrue(Globals.isImageAutoRotate());
	}

	@Test
	public void isShowPDFDownloadIconTest() {
		assertFalse(Globals.isShowPDFDownloadIcon());
	}

	@Test
	public void setShowPDFDownloadIconTest() {
		Globals.setShowPDFDownloadIcon(false);
	}

	@Test
	public void getScheduleLimitTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("schedule_limit")).thenReturn("1");
		assertEquals(Globals.getScheduleLimit(), 1000);
	}

	@Test
	public void getScheduleLimit1Test() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("schedule_limit")).thenReturn("");
		assertEquals(Globals.getScheduleLimit(), 1000);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getScheduleLimitExceptionTest() {
		Globals.setRaptorPdfProperties(properties);
		Mockito.when(properties.getProperty("schedule_limit")).thenThrow(NullPointerException.class);
		assertEquals(Globals.getScheduleLimit(), 1000);
	}

	// @Test
	// public void getCustomizedScheduleQueryForUsers1Test()
	// {
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("schedule_custom_query_for_users")).thenReturn("test");
	// assertEquals(Globals.getCustomizedScheduleQueryForUsers(), "");
	// }

	// @Test
	// public void getTimeFormatTest()
	// {
	// Globals.setRaptorPdfProperties(properties);
	// Mockito.when(properties.getProperty("time_format")).thenReturn("1");
	// assertEquals(Globals.getTimeFormat(), 1000);
	// }

	@Test
	public void getRequestParametersMapTest() {
		HashMap paramsMap = new HashMap<>();
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@Test
	public void getRequestParametersMap1Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("formField", "test");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@Test
	public void getRequestParametersMap11Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("formField", "test");
		paramsMap.put("test", "test");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_HR");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@Test
	public void getRequestParametersMap10Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("formField", "test");
		paramsMap.put("test", "test");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_MIN");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParametersMap5Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("formField", "test");
		paramsMap.put("test", "test");
		paramsMap.put("test_auto", "test_auto");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParametersMap6Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "test_auto");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParametersMap7Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "test_auto");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_MIN");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParametersMap8Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "test_auto");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_HR");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParametersMap9Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_HR");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);

	}

	@Test
	public void getRequestParametersMap4Test() throws Exception {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("formField", "");
		paramsMap.put("test", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_MIN");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParamtersMapTest() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("formField", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_MIN");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParamtersMap1Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test", "test");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_MIN");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParamtersMap2Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test", "test");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParamtersMap3Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test", "test");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_HR");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParamtersMap4Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "test");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_HR");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParamtersMap8Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_MIN");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getRequestParamtersMap9Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Globals.getRequestParametersMap(mockedRequest, paramsMap);
	}

	@Test
	public void getRequestParamtersMap6Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Globals.getRequestParamtersMap(mockedRequest, false);
	}

	@Test
	public void getRequestParamtersMap17Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Globals.getRequestParamtersMap(mockedRequest, false);
	}

	@Test
	public void getRequestParamtersMap16Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Mockito.when(mockedRequest.getParameterValues("test")).thenReturn((new String[] { "testSelected" }));
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getRequestFlag(Matchers.any(HttpServletRequest.class), Matchers.anyString()))
				.thenReturn(false);
		Globals.getRequestParamtersMap(mockedRequest, false);
	}

	@Test
	public void getRequestParamtersMap12Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Mockito.when(mockedRequest.getParameterValues("test")).thenReturn((new String[] { "testSelected" }));
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getRequestFlag(Matchers.any(HttpServletRequest.class), Matchers.anyString()))
				.thenReturn(true);
		Hashtable ht = new Hashtable();
		ht.put("test", "test");
		ReportParamValues ReportParamValues = PowerMockito.mock(ReportParamValues.class);
		Mockito.when(rr.getReportParamValues()).thenReturn(ReportParamValues);
		Globals.getRequestParamtersMap(mockedRequest, true);
	}

	@Test
	public void getRequestParamtersMap15Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX1");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Mockito.when(mockedRequest.getParameterValues("test")).thenReturn((new String[] { "testSelected" }));
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getRequestFlag(Matchers.any(HttpServletRequest.class), Matchers.anyString()))
				.thenReturn(false);
		Globals.getRequestParamtersMap(mockedRequest, false);
	}

	@Test
	public void getRequestParamtersMap13Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX1");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Mockito.when(mockedRequest.getParameterValues("test")).thenReturn((new String[] { "testSelected" }));
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getRequestFlag(Matchers.any(HttpServletRequest.class), Matchers.anyString()))
				.thenReturn(false);
		Mockito.when(mockedRequest.getParameter("test")).thenReturn("test");
		Globals.getRequestParamtersMap(mockedRequest, false);
	}

	@Test
	public void getRequestParamtersMap14Test() {
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(rr.getReportType()).thenReturn("Dashboard");

		Mockito.when(mockedRequest.getSession().getAttribute("FirstDashReport")).thenReturn(rr);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("test_auto", "");
		ReportWrapper reportWrapper = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = PowerMockito.mock(ReportFormFields.class);
		Mockito.when(reportFormFields.size()).thenReturn(1);
		Mockito.when(rr.getReportFormFields()).thenReturn(reportFormFields);
		FormField ff = PowerMockito.mock(FormField.class);
		Mockito.when(reportFormFields.getFormField(0)).thenReturn(ff);
		Mockito.when(ff.getFieldName()).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("CHECK_BOX1");
		Mockito.when(rr.getReportDefType()).thenReturn("test");
		Mockito.when(ff.getValidationType()).thenReturn("TIMESTAMP_SEC");
		Mockito.when(mockedRequest.getParameterValues("test")).thenReturn((new String[] { "testSelected" }));
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getRequestFlag(Matchers.any(HttpServletRequest.class), Matchers.anyString()))
				.thenReturn(false);
		Mockito.when(mockedRequest.getParameter("test")).thenReturn("test");
		Mockito.when(ff.getFieldType()).thenReturn("TEXTAREA");
		Mockito.when(rr.getReportDefType()).thenReturn("SQL-based");
		Globals.getRequestParamtersMap(mockedRequest, false);
	}

}
