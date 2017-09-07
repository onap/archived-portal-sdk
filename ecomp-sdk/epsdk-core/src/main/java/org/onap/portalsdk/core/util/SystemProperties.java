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
package org.onap.portalsdk.core.util;

import javax.servlet.ServletContext;

import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * SystemProperties contains a list of constants used throughout portions of the
 * application. Populated by Spring from multiple configuration files.
 * 
 * Should be used like this:
 * 
 * <pre>
 * 
 * &#64;Autowired
 * SystemProperties systemProperties;
 * </pre>
 */
@Configuration
@PropertySource(value = { "${container.classpath:}/WEB-INF/conf/system.properties",
		"${container.classpath:}/WEB-INF/fusion/conf/fusion.properties",
		"${container.classpath:}/WEB-INF/conf/sql.properties" })
public class SystemProperties {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SystemProperties.class);

	private static Environment environment;

	private ServletContext servletContext;

	// keys used to reference values in the system properties file
	public static final String DOMAIN_CLASS_LOCATION = "domain_class_location";
	public static final String DEFAULT_ERROR_MESSAGE = "default_error_message";

	public static final String CSP_COOKIE_NAME = "csp_cookie_name";
	public static final String CSP_GATE_KEEPER_DATA_KEY = "csp_gate_keeper_data_key";
	public static final String CSP_GATE_KEEPER_PROD_KEY = "csp_gate_keeper_prod_key";
	public static final String CSP_LOGIN_URL = "csp_login_url";
	public static final String CSP_LOGOUT_URL = "csp_logout_url";

	public static final String WEB_JUNCTION_USER_ID_HEADER_NAME = "web_junction_user_id_header_name";

	public static final String AUTHENTICATION_MECHANISM = "authentication_mechanism";

	public static final String APPLICATION_NAME = "application_name";
	public static final String HIBERNATE_CONFIG_FILE_PATH = "hibernate_config_file_path";
	public static final String APPLICATION_USER_ID = "application_user_id";

	public static final String POST_INITIAL_CONTEXT_FACTORY = "post_initial_context_factory";
	public static final String POST_PROVIDER_URL = "post_provider_url";
	public static final String POST_SECURITY_PRINCIPAL = "post_security_principal";
	public static final String POST_MAX_RESULT_SIZE = "post_max_result_size";
	public static final String POST_DEFAULT_ROLE_ID = "post_default_role_id";

	public static final String FILES_PATH = "files_path";
	public static final String TEMP_PATH = "temp_path";

	public static final String NUM_UPLOAD_FILES = "num_upload_files";

	public static final String SYS_ADMIN_ROLE_ID = "sys_admin_role_id";

	public static final String SYS_ADMIN_ROLE_FUNCTION_DELETE_FROM_UI = "sys_admin_role_function_delete_from_ui";
	public static final String USER_NAME = "user_name";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String APP_DISPLAY_NAME = "app_display_name";
	// Application base URL is a proper prefix of the on-boarding URL
	public static final String APP_BASE_URL = "app_base_url";

	public static final String MENU_PROPERTIES_FILE_LOCATION = "menu_properties_file_location";
	public static final String MENU_QUERY_NAME = "menu_query_name";
	public static final String APPLICATION_MENU_SET_NAME = "application_menu_set_name";
	public static final String APPLICATION_MENU_ATTRIBUTE_NAME = "application_menu_attribute_name";
	public static final String APPLICATION_MENU_PROPERTIES_NAME = "application_menu_properties_name";
	public static final String BUSINESS_DIRECT_MENU_SET_NAME = "business_direct_menu_set_name";
	public static final String BUSINESS_DIRECT_MENU_ATTRIBUTE_NAME = "business_direct_menu_attribute_name";
	public static final String BUSINESS_DIRECT_MENU_PROPERTIES_NAME = "business_direct_menu_properties_name";
	public static final String RAPTOR_CONFIG_FILE_PATH = "raptor_config_file_path";
	public static final String HOMEPAGE_DATA_CALLBACK_CLASS = "homepage_data_callback_class";
	public static final String ERROR_EMAIL_DISTRIBUTION = "error_email_distribution";
	public static final String ERROR_EMAIL_SOURCE_ADDRESS = "error_email_source_address";
	public static final String ERROR_EMAIL_SUBJECT_LINE = "error_email_subject_line";
	public static final String PROFILE_SEARCH_REPORT_ID = "profile_search_report_id";
	public static final String CALLABLE_PROFILE_SEARCH_REPORT_ID = "callable_profile_search_report_id";
	public static final String CLUSTERED = "clustered";

	public static final String USER_ATTRIBUTE_NAME = "user_attribute_name";
	public static final String ROLES_ATTRIBUTE_NAME = "roles_attribute_name";
	public static final String ROLE_FUNCTIONS_ATTRIBUTE_NAME = "role_functions_attribute_name";
	public static final String CLIENT_DEVICE_ATTRIBUTE_NAME = "client_device_attribute_name";
	public static final String CLIENT_DEVICE_EMULATION = "client_device_emulation";
	public static final String CLIENT_DEVICE_TYPE_TO_EMULATE = "client_device_type_to_emulate";
	// File generation - Document
	public static final String TEMPLATES_PATH = "templates_path";
	public static final String DOCUMENT_XML_ENCODING = "document_xml_encoding";

	// Transaction
	public static final String ROUTING_DATASOURCE_KEY = "routing_datasource_key";

	// Document Library keys
	public static final String DOCLIB_ADMIN_ROLE_ID = "doclib_admin_role_id";
	public static final String DOCLIB_USER_ROLE_ID = "doclib_user_role_id";

	public static final String SYSTEM_PROPERTIES_FILENAME = "system.properties";
	public static final String FUSION_PROPERTIES_FILENAME = "fusion.properties";
	public static final String SUCCESS_TASKS_PROPERTIES_FILENAME = "success_tasks.properties";

	// login methods
	public static final String LOGIN_METHOD_CSP = "login_method_csp";
	public static final String LOGIN_METHOD_WEB_JUNCTION = "login_method_web_junction";
	public static final String LOGIN_METHOD_BACKDOOR = "login_method_backdoor";
	public static final String LOGIN_METHOD_ATTRIBUTE_NAME = "login_method_attribute_name";
	public static final String ROLE_FUNCTION_LIST = "role_function_list";

	// login error message keys
	public static final String MESSAGE_KEY_LOGIN_ERROR_COOKIE_EMPTY = "login.error.hrid.empty";
	public static final String MESSAGE_KEY_LOGIN_ERROR_HEADER_EMPTY = "login.error.header.empty";
	public static final String MESSAGE_KEY_LOGIN_ERROR_USER_INACTIVE = "login.error.user.inactive";
	public static final String MESSAGE_KEY_LOGIN_ERROR_USER_NOT_FOUND = "login.error.hrid.not-found";
	public static final String MESSAGE_KEY_LOGIN_ERROR_APPLICATION_LOCKED = "login.error.application.locked";
	public static final String MESSAGE_KEY_AUTOLOGIN_NONE = "webphone.autoimport.nouser";
	public static final String MESSAGE_KEY_AUTOLOGIN_MULTIPLE = "webphone.autoimport.multiple";

	// Application Mobile capability
	public static final String MOBILE_ENABLE = "mobile_enable";

	public static final String DATABASE_TIME_ZONE = "db.time_zone";

	public static final String AUTO_USER_IMPORT_ENABLE = "auto_user_import_enable";
	public static final String AUTO_USER_IMPORT_ROLE = "auto_user_import_role";

	public static final String ITRACKER_EMAIL_SOURCE_ADDRESS = "itracker_email_source_address";
	public static final String ITRACKER_EMAIL_DISTRIBUTION = "itracker_email_distribution";
	public static final String ITRACKER_SYSTEM_USER = "itracker_system_user_id";

	public static final String MAIL_SERVER_HOST = "mail_server_host";
	public static final String MAIL_SERVER_PORT = "mail_server_port";

	// Routing Data Source keys
	public static final String ROUTING_DATASOURCE_KEY_NON_XA = "NON-XA";
	public static final String ROUTING_DATASOURCE_KEY_XA = "XA";
	public static final String QUARTZ_JOB_ENABLED = "quartz_job_enable";
	public static final String WORKFLOW_EMAIL_SENDER = "workflow_email_sender";
	public static final String DROOLS_GUVNOR_HOME = "drools.guvnor.home";

	// Hibernate Config
	public static final String HB_DIALECT = "hb.dialect";
	public static final String HB_SHOW_SQL = "hb.show_sql";
	public static final String IDLE_CONNECTION_TEST_PERIOD = "hb.idle_connection_test_period";

	// DataSource
	public static final String DB_DRIVER = "db.driver";
	public static final String DB_CONNECTIONURL = "db.connectionURL";
	public static final String DB_USERNAME = "db.userName";
	/** @deprecated this variable is used in many places so don't remove */
	public static final String DB_PASSWOR = "db.password";
	public static final String DB_PASSWORD = "db.password";
	public static final String DB_MIN_POOL_SIZE = "db.min_pool_size";
	public static final String DB_MAX_POOL_SIZE = "db.max_pool_size";
	public static final String TEST_CONNECTION_ON_CHECKOUT = "db.test_connection_on_checkout";
	public static final String PREFERRED_TEST_QUERY = "db.preferred_test_query";

	public static final String MYLOGINS_FEED_CRON = "mylogins_feed_cron";
	public static final String SESSIONTIMEOUT_FEED_CRON = "sessiontimeout_feed_cron";
	public static final String LOG_CRON = "log_cron";

	public static final String DB_ENCRYPT_FLAG = "db.encrypt_flag";

	// Decryption Key
	public static final String Decryption_Key = "decryption_key";

	// Logging/Audit Fields
	public static final String MDC_APPNAME = "AppName";
	public static final String MDC_REST_PATH = "RestPath";
	public static final String MDC_REST_METHOD = "RestMethod";
	public static final String INSTANCE_UUID = "instance_uuid";
	public static final String MDC_CLASS_NAME = "ClassName";
	public static final String MDC_LOGIN_ID = "LoginId";
	public static final String MDC_TIMER = "Timer";
	public static final String SDK_NAME = "ECOMP_SDK";
	public static final String ECOMP_REQUEST_ID = "X-ECOMP-RequestID";
	public static final String PARTNER_NAME = "PartnerName";
	public static final String FULL_URL = "Full-URL";
	public static final String AUDITLOG_BEGIN_TIMESTAMP = "AuditLogBeginTimestamp";
	public static final String AUDITLOG_END_TIMESTAMP = "AuditLogEndTimestamp";
	public static final String METRICSLOG_BEGIN_TIMESTAMP = "MetricsLogBeginTimestamp";
	public static final String METRICSLOG_END_TIMESTAMP = "MetricsLogEndTimestamp";
	public static final String CLIENT_IP_ADDRESS = "ClientIPAddress";
	public static final String STATUS_CODE = "StatusCode";
	public static final String RESPONSE_CODE = "ResponseCode";
	// Component or sub component name
	public static final String TARGET_ENTITY = "TargetEntity";
	// API or operation name
	public static final String TARGET_SERVICE_NAME = "TargetServiceName";

	// Logging Compliance
	public static final String DOUBLE_WHITESPACE_SEPARATOR = "  ";
	public static final String SINGLE_WHITESPACE_SEPARATOR = " ";
	public static final String SINGLE_QUOTE = "'";
	public static final String NA = "N/A";
	public static final String UNKNOWN = "Unknown";
	public static final String SECURITY_LOG_TEMPLATE = "Protocol:{0}  Security-Event-Type:{1}  Login-ID:{2}  {3}";
	public static final String ECOMP_PORTAL_BE = "ECOMP_PORTAL_BE";
	public static final String PROTOCOL = "PROTOCOL";
	public static final String SECURIRY_EVENT_TYPE = "SECURIRY_EVENT_TYPE";
	public static final String LOGIN_ID = "LOGIN_ID";
	public static final String ACCESSING_CLIENT = "ACCESSING_CLIENT";
	public static final String RESULT_STR = "RESULT";
	public static final String ECOMP_PORTAL_FE = "ECOMP_PORTAL_FE";
	public static final String ADDITIONAL_INFO = "ADDITIONAL_INFO";
	public static final String INTERFACE_NAME = "INTERFACE_NAME";
	public static final String USERAGENT_NAME = "user-agent";

	// Protocols
	public static final String HTTP = "HTTP";
	public static final String HTTPS = "HTTPS";
	public static final String SSO_VALUE = "sso";

	// Menu
	public static final String CONTACT_US_LINK = "contact_us_link";

	// Left Menu
	public static final String LEFT_MENU_PARENT = "parentList";
	public static final String LEFT_MENU_CHILDREND = "childItemList";

	public enum RESULT_ENUM {
		SUCCESS, FAILURE
	}

	public enum SecurityEventTypeEnum {
		FE_LOGIN_ATTEMPT, FE_LOGOUT, SSO_LOGIN_ATTEMPT_PHASE_1, SSO_LOGIN_ATTEMPT_PHASE_2, SSO_LOGOUT, LDAP_PHONEBOOK_USER_SEARCH, INCOMING_REST_MESSAGE, OUTGOING_REST_MESSAGE, REST_AUTHORIZATION_CREDENTIALS_MODIFIED, ECOMP_PORTAL_USER_MODIFIED, ECOMP_PORTAL_USER_ADDED, ECOMP_PORTAL_USER_REMOVED, ECOMP_PORTAL_WIDGET, INCOMING_UEB_MESSAGE, ECOMP_PORTAL_HEALTHCHECK
	}

	public SystemProperties() {
		super();
	}

	protected Environment getEnvironment() {
		return environment;
	}

	@Autowired
	public void setEnvironment(Environment environment) {
		SystemProperties.environment = environment;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Tests whether a property value is available for the specified key.
	 * 
	 * @param key
	 *            Property key
	 * @return True if the key is known, otherwise false.
	 */
	public static boolean containsProperty(String key) {
		return environment.containsProperty(key);
	}

	/**
	 * Returns the property value associated with the given key (never
	 * {@code null}), after trimming any trailing space.
	 * 
	 * @param key
	 *            Property key
	 * @return Property value; the empty string if the environment was not
	 *         autowired, which should never happen.
	 * @throws IllegalStateException
	 *             if the key is not found
	 */
	public static String getProperty(String key) {
		String value = "";
		if (environment == null) {
			logger.error(EELFLoggerDelegate.errorLogger, "getProperty: environment is null, should never happen!");
		} else {
			value = environment.getRequiredProperty(key);
			// java.util.Properties preserves trailing space
			if (value != null)
				value = value.trim();
		}
		return value;
	}

	/**
	 * Gets the property value for the key {@link #APPLICATION_NAME}.
	 * 
	 * method created to get around JSTL 1.0 limitation of not being able to access
	 * a static method of a bean
	 * 
	 * @return Application name
	 */
	public String getApplicationName() {
		return getProperty(APPLICATION_NAME);
	}

	/**
	 * Gets the property value for the key {@link #APP_DISPLAY_NAME}.
	 * 
	 * @return Application display name
	 */
	public String getAppDisplayName() {
		return getProperty(APP_DISPLAY_NAME);
	}

}
