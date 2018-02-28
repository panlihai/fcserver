package cn.pcorp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import cn.pcorp.controllor.util.MethodConstant;
import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.BaseService;
import cn.pcorp.service.system.SysServer;

/** 
* @author panlihai E-mail:18611140788@163.com 
* @version 创建时间：2016年1月8日 上午10:43:15 
* 类说明: 
*/
/**
 * @author Administrator
 *
 */
public class LogUtil {
	public final static String PROVINCE = "PROVINCE";
	public final static String CITY = "CITY";
	public final static String HOST = "CLIENTHOST";
	public final static String LAT = "LAT";
	public final static String LNG = "LNG";
	public final static String LOGTIME = "LOGTIME";

	/**
	 * 接口详情日志
	 * 
	 * @param request
	 * @param log
	 */
	public static DynaBean logRequest(DynaBean paramBean) {
		DynaBean logBean = new DynaBean("SYS_LOG");
		logBean.setStr("HOST", paramBean.getStr(HOST));
		logBean.set(LOGTIME, DateUtils.getTimestamp());
		logBean.setStr("PID", paramBean.getStr(MethodConstant.PID));
		logBean.setStr("ACTTYPE", paramBean.getStr(MethodConstant.ACT));
		logBean.setStr("LOGTYPE", "REQUEST");
		logBean.setStr("LOGID", SyConstant.getUUID());
		logBean.setStr("USERTOKEN", paramBean.getStr("TOKEN"));
		logBean.setStr("CONTENT", paramBean.toJsonString().replaceAll("\"", ""));
		logBean.setStr(LAT, paramBean.getStr(LAT));
		logBean.setStr(LNG, paramBean.getStr(LNG));
		return logBean;
	}

	/**
	 * 接口写日志
	 * 
	 * @param request
	 * @param log
	 */
	public static void logAppDetail(final DynaBean paramBean, final String appId, final DynaBean interfaceBean,
			final Object result, final String tableName) {
//		new Thread() {
//			public void run() {
//				DynaBean logBean = new DynaBean("SYS_LOG_" + tableName);
//				if (paramBean.getStr(LAT, "").length() == 0 && paramBean.getStr(LNG, "").length() == 0) {
//					Map<String, String> map = getLngLatByHostIp(paramBean.getStr(HOST));
//					logBean.setStr(LAT, map.get(LAT).toString());
//					logBean.setStr(LNG, map.get(LNG).toString());
//					logBean.setStr(PROVINCE, map.get(PROVINCE).toString());
//					logBean.setStr(CITY, map.get(CITY).toString());
//				} else {
//					Map<String, String> map = getProvinceCityByLNGLAT(paramBean.getStr(LNG), paramBean.getStr(LAT));
//					logBean.setStr(LAT, paramBean.getStr(LAT));
//					logBean.setStr(LNG, paramBean.getStr(LNG));
//					logBean.setStr(PROVINCE, map.get(PROVINCE).toString());
//					logBean.setStr(CITY, map.get(CITY).toString());
//				}
//				List saveList = null;
//				StringBuffer dataIds = new StringBuffer("");
//				if (result != null) {
//					switch (tableName) {
//					case ApiUtil.ACT_INFO:
//					case ApiUtil.ACT_INFOLIST:
//					//case ApiUtil.ACT_LISTINFO:// 列表查询
//					//case ApiUtil.ACT_LISTDETAIL:// 列表及列表的子表
//					case ApiUtil.ACT_UPDATE:
//						saveList = (List) result;
//						if(saveList.size()>0){
//							if(saveList.get(0) instanceof DynaBean){
//								for(int i=0;i<saveList.size();i++){
//									DynaBean dyBean = (DynaBean)saveList.get(i);
//									dataIds.append(dyBean.getStr("ID","")).append(",");
//								}								
//							}else{
//								for(int i=0;i<saveList.size();i++){
//									Map map =(Map)saveList.get(i);
//									Object id = map.get("ID");
//									if(id!=null){
//										dataIds.append(id.toString()).append(",");
//									}
//								}
//							}
//						}						
//						break;
//					case ApiUtil.ACT_CREATE:
//						List<DynaBean> saveList1 = (List) result;
//						for (DynaBean bean : saveList1) {
//							dataIds.append(bean.getStr("ID", "")).append(",");
//						}
//						break;
//					case ApiUtil.ACT_DELETE:
//						DynaBean deleteBean = (DynaBean) result;
//						dataIds.append(deleteBean.getStr("ID", "")).append(",");
//						break;
//						
//					}
//
//				}
//				if (dataIds.length() > 0) {
//					dataIds.setLength(dataIds.length() - 1);					
//					logBean.setStr("DATAID", dataIds.toString());
//				}
//				logBean.setStr("APPID", appId);
//				logBean.setStr("HOST", paramBean.getStr(HOST));
//				logBean.set(LOGTIME, DateUtils.getTimestamp());
//				logBean.setStr("PID", paramBean.getStr(ApiUtil.PID));
//				logBean.setStr("ACTTYPE", paramBean.getStr(ApiUtil.ACT));
//				logBean.setStr("LOGTYPE", "REQUEST");
//				logBean.setStr("LOGID", SyConstant.getUUID());
//				logBean.setStr("USERTOKEN", paramBean.getStr("TOKEN"));
//				String value = paramBean.toJsonString().replaceAll("\"", "");
//				if(value.length()>2000){
//					value = value.substring(0, 2000);
//				}
//				logBean.setStr("CONTENT", value);
//				writeBean(logBean);
//				// 积分
//				String dataId = logBean.getStr("DATAID", "");
//				writheIntegral(paramBean, interfaceBean, dataId);
//				switch (tableName) {
//				case ApiUtil.ACT_INFO:
//				case ApiUtil.ACT_INFOLIST:
//					setPageView(paramBean, dataId);
//				}
//			}
//		}.start();
	}

