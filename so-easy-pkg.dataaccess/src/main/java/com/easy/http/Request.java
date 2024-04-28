package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

public class Request {
	/**
	 * 待请求的url
	 */
	private String url = null;

	/**
	 * 默认的请求方式
	 */
	private String method = ProtocolConstant.MethodType.DEFAULT.getValue();

	// private int timeout = 0;

	// private int connectionTimeout = 0;

	/**
	 * Post方式请求时组装好的参数值对
	 */
	private NameValuePair[] parameters = null;

	/**
	 * Get方式请求时对应的参数
	 */
	private String queryString = null;

	/**
	 * 默认的请求编码方式
	 */
	private String charset = ProtocolConstant.Charset.DEFAULT.getValue();

	/**
	 * 请求发起方的ip地址
	 */
	private String clientIp;

	/**
	 * 请求返回的方式
	 */
	private String dataType = ProtocolConstant.DataType.DEFAULT.getValue();

	/**
	 * 默认请求数据类型(dataType)为String,请求字符集(charset)为UTF-8，请求方式(method)为post,请求参数为空
	 * 
	 * @param url
	 */
	public Request(String url) {
		super();
		this.url = url;
	}

	/**
	 * 默认请求数据类型(dataType)为String,请求字符集(charset)为UTF-8，请求方式(method)为post
	 * 
	 * @param parameters请求参数
	 * @param url请求地址
	 */
	public Request(Map<String, String> parameters, String url) {
		super();
		this.setParameters(parameters);
		this.url = url;
	}

	public Request(String dataType, String charset, Map<String, String> parameters, String url) {
		super();
		this.dataType = dataType;
		this.charset = charset;
		this.setParameters(parameters);
		this.url = url;
	}

	public Request(String dataType, String charset, Map<String, String> parameters, String url, String method) {
		super();
		this.dataType = dataType;
		this.charset = charset;
		this.setParameters(parameters);
		this.url = url;
		this.method = method;
	}

	/**
	 * @return Returns the clientIp.
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * @param clientIp
	 *            The clientIp to set.
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public NameValuePair[] getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		if (parameters != null) {
			NameValuePair[] nameValuePair = new NameValuePair[parameters.size()];
			int i = 0;
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
			}
			this.parameters = nameValuePair;
		}
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return Returns the charset.
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset
	 *            The charset to set.
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
