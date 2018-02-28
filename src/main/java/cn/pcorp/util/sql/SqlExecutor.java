/*
 * Copyright (c) Nap All rights reserved.
 */
package cn.pcorp.util.sql;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.bcel.classfile.Constant;

import cn.pcorp.model.DynaBean;
import cn.pcorp.util.BeanUtils;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;

/**
 * 数据处理器，实现数据的查询、修改和删除等SQL语句的执行，并支持分页查询。
 *
 * @author Nap
 */
public class SqlExecutor extends Object {
	/** 数据库类型：ms sql server */
	public static final String SERVER_TYPE_MSSQL = "sqlserver";
	/** 数据库类型：oracle */
	public static final String SERVER_TYPE_ORACLE = "oracle";
	/** 数据库类型：mysql */
	public static final String SERVER_TYPE_MYSQL = "mysql";
	/** 数据库类型：db2 */
	public static final String SERVER_TYPE_DB2 = "db2";
	/** 日志记录 */
	private static Log logger = LogFactory.getLog(SqlExecutor.class);
	/** 监控数据库记录数 */
	// private static final int LOG_ROW_COUNT =
	// Integer.parseInt(SysManager.getInitConfig("logRowCount", "0"));
	/** 监控查询执行时间 */
	// private static final int LOG_ROW_TIME =
	// Integer.parseInt(SysManager.getInitConfig("logRowTime", "0"));

	/** 缺省连接池当前链接的数据库类型 */
	private static String defServerType = null;

	/**
	 * 根据查询条件得到全部数据记录。
	 *
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            Sql语句
	 *
	 * @return DataSet 查询结果集
	 *
	 * @throws SQLException
	 *             数据库或SQL执行错误
	 */
	public static DynaBean query(Connection conn, String sql) throws SQLException {
		return query(conn, sql, 0, -1);
	}

	/**
	 * 根据查询条件得到指定页数、指定返回数量的数据记录。
	 *
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            Sql语句
	 * @param pageNum
	 *            指定页数,从1开始
	 * @param count
	 *            指定数量的结果
	 *
	 * @return DataSet 查询结果集
	 *
	 * @throws SQLException
	 *             数据库或SQL执行错误
	 */
	public static DynaBean queryPage(Connection conn, String sql, int pageNum, int count) throws SQLException {
		int offset;

		if (count <= 0) {
			count = -1;
		}

		if (pageNum <= 0) {
			offset = 0;
		} else {
			offset = ((pageNum - 1) * count) + 1;
		}

		return query(conn, sql, offset, count);
	}

