/*
 * Copyright (c) Nap All rights reserved.
 */
package cn.pcorp.util.sql;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.pcorp.model.DynaBean;
import cn.pcorp.util.BeanUtils;
import cn.pcorp.util.DateUtils;
import cn.pcorp.util.SyConstant;
import oracle.sql.BLOB;



/**
 * 动态执行通用SQL语句
 *
 * @author      Lihai Pan
 */
public class DynaSql implements Serializable {
    /** 日志记录 */
    private static Log logger = LogFactory.getLog(DynaSql.class);
    
    public static int insertList(List<DynaBean> list,Connection conn)throws Exception{
    	String strSql = "";
    	int result = 0;
          try {
        	  for(DynaBean dynaBean:list){
        		  DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
	        	  strSql = SqlBuilder.getInsertSql(dynaBean, false,defBean);
	        	  result += SqlExecutor.update(conn, strSql);
	        	  updateContent(dynaBean,conn);
	        	  updateBlobContent(dynaBean,conn);	        	  
        	  }
          }catch(Exception ex){
        	  ex.printStackTrace();
    	}
    	return result;
    }
    
    /**
     * 插入一条带指定数据的数据库记录，如果对应字段没有值，则系统将字符串类型字段置为'', 将数字类型字段置为0
     * 
     * @param dynaBean          存放要插入的数据库记录信息
     * @return 插入是否成功：      1 是， 0 否
     * @throws Exception      当发生数据库错误
     */
    public static int insert(DynaBean dynaBean,Connection conn)
      throws Exception {
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        String strSql = SqlBuilder.getInsertSql(dynaBean, false,defBean);
        try {
            int result = SqlExecutor.update(conn, strSql);
            if (result > 0) { //成功执行了数据操作,则判断是否记录操作历史
                //生成新的KEY_PK_CODE
                genPKCode(dynaBean,conn);
                /** modified by chuaiqing at 2007-11-04 begin */
                //判断long字段的值不为空的时候进行大文本字段处理
                String longFieldName = dynaBean.getStr(BeanUtils.KEY_LONG_FIELD, "");
                updateContent(dynaBean,conn);
                List<String> blobList = (ArrayList)dynaBean.get(BeanUtils.DEF_BLOB_FIELDS);
                if(blobList!=null){
	                for(String blobfield:blobList){
		                updateBlobContent(dynaBean,conn);
	                }
                }
                //if (dynaBean.getStr(BeanUtils.KEY_LONG_FIELD, "").length() > 0) { //进行大文本字段处理
                //    updateContent(dynaBean);
                //}                
                /** modified by chuaiqing at 2007-11-04 end */
                //记录SQL操作
                recordSql(dynaBean, strSql,conn);
            }
            return result;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 基于主键删除一条数据库记录
     * 
     * @param dynaBean      数据信息
     * @return              删除行数，正常为1
     * @throws Exception  当发生数据库错误
     */
    public static int delete(DynaBean dynaBean,Connection conn)
        throws Exception {
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        String strSql = SqlBuilder.getDeleteSql(dynaBean, false, true,defBean);
        try {
            int result = SqlExecutor.update(conn, strSql);
            if (result > 0) { //成功执行了数据操作,则判断是否记录操作历史
                recordSql(dynaBean, strSql,conn);
            }
            return result;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 根据动态类中$WHERE$项设定的条件删除多条数据库记录
     *
     * @param dynaBean          数据信息
     * @return                  删除行数
     * @throws Exception      当发生数据库错误
     */
    public static int deleteByCondition(DynaBean dynaBean,Connection conn)
      throws Exception {
        //基于where条件的删除
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        String strSql = SqlBuilder.getDeleteSql(dynaBean, false, false,defBean);        
        try {
            int result = SqlExecutor.update(conn, strSql);
            if (result > 0) { //成功执行了数据操作,则判断是否记录操作历史
               // recordSql(dynaBean, strSql);
            }
           
            return result;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 根据主键查询一条指定的记录。
     * 
     * @param dynaBean      数据信息
     * @return              数据库记录相信信息，不存在数据时返回空
     * @throws Exception   当发生数据库错误
     */
    public static DynaBean select(DynaBean dynaBean,Connection conn)
      throws Exception {
        return select(dynaBean, true,conn);
    }
    
    /**
     * 根据主键或者过滤条件查询一条指定的记录：
     * 如果是则使用主键字段，则自动根据动态类中主键的信息进行过滤
     * 如果不是使用主键字段，则使用$WHERE$中的信息进行过滤
     * 
     * @param dynaBean      数据信息
     * @param bKeySelect    是否基于主键查找一条指定的记录
     * @return              数据库记录相信信息，不存在数据时返回空
     * @throws Exception  当发生数据库错误
     */
    public static DynaBean select(DynaBean dynaBean, boolean bKeySelect,Connection conn)
      throws Exception {
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        String strSql = SqlBuilder.getSelectSql(dynaBean,defBean);
        try {
            DynaBean result = SqlExecutor.query(conn, strSql, 1, 1);
            
            ArrayList rows = (ArrayList) result.get(BeanUtils.KEY_ROWSET);
            if (rows.size() == 0) {
                return null;
            } else {
                //将获取到的值传递到bean中，并返回即带值又带参数的Bean
                DynaBean recordBean = new DynaBean();
                recordBean.getValues().putAll(((DynaBean) rows.get(0)).getValues());
                recordBean.set(BeanUtils.KEY_TABLE_CODE, dynaBean.getStr(BeanUtils.KEY_TABLE_CODE));
                return recordBean;
            }
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 根据$WEHRE$条件查询符合条件的纪录数量
     *
     * @param dynaBean      数据信息
     * @return              符合条件的纪录数
     * @throws Exception  当发生数据库错误
     */
    public static long selectCount(DynaBean dynaBean,Connection conn)
        throws Exception {
        DynaBean tableDef = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        if (dynaBean.getStr(BeanUtils.KEY_WHERE, "").length() == 0) { //自动拼装Where条件
            dynaBean.set(BeanUtils.KEY_WHERE, SqlBuilder.getWhere(dynaBean, tableDef).toString());
        }
        String strSql = "select count(*) as COUNT from "  
            + tableDef.get("TABLE_CODE") + " where 1=1 " + dynaBean.getStr(BeanUtils.KEY_WHERE, "");
        try {
            DynaBean result = SqlExecutor.query(conn, strSql);
            logger.debug(strSql);
            dynaBean = (DynaBean) ((ArrayList) result.get(BeanUtils.KEY_ROWSET)).get(0);
            String count = dynaBean.getStr("COUNT", "0");
            if (logger.isDebugEnabled()) {
                logger.debug("COUNT:" + count);
            }
            return Long.parseLong(count);
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }
    /**
     * 根据条件查询数据库中的一条记录，如果查询出多条记录，那么只取第一条记录
     * @param dynaBean  查询条件bean
     * @return  结果bean
     * @throws Exception  异常
     */
    public static DynaBean selectOne(DynaBean dynaBean,Connection conn) throws Exception {
        return select(dynaBean, false,conn);
    }

    /**
     * 根据条件查询数据库记录列表，不分页，取全部信息
     *
     * @param dynaBean          数据信息
     * @return                  数据库记录列表信息
     * @throws Exception      当发生数据库错误
     */
    public static ArrayList selectList(DynaBean dynaBean,Connection conn)
        throws Exception {
        DynaBean result = selectList(dynaBean , 0 , 0,conn);
        return (ArrayList) result.get(BeanUtils.KEY_ROWSET);
    }
    
    /**
     * 根据条件查询数据库记录列表
     *
     * @param dynaBean      查询信息
     * @param pageNum       当前所在页数，第一页应该为1
     * @param count         返回纪录数
     * @return              数据库记录以及结果信息
     * @throws Exception  当发生数据库错误
     */
    public static DynaBean selectList(DynaBean dynaBean,  
        int pageNum , int count,Connection conn) throws Exception {
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        //基于where条件的查询
        String strSql = SqlBuilder.getSelectSql(dynaBean,defBean);
        try {
            return SqlExecutor.queryPage(conn, strSql, pageNum, count);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }
    
    /**
     * 基于主键修改数据库记录信息
     * 
     * @param dynaBean          要更新的数据库记录信息
     * @return                  更新行数，正常为1
     * @throws Exception      当发生数据库错误
     */
    public static int update(DynaBean dynaBean,Connection conn)
        throws Exception {
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        String strSql = SqlBuilder.getUpdateSql(dynaBean, false, true,defBean);
        try {
            int result =  SqlExecutor.update(conn, strSql);
            if (result > 0) { //成功执行了数据操作
                //重新生成KEY_PK_CODE
                genPKCode(dynaBean,conn);
                /** modified by chuaiqing at 2007-11-04 begin */
                //判断long字段的值不为空的时候进行大文本字段处理
                String longFieldName = dynaBean.getStr(BeanUtils.KEY_LONG_FIELD, "");
                updateContent(dynaBean,conn);
                List<String> blobList = (ArrayList)dynaBean.get(BeanUtils.DEF_BLOB_FIELDS);
                for(String blobfield:blobList){
	                updateBlobContent(dynaBean,conn);
                }
                //if (dynaBean.getStr(BeanUtils.KEY_LONG_FIELD, "").length() > 0) { //进行大文本字段处理
                //    updateContent(dynaBean);
                //}
                /** modified by chuaiqing at 2007-11-04 end */
                //记录数据操作历史
                recordSql(dynaBean, strSql,conn);
            }
            return result;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 批量修改多条数据库记录
     *
     * @param dynaBean      数据信息，如果需要过滤条件，需要在$WHERE$中设定
     * @param setSql        set语句，例如： ABC_ID=1005,ABC_NAME='test'
     * @return              更新行数
     * @throws Exception  当发生数据库错误
     */
    public static int updateList(DynaBean dynaBean, String setSql,Connection conn)
      throws Exception {
        if ((setSql == null) || (setSql.length() <= 0)) {
            return 0;
        }
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        if (dynaBean.getStr(BeanUtils.KEY_WHERE, "").length() == 0) {         
        	//自动拼装Where条件            
            dynaBean.set(BeanUtils.KEY_WHERE, SqlBuilder.getWhere(dynaBean,defBean).toString());
        }
        setSql = "update " + dynaBean.get("TABLE_CODE") + " set " + setSql + " where 1=1 "
            + dynaBean.getStr(BeanUtils.KEY_WHERE, "");
        try {
            int result = SqlExecutor.update(conn , setSql);
            if (result > 0) { //成功执行了数据操作,则判断是否记录操作历史
                recordSql(dynaBean, setSql,conn);
            }
            return result;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }
   
    /**
     * 批量修改多条数据库记录
     *
     * @param dataBean     需要设置的数据放置到dataBean中，另外要求必须有$WHERE$设定，否则会抛出错误,
     *                     如果需要更新全部数据则要设为：and 1=1
     * @return             更新行数
     * @throws Exception 当发生数据库错误
     */
    public static int updateList(DynaBean dataBean,Connection conn) throws Exception {
        if (dataBean.getStr(BeanUtils.KEY_WHERE, "").length() == 0) { //自动拼装Where条件
            throw new Exception("不存在条件");
        }
    	DynaBean defBean = SqlBuilder.getTableDef(dataBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        String setSql = SqlBuilder.getUpdateSql(dataBean, false, false,defBean);
        try {
            int result = SqlExecutor.update(conn , setSql);
            if (result > 0) { //成功执行了数据操作,则判断是否记录操作历史
                recordSql(dataBean, setSql,conn);
            }
            return result;
        } catch (SQLException e) {
            throw new Exception(e.getMessage(), e);
        }
    }
    
    /**
     * 执行对表操作的sql历史记录
     * @param dynaBean      数据信息
     * @param sql           执行的SQL
     * @throws Exception  当获取表定义信息错误时
     */
    public static void recordSql(DynaBean dynaBean, String sql,Connection conn) throws Exception {
    	DynaBean defBean = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
        //当前数据表设定了需要记录SQL历史
        if (!defBean.getStr("TABLE_SQL_FLAG","N").equals("Y")) {
            return;
        }        
        //为提高效率自己拼写SQL进行执行
        sql = sql.replaceAll("'", "''");
        StringBuffer sb = 
            new StringBuffer("insert into SY_TABLE_SQL( TABLE_CODE, TS_SQL, TS_DATETIME, ");
        sb.append("USER_CODE, TABLE_PK, TS_SQL_TYPE) values ('");
        sb.append(defBean.getStr(BeanUtils.KEY_TABLE_CODE)).append("', '").append(sql).append("', '");
        sb.append(DateUtils.getDatetime()).append("', '").append(dynaBean.getStr(BeanUtils.KEY_USER, ""));
        sb.append("', '").append(dynaBean.getStr(BeanUtils.KEY_PK_CODE, ""));
        sb.append("',").append(SyConstant.SQL_TYPE_DML).append(")");
        try {
            SqlExecutor.update(conn , sb.toString());
        } catch (SQLException e) {
            logger.error("#ACTION# record SQL ERROR: " + sql, e);
        }
    }
    
    /**
     * 跟据动态Bean的属性信息动态的生成KEY_PK_OCDE，用于供其他应用调用
     * @param dynaBean      数据信息
     * @throws Exception  但生成KEY_PK_CODE信息错误时
     */
    public static void genPKCode(DynaBean dynaBean,Connection conn) throws Exception {
        String tableCode = dynaBean.getStr(BeanUtils.KEY_TABLE_CODE);
        DynaBean tableDef = SqlBuilder.getTableDef(tableCode,conn);
        DynaBean tableField;
        ArrayList pkList = (ArrayList) tableDef.get(BeanUtils.DEF_PK_FIELDS);
        StringBuffer keyCode = new StringBuffer("");
        for (int i = 0; i < pkList.size(); i++) { //根据主键循环得到KEY_PK_CODE字符串
            tableField = (DynaBean) pkList.get(i);
            keyCode.append(dynaBean.getStr(tableField.getStr("FIELD_CODE", ""))).append("`");
        }
        if (keyCode.length() > 0) { //去除最后一个分隔符
            dynaBean.set(BeanUtils.KEY_PK_CODE, 
                    keyCode.toString().substring(0, keyCode.length() - 1));                        
        } 
    }
    
    /**
     * 更新信息内容，可以突破4k字节的限制，前提需要判断dynaBean中的KEY_LONG_FIELD属性，如果有值说明有大文本字段
     * @param dynaBean      需要更新的大文本动态bean信息
     * @throws SQLException  当数据库错误时
     * @throws Exception  当获取表编号错误时
     */
    public static void updateContent(DynaBean dynaBean,Connection conn) throws SQLException, Exception {
        // 得到数据库连接
        
        StringBuffer sqlStmt = new StringBuffer("update ");
        boolean ownConn = false;
        if (conn == null) {           
            ownConn = true;
        }
        try {
            String fieldName = dynaBean.getStr(BeanUtils.KEY_LONG_FIELD,"");            
            if(fieldName.length()==0){
            	return;
            }
            sqlStmt.append(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE)); //表名
            sqlStmt.append(" set ").append(fieldName).append("=? where 1=1 ");
            DynaBean tableDef = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
            sqlStmt.append(SqlBuilder.getWhere(dynaBean, tableDef));
            //CLOB处理
            //恢复数据bean中的实际位置
            
            PreparedStatement stmt = conn.prepareStatement(sqlStmt.toString());
            stmt.setCharacterStream(1, new StringReader(dynaBean.getStr(fieldName)),dynaBean.getStr(fieldName).length());                
            dynaBean.set(BeanUtils.KEY_LONG_FIELD, ""); //清除大文本设定信息
                       
            //CLOB处理结束 
            stmt.executeUpdate();
            stmt.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        }finally {        
            if (ownConn) { //单独打开的数据库连接，则手工关闭，放会连接池。
                conn.close();
            }
        }
    }
    
    /**
     * 更新信息内容，可以突破4k字节的限制，前提需要判断dynaBean中的KEY_LONG_FIELD属性，如果有值说明有大文本字段
     * @param dynaBean      需要更新的大文本动态bean信息
     * @throws SQLException  当数据库错误时
     * @throws Exception  当获取表编号错误时
     */
    public static void updateBlobContent(DynaBean dynaBean,Connection conn) throws SQLException, Exception {
       StringBuffer sqlStmt = new StringBuffer("select ");
        StringBuffer sqlupdateStmt = new StringBuffer("update ");
        boolean ownConn = false;
        if (conn == null) {
            ownConn = true;
        }
        conn.setAutoCommit(false);
        try {
            String[] fieldName1 = dynaBean.getStr(BeanUtils.DEF_BLOB_FIELDS,"").split("~");
            if(fieldName1.length==0){
            	return;
            }
            for(String fieldName:fieldName1){
            	if(fieldName.length()==0){
            		continue;
            	}
	            sqlStmt.append(fieldName).append(" from ");
	            sqlStmt.append(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE)); 
	            sqlStmt.append(" where 1=1 ");
	            DynaBean tableDef = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
	            sqlStmt.append(SqlBuilder.getWhere(dynaBean, tableDef));
	            sqlStmt.append(" for update");
	            //Statement stmt = conn.createStatement();
	            PreparedStatement pstm =conn.prepareStatement(sqlStmt.toString()); 
	            ResultSet rs = pstm.executeQuery(); 
	            //ResultSet rs = stmt.executeQuery(sqlStmt.toString());
	            OutputStream os = null;
	            if(rs.next()){
	            	oracle.sql.BLOB b = (oracle.sql.BLOB)rs.getBlob(fieldName);
	            	b.putBytes(1, getFromBase64(dynaBean.getStr(fieldName)));
	            	sqlupdateStmt.append(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE)).append(" set ").append(fieldName).append(" =? ");
	            	sqlupdateStmt.append(" where 1=1 ");
	                DynaBean tableDefupdate = SqlBuilder.getTableDef(dynaBean.getStr(BeanUtils.KEY_TABLE_CODE),conn);
	                sqlupdateStmt.append(SqlBuilder.getWhere(dynaBean, tableDefupdate));
	               // pstm = conn.prepareStatement(sqlupdateStmt.toString()); 
	            	//pstm.setBlob(1, b); 
	            	pstm.executeUpdate(); 
	            	pstm.close(); 
	            	rs.close();
	            }
	            sqlStmt.setLength(0);
            }
           
            //dynaBean.set(BeanUtils.KEY_BLOB_FIELD, ""); 
                 
        }catch(Exception ex){
        	ex.printStackTrace();
        }finally {        
        	
            if (ownConn) { 
                conn.close();
            }
        }
    }
    public static byte[] getFromBase64(String s) {  
        byte[] b = null;  
        //String result = null;  
        if (s != null) {  
            try {  
                b = Base64.getDecoder().decode(s);  
                //result = new String(b, "utf-8");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return b;  
    }  
    /***
     * 读取oracleCLOB字段内容
     * @param conn
     * @return
     */
	public static String readCLOB(Connection conn) {		
		String sql = "select 大字段1,大字段2 from 印章基本信息_char_ccbb where yzbm='2'";
		String content = "";
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps1 = conn.prepareStatement(sql);
			ResultSet rs1 = ps1.executeQuery();
			while (rs1.next()) {
				oracle.sql.CLOB clob = (oracle.sql.CLOB) rs1.getClob("大字段1");
				BufferedReader in = new BufferedReader(
						clob.getCharacterStream());
				StringWriter out = new StringWriter();
				int c;
				while ((c = in.read()) != -1) {
					out.write(c);
				}
				content = out.toString();
				System.out.println(content);// 输出CLOB内容
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

    

   /***
     * 读取oracle的blob转换为字符串
     * @param conn
     * @return
 * @throws  
     */

	public static String ConvertBLOBtoString(Connection conn) {		
		StringBuffer newStr = new StringBuffer(); // 返回字符串
		long BlobLength; // BLOB字段长度
		byte[] bytes; // BLOB临时存储字节数组
		int i = 1; // 循环变量
		Statement st = null;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * from rp_define_file");
			while (rs.next()) {
				BLOB blob = (BLOB) rs.getBlob("BLFORMATDATA");
				byte[] msgContent = blob.getBytes(); // BLOB转换为字节数组
				BlobLength = blob.length(); // 获取BLOB长度
				if (msgContent == null || BlobLength == 0) // 如果为空，返回空值
				{
					return "";
				} else {
					while (i < BlobLength) // 循环处理字符串转换，每次1024；Oracle字符串限制最大4k
					{
						bytes = blob.getBytes(i, 1024);
						i = i + 1024;
						//newStr.append(new String(bytes, "gb2312"));						
						newStr.append(bytes);
					}
				}
				System.out.println(newStr.toString());				
				System.out.println(newStr.length());
				newStr.setLength(0);
			}
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return newStr.toString();
	}   
}
