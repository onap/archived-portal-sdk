package org.openecomp.portalsdk.core.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;

public class FunctionalMenuListServiceImpl implements FunctionalMenuListService{
	
private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FunctionalMenuListServiceImpl.class);
	
	@Autowired
	private DataAccessService  dataAccessService;

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RoleFunction> getFunctionCDList(HttpServletRequest request) throws Exception {
		return getDataAccessService().executeNamedQuery("functionCDlist", null, null);
	}

	
}
