package com.ruyicai.charge.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.consts.TransactionState;
import com.ruyicai.charge.dna.common.Strings;
import com.ruyicai.charge.dna.common.ToolKit;
import com.ruyicai.charge.dna.common.encrpt.MD5;
import com.ruyicai.charge.dna.thirdpart.PosMessage;
import com.ruyicai.charge.dna.thirdpart.TransactionUtil;
import com.ruyicai.charge.domain.Dnapay;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;

/**
 * Web网站和Wap网站-DNA手机银行充值:响应DNA异步返回数据
 * @author Administrator
 *
 */
public class DNABankChargeNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	private static ResourceBundle rbint = ResourceBundle.getBundle("charge"); 
	private Logger logger = Logger.getLogger(DNABankChargeNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Autowired
	ChargeUtil chargeUtil;
	
	@Autowired 
	ChargeconfigService chargeconfigService;

	/**
	 * DNA银行支付通知处理
	 * 
	 * @return
	 */
	public String dnaBankChargeServlet() {
		logger.info("DNA银行支付通知处理->开始");
		
		PosMessage pm = new PosMessage();
		try {
			pm = this.parseParams(request, pm);
		} catch (Exception e1) {
			logger.error("DNA银行支付通知处理->返回数据的获取发生了错误:", e1);			
			return null;
		}
		
		if (pm == null) {
			logger.info("DNA银行支付通知处理->返回数据的获取发生了错误(pm):\n" + pm.toString());
			return null;
		}
		logger.info("DNA银行支付通知处理->完整的返回数据(如下所示):\n" + pm.toString());
		
		String mac = request.getParameter("Mac");
		if (mac==null || mac.equals("")) {
			logger.info("DNA银行支付通知处理->返回数据的mac参数获取发生了错误:mac=" + mac);
			return null;
		}
		pm.setMac(mac);
		logger.info("DNA银行支付通知处理->返回数据的获取mac参数:mac=" + mac);
		
		String merchantPWD = chargeconfigService.getChargeconfig("DNAMerchantPw");//rbint.getString("DNAMerchantPw");//获取商户密钥
		if (merchantPWD == null || merchantPWD.equals("")) {
			logger.info("DNA银行支付通知处理->返回数据的获取mac参数:mac=" + mac);
			return null;
		}
	    
		String macString = TransactionUtil.getMacString(pm);
		logger.info("DNA银行支付通知处理->返回数据的获取macString参数:macString=" + macString);
		MD5 md5 = new MD5();//将返回数据进行MD5加密后 与返回的 MAC进行比对
		String macLocal = md5.getMD5ofStr(macString + " " + merchantPWD);
		logger.info("DNA银行支付通知处理->本地MD5(mdcLocal):" + macLocal);
		
		logger.info("DNA银行支付通知处理->MD5校验开始");		
		if(!macLocal.equals(mac)){
			logger.info("DNA银行支付通知处理->MD5校验失败，数据可能曾经被篡改->MD5本地加密:" + macLocal + "  对比   " + mac + "  不一致");
			return null;
		}
		logger.info("DNA银行支付通知处理->MD5校验结束：校验成功");
		
		logger.info("DNA银行支付通知处理->开始查询充值交易");
		String orderNoLocal = pm.getOrderNo().substring(2, pm.getOrderNo().indexOf("|")).replace("|", "");
		logger.info("DNA银行支付通知处理->本地交易号Id=" + orderNoLocal);
	    //查询订单号是否存在	
		String httpPostLotteryUrl = chargeconfigService.getChargeconfig("lotteryGetTtransactionById");//ConfigUtil.getConfig("lottery.properties","lotteryGetTtransactionById");
		String param = "id=" + orderNoLocal;
		String result = null;
		try {
			logger.info("DNA银行支付通知处理->根据id获取交易记录->请求地址url：" + httpPostLotteryUrl
					+  "请求参数param：" + param);
			result = HttpRequest.doPostRequest(httpPostLotteryUrl, param);
		} catch (IOException e) {
			logger.error("DNA银行支付通知处理->根据id获取交易记录->出现异常", e);
			return null;
		}
		
		logger.info("DNA银行支付通知处理->根据id获取交易记录->返回结果result=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode2 = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		Map<String,Object> mapValue = (Map<String, Object>) mapResult.get("value");
		String userno = mapValue.get("userno").toString();
		String amtStr = mapValue.get("amt").toString();
		if (userno == null || userno.equals("") || amtStr ==null || amtStr.equals("")) {
			logger.error("DNA银行支付通知处理->根据id获取交易记录->返回结果userno或amt为空");
			logger.error("DNA银行支付通知处理->Method:DNA支付处理后的返回码 在servlet中的处理，交易记录中ID为" + pm.getOrderNo() + "订单不存在");//订单号必须存在		
			return null;
		}
		
		String respCode = pm.getRespCode();
		String remark = pm.getRemark();
		logger.info("DNA银行支付通知处理->返回码->pm.getRespCode()=" + respCode + ";pm.getRemark()=" + remark);
		if (remark == null || "".equals(remark)) {
			remark = " ";
		}
		String state = TransactionState.fail.value().toString();
		
		if(respCode.equals("0000")){//如果返回码为0000 标识支付成功。进行数据库处理  否则执行else的订单失败 数据库处理	
			state = TransactionState.ok.value().toString();
			
			try {
				logger.info("DNA银行支付通知处理->查询用户及修改dna绑定记录开始");
				/*
				 * 修改用户DNA账户绑定记录状态为有效 20100415 by spl
				 */
				httpPostLotteryUrl = chargeconfigService.getChargeconfig("lotteryModifyTdnabindState");//ConfigUtil.getConfig("lottery.properties", "lotteryModifyTdnabindState");

				param = "userno=" + userno + "&state=1";
				try {
					logger.info("DNA银行支付通知处理->根据userno及state修改dna绑定信息->请求地址url："
							+ httpPostLotteryUrl + "请求参数param：" + param);
					result = HttpRequest.doPostRequest(httpPostLotteryUrl, param);
				} catch (IOException e) {
					logger.error("DNA银行支付通知处理->根据userno及state修改dna绑定信息->出现异常", e);			
					return null;
				}

				logger.info("DNA银行支付通知处理->根据userno及state修改dna绑定信息->返回结果result=" + result);				
				logger.info("DNA银行支付通知处理->用户userno=" + userno + "生效状态修改为1---");
				logger.info("DNA银行支付通知处理->查询用户及修改dna绑定记录结束");
			} catch (Exception w) {
				logger.error("DNA银行支付通知处理->异常：", w);
			}
			logger.info("DNA银行支付通知处理->开始查询充值交易结束");
			
		    ////////////////////////////成功后的 数据库处理/////////////////////////////
			logger.info("DNA银行支付通知处理->充值成功的返回码0000,开始进行校验，并执行数据库...");			
			
			DecimalFormat df2 = new DecimalFormat("#0.00");
	    	Double amt = Double.parseDouble(pm.getAmount());
	    	Double amtTrans = Double.parseDouble(df2.format(amt));//订单金额（2位小数）
	    	amtTrans = amtTrans*100; //
	    	String amtForTrans = String.valueOf(amtTrans);//订单金额（ 分）
			if(amtTrans.longValue()!=(new BigDecimal(amtStr).longValue())){  //订单金额与银行返回信息中的金额不等
				logger.error("DNA银行支付通知处理->Method:DNA:" + pm.getOrderNo()+ ",订单金额与银行返回信息中的金额不相等.amtForTrans:"
						+ amtForTrans + ",transaction.AMT:" + amtStr);
				logger.info("DNA银行支付通知处理->数据库中金额 " + amtStr + " 与 返回金额 " + amtForTrans + " 不一致");
				return null;
			}
			logger.info("DNA银行支付通知处理->DNA返回的充值金额amtForTrans=" + amtForTrans + ",数据库保存的充值金额balance=" + amtStr);
			
			String bankNo = "";			
			try {
				String bankCardNo = pm.getAccountNum();
				String[] params = new String[2];
				
				if(bankCardNo!=null && bankCardNo.length()>0)
				{
					logger.info("DNA银行支付通知处理->DNA返回值：bankCardNo=" + bankCardNo);
					params = bankCardNo.split("\\|");
					if(params != null)
					{
						bankNo = params[1];
						logger.info("DNA银行支付通知处理->DNA返回值：bankNo=" + bankNo);
					}
					else
					{
						logger.info("DNA银行支付通知处理->DNA返回值：pm.getAccountNum参数不包括银行卡号：bankNo=" + bankNo);
					}
				}
				else
				{
					logger.info("DNA银行支付通知处理->DNA返回值：pm.getAccountNum为空," + bankCardNo);
				}
				logger.info("DNA银行支付通知处理->交易参数设置成功");
			} catch (Exception e) {
				logger.error("DNA银行支付通知处理->交易参数设置出现异常:", e);
				return null;
			}
			
			logger.info("DNA银行支付通知处理->开始执行给用户加钱操作");			    			
			String drawamt = ChargeUtil.getDrawamt(amtStr);			
			try {
				StringBuffer sb = new StringBuffer();
				sb.append("ttransactionid=")
						.append(orderNoLocal)
						.append("&bankorderid=")
						.append(orderNoLocal)
						.append("&bankordertime=")
						.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
						.append("&banktrace=")
						.append(" ")
						.append("&retcode=")
						.append(respCode)
						.append("&retmemo=")
						.append(remark)
						.append("&amt=")
						.append(amtStr)
						.append("&drawamt=")
						.append(drawamt);
				
				String lotterySuccessUrl = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl")
				logger.info("DNA银行支付通知处理->充值成功请求：url=" + lotterySuccessUrl + ",请求参数（sb）=" + sb.toString());
				result = HttpRequest.doPostRequest(lotterySuccessUrl, sb.toString());
				logger.info("DNA银行支付通知处理->充值成功处理返回: result=" + result);
				chargeUtil.afterCharge(result);
			} catch (Exception e) {	
				logger.error("DNA银行支付通知处理->充值成功处理->返回result="+ result + "出现异常e=", e);
				return null;
			}			
		
		} else{
			state = TransactionState.fail.value().toString();
			
			try {
				logger.info("DNA银行支付通知处理->充值失败处理");
				StringBuffer sb = new StringBuffer();
				sb.append("ttransactionid=")
						.append(orderNoLocal)						
						.append("&bankordertime=")
						.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
						.append("&banktrace=")
						.append(" ")
						.append("&retcode=")
						.append(respCode)
						.append("&retmemo=")
						.append(remark)
						.append("&amt=")
						.append(amtStr);
				
				
				String lotteryFailUrl = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl")
				logger.info("DNA银行支付通知处理->充值失败请求：url=" + lotteryFailUrl + ",请求参数（sb）=" + sb.toString());
				result = HttpRequest.doPostRequest(lotteryFailUrl, sb.toString());
				logger.info("DNA银行支付通知处理->充值失败处理返回: result=" + result);				
			} catch (Exception e) {
				logger.error("DNA银行支付通知处理->充值失败处理->返回result="+ result + "出现异常", e);
				return null;
			}			
		}		
		
		modifyDnapay(orderNoLocal, respCode, remark, state);
		logger.info("DNA银行支付通知处理->结束");
		return null;
	}

	private void modifyDnapay(String transactionid, String retcode, String retmemo, String state) {
		try {
			Dnapay dnapay = Dnapay.modifyDnapay(transactionid, retcode, retmemo, state);
			logger.info("modifyDnapay:" + dnapay.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("modifyDnapay error:", e);
		}
	}
	
	private PosMessage parseParams(HttpServletRequest request, PosMessage httpOrder) throws Exception {
        httpOrder.setReturnAddress(request.getParameter("ReturnAddress"));
        httpOrder.setProcCode(request.getParameter("ProcCode"));

        if (httpOrder.getProcCode().startsWith("C") && !Strings.isNullOrEmpty(request.getParameter("AccountNum"))) {
            httpOrder.setAccountNum(ToolKit.unSign(request.getParameter("AccountNum")));
        } else {
            httpOrder.setAccountNum(request.getParameter("AccountNum"));
        }

        httpOrder.setProcessCode(request.getParameter("ProcessCode"));
        httpOrder.setAmount(request.getParameter("Amount"));
        httpOrder.setCurCode(request.getParameter("CurCode"));
        httpOrder.setTransDatetime(request.getParameter("TransDatetime"));
        httpOrder.setAcqSsn(request.getParameter("AcqSsn"));
        httpOrder.setLtime(request.getParameter("Ltime"));
        httpOrder.setLdate(request.getParameter("Ldate"));
        httpOrder.setSettleDate(request.getParameter("SettleDate"));
        httpOrder.setUpsNo(request.getParameter("UpsNo"));
        httpOrder.setTsNo(request.getParameter("TsNo"));
        httpOrder.setReference(request.getParameter("Reference"));
        httpOrder.setRespCode(request.getParameter("RespCode"));
        httpOrder.setTerminalNo(request.getParameter("TerminalNo"));
        httpOrder.setMerchantNo(request.getParameter("MerchantNo"));
        httpOrder.setOrderNo(request.getParameter("OrderNo"));
        httpOrder.setOrderState(request.getParameter("OrderState"));
        if (!Strings.isNullOrEmpty(request.getParameter("Description"))) {
            //httpOrder.setDescription(new String(request.getParameter("Description").getBytes("ISO8859-1"), "UTF-8"));
            httpOrder.setDescription(request.getParameter("Description"));
        }
        if (!Strings.isNullOrEmpty(request.getParameter("Remark"))) {
            //httpOrder.setRemark(new String(request.getParameter("Remark").getBytes("ISO8859-1"), "UTF-8"));
            httpOrder.setRemark(request.getParameter("Remark"));
        }
        httpOrder.setValidTime(request.getParameter("ValidTime"));
        httpOrder.setOrderType(request.getParameter("OrderType"));
        if (!Strings.isNullOrEmpty(request.getParameter("TransData"))) {
            //httpOrder.setTransData(new String(request.getParameter("TransData").getBytes("ISO8859-1"), "UTF-8"));
            httpOrder.setTransData(request.getParameter("TransData"));
        }
        httpOrder.setPin(request.getParameter("Pin"));
        httpOrder.setLoginPin(request.getParameter("LoginPin"));
        httpOrder.setMac(request.getParameter("Mac"));
        
        
        return httpOrder;

    }

	
	
	

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
//		try {
//			arg0.setCharacterEncoding("gb2312");
//		} catch (UnsupportedEncodingException e) {
//			logger.info("request:转换编码出错");
//			e.printStackTrace();
//		}
		
		this.request = arg0;
	}
}
