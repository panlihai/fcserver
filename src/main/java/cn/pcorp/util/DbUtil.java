package cn.pcorp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {
	public static void main(String args[]) {
		String urlstr = "jdbc:oracle:thin:@localhost:1521:NAP";
		Connection con = null;
		Statement stmt = null;
		String name;
		int no;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("classnotfoundexception :");
			System.err.print(e.getMessage());
		}
		String sql = "insert into c_region(ID,RGCODE,RGNAME,PARENT,PARENTNAME,SORT,RGLEVEL) values(";
		try {
			con = DriverManager.getConnection(urlstr, "TYSTORE", "TYSTORE");
			stmt = con.createStatement();
			Statement stmt1 = con.createStatement();
			// 向test表中插入一条数据
			ResultSet set = stmt.executeQuery("select * from c_region where rgname like '%（%'");
			while (set.next()) {
				String name1 = set.getString("RGNAME");
				name1 = name1.substring(name1.indexOf("（")+1, name1.length()-1);
				String sql1 = sql+"'"+SyConstant.getUUID()+"',(select max(to_number(rgcode))+1 from c_region where parent='"+set.getString("RGCODE")+"'),"
						+ "'"+name1+"','"+set.getString("RGCODE")+"','"+set.getString("RGNAME").substring(0, set.getString("RGNAME").indexOf("（"))+"',"
						+ "0,'4')";
				System.out.println(sql1);
				stmt1.executeUpdate(sql1);				
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			System.err.println("sqlexception :" + ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main0(String args[]) {
		String urlstr = "jdbc:mysql://182.92.180.112:4405/cloudfarm?useUnicode=true&characterEncoding=utf8";
		Connection con = null;
		Statement stmt = null;
		String name;
		int no;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("classnotfoundexception :");
			System.err.print(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(urlstr, "root", "QsNyt@2007");
			stmt = con.createStatement();
			// 向test表中插入一条数据
			File file = new File("C:/Users/panlihai/Documents/1.sql");
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt += bufferedReader.readLine()) != null) {
				// 写入文件(找了一个换行的不能用 }
				if (lineTxt.indexOf(";") != -1) {
					System.out.println(lineTxt);
					String sql = lineTxt.substring(lineTxt.indexOf("insert"), lineTxt.length() - 1);
					stmt.executeUpdate(sql);
					lineTxt = "";
				}
			}
			read.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			System.err.println("sqlexception :" + ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
