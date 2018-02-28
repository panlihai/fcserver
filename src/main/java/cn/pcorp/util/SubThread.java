package cn.pcorp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pcorp.controllor.util.MethodConstant;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.BaseService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 子线程
 * @author Administrator
 *
 */
public class SubThread implements Runnable{
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	private HttpServletRequest request;
	private HttpServletResponse response;
	private DynaBean paramBean;
	private BaseService baseService;
	private DynaBean appBean;
	private DynaBean productBean;
	private DynaBean interfaceBean;
	private List<DynaBean> paramList;
	private JSONArray paramJson;
    private CountDownLatch threadsSignal;
    private Map<String,Object> resultMap= new HashMap<String,Object>();
    private JSONObject composite;
    /**
	 * @return the resultMap
	 */
	public Map<String, Object> getResultMap() {
		return resultMap;
	}	
	/**
	 * @return the composite
	 */
	public JSONObject getComposite() {
		return composite;
	}
	/**
	 * @param composite the composite to set
	 */
	public void setComposite(JSONObject composite) {
		this.composite = composite;
	}
	@Override
    public void run(){   		
		paramBean.getValues().putAll(BeanUtils.jsonToDynaBean(composite).getValues());
		DynaBean result = null;//ApiUtil.doImpl(request,response,paramBean,productBean,appBean,interfaceBean,paramList,paramJson,baseService);
		resultMap.put(MethodConstant.DATA,result.getValues());		
        threadsSignal.countDown();
    } 
    public CountDownLatch getThreadsSignal() {
        return threadsSignal;
    }
 
    public void setThreadsSignal(CountDownLatch threadsSignal) {
        this.threadsSignal = threadsSignal;
    }
	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @return the paramBean
	 */
	public DynaBean getParamBean() {
		return paramBean;
	}

	/**
	 * @param paramBean the paramBean to set
	 */
	public void setParamBean(DynaBean paramBean) {
		this.paramBean = paramBean;
	}

	/**
	 * @return the baseService
	 */
	public BaseService getBaseService() {
		return baseService;
	}

	/**
	 * @param baseService the baseService to set
	 */
	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}
	public DynaBean getAppBean() {
		return appBean;
	}
	public void setAppBean(DynaBean appBean) {
		this.appBean = appBean;
	}
	public DynaBean getProductBean() {
		return productBean;
	}
	public void setProductBean(DynaBean productBean) {
		this.productBean = productBean;
	}
	public DynaBean getInterfaceBean() {
		return interfaceBean;
	}
	public void setInterfaceBean(DynaBean interfaceBean) {
		this.interfaceBean = interfaceBean;
	}
	public JSONArray getParamJson() {
		return paramJson;
	}
	public void setParamJson(JSONArray paramJson) {
		this.paramJson = paramJson;
	}
	public List<DynaBean> getParamList() {
		return paramList;
	}
	public void setParamList(List<DynaBean> paramList) {
		this.paramList = paramList;
	}
	 
}  