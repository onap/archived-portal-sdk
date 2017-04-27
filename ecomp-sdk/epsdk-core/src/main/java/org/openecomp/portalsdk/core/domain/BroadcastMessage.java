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
package org.openecomp.portalsdk.core.domain;

import java.util.*;

import org.openecomp.portalsdk.core.domain.support.DomainVo;

public class BroadcastMessage extends DomainVo {
	
	/**
	 * 
	 */
	
  private static final long serialVersionUID = 1L;
  public BroadcastMessage() {
  }

  public static final String ID_MESSAGE_LOCATION_LOGIN   = "10";
  public static final String ID_MESSAGE_LOCATION_WELCOME = "20";

  private String  messageText;
  private Integer locationId;
  private Date    startDate;
  private Date    endDate;
  private Integer sortOrder;
  private Boolean active;
  private String siteCd;

  public Boolean getActive() {
      return active;
  }

  public Date getEndDate() {
      return endDate;
  }

  public Integer getLocationId() {
      return locationId;
  }

  public String getMessageText() {
      return messageText;
  }

  public Integer getSortOrder() {
      return sortOrder;
  }

  public Date getStartDate() {
      return startDate;
  }

    public String getSiteCd() {
        return siteCd;
    }


    public void setActive(Boolean active) {
      this.active = active;
  }

  public void setEndDate(Date endDate) {
      this.endDate = endDate;
  }

  public void setLocationId(Integer locationId) {
      this.locationId = locationId;
  }

  public void setMessageText(String messageText) {
      this.messageText = messageText;
  }

  public void setSortOrder(Integer sortOrder) {
      this.sortOrder = sortOrder;
  }

  public void setStartDate(Date startDate) {
      this.startDate = startDate;
  }

  public void setSiteCd(String siteCd) {
      this.siteCd = siteCd;
  }


  public int compareTo(Object obj){
    Integer c1 = getLocationId();
    Integer c2 = ((BroadcastMessage)obj).getLocationId();

    if (c1.compareTo(c2) == 0) {
      c1 = getSortOrder();
      c2 = ((BroadcastMessage)obj).getSortOrder();

      if (c1.compareTo(c2) == 0) {
        Long c3 = getId();
        Long c4 = ((BroadcastMessage)obj).getId();

        return c3.compareTo(c4);
      }
    }

    return c1.compareTo(c2);
  }

}
