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

import java.io.Serializable;

public class UrlsAccessible extends FusionVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private UrlsAccessibleKey urlsAccessibleKey = new UrlsAccessibleKey();

    public UrlsAccessible() {}

    public UrlsAccessible(String url, String functionCd) {
      this();
      setUrl(url);
      setFunctionCd(functionCd);
    }

    public String getUrl() {
      return getUrlsAccessibleKey().getUrl();
    }

    public String getFunctionCd() {
      return getUrlsAccessibleKey().getFunctionCd();
    }

    public void setUrl(String url) {
    	getUrlsAccessibleKey().setUrl(url);
    }

    public void setFunctionCd(String functionCd) {
    	getUrlsAccessibleKey().setFunctionCd(functionCd);
    }

    public UrlsAccessibleKey getUrlsAccessibleKey() {
        return urlsAccessibleKey;
    }

    public void setUrlsAccessibleKey(UrlsAccessibleKey urlsAccessibleKey) {
        this.urlsAccessibleKey = urlsAccessibleKey;
    }

    // required by ZK for to set the selectedItems of Listboxes (used heavily for <select>-style drop-downs)
    public int hashCode() {
      int hash = getUrl().hashCode();
          hash = hash + getFunctionCd().hashCode();
          
      return hash;
    }

    public boolean equals( Object obj ) {
      boolean equivalent = false;
      
      UrlsAccessible lookup = (UrlsAccessible)obj;
      if( lookup.getUrl().equals(getUrl()) &&  lookup.getFunctionCd().equals(getFunctionCd())) {
        equivalent = true;
      }  

      return equivalent;
    }
    
}
