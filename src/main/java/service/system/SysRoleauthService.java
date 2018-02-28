package service.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.pcorp.controllor.RequestModel;
import cn.pcorp.controllor.ResponseModel;
import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.BaseService;
import cn.pcorp.service.ParentService;

public class SysRoleauthService extends ParentService {

	@Resource(name = "baseService")
	private BaseService baseService;
	
	public ResponseModel getRoleAuth2(RequestModel rm) {
		// 请求参数
		DynaBean paramBean = rm.getParamBean();
		String authType = paramBean.getStr("AUTHTYPE");
		ResponseModel response = null;
		
		if(authType==null || authType.length()==0){
			System.out.println("!!! Welcome !!!基础服务平台欢迎你");
//			// 访问库
//			BaseDao dao = baseService.getBaseDao();	
//			List<DynaBean> sysDataSourceList = dao.findWithQueryNoCache(new DynaBean("SYS_DATASOURCE", "and DSID='budget' and DSTYPE='MongoDB' "));
//			DynaBean dataSourceBean = null;
//			if(sysDataSourceList !=null && sysDataSourceList.size()>0 ){
//				dataSourceBean = sysDataSourceList.get(0);
//			}
//			System.out.println("mongoPARAMS:"+dataSourceBean.toJsonString());
			// 带回参数
			response = ResponseModel.getInstance();
			response.setData("!!! Welcome !!!基础服务平台欢迎你");
		}else{
			response = getRoleAuth2(rm);
		}
		return response;
	}
	
	/**
	 * 带回用户权限集合
	 * */
	public ResponseModel getRoleAuth(RequestModel rm ) {
		// 请求参数
		DynaBean paramBean = rm.getParamBean();
		String pid = paramBean.getStr("PID");
		String authType = paramBean.getStr("AUTHTYPE");
		DynaBean userInfo = null;
		String userCode = "";
		if(paramBean.get("USERINFO")!=null){
			userInfo = (DynaBean)paramBean.get("USERINFO");
			userCode = userInfo.getStr("USERCODE");//USERID
		}
		//用户角色查询语句
		String roleUserSql = "and PID='"+pid+"' ";
		if(userCode!=null && userCode.length()>0){
			roleUserSql = roleUserSql + "and USERID='" + userCode + "' ";
		}
		//用户权限查询语句
		String roleAuthSql = "and PID='"+pid+"' ";
		if(authType!=null && authType.length()>0){
			roleAuthSql = roleAuthSql + "and AUTHTYPE='" + authType + "' ";
		}
		// 访问库
		BaseDao dao = baseService.getBaseDao();		
		// 菜单缓存
		StringBuffer menusb = new StringBuffer("");
		// 指定用户的角色
		List<DynaBean> sysRoleUserList = dao.findWithQueryNoCache(new DynaBean("SYS_ROLEUSER", roleUserSql));
		if(sysRoleUserList !=null && sysRoleUserList.size()>0 ){
			for (DynaBean sysRoleUserBean : sysRoleUserList) {
				//指定角色的权限
				List<DynaBean> sysRoleAuthList = dao.findWithQueryNoCache(new DynaBean("SYS_ROLEAUTH",roleAuthSql+" and ROLEID='" + sysRoleUserBean.getStr("ROLEID")+ "' "));
				if(sysRoleAuthList !=null && sysRoleAuthList.size()>0 ){
					for(DynaBean sysRA : sysRoleAuthList){
						if(sysRA.getStr("CONTENT")!=null && sysRA.getStr("CONTENT").length()>0){
							menusb.append(sysRA.getStr("CONTENT"));menusb.append(",");
						}
					}
				}
			}
		}
		// 去除重复菜单
		if( menusb.length()>0 ){
			// 处理最后的 , 符号
			menusb.setLength(menusb.length()-1);
			// 处理重复菜单项
			String [] menuOld = menusb.toString().split(",");
			List<String> menuNew = new ArrayList<String>();
			boolean isExist = false;
			for (int i = 0; i < menuOld.length; i++) {
				isExist = false;
				if (i == 0) {
					menuNew.add(menuOld[i]);
					isExist = true;
				} else {
					for (int j = 0; j < menuNew.size(); j++) {
						if(menuNew.get(j).equals(menuOld[i]) ){
							//System.out.println(menuOld[i]);
							isExist = true;
							break;
						}
					}
				}
				// 是否有相同的
				if(!isExist){
					menuNew.add(menuOld[i]);
				}
			}	
			menusb.setLength(0);
			for(String menu : menuNew){
				menusb.append(menu+",");
			}
			menusb.setLength(menusb.length()-1);
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("CONTENT", menusb);
//		// 带回json格式数据
//		System.err.println("---********--- "+ JSON.toJSONString( resultMap ));
		// 带回参数
		ResponseModel response = ResponseModel.getInstance();
		response.setData(JSON.toJSONString( resultMap ));
		return response;
	}
	
	
}
