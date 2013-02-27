package com.ruyicai.charge.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ruyicai.charge.exception.RuyicaiException;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * JSON转换工具类
 */
public class JsonUtil {

	/**
	 * json字符串转换为Map<String,Object>格式
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, Object> transferJson2Map(String json) {
		if (StringUtils.isBlank(json)) {
			return new HashMap<String, Object>();
		}
		Map<String, Object> result = new JSONDeserializer<Map<String, Object>>().use(null,
				new HashMap<String, Object>().getClass()).deserialize(json);
		return result;
	}

	public static String toJson(Map map) {
		try {
			return new JSONSerializer().serialize(map);
		} catch (Exception e) {
			throw new RuyicaiException("toJson error", e);
		}
	}
	
}
