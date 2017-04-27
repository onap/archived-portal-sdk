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

import java.util.*;

import org.openecomp.portalsdk.analytics.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;

public class VisualManager extends RaptorObject {
	private HashMap hiddenCols = new HashMap();

	private String sortByColId = "";

	private String sortByAscDesc = AppConstants.SO_ASC;

	public VisualManager() {
		super();
	}

	public void hideColumn(String colId) {
		hiddenCols.put(colId, "Y");
	} // hideColumn

	public void showColumn(String colId) {
		hiddenCols.put(colId, "N");
	} // showColumn

	public boolean isColumnVisible(String colId) {
		return nvl((String) hiddenCols.get(colId), "N").equals("N");
	} // isColumnVisible

	public void setSortByColumn(String colId) {
		if (sortByColId.equals(colId))
			sortByAscDesc = sortByAscDesc.equals(AppConstants.SO_ASC) ? AppConstants.SO_DESC
					: AppConstants.SO_ASC;
		else {
			sortByColId = colId;
			sortByAscDesc = AppConstants.SO_ASC;
		}
	} // setSortByColumn

	public String getSortByColId() {
		return sortByColId;
	}

	public String getSortByAscDesc() {
		return sortByAscDesc;
	}

} // VisualManager
