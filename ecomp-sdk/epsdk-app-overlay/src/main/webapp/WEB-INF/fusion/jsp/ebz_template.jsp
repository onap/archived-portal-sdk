<%--
  ============LICENSE_START==========================================
  ONAP Portal SDK
  ===================================================================
  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
  ===================================================================
 
  Unless otherwise specified, all software contained herein is licensed
  under the Apache License, Version 2.0 (the “License”);
  you may not use this software except in compliance with the License.
  You may obtain a copy of the License at
 
              http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
  Unless otherwise specified, all documentation contained herein is licensed
  under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<!DOCTYPE html>
<html ng-app="abs">
    <head>
		<%@ include file="/WEB-INF/fusion/jsp/meta.jsp" %>
		<script src="static/js/jquery-1.10.2.js" type="text/javascript"></script>
	</head>
	<body class="templatebody" style="opacity: 1; background-color: rgb(242, 242, 242); padding: 0px;">
		<div class="applicationWindow">
			<div>
				<tiles:insertAttribute name="header" />
			</div>
			<br>
			<div class="content" id="mContent">
				<div class="body-content-jsp">
					<tiles:insertAttribute name="body" />
				</div>
			</div>
			<br>
			<div>
				<tiles:insertAttribute name="footer" /> 
			</div>
		</div>
	</body>
</html>