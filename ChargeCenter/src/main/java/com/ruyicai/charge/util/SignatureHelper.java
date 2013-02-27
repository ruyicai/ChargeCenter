package com.ruyicai.charge.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 功能：支付类调取异步返回处理帮助类 公司名称：alipay 修改时间：2008-10-10
 * */
public class SignatureHelper {
	private static final Logger logger = Logger
			.getLogger(SignatureHelper.class);

	public static String sign(Map params, String privateKey) {
		Properties properties = new Properties();

		for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			Object value = params.get(name);

			if (name == null || name.equalsIgnoreCase("sign")
					|| name.equalsIgnoreCase("sign_type")) {
				continue;
			}

			properties.setProperty(name, value.toString());

		}

		String content = getSignatureContent(properties);
		return sign(content, privateKey);
	}

	public static String getSignatureContent(Properties properties) {
		StringBuffer content = new StringBuffer();
		StringBuffer contentReturn = new StringBuffer();
		List keys = new ArrayList(properties.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = properties.getProperty(key);
			// content.append((i == 0 ? "" : "&") + key + "=" + value);
			content.append(key + "=" + value + "&");

		}
		if (content != null) {
			try {
				String kkk = content.substring(0, content.length() - 1);
				contentReturn.append(kkk);
			} catch (Exception e) {
				logger.info("支付宝语音充值返回参数排序出现异常  Exception:" + e.toString());
			}
		}
		logger.info("支付宝语音充值返回排序后的参数  contentReturn="
				+ contentReturn.toString());
		return contentReturn.toString();
	}

	public static String sign(String content, String privateKey) {
		if (privateKey == null) {
			return null;
		}
		String signBefore = content + privateKey;
		return Md5Encrypt.md5(signBefore);

	}
}
