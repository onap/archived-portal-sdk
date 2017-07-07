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
package org.openecomp.portalsdk.core.conf;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.openecomp.portalsdk.core.interceptor.ResourceInterceptor;
import org.openecomp.portalsdk.core.interceptor.SessionTimeoutInterceptor;
import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.format.AppMessagesEnum;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.menu.MenuBuilder;
import org.openecomp.portalsdk.core.onboarding.util.CipherUtil;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.service.DataAccessServiceImpl;
import org.openecomp.portalsdk.core.service.LocalAccessCondition;
import org.openecomp.portalsdk.core.service.RestApiRequestBuilder;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Configures Spring features in the ECOMP Portal SDK including request
 * interceptors and view resolvers. Application should subclass and override
 * methods as needed.
 */
public class AppConfig extends WebMvcConfigurerAdapter implements Configurable, ApplicationContextAware {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AppConfig.class);

	private final List<String> tileDefinitions = new ArrayList<String>();
	protected ApplicationContext appApplicationContext = null;

	public AppConfig() {
		// loads all default fields and marks logging
		// has been started for each log file type.
		initGlobalLocalContext();
	}

	/**
	 * Creates and returns a new instance of a secondary (order=2)
	 * {@link ViewResolver} that finds files by adding prefix "/WEB-INF/jsp/"
	 * and suffix ".jsp" to the base view name.
	 * 
	 * @return New instance of {@link ViewResolver}.
	 */
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setOrder(2);
		return viewResolver;
	}

	/**
	 * Loads all the default logging fields into the global MDC context and
	 * marks each log file type that logging has been started.
	 */
	private void initGlobalLocalContext() {
		logger.init();
	}

	/*
	 * Any requests from the url pattern /static/**, Spring will look for the
	 * resources from the /static/ Same as <mvc:resources mapping="/static/**"
	 * location="/static/"/> in xml
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/static/**").addResourceLocations("/static/");
		registry.addResourceHandler("/**").addResourceLocations("/");
	}

	/**
	 * Creates and returns a new instance of a {@link DataAccessService} class.
	 * 
	 * @return New instance of {@link DataAccessService}.
	 */
	@Bean
	public DataAccessService dataAccessService() {
		return new DataAccessServiceImpl();
	}

	/**
	 * Creates and returns a new instance of a {@link SystemProperties} class.
	 * 
	 * @return New instance of {@link SystemProperties}.
	 */
	@Bean
	public SystemProperties systemProperties() {
		return new SystemProperties();
	}

	/**
	 * Creates and returns a new instance of a {@link MenuBuilder} class.
	 * 
	 * @return New instance of {@link MenuBuilder}.
	 */
	@Bean
	public MenuBuilder menuBuilder() {
		return new MenuBuilder();
	}
	
	/**
	 * Creates and returns a new instance of a {@link UserUtils} class.
	 * 
	 * @return New instance of {@link UserUtils}.
	 */
	@Bean
	public UserUtils userUtil()
	{
		return new UserUtils();
	}

	/**
	 * Creates and returns a new instance of an {@link AppUtils} class.
	 * 
	 * @return New instance of {@link AppUtils}.
	 */
	@Bean
	public AppUtils appUtils() {
		return new AppUtils();
	}

	/**
	 * Creates and returns a new instance of a {@link TilesConfigurer} class.
	 * 
	 * @return New instance of {@link TilesConfigurer}.
	 */
	@Bean
	public TilesConfigurer tilesConfigurer() {
		TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions(tileDefinitions());
		tilesConfigurer.setCheckRefresh(true);
		return tilesConfigurer;
	}

	/**
	 * 
	 * Creates the Application Data Source.
	 * 
	 * @return DataSource Object
	 * @throws Exception
	 *             on failure to create data source object
	 */
	@Bean
	public DataSource dataSource() throws Exception {

		systemProperties();

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(SystemProperties.getProperty(SystemProperties.DB_DRIVER));
			dataSource.setJdbcUrl(SystemProperties.getProperty(SystemProperties.DB_CONNECTIONURL));
			dataSource.setUser(SystemProperties.getProperty(SystemProperties.DB_USERNAME));
			String password = SystemProperties.getProperty(SystemProperties.DB_PASSWORD);
			if (SystemProperties.containsProperty(SystemProperties.DB_ENCRYPT_FLAG)) {
				String encryptFlag = SystemProperties.getProperty(SystemProperties.DB_ENCRYPT_FLAG);
				if (encryptFlag != null && encryptFlag.equalsIgnoreCase("true")) {
					password = CipherUtil.decrypt(password);
				}
			}
			dataSource.setPassword(password);
			dataSource
					.setMinPoolSize(Integer.parseInt(SystemProperties.getProperty(SystemProperties.DB_MIN_POOL_SIZE)));
			dataSource
					.setMaxPoolSize(Integer.parseInt(SystemProperties.getProperty(SystemProperties.DB_MAX_POOL_SIZE)));
			dataSource.setIdleConnectionTestPeriod(
					Integer.parseInt(SystemProperties.getProperty(SystemProperties.IDLE_CONNECTION_TEST_PERIOD)));
			dataSource.setTestConnectionOnCheckout(getConnectionOnCheckout());
			dataSource.setPreferredTestQuery(getPreferredTestQuery());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger,
					"Error initializing database, verify database settings in properties file: "
							+ UserUtils.getStackTrace(e),
					AlarmSeverityEnum.CRITICAL);
			logger.error(EELFLoggerDelegate.debugLogger,
					"Error initializing database, verify database settings in properties file: "
							+ UserUtils.getStackTrace(e),
					AlarmSeverityEnum.CRITICAL);
			// Raise an alarm that opening a connection to the database failed.
			logger.logEcompError(AppMessagesEnum.BeDaoSystemError);
			throw e;
		}
		return dataSource;
	}

	/**
	 * Gets the value of the property
	 * {@link SystemProperties#PREFERRED_TEST_QUERY}; defaults to "Select 1" if
	 * the property is not defined.
	 * 
	 * @return String value that is a SQL query
	 */
	private String getPreferredTestQuery() {
		// Use simple default
		String preferredTestQueryStr = "SELECT 1";
		if (SystemProperties.containsProperty(SystemProperties.PREFERRED_TEST_QUERY)) {
			preferredTestQueryStr = SystemProperties.getProperty(SystemProperties.PREFERRED_TEST_QUERY);
			logger.debug(EELFLoggerDelegate.debugLogger, "getPreferredTestQuery: property key {} value is {}",
					SystemProperties.PREFERRED_TEST_QUERY, preferredTestQueryStr);
		} else {
			logger.info(EELFLoggerDelegate.errorLogger,
					"getPreferredTestQuery: property key {} not found, using default value {}",
					SystemProperties.PREFERRED_TEST_QUERY, preferredTestQueryStr);
		}
		return preferredTestQueryStr;
	}

	/**
	 * Gets the value of the property
	 * {@link SystemProperties#TEST_CONNECTION_ON_CHECKOUT}; defaults to true if
	 * the property is not defined.
	 * 
	 * @return Boolean value
	 */
	private Boolean getConnectionOnCheckout() {
		// Default to true, always test connection
		boolean testConnectionOnCheckout = true;
		if (SystemProperties.containsProperty(SystemProperties.TEST_CONNECTION_ON_CHECKOUT)) {
			testConnectionOnCheckout = Boolean
					.valueOf(SystemProperties.getProperty(SystemProperties.TEST_CONNECTION_ON_CHECKOUT));
			logger.debug(EELFLoggerDelegate.debugLogger, "getConnectionOnCheckout: property key {} value is {}",
					SystemProperties.TEST_CONNECTION_ON_CHECKOUT, testConnectionOnCheckout);
		} else {
			logger.info(EELFLoggerDelegate.errorLogger,
					"getConnectionOnCheckout: property key {} not found, using default value {}",
					SystemProperties.TEST_CONNECTION_ON_CHECKOUT, testConnectionOnCheckout);
		}
		return testConnectionOnCheckout;
	}

	/*
	 * TODO: Check whether it is appropriate to extend the list of tile
	 * definitions at every invocation.
	 */
	protected String[] tileDefinitions() {
		tileDefinitions.add("/WEB-INF/fusion/defs/definitions.xml");
		tileDefinitions.addAll(addTileDefinitions());

		return tileDefinitions.toArray(new String[0]);
	}

	/**
	 * Creates and returns a new empty list. This method should be overridden by
	 * child classes.
	 * 
	 * @return An empty list.
	 */
	public List<String> addTileDefinitions() {
		return new ArrayList<String>();
	}

	/**
	 * Creates and returns a new instance of a primary (order=1)
	 * {@link UrlBasedViewResolver} that finds files using the contents of
	 * definitions.xml files.
	 * 
	 * @return New instance of {@link UrlBasedViewResolver}
	 */
	@Bean
	public UrlBasedViewResolver tileViewResolver() {
		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
		viewResolver.setViewClass(TilesView.class);
		viewResolver.setOrder(1);
		return viewResolver;
	}

	/**
	 * Adds new instances of the following interceptors to the specified
	 * interceptor registry: {@link SessionTimeoutInterceptor},
	 * {@link ResourceInterceptor}
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SessionTimeoutInterceptor())
				.excludePathPatterns(getExcludeUrlPathsForSessionTimeout());
		registry.addInterceptor(resourceInterceptor());
	}

	/**
	 * Creates and returns a new instance of a {@link ResourceInterceptor}.
	 * 
	 * @return New instance of {@link ResourceInterceptor}
	 */
	@Bean
	public ResourceInterceptor resourceInterceptor() {
		return new ResourceInterceptor();
	}

	private String[] excludeUrlPathsForSessionTimeout = {};

	/**
	 * Gets the array of Strings that are paths excluded for session timeout.
	 * 
	 * @return Array of String
	 */
	public String[] getExcludeUrlPathsForSessionTimeout() {
		return excludeUrlPathsForSessionTimeout;
	}

	/**
	 * Sets the array of Strings that are paths excluded for session timeout.
	 * 
	 * @param excludeUrlPathsForSessionTimeout
	 *            Paths to exclude
	 */
	public void setExcludeUrlPathsForSessionTimeout(final String... excludeUrlPathsForSessionTimeout) {
		this.excludeUrlPathsForSessionTimeout = excludeUrlPathsForSessionTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appApplicationContext = applicationContext;

	}

}
