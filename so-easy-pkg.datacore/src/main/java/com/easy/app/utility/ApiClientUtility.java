package com.easy.app.utility;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.http.ProtocolConstant;
import com.easy.http.ProtocolUtil;
import com.easy.http.Request;
import com.easy.http.Response;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.utility.ZipUtil;

public class ApiClientUtility {
	
	public static String putMessageByClient(String Url,String XmlData){
		
		return "";
	}
	
	public static String getMessageByClient(String Url,String XmlData,String searchTable){
		try {
			Map<String, String> postParam = new HashMap<String, String>();
			postParam.put("ZIP_FLAG", "1");
			postParam.put("JSON_DATA", ZipUtil.zip(GetMessageJsonData(XmlData,searchTable)));
			postParam.put("CLIENT_IP", SysUtility.getCurrentHostIPAddress());
//			postParam.put("METHOD_NAME", Url.substring(Url.lastIndexOf("\\/")+1));
			String[] strs = XmlData.split("\\|");
			for (int i = 0; i < strs.length; i++) {
				String[] tmpStrs = strs[i].split("\\=");
				if(tmpStrs[0].equalsIgnoreCase("MESSAGE_TYPE")){
					postParam.put("MESSAGE_TYPE", tmpStrs[1]);
				}
			}
			Request request = new Request(postParam, Url);
			request.setClientIp(postParam.get("CLIENT_IP"));
			request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
			Response response = ProtocolUtil.execute(request);
			if(response.isSuccess() && SysUtility.isNotEmpty(response.getByteResult())){
				return ZipUtil.unZip(new String(response.getByteResult(),"UTF-8"));
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return "";
	}
	
	public static boolean deleteMessageByClient(String Url,String XmlData,String StringResult){
		if(SysUtility.isEmpty(Url) || SysUtility.isEmpty(StringResult) || StringResult.substring(0,3).equals("代码:")){
			return false;
		}
		if(Url.indexOf("GetMessage") >= 0){
			Url = Url.substring(0, Url.indexOf("GetMessage"))+"DeleteMessage";
		}
		try {
			HashMap<String, String> postParam = new HashMap<String, String>();
			postParam.put("ZIP_FLAG", "1");
			postParam.put("JSON_DATA", ZipUtil.zip(DeleteMessageJsonData(StringResult,XmlData)));
			postParam.put("CLIENT_IP", SysUtility.getCurrentHostIPAddress());
			String[] strs = XmlData.split("\\|");
			for (int i = 0; i < strs.length; i++) {
				String[] tmpStrs = strs[i].split("\\=");
				if(tmpStrs[0].equalsIgnoreCase("MESSAGE_TYPE")){
					postParam.put("MESSAGE_TYPE", tmpStrs[1]);
				}
			}
//			postParam.put("METHOD_NAME", "DeleteMessage");
			Request request = new Request(postParam, Url);
			request.setClientIp(postParam.get("CLIENT_IP"));
			request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
			Response response = ProtocolUtil.execute(request);
			if(response.isSuccess() && SysUtility.isNotEmpty(response.getByteResult())){
				return true;
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} 
		return false;
	}
	
	public static String invokeMessageByClient(String Url,String XmlData,String searchTable){
		try {
			Map<String, String> postParam = new HashMap<String, String>();
			postParam.put("ZIP_FLAG", "1");
			postParam.put("JSON_DATA", ZipUtil.zip(InvokeMessageJsonData(XmlData,searchTable)));
			postParam.put("CLIENT_IP", SysUtility.getCurrentHostIPAddress());
//			postParam.put("METHOD_NAME", Url.substring(Url.lastIndexOf("\\/")+1));
			String[] strs = XmlData.split("\\|");
			for (int i = 0; i < strs.length; i++) {
				String[] tmpStrs = strs[i].split("\\=");
				if(tmpStrs[0].equalsIgnoreCase("MESSAGE_TYPE")){
					postParam.put("MESSAGE_TYPE", tmpStrs[1]);
				}else if(tmpStrs[0].equalsIgnoreCase("SERVER_NAME")){
					postParam.put("SERVER_NAME", tmpStrs[1]);
				}
			}
			Request request = new Request(postParam, Url);
			request.setClientIp(postParam.get("CLIENT_IP"));
			request.setDataType(ProtocolConstant.DataType.BYTES.getValue());
			Response response = ProtocolUtil.execute(request);
			if(response.isSuccess() && SysUtility.isNotEmpty(response.getByteResult())){
				return ZipUtil.unZip(new String(response.getByteResult(),"UTF-8"));
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return "";
	}

	public static boolean xmlToLocalByClient(String StringResult,String SourcePath,String XmlData,String Encoding){
		String[] SourcePaths = SourcePath.split(";");
		if(SysUtility.isEmpty(SourcePaths) || SysUtility.isEmpty(StringResult) || StringResult.substring(0,3).equals("代码:")){
			return false;
		}
		try {
			HashMap<String,String> map = ApiClientUtility.getXmlMap(StringResult, Encoding, XmlData);
			String fileName = map.get("FILE_NAME");
			String fileData = map.get("FILE_DATA");
			for (int j = 0; j < SourcePaths.length; j++) {
				String createSourcePath = SourcePaths[j];
				FileUtility.createFile(createSourcePath, fileName, new String(fileData.getBytes("UTF-8"),"UTF-8"));//备份本地文件
			}
			return true;
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return false;
	}
	
	 private static HashMap<String,String> getXmlMap(String StringResult,String Encoding,String XmlData) throws UnsupportedEncodingException, LegendException, JSONException{
		HashMap<String,String> map = new HashMap<String,String>();
		if(SysUtility.isEmpty(XmlData) || SysUtility.isEmpty(StringResult) || StringResult.substring(0,3).equals("代码:")){
			return map;
		}
		JSONArray rtRoot = new JSONArray(StringResult);
		JSONObject row = (JSONObject)rtRoot.get(0);
		JSONArray Root = new JSONArray();
		JSONObject MessageHead = (JSONObject)row.get("MessageHead");
		Root.put(new JSONObject().put("MessageHead", MessageHead));
		Root.put(new JSONObject().put("MessageBody", row.get("MessageBody")));
		JSONObject JsonData = new JSONObject();
		if(SysUtility.isNotEmpty(SysUtility.getJsonField(MessageHead, "ATTRIBUTE1"))){
			JsonData.put(SysUtility.getJsonField(MessageHead, "ATTRIBUTE1"), Root);
			SysUtility.removeJsonField(MessageHead, "ATTRIBUTE1");
		}else{
			JsonData.put("OBORMessage", Root);
		}
		
		Iterator<?> keys = JsonData.keys();
		String rootName = keys.next().toString();
		HashMap ResponseMessage = SysUtility.JSONObjectToHashMap(JsonData, rootName);
		
		String fileData = "<?xml version=\"1.0\" encoding=\""+Encoding+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(rootName,ResponseMessage, 0);
		String fileName = SysUtility.GetUUID()+".xml";
		Datas datas = new Datas();
		datas.MapToDatas("MessageHead", ResponseMessage);
		if("EEntDeclIo".equals(datas.GetTableValue("MessageHead", "MESSAGE_TYPE"))){
			datas.MapToDatas("DCL_RESPONSE", ResponseMessage);
			String techRegCode = datas.GetTableValue("DCL_RESPONSE", "TechRegCode");
			String declNo = datas.GetTableValue("DCL_RESPONSE", "DeclNo");
			String rspCodes = datas.GetTableValue("DCL_RESPONSE", "RspCodes");
			fileName = techRegCode+"_"+declNo+"_"+rspCodes+".xml";
		}else{
			String messageType = datas.GetTableValue("MessageHead", "MESSAGE_TYPE");
			String SerialName = "";
			String[] strs = XmlData.split("\\|");
			for (int k = 0; k < strs.length; k++) {
				if(SysUtility.isEmpty(strs) || strs[k].split("=").length != 2){
					continue;
				}
				if("SERIAL_NAME".equalsIgnoreCase(strs[k].split("=")[0])){
					SerialName = strs[k].split("=")[1];
					break;
				}
			}
			List result = new ArrayList();
			SysUtility.getNameValue(ResponseMessage, SerialName, result);
			if(SysUtility.isNotEmpty(messageType) && SysUtility.isNotEmpty(result) && result.size() > 0){
				fileName = messageType+"_"+result.get(0)+".xml";
			}
		}
		
		map.put("FILE_NAME", fileName);
		map.put("FILE_DATA", new String(fileData.getBytes("UTF-8"),"UTF-8"));
		return map;
	}
	
	private static String GetMessageJsonData(String XmlData,String searchTable) throws JSONException, LegendException{
		if(SysUtility.isNotEmpty(XmlData) && XmlData.startsWith("<") && XmlData.endsWith(">")){
			HashMap RequestMessage = FileUtility.xmlParse(new ByteArrayInputStream(XmlData.getBytes()),"OBORMessage");
			return SysUtility.MapToJSONObject(RequestMessage).toString();
		}else if(SysUtility.isNotEmpty(XmlData)){
			JSONObject MessageHead = new JSONObject();
			String[] strs = XmlData.split("\\|");
			for (int k = 0; k < strs.length; k++) {
				if(SysUtility.isEmpty(strs) || strs[k].split("=").length != 2){
					continue;
				}
				MessageHead.put(strs[k].split("=")[0], strs[k].split("=")[1]);
			}
			String messageTime = SysUtility.getSysDate();
			String messageSource = (String)MessageHead.get("MESSAGE_SOURCE");
			String signData = (String)MessageHead.get("SIGN_DATA");
			if(SysUtility.isNotEmpty(signData)){
				MessageHead.put("MESSAGE_SIGN_DATA", SysUtility.EncryptKeys(messageSource, messageTime,signData));
			}
			MessageHead.put("MESSAGE_ID", SysUtility.GetUUID());
			MessageHead.put("MESSAGE_TIME", messageTime);
			MessageHead.put("MESSAGE_CATEGORY", "21");
			MessageHead.put("MESSAGE_VERSION", "1.0");
			
			if(SysUtility.isNotEmpty(SysUtility.getJsonField(MessageHead, "SERIAL_NAME"))){
				MessageHead.put("DELETE_FLAG", "N");
			}else{
				MessageHead.put("DELETE_FLAG", "Y");
			}
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			if(SysUtility.isNotEmpty(searchTable)){
				String[] searchs = searchTable.split("\\|");
				JSONObject searchHead = new JSONObject();
				for (int k = 0; k < searchs.length; k++) {
					if(SysUtility.isEmpty(searchs) || searchs[k].split("=").length != 2){
						continue;
					}
					searchHead.put(searchs[k].split("=")[0], searchs[k].split("=")[1]);
				}
				JSONArray searchHeads = new JSONArray();
				searchHeads.put(searchHead);
				
				JSONObject row = new JSONObject();
				row.put("SEARCH_TABLE", searchHeads);
				
				Root.put(new JSONObject().put("MessageBody", row));
			}
			JSONObject OBORMessage = new JSONObject();
			OBORMessage.put("OBORMessage", Root);
			return OBORMessage.toString();
		}else{
			return "";
		}
	}
	
	private static String DeleteMessageJsonData(String StringResult,String XmlData) throws JSONException, LegendException{
		if(SysUtility.isNotEmpty(XmlData) && XmlData.startsWith("<") && XmlData.endsWith(">")){
			HashMap RequestMessage = FileUtility.xmlParse(new ByteArrayInputStream(XmlData.getBytes()),"OBORMessage");
			return SysUtility.MapToJSONObject(RequestMessage).toString();
		}else{
			JSONObject MessageHead = new JSONObject();
			String[] strs = XmlData.split("\\|");
			for (int k = 0; k < strs.length; k++) {
				if(SysUtility.isEmpty(strs) || strs[k].split("=").length != 2){
					continue;
				}
				MessageHead.put(strs[k].split("=")[0], strs[k].split("=")[1]);
			}
			String messageTime = SysUtility.getSysDate();
			String messageSource = (String)MessageHead.get("MESSAGE_SOURCE");
			String signData = (String)MessageHead.get("SIGN_DATA");
			String SerialName = (String)MessageHead.get("SERIAL_NAME");
			if(SysUtility.isNotEmpty(signData)){
				MessageHead.put("MESSAGE_SIGN_DATA", SysUtility.EncryptKeys(messageSource, messageTime,signData));
			}
			MessageHead.put("MESSAGE_ID", SysUtility.GetUUID());
			MessageHead.put("MESSAGE_TIME", messageTime);
			MessageHead.put("MESSAGE_CATEGORY", "21");
			MessageHead.put("MESSAGE_VERSION", "1.0");
			
			List result = new ArrayList();
			List rtList = SysUtility.JSONArrayToList(new JSONArray(StringResult));
			SysUtility.getNameValue((HashMap)rtList.get(0), SerialName, result);
			StringBuffer ids = new StringBuffer();
			for (int j = 0; j < result.size(); j++) {
				ids.append(",").append(result.get(j));
			}
			JSONObject MessageBody = new JSONObject();
			JSONArray rows = new JSONArray();
			JSONObject row = new JSONObject();
			row.put(SerialName, ids.substring(1));
			rows.put(row);
			MessageBody.put("SEARCH_TABLE",rows);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			JSONObject OBORMessage = new JSONObject();
			OBORMessage.put("OBORMessage", Root);
			return OBORMessage.toString();
		}
	}
	
	private static String InvokeMessageJsonData(String XmlData,String searchTable) throws JSONException, LegendException{
		JSONObject MessageHead = new JSONObject();
		String[] strs = XmlData.split("\\|");
		for (int k = 0; k < strs.length; k++) {
			if(SysUtility.isEmpty(strs) || strs[k].split("=").length != 2){
				continue;
			}
			MessageHead.put(strs[k].split("=")[0], strs[k].split("=")[1]);
		}
		String messageTime = SysUtility.getSysDate();
		String messageSource = (String)MessageHead.get("MESSAGE_SOURCE");
		String signData = (String)MessageHead.get("SIGN_DATA");
		if(SysUtility.isNotEmpty(signData)){
			MessageHead.put("MESSAGE_SIGN_DATA", SysUtility.EncryptKeys(messageSource, messageTime,signData));
		}
		MessageHead.put("MESSAGE_ID", SysUtility.GetUUID());
		MessageHead.put("MESSAGE_TIME", messageTime);
		MessageHead.put("MESSAGE_CATEGORY", "21");
		MessageHead.put("MESSAGE_VERSION", "1.0");
		
		if(SysUtility.isNotEmpty(SysUtility.getJsonField(MessageHead, "SERIAL_NAME"))){
			MessageHead.put("DELETE_FLAG", "N");
		}else{
			MessageHead.put("DELETE_FLAG", "Y");
		}
		
		JSONArray Root = new JSONArray();
		Root.put(new JSONObject().put("MessageHead", MessageHead));
		if(SysUtility.isNotEmpty(searchTable)){
			String[] searchs = searchTable.split("\\|");
			JSONObject searchHead = new JSONObject();
			for (int k = 0; k < searchs.length; k++) {
				if(SysUtility.isEmpty(searchs) || searchs[k].split("=").length != 2){
					continue;
				}
				searchHead.put(searchs[k].split("=")[0], searchs[k].split("=")[1]);
			}
			JSONArray searchHeads = new JSONArray();
			searchHeads.put(searchHead);
			
			JSONObject row = new JSONObject();
			row.put("SEARCH_TABLE", searchHeads);
			
			Root.put(new JSONObject().put("MessageBody", row));
		}
		JSONObject OBORMessage = new JSONObject();
		OBORMessage.put("OBORMessage", Root);
		return OBORMessage.toString();
	}
}
