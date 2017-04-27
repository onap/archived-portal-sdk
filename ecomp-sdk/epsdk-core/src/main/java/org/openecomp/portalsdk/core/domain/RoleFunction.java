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


import org.openecomp.portalsdk.core.domain.support.DomainVo;

/**
 * <p>RoleFunction.java</p>
 *
 * <p>Represents a role function data object.</p>
 *
 * @version 1.0
 */
public class RoleFunction extends DomainVo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public RoleFunction() {}

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEditUrl() {
      return "/role_function.htm?role_function_id=" + getCode();    	
    }
    
    public int compareTo(Object obj){
      String c1 = getName();
      String c2 = ((RoleFunction)obj).getName();

      return (c1 == null || c2 == null) ? 1 : c1.compareTo(c2);
    }

    private String code;
    private String name;
    private String editUrl; 
    
}
