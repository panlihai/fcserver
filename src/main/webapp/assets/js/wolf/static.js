appMdl.service("constantService",function($rootScope){
	this.PID="WOLF";
	this.HOST="yd.pcorp.cn";
});
//静态变量
appMdl.service("staticService",function($rootScope,cacheService,userService,constantService){
	
	this.getPid=function(){
		return constantService.PID;
	};
	this.getUserToken = function(pid) {
		if(cacheService.get("USERTOKEN") != undefined){			
			$rootScope.USERTOKEN = cacheService.get("USERTOKEN");
			$rootScope.autoLogin(pid);	
		}else{
			userService.getUserToken(pid).then(function(res) {
				// 获取资源对象
				if(res.CODE==0){
					cacheService.set("USERTOKEN",res.DATA.USERTOKEN);
					$rootScope.USERTOKEN = res.DATA.USERTOKEN;
					$rootScope.autoLogin(pid);
				}else{
					console.log(res);
				}
			});						
		}
	};	
	$rootScope.autoLogin = function(pid){
		var userinfo = cacheService.getObject("USERINFO");
		var autoLogin = cacheService.get("autoLogin");
		if(userinfo!=undefined 
				&& autoLogin!=undefined 
					&& autoLogin=="true"){
			// 自动登录
			$rootScope.USERINFO = cacheService.getObject("USERINFO"); 
			$rootScope.isLogin = true;
		}else{
			$rootScope.isLogin = false;
		} 	
	}
	this.getUserToken(this.getPid());
});
/**
 * 公用方法,包含base64加密,随机数
 */
appMdl.service("commonService",function($rootScope) {	
	var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	// 加密 base64加密开始
	this.enCode64 = function(input) {		
		var output = "";
		var chr1, chr2, chr3 = "";
		var enc1, enc2, enc3, enc4 = "";
		var i = 0;
		do {
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
			output = output + keyStr.charAt(enc1)+ keyStr.charAt(enc2) + keyStr.charAt(enc3) + keyStr.charAt(enc4);
			chr1 = chr2 = chr3 = "";
			enc1 = enc2 = enc3 = enc4 = "";
		} while (i < input.length);
		return output;
	}
	//解密函数
	this.deCode64 = function(input){
		var output = "";
		var chr1, chr2, chr3 = "";
		var enc1, enc2, enc3, enc4 = "";
		var i = 0;
		if(input.length%4!=0){
	    	return "";
		}
		var base64test = /[^A-Za-z0-9\+\/\=]/g;	
		if (base64test.exec(input)){
			return "";
		}
		do {
			enc1 = keyStr.indexOf(input.charAt(i++));
			enc2 = keyStr.indexOf(input.charAt(i++));
			enc3 = keyStr.indexOf(input.charAt(i++));
			enc4 = keyStr.indexOf(input.charAt(i++));
			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;
			output = output + String.fromCharCode(chr1);
			if (enc3 != 64) { 
				output+=String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output+=String.fromCharCode(chr3);
			}
			chr1 = chr2 = chr3 = "";
			enc1 = enc2 = enc3 = enc4 = "";
		} while (i < input.length);
		return this.strAnsi2Unicode(output);
	};
	this.strAnsi2Unicode = function(asContents)
	{
	    var len1=asContents.length;
	    var temp="";
	    var chrcode;
	    for(var i=0;i<len1;i++)
	    {
	        var varasc=asContents.charCodeAt(i);
	        if(varasc>127)
	        {
	            chrcode=this.AnsiToUnicode((varasc<<8)+asContents.charCodeAt(++i));
	        }
	        else
	        {
	            chrcode=varasc;
	        }
	        temp+=String.fromCharCode(chrcode);
	    }
	    return temp;
	};
	this.AnsiToUnicode = function (chrCode)
	{
	    var chrHex=chrCode.toString(16);
	    chrHex="000"+chrHex.toUpperCase();
	    chrHex=chrHex.substr(chrHex.length-4);
	    var i= AnsicodeChr().indexOf(chrHex);
	    if(i!=-1)
	    {
	       chrHex=UnicodeChr().substr(i,4);
	    }
	    return parseInt(chrHex,16)
	};
	this.UnicodeToAnsi = function(chrCode)
	{
	    var chrHex=chrCode.toString(16);
	    chrHex="000"+chrHex.toUpperCase();
	    chrHex=chrHex.substr(chrHex.length-4);
	    var i=UnicodeChr().indexOf(chrHex);
	    if(i!=-1)
	    {
	            chrHex=AnsicodeChr().substr(i,4);
	    }
	    return parseInt(chrHex,16);
	};
	this.Random = function(n) {
		var chars = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
		var res = "";
	     for(var i = 0; i < n ; i ++) {
	         var id = Math.ceil(Math.random()*35);
	         res += chars[id];
	     }
	     return res;
	}
});
/**
 * 缓存服务
 */
appMdl.service("cacheService",
		function($rootScope, $window,commonService) {
	$rootScope.keys = [];
	//存储单个属性
	this.set = function(key,value){
		$rootScope.keys.push(key);
		$window.localStorage[key] = commonService.enCode64(value);
	};
	//读取单个属性
	this.get = function(key,defaultValue){	
		var v = $window.localStorage[key] ;
		if(v != undefined){
			return commonService.deCode64(v);
		}else{
			return defaultValue;
		}
	};
	//存储对象，以JSON格式存储
	this.setObject = function(key,value){
		$rootScope.keys.push(key);
		$window.localStorage[key] = commonService.enCode64(JSON.stringify(value));
	};
	//读取对象
	this.getObject = function (key) {
		return JSON.parse(this.get(key,'{}'));
	}; 
});


