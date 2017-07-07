package org.openecomp.portalsdk.core.service;

import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class CentralAccessCondition implements Condition {
    
	/**
	 * returns true if the application is centralized
	 */

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		boolean isRemote = false;
		
		if(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED) == null)
		{
			isRemote = false;
		}
		else if(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED) != null &&  PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED).equals("remote")){
		 isRemote = true;
	    } 
		
	 return isRemote;
  }
}