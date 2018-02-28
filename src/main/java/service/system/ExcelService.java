package service.system;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import cn.pcorp.controllor.RequestModel;
import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.ParentService;
import cn.pcorp.util.CacheUtil;
import cn.pcorp.util.ExcelException;
import cn.pcorp.util.ExcelUtil;
import cn.pcorp.util.PageUtil;

/**
 * Created by tao on 25/5/17.
 */
public class ExcelService extends ParentService {
	

	/**
	 * @author lht
	 * @version 创建时间：2017年5月24日 下午1:19:48 方法说明: 导出excel表
	 * @param baseDao
	 * @param paramBean
	 * @param request
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public Object exportExcel(RequestModel rm) throws SQLException, Exception {
		BaseDao baseDao = rm.getBaseService().getBaseDao();
		DynaBean paramBean = rm.getParamBean();
		HttpServletRequest request = rm.getRequest();
		HttpServletResponse response = rm.getResponse();
		DynaBean dynaBean = CacheUtil.getSysapp(baseDao.getReadCache(),
				paramBean.getStr("APPID"),baseDao);
		String tableName = dynaBean.get("MAINTABLE").toString();
		Map map = new HashMap();
		List<Map> list = (List<Map>) dynaBean.get("P_APPFIELDS");
		LinkedHashMap<String, String> lh = new LinkedHashMap<>();
		for (Map maps : list) {
			lh.put((maps.get("FIELDCODE")).toString(),
					(maps.get("FIELDNAME")).toString());
		}
		// paramType 等于1的时候是台账导出
		try {

			List<DynaBean> accountList = baseDao
					.findWithQueryNoCache(new DynaBean(tableName, paramBean
							.getStr("WHERE"), "SPERIODCODE"));
			ExcelUtil.listToExcel(accountList, lh, "sheet", response, dynaBean);
			map.put("message", "成功");
			map.put("succes", true);
			return map;
		} catch (Exception e) {
			map.put("message", e.getMessage());
			map.put("succes", false);
			return map;
		}
	}

	/**
	 * @author lht
	 * @version 创建时间：2016年1月14日 下午5:10:22 方法说明:
	 * @param
	 * @param request
	 * @param paramBean
	 *            Excel导入
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public Object importExcel(BaseDao baseDao, DynaBean paramBean,
			HttpServletRequest request) throws SQLException, Exception {
		InputStream inputStream = null;
		MultipartFile file = null;
		// 创建一个通用的多部分解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// // 判断 request 是否有文件上传,即多部分请求
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 取得request中的所有文件名
			Iterator<String> iter = multiRequest.getFileNames();
			boolean checkHasFile = false;
			while (iter.hasNext()) {
				// 记录上传过程起始时的时间，用来计算上传时间
				int pre = (int) System.currentTimeMillis();
				// 取得上传文件
				file = multiRequest.getFile(iter.next());
				if (file != null) {
					// 取得当前上传文件的文件liu
					inputStream = file.getInputStream();
				}
			}
		}
		DynaBean dynaBean1 = CacheUtil.getSysapp(baseDao.getReadCache(),
				paramBean.getStr("APPID"),baseDao);
		List<Map> list = (List<Map>) dynaBean1.get("P_APPFIELDS");
		LinkedHashMap<String, String> enMap = new LinkedHashMap<>();
		String[] uniqueFields = { "ID" };
		Map typeMap = new HashMap();
		for (Map maps : list) {
			enMap.put(maps.get("FIELDNAME").toString(), maps.get("FIELDCODE")
					.toString());
			if (maps.get("DBTYPE").equals("STR")) {
				typeMap.put(maps.get("FIELDCODE"), "str");
			} else {
				typeMap.put(maps.get("FIELDCODE"), "num");
			}
		}

		String tableName = dynaBean1.getStr("MAINTABLE");
		List<DynaBean> dynaBeanLists = ExcelUtil.excelToList(paramBean,
				baseDao, file, tableName, typeMap, enMap, uniqueFields);
		return dynaBeanLists;
	}
}
