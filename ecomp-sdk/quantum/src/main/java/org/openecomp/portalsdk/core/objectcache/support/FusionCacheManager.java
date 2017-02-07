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
package org.openecomp.portalsdk.core.objectcache.support;

import java.io.IOException;

import org.openecomp.portalsdk.core.FusionObject;

public interface FusionCacheManager extends FusionObject {

  Object  getObject(String key);
  void    putObject(String key, Object objectToCache);
  boolean isObjectInCache(String key);

  void removeObject(String key);
  void clearCache();
  void configure() throws IOException;
}

