package com.ruyicai.charge.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.alipay.batchpay.BatchPayCode;
import com.ruyicai.charge.alipay.batchpay.CheckURL;
import com.ruyicai.charge.alipay.batchpay.SignatureHelper;
import com.ruyicai.charge.alipay.tradequery.AlipayConfig;
import com.ruyicai.charge.consts.CashDetailState;
import com.ruyicai.charge.consts.CashDetailType;
import com.ruyicai.charge.domain.Talibatchpay;
import com.ruyicai.charge.domain.Tcashdetail;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.service.FundService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.StringUtil;

public class BatchpayNotifyAction implements ServletRequestAware, ServletResponseAware {		
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(BatchpayNotifyAction.class);
	
	@Autowired
	private FundService fundService;
	@Autowired
	ChargeconfigService chargeconfigService;
	
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	
	public String bgRet() {
		logger.info("支付宝批量付款通知处理->开始");
		
		try {
			String partner = chargeconfigService.getChargeconfig("batchpay.partner.id");//ConfigUtil.getConfig("charge.properties", "batchpay.partner.id"); 
			String key = chargeconfigService.getChargeconfig("batchpay.key");//ConfigUtil.getConfig("charge.properties", "batchpay.key"); 			
			String payGateway = chargeconfigService.getChargeconfig("batchpay.pay.gatewaynotify.https");//ConfigUtil.getConfig("charge.properties", "batchpay.pay.gatewaynotify.https");			
			String notifyId = request.getParameter("notify_id");
			String alipayNotifyURL = payGateway + "&partner=" + partner + "&notify_id=" + notifyId;
			
			logger.info("支付宝批量付款通知处理->alipayNotifyURL=" + alipayNotifyURL);
			String responseTxt = CheckURL.check(alipayNotifyURL);
			logger.info("支付宝批量付款通知处理->responseTxt=" + responseTxt);
			
			PrintWriter out = response.getWriter();
			if (!responseTxt.equals("true")) {
				//true是正确的订单信息，false 是无效的
				logger.info("支付宝批量付款通知处理->验证是否支付宝请求，验证失败");
				out.print("fail");
				return null;
			}
			
			Map params = new HashMap();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ","; 
				}
				params.put(name, valueStr);
			}
			
			String mySign = SignatureHelper.sign(params, key);	
			String sign = request.getParameter("sign");
			logger.info("支付宝批量付款通知处理->mySign=" + mySign);
			logger.info("支付宝批量付款通知处理->sign=" + sign);
			
			if (!mySign.equals(sign)) {
				logger.info("支付宝批量付款通知处理->验证签名，验证失败");
				out.print("fail");
				return null;
			}
			
			String notifyTime = request.getParameter("notify_time");
			String notifyType = request.getParameter("notify_type");
			String signType = request.getParameter("sign_type");
			String batchNo = request.getParameter("batch_no");
			String payUserId = request.getParameter("pay_user_id");
			String payUserName = request.getParameter("pay_user_name");
			String payAccountNo = request.getParameter("pay_account_no");
			String successDetails = request.getParameter("success_details");
			String failDetails = request.getParameter("fail_details");
			logger.info("支付宝批量付款通知处理->notifyTime=" + notifyTime + ";notifyType=" + notifyType + ";signType=" + signType 
					 + ";batchNo=" + batchNo  + ";payUserId=" + payUserId  + ";payUserName=" + payUserName
					 + ";payAccountNo=" + payAccountNo  + ";successDetails=" + successDetails  + ";failDetails=" + failDetails);
			
			//Talibatchpay.modifyTalibatchpay(batchNo, DateUtil.parse(notifyTime), notifyType, notifyId, signType, successDetails, failDetails);
			
			if (!StringUtil.isEmpty(successDetails)) {
				fundService.batchpaySuccessProcess(successDetails);	
			}
			
			boolean flag = true;
			if (!StringUtil.isEmpty(failDetails)) {
				flag = batchpayFailProcess(failDetails);
			}		
			
