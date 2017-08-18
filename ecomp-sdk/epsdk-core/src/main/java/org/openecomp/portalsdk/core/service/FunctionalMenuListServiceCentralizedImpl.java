package org.openecomp.portalsdk.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class FunctionalMenuListServiceCentralizedImpl implements FunctionalMenuListService{

private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FunctionalMenuListServiceCentralizedImpl.class);
	
	@Autowired
	private RestApiRequestBuilder restApiRequestBuilder;

	@SuppressWarnings("unchecked")
	@Override
	public List<RoleFunction> getFunctionCDList(HttpServletRequest request) throws Exception {
		User user = UserUtils.getUserSession(request);
		ObjectMapper mapper = new ObjectMapper();
		List roleFunctionFinalList = new ArrayList<>();
		try {
			String menuList = restApiRequestBuilder.getViaREST("/menuFunctions", true, user.getOrgUserId());
			roleFunctionFinalList = mapper.readValue(menuList,
					TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getFunctionCDList Failed", e);
			throw new Exception(e.getMessage());
		}
		return roleFunctionFinalList;
	}

	
}