	/**
	 * 根据查询条件得到指定位置、指定返回数量的数据记录。
	 *
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            Sql语句
	 * @param offset
	 *            开始位置偏移量
	 * @param count
	 *            返回结果数
	 *
	 * @return DataSet 查询结果集
	 *
	 * @throws SQLException
	 *             数据库或SQL执行错误
	 */
	public static DynaBean query(Connection conn, String sql, int offset, int count) throws SQLException {
		boolean rangeQuery = false;
		boolean canDoWhile = true;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
		try {
			if ((sql == null) || (sql.trim().length() == 0)) {
				return null;
			}

			if ((offset != 0) && (count > 0)) {
				rangeQuery = true;
			}

			if (rangeQuery) {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			}
			rs = stmt.executeQuery(sql);
			if (rangeQuery) { // 如果分页查询，则定位到对应的位置
				rs.absolute(offset);
			}
			if (rangeQuery) {
				try {
					if (!rs.absolute(offset)) {
						canDoWhile = false;
					}
				} catch (SQLException se) {
					canDoWhile = false;
				}
			} else {
				if (!rs.next()) {
					canDoWhile = false;
				}
			}
			DynaBean dynaBean = new DynaBean();
			DynaBean rowBean;
			String value;
			StringBuffer keyCode = new StringBuffer();
			String tableCode = sql.replaceFirst(".*from\\s(\\w+).*", "$1");
			long rowCount = 0;
			ArrayList rows = new ArrayList();
			if (canDoWhile) {
				metaData = rs.getMetaData();
				int colCount = metaData.getColumnCount();
				String[] columns = new String[colCount];
				do {
					rowBean = new DynaBean();
					keyCode.setLength(0);
					for (int i = 1; i <= colCount; i++) {
						// 判断所查询字段是否CLOB类型
						if (metaData.getColumnType(i) == Types.CLOB) {
							value = getCLOBContent(rs, i).toString();
						} else if (metaData.getColumnType(i) == Types.BLOB) {
							value = getBLOBContent(rs, i).toString();
						} else {
							value = rs.getString(i);
						}

						if (metaData.isNullable(i) == ResultSetMetaData.columnNoNulls) {
							keyCode.append(value).append(0);
						} else { // 由于系统数据存在null的情况,所以增加此类数据的判断,将来如果确保不存在null,可以去除此判断
							if (((metaData.getColumnType(i) == Types.INTEGER)
									|| (metaData.getColumnType(i) == Types.SMALLINT)
									|| (metaData.getColumnType(i) == Types.NUMERIC)
									|| (metaData.getColumnType(i) == Types.DECIMAL))
									&& ((value == null) || (value.length() == 0))) {
								value = "0";
							} else if (value == null) {
								value = "";
							}
						}
						if (rowCount == 0) {
							columns[i - 1] = metaData.getColumnName(i).toUpperCase();
						}
						// 判断所查询字段是否CLOB类型
						if (metaData.getColumnType(i) == Types.CLOB) {
							rowBean.set(BeanUtils.KEY_LONG_FIELD, columns[i - 1]);
						} else if (metaData.getColumnType(i) == Types.BLOB) {
							// rowBean.set(BeanUtils.KEY_BLOB_FIELD,columns[i -
							// 1]);
						}
						rowBean.set(columns[i - 1], value);
					}
					rowBean.set(BeanUtils.KEY_TABLE_CODE, tableCode);
					rowBean.setLong(BeanUtils.KEY_ROW_NUM, rowCount);
					rows.add(rowBean);
					rowCount++;
					if ((rowCount >= count) && (count > 0)) { // 只返回分页限定的记录数
						break;
					}
				} while (rs.next());
				dynaBean.set(BeanUtils.KEY_COLUMNS, columns);
			}
			// 计算总记录数
			if (rowCount >= count) { // 只有当数据量超出分页设定时才单独计算总记录数
				if (rangeQuery && (offset == 1)) {
					if (getDefaultServerType(conn).equals(SERVER_TYPE_ORACLE)) {
						rowCount = querySelectCount(conn, sql, stmt);
					} else {
						rs.last();
						rowCount = rs.getRow();
					}
				} // end if
			} // end if
				// 设置相关属性值
			dynaBean.set(BeanUtils.KEY_ROWSET, rows);
			dynaBean.set(BeanUtils.KEY_PAGE_COUNT, String.valueOf(rows.size()));
			dynaBean.set(BeanUtils.KEY_ALL_COUNT, String.valueOf(rowCount));
			dynaBean.set(BeanUtils.KEY_SQL, sql);
			// 以次关闭数据库资源
			if (rs != null) {
				rs.close();
			}

			if (stmt != null) {
				stmt.close();
			}
			// if (logger.isDebugEnabled()) { //增加判断，提高效率
			// logger.debug(sql + " >>" + rowCount + "Time Spend:" + (endTime -
			// startTime));
			// }
			return dynaBean;
		} catch (Exception e) {
			throw new SQLException(e.getMessage() + sql, sql);
		} finally {

		}
	}

	public static void main(String args[]) {

	}

