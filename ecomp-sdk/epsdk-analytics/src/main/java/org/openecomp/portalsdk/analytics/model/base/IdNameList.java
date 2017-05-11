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
package org.openecomp.portalsdk.analytics.model.base;

import java.util.*;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.system.*;

public class IdNameList extends Vector {
	protected int pageNo = -1;

	protected int pageSize = 50;

	private int nextElemIdx = 0;
	
	private String oldSql = null;

	public IdNameList() {
		super();
		pageSize = Globals.getFormFieldsListSize();
	} // IdNameList

	public int getPageNo() {
		return pageNo;
	} // getPageNo

	public int getPageSize() {
		return pageSize;
	} // getPageSize

	public int getDataSize() {
		return size();
	} // getDataSize

	public void resetNext() {
		resetNext(0);
	} // resetNext

	public void resetNext(int toPos) {
		nextElemIdx = toPos;
	} // resetNext

	public boolean hasNext() {
		return (nextElemIdx < size());
	} // hasNext

	public IdNameValue getNext() {
		return hasNext() ? getValue(nextElemIdx++) : null;
	} // getNext

	public int getCount() {
		return size();
	} // getCount

	public IdNameValue getValue(int idx) {
		return (IdNameValue) get(idx);
	} // getValue

	public void addValue(IdNameValue value) {
		add(value);
	} // addValue

	public void addValue(String id, String name, boolean defaultValue) {
		addValue(new IdNameValue(id, name, defaultValue));
	} // addValue

	public void addValue(String id, String name, boolean defaultValue, boolean readOnly) {
		addValue(new IdNameValue(id, name, defaultValue, readOnly));
	} // addValue
	
	public void addValue(String id, String name) {
		addValue(new IdNameValue(id, name));
	} // addValue

	public void addValue(int idx, IdNameValue value) {
		add(idx, value);
	} // addValue

	public void addValue(int idx, String id, String name) {
		addValue(idx, new IdNameValue(id, name));
	} // addValue

	public String getNameById(String id) {
		for (int i = 0; i < size(); i++) {
			IdNameValue value = getValue(i);
			if (value.getId().equals(id))
				return value.getName();
		} // for

		return null;
	} // getNameById

	public String getIdByName(String name) {
		for (int i = 0; i < size(); i++) {
			IdNameValue value = getValue(i);
			if (value.getName().equals(name))
				return value.getId();
		} // for

		return null;
	} // getIdByName

	public boolean canUseSearchString() {
		return true;
	}

	public String getBaseSQL() {
		return null;
	}

	public String getOldSql() {
		return oldSql;
	}

	public void setOldSql(String oldSql) {
		this.oldSql = oldSql;
	}
	public String getBaseWholeSQL() {
		return null;
	}
	
	public String getBaseWholeReadonlySQL() {
		return null;
	}
	
	public String getBaseSQLForPDFExcel(boolean multiParam) {
		return null;
	}
	
	public void clearData() {
	}

	public void loadData(String pageNo, String searchString, String dbInfo,String userId) throws RaptorException {}
	public void loadUserData(String pageNo, String searchString, String dbInfo,String userId) throws RaptorException {}
    public void loadUserData(int pageNo, String searchString, String dbInfo, String userId) throws RaptorException {}
    public void loadUserData(String searchString, int pageNo,  String dbInfo) throws RaptorException {}
	
    public void loadData(String pageNo) throws RaptorException {}
	public void loadData(int pageNo) throws RaptorException {}  
	public void loadData(String pageNo, String searchString, String dbInfo) throws RaptorException {}
	private void loadData(int pageNo, String searchString, String dbInfo) throws RaptorException {}	

/*    
	public void loadData(int pageNo, String dbInfo) throws RaptorException {
	}

    public void loadUserData(int pageNo, String dbInfo, String userId) throws RaptorException {
    }




	public void loadData(String pageNo, String searchString) throws RaptorException {
	}

*/
	protected static String nvl(String s) {
		return (s == null) ? "" : s;
	}

	protected static String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}

} // IdNameList
