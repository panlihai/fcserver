package cn.pcorp.util;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.scheduling.support.TaskUtils;
import org.springframework.web.context.WebApplicationContext;

import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.system.SysServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/*
 [{
 'PARAMS':'method=getSessionKey,username=PARAM.USER,password=PARAM.PASSWORD',
 'VALUE':'object',
 'CHILDREQUEST':
 	[{
 	'PARAMS':'method=getDeviceByFarmId,sessionkey=GRAND.seessionkey,custId=PARAM.ACCOUNTID',
 	'VALUE':'list',
 	'DOACT':
 		[{
 		'ACT':'SAVE',
 		'AID':'CLOC',
 		'FIELDS':'LOCID=PARENT.orgid,LOCNAME=PARENT.orgname,ENABLE=Y,ACCOUNTID=PARAM.ACCOUNTID,PID=PARAM.PID,CREATETIME=TIMESTAMP,HASCHILD=Y,STATE=Y,LAT=PARAM.LAT,LNG=PARAM.LNG',
 		'KEYWHERE':'LOCID,ACCOUNTID',
 		'CHILDNAME':'devarr',
 		'CHILDVALUE':'list',
 		'CHILDDOACT':
 			[{
 			'ACT':'SAVE',
 			'AID':'CLOC',
 			'FIELDS':'PARENT=PARENT.orgid,LOCID=PARENT.unitid,LOCNAME=PARENT.unitname,ENABLE=Y,ACCOUNTID=PARAM.ACCOUNTID,PID=PARAM.PID,CREATETIME=TIMESTAMP,HASCHILD=N,STATE=Y,LAT=PARAM.LAT,LNG=PARAM.LNG',
 			'KEYWHERE':'LOCID,ACCOUNTID'
 			},
 			{
 			'ACT':'SAVE',
 			'AID':'CASSETS',
 			'FIELDS':'ASSETSID=PARENT.addr,ASSETSNAME=PARENT.unitname,CATALOGID=CHILD.devtype,LOCID=PARENT.unitid,LOCNAME=PARENT.unitname,STATE=Y,HASCHILD=Y,ENABLE=Y,LAT=PARAM.LAT,LNG=PARAM.LNG',
 			'KEYWHERE':'ACCOUNTID,ASSETSID',
 			'CHILDNAME':'channelarr',
 			'CHILDVALUE':'list',
 			'CHILDDOACT':
 				[{
 				'ACT':'SAVE',
 				'AID':'CASSETS',
 				'FIELDS':'ASSETSID=PARENT.Chx,ASSETSNAME=PARENT.DisplayName,ASSETSSPEC=PARENT.Unit,ASSETSMODEL=PARENT.Unit,CATALOGID=PARENT.channelType,LOCID=PARENT.unitid,LOCNAME=PARENT.unitname,STATE=Y,HASCHILD=N,ENABLE=Y,LAT=PARAM.LAT,LNG=PARAM.LNG',
 				'KEYWHERE':'ACCOUNTID,ASSETSID'
 				}]
 			}]
 		}]
 	}]
 }]
 */
public class TaskUtil {
	// 执行请求的URL参数
	private static final String PARAMS = "PARAMS";
	// 值类型 object list两种类型
	private static final String VALUE = "VALUE";
	// 操作类型 save update
	private static final String ACT = "ACT";
	// 元数据编码
	private static final String AID = "AID";
	// 值类型 object list两种类型
	private static final String OBJECT = "object";
	// 值类型 object list两种类型
	private static final String LIST = "list";
	// 子方法
	private static final String CHILDREQUEST = "CHILDREQUEST";
	// 执行操作，主要针对数据库保存操作。
	private static final String DOACT = "DOACT";
	// 祖对象
	private static final String GRAND = "GRAND";
	// 参数默认外面的参数列表是个DynaBean
	private static final String PARAM = "PARAM";
	// 父对象
	private static final String PARENT = "PARENT";
	// 默认时间戳
	private static final String TIMESTAMP = "TIMESTAMP";
	// 保存条件，如果存在此字段的值，则忽略不保存，也 不更新。
	private static final String KEYWHERE = "KEYWHERE";
	// 字段配置
	private static final String FIELDS = "FIELDS";
	// 子操作
	private static final String CHILDDOACT = "CHILDDOACT";
	// 子操作在当前对象中key的值
	private static final String CHILDNAME = "CHILDNAME";
	// 子操作的对象值类型 list或为object
	private static final String CHILDVALUE = "CHILDVALUE";

