/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalapp.lm;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.onap.portalsdk.core.lm.LicenseableClass;

/*
 *  Please note that this class is not being used; its a dummy stub to have a qualifying bean for the interface.
 */
public class LicenseableClassImpl implements LicenseableClass {

	@Override
	public String getApplicationName() {
		return "";
	}

	@Override
	public InputStream getPublicKeystoreAsInputStream() throws FileNotFoundException {
	  return null;
	}

	@Override
	public String getAlias() {
		return "";
	}

	@Override
	public String getKeyPasswd() {
		return "";
	}

	@Override
	public String getPublicKeystorePassword() {
		return "";
	}

	@Override
	public String getCipherParamPassword() {
		return "";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class getClassToLicense() {
		return this.getClass();
	}

}
