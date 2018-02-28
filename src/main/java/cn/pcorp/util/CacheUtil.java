package cn.pcorp.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.Cache;
import cn.pcorp.model.DynaBean;
import cn.pcorp.model.MemaCache;
import cn.pcorp.util.sql.SqlBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/** 
 * @author panlihai E-mail:18611140788@163.com 
 * @version 创建时间：2015年12月30日 下午5:22:36 
 * 类说明: 
 */
/**
 * @author Administrator
 * 
 */
public class CacheUtil {

	private static Logger logger = Logger.getLogger(CacheUtil.class);	
	// 系统参数后缀
	public final static String SYSPARAM = "SYSPARAM";
	// 数据表结构后缀
	public final static String SYSTABLEDEF = "SYSTABLEDEF";
	// 数据表SYS_MENU
	public final static String SYSMENU = "SYSMENU";
	// 数据表SYSAPP
	public final static String SYSAPP = "SYSAPP";
	// 应用程序表字段结构后缀
	public final static String SYSAPPFIELDS = "SYSAPPFIELDS";
	// 应用程序表SYS_MENU
	public final static String SYSAPPBUTTONS = "SYSAPPBUTTONS";
	// 数据字典结构后缀
	public final static String SYSDIC = "SYSDIC";
	// 数据字典明细结构后缀
	public final static String SYSDICDETAIL = "SYSDICDETAIL";
	// 数据动态字典结构后缀
	public final static String SYSDICAPP = "SYSDICAPP";
	// 数据动态字典明细结构后缀
	public final static String SYSDICAPPDETAIL = "SYSDICAPPDETAIL";
	// SYSAPPLINKS 关联功能
	public final static String SYSAPPLINKS = "SYSAPPLINKS";
	// SYSROLE 角色
	public final static String SYSROLE = "SYSROLE";
	// SYSPRODUCT 产品
	public static final String SYSPRODUCT = "SYSPRODUCT";

	
	/**
	 * 适合批量写缓存，不重复从池中那jedis
	 * @param writeCache
	 * @param jedis
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setKeyCache(Cache cache,Jedis jedis,String key,String value){
		return cache.setStr(jedis, key, value);
	}	
	/**
	 * 删除
	 * @param writeCache
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean removeLocKeyTypeCache(Cache writeCache,String type,String key){
		return removeLocKeyCache(writeCache,type+key);
	}
	/**
	 * @param writeCache
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean removeLocKeyCache(Cache writeCache,String key){
		return writeCache.removeLocKey(key);
	}
	/**
	 * @param writeCache
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setLocKeyTypeCache(Cache writeCache,String type,String key,Object value){
		return setLocKeyCache(writeCache,type+key, value);
	}
	/**
	 * @param writeCache
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setLocKeyCache(Cache writeCache,String key,Object value){
		writeCache.setLocKey(key, value);
		logger.debug(key+" : "+value);
		return true;
	}
	/**
	 * @param writeCache
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 */
	public static Object getLocKeyTypeCache(Cache writeCache,String type,String key){
		Object obj = getLocKeyCache(writeCache,type+key);
		logger.debug(key+" : "+obj);
		return obj;
	}
	/**
	 * @param writeCache
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 */
	public static DynaBean getDynaBeanLocKeyTypeCache(Cache writeCache,String type,String key){
		DynaBean dynaBean = new DynaBean();
		dynaBean.setValues((Map)getLocKeyTypeCache(writeCache,type,key));
		return dynaBean;
	}
	/**
	 * @param writeCache
	 * @param type
	 * @param key
	 * @param value
	 * @return
	 */
	public static Object getLocKeyCache(Cache writeCache,String key){
		return writeCache.getLocKey(key);
	}
	/**
	 * 缓存类型为type的key对应的对象内容
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 * @param value
	 */
	public static boolean setTypeCache(Cache writeCache, String type, String key,
			String value) {		
		return writeCache.setStr(type+key, value);
	}
	/**
	 * 缓存类型为type的key对应的对象内容
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 * @param value
	 */
	public static boolean setTypeCacheByDyanBean(Cache writeCache, String type, String key,
			DynaBean value) {		
		return writeCache.setStr(type+key, JSON.toJSONString(value,SerializerFeature.WriteMapNullValue));
	}
	/**
	 * 根据缓存类型以及key获取缓存
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static DynaBean getDynaBeanByCacheID(Cache readCache, String key) {
		return (DynaBean) readCache.getDynaBean(key);
	}
	/**
	 * 根据缓存类型以及key获取缓存
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static void setDynaBeanByCacheID(Cache writeCache, String key,DynaBean dynaBean) {
		writeCache.setStr(key,JSON.toJSONString(dynaBean.getValues(),SerializerFeature.WriteMapNullValue));
	}
	/**
	 * 根据缓存类型
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static void setMapListByCacheID(Cache writeCache, String keyName,List<Map> mapList) {
		writeCache.setStrFromListMap(keyName, mapList);
	}
	/**
	 * 根据缓存类型
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static void setDynaBeanListByCacheID(Cache writeCache, String keyName,List<DynaBean> dyList) {
		writeCache.setStrFromListDynaBean(keyName, dyList);
	}
	/**
	 * 根据缓存类型以及key获取缓存
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static String getTypeCache(Cache readCache, String type, String key) {
		return readCache.getStr(type+key);
	}

	/**
	 * 根据缓存类型以及key获取缓存
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static DynaBean getDynaBeanByTypeCache(Cache readCache, String type,
			String key) {
		String tempKey = readCache.getStr(type+key);
		if (tempKey != null) {
			return readCache.getDynaBean(tempKey);
		} else {
			return null;
		}
	}

	/**
	 * 根据type及key删除
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static boolean removeTypeCache(Cache writeCache, String type, String key) {
		return writeCache.delete(type+key);
	}

	/**
	 * 根据type及key替换
	 * 
	 * @param cache
	 * @param type
	 *            缓存类型
	 * @param key
	 */
	public static boolean replaceTypeCache(Cache writeCache,String type,
			String key, String value) {
		return writeCache.replace(type+key, value);
	}

