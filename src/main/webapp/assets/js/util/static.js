

//核心请求
appMdl.factory("daoService",["$http","$q",
function($http, $q) {
	return {
		doAct : function(url, json) {
			var deferred = $q.defer(); // 声明延后执行，表示要去监控后面的执行
			// 如果已存在则直接返回
			var transform = function(data) {
				return $.param(data);
			};			
			$http.post(url,json,{
						headers : {"Content-Type" : "application/x-www-form-urlencoded;charset=UTF-8"},
						transformRequest : transform})
						.success(function(data, status, headers, config) {
									deferred.resolve(data); // 声明执行成功，即http请求数据成功，可以返回数据了
						}).error(function(data, status,headers, config) {
									alert(data + status+ headers+ config);
									deferred.reject(data);
								});
			return deferred.promise;
			}
	};
}]);
//包装产品的核心请求
appMdl.factory("baseService",["$http","$q","$rootScope","constantService",
function($http, $q, $rootScope,constantService) {
	return {
		doAct : function(url, json) {
			var host = constantService.HOST+constantService.REQURL + constantService.PID + "/";
			var deferred = $q.defer(); // 声明延后执行，表示要去监控后面的执行
			// 如果已存在则直接返回
			var transform = function(data) {
				return $.param(data);
			};
			var lat = "0";
			var lng = "0";			
			var params = {
					"USERTOKEN" : $rootScope.USERTOKEN,// 时间戳
					"TIMESTAMP" : Date.parse(new Date()) / 1000,
					"LAT" : lat,
					"LNG" : lng
					};

			
			$http.post(host + url,$.extend(true,params,json),{
						headers : {"Content-Type" : "application/x-www-form-urlencoded;charset=UTF-8"},
						transformRequest : transform})
					.success(function(data, status, headers, config) {
						deferred.resolve(data); // 声明执行成功，即http请求数据成功，可以返回数据了
					}).error(function(data, status,headers, config) {
						console.error(data + status+ headers+ config);
						deferred.reject(data);
			});			
			return deferred.promise;
		}
	};
}]);

/**
 * 缓存服务
 */
