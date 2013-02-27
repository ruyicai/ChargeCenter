package com.ruyicai.charge.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.liantonghuajian.LthjUtil;
import com.ruyicai.charge.liantonghuajian.XmlHttpConnection;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.XMLUtil;

public class LiantonghuajianNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired
	ChargeUtil chargeUtil;
	
	private static final String PAY_NOTIFY_CUPSRESPCODE = "00";
	private static final int TIME_OUT = 6000;
	private static final String PAY_TRANSTYPE = "01";
	private static final String TRANSNOTIFYRSPMESSAGE_RESPCODE = "0000";	
	//private static String requrl = ConfigUtil.getConfig("charge.properties", "liantonghuajian.requrl");
	
	private Logger logger = Logger.getLogger(LiantonghuajianNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		// try {
		// arg0.setCharacterEncoding("gb2312");
		// } catch (UnsupportedEncodingException e) {
		// logger.info("request:转换编码出错");
		// e.printStackTrace();
		// }

		this.request = arg0;
	}

	public String bgRet() {
		logger.info("联通华建支付通知处理->开始");		
		String xml  = getXML();		
		logger.info("xml=" + xml);
		Map<String, String> map = XMLUtil.xml2Map(xml);			
		boolean isOK = new LthjUtil().verifyNotifyMessageSign(map);
		if (!isOK) {
			logger.info("联通华建支付通知处理->签名校验失败");
			return null;
		}
		
		String merchantOrderId = map.get("merchantOrderId").toString();		
		String merchantOrderAmt = map.get("merchantOrderAmt").toString();		
		String cupsQid = map.get("cupsQid").toString();
		String cupsTraceNum = map.get("cupsTraceNum").toString();
		String cupsTraceTime = map.get("cupsTraceTime").toString();
		String cupsRespCode = map.get("cupsRespCode").toString();
		String cupsRespDesc = map.get("cupsRespDesc").toString();		
		String respCode = map.get("respCode").toString();
		
		String result = null;			
		String drawamt = ChargeUtil.getDrawamt(merchantOrderAmt);
		String url = null;
		StringBuffer param = new StringBuffer();
		param.append("ttransactionid=").append(merchantOrderId).append("&bankorderid=").append(cupsQid)
		.append("&bankordertime=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
		.append("&banktrace=").append(cupsTraceNum).append("&retcode=").append(cupsRespCode)
		.append("&retmemo=").append(cupsRespDesc).append("&amt=").append(merchantOrderAmt).append("&drawamt=").append(drawamt);
		
		if (PAY_NOTIFY_CUPSRESPCODE.equals(cupsRespCode)) {
			logger.info("联通华建支付通知处理->交易成功处理->开始!");
			try {
				url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
				logger.info("联通华建支付通知处理->交易成功处理->url=" + url + "；param=" + param.toString());
				result = HttpRequest.doPostRequest(url, param.toString());
			} catch (IOException e) {
				logger.error("联通华建支付通知处理->交易成功处理->出现异常!", e);				
				return null;
			}
			
			logger.info("联通华建支付通知处理->交易成功处理->返回result:" + result);
			chargeUtil.afterCharge(result);
			this.submitTransNotifyRspMessage(merchantOrderId);
			logger.info("联通华建支付通知处理->交易成功处理->结束!");		
		} else {
			logger.info("联通华建支付通知处理->交易失败处理->开始!");
			
			try {
				url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
				logger.info("联通华建支付通知处理->交易失败处理->url=" + url + "；param=" + param.toString());
				result = HttpRequest.doPostRequest(url, param.toString());
			} catch (IOException e) {
				logger.error("联通华建支付通知处理->交易失败处理->出现异常!", e);
				e.printStackTrace();
				return null;
			}
			
			logger.info("联通华建支付通知处理->交易失败处理->返回result:" + result);
			logger.info("联通华建支付通知处理->交易失败处理->结束!");			
		}
				
		logger.info("联通华建支付通知处理->结束");
		return null;
	}
	
	private String getXML() {
		String ret = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			//logger.info("sb=" + sb.toString());
			ret = sb.toString();//URLDecoder.decode(sb.toString(), "UTF-8");
			logger.info("ret=" + ret);
		} catch (Exception e) {
			logger.info("getXML error:", e);
		}
		return ret;
	}
	
	private void submitTransNotifyRspMessage(String merchantOrderId) {
		String url = chargeconfigService.getChargeconfig("liantonghuajian.requrl");//requrl;
		String merid = chargeconfigService.getChargeconfig("liantonghuajian.merid");
		String transNotifyRspMessage = new LthjUtil().createTransNotifyRspMessage(PAY_TRANSTYPE, merchantOrderId, TRANSNOTIFYRSPMESSAGE_RESPCODE, merid);
		XmlHttpConnection xmlhttpconnection =  new XmlHttpConnection(url, TIME_OUT);
		xmlhttpconnection.sendMsg(transNotifyRspMessage);
	}
}
