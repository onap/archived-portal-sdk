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
app.config(function($routeProvider) {
	$routeProvider
	.when('/view', {
		templateUrl: 'app/fusionapp/drools/view-models/droolsView.html',
		controller : "droolsViewController"
	})
	.otherwise({
		templateUrl: 'app/fusionapp/drools/view-models/droolsList.html',
		controller : "droolsListController"
	});
});
