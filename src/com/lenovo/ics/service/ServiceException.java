package com.lenovo.ics.service;

/**
 * @类功能描述：业务异常类
 * 
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = 7970277247809801379L;

	private int errorCode;

	public ServiceException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
