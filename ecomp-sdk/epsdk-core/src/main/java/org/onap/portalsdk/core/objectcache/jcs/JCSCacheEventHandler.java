/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.core.objectcache.jcs;

import org.apache.jcs.engine.control.event.behavior.IElementEvent;
import org.apache.jcs.engine.control.event.behavior.IElementEventConstants;
import org.apache.jcs.engine.control.event.behavior.IElementEventHandler;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class JCSCacheEventHandler implements IElementEventHandler, IElementEventConstants {
	
  private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(JCSCacheEventHandler.class);
	
  public JCSCacheEventHandler() {
    super();
  }

  public void handleElementEvent(IElementEvent event) {
    // Handle code for various event notifications on cached elements by JCS.
    switch (event.getElementEvent()) {
    case ELEMENT_EVENT_EXCEEDED_MAXLIFE_BACKGROUND:
      logger.error(EELFLoggerDelegate.errorLogger, "Event ELEMENT_EVENT_EXCEEDED_MAXLIFE_BACKGROUND occurred for element " + event);
      break;
    case ELEMENT_EVENT_EXCEEDED_MAXLIFE_ONREQUEST:
      logger.error(EELFLoggerDelegate.errorLogger, "Event ELEMENT_EVENT_EXCEEDED_MAXLIFE_ONREQUEST occurred for element " + event);
      break;
    case ELEMENT_EVENT_EXCEEDED_IDLETIME_BACKGROUND:
      logger.error(EELFLoggerDelegate.errorLogger, "Event ELEMENT_EVENT_EXCEEDED_IDLETIME_BACKGROUND occurred for element " + event);
      break;
    case ELEMENT_EVENT_EXCEEDED_IDLETIME_ONREQUEST:
      logger.error(EELFLoggerDelegate.errorLogger, "Event ELEMENT_EVENT_EXCEEDED_IDLETIME_ONREQUEST occurred for element " + event);
      break;
    case ELEMENT_EVENT_SPOOLED_DISK_AVAILABLE:
      logger.error(EELFLoggerDelegate.errorLogger, "Event ELEMENT_EVENT_SPOOLED_DISK_AVAILABLE occurred for element " + event);
      break;
    case ELEMENT_EVENT_SPOOLED_DISK_NOT_AVAILABLE:
      logger.error(EELFLoggerDelegate.errorLogger, "Event ELEMENT_EVENT_SPOOLED_DISK_NOT_AVAILABLE occurred for element " + event);
      break;
    case ELEMENT_EVENT_SPOOLED_NOT_ALLOWED:
      logger.error(EELFLoggerDelegate.errorLogger, "Event ELEMENT_EVENT_SPOOLED_NOT_ALLOWED occurred for element " + event);
    }
  }
}
