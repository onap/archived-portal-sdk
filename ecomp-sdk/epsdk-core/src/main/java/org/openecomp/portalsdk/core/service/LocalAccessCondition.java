package org.openecomp.portalsdk.core.service;

import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class LocalAccessCondition implements Condition{
	
	/**
	 * returns true if the application is not centralized
	 */
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {	
		return PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED) == null || (PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED) != null && ! PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED).equals("remote"));
	}		

}
