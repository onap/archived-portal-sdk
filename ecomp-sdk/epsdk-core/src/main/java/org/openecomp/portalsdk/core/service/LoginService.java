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
package org.openecomp.portalsdk.core.service;


import java.util.*;

import org.openecomp.portalsdk.core.command.*;


public interface LoginService {

	/**
	 * 
	 * @param bean
	 * @param menuPropertiesFilename
	 * @param additionalParams
	 * @return returns login user bean
	 * @throws Exception
	 */
    // validate user exists in the system
	@SuppressWarnings("rawtypes")
    LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams) throws Exception;
    
	
	/**
	 * 
	 * @param bean
	 * @param menuPropertiesFilename
	 * @param additionalParams
	 * @param matchPassword
	 * @return returns login user bean 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
    LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams, boolean matchPassword) throws Exception;
}
