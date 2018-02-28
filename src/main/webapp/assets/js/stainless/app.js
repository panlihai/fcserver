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

var appMdl = angular.module("appSteel", [ "ui.router", "ngAnimate",
		"ngSanitize","platframe"]);
//变量
appMdl.service("constantService",["$rootScope",function($rootScope) {
	this.WXAPPID = "wx9df60758c5353e1b";
	this.PID = "STAINLESS";
	this.HOST = "http://yd.pcorp.cn/server/";
	this.REQURL = "api/";
	this.WXREQURL = "wxjsapi/"; 
	this.USERINFO = "USERINFO";
	$rootScope.PROJECTNAME="i不锈钢";
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
	$stateProvider.state("user",{
			url:"/user",				
			templateUrl : "assets/tpls/stainless/user.html"				
		}).state("user.home",{
			url:"/home",
			templateUrl : "assets/tpls/stainless/common/home.html",
			controller : "homeCtrl"
		}).state("user.login",{
			url:"/login",
			templateUrl : "assets/tpls/stainless/common/login.html",
			controller : "loginRegisterCtrl"
		}).state("user.register",{
			url:"/register",
			templateUrl : "assets/tpls/stainless/common/register.html",
			controller : "loginRegisterCtrl"
		}).state("user.placeorder",{
			url : "/placeorder",
			templateUrl : "assets/tpls/stainless/placeorder.html",
			controller : "placeorderCtrl"
		}).state("user.order",{
			url : "/order",
			templateUrl :"assets/tpls/stainless/user/order.html",
			controller:"orderCtrl"
		}).state("user.account",{
			url : "/account",
			templateUrl :"assets/tpls/stainless/user/account.html",
			controller:"accountCtrl"
		}).state("user.profile",{
			url : "/profile",
			templateUrl :"assets/tpls/stainless/user/profile.html",
			controller:"profileCtrl"
		}).state("user.userinfo",{
			url : "/userinfo",
			templateUrl :"assets/tpls/stainless/user/userinfo.html",
			controller:"userinfoCtrl"
		}).state("user.customer",{
			url : "/customer",
			templateUrl :"assets/tpls/stainless/user/customer.html",
			controller:"customerCtrl"				
		}).state("user.invitation",{
			url : "/invitation",
			templateUrl :"assets/tpls/stainless/user/invitation.html",
			controller:"invitationCtrl"
		}).state("user.attention",{
			url : "/attention",
			templateUrl :"assets/tpls/stainless/user/attention.html",
			controller:"attentionCtrl"
		}).state("user.collect",{
			url : "/collect",
			templateUrl :"assets/tpls/stainless/user/collect.html",
			controller:"collectCtrl"
		}).state("user.message",{
			url : "/message",
			templateUrl :"assets/tpls/stainless/user/message.html",
			controller:"messageCtrl"
		}).state("user.say",{
			url : "/say",
			templateUrl :"assets/tpls/stainless/user/say.html",
			controller:"sayCtrl"
		}).state("user.help",{
			url : "/help",
			templateUrl :"assets/tpls/stainless/user/help.html",
			controller:"helpCtrl"
		}).state("index", {
		url : "/index",
		views : {
			"" : {
				templateUrl : "assets/tpls/stainless/index.html"
			},
			"nav@index" : {
				templateUrl : "assets/tpls/stainless/nav.html"
			},
			"body@index" : {
				templateUrl : "assets/tpls/stainless/content.html"
			},
			"loginregister@index" : {
				templateUrl : "assets/tpls/stainless/common/loginregister.html"
			}
		}
	});
});



