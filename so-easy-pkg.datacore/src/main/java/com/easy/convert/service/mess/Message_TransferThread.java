package com.easy.convert.service.mess;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.convert.service.util.ExcelToJsonArrayUtil;
import com.easy.convert.service.util.ExcelToXmlUtil;
import com.easy.convert.service.util.XmlToXmlUtilThread;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


/**
 * 
 * 线程转换线程版
 * @author chenchuang
 *
 */
public class Message_TransferThread implements Runnable{
	Message_TransferThread(){
		
	}

	@Override
	public void run() {
		try {
			JSONObject json = Message_TransferSier.getQuartzJson();
			JSONArray excelToJsonArray = Message_TransferSier.getExcelToJsonArray();
			if(SysUtility.isEmpty(excelToJsonArray)) {
				try {
					Message_TransferSier.setExcelToJsonArray(excelToJsonArray = ExcelToJsonArrayUtil.ExcelToJsonArray(SysUtility.GetSetting("System", "ConfigPath"),"ConfigPath"));
				} catch (Exception e) {
					LogUtil.printLog("配置文件不存在", Level.ERROR);
				}
			}
			
			long startTime=System.currentTimeMillis();   //获取开始时间
			int number = (int)(Math.random() * excelToJsonArray.length());
			if(SysUtility.isEmpty(json)) {
				Message_TransferSier.setQuartzJson(new JSONObject().put(Integer.toString(number), true));
				json = Message_TransferSier.getQuartzJson();
			}else {
				Message_TransferSier.setQuartzJson(json.put(Integer.toString(number), true));
			}
			
			
			JSONObject ConfigPathData = (JSONObject) excelToJsonArray.get( number );
			//是否启用
			synchronized (this) {
				if(SysUtility.isEmpty(ConfigPathData.getString("Isenabled")) || ConfigPathData.getString("Isenabled").equals("0")) {
					LogUtil.printLog(ConfigPathData.getString("ConfigName")+" 配置未启用", Level.ERROR);
					excelToJsonArray.remove(number);
					Message_TransferSier.setExcelToJsonArray(excelToJsonArray);
					return;
				}
			}
			
			if(json.getBoolean(Integer.toString(number))) {
				if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("1")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("1")){
					//按照XML格式读取 生成XML
					Message_TransferSier.setQuartzJson(json.put(Integer.toString(number), false));
					XmlToXmlUtilThread.XmlToXml(ConfigPathData,number);
				}else if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("2")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("1")){
					//按照excel格式读取 生成XML
					Message_TransferSier.setQuartzJson(json.put(Integer.toString(number), false));
					ExcelToXmlUtil.ExcelToXml(ConfigPathData);
				}else if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("1")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("2")){
					//按照XML格式读取 生成Excel
//					XmlToXmlUtil.XmlToExcel(ConfigPathData);
				}else if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("2")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("2")){
					//按照excel格式读取 生成Excel
//					ExcelToXmlUtil.ExcelToExcel(ConfigPathData);
				}
			}	
			long endTime=System.currentTimeMillis();   //获取结束时间
			LogUtil.printLog("扫描完毕：运行时长:"+(endTime-startTime)+"毫秒", Level.INFO);
			LogUtil.printLog("方法结束()", Level.INFO);
		} catch (Exception e) {
			LogUtil.printLog("方法异常 线程结束", Level.ERROR);
		}
	}

	
}
