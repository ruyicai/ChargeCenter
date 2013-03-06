package com.ruyicai.charge.dna.v2.common;

/** 参数格式异常
 * 
 * @author Administrator
 */
public class ArgumentFormatException extends RuntimeException {
	public ArgumentFormatException(String msg){
		super(msg);
	}
	
	public ArgumentFormatException(){
		super();
	}
}
