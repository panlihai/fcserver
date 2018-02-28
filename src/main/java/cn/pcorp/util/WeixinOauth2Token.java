package cn.pcorp.util;
/** 
 *@description WeixinOauth2Token.java微信accesstoken类
 *@author  zhaoxin
 *@date 创建时间：2015年11月23日 下午1:57:38
 * @version 1.0 
 * @parameter  
 * @since   jsonObject = JSONObject.fromObject(buffer.toString());
 * @return  
 */
public class WeixinOauth2Token {
	
	public String accessToken;

	public String expiresIn;
	public String refreshToken;
	public String openId;
	public String scope;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	
}
