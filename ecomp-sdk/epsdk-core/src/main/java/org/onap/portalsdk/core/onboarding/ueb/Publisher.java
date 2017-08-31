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

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Provides a publisher that sends messages to a UEB topic.
 * 
 * Utilizes AT&T's UEB/Cambria subscriber/publisher messaging service. 
 */
public class Publisher {

	private final Log logger = LogFactory.getLog(getClass());

	protected final LinkedList<String> urlList = Helper.uebUrlList();

	private final String topicName;
	private final String publisherKey;
	private final String publisherSecret;

	/**
	 * Accepts coordinates needed to publish to a UEB topic.
	 * 
	 * @param publisherKey
	 *            UEB key used to publish to the topic
	 * @param publisherSecret
	 *            UEB secret used to publish to the topic
	 * @param topicName
	 *            UEB topic name
	 */
	public Publisher(String publisherKey, String publisherSecret, String topicName) {
		this.publisherKey = publisherKey;
		this.publisherSecret = publisherSecret;
		this.topicName = topicName;
		logger.info("Publisher instantiated for topic " + topicName);
	}

	/**
	 * Creates a publisher, subscribes to the topic, sends the specified message
	 * to the topic, then closes the publisher. This ensures that the single
	 * message goes immediately. UEB is designed for high throughput and tries
	 * to batch up multiple messages in each send, but this method wants the
	 * single message to go immediately.
	 * 
	 * @param uebMsg
	 *            Message object to send as the payload.
	 * @throws UebException
	 *             If anything goes wrong, including JSON serialization of the
	 *             specified message object.
	 */
	public void send(UebMsg uebMsg) throws UebException {
		String msg = null;

		CambriaBatchingPublisher pub;
		try {
			pub = new CambriaClientBuilders.PublisherBuilder()
					.authenticatedBy(publisherKey, publisherSecret).usingHosts(urlList).onTopic(topicName).build();
		} catch (MalformedURLException e1) {
			logger.error("pub.build Exception ", e1);
			throw new UebException(PortalApiConstants.ECOMP_UEB_UNKNOWN_PUBLISH_ERROR, e1, topicName, null, msg);
		} catch (GeneralSecurityException e1) {
			logger.error("pub.build Exception ", e1);
			throw new UebException(PortalApiConstants.ECOMP_UEB_UNKNOWN_PUBLISH_ERROR, e1, topicName, null, msg);
		}

		try {
			ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
			msg = mapper.writeValueAsString(uebMsg);
		} catch (JsonProcessingException e) {
			throw new UebException(PortalApiConstants.ECOMP_UEB_INVALID_MSG, topicName, null, null);
		}

		try {
			logger.debug("Publishing to " + topicName + " msg: " + msg);
			int NumSent = pub.send(PortalApiConstants.ECOMP_GENERAL_UEB_PARTITION, msg);
			if (NumSent == 0) {
				throw new UebException(PortalApiConstants.ECOMP_UEB_UNKNOWN_PUBLISH_ERROR, topicName, null, msg);
			}
		} catch (IOException ex) {
			logger.error("Failed to publish", ex);
			throw new UebException(PortalApiConstants.ECOMP_UEB_UNKNOWN_PUBLISH_ERROR, ex, topicName, null, msg);
		}

		try {
			// close the publisher to make sure everything's sent before exiting
			pub.close(5, TimeUnit.SECONDS);
		} catch (Exception ex) {
			logger.error("pub.close Exception ", ex);
			throw new UebException(PortalApiConstants.ECOMP_UEB_UNKNOWN_PUBLISH_ERROR, ex, topicName, null, msg);
		}

	}
}