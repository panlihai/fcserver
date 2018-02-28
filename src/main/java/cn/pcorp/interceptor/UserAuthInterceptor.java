/*
 * 版权所有（C） 2015
 *
 * 
 *
 * 本程序是仅限于在公司项目或产品中使用。
 */
package cn.pcorp.interceptor;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.pcorp.service.BaseService;
import cn.pcorp.util.BaseInterceptor;



/**
 * @ClassName: UserAuthInterceptor
 * @Description: 用户自动登录验证拦截器
 */
public class UserAuthInterceptor extends BaseInterceptor implements HandlerInterceptor {
	
	@Resource(name = "baseService")
	private BaseService baseService;
    
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object hd) {
    	try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods","GET,PUT,DELETE,POST,OPTIONS,UPDATE");
        response.setHeader("Access-Control-Allow-Headers","x-requested-with,content-type");  
        return true;
    }
    
    public void afterCompletion(HttpServletRequest request, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    	 String sessionId = request.getSession().getId();
    	 
    }
    
    public void postHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    	//此sessionId是用户保存于客户端的识别码，用于用户后续自动访问的自动登录，不是本次访问的sessionid
        //防止攻击
        String sessionId = request.getSession().getId();
        String token = request.getHeader("token");
    }
    private void init(){
    	
    }
}
