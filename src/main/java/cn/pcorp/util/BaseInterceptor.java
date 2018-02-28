/*
 * 版权所有（C）北京神州中联软件有限责任公司 2015
 *
 * http://www.hongyousoft.com.cn
 *
 * 本程序是神州中联专有产品，神州中联拥有全部产权，仅限于在公司项目或产品中使用。
 */
package cn.pcorp.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @ClassName: cn.com.zlwh.hades.system.support.BaseInterceptor
 * @Description: 拦截器基类
 */
public class BaseInterceptor {
    protected final static Log LOG = LogFactory.getLog(BaseInterceptor.class);
    
    /**
     * @Description: 根据错误代码生成错误信息
     * @param errorCode
     * @return
     */
    protected String getErrorResult(Integer errorCode) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"errorCode\":");
        sb.append(errorCode);
        sb.append(",");
        sb.append("\"errorMessage\":");
        sb.append("错误");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * @Description: 根据错误代码和参数 生成错误信息
     * @param errorCode
     * @param param
     * @return
     */
    protected String getErrorResult(Integer errorCode, String param) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"errorCode\":");
        sb.append(errorCode);
        sb.append(",");
        sb.append("\"errorMessage\":");
        sb.append("[" + param + "]" + "异常");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * @Description: 获取request当中的参数值.所有需要从request中获取参数的方法都需要调用此方法.
     * @param paramName
     * @param request
     * @return
     */
    public String getRequestValue(String paramName, HttpServletRequest request) {
        return request.getParameter(paramName);
    }
    
    /**
     * @Description: 输出结果
     * @param response
     * @param message
     */
    public void outPrint(HttpServletResponse response, String message) {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }
}