	/**
	 * 从数据库中提取CLOB类型字段的内容并转换为字符串
	 * 
	 * @param rs
	 *            数据库ResultSet,含有CLOB类型的字段
	 * @param clobidx
	 *            含有CLOB类型字段在ResultSet中的索引
	 * @return 取出的字符内容
	 * @throws SQLException当数据库错误或SQL语句执行错误时抛出
	 */
	public static StringBuffer getCLOBContent(ResultSet rs, int clobidx) throws SQLException {
		logger.debug("开始获取CLOB内容,index=" + clobidx);
		StringBuffer sb = new StringBuffer();
		oracle.sql.CLOB clobField = ((OracleResultSet) rs).getCLOB(clobidx);
		long clen = 0;
		if (clobField == null) { // 判断CLOB字段值是否为null
			clen = 0;
			sb = sb.append(" ");
		} else {
			clen = clobField.length();
			logger.debug("length_____" + clen);
			char clobArray[] = new char[(int) clen];
			int readednum = clobField.getChars(1, (int) clen, clobArray);
			sb.append(clobArray);
		}
		// logger.debug("读出的CLOB内容为[\r\n"+sb.toString()+"]");
		return sb;
	}

	/**
	 * 从数据库中提取CLOB类型字段的内容并转换为字符串
	 * 
	 * @param rs
	 *            数据库ResultSet,含有CLOB类型的字段
	 * @param clobidx
	 *            含有CLOB类型字段在ResultSet中的索引
	 * @return 取出的字符内容
	 * @throws SQLException当数据库错误或SQL语句执行错误时抛出
	 */
	public static StringBuffer getBLOBContent(ResultSet rs, int clobidx) throws SQLException {
		logger.debug("开始获取BLOB内容,index=" + clobidx);
		StringBuffer sb = new StringBuffer();
		oracle.sql.BLOB clobField = ((OracleResultSet) rs).getBLOB(clobidx);
		long clen = 0;
		if (clobField == null) { // 判断CLOB字段值是否为null
			clen = 0;
			sb = sb.append(" ");
		} else {
			clen = clobField.length();
			logger.debug("length_____" + clen);
			byte clobArray[] = new byte[(int) clen];
			int readednum = clobField.getBytes(1, (int) clen, clobArray);
			try {
				sb.append(new String(clobArray, "utf-8"));
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		}
		// logger.debug("读出的BLOB内容为[\r\n"+sb.toString()+"]");
		return sb;
	}

	public static String getBLOBC(ResultSet rs, int fieldindex) {
		StringBuffer newStr = new StringBuffer(""); // 返回字符串
		byte[] bytes; // BLOB临时存储字节数组
		long bloblength;
		int i = 1; // 循环变量
		try {
			while (rs.next()) {
				BLOB blob = (BLOB) rs.getBlob(fieldindex);
				byte[] msgContent = blob.getBytes(); // BLOB转换为字节数组
				bloblength = blob.length(); // 获取BLOB长度
				if (msgContent == null || bloblength == 0) // 如果为空，返回空值
				{
					return "";
				} else {
					while (i < bloblength) // 循环处理字符串转换，每次1024；Oracle字符串限制最大4k
					{
						bytes = blob.getBytes(i, 1024);
						i = i + 1024;
						// newStr.append(new String(bytes, "gb2312"));
						newStr.append(bytes);
					}
				}
			}
		} catch (Exception ex) {

		}
		return newStr.toString();
	}

	/**
	 * 执行数据库修改或删除语句，得到修改或删除的记录数。
	 *
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            SQL语句
	 *
	 * @return 修改或删除的记录数
	 *
	 * @exception SQLException
	 *                当数据库错误或SQL语句执行错误时
	 */
	public static int update(Connection conn, String sql) throws SQLException {
		int count = 0;
		boolean ownConn;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			long startTime = System.currentTimeMillis();
			count = Math.abs(stmt.executeUpdate(sql));
			long endTime = System.currentTimeMillis();
			if (logger.isDebugEnabled()) { // 增加判断，提高效率
				logger.debug(sql + " >>" + count + "Time Spend:" + (endTime - startTime));
			}
			stmt.close();

			return count;
		} catch (SQLException eq) {
			eq.printStackTrace();
			throw new SQLException(eq.getMessage() + " SQL: " + sql, sql);
		} finally {

		}
	}

