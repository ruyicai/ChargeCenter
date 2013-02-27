package com.ruyicai.charge.alipay.batchpay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruyicai.charge.alipay.tradequery.Md5Encrypt;

public class Payment {
	public static String CreateUrl(String paygateway, String service,
			String partner, String sign_type, String batch_no,
			String account_name, String email, String pay_date,
			String notify_url, String batch_fee, String batch_num,
			String detail_data, String key, String input_charset
	) {

		Map params = new HashMap();
		params.put("service", service);
		params.put("partner", partner);
		params.put("batch_no", batch_no);
		params.put("account_name", account_name);
		params.put("email", email);
		params.put("pay_date", pay_date);
		params.put("notify_url", notify_url);
		params.put("batch_fee", batch_fee);
		params.put("batch_num", batch_num);
		params.put("detail_data", detail_data);
		params.put("_input_charset", input_charset);

		String prestr = "";

		prestr = prestr + key;
		// System.out.println("prestr=" + prestr);

		String sign = Md5Encrypt.md5(getContent(params, key));

		String parameter = "";
		parameter = parameter + paygateway;

		List keys = new ArrayList(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			try {
				parameter = parameter
						+ keys.get(i)
						+ "="
						+ URLEncoder.encode((String) params.get(keys.get(i)),
								input_charset) + "&";
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		}

		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return sign;

	}

	/**
	 * 功能：将安全校验码和参数排序 参数集合
	 * 
	 * @param params
	 *            安全校验码
	 * @param privateKey
	 * */
	private static String getContent(Map params, String privateKey) {
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr + privateKey;
	}
}
