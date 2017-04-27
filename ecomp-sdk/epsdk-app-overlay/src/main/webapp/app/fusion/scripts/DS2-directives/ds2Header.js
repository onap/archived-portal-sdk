	appDS2.directive('ds2Header', function () {
    return {
        restrict: 'A', //This menas that it will be used as an attribute and NOT as an element. I don't like creating custom HTML elements
        replace: false,
        templateUrl: "app/fusion/scripts/DS2-view-models/ds2Header.html",
        controller: ['$scope', '$filter','$http','$timeout', '$log','UserInfoServiceDS2', '$window', '$cookies', "$cookieStore", function ($scope, $filter, $http, $timeout, $log,UserInfoServiceDS2, $window, $cookies, $cookieStore) {
        	// copy from existing DS1
        	/*Define fields*/
        	$scope.userName;
        	$scope.userFirstName;
        	/*$scope.redirectUrl;
        	$scope.contactUsUrl;
        	$scope.getAccessUrl;
        	$scope.childData=[];
        	$scope.parentData=[];
        	$scope.menuItems = [];
        	$scope.loadMenufail=false;
        	$scope.megaMenuDataObject=[];       	
        	$scope.activeClickSubMenu = {
          		x: ''
          	};
        		$scope.activeClickMenu = {
        			x: ''
        		};
        		$scope.favoritesMenuItems = [];
            $scope.favoriteItemsCount = 0;
            $scope.showFavorites = false;
            $scope.emptyFavorites = false;
            $scope.favoritesWindow = false;*/

        	//DS2 code
        	$scope.tabItems = [
        	       {
            	   'title': 'ECOMP'            		   
        	       },
        	       {
            	   'title': 'Help',
            	   'subitems': [{
            		   'value': 'Contact Us'

            	   }, {
            		   'value': 'Get Access'
            	   }, 
            	]}, 
            ];

        	$scope.userProfile = {};
        	$scope.showInfo = false;
        	$scope.showProfile = function () {
        		$scope.showInfo = !$scope.showInfo;
        	};
        	$scope.loginOptions1 = [
        	                        {value: '', text: 'Log In..'},
        	                        {value: '1', text: 'Premier'},
        	                        {value: '2', text: 'Wifi Services'},
        	                        {value: '3', text: 'Cloud Solutions'}
        	                        ];
        	$scope.loginVal = {};
        	$scope.loginVal.value = $scope.loginOptions1[0].value;

        	$scope.clickLogin = function () {
        		$scope.openDropdown = !$scope.openDropdown;
        	};
        	$scope.skipNavigation = function () {
        		var element = angular.element(document.querySelector('li.last'))[0];
        		element.children[0].focus();
        	};

        	/***************functions**************/
        	/*Put user info into fields*/

        	$scope.inputUserInfo = function(userInfo){
        		if (typeof(userInfo) != "undefined" && userInfo!=null && userInfo!=''){
        			if(typeof(userInfo.USER_FIRST_NAME) != "undefined" && userInfo.USER_FIRST_NAME!=null){		
        				$scope.userFirstName = userInfo.USER_FIRST_NAME;
        			}
        		}		
        	}
        	/*getting user info from session*/
        	$scope.getUserNameFromSession = function(){
        		UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
        		.then(function (res) {
        			$scope.contactUsUrl=res.contactUsLink;
        			$scope.userName = res.userName;
        			$scope.userFirstName = res.firstName;
        			$scope.redirectUrl = res.portalUrl;
        			$scope.getAccessUrl = res.getAccessUrl;
        			$scope.userProfile.fullName = res.userName;
        			$scope.userProfile.email = res.email;
        		});
        	}

        	/*Put user info into fields*/
        	$scope.inputUserInfo = function(userInfo){
        		if (typeof(userInfo) != "undefined" && userInfo!=null && userInfo!=''){
        			if (typeof(userInfo.USER_FIRST_NAME) != "undefined" && userInfo.USER_FIRST_NAME!=null && userInfo.USER_FIRST_NAME!='')
        				$scope.userProfile.firstName = userInfo.USER_FIRST_NAME;
        			if (typeof(userInfo.USER_LAST_NAME) != "undefined" && userInfo.USER_LAST_NAME!=null && userInfo.USER_LAST_NAME!='')
        				$scope.userProfile.lastName = userInfo.USER_LAST_NAME;
        			if (typeof(userInfo.USER_EMAIL) != "undefined" && userInfo.USER_EMAIL!=null && userInfo.USER_EMAIL!='')
        				$scope.userProfile.email = userInfo.USER_EMAIL;		
        		}		
        	}

        	/*getting user info from shared context*/
        	$scope.getUserName=function() {
        		var promise = UserInfoServiceDS2.getFunctionalMenuStaticDetailShareContext();
        		promise.then(
        				function(res) { 
        					if(res==null || res==''){
        						$log.warn('DS2HeaderCtlr::getUserName: failed to get info from shared context');
        						$scope.getUserNameFromSession();
        					}else{
        						// $log.info('Received User information from shared context',res);
        						var resData = res;
        						$scope.inputUserInfo(resData);
        						$scope.userProfile.fullName = $scope.userProfile.firstName+ ' '+ $scope.userProfile.lastName;					
        					}
        				},
        				function(err) {
        					console.log('error');
        				}
        		);
        	};
        	
        	$scope.adjustHeader=function() {
        		$scope.showHeader = ($cookieStore.get("show_app_header") == undefined ? true : $cookies.show_app_header);
        		if($scope.showHeader == true) {
    	    		$scope.drawer_margin_top = 70;
    	    		$scope.drawer_custom_top = 54;
    	    		$scope.toggle_drawer_top = 55;
        		}
        		else  {
        			
        			$scope.drawer_margin_top = 60;
            		$scope.drawer_custom_top = 0;
            		$scope.toggle_drawer_top = 10;
        		}
        		
        	}
        	
        	/*call the get user info function*/
        	try{
        		$scope.getUserName();
        		$scope.adjustHeader();
            	
        	}catch(err){
        		$log.info('Error while getting User information',err);
        	}
        }]
    }
});