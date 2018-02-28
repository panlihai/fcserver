package service.system;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.ParentService;

/**
 * 即时消息交互类
 * 
 * */
@ServerEndpoint(value = "/msg/{USERID}")
@Component
public class SysMessageSocket {	
	
	public SysMessageSocket (){}
	private static SysMessageSocket instance = null;
	public static SysMessageSocket getInstance(){
		
		System.out.println("单例初始化SysMessageSocket.java");
		
		if(instance == null ){
			synchronized (SysMessageSocket.class) {
				if (instance == null) {
					instance = new SysMessageSocket();
				} 
			}
		}
		return instance;
	}
	
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。  
    private static int onlineCount = 0;  
        
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。  
    private static CopyOnWriteArraySet<SysMessageSocket> wsClientMap = new CopyOnWriteArraySet<>();    
    
    //与某个客户端的连接会话，需要通过它来给客户端发送数据  
    private Session session;  
    private String userid;
    
    /** 
     * 连接建立成功调用的方法 
     * @param session 当前会话session 
     */  
    @OnOpen  
    public void onOpen (@PathParam(value = "USERID") String userid, Session session){  
        this.session = session;  
        this.userid = userid;
        wsClientMap.add(this);  
        
        addOnlineCount();  
        System.err.println(session.getId()+" ** userid:"+userid+ ",有新链接加入，当前链接数为：" + wsClientMap.size());  
//        logger.info(session.getId()+"有新链接加入，当前链接数为：" + wsClientMap.size());  
        
		try {
			//用户接收自己的消息
			List<DynaBean> msgList = findSysMessage(userid);
			if(msgList!=null && msgList.size()>0){
				for(DynaBean message : msgList){
					sendMessage(message.toJsonString());
				}
				//更新状态为已发送
				updateSysMessage(msgList, userid);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }  
    
    /** 
     * 连接关闭 
     */  
    @OnClose  
    public void onClose (Session session, CloseReason closeReason){  
        wsClientMap.remove(this);    
        subOnlineCount();    
        System.err.println("有一链接关闭，当前链接数为：" + wsClientMap.size()+" ****** "+String.format("Session %s closed because of %s", session.getId(), closeReason));
//        logger.info("有一链接关闭，当前链接数为：" + wsClientMap.size());  
    } 
    
    /** 
     * 发生错误 
     */  
    @OnError  
    public void onError(Session session, Throwable error) {  
    	System.err.println("wsClientMap发生错误!");
//        logger.info("wsClientMap发生错误!");  
        error.printStackTrace();  
    }  
    
    /** 
     * 收到客户端消息 
     * @param message 客户端发送过来的消息 
     * @param session 当前会话session 
     * @throws IOException 
     */  
    @OnMessage  
    public void onMessage (String message, Session session) throws IOException {  
    	System.err.println("来终端的警情消息:" + message);
    	
    	sendMsgToAll(message);   
    }  
    
    /** 
     * 给所有客户端群发消息 
     * @param message 消息内容 
     * @throws IOException 
     */  
    public void sendMsgToAll(String message) throws IOException {  
        for ( SysMessageSocket item : wsClientMap ){    
            item.session.getBasicRemote().sendText(message);  
        }  
        System.err.println("成功群送一条消息:" + wsClientMap.size());
//        logger.info("成功群送一条消息:" + wsClientMap.size());  
    }  
    
    /** 
     * 给当前session客户端发消息 
     * @param message 消息内容 
     * @throws IOException 
     */
    public void sendMessage (String message) throws IOException {  
        this.session.getBasicRemote().sendText(message);  
        System.err.println("成功发送一条消息:" + message);
//        logger.info("成功发送一条消息:" + message);  
    } 
    
    /**
     * 获取在线数量
     * */
    public static synchronized  int getOnlineCount (){  
        return SysMessageSocket.onlineCount;    
    }  
    
    /**
     * 增加在线数量
     * */
    public static synchronized void addOnlineCount (){  
    	SysMessageSocket.onlineCount++;    
    }  
    
    /**
     * 减少在线数量
     * */
    public static synchronized void subOnlineCount (){  
    	SysMessageSocket.onlineCount--;    
    }
    
//  ----------------begin--------------------------------------------------
    
    /**
     * 建立连接即可发消息
     * */
	public void sendMsg() throws IOException {
    	// 在线用户信息
		List<DynaBean> listSessionBean = findSysSession("");
		if (listSessionBean != null && listSessionBean.size() > 0) {
			for(DynaBean sessionBean : listSessionBean ){
				// 指定在线用户 消息内容
				List<DynaBean> msgList = findSysMessage(sessionBean.getStr("USERID"));

				if(msgList!=null && msgList.size()>0){
					sendMsgListToObject(msgList, sessionBean.getStr("USERID"));
					//更新状态为已发送
					updateSysMessage(msgList, sessionBean.getStr("USERID"));
				}
			}
		}
    }
	
	/** 
     * 给其他session客户端发消息 
     * @param message 消息内容 
     * @throws IOException 
     */
    public void sendMsgListToObject (List<DynaBean> listMsg, String userid) throws IOException {  
    	for ( SysMessageSocket item : wsClientMap ){    
    		if(item.userid.equals(userid)){
    			for(DynaBean message : listMsg){
    				item.session.getBasicRemote().sendText( message.getStr("CONTENT"));
        			System.err.println(item.session.getId()+"成功发送一条消息:" + message.getStr("CONTENT") );
    			}
    			break;
    		}
        }    	
    }  
		
//    -------------------end-----------------------------------------------
    
    /**
     * 未读且未发送的消息列表
     * */
    public List<DynaBean> findSysMessage (String NOTIFICATIONUSERID) throws IOException{
    	BaseDao dao = (BaseDao)cn.pcorp.service.system.SysServer.getServer().getBean("baseDao");
    	List<DynaBean> listBean = dao.findWithQueryNoCache(new DynaBean("SYS_MESSAGE"," AND ISREAD='N' AND ISSEND='N' and NOTIFICATIONUSERID='"+NOTIFICATIONUSERID+"'","TS desc"));
    	return listBean;
    }
    
    /**
     * 在线人员信息
     * */
    public List<DynaBean> findSysSession(String sql) throws IOException{
    	BaseDao dao = (BaseDao)cn.pcorp.service.system.SysServer.getServer().getBean("baseDao");
    	if(sql==null || sql.length()==0){
    		sql = "";
    	}
    	List<DynaBean>  listBean = dao.findWithQueryNoCache(new DynaBean("SYS_SESSION"," and STATUS='Y' "+sql));
    	return listBean;
    }
	/**
	 * 更新状态为已发送
	 * */
    public void updateSysMessage(List<DynaBean> msgList, String userid){
    	//修改消息为已发送状态
		BaseDao dao = (BaseDao)cn.pcorp.service.system.SysServer.getServer().getBean("baseDao");
		DynaBean nMsgBean = new DynaBean("SYS_MESSAGE"," AND ISREAD='N' AND ISSEND='N' and NOTIFICATIONUSERID='"+userid+"'");
		nMsgBean.setStr("ISSEND", "Y");
		dao.updateOne(nMsgBean);
    }
	
}
