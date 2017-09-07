package com.loveyou.webController.common;

public class ParameterMissingException extends RuntimeException {

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 1L;

	private String msg;

	public ParameterMissingException(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "ParameterMissingException 缺少参数：" + msg;
	}
}
