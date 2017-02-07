/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.core.service.support;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.InitialLdapContext;

import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.stereotype.Service;

/**
 * This class implements the J2EE service locator pattern. It provides lookup
 * facilities for various services. Currenttly LDAP (pre-v3) is supported
 */
@Service("serviceLocator")
public class ServiceLocatorImpl implements ServiceLocator {

  private Context            context;       // JNDI context (not currently in use)
  private Context            rootContext;   // Java env root context (not currently in use)
  private DirContext         dirContext;    // LDAP DIR context
  private InitialLdapContext ldapContext;   // LDAP context LDAPv3-style (not currently in use)


  // cannot directly instantiate
  public ServiceLocatorImpl() {}

  // Get an LDAP directory context
  public DirContext getDirContext(String initialContextFactory, String providerUrl, String securityPrincipal) {

    if (dirContext == null) {

      Properties properties = new Properties();
      properties.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
      properties.put(Context.PROVIDER_URL, providerUrl);
      properties.put(Context.SECURITY_PRINCIPAL, securityPrincipal);

      try {
        dirContext = new InitialDirContext(properties);
      }
      catch (NamingException ne) {
        logger.error(EELFLoggerDelegate.errorLogger, "An error has occurred while creating an Initial Directory Context: " + ne.getMessage());
        logger.error(EELFLoggerDelegate.errorLogger, "Explanation: " + ne.getExplanation());
      }
    }

    return dirContext;
  }

  /** Logger for this class and subclasses */
  EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ServiceLocatorImpl.class);

}
