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
package org.openecomp.portalsdk.analytics.system.fusion.web;


import java.util.*;

import javax.servlet.http.*;

import org.openecomp.portalsdk.analytics.system.fusion.domain.CR_Report;
import org.openecomp.portalsdk.analytics.system.fusion.service.RaptorService;
import org.springframework.web.bind.*;
import org.springframework.web.servlet.*;


public class ReportsSearchListController  {
    
    private RaptorService raptorService =  null;

    /**
	 * @return the raptorService
	 */
	public RaptorService getRaptorService() {
		return raptorService;
	}

	/**
	 * @param raptorService the raptorService to set
	 */
	public void setRaptorService(RaptorService raptorService) {
		this.raptorService = raptorService;
	}

	public ModelAndView handleRequestInternal(HttpServletRequest request,
                                              HttpServletResponse response) {

        /*List   items  = null;
        int    reportId = ServletRequestUtils.getIntParameter(request, "report_id", 0);
        String task   = ServletRequestUtils.getStringParameter(request, "task", TASK_GET);

        HashMap  additionalParams = new HashMap();
        additionalParams.put(Parameters.PARAM_HTTP_REQUEST, request);

        if (reportId != 0 && task.equals(TASK_DELETE)) { // delete the selected record
          getRaptorService().deleteReport(new Long(reportId));
        }
  
        items = getRaptorService().getReports();

        Map model = new HashMap();
        model.put("items", items);

        return new ModelAndView(getViewName(), "model", model);
        */
		//return new ModelAndView(getViewName(), "model", null);
		System.out.println("Fill with proper code");
		return null;
    }

}
