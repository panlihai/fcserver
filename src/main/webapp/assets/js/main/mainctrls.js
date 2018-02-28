"use strict";
appMdl.controller("mainCtrl",
		["$rootScope","$scope","$sce",
		 "cacheService","bookService",
		 "userService","staticService",
         function($rootScope,$scope,$sce,
        		 cacheService,bookService,
        		 userService,staticService){
	$scope.PID = staticService.getPid();	
	//监听用户token
	$rootScope.$watch('USERTOKEN', function(newValue, oldValue) {
			$scope.getVideoList();
	});
}]);

appMdl.controller("loginRegisterCtrl", 
		["$rootScope","$scope","cacheService",
		 "userService","commonService","staticService",
	        function($rootScope,$scope,cacheService,
	        		userService,commonService,staticService) { 
		$scope.PID = staticService.getPid();
		// 用户界面绑定数据
		$scope.userLoginInfo = {			
				// 用户名称
				USERID : cacheService.get("USERID",""),
				// 用户密码
				PASSWORD : cacheService.get("PASSWORD",""),
				// 是否自动登录
				autoLogin : cacheService.get("autoLogin","true")=="true"?true:false,
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
				userService.login($scope.PID,$scope.userLoginInfo).then(function(res){
					if(res.CODE=='0'){
						cacheService.set("USERID",$scope.userLoginInfo.USERID);
						cacheService.set("PASSWORD",$scope.userLoginInfo.PASSWORD);
						cacheService.set("autoLogin",$scope.userLoginInfo.autoLogin?"true":"false");
						$rootScope.USERINFO = res.DATA;
						cacheService.setObject("USERINFO",res.DATA);
						$("#login_modal_page").modal("hide");
						$scope.userLoginInfo.error = false;
						$scope.userLoginInfo.msg = "";
						$rootScope.isLogin = true;
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
			} 
		
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
			userService.getTelsms($scope.PID,$scope.userRegisterInfo).then(function(res){
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
				userService.register($scope.PID,$scope.userRegisterInfo).then(function(res){
					if(res.CODE=='0'){
						cacheService.set("USERID",$scope.userRegisterInfo.USERID);
						cacheService.set("PASSWORD",$scope.userRegisterInfo.PASSWORD);
						cacheService.set("autoLogin",$scope.userLoginInfo.autoLogin?"true":"false");
						cacheService.setObject("USERINFO",res.DATA);	
						$("#login_modal_page").modal("hide");
						$scope.userRegisterInfo.error = false;
						$scope.userRegisterInfo.msg = "";
						$rootScope.isLogin = true;
					}else{
						$scope.userRegisterInfo.error = true;
						$scope.userRegisterInfo.msg = res.MSG;
					}
				});		
			};
	} ]);