	/**
	 * 通过select count 方式执行数据库查询语句，快速得到结果集的数量，适用于Oracle。
	 * 
	 * @param conn
	 *            数据库连接(必须有可用的链接）
	 * @param sql
	 *            查询语句
	 * @param stmt
	 *            statement对应，如果为空，则自己创建一个
	 * @return 符合条件的数据数
	 *
	 * @throws SQLException
	 *             当数据库错误或SQL语句执行错误时
	 */
	private static long querySelectCount(Connection conn, String sql, Statement stmt) throws SQLException {
		long result = 0;
		boolean ownStmt = false;
		ResultSet jdbcRs = null;
		try {
			if (stmt == null) {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ownStmt = true;
			}
			// 通过正则表达式，将参数sql语句中的包含大写字母的FROM替换为小写的from，
			// 以便下面的：sql.indexOf(" from ")能得到正确结果
			sql = sql.replaceAll("\\s+([f|F][R|r][o|O][m|M])\\s+", " from ");
			// 预处理SQL语句，去除order by 部分
			int pos = sql.indexOf(" from ");
			sql = "select count(*) " + sql.substring(pos);
			sql = sql.replaceFirst(" (order by|ORDER BY).*", "");
			long startTime = System.currentTimeMillis();

			jdbcRs = stmt.executeQuery(sql);
			long endTime = System.currentTimeMillis();

			if (jdbcRs.next()) {
				result = jdbcRs.getLong(1);
			}
			// if ((LOG_ROW_TIME > 0) && (endTime - startTime > LOG_ROW_TIME)) {
			// logger.debug("超时:" + "数量【" + result + "】 时间【" + (endTime -
			// startTime) + "】 " + sql);
			// }
			// if (logger.isDebugEnabled()) { //增加判断，提高效率
			// logger.debug(sql + " >>" + result + "Time Spend:" + (endTime -
			// startTime));
			// }
		} catch (SQLException eq) {
			throw new SQLException(eq.getMessage() + " SQL: " + sql, sql);
		} finally {
			if (jdbcRs != null) {
				jdbcRs.close();
			}

			if (ownStmt) {
				stmt.close();
			}
		}
		return result;
	}

	/**
	 * 直接从数据表中返回第一条记录所选一个字段的值
	 * 
	 * @param conn
	 *            数据连接
	 * @param selFieldName
	 *            选择的字段名
	 * @param tableName
	 *            数据表（或视图）名称
	 * @param sqlWhere
	 *            查询条件
	 * @return String 返回值
	 * @throws SQLException
	 *             SQL错误信息
	 */
	public static String sqlSelect1(Connection conn, String selFieldName, String tableName, String sqlWhere)
			throws SQLException {
		StringBuffer sbSql = new StringBuffer("select ").append(selFieldName).append(" from ").append(tableName)
				.append(" ").append(sqlWhere);
		return sqlSelect1(conn, sbSql.toString(), null);
	}

	/**
	 * 直接从数据表中返回第一条记录所选一个字段的值
	 * 
	 * @param conn
	 *            数据连接
	 * @param sqlStr
	 *            sql语句
	 * @param stmt
	 *            Statement
	 * @return String 返回值
	 * @throws SQLException
	 *             SQL错误信息
	 */
	public static String sqlSelect1(Connection conn, String sqlStr, Statement stmt) throws SQLException {
		String result = "";
		boolean ownStmt = false;
		boolean ownConn = false;
		ResultSet jdbcRs = null;
		try {

			if (stmt == null) {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ownStmt = true;
			}
			jdbcRs = stmt.executeQuery(sqlStr);
			if (jdbcRs.next()) {
				result = jdbcRs.getString(1);
			}
		} catch (SQLException se) {
			//
			throw new SQLException("SQL语句查询错误：\r\n" + sqlStr + "错误原因：\r\n" + se.getMessage());
		} finally {

			// 关闭资源
			if (jdbcRs != null) {
				jdbcRs.close();
			}

			if (ownStmt) {
				stmt.close();
			}
		}
		return result;
	}

