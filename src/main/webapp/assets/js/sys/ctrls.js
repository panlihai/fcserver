/**
 * 导航栏功能控制器
 */
appMdl.controller("navCtrl",["$rootScope","$scope","$state","userService",
                             "cacheService","$window","constantService",                         
                             function($rootScope,$scope,$state,userService,
                            cacheService,$window,constantService){
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
	/**
	 * 点击菜单后隐藏
	 */
	$scope.hiddenCollapse = function(){		
		$(".collapse").collapse("hide");
	};		
	//退出.
	/**
	 * 登录系统
	 */
	$scope.logout = function(){
		/**
		* @param 产品名称
		* @param 用户界面信息
		*/
		userService.logout().then(function(res){
			cacheService.remove("USERID");
			cacheService.remove("PASSWORD");
			cacheService.remove("autoLogin");				
			cacheService.remove("USERINFO");
			$rootScope.USERINFO = {};			
			$rootScope.isLogin = false;	
			//调整到主页.
			$rootScope.$state.go("user.home");
		});		
	};
}]);

appMdl.controller("loginRegisterCtrl", 
		["$rootScope","$scope","$state","cacheService",
		 "userService","commonService","constantService",
	        function($rootScope,$scope,$state,cacheService,
	        		userService,commonService,constantService) { 
		// 用户界面绑定数据
		$scope.userLoginInfo = {			
				// 用户名称
				USERID : cacheService.get("USERID",""),
				// 用户密码
				PASSWORD : cacheService.get("PASSWORD",""),
				// 是否自动登录
				autoLogin : cacheService.get("AUTOLOGIN","true")=="true"?true:false,
				// 是否有错误
				error:false,
				// 错误消息
				msg:""
			};
		/**
		 * 登录系统
		 */
		$scope.login = function(){
			/**
			* @param 产品名称
			* @param 用户界面信息
			*/
			userService.login($scope.userLoginInfo).then(function(res){
				if(res.CODE=='0'){
					cacheService.set("USERID",$scope.userLoginInfo.USERID);
					cacheService.set("PASSWORD",$scope.userLoginInfo.PASSWORD);
					cacheService.set("AUTOLOGIN",$scope.userLoginInfo.autoLogin?"true":"false");
					$rootScope.USERINFO = res.DATA;
					cacheService.setObject(constantService.USERINFO,res.DATA);
					//$("#login_modal_page").modal("hide");
					$scope.userLoginInfo.error = false;
					$scope.userLoginInfo.msg = "";
					$rootScope.isLogin = true;
					$state.go("user.profile");
				}else{
					$scope.userLoginInfo.error = true;
					$scope.userLoginInfo.msg = res.MSG;
				}
			});		
		};			
		/**
		 * 重置
		*/
		$scope.reset = function(){
			$scope.userLoginInfo.USERID = cacheService.get("USERID","");
			$scope.userLoginInfo.PASSWORD = cacheService.get("PASSWORD","");
			$scope.userLoginInfo.autoLogin = cacheService.get("autoLogin","fasle")=="false"?false:true;
		};
			
		// 用户界面绑定数据
		$scope.userRegisterInfo = {
				// 用户名称
				MOBILEPHONE :'',
				// 用户名称
				USERID :'',			
				// 随机数
				RANDOM : commonService.Random(6),
				// 用户密码
				PASSWORD : '',
				//验证码
				AUTHCODE:'',
				//只有点击获取验证码之后才能显示
				needcode:false,
				// 是否自动登录
				autoLogin : true,
				// 是否有错误
				error:false,
				// 错误消息
				msg:""
			};
		$scope.getTelsms = function(){
			userService.getTelsms($scope.userRegisterInfo).then(function(res){
				if(res.CODE=='0'){
					$scope.userRegisterInfo.needcode = true;
					$scope.userRegisterInfo.error = false;
					$scope.userRegisterInfo.error = "";
				}else{
					$scope.userRegisterInfo.error = true;
					$scope.userRegisterInfo.msg = res.MSG;
				}
			});
		};
		/**
		 * 注册
		 */
		$scope.register = function(){	
			$scope.userRegisterInfo.USERID = $scope.userRegisterInfo.MOBILEPHONE;
			userService.register($scope.userRegisterInfo).then(function(res){
				if(res.CODE=='0'){
					cacheService.set("USERID",$scope.userRegisterInfo.USERID);
					cacheService.set("PASSWORD",$scope.userRegisterInfo.PASSWORD);
					cacheService.set("autoLogin",$scope.userLoginInfo.autoLogin?"true":"false");
					cacheService.setObject("USERINFO",res.DATA);	
//					$("#login_modal_page").modal("hide");
					$scope.userRegisterInfo.error = false;
					$scope.userRegisterInfo.msg = "";
					$rootScope.isLogin = true;
					$state.go("user.profile");
				}else{
					$scope.userRegisterInfo.error = true;
					$scope.userRegisterInfo.msg = res.MSG;
				}
			});		
		};
			
}]);

//
