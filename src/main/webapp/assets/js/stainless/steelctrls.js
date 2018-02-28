"use strict";
// 个人信息控制器
appMdl.controller("profileCtrl", [ "$rootScope", "$scope", "cacheService",
		"$state", function($rootScope, $scope, cacheService, $state) {

		} ]);

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
// 控制器全部订单
appMdl.controller("orderCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};
			$scope.paginationConf = {
				currentPage : 1,
				totalItems : 8000,
				itemsPerPage : 15,
				pagesLength : 15,
				perPageOptions : [ 10, 20, 30, 40, 50 ],
				rememberPerPage : 'perPageItems',
				onChange : function() {

				}
			};
		} ]);
// 控制器我的客户
appMdl.controller("customerCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};

		} ]);
// 控制器邀请好友
appMdl.controller("invitationCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};

		} ]);
// 控制器关注厂家
appMdl.controller("attentionCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};

		} ]);
// 控制器我的收藏
appMdl.controller("collectCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};
		} ]);
// 控制器我的消息
appMdl.controller("messageCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};

		} ]);
// 控制器我要吐槽
appMdl.controller("sayCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};

		} ]);
// 控制器联系小i客服
appMdl.controller("helpCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.orderlist = {};
			$scope.getOrderList = function() {

			};
		} ]);

appMdl.controller("accountCtrl", [ "$rootScope", "$scope",
		function($rootScope, $scope) {
			$scope.account = {

			};
		} ]);

