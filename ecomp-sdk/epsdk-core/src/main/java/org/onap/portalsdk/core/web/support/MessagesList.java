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
package org.onap.portalsdk.core.web.support;

import java.util.ArrayList;
import java.util.List;

public class MessagesList {

	private boolean successMessageDisplayed = true;
	private boolean includeCauseInCustomExceptions = false;

	private List successMessages;
	private List exceptionMessages;

	public MessagesList() {
		setExceptionMessages(new ArrayList());
		setSuccessMessages(new ArrayList());
	}

	public MessagesList(boolean displaySuccess) {
		this();
		setSuccessMessageDisplayed(displaySuccess);
	}

	public List getExceptionMessages() {
		return exceptionMessages;
	}

	public List getSuccessMessages() {
		return successMessages;
	}

	public boolean isSuccessMessageDisplayed() {
		return successMessageDisplayed;
	}

	public boolean isIncludeCauseInCustomExceptions() {
		return includeCauseInCustomExceptions;
	}

	public void setExceptionMessages(List exceptionMessages) {
		this.exceptionMessages = exceptionMessages;
	}

	public void setSuccessMessages(List successMessages) {
		this.successMessages = successMessages;
	}

	public void setSuccessMessageDisplayed(boolean successMessageDisplayed) {
		this.successMessageDisplayed = successMessageDisplayed;
	}

	public void setIncludeCauseInCustomExceptions(boolean includeCauseInCustomExceptions) {
		this.includeCauseInCustomExceptions = includeCauseInCustomExceptions;
	}

	public void addSuccessMessage(FeedbackMessage message) {
		getSuccessMessages().add(message);
	}

	public void addExceptionMessage(FeedbackMessage message) {
		getExceptionMessages().add(message);
	}

	public boolean hasExceptionMessages() {
		return !getExceptionMessages().isEmpty();
	}

	public boolean hasSuccessMessages() {
		return !getSuccessMessages().isEmpty();
	}

}
