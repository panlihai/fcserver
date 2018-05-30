package cn.pcorp.controllor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javacommon.util.JsonUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pcorp.controllor.util.MethodConstant;
import cn.pcorp.dao.BaseDao;
import cn.pcorp.dynamicdatasource.DynamicDataSource;
import cn.pcorp.impl.sys.ServiceListener;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.BaseService;
import cn.pcorp.service.ParentService;
import cn.pcorp.service.system.SysServer;
import cn.pcorp.util.ApiUtil;
import cn.pcorp.util.BeanUtils;
import cn.pcorp.util.CacheUtil;
import cn.pcorp.util.DateUtils;
import cn.pcorp.util.LogUtil;
import cn.pcorp.util.PageUtil;
import cn.pcorp.util.SubThread;
import cn.pcorp.util.SyConstant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author panlihai E-mail:18611140788@163.com
 * @version 创建时间：2015年12月4日 上午10:54:45 类说明: 对平台处理操作
 */
@RestController()
public class MainController {
	@Resource(name = "baseService")
	private BaseService baseService;
	private static org.apache.log4j.Logger logger = Logger.getLogger(MainController.class);
	@Resource(name = "dynamicDataSource")
	private DynamicDataSource dynamicDataSource;
	/**
	 * 对APPID进行操作显示 get请求只作显示操作
	 * 
	 * @param appId
	 * @param action
	 * @param request
	 * @version SUPVISOR
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/api/{pId}/{appId}/{action}",method={RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
	@ResponseBody
	public void impl(@PathVariable String pId, @PathVariable String appId,
			@PathVariable String action,HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		// 事务对象
		DataSourceTransactionManager transactionManager = null;
		// 事务状态
		TransactionStatus status = null;
		BaseDao dao = baseService.getBaseDao();
		// 返回数据对象
		DynaBean result = new DynaBean();
		try {
			result.set(MethodConstant.CODE, "0");// 请求成功
			result.set(MethodConstant.MSG,ApiUtil.getBackName(dao, "0"));
			
			// 获取参数列表信息
			DynaBean paramBean = BeanUtils.requestToDynaBean(request);			
			// 数据源名称编码
			paramBean.setStr(BeanUtils.KEY_DATASOURCEKEY, "");
			// 获取操作参数
			paramBean.set(MethodConstant.ACT, action);
			// 获取产品id
			paramBean.set(MethodConstant.PID, pId);
			// 得到产品对象
			DynaBean productBean = CacheUtil.getDynaBeanLocKeyTypeCache(baseService.getBaseDao().getReadCache(),CacheUtil.SYSPRODUCT, pId);
			// 获取应用id
			paramBean.set(MethodConstant.AID, appId);
			// // 得到产品对象
			List<DynaBean> intefaceList = dao.findWithQueryNoCache("", new DynaBean("SYS_INTERFACE"," and REQTYPE='" + action + "' and PID='" + pId	+ "' and REQURL='" + appId + "'"));
			if (intefaceList.size() == 0) {
				result.setStr(MethodConstant.CODE, "40004");// 不合法的AID凭证
				result.setStr(MethodConstant.MSG, ApiUtil.getBackName(dao, "40004"));
				logger.debug("校验异常:校验产品的接口是否开放,请参考:" + result.toJsonString());
			} else {
				// 获取接口配置信息表
				DynaBean interfaceBean = intefaceList.get(0);
				DynaBean appBean = CacheUtil.getSysapp(dao.getReadCache(),interfaceBean.getStr(MethodConstant.APPID),dao);
				// 得到接口请求及响应的参数列表
				List<DynaBean> paramList = dao.findWithQueryNoCache("", new DynaBean("SYS_INTERFACEPARAM", "and IMPLID='"+ interfaceBean.getStr("IMPLID")+ "' and PID='" + pId + "'"));
				paramBean.setStr(MethodConstant.AID, appBean.getStr(MethodConstant.APPID));
				// 获取参数体消息
				JSONArray paramJson = JSONArray.parseArray(paramBean.getStr(MethodConstant.DATA, "[]"));
				if(paramJson.size()==0){
					BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(),Charset.forName("UTF-8")));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = in.readLine()) != null) {
						sb.append(line);
					}
					line=null;
					String jstr=sb.toString();
					if(jstr.length()!=0){
						//中文解码
						try{
							JSONObject objJson = JSONObject.parseObject(jstr);
							if(objJson!=null){
								paramJson = objJson.getJSONArray(MethodConstant.DATA);
							}
							paramBean.set(MethodConstant.DATA, jstr);
						}catch(Exception ex){			
							ex.printStackTrace();
						}
					}
				}			
				// 校验合法性
				if (!ApiUtil.checkParams(dao, paramBean, paramJson,
						interfaceBean, paramList, result)) {
					logger.debug("校验异常:校验请求参数是否合法,请参考:" + result.toJsonString());
				} else {
					// 记录请求日志
					DynaBean log = LogUtil.logRequest(paramBean);
					// 记录APPID
					log.setStr(MethodConstant.AID, paramBean.getStr(MethodConstant.AID));					
					// 获取返回结果
					String compositeStr = paramBean.getStr("COMPOSITE", "");
					if (compositeStr.length() == 0) {						
							paramBean.setStr(PageUtil.PAGE_APPID,paramBean.getStr(MethodConstant.AID));
							Object lstn = SysServer.getServer().getBean(paramBean.getStr(PageUtil.PAGE_APPID));
							if(lstn==null){
								lstn =  SysServer.getServer().getBean("PARENTSERVICE");
							}
							ParentService listener = (ParentService)getJdkDynamicProxyTargetObject(lstn);
							
							//获取用户信息
							paramBean.set("USERINFO", baseService.getUserInfo(baseService.getBaseDao(),null,  paramBean));
							Class ownerClass = listener.getClass(); 
							Class[] argsClass = new Class[1];
							argsClass[0] = RequestModel.class.getClass(); 
							Method[] methods = ownerClass.getMethods();
							for(Method method:methods){
								if(method.getName().toUpperCase().equals(action.toUpperCase())){
									// 事务设置数据源-20180410
									transactionManager = dynamicDataSource.getTransactionManager(paramBean.getStr(BeanUtils.KEY_DATASOURCEKEY));
									// 获得事务状态-20180410
									status = dynamicDataSource.getTransactionStatus(transactionManager);
									
									RequestModel rm = new RequestModel(request,response,paramBean,productBean, appBean, interfaceBean,paramList, paramJson, baseService);							     
									ResponseModel rs = (ResponseModel)method.invoke(listener, rm);
									result = rs.getResult();
									
									if("0".equals(rs.getCode())){
										// 提交-20180410
										transactionManager.commit(status);
									}else{
										// 回滚事务-20180524
										transactionManager.rollback(status);
									}
									break;
								}
							}
					} else {
						Map resultMap = new HashMap();
						paramBean.getValues().remove("COMPOSITE");
						paramBean.getValues().remove(MethodConstant.AID);
						paramBean.getValues().remove(MethodConstant.ACT);
						JSONArray compositeArray = JSONArray.parseArray(compositeStr);
						// 线程计数器
						CountDownLatch threadsSignal = new CountDownLatch(compositeArray.size());
						List<SubThread> subThreadList = new ArrayList<SubThread>();
						String paramStr = paramBean.toJsonString();
						for (int i = 0; i < compositeArray.size(); i++) {
							DynaBean pBean = BeanUtils.jsonToDynaBean(JSONObject.parseObject(paramStr));
							SubThread subThread = new SubThread();
							subThreadList.add(subThread);
							JSONObject composite = (JSONObject) compositeArray.get(i);
							subThread.setComposite(composite);
							subThread.setBaseService(baseService);
							subThread.setParamBean(pBean);
							subThread.setRequest(request);
							subThread.setResponse(response);
							subThread.setProductBean(productBean);
							subThread.setAppBean(appBean);
							subThread.setInterfaceBean(interfaceBean);
							subThread.setParamList(paramList);
							subThread.setParamJson(paramJson);
							subThread.setThreadsSignal(threadsSignal);
							subThread.run();
						}
						threadsSignal.await();
						for (SubThread th : subThreadList) {
							resultMap.put(th.getParamBean().getStr(MethodConstant.AID, "")	+ "-" + th.getParamBean().getStr(MethodConstant.ACT, ""),
									th.getResultMap());
						}
						result.set(MethodConstant.DATA, resultMap);
					}
					// 记录响应日志
//					LogUtil.logResponse(log, result);
					// 操作原样返回
					result.set(MethodConstant.ACT, action);
					// 返回时间戳
					result.set(MethodConstant.TIMESTAMP, DateUtils.getTimestamp());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 错误详情
			result.set(MethodConstant.CODE, "-1");
			// 错误消息体
			result.set(MethodConstant.MSG, "处理异常，请联系管理员");
			
			// 回滚事务-20180410
			if(transactionManager!=null)
				transactionManager.rollback(status);
		}
		try {
			if(!action.equals(MethodConstant.EXPORT)){
				// UTF-8编码
				response.setCharacterEncoding("UTF-8");
				// 把结果写回响应中
				response.getWriter().write(JSON.toJSONString(result.getValues(),SerializerFeature.WriteMapNullValue));
				// 刷新
				response.getWriter().flush();
				logger.debug(result.toJsonString());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 对APPCODE进行操作get请求只作功能操作
	 * 
	 * @param appCode
	 * @param action
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/ajax/{pId}/{menuId}/{appId}/{action}", method = RequestMethod.GET)
	@ResponseBody
	public void getMenuAjax(@PathVariable String pId,
			@PathVariable String menuId, @PathVariable String appId,
			@PathVariable String action,HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
		String json = "";
		try {
			DynaBean paramBean = BeanUtils.requestToDynaBean(request);
			//获取用户信息
			paramBean.set("USERINFO", baseService.getUserInfo(baseService.getBaseDao(),null,  paramBean));			
			// 数据源名称编码
			paramBean.setStr("KEY", "");
			paramBean.set(PageUtil.PAGE_ACTION, action);
			paramBean.set(PageUtil.PAGE_APPID, appId);
			paramBean.set(PageUtil.PAGE_MENUID, menuId.equals("TOP") ? "" : menuId);
			paramBean.set(PageUtil.PAGE_PID, pId);
			switch (action) {
			case SyConstant.ACT_VIEW_MENUS:
				json = JsonUtils.toJson(baseService.showMenus(paramBean).getModel());
				break;
			/** ACT_DATA_MENUS 显示所有的数据 */
			case SyConstant.ACT_VIEW_ONE:
				json = JsonUtils.toJson(baseService.viewOne(paramBean)
						.getModel());
				break;
			/** $ACTION$：显示列表查看页面 */
			case SyConstant.ACT_LIST_VIEW:
				json = JsonUtils.toJson(baseService.listView(paramBean)
						.getModel());
				break;
			/** $ACTION$：显示列表JSON数据 */
			case SyConstant.ACT_DATA_JSON:
				// 根据应用程序获得json
				json = baseService.listJsonFromAppid(paramBean.getStr(BeanUtils.KEY_DATASOURCEKEY), appId);
				break;
			/** $ACTION$：获取静态数据字典列表列表JSON数据 */
			case SyConstant.ACT_DATA_JSON_VALUE:
				json = baseService.listJsonValueByDicId(appId);
				break;
			/** $ACTION$：显示添加页面 */
			case SyConstant.ACT_CARD_ADD:
				/** $ACTION$：显示列表编辑页面 */
			case SyConstant.ACT_LIST_EDIT:
			case SyConstant.ACT_LIST_ADD:
				json = JsonUtils.toJson(baseService.cardAdd(paramBean)
						.getModel());
				break;
			/** $ACTION$：卡片保存 */
			case SyConstant.ACT_CARD_SAVE:
				json = JsonUtils.toJson(baseService.cardSave(paramBean)
						.getModel());
				break;
			/** $ACTION$：删除 */
			case SyConstant.ACT_DELETE:
				json = JsonUtils.toJson(baseService.listDelete(paramBean)
						.getModel());
				break;
			default: // 执行自定义的操作
				paramBean.setStr(PageUtil.PAGE_APPID,paramBean.getStr(MethodConstant.AID));
				Object lstn = SysServer.getServer().getBean(appId);
				if(lstn==null){
					lstn =  SysServer.getServer().getBean("PARENTSERVICE");
				}
				ServiceListener listener = (ServiceListener)lstn;							
				//获取用户信息
				paramBean.set("USERINFO", baseService.getUserInfo(baseService.getBaseDao(),null,  paramBean));				
				json = JsonUtils.toJson(listener.execute(request,response,baseService.getBaseDao(),paramBean));			
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			logger.debug(json);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void clearCache() {
		baseService.clearCache();
	}