	/**
	 * 根据type接key的值获取key缓存的多个key,并把从key中获取对象
	 * 
	 * @param cache
	 * @param type
	 * @param key
	 *            单个key 此key的缓存是多个key值,再把key值从缓存中获取对象
	 */
	public static List<DynaBean> getListDynaBeanTypeCache(Cache readCache, String type,
			String key) {
		List<DynaBean> beanList = new ArrayList<DynaBean>();
		JSONArray  jList = JSONArray.fromObject(readCache.getStr(key+type));
		if (jList == null) {
			return null;
		}
		for (int i=0;i<jList.size();i++) {
			DynaBean dynaBean = readCache.getDynaBean(jList.getJSONObject(i).getString("ID").toString());
			if (dynaBean != null) {
				beanList.add(dynaBean);
			}
		}
		return beanList;
	}
	/**
	 * 根据type接key的值获取key缓存的多个key,并把从key中获取对象 
	 * @param cache
	 * @param type
	 * @param key 单个key 此key的缓存是多个key值,再把key值从缓存中获取对象
	 */
	public static List<Map> getListMapTypeCache(Jedis jedis, String value) {
		List<Map> beanList = new ArrayList<Map>();
		if(value!=null&&value.length()!=0){
			List<String> strs = jedis.mget(value.split(","));
			for(String s:strs){
				beanList.add(BeanUtils.jsonToDynaBean(JSON.parseObject(s)).getValues());
			}
		}
		return beanList;
	}
	/**
	 * 
	 * @param writeCache
	 * @param typeCode
	 * @param key
	 * @param mapList
	 */
	public static void setTypeCacheMapList(Cache writeCache,
			String typeCode, String key, List<Map> mapList) {
		setTypeCache(writeCache,typeCode,key,JSON.toJSONString(mapList,SerializerFeature.WriteMapNullValue));
	}
	/**
	 * 根据应用程序获取
	 * 
	 * @param cache
	 * @param key
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static DynaBean getSysapp(Cache readCache, String appId,BaseDao dao) throws SQLException, Exception {
		DynaBean appBean = (DynaBean) getDynaBeanLocKeyTypeCache(readCache, SYSAPP,	appId);
		if (appBean == null||appBean.getValues()==null) {
			List<DynaBean> appList = dao.findWithQueryNoCache(new DynaBean("SYS_APP","AND APPID='"+appId+"'"));
			if(appList.size()!=0){
				initSysapp(dao.getWriteCache(),appList.get(0), dao,dao.getDataSource().getConnection());
				appBean = (DynaBean) getDynaBeanLocKeyTypeCache(readCache, SYSAPP,	appId);
			}else{
				return null;				
			}
		}
		List<Map> dicList = (List<Map>)getLocKeyTypeCache(readCache, SYSDIC, appId);
		if(dicList==null){
			dicList = new ArrayList();
		}else{
			for (Map map : dicList) {
				DynaBean dynaBean = new DynaBean();
				dynaBean.setValues(map);
				if (dynaBean.getStr("DICTYPE", "").equals("LISTVALUE")) {
					dynaBean.set(PageUtil.PAGE_APPDICDETAILS,getLocKeyTypeCache(readCache, SYSDICDETAIL,appId + dynaBean.getStr("DICID")));
				} else if (dynaBean.getStr("DICTYPE", "").equals("LISTAPP")) {
					List<Map> childList = (List)getLocKeyTypeCache(readCache, SYSDICAPP,appId + dynaBean.getStr("DICID"));
					if(childList!=null){
						for (Map map1 : childList) {
							DynaBean cBean = new DynaBean();
							cBean.setValues(map1);				
							cBean.set(PageUtil.PAGE_APPDICAPPDETAILS,getLocKeyTypeCache(readCache,SYSDICAPPDETAIL,appId + dynaBean.getStr("DICID")+ cBean.getStr("APPID")));
						}
					}else{
						childList = new ArrayList();
					}
					dynaBean.set(PageUtil.PAGE_APPDICAPPS, childList);
				}
			}
		}
		appBean.set(PageUtil.PAGE_APPDICS, dicList);
		appBean.set(PageUtil.PAGE_APPFIELDS,getLocKeyTypeCache(readCache, SYSAPPFIELDS, appId));
		appBean.set(PageUtil.PAGE_APPBUTTONS,getLocKeyTypeCache(readCache, SYSAPPBUTTONS, appId));
		appBean.set(PageUtil.PAGE_APPLINKS,getLocKeyTypeCache(readCache, SYSAPPLINKS, appId));
		return appBean;
	}

	/**
	 * 根据应用程序获取
	 * 
	 * @param cache
	 * @param key
	 */
	public static void removeSysapp(Cache writeCache,String appId) {
		removeLocKeyTypeCache(writeCache, SYSAPP, appId);
		removeLocKeyTypeCache(writeCache, SYSAPPFIELDS, appId);
		removeLocKeyTypeCache(writeCache, SYSAPPBUTTONS, appId);
		DynaBean dynaBean = getDynaBeanByTypeCache(writeCache, SYSDIC, appId);
		removeLocKeyTypeCache(writeCache, SYSDIC, appId);
		removeLocKeyTypeCache(writeCache, SYSDICDETAIL, appId + dynaBean.getStr("DICID"));
		removeLocKeyTypeCache(writeCache, SYSAPPLINKS, appId);
	}	

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月8日 下午3:56:10 方法说明: 缓存应用程序对应的表结构
	 *          先写应用程序及结构表加入缓存,确保所有表结构都写入缓存
	 * 
	 * @param cache
	 * @param beanList
	 * @param conn
	 * @throws Exception
	 */
	public static void initSysAppList(Cache writeCache,	BaseDao dao) throws Exception {
		List<Map> mapList = dao.findWithQueryNoCacheMap(new DynaBean("SYS_APP","and ENABLE='Y'"));
		Connection conn = dao.getDataSource().getConnection();
		for (Map map : mapList) {
			DynaBean dynaBean = new DynaBean();
			dynaBean.setValues(map);
			initSysapp(writeCache, dynaBean,dao,conn);
		}
	}

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月8日 下午3:56:10 方法说明: 缓存应用程序对应的表结构
	 *          先写应用程序及结构表加入缓存,确保所有表结构都写入缓存
	 * 
	 * @param cache
	 * @param beanList
	 * @param conn
	 * @throws Exception
	 */
	public static void initSysapp(Cache writeCache, DynaBean appBean, BaseDao dao,Connection conn) throws Exception {
		String appId = appBean.getStr("APPID");
		String tablename = appBean.getStr("MAINTABLE");
		// 初始化数据结构
		DynaBean dynaBean = SqlBuilder.getTableDef(appBean.getStr("MAINTABLE"),
				conn);
		//缓存在jdk缓存中
		setLocKeyTypeCache(writeCache,SYSTABLEDEF,tablename,dynaBean);
		// 把appid作为Key存入
		setLocKeyTypeCache(writeCache, SYSAPP, appId, appBean.getValues());
		// 字段写入缓存
		initSysAppFields(writeCache, dao, appId);
		// 按钮写入缓存
		initSysAppButtons(writeCache, dao, appId);
		// 关联应用写入app缓存
		initSysAppLinks(writeCache, dao, appId);
		// 字典表加入缓存,必须在表结构缓存写完后执行,把数据字典写入缓存中
		initSysAppSysDic(writeCache, dao, appId);
		// 元数据对应的接口信息。
		initSysAppProductInterface(writeCache,dao,appId);
	}

