package com.ruyicai.charge;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.charge.consts.TransactionState;
import com.ruyicai.charge.domain.Nineteenpay;
import com.ruyicai.charge.domain.Tcard;
import com.ruyicai.charge.domain.Tchannelcard;
import com.ruyicai.charge.util.ConfigUtil;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/applicationContext.xml" })
public class MailTest {


	@Test
	public void test() throws Exception {
		//Tcard.findAllTcards();
	}
	
	@Test
	public void testCreate() throws Exception {
		Nineteenpay nineteenpay = new Nineteenpay();
		nineteenpay.setTransactionid("123");
		nineteenpay.setUserno("123");
		nineteenpay.setCardno("123");
		nineteenpay.setCardpwd("123");
		nineteenpay.setAmt("100");
		nineteenpay.setTotalamt("100");
		nineteenpay.setBalance("100");
		nineteenpay.setCardtype("123");
		nineteenpay.setChargetime(new Date());
		nineteenpay.setState(TransactionState.processing.value().toString());
		nineteenpay.persist();
		System.out.println(nineteenpay.toString());
	}
	
	@Test
	public void testCreate2() throws Exception {
		Tchannelcard tchannelcard = new Tchannelcard();
		tchannelcard.setId("123456");
		tchannelcard.setAgencyno("111");// ? agencyno?
		tchannelcard.setCardamt(BigDecimal.ONE);
		tchannelcard.setCardcode("9999999");
		tchannelcard.setGettime(new Date());
		tchannelcard.setCardcount(BigDecimal.ONE);// ? 设置购买数量
		tchannelcard.setDecrptkey("111111");
		tchannelcard.persist();
		System.out.println(tchannelcard.toString());
	}
	
	
	@Test
	public void testCreate4() throws Exception {
		Nineteenpay nineteenpay = Nineteenpay.findNineteenpay("1111");		
		System.out.println(nineteenpay.toString());
	}

	@Test
	public void testCreate5() throws Exception {
		Nineteenpay nineteenpay = new Nineteenpay();
		nineteenpay.setTransactionid("1234");
		nineteenpay.setUserno("123");
		nineteenpay.setCardno("123");
		nineteenpay.setCardpwd("123");
		nineteenpay.setAmt("100");
		nineteenpay.setTotalamt("100");
		nineteenpay.setBalance("100");
		nineteenpay.setCardtype("123");
		nineteenpay.setChargetime(new Date());
		nineteenpay.setState(TransactionState.processing.value().toString());
		
		Connection conn = null;
		PreparedStatement stmt = null;
		String url = "jdbc:mysql://219.148.162.70:3306/chargecenter?characterEncoding=utf-8";
		String user = "jrtConsole";
		String password = "jrtConsole321";
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			String sql = "INSERT INTO nineteenpay values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";		        
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, nineteenpay.getTransactionid());
			stmt.setString(2, nineteenpay.getUserno());
			stmt.setString(3, nineteenpay.getAmt());
			stmt.setString(4, null);
			stmt.setString(5, null);
			stmt.setString(6, null);
			stmt.setString(7, null);
			stmt.setString(8, null);
			stmt.setString(9, null);
			stmt.setString(10, null);
			stmt.setString(11, null);
			stmt.setString(12, null);
			stmt.setString(13, null);
			stmt.setString(14, null);
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
}
