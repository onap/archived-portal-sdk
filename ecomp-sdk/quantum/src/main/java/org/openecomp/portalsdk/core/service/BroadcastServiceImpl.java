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
package org.openecomp.portalsdk.core.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.BroadcastMessage;
import org.openecomp.portalsdk.core.domain.Lookup;
import org.openecomp.portalsdk.core.service.support.FusionService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

@SuppressWarnings("rawtypes")
@Service("broadcastService")
@Transactional
public class BroadcastServiceImpl extends FusionService implements BroadcastService {
  
  public BroadcastServiceImpl() {
  }

  @Autowired
  private DataAccessService  dataAccessService;
  private static Hashtable         broadcastMessages = new Hashtable();

  @SuppressWarnings("unchecked")
  public void loadMessages() {
    List messageLocations = AppUtils.getLookupListNoCache("fn_lu_message_location", "message_location_id", "message_location_descr", "", "message_location_id");

    for (int i=0; i < messageLocations.size(); i++) {
      Lookup location   = (Lookup)messageLocations.get(i);
      String locationId = location.getValue();

      broadcastMessages.put(locationId, getPersistedBroadcastMessages(locationId));
    }
  }
  
  @SuppressWarnings("unchecked")
  public HashMap getBcModel(HttpServletRequest request){
		HashMap    bcModel = new HashMap();

        List   items     = null;
        int    messageId = ServletRequestUtils.getIntParameter(request, "message_id", 0);
        String task      = ServletRequestUtils.getStringParameter(request, "task", "get");

        // delete or toggle activation on the selected record (if applicable)
        if (messageId != 0 && (task.equals("delete") || task.equals("toggleActive"))) {
          BroadcastMessage message = (BroadcastMessage)getDataAccessService().getDomainObject(BroadcastMessage.class, new Long(messageId), null);

          if (task.equals("delete")) {
            getDataAccessService().deleteDomainObject(message, null);
          }
          else if (task.equals("toggleActive")) {
            HashMap  additionalParams = new HashMap();
            additionalParams.put(Parameters.PARAM_HTTP_REQUEST, request);

            message.setActive(new Boolean(!message.getActive().booleanValue()));
            getDataAccessService().saveDomainObject(message, additionalParams);
          }
          loadMessages();
        }

        items = getDataAccessService().getList(BroadcastMessage.class, null);
        Collections.sort(items);
        bcModel.put("messagesList", packageMessages(items));

        List locations = AppUtils.getLookupList("fn_lu_message_location", "message_location_id", "message_location_descr", "", "message_location_id");
        bcModel.put("messageLocations", locations);

        if ("true".equals(SystemProperties.getProperty(SystemProperties.CLUSTERED))) {
          List sites = AppUtils.getLookupList("fn_lu_broadcast_site", "broadcast_site_cd", "broadcast_site_descr", "", "broadcast_site_descr");
          bcModel.put("broadcastSites", sites);
        }
        
        return bcModel;
  }
  
  @SuppressWarnings("unchecked")
  private HashMap packageMessages(List messages) {
      HashMap messagesList     = new HashMap();
      Set     locationMessages = null;

      Integer previousLocationId = null;

      for (int i=0; i < messages.size(); i++) {
        BroadcastMessage message = (BroadcastMessage)messages.get(i);

        if (!message.getLocationId().equals(previousLocationId)) {
          if (previousLocationId != null) {
            messagesList.put(previousLocationId.toString(), locationMessages);
          }

          locationMessages = new TreeSet();
          previousLocationId = message.getLocationId();
        }

        locationMessages.add(message);
      }

      if (previousLocationId != null) {
        messagesList.put(previousLocationId.toString(), locationMessages);
      }

      return messagesList;
  }


  @SuppressWarnings("unchecked")
  private List getPersistedBroadcastMessages(String locationId) {
	  
    HashMap params = new HashMap();
    params.put("location_id", new Integer(locationId));
    Calendar calInstanceToday = Calendar.getInstance();
    calInstanceToday.set(Calendar.HOUR, 0);
    calInstanceToday.set(Calendar.MINUTE, 0);
    calInstanceToday.set(Calendar.SECOND, 0);
    params.put("today_date", calInstanceToday.getTime());
  
    return getDataAccessService().executeNamedQuery("broadcastMessages", params, null);
  }

  public Hashtable getBroadcastMessages() {
    return broadcastMessages;
  }

  public static List getBroadcastMessages(String locationId) {
    return (List)broadcastMessages.get(locationId);
  }

  public static String displayMessages(String locationId) {
    return displayServerMessages(locationId, null);
  }

  public static String displayServerMessages(String locationId, String siteCd) {
    StringBuffer html = new StringBuffer();

    List messages = getBroadcastMessages(locationId);

    for (int i=0; i < messages.size(); i++) {
      BroadcastMessage message = (BroadcastMessage)messages.get(i);
      
      if ((message.getSiteCd() == null) || ((message.getSiteCd() != null) && message.getSiteCd().equals(siteCd))) {
        html.append("<li class=\"broadcastMessage\">")
            .append(message.getMessageText());
      }
    }

    if (html.length() > 0) {
      html.insert(0, "<ul class=\"broadcastMessageList\">");
      html.append("</ul>");
    }

    return html.toString();
  }

  public static boolean hasMessages(String locationId) {
    return hasServerMessages(locationId, null);
  }

  public static boolean hasServerMessages(String locationId, String siteCd) {
    List messages = getBroadcastMessages(locationId);

    boolean     messagesExist = !((messages == null) || messages.size() == 0);

    if (siteCd == null) {
      return messagesExist;
    }
    else {
      for (int i=0; i < messages.size(); i++) {
        BroadcastMessage message = (BroadcastMessage)messages.get(i);
         
        if ((message.getSiteCd() == null) || message.getSiteCd().equals(siteCd)) {
          return true;
        }
      }
      return false;
    }
  }

  public DataAccessService getDataAccessService() {
	return dataAccessService;
  }

  public void setDataAccessService(DataAccessService dataAccessService) {
	this.dataAccessService = dataAccessService;
  }

  public BroadcastMessage getBroadcastMessage(HttpServletRequest request) {
      long messageId = ServletRequestUtils.getLongParameter(request, "message_id", 0);

      BroadcastMessage message = new BroadcastMessage();
      if(messageId!=0)
    	  message = (BroadcastMessage)getDataAccessService().getDomainObject(BroadcastMessage.class, new Long(messageId), null);

      if (message.getLocationId() == null) {
        try {
			message.setLocationId(new Integer(ServletRequestUtils.getStringParameter(request, "message_location_id")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ServletRequestBindingException e) {
			e.printStackTrace();
		}
        message.setActive(Boolean.TRUE);
      }

      return message;
    }

	@Override
	public void saveBroadcastMessage(BroadcastMessage broadcastMessage) {
		dataAccessService.saveDomainObject(broadcastMessage, null);
	}

	@Override
	public void removeBroadcastMessage(BroadcastMessage broadcastMessage) {
		dataAccessService.deleteDomainObject(broadcastMessage, null);
	}
  
}
