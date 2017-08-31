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
package org.onap.portalsdk.analytics.system.fusion.adapter;


import java.io.*;

import org.onap.portalsdk.core.domain.FusionVo;
import org.onap.portalsdk.core.domain.support.NameValueId;



public class Lookup extends FusionVo implements Serializable {

    private NameValueId nameValueId = new NameValueId();

    public Lookup() {}

    public Lookup(String label, String value) {
      this();
      setLabel(label);
      setValue(value);
    }

    public String getValue() {
      return getNameValueId().getVal();
    }

    public String getLabel() {
      return getNameValueId().getLab();
    }

    public void setValue(String value) {
      getNameValueId().setVal(value);
    }

    public void setLabel(String label) {
      getNameValueId().setLab(label);
    }

    public NameValueId getNameValueId() {
        return nameValueId;
    }

    public void setNameValueId(NameValueId nameValueId) {
        this.nameValueId = nameValueId;
    }

    // required by ZK for to set the selectedItems of Listboxes (used heavily for <select>-style drop-downs)
    public int hashCode() {
      int hash = getValue().hashCode();
          hash = hash + getLabel().hashCode();
          
      return hash;
    }

    public boolean equals( Object obj ) {
      boolean equivalent = false;
      
      Lookup lookup = (Lookup)obj;
      if( lookup.getValue().equals(getValue()) &&  lookup.getLabel().equals(getLabel())) {
        equivalent = true;
      }  

      return equivalent;
    }
    
}
