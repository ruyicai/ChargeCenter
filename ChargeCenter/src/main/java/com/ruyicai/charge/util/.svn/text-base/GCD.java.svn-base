package com.ruyicai.charge.util;

public class GCD {
	/*
	 * 辗转相除法, 又名欧几里德算法 设两数为a、b(b＜a)，求它们最大公约数(a、b)的步骤如下：用b除a，得a＝bq......r1(0≤r)。
	 * 若r1=0，则(a，b)＝b；若r1≠0，则再用r1除b，得b＝r1q......r2(0≤r2).若r2＝0，则(a，b)＝r1，若r2≠0，
	 * 则继续用r2除r1,…… 如此下去，直到能整除为止。其最后一个非零余数即为(a，b)。
	 */
	public static int getGCD(int a, int b) {// 递归法
		if (a > b) {// 始终保证a<b
			int temp = a;
			a = b;
			b = temp;
		}
		if (b % a == 0)
			return a;
		else
			return getGCD(a, b % a);
	}
}
