package cn.pcorp.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.pcorp.model.DynaBean;

import com.alibaba.fastjson.JSONObject;


/**
 * 辅助自定义动态类，提供一些方便使用的扩展方法。
 * 
 */ 
public class BeanUtils {

	/** 特殊属性:自动编码为UUID */
	public static final String KEY_TABLEFIELDUUID = "UUID";
	/** 特殊属性:自动编码为UUID */
	public static final String KEY_TABLEFIELDYYYY = "yyyy";
	/** 特殊属性:自动编码为UUID */
	public static final String KEY_TABLEFIELDYYYYMM = "yyyyMM";
	/** 特殊属性:自动编码为UUID */
	public static final String KEY_TABLEFIELDYYYYMMDD = "yyyyMMdd";
	/** 特殊属性:自动编码为UUID */
	public static final String KEY_TABLEFIELDDATETIME = "yyyyMMddHHmmssSSS";
	public static final String KEY_TABLEFIELDDATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final String KEY_TABLEFIELDDATE = "yyyy-MM-dd";
	public static final String KEY_TABLEFIELDTIMESTAMP = "TIMESTAMP";
	/** 特殊属性：表编码 */
	public static final String KEY_TABLE_CODE = "TABLE_CODE";

	/** 特殊属性：结果集 */
	public static final String KEY_ROWSET = "ROWSET";

	/** 特殊属性：当前页数 */
	public static final String KEY_PAGE_COUNT = "P_COUNT";

	/** 特殊属性：页记录数 */
	public static final String KEY_PAGE_SIZE = "P_SIZE";

	/** 特殊属性：总记录数 */
	public static final String KEY_ALL_COUNT = "A_COUNT";

	/** 特殊属性：查询结果的列表 */
	public static final String KEY_COLUMNS = "A_COLUMENS";

	/** 特殊属性：本条数据的所在行数 */
	public static final String KEY_ROW_NUM = "ROW_NUM";

	/** 特殊属性：SQL语句 */
	public static final String KEY_SQL = "SQL";

	/** 特殊属性：select字段列 */
	public static final String KEY_SELECT = "SELECT";

	/** 特殊属性：Where条件 */
	public static final String KEY_WHERE = "WHERE";

	/** 特殊属性：group by */
	public static final String KEY_GROUP = "GROUP";

	/** 特殊属性: Order By */
	public static final String KEY_ORDER = "ORDER";

	/** 特殊属性：主键字符串 */
	public static final String KEY_PK_CODE = "PK_CODE";

	/** 特殊属性：大文本内容 */
	public static final String KEY_LONG_FIELD = "LONG";

	/** 特殊属性：二进制内容 */
	// public static final String KEY_BLOB_FIELD = "LONGBLOB";

	/** 特殊属性：操作用户名 */
	public static final String KEY_USER = "USER";

	/** 特殊属性：request对象 */
	public static final String KEY_REQUEST = "REQUEST";

	/** 特殊属性：response对象 */
	public static final String KEY_RESPONSE = "RESPONSE";

	/** 特殊属性：唯一组重复字段 */
	public static final String KEY_DUPCODE = "DUPCODE";

	/** 参数属性：是否进行数据库匹配 */
	public static final String PARAM_IS_DBMAP = "isDBMap";

	/** 表定义的参数：字段列表 */
	public static final String DEF_ALL_FIELDS = "ALL_FIELDS";

	/** 表定义的参数：BLOB字段列表 */
	public static final String DEF_BLOB_FIELDS = "BLOB_FIELDS";

	/** 表定义的参数：主键列表 */
	public static final String DEF_PK_FIELDS = "PK_FIELDS";

	/** 表定义的参数：非主键列表 */
	public static final String DEF_NPK_FIELDS = "NPK_FIELDS";

	/** 特殊属性：空字符串，在Where生成时使用自动替换为空 */
	public static final String KEY_VALUE_NULL = "~￥~==~￥￥~";

	/** 特殊属性：数字零，在Where生成时使用自动替换为数字零 */
	public static final String KEY_VALUE_ZERO = "~￥￥~==~￥~";

	/** 特殊属性：是否在流程中，当前数据Bean是否在流程处理中执行 */
	public static final String KEY_IS_IN_WORKFLOW = "IS_IN_WORKFLOW";

