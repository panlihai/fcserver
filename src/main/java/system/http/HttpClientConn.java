package system.http;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**		
使用HttpClient发送请求、接收响应很简单，一般需要如下几步即可。
1. 创建HttpClient对象。
2. 创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。
3. 如果需要发送请求参数，可调用HttpGet、HttpPost共同的setParams(HetpParams params)方法来添加请求参数；对于HttpPost对象而言，也可调用setEntity(HttpEntity entity)方法来设置请求参数。
4. 调用HttpClient对象的execute(HttpUriRequest request)发送请求，该方法返回一个HttpResponse。
5. 调用HttpResponse的getAllHeaders()、getHeaders(String name)等方法可获取服务器的响应头；调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
6. 释放连接。无论执行方法是否成功，都必须释放连接
*/

public class HttpClientConn {
	
    /** 
     * 发送 get请求 
     * @param urlParamStr {"URLPARAMS":{"APPID":"MongoLogs","PID":"BUDGET"},"URL":"http://172.28.19.107:7091/mongod/getMlogList"}
     */  
    public static String get(String urlParamStr) {  
    	String result = "";
    	// 创建默认的httpClient实例.  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
        	JSONObject urlParams = JSON.parseObject(urlParamStr);  	
        	// 访问地址
    		StringBuffer urlSB = new StringBuffer(urlParams.getString("URL"));
    		// 拼接参数
    		JSONObject params = urlParams.getJSONObject("URLPARAMS");
    		if(params!=null && params.size()>0){
    			urlSB.append("?");
    			for (Map.Entry<String, Object> entry : params.entrySet()) {
    				urlSB.append(entry.getKey()+"="+
//    						entry.getValue().toString() +"&");
    						URLEncoder.encode( entry.getValue().toString() ,"UTF-8") +"&");
    			}
    			urlSB.setLength(urlSB.length()-1);		
    		}
    		
    		System.out.println("executing request :" + urlSB.toString());
    		
            // 创建httpget.
            HttpGet httpget = new HttpGet(urlSB.toString());
            
            System.out.println("executing request :" + httpget.getURI());  
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity();  
//                System.out.println("--------------------------------------");  
                // 打印响应状态    
//                System.out.println("打印响应状态:"+response.getStatusLine()); 
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                	if (entity != null) {  
                        // 打印响应内容长度    
//                        System.out.println("Response content length: " + entity.getContentLength());  
                        // 打印响应内容    [只调用一次]
//                        System.out.println("Response content: " + EntityUtils.toString(entity,"utf-8"));  
                        result = EntityUtils.toString(entity,"utf-8");
                        //关闭HttpEntity实体流
                        EntityUtils.consume(entity);
                    }
                }
//                System.out.println("------------------------------------");  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return result;
    }  
    
	/** 
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果 
     * @param urlParamStr {"URLPARAMS":{"APPID":"MongoLogs","PID":"BUDGET"},"URL":"http://172.28.19.107:7091/mongod/getMlogList"}
     */  
    public static String post(String urlParamStr) {  
    	String result = "";
    	// 创建默认的httpClient实例.    
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {
        	JSONObject urlParams = JSON.parseObject(urlParamStr);
			// 创建httppost  
	        HttpPost httppost = new HttpPost(urlParams.getString("URL"));  
	        // 参数值 
	        JSONObject params = urlParams.getJSONObject("URLPARAMS");
	        if(params!=null && params.size()>0){
		        // 创建参数队列<参数名，参数值>    
		        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString())); 
				}
				httppost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
//				httppost.setEntity(new UrlEncodedFormEntity(nvps));
	        }
	        StringEntity s = new StringEntity("");
	        // 执行get请求.  
	        CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
            	// 获取响应实体 
                HttpEntity entity = response.getEntity();  
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	                if (entity != null) {  
	//                    System.out.println("--------------------------------------");  
	//                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));  
	//                    System.out.println("--------------------------------------");  
	                    //响应内容
	                	result=EntityUtils.toString(entity, "UTF-8");
	                    //关闭HttpEntity实体流
	                    EntityUtils.consume(entity);
	                }  
                }
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } 
        return result;
    }  
    
    
    
}
