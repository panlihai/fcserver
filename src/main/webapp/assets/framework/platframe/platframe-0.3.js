/*! zlwhPlatform v0.1 | (c) 2015  */
var pathName = window.document.location.pathname;
var CTX = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
var P_ID = 'SYSTEM';
var P_APP, P_MAPP;
var P_PAGENUM;
$(document).ready(function() { // 创建打开主页功能 系统产品
	createTopMenu();
});

function createTopMenu() {
	// 菜单清空
	$("#page_top_menu").html('');
	clearPageContent();
	// 重置菜单内容
	$.ajax({
		async : true, // 是否异步
		cache : false, // 是否使用缓存
		data : {},
		type : 'get',
		dataType : 'json', // 数据传输格式
		url : CTX + '/ajax/' + P_ID + '/TOP/SYSMENU/showMenus',
		success : function(data) { // 若Ajax处理成功后返回的信息
			// 设置标题
			createTopMenuContent(data.P_MENUS);
		},
		error : function(msg) { // 若Ajax处理失败后返回的信息
			alert("错误");
		}
	});
}
/**
 * 获取显示的菜单,默认选中第一个应用程序.
 * <li><a href="#" onClick="createListMenu('${ctx}','HOME')">首页</a></li>
 * 
 * @param
 * @author
 */
function createTopMenuContent(data) {
	var menuContent = '';
	var menuId = '';
	var pId = '';
	if (data.length == 0) {
		return;
	}
	for (var i = 0; i < data.length; i++) {
		var menu = data[i];
		if (i == 0) {
			// 第一个菜单默认打开
			menuId = menu.MENUID;
			pId = menu.PID;
		}
		menuContent += '<li>';
		// 普通菜单的处理
		if (menu.MENUTYPE == 'MENU') {
			menuContent += '<a href="#"' + ' onclick="createListMenu(\''
					+ menu.PID + '\',\'' + menu.MENUID + '\');"><span class="'
					+ menu.MENUICON + '" aria-hidden="true"></span> '
					+ menu.MENUNAME + '</a></li>';
		} else if (menu.MENUTYPE == 'DROPDOWN') {// 下拉菜单的处理
			menuContent += '<div class="dropdown">';
			menuContent += '<button class="btn btn-default dropdown-toggle" type="button" id="dropdown'
					+ menu.MENUID
					+ '" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">';
			menuContent += 'Dropdown<span class="caret"></span></button>';
			menuContent += '<ul class="dropdown-menu" aria-labelledby="dropdown'
					+ menu.MENUID + '">';
			// 下拉子菜单的处理
			menuContent += '<li><a href="#">Action</a></li>';
			menuContent += '</ul>';
			menuContent += '</div>';
		}
		menuContent += '</li>';
	}
	$('#page_top_menu').html(menuContent);
	// 默认选中第一个应用程序
	createListMenu(pId, menuId);
}
/**
 * 显示列表应用程序.
 * 
 */
function createListMenu(pId, menuId) {
	// 菜单清空
	$("#main-nav").html('');
	clearPageContent();
	// 
	$.ajax({
		async : true, // 是否异步
		cache : false, // 是否使用缓存
		data : {},
		type : 'get',
		dataType : 'json', // 数据传输格式
		url : CTX + '/ajax/' + pId + '/' + menuId + '/SYSMENU/showMenus',
		success : function(data) { // 若Ajax处理成功后返回的信息
			// 设置标题
			createListMenuContent(data.P_MENUS);
		},
		error : function(msg) { // 若Ajax处理失败后返回的信息
			alert("错误");
		}
	});
}
/**
 * 清空菜单及内容
 */
function clearPageContent() {
	// 设置标题
	$('#page_content_title').html('');
	// 显示列表按钮及表头
	$('#page_content_thead').html('');
	// 显示查询的列表
	$('#page_content_tbody').html('');
	// 分页内容生成
	$('#page_content_tfoot').html('');
}
/**
 * 获取显示的菜单,默认选中第一个应用程序.
 * 
 * @param
 * @author
 */
function createListMenuContent(data) {
	var menuContent = '';
	var firstMenuId = '';
	var firstAppId = '';// 第一个菜单默认打开的应用程序
	if (data.length == 0) {
		return;
	}
	// 内容清空
	clearPageContent();
	for (var i = 0; i < data.length; i++) {
		var obj = data[i];
		if(obj == undefined){
			continue;
		}
		//微信菜单不显示
		if(obj.WXMENU != undefined && obj.WXMENU == 'Y'){
			continue;
		}
		if (firstAppId.length == 0) {
			firstAppId = obj.APPID == undefined ? '' : obj.APPID;
			firstMenuId = obj.MENUID;
		}
		if (obj.MENUTYPE == 'MENU') {// 如果是菜单的时候
			var childContent = '';
			var chliddata = obj.P_CHILDMENUS;
			for (var j = 0; j < chliddata.length; j++) {// 重新循环
				
				var objChild = chliddata[j];
				if(!objChild){
					continue;
				}
				//微信菜单不显示
				if(objChild.WXMENU != undefined && objChild.WXMENU == 'Y'){
					continue;
				}
				// 第一个菜单的第一个子应用程序
				if (firstAppId.length == 0) {
					firstAppId = objChild.APPID;
				}
				childContent += '<li><a href="#"'
						+ ' onclick="clickListMenu(\'\',\'' + objChild.MENUID
						+ '\',\'\',\'\',1,\'\',\'listView\',\''
						+ objChild.APPID + '\');"><i class="'
						+ objChild.MENUICON + '" ></i>&nbsp;'
						+ objChild.MENUNAME + '</a></li>';
			}
			// 如果childContent 不为空 则说明有子应用需要显示
			if (childContent.length > 0) {
				menuContent += '<li><a href="#'
						+ obj.MENUID
						+ '"'
						+ ' data-toggle="collapse" class="nav-header collapsed"><i class="'
						+ obj.MENUICON
						+ '"  ></i> '
						+ obj.MENUNAME
						+ '<span class="pull-right glyphicon glyphicon-chevron-toggle"></span></a>';
				menuContent += '<ul id="' + obj.MENUID
						+ '" class="nav nav-list secondmenu collapse">'
						+ childContent + '</ul></li>';
			}
		} else {
			menuContent += '<li><a href="#"'
					+ ' onclick="clickListMenu(\'\',\'' + obj.MENUID
					+ '\',\'\',\'\',1,\'\',\'listView\',\''
					+ data[i].APPID + '\');"><i class="'
					+ data[i].MENUICON + '"  ></i>&nbsp;'
					+ data[i].MENUNAME + '</a></li>';
		}
	}
	$('#main-nav').html(menuContent);
	// 默认选中第一个应用程序
	clickListMenu('', firstMenuId, '', '', 1, '', 'listView', firstAppId);
}
/**
 * 全选及取消全选操作.
 */
function selectCheckbox(page, mainApp, mainAppId) {
	if ($('#' + page + 'allcheckid').prop('checked')) {
		$("input[name='ids']").each(function() {
			$(this).prop("checked", true);
		})
	} else {
		$("input[name='ids']").each(function() {
			$(this).prop("checked", false);
		})
	}
}

/**
 * 点击列表页面菜单
 */
function clickListMenu(page, menuId, mainApp, mainAppId, pageNumber,
		pageFilter, action, appId) {
	this.P_PAGENUM = pageNumber;
	// 生成第一级目录列表内容
	createList(page, menuId, mainApp, mainAppId, pageNumber, pageFilter,
			action, appId);
}
/**
 * 获取数据内容,同时获取按钮,标题,内容,分页信息.
 * 
 * @param page
 *            当前的动作是哪个页面 当为''时则表示为最上层的父级,否则为关联功能的ID
 * @param mainApp
 *            主应用的应用程序编码APPID
 * @param mainAppId
 *            主应用程序的ID
 * @param pageNumber
 *            分页
 * @action 执行的操作
 * @appCode 当前应用程序的ID
 */
