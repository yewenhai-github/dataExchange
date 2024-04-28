package com.easy.convert.service.test;
//package com.createlegend.service.test;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.junit.Test;
//
//import com.createlegend.service.util.ExcelToJsonArrayUtil;
//import com.createlegend.service.util.ExcelToXmlUtil;
//import com.createlegend.service.util.XmlToXmlUtil;
//
//import createlegend.utility.SysUtility;
//
///**
// * 测试类
// * @author chenchuang
// *
// */
//public class MessTestMain {
//	
//
//	
//	@Test
//	public void ExcelToJsonArrayTest() throws Exception{
//		JSONArray excelToJsonArray = ExcelToJsonArrayUtil.ExcelToJsonArray("C:\\Users\\Administrator\\Desktop\\Config.xls","ConfigPath");
//		if(SysUtility.isEmpty(excelToJsonArray)){
//			System.err.println("读取配置文件出现错误 请检查问题");
//			return;
//		}else{
//			for(int i=0;i<excelToJsonArray.length();i++){
//				JSONObject ConfigPathData = (JSONObject) excelToJsonArray.get(i);
//				//判断源文件为Excel还是XML
//				if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("1")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("1")){
//					//按照XML格式读取 生成XML
//					XmlToXmlUtil.XmlToXml(ConfigPathData);
//				}else if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("2")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("1")){
//					//按照excel格式读取 生成XML
//					ExcelToXmlUtil.ExcelToXml(ConfigPathData);
//				}else if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("1")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("2")){
//					//按照XML格式读取 生成Excel
////					XmlToXmlUtil.XmlToExcel(ConfigPathData);
//				}else if(!SysUtility.isEmpty(ConfigPathData.get("SourceFileType"))&&ConfigPathData.get("SourceFileType").toString().toUpperCase().equals("2")&&ConfigPathData.get("TargetFileType").toString().toUpperCase().equals("2")){
//					//按照excel格式读取 生成Excel
////					ExcelToXmlUtil.ExcelToExcel(ConfigPathData);
//				}
//				
//			}
//		}
//	}
//}