	/** 日志记录 */
	private static Log logger = LogFactory.getLog(BeanUtils.class);
	/**
	 * 获取根据应用程序对应的字段列表 主键
	 * @param appBean
	 * @return
	 */
	public static String getKeyseqFromApp(DynaBean appBean){
		String key ="ID";
		List<Map> fieldMapList = (List<Map>)appBean.get(PageUtil.PAGE_APPFIELDS);
		for(Map map:fieldMapList){
			DynaBean dynaBean = new DynaBean("");
			dynaBean.setValues(map);
			if(dynaBean.getStr("KEYSEQ","N").equals("Y")){
				key=dynaBean.getStr("FIELDCODE");
				break;
			}
		}
		return key;
	}
	/**
	 * 
	 * @param mainApp
	 * @param itemAppId
	 * @param mainId
	 * @return
	 */
	public static String getSqlWhereBy(final Map parentData,final String itemAppId,final DynaBean itemAppBean,String filter) {
		StringBuffer sqlSb = new StringBuffer(" ");
		// 解析条件
		String[] where = filter.split(":\\{");
		for (String str : where) {
			if (str.indexOf("}") == -1) {
				sqlSb.append(str.replaceAll(itemAppId + "\\.", ""));
			} else {
				String[] str2 = str.split("\\}");
				if(str2.length==2){
					sqlSb.append(parentData.get(str2[0].toUpperCase()).toString()).append(str2[1]);
				}else{
					sqlSb.append(parentData.get(str2[0].toUpperCase()).toString());
				}
			}
		}
		if (itemAppBean != null && itemAppBean.getStr("APPFILTER", "").length() != 0) {
			sqlSb.append(itemAppBean.getStr("APPFILTER", ""));
		}
		logger.debug(sqlSb.toString());
		// 把子表的应用名称去除掉
		return sqlSb.toString().replaceAll(itemAppId + "\\.", "");
	}
	/**
	 * 得到动态类的所有属性的名称
	 * 
	 * @param dynaBean
	 *            动态类
	 * @return 属性名称集
	 */
	public static String[] getNames(DynaBean dynaBean) {
		Map valueMap = dynaBean.getValues();
		String[] names = new String[valueMap.size()];
		int i = 0;
		for (Iterator it = valueMap.keySet().iterator(); it.hasNext(); i++) {
			names[i] = (String) it.next();
		}
		return names;
	}

	/**
	 * 得到动态类的所有值的内容
	 * 
	 * @param dynaBean
	 *            动态类
	 * @return 动态类包含的值集
	 */
	public static Object[] getValues(DynaBean dynaBean) {
		Map valueMap = dynaBean.getValues();
		Object[] values = new String[valueMap.size()];
		int i = 0;
		for (Iterator it = valueMap.keySet().iterator(); it.hasNext(); i++) {
			values[i] = valueMap.get((String) it.next());
		}
		return values;
	}

	/**
	 * 得到动态类的内容转为字符串。
	 * 
	 * @param dynaBean
	 *            动态类
	 * @return 动态类对应的字符串内容
	 */
	public static String toString(DynaBean dynaBean) {
		Map valueMap = dynaBean.getValues();
		StringBuffer sb = new StringBuffer(valueMap.size() * 4);
		String name;
		for (Iterator it = valueMap.keySet().iterator(); it.hasNext();) {
			name = (String) it.next();
			sb.append(name).append("=").append(valueMap.get(name)).append(";");
		}
		return sb.toString();
	}

	/**
	 * 得到动态类中字符串类型的值
	 * 
	 * @param dynaBean
	 *            动态类
	 * @param name
	 *            属性
	 * @return 值
	 */
	public static String getStringValue(DynaBean dynaBean, String name) {
		Object value = dynaBean.get(name);
		if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}

	/**
	 * 得到动态类中字符串类型的值
	 * 
	 * @param dynaBean
	 *            动态类
	 * @param name
	 *            属性
	 * @param defValue
	 *            缺省值， 如果取不到就返回缺省值
	 * @return 值
	 */
	public static String getStringValue(DynaBean dynaBean, String name, String defValue) {
		Object value = dynaBean.get(name);
		if (value == null) {
			return defValue;
		} else {
			return value.toString();
		}
	}

	/**
	 * 得到自定义处理的Listener实例，进行逻辑处理。
	 * 
	 * @param className
	 *            实现接口的类的全名
	 * @return 自定义处理的Listener实例，如果没有则返回null。
	 */
	public static Object getClassInstance(String className) {
		Object listener = null;

		try {
			if (className.length() > 0) {
				listener = Class.forName(className).newInstance();
			}
		} catch (Exception e) {
			logger.debug("do get Listener Error!", e);
		}

		return listener;
	}

