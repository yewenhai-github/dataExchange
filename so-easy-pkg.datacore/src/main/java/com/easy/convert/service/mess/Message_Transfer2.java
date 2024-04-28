package com.easy.convert.service.mess;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.service.util.ExcelToJsonArrayUtil;
import com.easy.convert.service.util.ExcelToXmlUtil;
import com.easy.convert.service.util.XmlToXmlUtil;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


public class Message_Transfer2 extends MainServlet implements IGlobalService{
	
	static boolean quartzFlat = true;
	private static final long serialVersionUID = 6288128270284406321L;
	public Message_Transfer2(String param) {
		super();
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception {
		LogUtil.printLog("进入本地配置版", Level.WARN);
		while(true) {
			JSONArray excelToJsonArray = Message_TransferSier.getExcelToJsonArray();
			if(SysUtility.isEmpty(excelToJsonArray)) {
				try {
				Message_TransferSier.setExcelToJsonArray(ExcelToJsonArrayUtil.ExcelToJsonArray(GetSetting("System", "ConfigPath"),"ConfigPath"));
				} catch (Exception e) {
					LogUtil.printLog("配置文件不存在", Level.WARN);
				}
				excelToJsonArray = Message_TransferSier.getExcelToJsonArray();
			}
			for(int i=0;i<excelToJsonArray.length();i++) {
				long startTime=System.currentTimeMillis();   //获取开始时间
				JSONObject ConfigPathData = null;
				if(excelToJsonArray.get(i) instanceof Map) {
					ConfigPathData = new JSONObject(excelToJsonArray.get(i).toString());
				}else if(excelToJsonArray.get(i) instanceof JSONObject) {
					ConfigPathData =(JSONObject) excelToJsonArray.get(i);
				}
			
				//是否启用
				if(!ConfigPathData.isNull("Isenabled")) {
					if(SysUtility.isEmpty(ConfigPathData.getString("Isenabled")) || ConfigPathData.getString("Isenabled").equals("0")) {
						LogUtil.printLog(ConfigPathData.getString("CONFIGNAME")+" 配置未启用", Level.WARN);
						excelToJsonArray.remove(i);
						Message_TransferSier.setExcelToJsonArray(excelToJsonArray);
						i--;
						continue;
					}
				}else if(!ConfigPathData.isNull("ISENABLED")) {
					if(SysUtility.isEmpty(ConfigPathData.getString("ISENABLED")) || ConfigPathData.getString("ISENABLED").equals("0")) {
						LogUtil.printLog(ConfigPathData.getString("CONFIGNAME")+" 配置未启用", Level.WARN);
						excelToJsonArray.remove(i);
						Message_TransferSier.setExcelToJsonArray(excelToJsonArray);
						i--;
						continue;
					}
				}
				
				if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("1")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
					//按照XML格式读取 生成XML
					XmlToXmlUtil.XmlToXml(ConfigPathData,i);
				}else if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("2")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
					//按照excel格式读取 生成XML
					ExcelToXmlUtil.ExcelToXml(ConfigPathData);
				}else if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("1")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("2")){
					//按照XML格式读取 生成Excel
//					XmlToXmlUtil.XmlToExcel(ConfigPathData);
				}else if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("2")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("2")){
					//按照excel格式读取 生成Excel
//					ExcelToXmlUtil.ExcelToExcel(ConfigPathData);
				}
				long endTime=System.currentTimeMillis();   //获取结束时间
				LogUtil.printLog("扫描完毕：运行时长:"+(endTime-startTime)+"毫秒", Level.INFO);
			}
			LogUtil.printLog("方法结束()", Level.INFO);
		}
	}
	
}