function createList(page, menuId, mainApp, mainAppId, pageNumber, pageFilter,
		action, appId) {
	$
			.ajax({
				async : true, // 是否异步
				cache : false, // 是否使用缓存
				data : {
					'P_COUNT' : pageNumber - 1,
					'MAINAPP' : mainApp,
					'MAINAPPID' : mainAppId,
					'P_LISTFILTER' : pageFilter
				},
				type : 'get',
				dataType : 'json', // 数据传输格式
				url : CTX + '/ajax/' + P_ID + '/' + menuId + '/' + appId + '/'
						+ action,
				success : function(data) { // 若Ajax处理成功后返回的信息
					// APP
					P_APP = data.P_APP;
					P_PAGENUM = data.P_COUNT + 1;
					if (undefined == P_APP) {
						return;
					}
					// APPFIELDS
					P_APPFIELDS = data.P_APP.P_APPFIELDS;
					// 设置标题
					$('#' + page + 'page_content_title').html(
							data.P_APP.APPNAME);
					// 显示列表按钮及表头
					$('#' + page + 'page_content_thead').html(
							createSearchFields(page, menuId, mainApp,
									mainAppId, pageFilter, data)
									//+ createListBtns(page, menuId, mainApp,
									//		mainAppId, pageFilter, data)
									+ createListHead(page, menuId, mainApp,
											mainAppId, data));
					// 显示查询的列表
					$('#' + page + 'page_content_tbody').html(
							createListBody(page, menuId, mainApp, mainAppId,
									pageNumber, pageFilter, data.P_SIZE, data));
					// 绑定查询事件
					$('#' + page + menuId + mainAppId + 'QUERY')
							.click(
									function(e) {
										var searchValues = '';
										for (var j = 0; j < data.P_APP.P_APPFIELDS.length; j++) {
											var field = data.P_APP.P_APPFIELDS[j];
											if (field.ENABLESEARCH == 'Y') {
												var valueObj = document
														.getElementById(field.APPID
																+ '.'
																+ field.FIELDCODE);
												var value = '';
												if (undefined != valueObj) {
													value = valueObj.value;
													if (field.DBTYPE != 'STR') {
														// 区间的查询条件
														var endValue = document
																.getElementById(field.APPID
																		+ '.'
																		+ field.FIELDCODE
																		+ 'END');
														if (endValue != undefined) {
															value += '","'
																	+ field.FIELDCODE
																	+ 'END":"'
																	+ endValue.value;
														}
													}
												} else {
													var arrayObj = document
															.getElementsByName(field.APPID
																	+ '.'
																	+ field.FIELDCODE);
													if (undefined != arrayObj) {
														for (var i = 0; i < arrayObj.length; i++) {
															if (arrayObj[i].checked) {
																value += arrayObj[i].value
																		+ ',';
															}
														}
														if (value.length > 0) {
															value = value
																	.substr(
																			0,
																			value.length - 1);
														}
													}
												}
												searchValues += '"'
														+ field.FIELDCODE
														+ '":"' + value + '",';
											}
										}
										if (searchValues.length > 0) {
											searchValues = '{'
													+ searchValues
															.substr(
																	0,
																	searchValues.length - 1)
													+ '}';
										}
										// 执行查询
										createList(page, menuId, mainApp,
												mainAppId, pageNumber,
												searchValues, action, appId)
									});
					// 绑定重置事件
					$('#' + page + menuId + mainAppId + "RESET")
							.click(
									function(e) {
										for (var j = 0; j < data.P_APP.P_APPFIELDS.length; j++) {
											var field = data.P_APP.P_APPFIELDS[j];
											if (field.ENABLESEARCH == 'Y') {
												var valueObj = document
														.getElementById(field.APPID
																+ '.'
																+ field.FIELDCODE);
												if (undefined != valueObj) {
													valueObj.value = '';
													if (field.DBTYPE != 'STR') {
														// 区间的查询条件
														var endValueObj = document
																.getElementById(field.APPID
																		+ '.'
																		+ field.FIELDCODE
																		+ 'END');
														if (undefined != valueObj) {
															endValueObj.value = '';
														}
													}
												} else {
													// 获取内容
													var arrayObj = document
															.getElementsByName(field.APPID
																	+ '.'
																	+ field.FIELDCODE);
													if (undefined != arrayObj) {
														for (var i = 0; i < arrayObj.length; i++) {
															if (arrayObj[i].checked) {
																// 为不选中
																arrayObj[i].checked = false;
															}
														}
													}
												}
											}
										}

									});
					// 设置可选择的checkbox
					$("#" + page + "page_content_tbody td").click(function(e) {
						var $obj = $(e.target).parent().find("input");
						if (e.target != $obj[0]) {
							if ($obj.prop('checked')) {
								$obj.prop("checked", false);
							} else {
								$obj.prop("checked", true);
							}
						}
					});
					// 双击进入表单内容进行编辑
					$("#" + page + "page_content_tbody tr").dblclick(
							function(e) {
								var $obj = $(e.target).parent().find("input");
								listEdit(page, menuId, mainApp, mainAppId,
										pageFilter, P_APP.APPID, $obj
												.prop("id"));
							});
					// 分页内容生成
					$('#' + page + 'page_content_tfoot')
							.html(
									createListPaging(page, menuId, mainApp,
											mainAppId, data, action, appId,
											pageNumber, pageFilter));
				},
				error : function(msg) { // 若Ajax处理失败后返回的信息
					alert("错误");
				}
			});
}
/**
 * 生成查询条件
 * 
 * @param page
 * @param menuId
 * @param mainApp
 * @param mainAppId
 * @param data
 */
function createSearchFields(page, menuId, mainApp, mainAppId, pageFilter, data) {
	// 显示字段列表
	var contentFieldsResult = data.P_APP.P_APPFIELDS;
	var cols = 0;
	for (var i = 0; i < contentFieldsResult.length; i++) {
		if (contentFieldsResult[i].SHOWLIST == 'Y') {
			// 计算有几列
			cols++;
		}
	}
	var hsFilter = false;
	// 还原pageFilter的内容
	var filterValues = pageFilter.length == 0 ? '' : JSON.parse(pageFilter);
	// 头部的设置
	var content = '<tr><th colspan="'
			+ (cols + 2)
			+ '"><form class="form-horizontal bg-success" style="padding:10px;">';
	for (var i = 0; i < contentFieldsResult.length; i++) {
		var field = contentFieldsResult[i];
		if (field.ENABLESEARCH == 'Y') {
			content += '<div class="form-group" style="width:99%;padding-bottom:0px;margin:0px">';
			content += createLabel(field.APPID + '.' + field.FIELDCODE,
					field.FIELDNAME);
			content += ''
					+ createCardFieldInput(page, menuId, mainApp, mainAppId,
							data, field, filterValues, true);
			content += "</div>";
			hsFilter = true;
		}
	}// 在最后一列中加入查询按钮
	if (hsFilter) {
		content += '<div class="form-group" style="width:99%;margin-left:15px;margin-top:10px;margin-bottom:0px;">';
		content += '<label class="col-sm-2 control-label">&nbsp;</label>'
				+ '<div class="col-sm-10"><button type="button" id="'
				+ page
				+ menuId
				+ mainAppId
				+ 'QUERY" class="btn btn-default">'
				+ '<span class="glyphicon glyphicon-search" aria-hidden="true"></span> 查询</button>';
		content += '&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-default"id="'
				+ page
				+ menuId
				+ mainAppId
				+ 'RESET"  ><span class="glyphicon glyphicon-repeat" aria-hidden="true"></span> 重置</button></div></div>'
		content += '</form></th></tr>';
		return content;
	} else {
		return '';
	}

}
/**
 * 获取列表按钮
 * 
 * @param page
 *            当前的动作是哪个页面 当为''时则表示为最上层的父级,否则为关联功能的ID
 * @param mainApp
 *            主应用的应用程序编码APPID
 * @param mainAppId
 *            主应用程序的ID
 */
function createListBtns(page, menuId, mainApp, mainAppId, pageFilter, data) {
	// 显示字段列表
	var contentFieldsResult = data.P_APP.P_APPFIELDS;
	var cols = 0;// 记录列数
	for (var i = 0; i < contentFieldsResult.length; i++) {
		if (contentFieldsResult[i].SHOWLIST == 'Y') {
			// 计算有几列
			cols++;
		}
	}
	// btn获取内容
	var content = '<tr><th colspan="' + (cols + 2)
			+ '"><div class="btn-group" role="toolbar">';
	// 按钮列表
	var btnList = data.P_APP.P_APPBUTTONS;
	if (btnList == undefined || btnList.length == 0) {
		return;
	}

	for (var i = 0; i < btnList.length; i++) {
		var obj = btnList[i];
		if (obj.BTNTYPE != 'LIST') {
			continue;
		}
		content += '<button type="button" class="btn btn-default" id="'
				+ obj.BTNCODE + '" onClick="doAct(\'' + page + '\',\'' + menuId
				+ '\',\'' + mainApp + '\',\'' + mainAppId + '\',\'' + obj.APPID
				+ '\',\'' + obj.ACTCODE + '\',\'' + pageFilter
				+ '\');" ><span class="' + obj.BTNICON
				+ '" aria-hidden="true"></span> ';
		content += obj.BTNNAME + '</button>';

	}

	content += '</div></th></tr>';
	return content;
}
/**
 * 生成列表分页内容
 * 
 * @param page
 *            当前的动作是哪个页面 当为''时则表示为最上层的父级,否则为关联功能的ID
 */
