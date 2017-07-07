package org.openecomp.portalsdk.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.exception.SessionExpiredException;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class UrlAccessCentalizedImpl implements UrlAccessService {
	
	@Autowired
	AppService appService;	
	
	@Autowired
	RoleService roleService;


	@Override
	public boolean isUrlAccessible(HttpServletRequest request, String currentUrl) {
		
		boolean isAccessible = false;
		User user = UserUtils.getUserSession(request);
		
		
		HttpSession session = AppUtils.getSession(request);

		if (session == null) {
			throw new SessionExpiredException();
		}

		@SuppressWarnings("unchecked")
		List<RoleFunction> allRoleFunctionsList  = (List<RoleFunction>) session.getAttribute(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTION_LIST));
				
		List<String> allUrls = new ArrayList<String>();

		for (int i = 0; i < allRoleFunctionsList.size(); i++) {
			if (allRoleFunctionsList.get(i).getCode() != null && ((String) allRoleFunctionsList.get(i).getCode()).substring(0, 4).toUpperCase().equals("url_".toUpperCase())) {
				String functionCd = ((String) allRoleFunctionsList.get(i).getCode()).substring(4).toUpperCase();
				allUrls.add(functionCd);
			}
		}

		@SuppressWarnings("unchecked")
		Set<RoleFunction> roleFunction = UserUtils.getRoleFunctions(request);
		List list = new ArrayList<>(roleFunction);
		List<String> UserURLlist = new ArrayList<String>();
		
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != null && ((String) list.get(i)).substring(0, 4).toUpperCase().equals("url_".toUpperCase())) {
					String functionCd = ((String) list.get(i)).substring(4).toUpperCase();
					UserURLlist.add(functionCd);
				}
			}
		}
		
		if((!UserURLlist.contains(currentUrl) && !allUrls.contains(currentUrl)) || (UserURLlist.contains(currentUrl) && allUrls.contains(currentUrl)))
		{
			 isAccessible = true;
		}else {
			 isAccessible = false;
	     }
	return isAccessible;

	}
	
	
}
