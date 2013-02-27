package com.ruyicai.charge.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * 本地交易ID与外部订单号映射操作
 * @author ryc
 *
 */
public class TransactionMapUtil {
	
	public static void setTransactionMap(String transactionid,String outorderid,String desc){
		Connection conn = null;
		PreparedStatement stmt = null;
		String url = ConfigUtil.getConfig("charge.properties", "mysql_url");
		String user = ConfigUtil.getConfig("charge.properties", "mysql_username");
		String password = ConfigUtil.getConfig("charge.properties", "mysql_password");
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			String sql = "insert into msbankorderid values(?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, outorderid);
			stmt.setString(2, transactionid);
			stmt.setString(3, desc);
			stmt.setString(4,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			stmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(stmt != null){
					stmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getTransactionid(String outorderid){
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String url = ConfigUtil.getConfig("charge.properties", "mysql_url");
		String user = ConfigUtil.getConfig("charge.properties", "mysql_username");
		String password = ConfigUtil.getConfig("charge.properties", "mysql_password");
		String transactionid = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			String sql = "select * from msbankorderid where outId=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, outorderid);
			rs = stmt.executeQuery();
			while(rs.next()){
				transactionid = rs.getString("transactionId");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(rs != null){
					rs.close();
				}
				if(stmt != null){
					stmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return transactionid;
	}
	
	public static void main(String[] args) {
		System.out.println(TransactionMapUtil.getTransactionid("987654321"));
//		TransactionMapUtil.setTransactionMap("123456789", "987654321", "msBankTest");
	}
}
