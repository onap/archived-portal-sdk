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
package org.onap.portalsdk.core.objectcache;

import java.io.*;

import org.onap.portalsdk.core.objectcache.support.*;


public abstract class AbstractCacheManager implements FusionCacheManager {
  public AbstractCacheManager() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Object getObject(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  public void putObject(String key, Object objectToCache) {
    // TODO Auto-generated method stub
  }

  public boolean isObjectInCache(String key) {
    // TODO Auto-generated method stub
    return false;
  }

  public void removeObject(String key) {
    // TODO Auto-generated method stub
  }

  public void clearCache() {
    // TODO Auto-generated method stub
  }

  public void configure() throws IOException {
    // TODO Auto-generated method stub

  }
}

