package com.easy.api.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.http.ProtocolConstant;
import com.easy.http.ProtocolUtil;
import com.easy.http.Request;
import com.easy.http.Response;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class DispatcherCacheUtility {
	private static Map<String,Map> apiToServerMap = new HashMap();
	private static Map<String,Map> accessCustomerMap = new HashMap();
	private static Map<String,List> accessLimitMap = new HashMap();
	
	public static Map getApiToServerMap(String serverUrl,String ServerName,String MethodName,String MessageType){
		SysUtility.OutDate5MinuteReset(apiToServerMap);
		Map map = new HashMap();
		if(SysUtility.isEmpty(serverUrl)){
			return map;
		}
		String key = ServerName+MethodName+MessageType;
		if(SysUtility.isNotEmpty(apiToServerMap.get(key))){
			map = (HashMap)apiToServerMap.get(key);
			return map;
		}
		
		try {
			String tempServerUrl = serverUrl.substring(0, serverUrl.indexOf("OpenApiMainRun")) + "GetApiToServer";
			
			Map<String, String> postParam = new HashMap<String, String>();
			postParam.put("SERVER_NAME", ServerName);
			postParam.put("METHOD_NAME", MethodName);
			postParam.put("MESSAGE_TYPE", MessageType);
			Request request = new Request(postParam, tempServerUrl);
			request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
			Response response = ProtocolUtil.execute(request);
			if (response.isSuccess()) {
				String rt = new String(response.getByteResult(),"UTF-8");
				if(SysUtility.isNotEmpty(rt) && rt.startsWith("{")){
					JSONObject json = new JSONObject(rt);
					map = SysUtility.JSONObjectToHashMap(json);
					apiToServerMap.put(key, map);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		
		return map;
	}
	
	public static Map getCustomerMap(String serverUrl,String messageType,String messageSource,String techRegCode){
		SysUtility.OutDate5MinuteReset(accessCustomerMap);
		Map map = new HashMap();
		if(SysUtility.isEmpty(serverUrl)){
			return map;
		}
		String key = messageType+messageSource+techRegCode;
		if(SysUtility.isNotEmpty(accessCustomerMap.get(key))){
			map = (HashMap)accessCustomerMap.get(key);
			return map;
		}
		try {
			String tempServerUrl = serverUrl.substring(0, serverUrl.indexOf("OpenApiMainRun")) + "GetAccessCustomer";
			
			Map<String, String> postParam = new HashMap<String, String>();
			postParam.put("MESSAGE_TYPE", messageType);
			postParam.put("MESSAGE_SOURCE", messageSource);
			postParam.put("TECH_REG_CODE", techRegCode);
			Request request = new Request(postParam, tempServerUrl);
			request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
			Response response = ProtocolUtil.execute(request);
			if (response.isSuccess()) {
				String rt = new String(response.getByteResult(),"UTF-8");
				if(SysUtility.isNotEmpty(rt) && rt.startsWith("{")){
					JSONObject json = new JSONObject(rt);
					map = SysUtility.JSONObjectToHashMap(json);
					accessCustomerMap.put(key, map);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return map;
	}
	
	public static List getAccessLimitList(String serverUrl,String clientIp){
		SysUtility.OutDate5MinuteReset(accessLimitMap);
		List list = new ArrayList();
		if(SysUtility.isEmpty(serverUrl)){
			return list;
		}
		String key = clientIp;
		if(SysUtility.isNotEmpty(accessLimitMap.get(key))){
			list = (List)accessLimitMap.get(key);
			return list;
		}
		try {
			String tempServerUrl = serverUrl.substring(0, serverUrl.indexOf("OpenApiMainRun")) + "GetAccessLimit";
			
			Map<String, String> postParam = new HashMap<String, String>();
			postParam.put("CLIENT_IP", clientIp);
			Request request = new Request(postParam, tempServerUrl);
			request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
			Response response = ProtocolUtil.execute(request);
			if (response.isSuccess()) {
				String rt = new String(response.getByteResult(),"UTF-8");
				if(SysUtility.isNotEmpty(rt) && rt.startsWith("[{")){
					JSONArray array = new JSONArray(rt);
					list = SysUtility.JSONArrayToList(array);
					accessLimitMap.put(key, list);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		
		return list;
	}
}