	/**
	 * 取得值中某一个字符。
	 * 
	 * @param dynaBean
	 *            动态类
	 * @param name
	 *            属性
	 * @param index
	 *            字符序号，从1开始
	 * @param defValue
	 *            缺省值， 如果取不到就返回缺省值
	 * @return value 值
	 */
	public static String getCharAt(DynaBean dynaBean, String name, int index, String defValue) {
		String value = getStringValue(dynaBean, name, "");
		if (value.length() >= index) {
			defValue = value.substring(index - 1, index);
		}
		return defValue;
	}

	/**
	 * 设置值中某一个字符。
	 * 
	 * @param dynaBean
	 *            动态类
	 * @param name
	 *            属性
	 * @param index
	 *            字符序号，从1开始
	 * @param value
	 *            设置的值
	 * @param defValue
	 *            补位缺省值
	 */
	public static void setCharAt(DynaBean dynaBean, String name, int index, String value, String defValue) {
		String oldValue = getStringValue(dynaBean, name, "");
		while (oldValue.length() < index) {
			oldValue += defValue;
		}
		StringBuffer sb = new StringBuffer(oldValue);
		sb.replace(index - 1, index, value);
		dynaBean.set(name, sb.toString());
	}

	/**
	 * 将json格式的字符串解析成Map对象
	 * <li>json格式：{"name":"admin","retries":"3fff","testname"
	 * :"ddd","testretries":"fffffffff"}
	 */
	public static HashMap<String, String> fromJsontoHashMap(String object) {
		return fromJsontoHashMap(JSONObject.parseObject(object));
	}
	
