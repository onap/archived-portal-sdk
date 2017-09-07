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
package org.onap.portalsdk.core.web.socket;

import java.util.Hashtable;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/webrtc")
public class WebRTCSocket {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(WebRTCSocket.class);

	private final static Map<String, Hashtable<String, Object[]>> channelMap = new Hashtable<String, Hashtable<String, Object[]>>();
	private final Map<String, String> sessionMap = new Hashtable<String, String>();
	private final ObjectMapper mapper = new ObjectMapper();

	@OnMessage
	public void message(String message, Session session) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonObject = mapper.readValue(message, Map.class);
			try {
				Object isOpen = jsonObject.get("open");
				if (isOpen != null && (Boolean) isOpen) {
					String channel = (String) jsonObject.get("channel");
					Object value = channelMap.get(channel);
					Hashtable<String, Object[]> sourceDestMap;
					if (value == null)
						sourceDestMap = new Hashtable<>();
					else
						sourceDestMap = (Hashtable<String, Object[]>) value;

					sourceDestMap.put(session.getId(), new Object[] { session });
					channelMap.put(channel, sourceDestMap);
					sessionMap.put(session.getId(), channel);

				}
			} catch (Exception je) {
				logger.error(EELFLoggerDelegate.errorLogger, "mesage failed", je);
			}

			try {
				Object dataObj = jsonObject.get("data");
				if (dataObj == null)
					return;
				Map<String, Object> dataMapObj = (Map<String, Object>) dataObj;
				String channel = null;
				try {
					Object channelObj = dataMapObj.get("sessionid");
					if (channelObj != null)
						channel = (String) channelObj;
					else
						channel = (String) jsonObject.get("channel");
				} catch (Exception json) {
					logger.error(EELFLoggerDelegate.errorLogger, "mesage failed", json);
				}


				Hashtable<String, Object[]> sourceDestMap = channelMap.get(channel);
				if (sourceDestMap != null)
					for (String id : sourceDestMap.keySet()) {
						if (!id.equals(session.getId())) {
							Session otherSession = (Session) (sourceDestMap.get(id))[0];
							if (otherSession.isOpen())
								otherSession.getBasicRemote().sendText(mapper.writeValueAsString(dataObj));
						}

					}
			} catch (Exception je) {
				logger.error(EELFLoggerDelegate.errorLogger, "mesage failed", je);
			}

		} catch (Exception je) {
			logger.error(EELFLoggerDelegate.errorLogger, "mesage failed", je);
		}
	}

	@OnOpen
	public void open(Session session) {
		logger.debug(EELFLoggerDelegate.debugLogger, "Session opened {}", session);
	}

	@OnClose
	public void close(Session session) {
		String channel = sessionMap.get(session.getId());
		if (channel != null) {
			channelMap.remove(channel);
		}
		logger.debug(EELFLoggerDelegate.debugLogger, "Channel closed");
	}

}
