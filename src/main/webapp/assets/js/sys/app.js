"use strict";
//对浏览器的UserAgent进行正则匹配，不含有微信独有标识的则为其他浏览器
/*var useragent = navigator.userAgent;
if (useragent.match(/MicroMessenger/i) != 'MicroMessenger') {
    // 这里警告框会阻塞当前页面继续加载
    alert('已禁止本次访问：您必须使用微信内置浏览器访问本页面！');
    // 以下代码是用javascript强行关闭当前页面
    var opened = window.open('about:blank', '_self');
    opened.opener = null;
    opened.close();
};*/

var appMdl = angular.module("appSys", ["ui.router", "ngAnimate",
		"ngSanitize","platframe"]);
//变量
appMdl.service("constantService",["$rootScope",function($rootScope) {
	this.WXAPPID = "wx9df60758c5353e1b";
	this.PID = "SYSTEM";
	this.HOST = "http://127.0.0.1/server/";
	this.REQURL = "api/";
	this.WXREQURL = "wxjsapi/"; 
	this.USERINFO = "USERINFO";
	$rootScope.PROJECTNAME="系统管理";
}]);
/**
 * 由于整个应用都会和路由打交道，所以这里把$state和$stateParams这两个对象放到$rootScope上，方便其它地方引用和注入。
 * 这里的run方法只会在angular启动的时候运行一次。
 * 
 * @param {[type]}
 *            $rootScope
 * @param {[type]}
 *            $state
 * @param {[type]}
 *            $stateParams
 * @return {[type]}
 */
appMdl.run(function($rootScope, $state, $stateParams,appService,userService) {
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;	
	$rootScope.USERINFO = {};
	//获取usertoken
	userService.getUserToken();	
	appService.initApp(["CUSER","CUSERADD","CQUOTATIONBILL"]);		
});

/**
 * 配置路由。 注意这里采用的是ui-router这个路由，而不是ng原生的路由。 ng原生的路由不能支持嵌套视图，所以这里必须使用ui-router。
 * 
 * @param {[type]}
 *            $stateProvider
 * @param {[type]}
 *            $urlRouterProvider
 * @return {[type]}
 */
appMdl.config(function($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise("/index");
	$stateProvider.state("index", {
		url : "/index",
		views : {
			"" : {
				templateUrl : "assets/tpls/sys/index.html"
			},
			"modal@index" : {
				templateUrl : "assets/tpls/sys/common/modal.html"
				
			},
			"loginregister@index" : {
				templateUrl : "assets/tpls/sys/common/loginregister.html"
			}
		}
	});
});



