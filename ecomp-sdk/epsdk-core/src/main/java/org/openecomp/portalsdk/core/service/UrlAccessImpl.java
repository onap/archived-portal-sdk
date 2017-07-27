package org.openecomp.portalsdk.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.UrlsAccessible;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("urlAccessService")
public class UrlAccessImpl implements UrlAccessService{

	 @Autowired
	 DataAccessService dataAccessService;
	
	@Override
	public boolean isUrlAccessible(HttpServletRequest request, String currentUrl) {
		boolean isAccessible = false;
		Map<String, String> params = new HashMap<>();
		params.put("current_url", currentUrl);
		List list = dataAccessService.executeNamedQuery("restrictedUrls", params, null);
		
		// loop through the list of restricted URL's
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				UrlsAccessible urlFunctions = (UrlsAccessible) list.get(i);
				// String url = (String) urlFunctions.getUrl();
				String functionCd = (String) urlFunctions.getFunctionCd();
				if (UserUtils.isAccessible(request, functionCd)) {
					isAccessible = true;
				}
			}
			return isAccessible;
	     	}
		return true;
	}

}
