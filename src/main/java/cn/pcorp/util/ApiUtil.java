package cn.pcorp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javacommon.util.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import redis.clients.jedis.Jedis;
import cn.pcorp.controllor.util.MethodConstant;
import cn.pcorp.controllor.util.SubThreadListDetail;
import cn.pcorp.dao.BaseDao;
import cn.pcorp.impl.sys.ServiceListener;
import cn.pcorp.model.Cache;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.BaseService;
import cn.pcorp.service.system.SysServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ApiUtil {
	private static Logger logger = Logger.getLogger(ApiUtil.class);
	


	/**
	 * 检查数据主键是否重复
	 * 
	 * @param dao
	 * @param appBean
	 * @param insertBeanList
	 * @return
	 */
	public static boolean checkKey(BaseDao dao, DynaBean appBean, List<DynaBean> insertBeanList, DynaBean resultBean) {
		List<Map> fieldsList = (List<Map>) appBean.get(PageUtil.PAGE_APPFIELDS, new ArrayList<Map>());
		StringBuffer sb = new StringBuffer("");
		for (Map fieldMap : fieldsList) {
			DynaBean field =  new DynaBean();
			field.setValues(fieldMap);
			// 当有内容是不自动赋值
			if (field.getStr("KEYSEQ", "N").equals("Y")) {
				sb.append(field.getStr("FIELDCODE")).append(",");
			}
		}
		String[] keys = sb.toString().split(",");
		boolean flag = true;
		if (sb.length() > 0) {
			for (DynaBean bean : insertBeanList) {
				for (String key : keys) {
					if (bean.getStr(key, "").length() == 0) {
						resultBean.set(MethodConstant.CODE, "41003");// 主键值不能为空
						resultBean.set(MethodConstant.MSG, getBackName(dao, "41003") + ",主键为空,参考:" + key);
						flag = false;
						break;
					}
					long count = dao.findCountWithQuery(
							new DynaBean(appBean.getStr("MAINTABLE"), " and " + key + "='" + bean.getStr(key) + "'"));
					if (count != 0) {
						resultBean.set(MethodConstant.CODE, "40007");// 主键值重复
						resultBean.set(MethodConstant.MSG, getBackName(dao, "40007") + ",主键重复,参考:" + key);
						flag = false;
						break;
					}
				}
				if (!flag) {
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年4月30日 下午4:22:17 方法说明: 元数据的新增记录
	 * @param dao
	 * @param sqlwhere
	 * @param aids
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static List<Map> createList(BaseService baseService, DynaBean paramBean, String sqlwhere, String aids) throws SQLException, Exception {
		return getModifyList(baseService, paramBean, sqlwhere, aids, "CREATE");
	}

	/**
	 * 根据新建的内容返回值
	 * 
	 * @param dao
	 * @param insertBean
	 * @param interfaceparamList
	 * @return
	 */
	public static List<Map<String, String>> createBackParam(BaseDao dao, List<DynaBean> saveList,
			List<DynaBean> interfaceparamList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("COUNT", saveList.size() + "");
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		// 返回值获取
		for (DynaBean dataBean : saveList) {
			Map<String, String> resultMap = new HashMap<String, String>();
			for (DynaBean param : interfaceparamList) {
				// 输出参数才需要循环
				if (param.getStr("GETPUT", "GET").equals("GET")) {
					continue;
				}
				String name = param.getStr("PARAMNAME", "");
				// 首先从当前对象中获取值
				String value = dataBean.getStr(name, "");
				// 当前对象中没有此值
				if (name.length() != 0) {
					// 从父级对象中获取值
					resultMap.put(name, value);
				}
			}
			resultList.add(resultMap);
		}

		return resultList;
	}

	/**
	 * 
	 * @return
	 */
	public static List<DynaBean> createMethod(HttpServletRequest request, BaseDao dao, DynaBean paramBean,
			DynaBean product, DynaBean appBean) {
		// 如果是用户获取token的，判定是否微信登录（如果是微信登录则根据sessionid查询到token）
		if (paramBean.getStr(MethodConstant.AID).equals("TOKEN")) {
			String sessionId = CookieUtils.getValue(request, "TOKEN");
			if (sessionId != null) {
				List<DynaBean> productList = dao.findWithQueryNoCache(new DynaBean("SYS_PRODUCTUSER",
						" and TOKEN in(select TOKEN from SYS_SESSION where SESSIONID='" + sessionId + "')"));
				return productList;
			}
		}
		List<DynaBean> saveList = new ArrayList<DynaBean>();
		if (!paramBean.getValues().containsKey(MethodConstant.DATA)) {
			// 为了兼容单个记录的新增
			DynaBean insertBean = new DynaBean();
			insertBean.setValues(paramBean.getValues());
			insertBean.set(BeanUtils.KEY_TABLE_CODE, appBean.getStr("MAINTABLE"));
			// 设置默认值
			// 如果是TOKEN新建的情况，则首先从
			BeanUtils.setDefaultValueByAppFieldSetting(appBean, insertBean, false);
			if(insertBean.getStr(MethodConstant.AID)==null){
				insertBean.set(MethodConstant.AID, appBean.getStr(MethodConstant.AID));
			}
			insertBean.set("PID", paramBean.getStr(MethodConstant.PID));
			saveList.add(insertBean);
		} else {
			JSONArray jsons = (JSONArray) paramBean.get(MethodConstant.DATA);
			for (Object obj : jsons) {
				DynaBean insertBean = new DynaBean();
				insertBean.setValues(BeanUtils.fromJsontoHashMap((JSONObject) obj));
				insertBean.set(BeanUtils.KEY_TABLE_CODE, appBean.getStr("MAINTABLE"));
				// 设置默认值
				BeanUtils.setDefaultValueByAppFieldSetting(appBean, insertBean, false);
				if(insertBean.getStr(MethodConstant.AID)==null){
					insertBean.set(MethodConstant.AID, appBean.getStr(MethodConstant.AID));
				}
				insertBean.set("PID", paramBean.getStr(MethodConstant.PID));
				// 校验是否有主键
				saveList.add(insertBean);
			}
		}
		return saveList;
	}

	/**
	 * 
	 * @return
	 */
	public static List<DynaBean> updateMethod(BaseDao dao, DynaBean paramBean, DynaBean product, DynaBean appBean) {
		List<DynaBean> saveList1 = new ArrayList<DynaBean>();
		JSONArray jsons = (JSONArray) paramBean.get(MethodConstant.DATA);
		for (Object obj : jsons) {
			DynaBean updateBean = new DynaBean();
			updateBean.setValues(BeanUtils.fromJsontoHashMap((JSONObject) obj));
			updateBean.set(BeanUtils.KEY_TABLE_CODE, appBean.getStr("MAINTABLE"));
			if(updateBean.getValues().containsKey(BeanUtils.KEY_WHERE)){
				updateBean.setStr(BeanUtils.KEY_WHERE," and " + updateBean.getStr(BeanUtils.KEY_WHERE));
			}else{
				updateBean.setStr(BeanUtils.KEY_WHERE, " and id ='" + updateBean.getStr(MethodConstant.ID) + "' ");
			}
			updateBean.set(MethodConstant.AID, appBean.getStr(MethodConstant.AID));
			updateBean.set("PID", paramBean.getStr(MethodConstant.PID));
			saveList1.add(updateBean);
		}
		return saveList1;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年4月30日 下午4:22:17 方法说明:元数据的删除记录
	 * @param dao
	 * @param sqlwhere
	 * @param aids
	 * @return
	 */
	public static List<Map> removeList(BaseService baseService, DynaBean paramBean, String sqlwhere, String aids) {
		List<Map> createList = new ArrayList<Map>();
		String[] ids = aids.split(",");
		StringBuffer resultIds = new StringBuffer("");
		// 遍历元数据
		for (String id : ids) {
			resultIds.setLength(0);
			if (id.length() == 0) {
				continue;
			}
			// 找到修改日志,根据修改日志获取DATAID并把数据获取并放入返回结果集中.
			DynaBean queryBean = new DynaBean("SYS_LOG_REMOVE", sqlwhere + " and APPID='" + id + "'", "LOGTIME");
			long pageNum = paramBean.getLong("PAGENUM", 0);
			long pageSize = paramBean.getLong("PAGESIZE", 10);// 默认10条记录
			queryBean.set(BeanUtils.KEY_PAGE_COUNT, pageNum);
			queryBean.set(BeanUtils.KEY_PAGE_SIZE, pageSize > 20 ? 20 : pageSize);// 最多20条
			queryBean.setStr(BeanUtils.KEY_SELECT, "DATAID");
			queryBean.setStr(BeanUtils.KEY_GROUP, MethodConstant.ID);
			List<Map> resultList = baseService.getBaseDao().findWithQueryMap(queryBean);
			Map map = new HashMap();
			map.put("AID", id);
			queryBean.setStr(BeanUtils.KEY_GROUP, "");
			map.put(MethodConstant.TOTALSIZE, baseService.getBaseDao().findCountWithQuery(queryBean));
			if (resultList.size() != 0) {
				for (Map rMap : resultList) {
					resultIds.append("'").append(rMap.get("DATAID")).append("',");
				}
				if (resultIds.length() > 0) {
					resultIds.setLength(resultIds.length() - 1);
				}
				map.put(MethodConstant.LISTSIZE, resultList.size());
				map.put(MethodConstant.DATA, resultIds.toString().split(","));
			} else {
				map.put(MethodConstant.LISTSIZE, 0);
				map.put(MethodConstant.DATA, new ArrayList());
			}
			createList.add(map);
		}
		return createList;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年4月30日 下午4:22:17 方法说明:元数据的修改记录
	 * @param dao
	 * @param sqlwhere
	 * @param aids
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static List<Map> modifyList(BaseService baseService, DynaBean paramBean, String sqlwhere, String aids) throws SQLException, Exception {
		return getModifyList(baseService, paramBean, sqlwhere, aids, "UPDATE");
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年4月30日 下午5:19:23 方法说明:获取新增或修改的记录
	 * @param baseService
	 * @param paramBean
	 * @param sqlwhere
	 * @param aids
	 * @param string
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static List<Map> getModifyList(BaseService baseService, DynaBean paramBean, String sqlwhere, String aids,
			String act) throws SQLException, Exception {
		List<Map> createList = new ArrayList<Map>();
		String[] ids = aids.split(",");
		StringBuffer resultIds = new StringBuffer("");
		// 遍历元数据
		for (String id : ids) {
			resultIds.setLength(0);
			if (id.length() == 0) {
				continue;
			}
			// 找到修改日志,根据修改日志获取DATAID并把数据获取并放入返回结果集中.
			DynaBean queryBean = new DynaBean("SYS_LOG_" + act, sqlwhere + " and APPID='" + id + "'", "LOGTIME");
			long pageNum = paramBean.getLong("PAGENUM", 0);
			long pageSize = paramBean.getLong("PAGESIZE", 10);// 默认10条记录
			queryBean.set(BeanUtils.KEY_PAGE_COUNT, pageNum);
			queryBean.set(BeanUtils.KEY_PAGE_SIZE, pageSize > 20 ? 20 : pageSize);// 最多20条
			queryBean.setStr(BeanUtils.KEY_SELECT, "DATAID");
			queryBean.setStr(BeanUtils.KEY_GROUP, MethodConstant.ID);
			List<Map> resultList = baseService.getBaseDao().findWithQueryMap(queryBean);
			Map map = new HashMap();
			map.put("AID", id);
			queryBean.setStr(BeanUtils.KEY_GROUP, "");
			map.put(MethodConstant.TOTALSIZE, baseService.getBaseDao().findCountWithQuery(queryBean));
			if (resultList.size() != 0) {
				DynaBean appBean = baseService.findSysAppByCode(id);
				for (Map rMap : resultList) {
					resultIds.append("'").append(rMap.get("DATAID")).append("',");
				}
				if (resultIds.length() > 0) {
					resultIds.setLength(resultIds.length() - 1);
				}
				DynaBean resultBean = new DynaBean(appBean.getStr("MAINTABLE"),
						" and id in (" + resultIds.toString() + ")");
				resultBean.set(BeanUtils.KEY_PAGE_COUNT, pageNum);
				resultBean.set(BeanUtils.KEY_PAGE_SIZE, pageSize > 20 ? 20 : pageSize);// 最多20条
				List<Map> returnList = baseService.getBaseDao().findWithQueryMap(resultBean);
				map.put(MethodConstant.LISTSIZE, returnList.size());
				map.put(MethodConstant.DATA, returnList);
			} else {
				map.put(MethodConstant.LISTSIZE, 0);
				map.put(MethodConstant.DATA, new ArrayList());
			}
			createList.add(map);
		}
		return createList;
	}
	/**
	 * 判断是否是缓存场景，如果是缓存场景的明细
	 * @param readCache
	 * @param appId
	 * @param paramBean
	 * @return
	 */
	public static Map checkInfoScene(final BaseService baseService,final Cache readCache,final DynaBean appBean,DynaBean paramBean){		
		//获取主对象的缓存对象
		Map scene = (Map)CacheUtil.getLocKeyTypeCache(readCache,"SYSCACHE",appBean.getStr("APPID"));
		if(scene!=null){
			//获取缓存对象的字段内容，此字段是为了合成缓存key
			String[] keys = scene.get("SQLFIELDS").toString().split(",");
			StringBuffer sb = new StringBuffer("");
			//合成key
			for(String key:keys){
				sb.append(paramBean.getStr(key)).append(":");
			}
			Jedis jedis = null;
			try{
				//获取缓存工具类
				jedis = (Jedis) readCache.getRealCache(readCache.getReadjedisPool());
				//合成key最终格式为 字段内容:AID 
				sb.append(appBean.getStr("APPID"));
				//根据key获取主对象内容
				String val = jedis.get(sb.toString());
				if(val==null){
					return null;
				}
				//反序列化主对象值
				DynaBean d = BeanUtils.jsonToDynaBean(JSONObject.parseObject(val));
				if(d==null){
					return null;
				}
				//主对象内容返回结果内容
				Map result = d.getValues();
				// 如果是获取详情子表，每个子表进行获取相关内容，否则返回当前结果内容
				if (paramBean.getStr(MethodConstant.ACT).equals(MethodConstant.ACT_INFOLIST)) {
					//解析客户端需要的元数据内容
					JSONArray cids = JSONArray.parseArray(paramBean.getStr(MethodConstant.ACT_LISTDETAIL));
					//遍历元数据，并根据规则组装数据。
					for (int i = 0; i < cids.size(); i++) {
						//获取当前遍历的元数据对象
						JSONObject aidJson = cids.getJSONObject(i);
						//获取当前遍历的元数据名称
						String aid = aidJson.getString("AID");					
						// 得到元数据的内容
						DynaBean appDetailBean = CacheUtil.getSysapp(readCache, aid,baseService.getBaseDao());
						Map map = new HashMap();
						List<Map> mm = new ArrayList<Map>();
						long pageNum = aidJson.containsKey("PAGENUM") ? aidJson.getLong("PAGENUM") : 0;
						long pageSize = aidJson.containsKey("PAGESIZE") ? aidJson.getLong("PAGESIZE") : appDetailBean.getLong("PAGESIZE", 10);	
						boolean flag = false;
						//缓存获取该元数据对应的数据内容 根据主对象key:aid的值作为key从缓存中获取数据。
						mm = CacheUtil.getListMapTypeCache(jedis,result.get((sb.toString()+":"+aid).toUpperCase()).toString());
						//如果为空则标记为true;
						if(mm==null||mm.isEmpty()){
							flag = true;
						}else{
							//获取当前主对象的关联缓存，需要从关联缓存中获取数据				
							Map itemCache = (Map)CacheUtil.getLocKeyTypeCache(readCache,"SYSCACHE",aid);
							List<Map> cacheLink = (List<Map>)CacheUtil.getLocKeyTypeCache(readCache,aid,"SYSCACHELINK");
							//如果不为空则有关联缓存。
							if(cacheLink!=null){
								//子表数据的每个记录进行遍历，查看关联缓存的对象，是否还有子表缓存数据。
								for(Map map1:mm){								
									for(Map cacheMap:cacheLink){										
										logger.info(map1.get("ID").toString().toUpperCase()+":"+aid+":"+cacheMap.get("APPID"));
										String k = map1.get("ID").toString().toUpperCase()+":";
										if(itemCache!=null){
											k = "";
											String[] refFields = itemCache.get("SQLFIELDS").toString().split(",");
											for(String f:refFields){
												k+=map1.get(f).toString()+":";
											}
										}					
										if(map1.containsKey(k.toUpperCase()+aid+":"+cacheMap.get("APPID"))){
											String key = map1.get(k.toUpperCase()+aid+":"+cacheMap.get("APPID")).toString();
											if(key.length()!=0){
												List<String> value = jedis.mget(key.split(","));
												if(value!=null){		
													List<Map> rList = new ArrayList<Map>();
													for(String r:value){
														rList.add(BeanUtils.fromJsontoHashMap(r));
													}
													map1.put(cacheMap.get("APPID").toString(),rList);
												}else{
													map1.put(cacheMap.get("APPID").toString(), new ArrayList());
												}
											}else{
												map1.put(cacheMap.get("APPID").toString(), new ArrayList());
											}
										}else{
											map1.put(cacheMap.get("APPID").toString(), new ArrayList());
										}										
									}
								}
							}
						}
						if(flag){
							// 根据主子表的关系组成查询条件
							String sqlwhere = baseService.getSqlWhereBy(appBean, result.get(MethodConstant.ID).toString(),appDetailBean.getStr("APPID"));
							if (aidJson.containsKey(BeanUtils.KEY_WHERE) && aidJson.getString(BeanUtils.KEY_WHERE).length() > 0) {
								sqlwhere += " and " + aidJson.getString(BeanUtils.KEY_WHERE);
							}
							DynaBean queryBean = new DynaBean(appDetailBean.getStr("MAINTABLE"), sqlwhere);
							map.put(MethodConstant.TOTALSIZE, baseService.getBaseDao().findCountWithQuery(queryBean));
							queryBean.set(BeanUtils.KEY_PAGE_COUNT, pageNum);
							queryBean.set(BeanUtils.KEY_PAGE_SIZE, pageSize);	
							if (aidJson.containsKey(BeanUtils.KEY_ORDER)) {
								queryBean.setStr(BeanUtils.KEY_ORDER, aidJson.getString(BeanUtils.KEY_ORDER));
							}
							if (aidJson.containsKey(BeanUtils.KEY_SELECT)) {
								queryBean.setStr(BeanUtils.KEY_SELECT, aidJson.getString(BeanUtils.KEY_SELECT));
							}
							mm = baseService.getBaseDao().findWithQueryMap(queryBean);
						}
						map.put(MethodConstant.LISTSIZE, mm.size());
						map.put(MethodConstant.DATA, mm);
						result.put(aid, map);
					}
				}
				return result;			
			}catch(Exception ex){
				ex.printStackTrace();
				return null;
			}finally{
				if(jedis!=null){
					readCache.shutdown(readCache.getReadjedisPool(), jedis);
				}
			}
		}else{
			return null;
		}
	}
	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年4月28日 下午5:38:56 方法说明:
	 * @param productBean
	 * @param appBean
	 * @param paramBean
	 * @return
	 */
	public static void detailList(BaseService baseService, DynaBean productBean, DynaBean appBean, DynaBean paramBean,
			Map result) {
		// 转换成集合
		Object ddd = JSONObject.parse(paramBean.getStr(MethodConstant.ACT_LISTDETAIL));
		JSONArray cids = new JSONArray();
		if(ddd instanceof JSONObject){
			cids.add(ddd);
		}else{
			cids = JSONArray.parseArray(paramBean.getStr(MethodConstant.ACT_LISTDETAIL));
		}
		List<Map> appLinkList = (List<Map>) appBean.get(PageUtil.PAGE_APPLINKS);		
		// 线程计数器
		CountDownLatch threadsSignal = new CountDownLatch(cids.size());
		List<SubThreadListDetail> subThreadList = new ArrayList<SubThreadListDetail>();
		for (int i = 0; i < cids.size(); i++) {
			SubThreadListDetail subThread = new SubThreadListDetail();
			subThread.setResult(result);
			subThreadList.add(subThread);
			subThread.setDetail(cids.getJSONObject(i));
			subThread.setBaseService(baseService);
			subThread.setAppLinkList(appLinkList);
			subThread.setThreadsSignal(threadsSignal);
			subThread.run();
		}
		try {
			threadsSignal.await();		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	/**
	 * 
	 * if (dataJson == null) { result.setStr(CODE, "40005");// 不合法的消息体
	 * result.setStr(MSG, getBackName(dao, "40005")); return false; } return
	 * false; }
	 * 
	 * /**
	 * 
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月18日 下午1:38:44 方法说明:
	 * @param dao
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 * @return
	 */
	public static String queryMethod(BaseDao dao, DynaBean paramBean, DynaBean productBean, DynaBean appBean,
			List<DynaBean> paramList) {
		StringBuffer sqlwhere = new StringBuffer(appBean.getStr("APPFILTER", " and 1=1 "));
		for (DynaBean param : paramList) {
			// 只看入口参数作为查询条件
			if (param.getStr("GETPUT", "PUT").equals("PUT")) {
				continue;
			}
			// 只对条件参数进行条件拼接
			if (!param.getStr("PARAMTYPE", "").equals("PARAMWHERE")) {
				continue;
			}
			switch (param.getStr("VALUETYPE", "")) {
			case "STR":// 字符型
				if(paramBean.getStr(param.getStr("PARAMNAME"), "").length()!=0){
					sqlwhere.append(" and ").append(param.getStr("PARAMNAME")).append(" = '")
							.append(paramBean.getStr(param.getStr("PARAMNAME"), "")).append("'");
				}
				break;
			case "NUM":
				sqlwhere.append(" and ").append(param.getStr("PARAMNAME")).append(" =")
						.append(paramBean.getStr(param.getStr("PARAMNAME"), ""));
				break;
			case "DATE":
				sqlwhere.append(" and ").append(param.getStr("PARAMNAME")).append(" ='")
						.append(paramBean.getStr(param.getStr("PARAMNAME"), "")).append("'");
				break;
			case "USERDEFINE":// 自定义的条件,这时候需要客户端写字段名称,不进行拼接
				sqlwhere.append(" and ").append(paramBean.getStr(param.getStr("PARAMNAME"), " 1=1 ")).append(" ");
				break;
			}

		}
		List<DynaBean> menuBeanList = dao.findWithQueryNoCache(new DynaBean("SYS_MENU",
				" and PID='" + paramBean.get(MethodConstant.PID) + "' and WXMENU='N' and APPID = '" + appBean.getStr(MethodConstant.AID) + "'"));
		if (menuBeanList.size() > 0) {
			DynaBean menuBean = menuBeanList.get(0);
			if (menuBean.getStr("APPFILTER", "").length() != 0) {
				sqlwhere.append(" ").append(menuBean.getStr("APPFILTER"));
			}
		}
		List<Map> fieldsList = (List<Map>) appBean.get(PageUtil.PAGE_APPFIELDS);
		for (Map map : fieldsList) {
			DynaBean field = new DynaBean();
			field.setValues(map);
			// 如果包含此字段并且不是系统软件.
			if (field.getStr("FIELDCODE", "").equals(MethodConstant.SUPERVISE) && !paramBean.getStr(MethodConstant.PID).equals("SYSTEM")
					&& !appBean.getStr(MethodConstant.AID).equals("CUSER")) {
				sqlwhere.append(" and SUPERVISE='").append(paramBean.get(MethodConstant.SUPERVISE)).append("'");
			}
			if (field.getStr("FIELDCODE", "").equals(MethodConstant.PID) && !paramBean.getStr(MethodConstant.PID).equals("SYSTEM")) {
				sqlwhere.append(" and PID='").append(paramBean.get(MethodConstant.PID)).append("'");
			}
		}
		sqlwhere.append(" and ").append(paramBean.getStr(BeanUtils.KEY_WHERE, " 1=1"));
		return sqlwhere.toString();
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月21日 下午5:37:02 方法说明:
	 * @param dao
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 * @param paramList
	 * @param result
	 */
	public static void resetPsw(BaseDao dao, DynaBean paramBean, DynaBean productBean, DynaBean appBean,
			List<DynaBean> paramList, DynaBean result) {
		List<DynaBean> dy = dao
				.findWithQueryNoCache(new DynaBean("C_USER", " and TEL='" + paramBean.getStr("MOBILEPHONE") + "'"));
		DynaBean dynaBean = dy.get(0);
		dynaBean.setValues(paramBean.getValues());
		dynaBean.setStr(BeanUtils.KEY_TABLE_CODE, "C_USER");
		dynaBean.setStr("PWD", MD5Util.MD5(paramBean.getStr("PASSWORD")));
		dynaBean.setStr("USERID", paramBean.getStr("USERID", paramBean.getStr("MOBILEPHONE")));
		dynaBean.setStr(BeanUtils.KEY_WHERE, " and ID='" + dynaBean.getStr("ID") + "'");
		dao.updateOne(dynaBean);
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月21日 下午5:37:02 方法说明:
	 * @param dao
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 * @param paramList
	 * @param result
	 */
	public static void register(BaseDao dao, DynaBean paramBean, DynaBean productBean, DynaBean appBean,
			List<DynaBean> paramList, DynaBean result) {
		DynaBean dynaBean = new DynaBean();
		dynaBean.setValues(paramBean.getValues());
		dynaBean.setStr(BeanUtils.KEY_TABLE_CODE, "C_USER");
		dynaBean.setStr("PWD", MD5Util.MD5(paramBean.getStr("PASSWORD")));
		dynaBean.setLong("CREATETIME", DateUtils.getTimestamp());
		dynaBean.setStr("TEL", paramBean.getStr("MOBILEPHONE", ""));
		dynaBean.setStr("USERID", paramBean.getStr("USERID", paramBean.getStr("MOBILEPHONE")));
		dynaBean.setStr("ENABLE", SyConstant.STR_YES);
		dao.insertOne(dynaBean);
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月21日 下午5:36:54 方法说明:
	 * @param str
	 */
	public static void logout(BaseDao dao, String token) {
		DynaBean session = new DynaBean("SYS_SESSION", "and TOKEN='" +token + "'");
		List<DynaBean> userSessionList = dao.findWithQueryNoCache(session);
		// 曾经登录过
		if (userSessionList.size() > 0) {
			session = userSessionList.get(0);
		}
		session.setStr(MethodConstant.TOKEN, token);
		session.setStr("STATUS", "N");// 记录在线状态
		session.set("LASTLOGOUTTIME", DateUtils.getTimestamp());// 记录登录时间
		session.setStr(BeanUtils.KEY_TABLE_CODE, "SYS_SESSION");
		if (userSessionList.size() > 0) {
			session.setStr(BeanUtils.KEY_WHERE, "and TOKEN='" + token + "' and ID='"+session.getStr("ID")+"'");
			dao.updateOne(session);
		} else {
			dao.insertOne(session);
		}
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年5月3日 上午11:28:00 方法说明:文件上传,为特定的元数据上传图片及上传其它文件,
	 *          通过SOURCEAID(元数据), SOURCEID(元数据字段ID的值),SOURCEFIELD(上传文件绑定到的字段名),
	 *          FILETYPE(文件类型),RESTITLE(文件描述或标题),
	 * @param request
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 */
	public static Boolean upload(HttpServletRequest request, DynaBean paramBean, DynaBean productBean,
			DynaBean appBean, DynaBean result, BaseService service) {
		StringBuffer rtnSb = new StringBuffer("");
		StringBuffer fileSrc = new StringBuffer("");
		try {
			List<DynaBean> saveList = new ArrayList<DynaBean>();
			// 此元数据ID值,如CUSER用户元数据
			String sourceAid = paramBean.getStr(MethodConstant.ACT_SOURCEAID);
			// 此元数据对应的数据ID字段的值
			String sourceId = paramBean.getStr(MethodConstant.ACT_SOURCEID);
			// 此元数据绑定的字段
			String sourceField = paramBean.getStr(MethodConstant.ACT_SOURCEFIELD);
			// 此上传文件的类型
			String fileType = paramBean.getStr(MethodConstant.ACT_UPLOADFILETYPE);
			// 此上传文件的标题,如果没有此文件标题,则取文件名称作为标题.写入资源库中
			String resTitle = new String(paramBean.getStr(MethodConstant.ACT_RESTITLE).getBytes("ISO-8859-1"), "UTF-8");
			// 创建一个通用的多部分解析器
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
					request.getSession().getServletContext());
			// 判断 request 是否有文件上传,即多部分请求
			if (multipartResolver.isMultipart(request)) {
				// 转换成多部分request
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				// 取得request中的所有文件名
				Iterator<String> iter = multiRequest.getFileNames();
				boolean checkHasFile = false;
				while (iter.hasNext()) {
					// 记录上传过程起始时的时间，用来计算上传时间
					int pre = (int) System.currentTimeMillis();
					// 取得上传文件
					MultipartFile file = multiRequest.getFile(iter.next());
					if (file != null) {
						// 取得当前上传文件的文件名称
						String myFileName = file.getOriginalFilename();
						// 如果名称不为“”,说明该文件存在，否则说明该文件不存在
						if (myFileName.trim() != "") {
							checkHasFile = true;
							String[] resPath = productBean.getStr("RESPATH").split(":");
							// 重命名上传后的文件名
							String fileName = resPath[1] + "/" + sourceAid + "/"
									+ SyConstant.getUUID()
									+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
							// 定义上传路径
							String serverPath = request.getRealPath("/");
							String path = serverPath.substring(0, serverPath.indexOf("server")-1) + "/" + fileName;
							String hostPath = request.getScheme() + "://" + request.getServerName() + ":"
									+ resPath[0];
							File localFile = new File(path);
							File parentFile = localFile.getParentFile();
							if (!parentFile.exists()) {
								parentFile.mkdirs();
							}
							file.transferTo(localFile);
							DynaBean resBean = new DynaBean("SYS_RESLIB");
							resBean.setStr("RESID", SyConstant.getUUID());
							fileSrc.append(resBean.getStr("RESID")).append(",");
							resBean.setStr("PID", paramBean.getStr(MethodConstant.PID));
							resBean.setStr("RESPATH", hostPath + "/" + fileName);
							rtnSb.append(resBean.getStr("RESPATH")).append(",");
							resBean.setStr("RESTYPE", fileType);
							resBean.setStr("ENABLE", "Y");
							resBean.set("SORT", DateUtils.getTimestamp());
							resBean.setStr("RESNAME", resTitle.length() == 0
									? myFileName.substring(0, myFileName.lastIndexOf(".")) : resTitle);
							resBean.setStr("REMARK", myFileName + " SOURCEAID=" + sourceAid + " SOURCEID=" + sourceId
									+ " SOURCEFIELD=" + sourceField);
							saveList.add(resBean);
						}
					}
				}
				if (!checkHasFile) {
					// 没有任何文件
					result.set(MethodConstant.CODE, "44001");// 没有任何文件
					result.set(MethodConstant.MSG, getBackName(service.getBaseDao(), "44001"));
					return false;

				}
			} else {
				// 没有任何文件
				result.set(MethodConstant.CODE, "44001");// 没有任何文件
				result.set(MethodConstant.MSG, getBackName(service.getBaseDao(), "44001"));
				return false;
			}
			// 写主表数据
			// 判断是否存在元数据编码,是否存在元数据字段,是否存在此id对应的值
//			List<DynaBean> dList = service.getBaseDao()
//					.findWithQueryNoCache(new DynaBean("SYS_APP", " and APPID in (select APPID from SYS_INTERFACE where PID='"
//							+ paramBean.getStr(PID, "") + "' and REQURL='" + sourceAid + "')"));
//			if (dList.size() != 0) {
//				DynaBean sourceAppBean = dList.get(0);
//				List<DynaBean> sourceList = service.getBaseDao()
//						.findWithQueryNoCache(new DynaBean(sourceAppBean.getStr("MAINTABLE"), " and ID='" + sourceId + "'"));
//				if (sourceList.size() == 0) {
//					result.setStr(CODE, "40117");// 不存在此id的记录
//					result.setStr(MSG, getBackName(service.getBaseDao(), "40117") + ",参考:ID值'" + sourceId + "'");
//					return false;
//				}
//				DynaBean sourceBean = sourceList.get(0);
//				sourceBean.setStr(BeanUtils.KEY_TABLE_CODE, sourceAppBean.getStr("MAINTABLE"));
//				sourceBean.setStr(BeanUtils.KEY_WHERE, " and ID='" + sourceBean.getStr(ID) + "'");
//				fileSrc.setLength(fileSrc.length() - 1);
//				sourceBean.setStr(sourceField, fileSrc.toString());
//				service.getBaseDao().updateOne(sourceBean);
				service.getBaseDao().insertBatList(saveList);
//			} else {
//				result.setStr(CODE, "40003");// 不合法的AID
//				result.setStr(MSG, getBackName(service.getBaseDao(), "40003") + ",参考:元数据" + sourceAid);
//				return false;
//			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (rtnSb.length() > 0) {
			rtnSb.setLength(rtnSb.length() - 1);
		}
		result.setStr(MethodConstant.DATA, rtnSb.toString());
		result.setStr("RESIDS", fileSrc.toString());
		return true;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年5月3日 上午11:28:00 方法说明:文件上传,为特定的元数据上传图片及上传其它文件,
	 *          通过SOURCEAID(元数据), SOURCEID(元数据字段ID的值),SOURCEFIELD(上传文件绑定到的字段名),
	 *          FILETYPE(文件类型),RESTITLE(文件描述或标题),
	 * @param request
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 */
	public static Boolean uploadFile(HttpServletRequest request, DynaBean paramBean, DynaBean productBean,
			DynaBean appBean, DynaBean result, BaseService service) {
		StringBuffer rtnSb = new StringBuffer("");
		StringBuffer fileSrc = new StringBuffer("");
		List<DynaBean> saveList = new ArrayList<DynaBean>();
		try {
			// 此上传文件的类型
			String fileType = paramBean.getStr(MethodConstant.ACT_UPLOADFILETYPE);
			// 此上传文件的标题,如果没有此文件标题,则取文件名称作为标题.写入资源库中
			String resTitle = new String(paramBean.getStr(MethodConstant.ACT_RESTITLE).getBytes("ISO-8859-1"), "UTF-8");
			// 创建一个通用的多部分解析器
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
					request.getSession().getServletContext());
			// 判断 request 是否有文件上传,即多部分请求
			if (multipartResolver.isMultipart(request)) {
				// 转换成多部分request
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				// 取得request中的所有文件名
				Iterator<String> iter = multiRequest.getFileNames();
				boolean checkHasFile = false;
				while (iter.hasNext()) {
					// 记录上传过程起始时的时间，用来计算上传时间
					int pre = (int) System.currentTimeMillis();
					// 取得上传文件
					MultipartFile file = multiRequest.getFile(iter.next());
					if (file != null) {
						// 取得当前上传文件的文件名称
						String myFileName = file.getOriginalFilename();
						// 如果名称不为“”,说明该文件存在，否则说明该文件不存在
						if (myFileName.trim() != "") {
							checkHasFile = true;
							// 重命名上传后的文件名
							String fileName = productBean.getStr("RESPATH") + "/RESLIB/" + SyConstant.getUUID()
									+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
							// 定义上传路径
							String path = request.getRealPath("/") + "/" + fileName;
							String hostPath = request.getScheme() + "://" + request.getServerName() + ":"
									+ request.getServerPort() + request.getContextPath();
							File localFile = new File(path);
							File parentFile = localFile.getParentFile();
							if (!parentFile.exists()) {
								parentFile.mkdirs();
							}
							file.transferTo(localFile);
							DynaBean resBean = new DynaBean("SYS_RESLIB");
							resBean.setStr("RESID", SyConstant.getUUID());
							fileSrc.append(resBean.getStr("RESID")).append(",");
							resBean.setStr("PID", paramBean.getStr(MethodConstant.PID));
							resBean.setStr("RESPATH", hostPath + "/" + fileName);
							rtnSb.append(resBean.getStr("RESPATH")).append(",");
							resBean.setStr("RESTYPE", fileType);
							resBean.setStr("ENABLE", "Y");
							resBean.set("SORT", DateUtils.getTimestamp());
							resBean.setStr("RESNAME", resTitle.length() == 0
									? myFileName.substring(0, myFileName.lastIndexOf(".")) : resTitle);
							resBean.setStr("REMARK", myFileName);
							saveList.add(resBean);
						}
					}
				}
				if (!checkHasFile) {
					// 没有任何文件
					result.set(MethodConstant.CODE, "44001");// 没有任何文件
					result.set(MethodConstant.MSG, getBackName(service.getBaseDao(), "44001"));
					return false;

				}
				service.getBaseDao().insertBatList(saveList);
			} else {
				// 没有任何文件
				result.set(MethodConstant.CODE, "44001");// 没有任何文件
				result.set(MethodConstant.MSG, getBackName(service.getBaseDao(), "44001"));
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (rtnSb.length() > 0) {
			rtnSb.setLength(rtnSb.length() - 1);
		}
		if (fileSrc.length() > 0) {
			fileSrc.setLength(fileSrc.length() - 1);
		}
		result.setStr(MethodConstant.DATA, rtnSb.toString());
		result.setStr("RESIDS", fileSrc.toString());
		result.set("DATAS", saveList);
		return true;
	}

	/**
	 * 
	 * @param dao
	 * @param backCode
	 * @return
	 */
	public static String getBackName(BaseDao dao, String backCode) {
		String value = (String)CacheUtil.getLocKeyTypeCache(dao.getWriteCache(),"SYSBACKCODE", backCode);
		if(value==null){
			List<DynaBean> backBeanList = dao.findWithQueryNoCache(new DynaBean("SYS_BACKCODE", " and backCode ='" + backCode + "'"));			
			if (backBeanList.size() == 0) {
				return "未知错误";
			}
			CacheUtil.setLocKeyTypeCache(dao.getWriteCache(),"SYSBACKCODE", backCode, backBeanList.get(0).getStr("BACKNAME", ""));
			return backBeanList.get(0).getStr("BACKNAME", "");
		}else{
			return value;
		}
		
	}

	private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '+', '/', };

	private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
			5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26,
			27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1,
			-1, -1, -1 };

	/**
	 * 解密
	 * 
	 * @param str
	 * @return
	 */
	private static byte[] decode(String str) {
		byte[] data = str.getBytes();
		int len = data.length;
		ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
		int i = 0;
		int b1, b2, b3, b4;

		while (i < len) {
			do {
				b1 = base64DecodeChars[data[i++]];
			} while (i < len && b1 == -1);
			if (b1 == -1) {
				break;
			}

			do {
				b2 = base64DecodeChars[data[i++]];
			} while (i < len && b2 == -1);
			if (b2 == -1) {
				break;
			}
			buf.write((int) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

			do {
				b3 = data[i++];
				if (b3 == 61) {
					return buf.toByteArray();
				}
				b3 = base64DecodeChars[b3];
			} while (i < len && b3 == -1);
			if (b3 == -1) {
				break;
			}
			buf.write((int) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

			do {
				b4 = data[i++];
				if (b4 == 61) {
					return buf.toByteArray();
				}
				b4 = base64DecodeChars[b4];
			} while (i < len && b4 == -1);
			if (b4 == -1) {
				break;
			}
			buf.write((int) (((b3 & 0x03) << 6) | b4));
		}
		return buf.toByteArray();
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月21日 下午5:36:51 方法说明:
	 * @param str
	 * @param str2
	 */
	public static String login(BaseDao dao, DynaBean paramBean) {
		String token = null;		
		if(paramBean.getStr(MethodConstant.TOKEN,"").length()==0){
			token = paramBean.getStr("SESSIONID");
		}else{
			token = paramBean.getStr(MethodConstant.TOKEN,"");
		}
		String userId = new String(decode(paramBean.getStr("USERID")));
		DynaBean session = new DynaBean("SYS_SESSION", "and USERID='" + userId + "'");
		List<DynaBean> userSessionList = dao.findWithQueryNoCache(session);
		// 曾经登录过
		if (userSessionList.size() > 0) {
			session = userSessionList.get(0);
		}
		session.setStr(MethodConstant.TOKEN, token);
		session.setStr("USERID", userId);
		session.setStr("SESSIONID", paramBean.getStr("SESSIONID"));
		session.setStr("STATUS", "Y");// 记录在线状态
		session.setLong("LOGTIME", DateUtils.getTimestamp());// 记录登录时间
		session.setStr(BeanUtils.KEY_TABLE_CODE, "SYS_SESSION");
		if (userSessionList.size() > 0) {
			session.setStr(BeanUtils.KEY_WHERE, "and ID='"+session.getStr("ID")+"'");
			dao.updateOne(session);
		} else {
			dao.insertOne(session);
		}
		return session.getStr("SESSIONID");
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月21日 下午5:36:48 方法说明: 如果当前传过来的参数token新获取的,
	 *          那么需要自动获取原来的token,客户端需要更新本地的token
	 * @param dao
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 * @param paramList
	 * @param result
	 * @return
	 */
	public static boolean checkUserPassword(BaseDao dao, DynaBean paramBean, DynaBean productBean, DynaBean appBean,
			List<DynaBean> paramList, DynaBean result) {
		DynaBean dynaBean;
		try {
			dynaBean = new DynaBean("SYS_USER",
					"and upper(PASSWORD)=upper('" + MD5Util.MD5(new String(decode(paramBean.getStr("PASSWORD", "")), "UTF-8"))
							+ "') and USERCODE='" + new String(decode(paramBean.getStr("USERID", "")), "UTF-8") + "' ");
			List<DynaBean> userBeanList = dao.findWithQueryNoCache(dynaBean);
			if (userBeanList.size() == 0) {
				result.setStr(MethodConstant.CODE, "61001");// 密码不正确
				result.set(MethodConstant.MSG, getBackName(dao, "61001"));
				return false;
			} else {
				dynaBean = userBeanList.get(0);
				// 判断是否注销后重新登录的用户,如果是则变更token为最新的.
				Map map = dynaBean.getValues();
				map.remove("PASSWORD");
				map.remove(BeanUtils.KEY_WHERE);
				map.remove(BeanUtils.KEY_TABLE_CODE);
				result.set(MethodConstant.DATA, map);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月21日 上午9:13:11 方法说明: 注册校验 重复注册 用户名重复
	 * @param dao
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 * @param paramList
	 * @param result
	 * @param isUpdate
	 *            是否是修改密码
	 * @return
	 */
	public static boolean checkTelsms(BaseDao dao, DynaBean paramBean, DynaBean productBean, DynaBean appBean,
			List<DynaBean> paramList, DynaBean result) {
		// 校验验证码是否正确
		if (dao.findCountWithQuery(new DynaBean("SYS_TELSMS",
				"and TOKEN='" + paramBean.getStr(MethodConstant.TOKEN) + "' and MOBILEPHONE='"
						+ paramBean.getStr("MOBILEPHONE") + "'" + " and SMSCODE='" + paramBean.getStr("AUTHCODE") + "'"
						+ " and RANDOM='" + paramBean.getStr("RANDOM", "") + "'")) == 0) {
			result.set(MethodConstant.CODE, "61005");// 手机验证码错误
			result.set(MethodConstant.MSG, getBackName(dao, "61005"));
			return false;
		}
		return true;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月21日 上午9:13:11 方法说明: 注册校验 重复注册 用户名重复
	 * @param dao
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 * @param paramList
	 * @param result
	 * @param isUpdate
	 *            是否是修改密码
	 * @return
	 */
	public static boolean checkUser(BaseDao dao, DynaBean paramBean, DynaBean productBean, DynaBean appBean,
			List<DynaBean> paramList, DynaBean result, boolean isUpdate) {
		// 校验验证码是否正确
		//获取系统参数，校验是否需要短信验证码注册 默认需要短信验证码
		Map telParam = (Map)CacheUtil.getLocKeyTypeCache(dao.getReadCache(), CacheUtil.SYSPARAM, "SYS_REGISTER_TELSMS");
		boolean flag = false;
		if(telParam!=null){
			if(telParam.containsKey("PARAMVALUE")){
				flag = telParam.get("PARAMVALUE").toString().equals("Y");
			}
		}
		if (flag&&dao.findCountWithQuery(new DynaBean("C_TELSMS","and TOKEN='" + paramBean.getStr(MethodConstant.TOKEN) + "' and MOBILEPHONE='"
						+ paramBean.getStr("MOBILEPHONE") + "'" + " and SMSCODE='" + paramBean.getStr("AUTHCODE") + "'"
						+ " and RANDOM='" + paramBean.getStr("RANDOM", "") + "'")) == 0) {
			result.set(MethodConstant.CODE, "61005");// 手机验证码错误
			result.set(MethodConstant.MSG, getBackName(dao, "61005"));
			return false;
		}
		// 找到此用户
		List<DynaBean> userList = dao.findWithQueryNoCache(new DynaBean("C_USER", " and TEL='" + paramBean.getStr("MOBILEPHONE", "") + "'"));
		if (!isUpdate) {
			if (dao.findCountWithQuery(new DynaBean("C_USER", " and (TEL='" + paramBean.getStr("MOBILEPHONE", "") + "' or USERID='" + paramBean.getStr("USERID", "") + "')")) > 0) {
				result.set(MethodConstant.CODE, "61004");// 此用户号已经注册
				result.set(MethodConstant.MSG, getBackName(dao, "61004"));
				return false;
			}
			if (userList.size() > 0) {
				result.set(MethodConstant.CODE, "61002");// 存在此用户异常
				result.set(MethodConstant.MSG, getBackName(dao, "61002"));
				return false;
			}
		} else {
			if (userList.size() == 0) {
				result.set(MethodConstant.CODE, "61006");// 此用户没有注册,请注册
				result.set(MethodConstant.MSG, getBackName(dao, "61006"));
				return false;
			}
		}
		return true;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月22日 下午2:47:01 说明:手机号码校验
	 * @param dao
	 * @param paramBean
	 * @param productBean
	 * @param appBean
	 * @param paramList
	 * @param result
	 * @return
	 */
	public static boolean checkTel(BaseDao dao, DynaBean paramBean, DynaBean productBean, DynaBean appBean,
			List<DynaBean> paramList, DynaBean result) {
		String telnum = paramBean.getStr("MOBILEPHONE", "");
		if (telnum.length() != 11) {
			result.setStr(MethodConstant.CODE, "40040");// 手机号必须为11位数字
			result.setStr(MethodConstant.MSG, getBackName(dao, "40040"));
			return false;
		}
		return true;
	}

	/**
	 * 校验产品PID合法性
	 * 
	 * @param dao
	 * @param paramBean
	 * @param result
	 * @return
	 */
	public static boolean checkPid(BaseDao dao, DynaBean paramBean, DynaBean result) {
		DynaBean implBean = new DynaBean("SYS_INTERFACE");
		implBean.setStr(BeanUtils.KEY_WHERE, " and PID='" + paramBean.getStr(MethodConstant.PID, "") + "'");
		if (dao.findCountWithQuery(implBean) == 0) {
			result.setStr(MethodConstant.CODE, "40001");// 不合法的产品凭证
			result.setStr(MethodConstant.MSG, getBackName(dao, "40001"));
			return false;
		}
		return true;
	}

	/**
	 * 校验应用AID合法性 包含子表AID的子表合法性,校验是否存在子表关系
	 * 
	 * @param dao
	 * @param paramBean
	 * @param result
	 * @return
	 */
	public static boolean checkAid(BaseDao dao, DynaBean paramBean, DynaBean result) {
		DynaBean implBean = new DynaBean("SYS_APP");
		implBean.setStr(BeanUtils.KEY_WHERE, " and APPID in (select APPID from SYS_INTERFACE where PID='"
				+ paramBean.getStr(MethodConstant.PID, "") + "' and REQURL='" + paramBean.getStr(MethodConstant.AID, "") + "')");
		if (dao.findCountWithQuery(implBean) == 0) {
			result.setStr(MethodConstant.CODE, "40003");// 不合法的AID
			result.setStr(MethodConstant.MSG, getBackName(dao, "40003"));
			return false;
		}
		// 子表id的校验
		switch (paramBean.getStr(MethodConstant.ACT)) {
		case MethodConstant.ACT_LISTDETAIL:
		case MethodConstant.ACT_INFOLIST:
			if (paramBean.getStr(MethodConstant.ACT_LISTDETAIL, "").length() != 0) {// 获取子表AID及页数,排序号
				try {
					// 转换成集合
					JSONArray cids = JSONArray.parseArray(paramBean.getStr(MethodConstant.ACT_LISTDETAIL));
					String aids = "";
					for (int i = 0; i < cids.size(); i++) {
						JSONObject aidJson = cids.getJSONObject(i);
						aids += aidJson.getString("AID") + "','";
					}
					if (aids.length() != 0) {
						implBean.setStr(BeanUtils.KEY_WHERE,
								// " and APPID in (select APPID from
								// SYS_APPLINKS where APPID in (select APPID
								// from SYS_INTERFACE where REQURL in ('" + aids
								// + "') and PID='"
								" and APPID in (select APPID from SYS_INTERFACE where REQURL in ('" + aids
										+ "') and PID='" + paramBean.getStr(MethodConstant.PID, "") + "')");
						if (dao.findCountWithQuery(implBean) < cids.size()) {
							result.setStr(MethodConstant.CODE, "40034");// 不合法的子表AID
							result.setStr(MethodConstant.MSG,
									getBackName(dao, "40034") + ",参考子表AID:" + aids.replaceAll("'", "") + "确认是否存在关系");
							return false;
						}
					} else {
						result.setStr(MethodConstant.CODE, "40034");// 不合法的子表AID
						result.setStr(MethodConstant.MSG, getBackName(dao, "40034") + ",参考子表AID:" + aids.replaceAll("'", ""));
						return false;
					}
					return true;
				} catch (Exception ex) {
					result.setStr(MethodConstant.CODE, "40035");// 不合法的参数
					result.setStr(MethodConstant.MSG, getBackName(dao, "40035"));
					return false;
				}

			} else {
				result.setStr(MethodConstant.CODE, "40035");// 不合法的参数
				result.setStr(MethodConstant.MSG, getBackName(dao, "40035") + "缺失参数:LISTDETAIL");
				return false;
			}
		default:
			return true;
		}
	}

	/**
	 * 校验产品应用的操作ACT合法性
	 * 
	 * @param dao
	 * @param paramBean
	 * @param result
	 * @return
	 */
	public static boolean checkAct(BaseDao dao, DynaBean paramBean, DynaBean result) {
		DynaBean implBean = new DynaBean("SYS_INTERFACE");
		implBean.setStr(BeanUtils.KEY_WHERE, "and REQTYPE='" + paramBean.getStr(MethodConstant.ACT, "") + "' and REQURL='"
				+ paramBean.getStr(MethodConstant.AID, "") + "' and PID='" + paramBean.getStr(MethodConstant.PID, "") + "'");
		if (dao.findCountWithQuery(implBean) == 0) {
			result.setStr(MethodConstant.CODE, "40004");// 不合法的操作凭证
			result.setStr(MethodConstant.MSG, getBackName(dao, "40004"));
			return false;
		}
		return true;
	}

	// 校验接口合法性
	public static boolean checkParams(BaseDao dao, DynaBean paramBean, JSONArray dataJson, DynaBean interfaceBean,
			List<DynaBean> paramList, DynaBean result) {
		if (dataJson == null) {
			result.setStr(MethodConstant.CODE, "40005");// 不合法的消息体
			result.setStr(MethodConstant.MSG, getBackName(dao, "40005"));
			return false;
		}				
		// 缺少LAT 41002
		if (interfaceBean.getStr("NEEDLAT", "N").equals("Y") && paramBean.getStr(MethodConstant.LAT, "").length() == 0) {
			result.setStr(MethodConstant.CODE, "41002");// 缺少LAT
			result.setStr(MethodConstant.MSG, getBackName(dao, "41002") + ":LAT");
			return false;
		}
		// 缺少LNG 41002
		if (interfaceBean.getStr("NEEDLNG", "N").equals("Y") && paramBean.getStr(MethodConstant.LNG, "").length() == 0) {
			result.setStr(MethodConstant.CODE, "41002");// 缺少LNG
			result.setStr(MethodConstant.MSG, getBackName(dao, "41002") + ":LNG");
			return false;
		}
		// 校验TOKEN的合法性
//		if (interfaceBean.getStr("NEEDUSERTOKEN", "N").equals("Y")) {
//			List<DynaBean> userTokenList = dao.findWithQueryNoCache(new DynaBean("SYS_PRODUCTUSER", "and TOKEN='" + paramBean.getStr(TOKEN) + "'"));
//			// 不存在此用户token
//			if (userTokenList.size() == 0) {
//				result.setStr(CODE, "46004");// 非法用户token
//				result.setStr(MSG, getBackName(dao, "46004"));
//				return false;
//			}
//			boolean rtn = false;
//			for (int i = 0; i < userTokenList.size(); i++) {
//				DynaBean userToken = (DynaBean) userTokenList.get(i);
//				if (!userToken.getStr("PID", "").equals(paramBean.getStr(PID))) {
//					continue;
//				}
//				// 用户受限，可能是违规后接口被封禁
//				if (!userToken.getStr("ENABLE", "").equals("Y")) {
//					result.setStr(CODE, "50002");// 用户受限，可能是违规后接口被封禁
//					result.setStr(MSG, getBackName(dao, "50002"));
//					rtn = false;
//				}
//				rtn = true;
//			}
//			// 用户受限或未对此pid授权
//			if (!rtn) {
//				if (result.getStr(CODE, "").length() == 0) {
//					result.setStr(CODE, "50001");// 未被授权的用户api
//					result.setStr(MSG, getBackName(dao, "50001"));
//				}
//				return false;
//			}
//		}
		// 缺少参数检查
		for (DynaBean inparam : paramList) {
			// 只检查入口参数
			if (inparam.getStr("GETPUT", "PUT").equals("PUT")) {
				continue;
			}
			String paramName = inparam.getStr("PARAMNAME", "").trim();
			// 是否必须的参数
			boolean isnull = inparam.getStr("ISNULL", "N").equals("Y");
			// 必须要的参数不能为空
			if (isnull) {
				// 不能为空字符串
				if (paramBean.getStr(paramName, "").length() == 0) {
					// 循环检查消息体 查看参数是否合法
					for (int i = 0; i < dataJson.size(); i++) {
						JSONObject json = dataJson.getJSONObject(i);
						if (!json.containsKey(paramName)) {
							result.setStr(MethodConstant.CODE, "41002");
							result.set(MethodConstant.MSG, getBackName(dao, "41002") + ":" + paramName);
							return false;
						} else if (json.getString(paramName).length() == 0) {
							result.setStr(MethodConstant.CODE, "41003");
							result.set(MethodConstant.MSG, getBackName(dao, "41003") + ":" + paramName);
							return false;
						}
					}
				}
			}
			// 不合法的偏移量
			if (paramBean.getStr(MethodConstant.ACT, "").equals("LISTINFO")) {
				if (paramBean.getInt("PAGESIZE", 0) > 11000) {
					result.setStr(MethodConstant.CODE, "40116");
					result.set(MethodConstant.MSG, getBackName(dao, "41116"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年3月7日 下午5:17:01 方法说明: 判断是否合法,如果合法则把单对象转换为数组
	 * @param str
	 *
	 * @return
	 */
	public static boolean checkData(DynaBean paramBean, DynaBean result, BaseDao dao) {
		JSONArray json = new JSONArray();
		JSONObject objJson = null;
		try {
			objJson = JSONObject.parseObject(paramBean.getStr(MethodConstant.DATA, ""));
			json.add(objJson);
		} catch (Exception ex) {
			try {
				json = JSONArray.parseArray(paramBean.getStr(MethodConstant.DATA, ""));
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
		}
		if (json == null || json.size() == 0) {
			result.setStr(MethodConstant.CODE, "61001");
			result.set(MethodConstant.MSG, getBackName(dao, "61001"));
			return false;
		} else {
			paramBean.set(MethodConstant.DATA, json);
			return true;
		}
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年3月7日 下午5:17:01 方法说明:
	 *          判断上传参数是否合法,是否有此元数据,是否存在此字段,是否有这条记录
	 * @param str
	 *
	 * @return
	 */
	public static boolean checkUpload(BaseService service, DynaBean paramBean, DynaBean result) {
		// 此元数据ID值,如CUSER用户元数据
		String sourceAid = paramBean.getStr(MethodConstant.ACT_SOURCEAID);
		// 此元数据对应的数据ID字段的值
		String sourceId = paramBean.getStr(MethodConstant.ACT_SOURCEID);
		// 此元数据绑定的字段
		String sourceField = paramBean.getStr(MethodConstant.ACT_SOURCEFIELD);
		// 此上传文件的类型
		String fileType = paramBean.getStr(MethodConstant.ACT_UPLOADFILETYPE);
		boolean rtn = true;
		// 校验文件类型的支持
		switch (fileType) {
		case "DOC":// word文件
		case "XLSX":// EXCEL文件
		case "PDF":// PDF文件
		case "AUDIO":// 音频文件
		case "TXT":// 文本文件
		case "VIDEO":// 视频文件
		case "PIC":// 图片
		case "THUMBNAIL":// 缩略图
		case "ICO":// ICON图标
		case "LOG":// 日志文件
		case "CODE":// 二维码
		case "OTHER":// 其它文件类型
			break;
		default:
			result.setStr(MethodConstant.CODE, "9001008");// 不合法的文件类型凭证
			result.setStr(MethodConstant.MSG, getBackName(service.getBaseDao(), "9001008")
					+ ",参考文件类型:DOC,XLSX,PDF,AUDIO,TXT,VIDEO,PIC,THUMBNAIL,ICO,LOG,QRCODE,OTHER");
			rtn = false;
		}
		// 判断是否存在元数据编码,是否存在元数据字段,是否存在此id对应的值
//		List<DynaBean> dList = service.getBaseDao().findWithQueryNoCache(new DynaBean("SYS_APP", " and APPID in (select APPID from SYS_INTERFACE where PID='"	+ paramBean.getStr(PID, "") + "' and REQURL='" + sourceAid + "')"));
//		if (dList.size() == 0) {
//			result.setStr(CODE, "40003");// 不合法的AID
//			result.setStr(MSG, getBackName(service.getBaseDao(), "40003") + ",参考:元数据" + sourceAid);
//			rtn = false;
//		}
//		if (dList.size() > 0) {
//			// 获取元数据对应的记录
//			DynaBean appBean = dList.get(0);
//			if (service.getBaseDao().findCountWithQuery(new DynaBean(appBean.getStr("MAINTABLE", " and ID='" + sourceId + "'"))) == 0) {
//				result.setStr(CODE, "40117");// 不存在此id的记录
//				result.setStr(MSG, getBackName(service.getBaseDao(), "40117") + ",参考:ID值'" + sourceId + "'");
//				rtn = false;
//			}
//		}
		if (service.getBaseDao().findCountWithQuery(new DynaBean("SYS_APPFIELDS"," and FIELDCODE='" + sourceField + "' and APPID in (select APPID from SYS_INTERFACE where PID='"
						+ paramBean.getStr(MethodConstant.PID, "") + "' and REQURL='" + sourceAid + "')")) == 0) {
			result.setStr(MethodConstant.CODE, "40036");// 不合法的元数据字段
			result.setStr(MethodConstant.MSG, getBackName(service.getBaseDao(), "40036") + ",参考:元数据字段" + sourceField);
			rtn = false;
		}
		return rtn;
	}
}