	/**
	 * 根据APpid字典表写入缓存
	 * 
	 * @param cache
	 * @param dao
	 * @param appId
	 */
	public static void initSysAppSysDic(Cache writeCache,BaseDao dao, String appId) {
		DynaBean dynaBean = new DynaBean("SYS_DIC","and DICID in(select DICCODE from SYS_APPFIELDS where APPID='"+ appId + "')");
		List<Map> mapList = dao.findWithQueryNoCacheMap(dynaBean);
		// 数据字典写入缓存
		setLocKeyTypeCache(writeCache, SYSDIC, appId, mapList);
		for (Map map : mapList) {
			if (map.get("DICTYPE") != null&& map.get("DICTYPE").toString().equals("LISTVALUE")) {
				// 静态字典写入缓存
				dynaBean = new DynaBean("SYS_DICDETAIL", " and DICID='"	+ map.get("DICID") + "'", "SORT");
				List<Map> childMapList = dao.findWithQueryNoCacheMap(dynaBean);
				setLocKeyTypeCache(writeCache, SYSDICDETAIL, appId	+ map.get("DICID").toString(), childMapList);
			} else {
				// 动态字段写入缓存
				dynaBean = new DynaBean("SYS_DICAPP", " and DICID='" + map.get("DICID") + "'");
				List<Map> childMapList = dao.findWithQueryNoCacheMap(dynaBean);
				setLocKeyTypeCache(writeCache, SYSDICAPP, appId	+ map.get("DICID").toString(), childMapList);
				for (Map map1 : childMapList) {
					dynaBean = new DynaBean("SYS_DICAPPDETAIL", " and DICID='" + map.get("DICID") + "'");
					List<Map> childMapList1 = dao.findWithQueryNoCacheMap(dynaBean);
					setLocKeyTypeCache(writeCache,SYSDICAPPDETAIL,appId + map.get("DICID").toString()+ map1.get("APPID").toString(),childMapList1);
				}
			}
		}
	}
	/**
	 * 根据元数据编码对应的产品中的某个接口是否有权限
	 * @param writeCache
	 * @param dao
	 * @param appId
	 */
	public static void initSysAppProductInterface(Cache writeCache,BaseDao dao, String appId) {
		DynaBean dynaBean = new DynaBean("SYS_INTERFACE","and APPID='"+appId+"'");
		List<Map> mapList = dao.findWithQueryNoCacheMap(dynaBean);
	}
	/**
	 * 根据APpid字段写入缓存
	 * 
	 * @param cache
	 * @param dao
	 * @param appId
	 */
	public static void initSysAppFields(Cache writeCache, BaseDao dao, String appId) {
		DynaBean dynaBean = new DynaBean("SYS_APPFIELDS", "and ENABLE='Y' and APPID='" + appId + "'", "SORT");
		//如果不存在数据，则从库中加载		
		setLocKeyTypeCache(writeCache, SYSAPPFIELDS, appId,	dao.findWithQueryNoCacheMap(dynaBean));
	}

