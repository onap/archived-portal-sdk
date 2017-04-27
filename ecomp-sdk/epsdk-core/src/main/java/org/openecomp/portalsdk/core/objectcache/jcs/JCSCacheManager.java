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
package org.openecomp.portalsdk.core.objectcache.jcs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.CacheConstants;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.objectcache.AbstractCacheManager;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.web.context.ServletContextAware;

public abstract class JCSCacheManager extends AbstractCacheManager implements CacheConstants, ServletContextAware {

	public static String LOOKUP_OBJECT_CACHE_NAME = "lookUpObjectCache";
	public static String JCS_CONFIG_FILE_PATH = "cache_config_file_path";
	public static String CACHE_LOAD_ON_STARTUP = "cache_load_on_startup";
	public static String CACHE_PROPERTY_VALUE_TRUE = "true";
	public static String CACHE_CONTROL_SWITCH_ON = "1";
	public static String CACHE_CONTROL_SWITCH_OFF = "0";
	public static String CACHE_CONTROL_SWITCH = "cache_switch";

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(JCSCacheManager.class);

	private static JCS lookUpCache;
	private ServletContext servletContext;

	private Properties cacheConfigProperties = null;
	private final Vector<String> jscManagedCacheList = new Vector<String>();

	private DataAccessService dataAccessService;

	public JCSCacheManager() {
		super();
		jscManagedCacheList.add(LOOKUP_OBJECT_CACHE_NAME);
	}

	@PostConstruct
	public void configure() throws IOException {
		super.configure();

		String jcsConfigFilePath = SystemProperties.getProperty(JCS_CONFIG_FILE_PATH);
		// getProperty throws if the key is missing; but check anyhow.
		if (jcsConfigFilePath == null || jcsConfigFilePath.length() == 0)
			throw new IOException("configure: failed to get value for config property " + JCS_CONFIG_FILE_PATH);
		InputStream jcsConfigInputStream = getServletContext().getResourceAsStream(jcsConfigFilePath);
		if (jcsConfigInputStream == null)
			throw new IOException("configure: failed to open stream for config property " + JCS_CONFIG_FILE_PATH
					+ " with name " + jcsConfigFilePath);
		logger.debug(EELFLoggerDelegate.debugLogger,
				"configure: loading cache properties from classpath resource {} ", jcsConfigFilePath);
		Properties p = new Properties();
		p.load(jcsConfigInputStream);
		jcsConfigInputStream.close();

		CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
		ccm.configure(p);
		setCacheConfigProperties(p);

		try {
			initializeLookUpCache();
		} catch (CacheException ce) {
			throw new IOException("configure: failed to initialize lookup cache", ce);
		}

	}

	private void initializeLookUpCache() throws CacheException {
		lookUpCache = JCS.getInstance(LOOKUP_OBJECT_CACHE_NAME);

		JCSCacheEventHandler eventHandler = new JCSCacheEventHandler();
		IElementAttributes elementAttributes = lookUpCache.getDefaultElementAttributes();

		elementAttributes.addElementEventHandler(eventHandler);

		lookUpCache.setDefaultElementAttributes(elementAttributes);

		if (CACHE_PROPERTY_VALUE_TRUE.equalsIgnoreCase(SystemProperties.getProperty(CACHE_LOAD_ON_STARTUP))) {
			loadDataOnStartUp();
		}
	}

	public Object getObject(String key) {
		if (CACHE_CONTROL_SWITCH_ON.equalsIgnoreCase(SystemProperties.getProperty(CACHE_CONTROL_SWITCH))) {
			if (lookUpCache == null)
				return null;
			else
				return lookUpCache.get(key);
		} else
			return null;
	}

	public void putObject(String key, Object objectToCache) {
		try {
			if (CACHE_CONTROL_SWITCH_ON.equalsIgnoreCase(SystemProperties.getProperty(CACHE_CONTROL_SWITCH))) {
				if (lookUpCache != null) {
					lookUpCache.put(key, objectToCache);
				}
			}
		} catch (CacheException ce) {
			logger.error(EELFLoggerDelegate.errorLogger, "putObject: failed to put the object with key " + key, ce);
		}
	}

	public void clearCache(String region) {
		try {
			if (region.equals(LOOKUP_OBJECT_CACHE_NAME))
				lookUpCache.clear();
		} catch (CacheException ce) {
			logger.error(EELFLoggerDelegate.errorLogger,
					"clearCache: failed to clear the cache for the region " + region, ce);
		}
	}

	public void clearCache() {
		clearCache(LOOKUP_OBJECT_CACHE_NAME);
	}

	private void loadDataOnStartUp() {
		loadLookUpCache();
	}

	public abstract void loadLookUpCache();

	public void refreshLookUpCache() {
		clearCache(LOOKUP_OBJECT_CACHE_NAME);
		loadLookUpCache();
	}

	public Properties getCacheConfigProperties() {
		return cacheConfigProperties;
	}

	public void setCacheConfigProperties(Properties cacheConfigProperties) {
		this.cacheConfigProperties = cacheConfigProperties;
	}

	public Vector<String> getJscManagedCacheList() {
		return jscManagedCacheList;
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
