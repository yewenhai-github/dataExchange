package com.easy.api.client;
import java.util.Hashtable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.utility.ZipUtil;
import com.easy.web.MainServlet;

@WebServlet(name = "OpenApiDispatcherRunTest", value = {"/TestPutMessage","/TestGetMessage","/TestDeleteMessage"})
public class OpenApiDispatcherRunTest extends MainServlet {/*
	private static final long serialVersionUID = 1L;

	public OpenApiDispatcherRunTest() {
		super();
		SetCheckLogin(false);
	}

	public void DoCommand() throws Exception { 
		HttpServletResponse Response = getResponse();
		HttpServletRequest Request = getRequest();
		String MethodName=Request.getRequestURI().substring(Request.getRequestURI().lastIndexOf("/")+1, Request.getRequestURI().length());
		String rt="";
		if("TestPutMessage".equals(MethodName)){
			rt = TestPutMessage();
	    }else if("TestGetMessage".equals(MethodName)){
			rt = TestGetMessage();
	    }else if("TestDeleteMessage".equals(MethodName)){
			rt = TestDeleteMessage();
	    }		
		Response.setHeader("Content-type", "text/html;charset=UTF-8");
        ReturnMessage(true, "", "", "{\"ReturnMessage\":\"" + rt.replace("\"", "\\\"") + "\"}");
	}

	private String TestPutMessage() throws Exception{
		Hashtable<String, Object> EnvDatasStr = getEnvDatas();  
	 	String json =EnvDatasStr.get("serviceJson").toString();  
		JSONArray rows = new JSONArray(json);
		JSONObject row = (JSONObject)rows.get(0); 
		
		Hashtable<String, Object> EnvDatas =new Hashtable<String, Object>(); 
		if(row.has("CLIENT_IP")){
			EnvDatas.put("CLIENT_IP",row.get("CLIENT_IP").toString());
		}
		if(row.has("MESSAGE_TYPE")){
			EnvDatas.put("MESSAGE_TYPE", row.get("MESSAGE_TYPE").toString());
		}
		if(row.has("ZIP_FLAG")){
			EnvDatas.put("ZIP_FLAG",row.get("ZIP_FLAG").toString());
		}
		String data = "";
		if(row.has("JSON_DATA")&&SysUtility.isNotEmpty(row.get("JSON_DATA").toString())){
			data = row.get("JSON_DATA").toString();
			String tempStr = "1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.zip(data):data;			
			EnvDatas.put("JSON_DATA",tempStr);
		}else if(row.has("XML_DATA")&&SysUtility.isNotEmpty(row.get("XML_DATA").toString())){	
			data = row.get("XML_DATA").toString();
			String tempStr = "1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.zip(data):data;			
			EnvDatas.put("XML_DATA",tempStr);
		} 
    	if(row.has("FILE_NAME")){
    		EnvDatas.put("FILE_NAME", row.get("FILE_NAME"));
    	}
    	EnvDatas.put("METHOD_NAME", "PutMessage");        		
		EnvDatas.put("ROOT_NAME", "RequestMessage");
		
		DispatcherManager manager = new DispatcherManager();
		String rt = manager.execute(EnvDatas);
		rt="1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.unZip(rt):rt;	
		return rt;
	} 
	
	private String TestGetMessage() throws Exception{

		Hashtable<String, Object> EnvDatasStr = getEnvDatas();  
	 	String json =EnvDatasStr.get("serviceJson").toString();  
		JSONArray rows = new JSONArray(json);
		JSONObject row = (JSONObject)rows.get(0); 
		
		Hashtable<String, Object> EnvDatas =new Hashtable<String, Object>(); 
		if(row.has("CLIENT_IP")){
			EnvDatas.put("CLIENT_IP",row.get("CLIENT_IP").toString());
		}
		if(row.has("MESSAGE_TYPE")){
			EnvDatas.put("MESSAGE_TYPE", row.get("MESSAGE_TYPE").toString());
		}
		if(row.has("ZIP_FLAG")){
			EnvDatas.put("ZIP_FLAG",row.get("ZIP_FLAG").toString());
		}
		String data = "";
		if(row.has("JSON_DATA")&&SysUtility.isNotEmpty(row.get("JSON_DATA").toString())){
			data = row.get("JSON_DATA").toString();
			String tempStr = "1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.zip(data):data;			
			EnvDatas.put("JSON_DATA",tempStr);
		}else if(row.has("XML_DATA")&&SysUtility.isNotEmpty(row.get("XML_DATA").toString())){	
			data = row.get("XML_DATA").toString();
			String tempStr = "1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.zip(data):data;			
			EnvDatas.put("XML_DATA",tempStr);
		} 
    	EnvDatas.put("METHOD_NAME", "GetMessage");
		EnvDatas.put("ROOT_NAME", "RequestMessage");
		
		DispatcherManager manager = new DispatcherManager();
		String rt = manager.execute(EnvDatas);
		rt="1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.unZip(rt):rt;	
		return rt;
	} 

	private String TestDeleteMessage() throws Exception{

		Hashtable<String, Object> EnvDatasStr = getEnvDatas();  
	 	String json =EnvDatasStr.get("serviceJson").toString();  
		JSONArray rows = new JSONArray(json);
		JSONObject row = (JSONObject)rows.get(0); 
		
		Hashtable<String, Object> EnvDatas =new Hashtable<String, Object>(); 
		if(row.has("CLIENT_IP")){
			EnvDatas.put("CLIENT_IP",row.get("CLIENT_IP").toString());
		}
		if(row.has("MESSAGE_TYPE")){
			EnvDatas.put("MESSAGE_TYPE", row.get("MESSAGE_TYPE").toString());
		}
		if(row.has("ZIP_FLAG")){
			EnvDatas.put("ZIP_FLAG",row.get("ZIP_FLAG").toString());
		}
		String data = "";
		if(row.has("JSON_DATA")&&SysUtility.isNotEmpty(row.get("JSON_DATA").toString())){
			data = row.get("JSON_DATA").toString();
			String tempStr = "1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.zip(data):data;			
			EnvDatas.put("JSON_DATA",tempStr);
		}else if(row.has("XML_DATA")&&SysUtility.isNotEmpty(row.get("XML_DATA").toString())){	
			data = row.get("XML_DATA").toString();
			String tempStr = "1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.zip(data):data;			
			EnvDatas.put("XML_DATA",tempStr);
		} 
    	EnvDatas.put("METHOD_NAME", "DeleteMessage");		
		EnvDatas.put("ROOT_NAME", "RequestMessage");
		
		DispatcherManager manager = new DispatcherManager();
		String rt = manager.execute(EnvDatas);
		rt="1".equals(row.get("ZIP_FLAG").toString())?ZipUtil.unZip(rt):rt;	
		return rt;
	}
	
*/}
