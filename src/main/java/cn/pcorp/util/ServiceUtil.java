package cn.pcorp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pcorp.model.DynaBean;

/** 
* @author panlihai E-mail:18611140788@163.com 
* @version 创建时间：2016年1月8日 上午10:43:15 
* 类说明: 
*/
/**
 * @author Administrator
 *
 */
public class ServiceUtil {
	/**
	 * 根据主数据内容,子应用id,子应用字段,关联子应用条件获取子应用数据的初始化值
	 * @param mainBean
	 * @param filter
	 * @return
	 */
	public static DynaBean getDefaultFieldValueByLinkFilter(DynaBean mainBean, String itemAppId, List<Map> fieldBeanList,
			String filter) {
		DynaBean dynaBean = new DynaBean("");
		StringBuffer sb = new StringBuffer(",");
		// 循环的把字段值放入到
		String[] flds = filter.split(itemAppId + ".");
		for (String fld : flds) {
			// 获取需要初始化的子表字段
			for (Map map : fieldBeanList) {
				DynaBean fieldBean = new DynaBean();
				fieldBean.setValues(map);
				String field = itemAppId + "." + fld;
				String fieldStr = itemAppId + "." + fieldBean.getStr("FIELDCODE");
				if (field.indexOf(fieldStr) != -1) {
					// 去除相同字段前部的过滤
					if (sb.indexOf("," + fieldBean.getStr("FIELDCODE") + ",") == -1) {
						// 此时这个字段则在条件中存在
						// 标记已经设置了初始化值
						sb.append(fieldBean.getStr("FIELDCODE")).append(",");
						// 解析条件并获得这个字段的参数
						String[] where = fld.split(":\\{");
						for (String str : where) {
							if (str.indexOf("}") != -1) {
								String[] str2 = str.split("\\}");
								dynaBean.setStr(fieldBean.getStr("FIELDCODE"), mainBean.getStr(str2[0].toUpperCase()));
							}
						}
					}
				}
			}
		}
		return dynaBean;
	}
	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2015年12月16日 下午2:06:28 方法说明:自动导入默认的字段内容;未来重构内容
	 * @param appBean 应用程序
	 * @param defBean 数据表内容
	 */
	public static List<DynaBean> getFieldBeanByAppBean(DynaBean appBean,DynaBean defBean){
		@SuppressWarnings("unchecked")
		List<DynaBean> list = (List<DynaBean>) defBean.get(BeanUtils.DEF_ALL_FIELDS);
		@SuppressWarnings("unchecked")
		List<DynaBean> pkList = (List<DynaBean>) defBean.get(BeanUtils.DEF_PK_FIELDS);
		StringBuffer sb = new StringBuffer("");
		for (DynaBean pkBean : pkList) {
			sb.append(pkBean.getStr("FIELDCODE")).append(",");
		}
		List<DynaBean> saveList = new ArrayList<DynaBean>();
		int i = 0;
		for (DynaBean bean : list) {
			DynaBean fieldBean = new DynaBean("SYS_APPFIELDS");
			fieldBean.setStr("APPID", appBean.getStr("APPID"));
			fieldBean.setStr("FIELDCODE", bean.getStr("FIELD_CODE"));
			fieldBean.setStr("FIELDNAME", bean.getStr("FIELD_NAME", bean.getStr("FIELD_CODE")));
			fieldBean.setStr("DBTYPE", bean.getStr("FIELD_TYPE"));
			fieldBean.setStr("LENGTH", bean.getStr("FIELD_LEN"));
			fieldBean.setStr("ENABLE", SyConstant.STR_YES);
			// 判断是否是主键,如果是主键这必填
			if (sb.toString().indexOf(bean.getStr("FIELD_CODE")) != -1) {
				fieldBean.setStr("ISNULL", SyConstant.STR_YES);
			} else {
				fieldBean.setStr("ISNULL", SyConstant.STR_NO);
			}
			fieldBean.setStr("KEYSEQ", SyConstant.STR_NO);
			fieldBean.setInt("SORT", i++);
			fieldBean.setStr("ENABLELOG", SyConstant.STR_NO);// 写日志
			fieldBean.setStr("SHOWLIST", SyConstant.STR_YES);// 默认列表不显示
			fieldBean.setStr("SHOWCARD", SyConstant.STR_YES);// 默认表单显示
			fieldBean.setStr("INPUTTYPE", "text");// 默认输入框
			fieldBean.setStr("ENABLESEARCH",SyConstant.STR_NO);// 写查询条件
			saveList.add(fieldBean);
		}
		return saveList;
	}
	/**
	 * @author panlihai E-mail:18611140788@163.com
	 * @version 创建时间：2015年12月16日 下午2:06:28 方法说明:自动导入默认的菜单按钮内容;未来重构内容
	 * @param appCode 应用id
	 */
	public static List<DynaBean> getSysAppButtonsByTempletModel(String appCode) {
		List<DynaBean> saveList = new ArrayList<DynaBean>();
		// BTNCARDADD 新增 cardAdd CARD 0 Y
		DynaBean dynaBean3 = new DynaBean("SYS_APPBUTTONS");
		dynaBean3.setStr("APPID", appCode);
		dynaBean3.setStr("BTNCODE", "BTNCARDADD");
		dynaBean3.setStr("BTNNAME", "新建记录");
		dynaBean3.setStr("ACTCODE", "cardAdd");
		dynaBean3.setStr("BTNTYPE", "CARD");
		dynaBean3.setInt("SORT", 1);
		dynaBean3.setStr("ENABLE", "Y");
		dynaBean3.setStr("BTNICON", "glyphicon glyphicon-file");
		saveList.add(dynaBean3);

		DynaBean dynaBean1 = new DynaBean("SYS_APPBUTTONS");
		dynaBean1.setStr("APPID", appCode);
		dynaBean1.setStr("BTNCODE", "BTNCARDSAVE");
		dynaBean1.setStr("BTNNAME", "保存记录");
		dynaBean1.setStr("ACTCODE", "cardSave");
		dynaBean1.setStr("BTNTYPE", "CARD");
		dynaBean1.setInt("SORT", 2);
		dynaBean1.setStr("ENABLE", "Y");
		dynaBean1.setStr("BTNICON", "glyphicon glyphicon-floppy-save");
		saveList.add(dynaBean1);
		DynaBean dynaBean11 = new DynaBean("SYS_APPBUTTONS");
		dynaBean11.setStr("APPID", appCode);
		dynaBean11.setStr("BTNCODE", "BTNCARDSAVENEW");
		dynaBean11.setStr("BTNNAME", "保存新建");
		dynaBean11.setStr("ACTCODE", "cardSaveNew");
		dynaBean11.setStr("BTNTYPE", "CARD");
		dynaBean11.setInt("SORT", 3);
		dynaBean11.setStr("ENABLE", "Y");
		dynaBean11.setStr("BTNICON", "glyphicon glyphicon-floppy-open");
		saveList.add(dynaBean11);
		DynaBean dynaBean12 = new DynaBean("SYS_APPBUTTONS");
		dynaBean12.setStr("APPID", appCode);
		dynaBean12.setStr("BTNCODE", "BTNCARDCOPYNEW");
		dynaBean12.setStr("BTNNAME", "保存复制");
		dynaBean12.setStr("ACTCODE", "cardSaveCopy");
		dynaBean12.setStr("BTNTYPE", "CARD");
		dynaBean12.setInt("SORT", 4);
		dynaBean12.setStr("ENABLE", "Y");
		dynaBean12.setStr("BTNICON", "glyphicon glyphicon-subtitles");
		saveList.add(dynaBean12);
		DynaBean dynaBean0 = new DynaBean("SYS_APPBUTTONS");
		dynaBean0.setStr("APPID", appCode);
		dynaBean0.setStr("BTNCODE", "BTNCARDSAVEBACK");
		dynaBean0.setStr("BTNNAME", "保存返回");
		dynaBean0.setStr("ACTCODE", "cardSaveBack");
		dynaBean0.setStr("BTNTYPE", "CARD");
		dynaBean0.setInt("SORT", 5);
		dynaBean0.setStr("ENABLE", "Y");
		dynaBean0.setStr("BTNICON", "glyphicon glyphicon-floppy-saved");
		saveList.add(dynaBean0);

		// BTNCARDBACK 返回 cardBack CARD 9 Y
		DynaBean dynaBean4 = new DynaBean("SYS_APPBUTTONS");
		dynaBean4.setStr("APPID", appCode);
		dynaBean4.setStr("BTNCODE", "BTNCARDBACK");
		dynaBean4.setStr("BTNNAME", "返回列表");
		dynaBean4.setStr("ACTCODE", "cardBack");
		dynaBean4.setStr("BTNTYPE", "CARD");
		dynaBean4.setInt("SORT", 9);
		dynaBean4.setStr("ENABLE", "Y");
		dynaBean4.setStr("BTNICON", "glyphicon glyphicon-arrow-left");
		saveList.add(dynaBean4);

		// BTNLISTONEVIEW 预览 listOneView LISTONE 9 Y
		DynaBean dynaBean5 = new DynaBean("SYS_APPBUTTONS");
		dynaBean5.setStr("APPID", appCode);
		dynaBean5.setStr("BTNCODE", "BTNLISTONEVIEW");
		dynaBean5.setStr("BTNNAME", "预览");
		dynaBean5.setStr("ACTCODE", "listOneView");
		dynaBean5.setStr("BTNTYPE", "LISTONE");
		dynaBean5.setInt("SORT", 8);
		dynaBean5.setStr("ENABLE", "Y");
		dynaBean5.setStr("BTNICON", "glyphicon glyphicon-eye-open");
		saveList.add(dynaBean5);
		// BTNLISTONEEDIT 修改 listEdit LISTONE 8 Y
		DynaBean dynaBean6 = new DynaBean("SYS_APPBUTTONS");
		dynaBean6.setStr("APPID", appCode);
		dynaBean6.setStr("BTNCODE", "BTNLISTONEEDIT");
		dynaBean6.setStr("BTNNAME", "修改");
		dynaBean6.setStr("ACTCODE", "listEdit");
		dynaBean6.setStr("BTNTYPE", "LISTONE");
		dynaBean6.setInt("SORT", 9);
		dynaBean6.setStr("ENABLE", "Y");
		dynaBean6.setStr("BTNICON", "glyphicon glyphicon-edit");
		saveList.add(dynaBean6);

		// BTNLISTONEDELETE 删除 listOneDelete LISTONE 10 Y
		DynaBean dynaBean2 = new DynaBean("SYS_APPBUTTONS");
		dynaBean2.setStr("APPID", appCode);
		dynaBean2.setStr("BTNCODE", "BTNLISTONEDELETE");
		dynaBean2.setStr("BTNNAME", "删除");
		dynaBean2.setStr("ACTCODE", "listOneDelete");
		dynaBean2.setStr("BTNTYPE", "LISTONE");
		dynaBean2.setInt("SORT", 10);
		dynaBean2.setStr("ENABLE", "Y");
		dynaBean2.setStr("BTNICON", "glyphicon glyphicon-trash");
		saveList.add(dynaBean2);
		// BTNLISTADD 新增 listAdd LIST 0 Y
		DynaBean dynaBean7 = new DynaBean("SYS_APPBUTTONS");
		dynaBean7.setStr("APPID", appCode);
		dynaBean7.setStr("BTNCODE", "BTNLISTADD");
		dynaBean7.setStr("BTNNAME", "新增");
		dynaBean7.setStr("ACTCODE", "listAdd");
		dynaBean7.setStr("BTNTYPE", "LIST");
		dynaBean7.setInt("SORT", 0);
		dynaBean7.setStr("ENABLE", "Y");
		dynaBean7.setStr("BTNICON", "glyphicon glyphicon-file");
		saveList.add(dynaBean7);
		// BTNLISTDEL 批量删除 listDelete LIST 1 Y
		DynaBean dynaBean8 = new DynaBean("SYS_APPBUTTONS");
		dynaBean8.setStr("APPID", appCode);
		dynaBean8.setStr("BTNCODE", "BTNLISTDEL");
		dynaBean8.setStr("BTNNAME", "删除");
		dynaBean8.setStr("ACTCODE", "listDelete");
		dynaBean8.setStr("BTNTYPE", "LIST");
		dynaBean8.setInt("SORT", 1);
		dynaBean8.setStr("ENABLE", "Y");
		dynaBean8.setStr("BTNICON", "glyphicon glyphicon-trash");
		saveList.add(dynaBean8);
		// BTNLISTDEL 批 listDelete LIST 1 Y
		DynaBean dynaBean9 = new DynaBean("SYS_APPBUTTONS");
		dynaBean9.setStr("APPID", appCode);
		dynaBean9.setStr("BTNCODE", "BTNLISTHELP");
		dynaBean9.setStr("BTNNAME", "WIKI");
		dynaBean9.setStr("ACTCODE", "listHelp");
		dynaBean9.setStr("BTNTYPE", "LIST");
		dynaBean9.setInt("SORT", 9);
		dynaBean9.setStr("ENABLE", "Y");
		dynaBean9.setStr("BTNICON", "glyphicon glyphicon-question-sign");
		saveList.add(dynaBean9);		
		// BTNLISTDEL 批 listDelete LIST 1 Y
		DynaBean dynaBean10 = new DynaBean("SYS_APPBUTTONS");
		dynaBean10.setStr("APPID", appCode);
		dynaBean10.setStr("BTNCODE", "BTNLISTIMPORT");
		dynaBean10.setStr("BTNNAME", "导入");
		dynaBean10.setStr("ACTCODE", "import");
		dynaBean10.setStr("BTNTYPE", "LIST");
		dynaBean10.setInt("SORT", 10);
		dynaBean10.setStr("ENABLE", "Y");
		dynaBean10.setStr("BTNICON", "glyphicon glyphicon-question-sign");		
		saveList.add(dynaBean10);		
		// 导出
		DynaBean dynaBean13 = new DynaBean("SYS_APPBUTTONS");
		dynaBean13.setStr("APPID", appCode);
		dynaBean13.setStr("BTNCODE", "BTNLISTEXPORT");
		dynaBean13.setStr("BTNNAME", "导出");
		dynaBean13.setStr("ACTCODE", "export");
		dynaBean13.setStr("BTNTYPE", "LIST");
		dynaBean13.setInt("SORT", 13);
		dynaBean13.setStr("ENABLE", "Y");
		dynaBean13.setStr("BTNICON", "glyphicon glyphicon-question-sign");
		saveList.add(dynaBean13);		
		return saveList;
	}
}
