package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.NameValuePair;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class HttpsProtocolHandler implements ProtocolHandler {
	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private static HttpsProtocolHandler httpsProtocolHandler = new HttpsProtocolHandler();

	public static HttpsProtocolHandler getInstance() {
		return httpsProtocolHandler;
	}

	public Response execute(Request request) {
		return execute(request, null);
	}

	public Response execute(Request request, NTProxy ntProxy) {
		Response response = new Response();

		StringBuffer stringResult = new StringBuffer();

		HttpsURLConnection httpsURLConnection = null;
		BufferedReader bufferedReader = null;
		OutputStream outputStream = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			String requestUrl = request.getUrl();
			// url请求加上随机数
			if (request.getUrl().indexOf("?") < 0) {
				requestUrl += "?requestRandom=" + new SimpleDateFormat(ProtocolConstant.DATE_TIME_FORMAT).format(new Date());
			}
			URL url = new URL(requestUrl);
			httpsURLConnection = (HttpsURLConnection) url.openConnection();
			httpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
			httpsURLConnection.setHostnameVerifier(new TrustAnyHostnameVerifier());
			// 设置发送数据的方式post,get
			httpsURLConnection.setRequestMethod(request.getMethod());
			httpsURLConnection.setDoInput(true);
			if (request.getMethod().equals(ProtocolConstant.MethodType.POST.getValue())) {
				httpsURLConnection.setDoOutput(true);
			}
			httpsURLConnection.connect();
			
			// 构建发送数据的url格式，并设置字符集
			if (request.getMethod().equals(ProtocolConstant.MethodType.POST.getValue())) {
				outputStream = httpsURLConnection.getOutputStream(); 
				outputStream.write(buildPostData(request).getBytes(request.getCharset()));
			}

			bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringResult.append(line);
			}
			if (request.getDataType().equals(ProtocolConstant.DataType.BYTES.getValue())) {
				response.setByteResult(stringResult.toString().getBytes());
			} else {
				response.setStringResult(stringResult.toString());
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), LogUtil.ERROR, e);
			// 响应失败
			response.setSuccess(false);
			response.setFailureMessage(e.getMessage());
		} finally {
			// 回收
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (httpsURLConnection != null) {
					httpsURLConnection.disconnect();
				}
			} catch (IOException e) {
				LogUtil.printLog(e.getMessage(), LogUtil.ERROR);
			}
		}
		return response;
	}

	/**
	 * 将NameValuePair 数据组装成 a=1?b=2?c=3
	 * 
	 * @param request
	 * @return
	 */
	private String buildPostData(Request request) {
		StringBuffer stringBuffer = new StringBuffer();
		NameValuePair[] nameValuePairs = request.getParameters();
		if(SysUtility.isEmpty(nameValuePairs)) {
			return "";
		}
		for (int i = nameValuePairs.length - 1; i >= 0; i--) {
			NameValuePair nameValuePair = nameValuePairs[i];
			stringBuffer.append(nameValuePair.getName());
			stringBuffer.append("=");
			stringBuffer.append(nameValuePair.getValue());
			if (i != 0) {
				stringBuffer.append("&");
			}
		}
		return stringBuffer.toString();
	}
}