	/**
	 * 将json格式的字符串解析成Map对象
	 * <li>json格式：{"name":"admin","retries":"3fff","testname"
	 * :"ddd","testretries":"fffffffff"}
	 */
	public static HashMap<String, String> fromJsontoHashMap(JSONObject jsonObject) {
		HashMap<String, String> data = new HashMap<String, String>();
		if (jsonObject == null) {
			return data;
		}
		Iterator it = jsonObject.entrySet().iterator();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			Entry set = (Entry) it.next();
			String key = set.getKey().toString();
			data.put(key, set.getValue() + "");
		}
		return data;
	}

	/**
	 * 取得request对象中 所有的参数值并生成 一个相应的对象返回
	 * 
	 * @author panlihai
	 * @param request
	 * @return
	 */
	public static DynaBean requestToDynaBean(HttpServletRequest request) {
		try {
			/** 创建封装数据的bean **/
			DynaBean bean = new DynaBean();
			Enumeration<String> en = request.getParameterNames();
			while (en.hasMoreElements()) {
				String el = (String) en.nextElement();				
				bean.set(el.toUpperCase(),request.getParameter(el));
			}
			Enumeration<String> hen = request.getHeaderNames();
			while (hen.hasMoreElements()) {
				String el = (String) hen.nextElement();
				bean.set(el.toUpperCase(),request.getHeader(el));
			}
			bean.set("CLIENTHOST", request.getRemoteHost());	
			bean.set("SESSIONID",request.getSession().getId());
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 取得request对象中 所有的参数值并生成 一个相应的对象返回
	 * 
	 * @author panlihai
	 * @param request
	 * @return
	 */
	public static DynaBean mapToDynaBean(Map<String, Object> map) {
		try {
			/** 创建封装数据的bean **/
			DynaBean bean = new DynaBean();
			for (String key : map.keySet()) {
				bean.set(key.toUpperCase(), map.get(key));
			}
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 取得json对象中 所有的参数值并生成 一个相应的对象返回
	 * 
	 * @author panlihai
	 * @param request
	 * @return
	 */
	public static DynaBean jsonStrToDynaBean(String json) {
			return jsonToDynaBean(JSONObject.parseObject(json));
	}

	/**
	 * 取得json对象中 所有的参数值并生成 一个相应的对象返回
	 * 
	 * @author panlihai
	 * @param request
	 * @return
	 */
	public static DynaBean jsonToDynaBean(JSONObject json) {
		try {
			/** 创建封装数据的bean **/
			DynaBean bean = new DynaBean();
			Iterator en = json.entrySet().iterator();
			while (en.hasNext()) {
				Entry el = (Entry) en.next();
				bean.set(el.getKey().toString().toUpperCase(), el.getValue());
			}
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 把dynaBean转成json,返回 { "ID": 0, "NAME": "Item 0", "PRICE": "$0" }格式
	 */
	public static String toJsonString(DynaBean dyanBean) {
		return toJsonString(dyanBean.getValues());
	}

	/**
	 * 把Map转成json,返回 { "ID": 0, "NAME": "Item 0", "PRICE": "$0" }格式
	 */
	public static String toJsonString(Map map) {
		StringBuilder sb = new StringBuilder("{");
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			sb.append("\"").append(entry.getKey() + "\":\"" + entry.getValue() + "\",");
		}
		if (sb.length() > 1) {
			sb.replace(sb.length() - 1, sb.length(), "}");
		} else {
			return "";
		}
		return sb.toString();
	}

	/**
	 * 把list对象转换成json列表 [ { "ID": 0, "NAME": "Item 0", "PRICE": "$0" }, { "ID":
	 * 1, "NAME": "Item 1", "PRICE": "$1" } ... ]
	 * 
	 * @param list
	 * @return
	 */
	public static String listBeanToJson(List<DynaBean> list) {
		StringBuffer sb = new StringBuffer("[");
		for (DynaBean dynaBean : list) {
			sb.append(toJsonString(dynaBean.getValues())).append(",");
		}
		if (sb.length() > 1) {
			sb.replace(sb.length() - 1, sb.length(), "]");
		} else {
			return "";
		}
		return sb.toString();
	}

	/**
	 * 把list对象转换DynaBean集合
	 * 
	 * @param list
	 * @return
	 */
	public static List<DynaBean> listMapToDynaBean(List<Map> list) {
		List<DynaBean> saveList = new ArrayList<DynaBean>();
		for (Map map : list) {
			DynaBean dyBean = new DynaBean();
			dyBean.setValues(map);
			saveList.add(dyBean);
		}
		list.clear();
		list = null;
		return saveList;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月16日 上午11:10:53 方法说明:根据应用程度默人设置为对象赋值
	 * @param appBean
	 * @param saveBean
	 * @param iscover
	 *            是否覆盖已经有的值
	 */
	public static void setDefaultValueByAppFieldSetting(DynaBean appBean, DynaBean saveBean, boolean iscover) {
		List<Map> fieldsList = (List<Map>) appBean.get(PageUtil.PAGE_APPFIELDS, new ArrayList<Map>());
		for (Map map : fieldsList) {
			DynaBean field =new DynaBean();
			field.setValues(map);
			// 当有内容是不自动赋值
			if (!iscover && saveBean.getStr(field.getStr("FIELDCODE"), "").length() > 0) {
				continue;
			}
			String autoCode = field.getStr("AUTOCODE", "");
			switch (autoCode) {
			/** 特殊属性:自动编码为UUID */
			case KEY_TABLEFIELDUUID:
				saveBean.setStr(field.getStr("FIELDCODE"), SyConstant.getUUID());
				break;
			/** 特殊属性:自动编码为YYYY */
			case KEY_TABLEFIELDYYYY:
				saveBean.setStr(field.getStr("FIELDCODE"), DateUtils.getStringFromDate(KEY_TABLEFIELDYYYY));
				break;
			/** 特殊属性:自动编码为yyyyMM */
			case KEY_TABLEFIELDYYYYMM:
				saveBean.setStr(field.getStr("FIELDCODE"), DateUtils.getStringFromDate(KEY_TABLEFIELDYYYYMM));
				break;
			/** 特殊属性:自动编码为yyyyMMdd */
			case KEY_TABLEFIELDYYYYMMDD:
				saveBean.setStr(field.getStr("FIELDCODE"), DateUtils.getStringFromDate(KEY_TABLEFIELDYYYYMMDD));
				break;
			/** 特殊属性:自动编码为yyyyMMddhhmmSSsss */
			case KEY_TABLEFIELDDATETIME:
				saveBean.setStr(field.getStr("FIELDCODE"), DateUtils.getStringFromDate(KEY_TABLEFIELDDATETIME));
				break;
			case KEY_TABLEFIELDDATE_TIME:
			case KEY_TABLEFIELDTIMESTAMP:
			case KEY_TABLEFIELDDATE:
				saveBean.set(field.getStr("FIELDCODE"), DateUtils.getTimestamp());
				break;
			}
			String defaultValue = field.getStr("FIELDDEFAULT", "");
			if (defaultValue.length() > 0) {
				saveBean.setStr(field.getStr("FIELDCODE"), defaultValue);
			}
		}
	}
	/**
	 * 对象属性复制。
	 * @param fromObject
	 * @param toObject
	 */
	public static void copyProperties(Object toObject,Object  fromObject){
		try {
			PropertyUtils.copyProperties( toObject,fromObject);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}	 
}