function createListPaging(page, menuId, mainApp, mainAppId, data, action,
		appcode, pageNumber, pageFilter) {
	// 显示字段列表
	var contentFieldsResult = data.P_APP.P_APPFIELDS;
	var pageSize = data.P_SIZE;
	var totalCount = data.P_A_COUNT;
	// 当前页
	var curPage = pageNumber;
	var nextPage; // 下一页
	var previousPage; // 上一页
	// 总页数
	var totalPage = parseInt(totalCount / pageSize);
	totalPage = totalPage + (parseInt(totalCount % pageSize) == 0 ? 0 : 1);
	if (curPage > totalPage) {
		curPage = totalPage;
	}
	if (curPage < 1) {
		curPage = 1;
	}
	// 下一页
	nextPage = parseInt(curPage) + 1;
	// 上一页
	previousPage = parseInt(curPage) - 1;
	var pageText = '<tr><td style=" text-align:right;" colspan="'
			+ (contentFieldsResult.length + 2) + '">';
	pageText += '<nav class="navbar" style="min-height:34px;margin-bottom:-5px;margin-top:0px"><ul class="pagination" style="margin-bottom:0px;margin-top:0px">';
	var beginPage = curPage - 5, lastPage = curPage + 5;
	if (beginPage <= 0) {
		lastPage = lastPage - beginPage;
		beginPage = 1;
	}
	if (curPage > 1) {
		pageText += '<li><a href="#" onclick="createList(\''
				+ page
				+ '\',\''
				+ menuId
				+ '\',\''
				+ mainApp
				+ '\',\''
				+ mainAppId
				+ '\','
				+ (curPage - 1)
				+ ',\''
				+ pageFilter
				+ '\',\''
				+ action
				+ '\',\''
				+ appcode
				+ '\')" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';
	}
	if (lastPage >= totalPage) {
		lastPage = totalPage;
	}

	for (var i = beginPage; i <= lastPage; i++) {
		if (i == curPage) {
			pageText += '<li class="active"><a href="#">' + i + '</a></li>';
		} else {
			pageText += '<li><a href="#" onclick="createList(\'' + page
					+ '\',\'' + menuId + '\',\'' + mainApp + '\',\''
					+ mainAppId + '\',' + i + ',\'' + pageFilter + '\',\''
					+ action + '\',\'' + appcode + '\')">' + i + '</a></li>';
		}
	}
	if (curPage < totalPage) {
		pageText += '<li><a href="#" aria-label="Next" onclick="createList(\''
				+ page + '\',\'' + menuId + '\',\'' + mainApp + '\',\''
				+ mainAppId + '\',' + (curPage + 1) + ',\'' + pageFilter
				+ '\',\'' + action + '\',\'' + appcode
				+ '\')"><span aria-hidden="true">&raquo;</span></a></li>';
	}
	pageText += '</ul></nav></td></tr>';
	return pageText;
}
/**
 * 生成列表表头内容
 * 
 * @param page
 *            当前的动作是哪个页面 当为''时则表示为最上层的父级,否则为关联功能的ID
 * 
 */
function createListHead(page, menuId, mainApp, mainAppId, data) {
	var contentFieldsResult = data.P_APP.P_APPFIELDS;
	var page_content_header = '<tr class="bg-primary">';
	page_content_header += '<th><input type="checkbox" id="' + page
			+ 'allcheckid" width="20px" onClick="selectCheckbox(\'' + page
			+ '\',\'' + menuId + '\',\'' + mainApp + '\',\'' + mainAppId
			+ '\')"/></th>';
	page_content_header += '<th></th>';
	for (var i = 0; i < contentFieldsResult.length; i++) {
		if (contentFieldsResult[i].SHOWLIST == 'Y') {
			page_content_header += '<th>'
					+ contentFieldsResult[i].FIELDNAME + '</th>';
		}
	}
	page_content_header += '</tr>';
	return page_content_header;
}
/**
 * 生成列表内容
 * 
 * @param page
 *            当前的动作是哪个页面 当为''时则表示为最上层的父级,否则为关联功能的ID
 */
function createListBody(page, menuID, mainApp, mainAppId, pageNumber,
		pageFilter, pageSize, data) {
	var contentResult = data.P_LISTVALUE;
	var contentFieldsResult = data.P_APP.P_APPFIELDS;
	if (page != undefined && page == 'modal_') {
		this.P_MAPP = data.P_APP;
	} else {
		this.P_APP = data.P_APP;
	}
	var contentBody = '';
	for (var i = 0; i < contentResult.length; i++) {
		var result = contentResult[i];
		contentBody += '<tr>';
		contentBody += '<td><input name="ids" type="checkbox" id="'
				+ result["ID"] + '"/></td>';
		contentBody += '<td>' + (i + 1) + '</td>';
		for (var j = 0; j < contentFieldsResult.length; j++) {
			var field = contentFieldsResult[j];
			if (field.SHOWLIST == 'Y') {
				var value = result[field.FIELDCODE];
				if (value == undefined) {
					value = '';
				}
				switch (field.INPUTTYPE) {
				case 'date':
					if (value.length != 0) {
						value = getLocalDate(value);
					}
					break;
				case 'datetime':
					if (value.length != 0) {
						value = getLocalDateTime(value);
					}
					break;
				case 'radio':
				case 'checkbox':
					value = findDicNameByDicValue(data, field.DICCODE, value);
					break;
				default:
					if (undefined != field.LISTMAXLEN && field.LISTMAXLEN != 0
							&& value.length > field.LISTMAXLEN) {
						var value1 = value;
						if (field.DBTYPE == 'STR') {
							var reg = new RegExp("'", "g");
							value1 = value.replace(reg, '"');
						}
						value = "<abbr  style='border-bottom:0px' title='"
								+ value1 + "'>"
								+ value.substr(0, field.LISTMAXLEN)
								+ "...</abbr>";
					}
				}
				if (value.length == 0) {
					value = '-';
				}
				contentBody += '<td>' + value + '</td>';
			}
		}
		contentBody += '</tr>';
	}
	return contentBody;
}
/**
 * 根据数据字典名称及值获取中文名称 静态字典
 * 
 * @param dicId
 * @param dicValue
 * @returns
 */
function findDicNameByDicValue(data, dicId, dicValue) {
	var rtnVal = '';
	var dicDetail = findDicByFieldName(data, dicId).P_APPDICDETAILS;
	for (var i = 0; i < dicDetail.length; i++) {
		var dic = dicDetail[i];
		var dicValues = (dicValue + ',').split(",");
		for (var j = 0; j < dicValues.length; j++) {
			if (dicValues[j] == dic.DICVALUE) {
				rtnVal += dic.DICDESC + ',';
			}
		}
	}
	if (rtnVal.length > 0) {
		rtnVal = rtnVal.substr(0, rtnVal.length - 1);
	}
	return rtnVal;
}
/**
 * 内容单击事件处理
 * 
 * @param tar
 */
function listBodyTrOnClick(id) {
	// var checkbox = document.getElementById(id);
	$('#' + id).prop("checked", true);

}
/**
 * 内容双击事件处理
 * 
 * @param tar
 */
function listBodyTrOnDbClick(id) {
	$('#' + id).prop("checked", true);
}
/**
 * 执行按钮事件
 */
