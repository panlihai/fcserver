/**
 * 微信服务启动
 */
appMdl.service("weixinService",["daoService",
                                function(daoService){
	/**
	 * wxReqUrl 请求服务的url
	 * wxappId 微信公众号appid
	 * reqUrl当前网页的url
	 */
	this.wxAct = function(wxReqUrl,wxappId,requrl){
		daoService.doAct(wxReqUrl,{"url":requrl}).then(function(res){
			wx.config({
			    debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			    appId: wxappId, // 必填，公众号的唯一标识
			    timestamp:res.timestamp, // 必填，生成签名的时间戳
			    nonceStr: res.noncestr, // 必填，生成签名的随机串
			    signature:res.signature,// 必填，签名，见附录1
			    jsApiList: ["onMenuShareTimeline","onMenuShareAppMessage","onMenuShareQQ","onMenuShareWeibo",
			                "onMenuShareQZone","startRecord","stopRecord","onVoiceRecordEnd","playVoice",
			                "pauseVoice","stopVoice","onVoicePlayEnd","uploadVoice","downloadVoice",
			                "chooseImage","previewImage","uploadImage","downloadImage","translateVoice",
			                "getNetworkType","openLocation","getLocation","hideOptionMenu","showOptionMenu",
			                "hideMenuItems","showMenuItems","hideAllNonBaseMenuItem","showAllNonBaseMenuItem",
			                "closeWindow","scanQRCode","chooseWXPay","openProductSpecificView","addCard",
			                "chooseCard","openCard"] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
			});
			
		});
	}	
}]);
/**
 * 获取用户usertoken
 */
appMdl.service("userService",["$rootScope","baseService","cacheService","commonService",
		function($rootScope, baseService,cacheService,commonService) {		
	//登录
	this.login = function(user) {
		var url = "USER/1.0/LOGIN";
		var json = {
			"USERID" : commonService.enCode64(user.USERID),
			"PASSWORD" : commonService.enCode64(user.PASSWORD)
		};
		return baseService.doAct(url, json);
	};
	// 自动启动
	// base64加密结束
	// 获取手机验证码
	this.getTelsms = function(user) {
		var url = "SMS/1.0/TELSMS";
		var json = {
			"RANDOM" : user.RANDOM,
			"MOBILEPHONE" : user.MOBILEPHONE
		};
		return baseService.doAct(url, json);
	};
	//注册
	this.register = function(user) {
		var url = "USER/1.0/REGISTER";
		return baseService.doAct(url, user);
	};
	//注销
	this.logout = function() {
		cacheService.set("AUTOLOGIN","false");
		$rootScope.isLogin = false;
		$rootScope.USERINFO = {};
		var url = "USER/1.0/LOGOUT";
		var json = {
				"USERID" : commonService.enCode64(cacheService.get("USERID"))
			};
		return baseService.doAct(url, json);
	};
	this.getUserToken = function() {
		var usertoken = cacheService.get("USERTOKEN");		
		if (usertoken != undefined) {
			$rootScope.USERTOKEN = usertoken;
			//this.autoLogin();
		} else {
			var url = "USERTOKEN/1.0/CREATE";
			var json = {};
			var userS = this;
			baseService.doAct(url, json).then(function(res) {
				// 获取资源对象
				if (res.CODE == 0) {
					var usertoken = res.DATA.RESULT[0].USERTOKEN;
					cacheService.set("USERTOKEN", usertoken);
					$rootScope.USERTOKEN = usertoken;
					userS.autoLogin();					
				} else {
					alert("网络异常");
					console.error(res.MSG);
				}
			});
		}
	};
	this.autoLogin = function() {
		var userinfo = cacheService.getObject("USERINFO");
		var autoLogin = cacheService.get("AUTOLOGIN");
		if (userinfo != undefined && autoLogin != undefined
				&& autoLogin == "true") {
			// 自动登录
			$rootScope.USERINFO = userinfo;
			$rootScope.isLogin = true;
		} else {
			$rootScope.isLogin = false;
			var url = "USERTOKEN/1.0/CREATE";
			var json = {};
			var userS = this;
			baseService.doAct(url, json).then(function(res) {
				// 获取资源对象
				if (res.CODE == 0) {
					var usertoken = res.DATA.RESULT[0].USERTOKEN;
					cacheService.set("USERTOKEN", usertoken);
					$rootScope.USERTOKEN = usertoken;					
				} else {
					alert("网络异常");
					console.error(res.MSG);
				}
			});
		}
	};
}]);
/**
 * 增删改查
 */
appMdl.service("actService",["$rootScope","baseService",
                             function($rootScope, baseService) {
	this.save = function(appid,json){
		var array = new Array();
		array.push(json);
		return this.saveList(appid,array);
	};
	this.saveList = function(appid,json){
		var js = JSON.stringify(json)
		return baseService.doAct(appid + "/1.0/CREATE",{DATA:js});
	};
	this.del = function(appid,json){		
		return baseService.doAct(appid +"/1.0/REMOVE", json);
	};
	this.update = function(appid,json){				
		return baseService.doAct(appid +"/1.0/UPDATE", {DATA:json});
	};
	this.query = function(appid,json){				
		return baseService.doAct(appid +"/1.0/LISTINFO", json);
	};
	this.queryCount = function(appid,listwhere){
		if(appid == undefined || appid.length==0){
			console.error("请加入APPCODE参数");
			return;
		}
		if(listwhere == undefined || listwhere.length==0){
			listwhere = "1=1";
		}
		return baseService.doAct("APPCOUNT/1.0/COUNT", {APPCODE:appid,LISTWHERE:listwhere});
	};
	this.get = function(appid,json){				
		return baseService.doAct(appid +"/1.0/INFO", json);
	};
	this.getApp = function(json){				
		return baseService.doAct("APPINFO/1.0/APPDETAIL", json);
	};
}]);