	/**
	 * 对APPID进行操作显示 get请求只作显示操作
	 * 
	 * @param appId
	 * @param action
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/view/{pId}/{appId}/{action}", method = RequestMethod.GET)
	public ModelAndView view(@PathVariable String pId,
			@PathVariable String appId, @PathVariable String action,
			HttpServletRequest request) throws ParseException {
		ModelAndView view = null;
		HttpSession session = request.getSession();
		try {
				DynaBean paramBean = BeanUtils.requestToDynaBean(request);
				paramBean.set(PageUtil.PAGE_ACTION, action);
				paramBean.set(PageUtil.PAGE_APPID, appId);
				paramBean.set(PageUtil.PAGE_PID, pId);
				switch (action) {
				/** $ACTION$：显示列表查看页面 */
				case SyConstant.ACT_LIST_VIEW:
					return new ModelAndView("/view");
					/** $ACTION$：显示添加页面 */
				case SyConstant.ACT_CARD_ADD:
					return new ModelAndView("/view");
				}
		} catch (Exception e) {
			e.printStackTrace();
			view = new ModelAndView("/exception");
		}
		return view;
	}
	private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {  
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");  
        h.setAccessible(true);  
        AopProxy aopProxy = (AopProxy) h.get(proxy);  
          
        Field advised = aopProxy.getClass().getDeclaredField("advised");  
        advised.setAccessible(true);  
          
        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();  
          
        return target;  
    }

}