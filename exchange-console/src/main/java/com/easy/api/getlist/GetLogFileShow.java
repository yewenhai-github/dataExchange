package com.easy.api.getlist;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Level;
import org.json.JSONException;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.http.HttpUtility;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetLogFileShow")
public class GetLogFileShow extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String MSG_NAME = (String)getEnvDatas().get("MSG_NAME");
		String SOURCE_PATH = (String)getEnvDatas().get("SOURCE_PATH");
		String TARGET_PATH = (String)getEnvDatas().get("TARGET_PATH");
		String ERROR_PATH = (String)getEnvDatas().get("ERROR_PATH");
		String appIp = (String)getEnvDatas().get("RECEIVED_IP_ADDRESS");
		String appPort = (String)getEnvDatas().get("RECEIVED_PORT");
		String appName = (String)getEnvDatas().get("RECEIVED_CONTEXT");
		String appServletName = "GetFileData";
		
		Datas datas = new Datas();
		String sourceData = getFileData(appIp, appPort, appName, appServletName, SOURCE_PATH, MSG_NAME);
		String targetData = getFileData(appIp, appPort, appName, appServletName, TARGET_PATH, MSG_NAME);
		String errorData = getFileData(appIp, appPort, appName, appServletName, ERROR_PATH, MSG_NAME);
		datas.SetTableValue("FileData", "SOURCEDATA",sourceData);
		datas.SetTableValue("FileData", "TARGETDATA",targetData);
		datas.SetTableValue("FileData", "ERRORDATA",errorData);
		ReturnMessage(true,"", "", datas.getDataJSON().getJSONArray("FileData").toString());	
	}
	
	private String getFileData(String appIp,String appPort,String appName,String appServletName,String FILE_PATH,String MSG_NAME) throws JSONException, LegendException{
		String rt = "";
		if(SysUtility.isNotEmpty(FILE_PATH) && SysUtility.isNotEmpty(MSG_NAME)){
			rt = FileUtility.readFile(FILE_PATH+File.separator+MSG_NAME, false);
			try {
				if(SysUtility.isEmpty(rt)){
					Map<String, String> postParam = new HashMap<String, String>();
					postParam.put("FILE_PATH", FILE_PATH);
					postParam.put("FILE_NAME", MSG_NAME);
					rt = HttpUtility.clientInvokeServer(appIp, appPort, appName, appServletName, postParam);
				}
				if(SysUtility.isNotEmpty(rt) && rt.indexOf("Apache Tomcat") >= 0){
					rt = "";
				}
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
			if(SysUtility.isEmpty(rt)){
				rt = "文件不存在或已归档！";
			}
		}
		return rt;
	}
	
	
	
}