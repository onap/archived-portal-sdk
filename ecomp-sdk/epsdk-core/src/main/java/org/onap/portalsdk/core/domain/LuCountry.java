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


import org.onap.portalsdk.core.domain.support.*;


/**
 * <p>luCountry.java</p>
 *
 * <p>Represents a country data object.</p>
 *
 * @version 1.0
 */
public class LuCountry extends DomainVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    LuCountry() {}

    public String getCountry() {
        return country;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }


    public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getWebphoneCountryLabel() {
		return webphoneCountryLabel;
	}

	public void setWebphoneCountryLabel(String webphoneCountryLabel) {
		this.webphoneCountryLabel = webphoneCountryLabel;
	}

	public int compareTo(Object obj){
      String c1 = getCountry();
      String c2 = ((LuCountry)obj).getCountry();

      return c1.compareTo(c2);
    }


    private String abbr;
    private String country;
    private String fullName;
    private String webphoneCountryLabel;

}
