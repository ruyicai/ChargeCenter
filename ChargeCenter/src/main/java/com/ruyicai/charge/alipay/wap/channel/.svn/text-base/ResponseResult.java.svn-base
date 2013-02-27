package com.ruyicai.charge.alipay.wap.channel;

import com.ruyicai.charge.alipay.client.security.ErrorCode;

public class ResponseResult {

	/**
	 * 是否调用成功 默认为false 所以在每次调用都必须设置这个值为true；
	 */
	private boolean isSuccess = false;
	
	/**
	 * 调用的业务成功结果 如果调用失败 这个将是空值
	 */
	private String businessResult;

	/**
	 * 错误信息
	 */
	private ErrorCode errorMessage;

	/**
	 * @return Returns the errorMessage.
	 */
	public ErrorCode getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *            The errorMessage to set.
	 */
	public void setErrorMessage(ErrorCode errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return Returns the businessResult.
	 */
	public String getBusinessResult() {
		return businessResult;
	}

	/**
	 * @param businessResult
	 *            The businessResult to set.
	 */
	public void setBusinessResult(String businessResult) {
		this.businessResult = businessResult;
	}

	/**
	 * @return Returns the isSuccess.
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess
	 *            The isSuccess to set.
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}