/**
 * 
 */
appMdl.service("appService",["$rootScope","actService","cacheService",
                             function($rootScope, actService,cacheService) {
	//初始化应用程序,从后台获取应用程序数据.
	this.initApp = function(apps){
		for(var i =0 ;i<apps.length;i++){
			var appid = apps[i];
			var app = cacheService.getObject(appid);
			if(app.APPID == undefined){
				actService.getApp({"APPCODE":appid}).then(function(res){
					if(res.CODE=="0"){
						var app = res.DATA;
						cacheService.setObject(app.APPID,app);				
					}
				});
			}		
		}
	};
	//获取APP;
	this.getApp = function(appid){
		if(appid == undefined){
			console.error("appid不能为空");
			return;
		}		
		return cacheService.getObject(appid);
	};
	//获取数据字典;
	this.getAppDic = function(app,fieldCode){
		if(app == undefined){
			console.error("应用未加载,请先初始化,调用appService.initApp()方法");
			return;
		}
		var diccode = this.getAppField(app, fieldCode).DICCODE;
		var item = {};
		for(var i=0 ;i<app.P_APPDICS.length;i++){
			var dic = app.P_APPDICS[i];
			if(dic.DICID==diccode){
				item = dic;
				break;
			}
		}
		return item.P_APPDICDETAILS;
	};
    this.getAppLinks = function(app){
    	if(app == undefined){
    		console.error("应用未加载,请先初始化,调用appService.initApp()方法");
			return;
		}
		return app.P_APPLINKS;		
	};
	this.getAppLink = function(app,linkAppId){
    	var items = this.getAppLinks(app);
    	if(linkAppId == undefined || linkAppId.length == 0){
    		console.error("linkAppId不能为空");
			return;
    	}
    	var item ={};
    	for(var i=0;i<items.length;i++){
    		var a = items[i];
    		if(a.ITEMAPP == linkAppId){
    			item = a;
    			break;
    		}
    	}
		return item;		
	};
	this.getAppButtons = function(app){
    	if(app == undefined){
    		console.error("应用未加载,请先初始化,调用appService.initApp()方法");
			return;
		}
		return app.P_APPBUTTONS;		
	};
	this.getAppButton = function(app,btnCode){
    	var items = this.getAppButtons(app);
    	if(btnCode == undefined || btnCode.length == 0){
    		console.error("btnCode不能为空");
			return;
    	}
    	var item ={};
    	for(var i=0;i<items.length;i++){
    		var a = items[i];
    		if(a.BTNCODE == btnCode){
    			item = a;
    			break;
    		}
    	}
		return item;		
	};
	this.getAppFields = function(app){
    	if(app == undefined){
    		console.error("应用未加载,请先初始化,调用appService.initApp()方法");
			return;
		}
		return app.P_APPFIELDS;		
	};
	
	this.getAppField = function(app,fieldCode){
    	var items = this.getAppFields(app);
    	if(fieldCode == undefined || fieldCode.length == 0){
    		console.error("linkAppId不能为空");
			return;
    	}
    	var item ={};
    	for(var i=0;i<items.length;i++){
    		var a = items[i];
    		if(a.FIELDCODE == fieldCode){
    			item = a;
    			break;
    		}
    	}
		return item;		
	};
	this.getAppField = function(app,fieldCode){
    	var items = this.getAppFields(app);
    	if(fieldCode == undefined || fieldCode.length == 0){
    		console.error("linkAppId不能为空");
			return;
    	}
    	var item ={};
    	for(var i=0;i<items.length;i++){
    		var a = items[i];
    		if(a.FIELDCODE == fieldCode){
    			item = a;
    			break;
    		}
    	}
		return item;		
	};
	this.getAppBtnListOne = function(appid){
		var btnList = [];
    	var btnListAll = this.getAppButtons(appid);
    	for(var i =0 ;i < btnListAll.length;i++){
			var btnObj = btnListAll[i];
			if(btnObj.BTNTYPE=='LISTONE'){
				btnList.push(btnObj);
			}						
		};
		return btnList;		
	};
	this.getAppBtnList = function(appid){
		var btnList = [];
    	var btnListAll = this.getAppButtons(appid);
    	for(var i =0 ;i < btnListAll.length;i++){
			var btnObj = btnListAll[i];
			if(btnObj.BTNTYPE=='LIST'){
				btnList.push(btnObj);
			}						
		};
		return btnList;		
	};
	this.getAppBtnCardList = function(appid){
		var btnList = [];
    	var btnListAll = this.getAppButtons(appid);
    	for(var i =0 ;i < btnListAll.length;i++){
			var btnObj = btnListAll[i];
			if(btnObj.BTNTYPE=='CARD'){
				btnList.push(btnObj);
			}						
		};
		return btnList;		
	};
}]);
