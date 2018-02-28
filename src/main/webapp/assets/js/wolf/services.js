//核心请求
appMdl.factory('baseService',['$http','$q','cacheService',"constantService",
function($http, $q, cacheService,constantService) {
	return {
		doAct : function(pid, url, json) {
			var host = "http://"+constantService.HOST+"/web/api/" + pid + "/";
			var deferred = $q.defer(); // 声明延后执行，表示要去监控后面的执行
			// 如果已存在则直接返回
			var transform = function(data) {
				return $.param(data);
			};
			var lat = "0";
			var lng = "0";
			// 获取定位
			navigator.geolocation.getCurrentPosition(
				function(position) {
					lng = position.coords.longitud;// 经度
					lat = position.coords.latitude;// 纬度
					// '精确度:position.coords.accuracy
					// '海拔高度精确度:position.coords.altitudeAccuracy
					// '运动方向:
					// position.coords.heading
					// '速度:position.coords.speed
					// '时间戳:new
					// Date(position.timestamp)
				},
				function(error) {
					lat = "0";
					lng = "0";});
			var params = {
					'USERTOKEN' : cacheService.get("USERTOKEN"),// 时间戳
					'TIMESTAMP' : Date.parse(new Date()) / 1000,
					'LAT' : lat,
					'LNG' : lng
					};
			$http.post(host + url,
					$.extend(true,params,json),
					{
						headers : {'Content-Type' : 'application/x-www-form-urlencoded;charset=UTF-8'},
						transformRequest : transform})
						.success(
								function(data, status, headers, config) {
									deferred.resolve(data); // 声明执行成功，即http请求数据成功，可以返回数据了
						}).error(
								function(data, status,headers, config) {
									alert(data + status+ headers+ config);
									deferred.reject(data);
								});
			return deferred.promise;
			}
	};
}]);

/**
 * 获取用户usertoken
 */
appMdl.service("userService",
		function($rootScope, baseService,cacheService,commonService) {		
	this.getUserToken = function(pid) {
		var url = "USERTOKEN/1.0/CREATE";
		var json = {};
		return baseService.doAct(pid, url, json);							
	};
	//获取用户信息
	this.getUserinfo = function(pid, user) {
		var obj = cacheService.getObject("USERINFO");
		return obj;
	};
	//登录
	this.login = function(pid, user) {
		var url = "USER/1.0/LOGIN";
		var json = {
			"USERID" : commonService.enCode64(user.USERID),
			"PASSWORD" : commonService.enCode64(user.PASSWORD)
		};
		return baseService.doAct(pid, url, json);
	};
	// 自动启动
	// base64加密结束
	// 获取手机验证码
	this.getTelsms = function(pid, user) {
		var url = "SMS/1.0/TELSMS";
		var json = {
			"RANDOM" : user.RANDOM,
			"MOBILEPHONE" : user.MOBILEPHONE
		};
		return baseService.doAct(pid, url, json);
	};
	//注册
	this.register = function(pid, user) {
		var url = "USER/1.0/REGISTER";
		return baseService.doAct(pid, url, user);
	};
	//注销
	this.logout = function(pid, user) {
		var url = "USER/1.0/LOGOUT";
		return baseService.doAct(pid, url, user);
	};
});
/**
 * 图书管理系统业务类
 */
appMdl.service('bookService', function(baseService) {
	/**
	 * 获取图书视频列表内容
	 */
	this.videoListinfo = function(pid) {
		var url = "VIDEO/1.0/LISTINFO";
		var json = {
			'BOOKID' : '1a77f29b3f644afc9066e2e683ab57a1',
			'CLIENTTYPE' : 'PAD',
			'PAGENUM' : 0,
			'PAGESIZE' : 20
		};
		return baseService.doAct(pid, url, json);
	};
	/**
	 * 获取图书视频详情内容
	 */
	this.videoInfo = function(pid, videoId) {
		var url = "VIDEO/1.0/INFO";
		var json = {
			'BOOKID' : '1a77f29b3f644afc9066e2e683ab57a1',
			'AUDIOID' : videoId,
			'CLIENTTYPE' : 'PAD'
		};
		return baseService.doAct(pid, url, json);
	};
	/**
	 * 获取音频列表内容
	 */
	this.audioListinfo = function(pid) {
		var url = "AUDIO/1.0/LISTINFO";
		var json = {
			'BOOKID' : '1a77f29b3f644afc9066e2e683ab57a1',
			'CLIENTTYPE' : 'PAD',
			'PAGENUM' : 0,
			'PAGESIZE' : 20
		};
		return baseService.doAct(pid, url, json);
	};
	/**
	 * 获取音频详情
	 */
	this.audioInfo = function(pid, audioId) {
		var url = "AUDIO/1.0/INFO";
		var json = {
			'BOOKID' : '1a77f29b3f644afc9066e2e683ab57a1',
			'AUDIOID' : audioId,
			'CLIENTTYPE' : 'PAD'
		};
		return baseService.doAct(pid, url, json);
	};
	/**
	 * 获取视频或音频资源路径.
	 */
	this.getResObj = function(pid, resId) {
		var url = "RESOURCE/1.0/INFO";
		var json = {
			'RESID' : resId
		}
		return baseService.doAct(pid, url, json);
	}
});// 图书service类结尾

