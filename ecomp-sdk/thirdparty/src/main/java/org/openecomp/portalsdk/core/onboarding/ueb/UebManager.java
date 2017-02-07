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
package org.openecomp.portalsdk.core.onboarding.ueb;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiProperties;

/**
 * Manages UEB interactions and provides methods for publishing requests,
 * replies and others.
 */
public class UebManager {

	private final Log logger = LogFactory.getLog(getClass());

	private WaitingRequestersQueueList waitingRequestersQueueList;
	private PublisherList publisherList = new PublisherList();
	private static UebManager uebManager = null;

	private final String inTopicName;
	private final String consumerGroupName;
	private final String outTopicName;
	private final String appUebKey;
	private final String appUebSecret;

	private Publisher appPublisher;
	private Thread listenerThread;
	private boolean bThisIsEcompPortalServer = false;

	/**
	 * Constructor initializes fields and validates values obtained from
	 * properties.
	 * 
	 * The picture below is a simplified view of the relationships among ECOMP
	 * Portal and applications communicating via UEB:
	 * 
	 * <PRE>
	*                      ECOMP out to many.
	*                      App out to only ECOMP.
	*
	*  |----------------|<---------------------------------------------   
	*  |                |                                         | |  |
	*  |                |---------------------------> App 1 ------  |  |
	*  |  ECOMP Portal  |---------------------------> App 2 ---------  |
	*  |                |                            ...               |
	*  |                |---------------------------> App n -----------
	*  |----------------|
	 * </PRE>
	 * 
	 * @throws IOException
	 */
	protected UebManager() throws UebException {
		waitingRequestersQueueList = null;
		listenerThread = null;
		outTopicName = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_PORTAL_INBOX_NAME);
		inTopicName = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_INBOUND_MAILBOX_NAME);
		appUebKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
		appUebSecret = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_SECRET);
		String consGrp = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_CONSUMER_GROUP_NAME);

		if (outTopicName == null || outTopicName.length() == 0)
			throw new UebException("Failed to get property " + PortalApiConstants.ECOMP_PORTAL_INBOX_NAME, null, null,
					null);
		if (inTopicName == null || inTopicName.length() == 0)
			throw new UebException("Failed to get property " + PortalApiConstants.UEB_APP_INBOUND_MAILBOX_NAME, null,
					null, null);
		if (consGrp == null || consGrp.length() == 0)
			throw new UebException("Failed to get property " + PortalApiConstants.UEB_APP_CONSUMER_GROUP_NAME, null,
					null, null);
		if (appUebKey == null || appUebKey.length() == 0)
			throw new UebException("Failed to get property " + PortalApiConstants.UEB_APP_KEY, null, null, null);
		if (appUebSecret == null || appUebSecret.length() == 0)
			throw new UebException("Failed to get property " + PortalApiConstants.UEB_APP_SECRET, null, null, null);
		List<String> uebUrlList = Helper.uebUrlList();
		if (uebUrlList == null || uebUrlList.size() == 0)
			throw new UebException("Failed to get property" + PortalApiConstants.UEB_URL_LIST, null, null, null);
		// A bit of magic: if consumer group is a magic token, generate one.
		consumerGroupName = (PortalApiConstants.UEB_APP_CONSUMER_GROUP_NAME_GENERATOR.equals(consGrp)
				? UUID.randomUUID().toString() : consGrp);
	}

	/**
	 * Gets the static instance, creating it if necessary.
	 * 
	 * @return Instance of UebManager
	 * @throws IOException
	 */
	public static synchronized UebManager getInstance() throws UebException {
		if (uebManager == null) {
			uebManager = new UebManager();
		}
		return uebManager;
	}

	/**
	 * Answers whether the getInstance() method has previously been called.
	 * 
	 * @return True if a static instance is available, else false.
	 */
	public static boolean isInstanceAvailable() {
		return uebManager != null;
	}

	/**
	 * Creates a list of waiting requesters, creates and a consumer using cached
	 * information, and starts a new thread to run the consumer that listens for
	 * messages published to the inbound topic configured in the constructor.
	 * 
	 * @param inboxQueue
	 *            Queue supplied to the consumer. If not null, the consumer will
	 *            enqueue every message it receives.
	 */
	public void initListener(ConcurrentLinkedQueue<UebMsg> inboxQueue) throws UebException {
		waitingRequestersQueueList = new WaitingRequestersQueueList();
		Consumer runnable = new Consumer(appUebKey, appUebSecret, inTopicName, consumerGroupName, inboxQueue,
				waitingRequestersQueueList);
		this.listenerThread = new Thread(runnable, "UEBConsumerThread");
		this.listenerThread.start();
		Helper.sleep(400); // UEB functions more reliably when we give this some
							// time

		logger.info("UEBManager instance starting... " + inTopicName + " listener thread "
				+ this.listenerThread.getName() + " state = " + this.listenerThread.getState());

		/*
		 * ECOMP Portal manages a dynamic list of outbound topics and so the
		 * outTopicName is initialized in this logic with the same value as the
		 * inbound topic. The real outbound topics name will be added to the
		 * publisher list for ECOMP Portal. For an SDK/App instance only one
		 * publisher is needed, appPublisher.
		 */
		if (inTopicName.equalsIgnoreCase(outTopicName)) {
			this.bThisIsEcompPortalServer = true;
		} else {
			appPublisher = new Publisher(appUebKey, appUebSecret, outTopicName);
			Helper.sleep(400);
		}
	}

	/**
	 * Creates and adds a publisher to the list for the specified topic. This
	 * should only be called by the ECOMP Portal App, other Apps have just one
	 * publisher and use appPublisher
	 * 
	 * @param topicName
	 */
	public void addPublisher(String topicName) {
		logger.info("UEBManager adding publisher for " + topicName);
		Publisher outBoxToAppPublisher = new Publisher(appUebKey, appUebSecret, topicName);
		publisherList.addPublisherToMap(topicName, outBoxToAppPublisher);
	}

	/**
	 * Removes a publisher from the list for the specified topic.
	 *
	 * This should only be called by the ECOMP Portal App, other Apps have just
	 * one publisher and use appPublisher
	 * 
	 * @param topicName
	 */
	public void removePublisher(String topicName) {
		logger.info("UEBManager removing publisher for " + topicName);
		publisherList.removePublisherFromMap(topicName);
	}

	/**
	 * Adds the default ECOMP message ID to the message and sends the message to
	 * the topic.
	 * 
	 * @param msg
	 * @throws UebException
	 */
	public void publish(UebMsg msg) throws UebException {
		msg.putMsgId(PortalApiConstants.ECOMP_DEFAULT_MSG_ID);
		appPublisher.send(msg);
	}

	/**
	 * Sends the message using the default publisher.
	 * 
	 * @param msg
	 * @throws UebException
	 */
	public void publishReply(UebMsg msg) throws UebException {
		// Caller populates msgId with the echoed value from the request
		appPublisher.send(msg);
	}

	/**
	 * Sends the message using the appropriate publisher for the specified
	 * topic.
	 * 
	 * @param msg
	 * @param topicName
	 * @throws UebException
	 */
	public void publishEP(UebMsg msg, String topicName) throws UebException {
		Publisher publisher = publisherList.getPublisher(topicName);
		if (publisher != null) {
			msg.putMsgId(PortalApiConstants.ECOMP_DEFAULT_MSG_ID);
			publisher.send(msg);
		}
	}

	/**
	 * Publishes a reply using the appropriate publisher for the specified
	 * topic.
	 * 
	 * @param msg
	 * @param topicName
	 * @throws UebException
	 */
	public void publishReplyEP(UebMsg msg, String topicName) throws UebException {
		// Caller populates msgId with the echoed value from the request
		Publisher publisher = publisherList.getPublisher(topicName);
		if (publisher != null) {
			publisher.send(msg);
		}
	}

	/**
	 * Sends the specified message using the specified publisher, and waits for
	 * a reply. Retransmits if no reply is received in 5 seconds; gives up after
	 * 3 retries.
	 * 
	 * @param msg
	 * @param publisher
	 * @return Message from a remote publisher, or null if timeout happens.
	 * @throws UebException
	 */
	public UebMsg requestReplyUsingPublisher(UebMsg msg, Publisher publisher) throws UebException {
		UebMsg reply = null;
		if (waitingRequestersQueueList == null) {
			logger.error("requestReplyUsingPublisher called but listener thread not initialized");
		} else {
			// Storing a non-default message ID identifies this as a synchronous
			// request
			msg.putMsgId(UUID.randomUUID().toString());

			/*
			 * Create a queue for this request, the consumer thread will insert
			 * the reply on this queue
			 */
			LinkedBlockingQueue<UebMsg> replyQueue = new LinkedBlockingQueue<UebMsg>();
			waitingRequestersQueueList.addQueueToMap(msg.getMsgId(), replyQueue);

			/*
			 * Send the request
			 */
			publisher.send(msg);

			/*
			 * Wait for reply up to 3 * 5 = 15 seconds
			 */
			int reTransmits = 0;
			int maxRetransmits = 3;
			int retransmitTimeMs = 5000;
			long sendTimeStamp = System.currentTimeMillis();
			while (reTransmits < maxRetransmits) {
				if ((reply = replyQueue.poll()) != null)
					break;

				long now = System.currentTimeMillis();
				if (now - sendTimeStamp > retransmitTimeMs) {
					logger.debug("Retransmitting send... msg = " + msg.getPayload() + msg.getMsgId());
					publisher.send(msg);
					sendTimeStamp = System.currentTimeMillis();
					reTransmits++;
				}
			}
			waitingRequestersQueueList.removeQueueFromMap(msg.getMsgId());
			if (reTransmits == maxRetransmits)
				throw new UebException(PortalApiConstants.ECOMP_UEB_TIMEOUT_ERROR, inTopicName, null, msg.toString());

		}
		return reply;
	}

	/**
	 * Sends the specified message using the default publisher and waits for a
	 * reply.
	 * 
	 * @param msg
	 * @return Message from a remote publisher, or null if timeout happens.
	 * @throws UebException
	 */
	public UebMsg requestReply(UebMsg msg) throws UebException {
		return requestReplyUsingPublisher(msg, appPublisher);
	}

	/**
	 * Sends the specified message using the publisher appropriate for the
	 * specified topic name, and waits for a reply.
	 * 
	 * @param msg
	 * @param topicName
	 * @return Message from a remote publisher, or null if timeout happens.
	 * @throws UebException
	 */
	public UebMsg requestReplyEP(UebMsg msg, String topicName) throws UebException {
		UebMsg returnMsg = null;
		Publisher publisher = publisherList.getPublisher(topicName);
		if (publisher != null) {
			returnMsg = requestReplyUsingPublisher(msg, publisher);
		}
		return returnMsg;
	}

	/**
	 * Publishes the payload as a UEB widget-notification message on the default
	 * publisher. Intended for use by Apps inter widget communication, not EP
	 * itself.
	 * 
	 * @param payload
	 * @param userId
	 */
	public void postWidgetNotification(String payload, String userId) throws UebException {
		UebMsg msg = new UebMsg();
		msg.putPayload(payload);
		msg.putUserId(userId);
		msg.putMsgType(UebMsgTypes.UEB_MSG_TYPE_WIDGET_NOTIFICATION);
		this.publish(msg);
	}

	/**
	 * Interrupts the long-running thread that runs the consumer.
	 */
	public void shutdown() {
		if (this.listenerThread != null) {
			this.listenerThread.interrupt();
		}
	}
}
