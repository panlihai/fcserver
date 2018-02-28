package service.report;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import oracle.sql.BLOB;

import org.json.JSONObject;
import org.json.XML;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.pcorp.controllor.RequestModel;
import cn.pcorp.controllor.ResponseModel;
import cn.pcorp.dao.BaseDao;
import cn.pcorp.model.DynaBean;
import cn.pcorp.service.BaseService;
import cn.pcorp.service.ParentService;
import cn.pcorp.util.BeanUtils;

/**
 * 报表查询
 * @author zhao 2017/12/26
 *
 */
public class QueryReportService extends ParentService  {
	
	@Resource(name = "baseService")
	private BaseService baseService;
	
	

	public ResponseModel queryReport(RequestModel rm ) throws SQLException, IOException { 
		//rm.getResponse().setHeader("Content-type", "text/html;charset=UTF-8");
		JSONArray paramJson = rm.getParamJson();
		com.alibaba.fastjson.JSONObject jsonObj = paramJson.getJSONObject(0);
		/*DynaBean paramBean = rm.getParamBean();
		paramBean.set("STASKCODE", "2018JYYS");
		paramBean.set("SCORPCODE", "1000000000");
		paramBean.set("SREPORTCODE", "财预02表(国铁)");
		paramBean.set("SPERIODVALUE", "2017");
		paramBean.set("SCOMPANYCODE", "GT01");*/
		Map<String,Object> result = new HashMap<String,Object>();
		//报表数据
		String sql = "SELECT SREPORTCODE,SREPORTINSID,NSYSROW,SROWID,SROWTYPE,SROWVALUE,SUSERROWCODE,NWORKNUM,CTS,MTS "
				+ "FROM RP_INS_DATA A "
				+ "WHERE A.SREPORTINSID = "
				+ "(SELECT A.SREPORTINSID FROM RP_INS A "
				+ "WHERE A.STASKCODE='"+(String) jsonObj.get("STASKCODE")+"' "
				+ "AND A.SCORPCODE='"+(String) jsonObj.get("SCORPCODE")+"' "
				+ "AND A.SREPORTCODE='"+(String) jsonObj.get("SREPORTCODE")+"' "
				+ "AND A.SPERIODVALUE='"+(String) jsonObj.get("SPERIODVALUE")+"' "
				+ "AND A.SCOMPANYCODE='"+(String) jsonObj.get("SCOMPANYCODE")+"')";
		// 访问库
		BaseDao dao = baseService.getBaseDao();
		DynaBean sqlBean = new DynaBean("RP_MAIN");
		sqlBean.setStr(BeanUtils.KEY_SQL,sql);
		List<DynaBean> insDataList = dao.findWithQueryNoCache(sqlBean);
		//ins_data
		List<Object> rp_ins_data = new ArrayList<Object>();
		for (DynaBean dynaBean : insDataList) {
			JSONObject xmlJSONObj = XML.toJSONObject(dynaBean.get("SROWVALUE").toString()); 
			dynaBean.set("SROWVALUE", xmlJSONObj.toString());
			rp_ins_data.add(dynaBean);
		}
		result.put("RP_INS_DATA",rp_ins_data);
		//报表列定义
		sql = "SELECT ID,STASKCODE,SCORPCODE, SREPORTCODE,SPERIODVALUE,NSYSCOL,SCOLCODE,SCOLTYPE,SCOLVALUE "
				+ "FROM RP_COL B "
				+ "WHERE B.SREPORTCODE = '"+(String) jsonObj.get("SREPORTCODE")+"' "
				+ "AND B.SPERIODVALUE='"+(String) jsonObj.get("SPERIODVALUE")+"' "
				+ "AND STASKCODE ='"+(String) jsonObj.get("STASKCODE")+"'";
		sqlBean.setStr(BeanUtils.KEY_SQL,sql);
		List<DynaBean> colList = dao.findWithQueryNoCache(sqlBean);
		List<Object> rp_col = new ArrayList<Object>();
		for (DynaBean dynaBean : colList) {
			rp_col.add(dynaBean);
		}
		result.put("RP_COL",rp_col);
		//表样入库:
		//this.updateBlob(baseService,"",paramBean);
		
		//报表表样
		sql = "SELECT  A.BLFORMATDATA "
				+ "FROM RP_DEFINE_FILE A "
				+ "WHERE A.STASKCODE='"+(String) jsonObj.get("STASKCODE")+"' "
				+ "AND A.SCORPCODE='"+(String) jsonObj.get("SCORPCODE")+"' "
				+ "AND A.SREPORTCODE='"+(String) jsonObj.get("SREPORTCODE")+"' "
				+ "AND A.SPERIODVALUE='"+(String) jsonObj.get("SPERIODVALUE")+"' ";
		String BLFORMATDATA = this.readBlob(baseService,sql);
		
		result.put("RP_DEFINE_FILE",JSON.parse(BLFORMATDATA));
		
		ResponseModel response = ResponseModel.getInstance();
		
		response.setData( result );
		return response;
	}
	
	 /** 
     * 从数据库中读大对象出来 
     */  
    public String readBlob(BaseService baseService,String sql) {  
        try {  
        	Connection conn = baseService.getBaseDao().getDataSource().getConnection();
            Statement st = conn.createStatement();  
            ResultSet rs = st.executeQuery(sql);  
            BLOB blob = null;
            while (rs.next()) {  
            	blob = (BLOB) rs.getBlob("BLFORMATDATA");  
            }
            rs.close();
            st.close();
            conn.close();
            InputStream is = blob.getBinaryStream();
            int length = (int) blob.length();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();
            return new String(buffer,"utf-8"); 
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return null;  
    }  
    /** 
     * 更新blob数据 
     */  
    public String updateBlob(BaseService baseService, String sql, DynaBean paramBean) {  
    	try {  
    		Connection conn = baseService.getBaseDao().getDataSource().getConnection();
    		conn.setAutoCommit(false); //切记要把AutoCommit设置为FALSE
    		String param = "WHERE A.STASKCODE='"+paramBean.getStr("STASKCODE")+"' "
    				+ "AND A.SCORPCODE='"+paramBean.getStr("SCORPCODE")+"' "
    				+ "AND A.SREPORTCODE='"+paramBean.getStr("SREPORTCODE")+"' "
    				+ "AND A.SPERIODVALUE='"+paramBean.getStr("SPERIODVALUE")+"' ";
    		String updateSql ="UPDATE RP_DEFINE_FILE A SET A.BLFORMATDATA = EMPTY_BLOB() " +param;
    		String selectSql = "SELECT A.BLFORMATDATA FROM RP_DEFINE_FILE A " +param +" FOR UPDATE";
    		Statement st = conn.createStatement();  
    		st.executeUpdate(updateSql);
            ResultSet rs = st.executeQuery(selectSql);
            while (rs.next()) {  
                oracle.sql.BLOB blformatdata = (oracle.sql.BLOB)rs.getBlob("BLFORMATDATA");  
                OutputStream os = blformatdata.setBinaryStream(0);   
                InputStream ist = new FileInputStream("D:/qwe.txt");    
                // 依次读取流字节,并输出到已定义好的数据库字段中.    
                int i = 0;
                while ((i = ist.read()) != -1) {    
                    os.write(i);                                               //Blob的输入流，相当于输入到数据库中  
                }
                os.flush();    
                os.close();    
                conn.commit();    
                conn.setAutoCommit(true);// 恢复现场 
            }  
          //  is.close();  
            rs.close();
            st.close();
            conn.close();
    	} catch (Exception e) {  
    		e.printStackTrace();  
    	}
    	return null;  
    }  
    
}