	private static void setPageView(DynaBean paramBean, String dataId) {
		BaseDao dao = (BaseDao) SysServer.getServer().getBean("baseDao");
		if (dataId == null && dataId.length() == 0) {
			return;
		}
		List<DynaBean> dList = dao
				.findWithQueryNoCache(new DynaBean("SYS_PAGEVIEW", " and SOURCEAID='" + paramBean.getStr(MethodConstant.AID)
						+ "' and SOURCEID='" + dataId + "' and PID='" + paramBean.getStr("PID") + "'"));
		if (dList.size() == 0) {
			DynaBean itgr = new DynaBean("SYS_PAGEVIEW");
			itgr.set("SOURCEAID", paramBean.get(MethodConstant.AID));
			if(dataId.length()>40){
				return;
			}
			itgr.setStr("SOURCEID", dataId);
			itgr.set("PID", paramBean.get("PID"));
			itgr.set("NUM", 1);
			dao.insertOne(itgr);
		} else {
			DynaBean itgr = dList.get(0);
			itgr.setDouble("NUM", itgr.getDouble("NUM") + 1);
			itgr.set(BeanUtils.KEY_TABLE_CODE, "SYS_PAGEVIEW");
			itgr.set(BeanUtils.KEY_WHERE, " and ID='" + itgr.getStr("ID") + "'");
			dao.updateOne(itgr);
		}
	}

