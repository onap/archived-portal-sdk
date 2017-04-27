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
package org.openecomp.portalsdk.core.onboarding.ueb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A thin wrapper around ConcurrentHashMap that stores a queue for each
 * Requester that is waiting for a Reply. When a reply is received that has a
 * matching msgId, that requesters queue is populated with the reply message.
 * 
 * Primarily for Portal core to track the remote applications that have placed
 * requests; never used by those applications.
 */
public class PublisherList {

	private final Log logger = LogFactory.getLog(getClass());

	private final Map<String, Publisher> map;
	
	public PublisherList() {
		 map = new ConcurrentHashMap<>();
	}
	
	public void addPublisherToMap(String topicName, Publisher publisher) {
		if (this.map.containsKey(topicName)) {
			logger.error("Publisher already exists for " + topicName);
		} else {
			this.map.put(topicName, publisher);
		}
	}

	public Publisher getPublisher(String topicName) {
		return this.map.get(topicName);
	}

	public void removePublisherFromMap(String topicName) {
		this.map.remove(topicName);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Map contains " + this.map.size() + " Publishers.");
		for (Map.Entry<String, Publisher> entry : this.map.entrySet()) {
			String key = entry.getKey().toString();
			Publisher pub = entry.getValue();
			sb.append("Entry msgId, " + key + " publisher" + pub);
		}
		return sb.toString();
	}

	public int size() {
		return this.map.size();
	}

}