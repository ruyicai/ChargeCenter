package com.ruyicai.charge.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * 功能：支付类
 * 公司名称：alipay
 * 修改时间：2008年10月10日。
 */
public class AlipayUtil {
	private static final Logger logger = Logger.getLogger(AlipayUtil.class);
    public static String CreateUrl(String paygateway, String service, String sign_type,
    		String out_trade_no,String input_charset,
    		String partner,String key,String show_url, 
    		String body, String total_fee, String payment_type,
    		String seller_email,String subject ,String notify_url, 
    		String return_url,String paymethod,String defaultbank) {

        Map params = new HashMap();
        params.put("service", service);
        params.put("partner", partner);
        params.put("subject", subject);
        params.put("body", body);
        params.put("out_trade_no", out_trade_no);
        params.put("total_fee", total_fee);
        params.put("show_url", show_url);
        params.put("payment_type",payment_type);
        params.put("seller_email", seller_email);
        params.put("return_url", return_url);
        params.put("notify_url", notify_url);
        params.put("_input_charset", input_charset);
        params.put("paymethod", paymethod);
        params.put("defaultbank", defaultbank);
       logger.info("支付宝语音充值封装到map 中 params="+params.toString());
        String prestr = "";

        prestr = prestr + key;
        //System.out.println("prestr=" + prestr);

        String sign = Md5Encrypt.md5(getContent(params, key));

        String parameter = "";
        parameter = parameter + paygateway;
        //System.out.println("prestr="  + parameter);
        List keys = new ArrayList(params.keySet());
        for (int i = 0; i < keys.size(); i++) {
          	String value =(String) params.get(keys.get(i));
            if(value == null || value.trim().length() ==0){
            	continue;
            }
            try {
                parameter = parameter + keys.get(i) + "="
                    + URLEncoder.encode(value, input_charset) + "&";
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        }

        parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

        return sign;

    }
    
    public static String CreateUrl2(String paygateway,String service,
        	String partner,String subject,String body,
        	String out_trade_no,String total_fee,String payment_type,
        	String seller_email,String notify_url,String is_ivr_pay,
        	String receive_mobile,String key,String sign_type,
        	String show_url,String quantity,String input_charset) {
    	
		Map params = new HashMap();
		params.put("service", service);
		params.put("partner", partner);
		params.put("subject", subject);
		params.put("body", body);
		params.put("out_trade_no", out_trade_no);
		params.put("total_fee", total_fee);
		params.put("payment_type",payment_type);
		params.put("seller_email", seller_email);
		params.put("notify_url", notify_url);
		params.put("is_ivr_pay", is_ivr_pay);
		params.put("receive_mobile", receive_mobile);
		params.put("quantity", quantity);
		params.put("show_url", show_url);

		params.put("_input_charset", input_charset);

		String prestr = "";

		prestr = prestr + key;
		//System.out.println("prestr=" + prestr);

		String sign = Md5Encrypt.md5(getContent(params, key));

		String parameter = "";
		parameter = parameter + paygateway;
		//对参数进行编码
		List keys = new ArrayList(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			try {
				parameter = parameter + keys.get(i) + "="
				+ URLEncoder.encode((String) params.get(keys.get(i)), input_charset) + "&";
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		}

		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return parameter;

	}
    
    /**
	 * 生成url方法
	 * 网关
	 * @param paygateway
	 * 服务参数
	 * @param service
	 * 签名类型
	 * @param sign_type
	 * 外部订单号
	 * @param out_trade_no
	 * 编码机制
	 * @param input_charset
	 * 合作者ID
	 * @param partner
	 * 安全校验码
	 * @param key
	 * 商品展示地址
	 * @param show_url
	 * 商品描述
	 * @param body
	 * 商品价格
	 * @param total_fee
	 * 支付类型
	 * @param payment_type
	 * 卖家账户
	 * @param seller_email
	 * 商品名称
	 * @param subject
	 * 异步返回地址
	 * @param notify_url
	 * 同步返回地址
	 * @param return_url
	 * 支付方式
	 * @param paymethod
	 * 默认银行
	 * @param defaultbank
	 * 支付宝快捷登录返回字段
	 * @param token
	 * @return
	 */
    public static String CreateUrl(String paygateway, String service, String sign_type,
    		String out_trade_no,String input_charset,
    		String partner,String key,String show_url, 
    		String body, String total_fee, String payment_type,
    		String seller_email, String subject ,String notify_url, 
    		String return_url,String paymethod,String  defaultbank,String token) {//, String it_b_pay

        Map params = new HashMap();
        params.put("service", service);
        params.put("partner", partner);
        params.put("subject", subject);
        params.put("body", body);
        params.put("out_trade_no", out_trade_no);
        params.put("total_fee", total_fee);
        params.put("show_url", show_url);
        params.put("payment_type",payment_type);
        params.put("seller_email", seller_email);
        //params.put("buyer_email", buyerAccountName);//买家账号
//        params.put("it_b_pay", it_b_pay);
        params.put("return_url", return_url);
        params.put("notify_url", notify_url);
        params.put("_input_charset", input_charset);
        params.put("paymethod", paymethod);
        if(null != defaultbank && !"".equals(defaultbank) && "bankPay".equals(paymethod))
        	params.put("defaultbank", defaultbank);
        if(token != null && !"".equals(token)){
        	params.put("token", token);
        }
        logger.info("params="+params.toString());
        String prestr = "";

        prestr = prestr + key;
        System.out.println("prestr=" + prestr);

        String sign = Md5Encrypt.md5(getContent(params, key));

        String parameter = "";
        parameter = parameter + paygateway;

        List keys = new ArrayList(params.keySet());
        for (int i = 0; i < keys.size(); i++) {
            try {
                parameter = parameter + keys.get(i) + "="
                            + URLEncoder.encode((String) params.get(keys.get(i)), input_charset) + "&";
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
        }

        parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

        return parameter;

    }

    /**
     * 参数排序
     * @param params
     * @param privateKey
     * @return
     */
    private static String getContent(Map params, String privateKey) {
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);//����

        String prestr = "";
      //重新构建排序后的参数
		boolean first = true;
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);
			if (value == null || value.trim().length() == 0) {
				continue;
			}
			if (first) {
				prestr = prestr + key + "=" + value;
				first = false;
			} else {
				prestr = prestr + "&" + key + "=" + value;
			}
		}

        return prestr + privateKey;
    }
}

