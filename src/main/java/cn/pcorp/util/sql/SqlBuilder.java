package cn.pcorp.util.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.pcorp.model.DynaBean;
import cn.pcorp.util.BeanUtils;
import cn.pcorp.util.DateUtils;
import cn.pcorp.util.SyConstant;

import com.ibatis.sqlmap.engine.execution.SqlExecutor;

/**
 * 根据各自表和字段的定义辅助生成相关的数据库执行的SQL。
 * 
 * @author panlihai
 */
public class SqlBuilder {
	/** 数据库类型：ms sql server */
	public static final String SERVER_TYPE_MSSQL = "sqlserver";
	/** 数据库类型：oracle */
	public static final String SERVER_TYPE_ORACLE = "oracle";
	/** 数据库类型：mysql */
	public static final String SERVER_TYPE_MYSQL = "mysql";
	/** 数据库类型：db2 */
	public static final String SERVER_TYPE_DB2 = "db2";
	/** 日志记录 */
	private static Log logger = LogFactory.getLog(SqlBuilder.class);
	/** 监控数据库记录数 */
	// private static final int LOG_ROW_COUNT =
	// Integer.parseInt(SysManager.getInitConfig("logRowCount", "0"));
	/** 监控查询执行时间 */
	// private static final int LOG_ROW_TIME =
	// Integer.parseInt(SysManager.getInitConfig("logRowTime", "0"));

	/** 缺省连接池当前链接的数据库类型 */
	private static String defServerType = null;

