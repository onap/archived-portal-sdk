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

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/contact")
public class PeerBroadcastSocket {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PeerBroadcastSocket.class);

	public static Map<String, Object> channelMap = new Hashtable<String, Object>();
	public Map<String, String> sessionMap = new Hashtable<String, String>();
	ObjectMapper mapper = new ObjectMapper();

	@OnMessage
	public void message(String message, Session session) {
		try {
			// JSONObject jsonObject = new JSONObject(message);
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonObject = mapper.readValue(message, Map.class);
			try {
				Object from = jsonObject.get("from");
				if (from != null) {
					channelMap.put(from.toString(), session);
					sessionMap.put(session.getId(), from.toString());
				}
			} catch (Exception je) {
				logger.error(EELFLoggerDelegate.errorLogger, "Failed to read value" + je.getMessage());
			}

			try {
				Object to = jsonObject.get("to");
				if (to == null)
					return;
				Object toSessionObj = channelMap.get(to);
				if (toSessionObj != null) {
					Session toSession = null;
					toSession = (Session) toSessionObj;
					toSession.getBasicRemote().sendText(message);
				}

			} catch (Exception ex) {
				logger.error(EELFLoggerDelegate.errorLogger, "Failed to send text" + ex.getMessage());
			}

		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "Failed" + ex.getMessage());
		}

	}

	@OnOpen
	public void open(Session session) {
		logger.info(EELFLoggerDelegate.debugLogger, "Channel opened");
	}

	@OnClose
	public void close(Session session) {
		String channel = sessionMap.get(session.getId());
		if (channel != null) {
			Object sessObj = channelMap.get(channel);
			if (sessObj != null) {
				try {
					((Session) sessObj).close();
				} catch (IOException e) {
					logger.error(EELFLoggerDelegate.errorLogger, "Failed to close" + e.getMessage());
				}
			}
			channelMap.remove(channel);
		}
		logger.info(EELFLoggerDelegate.debugLogger, "Channel closed");
	}

}
