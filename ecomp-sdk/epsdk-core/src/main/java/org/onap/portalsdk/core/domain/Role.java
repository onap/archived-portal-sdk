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
package org.onap.portalsdk.core.domain;


import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.onap.portalsdk.core.domain.support.DomainVo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <p>Role.java</p>
 * <p>Represents a role data object.</p>
 *
 * @version 1.0
 */
public class Role extends DomainVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String  name;
    private boolean active;
    private Integer priority;

    private Set     roleFunctions = new TreeSet();
    
    private Set     childRoles    = new TreeSet();
    @JsonIgnore
    private Set     parentRoles    = new TreeSet();

    public Role() {}

    public String getName() {
        return name;
    }

    public boolean getActive() {
        return active;
    }

    public Set getRoleFunctions() {
        return roleFunctions;
    }

    public Integer getPriority() {
        return priority;
    }

    public Set getChildRoles() {
        return childRoles;
    }

    public Set getParentRoles() {
        return parentRoles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRoleFunctions(Set roleFunctions) {
        this.roleFunctions = roleFunctions;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    
    public void setChildRoles(Set childRoles) {
        this.childRoles = childRoles;
    }

    public void setParentRoles(Set parentRoles) {
        this.parentRoles = parentRoles;
    }
    
    @SuppressWarnings("unchecked")
    public void addRoleFunction(RoleFunction roleFunction) {
        this.roleFunctions.add(roleFunction);
    }

    @SuppressWarnings("unchecked")
    public void addChildRole(Role role) {
        this.childRoles.add(role);
    }

    @SuppressWarnings("unchecked")
    public void addParentRole(Role role) {
        this.parentRoles.add(role);
    }

    public String getEditUrl() {
        return "/role.htm?role_id=" + getId();    	
    }
    
	public String getToggleActiveImage() {
		return "/static/fusion/images/" + (getActive() ? "active.png" : "inactive.png" );
	}

	public String getToggleActiveAltText() {
		return getActive() ? "Click to Deactivate Role" : "Click to Activate Role";
	}
    
    public void removeChildRole(Long roleId) {
      Iterator i = this.childRoles.iterator();

      while (i.hasNext()) {
        Role childRole = (Role)i.next();
        if (childRole.getId().equals(roleId)) {
          this.childRoles.remove(childRole);
          break;
        }
      }
    }

    public void removeParentRole(Long roleId) {
        Iterator i = this.parentRoles.iterator();

        while (i.hasNext()) {
          Role parentRole = (Role)i.next();
          if (parentRole.getId().equals(roleId)) {
            this.parentRoles.remove(parentRole);
            break;
          }
        }
      }

    public void removeRoleFunction(String roleFunctionCd) {
      Iterator i = this.roleFunctions.iterator();

      while (i.hasNext()) {
        RoleFunction roleFunction = (RoleFunction)i.next();
        if (roleFunction.getCode().equals(roleFunctionCd)) {
          this.roleFunctions.remove(roleFunction);
          break;
        }
      }
    }

    public int compareTo(Object obj){
      String c1 = getName();
      String c2 = ((Role)obj).getName();

      return (c1 == null || c2 == null) ? 1 : c1.compareTo(c2);
    }
    
}
