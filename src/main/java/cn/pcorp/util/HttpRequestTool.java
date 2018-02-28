package cn.pcorp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import net.sf.json.JSONObject;

/**
 * @description HttpRequestTool.java
 * @author zhaoxin
 * @date 创建时间：2015年11月19日 上午11:53:26
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class HttpRequestTool {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HttpRequestTool.class);
	public static String sendGet(String url,String param,String code){

		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" +param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				logger.debug(key + "--->" + map.get(key));
//				System.out.println(map.get(key));
//			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(),code));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			logger.debug(result);
		} catch (Exception e) {
			logger.error("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	
	}
		/**
		 * 向指定URL发送GET方法的请求
		 * 
		 * @param url
		 *            发送请求的URL
		 * @param param
		 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
		 * @return URL 所代表远程资源的响应结果
		 */
		public static String sendGet(String url, String param) {
			return sendGet(url,param,"utf-8");
		}

		/**
		 * 向指定 URL 发送POST方法的请求
		 * 
		 * @param url
		 *            发送请求的 URL
		 * @param param
		 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
		 * @return 所代表远程资源的响应结果
		 */
		public static String sendPost(String url, String param) {
			PrintWriter out = null;
			BufferedReader in = null;
			String result = "";
			try {
				URL realUrl = new URL(url);
				// 打开和URL之间的连接
				URLConnection conn = realUrl.openConnection();
				// 设置通用的请求属性
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				// 发送POST请求必须设置如下两行
				conn.setDoOutput(true);
				conn.setDoInput(true);
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
				// 定义BufferedReader输入流来读取URL的响应
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
			} catch (Exception e) {
				logger.error("发送 POST 请求出现异常！" + e);
				e.printStackTrace();
			}
			// 使用finally块来关闭输出流、输入流
			finally {
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return result;
		}
		 public static String testGetRequest(String url) throws IllegalStateException, IOException {  
		        HttpClient client = new HttpClient();  
		        StringBuilder sb = new StringBuilder();  
		        InputStream ins = null;  
		        // Create a method instance.  
		        GetMethod method = new GetMethod(url);  
		        // Provide custom retry handler is necessary  
		        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,  
		                new DefaultHttpMethodRetryHandler(3, false));  
		        try {  
		            // Execute the method.  
		            int statusCode = client.executeMethod(method);  
		            logger.debug(statusCode);  
		            if (statusCode == HttpStatus.SC_OK) {  
		                ins = method.getResponseBodyAsStream();  
		                byte[] b = new byte[1024];  
		                int r_len = 0;  
		                while ((r_len = ins.read(b)) > 0) {  
		                    sb.append(new String(b, 0, r_len, method  
		                            .getResponseCharSet()));  
		                }  
		            } else {  
		            	logger.debug("Response Code: " + statusCode);  
		            }  
		        } catch (HttpException e) {  
		        	logger.error("Fatal protocol violation: " + e.getMessage());  
		        } catch (IOException e) {  
		        	logger.error("Fatal transport error: " + e.getMessage());  
		        } finally {  
		            method.releaseConnection();  
		            if (ins != null) {  
		                ins.close();  
		            }  
		        }  
		        logger.debug(sb.toString());  
		        return sb.toString();
		    }  
		public static String httpsRequestByWx(String requestUrl, String requestMethod, String outputStr) {
			JSONObject jsonObject = null;
			StringBuffer buffer = new StringBuffer();
			try {
				// 创建SSLContext对象，并使用我们指定的信任管理器初始化
				TrustManager[] tm = { new MyX509TrustManager() };
				SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
				sslContext.init(null, tm, new java.security.SecureRandom());
				// 从上述SSLContext对象中得到SSLSocketFactory对象
				SSLSocketFactory ssf = sslContext.getSocketFactory();

				URL url = new URL(requestUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setSSLSocketFactory(ssf);
				
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				// 设置请求方式（GET/POST）
				conn.setRequestMethod(requestMethod);

				// 当outputStr不为null时向输出流写数据
				if (null != outputStr) {
					OutputStream outputStream = conn.getOutputStream();
					// 注意编码格式
					outputStream.write(outputStr.getBytes("UTF-8"));
					outputStream.close();
				}

				// 从输入流读取返回内容
				InputStream inputStream = conn.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				
				while ((str = bufferedReader.readLine()) != null) {
					buffer.append(str);
				}

				// 释放资源
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
				inputStream = null;
				conn.disconnect();
				jsonObject = JSONObject.fromObject(buffer.toString());
			} catch (ConnectException ce) {
				logger.error("连接超时：{}", ce);
			} catch (Exception e) {
				logger.error("https请求异常：{}", e);
			}
			return buffer.toString();
		}
	public static JSONObject getLocation(String address,String province,String city) throws UnsupportedEncodingException {
		String data1;
		try {
			data1 = HttpRequestTool.sendGet("http://api.map.baidu.com/geocoder/v2/?ak=iqc6wilKUXo4DcVTHrH36HWxoAaGhhMs&output=json&address="+address,"");
			JSONObject js = JSONObject.fromObject(data1);
			if(js.getString("status").equals("1")){
				data1 = HttpRequestTool.sendGet("http://api.map.baidu.com/geocoder/v2/?ak=iqc6wilKUXo4DcVTHrH36HWxoAaGhhMs&output=json&address="+province+city+address,"");
				js = JSONObject.fromObject(data1);
			}
			JSONObject loc = ((JSONObject)js.getJSONObject("result"));
			if(loc==null){				
				return null;
			}else{
				loc=loc.getJSONObject("location");				
			}
			data1 = HttpRequestTool.sendGet("http://api.map.baidu.com/geocoder/v2/?ak=iqc6wilKUXo4DcVTHrH36HWxoAaGhhMs&location="+loc.getDouble("lat")+","+loc.getDouble("lng")+"&output=json&pois=0","");
			js = JSONObject.fromObject(data1);
			loc = ((JSONObject)js.getJSONObject("result"));
			return loc;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} 
	
	}
	public static void main(String args[]){
		String data1;
		data1 = HttpRequestTool.sendGet("http://121.26.223.18:89/qfls/api","method=getHistoryDataList&sessionkey=239A506CD4E38A8757A2314442146CF3&startTime="+URLEncoder.encode("2016-11-24 00:00:00")+"&endTime="+URLEncoder.encode("2016-11-24 16:00:00")+"&addr=11002016092813706&pageSize=499&pageNumber=1");
		JSONObject js = JSONObject.fromObject(data1);
		System.out.print(js);
	
	}
}
