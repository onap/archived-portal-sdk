/*app.
controller('notebookFrameController', ['$scope', '$location','$window','$http', function ($scope,$location,$window,$http) {
	
	$scope.invokeSaveNotebook() = function() { 
		
		
		//	$http.post('rNotebookFE/authCr', $scope.postData).success(function(data, status) {
			$http({method:'POST', url:'rNotebookFE/authCr', data: $scope.notebookvalue, params:{'qparams' : $scope.additionalqueryParams}}).success(function(data, status) {
		            console.log('Data Sent', data);
		            console.log('Status ', status);
		            
		        
		         	 
		            // iframe.name = "my_iframe";
		            
		          //  $scope.ifr = "<div><iframe src='http://www.w3schools.com'/></div>"
		          
		          //  var url = "https://rcloud.research.att.com";
		        //    document.getElementById('itestfr').src = data;
		            window.open ('notebook.htm#/notebook-frame','_self',false);
		        //   $scope.iframevisibility = true; 
		        //    document.getElementById('itestfr').src = data;
		           
				
				})
			
			
		}

}]);*/
appDS2.controller('notebookFrameController', function ($scope,$location,$window,$http,$routeParams) {
	
	//alert($location.search(1, $scope.additionalqueryParams));
	//var nid = $routeParams.nid;
	//var qprms = $routeParams.qprms;
	//var value = $routeParams.value;
	//console.log('check id ');
	var nid = $window.location.search.substr($window.location.search.indexOf("=")+1);
	//console.log('nid',nid);
	//console.log('qprms',qprms);
	//$scope.notebookvalue = '833c0a69ec1433fbb2f8752af733cf0e'; 
	$scope.additionalqueryParams={};
	if ($window.location.search.substr($window.location.search.indexOf("=")+1)) {
		$scope.queryParams = $window.location.search;
		//console.log('$window.location.search',$window.location.search.substring(0, $window.location.search.length-1));
		//if ($window.location.search.indexOf("&")!=-1) {
		if($window.location.search.substring(0, $window.location.search.length-1).indexOf("&")!=-1) {
			$scope.notebookparam = $window.location.search.substring($window.location.search.indexOf("?")+1,$window.location.search.indexOf("&"));
			$scope.additionalqueryParams = JSON.parse('{"' + decodeURI($scope.queryParams.substr($scope.queryParams.indexOf("&")+1).replace(/&/g, "\",\"").replace(/=/g,"\":\"")) + '"}');
			//console.log('Additional parameters present');
		}
		else {
			$scope.notebookparam1 = $window.location.search.substr($window.location.search.indexOf("?")+1);
			$scope.notebookparam = $scope.notebookparam1.substring(0, $scope.notebookparam1.length - 1);
			//console.log('Additional parameters absent');
		}
		//console.log('add parameters',$scope.additionalqueryParams);
	//	$scope.notebookid = $scope.notebookparam.substring(0,$scope.notebookparam.indexOf("="));
		$scope.notebookvalue = $scope.notebookparam.substr($scope.notebookparam.indexOf("=")+1);
	//	$scope.notebookvalue = $scope.notebookvalue1.substring(0, $scope.notebookvalue1.length - 1); 
		//console.log('New VALL',$scope.notebookvalue);
		//$scope.postData = $window.location.search.substr($window.location.search.indexOf("=")+1);
		//console.log('Notebook value present ',$scope.notebookvalue);
	}
	else {
		$scope.notebookvalue = '833c0a69ec1433fbb2f8752af733cf0e';
		//console.log('Notebook value absent ',$scope.notebookvalue);
	}
	
	
	
/*	$http({method:'POST', url:'rNotebookFE/authCr', data: $scope.notebookvalue, params:{'qparams' : $scope.additionalqueryParams}}).success(function(data, status) {
        //console.log('Data received', data);
        //console.log('Status ', status);
        document.getElementById('itestframe').src = data;
   
	})*/
	$http({method:'POST', url:'rNotebookFE/authCr', data: $scope.notebookvalue, params:{'qparams' : $scope.additionalqueryParams}, transformResponse: [function (data) {
	      // Do whatever you want!
        document.getElementById('itestframe').src = data;
	      return data;
	  }]})
	
	
});
