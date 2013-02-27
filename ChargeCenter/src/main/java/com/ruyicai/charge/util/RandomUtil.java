package com.ruyicai.charge.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomUtil {

	public static String genRandomNum(int n) {
		Random random = new Random();
		String sRand = "";
		for (int i = 0; i < n; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		if (sRand.substring(0, 1).equals("0")) {
			sRand = "1" + sRand.substring(1);
		}
		return sRand;
	}
	
	public static <T> T random(List<T> list) {
		return list.get(new Random().nextInt(list.size()));
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("\\-", "");
	}
}
