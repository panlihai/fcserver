package cn.pcorp.util;

import java.util.List;

import cn.pcorp.model.DynaBean;
import net.sf.json.JSONObject;

/** 
* @author panlihai E-mail:18611140788@163.com 
* @version 创建时间：2015年12月11日 上午11:40:22 
* 类说明: 
*/
/**
 * @author Administrator
 *
 */
public class PageUtil {
	/** 特殊属性：页面列表编码 */
	public static final String PAGE_LISTVALUE = "P_LISTVALUE";
	/** 特殊属性：页面列表过滤条件 */
	public static final String PAGE_LISTFILTER = "P_LISTFILTER";
	/** 特殊属性：页面应用程序列表过滤条件 */
	public static final String PAGE_APPFILTER = "P_APPFILTER";
	/** 特殊属性：操作编码 */
	public static final String PAGE_ACTION = "P_ACTION";
	/** 特殊属性：产品编码 */
	public static final String PAGE_PID = "P_PID";
	/** 特殊属性：产品菜单编码 */
	public static final String PAGE_MENUID = "P_MENUID";
	/** 特殊属性：APP编码 */
	public static final String PAGE_APPID = "P_APPID";
	/** 特殊属性：APP编码 */
	public static final String PAGE_SYSAPP = "P_APP";
	/** 特殊属性：APP字段结构写入页面 */
	public static final String PAGE_APPFIELDS = "P_APPFIELDS";
	/** 特殊属性：APP按钮结构写入页面 */
	public static final String PAGE_APPBUTTONS = "P_APPBUTTONS";
	/** 特殊属性：APP功能包含的数据字典 */
	public static final String PAGE_APPDICS = "P_APPDICS";
	/** 特殊属性：APP功能包含的静态数据字典 */
	public static final String PAGE_APPDICDETAILS = "P_APPDICDETAILS";
	/** 特殊属性：APP功能包含的动态数据字典 */
	public static final String PAGE_APPDICAPPS = "P_APPDICAPPS";
	/** 特殊属性：APP功能包含的静态数据字典 */
	public static final String PAGE_APPDICAPPDETAILS = "P_APPDICAPPDETAILS";
	/** 特殊属性：APP功能包含的关联功能 */
	public static final String PAGE_APPLINKS = "P_APPLINKS";
	/** 特殊属性：当前页数 */
	public static final String PAGE_COUNT = "P_COUNT";
	/** 特殊属性：页记录数 */
	public static final String PAGE_SIZE = "P_SIZE";
	/** 特殊属性：总记录数 */
	public static final String PAGE_ALLCOUNT = "P_A_COUNT";
	/** 特殊属性：页面菜单内容 */
	public static final String PAGE_MENUS = "P_MENUS";
	/** 特殊属性：页面子菜单内容 */
	public static final String PAGE_CHILDMENUS = "P_CHILDMENUS";
	/** 特殊属性：页面子内容 */
	public static final String PAGE_CHILD = "P_CHILD";
	/** 特殊属性：页面JSON名称 */
	public static final String PAGE_JSON = "P_JSON";
	/** 特殊属性：页面表单中对象内容名称 */
	public static final String PAGE_CARDVALUE = "P_CARDVALUE";
	/** 特殊属性：页面列表IDS */
	public static final String PAGE_IDS = "P_IDS";
	/** 特殊属性：页面状态 */
	public static final String PAGE_STATUS = "P_STATUS";
	/** 特殊属性：页面父级 */
	public static final String PAGE_PARENT = "P_PARENT";
	public static final String PID = "PID";

	/**
	 * 根据页面传输的消息进行处理,并把处理的sql条件返回.
	 * 
	 * @param appBean
	 * @param filterJson
	 * @return
	 */
	public static String getPageFilterSql(DynaBean appBean, String filterJson) {
		String sqlwhere = "";
		JSONObject json = JSONObject.fromObject(filterJson);
		@SuppressWarnings("unchecked")
		List<DynaBean> fieldsList = (List<DynaBean>) appBean.get(PAGE_APPFIELDS, "");
		for (DynaBean field : fieldsList) {
			// 没有查询的跳过
			if (!field.getStr("ENABLESEARCH").equals(SyConstant.STR_YES)) {
				continue;
			}
			String fieldCode = field.getStr("FIELDCODE", "");
			String fieldValue = json.optString(fieldCode);
			String fieldEndValue = json.optString(fieldCode + "END");
			// 不是字符串的类型则是有范围的查询条件
			if (fieldValue.length() != 0) {
				if (field.getStr("DBTYPE").equals("STR")) {
					// 如果是数据字典的则用等于
					if (field.getStr("DICCODE", "").length() != 0) {
						sqlwhere += " and " + fieldCode + " = '" + fieldValue + "'";
					} else {
						sqlwhere += " and " + fieldCode + " like '%" + fieldValue + "%'";
					}
				} else if (field.getStr("DBTYPE").equals("NUM")) {
					sqlwhere += " and " + fieldCode + " >= " + fieldValue;// 数字
				} else {
					sqlwhere += " and " + fieldCode + " >= '" + fieldValue + "'";// 日期
				}
			}
			if (fieldEndValue.length() != 0) {
				if (field.getStr("DBTYPE").equals("NUM")) {
					sqlwhere += " and " + fieldCode + " <= " + fieldValue;// 数字
				} else {
					sqlwhere += " and " + fieldCode + " <= '" + fieldValue + "'";// 日期
				}
			}
		}
		return sqlwhere;
	}

}
