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
package org.openecomp.portalsdk.core.domain.support;

import java.io.Serializable;


public class NameValueId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String lab;
    private String val;

    public NameValueId() {
    }

    public NameValueId(String value, String label) {
      setVal(value);
      setLab(label);
    }


    public String getLab() {
        return lab;
    }


    public String getVal() {
        return val;
    }


    public void setLab(String label) {
        this.lab = label;
    }


    public void setVal(String value) {
        this.val = value;
    }


    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }

      if (o == null) {
        return false;
      }

      if (!(o instanceof NameValueId)) {
        return false;
      }

      final NameValueId nameValueId = (NameValueId)o;

      if (!getVal().equals(nameValueId.getVal())) {
        return false;
      }

      if (!getLab().equals(nameValueId.getLab())) {
        return false;
      }

      return true;
    }


    public int hashCode() {
      return getVal().hashCode();
    }

}
