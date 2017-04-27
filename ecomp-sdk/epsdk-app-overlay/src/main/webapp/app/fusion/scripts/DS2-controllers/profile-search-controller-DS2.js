appDS2.controller('profileSearchCtrlDS2', function($scope, $http,ProfileServiceDS2){
    $scope.showInput = true;
    $scope.totalPages1 = 5;
    $scope.viewPerPage1 = 8;
    $scope.currentPage1 = 1;
    $scope.showLoader = false;

	$scope.$watch('viewPerPage1', function(val) {
		$scope.showLoader = true;
		ProfileServiceDS2.getProfilePagination($scope.currentPage1, val).then(function(data){
    		var j = data;
      		$scope.data = JSON.parse(j.data);
      		$scope.tableData =JSON.parse($scope.data.profileList);     		
      		$scope.totalPages1 =JSON.parse($scope.data.totalPage);
      		for(x in $scope.tableData){
				if($scope.tableData[x].active_yn=='Y')
					$scope.tableData[x].active_yn=true;
				else
					$scope.tableData[x].active_yn=false;
			}
      		$scope.showLoader = false;
    	},function(error){
    		console.log("failed");
    		reloadPageOnce();
    	});
		
	});
	    
	$scope.customHandler = function(num) {
	    	$scope.currentPage1 = num;	 
	    	$scope.showLoader = true;
	    	ProfileServiceDS2.getProfilePagination($scope.currentPage1,$scope.viewPerPage1).then(function(data){
	    		var j = data;
	      		$scope.data = JSON.parse(j.data);
	      		$scope.tableData =JSON.parse($scope.data.profileList);
	      		$scope.totalPages1 =JSON.parse($scope.data.totalPage);
	      		for(x in $scope.tableData){
					if($scope.tableData[x].active_yn=='Y')
						$scope.tableData[x].active_yn=true;
					else
						$scope.tableData[x].active_yn=false;
				}
	      		$scope.showLoader = false;
	    	},function(error){
	    		console.log("failed");
	    		reloadPageOnce();
	    	});

	    };

	$scope.editRow = function(profileId){
        window.location = 'userProfile#/profile/' + profileId;
    };
   
	$scope.toggleProfileActive = function(rowData) {
    	modalService.popupConfirmWinWithCancel("Confirm","You are about to change user's active status. Do you want to continue?",
    			function(){ 
    		        $http.get("profile/toggleProfileActive?profile_id="+rowData.id).success(function(){});
    	},
    	function(){
    		rowData.active=!rowData.active;
    	})
    };
});