function doAct(page, menuId, mainApp, mainAppId, appcode, actcode, pageFilter) {
	switch (actcode) {
	case 'cardSave':
		cardSave(page, menuId, mainApp, mainAppId, 'cardSave', pageFilter);
		break;
	case 'listDelete':
		listDelete(page, menuId, mainApp, mainAppId, appcode, actcode,
				pageFilter);
		break;
	case 'cardBack':
		// 返回到列表页面
		clickListMenu(page, menuId, mainApp, mainAppId, this.P_PAGENUM,
				pageFilter, 'listView', appcode);
		break;
	case 'cardSaveBack':
		cardSave(page, menuId, mainApp, mainAppId, 'cardSaveBack', pageFilter);
		break;
	case 'cardSaveNew':
		cardSave(page, menuId, mainApp, mainAppId, 'cardSaveNew', pageFilter);
		break;
	case 'cardSaveCopy':
		cardSave(page, menuId, mainApp, mainAppId, 'cardSaveCopy', pageFilter);
		break;
	default:
		$
				.ajax({
					async : true, // 是否异步
					cache : false, // 是否使用缓存
					data : {
						'MAINAPP' : mainApp,
						'MAINAPPID' : mainAppId
					},
					type : 'get',
					dataType : 'json', // 数据传输格式
					url : CTX + '/ajax/' + P_ID + '/' + menuId + '/' + appcode
							+ '/' + actcode,
					success : function(data) { // 若Ajax处理成功后返回的信息
						// $('#page_content_title').html('');
						// 显示列表按钮及表头
						// $('#page_content_thead').html('');
						// 显示查询的列表

						switch (actcode) {
						case 'listAdd':
						case 'cardAdd':
							$('#' + page + 'page_content_tbody').html('');
							// 分页内容生成
							$('#' + page + 'page_content_tfoot').html('');
							listAdd(page, menuId, mainApp, mainAppId,
									pageFilter, data);
							break;
						}
					},
					error : function(msg) { // 若Ajax处理失败后返回的信息
						alert("正在开发中...");
					}
				});
	}
}

/**
 * 表单页面新增修改等
 */
function listAdd(page, menuId, mainApp, mainAppId, pageFilter, data) {
	// 设置标题
	$('#' + page + 'page_content_title').html(data.P_APP.APPNAME);
	// 生成表单按钮及表头
	$('#' + page + 'page_content_thead').html(
			createCardBtns(page, menuId, mainApp, mainAppId, data, pageFilter));
	// 生成表单主数据内容
	createCardBody(page, menuId, mainApp, mainAppId, data, "");
	// 生成表单主数据下方的内容
	$('#' + page + 'page_content_tfoot').html(
			createCardFoot(page, menuId, mainApp, mainAppId, data));
}

/**
 * 表单页面新增
 */
function listEdit(page, menuId, mainApp, mainAppId, mainFilter, appid, id) {
	$.ajax({
		async : true, // 是否异步
		cache : false, // 是否使用缓存
		data : {
			"ID" : id,
			"MAINAPP" : mainApp,
			"MAINAPPID" : mainAppId
		},
		type : 'get',
		dataType : 'json', // 数据传输格式
		url : CTX + '/ajax/' + P_ID + '/' + menuId + '/' + appid + '/listEdit',
		success : function(data) { // 若Ajax处理成功后返回的信息
			$('#' + page + 'page_content_tbody').html('');
			// 分页内容生成
			$('#' + page + 'page_content_tfoot').html('');
			// 显示内容
			listAdd(page, menuId, mainApp, mainAppId, mainFilter, data);
		},
		error : function(msg) { // 若Ajax处理失败后返回的信息
			alert("错误");
		}
	});
}
/**
 * 列表删除操作
 */
function listDelete(page, menuId, mainApp, mainAppId, appcode, actcode,
		pageFilter) {
	var id_array = new Array();
	$('input[name="ids"]:checked').each(function() {
		id_array.push($(this).attr('id'));// 向数组中添加元素
	});
	// 没有选中则返回。
	if (id_array.length == 0) {
		return;
	}
	var idstr = id_array.join('\',\'');// 将数组元素连接起来以构建一个字符串
	var content = '{\"' + this.P_APP.APPID + '\":{\"P_IDS\":\"\''
			+ idstr;
	content += '\'\"}}';
	if (id_array.length != 0) {
		$.ajax({
			async : true, // 是否异步
			cache : false, // 是否使用缓存
			data : {
				"P_JSON" : content,
				"MAINAPP" : mainApp,
				"MAINAPPID" : mainAppId
			},
			type : 'get',
			dataType : 'json', // 数据传输格式
			url : CTX + '/ajax/' + P_ID + '/' + menuId + '/'
					+ P_APP.APPID + '/listDelete/',
			success : function(data) { // 若Ajax处理成功后返回的信息
				$('#' + page + 'page_content_title').html('');
				// 显示列表按钮及表头
				$('#' + page + 'page_content_thead').html('');
				// 显示查询的列表
				$('#' + page + 'page_content_tbody').html('');
				// 分页内容生成
				$('#' + page + 'page_content_tfoot').html('');
				createList(page, menuId, mainApp, mainAppId, P_PAGENUM,
						pageFilter, 'listView', appcode)
			},
			error : function(msg) { // 若Ajax处理失败后返回的信息
				alert("正在开发中...");
			}
		});
	} else {
		alert(err + '不能为空!');
		// 加入智能控制
	}
	// 获取数据内容
}
/**
 * 新建后的保存操作
 * 
 * @param actType
 *            保存分别包含 保存返回，保存新建，保存复制，保存
 */
function cardSave(page, menuId, mainApp, mainAppId, actType, pageFilter) {
	var cansubmit = true;// 默认可以提交
	var err = '';
	var appfields = this.P_APP.P_APPFIELDS;
	// 内容数据
	var content = '{' + this.P_APP.APPID + ':{';
	// 
	for (var j = 0; j < appfields.length; j++) {
		var field = appfields[j];
		var valueObj = document.getElementById(field.APPID + '.'
				+ field.FIELDCODE);
		var value = '';
		if (undefined != valueObj) {
			value = valueObj.value;
		} else {
			var arrayObj = document.getElementsByName(field.APPID + '.'
					+ field.FIELDCODE);
			if (undefined != arrayObj) {
				for (var i = 0; i < arrayObj.length; i++) {
					if (arrayObj[i].checked) {
						value += arrayObj[i].value + ',';
					}
				}
				if (value.length > 0) {
					value = value.substr(0, value.length - 1);
				}
			}
		}
		// 字段不为空需要提示
		if (field.ISNULL == 'Y' && (value == undefined || value.length == 0)
				&& field.FIELDCODE != 'ID') {
			cansubmit = false;
			err += '<tr><td>' + field.FIELDNAME + '</td></tr>';
			continue;
		}
		content += '\"' + field.FIELDCODE + '\":\"' + value + '\",';
	}
	content += '}}';
	if (cansubmit) {
		$
				.ajax({
					async : true, // 是否异步
					cache : false, // 是否使用缓存
					data : {
						"P_JSON" : content,
						"MAINAPP" : mainApp,
						"MAINAPPID" : mainAppId
					},
					type : 'get',
					contentType : "application/x-www-form-urlencoded; charset=utf-8",
					dataType : 'json', // 数据传输格式
					url : CTX + '/ajax/' + P_ID + '/' + menuId + '/'
							+ P_APP.APPID + '/cardSave',
					success : function(data) { // 若Ajax处理成功后返回的信息
						$('#' + page + 'page_content_title').html('');
						// 显示列表按钮及表头
						$('#' + page + 'page_content_thead').html('');
						// 显示查询的列表
						$('#' + page + 'page_content_tbody').html('');
						// 分页内容生成
						$('#' + page + 'page_content_tfoot').html('');
						switch (actType) {
						case 'cardSave':// 直接保存
							listAdd(page, menuId, mainApp, mainAppId,
									pageFilter, data);
							break;
						case 'cardSaveBack':
							// 返回到列表页面
							clickListMenu(page, menuId, mainApp, mainAppId,
									P_PAGENUM, pageFilter, 'listView',
									P_APP.APPID);
							break;
						case 'cardSaveNew':// 删除当前返回的数据
							data.P_CARDVALUE = undefined;
							listAdd(page, menuId, mainApp, mainAppId,
									pageFilter, data);
							break;
						case 'cardSaveCopy':// 需要把ID值清空
							data.P_CARDVALUE.ID = '';// 需要把其它默认值也赋值为空
							// 把默认值清空
							for (var i = 0; i < data.P_APP.P_APPFIELDS.length; i++) {
								// 获取字段配置信息
								var field = data.P_APP.P_APPFIELDS[i];
								if (field.FIELDDEFAULT.length > 0
										|| field.AUTOCODE.length > 0) {
									data.P_CARDVALUE[field.FIELDCODE] = '';
								}
							}
							listAdd(page, menuId, mainApp, mainAppId,
									pageFilter, data);
							break;
						default: {
						}
						}
					},
					error : function(msg) { // 若Ajax处理失败后返回的信息
						alert("正在开发中...");
					}
				});
	} else {
		$('#modal_page_content_thead').html('');
		$('#modal_page_content_tfoot').html('');
		$('#modal_page_title').html('请补充下列信息后再保存.');
		$('#modal_page_content_tbody').html(err);
		$('#modal_page').modal({
			show : true
		});
		return;
	}
	// 获取数据内容
}
/**
 * 生成表单页面按钮
 * 
 * @param data
 */
