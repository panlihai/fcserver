package cn.pcorp.util;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/** 
 *@description Log4jlistener.java
 *@author  zhaoxin
 *@date 创建时间2015-12-25 12:46:10
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  
 */
public class Log4jlistener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//System.getProperties().remove(ZlsjXConstants.LOG4J_DIR_KEY); 
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		/*String log4jdir11 = this.getClass().getClassLoader().getResource("/").getPath();
		String log4jdir22 = arg0.getServletContext().getRealPath(File.separator);*/
		InputStream log4jdir = Log4jlistener.class.getClassLoader().getResourceAsStream("/config/config.properties");
		InputStream log4jdir11 =Object.class.getResourceAsStream("/config/config.properties");
		String p2=Log4jlistener.class.getResource("").getPath(); 
		System.out.println("JdomParse.class.getResource---"+p2);
		System.setProperty("log4jdir", "/Data/app_1/tomcat_zlsj/logs"); 
		System.out.println(p2);
		System.out.println(System.getProperty("user.dir"));
	}

	
	public static String getRootPath() {
		  String classPath = Log4jlistener.class.getClassLoader().getResource("/").getPath();
		  
		  String rootPath  = "";
		  //windows下
		  if("\\".equals(File.separator)){   
		   rootPath  = classPath.substring(1,classPath.indexOf("/WEB-INF/classes"));
		   rootPath = rootPath.replace("/", "\\");
		  }
		  //linux下
		  if("/".equals(File.separator)){   
		   rootPath  = classPath.substring(0,classPath.indexOf("/WEB-INF/classes"));
		   rootPath = rootPath.replace("\\", "/");
		  }
		  return rootPath;
		 }
	
	public static void main(String[] args) {
		System.out.println(getRootPath());
		
		
	}
}
