package org.openecomp.portalsdk.core.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.RoleFunction;

public interface FunctionalMenuListService {
	
	List<RoleFunction> getFunctionCDList(HttpServletRequest request) throws Exception;

}
