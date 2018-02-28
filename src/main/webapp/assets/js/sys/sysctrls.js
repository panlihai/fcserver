"use strict";
// 个人信息控制器
appMdl.controller("profileCtrl", [ "$rootScope", "$scope", "cacheService",
		"$state", function($rootScope, $scope, cacheService, $state) {

		} ]);
//核心控制器
//控制器个人资料
appMdl.controller("mainCtrl",[
						"$rootScope",
						"$scope",
						"$state",
						"actService",
						"cacheService",
						"appService",
function($rootScope,
		$scope, 
		$state, 
		actService,
		cacheService, 
		appService) {
	$scope.getProducts = function(){
		actService.query("PRODUCT",{}).then(function(data){
			$scope.products = data.DATA;
		});	
	};		
	$scope.getProducts();
}]);

// 控制器个人资料
appMdl.controller("userinfoCtrl",[
						"$rootScope",
						"$scope",
						"$state",
						"actService",
						"cacheService",
						"appService",
function($rootScope,
		$scope, 
		$state, 
		actService,
		cacheService, 
		appService) {
	$scope.userinfo = $rootScope.USERINFO;
	$scope.MAINAPP = appService.getApp("CUSER");
	$scope.ADDAPP = appService.getApp("CUSERADD");
	$scope.addressList = {};
	$scope.address = {
		USERID : $rootScope.USERINFO.USERID,
		ENABLE : 'Y',
		PROVINCE : $rootScope.USERINFO.PROVINCE,
		CITY : $rootScope.USERINFO.CITY,
		COUNTY : $rootScope.USERINFO.COUNTY,
		ADDRESS : $rootScope.USERINFO.ADDRESS,
		ISDEFAULT : 'Y'
	};
	$scope.update = function() {
		actService.update("USER", $rootScope.USERINFO).then(function(res) {
			if (res.CODE == "0") {
				$rootScope.USERINFO = $scope.userinfo;
				cacheService.setObject("USERINFO",$rootScope.USERINFO);
				// 保存默认地址到如果有地址就不再保存
				$state.go("user.profile");
				actService.queryCount("CUSERADD","USERID='"+ $rootScope.USERINFO.USERID+ "'").then(
					function(count) {
						if (count.CODE == "0" && count.DATA == 0) {
							actService.save("CUSERADD",$scope.address).then(
									function(result) {
										if (result.CODE != "0") {
											console.error(result.CODE + ":"	+ result.MSG);
										}
									});
						}
					});
			} else {
				console.error(res);
			}
		});
	};
	$scope.reset = function() {
		$scope.userinfo = $rootScope.USERINFO;
	};
	// 从数据字典中获取字段列表
	$scope.sex = appService.getAppDic($scope.MAINAPP,"SEX");
	$scope.provinces = {};
	// 获取省
	$scope.getProvinces = function() {
		cacheService.getRemoteSessionCache("REGION",
				"PROVINCE000000", {
					PARENT : "000000"
				}).then(function(data) {
			$scope.provinces = data;
		});
	};
// 获取省
	$scope.getProvinces();
	$scope.citys = {};
	$scope.countys = {};
	$scope.$watch('userinfo.CITY', function(county) {
		if (county != undefined) {
			$scope.countys = {};
			// 获取城市
			cacheService.getRemoteSessionCache(
					"REGION", "COUNTY" + county, {
						PARENT : county
					}).then(function(data) {
				$scope.countys = data;
			});
		}
	});
	// 更换省的时候获取城市
	$scope.$watch('userinfo.PROVINCE', function(
			province) {
		if (province != undefined) {
			$scope.citys = {};
			// 获取城市
			cacheService.getRemoteSessionCache(
					"REGION", "CITY" + province, {
						PARENT : province
					}).then(function(data) {
				$scope.citys = data;
			});
		}
	});
}]);
