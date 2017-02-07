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
package org.openecomp.portalsdk.core.web.socket;

import java.util.Hashtable;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/webrtc")
public class WebRTCSocket {
	
	
	   public static Map<String,Hashtable<String,Object[]>> channelMap = new Hashtable<String,Hashtable<String,Object[]>>();
	   public  Map<String,String> sessionMap = new Hashtable<String,String>();
	   ObjectMapper mapper = new ObjectMapper(); 

	     
		@OnMessage
	     public void message(String message, Session session) {
	    	 try {
	    	 //JSONObject jsonObject = new JSONObject(message);
	    	 @SuppressWarnings("unchecked")
			 Map<String,Object> jsonObject = mapper.readValue(message, Map.class);
		    	 try {
		    	    Object isOpen = jsonObject.get("open");
		    	    if(isOpen != null && (Boolean)isOpen == true) {
		    		String channel = (String) jsonObject.get("channel");
					Object value = channelMap.get(channel);
					Hashtable<String,Object[]> sourceDestMap = null;
		    		if(value == null) 
		    			sourceDestMap = new Hashtable<String,Object[]>();
		    		else
		    			sourceDestMap = (Hashtable<String,Object[]>) value;
		    		
		    		sourceDestMap.put(session.getId(), new Object[]{session});
		    		channelMap.put(channel, sourceDestMap);
		    		sessionMap.put(session.getId(), channel);
			    	
		    		 		    		 
		    	    }
		    	 }
				 catch (Exception je) {
		    		 je.printStackTrace();
		    	 }
	    		 
	    		 try{
	    		 
	    		 Object dataObj = jsonObject.get("data");
	    		 if(dataObj == null)
	    			 return;
	    		 Map<String,Object> dataMapObj =  ( Map<String,Object>)dataObj; 
	    		 //Object thisUserId = dataMapObj.get("userid");
	    		 String channel = null;
	    		 try{
	    			 Object channelObj = dataMapObj.get("sessionid");
	    			 if(channelObj != null)
	    				 channel = (String) channelObj;
	    			 else 
	    				 channel = (String) jsonObject.get("channel");
	    		 }
	    		 catch(Exception json) {
	    			 json.printStackTrace();
	    		 }
	    		 
	    		/* 
	    		 JSONObject dataMapObj =  (JSONObject)dataObj; 
	    		 Object thisUserId = dataMapObj.get("userid");
	    		 String channel = (String) dataMapObj.get("sessionid");
	    		 Hashtable<String,Object> sourceDestMap = sessionMap.get(channel);
	    		 
				 if(thisUserId != null && sourceDestMap.get((String)thisUserId) == null) {
	    			 sourceDestMap.put((String)thisUserId, new Object[] {message, session});
	    		 }
				 
				 for(String userId : sourceDestMap.keySet()){
					 if(!userId.equals(thisUserId)) {
						 Session otherSession = (Session) ((Object[])sourceDestMap.get(userId))[1];
						 otherSession.getBasicRemote().sendText(message);
					 }
				 }
				 */
	    		  
	    		 Hashtable<String,Object[]> sourceDestMap = channelMap.get(channel);
	    		 if(sourceDestMap != null)
	    		 for(String id : sourceDestMap.keySet()){
					 if(!id.equals(session.getId())) {
						 Session otherSession = (Session) ((Object[])sourceDestMap.get(id))[0];
						 if(otherSession.isOpen())
							 otherSession.getBasicRemote().sendText(mapper.writeValueAsString(dataObj));
					 }
	    		 
	    		 }
	    		 }
	    		 catch (Exception  je) {
		    		 je.printStackTrace();
		    	 }
	    		 
	    	 }
	    	 catch (Exception  je) {
	    		 je.printStackTrace();
	    	 }
	         //System.out.println("Message received:" + message);
	     }
	     
	     @OnOpen
	     public void open(Session session) {
	         // System.out.println("Channel opened");
	     }
	     
	     @OnClose
	     public void close(Session session) {
	    	 String channel = sessionMap.get(session.getId());
	    	 if (channel != null) {
	    		 channelMap.remove(channel); 
	    	 }
	    	 // System.out.println("Channel closed");
	     }

}
