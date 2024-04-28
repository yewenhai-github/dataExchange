package com.easy.convert.service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.convert.service.mess.Message_TransferSier;
import com.easy.file.FileFilterHandle;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * 暂时不能同时进行
 * 此功能为报文转换 XML转XML
 * @author chenchuang
 * @email chen_create@163.com
 */
public class XmlToXmlUtilThread {
	
	static JSONObject JSON = new JSONObject();
	static JSONObject json = Message_TransferSier.getQuartzJson();
	final static int FILECOUNT = 500;//单个线程最大处理报文文件数
	/**
	 * 此JSON数据必须包括 ExcelPath节点
	 * @param ConfigPathData
	 * @throws Exception
	 */
	public synchronized static void XmlToXml(JSONObject ConfigPathData,int number) throws Exception{
			try {
				if(SysUtility.isEmpty(ConfigPathData.getString("SourceFilePath"))){
					return;
				}
				File files[] = new File(ConfigPathData.getString("SourceFilePath")).listFiles(new FileFilterHandle());
				Message_TransferSier.setQuartzJson(json.put(Integer.toString(number), false));
				if(SysUtility.isEmpty(files) || files.length<=0) {
					return;
				}
					for (int i = 0; i < files.length; i++) {
						if(i>FILECOUNT) {
							return;
						}
						try {
							if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){
								//只处理XML文件 
								continue;
							}
							Thread.sleep(100);
							long startTime=System.currentTimeMillis();   //获取开始时间
							LogUtil.printLog("开始获取开始时间完成"+startTime, Level.INFO);
							initRoot();
							LogUtil.printLog("初始化Dom节点完成", Level.INFO);
							HashMap<String, JSONArray> excelSheetToMap = Message_TransferSier.getExcelSheetToMap();
							//读取sheet页
							JSONArray excelToJsonArray = null;
				 			if(SysUtility.isEmpty(excelSheetToMap)){
				 				excelSheetToMap = new HashMap<String, JSONArray>();
				 				JSONArray SheetJson = ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigPathData.getString("ExcelPath"), ConfigPathData.getString("ConfigName"));
				 				excelSheetToMap.put(ConfigPathData.getString("ConfigName"),SheetJson);
				 				Message_TransferSier.setExcelSheetToMap(excelSheetToMap);
								excelToJsonArray =excelSheetToMap.get(ConfigPathData.getString("ConfigName"));
							}else {
								excelToJsonArray = excelSheetToMap.get(ConfigPathData.getString("ConfigName"));
								if(SysUtility.isEmpty(excelToJsonArray)) {
					 				excelSheetToMap.put(ConfigPathData.getString("ConfigName"), ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigPathData.getString("ExcelPath"), ConfigPathData.getString("ConfigName")));
					 				Message_TransferSier.setExcelSheetToMap(excelSheetToMap);
									excelToJsonArray =excelSheetToMap.get(ConfigPathData.getString("ConfigName"));
								}
							}
				 			
							String XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
							//存放出现次数
							Map<String, Integer> AppearMap = new LinkedHashMap<String, Integer>();
							//循环行
							for(int j=0;j<excelToJsonArray.length();j++){
								JSONObject HJsonData = (JSONObject) excelToJsonArray.get(j);
								Integer cout = AppearMap.get(HJsonData.getString("SourceNoteName"));
								 if(SysUtility.isEmpty(cout)){
									 AppearMap.put(HJsonData.getString("SourceNoteName"), 1);
								 }else{
									 AppearMap.put(HJsonData.getString("SourceNoteName"), cout+1);
								 }
								 if(SysUtility.isEmpty(HJsonData.getString("Seq")) || "".equals(HJsonData.getString("Seq")) || !((j+1)+"").equals(HJsonData.getString("Seq"))){
									 System.err.println(ConfigPathData.getString("ConfigName")+"Seq序号不准确");
									 return;
								 }
								//重表逻辑
								 if("Y".equals(HJsonData.getString("IsSubList").toUpperCase())){
									 LogUtil.printLog("重表逻辑准备执行", Level.INFO);
									 String Seq = HJsonData.getString("Seq");
									 String SourceNoteName = HJsonData.getString("SourceNoteName");
									 String TargetFileFloor = HJsonData.getString("TargetFileFloor");
									 int DefNumber = FindDef(XMLDATA, SourceNoteName);
									 for(int k=0;k<DefNumber;k++){
										//截取重表XML内容
										 String DefXML = subStringNum(XMLDATA,SourceNoteName,k+1);
										 String SourceName = "";
										 while(SourceName!="!"){
											//创建XML
											 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
											 j++;
											 for(;j<excelToJsonArray.length();j++){
												 HJsonData = (JSONObject) excelToJsonArray.get(j); 
												 /*cout = AppearMap.get(HJsonData.getString("SourceNoteName"));
												 if(SysUtility.isEmpty(cout)){
													 AppearMap.put(HJsonData.getString("SourceNoteName"), 1);
												 }else{
													 AppearMap.put(HJsonData.getString("SourceNoteName"), cout+1);
												 }*/
												 
												 //重重表逻辑
												 if("Y1".equals(HJsonData.getString("IsSubList").toUpperCase())){
													 String YSeq = HJsonData.getString("Seq");
													 String YSourceNoteName = HJsonData.getString("SourceNoteName");
													 String YTargetFileFloor = HJsonData.getString("TargetFileFloor");
													 int YDefNumber = FindDef(DefXML, YSourceNoteName);
													 for(int l=0;l<YDefNumber;l++){
														//截取重表XML内容
														 String YDefXML = subStringNum(DefXML,YSourceNoteName,l+1);
														 String YSourceName = "";
														 while(YSourceName!="!"){
															//创建XML
															 AddXmlElement(HJsonData, excelToJsonArray, Integer.parseInt(YSeq)-1, AppearMap, YDefXML ,XMLDATA);
															 j++;
															 for(;j<excelToJsonArray.length();j++){
																 HJsonData = (JSONObject) excelToJsonArray.get(j); 
																 if(Integer.parseInt(HJsonData.getString("TargetFileFloor"))==Integer.parseInt(YTargetFileFloor)||Integer.parseInt(HJsonData.getString("TargetFileFloor"))<Integer.parseInt(YTargetFileFloor)){
																	 SourceName="!";
																	 break;
																 }
																 if(SysUtility.isEmpty(HJsonData.getString("Seq")) || "".equals(HJsonData.getString("Seq")) || !((j+1)+"").equals(HJsonData.getString("Seq"))){
																	 return;
																 }
																/* cout = AppearMap.get(HJsonData.getString("SourceNoteName"));
																 if(SysUtility.isEmpty(cout)){
																	 AppearMap.put(HJsonData.getString("SourceNoteName"), 1);
																 }else{
																	 AppearMap.put(HJsonData.getString("SourceNoteName"), cout+1);
																 }*/
																//创建XML
																 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, YDefXML ,XMLDATA);
															 }
															 YSourceName="!";
															 if(YDefNumber-1>l){
																 j=Integer.parseInt(YSeq)-1;
																 HJsonData = excelToJsonArray.getJSONObject(j);
															 }
														 }
													 }
												 }
												 if(Integer.parseInt(HJsonData.getString("TargetFileFloor"))==Integer.parseInt(TargetFileFloor)||Integer.parseInt(HJsonData.getString("TargetFileFloor"))<Integer.parseInt(TargetFileFloor)){
													 SourceName="!";
													 if(j+1==DefNumber){
														 AddXmlElement(HJsonData, excelToJsonArray, j+1, AppearMap, DefXML,XMLDATA); 
													 }
													 break;
												 }
												 if(SysUtility.isEmpty(HJsonData.getString("Seq")) || "".equals(HJsonData.getString("Seq")) || !((j+1)+"").equals(HJsonData.getString("Seq"))){
													 return;
												 }
												//创建XML
												 if(SysUtility.isEmpty(XMLDATA)) {
													 XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
												 }
												 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
											 }
											 SourceName="!";
											 if(DefNumber-1>k){
												 j=Integer.parseInt(Seq)-1;
												 HJsonData = excelToJsonArray.getJSONObject(j);
											 }
										 }
									 }
										LogUtil.printLog("重表逻辑执行结束", Level.INFO);
								 }else{
									//创建XML
									 LogUtil.printLog("添加Dom节点", Level.INFO);
									 if(SysUtility.isEmpty(XMLDATA)) {
										 XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
									 }
									 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, XMLDATA,XMLDATA);
									 LogUtil.printLog("添加Dom节点完成", Level.INFO);
								 }
							}			
							Document Doc  = new Document(root);
							XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
							String path =ConfigPathData.getString("TargetFilePath");
							fileHandle(path);
							fileHandle(ConfigPathData.getString("BackPath"));
							XMLOut.output(Doc, new FileOutputStream(path+File.separator+files[i].getName()));  
							FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("BackPath")+File.separator+files[i].getName());
							FileUtility.deleteFile(files[i].getPath());
							long endTime=System.currentTimeMillis();
							LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName()+"运行时长:"+(endTime-startTime)+"毫秒", Level.WARN);
							
						} catch (Exception e) {
							e.printStackTrace();
							fileHandle(ConfigPathData.getString("ErrorPath"));
							FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("ErrorPath")+File.separator+files[i].getName());
							FileUtility.deleteFile(files[i].getPath());
							LogUtil.printLog("文件转换失败："+files[i].getPath()+"请检查"+ConfigPathData.getString("ConfigName")+"Sheet相关配置！", Level.WARN);
						} 
					}
				
				} finally {
					
					Message_TransferSier.setQuartzJson(json.put(Integer.toString(number), true));
				}
	}
	
	
	/**
	 * 此JSON数据必须包括 ExcelPath节点
	 * @param ConfigPathData
	 * @throws Exception
	 */
	public static void XmlToXml(JSONObject ConfigPathData) throws Exception{
			if(SysUtility.isEmpty(ConfigPathData.getString("SourceFilePath"))){
				return;
			}
			File files[] = new File(ConfigPathData.getString("SourceFilePath")).listFiles(new FileFilterHandle());
			if(SysUtility.isEmpty(files)) {
				return;
			}
			try {
				for (int i = 0; i < files.length; i++) {
					try {
						if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){
							//只处理XML文件 
							continue;
						}
						Thread.sleep(100);
						long startTime=System.currentTimeMillis();   //获取开始时间
						LogUtil.printLog("开始获取开始时间完成"+startTime, Level.INFO);
						initRoot();
						LogUtil.printLog("初始化Dom节点完成", Level.INFO);
						HashMap<String, JSONArray> excelSheetToMap = Message_TransferSier.getExcelSheetToMap();
						//读取sheet页
						JSONArray excelToJsonArray = null;
			 			if(SysUtility.isEmpty(excelSheetToMap)){
			 				excelSheetToMap = new HashMap<String, JSONArray>();
			 				JSONArray SheetJson = ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigPathData.getString("ExcelPath"), ConfigPathData.getString("ConfigName"));
			 				excelSheetToMap.put(ConfigPathData.getString("ConfigName"),SheetJson);
			 				Message_TransferSier.setExcelSheetToMap(excelSheetToMap);
							excelToJsonArray =excelSheetToMap.get(ConfigPathData.getString("ConfigName"));
						}else {
							excelToJsonArray = excelSheetToMap.get(ConfigPathData.getString("ConfigName"));
							if(SysUtility.isEmpty(excelToJsonArray)) {
				 				excelSheetToMap.put(ConfigPathData.getString("ConfigName"), ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigPathData.getString("ExcelPath"), ConfigPathData.getString("ConfigName")));
				 				Message_TransferSier.setExcelSheetToMap(excelSheetToMap);
								excelToJsonArray =excelSheetToMap.get(ConfigPathData.getString("ConfigName"));
							}
						}
			 			
						String XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
						//存放出现次数
						Map<String, Integer> AppearMap = new LinkedHashMap<String, Integer>();
						//循环行
						for(int j=0;j<excelToJsonArray.length();j++){
							JSONObject HJsonData = (JSONObject) excelToJsonArray.get(j);
							Integer cout = AppearMap.get(HJsonData.getString("SourceNoteName"));
							 if(SysUtility.isEmpty(cout)){
								 AppearMap.put(HJsonData.getString("SourceNoteName"), 1);
							 }else{
								 AppearMap.put(HJsonData.getString("SourceNoteName"), cout+1);
							 }
							 if(SysUtility.isEmpty(HJsonData.getString("Seq")) || "".equals(HJsonData.getString("Seq")) || !((j+1)+"").equals(HJsonData.getString("Seq"))){
								 System.err.println(ConfigPathData.getString("ConfigName")+"Seq序号不准确");
								 return;
							 }
							//重表逻辑
							 if("Y".equals(HJsonData.getString("IsSubList").toUpperCase())){
								 LogUtil.printLog("重表逻辑准备执行", Level.INFO);
								 String Seq = HJsonData.getString("Seq");
								 String SourceNoteName = HJsonData.getString("SourceNoteName");
								 String TargetFileFloor = HJsonData.getString("TargetFileFloor");
								 int DefNumber = FindDef(XMLDATA, SourceNoteName);
								 for(int k=0;k<DefNumber;k++){
									//截取重表XML内容
									 String DefXML = subStringNum(XMLDATA,SourceNoteName,k+1);
									 String SourceName = "";
									 while(SourceName!="!"){
										//创建XML
										 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
										 j++;
										 for(;j<excelToJsonArray.length();j++){
											 HJsonData = (JSONObject) excelToJsonArray.get(j); 
											 /*cout = AppearMap.get(HJsonData.getString("SourceNoteName"));
											 if(SysUtility.isEmpty(cout)){
												 AppearMap.put(HJsonData.getString("SourceNoteName"), 1);
											 }else{
												 AppearMap.put(HJsonData.getString("SourceNoteName"), cout+1);
											 }*/
											 
											 //重重表逻辑
											 if("Y1".equals(HJsonData.getString("IsSubList").toUpperCase())){
												 String YSeq = HJsonData.getString("Seq");
												 String YSourceNoteName = HJsonData.getString("SourceNoteName");
												 String YTargetFileFloor = HJsonData.getString("TargetFileFloor");
												 int YDefNumber = FindDef(DefXML, YSourceNoteName);
												 for(int l=0;l<YDefNumber;l++){
													//截取重表XML内容
													 String YDefXML = subStringNum(DefXML,YSourceNoteName,l+1);
													 String YSourceName = "";
													 while(YSourceName!="!"){
														//创建XML
														 AddXmlElement(HJsonData, excelToJsonArray, Integer.parseInt(YSeq)-1, AppearMap, YDefXML ,XMLDATA);
														 j++;
														 for(;j<excelToJsonArray.length();j++){
															 HJsonData = (JSONObject) excelToJsonArray.get(j); 
															 if(Integer.parseInt(HJsonData.getString("TargetFileFloor"))==Integer.parseInt(YTargetFileFloor)||Integer.parseInt(HJsonData.getString("TargetFileFloor"))<Integer.parseInt(YTargetFileFloor)){
																 SourceName="!";
																 break;
															 }
															 if(SysUtility.isEmpty(HJsonData.getString("Seq")) || "".equals(HJsonData.getString("Seq")) || !((j+1)+"").equals(HJsonData.getString("Seq"))){
																 return;
															 }
															/* cout = AppearMap.get(HJsonData.getString("SourceNoteName"));
															 if(SysUtility.isEmpty(cout)){
																 AppearMap.put(HJsonData.getString("SourceNoteName"), 1);
															 }else{
																 AppearMap.put(HJsonData.getString("SourceNoteName"), cout+1);
															 }*/
															//创建XML
															 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, YDefXML ,XMLDATA);
														 }
														 YSourceName="!";
														 if(YDefNumber-1>l){
															 j=Integer.parseInt(YSeq)-1;
															 HJsonData = excelToJsonArray.getJSONObject(j);
														 }
													 }
												 }
											 }
											 if(Integer.parseInt(HJsonData.getString("TargetFileFloor"))==Integer.parseInt(TargetFileFloor)||Integer.parseInt(HJsonData.getString("TargetFileFloor"))<Integer.parseInt(TargetFileFloor)){
												 SourceName="!";
												 if(j+1==DefNumber){
													 AddXmlElement(HJsonData, excelToJsonArray, j+1, AppearMap, DefXML,XMLDATA); 
												 }
												 break;
											 }
											 if(SysUtility.isEmpty(HJsonData.getString("Seq")) || "".equals(HJsonData.getString("Seq")) || !((j+1)+"").equals(HJsonData.getString("Seq"))){
												 return;
											 }
											//创建XML
											 if(SysUtility.isEmpty(XMLDATA)) {
												 XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
											 }
											 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
										 }
										 SourceName="!";
										 if(DefNumber-1>k){
											 j=Integer.parseInt(Seq)-1;
											 HJsonData = excelToJsonArray.getJSONObject(j);
										 }
									 }
								 }
									LogUtil.printLog("重表逻辑执行结束", Level.INFO);
							 }else{
								//创建XML
								 LogUtil.printLog("添加Dom节点", Level.INFO);
								 if(SysUtility.isEmpty(XMLDATA)) {
									 XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
								 }
								 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, XMLDATA,XMLDATA);
								 LogUtil.printLog("添加Dom节点完成", Level.INFO);
							 }
						}			
						Document Doc  = new Document(root);
						XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
						String path =ConfigPathData.getString("TargetFilePath");
						fileHandle(path);
						fileHandle(ConfigPathData.getString("BackPath"));
						XMLOut.output(Doc, new FileOutputStream(path+File.separator+files[i].getName()));  
						FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("BackPath")+File.separator+files[i].getName());
						FileUtility.deleteFile(files[i].getPath());
						long endTime=System.currentTimeMillis();
						LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName()+"运行时长:"+(endTime-startTime)+"毫秒", Level.WARN);
						
					} catch (Exception e) {
						e.printStackTrace();
						fileHandle(ConfigPathData.getString("ErrorPath"));
						FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("ErrorPath")+File.separator+files[i].getName());
						FileUtility.deleteFile(files[i].getPath());
						LogUtil.printLog("文件转换失败："+files[i].getPath()+"请检查"+ConfigPathData.getString("ConfigName")+"Sheet相关配置！", Level.WARN);
					} 
				}
			
			} finally {
//				HashMap  Map = Message_TransferSier.getMap();
//				Map.put(number,"1");
				System.gc();
			}
		
	}
		
	//文件处理
	public static void fileHandle(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				   if(file.mkdirs()){
					   LogUtil.printLog("文件夹创建成功："+file.getPath(), Level.INFO);
			   }
			}
		} catch (Exception e) {
		}
	}
	 public static Format FormatXML(){  
	        //格式化生成的xml文件，如果不进行格式化的话，生成的xml文件将会是很长的一行...  
	        Format format = Format.getCompactFormat();  
	        format.setEncoding("utf-8");  
	        format.setIndent("   ");  
	        return format;  
	  }  
	
	public static void AddXmlElement(JSONObject HJsonData,JSONArray excelToJsonArray,int i,Map<String, Integer> AppearMap,String xmlToString,String XMLDATA) throws JSONException{
		JSONObject object = HJsonData;
		if(SysUtility.isEmpty(AppearMap.get(HJsonData.getString("SourceNoteName")))){
			AppearMap.put(HJsonData.getString("SourceNoteName"), 1);
		}
		 //创建XML
		 if("0".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&& SysUtility.isEmpty(root)){
			 root = new Element(HJsonData.getString("TargetColName")); 
		 }else if("1".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 value = isJsonFunctionValue(HJsonData,value,value);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 root.addContent(element1 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("2".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element1.addContent(element2 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("3".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element2.addContent(element3 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("4".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element3.addContent(element4 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("5".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element4.addContent(element5 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("6".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element5.addContent(element6 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("7".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element6.addContent(element7 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("8".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element7.addContent(element8 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("9".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element8.addContent(element9 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("10".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element9.addContent(element0 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("11".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element0.addContent(element11 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("12".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element11.addContent(element12 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("13".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element12.addContent(element13 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("14".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element13.addContent(element14 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("15".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element14.addContent(element15 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("16".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element15.addContent(element16 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("17".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("SourceFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element16.addContent(element17 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("18".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element17.addContent(element18 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("19".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element18.addContent(element19 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("20".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element19.addContent(element20 = new Element(HJsonData.getString("TargetColName")).setText(value));
		 }else if("21".equals(HJsonData.getString("TargetFileFloor"))&&!SysUtility.isEmpty(HJsonData.getString("TargetColName"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SourceNoteName")), HJsonData.getString("SourceNoteName"),object);
			 String NextTargetFileFloor =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTargetFileFloor =  nextjsonObject.getString("TargetFileFloor");
			 }
			 value = init(HJsonData.getString("TargetFileFloor"), NextTargetFileFloor, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element20.addContent(new Element(HJsonData.getString("TargetColName")).setText(value));
		 }
	}
	/**
	 * <x></x> 寻找X节点 的VALUE值
	 * @param str
	 * @param number
	 * @param theStr
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	 public static String FindTheLocation(String str,int number,String theStr,JSONObject jsonObject) throws JSONException{
		 try {
		
		 	LogUtil.printLog("执行查询Value值初始化"+str, Level.INFO);
	    	if(SysUtility.isNotEmpty(jsonObject)&&!SysUtility.isEmpty(jsonObject.getString("DefValue"))&&!"".equals(jsonObject.getString("DefValue"))){
	    		return jsonObject.getString("DefValue");
	    	}
	    	int indexOf = 0;
	    	theStr = "<"+theStr+">";
	    	for (int i = 0; i < number; i++) {
	    		indexOf= str.indexOf(theStr,indexOf+1);
			}
	    	int indexOf2 = str.indexOf(">",indexOf+1);
			int indexOf3 = str.indexOf("<",indexOf2);
			String returnStr = str.substring(indexOf2+1,indexOf3);
			LogUtil.printLog("执行查询Value值完成", Level.INFO);
	    	return returnStr;
		 }catch (Exception e) {
			 return "";
		 }
	  }
	 
	 /**
		 * (X) 寻找X的VALUE值
		 * @param str
		 */
		 public static String FindValue(String str) {
		    	int indexOf = 1;
		    	int indexOf2 = str.indexOf("(",indexOf+1);
				int indexOf3 = str.indexOf(")",indexOf2);
				String returnStr = str.substring(indexOf2+1,indexOf3);
		    	return returnStr;
		  }
	
	 
	 public static String init(String TargetFileFloor,String NextTargetFileFloor,String value){
		  try {
			  if(Integer.parseInt(NextTargetFileFloor)>Integer.parseInt(TargetFileFloor)){
				  return null;
			  }else{
				  return value;
			  }
		  } catch (Exception e) {
			  return value;
		}
	  }
	 
	 /**
		 * @param xml
		 * @param theStr
		 * @return 用来查询从表的次数
		 */
		 public static int FindDef(String xml,String theStr){
				String findstr = "<"+theStr+">";
				int indexOf = 0;
				int number = 0;
				while (indexOf!=-1) {
					indexOf = xml.indexOf(findstr,indexOf+1);
					if(indexOf!=-1){
						number++;
					}
				}
				return number;
		 }
		 
		 public static String isJsonFunctionValue(JSONObject HJsonData,String value ,String XMLDATA) throws JSONException {
			 try {
				 if(SysUtility.isNotEmpty(HJsonData.getString("Function"))) {
					 LogUtil.printLog("执行Function函数", Level.INFO);
					 if(SysUtility.isNotEmpty(value)) {
						 value = value.replaceAll(" ","");
						 value = value.replaceAll("\r","");
						 value = value.replaceAll("\n","");	 
					 }
					
					 String[] FunctionSplit = HJsonData.getString("Function").split(";");
					 if(FunctionSplit.length>0) {
						 if(FunctionSplit[0].toUpperCase().equals("GETUUID()")) {
							 value = SysUtility.GetUUID();
						 }else if(FunctionSplit[0].toUpperCase().equals("GETTIME()")) {
							 value = SysUtility.getSysDate();
						 }else if(FunctionSplit[0].toUpperCase().equals("SETUUID()")) {
							 String uuid = SysUtility.GetUUID();
							 JSON.put(HJsonData.getString("TargetColName"), uuid);
							 value=uuid;
						 }else if(FunctionSplit[0].toUpperCase().substring(0,3).equals("GET")) {
							 value = FindTheLocation(XMLDATA, 1, FunctionSplit[0].substring(3,FunctionSplit[0].toUpperCase().indexOf("(")), null);
						 }else if(FunctionSplit[0].toUpperCase().split("\\(")[0].equals("ISNULL")){
							 if(SysUtility.isEmpty(value) || value.equals("\r\n") || value.equals("\n") ) {
								 String str = FindValue(FunctionSplit[0]);
								 if(SysUtility.isNotEmpty(str)) {
									 String[] StrSplit = str.split(",");
									 for(int i =0;i<StrSplit.length;i++) {
										 value = FindTheLocation(XMLDATA, 1, StrSplit[i].replaceAll(" ", ""), null);
										 if(SysUtility.isNotEmpty(value)) {
											 break;
										 }
									 }
									 
								 } 
							 }
						 }else{
							 try {
								 value = JSON.getString(FunctionSplit[0].substring(0,FunctionSplit[0].toUpperCase().indexOf("(")));
							 } catch (Exception e) {
								 LogUtil.printLog(HJsonData.getString("Seq")+"函数执行失败", Level.ERROR);
								 return value;
							 }
						 }
					 }
				 }
				 
				 if(SysUtility.isNotEmpty(HJsonData.getString("MapPing"))){
					 LogUtil.printLog("执行Mapping映射", Level.INFO);
					 if(isGoodJson(HJsonData.getString("MapPing"))) {
						 JSONObject MapPingJson = new JSONObject(HJsonData.getString("MapPing"));
						 int v=0;
						 try {
							 v = Integer.parseInt(value);
							 value = v+"";
						 } catch (Exception e) {
						 }
						 value = MapPingJson.getString(value);
					 }
				 }
			 }catch (Exception e) {
				 return value;
			 }
			 
			 return value;
		 }
		 
		 
		 /**
		  * 
		  * @return 得到重表内容规定次数的内容
		  */
		 public static String subStringNum(String xml,String SourceNoteName,int number){
			 int indexOf = 0;
			 int indexOutOf = 0;
			 String xmlBody="";
			 for(int i=0;i<number;i++){
				 indexOf = xml.indexOf("<"+SourceNoteName+">",indexOf+1);
				 indexOutOf = xml.indexOf("</"+SourceNoteName+">",indexOf);
				 xmlBody = xml.substring(indexOf,indexOutOf+(3+SourceNoteName.length()));
			 }
			 return xmlBody;
		 }
		 
		 public static boolean isGoodJson(String json) {    
		      
			   try {    
			       new JSONObject(json);
			       return true;    
			   } catch (Exception e) {    
			       return false;    
			   }    
			}
		 
	 public static void initRoot(){
    	 root = null;
		 element1 = null;
		 element2 = null;
		 element3 = null;
		 element4 = null;
		 element5 = null;
		 element6 = null;
		 element7 = null;
		 element8 = null;
		 element9 = null;
		 element0 = null;
		 element11 = null;
		 element12 = null;
		 element13 = null;
		 element14 = null;
		 element15 = null;
		 element16 = null;
		 element17 = null;
		 element18 = null;
		 element19 = null;
		 element20 = null;
    }
    
    
    private static Element root = null;
	private static Element element1 = null;
	private static Element element2 = null;
	private static Element element3 = null;
	private static Element element4 = null;
	private static Element element5 = null;
	private static Element element6 = null;
	private static Element element7 = null;
	private static Element element8 = null;
	private static Element element9 = null;
	private static Element element0 = null;
	private static Element element11 = null;
	private static Element element12 = null;
	private static Element element13 = null;
	private static Element element14 = null;
	private static Element element15 = null;
	private static Element element16 = null;
	private static Element element17 = null;
	private static Element element18 = null;
	private static Element element19 = null;
	private static Element element20 = null;
}