	/**
	 * 得到插入语句的SQL
	 *
	 * @param dynaBean
	 *            数据信息
	 * @param bPreStatement
	 *            是否是预编译SQL
	 * @return 插入语句SQL
	 * @throws Exception
	 *             当获取表定义信息错误时
	 */
	public static String getInsertBatSql(List<DynaBean> dynaBeanList, boolean bPreStatement, DynaBean tableDef)
			throws Exception {
		StringBuffer bfField = new StringBuffer("insert into ");
		StringBuffer bfValue = new StringBuffer(") values ");
		bfField.append(tableDef.get("TABLE_CODE")).append(" (");
		String quotes;
		String defValue;
		String fieldValue;
		List<DynaBean> tableFields = (ArrayList<DynaBean>) tableDef.get(BeanUtils.DEF_ALL_FIELDS);
		int fieldCount = tableFields.size();
		for (int i = 0; i < fieldCount; i++) {
			DynaBean tableField = (DynaBean) tableFields.get(i);
			// 大文本处理或二进制处理
			bfField.append(tableField.get("FIELD_CODE")).append(",");				
		}		
		for (DynaBean dynaBean : dynaBeanList) {
			if(dynaBean==null){
				continue;
			}
			dynaBean.setStr("ID", SyConstant.getUUID());
			bfValue.append("(");
			for (int i = 0; i < fieldCount; i++) {
				DynaBean tableField = (DynaBean) tableFields.get(i);				
				if (!bPreStatement) {
					defValue = tableField.getStr("FIELD_DEFAULT", "");
					fieldValue = dynaBean.getStr(tableField.getStr("FIELD_CODE"), defValue).replaceAll("&“", "\"")
							.replaceAll("&：", ":").replaceAll("&，", ",").replaceAll("&；", ";").replaceAll("&＼", "\\")
							.replaceAll("&＇", "'").replaceAll("&［", "[").replaceAll("&］", "]").replaceAll("&｛", "{")
							.replaceAll("&｝", "}");
					if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_NUM)) {
						quotes = "";
						if (fieldValue.length() == 0) {
							fieldValue = "0";
						}
					} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_DATETIME)) { // 文本或者大文本
						quotes = "";
						if (defValue.equals("DATETIME")) {
							fieldValue = DateUtils.getTimestamp() + "";
						} else if (defValue.equals("DATE")) {
							fieldValue = DateUtils.getTimestamp() + "";
						}
						if (fieldValue.length() == 0) {
							quotes = "";
							fieldValue = "null";
						}
					} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_DATE)) { // 文本或者大文本
						quotes = "";
						if (defValue.equals("DATETIME")) {
							fieldValue = DateUtils.getTimestamp() + "";
						} else if (defValue.equals("DATE")) {
							fieldValue = DateUtils.getTimestamp() + "";
						}
						if (fieldValue.length() == 0) {
							quotes = "";
							fieldValue = "null";
						}
					} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_BLOB)) {
						quotes = "";
						fieldValue = "empty_blob()";
					} else {
						quotes = "\"";
					}
					if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_PIC)) { // 大文本
						dynaBean.set(BeanUtils.KEY_LONG_FIELD, tableField.getStr("FIELD_CODE"));
						fieldValue = ""; // 暂时先清除文本的内容
					} else if (tableField.getStr("GEN_CODE", "").length() > 0) { // 设定了自动递增字段
						if (fieldValue.equals(defValue)) { // 此项没有提前设定值则获取一个自动递增的值
							dynaBean.set(tableField.getStr("FIELD_CODE"), fieldValue);
						}
					}
					bfValue.append(quotes).append(fieldValue).append(quotes).append(",");
				} else {
					if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_BLOB)) {
						bfValue.append("empty_blob(),");
					} else {
						bfValue.append("?,");
					}
				}
			}
			bfValue.setLength(bfValue.length() - 1);
			bfValue.append("),");
		}
		// 去掉逗号
		bfField.setLength(bfField.length() - 1);
		bfValue.setLength(bfValue.length() - 1);
		return bfField.append(bfValue).toString();
	}

	/**
	 * 得到插入语句的SQL
	 *
	 * @param dynaBean
	 *            数据信息
	 * @param bPreStatement
	 *            是否是预编译SQL
	 * @return 插入语句SQL
	 * @throws Exception
	 *             当获取表定义信息错误时
	 */
	public static String getInsertSql(DynaBean dynaBean, boolean bPreStatement, DynaBean tableDef) throws Exception {
		StringBuffer bfField = new StringBuffer("insert into ");
		StringBuffer bfValue = new StringBuffer(") values (");
		bfField.append(tableDef.get("TABLE_CODE")).append("(");
		String quotes;
		String defValue;
		String fieldValue;
		List<DynaBean> tableFields = (ArrayList<DynaBean>) tableDef.get(BeanUtils.DEF_ALL_FIELDS);
		int fieldCount = tableFields.size();

		for (int i = 0; i < fieldCount; i++) {
			DynaBean tableField = (DynaBean) tableFields.get(i);
			// 大文本处理或二进制处理
			bfField.append("").append(tableField.get("FIELD_CODE")).append(",");
			if (!bPreStatement) {
				defValue = tableField.getStr("FIELD_DEFAULT", "");
				fieldValue = dynaBean.getStr(tableField.getStr("FIELD_CODE"), defValue).replaceAll("&“", "\"")
						.replaceAll("&：", ":").replaceAll("&，", ",").replaceAll("&；", ";").replaceAll("&＼", "\\")
						.replaceAll("&＇", "'").replaceAll("&［", "[").replaceAll("&］", "]").replaceAll("&｛", "{")
						.replaceAll("&｝", "}");
				if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_NUM)) {
					quotes = "";
					if (fieldValue.length() == 0) {
						fieldValue = "0";
					}
				} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_DATETIME)) { // 文本或者大文本
					quotes = "";
					if (defValue.equals("DATETIME")) {
						fieldValue = DateUtils.getTimestamp() + "";
					} else if (defValue.equals("DATE")) {
						fieldValue = DateUtils.getTimestamp() + "";
					}
					if (fieldValue.length() == 0) {
						quotes = "";
						fieldValue = "null";
					}
				} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_DATE)) { // 文本或者大文本
					quotes = "";
					if (defValue.equals("DATETIME")) {
						fieldValue = DateUtils.getTimestamp() + "";
					} else if (defValue.equals("DATE")) {
						fieldValue = DateUtils.getTimestamp() + "";
					}
					if (fieldValue.length() == 0) {
						quotes = "";
						fieldValue = "null";
					}
				} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_BLOB)) {
					quotes = "";
					fieldValue = "empty_blob()";
				} else {
					quotes = "'";
				}
				if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_PIC)) { // 大文本
					dynaBean.set(BeanUtils.KEY_LONG_FIELD, tableField.getStr("FIELD_CODE"));
					fieldValue = ""; // 暂时先清除文本的内容
				} else if (tableField.getStr("GEN_CODE", "").length() > 0) { // 设定了自动递增字段
					if (fieldValue.equals(defValue)) { // 此项没有提前设定值则获取一个自动递增的值
						// fieldValue =
						// SerialGenerator.getSerial(tableField.getStr("GEN_CODE"));
						dynaBean.set(tableField.getStr("FIELD_CODE"), fieldValue);
					}
				}
				if(fieldValue.indexOf("\'")!=-1){
					fieldValue=fieldValue.replaceAll("\'", "\'\'");
				}
				bfValue.append(quotes).append(fieldValue).append(quotes).append(",");
			} else {
				if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_BLOB)) {
					bfValue.append("empty_blob(),");
				} else {
					bfValue.append("?,");
				}
			}
		}

		// 去掉逗号
		bfField.setLength(bfField.length() - 1);
		bfValue.setLength(bfValue.length() - 1);
		return bfField.append(bfValue).append(")").toString();
	}

	/**
	 * 得到插入语句的SQL
	 *
	 * @param dynaBean
	 *            数据信息
	 * @param bPreStatement
	 *            是否是预编译SQL
	 * @param bKeySelect
	 *            是否基于主键的删除，如果不是则从$WHERE$变量中取值
	 * @return 插入语句SQL
	 * @throws Exception
	 *             当获取表定义信息错误时
	 */
	public static String getUpdateSql(DynaBean dynaBean, boolean bPreStatement, boolean bKeySelect, DynaBean tableDef)
			throws Exception {
		StringBuffer bfSql = new StringBuffer("update ");
		bfSql.append(tableDef.get("TABLE_CODE")).append(" set ");
		String quotes;
		@SuppressWarnings("unchecked")
		List<DynaBean> tableFields = (List<DynaBean>) tableDef.get(BeanUtils.DEF_ALL_FIELDS);
		int fieldCount = tableFields.size();
		for (int i = 0; i < fieldCount; i++) {
			DynaBean tableField = (DynaBean) tableFields.get(i);
			String fieldValue = dynaBean.getStr(tableField.getStr("FIELD_CODE"));
			if (fieldValue == null) { // 如果数据不存在就不执行update
				continue;
			}
			bfSql.append(tableField.get("FIELD_CODE")).append("=");
			if (!bPreStatement) {
				if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_NUM)) {
					quotes = "";
					if (fieldValue.length() == 0) {
						fieldValue = "0";
					}
				} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_DATETIME)) { // 文本或者大文本
					quotes = "";
					if (fieldValue.length() == 0) {
						quotes = "";
						fieldValue = "";
					}
				} else if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_DATE)) { // 文本或者大文本
					quotes = "";
					if (fieldValue.length() == 0) {
						quotes = "";
						fieldValue = "";
					}
				} else {
					quotes = "'";
				}
				if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_PIC)) { // 大文本
					dynaBean.set(BeanUtils.KEY_LONG_FIELD, tableField.getStr("FIELD_CODE"));
					fieldValue = ""; // 暂时先清除文本的内容
				}
				// 转义
				fieldValue = fieldValue.replaceAll("\'", "\'\'");
				bfSql.append(quotes).append(fieldValue).append(quotes).append(",");
			} else {
				bfSql.append("?,");
			}
		}

		// 去掉最后一个逗号
		bfSql.setLength(bfSql.length() - 1);
		bfSql.append(" where 1=1 ");
		// 得到基于主键的where条件
		bfSql.append(dynaBean.getStr(BeanUtils.KEY_WHERE, ""));
		return bfSql.toString();
	}

	/**
	 * 得到删除语句的SQL
	 *
	 * @param dynaBean
	 *            数据信息
	 * @param bPreStatement
	 *            是否是预编译SQL
	 * @param bKeySelect
	 *            是否基于主键的删除，如果不是则从$WHERE$变量中取值
	 * @return 插入语句SQL
	 * @throws Exception
	 *             当获取表定义信息错误时
	 */
	public static String getDeleteSql(DynaBean dynaBean, boolean bPreStatement, boolean bKeySelect, DynaBean tableDef)
			throws Exception {
		StringBuffer bfSql = new StringBuffer("delete from ");
		bfSql.append(tableDef.get("TABLE_CODE"));
		bfSql.append(" where 1=1 ");
		if (dynaBean.getStr(BeanUtils.KEY_WHERE, "").length() == 0) { // 自动拼装Where条件
			dynaBean.set(BeanUtils.KEY_WHERE, SqlBuilder.getWhere(dynaBean, tableDef).toString());
		}
		bfSql.append(dynaBean.getStr(BeanUtils.KEY_WHERE, ""));
		return bfSql.toString();
	}

	/**
	 * 得到查询语句的SQL
	 *
	 * @param dynaBean
	 *            数据信息
	 * @param bPreStatement
	 *            是否是预编译SQL
	 * @param bKeySelect
	 *            是否基于主键的操作，如果不是则从$WHERE$变量中取值
	 * @return 查询语句SQL
	 * @throws Exception
	 *             当获取表定义信息错误时
	 */
	public static String getSelectCountSql(DynaBean dynaBean, DynaBean tableDef) throws Exception {
		StringBuffer bfSql = new StringBuffer("select count(1) ");
		bfSql.append(" from ").append(dynaBean.get(BeanUtils.KEY_TABLE_CODE)).append(" where 1=1 ");
		// 判断是否设定了Where参数,如果没有则手工生成对应的Where条件
		if (dynaBean.getStr(BeanUtils.KEY_WHERE, "").length() == 0) {
			dynaBean.set(BeanUtils.KEY_WHERE, getWhere(dynaBean, tableDef).toString());
		}
		bfSql.append(dynaBean.getStr(BeanUtils.KEY_WHERE, ""));
		if (dynaBean.getStr(BeanUtils.KEY_GROUP, "").length() > 0) {
			bfSql.append(" group by ").append(dynaBean.getStr(BeanUtils.KEY_GROUP));
		}
		return bfSql.toString();
	}

	/**
	 * 得到查询语句的SQL select * from 表名 表名__T
	 *
	 * @param dynaBean
	 *            数据信息
	 * @param bPreStatement
	 *            是否是预编译SQL
	 * @param bKeySelect
	 *            是否基于主键的操作，如果不是则从$WHERE$变量中取值
	 * @return 查询语句SQL
	 * @throws Exception
	 *             当获取表定义信息错误时
	 */
	public static String getSelectSql(DynaBean dynaBean, DynaBean tableDef) throws Exception {
		StringBuffer bfSql = new StringBuffer("select ");
		if (dynaBean.getStr(BeanUtils.KEY_SELECT, "").length() == 0) {
			bfSql.append(tableDef.getStr(BeanUtils.KEY_SELECT));
		} else {
			bfSql.append(dynaBean.getStr(BeanUtils.KEY_SELECT));
		}
		bfSql.append(" from ").append(dynaBean.get(BeanUtils.KEY_TABLE_CODE)).append(" ")
				.append(dynaBean.get(BeanUtils.KEY_TABLE_CODE)).append("__T").append(" where 1=1 ");
		// 判断是否设定了Where参数,如果没有则手工生成对应的Where条件
		if (dynaBean.getStr(BeanUtils.KEY_WHERE, "").length() == 0) {
			dynaBean.set(BeanUtils.KEY_WHERE, getWhere(dynaBean, tableDef).toString());
		}
		bfSql.append(dynaBean.getStr(BeanUtils.KEY_WHERE, ""));
		if (dynaBean.getStr(BeanUtils.KEY_GROUP, "").length() > 0) {
			bfSql.append(" group by ").append(dynaBean.getStr(BeanUtils.KEY_GROUP));
		}
		if (dynaBean.getStr(BeanUtils.KEY_ORDER, "").length() > 0) {
			bfSql.append(" order by ").append(dynaBean.getStr(BeanUtils.KEY_ORDER));
		}
		StringBuffer sb = new StringBuffer("");
		// mysql分页处理
		if (dynaBean.getStr(BeanUtils.KEY_PAGE_COUNT, "").length() > 0
				&& dynaBean.getStr(BeanUtils.KEY_PAGE_SIZE, "").length() > 0) {
			int start= (dynaBean.getInt(BeanUtils.KEY_PAGE_COUNT)-1) * dynaBean.getInt(BeanUtils.KEY_PAGE_SIZE);
			int end = (dynaBean.getInt(BeanUtils.KEY_PAGE_COUNT)) * dynaBean.getInt(BeanUtils.KEY_PAGE_SIZE);
			sb.append("select * from (select A.*,ROWNUM RN FROM (").append(bfSql.toString()).append(") A WHERE ROWNUM<=").append(end).append(") WHERE RN>=").append(start);
			return sb.toString();
		}
		return bfSql.toString();
	}

	/**
	 * 得到基于主键的Where限定语句，不带where限定词本身,从and开始
	 * 
	 * @param dynaBean
	 *            数据信息
	 * @param tableDef
	 *            数据表的定义信息
	 * @return 基于主键的Where限定语句，不带where限定条件本身,从and开始
	 */
	public static StringBuffer getWhere(DynaBean dynaBean, DynaBean tableDef) {
		StringBuffer bfSql = new StringBuffer("");
		String quotes;
		String value;
		boolean bWhere;
		// 基于所有字段进行Where条件的生成
		@SuppressWarnings("unchecked")
		ArrayList<DynaBean> fieldList = (ArrayList<DynaBean>) tableDef.get(BeanUtils.DEF_ALL_FIELDS);
		DynaBean tableField;
		for (int i = 0; i < fieldList.size(); i++) {
			tableField = (DynaBean) fieldList.get(i);
			if (tableField.getStr("FIELD_TYPE").equals(SyConstant.DATA_TYPE_STR)) {
				quotes = "'";
				value = dynaBean.getStr(tableField.getStr("FIELD_CODE"), "");
				if (value.length() == 0) { // 是缺省值，则不进行Where过滤
					bWhere = false;
				} else {
					bWhere = true;
				}
			} else {
				quotes = "";
				value = dynaBean.getStr(tableField.getStr("FIELD_CODE"), "0");
				if (value.equals("0")) { // 是缺省值，则不进行Where过滤
					bWhere = false;
				} else {
					bWhere = true;
					if (BeanUtils.KEY_VALUE_ZERO.equals(value)) { // 如果为系统设定的数字零
						value = "0";
					}
				}
			}
			if (bWhere) { // 设定了相关值，可以进行Where过滤
				bfSql.append(" and ").append(tableField.get("FIELD_CODE")).append("=");
				bfSql.append(quotes).append(value).append(quotes);
			}
		}
		return bfSql;
	}

	/**
	 * 得到对应数据库链接的数据库服务器类型
	 * 
	 * @param conn
	 *            数据库链接，如果为null,则使用缺省数据库链接
	 * @return 数据库类型
	 */
	public static String getServerType(Connection conn) {
		boolean ownConn;
		String serverType = "";
		try {

			DatabaseMetaData dbmd = conn.getMetaData();
			if (dbmd.getURL().indexOf(SERVER_TYPE_MSSQL) >= 0) {
				serverType = SERVER_TYPE_MSSQL;
			} else if (dbmd.getURL().indexOf(SERVER_TYPE_ORACLE) >= 0) {
				serverType = SERVER_TYPE_ORACLE;
			} else if (dbmd.getURL().indexOf(SERVER_TYPE_DB2) >= 0) {
				serverType = SERVER_TYPE_DB2;
			} else {
				serverType = SERVER_TYPE_MYSQL;
			}
			dbmd = null;
		} catch (Exception e) {
			logger.error("getServerType ERROR!", e);
		}
		return serverType;
	}

	/**
	 * 得到缺省数据库链接的数据服务器类型。
	 * 
	 * @return 数据库服务器类型，支持的类型有：sqlserver、oracle
	 */
	public static String getDefaultServerType(Connection conn) {
		if (defServerType == null) {
			defServerType = getServerType(conn);
		}
		return defServerType;
	}

	/**
	 * 根据表编码对应的表定义信息，如果缓存中没有，则从数据库中获取
	 * 
	 * @param tableCode
	 *            表编码（即数据库的表名）
	 * @return 根据表编码对应的表定义信息
	 * @throws Exception
	 *             当获取表定义信息错误时
	 */
	public static DynaBean getTableDef(String tableCode, Connection con) throws Exception {
		tableCode = tableCode.toUpperCase();
		// 必须是一个有效的TABLECODE
		if ((tableCode == null) || (tableCode.length() == 0) || (tableCode.equals("null"))) {
			throw new Exception("不存在此表");
		}
		DynaBean tableDefBean = new DynaBean();
		List<DynaBean> fieldList = impTableDef(tableCode, con);
		// 得到字段定义信息:全部字段，主键字段，非主键字段
		ArrayList<DynaBean> pkFields = new ArrayList<DynaBean>();
		ArrayList<DynaBean> npkFields = new ArrayList<DynaBean>();
		StringBuffer blobFields = new StringBuffer("");
		StringBuffer selectFields = new StringBuffer("");
		for (DynaBean tableField : fieldList) {
			if (tableField.getStr("FIELD_PKEY", SyConstant.STR_NO).equals(SyConstant.STR_YES)) {
				pkFields.add(tableField);
			} else if (tableField.getStr("FIELD_TYPE").equals("BLOB")) {
				blobFields.append(tableField.getStr("FIELD_CODE") + "~");
			} else {
				npkFields.add(tableField);
			}
			selectFields.append(tableField.getStr("FIELD_CODE")).append(",");
		}
		if (selectFields.length() > 1) {
			selectFields.setLength(selectFields.length() - 1);
		}
		tableDefBean.set(BeanUtils.KEY_SELECT, selectFields.toString());
		tableDefBean.set(BeanUtils.KEY_TABLE_CODE, tableCode);
		tableDefBean.set(BeanUtils.DEF_ALL_FIELDS, fieldList);
		tableDefBean.set(BeanUtils.DEF_PK_FIELDS, pkFields);
		tableDefBean.set(BeanUtils.DEF_NPK_FIELDS, npkFields);
		tableDefBean.set(BeanUtils.DEF_BLOB_FIELDS, blobFields.toString());
		return tableDefBean;
	}

	/**
	 * 导入所有表结构信息
	 * 
	 * @param conn
	 * @return
	 */
	public static Map<String, Object> impTableList(Connection conn) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			String dbUserName = dbmd.getUserName();
			String jdbcURL = dbmd.getURL();
			if (jdbcURL.indexOf("sqlserver") > 0) { // 特殊处理mssqlserver的情况，缺省owner都为dbo
				dbUserName = "dbo";
			} else { // oracle 需要大写
				dbUserName = dbUserName.toUpperCase();
			}
			String[] tableType = { "TABLE", "VIEW" };
			ResultSet rs = dbmd.getTables(null, dbUserName, "%", tableType);
			while (rs.next()) {
				map.put(rs.getString(3).toUpperCase(), impTableDef(rs.getString(3), conn));
			}
			rs.close();
			dbmd = null;
		} catch (Exception e) {
			logger.error("impTables Error!", e);
		}
		return map;
	}

	/**
	 * 根据表的信息导入所对应的数据库定义的字段。
	 * 
	 * @param tableCode
	 *            表编码
	 * @return 导入的字段数
	 */
	public static List<DynaBean> impTableDef(String tableCode, Connection conn) {
		List<DynaBean> fieldList = new ArrayList<DynaBean>();
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			String dbUserName = dbmd.getUserName();
			String jdbcURL = dbmd.getURL();
			if (jdbcURL.indexOf("sqlserver") > 0) { // 特殊处理mssqlserver的情况，缺省owner都为dbo
				dbUserName = "dbo";
			} else if (jdbcURL.indexOf("oracle") > 0) { // 特殊处理mssqlserver的情况，缺省owner都为dbo
				dbUserName = dbUserName.toUpperCase();
			}
			ResultSet rs = dbmd.getColumns(null, dbUserName, tableCode, "%");
			try {
				while (rs.next()) {
					DynaBean fieldBean = toFieldBean(rs, conn);
					fieldList.add(fieldBean);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("imp fields error", e);
				throw new Exception(e.getLocalizedMessage());
			}
			rs.close();
			dbmd = null;
		} catch (Exception e) {
			logger.error("importTableFields Error!", e);
		}
		return fieldList;
	}

	/**
	 * 将当前JDBC字段记录元数据转换为字段bean
	 * 
	 * @param rs
	 *            字段结果集
	 * @return 字段bean
	 * @throws SQLException
	 *             例外
	 */
	public static DynaBean toFieldBean(ResultSet rs, Connection conn) throws SQLException {
		DynaBean fieldBean = new DynaBean();
		fieldBean.set("FIELD_CODE", rs.getString(4).toUpperCase()); // COLUMN_NAME
		fieldBean.set("FIELD_LEN", rs.getString(7)); // COLUMN_SIZE
		String fieldType = SyConstant.DATA_TYPE_STR;
		String typeName = rs.getString(6).toUpperCase();
		if (typeName.equals("NUMERIC") // MSSQL
				|| typeName.equals("NUMBER") // ORACLE
				|| typeName.equals("LONG") // ORACLE
				|| typeName.equals("DOUBLE") // ORACLE
				|| typeName.equals("DECIMAL") // MYSQL\DB2
				|| typeName.equals("BIGINT") // DB2
				|| typeName.equals("SMALLINT") // DB2
		) {
			fieldType = SyConstant.DATA_TYPE_NUM;
		} else if (typeName.equals("VARCHAR2")) {
			fieldType = SyConstant.DATA_TYPE_STR;
		} else if (typeName.equals("DATE")) {
			fieldType = SyConstant.DATA_TYPE_DATE;
		} else if (typeName.equals("DATETIME")) {
			fieldType = SyConstant.DATA_TYPE_DATETIME;
		} else if (typeName.equals("BLOB")) {
			fieldType = SyConstant.DATA_TYPE_BLOB;
		}
		fieldBean.set("FIELD_TYPE", fieldType);
		if (fieldType.equals(SyConstant.DATA_TYPE_NUM)) {
			fieldBean.set("FIELD_DEC", rs.getString(9)); // DECIMAL_DIGITS
			fieldBean.set("FIELD_DEFAULT", "0");
		} else {
			fieldBean.set("FIELD_DEC", ""); // DECIMAL_DIGITS
			fieldBean.set("FIELD_DEFAULT", "");
		}
		// 主键字段的获取暂时由非空字段得到
		if (rs.getString(18).toUpperCase().trim().equals("NO")) { // IS_NULLABLE
			fieldBean.set("FIELD_PKEY", SyConstant.STR_YES);
		} else {
			fieldBean.set("FIELD_PKEY", SyConstant.STR_NO);
		}
		String fieldName = rs.getString("REMARKS");
		if (fieldName != null) {
			fieldBean.set("FIELD_NAME", fieldName);
		}
		return fieldBean;
	}
	public static void main(String args[]){
		String s ="''";
		s=s.replaceAll("\'", "\'\'");
		System.out.println(s);
	}
}
