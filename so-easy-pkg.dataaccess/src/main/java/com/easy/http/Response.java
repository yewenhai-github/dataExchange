package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Header;

public class Response {

	/**
	 * 返回中的Header信息
	 */
	private Header[] responseHeaders;

	/**
	 * String类型的result
	 */
	private String stringResult;

	/**
	 * btye类型的result
	 */
	private byte[] byteResult;

	/**
	 * 成功响应标识
	 */
	private boolean success = true;

	/**
	 * 没有成功响应的失败的信息
	 */
	private String failureMessage;

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Header[] responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public byte[] getByteResult() {
		if (byteResult != null) {
			return byteResult;
		}
		if (stringResult != null) {
			return stringResult.getBytes();
		}
		return null;
	}

	public void setByteResult(byte[] byteResult) {
		this.byteResult = byteResult;
	}

	public String getStringResult(String charset) {
		if (stringResult != null) {
			return stringResult;
		}
		if (byteResult != null) {
			try {
				return new String(byteResult, charset);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		return null;
	}

	public void setStringResult(String stringResult) {
		this.stringResult = stringResult;
	}

	/**
	 * 判断是否成功响应
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * 没有成功响应，会有失败的信息
	 * 
	 * @return
	 */
	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

}
