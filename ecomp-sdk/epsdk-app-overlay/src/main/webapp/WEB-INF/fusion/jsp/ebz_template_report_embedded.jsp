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
    <%@ include file="/WEB-INF/fusion/jsp/meta.jsp" %>
		<script src="static/js/jquery-1.10.2.js" type="text/javascript"></script>
<script src= "app/fusion/external/ebz/angular_js/angular.js"></script> 
<script src= "app/fusion/external/ebz/angular_js/angular-route.min.js"></script>
<script src= "app/fusion/external/ebz/angular_js/angular-sanitize.js"></script>
<script src= "app/fusion/external/ebz/angular_js/app.js"></script>
<script src= "app/fusion/external/ebz/angular_js/gestures.js"></script>
<script src="static/js/jquery-1.10.2.js"></script>
<script src="static/js/modalService.js"></script>
<script src="static/js/jquery.mask.min.js" type="text/javascript"></script>
<script src="static/js/jquery-ui.js" type="text/javascript"></script>
<script src="static/fusion/js/att_angular_gridster/ui-gridster-tpls.js"></script>
<script src="static/fusion/js/att_angular_gridster/angular-gridster.js"></script>
<script src= "app/fusion/external/ebz/angular_js/checklist-model.js"></script>
<script src="app/fusion/external/angular-ui/ui-bootstrap-tpls-1.1.2.min.js"></script>		

    
	<body class="templatebody" style="opacity: 1; background-color: rgb(242, 242, 242); padding: 0px;">
		<div class="applicationWindow">
			<div class="content">
					<tiles:insertAttribute name="body" />
			</div>
		</div>
	</body>
</html>