appMdl.controller("placeorderCtrl", [
		"$rootScope",
		"$scope",
		"weixinService",
		"cacheService",
		"constantService",
		"commonService",
		"actService",
		function($rootScope, $scope, weixinService, cacheService,
				constantService, commonService,actService) {
			// 第一页显示
			$scope.firstPage = true;
			// 第二页显示
			$scope.twoPage = false;
			// 第三页显示
			$scope.threePage = false;
			// 显示第二页
			$scope.oneNext = function() {
				$scope.firstPage = false;
				$scope.twoPage = true;
			};
			// 显示上一页
			$scope.oneUp = function() {
				$scope.firstPage = true;
				$scope.twoPage = false;
			};
			// 显示第三页
			$scope.twoNext = function() {
				$scope.twoPage = false;
				$scope.threePage = true;
			};
			// //显示上一页
			$scope.twoUp = function() {
				$scope.twoPage = true;
				$scope.threePage = false;
			};
			// 默认的地址
			$scope.address = {};
			// 默认的地址
			$scope.defaultAddress = {
				PROVINCE : "",
				CITY : "",
				COUNTY : "",
				ADDRESS : ""
			};
			// 获取默认地址内容。
			$scope.getDefaultAddress = function() {
				cacheService.getRemoteSessionCache("CUSERADD", "CUSERADD", {
					USERID : $rootScope.USERINFO.USERID
				}).then(function(data) {
					$scope.address = data;
					for (var i = 0; i < data.length; i++) {
						var add = data[i];
						if (add.ISDEFAULT == 'Y') {
							$scope.defaultAddress = add;
							break;
						}
					}
				});
			};
			// 获取默认
			$scope.getDefaultAddress();
			// 订单主表内容
			$scope.order = {
				PID : "STAINLESS",
				USERID : "",
				REALNAME : "",
				TEL : "",
				PROVINCE : "",
				CITY : "",
				COUNTY : "",
				ADDRESS : "",
				CREATOR : "",
				CREATETIME : commonService.Timestamp()
			};
			//初始化订单内容
			$scope.initOrder = function(){
				$scope.order.USERID = $rootScope.USERINFO.USERID;
				$scope.order.REALNAME = $rootScope.USERINFO.REALNAME;
				$scope.order.TEL = $rootScope.USERINFO.TEL;
				$scope.order.PROVINCE = $scope.defaultAddress.PROVINCE;
				$scope.order.CITY = $scope.defaultAddress.CITY;
				$scope.order.COUNTY = $scope.defaultAddress.COUNTY;
				$scope.order.ADDRESS = $scope.defaultAddress.ADDRESS;
				$scope.order.CREATOR = $rootScope.USERINFO.USERID;
			};
			$scope.initOrder();
			// 监控默认地址是否有内容
			$scope.$watch('defaultAddress.PROVINCE', function(province) {
				if (province != undefined) {
					$scope.order.PROVINCE = $scope.defaultAddress.PROVINCE;
					$scope.order.CITY = $scope.defaultAddress.CITY;
					$scope.order.COUNTY = $scope.defaultAddress.COUNTY;
					$scope.order.ADDRESS = $scope.defaultAddress.ADDRESS;
				}
			});
			//省列表
			$scope.provinces = {};
			// 获取省列表
			$scope.getProvinces = function() {
				cacheService.getRemoteSessionCache("REGION", "PROVINCE000000",
						{
							PARENT : "000000"
						}).then(function(data) {
					$scope.provinces = data;
				});
			};
			// 初始化获取省
			$scope.getProvinces();
			$scope.citys = {};
			$scope.countys = {};
			$scope.$watch('order.CITY', function(county) {
				if (county != undefined) {
					$scope.countys = {};
					// 获取城市
					cacheService.getRemoteSessionCache("REGION",
							"COUNTY" + county, {
								PARENT : county
							}).then(function(data) {
						$scope.countys = data;
					});
				}
			});
			// 更换省的时候获取城市
			$scope.$watch('order.PROVINCE', function(province) {
				if (province != undefined) {
					$scope.citys = {};
					// 获取城市
					cacheService.getRemoteSessionCache("REGION",
							"CITY" + province, {
								PARENT : province
							}).then(function(data) {
						$scope.citys = data;
					});
				}
			});
			//上级分类
			$scope.parentCatalogs ={};
			// 获取上级分类列表 A为最上级大类
			$scope.getParentCatalogs = function(parentId) {
				cacheService.getRemoteSessionCache("CATALOG", "PARENTCATALOG"+parentId,
						{
							PARENT : parentId
						}).then(function(data) {
					$scope.parentCatalogs = data;
				});
			};
			// 初始化获取上级分类 A为最顶层分类
			$scope.getParentCatalogs("A");
			//二级分类
			$scope.catalogs ={};			
			//监控大类的变化
			$scope.$watch('orderDetail.PARENTCATALOGID', function(parentId) {
				if (parentId != undefined) {
					$scope.catalogids = {};
					// 获取城市
					cacheService.getRemoteSessionCache("CATALOG", "CATALOG"+parentId,{PARENT : parentId}).then(
							function(data) {
								$scope.catalogs = data;
					});
				}
			});
			//监控大类的变化
			$scope.$watch('orderDetail.CATALOGID', function(catalogId) {
				if (catalogId != undefined) {
					$scope.items = {};
					// 获取物品
					cacheService.getRemoteSessionCache("ITEM", "ITEM" + catalogId,{ITEMTYPE : catalogId}).then(
							function(data) {
								$scope.items = data;
								$scope.itemspecs = data;
					});
				}
			});	
			//监控规格的变化
			$scope.$watch('orderDetail.ITEMSPEC', function(itemspec) {
				if (itemspec != undefined) {
					$scope.itemmodels = new Array();
					for(var i=0;i<$scope.items.length;i++){
						if(itemspec == $scope.items[i].ITEMSPEC){
							$scope.orderDetail.ITEMCODE = $scope.items[i].ITEMCODE;
							//修改ordergoodsname
							$scope.orderDetail.GOODSNAME = $scope.items[i].ITEMNAME+' ' 
							+ $scope.items[i].ITEMSPEC + ' ' + $scope.items[i].ITEMSPEC;
							$scope.itemmodels.push($scope.items[i]);
						}						
					}					
				}
			});
			//监控规格的变化
			$scope.$watch('orderDetail.ITEMMODEL', function(itemmodel) {
				if (itemmodel != undefined) {
					for(var i=0;i<$scope.items.length;i++){
						if($scope.orderDetail.ITEMSPEC == $scope.items[i].ITEMSPEC 
								&& itemmodel == $scope.items[i].ITEMMODEL){
							$scope.orderDetail.ITEMCODE = $scope.items[i].ITEMCODE;
							//修改ordergoodsname
							$scope.orderDetail.GOODSNAME = $scope.items[i].ITEMNAME+' ' 
							+ $scope.items[i].ITEMSPEC + ' ' + $scope.items[i].ITEMSPEC;
						}						
					}					
				}
			});	
			//物品列表
			$scope.items={};
			$scope.itemspecs = {};
			$scope.itemmodels={};
			$scope.initOrderDetail = function(){
				return {
						PID : "STAINLESS",
						USERID:$scope.order.USERID,//用户id
						REALNAME:$scope.order.REALNAME,//姓名
						CREATETIME:commonService.Timestamp(),
						FROMPLACE:$scope.order.ADDRESS,//发站
						COLLATEQUANTITY:"",//报单数量
						QUANTITY:"",//订单数量
						GOODSNAME:"",//品名
						TOPLACE:"",//到站
						CONTACT:$scope.order.REALNAME,//联系人
						TELEPHONE:$scope.order.TEL,//联系电话
						SUPPLYORDEMAND:"0",//购货单:0;销货单:1;
						WHOLESERVICE:"N",//委托?
						STATUS:"0",//0:未处理单据
						PRICE:"",//单价
						PAYFORINLINE:"N",//线上支付?
						RESPONDSTATUS:"0",//0:未应单
						COLLATEQUANTITY:"",//挂单数量
						PARENTCATALOGID:"",//上级分类
						CATALOGID:"",//分类
						CATALOGNAME:"",//分类名称
						ITEMCODE:"",//物品编码
						ITEMSPEC:"",//规格
						ITEMMODEL:"",//型号
						REMARK:"",//备注
						LINENO:0//行号
				};
			};
			//默认的第一个订单明细内容.
			$scope.orderDetail = $scope.initOrderDetail();
			// 订单详情
			$scope.orderDetails = new Array();
			$scope.lineno = 0;
			// 加入到集合
			$scope.addList = function() {										
				//加入到订单明细
				//首先查看lineno对应的内容有没有,如果有则覆盖 ,实现重新编辑的功能
				if($scope.orderDetail.LINENO != 0){
					for(var i = 0; i < $scope.orderDetails.length ; i++){
						if($scope.orderDetail.LINENO == $scope.orderDetails[i].LINENO){
							$scope.orderDetails[i] = $scope.orderDetail;
							break;
						}
					}					
				}else{
					//默认行号
					$scope.orderDetail.LINENO = $scope.lineno + 1;
					//修改行号
					$scope.lineno = $scope.lineno + 1;
					$scope.orderDetails.push($scope.orderDetail);
				}				
			};
			// 增加一项
			$scope.addnew = function() {
				if($scope.orderDetail.ITEMCODE!=''){
					$scope.addList();
				}				
				//重新初始化
				$scope.orderDetail = $scope.initOrderDetail();	
				$scope.items={};
				$scope.itemspecs = {};
				$scope.itemmodels={};			
			};
			// 重新编辑
			$scope.edit = function(rownum) {
				for(var i=0;i<$scope.orderDetails.length;i++){
					if(rownum == $scope.orderDetails[i].LINENO){
						$scope.orderDetail = $scope.orderDetails[i]; 
					}					 
				}				
			};
			// 删除
			$scope.del = function(rownum) {
				var details  = new Array();
				for(var i=0;i<$scope.orderDetails.length;i++){
					if(rownum != $scope.orderDetails[i].LINENO){
						details.push($scope.orderDetail);
					}
				}				
				$scope.orderDetails = details;
			};
			// 下单方法
			$scope.save = function() {
				if($scope.orderDetail.ITEMCODE!=''){
					$scope.addList();
				}				
				actService.save("ORDER",$scope.order).then(function(data1){
					if(data1.CODE == '0'){
						//获取服务端产生的id
						for(var i = 0 ; i < $scope.orderDetails.length ; i++){
							$scope.orderDetails[i].ORDERID = data1.DATA.RESULT[0].ORDERID;
							$scope.orderDetails[i].QUANTITY = $scope.orderDetails[i].COLLATEQUANTITY;
						}
						actService.saveList("ORDERDETAIL",$scope.orderDetails).then(function(data2){
							if(data2.CODE=='0'){
								$rootScope.$state.go("user.profile");
							}
						})						
					}
				});				
			};
			// 上传图片方法
			$scope.wxUploadImages = function() {
				// 获取签名
				weixinService.wxAct(constantService.HOST
						+ constantService.WXREQURL + constantService.PID,
						constantService.WXAPPID, constantService.HOST
								+ "stainless.html");
				// 5 图片接口
				// 5.1 拍照、本地选图
				$scope.images = {
					localId : [],
					serverId : []
				};
				wx.chooseImage({
					success : function(res) {
						$scope.images.localId = res.localIds;
						alert('已选择 ' + res.localIds.length + ' 张图片');
						var i = 0, length = images.localId.length;
						$scope.images.serverId = [];
						function upload() {
							wx.uploadImage({
								localId : images.localId[i],
								success : function(res) {
									i++;
									alert('已上传：' + i + '/' + length);
									images.serverId.push(res.serverId);
									if (i < length) {
										upload();
									}
								},
								fail : function(res) {
									alert(JSON.stringify(res));
								}
							});
						}
						upload();
					}
				});
			}

		} ]);
