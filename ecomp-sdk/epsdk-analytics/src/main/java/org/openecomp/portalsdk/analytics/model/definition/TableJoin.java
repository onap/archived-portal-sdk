/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalsdk.analytics.model.definition;

import org.openecomp.portalsdk.analytics.RaptorObject;

public class TableJoin extends RaptorObject {
	private String srcTableName = null;

	private String destTableName = null;

	private String joinExpr = null;

	public TableJoin() {
		super();
	}

	public TableJoin(String srcTableName, String destTableName, String joinExpr) {
		this();

		setSrcTableName(srcTableName);
		setDestTableName(destTableName);
		setJoinExpr(joinExpr);
	} // TableJoin

	public String getSrcTableName() {
		return srcTableName;
	}

	public String getDestTableName() {
		return destTableName;
	}

	public String getJoinExpr() {
		return joinExpr;
	}

	public void setSrcTableName(String srcTableName) {
		this.srcTableName = srcTableName;
	}

	public void setDestTableName(String destTableName) {
		this.destTableName = destTableName;
	}

	public void setJoinExpr(String joinExpr) {
		this.joinExpr = joinExpr;
	}

} // TableJoin