	/**
	 * 根据APpid按钮写入缓存
	 * 
	 * @param cache
	 * @param dao
	 * @param appId
	 */
	public static void initSysAppButtons(Cache writeCache, BaseDao dao, String appId) {
		DynaBean dynaBean = new DynaBean("SYS_APPBUTTONS", "and ENABLE='Y' and APPID='" + appId + "'", "SORT");
		// 按钮写入缓存
		setLocKeyTypeCache(writeCache, SYSAPPBUTTONS, appId,dao.findWithQueryNoCacheMap(dynaBean));
	}

	/**
	 * 根据APpid设置关联应用
	 * 
	 * @param cache
	 * @param dao
	 * @param appId
	 */
	public static void initSysAppLinks(Cache writeCache, BaseDao dao, String appId) {
		DynaBean dynaBean = new DynaBean("SYS_APPLINKS", " and MAINAPP='"	+ appId + "'", "SORTBY");
		setLocKeyTypeCache(writeCache, SYSAPPLINKS, appId,dao.findWithQueryNoCacheMap(dynaBean));
	}
 

	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2016年1月8日 下午4:01:41 方法说明:
	 * @param cache
	 * @param beanList
	 * @param dao
	 * @throws Exception
	 */
	public static void initPersonalData(Cache writeCache,Cache readCache, Map map, BaseDao dao)
			throws Exception {
		String tableName = map.get("MAINTABLE").toString();
		DynaBean childsBean = new DynaBean(tableName);
		DynaBean tableDef = (DynaBean) getLocKeyTypeCache(readCache, SYSTABLEDEF, tableName);
		if (tableDef == null) {
			tableDef = SqlBuilder.getTableDef(tableName, dao.getDataSource().getConnection());
			setLocKeyTypeCache(writeCache,SYSTABLEDEF,tableName,tableDef);
		}
		List<Map> childBeanList = dao.findWithQueryMap(childsBean,tableDef);
		//批量写缓存	
		setMapListByCacheID(writeCache, "ID",childBeanList);		
	}
	/**
	 * 另外一个线程开始写缓存至缓存服务器
	 * @param writeCache
	 * @param readCache
	 * @param beanList
	 * @param dao
	 */
	private static void initPersonalDataList(final Cache writeCache,final Cache readCache,
			final List<Map> mapList, final BaseDao dao) {
		new Thread(){
			public void run(){
				for (Map map: mapList) {
					try {
						initPersonalData(writeCache,readCache, map, dao);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	/**
	 * 加载某个系统结构缓存，如果appId为空则全部加载
	 * @param writeCache
	 * @param dao
	 * @param appId 元数据编码 
	 * @throws Exception 
	 */
	public static void initSystemCache(BaseDao dao,String appId,Connection conn) throws Exception{
			//通用缓存数据加载 			
			DynaBean parentAppBean = getSysapp(dao.getReadCache(), appId,dao);
			Map map = parentAppBean.getValues();
			DynaBean tableDef = null;
				try {
					tableDef = SqlBuilder.getTableDef(map.get("MAINTABLE").toString(),conn);					
					setLocKeyTypeCache(dao.getWriteCache(),SYSTABLEDEF,map.get("MAINTABLE").toString(),tableDef);		
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String sqlFilter = "";
				if(map.get("APPFILTER")!=null){
					sqlFilter=map.get("APPFILTER").toString();
				}
				//获取主应用对应的数据
				List<Map> dataMapList = dao.findWithQueryNoCacheMap(new DynaBean(map.get("MAINTABLE").toString(),sqlFilter), tableDef);
				String key = map.get("APPID").toString();				
				String parentKeyFieldCode = BeanUtils.getKeyseqFromApp(parentAppBean);
				List<Map> linkMapList = dao.findWithQueryMap(new DynaBean("SYS_APPLINKS"," and MAINAPP='" + key + "' and ENABLECACHE='Y'",""),SqlBuilder.getTableDef("SYS_APPLINKS", conn));					
				for(Map dataMap:dataMapList){
					String dataKey = dataMap.get(parentKeyFieldCode).toString();
					setLocKeyTypeCache(dao.getWriteCache(),key,dataKey,dataMap);
					//关联查询缓存
					for(Map linkMap:linkMapList){
						String linkKey=linkMap.get("ITEMAPP").toString();
						if(linkKey.length()==0){
							continue;
						}
						DynaBean childAppBean = getSysapp(dao.getReadCache(),linkKey,dao);	
						if(childAppBean!=null){
							String linkFilter = BeanUtils.getSqlWhereBy(dataMap,linkKey,childAppBean,linkMap.get("LINKFILTER").toString());
							List<Map> childDataMapList = dao.findWithQueryNoCacheMap(new DynaBean(childAppBean.getStr("MAINTABLE"),linkFilter), SqlBuilder.getTableDef(childAppBean.getStr("MAINTABLE"),conn));
							if(childDataMapList.size()>0){
								setLocKeyTypeCache(dao.getWriteCache(),dataKey,linkKey,childDataMapList);
							}
						}
					}			
				}			
	}
	
	/**
	 * 缓存场景初始化
	 * @param writeCache 写缓存对象
	 * @param readCache 读缓存对象
	 * @param cacheList 缓存场景集合
	 * @param dao 数据库持久类
	 */
	public static void initSceneDataList(final Cache writeCache,final Cache readCache,final List<Map> cacheList,final BaseDao dao) {
//		final Jedis jedis = (Jedis) writeCache.getRealCache(writeCache.getWritejedisPool());
		for(final Map map:cacheList){
			logger.debug(map.toString());
			 new Thread(){
				public void run(){
					long s = DateUtils.getTimestamp();
					Jedis jedis = null;
					try{
						jedis = (Jedis) writeCache.getRealCache(writeCache.getWritejedisPool());
						initSceneData(writeCache,readCache,map,dao,jedis);
					//释放回缓存池
					}catch(Exception ex){
						ex.printStackTrace();
					}finally{
						if(jedis!=null){
							writeCache.shutdown(writeCache.getWritejedisPool(), jedis);				
						}
					}
					long t = DateUtils.getTimestamp();
					logger.info("Flush "+map.get("APPID") +" need "+(t-s));
				}
			}.start();
		}
		//释放回缓存池
//		writeCache.shutdown(writeCache.getWritejedisPool(), jedis);
	}
	/**
	 * 缓存场景初始化
	 * @param writeCache 写缓存对象
	 * @param readCache 读缓存对象
	 * @param cache 缓存场景对象
	 * @param dao 数据库持久类
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private static void initSceneData(final Cache writeCache,final Cache readCache,final Map map,final BaseDao dao,final Jedis jedis) throws SQLException, Exception {
		DynaBean sceneBean = new DynaBean();
		sceneBean.setValues(map);
		String[] keys = sceneBean.getStr("SQLFIELDS","").split(",");
		DynaBean appBean = CacheUtil.getSysapp(readCache, sceneBean.getStr("APPID",""),dao);
		//内核未加载完成
		if(appBean==null){
			return;
		}
		String sqlFilter = appBean.getStr("APPFILTER","")+ " and PID='"+sceneBean.getStr("PID")+"' "+ sceneBean.getStr("FILTER","");
		DynaBean parentDataBean = new DynaBean(appBean.getStr("MAINTABLE",""),sqlFilter);
		parentDataBean.set(BeanUtils.KEY_SELECT, sceneBean.getStr("FIELDS",""));		
		List<Map> sceneDataList = dao.findWithQueryMap(parentDataBean);		
		//查询关联缓存配置信息
		List<Map> cacheLinkList = dao.findWithQueryMap(new DynaBean("SYS_CACHELINK"," and PID='"+sceneBean.getStr("PID")+"' and ENABLE='Y' and SCENEID='"+sceneBean.getStr("SCENEID")+"'"));
		for(Map dataMap:sceneDataList){			
			initSceneData(writeCache,readCache,map,dataMap,keys,appBean,cacheLinkList,dao,jedis);
		}
	}
	/**
	 * 缓存场景初始化
	 * @param writeCache
	 * @param readCache
	 * @param map 主对象的cache对象
	 * @param dataMap 主对象的内容
	 * @param dao
	 * @param jedis
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static void initSceneData(final Cache writeCache,final Cache readCache,final Map map,final Map dataMap,String[] keys,final DynaBean appBean,
			final List<Map> cacheLinkList,final BaseDao dao,Jedis jedis) throws SQLException, Exception{
		StringBuffer keysf= new StringBuffer("");		
		//加载对象至缓存中							
		for(String key:keys){
			Object v = dataMap.get(key);
			if(v!=null){
				keysf.append(v.toString()).append(":");
			}
		}
		if(keysf.length()!=0){
			keysf.append(appBean.getStr("APPID"));
		}else{
			return;
		}
		String sqlFilter="";		
		//遍历所有的关联缓存对象
		for(Map cachelink:cacheLinkList){
			DynaBean linkBean = new DynaBean();
			linkBean.setValues(cachelink);
			DynaBean linkAppBean = CacheUtil.getSysapp(readCache, linkBean.getStr("APPID"),dao);
			List<Map> linkDataList = null;
			List<Map> refMapList = null;
			switch(linkBean.getStr("LINKTYPE","")){
			case "SYSAPP"://元数据
				sqlFilter = linkAppBean.getStr("APPFILTER","")+ " and PID='"+linkBean.getStr("PID")+"' "+ linkBean.getStr("LINKFILTER","");
				sqlFilter = BeanUtils.getSqlWhereBy(dataMap,linkBean.getStr("APPID"),linkAppBean,sqlFilter);
				DynaBean linkDataBean = new DynaBean(linkAppBean.getStr("MAINTABLE",""),sqlFilter,linkAppBean.getStr("SORT",""));
				linkDataBean.set(BeanUtils.KEY_SELECT, linkBean.getStr("FIELDS",""));
				linkDataList = dao.findWithQueryMap(linkDataBean);							
				break;
			case "OTHERSCENE"://其它元数据
				String refSceneId = linkBean.getStr("REFEFSCENEID","");
				refMapList = dao.findWithQueryMap(new DynaBean("SYS_CACHE","and PID='"+linkBean.getStr("PID")+"' and sceneid='"+refSceneId+"'"));
				for(Map refMap:refMapList){
					DynaBean refBean = new DynaBean();
					refBean.setValues(refMap);
					DynaBean refAppBean = CacheUtil.getSysapp(readCache, refBean.getStr("APPID",""),dao);
					DynaBean linkAppBean1 = CacheUtil.getSysapp(readCache, linkBean.getStr("APPID"),dao);						
					sqlFilter = BeanUtils.getSqlWhereBy(dataMap,linkAppBean1.getStr("APPID"),linkAppBean1,linkBean.getStr("LINKFILTER"));						
					DynaBean refVBean = new DynaBean(refAppBean.getStr("MAINTABLE"),sqlFilter,linkAppBean1.getStr("SORT",""));
					refVBean.setStr(BeanUtils.KEY_SELECT, refBean.getStr("SQLFIELDS"));
					linkDataList = dao.findWithQueryMap(refVBean);
				}
				break;
			case "SQL"://sql条件作为数据
				String sql = linkBean.getStr("EXECSQL","");
				DynaBean sqlBean = new DynaBean("");
				sqlBean.setStr(BeanUtils.KEY_SQL, sql);
				linkDataList = dao.findWithQueryMap(sqlBean);
				break;
			}				
			StringBuffer sb= new StringBuffer("");
			//根据关联的缓存对象查询所有的数据记录
			for(Map childDataMap:linkDataList){
				if(refMapList==null){
					sb.append(childDataMap.get("ID")).append(":").append(linkAppBean.getStr("APPID")).append(",");
					CacheUtil.setKeyCache(writeCache,jedis,childDataMap.get("ID").toString()+":"+linkAppBean.getStr("APPID"),JSON.toJSONString(childDataMap,SerializerFeature.WriteMapNullValue));
				}else{
					for(Map refMap:refMapList){
						DynaBean refBean = new DynaBean();
						refBean.setValues(refMap);
						String[] fields = refBean.getStr("SQLFIELDS").split(",");
						for(String s:fields){
							sb.append(childDataMap.get(s)).append(":");
						}
						if(fields.length!=0){
							sb.append(linkAppBean.getStr("APPID"));
						}
						sb.append(",");
					}
				}
			}
			if(sb.length()!=0){
				sb.setLength(sb.length()-1);
			}
			dataMap.put(keysf.toString()+":"+linkBean.getStr("APPID"), sb.toString());
			logger.debug(keysf.toString()+":"+linkBean.getStr("APPID")+" : "+sb.toString());
		}
		CacheUtil.setKeyCache(writeCache, jedis, keysf.toString(),JSON.toJSONString(dataMap,SerializerFeature.WriteMapNullValue));		
	}
	/**
	 * 缓存场景初始化
	 * @param writeCache
	 * @param readCache
	 * @param map
	 * @param dao
	 * @param jedis
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private static void initSceneDataOne(final Cache writeCache,final Cache readCache,final Map map,final BaseDao dao,Jedis jedis) throws SQLException, Exception{
		DynaBean sceneBean = new DynaBean();
		sceneBean.setValues(map);
		String[] keys = sceneBean.getStr("SQLFIELDS","").split(",");
		//查询关联缓存配置信息
		List<Map> cacheLinkList = dao.findWithQueryMap(new DynaBean("SYS_CACHELINK"," and PID='"+sceneBean.getStr("PID")+"' and ENABLE='Y' and SCENEID='"+sceneBean.getStr("SCENEID")+"'"));
		DynaBean appBean = CacheUtil.getSysapp(readCache, sceneBean.getStr("APPID",""),dao);
		//内核未加载完成
		if(appBean==null){
			return;
		}
		String sqlFilter = appBean.getStr("APPFILTER","")+ " and PID='"+sceneBean.getStr("PID")+"' "+ sceneBean.getStr("FILTER","");
		DynaBean parentDataBean = new DynaBean(appBean.getStr("MAINTABLE",""),sqlFilter);
		parentDataBean.set(BeanUtils.KEY_SELECT, sceneBean.getStr("FIELDS",""));		
		List<Map> sceneDataList = dao.findWithQueryMap(parentDataBean);
		StringBuffer keysf= new StringBuffer("");
		for(Map dataMap:sceneDataList){
			keysf.setLength(0);
			//加载对象至缓存中							
			for(String key:keys){
				Object v = dataMap.get(key);
				if(v!=null){
					keysf.append(v.toString()).append(":");
				}
			}
			if(keysf.length()!=0){
				keysf.append(appBean.getStr("APPID"));
			}else{
				continue;
			}			
			//遍历所有的关联缓存对象
			for(Map cachelink:cacheLinkList){
				DynaBean linkBean = new DynaBean();
				linkBean.setValues(cachelink);
				DynaBean linkAppBean = CacheUtil.getSysapp(readCache, linkBean.getStr("APPID"),dao);
				List<Map> linkDataList = null;
				List<Map> refMapList = null;
				switch(linkBean.getStr("LINKTYPE","")){
				case "SYSAPP"://元数据
					sqlFilter = linkAppBean.getStr("APPFILTER","")+ " and PID='"+linkBean.getStr("PID")+"' "+ linkBean.getStr("LINKFILTER","");
					sqlFilter = BeanUtils.getSqlWhereBy(dataMap,linkBean.getStr("APPID"),linkAppBean,sqlFilter);
					DynaBean linkDataBean = new DynaBean(linkAppBean.getStr("MAINTABLE",""),sqlFilter,linkAppBean.getStr("SORT",""));
					linkDataBean.set(BeanUtils.KEY_SELECT, linkBean.getStr("FIELDS",""));
					linkDataList = dao.findWithQueryMap(linkDataBean);							
					break;
				case "OTHERSCENE"://其它元数据
					String refSceneId = linkBean.getStr("REFEFSCENEID","");
					refMapList = dao.findWithQueryMap(new DynaBean("SYS_CACHE","and PID='"+linkBean.getStr("PID")+"' and sceneid='"+refSceneId+"'"));
					for(Map refMap:refMapList){
						DynaBean refBean = new DynaBean();
						refBean.setValues(refMap);
						DynaBean refAppBean = CacheUtil.getSysapp(readCache, refBean.getStr("APPID",""),dao);
						DynaBean linkAppBean1 = CacheUtil.getSysapp(readCache, linkBean.getStr("APPID"),dao);						
						sqlFilter = BeanUtils.getSqlWhereBy(dataMap,linkAppBean1.getStr("APPID"),linkAppBean1,linkBean.getStr("LINKFILTER"));						
						DynaBean refVBean = new DynaBean(refAppBean.getStr("MAINTABLE"),sqlFilter,linkAppBean1.getStr("SORT",""));
						refVBean.setStr(BeanUtils.KEY_SELECT, refBean.getStr("SQLFIELDS"));
						linkDataList = dao.findWithQueryMap(refVBean);
					}
					break;
				case "SQL"://sql条件作为数据
					String sql = linkBean.getStr("EXECSQL","");
					DynaBean sqlBean = new DynaBean("");
					sqlBean.setStr(BeanUtils.KEY_SQL, sql);
					linkDataList = dao.findWithQueryMap(sqlBean);
					break;
				}				
				StringBuffer sb= new StringBuffer("");
				//根据关联的缓存对象查询所有的数据记录
				for(Map childDataMap:linkDataList){
					if(refMapList==null){
						sb.append(childDataMap.get("ID")).append(":").append(linkAppBean.getStr("APPID")).append(",");
						CacheUtil.setKeyCache(writeCache,jedis,childDataMap.get("ID").toString()+":"+linkAppBean.getStr("APPID"),JSON.toJSONString(childDataMap,SerializerFeature.WriteMapNullValue));
					}else{
						for(Map refMap:refMapList){
							DynaBean refBean = new DynaBean();
							refBean.setValues(refMap);
							String[] fields = refBean.getStr("SQLFIELDS").split(",");
							for(String s:fields){
								sb.append(childDataMap.get(s)).append(":");
							}
							if(fields.length!=0){
								sb.append(linkAppBean.getStr("APPID"));
							}
							sb.append(",");
						}
					}
				}
				if(sb.length()!=0){
					sb.setLength(sb.length()-1);
				}
				dataMap.put(keysf.toString()+":"+linkBean.getStr("APPID"), sb.toString());
				logger.debug(keysf.toString()+":"+linkBean.getStr("APPID")+" : "+sb.toString());
			}
			CacheUtil.setKeyCache(writeCache, jedis, keysf.toString(),JSON.toJSONString(dataMap,SerializerFeature.WriteMapNullValue));
		}					
	}

	/**
	 * 缓存清除
	 */
	public static void clearCache(BaseDao dao) {
		if(dao.getWriteCache() instanceof MemaCache){
			((MemaCache)dao.getWriteCache()).flushAll();
		}
	}
}