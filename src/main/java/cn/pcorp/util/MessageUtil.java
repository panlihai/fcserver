package cn.pcorp.util;

import cn.pcorp.model.DynaBean;
import cn.pcorp.service.system.SysServer;

public class MessageUtil {

	public static void main(String[] args) {

	}
	/**
	 * 
	 * @param title
	 * @param touser
	 * @param msgType
	 * @param content
	 * @param pid
	 * @param publishuser
	 * @param sAid
	 * @param sId
	 * @param supervise
	 * @param noticetime
	 */
	public static void sendMessage(String title, String touser, String msgType,
			String content, String pid, String publishuser, String sAid,
			String sId, String supervise,Long noticetime) {
		DynaBean msg = new DynaBean("C_MESSAGE");
		msg.set("TITLE", title);
		msg.set("TOUSER",touser);
		msg.set("MSGID", DateUtils.getTimestamp());
		msg.set("MESSAGETYPE",msgType);
		msg.set("CONTENT",content);
		msg.set("PID",pid);
		msg.set("ISREAD", "N");
		msg.set("SORT", DateUtils.getTimestamp());
		msg.set("PUBLISHUSER",publishuser);
		msg.set("SOURCEAID",sAid);
		msg.set("SOURCEID", sId);
		msg.set("SUPERVISE", supervise);
		msg.set("NOTICETIME", noticetime);
		msg.set("PUBLISHTIME",DateUtils.getTimestamp());
		msg.set("ID", SyConstant.getUUID());		
		SysServer.getServer().getQueue().offer(msg);
	}
	/**
	 * 任务调度异常处理
	 * @param content 内容
	 * @param sAid 数据源
	 * @param sId id
	 * @param pid 产品id
	 * @param supervise 监管单位
	 */
	public static void sendTaskMessage(String title,String content,String sAid,String sId,String pid,String supervise){
		sendMessage(title,"ADMIN","SYSTEMTASK",content,pid,"SYSTEM",sAid,sId,supervise,DateUtils.getTimestamp());
	}
}
