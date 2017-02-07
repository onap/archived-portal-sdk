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
package org.openecomp.portalsdk.core.onboarding.crossapi;

public interface PortalApiConstants {
	public static final String API_PREFIX = "/api";
	public static final String PORTAL_JSESSION_ID = "PORTAL_JSESSION_ID";
	public static final String PORTAL_JSESSION_BIND = "PORTAL_JSESSION_BIND";
	public static final String ACTIVE_USERS_NAME = "activeUsers";
	
	/** Portal service cookie name */
	public static final String EP_SERVICE = "EPService";

	public static final String GLOBAL_SESSION_MAX_IDLE_TIME = "global_session_max_idle_time";
	public static final String PORTAL_SESSION_SLOT_CHECK = "portal_session_slot_check";
	public static final String SESSION_PREVIOUS_ACCESS_TIME = "session_previous_access_time";
	public static final String MAX_IDLE_TIME = "max.idle.time";

	// Names of keys in the portal.properties file
	public static final String PORTAL_API_IMPL_CLASS = "portal.api.impl.class";
	public static final String ECOMP_REDIRECT_URL = "ecomp_redirect_url";
	public static final String ECOMP_REST_URL = "ecomp_rest_url";
	
	// UEB related
    public static final String UEB_URL_LIST = "ueb_url_list"; // In properties file
    public static final String ECOMP_PORTAL_INBOX_NAME = "ecomp_portal_inbox_name";
    public static final String ECOMP_DEFAULT_MSG_ID = "0";
    public static final String ECOMP_GENERAL_UEB_PARTITION = "EPGeneralPartition";
    public static final String UEB_LISTENERS_ENABLE = "ueb_listeners_enable";
    public static final String UEB_APP_INBOUND_MAILBOX_NAME = "ueb_app_mailbox_name";
    public static final String UEB_APP_CONSUMER_GROUP_NAME = "ueb_app_consumer_group_name";
    // UebManager generates a consumer group name for special token {UUID} 
    public static final String UEB_APP_CONSUMER_GROUP_NAME_GENERATOR = "{UUID}";
    public static final String UEB_APP_KEY = "ueb_app_key";
    public static final String UEB_APP_SECRET = "ueb_app_secret";
	public static final String ECOMP_UEB_INVALID_MSG   = "100: Invalid Message format.";
    public static final String ECOMP_UEB_TIMEOUT_ERROR = "101: Timeout"; 
    public static final String ECOMP_UEB_UNKNOWN_PUBLISH_ERROR = "102: Unknown error during publish";
    public static final String ECOMP_UEB_UNKNOWN_CONSUME_ERROR = "103: Unknown error during consume";
    public static final String USE_REST_FOR_FUNCTIONAL_MENU = "use_rest_for_functional_menu";
    
    //encrpt key
    public static final String Decryption_Key = "decryption_key";

}
