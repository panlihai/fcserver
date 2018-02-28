package cn.pcorp.controllor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pcorp.model.DynaBean;
import cn.pcorp.service.BaseService;

import com.alibaba.fastjson.JSONArray;

public class RequestModel {
	private String data;
	private String timestamp;
	private String lat;
	private String lng;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private DynaBean paramBean;
	private DynaBean productBean;
	private DynaBean appBean;
	private DynaBean interfaceBean;
	private List<DynaBean> paramList;
	private JSONArray paramJson;
	private BaseService baseService;

	public RequestModel(HttpServletRequest request,
			HttpServletResponse response, DynaBean paramBean,
			DynaBean productBean, DynaBean appBean, DynaBean interfaceBean,
			List<DynaBean> paramList, JSONArray paramJson,
			BaseService baseService) {
		this.request = request;
		this.response = response;
		this.paramBean = paramBean;
		this.productBean = productBean;
		this.appBean = appBean;
		this.interfaceBean = interfaceBean;
		this.paramList = paramList;
		this.paramJson = paramJson;
		this.baseService = baseService;
		}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public DynaBean getParamBean() {
		return paramBean;
	}

	public void setParamBean(DynaBean paramBean) {
		this.paramBean = paramBean;
	}

	public DynaBean getProductBean() {
		return productBean;
	}

	public void setProductBean(DynaBean productBean) {
		this.productBean = productBean;
	}

	public DynaBean getAppBean() {
		return appBean;
	}

	public void setAppBean(DynaBean appBean) {
		this.appBean = appBean;
	}

	public DynaBean getInterfaceBean() {
		return interfaceBean;
	}

	public void setInterfaceBean(DynaBean interfaceBean) {
		this.interfaceBean = interfaceBean;
	}

	public List<DynaBean> getParamList() {
		return paramList;
	}

	public void setParamList(List<DynaBean> paramList) {
		this.paramList = paramList;
	}

	public JSONArray getParamJson() {
		return paramJson;
	}

	public void setParamJson(JSONArray paramJson) {
		this.paramJson = paramJson;
	}

	public BaseService getBaseService() {
		return baseService;
	}

	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}
}