	/**
	 * 通过游标滚动定位到最后的方式方式执行数据库查询语句，快速得到结果集的数量，适用于Ms SQL Server。
	 * 
	 * @param conn
	 *            数据库连接(必须有可用的链接）
	 * @param sql
	 *            查询语句
	 * @return 符合条件的数据数
	 *
	 * @throws SQLException
	 *             当数据库错误或SQL语句执行错误时
	 */
	private static long queryScrollCount(Connection conn, String sql) throws SQLException {
		long count = 0;
		Statement stmt = null;
		ResultSet jdbcRs = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// 执行sql语句，通过快速定位法确定最后一个记录所占的行数，比直接使用select count(*)有效率高
			jdbcRs = stmt.executeQuery(sql);

			if (jdbcRs.next()) {
				jdbcRs.last();
				count = jdbcRs.getRow();
			}
			if (jdbcRs != null) {
				jdbcRs.close();
			}

			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException eq) {
			throw new SQLException(eq.getMessage() + " SQL: " + sql, sql);
		}
		return count;
	}

	/**
	 * 执行数据库查询语句，快速得到结果集的数量。
	 *
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            查询语句，可以使包含@@符号的被替换SQL
	 *
	 * @return 符合条件的数据数
	 *
	 * @throws SQLException
	 *             当数据库错误或SQL语句执行错误时
	 */
	public static long queryCount(Connection conn, String sql) throws SQLException {
		long count;
		long startTime = System.currentTimeMillis();
		boolean ownConn;
		// 如果connection缺省为空，则直接从链接池中获取一个新链接

		if (getDefaultServerType(conn).equals(SERVER_TYPE_ORACLE)) { // 只优化缺省数据库，不支持附加数据库
			count = querySelectCount(conn, sql, null);
		} else {
			count = queryScrollCount(conn, sql);
		}
		long endTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) { // 增加判断，提高效率
			logger.debug(sql + " >>" + count + "Time Spend:" + (endTime - startTime));
		}

		return count;
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
	 * 执行数据库修改或删除语句，得到修改或删除的记录数。
	 * 
	 * @param conn
	 *            数据库连接
	 * @param psql
	 *            prepare SQL语句
	 * @param dataList
	 *            实际需要执行的数量列表，最外层为ArrayList，内部为HashMap对象，对象内按照1,2,3,4,5,6方式设置值
	 * @exception SQLException
	 *                当数据库错误或SQL语句执行错误时
	 */
	public static void batchSql(Connection conn, String psql, ArrayList dataList) throws SQLException {
		boolean ownConn;
		// 如果connection缺省为空，则直接从链接池中获取一个新链接

		PreparedStatement stmt = null;
		HashMap dataMap;
		int count = 0;
		try {
			stmt = conn.prepareStatement(psql);
			for (int i = 0; i < dataList.size(); i++) {
				dataMap = (HashMap) dataList.get(i);
				if (count == 0) {
					count = dataMap.size();
				}
				for (int j = 1; j <= count; j++) {
					logger.debug(j + "=" + dataMap.get(String.valueOf(j)));
					stmt.setString(j, (String) dataMap.get(String.valueOf(j)));
				}
				stmt.addBatch();
			}
			stmt.executeBatch();
		} catch (SQLException eq) {
			throw new SQLException(eq.getMessage() + " SQL: " + psql, psql);
		} finally {

		}
	}

	/**
	 * 执行数据库修改或删除语句，得到修改或删除的记录数。
	 * 
	 * @param conn
	 *            数据库连接
	 * @param psql
	 *            prepare SQL语句
	 * @param dataList
	 *            实际需要执行的数量列表，最外层为ArrayList
	 * @exception SQLException
	 *                当数据库错误或SQL语句执行错误时
	 */
	public static int batchStringSql(Connection conn, String psql, ArrayList<String> dataList) throws SQLException {
		PreparedStatement stmt = null;
		String sql;
		int count = 0;
		try {
			stmt = conn.prepareStatement(psql);
			for (int i = 1; i <= dataList.size(); i++) {
				sql = (String) dataList.get(i - 1);
				count += dataList.size();
				stmt.setString(i, sql);
				stmt.addBatch();
			}
			stmt.executeBatch();
		} catch (SQLException eq) {
			throw new SQLException(eq.getMessage() + " SQL: " + psql, psql);
		} finally {

		}
		return count;
	}
}
