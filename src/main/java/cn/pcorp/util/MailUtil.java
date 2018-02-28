package cn.pcorp.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** 
* @author panlihai E-mail:18611140788@163.com 
* @version 创建时间：2015年12月7日 下午2:34:21 
* 类说明: 
*/
public class MailUtil {
	//是否身份验证
	 private static final String MAIL_SMTP_AUTH="mail.smtp.auth";
	 //发送邮件主机
	 private static final String MAIL_SMTP_HOST="mail.smtp.host";
	 //发送邮件名称
	 private static final String MAIL_USER="mail.user";
	 //发送邮件密码
	 private static final String MAIL_PASSWORD="mail.password";
	 
	/**
	 * 发送邮件
	 * @param receiveMail 收件人地址
	 * @param title 发送邮件标题
	 * @param body 发送文件内容 
	 * @return 判断是否发送成功
	 * @throws Exception
	 */
	public static boolean sendMail(String receiveMail,String title,String body){
		// 配置发送邮件的环境属性
        final Properties props = new Properties();
        /*
         * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
         * mail.user / mail.from
         */
        // 表示SMTP发送邮件，需要进行身份验证
        props.put(MAIL_SMTP_AUTH, "true");
        props.put(MAIL_SMTP_HOST, ParamUtil.MAIL_HOST);
        // 发件人的账号
        props.put(MAIL_USER, ParamUtil.MAIL_USER);
        // 访问SMTP服务时需要提供的密码
        props.put(MAIL_PASSWORD, ParamUtil.MAIL_PSW);

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                return new PasswordAuthentication(props.getProperty(MAIL_USER),
                		props.getProperty(MAIL_PASSWORD));
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);        
        try{
        	// 设置发件人
            InternetAddress form = new InternetAddress(
                    props.getProperty(MAIL_USER));
            message.setFrom(form);
	        // 设置收件人
	        InternetAddress to = new InternetAddress(receiveMail);
	        message.setRecipient(RecipientType.TO, to);
	
	//        // 设置抄送
	//        InternetAddress cc = new InternetAddress("luo_aaaaa@yeah.net");
	//        message.setRecipient(RecipientType.CC, cc);
	//
	//        // 设置密送，其他的收件人不能看到密送的邮件地址
	//        InternetAddress bcc = new InternetAddress("aaaaa@163.com");
	//        message.setRecipient(RecipientType.CC, bcc);
	
	        // 设置邮件标题
	        message.setSubject(title);
	
	        // 设置邮件的内容体
	        message.setContent(body, "text/html;charset=UTF-8");
	       // message.setContent("<a href='http://www.**'>测试的HTML邮件</a>", "text/html;charset=UTF-8");
	
	        // 发送邮件
	        Transport.send(message);
        }catch(Exception ex){
        	ex.printStackTrace();
        	return false;
        }
        return true;
	}
	public static void main(String args[]){
		MailUtil.sendMail("524373231@qq.com", "测试邮件", "我要测试是否需要其他内容");
	}
}

 