	/**
	 * 接口写日志
	 * 
	 * @param request
	 * @param log
	 */
	public static void logResponse(final DynaBean parentLog, final DynaBean log) {
		new Thread() {
			public void run() {
				if (parentLog.getStr(LAT, "").length() == 0 && parentLog.getStr(LNG, "").length() == 0) {
					Map<String, String> map = getLngLatByHostIp(parentLog.getStr(HOST));
					parentLog.setStr(LAT, map.get(LAT).toString());
					parentLog.setStr(LNG, map.get(LNG).toString());
					parentLog.setStr(PROVINCE, map.get(PROVINCE).toString());
					parentLog.setStr(CITY, map.get(CITY).toString());
				} else {
					Map<String, String> map = getProvinceCityByLNGLAT(parentLog.getStr(LNG), parentLog.getStr(LAT));
					parentLog.setStr(LAT, parentLog.getStr(LAT));
					parentLog.setStr(LNG, parentLog.getStr(LNG));
					parentLog.setStr(PROVINCE, map.get(PROVINCE).toString());
					parentLog.setStr(CITY, map.get(CITY).toString());
				}
				// 加入队列
				writeBean(parentLog);
				DynaBean logBean = new DynaBean("SYS_LOG");
				logBean.setStr("HOST", parentLog.getStr("HOST"));
				logBean.set(LOGTIME, DateUtils.getTimestamp());
				logBean.setStr("PID", parentLog.getStr("PID"));
				logBean.setStr("ACTTYPE", parentLog.getStr("ACTTYPE"));
				logBean.setStr("APPID", parentLog.getStr("APPID"));
				logBean.setStr("LOGID", SyConstant.getUUID());
				logBean.setStr("USERTOKEN", parentLog.getStr("TOKEN"));
				logBean.setStr("LOGTYPE", "RESPONSE");
				String data = log.toJsonString().replaceAll("\"", "");
				if(data.length()>2000){
					data = data.substring(0,2000);					
				}
				logBean.setStr("CONTENT", data);
				logBean.setStr(LAT, parentLog.getStr(LAT));
				logBean.setStr(LNG, parentLog.getStr(LNG));
				logBean.setStr(PROVINCE, parentLog.getStr(PROVINCE));
				logBean.setStr(CITY, parentLog.getStr(CITY));
				// 加入队列
				writeBean(logBean);
			}
		}.start();
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月20日 上午11:05:02 方法说明:
	 * @param dao
	 * @param obj
	 */
	private static void writeBean(DynaBean obj) {
		List<DynaBean> objList = new ArrayList<DynaBean>();
		objList.add(obj);
		SysServer.getServer().getQueue().offer(objList);
	}

	/**
	 * 根据ip地址获取经纬度
	 * 
	 * @param ip
	 * @return
	 */
	private static Map<String, String> getLngLatByHostIp(String ip) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject json = readJsonFromUrl(
					"http://api.map.baidu.com/location/ip?ak=F454f8a5efe5e577997931cc01de3974&ip=" + ip);
			if (json.containsKey("content")) {
				JSONObject contentJson = json.getJSONObject("content");
				if (contentJson.containsKey("address_detail")) {
					JSONObject addJson = contentJson.getJSONObject("address_detail");
					if (addJson.containsKey("province")) {
						map.put(PROVINCE, addJson.getString("province"));
					}
					if (addJson.containsKey("city")) {
						map.put(CITY, addJson.getString("city"));
					}
				}
				if (contentJson.containsKey("point")) {
					JSONObject point = contentJson.getJSONObject("point");
					if (point.containsKey("y")) {
						map.put(LNG, point.getString("y"));

					}
					if (point.containsKey("x")) {
						map.put(LAT, point.getString("x"));
					}
				}
			} else {
				map.put(LNG, "");
				map.put(LAT, "");
				map.put(PROVINCE, "");
				map.put(CITY, "");
			}

		} catch (JSONException | IOException e) {
			e.printStackTrace();
			map.put(LNG, "");
			map.put(LAT, "");
			map.put(PROVINCE, "");
			map.put(CITY, "");
		}
		return map;
	}

	/**
	 * 根据经纬度获取省份及城市
	 * 
	 * @param ip
	 * @return
	 */
	private static Map getProvinceCityByLNGLAT(String lng, String lat) {
		// String key = "f247cdb592eb43ebac6ccd27f796e2d2";
		// String url = String
		// .format("http://api.map.baidu.com/geocoder?address=%s&output=json&key=%s",
		// address, key);//获取经纬度
		Map<String, String> map = new HashMap<String, String>();
		String str = "http://api.map.baidu.com/geocoder/v2/?ak=F454f8a5efe5e577997931cc01de3974&location=" + lat + ","
				+ lng + "&output=json&pois=1";// 获取地址
		try {
			JSONObject json = readJsonFromUrl(str);
			if (json.containsKey("result")) {
				JSONObject contentJson = json.getJSONObject("result");
				if (contentJson.containsKey("addressComponent")) {
					JSONObject addJson = contentJson.getJSONObject("addressComponent");
					if (addJson.containsKey("province")) {
						map.put(PROVINCE, addJson.getString("province"));
					}
					if (addJson.containsKey("city")) {
						map.put(CITY, addJson.getString("city"));
					}
				}
			} else {
				map.put(PROVINCE, "");
				map.put(CITY, "");
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			map.put(PROVINCE, "");
			map.put(CITY, "");
		}
		return map;
	}

	public static void main(String args[]) {
		// 这里调用百度的ip定位api服务 详见
		// http://api.map.baidu.com/lbsapi/cloud/ip-location-api.htm
		JSONObject json;
		try {
			json = readJsonFromUrl(
					"http://api.map.baidu.com/location/ip?ak=F454f8a5efe5e577997931cc01de3974&ip=202.198.16.3");
			System.out.println(json.toString());
			json = readJsonFromUrl(
					"http://api.map.baidu.com/geocoder/v2/?ak=F454f8a5efe5e577997931cc01de3974&location=39.975369,116.250246&output=json&pois=1");
			System.out.println(json.toString());

			// {"status":0,"result":
			// {"location":
			// {"lng":104.04701,"lat":30.548397},
			// "formatted_address":"四川省成都市武侯区天府四街",
			// "business":"",
			// "addressComponent":
			// {"city":"成都市",
			// "country":"中国",
			// "direction":"",
			// "distance":"",
			// "district":"武侯区",
			// "province":"四川省",
			// "street":"天府四街",
			// "street_number":"",
			// "country_code":0
			// }
			// }
			// }
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = JSONObject.fromObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param dao
	 * @param paremBean
	 * @param interfaceBean
	 */
	private static void writheIntegral(DynaBean paramBean, DynaBean interfaceBean, String dataId) {

		String token = paramBean.getStr("TOKEN");
		BaseService service = (BaseService) SysServer.getServer().getBean("baseService");
		DynaBean userBean = service.getUserBeanByToken(token);
		if (userBean == null) {
			return;
		}
		DynaBean accountBean = (DynaBean) userBean.get("CACCOUNT");
		String integraltype = interfaceBean.getStr("INTEGRALTYPE", "");
		switch (integraltype) {
		// 基础积分模型
		case "0":// 用户独立积分
			int v = interfaceBean.getInt("USERINTEGRALVALUE", 0);
			if (v != 0) {
				writeBean(getIntegralBean(paramBean, interfaceBean.getStr("IMPLID"), v, "CUSER", userBean.getStr("ID"),
						integraltype));
			}
			break;
		case "1":// 组织独立积分
			v = interfaceBean.getInt("CORPINTEGRALVALUE", 0);
			if (v != 0 && accountBean != null) {
				writeBean(getIntegralBean(paramBean, interfaceBean.getStr("IMPLID"), v, "CACCOUNT",
						accountBean.getStr("ID"), integraltype));
			}
			break;
		case "2":// 用户及组织分别积分
			v = interfaceBean.getInt("USERINTEGRALVALUE", 0);
			if (v != 0) {
				writeBean(getIntegralBean(paramBean, interfaceBean.getStr("IMPLID"), v, "CUSER", userBean.getStr("ID"),
						integraltype));
			}
			v = interfaceBean.getInt("CORPINTEGRALVALUE", 0);
			if (v != 0 && accountBean != null) {
				writeBean(getIntegralBean(paramBean, interfaceBean.getStr("IMPLID"), v, "CACCOUNT",
						accountBean.getStr("ID"), integraltype));
			}
			break;
		}
		int v = interfaceBean.getInt("INTEGRALVALUE", 0);
		if (v != 0 && dataId.length() != 0) {
			writeBean(getIntegralBean(paramBean, interfaceBean.getStr("IMPLID"), v, paramBean.getStr(MethodConstant.AID), dataId,
					"999"));
		}
	}

	/**
	 * @author panlihai
	 * @return
	 */
	private static DynaBean getIntegralBean(DynaBean paramBean, String integralcode, double score, String sourceAid,
			String sourceId, String integraltype) {
		DynaBean dynaBean = new DynaBean("SYS_INTEGRALDETAIL");
		dynaBean.setStr("TOKEN", paramBean.getStr("TOKEN"));
		dynaBean.set("INTEGRALTIME", DateUtils.getTimestamp());
		dynaBean.setStr("INTEGRALCODE", integralcode);
		dynaBean.set("SCORE", score);
		// dynaBean.setStr("REMARK", "");
		dynaBean.setStr("STATUS", "Y");
		dynaBean.setStr("PID", paramBean.getStr("PID"));
		dynaBean.setStr("SOURCEID", sourceId);
		dynaBean.setStr("SOURCEAID", sourceAid);
		dynaBean.setStr("INTEGRALTYPE", integraltype);
		setIntegral(dynaBean);
		return dynaBean;
	}

	/**
	 * 
	 * @param dynaBean。
	 */
	private static void setIntegral(DynaBean dynaBean) {
		BaseDao dao = (BaseDao) SysServer.getServer().getBean("baseDao");
		List<DynaBean> dList = dao.findWithQueryNoCache(
				new DynaBean("SYS_INTEGRAL", " and SOURCEAID='" + dynaBean.getStr("SOURCEAID") + "' and SOURCEID='"
						+ dynaBean.getStr("SOURCEID") + "' and PID='" + dynaBean.getStr("PID") + "'"));
		if (dList.size() == 0) {
			DynaBean itgr = new DynaBean("C_INTEGRAL");
			itgr.set("SOURCEAID", dynaBean.get("SOURCEAID"));
			itgr.set("SOURCEID", dynaBean.get("SOURCEID"));
			itgr.set("PID", dynaBean.get("PID"));
			itgr.set("SCORE", dynaBean.get("SCORE"));
			dao.insertOne(itgr);
		} else {
			DynaBean itgr = dList.get(0);
			itgr.setDouble("SCORE", itgr.getDouble("SCORE") + dynaBean.getDouble("SCORE"));
			itgr.set(BeanUtils.KEY_TABLE_CODE, "C_INTEGRAL");
			itgr.set(BeanUtils.KEY_WHERE, " and ID='" + itgr.getStr("ID") + "'");
			dao.updateOne(itgr);
		}
	}

}