			if (flag) {
				out.println("success");
			}
						
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝批量付款通知处理->出现异常", e);			
			return null;
		}
		logger.info("支付宝批量付款通知处理->结束");
		return null;
	}
	
	
	private boolean batchpayFailProcess(String failDetails) throws IOException{
		boolean ret = true;
		String[] records = failDetails.split(AlipayConfig.BATCHPAY_DELIMITER_1);
		for (String record : records) {
			String[] items = record.split(AlipayConfig.BATCHPAY_DELIMITER_2);
			Tcashdetail tcashdetail = Tcashdetail.findTcashdetail(items[0]);
			if (null == tcashdetail) {
				logger.info("提现记录为空，提现id=" + items[0]);
				throw new RuyicaiException(ErrorCode.Taccountdetail_Empty);
			}
			if (tcashdetail.getState().equals(CashDetailState.Chenggong.value())) {
				logger.info("该提现已成功，提现id=" + items[0]);
				//throw new RuyicaiException(ErrorCode.BatchPay_AlreadySuccess);
				continue;
			}			
			if (!tcashdetail.getType().equals(CashDetailType.Zhifubao.value())){
				logger.info("该提现类型为非支付宝提现，提现id=" + items[0]);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailTypeNotAlipay);
			}
			if (!tcashdetail.getState().equals(CashDetailState.Shenghezhong.value())) {
				logger.info("该提现非已审核状态，提现id=" + items[0]);
				//throw new RuyicaiException(ErrorCode.BatchPay_cashdetailStateNotShenghezhong);
				continue;
			}
			
			logger.info("该提现id=" + items[0] + "失败，被驳回，原因：" + items[5]);	
			
			
			StringBuffer param = new StringBuffer();
			param.append("cashdetailId=").append(items[0]).append("&rejectreason=").append(BatchPayCode.getMemo(items[5]));
			String url = ConfigUtil.getConfig("lottery.properties", "bohuiTcashDetail");
			logger.info("支付宝批量付款通知处理->失败处理：url=" + url + ",param=" + param.toString());	
		 
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("支付宝批量付款通知处理->失败处理，返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			String errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!ErrorCode.OK.value.equals(errorCode)) {				
				logger.info("支付宝批量付款通知处理->失败处理，该提现id=" + items[0] + "失败, errorCode=" + errorCode);				
				ret = false;
			} 
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		String str1 = "122|234";
		String str2 = "122|2341|";
		String str3 = "456^222";
		String str4 = "789^3333^";
		System.out.println("str1=" + str1);
		System.out.println("str2=" + str2);
		System.out.println("str3=" + str3);
		System.out.println("str4=" + str4);
		
		String[] arr1 = str1.split(AlipayConfig.BATCHPAY_DELIMITER_1);
		System.out.println("\\|=" + AlipayConfig.BATCHPAY_DELIMITER_1);
		System.out.println("arr1.length=" + arr1.length);
		int i = 0;
		for (String str11 : arr1) {
			i++;
			System.out.println(i + "=" + str11);
		}
		
		i = 0;
		String[] arr2 = str2.split(AlipayConfig.BATCHPAY_DELIMITER_1);
		System.out.println("arr2.length=" + arr2.length);
		for (String str22 : arr2) {
			i++;
			System.out.println(i + "=" + str22);
		}
		
		String[] arr3 = str3.split(AlipayConfig.BATCHPAY_DELIMITER_2);		
		System.out.println("arr3.length=" + arr3.length);
		i = 0;
		for (String str33 : arr3) {
			i++;
			System.out.println(i + "=" + str33);
		}
		
		i = 0;
		String[] arr4 = str4.split(AlipayConfig.BATCHPAY_DELIMITER_2);
		System.out.println("arr4.length=" + arr4.length);
		for (String str44 : arr4) {
			i++;
			System.out.println(i + "=" + str44);
		}
	}

}
