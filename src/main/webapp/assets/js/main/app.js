"use strict";
var appMdl = angular.module('app', [ "ui.router", "ngAnimate", "ngCookies",
		"ngSanitize", "com.2fdevs.videogular",
		"com.2fdevs.videogular.plugins.controls",
		"com.2fdevs.videogular.plugins.overlayplay",
		"com.2fdevs.videogular.plugins.poster" ]);

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
appMdl.run(function($rootScope, $state, $stateParams) {
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;
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
	$urlRouterProvider.otherwise('/index');
	$stateProvider.state('index', {
		url : '/index',
		views : {
			'' : {
				templateUrl : 'assets/tpls/main/index.html'
			},
			'nav@index' : {
				templateUrl : 'assets/tpls/main/nav.html'
			},
			'banner@index' : {
				templateUrl : 'assets/tpls/main/banner.html'
			},
			'body@index' : {
				templateUrl : 'assets/tpls/main/body.html'
			},
			'modal@index' : {
				templateUrl : 'assets/tpls/main/modal/modal.html'
			},
			'loginregister@index' : {
				templateUrl : 'assets/tpls/main/modal/loginregister.html'
			}
		}
	}).state('main', {
		url : '/{bookType:[0-9]{1,4}}',
		views : { // 注意这里的写法，当一个页面上带有多个ui-view的时候如何进行命名和视图模板的加载动作
			'' : {
				templateUrl : 'assets/tpls/bookList.html'
			},
			'booktype@booklist' : {
				templateUrl : 'assets/tpls/bookType.html'
			},
			'bookgrid@booklist' : {
				templateUrl : 'assets/tpls/bookGrid.html'
			}
		}
	}).state('addbook', {
		url : '/addbook',
		templateUrl : 'assets/tpls/addBookForm.html'
	}).state('bookdetail', {
		url : '/bookdetail/:bookId', // 注意这里在路由中传参数的方式
		templateUrl : 'tpls/bookDetail.html'
	})
});
