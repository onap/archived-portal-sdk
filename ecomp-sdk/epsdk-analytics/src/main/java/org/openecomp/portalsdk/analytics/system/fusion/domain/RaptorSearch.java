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
package org.openecomp.portalsdk.analytics.system.fusion.domain;

import java.util.Date;

import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.core.domain.support.DomainVo;;

public class RaptorSearch extends DomainVo {

	private Long repId;
	private String title;
	private String descr;
	private String ownerName;
	private Date createDate;
	private String canEdit;
	private String readOnly;
	private String schedule;
	// private Long countRows;

	/**
	 * @return the repId
	 */
	public Long getRepId() {
		return repId;
	}

	/**
	 * @param repId
	 *            the repId to set
	 */
	public void setRepId(Long repId) {
		this.repId = repId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * @param descr
	 *            the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate
	 *            the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName
	 *            the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * @return the schedule
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule
	 *            the schedule to set
	 */
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the canEdit
	 */
	public String getCanEdit() {
		return canEdit;
	}

	/**
	 * @param canEdit
	 *            the canEdit to set
	 */
	public void setCanEdit(String canEdit) {
		this.canEdit = canEdit;
	}

	/**
	 * @return the canDelete
	 */
	public boolean canDelete() {
		String s = getCanEdit();
		if (s != null && s.length() > 0 && s.equals("Y")) {
			return true;
		}
		return false;
	}

	/**
	 * @return the canCopy
	 */
	public boolean canCopy() {
		String s = getReadOnly();
		if (returnTrueOrFalse(s)) {
			return Globals.getCanCopyOnReadOnly() ? true : returnTrueOrFalse(getCanEdit());
		}
		return true;
	}

	/**
	 * @return the readOnly
	 */
	public String getReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly
	 *            the readOnly to set
	 */
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}

	private boolean returnTrueOrFalse(String s) {
		if (s != null && s.length() > 0 && s.equals("Y")) {
			return true;
		}
		return false;
	}

}
