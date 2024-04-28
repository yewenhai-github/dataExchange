package com.easy.http;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONException;

import com.easy.exception.ErrorCode;
import com.easy.exception.LegendException;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class HttpUtility {
	
	public static synchronized String execute(String url,String data) {
		String rtMsg = "";
		HttpURLConnection conn = null;
		try {
			URL urls = new URL(url);
			conn = (HttpURLConnection) urls.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			conn.connect();
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.write(data.getBytes());
			out.flush();
			out.close();
			int responseCode = conn.getResponseCode();
			if (responseCode == 200){
				InputStream is = conn.getInputStream();
				rtMsg = new String(SysUtility.InputStreamToByte(is),"UTF-8");
			}
		} catch (Exception e) {
			LogUtil.printLog(ErrorCode.ErrorCode61+e.getMessage(), Level.ERROR);
			rtMsg = ErrorCode.ErrorCode61+e.getMessage();
		} finally{
			if(SysUtility.isNotEmpty(conn)){
				conn.disconnect();
			}
		}
		return rtMsg;
	}
	
	public static String clientInvokeServer(String appIp,String appPort,String appName,String appServletName,Map<String, String> postParam) throws JSONException, LegendException{
		if(SysUtility.isEmpty(appIp) || SysUtility.isEmpty(appPort) || SysUtility.isEmpty(appName)
				 || SysUtility.isEmpty(appServletName) || SysUtility.isEmpty(postParam)){
			return "";
		}
		
		String url = "http://"+appIp+":"+appPort+"/"+appName+"/"+appServletName;
		Request request = new Request(postParam, url);
		request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
		Response response = ProtocolUtil.execute(request);
		String rt = "";
		if (response.isSuccess()) {
			rt = response.getStringResult("UTF-8");
		} else {
			rt = response.getFailureMessage();
		}
		return rt;
	}
	
}
