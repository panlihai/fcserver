package service.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import system.http.HttpClientConn;
import cn.pcorp.controllor.RequestModel;
import cn.pcorp.controllor.ResponseModel;
import cn.pcorp.controllor.util.MethodConstant;
import cn.pcorp.service.BaseService;
import cn.pcorp.service.ParentService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SysLogService extends ParentService  {
	
	private static final String URL_ADDRESS = "http://localhost:9090/";
	private static final String SERVER_LOG = "mongodServer/mongod/";
	
	@Resource(name = "baseService")
	private BaseService baseService;
	
	public ResponseModel ADDSYSLOG(RequestModel rm) {
//		http://localhost:7091/TOBMS/mongod/getCollectionCount?APPID=MongoLogs   
//		addCollection getCollectionList getCollectionCount getCollectionById
//		getCollectionByPage getCollectionByFilter
//		addDocumentOne addDocumentList
		String urlParamStr = "";
    	
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("LOGINIP", "127.0.0.1");
    	map.put("TYPE", 1);
    	map.put("OPERATION", "test集合插入");
    	map.put("CREATEDATE", new Date());
    	map.put("REMARK", "333日日");
    	list.add(map);
    	Map<String, Object> map2 = new HashMap<String, Object>();
    	map2.put("LOGINIP", "127.0.0.1");
    	map2.put("TYPE", 1);
    	map2.put("OPERATION", "test集合插入");
    	map2.put("CREATEDATE", new Date());
    	map2.put("REMARK", "444呜呜");
    	list.add(map2);
//    	mongoDBService.addDocumentManyMap(list ,mongoCollection);
		
//		JSONObject obj = new JSONObject();
//		obj.put("LOGINIP", "localhost");
//		obj.put("TYPE", "2");
//		obj.put("OPERATION", "Documenttest日志oneDocument存储");
//		obj.put("REMARK", "11111111存储");
		
		// 参数集合
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("APPID", "MongoLogs");
//		paramMap2.put("PAGENO", "1");
//		paramMap2.put("PAGESIZE", "3");
		paramMap2.put("DOCUMENT", list);
		
		// 参数集合
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("MPARAMS", paramMap2);
		
//		{"LOGINIP":"127.0.0.1","TYPE":1,"OPERATION":"test日志one存储","CREATEDATE":"","REMARK":"test日志one存储"} 
		
		// 请求信息
		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("URL", URL_ADDRESS+SERVER_LOG+"addDocumentList");
		urlMap.put("URLPARAMS", paramMap);
		urlParamStr = JSON.toJSONString(urlMap);
		
		System.out.println("server请求信息："+urlParamStr);
		// get请求
//		String result = HttpClientConn.get(urlParamStr);
		// post请求
		String result = HttpClientConn.post(urlParamStr);
		
		System.out.println("server响应信息:" + result);

		// 请求结果
		JSONObject resjson = JSON.parseObject(result);
		
		System.out.println(resjson.get("DATA"));
		
		// 带回参数
		ResponseModel resp = ResponseModel.getInstance();
		resp.setData( resjson.get("DATA") );
		return resp;
	}
	
	
	
	
	
	
	public ResponseModel ADDSYSLOGONE(RequestModel rm) {
//		http://localhost:7091/TOBMS/mongod/addDocumentOne?APPID=MongoLogs   
		String urlParamStr = "";
		JSONObject obj = new JSONObject();
		obj.put("LOGINIP", "localhost");
		obj.put("TYPE", "2");
		obj.put("OPERATION", "Documenttest日志oneDocument存储");
		obj.put("REMARK", "11111111存储");
		
		// 参数集合
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("APPID",  rm.getParamBean().getStr(MethodConstant.AID));
		paramMap2.put("DOCUMENT", obj);
		// 参数集合
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("MPARAMS", paramMap2);
		// 请求信息
		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("URL", URL_ADDRESS+SERVER_LOG+"addDocumentOne");
		urlMap.put("URLPARAMS", paramMap);
		urlParamStr = JSON.toJSONString(urlMap);
		// post请求
		String result = HttpClientConn.post(urlParamStr);
		// 请求结果
		JSONObject resjson = JSON.parseObject(result);
		// 带回参数
		ResponseModel resp = ResponseModel.getInstance();
		resp.setData( resjson.get("DATA") );
		return resp;
	}
	
	public ResponseModel ADDSYSLOGLIST(RequestModel rm) {
//		http://localhost:7091/TOBMS/mongod/addDocumentList?APPID=MongoLogs   
		String urlParamStr = "";
    	
//    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//    	Map<String, Object> map = new HashMap<String, Object>();
//    	map.put("LOGINIP", "127.0.0.1");
//    	map.put("TYPE", 1);
//    	map.put("OPERATION", "test集合插入");
//    	map.put("CREATEDATE", new Date());
//    	map.put("REMARK", "333日日");
//    	list.add(map);
//    	Map<String, Object> map2 = new HashMap<String, Object>();
//    	map2.put("LOGINIP", "127.0.0.1");
//    	map2.put("TYPE", 1);
//    	map2.put("OPERATION", "test集合插入");
//    	map2.put("CREATEDATE", new Date());
//    	map2.put("REMARK", "444呜呜");
//    	list.add(map2);
		
		// 参数集合
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("APPID", rm.getParamBean().getStr(MethodConstant.AID));
		paramMap2.put("DOCUMENT",  rm.getParamJson());
		// 参数集合
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("MPARAMS", paramMap2);
		// 请求信息
		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("URL", URL_ADDRESS+SERVER_LOG+"addDocumentList");
		urlMap.put("URLPARAMS", paramMap);
		urlParamStr = JSON.toJSONString(urlMap);
		// post请求
		String result = HttpClientConn.post(urlParamStr);
		// 请求结果
		JSONObject resjson = JSON.parseObject(result);
		// 带回参数
		ResponseModel resp = ResponseModel.getInstance();
		resp.setData( resjson.get("DATA") );
		return resp;
	}
	
	public ResponseModel GETSYSLOGBYID(RequestModel rm) {
//		http://localhost:7091/TOBMS/mongod/getCollectionById?APPID=MongoLogs&DOCID=5a291b4c0da612199c318b53 
		String urlParamStr = "";
		// 参数集合
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("APPID", rm.getParamBean().getStr(MethodConstant.AID));
		paramMap2.put("DOCID", "5a1fc67f947749325c3db1c7");
		
		// 参数集合
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("MPARAMS", paramMap2);
		
		// 请求信息
		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("URL", URL_ADDRESS+SERVER_LOG+"getCollectionById");
		urlMap.put("URLPARAMS", paramMap);
		urlParamStr = JSON.toJSONString(urlMap);
		// post请求
		String result = HttpClientConn.post(urlParamStr);
		// 请求结果
		JSONObject resjson = JSON.parseObject(result);
		// 带回参数
		ResponseModel resp = ResponseModel.getInstance();
		resp.setData( resjson.get("DATA") );
		return resp;
	}
	
	/**
	 * 所有日志
	 * */
	public ResponseModel GETSYSLOGLIST(RequestModel rm) {
//		http://localhost:7091/TOBMS/mongod/getCollectionList?APPID=MongoLogs
		String urlParamStr = "";
		// 参数集合
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("APPID", rm.getParamBean().getStr(MethodConstant.AID));
		// 请求信息
		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("URL", URL_ADDRESS+SERVER_LOG+"getCollectionList");
		urlMap.put("URLPARAMS", paramMap);
		urlParamStr = JSON.toJSONString(urlMap);
		// get请求
		String result = HttpClientConn.get(urlParamStr);
		// 请求结果
		JSONObject resjson = JSON.parseObject(result);
		// 带回参数
		ResponseModel resp = ResponseModel.getInstance();
		resp.setData( resjson.get("DATA") );
		return resp;
	}
	
	/**
	 * 日志数量
	 * */
	public ResponseModel GETSYSLOGCOUNT(RequestModel rm) {
//		http://localhost:7091/TOBMS/mongod/getCollectionCount?APPID=MongoLogs
		String urlParamStr = "";
		// 参数集合
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("APPID", rm.getParamBean().getStr(MethodConstant.AID));
		// 请求信息
		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("URL", URL_ADDRESS+SERVER_LOG+"getCollectionCount");
		urlMap.put("URLPARAMS", paramMap);
		urlParamStr = JSON.toJSONString(urlMap);
		// get请求
		String result = HttpClientConn.get(urlParamStr);
		// 请求结果
		JSONObject resjson = JSON.parseObject(result);
		// 带回参数
		ResponseModel resp = ResponseModel.getInstance();
		resp.setData( resjson.get("DATA") );
		return resp;
	}
	
	public ResponseModel ADDSYSLOGAPP(RequestModel rm) {
//		http://localhost:7091/TOBMS/mongod/addCollection?APPID=MongoLogs
		// 参数集合
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("APPID", rm.getParamBean().getStr(MethodConstant.AID));
		// 请求信息
		Map<String, Object> urlMap = new HashMap<String, Object>();
		urlMap.put("URL", URL_ADDRESS+SERVER_LOG+"addCollection");
		urlMap.put("URLPARAMS", paramMap);
		String urlParamStr = JSON.toJSONString(urlMap);
		// post请求
		String result = HttpClientConn.post(urlParamStr);
		// 请求结果
		JSONObject resjson = JSON.parseObject(result);
		// 带回参数
		ResponseModel resp = ResponseModel.getInstance();
		resp.setData( resjson.get("DATA") );
		return resp;
	}
	
}
