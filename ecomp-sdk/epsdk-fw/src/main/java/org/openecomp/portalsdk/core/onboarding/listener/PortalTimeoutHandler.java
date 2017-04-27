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
package org.openecomp.portalsdk.core.onboarding.listener;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.portalsdk.core.onboarding.crossapi.SessionCommunicationService;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;
import org.openecomp.portalsdk.core.restful.domain.PortalTimeoutVO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Invoked by listeners (UserContextListener and UserSessionListener) to track
 * user sessions.
 */
public class PortalTimeoutHandler {

	protected static final SessionCommInf sessionComm = new SessionComm();

	public interface SessionCommInf {
		public Integer fetchSessionSlotCheckInterval(String... params) throws Exception;

		public void extendSessionTimeOuts(String... sessionMap) throws Exception;
	}

	public static class SessionComm implements SessionCommInf {
		public Integer fetchSessionSlotCheckInterval(String... params) throws Exception {

			String ecompRestURL = params[0];
			String userName = params[1];
			String pwd = params[2];
			String uebKey = params[3];

			String sessionSlot = SessionCommunicationService.getSessionSlotCheckInterval(ecompRestURL, userName, pwd,
					uebKey);
			if (sessionSlot == null)
				return null;
			return Integer.parseInt(sessionSlot);
		}

		public void extendSessionTimeOuts(String... params) throws Exception {

			String ecompRestURL = params[0];
			String userName = params[1];
			String pwd = params[2];
			String uebKey = params[3];
			String sessionTimeoutMap = params[4];

			SessionCommunicationService.requestPortalSessionTimeoutExtension(ecompRestURL, userName, pwd, uebKey,
					sessionTimeoutMap);
		}
	}

	public static final Map<String, HttpSession> sessionMap = new Hashtable<String, HttpSession>();
	public static final Integer repeatInterval = 15 * 60; // 15 minutes
	protected static final Log logger = LogFactory.getLog(PortalTimeoutHandler.class);
	static ObjectMapper mapper = new ObjectMapper();
	private static PortalTimeoutHandler timeoutHandler;

	public static PortalTimeoutHandler getInstance() {
		if (timeoutHandler == null)
			timeoutHandler = new PortalTimeoutHandler();

		return timeoutHandler;
	}

	/**
	 * TODO: remove static
	 * 
	 * @param portalJSessionId
	 * @param jSessionId
	 * @param session
	 */
	public static void sessionCreated(String portalJSessionId, String jSessionId, HttpSession session) {

		storeMaxInactiveTime(session);

		// this key is a combination of portal jsession id and app session id
		String jSessionKey = jSessionKey(jSessionId, portalJSessionId);
		Object jSessionKeySessionVal = session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID);

		// do not reset the attributes if the same values have already been set
		// because that will cause PortalTimeoutBindingListener to unbound the
		// value from map
		if (jSessionKeySessionVal != null && jSessionKeySessionVal.equals(jSessionKey)) {
			logger.debug(" Session Values already exist in te map for sessionKey " + jSessionKey);
			return;
		}

		session.setAttribute(PortalApiConstants.PORTAL_JSESSION_ID, jSessionKey);