function createCardBtns(page, menuId, mainApp, mainAppId, data, pageFilter) {
	// btn获取内容
	var content = '<tr><th><div class="btn-group" role="toolbar">';
	// 按钮列表
	var btnList = data.P_APP.P_APPBUTTONS;
	if (btnList == undefined || btnList.length == 0) {
		return;
	}
	for (var i = 0; i < btnList.length; i++) {
		var obj = btnList[i];
		if (obj.BTNTYPE != 'CARD') {
			continue;
		}
		if(obj.BTNCODE!='BTNCARDBACK'){
			continue;
		}
		content += '<button type="submit" class="btn-group btn btn-default" id="'
				+ obj.BTNCODE
				+ '" onClick="doAct(\''
				+ page
				+ '\',\''
				+ menuId
				+ '\',\''
				+ mainApp
				+ '\',\''
				+ mainAppId
				+ '\',\''
				+ obj.APPID
				+ '\',\''
				+ obj.ACTCODE
				+ '\',\''
				+ pageFilter
				+ '\');" ><span class="'
				+ obj.BTNICON
				+ '" aria-hidden="true"></span> ';
		content += obj.BTNNAME + '</button>';
	}
	content += '</div></th></tr>';
	return content;
}

/**
 * 生成表单页面主数据
 * 
 * @param data
 */
function createCardBody(page, menuId, mainApp, mainAppId, data, pageFilter) {
	if (data.P_APP.CARDPAGE == undefined
			|| data.P_APP.CARDPAGE.length == 0) {
		var value = data.P_CARDVALUE;
		var val;
		if (undefined != value) {
			val = value;
		} else {
			val = '';
		}
		// 自动产生页面内容并赋值
		$('#' + page + 'page_content_tbody').html(
				createFormByFieldsConfig(page, menuId, mainApp, mainAppId,
						data, data.P_APP.P_APPFIELDS, val));
		// 对关联功能产生列表内容事件
		$(function() {
			var name = '#' + data.P_APP.APPID + 'CARDTABS a';// 表单TAB
			$(name + '[data-toggle="tab"]').on(
					'shown.bs.tab',
					function(e) {
						// 获取已激活的标签页的名称
						var activeTabHref = $(e.target).attr("href");
						// 获取id内容 表单关联tab的id 命名规范 主表应用APPID+子应用程序ITEMAPP+CARDTAB
						// 去掉#及主表应用ID及CARDTAB得到ID
						var itemAppId = activeTabHref.substring(
								1 + data.P_APP.APPID.length,
								activeTabHref.length - 7);
						// 如果是点击的是本身的tab页则不处理
						if (itemAppId.length != 0
								&& itemAppId != '#' + data.P_APP.APPID
										+ 'CARDTAB') {
							// 从服务器获取内容
							createList(data.P_APP.APPID + itemAppId
									+ 'child', menuId, data.P_APP.APPID,
									val.ID, 1, pageFilter, 'listView',
									itemAppId);
						}
					});
		});
	} else {
		var page = CTX + '/assets/templates/' + data.P_APP.CARDPAGE;
		$("#" + page + "page_content_tbody").load(page,
				function(response, status, xhr) {
					$('#' + page + 'page_content_tbody').html(response);
				});
	}
}

/**
 * 生成表单页面foot内容
 * 
 * @param data
 */
function createCardFoot(page, menuId, mainApp, mainAppId, data) {
	var content = "<tr><td></td></tr>";
}
/**
 * 根据应用程序配置及字段配置,生成表单内容,并且把对象内容写入表单内容中.
 * 
 * @param app
 *            应用程序
 * @param fieldsList
 *            字段列表
 * @param values
 *            默认值
 */
function createFormByFieldsConfig(page, menuId, mainApp, mainAppId, data,
		fieldsList, values) {
	var content = "";
	// 主应用程序
	var mainApp = data.P_APP;
	content += '<tr><td><div>';
	// 如果有关联应用则出现tap页
	var itemAppArray = mainApp.P_APPLINKS;
	if (undefined != itemAppArray && itemAppArray.length > 0) {
		content += '<ul id="' + mainApp.APPID
				+ 'CARDTABS" class="nav nav-pills" role="tablist">';
		// 主表单标题,id为主应用名称
		content += '<li role="presentation" class="active">';
		content += '<a href="#' + mainApp.APPID + 'CARDTAB" aria-controls="'
				+ mainApp.APPID + 'CARDTAB" role="tab" data-toggle="tab">';
		content += mainApp.APPNAME + '详情</a></li>';
		// 循环各个子应用程序

		// 循环子应用程序,生成tab头内容
		for (var i = 0; i < itemAppArray.length; i++) {
			var itemApp = itemAppArray[i];
			content += '<li role="presentation">';
			content += '<a href="#' + mainApp.APPID + itemApp.ITEMAPP
					+ 'CARDTAB" aria-controls="' + mainApp.APPID
					+ itemApp.ITEMAPP
					+ 'CARDTAB" role="tab" data-toggle="tab">';
			content += itemApp.LINKNAME + '</a></li>';
		}
		// 子应用循环结束.
		content += '</ul>';
		// TAP容器
		content += '<div class="tab-content">';
		// 主表单内容
		content += '<div role="tabpanel" class="tab-pane fade in active" id="'
				+ mainApp.APPID + 'CARDTAB">';
		// 主表单内容
		content += createCardFormBy(page, menuId, mainApp, mainAppId, data,
				fieldsList, values);
		content += '</div>';
		// 循环子应用程序,生成tab头内容
		for (var i = 0; i < itemAppArray.length; i++) {
			var itemApp = itemAppArray[i];
			content += '<div role="tabpanel" class="tab-pane fade" id="'
					+ mainApp.APPID + itemApp.ITEMAPP + 'CARDTAB">';
			// 模板 分别制定table的头部 主体 尾部
			content += '<div class="table-responsive">';
			content += '<table id="' + mainApp.APPID + itemApp.ITEMAPP
					+ 'childpage_content" style="margin-bottom: 0px"';
			content += 'class="table table-striped table-hover table-condensed">';
			content += '<thead id="' + mainApp.APPID + itemApp.ITEMAPP
					+ 'childpage_content_thead"></thead>';
			content += '<tbody id="' + mainApp.APPID + itemApp.ITEMAPP
					+ 'childpage_content_tbody"></tbody>';
			content += '<tfoot id="' + mainApp.APPID + itemApp.ITEMAPP
					+ 'childpage_content_tfoot"></tfoot>';
			content += '</table></div>';
			// 结尾
			content += '</div>';
		}
		content += '</div>';
	} else {
		// 如果没有关联应用则显示一个
		content += createCardFormBy(page, menuId, mainApp, mainAppId, data,
				fieldsList, values);
	}
	content += '</div></td></tr>';

	// alert(content);
	return content;
}
/**
 * 根据
 * 
 * @param data
 * @param fieldsList
 * @param values
 * @returns {String}
 */
function createCardFormBy(page, menuId, mainApp, mainAppId, data, fieldsList,
		values) {
	var content = "";
	var hcontent = "";
	content += '<form class="form-horizontal bg-success" style="padding:15px">';
	var i = 0;
	while (i < fieldsList.length) {
		if (fieldsList[i].SHOWCARD == 'Y') {
			content += '<div class="form-group" style="width:99%;margin-left:15px">';
			content += createLabel(fieldsList[i].APPID + '.'
					+ fieldsList[i].FIELDCODE,
					fieldsList[i].FIELDNAME);
			content += ''
					+ createCardFieldInput(page, menuId, mainApp, mainAppId,
							data, fieldsList[i], values, false);
			content += "</div>";
		} else {
			if (values != undefined
					&& values[fieldsList[i].FIELDCODE] != undefined) {
				hcontent += '<input id="' + fieldsList[i].APPID + '.'
						+ fieldsList[i].FIELDCODE
						+ '" type="hidden" value="'
						+ values[fieldsList[i].FIELDCODE] + '" />';
			} else {
				hcontent += '<input id="' + fieldsList[i].APPID + '.'
						+ fieldsList[i].FIELDCODE
						+ '" type="hidden" value="" />';
			}
		}
		i++;
	}
	content += hcontent + '</form>';
	return content;
}
/**
 * 生成输入框
 * 
 * @param field
 * @param value
 */
