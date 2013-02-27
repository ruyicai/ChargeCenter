package com.ruyicai.charge.alipay.tradequery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


public class AlipayService {	
	/** 
	 * 功能：把数组所有元素按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @param input_charset 编码格式
	 * @return 拼接后字符串
	 */
	public static String CreateLinkStringUrlencode(Map params, String input_charset){
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);

			try {
				prestr = prestr + key + "=" + URLEncoder.encode(value, input_charset) + "&";
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		}

		System.out.println("CreateLinkStringUrlencode->prestr=" + prestr);
		return prestr;
	}
	
	/**
	 * 功能：远程xml解析
	 * @param partner 合作身份者ID
	 * @param out_trade_no 商户网站已经付款完成的商户网站订单号
	 * @param trade_no 已经付款完成的支付宝交易号，与商户网站订单号out_trade_no相对应
	 * @param input_charset 字符编码格式 目前支持 GBK 或 utf-8
	 * @param key 安全校验码
	 * @param sign_type 签名方式 不需修改
	 * @return 获得解析结果
	 */
	public static String PostXml(String partner, String out_trade_no, String trade_no, String input_charset, String key,
            String sign_type, String requrl) throws Exception{
		Map sPara = new HashMap();
		sPara.put("_input_charset", input_charset);
		sPara.put("out_trade_no", out_trade_no);
		sPara.put("trade_no", trade_no);
		sPara.put("partner", partner);
		sPara.put("service","single_trade_query");
		
		Map sParaNew = AlipayFunction.ParaFilter(sPara); //除去数组中的空值和签名参数
		String mysign = AlipayFunction.BuildMysign(sParaNew, key);//生成签名结果
		
		sParaNew.put("sign", mysign);
		sParaNew.put("sign_type", "MD5");
		
		//String strUrl = "https://www.alipay.com/cooperate/gateway.do?";
		//String strUrl = "https://www.alipay.com/cooperate/gateway.do?_input_charset=utf-8";
		//String strUrl = "https://mapi.alipay.com/gateway.do?_input_charset=utf-8";
		URL url = new URL(requrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		OutputStream os = conn.getOutputStream();
		os.write(CreateLinkStringUrlencode(sParaNew, input_charset).getBytes("utf-8"));
		os.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		String xmlResult = "";
		while( (line = br.readLine()) != null ){
			//xmlResult += "\n" + new String(line.getBytes("gbk"), "utf-8");
			//xmlResult += "\n" + line;
			xmlResult += line;
		}
		br.close();

		return xmlResult;
	}
}
