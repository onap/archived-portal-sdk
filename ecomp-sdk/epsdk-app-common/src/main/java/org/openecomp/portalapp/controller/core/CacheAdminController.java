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
package org.openecomp.portalapp.controller.core;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jcs.JCS;
import org.apache.jcs.admin.CacheRegionInfo;
import org.apache.jcs.admin.JCSAdminBean;
import org.apache.jcs.engine.behavior.ICacheElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
@RequestMapping("/")
public class CacheAdminController extends RestrictedBaseController {

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CacheAdminController.class);

	private JCSAdminBean jcsAdminBean = new JCSAdminBean();

	@RequestMapping(value = { "/jcs_admin" }, method = RequestMethod.GET)
	public ModelAndView cacheAdmin(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("model", getRegions());

		return new ModelAndView(getViewName(), model);
	}

	@RequestMapping(value = { "/get_regions" }, method = RequestMethod.GET)
	public void getRegions(HttpServletRequest request, HttpServletResponse response) {
		try {
			JsonMessage msg = new JsonMessage(getRegions().toString());
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRegions failed", e);
		}
	}

	@RequestMapping(value = { "/jcs_admin/clearRegion" }, method = RequestMethod.GET)
	public void clearRegion(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String cacheName = (String) request.getParameter("cacheName");
		clearCacheRegion(cacheName);

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write(getRegions().toString());
	}

	@RequestMapping(value = { "/jcs_admin/clearAll" }, method = RequestMethod.GET)
	public void clearAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		clearAllRegions();

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write(getRegions().toString());
	}

	@RequestMapping(value = { "/jcs_admin/clearItem" }, method = RequestMethod.GET)
	public void clearItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String keyName = (String) request.getParameter("keyName");
		String cacheName = (String) request.getParameter("cacheName");
		clearCacheRegionItem(cacheName, keyName);

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write(getRegions().toString());
	}

	@RequestMapping(value = { "/jcs_admin/showItemDetails" }, method = RequestMethod.GET)
	public void showItemDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String cacheName = (String) request.getParameter("cacheName");
		String keyName = (String) request.getParameter("keyName");
		String details = null;

		try {
			details = getItemDetails(cacheName, keyName);
		} catch (Exception e) {
			details = "There was an error retrieving the region details. Please try again.";
			logger.error(EELFLoggerDelegate.errorLogger, "showItemDetails failed for cache name " + cacheName, e);
		}
		JSONObject j = new JSONObject(details);
		response.setContentType("application/json");
		// response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.write(j.toString());
	}

	@RequestMapping(value = { "/jcs_admin/showRegionDetails" }, method = RequestMethod.GET)
	public void showRegionDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String cacheName = (String) request.getParameter("cacheName");
		String details = null;
		ObjectMapper mapper = new ObjectMapper();
		JSONObject j = null;
		try {
			details = getRegionStats(cacheName);
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(details));
			j = new JSONObject(msg);

		} catch (Exception e) {
			details = "There was an error retrieving the region details. Please try again.";
			logger.error(EELFLoggerDelegate.errorLogger, "showRegionDetailed failed for cache name " + cacheName, e);
		}

		response.setContentType("application/json");

		PrintWriter out = response.getWriter();
		// out.write(details);
		out.write(j.toString());
	}

	@SuppressWarnings("unchecked")
	public JSONArray getRegions() {
		LinkedList<CacheRegionInfo> regions = null;
		JSONArray ja = new JSONArray();
		try {
			regions = getJcsAdminBean().buildCacheInfo();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			for (CacheRegionInfo cri : regions) {
				if (cri.getCache().getCacheName() != null && !cri.getCache().getCacheName().equals("[object Object]")) {
					JSONObject jo = new JSONObject();
					jo.put("cacheName", cri.getCache().getCacheName());
					jo.put("size", cri.getCache().getSize());
					jo.put("byteCount", cri.getByteCount());
					jo.put("status", cri.getStatus());
					jo.put("hitCountRam", cri.getCache().getHitCountRam());
					jo.put("hitCountAux", cri.getCache().getHitCountAux());
					jo.put("missCountNotFound", cri.getCache().getMissCountNotFound());
					jo.put("missCountExpired", cri.getCache().getMissCountExpired());
					jo.put("items",
							new JSONArray(mapper.writeValueAsString(getRegionItems(cri.getCache().getCacheName()))));
					ja.put(jo);
				}
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRegions failed", e);
		}

		return ja;
	}

	private String getRegionStats(String cacheName) throws Exception {
		String stats = "";

		JCS cache = JCS.getInstance(cacheName);
		stats = cache.getStats();

		return stats;
	}

	private String getItemDetails(String cacheName, String keyName) throws Exception {
		String details = "";

		JCS cache = JCS.getInstance(cacheName);
		ICacheElement element = cache.getCacheElement(keyName);

		if (element != null) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			details = mapper.writeValueAsString(element);
		}

		return details;
	}

	@SuppressWarnings("rawtypes")
	private List getRegionItems(String cacheName) {
		List items = null;

		try {
			items = getJcsAdminBean().buildElementInfo(cacheName);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRegionItems failed for cache name " + cacheName, e);
		}
		return items;
	}

	private void clearAllRegions() {
		try {
			getJcsAdminBean().clearAllRegions();
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "clearAllRegions faield", e);
		}
	}

	private void clearCacheRegion(String cacheName) {
		try {
			getJcsAdminBean().clearRegion(cacheName);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "clearCacheRegion failed for cache name " + cacheName, e);
		}
	}

	private void clearCacheRegionItem(String cacheName, String keyName) {
		try {
			getJcsAdminBean().removeItem(cacheName, keyName);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "clearCacheRegionItem failed for key name " + keyName, e);
		}
	}

	public JCSAdminBean getJcsAdminBean() {
		return jcsAdminBean;
	}

	public void setJcsAdminBean(JCSAdminBean jcsAdminBean) {
		this.jcsAdminBean = jcsAdminBean;
	}
}
