package org.openecomp.portalsdk.core.service;

import javax.servlet.http.HttpServletRequest;

public interface UrlAccessService {
	
	/**
	 * Answers whether the specified URL is accessible.
	 * 
	 * @param request
	 * @param currentUrl
	 * @return true if yes, false if no.
	 */
	public  boolean isUrlAccessible(HttpServletRequest request, String currentUrl);
}
