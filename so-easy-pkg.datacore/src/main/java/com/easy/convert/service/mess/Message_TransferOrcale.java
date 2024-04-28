package com.easy.convert.service.mess;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.service.util.ExcelToXmlUtil;
import com.easy.convert.service.util.MessageUtil;
import com.easy.convert.service.util.XmlToXmlUtil;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


public class Message_TransferOrcale extends  MainServlet implements IGlobalService{
	
	static boolean quartzFlat = true;
	private static final long serialVersionUID = 6288128270284406321L;
	
	public Message_TransferOrcale(String param) {
		super();
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception {
		List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_path WHERE nvl(ISENABLED,'1') = '1' AND FUNCTIONTYPE!=2 ORDER BY ORG_ID");
		if(query4List.size()>0) {
				JSONArray jsonArrayByList = MessageUtil.getJSONArrayByList(query4List);
				JSONArray excelToJsonArray = jsonArrayByList;
				if(SysUtility.isEmpty(excelToJsonArray)) {
					LogUtil.printLog("配置信息不存在", Level.ERROR);
					return;
				}
				for(int i=0;i<excelToJsonArray.length();i++) {
					long startTime=System.currentTimeMillis();   //获取开始时间
					JSONObject ConfigPathData = new JSONObject((HashMap)excelToJsonArray.get(i));
					//是否启用
					if(ConfigPathData.isNull("ISENABLED") || ConfigPathData.getString("ISENABLED").equals("0")) {
						LogUtil.printLog(ConfigPathData.getString("CONFIGNAME")+" 配置未启用", Level.ERROR);
						excelToJsonArray.remove(i);
						Message_TransferSier.setExcelToJsonArray(excelToJsonArray);
						i--;
						continue;
					}
					if(ConfigPathData.isNull("SOURCEFILETYPE") || ConfigPathData.isNull("TARGETFILETYPE")) {
						continue;
					}
					if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("1")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
						//按照XML格式读取 生成XML
						XmlToXmlUtil.XmlToXml2(ConfigPathData,getDataAccess());
					}else if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("2")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
						//按照excel格式读取 生成XML
						ExcelToXmlUtil.ExcelToXmlTo(ConfigPathData,getDataAccess());
						
//						ExcelToXmlUtil.ExcelToXml(ConfigPathData,getDataAccess());
					}else if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("1")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("2")){
						//按照XML格式读取 生成Excel
//						XmlToXmlUtil.XmlToExcel(ConfigPathData);
					}else if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("2")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("2")){
						//按照excel格式读取 生成Excel
//						ExcelToXmlUtil.ExcelToExcel(ConfigPathData);
					}else if(!SysUtility.isEmpty(ConfigPathData.get("SOURCEFILETYPE"))&&ConfigPathData.get("SOURCEFILETYPE").toString().toUpperCase().equals("3")&&ConfigPathData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
						//按照XML格式读取 生成XML
						//XmlToXmlUtil.XmlToXmlOracle(ConfigPathData,getDataAccess());
					}
					long endTime=System.currentTimeMillis();   //获取结束时间
					LogUtil.printLog("扫描完毕：运行时长:"+(endTime-startTime)+"毫秒", Level.INFO);
				}
				LogUtil.printLog("方法结束()", Level.INFO);
		}else {
			return;
		}
		
	}
	
}
