var userInfoModule = angular.module('UserInfoModule',[]);
userInfoModule.controller('UserInfoCtrl',['$scope',function($scope){
	$scope.userInfo={
			email:"hlhailan@163.com",
			password:"018042",
			autoLogin:true
	};
	$scope.getFormData=function(){
		console.log($scope.userInfo);
	};
	$scope.setFormData=function(){
		$scope.userInfo={
			email:"sldjfaslkfjlasf@163.com",
			password:"sldjfaslkfjlasdkjflasdkfd",
			autoLogin:false
		};
	};
}]);