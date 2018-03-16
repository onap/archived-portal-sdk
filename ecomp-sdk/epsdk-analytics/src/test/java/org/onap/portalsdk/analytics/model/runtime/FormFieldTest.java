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
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.base.IdNameList;
import org.onap.portalsdk.analytics.model.base.IdNameSql;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.xmlobj.JavascriptItemType;
import org.onap.portalsdk.analytics.xmlobj.JavascriptList;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppUtils.class, Globals.class, ConnectionUtils.class, IdNameSql.class })
public class FormFieldTest {

	@Test
	public void getAjaxHtmlTest() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");

		HashMap formValues = new HashMap<>();
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, false);
	}

	@Test
	public void getAjaxHtml1Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, false);
	}

	@Test
	public void getAjaxHtml2Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, false);
	}

	@Test
	public void getAjaxHtml3Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, true);
	}

	@Test
	public void getAjaxHtml4Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, true);
	}

	@Test
	public void getAjaxHtml5Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, false);
	}

	@Test
	public void getAjaxHtml11Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, false);
	}

	@Test
	public void getAjaxHtml10Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);

		formField.setLookupList(idNameSql);

		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, true);
	}

	@Test
	public void getAjaxHtml12Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "TIMESTAMPTEST:test:test1",
				true, "", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		Mockito.when(lookupList.getBaseSQL()).thenReturn("baseSQL");
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		Mockito.when(idNameSql.getBaseWholeSQL()).thenReturn("testff_readonly");
		Mockito.when(idNameSql.getBaseWholeReadonlySQL()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		idNameSql.setSQL("test");
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		formField.getAjaxHtml("test", formValues, rr, true);

	}

	@Test
	public void getAjaxHtml9Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "HIDDEN", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);

		formField.setLookupList(idNameSql);

		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, true);
	}

	@Test
	public void getAjaxHtml6Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "validationType", false, "defaultValue",
				"helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL", "rangeEndDateSQL",
				"multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);

		formField.setLookupList(idNameSql);

		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, true);
	}

	@Test
	public void getAjaxHtml7Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "TEXTAREA", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);

		formField.setLookupList(idNameSql);

		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, true);
	}

	@Test
	public void getAjaxHtml8Test() throws Exception {
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "LIST_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);

		formField.setLookupList(idNameSql);

		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getAjaxHtml("test", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtmlTest() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		formField.getHtml("test", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml1Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "validationType", true,
				"defaultValue", "helpText", predefinedValues, true, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		formField.setLookupList(lookupList);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		formField.getHtml("test", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml2Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "validationType", true, "defaultValue",
				"helpText", predefinedValues, true, "dependsOn", null, null, "rangeStartDateSQL", "rangeEndDateSQL",
				"multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		formField.setLookupList(lookupList);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		formField.getHtml("test", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml3Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "TIMESTAMPTEST:test", true,
				"defaultValue", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		formField.getHtml("test", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml4Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "TIMESTAMPTEST:test:test1", true, "",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml5Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "DATE", true, "", "helpText",
				predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL", "selectrangeEndDateSQL",
				"multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml6Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "TIMESTAMP_HR", true, "", "helpText",
				predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL", "selectrangeEndDateSQL",
				"multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml7Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "TIMESTAMP_MIN", true, "", "helpText",
				predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL", "selectrangeEndDateSQL",
				"multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml8Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "TIMESTAMP_SEC", true, "", "helpText",
				predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL", "selectrangeEndDateSQL",
				"multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml9Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "TIMESTAMP_SEC", true, "", "helpText",
				predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL", "selectrangeEndDateSQL",
				"multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml10Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXTAREA", "TIMESTAMP_SEC", true, "",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml11Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXTAREA", "TIMESTAMP_SEC", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml14Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "TIMESTAMP_SEC", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml15Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "TIMESTAMP_SEC", false, "Test",
				"helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml13Test1() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_BOX", "TIMESTAMP_SEC", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml13Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_BOX", "TIMESTAMP_SEC", false, "Test",
				"helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml16Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "HIDDEN", "TIMESTAMP_SEC", false, "Test",
				"helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml17Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "RADIO_BTN", "TIMESTAMP_SEC", false, "Test",
				"helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml18Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "RADIO_BTN", "TIMESTAMP_SEC", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml20Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "CHECK_BOX", "TIMESTAMP_SEC", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		lookupList.add(lookupList);
		lookupList.resetNext();
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		formField.setLookupList(lookupList);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml23Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "CHECK_BOX", "TIMESTAMP_SEC", false, "Test",
				"helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		lookupList.add(lookupList);
		lookupList.resetNext();
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		formField.setLookupList(lookupList);
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml21Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "TIMESTAMP_SEC", false,
				"Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		lookupList.add(lookupList);
		lookupList.resetNext();
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		Mockito.when(idNameSql.getSql()).thenReturn("url");
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml22Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "TIMESTAMP_SEC", true,
				"Test", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		lookupList.add(lookupList);
		lookupList.resetNext();
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		Mockito.when(idNameSql.getSql()).thenReturn("url");
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHtml24Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "BLANK", "TIMESTAMP_SEC", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		IdNameList lookupList = PowerMockito.mock(IdNameList.class);
		lookupList.add(lookupList);
		lookupList.resetNext();
		IdNameSql idNameSql = PowerMockito.mock(IdNameSql.class);
		Mockito.when(idNameSql.getSql()).thenReturn("testff_readonly");
		lookupList.add(idNameSql);
		formField.setLookupList(idNameSql);
		Mockito.when(idNameSql.getSql()).thenReturn("url");
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		formValues.put("fieldDisplayName", "test");
		formValues.put("new", "");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getImgFolderURL()).thenReturn("test");
		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getString(0, 1)).thenReturn("dsTest");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, 0)).thenReturn("1990-11-11 11:11:11");
		Mockito.when(Globals.isScheduleDateParamAutoIncr()).thenReturn(false);
		formField.getHtml("", formValues, rr, true);
	}

	@Test
	public void getValidateJavaScriptTest() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "TIMESTAMP_SEC", true,
				"Test", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		PowerMockito.mockStatic(Globals.class);

		Mockito.when(Globals.getCalendarOutputDateFormat()).thenReturn("test");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript1Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "INTEGER", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript2Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "POSITIVE_INTEGER", true,
				"Test", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript3Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "NON_NEGATIVE_INTEGER", true,
				"Test", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript4Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "FLOAT", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.setHasPredefinedList(false);
		formField.setFieldDefaultSQL("test");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript5Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "POSITIVE_FLOAT", true,
				"Test", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript6Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT_WITH_POPUP", "NON_NEGATIVE_FLOAT", true,
				"Test", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript7Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXT", "NON_NEGATIVE_FLOAT", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript8Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "TEXTAREA", "NON_NEGATIVE_FLOAT", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript9Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "CHECK_BOX", "NON_NEGATIVE_FLOAT", true, "Test",
				"helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript10Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "CHECK_BOX", "NON_NEGATIVE_FLOAT", false,
				"Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript11Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT", true,
				"Test", "helpText", predefinedValues, true, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getValidateJavaScript12Test() throws Exception {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT",
				false, "Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		formField.getValidateJavaScript();
	}

	@Test
	public void getCallableAfterChainingJavascriptTest() {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT",
				false, "Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		JavascriptList list = new JavascriptList();
		List<JavascriptItemType> javalist = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("test");
		javascriptItemType.setCallText("afterchaining");
		javalist.add(javascriptItemType);
		list.setJavascriptItem(javalist);
		Mockito.when(rr.getJavascriptList()).thenReturn(list);
		formField.getCallableAfterChainingJavascript("test", rr);
	}

	@Test
	public void getCallableAfterChainingJavascript1Test() {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT",
				false, "Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		JavascriptList list = new JavascriptList();
		List<JavascriptItemType> javalist = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("test");
		javascriptItemType.setCallText("testafterchaining");
		javalist.add(javascriptItemType);
		list.setJavascriptItem(javalist);
		Mockito.when(rr.getJavascriptList()).thenReturn(list);
		formField.getCallableAfterChainingJavascript("test", rr);
	}

	@Test
	public void getCallableJavascriptTest() {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT",
				false, "Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		JavascriptList list = new JavascriptList();
		List<JavascriptItemType> javalist = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("test");
		javascriptItemType.setCallText("afterchaining");
		javalist.add(javascriptItemType);
		list.setJavascriptItem(javalist);
		Mockito.when(rr.getJavascriptList()).thenReturn(list);
		formField.getCallableJavascript("test", rr);
	}

	@Test
	public void getCallableJavascript1Test() {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT",
				false, "Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		JavascriptList list = new JavascriptList();
		List<JavascriptItemType> javalist = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("test");
		javascriptItemType.setCallText("testafterchaining");
		javalist.add(javascriptItemType);
		list.setJavascriptItem(javalist);
		Mockito.when(rr.getJavascriptList()).thenReturn(list);
		formField.getCallableJavascript("test", rr);
	}

	@Test
	public void getCallableOnChangeJavascriptTest() {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT",
				false, "Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		JavascriptList list = new JavascriptList();
		List<JavascriptItemType> javalist = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("test");
		javascriptItemType.setCallText("afterchaining");
		javalist.add(javascriptItemType);
		list.setJavascriptItem(javalist);
		Mockito.when(rr.getJavascriptList()).thenReturn(list);
		formField.getCallableOnChangeJavascript("test", rr);
	}

	@Test
	public void getCallableOnChangeJavascript1Test() {
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "LIST_MULTI_SELECT", "NON_NEGATIVE_FLOAT",
				false, "Test", "helpText", predefinedValues, false, "dependsOn", null, null, "selectrangeStartDateSQL",
				"selectrangeEndDateSQL", "multiSelectListSize");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		JavascriptList list = new JavascriptList();
		List<JavascriptItemType> javalist = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("test");
		javascriptItemType.setCallText("testafterchainingonchange.");
		javalist.add(javascriptItemType);
		list.setJavascriptItem(javalist);
		Mockito.when(rr.getJavascriptList()).thenReturn(list);
		formField.getCallableOnChangeJavascript("test", rr);
	}
}
