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
package org.openecomp.portalsdk.core.domain;


import org.openecomp.portalsdk.core.domain.support.*;


/**
 * <p>LuTimeZone.java</p>
 *
 * <p>Represents a LuTimeZone data object.</p>
 *
 * @version 1.0
 */
public class LuTimeZone extends DomainVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    LuTimeZone() {}

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public Long getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(Long timezoneId) {
		this.timezoneId = timezoneId;
	}

	public int compareTo(Object obj){
      Long c1 = getId();
      Long c2 = ((LuTimeZone)obj).getId();

      return c1.compareTo(c2);
    }


    private String name;
    private Long timezoneId;
    private String value;

}
