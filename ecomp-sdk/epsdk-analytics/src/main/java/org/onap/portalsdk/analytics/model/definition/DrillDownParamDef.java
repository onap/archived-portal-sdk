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
package org.onap.portalsdk.analytics.model.definition;

import org.onap.portalsdk.analytics.RaptorObject;

public class DrillDownParamDef extends RaptorObject {
	private String fieldName = "";

	private String valType = "0";

	private String valValue = "";

	private String valColId = "";

	private String valFieldId = "";

	public DrillDownParamDef(String drillDownParamStr) {
		super();

		drillDownParamStr = nvl(drillDownParamStr).trim();
		if (drillDownParamStr.indexOf('=') >= 0) {
			fieldName = drillDownParamStr.substring(0, drillDownParamStr.indexOf('='));

			if (drillDownParamStr.length() > drillDownParamStr.indexOf('=') + 2
					&& drillDownParamStr.charAt(drillDownParamStr.indexOf('=') + 1) == '['
					&& drillDownParamStr.charAt(drillDownParamStr.length() - 1) == ']') {
				drillDownParamStr = drillDownParamStr.substring(
						drillDownParamStr.indexOf('=') + 2, drillDownParamStr.length() - 1);

				if (drillDownParamStr.indexOf('!') < 0)
					valColId = drillDownParamStr;
				else if (drillDownParamStr.indexOf('!') == 0)
					valFieldId = drillDownParamStr.substring(1);
				else {
					valColId = drillDownParamStr.substring(0, drillDownParamStr.indexOf('!'));
					valFieldId = drillDownParamStr
							.substring(drillDownParamStr.indexOf('!') + 1);
				} // else

				if (valColId.length() > 0 && valFieldId.length() > 0)
					valType = "4";
				else if (valFieldId.length() > 0)
					valType = "3";
				else if (valColId.length() > 0)
					valType = "2";
			} else {
				valType = "1";
				valValue = drillDownParamStr.substring(drillDownParamStr.indexOf('=') + 1);
			} // else
		} // if
	} // DrillDownParamDef

	public String getFieldName() {
		return fieldName;
	}

	public String getValType() {
		return valType;
	}

	public String getValValue() {
		return valValue;
	}

	public String getValColId() {
		return valColId;
	}

	public String getValFieldId() {
		return valFieldId;
	}

	private void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	private void setValType(String valType) {
		this.valType = valType;
	}

	private void setValValue(String valValue) {
		this.valValue = valValue;
	}

	private void setValColId(String valColId) {
		this.valColId = valColId;
	}

	private void setValFieldId(String valFieldId) {
		this.valFieldId = valFieldId;
	}

} // DrillDownParamDef
