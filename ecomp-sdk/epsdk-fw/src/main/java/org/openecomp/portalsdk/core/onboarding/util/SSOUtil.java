package org.openecomp.portalsdk.core.onboarding.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SSOUtil {

	private static final Log logger = LogFactory.getLog(SSOUtil.class);

	/**
	 * Constructs a path for this server, this app's context, etc.
	 * 
	 * @param request
	 * @param response
	 * @param forwardPath
	 * @return
	 */
	public static String getECOMPSSORedirectURL(HttpServletRequest request, HttpServletResponse response,
			String forwardPath) {
		String appURL = (request.isSecure() ? "https://" : "http://") + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/" + forwardPath;
		String encodedAppURL = null;
		try {
			encodedAppURL = URLEncoder.encode(appURL, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			logger.error("getECOMPSSORedirectURL: Failed to encode app URL "
			 + appURL);
		}
		String portalURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
		if (portalURL == null || portalURL.length() == 0) {
			logger.error("getECOMPSSORedirectURL: Failed to get property " +
			 PortalApiConstants.ECOMP_REDIRECT_URL);
			return null;
		}
		String redirectURL = portalURL + "?redirectUrl=" + encodedAppURL;
		return redirectURL;
	}

}