	/**
	 * 获取对象类型 VALUE：值类型（object为对象；list为对象集合）； PARAMS:参数列表 逗号分隔 =号后面的为参数值的取值规则：
	 * 参数值PARAM； TIMESTAMP为当前时间； 作为前缀的从上级方法获取的对象中取值，上级没有其值的，从上级的上级中取值）；
	 * CHILDREQUEST:子请求；
	 * 
	 * @param url
	 * @param task
	 * @param paramBean
	 * @param dao
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static boolean doTask(String url, String task, DynaBean paramBean, BaseDao dao) throws SQLException, Exception {
		boolean flag = false;
		// 获取任务配置信息，包含执行顺序
		JSONArray grandArrayRequest = JSONArray.parseArray(task);
		for (int i = 0; i < grandArrayRequest.size(); i++) {
			// 默认一次请求的保存。
			List<DynaBean> saveList = new ArrayList<DynaBean>();
			JSONObject grandJson = (JSONObject) grandArrayRequest.get(i);
			Map paramMap = TaskUtil.getParamValuesMap(paramBean, null, null, grandJson.getString(PARAMS));
			String values = HttpUtils.URLGet(url, paramMap, HttpUtils.URL_PARAM_DECODECHARSET_UTF8);
			// 返回的值类型
			String vType = grandJson.getString(VALUE);
			// 第一层循环
			if (vType.equals(OBJECT)) {
				JSONObject grandValue = JSONObject.parseObject(values);
				// 处理祖对象的操作
				// 当有子任务的时候
				if (grandJson.containsKey(CHILDREQUEST)) {
					JSONArray parentArrayRequest = JSONArray.parseArray(grandJson.getString(CHILDREQUEST));
					for (int j = 0; j < parentArrayRequest.size(); j++) {
						JSONObject parentJson = (JSONObject) parentArrayRequest.get(j);
						paramMap.clear();
						paramMap = TaskUtil.getParamValuesMap(paramBean, grandValue, null,
								parentJson.getString(PARAMS));
						values = HttpUtils.URLGet(url, paramMap, HttpUtils.URL_PARAM_DECODECHARSET_UTF8);
						// 返回的值类型
						vType = parentJson.getString(VALUE);
						// 当是请求结构是集合的时候
						if (vType.equals(LIST)) {
							if (!values.startsWith("[")) {
								continue;
							}
							// 获取集合对象
							JSONArray parentArrayValue = JSONArray.parseArray(values);
							// 循环对象，并执行操作，保存到库中，如果keywhere中的值在库中存在，则忽略。
							for (int n = 0; n < parentArrayValue.size(); n++) {
								// 取得结果值。
								JSONObject parentValue = (JSONObject) parentArrayValue.get(n);
								JSONObject parentJSON = new JSONObject();
								parentJSON.putAll(grandValue);
								parentJSON.putAll(parentValue);
								// 获取操作。包含子操作
								JSONArray parentArrayDoAct = JSONArray.parseArray(parentJson.getString(DOACT));
								// 执行操作
								for (int m = 0; m < parentArrayDoAct.size(); m++) {
									// 获取父级的操作
									JSONObject parentDoAct = (JSONObject) parentArrayDoAct.get(m);
									// 执行父级操作
									if (parentDoAct.containsKey("ACT") && parentDoAct.getString("ACT").equals("SAVE")) {
										// 解析字段配置，并根据配置赋值。
										DynaBean data = setDynaBeanFromJSON(dao, parentDoAct.getString(FIELDS),
												paramBean, parentJSON, parentDoAct);
										// 如果不存在则保存，根据keywhere的配置判断。
										checkIsExist(dao, paramBean, parentJSON, data, parentDoAct);
										saveList.add(data);
									}
									// 执行子操作
									saveList.addAll(doChildAct(dao, paramBean, parentDoAct, parentJSON));
								}
							}
						} else {
							// 如果是对象的处理
							if (!values.startsWith("{")) {
								continue;
							}
							// 取得结果值。
							JSONObject parentValue = JSONObject.parseObject(values);
							JSONObject parentJSON = new JSONObject();
							parentJSON.putAll(grandValue);
							parentJSON.putAll(parentValue);
							// 获取操作。包含子操作
							JSONArray parentArrayDoAct = JSONArray.parseArray(parentJson.getString(DOACT));
							// 执行操作
							for (int m = 0; m < parentArrayDoAct.size(); m++) {
								// 获取父级的操作
								JSONObject parentDoAct = (JSONObject) parentArrayDoAct.get(m);
								// 执行父级操作
								if (parentDoAct.containsKey("ACT") && parentDoAct.getString("ACT").equals("SAVE")) {
									// 解析字段配置，并根据配置赋值。
									DynaBean data = setDynaBeanFromJSON(dao, parentDoAct.getString(FIELDS), paramBean,
											parentJSON, parentDoAct);
									// 如果不存在则保存，根据keywhere的配置判断。
									checkIsExist(dao, paramBean, parentJSON, data, parentDoAct);
									saveList.add(data);
								}
								// 执行子操作
								saveList.addAll(doChildAct(dao, paramBean, parentDoAct, parentJSON));
							}
						}
					}
				}
			} else if (vType.equals(LIST)) {
				paramMap.clear();
			}
			if (saveList.size() != 0) {
				dao.insertList(saveList);
				flag = true;
			}
		}

		return flag;
	}

	private static List<DynaBean> doChildAct(BaseDao dao, DynaBean paramBean, JSONObject pDoAct, JSONObject pValue) throws SQLException, Exception {
		String vType = "";
		List<DynaBean> saveList = new ArrayList<DynaBean>();
		if (pDoAct.containsKey(CHILDDOACT)) {
			JSONArray childActArray = JSONArray.parseArray(pDoAct.getString(CHILDDOACT));
			for (int i = 0; i < childActArray.size(); i++) {
				JSONObject childDoAct = (JSONObject) childActArray.get(i);
				// 获取子操作的值类型
				vType = pDoAct.getString(CHILDVALUE);
				if (vType.equals(LIST)) {
					// 子集是对象集合的时候
					if (!pValue.containsKey(pDoAct.getString(CHILDNAME))) {
						continue;
					}
					JSONArray childArrayValue = JSONArray.parseArray(pValue.getString(pDoAct.getString(CHILDNAME)));
					for (int p = 0; p < childArrayValue.size(); p++) {
						JSONObject childValue = (JSONObject) childArrayValue.get(p);
						JSONObject parentValue = new JSONObject();
						parentValue.putAll(pValue);
						parentValue.putAll(childValue);
						// 只有标记为保存的才需要此操作。
						if (childDoAct.containsKey("ACT") && childDoAct.getString("ACT").equals("SAVE")) {
							DynaBean childBean = setDynaBeanFromJSON(dao, childDoAct.getString(FIELDS), paramBean,
									parentValue, childDoAct);
							checkIsExist(dao, paramBean, parentValue, childBean, childDoAct);
							saveList.add(childBean);
						}
						// 处理最末端的对象内容。
						saveList.addAll(doChildAct(dao, paramBean, childDoAct, parentValue));
					}

				} else {
					// 子集是对象的时候
				}
			}
		}
		return saveList;
	}

	/**
	 * 根据配置KEYWHERE="字段1,字段2" 判断是否存在条件为 字段1=data.getStr("字段1") and
	 * 字段2=data.getStr("字段2")
	 * 
	 * @param data
	 * @param parentDoAct
	 * @return
	 */
	private static void checkIsExist(BaseDao dao, DynaBean paramBean, JSONObject parentJson, DynaBean data,
			JSONObject parentDoAct) {
		//
		if (!parentDoAct.containsKey(KEYWHERE)) {
			return;
		}
		String[] keys = parentDoAct.getString(KEYWHERE).split(",");
		StringBuffer sb = new StringBuffer("");
		for (String key : keys) {
			if (key.indexOf("=PARAM.") != -1) {
				String[] fields = key.split("=PARAM.");
				sb.append(" and ").append(fields[0]).append("='").append(paramBean.getStr(fields[1], "")).append("'");
			} else if (key.indexOf("=PARENT.") != -1) {
				String[] fields = key.split("=PARENT.");
				if (parentJson.containsKey(fields[1])) {
					sb.append(" and ").append(fields[0]).append("='").append(parentJson.getString(fields[1]))
							.append("'");
				}
			} else if (key.indexOf("=") != -1) {
				String[] fields = key.split("=");
				if (parentJson.containsKey(fields[1])) {
					sb.append(" and ").append(fields[0]).append("='").append(parentJson.getString(fields[1]))
							.append("'");
				}
			} else {
				sb.append(" and ").append(key).append("='").append(data.getStr(key)).append("'");
			}
		}
		DynaBean sourceBean = new DynaBean(data.getStr(BeanUtils.KEY_TABLE_CODE, ""), sb.toString());
		if (dao.findCountWithQuery(sourceBean) != 0) {
			dao.delete(sourceBean);
		}
	}

