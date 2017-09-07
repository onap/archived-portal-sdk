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
package org.onap.portalsdk.core.onboarding.ueb;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;

import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides a consumer that reads messages from a UEB topic. Intended to be
 * passed to a separate thread as its runnable object.
 */
public class Consumer implements Runnable {

	private final Log logger = LogFactory.getLog(getClass());

	private final List<String> urlList = Helper.uebUrlList();
	private final Queue<UebMsg> queue;
	private final WaitingRequestersQueueList waitingRequestersList;
	private final String consumerKey;
	private final String consumerSecret;
	private final String topicName;
	private final String consumerGroupName;

	/**
	 * Accepts coordinates needed to subscribe to a UEB topic, as well as the queues
	 * for passing along messages that arrive.
	 * 
	 * @param consumerKey
	 *            UEB key used to subscribe to the topic
	 * @param consumerSecret
	 *            UEB secret used to subscribe to the topic
	 * @param topicName
	 *            UEB topic name
	 * @param consumerGroupName
	 *            consumer group name
	 * @param queue
	 *            Queue to receive UEB messages. All inbound messages are enqueued
	 *            here; ignored if null.
	 * @param waitingRequestersList
	 *            Collection of queues to receive UEB messages that arrive in
	 *            response to requests; i.e., emulating a synchronous request via
	 *            pub/sub.
	 */
	public Consumer(String consumerKey, String consumerSecret, String topicName, String consumerGroupName,
			Queue<UebMsg> queue, WaitingRequestersQueueList waitingRequestersList) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.topicName = topicName;
		this.consumerGroupName = consumerGroupName;
		this.queue = queue;
		this.waitingRequestersList = waitingRequestersList;
	}

	/**
	 * Subscribes to a topic using credentials as supplied to the constructor.
	 * Distributes messages appropriately as they arrive:
	 * <UL>
	 * <LI>If the queue is not null, adds the message to the queue.
	 * <LI>If the message's getMsgId() method returns non-null and the ID is found
	 * in the collection of waiting requesters, adds the message in that requester's
	 * queue.
	 * </UL>
	 * 
	 * This is intended to be called in a long running thread as a listener for any
	 * published messages on a topic. Typical async pub/sub model. We use a filter
	 * of "0" to prevent collisions with P2P messages with unique filter ids.
	 */
	protected void consume() throws IOException, UebException, GeneralSecurityException {
		final String id = UUID.randomUUID().toString();

		CambriaConsumer cc = new CambriaClientBuilders.ConsumerBuilder().usingHosts(urlList)
				.authenticatedBy(consumerKey, consumerSecret).onTopic(topicName).knownAs(consumerGroupName, id)
				.waitAtServer(15 * 1000).receivingAtMost(1000).build();

		while (true) {
			for (String msg : cc.fetch()) {
				logger.debug(" <== consume from topicName " + topicName + " msg: " + msg);
				UebMsg uebMsg = new ObjectMapper().readValue(msg, UebMsg.class);
				if (queue != null) {
					// Add to general queue allowing listeners to act on any
					// incoming messages. We don't know if a listener is
					// also going to be a responder to a synchronous
					// request. So put all received messages on the general
					// listener queue.
					queue.add(uebMsg);
					if (logger.isDebugEnabled())
						logger.debug("Added msg to queue " + this.queue + " msg :" + uebMsg.getPayload());
				}
				if (waitingRequestersList != null //
						&& uebMsg.getMsgId() != null //
						&& !(uebMsg.getMsgId()
								.equals(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_DEFAULT_MSG_ID)))) {
					// If a msgId is present, this could be a synchronous
					// reply. Here we add it to the waiting requester's
					// queue if we find a requester waiting for this msgId.
					waitingRequestersList.addMsg(uebMsg.getMsgId(), uebMsg);
				}
			}
			if (Thread.interrupted()) {
				logger.warn(Thread.currentThread() + " interrupted, exiting");
				break;
			}
			Helper.sleep(10);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			consume();
		} catch (Exception ex) {
			Thread t = Thread.currentThread();
			t.getUncaughtExceptionHandler().uncaughtException(t, ex);
		}
	}

}