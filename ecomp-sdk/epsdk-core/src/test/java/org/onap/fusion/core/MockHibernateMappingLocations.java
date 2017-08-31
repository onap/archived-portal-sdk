package org.onap.fusion.core;

import org.onap.portalsdk.core.conf.HibernateMappingLocatable;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class MockHibernateMappingLocations implements HibernateMappingLocatable{

	@Override
	public Resource[] getMappingLocations() {
		return new Resource[]{new ClassPathResource("WEB-INF/fusion/orm/Fusion.hbm.xml"), new ClassPathResource("WEB-INF/fusion/orm/Workflow.hbm.xml")};
	}

	@Override
	public String[] getPackagesToScan() {
		return new String[] { "org.onap" };
	}

}