function createLabel(fieldCode, fieldName) {
	var content = '';
	content += '<label class="col-sm-2 control-label" for="' + fieldCode + '">'
			+ fieldName + '</label>';
	return content;
}
/**
 * 获取数据字典内容列表
 */
function findListValueByName(diccode) {
	var d = '';
	$.ajax({
		async : true, // 是否异步
		cache : false, // 是否使用缓存
		data : {},
		type : 'get',
		dataType : 'json', // 数据传输格式
		url : CTX + '/ajax/' + P_ID + '/' + diccode + '/listJsonValue',
		success : function(data) { // 若Ajax处理成功后返回的信息
			d = data;
		},
		error : function(msg) { // 若Ajax处理失败后返回的信息
			d = "";
		}
	});
	return d;
}

/**
 * 获取数据字典内容列表
 */
function findDicByFieldName(data, dicId) {
	// 应用程序中获取数据字典列表
	var dicList = data.P_APP.P_APPDICS;
	for (var i = 0; i < dicList.length; i++) {
		var dic = dicList[i];
		if (dic.DICID == dicId) {
			return dic;// 获取静态字典
		}
	}
}
/**
 * 单选框的获取
 * 
 * @param data
 * @param field
 * @param value
 * @returns
 */
function createFieldRadio(data, field, value) {
	if (field.DICID != undefined || field.DICID != '') {
		var content = '<div class="col-sm-10" ><div class="row" style="margin-top:5px">';
		// 根据字段名称获取数据字典
		var vlist = findDicByFieldName(data, field.DICCODE).P_APPDICDETAILS;
		for (var i = 0; i < vlist.length; i++) {
			var dic = vlist[i];
			content += '<label  class="col-xs-5 col-md-3 col-lg-2"><input type="radio" id="'
					+ field.APPID
					+ '.'
					+ field.FIELDCODE
					+ '.'
					+ field.DICCODE
					+ '.'
					+ dic.DICVALUE
					+ '" name="'
					+ field.APPID
					+ '.'
					+ field.FIELDCODE + '" value="' + dic.DICVALUE + '"';
			if (value[field.FIELDCODE] != undefined
					&& value[field.FIELDCODE] == dic.DICVALUE) {
				content += ' checked ';
			}
			content += ' />  ' + dic.DICDESC + '  </label>';
		}
	}
	return content + "</div></div>";

}
/**
 * 单选框的获取
 * 
 * @param data
 * @param field
 * @param value
 * @returns
 */
function createFieldCheckbox(data, field, value) {
	if (field.DICID != undefined || field.DICID != '') {
		var content = '<div class="col-sm-10" ><div class="row" style="margin-top:5px">';
		// 根据字段名称获取数据字典
		var vlist = findDicByFieldName(data, field.DICCODE).P_APPDICDETAILS;
		for (var i = 0; i < vlist.length; i++) {
			var dic = vlist[i];
			content += '<label class="col-xs-5 col-md-3 col-lg-2"><input type="checkbox" id="'
					+ field.APPID
					+ '.'
					+ field.FIELDCODE
					+ '.'
					+ field.DICCODE
					+ '.'
					+ dic.DICVALUE
					+ '" name="'
					+ field.APPID
					+ '.'
					+ field.FIELDCODE + '" value="' + dic.DICVALUE + '"';
			if (value[field.FIELDCODE] != undefined
					&& value[field.FIELDCODE].indexOf(dic.DICVALUE) != -1) {
				content += ' checked ';
			}
			content += ' />  ' + dic.DICDESC + '  </label>';
		}
	}
	return content + "</div></div>";

}
/**
 * 设置文本框输入方式及内容
 * 
 * @param field
 * @param value
 */
function createFieldText(data, field, value, issearch) {
	var placeholder = field.PLACEHOLDER == undefined ? '' : field.PLACEHOLDER;
	if (issearch != undefined && issearch && field.DBTYPE == 'NUM') {
		// 如果是查询 则
		var content = '<div class="col-sm-10" ><div class="row" style="margin-top:5px"><div class="input-group" style="width:50%">';
		content += '<input id="'
				+ field.APPID
				+ '.'
				+ field.FIELDCODE
				+ '" type="'
				+ field.INPUTTYPE
				+ '" placeholder="'
				+ placeholder
				+ '"  class="form-control col-xs-1 col-md-2 col-lg-4" style="margin-left:15px" ';
		if (value != undefined && value[field.FIELDCODE] != undefined) {
			content += ' value="' + value[field.FIELDCODE] + '" ';
		}
		content += '/>';
		content += '<span class="input-group-addon" style="padding-left:25px">至</span><input id="'
				+ field.APPID
				+ '.'
				+ field.FIELDCODE
				+ 'END" type="'
				+ field.INPUTTYPE
				+ '" placeholder="'
				+ placeholder
				+ '"  class="form-control col-xs-1 col-md-2 col-lg-4" style="margin-left:0px;"';
		if (value != undefined && value[field.FIELDCODE + 'END'] != undefined) {
			content += ' value="' + value[field.FIELDCODE + 'END'] + '" ';
		}
		content += '/>';
		content += '</div></div></div>';
		return content;
	} else {
		var content = '<div class="col-sm-10">';
		content += '<input id="' + field.APPID + '.' + field.FIELDCODE
				+ '" type="' + field.INPUTTYPE + '" placeholder="'
				+ placeholder + '" class="form-control" style="' + field.STYLE
				+ '" tabindex="' + field.SORT + '"';
		// 输入不为空的处理
		if (field.ISNULL != 'N') {
			content += ' required="true" ';
		}
		if (value != undefined && value[field.FIELDCODE] != undefined) {
			content += ' value="' + value[field.FIELDCODE] + '" ';
		}
		content += '/></div>';
		return content;
	}

}
/**
 * 设置文本框输入方式及内容
 * 
 * @param field
 * @param value
 */
function createFieldTextArea(data, field, value) {
	var placeholder = field.PLACEHOLDER == undefined ? '' : field.PLACEHOLDER;
	var content = '<div class="col-sm-10"><textarea id="' + field.APPID + '.'
			+ field.FIELDCODE + '" rows="' + field.ROWSPAN + '" placeholder="'
			+ placeholder + '" class="form-control" style="' + field.STYLE
			+ '" tabindex="' + field.SORT + '"';
	// 输入不为空的处理
	if (field.ISNULL != 'N') {
		content += ' required="true"';
	}
	content += '>'
	if (value != undefined && value[field.FIELDCODE] != undefined) {
		content += value[field.FIELDCODE];
	}
	content += '</textarea></div>';
	return content;
}
/**
 * 设置文本框输入方式及内容
 * 
 * @param field
 * @param value
 * @param issearch
 *            是否是查询
 */
function createFieldDateTimeBox(data, field, value, issearch) {
	var placeholder = field.PLACEHOLDER == undefined ? '' : field.PLACEHOLDER;
	if (issearch != undefined && issearch) {
		// 如果是查询 则
		var content = '<div class="col-sm-10" ><div class="row" style="margin-top:5px"><div class="input-group" style="width:50%">';
		content += '<input id="'
				+ field.APPID
				+ '.'
				+ field.FIELDCODE
				+ '" type="'
				+ field.INPUTTYPE
				+ '" placeholder="'
				+ placeholder
				+ '"  class="form-control col-xs-1 col-md-2 col-lg-4" style="margin-left:15px" ';
		if (value != undefined && value[field.FIELDCODE] != undefined) {
			content += ' value="' + value[field.FIELDCODE] + '" ';
		}
		content += ' onclick="laydate({istime: true, format: \'YYYY-MM-DD hh:mm:ss\'})"/>';
		content += '<span class="input-group-addon" style="padding-left:25px">至</span><input id="'
				+ field.APPID
				+ '.'
				+ field.FIELDCODE
				+ 'END" type="'
				+ field.INPUTTYPE
				+ '" placeholder="'
				+ placeholder
				+ '"  class="form-control col-xs-1 col-md-2 col-lg-4" style="margin-left:0px;"';
		if (value != undefined && value[field.FIELDCODE + 'END'] != undefined) {
			content += ' value="' + value[field.FIELDCODE + 'END'] + '" ';
		}
		content += ' onclick="laydate({istime: true, format: \'YYYY-MM-DD hh:mm:ss\'})"/>';
		content += '</div></div></div>';
		return content;
	} else {
		var content = '<div class="col-sm-10"><input id="' + field.APPID + '.'
				+ field.FIELDCODE + '" type="' + field.INPUTTYPE
				+ '" placeholder="' + placeholder
				+ '" class="form-control" style="' + field.STYLE
				+ '" tabindex="' + field.SORT + '"';
		// 输入不为空的处理
		if (field.ISNULL != 'N') {
			content += ' required="true" ';
		}
		if (value != undefined && value[field.FIELDCODE] != undefined) {
			content += ' value="' + value[field.FIELDCODE] + '" ';
		}
		content += ' onclick="laydate({istime: true, format: \'YYYY-MM-DD hh:mm:ss\'})"/>';
		content += '</div>';
		return content;
	}

}
/**
 * 获取日期数据
 * 
 * @param nS
 * @returns {String}
 */
