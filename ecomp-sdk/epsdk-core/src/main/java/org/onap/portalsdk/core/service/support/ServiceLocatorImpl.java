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
package org.onap.portalsdk.core.service.support;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.InitialLdapContext;

import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.stereotype.Service;

/**
 * This class implements the J2EE service locator pattern. It provides lookup
 * facilities for various services. Currenttly LDAP (pre-v3) is supported
 */
@Service("serviceLocator")
public class ServiceLocatorImpl implements ServiceLocator {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ServiceLocatorImpl.class);

	private Context context; // JNDI context (not currently in use)
	private Context rootContext; // Java env root context (not currently in use)
	private DirContext dirContext; // LDAP DIR context
	private InitialLdapContext ldapContext; // LDAP context LDAPv3-style (not currently in use)

	// cannot directly instantiate
	public ServiceLocatorImpl() {
	}

	/*
	 * public static ServiceLocator getLocator() { if (locator == null) locator =
	 * new ServiceLocator(); return locator; }
	 * 
	 * public Object clone() throws CloneNotSupportedException { throw new
	 * CloneNotSupportedException(); }
	 */

	// Get an LDAP directory context
	public DirContext getDirContext(String initialContextFactory, String providerUrl, String securityPrincipal) {

		if (dirContext == null) {

			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
			properties.put(Context.PROVIDER_URL, providerUrl);
			properties.put(Context.SECURITY_PRINCIPAL, securityPrincipal);

			try {
				dirContext = new InitialDirContext(properties);
			} catch (NamingException ne) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"An error has occurred while creating an Initial Directory Context: " + ne.getMessage());
				logger.error(EELFLoggerDelegate.errorLogger, "Explanation: " + ne.getExplanation());
			}
		}

		return dirContext;
	}

	// Get an LDAP directory context - LDAPv3-style
	/*
	 * public InitialLdapContext getLdapContext() { //throws NamingException { if
	 * (ldapContext == null) { Properties properties = new Properties(); // @todo -
	 * need to parameterize context factoy class and url
	 * properties.put(Context.INITIAL_CONTEXT_FACTORY,
	 * AttLdap.DIR_INITIAL_CONTEXT_FACTORY); properties.put(Context.PROVIDER_URL,
	 * AttLdap.DIR_PROVIDER_URL); properties.put(Context.SECURITY_PRINCIPAL,
	 * AttLdap.DIR_SECURITY_PRINCIPAL); Control[] ctrl = null; try { ldapContext =
	 * new InitialLdapContext(properties, ctrl); } catch (NamingException ne) { //
	 * MJ FIX log exception? } } return ldapContext; }
	 */

}
