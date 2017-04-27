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
 * <p>State.java</p>
 *
 * <p>Represents a state data object.</p>
 *
 * @version 1.0
 */
public class LuState extends DomainVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    LuState() {}

    public String getState() {
        return state;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }


    public int compareTo(Object obj){
      String c1 = getState();
      String c2 = ((LuState)obj).getState();

      return c1.compareTo(c2);
    }


    private String abbr;
    private String state;

}
