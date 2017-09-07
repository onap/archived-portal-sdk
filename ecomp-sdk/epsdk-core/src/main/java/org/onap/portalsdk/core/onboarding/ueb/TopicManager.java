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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.nsa.apiClient.http.HttpClient;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.cambria.client.CambriaClient.CambriaApiException;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaTopicManager;

/**
 * Provides methods to facilitate creating topics, and adding publishers and
 * subscribers to existing topics.
 * 
 * Utilizes UEB/Cambria subscriber/publisher messaging service.
 */
public class TopicManager {

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * Creates a topic with the specified information.
	 * 
	 * @param key
	 *            Topic key
	 * @param secret
	 *            Topic secret key
	 * @param topicName
	 *            Topic name
	 * @param topicDescription
	 *            Topic description
	 * @throws HttpException
	 * @throws CambriaApiException
	 * @throws IOException
	 */
	public void createTopic(String key, String secret, String topicName, String topicDescription)
			throws HttpException, CambriaApiException, IOException {
		final List<String> urlList = Helper.uebUrlList();
		if (logger.isInfoEnabled()) {
			logger.info("==> createTopic");
			logger.info("topicName: " + topicName);
			logger.info("topicDescription: " + topicDescription);
		}
		CambriaTopicManager tm = null;
		try {
			tm = CambriaClientFactory.createTopicManager(null, urlList, key, secret);
		} catch (GeneralSecurityException e) {
			logger.error("pub.build Exception ", e);
			throw new CambriaApiException(topicName);
		}
		tm.createTopic(topicName, topicDescription, 1, 1);
	}

	/**
	 * Modifies the specified topic to accept a subscriber using the specified key.
	 * 
	 * @param topicOwnerKey
	 * @param topicOwnerSecret
	 * @param subscriberKey
	 * @param topicName
	 * @throws HttpException
	 * @throws CambriaApiException
	 * @throws IOException
	 */
	public void addSubscriber(String topicOwnerKey, String topicOwnerSecret, String subscriberKey, String topicName)
			throws HttpException, CambriaApiException, IOException {
		logger.info("==> addSubscriber to topic " + topicName);
		final List<String> urlList = Helper.uebUrlList();
		CambriaTopicManager tm = null;
		try {
			tm = new CambriaClientBuilders.TopicManagerBuilder().usingHosts(urlList)
					.authenticatedBy(topicOwnerKey, topicOwnerSecret).build();
			tm.allowConsumer(topicName, subscriberKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("addSubscriber failed", e);
		}
	}

	/**
	 * Modifies the specified topic to accept a publisher using the specified key.
	 * 
	 * @param topicOwnerKey
	 * @param topicOwnerSecret
	 * @param publisherKey
	 * @param topicName
	 * @throws HttpException
	 * @throws CambriaApiException
	 * @throws IOException
	 */

	@SuppressWarnings("deprecation")
	public void addPublisher(String topicOwnerKey, String topicOwnerSecret, String publisherKey, String topicName)
			throws HttpException, CambriaApiException, IOException {
		logger.info("==> addPublisher to topic " + topicName);
		final List<String> urlList = Helper.uebUrlList();
		CambriaTopicManager tm = null;
		try {
			tm = CambriaClientFactory.createTopicManager(HttpClient.ConnectionType.HTTPS, urlList, topicOwnerKey,
					topicOwnerSecret);
		} catch (GeneralSecurityException e) {
			logger.error("pub.build Exception ", e);
			throw new CambriaApiException(topicName);
		}
		tm.allowProducer(topicName, publisherKey);
	}
}