function getLocalDate(nS) {
	var date = new Date(parseInt(nS*1000));
	var month = date.getMonth() + 1;
	var day = date.getDate();
	return date.getFullYear() + "-" + (month > 9 ? '' : '0') + month + "-"
			+ (day > 9 ? '' : '0') + day;
}
/**
 * 获取时间数据
 * 
 * @param nS
 * @returns {String}
 */
function getLocalDateTime(nS) {
	var date = new Date(parseInt(nS*1000));
	var month = date.getMonth() + 1;
	var day = date.getDate();
	var hh = date.getHours();
	var mi = date.getMinutes();
	var se = date.getSeconds();
	return date.getFullYear() + "-" + (month > 9 ? '' : '0') + month + "-"
			+ (day > 9 ? '' : '0') + day + " " + (hh > 9 ? '' : '0') + hh + ":"
			+ (mi > 9 ? '' : '0') + mi + ":" + (se > 9 ? '' : '0') + se;
}
/**
 * 设置文本框输入方式及内容
 * 
 * @param field
 * @param value
 */
function createFieldDateBox(data, field, value, issearch) {
	var placeholder = field.PLACEHOLDER == undefined ? '' : field.PLACEHOLDER;
	if (issearch != undefined && issearch) {
		// 如果是查询 则
		var content = '<div class="col-sm-10" ><div class="row" style="margin-top:5px"><div class="input-group" style="width:50%">';
		content += '<input id="'
				+ field.APPID
				+ '.'
				+ field.FIELDCODE
				+ '" type="'
				+ field.INPUTTYPE
				+ '" placeholder="'
				+ placeholder
				+ '"  class="form-control col-xs-1 col-md-2 col-lg-4" style="margin-left:15px" ';
		if (value != undefined && value[field.FIELDCODE] != undefined) {
			content += ' value="' + value[field.FIELDCODE] + '" ';
		}
		content += ' onclick="laydate({istime: true, format: \'YYYY-MM-DD hh:mm:ss\'})"/>';
		content += '<span class="input-group-addon" style="padding-left:25px">至</span><input id="'
				+ field.APPID
				+ '.'
				+ field.FIELDCODE
				+ 'END" type="'
				+ field.INPUTTYPE
				+ '" placeholder="'
				+ placeholder
				+ '"  class="form-control col-xs-1 col-md-2 col-lg-4" style="margin-left:0px;"';
		if (value != undefined && value[field.FIELDCODE + 'END'] != undefined) {
			content += ' value="' + value[field.FIELDCODE + 'END'] + '" ';
		}
		content += ' onclick="laydate()"/>';
		content += '</div></div></div>';
		return content;
	} else {
		var content = '<div class="col-sm-10"><input id="' + field.APPID + '.'
				+ field.FIELDCODE + '" type="' + field.INPUTTYPE
				+ '" placeholder="' + placeholder
				+ '" class="form-control" style="' + field.STYLE
				+ '" tabindex="' + field.SORT + '"';
		// 输入不为空的处理
		if (field.ISNULL != 'N') {
			content += ' required="true" ';
		}
		if (value != undefined && value[field.FIELDCODE] != undefined) {
			content += ' value="' + value[field.FIELDCODE] + '" ';
		}
		content += ' onclick="laydate()"/>';
		content += '</div>';
		return content;
	}
}
/**
 * 查询选择器,出现模态选择窗口.
 * 
 * @param page
 * @param mainApp
 * @param mainAppId
 * @param data
 * @param field
 * @param value
 * @returns {String}
 */
function createFieldQuerySelect(page, menuId, data, field, value) {
	var placeholder = field.PLACEHOLDER == undefined ? '' : field.PLACEHOLDER;
	var dicArray = findDicByFieldName(data, field.DICCODE).P_APPDICAPPS;// 动态字典
	var dicObj = dicArray.length > 0 ? dicArray[0] : '';
	// 处理字典赋值列表
	var fields = '';
	fields += dicObj.FIELDCODE + ':' + field.FIELDCODE + ";";// 主字段
	var dicAppDetails = dicObj.P_APPDICAPPDETAILS;
	for (var i = 0; i < dicAppDetails.length; i++) {
		var appDetail = dicAppDetails[i];
		if (appDetail.TOAPPID == field.APPID) {
			// 只有是本身的才需要赋值操作.
			fields += appDetail.FIELDCODE + ':' + appDetail.TOFIELDCODE + ";";
		}
	}
	var content = '<div class="col-sm-10"><input id="' + field.APPID + '.'
			+ field.FIELDCODE + '" type="text" placeholder="' + placeholder
			+ '" class="form-control" style="' + field.STYLE + '" tabindex="'
			+ field.SORT + '"';
	// 输入不为空的处理
	if (field.ISNULL != 'N') {
		content += ' required="true" ';
	}
	if (value != undefined && value[field.FIELDCODE] != undefined) {
		content += ' value="' + value[field.FIELDCODE] + '" ';
	}
	var filter = '';
	if (dicObj.APPFILTER != undefined && dicObj.APPFILTER.length != 0) {
		filter = toJsonFilter(dicObj.APPFILTER);
	}
	content += ' onclick="createListModal(\'modal_\',\'' + menuId + '\',\''
			+ field.APPID + '\',\'1\',\'listView\',\'' + dicObj.APPID + '\',\''
			+ filter + '\',\'' + fields + '\',\'' + value[field.FIELDCODE]
			+ '\')"/></div>';
	return content;
}
/**
 * 对字符' 及"进行转义
 * 
 * @param val
 */