appMdl.service("cacheService",["$window","$q","commonService","actService",
                               function($window,$q,commonService,actService) {
	// 存储单个属性
	this.set = function(key, value) {
		$window.localStorage[key] = commonService.enCode64(value);
	};
	// 读取单个属性
	this.get = function(key, defaultValue) {
		var v = $window.localStorage[key];
		if (v != undefined) {
			return commonService.deCode64(v);
		} else {
			return defaultValue;
		}
	};
	// 存储对象，以JSON格式存储
	this.setObject = function(key, value) {		
		$window.localStorage[key] = commonService.enCode64(JSON.stringify(value));
	};
	// 读取对象
	this.getObject = function(key) {
		return JSON.parse(this.get(key, '{}'));
	};
	//移除
	this.remove = function(key){
		$window.localStorage.removeItem(key);
	};
	
	// 存储单个属性
	this.setS = function(key, value) {
		$window.sessionStorage[key] = value;
	};
	// 读取单个属性
	this.getS = function(key, defaultValue) {
		var v = $window.sessionStorage[key];
		if (v != undefined) {
			return v;
		} else {
			return defaultValue;
		}
	};
	// 存储对象，以JSON格式存储
	this.setSObject = function(key, value) {		
		$window.sessionStorage[key] = JSON.stringify(value);
	};
	// 读取对象
	this.getSObject = function(key) {
		return JSON.parse(this.getS(key, '{}'));
	};
	//移除
	this.removeS = function(key){
		$window.sessionStorage.removeItem(key);
	};
	//获取远程对象并缓存,如果则从中获取
	this.getRemoteLocalCache = function(appId,cacheName,json){
		var deferred = $q.defer(); 
		var object  = this.getObject(cacheName);
		if(!(object instanceof Array)){			
			actService.query(appId,json).then(function(res){
				if(res.CODE=="0"){								
					deferred.resolve(res.DATA);
					$window.localStorage[cacheName] = commonService.enCode64(JSON.stringify(res.DATA));
				}else{
					deferred.reject(res);
				}
			});
		}else{
			deferred.resolve(object);
			deferred.reject(object);
		}		
		return deferred.promise;
	};
	//获取远程对象并缓存,如果则从中获取
	this.getRemoteSessionCache = function(appId,cacheName,json){
		var deferred = $q.defer(); 
		var object  = this.getSObject(cacheName);
		if(!(object instanceof Array)){			
			actService.query(appId,json).then(function(res){
				if(res.CODE=="0"){								
					deferred.resolve(res.DATA);
					$window.sessionStorage[cacheName] = JSON.stringify(res.DATA);
				}else{
					deferred.reject(res);
				}
			});
		}else{
			deferred.resolve(object);
			deferred.reject(object);
		}		
		return deferred.promise;
	};
}]);
//地图服务
appMdl.service("mapService",
		function($rootScope) {
			$rootScope.windowsArr = [];
			$rootScope.marker = [];
			$rootScope.map = new AMap.Map("mapContainer", {
				resizeEnable : true,
				view : new AMap.View2D({
					resizeEnable : true,
					zoom : 15
				// 地图显示的缩放级别
				}),
				keyboardEnable : false
			});
			$rootScope.map.plugin('AMap.Geolocation', function() {
				geolocation = new AMap.Geolocation({
					enableHighAccuracy : true,// 是否使用高精度定位，默认:true
					timeout : 10000, // 超过10秒后停止定位，默认：无穷大
					buttonOffset : new AMap.Pixel(10, 20),// 定位按钮与设置的停靠位置的偏移量，默认：Pixel(10,
					// 20)
					zoomToAccuracy : false, // 定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
					buttonPosition : 'RB'
				});
				$rootScope.map.addControl(geolocation);
				geolocation.getCurrentPosition();
				AMap.event.addListener(geolocation, 'complete',
						$rootScope.onComplete);// 返回定位信息
				AMap.event.addListener(geolocation, 'error',
						$rootScope.onError); // 返回定位出错信息
			});
			// 解析定位结果
			$rootScope.onComplete = function(data) {
				var str = [ '定位成功' ];
				str.push('经度：' + data.position.getLng());
				str.push('纬度：' + data.position.getLat());
				str.push('精度：' + data.accuracy + ' 米');
				str.push('是否经过偏移：' + (data.isConverted ? '是' : '否'));
				console.log(str);
				// this.mapObj.panTo([
				// data.position.getLat(),data.position.getLng()]);
			};
			// 解析定位错误信息
			$rootScope.onError = function(data) {
				console.log('定位失败');
			};
			$rootScope.marker = new AMap.Marker({
				position : $rootScope.map.getCenter()
			});
			$rootScope.marker.setMap($rootScope.map);
			$rootScope.marker.on('click', function(e) {
				$rootScope.infowindow.open($rootScope.map, e.target.getPosition());
			});
			$rootScope.infowindow = new AMap.InfoWindow({
			     content: '<span class="info">您需要什么不锈钢制品？</span><div> <textarea rows="5" cols="22" name="content"></textarea><br/>'
			    	 +'<input type="button" class="button" value="提交需求" onClick="javascript:alert(document.getElementByName(\'content\').value);"/></div>',
			     offset: new AMap.Pixel(0, -30),
			     size:new AMap.Size(200,0)
			})
			AMap.plugin([ 'AMap.ToolBar', 'AMap.Scale' ], function() {
				var toolBar = new AMap.ToolBar();
				var scale = new AMap.Scale();
				$rootScope.map.addControl(toolBar);
				$rootScope.map.addControl(scale);
			});
			$rootScope.infowindow.open($rootScope.map, $rootScope.map.getCenter());			
		});

/**
 * 公用方法,包含base64加密,随机数
 */
