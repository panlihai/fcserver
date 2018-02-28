package cn.pcorp.util;

import java.io.InputStream;
import java.util.Properties;

public final class ParameterPropertiesReader {

	private static final long serialVersionUID = 5400758185968736187L;

	private static Properties pro = new Properties();
	
	static
	{
		try{
			InputStream is = ParameterPropertiesReader.class
					.getResourceAsStream("/MailMessage.properties");
			pro.load(is);
			is.close();
		}catch(Exception e){
			System.err.println("文件不存在");
		}
	}
	
	public static String getProperty(String key,String defaultValue)
	{
		return (String)pro.getProperty(key, defaultValue);
	}
	
	public static String getProperty(String key)
	{
		return (String)pro.getProperty(key);
	}
}
