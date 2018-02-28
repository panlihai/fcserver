/**
 * 导航栏功能控制器
 */
appMdl.controller("navCtrl",["$rootScope","$scope","userService","cacheService",
                             function($rootScope,$scope,userService,cacheService){
	//是否已经登录登录会自动加入到缓存中
	$rootScope.isLogin = false;
	//打开登录 页面 
	$scope.openLogin = function(){
		$("#login_modal_page").modal("show");
		$('#login_modal_page a[href="#loginView"]').tab('show')
	};
	//打开登录 页面 
	$scope.openRegister = function(){
		$("#login_modal_page").modal("show");
		$('#login_modal_page a[href="#registerView"]').tab('show')
	};
	 
}]);


