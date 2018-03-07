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

package org.onap.portalsdk.analytics.controller;


import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.system.IAppUtils;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ WizardProcessor.class, AppUtils.class, Globals.class, DbUtils.class})
public class WizardProcessorTest {

	WizardProcessor wizardProcessor;
	
	@Mock
	HttpServletRequest httpServletRequest;
	
	@Mock
	HttpSession httpSession;

	@Mock
	IAppUtils iAppUtils;
	
	@Mock
	ReportRuntime reportRuntime;

	@Mock 
	ReportHandler reportHandler;
	
	@Mock
	ReportDefinition reportDefinition;

	@Mock
	WizardSequence wizardSequence;
	
	
	@Before
    public void init() throws Exception {
				
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);
						
		MockitoAnnotations.initMocks(this);

		PowerMockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
 	    PowerMockito.when(AppUtils.getImgFolderURL()).thenReturn("http://sometesturl:9090/hi");
 	    PowerMockito.when(httpServletRequest.getSession()).thenReturn(httpSession);
  	
 	    PowerMockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
 	    
 		PowerMockito.when(AppUtils.nvl(Mockito.anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      String inputString = (String) args[0];
					return (inputString == null) ? "" : inputString;
			}
		} );
		
		PowerMockito.when(AppUtils.getRequestNvlValue(Mockito.anyObject(), Mockito.anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      HttpServletRequest request = (HttpServletRequest) args[0];
			      String valueID = (String) args[1];
			      String value = (String) request.getAttribute(valueID);

			        /**
					if (value == null)
						value = ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(), request.getParameter(valueID));
						***/

					return (value == null) ? "" : value;
			}
		} );
	
		
		PowerMockito.when(AppUtils.getRequestValue(Mockito.anyObject(), Mockito.anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      HttpServletRequest request = (HttpServletRequest) args[0];
			      String valueID = (String) args[1];
			      String value = (String) request.getAttribute(valueID);

			      /***
					if (value == null)
						value = ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(), request.getParameter(valueID));
				   ***/
					return value;
			}
		} );
		
		
		
		
		wizardProcessor = new WizardProcessor();
	}

	
	@Test
	public void testWizardProcessor() {
		WizardProcessor wizardProcessorLocal = new WizardProcessor();
				
		assertNotNull(wizardProcessorLocal);
	}

	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments() throws Exception {
		wizardProcessor.persistReportDefinition(null, null);
	}

	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments_case1() throws Exception {
		wizardProcessor.persistReportDefinition(httpServletRequest, null);
	}

	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments_case2() throws Exception {
		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(reportRuntime);
		wizardProcessor.persistReportDefinition(httpServletRequest, null);
	}

	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments_case3() throws Exception {
		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(reportRuntime);
		PowerMockito.when(reportRuntime.getReportID()).thenReturn("Report#1");
		
		wizardProcessor.persistReportDefinition(httpServletRequest, null);
	}

	@Test
	public void testPersistReportDefinition_not_null_arguments_case1() throws Exception {
		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(reportRuntime);
		Mockito.when(reportRuntime.getReportID()).thenReturn("Report#1");
		wizardProcessor.persistReportDefinition(httpServletRequest, reportDefinition);
	}
	
	@Test
	public void testPersistReportDefinition_not_null_arguments_case2() throws Exception {
		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(reportRuntime);
		Mockito.when(reportRuntime.getReportID()).thenReturn("Report#1");
		Mockito.when(reportDefinition.getReportID()).thenReturn("Report#1");

		wizardProcessor.persistReportDefinition(httpServletRequest, reportDefinition);
	}
	
	@Test(expected=NullPointerException.class)
	public void testProcessWizardStep_null_arguments_case1() throws Exception {
		wizardProcessor.processWizardStep(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testProcessWizardStep_not_null_arguments_case1() throws Exception {
		wizardProcessor.processWizardStep(httpServletRequest);
	}
	
	@Test(expected=NullPointerException.class)
	public void testProcessWizardStep_not_null_arguments_case2() throws Exception {
		Mockito.when(httpServletRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn(null);
		wizardProcessor.processWizardStep(httpServletRequest);
	}

	@Test(expected=NullPointerException.class)
	public void testProcessWizardStep_not_null_arguments_case3() throws Exception {
		Mockito.when(httpServletRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("NA");
		wizardProcessor.processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_not_null_arguments_case4() throws Exception {
		PowerMockito.whenNew(ReportHandler.class).withNoArguments().thenReturn(reportHandler);	
		Mockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_REPORT_ID)).thenReturn("Report#1");
		Mockito.when(reportHandler.loadReportDefinition(httpServletRequest,"Report#1")).thenReturn(reportDefinition);
		Mockito.when(httpServletRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("NA");
		Mockito.when(AppUtils.getRequestNvlValue(httpServletRequest, "showDashboardOptions")).thenReturn("");
		Mockito.when(reportDefinition.getWizardSequence()).thenReturn(wizardSequence);
		Mockito.when(wizardSequence.getCurrentStep()).thenReturn("NA");
		Mockito.when(wizardSequence.getCurrentSubStep()).thenReturn("NA");
		wizardProcessor.processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_not_null_arguments_case5() throws Exception {
		PowerMockito.whenNew(ReportHandler.class).withNoArguments().thenReturn(reportHandler);	
		Mockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_REPORT_ID)).thenReturn("Report#1");
		Mockito.when(AppUtils.getRequestNvlValue(httpServletRequest, "showDashboardOptions")).thenReturn("");
	
		Mockito.when(AppUtils.getRequestNvlValue(httpServletRequest, "reportType")).thenReturn(AppConstants.RT_DASHBOARD);
		Mockito.when(reportHandler.loadReportDefinition(httpServletRequest,"Report#1")).thenReturn(reportDefinition);
		
		Mockito.when(httpServletRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("NA");
		Mockito.when(reportDefinition.getWizardSequence()).thenReturn(wizardSequence);
		Mockito.when(wizardSequence.getCurrentStep()).thenReturn(AppConstants.WS_DEFINITION);
		Mockito.when(wizardSequence.getCurrentSubStep()).thenReturn("NA");
		
		Mockito.when(reportDefinition.getReportID()).thenReturn("1");
		
		wizardProcessor.processWizardStep(httpServletRequest);
	}
	
	
	/***

	@Test
	public void testProcessImportSemaphorePopup() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessSemaphorePopup() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessAdhocSchedule() {
		fail("Not yet implemented");
	}

*/}
