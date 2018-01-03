<%--
  ============LICENSE_START==========================================
  ONAP Portal SDK
  ===================================================================
  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
  ===================================================================
 
  Unless otherwise specified, all software contained herein is licensed
  under the Apache License, Version 2.0 (the "License");
  you may not use this software except in compliance with the License.
  You may obtain a copy of the License at
 
              http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
  Unless otherwise specified, all documentation contained herein is licensed
  under the Creative Commons License, Attribution 4.0 Intl. (the "License");
  you may not use this documentation except in compliance with the License.
  You may obtain a copy of the License at
 
              https://creativecommons.org/licenses/by/4.0/
 
  Unless required by applicable law or agreed to in writing, documentation
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
  ============LICENSE_END============================================
 
  ECOMP is a trademark and service mark of AT&T Intellectual Property.
  --%>
<%@ page language="java" contentType="text/html;"
	pageEncoding="US-ASCII" isErrorPage="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;">
		<title>Error Page</title>
	</head>
	<body>
		<h1>Something went wrong. Please go back to the previous page or
			try again later.</h1>
			
		<h3>Please see the exception:</h3>
	
		<table width="100%" border="1">
			<tr valign="top">
				<td width="40%"><b>Error:</b></td>
				<td>${pageContext.exception}</td>
			</tr>
	
			<tr valign="top">
				<td><b>URI:</b></td>
				<td>${pageContext.errorData.requestURI}</td>
			</tr>
	
			<tr valign="top">
				<td><b>Status code:</b></td>
				<td>${pageContext.errorData.statusCode}</td>
			</tr>
		</table>
	
	</body>
</html>