function toJsonFilter(val) {
	if (val == undefined) {
		return '';
	}
	val = val.replace(/\"/g, ":”");
	val = val.replace(/\'/g, ":‘");
	return val;
}
/**
 * 对字符:‘及:“进行转义
 * 
 * @param val
 */
function fromJsonFilter(val) {
	if (val == undefined) {
		return '';
	}
	val = val.replace(":~~", "\"");
	val = val.replace(":~", "\'");
	return val;
}
/**
 * 获取数据内容,同时获取按钮,标题,内容,分页信息.
 * 
 * @param page
 *            当前的动作是哪个页面 当为''时则表示为最上层的父级,否则为关联功能的ID
 * @param mainApp
 *            主应用的应用程序编码APPID
 * @param mainAppId
 *            主应用程序的ID
 * @param pageNumber
 *            分页
 * @action 执行的操作
 * @appCode 当前应用程序的ID
 */
function createListModal(page, menuId, mainAppId, pageNumber, action, appCode,
		condition, fields, value) {
	$.ajax({
		async : true, // 是否异步
		cache : false, // 是否使用缓存
		data : {
			'P_COUNT' : pageNumber - 1,
			'P_APPFILTER' : condition
		},
		type : 'get',
		dataType : 'json', // 数据传输格式
		url : CTX + '/ajax/' + P_ID + '/' + menuId + '/' + appCode + '/'
				+ action,
		success : function(data) { // 若Ajax处理成功后返回的信息
			// APP
			P_MAPP = data.P_APP;
			P_MPAGENUM = data.P_COUNT + 1;
			if (undefined == P_MAPP) {
				return;
			}
			// APPFIELDS
			P_APPFIELDS = data.P_APP.P_APPFIELDS;
			// 设置标题
			$('#' + page + 'page_content_title').html(
					'请选择' + data.P_APP.APPNAME);
			// 显示列表按钮及表头
			$('#' + page + 'page_content_thead').html(
			// createListBtns(page, mainApp, mainAppId, data)+
			createListHead(page, menuId, undefined, undefined, data));
			// 显示查询的列表
			$('#' + page + 'page_content_tbody').html(
					createListBody(page, menuId, undefined, undefined,
							pageNumber, condition, data.P_SIZE, data));
			// 设置可选择的checkbox
			$("#" + page + "page_content_tbody td").click(function(e) {
				var $obj = $(e.target).parent().find("input");
				if (e.target != $obj[0]) {
					if ($obj.prop('checked')) {
						$obj.prop("checked", false);
					} else {
						$obj.prop("checked", true);
					}
				}
			});
			// 双击中后关闭
			$("#" + page + "page_content_tbody tr").dblclick(
					function(e) {
						var $obj = $(e.target).parent().find("input");
						onSelectedFromListModal(page, menuId, mainAppId,
								fields, value, data, $obj.prop("id"));
					});
			// 单击确定后关闭
			$("#" + page + "page_select").click(
					function(e) {
						var id_array = new Array();
						$('input[name="ids"]:checked').each(function() {
							id_array.push($(this).attr('id'));// 向数组中添加元素
						});
						// 没有选中则返回。
						if (id_array.length == 0) {
							$("#" + page + "page_content_msg")
									.html('没有选中任何选项!');
							setTimeout(function() {
								$("#" + page + "page_content_msg").html('');
							}, 2000);
							return;
						} else if (id_array.length > 1) {
//							$("#" + page + "page_content_msg")
//									.html('只能选择一个选项!');
//							setTimeout(function() {
//								$("#" + page + "page_content_msg").html('');
//							}, 2000);
							onSelectedMulFromListModal(page, menuId, mainAppId,
									fields, value, data, id_array[0]);
						} else {
							// 只能选择单个
							onSelectedFromListModal(page, menuId, mainAppId,
									fields, value, data, id_array[0]);
						}
					});

			// 分页内容生成
			$('#' + page + 'page_content_tfoot')
					.html(
							createListModalPaging(page, menuId, mainAppId,
									fields, value, data, action, appCode,
									pageNumber, condition));
			$('#' + page + 'page').modal({
				show : true
			});
		},
		error : function(msg) { // 若Ajax处理失败后返回的信息
			alert("错误");
		}
	});
}

/**
 * 生成列表分页内容
 * 
 * @param page
 *            当前的动作是哪个页面 当为''时则表示为最上层的父级,否则为关联功能的ID
 */
function createListModalPaging(page, menuId, mainAppid, fields, value, data,
		action, appcode, pageNumber, condition) {
	// 显示字段列表
	var contentFieldsResult = data.P_APP.P_APPFIELDS;
	var pageSize = data.P_SIZE;
	var totalCount = data.P_A_COUNT;
	// 当前页
	var curPage = pageNumber;
	var nextPage; // 下一页
	var previousPage; // 上一页
	// 总页数
	var totalPage = parseInt(totalCount / pageSize);
	totalPage = totalPage + (parseInt(totalCount % pageSize) == 0 ? 0 : 1);
	if (curPage > totalPage) {
		curPage = totalPage;
	}
	if (curPage < 1) {
		curPage = 1;
	}
	// 下一页
	nextPage = parseInt(curPage) + 1;
	// 上一页
	previousPage = parseInt(curPage) - 1;
	var pageText = '<tr><td style=" text-align:right;" colspan="'
			+ (contentFieldsResult.length + 2) + '">';
	pageText += '<nav class="navbar" style="min-height:34px;margin-bottom:-5px;margin-top:0px"><ul class="pagination" style="margin-bottom:0px;margin-top:0px">';
	var beginPage = curPage - 5, lastPage = curPage + 5;
	if (beginPage <= 0) {
		lastPage = lastPage - beginPage;
		beginPage = 1;
	}
	// createListModal(page,mainAppId,pageNumber, action, appCode,fields, value)
	if (curPage > 1) {
		pageText += '<li><a href="#" onclick="createListModal(\''
				+ page
				+ '\',\''
				+ menuId
				+ '\',\''
				+ mainAppid
				+ '\','
				+ (curPage - 1)
				+ ',\''
				+ action
				+ '\',\''
				+ appcode
				+ '\',\''
				+ condition
				+ '\',\''
				+ fields
				+ '\',\''
				+ value
				+ '\')" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';
	}
	if (lastPage >= totalPage) {
		lastPage = totalPage;
	}

	for (var i = beginPage; i <= lastPage; i++) {
		if (i == curPage) {
			pageText += '<li class="active"><a href="#">' + i + '</a></li>';
		} else {
			pageText += '<li><a href="#" onclick="createListModal(\'' + page
					+ '\',\'' + menuId + '\',\'' + mainAppid + '\',' + i
					+ ',\'' + action + '\',\'' + appcode + '\',\'' + condition
					+ '\',\'' + fields + '\',\'' + value + '\')">' + i
					+ '</a></li>';
		}
	}
	if (curPage < totalPage) {
		pageText += '<li><a href="#" aria-label="Next" onclick="createListModal(\''
				+ page
				+ '\',\''
				+ menuId
				+ '\',\''
				+ mainAppid
				+ '\','
				+ '\','
				+ (curPage + 1)
				+ ',\''
				+ action
				+ '\',\''
				+ appcode
				+ '\',\''
				+ condition
				+ '\',\''
				+ fields
				+ '\',\''
				+ value
				+ '\')"><span aria-hidden="true">&raquo;</span></a></li>';
	}
	pageText += '</ul></nav></td></tr>';
	return pageText;
}
/**
 * 选中后的赋值操作.
 * 
 * @param mainAppId
 * @param id
 * @param appid
 */
function onSelectedMulFromListModal(page, menuId, mainAppId, fields, value, data,
		ids) {
	var valueObjs = data.P_LISTVALUE;
	for (var i = 0; i < valueObjs.length; i++) {
		var valueObj = valueObjs[i];
		if (id == valueObj.ID) {
			var fieldStr = fields.split(';');
			for (var j = 0; j < fieldStr.length; j++) {
				if (fieldStr[j].length > 0) {
					var field = fieldStr[j].split(':');
					// 开始赋值
					document.getElementById(mainAppId + '.' + field[1]).value = valueObj[field[0]];
				}
			}
			break;
		}
	}
	$('#' + page + 'page').modal('hide');
}
/**
 * 选中后的赋值操作.
 * 
 * @param mainAppId
 * @param id
 * @param appid
 */
function onSelectedFromListModal(page, menuId, mainAppId, fields, value, data,
		id) {
	var valueObjs = data.P_LISTVALUE;
	for (var i = 0; i < valueObjs.length; i++) {
		var valueObj = valueObjs[i];
		if (id == valueObj.ID) {
			var fieldStr = fields.split(';');
			for (var j = 0; j < fieldStr.length; j++) {
				if (fieldStr[j].length > 0) {
					var field = fieldStr[j].split(':');
					// 开始赋值
					document.getElementById(mainAppId + '.' + field[1]).value = valueObj[field[0]];
				}
			}
			break;
		}
	}
	$('#' + page + 'page').modal('hide');
}
/**
 * 设置输入方式
 * 
 * @param field
 * @param value
 */
function createCardFieldInput(page, menuId, mainApp, mainAppId, data, field,
		value, issearch) {
	if (issearch) {
		switch (field.INPUTTYPE) {
		case 'text':
		case 'textarea':
			return createFieldText(data, field, value, issearch);
		case 'radio':
			return createFieldRadio(data, field, value);
		case 'checkbox':
			return createFieldCheckbox(data, field, value);
		case 'datetime':
			return createFieldDateTimeBox(data, field, value, issearch);
			break;
		case 'date':
			return createFieldDateBox(data, field, value, issearch);
		case 'queryselect':
			return createFieldQuerySelect(page, menuId, data, field, value);
			break;
		default:
			return createFieldText(data, field, value, issearch);
		}

	} else {
		switch (field.INPUTTYPE) {
		case 'text':
			return createFieldText(data, field, value);
		case 'textarea':
			return createFieldTextArea(data, field, value);
		case 'radio':
			return createFieldRadio(data, field, value);
		case 'checkbox':
			return createFieldCheckbox(data, field, value);
		case 'datetime':
			return createFieldDateTimeBox(data, field, value);
			break;
		case 'date':
			return createFieldDateBox(data, field, value);
		case 'queryselect':
			return createFieldQuerySelect(page, menuId, data, field, value);
			break;
		default:
			return createFieldText(data, field, value);
		}

	}

}