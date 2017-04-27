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
 package org.openecomp.portalsdk.analytics.controller;

import java.util.*;

import org.openecomp.portalsdk.analytics.model.base.*;
import org.openecomp.portalsdk.analytics.model.definition.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;

public class WizardSequence extends Vector {
	// private String currentStep = AppConstants.WS_DEFINITION;
	private int currentStepIdx = 0;

	private String currentSubStep = "";

	private int nextElemIdx = 0;

	public void resetNext() {
		resetNext(0);
	} // resetNext

	public void resetNext(int toPos) {
		nextElemIdx = toPos;
	} // resetNext

	public boolean hasNext() {
		return (nextElemIdx < size());
	} // hasNext

	public String getNext() {
		return hasNext() ? getStep(nextElemIdx++) : null;
	} // getNext

	// *****************************************************

	public WizardSequence() {
		add(AppConstants.WS_DEFINITION);
	} // WizardSequence

	private String getStep(int index) {
		return (String) get(index);
	} // getStep

	private int getStepIndex(String step) {
		for (int i = 0; i < size(); i++)
			if (getStep(i).equals(step))
				return i;

		throw new IndexOutOfBoundsException();
	} // getStepIndex

	/*
	 * private String getInitialStep() { return getStep(0); } // getInitialStep
	 * 
	 * private String getFinalStep() { return getStep(getStepCount()-1); } //
	 * getFinalStep
	 */
	private boolean isInitialStep(int index) {
		return (index == 0);
	} // isInitialStep

	/*
	 * private boolean isInitialStep(String step) { return
	 * isInitialStep(getStepIndex(step)); } // isInitialStep
	 */
	private boolean isFinalStep(int index) {
		if (index == 0)
			return false;

		return (index == (getStepCount() - 1));
	} // isFinalStep

	/*
	 * private boolean isFinalStep(String step) { return
	 * isFinalStep(getStepIndex(step)); } // isFinalStep
	 */

	private int getNextStepIndex(int index) {
		return (index == (getStepCount() - 1)) ? index : (index + 1);
	} // getNextStep

	/*
	 * private String getNextStep(String step) { return
	 * getStep(getNextStepIndex(getStepIndex(step))); } // getNextStep
	 * 
	 * private String getNextStep(String step, String subStep) {
	 * if(subStep.length()>0) return step;
	 * 
	 * return getNextStep(step); } // getNextStep
	 */
	private int getPrevStepIndex(int index) {
		return (index == 0) ? index : (index - 1);
	} // getPrevStepIndex

	/*
	 * private String getPrevStep(String step) { return
	 * getStep(getPrevStepIndex(getStepIndex(step))); } // getPrevStep
	 * 
	 * private String getPrevStep(String step, String subStep) {
	 * if(subStep.length()>0) return step;
	 * 
	 * return getPrevStep(step); } // getPrevStep
	 */
	// *****************************************************
	public int getStepCount() {
		return size();
	} // getStepCount

	public int getCurrentStepIndex() {
		return currentStepIdx + 1;
	} // getCurrentStepIndex

	public String getCurrentStep() {
		return getStep(currentStepIdx);
	} // getCurrentStep

	public String getCurrentSubStep() {
		return currentSubStep;
	} // getCurrentSubStep

	public boolean isInitialStep() {
		return isInitialStep(currentStepIdx);
	} // isInitialStep

	public boolean isFinalStep() {
		return isFinalStep(currentStepIdx);
	} // isFinalStep

	public void performAction(String action, ReportDefinition rdef) {
		if (action.equals(AppConstants.WA_BACK))
			if (currentSubStep.length() > 0)
				currentSubStep = "";
			else
				currentStepIdx = getPrevStepIndex(currentStepIdx);
		else if (action.equals(AppConstants.WA_NEXT)) {
			if (currentSubStep.length() > 0)
				currentSubStep = "";
			else {
				currentStepIdx = getNextStepIndex(currentStepIdx);
				if (rdef != null)
					if (!rdef.getReportDefType().equals(AppConstants.RD_SQL_BASED))
						if (getCurrentStep().equals(AppConstants.WS_TABLES)
								&& (rdef.getDataSourceList().getDataSource().size() == 0))
							currentSubStep = AppConstants.WSS_ADD;
						else if (getCurrentStep().equals(AppConstants.WS_COLUMNS)
								&& (rdef.getAllColumns().size() == 0))
							currentSubStep = (rdef.getReportType().equals(
									AppConstants.RT_CROSSTAB) ? AppConstants.WSS_ADD
									: AppConstants.WSS_ADD_MULTI);
			}
		} else if (action.equals(AppConstants.WA_EDIT) || action.equals(AppConstants.WA_ADD)
				|| action.equals(AppConstants.WA_ADD_MULTI)
				|| action.equals(AppConstants.WA_ORDER_ALL)|| action.equals(AppConstants.WSS_ADD_BLANK) || action.equals(AppConstants.WA_MODIFY)) {
			currentSubStep = action;
		}
		else if (currentSubStep.equals(AppConstants.WSS_ADD)
				|| currentSubStep.equals(AppConstants.WSS_EDIT))
			currentSubStep = AppConstants.WSS_EDIT;
		else
			currentSubStep = "";
	} // performAction

	public void performGoToStep(String step) {
		int stepIdx = getStepIndex(step);

		if (stepIdx >= 0 && stepIdx < getStepCount()) {
			currentStepIdx = stepIdx;
			currentSubStep = "";
		}
	} // performGoToStep

} // WizardSequence
