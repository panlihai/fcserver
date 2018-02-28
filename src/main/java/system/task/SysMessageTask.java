package system.task;

import java.io.IOException;
import java.util.Map;

import service.system.SysMessageSocket;
import cn.pcorp.schedultask.Task;
import cn.pcorp.util.DateUtils;

public class SysMessageTask extends Task{

	@Override
	public void execute(Map map) throws Exception {
		// TODO Auto-generated method stub
		String dateString = DateUtils.getDatetime();
		System.out.println(dateString+" SysMessageTask------------------》开始--执行定时任务");
		
		SysMessageSocket socket= SysMessageSocket.getInstance();
		try {
			//发送所有在线人员未读取消息
			socket.sendMsg();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(dateString+" SysMessageTask------------------》结束--执行定时任务");
	}
	
	
}
