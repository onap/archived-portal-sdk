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
package org.openecomp.portalsdk.analytics.system.fusion.domain;


import java.util.Date;

import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.domain.support.DomainVo;;

/**
 * <p>CR_Report.java</p>
 * <p>Represents a RAPTOR report data object.</p>
 *
 * @version 1.0
 */
public class CR_Report extends DomainVo {

    private String title;
    private String descr;
    private String public_yn;
    //private String report_xml;
    private Date   createDate;
    private Date   maintDate;
    private String menuId;
    private String menuApproved_YN;
    private User  ownerId;
    private Long   folderId;
    private String dashboard_type_YN;
    private String dashboard_yn;
    private User createId;
    private User maintId;

/*
    //New Buttons
    private String copyImagePath = "/static/fusion/raptor/img/cross-small.png" ; 
    private String editImagePath = "/static/fusion/raptor/img/pencil-small.png" ; 
    private String deleteImagePath = "/static/fusion/raptor/img/DeleteCross-16x16.png" ; 
    private String scheduleImagePath = "/static/fusion/raptor/img/Calendar-16x16.png" ; 
    private String runImagePath = "/static/fusion/raptor/img/tick-small.png" ; 
    
*/  
    //private Set     reportAccess  = new TreeSet();

    public CR_Report() {}

    

    /**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}



	/**
	 * @param title the title to set
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
	 * @param descr the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}



	/**
	 * @return the public_yn
	 */
	public String getPublic_yn() {
		return public_yn;
	}



	/**
	 * @param public_yn the public_yn to set
	 */
	public void setPublic_yn(String public_yn) {
		this.public_yn = public_yn;
	}


	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}



	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	/**
	 * @return the maintDate
	 */
	public Date getMaintDate() {
		return maintDate;
	}



	/**
	 * @param maintDate the maintDate to set
	 */
	public void setMaintDate(Date maintDate) {
		this.maintDate = maintDate;
	}



	/**
	 * @return the menuId
	 */
	public String getMenuId() {
		return menuId;
	}



	/**
	 * @param menuId the menuId to set
	 */
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}



	/**
	 * @return the menuApproved_YN
	 */
	public String getMenuApproved_YN() {
		return menuApproved_YN;
	}



	/**
	 * @param menuApproved_YN the menuApproved_YN to set
	 */
	public void setMenuApproved_YN(String menuApproved_YN) {
		this.menuApproved_YN = menuApproved_YN;
	}




	/**
	 * @return the folderId
	 */
	public Long getFolderId() {
		return folderId;
	}



	/**
	 * @param folderId the folderId to set
	 */
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}



	/**
	 * @return the dashboard_type_YN
	 */
	public String getDashboard_type_YN() {
		return dashboard_type_YN;
	}



	/**
	 * @param dashboard_type_YN the dashboard_type_YN to set
	 */
	public void setDashboard_type_YN(String dashboard_type_YN) {
		this.dashboard_type_YN = dashboard_type_YN;
	}



	/**
	 * @return the dashboard_yn
	 */
	public String getDashboard_yn() {
		return dashboard_yn;
	}



	/**
	 * @param dashboard_yn the dashboard_yn to set
	 */
	public void setDashboard_yn(String dashboard_yn) {
		this.dashboard_yn = dashboard_yn;
	}



	/**
	 * @return the ownerId
	 */
	public User getOwnerId() {
		return ownerId;
	}



	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(User ownerId) {
		this.ownerId = ownerId;
	}



	/**
	 * @return the createId
	 */
	public User getCreateId() {
		return createId;
	}



	/**
	 * @param createId the createId to set
	 */
	public void setCreateId(User createId) {
		this.createId = createId;
	}



	/**
	 * @return the maintId
	 */
	public User getMaintId() {
		return maintId;
	}



	/**
	 * @param maintId the maintId to set
	 */
	public void setMaintId(User maintId) {
		this.maintId = maintId;
	}



	public int compareTo(Object obj){
      String c1 = getTitle();
      String c2 = ((CR_Report)obj).getTitle();

      return (c1 == null || c2 == null) ? 1 : c1.compareTo(c2);
    }

}