		// session binding listener will add this value to the static map
		// and with session replication the listener will fire in all tomcat
		// instances
		session.setAttribute(PortalApiConstants.PORTAL_JSESSION_BIND, new PortalTimeoutBindingListener());
		// sessionMap.put((String)session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID),
		// session);

	}

	/**
	 * TODO: remove static
	 * 
	 * @param session
	 */
	protected static void storeMaxInactiveTime(HttpSession session) {
		if (session.getAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME) == null)
			session.setAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME, session.getMaxInactiveInterval());
	}

	/**
	 * TODO: remove static
	 * 
	 * @param session
	 */
	public static void sessionDestroyed(HttpSession session) {
		try {
			logger.info(" Session getting destroyed - id: " + session.getId());
			session.removeAttribute(PortalApiConstants.PORTAL_JSESSION_BIND);
			// sessionMap.remove((String)session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID));
		} catch (Exception e) {
			logger.error("sessionDestroyed failed", e);
		}
	}

	/***
	 * TODO: remove static
	 * 
	 * @param portalJSessionId
	 * @return true on success, false if the session cannot be found, etc.
	 */
	public static boolean invalidateSession(String portalJSessionId) {
		boolean result = false;
		logger.debug("Session Management: request from Portal to invalidate the session: " + portalJSessionId);
		for (String jSessionKey : sessionMap.keySet()) {
			try {
				HttpSession session = sessionMap.get(jSessionKey);
				if (portalJSessionId(jSessionKey).equals(portalJSessionId)) {
					session.invalidate();
					result = true;
				}
			} catch (Exception e) {
				logger.error("invalidateSession failed", e);
			}
		}
		return result;
	}

	/**
	 * TODO: remove static
	 * 
	 * @return json version of the timeout map: session ID -> timeout object
	 */
	public static String gatherSessionExtensions() {
		logger.debug("Session Management: gatherSessionExtensions");

		Map<String, PortalTimeoutVO> sessionTimeoutMap = new Hashtable<String, PortalTimeoutVO>();
		String jsonMap = "";

		for (String jSessionKey : sessionMap.keySet()) {

			try {
				// get the expirytime in seconds
				HttpSession session = sessionMap.get(jSessionKey);

				Long lastAccessedTimeMilliSec = session.getLastAccessedTime();
				Long maxIntervalMilliSec = session.getMaxInactiveInterval() * 1000L;
				// Long currentTimeMilliSec =
				// Calendar.getInstance().getTimeInMillis() ;
				// (maxIntervalMilliSec - (currentTimeMilliSec -
				// lastAccessedTimeMilliSec) + ;
				Calendar instance = Calendar.getInstance();
				instance.setTimeInMillis(session.getLastAccessedTime());
				logger.debug("Session Management: Last Accessed time for " + jSessionKey + ": " + instance.getTime());

				Long sessionTimOutMilliSec = maxIntervalMilliSec + lastAccessedTimeMilliSec;

				sessionTimeoutMap.put(portalJSessionId(jSessionKey),
						getSingleSessionTimeoutObj(jSessionKey, sessionTimOutMilliSec));
				logger.debug("Session Management: putting session in map " + jSessionKey + " sessionTimoutSec"
						+ (int) (sessionTimOutMilliSec / 1000));

				jsonMap = mapper.writeValueAsString(sessionTimeoutMap);

			} catch (Exception e) {
				logger.error("gatherSessionExtensions failed", e);
			}

		}

		return jsonMap;

	}

	/**
	 * TODO: remove static
	 * 
	 * @param sessionTimeoutMapStr
	 * @return true on success, false otherwise
	 * @throws Exception
	 */
	public static boolean updateSessionExtensions(String sessionTimeoutMapStr) throws Exception {
		logger.debug("Session Management: updateSessionExtensions");
		// Map<String,Object> sessionTimeoutMap =
		// mapper.readValue(sessionTimeoutMapStr, Map.class);
		Map<String, PortalTimeoutVO> sessionTimeoutMap = null;

		try {
			TypeReference<Hashtable<String, PortalTimeoutVO>> typeRef = new TypeReference<Hashtable<String, PortalTimeoutVO>>() {
			};
			sessionTimeoutMap = mapper.readValue(sessionTimeoutMapStr, typeRef);
		} catch (Exception e) {
			logger.error("updateSessionExtensions failed to parse the sessionTimeoutMap from portal", e);
			return false;
		}

		boolean result = true;
		for (String jPortalSessionId : sessionTimeoutMap.keySet()) {
			try {
				PortalTimeoutVO extendedTimeoutVO = mapper.readValue(
						mapper.writeValueAsString(sessionTimeoutMap.get(jPortalSessionId)), PortalTimeoutVO.class);
				HttpSession session = sessionMap.get(jSessionKey(extendedTimeoutVO.getjSessionId(), jPortalSessionId));

				if (session == null) {
					continue;
				}

				Long lastAccessedTimeMilliSec = session.getLastAccessedTime();
				Long maxIntervalMilliSec = session.getMaxInactiveInterval() * 1000L;
				Long sessionTimOutMilliSec = maxIntervalMilliSec + lastAccessedTimeMilliSec;

				Long maxTimeoutTimeMilliSec = extendedTimeoutVO.getSessionTimOutMilliSec();
				if (maxTimeoutTimeMilliSec > sessionTimOutMilliSec) {
					session.setMaxInactiveInterval((int) (maxTimeoutTimeMilliSec - lastAccessedTimeMilliSec) / 1000);
					logger.debug("Session Management: extended session for :" + session.getId() + " to :"
							+ (int) (maxTimeoutTimeMilliSec / 1000));
					// System.out.println("!!!!!!!!!extended session for :" +
					// session.getId() + " to :" +
					// (int)(maxTimeoutTimeMilliSec/1000));
				}
			} catch (Exception e) {
				logger.error("updateSessionExtensions failed to update session timeouts", e);
				// Signal a problem if any one of them fails
				result = false;
			}

		}
		return result;
	}

	/**
	 * TODO: Remove static
	 * 
	 * @param request
	 * @param userName
	 * @param pwd
	 * @param ecompRestURL
	 * @param _sessionComm
	 */
	public static void handleSessionUpdatesNative(HttpServletRequest request, String userName, String pwd,
			String uebKey, String ecompRestURL, SessionCommInf _sessionComm) {

		if (_sessionComm == null) {
			_sessionComm = sessionComm;
		}
		try {
			synchronizeSessionForLastMinuteRequests(request, ecompRestURL, userName, pwd, uebKey, _sessionComm);
		} catch (Exception e) {
			logger.error("handleSesionUpdatesNative failed", e);
		}
		resetSessionMaxIdleTimeOut(request);
	}

	/**
	 * TODO: remove Static
	 * 
	 * @param request
	 * @param ecompRestURL
	 * @param userName
	 * @param pwd
	 * @param _sessionComm
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	public static void synchronizeSessionForLastMinuteRequests(HttpServletRequest request, String ecompRestURL,
			String userName, String pwd, String uebKey, SessionCommInf _sessionComm)
			throws JsonProcessingException, Exception {

		HttpSession session = request.getSession(false);
		if (session == null)
			return;

		Object portalSessionSlotCheckObj = session.getServletContext()
				.getAttribute(PortalApiConstants.PORTAL_SESSION_SLOT_CHECK);
		Integer portalSessionSlotCheckinMilliSec = 5 * 60 * 1000; // (5 minutes)
		if (portalSessionSlotCheckObj != null) {
			portalSessionSlotCheckinMilliSec = Integer.valueOf(portalSessionSlotCheckObj.toString());
		} else {
			portalSessionSlotCheckObj = _sessionComm
					.fetchSessionSlotCheckInterval(new String[] { ecompRestURL, userName, pwd, uebKey });
			logger.debug("Fetching Portal Session Slot Object: " + portalSessionSlotCheckObj);
			if (portalSessionSlotCheckObj != null) {
				portalSessionSlotCheckinMilliSec = Integer.valueOf(portalSessionSlotCheckObj.toString());
				session.getServletContext().setAttribute(PortalApiConstants.PORTAL_SESSION_SLOT_CHECK,
						portalSessionSlotCheckinMilliSec);
			}
		}

		Object previousToLastAccessTimeObj = session.getAttribute(PortalApiConstants.SESSION_PREVIOUS_ACCESS_TIME);
		final long lastAccessedTimeMilliSec = session.getLastAccessedTime();
		if (previousToLastAccessTimeObj == null) {
			previousToLastAccessTimeObj = lastAccessedTimeMilliSec;
			session.setAttribute(PortalApiConstants.SESSION_PREVIOUS_ACCESS_TIME, previousToLastAccessTimeObj);
		} else {
			Long previousToLastAccessTime = (Long) previousToLastAccessTimeObj;
			final int maxIntervalMilliSec = session.getMaxInactiveInterval() * 1000;
			if (maxIntervalMilliSec
					- (lastAccessedTimeMilliSec - previousToLastAccessTime) <= portalSessionSlotCheckinMilliSec) {

				String jSessionKey = (String) session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID);
				Map<String, PortalTimeoutVO> sessionTimeoutMap = new Hashtable<String, PortalTimeoutVO>();
				Long sessionTimOutMilliSec = maxIntervalMilliSec + lastAccessedTimeMilliSec;

				sessionTimeoutMap.put(PortalTimeoutHandler.portalJSessionId(jSessionKey),
						PortalTimeoutHandler.getSingleSessionTimeoutObj(jSessionKey, sessionTimOutMilliSec));
				String jsonMap = mapper.writeValueAsString(sessionTimeoutMap);
				logger.debug("Extension requested for all the Apps and Portal;  JessionKey: " + jSessionKey
						+ "; SessionMap: " + sessionTimeoutMap);
				_sessionComm.extendSessionTimeOuts(new String[] { ecompRestURL, userName, pwd, uebKey, jsonMap });
			}

		}
	}

	/**
	 * TODO: remove static
	 * 
	 * @param request
	 */
	public static void resetSessionMaxIdleTimeOut(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession(false);
			if (session == null)
				return;
			final Object maxIdleAttribute = session.getAttribute(PortalApiConstants.GLOBAL_SESSION_MAX_IDLE_TIME);
			if (maxIdleAttribute != null) {
				session.setMaxInactiveInterval(Integer.parseInt(maxIdleAttribute.toString()));
			}
		} catch (Exception e) {
			logger.error("resetSessionMaxIdleTimeout failed", e);
		}

	}

	/**
	 * 
	 * @param jSessionKey
	 * @param sessionTimOutMilliSec
	 * @return
	 */
	private static PortalTimeoutVO getSingleSessionTimeoutObj(String jSessionKey, Long sessionTimOutMilliSec) {
		return new PortalTimeoutVO(jSessionId(jSessionKey), sessionTimOutMilliSec);
	}

	/**
	 * 
	 * @param jSessionId
	 * @param portalJSessionId
	 * @return
	 */
	private static String jSessionKey(String jSessionId, String portalJSessionId) {
		return portalJSessionId + "-" + jSessionId;
	}

	/**
	 * 
	 * @param jSessionKey
	 * @return
	 */
	private static String portalJSessionId(String jSessionKey) {
		return jSessionKey.split("-")[0];
	}

	/**
	 * 
	 * @param jSessionKey
	 * @return
	 */
	private static String jSessionId(String jSessionKey) {
		return jSessionKey.split("-")[1];
	}

}