	/**
	 * 根据配置的字段，自动赋值到dynaBean中。
	 * 
	 * @param string
	 * @param parentValue
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private static DynaBean setDynaBeanFromJSON(BaseDao dao, String fieldssetting, DynaBean paramBean,
			JSONObject parentValue, JSONObject doActBean) throws SQLException, Exception {
		DynaBean appBean = (DynaBean) CacheUtil.getSysapp(dao.getReadCache(), doActBean.getString(AID),dao);
		DynaBean dynaBean = new DynaBean(appBean == null ? doActBean.getString(AID) : appBean.getStr("MAINTABLE"));
		// DynaBean dynaBean = new DynaBean(doActBean.getString(AID));
		String[] fields = fieldssetting.split(",");
		for (String field : fields) {
			String[] codeValue = field.split("=");
			if (codeValue.length != 2) {
				continue;
			}
			if (codeValue[1].indexOf(PARAM) != -1) {
				String[] values = codeValue[1].split(PARAM + ".");
				if (values.length == 2) {
					dynaBean.setStr(codeValue[0], paramBean.getStr(values[1], ""));
				}
				continue;
			}
			if (codeValue[1].indexOf(PARENT) != -1) {
				String[] values = codeValue[1].split(PARENT + ".");
				if (values.length == 2 && parentValue.containsKey(values[1])) {
					dynaBean.setStr(codeValue[0], parentValue.getString(values[1]));
				}
				continue;
			}
			if (codeValue[1].indexOf(TIMESTAMP) != -1) {
				dynaBean.set(codeValue[0], DateUtils.getTimestamp());
				continue;
			}
			if((codeValue[1]+"").equals("-999.9")){
				continue;
			}
			dynaBean.setStr(codeValue[0], codeValue[1]);
		}
		return dynaBean;
	}

	/**
	 * 根据任务对象的配置获取参数值; 第一级不存在此参数值时， 获取上级数据的值作为参数, 如果上级数值中不包含此参数的值， 则从主记录对象中获取值。
	 * 
	 * @param dsBean
	 *            主记录对象
	 * @param parentJson
	 *            上级对象
	 * @param json
	 *            当前对象
	 * @param param
	 *            参数列表
	 * @return
	 */
	public static Map getParamValuesMap(DynaBean dsBean, JSONObject parentJson, JSONObject json, String paramValues) {
		Map map = new HashMap();
		String[] params = paramValues.split(",");
		for (String paramValue : params) {
			String[] param = paramValue.split("=");
			if (param.length == 2) {
				if (param[1].indexOf(PARAM + ".") != -1) {
					String[] v = param[1].split(PARAM + ".");
					map.put(param[0], dsBean.getStr(v[1].toUpperCase()));
				} else if (param[1].indexOf(TIMESTAMP) != -1) {
					map.put(param[0], DateUtils.getDatetime());
				} else if (param[1].indexOf(GRAND) != -1) {
					String[] v = param[1].split(GRAND + ".");
					if (parentJson != null && parentJson.containsKey(v[1])) {
						map.put(param[0], parentJson.get(v[1]));
					}
				} else if (param[1].indexOf(PARENT + ".") != -1) {
					String[] v = param[1].split(PARENT + ".");
					if (json != null && json.containsKey(v[1])) {
						map.put(param[0], json.get(v[1]));
					}
				} else {
					map.put(param[0], param[1]);
				}
				if (!map.containsKey(param[0])) {
					map.put(param[0], "");
				}
			}
		}
		return map;
	}
	
	public static Map getLocalHost(){
		Map map = new HashMap();
		try{			
			InetAddress netAddress =  InetAddress.getLocalHost();
			Map map1 = System.getenv();
			Object obj =netAddress.getLoopbackAddress();
			map.put("IP", netAddress.getLocalHost().getHostAddress());
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;				
	}
	public static void main(String[] args) {
		 System.out.println(TaskUtil.getLocalHost().get("IP"));
	}
}