appMdl.service("commonService",
				function() {
	this.base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";  
	this.base64DecodeChars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
			-1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 
			63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 
			-1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 
			6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 
			19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, 
			-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 
			37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
			49, 50, 51, -1, -1, -1, -1, -1);  
	/** 
	 * base64编码 
	 * @param {Object} str 
	 */  
	this.base64encode = function(str){  
	    var out, i, len;  
	    var c1, c2, c3;  
	    len = str.length;  
	    i = 0;  
	    out = "";  
	    while (i < len) {  
	        c1 = str.charCodeAt(i++) & 0xff;  
	        if (i == len) {  
	            out += this.base64EncodeChars.charAt(c1 >> 2);  
	            out += this.base64EncodeChars.charAt((c1 & 0x3) << 4);  
	            out += "==";  
	            break;  
	        }  
	        c2 = str.charCodeAt(i++);  
	        if (i == len) {  
	            out += this.base64EncodeChars.charAt(c1 >> 2);  
	            out += this.base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));  
	            out += this.base64EncodeChars.charAt((c2 & 0xF) << 2);  
	            out += "=";  
	            break;  
	        }  
	        c3 = str.charCodeAt(i++);  
	        out += this.base64EncodeChars.charAt(c1 >> 2);  
	        out += this.base64EncodeChars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));  
	        out += this.base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6));  
	        out += this.base64EncodeChars.charAt(c3 & 0x3F);  
	    }  
	    return out;  
	};  
	/** 
	 * base64解码 
	 * @param {Object} str 
	 */  
	this.base64decode =  function(str){  
	    var c1, c2, c3, c4;  
	    var i, len, out;  
	    len = str.length;  
	    i = 0;  
	    out = "";  
	    while (i < len) {  
	        /* c1 */  
	        do {  
	            c1 = this.base64DecodeChars[str.charCodeAt(i++) & 0xff];  
	        }  
	        while (i < len && c1 == -1);  
	        if (c1 == -1)   
	            break;  
	        /* c2 */  
	        do {  
	            c2 = this.base64DecodeChars[str.charCodeAt(i++) & 0xff];  
	        }  
	        while (i < len && c2 == -1);  
	        if (c2 == -1)   
	            break;  
	        out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));  
	        /* c3 */  
	        do {  
	            c3 = str.charCodeAt(i++) & 0xff;  
	            if (c3 == 61)   
	                return out;  
	            c3 = this.base64DecodeChars[c3];  
	        }  
	        while (i < len && c3 == -1);  
	        if (c3 == -1)   
	            break;  
	        out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));  
	        /* c4 */  
	        do {  
	            c4 = str.charCodeAt(i++) & 0xff;  
	            if (c4 == 61)   
	                return out;  
	            c4 = this.base64DecodeChars[c4];  
	        }  
	        while (i < len && c4 == -1);  
	        if (c4 == -1)   
	            break;  
	        out += String.fromCharCode(((c3 & 0x03) << 6) | c4);  
	    }  
	    return out;  
	};  
	/** 
	 * utf16转utf8 
	 * @param {Object} str 
	 */  
	this.utf16to8 = function(str){  
	    var out, i, len, c;  
	    out = "";  
	    len = str.length;  
	    for (i = 0; i < len; i++) {  
	        c = str.charCodeAt(i);  
	        if ((c >= 0x0001) && (c <= 0x007F)) {  
	            out += str.charAt(i);  
	        }  
	        else   
	            if (c > 0x07FF) {  
	                out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));  
	                out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));  
	                out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));  
	            }  
	            else {  
	                out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));  
	                out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));  
	            }  
	    }  
	    return out;  
	};  
	/** 
	 * utf8转utf16 
	 * @param {Object} str 
	 */  
	this.utf8to16 = function(str){  
	    var out, i, len, c;  
	    var char2, char3;  
	    out = "";  
	    len = str.length;  
	    i = 0;  
	    while (i < len) {  
	        c = str.charCodeAt(i++);  
	        switch (c >> 4) {  
	            case 0:  
	            case 1:  
	            case 2:  
	            case 3:  
	            case 4:  
	            case 5:  
	            case 6:  
	            case 7:  
	                // 0xxxxxxx  
	                out += str.charAt(i - 1);  
	                break;  
	            case 12:  
	            case 13:  
	                // 110x xxxx 10xx xxxx  
	                char2 = str.charCodeAt(i++);  
	                out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));  
	                break;  
	            case 14:  
	                // 1110 xxxx10xx xxxx10xx xxxx  
	                char2 = str.charCodeAt(i++);  
	                char3 = str.charCodeAt(i++);  
	                out += String.fromCharCode(((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));  
	                break;  
	        }  
	    }  
	    return out;  
	};
	/**
	 * 加密
	 */
	this.enCode64 = function(str){
		if(str==undefined){
			return "";
		}
		return this.base64encode(this.utf16to8(str));
	};
	/**
	 * 解密
	 */
	this.deCode64 = function(str){
		if(str==undefined){
			return "";
		}
		return this.utf8to16(this.base64decode(str));
	};
	//获取随机数
	this.Random = function(n) {
		var chars = [ '0', '1', '2', '3', '4', '5', '6', '7',
					'8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
					'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
					'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
					'Z' ];
		var res = "";
		for (var i = 0; i < n; i++) {
			var id = Math.ceil(Math.random() * 35);
			res += chars[id];
		}
		return res;
	};
	//获取时间戳
	this.Timestamp = function(){
		return Date.parse(new Date()) / 1000;
	};					
});