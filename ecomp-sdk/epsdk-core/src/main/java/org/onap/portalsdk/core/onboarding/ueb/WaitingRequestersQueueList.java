/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.core.onboarding.ueb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A thin wrapper around ConcurrentHashMap that stores a queue for each
 * Requester that is waiting for a Reply. When a reply is received that has a
 * matching msgId, that requesters queue is populated with the reply message.
 * 
 * Primarily for the UebManager to track requests while it waits for responses.
 */
public class WaitingRequestersQueueList {
	private final Log logger = LogFactory.getLog(getClass());

	private final Map<String, LinkedBlockingQueue<UebMsg>> map;

	public WaitingRequestersQueueList() {
		map = new ConcurrentHashMap<>();
	}
	
	public void addQueueToMap(String msgId, LinkedBlockingQueue<UebMsg> queue) {
		this.map.put(msgId, queue);
	}

	public void addMsg(String msgId, UebMsg message) {
		LinkedBlockingQueue<UebMsg> queue = this.map.get(msgId);
		if (queue != null) {
			queue.add(message);
		} else {
			logger.warn("Did not find entry in WaitingRequestersQueueList for msgId " + msgId);
		}
	}

	public void removeQueueFromMap(String msgId) {
		this.map.remove(msgId);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Map contains " + this.map.size() + " Publishers.");
		for (Map.Entry<String, LinkedBlockingQueue<UebMsg>> entry : this.map.entrySet()) {
			String key = entry.getKey().toString();
			LinkedBlockingQueue<UebMsg> queue = entry.getValue();
			sb.append("Entry msgId, " + key + " queue " + queue);
		}
		return sb.toString();
	}

}
