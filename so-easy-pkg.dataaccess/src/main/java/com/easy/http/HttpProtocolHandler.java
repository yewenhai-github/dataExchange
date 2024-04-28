package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class HttpProtocolHandler implements ProtocolHandler {

	/**
	 * HTTP连接管理器，该连接管理器必须是线程安全的.
	 */
	private HttpConnectionManager connectionManager;

	private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();

	/**
	 * 工厂方法
	 * 
	 * @return
	 */
	public static HttpProtocolHandler getInstance() {
		return httpProtocolHandler;
	}

	private HttpProtocolHandler() {
		// 创建一个线程安全的HTTP连接池
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(HttpConfig.httpDefaultMaxConnPerHost);
		connectionManager.getParams().setMaxTotalConnections(HttpConfig.httpDefaultMaxTotalConn);
		// 定期关闭空闲连接
		IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
		ict.addConnectionManager(connectionManager);
		// 单个request连接闲置时间
		ict.setConnectionTimeout(HttpConfig.httpDefaultIdleConnTimeout);
		ict.start();
	}

	/**
	 * 执行Http请求
	 * 
	 * @param request
	 * @return
	 * @throws UnknownHostException
	 */
	public synchronized Response execute(Request request) {
		return this.execute(request, null);
	}

	public synchronized Response execute(Request request, NTProxy ntProxy) {
		HttpClient httpclient = new HttpClient(connectionManager);

		// 设置连接超时
		httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(HttpConfig.httpRequestDefaultConnectionTimeout);

		// 设置回应超时
		httpclient.getHttpConnectionManager().getParams().setSoTimeout(HttpConfig.httpDefaultSoTimeout);

		// 设置等待ConnectionManager释放connection的时间
		httpclient.getParams().setConnectionManagerTimeout(HttpConfig.httpDefaultConnectionManagerTimeout);

		if (ntProxy != null) {
			httpclient.getHostConfiguration().setProxy(ntProxy.getProxyHost(), ntProxy.getProxyPort());
			NTCredentials proxyCredentials = new NTCredentials(ntProxy.getUserName(), ntProxy.getPassword(), ntProxy.getLocalHost(), ntProxy.getDomain());
			httpclient.getState().setProxyCredentials(null, null, proxyCredentials);
		}

		HttpMethod method = null;

		if (request.getMethod().equals(ProtocolConstant.MethodType.GET.getValue())) {
			method = new GetMethod(request.getUrl());
			method.getParams().setCredentialCharset(request.getCharset());

			// parseNotifyConfig会保证使用GET方法时，request一定使用QueryString
			method.setQueryString(request.getQueryString());
		} else {
			method = new PostMethod(request.getUrl());
			((PostMethod) method).addParameters(request.getParameters());
			method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + request.getCharset());
		}
		// 设置Http Header中的User-Agent属性
		method.addRequestHeader("User-Agent", "Mozilla/4.0");
		Response response = new Response();
		try {
			httpclient.executeMethod(method);
			if (request.getDataType().equals(ProtocolConstant.DataType.STRING.getValue())) {
				response.setStringResult(method.getResponseBodyAsString());
			} else if (request.getDataType().equals(ProtocolConstant.DataType.BYTES.getValue())) {
				response.setByteResult(method.getResponseBody());
			} else if (request.getDataType().equals(ProtocolConstant.DataType.INPUTSTREAM.getValue())) {
				InputStream is = method.getResponseBodyAsStream();
				response.setStringResult(new String(SysUtility.InputStreamToByte(is),"UTF-8"));
			} else{
				response.setStringResult(method.getResponseBodyAsString());
			}
			response.setResponseHeaders(method.getResponseHeaders());
		} catch (Exception e) {
			LogUtil.printLog(request.getUrl()+" Error:"+e.getMessage(), Level.ERROR);
			// 响应失败
			response.setSuccess(false);
			response.setFailureMessage(e.getMessage());
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 将NameValuePairs数组转变为字符串
	 * 
	 * @param nameValues
	 * @return
	 */
	protected String toString(NameValuePair[] nameValues) {
		if (nameValues == null || nameValues.length == 0) {
			return "null";
		}

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < nameValues.length; i++) {
			NameValuePair nameValue = nameValues[i];

			if (i == 0) {
				buffer.append(nameValue.getName() + "=" + nameValue.getValue());
			} else {
				buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
			}
		}

		return buffer.toString();
	}
	
	
	public synchronized Response executeCitic(Request request) {
		HttpClient httpclient = new HttpClient(connectionManager);

		// 设置连接超时
		httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(HttpConfig.httpRequestDefaultConnectionTimeout);

		// 设置回应超时
		httpclient.getHttpConnectionManager().getParams().setSoTimeout(HttpConfig.httpDefaultSoTimeout);

		// 设置等待ConnectionManager释放connection的时间
		httpclient.getParams().setConnectionManagerTimeout(HttpConfig.httpDefaultConnectionManagerTimeout);

		PostMethod method = new PostMethod(request.getUrl());
		method.getParams().setContentCharset(request.getCharset());
		StringRequestEntity requestEntity = null;
		try {
			requestEntity = new StringRequestEntity(request.getQueryString(),null,request.getCharset());
		} catch (UnsupportedEncodingException e) {
			LogUtil.printLog("executeCitic error:"+e.getMessage(), Level.ERROR);
		}
		method.setRequestEntity(requestEntity);
		Response response = new Response();
		try {
			httpclient.executeMethod(method);
			response.setStringResult(method.getResponseBodyAsString());
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			// 响应失败
			response.setSuccess(false);
			response.setFailureMessage(e.getMessage());
		} finally {
			method.releaseConnection();
		}
		return response;
	}
}
