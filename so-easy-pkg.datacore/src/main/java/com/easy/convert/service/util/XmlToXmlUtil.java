package com.easy.convert.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.convert.service.mess.Message_TransferSier;
import com.easy.file.FileFilterHandle;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * 此功能为报文转换 XML转XML
 * @author chenchuang
 * @email chen_create@163.com
 */
public class XmlToXmlUtil{
	
	static JSONObject JSON = new JSONObject();
	private final static Integer FILECOUNT = 10000;//单个线程最大处理报文数

	/**
	 * 此JSON数据必须包括 ExcelPath节点  本地版本
	 * @param ConfigPathData
	 * @throws Exception
	 */
	public static void XmlToXml(JSONObject ConfigPathData,int number) throws Exception{
		try {
			if(SysUtility.isEmpty(ConfigPathData.getString("SOURCEFILEPATH"))){
				return;
			}
			File files[] = new File(ConfigPathData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
			
			if(SysUtility.isEmpty(files) || files.length<=0) {
				return;
			}
				for (int i = 0; i < files.length; i++) {
					try {
						if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){
							//只处理XML文件 
							continue;
						}
						if(i > FILECOUNT) {//预防内存爆炸 处理单次处理FILECOUNT 内存要求：4G以上
							return;
						}
						long startTime=System.currentTimeMillis();   //获取开始时间
						LogUtil.printLog("开始获取开始时间完成"+startTime, Level.INFO);
						initRoot();
						LogUtil.printLog("初始化Dom节点完成", Level.INFO);
						HashMap<String, JSONArray> excelSheetToMap = Message_TransferSier.getExcelSheetToMap();
						//读取sheet页
						JSONArray excelToJsonArray = null;
			 			if(SysUtility.isEmpty(excelSheetToMap)){
			 				excelSheetToMap = new HashMap<String, JSONArray>();
			 				JSONArray SheetJson = ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigPathData.getString("EXCELPATH"), ConfigPathData.getString("CONFIGNAME"));
			 				excelSheetToMap.put(ConfigPathData.getString("CONFIGNAME"),SheetJson);
			 				Message_TransferSier.setExcelSheetToMap(excelSheetToMap);
							excelToJsonArray =excelSheetToMap.get(ConfigPathData.getString("CONFIGNAME"));
						}else {
							excelToJsonArray = excelSheetToMap.get(ConfigPathData.getString("CONFIGNAME"));
							if(SysUtility.isEmpty(excelToJsonArray)) {
				 				excelSheetToMap.put(ConfigPathData.getString("CONFIGNAME"), ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigPathData.getString("EXCELPATH"), ConfigPathData.getString("CONFIGNAME")));
				 				Message_TransferSier.setExcelSheetToMap(excelSheetToMap);
								excelToJsonArray =excelSheetToMap.get(ConfigPathData.getString("CONFIGNAME"));
							}
						}
			 			
						String XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
						//存放出现次数
						Map<String, Integer> AppearMap = new LinkedHashMap<String, Integer>();
						//循环行
						for(int j=0;j<excelToJsonArray.length();j++){
							JSONObject HJsonData = (JSONObject) excelToJsonArray.get(j);
							Integer cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
							 if(SysUtility.isEmpty(cout)){
								 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
							 }else{
								 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
							 }
							 if(SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
								 System.err.println(ConfigPathData.getString("CONFIGNAME")+"Seq序号不准确");
								 return;
							 }
							//重表逻辑
							 if("Y".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
								 LogUtil.printLog("重表逻辑准备执行", Level.INFO);
								 String Seq = HJsonData.getString("SEQ");
								 String SOURCENOTENAME = HJsonData.getString("SOURCENOTENAME");
								 String TARGETFILEFLOOR = HJsonData.getString("TARGETFILEFLOOR");
								 int DefNumber = FindDef(XMLDATA, SOURCENOTENAME);
								 for(int k=0;k<DefNumber;k++){
									//截取重表XML内容
									 String DefXML = subStringNum(XMLDATA,SOURCENOTENAME,k+1);
									 String SourceName = "";
									 while(SourceName!="!"){
										//创建XML
										 AddXmlElement(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
										 j++;
										 for(;j<excelToJsonArray.length();j++){
											 HJsonData = (JSONObject) excelToJsonArray.get(j); 
											 /*cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
											 if(SysUtility.isEmpty(cout)){
												 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
											 }else{
												 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
											 }*/
											 
											 //重重表逻辑
											 if("Y1".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
												 String YSeq = HJsonData.getString("SEQ");
												 String YSOURCENOTENAME = HJsonData.getString("SOURCENOTENAME");
												 String YTARGETFILEFLOOR = HJsonData.getString("TARGETFILEFLOOR");
												 int YDefNumber = FindDef(DefXML, YSOURCENOTENAME);
												 for(int l=0;l<YDefNumber;l++){
													//截取重表XML内容
													 String YDefXML = subStringNum(DefXML,YSOURCENOTENAME,l+1);
													 String YSourceName = "";
													 while(YSourceName!="!"){
														//创建XML
														 AddXmlElement(HJsonData, excelToJsonArray, Integer.parseInt(YSeq)-1, AppearMap, YDefXML ,XMLDATA);
														 j++;
														 for(;j<excelToJsonArray.length();j++){
															 HJsonData = (JSONObject) excelToJsonArray.get(j); 
															 if(Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))==Integer.parseInt(YTARGETFILEFLOOR)||Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))<Integer.parseInt(YTARGETFILEFLOOR)){
																 SourceName="!";
																 break;
															 }
															 if(SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
																 return;
															 }
															/* cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
															 if(SysUtility.isEmpty(cout)){
																 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
															 }else{
																 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
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
											 if(Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))==Integer.parseInt(TARGETFILEFLOOR)||Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))<Integer.parseInt(TARGETFILEFLOOR)){
												 SourceName="!";
												 if(j+1==DefNumber){
													 AddXmlElement(HJsonData, excelToJsonArray, j+1, AppearMap, DefXML,XMLDATA); 
												 }
												 break;
											 }
											 if(SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
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
						String path =ConfigPathData.getString("TARGETFILEPATH");
						fileHandle(path);
						fileHandle(ConfigPathData.getString("BACKPATH"));
						XMLOut.output(Doc, new FileOutputStream(path+File.separator+files[i].getName()));  
						FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("BACKPATH")+File.separator+files[i].getName());
						FileUtility.deleteFile(files[i].getPath());
						long endTime=System.currentTimeMillis();
						LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName()+"运行时长:"+(endTime-startTime)+"毫秒", Level.WARN);
						
					} catch (Exception e) {
						e.printStackTrace();
						fileHandle(ConfigPathData.getString("ERRORPATH"));
						FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("ERRORPATH")+File.separator+files[i].getName());
						FileUtility.deleteFile(files[i].getPath());
						LogUtil.printLog("文件转换失败："+files[i].getPath()+"请检查"+ConfigPathData.getString("CONFIGNAME")+"Sheet相关配置！", Level.WARN);
					} 
				}
			
			} finally {
				
			}
		
	}
	
	
	/**
	 * 此JSON数据必须包括 ExcelPath节点
	 * @param ConfigPathData
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void XmlToXmlOracle(JSONObject ConfigPathData,IDataAccess dataAccess) throws Exception{
			if(SysUtility.isEmpty(ConfigPathData.getString("SOURCEFILEPATH"))){
				return;
			}
			File files[] = new File(ConfigPathData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
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
						HashMap logMap = new HashMap();
						Thread.sleep(100);
						long startTime=System.currentTimeMillis();   //获取开始时间
						LogUtil.printLog("开始获取开始时间完成"+startTime, Level.INFO);
						initRoot();
						List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_name WHERE P_INDX='"+ConfigPathData.getInt("INDX")+"' ORDER BY to_number(exs_convert_config_name.SEQ)");
						//读取sheet页
						JSONArray excelToJsonArray = MessageUtil.getJSONArrayByList(query4List);
			 			if(excelToJsonArray.length()==0) {
			 				throw new RuntimeException("映射配置表为空无法转换文件");
			 			}
						String XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
						//存放出现次数
						Map<String, Integer> AppearMap = new LinkedHashMap<String, Integer>();
						//循环行
						for(int j=0;j<excelToJsonArray.length();j++){
							JSONObject HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
							if(!HJsonData.isNull("SOURCENOTENAME")) {
								 Integer cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
								 if(SysUtility.isEmpty(cout)){
									 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
								 }else{
									 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
								 }
							}
							 if(HJsonData.isNull("SEQ") || SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
								 throw new RuntimeException(ConfigPathData.getString("CONFIGNAME")+"SEQ序号不准确");
							 }
							//重表逻辑
							 if(!HJsonData.isNull("ISSUBLIST") && "2".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
								 LogUtil.printLog("重表逻辑准备执行", Level.INFO);
								 String Seq = HJsonData.getString("SEQ");
								 String SOURCENOTENAME = "";
								 if(!HJsonData.isNull("SOURCENOTENAME")) {
									 SOURCENOTENAME = HJsonData.getString("SOURCENOTENAME");
								 }
								 String TARGETFILEFLOOR ="";
								 if(!HJsonData.isNull("TARGETFILEFLOOR")) {
									 TARGETFILEFLOOR =  HJsonData.getString("TARGETFILEFLOOR");
								 }
								 int DefNumber = FindDef(XMLDATA, SOURCENOTENAME);
								 if(DefNumber==0) {
									 DefNumber=1;
								 }
								 for(int k=0;k<DefNumber;k++){
									//截取重表XML内容
									 String DefXML = subStringNum(XMLDATA,SOURCENOTENAME,k+1);
									 String SourceName = "";
									 while(SourceName!="!"){
										//创建XML
										 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
										 j++;
										 for(;j<excelToJsonArray.length();j++){
											 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
											 /*cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
											 if(SysUtility.isEmpty(cout)){
												 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
											 }else{
												 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
											 }*/
											 
											 //重重表逻辑
											 if(!HJsonData.isNull("ISSUBLIST") && "2".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
												 String YSeq = HJsonData.getString("SEQ");
												 String YSOURCENOTENAME = "";
														 
												 if(!HJsonData.isNull("SOURCENOTENAME")) {
													 YSOURCENOTENAME = HJsonData.getString("SOURCENOTENAME");
												 }
												 String YTARGETFILEFLOOR ="";
												 if(!HJsonData.isNull("TARGETFILEFLOOR")){
													 YTARGETFILEFLOOR =  HJsonData.getString("TARGETFILEFLOOR");
												 }
												 int YDefNumber = FindDef(DefXML, YSOURCENOTENAME);
												 for(int l=0;l<YDefNumber;l++){
													//截取重表XML内容
													 String YDefXML = subStringNum(DefXML,YSOURCENOTENAME,l+1);
													 String YSourceName = "";
													 while(YSourceName!="!"){
														 //创建XML
														 AddXmlElementOracle(HJsonData, excelToJsonArray, Integer.parseInt(YSeq)-1, AppearMap, YDefXML ,XMLDATA);
														 j++;
														 for(;j<excelToJsonArray.length();j++){
															 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
															 if(Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))==Integer.parseInt(YTARGETFILEFLOOR)||Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))<Integer.parseInt(YTARGETFILEFLOOR)){
																 SourceName="!";
																 break;
															 }
															 if(SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
																 return;
															 }
															/* cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
															 if(SysUtility.isEmpty(cout)){
																 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
															 }else{
																 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
															 }*/
															//创建XML
															 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, YDefXML ,XMLDATA);
														 }
														 YSourceName="!";
														 if(YDefNumber-1>l){
															 j=Integer.parseInt(YSeq)-1;
															 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
														 }
													 }
												 }
											 }
											 if(Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))==Integer.parseInt(TARGETFILEFLOOR)||Integer.parseInt(HJsonData.getString("TARGETFILEFLOOR"))<Integer.parseInt(TARGETFILEFLOOR)){
												 SourceName="!";
//												 if(j+1==DefNumber){
												 AddXmlElementOracle(HJsonData, excelToJsonArray, j+1, AppearMap, DefXML,XMLDATA); 
//												 }
												 break;
											 }
											 if(SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
												 return;
											 }
											//创建XML
											 if(SysUtility.isEmpty(XMLDATA)) {
												 XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
											 }
											 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
										 }
										 SourceName="!";
										 if(DefNumber-1>k){
											 j=Integer.parseInt(Seq)-1;
											 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
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
								 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, XMLDATA,XMLDATA);
								 LogUtil.printLog("添加Dom节点完成", Level.INFO);
							 }
						}
						if(SysUtility.isNotEmpty(root)) {
							String createSourcePath ="";
							String SuccessPath = "";
							Document Doc  = new Document(root);
							XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
							String path =ConfigPathData.getString("TARGETFILEPATH");
							String Successpath =ConfigPathData.getString("SUCCESS_BACK_PATH");
							fileHandle(path);
							fileHandle(ConfigPathData.getString("BACKPATH"));
							fileHandle(ConfigPathData.getString("SOURCE_BACK_PATH"));
							fileHandle(ConfigPathData.getString("SUCCESS_BACK_PATH"));
							XMLOut.output(Doc, new FileOutputStream(createSourcePath = path+File.separator+files[i].getName()));  
							XMLOut.output(Doc, new FileOutputStream(SuccessPath = Successpath+File.separator+files[i].getName()));  
							Doc.clone();
							FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("BACKPATH")+File.separator+files[i].getName());
							FileUtility.deleteFile(files[i].getPath());
							long endTime=System.currentTimeMillis();
							LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName()+"运行时长:"+(endTime-startTime)+"毫秒", Level.WARN);
							
							String[] fileSplit = files[i].getName().split("_");
							if(fileSplit.length<4) {
								continue;
							}
							String regNo =  fileSplit[0];
							String fileType = fileSplit[1];
							String fileDate = fileSplit[2];
							String no =fileSplit[3];
							logMap.put("DATA_SOURCE", "XML转换");
		    				logMap.put("SERIAL_NO", regNo);
		    				logMap.put("TARGET_FILE_NAME", files[i].getName());
		    				logMap.put("FILE_PATH", createSourcePath);
		    				logMap.put("TRANSFORMATION_CODE", 2);
		    				logMap.put("TRANSFORMATION_NAME", "成功");
		    				logMap.put("TRANSFORMATION_TIME", "1");
		    				logMap.put("PROCESS_MSG", "转换完成");
		    				logMap.put("SUCCESS_BACK_PATH", SuccessPath);
							Util.AddMessLog(dataAccess, logMap);
						}else {
							throw new RuntimeException("根节点错误  请检查配置信息");
						}
												
					} catch (Exception e) {
						String ErrSourcePath = "";
						e.printStackTrace();
						fileHandle(ErrSourcePath = ConfigPathData.getString("ERRORPATH"));
						FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("ERRORPATH")+File.separator+files[i].getName());
						FileUtility.deleteFile(files[i].getPath());
						LogUtil.printLog("文件转换失败："+files[i].getPath()+"请检查"+ConfigPathData.getString("CONFIGNAME")+"Sheet相关配置！", Level.WARN);
						String[] fileSplit = files[i].getName().split("_");
						if(fileSplit.length<4) {
							continue;
						}
						String regNo =  fileSplit[0];
						String fileType = fileSplit[1];
						String fileDate = fileSplit[2];
						String no =fileSplit[3];
						HashMap logMap = new HashMap();
						logMap.put("DATA_SOURCE", "XML转换");
	    				logMap.put("SERIAL_NO", regNo);
	    				logMap.put("TARGET_FILE_NAME", files[i].getName());
	    				logMap.put("FILE_PATH", ErrSourcePath);
	    				logMap.put("TRANSFORMATION_CODE", 3);
	    				logMap.put("TRANSFORMATION_NAME", "失败");
	    				logMap.put("TRANSFORMATION_TIME", "1");
	    				logMap.put("PROCESS_MSG", e.getMessage());
						Util.AddMessLog(dataAccess, logMap);
					} 
				}
			
			} finally {
				System.gc();
			}
		
	}
	
	
	
	/**
	 * 此JSON数据必须包括 ExcelPath节点  数据库版本
	 * @param ConfigPathData
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void XmlToXml2(JSONObject ConfigPathData,IDataAccess dataAccess) throws Exception{
			if(SysUtility.isEmpty(ConfigPathData.getString("SOURCEFILEPATH"))){
				return;
			}
			File files[] = new File(ConfigPathData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
			if(SysUtility.isEmpty(files)) {
				return;
			}
			try {
				for (int i = 0; i < files.length; i++) {
					try {
						if(i > FILECOUNT) {//预防内存爆炸 处理单次处理FILECOUNT 内存要求：4G以上
							return;
						}
						if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){
							//只处理XML文件 
							continue;
						}
						HashMap logMap = new HashMap();
						long startTime=System.currentTimeMillis();   //获取开始时间
						LogUtil.printLog("开始获取开始时间完成"+startTime, Level.INFO);
						initRoot();
						List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_name WHERE P_INDX='"+ConfigPathData.getInt("INDX")+"' ORDER BY to_number(exs_convert_config_name.SEQ)");
						//读取sheet页
						JSONArray excelToJsonArray = MessageUtil.getJSONArrayByList(query4List);
			 			if(excelToJsonArray.length()==0) {
			 				throw new RuntimeException("映射配置表为空无法转换文件");
			 			}
						String XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
						//存放出现次数
						Map<String, Integer> AppearMap = new LinkedHashMap<String, Integer>();
						//循环行
						for(int j=0;j<excelToJsonArray.length();j++){
							JSONObject HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
							if(!HJsonData.isNull("SOURCENOTENAME")) {
								 Integer cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
								 if(SysUtility.isEmpty(cout)){
									 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
								 }else{
									 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
								 }
							}
							 if(HJsonData.isNull("SEQ") || SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
								 throw new RuntimeException(ConfigPathData.getString("CONFIGNAME")+"SEQ序号不准确");
							 }
							 //重表逻辑
							 if(!HJsonData.isNull("ISSUBLIST") && "2".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
								 LogUtil.printLog("重表逻辑准备执行", Level.INFO);
								 int Seq = HJsonData.getInt("SEQ");
								 String SOURCENOTENAME = "";
								 if(!HJsonData.isNull("SOURCENOTENAME")) {
									 SOURCENOTENAME = HJsonData.getString("SOURCENOTENAME");
								 }
								 String TARGETFILEFLOOR ="";
								 if(!HJsonData.isNull("TARGETFILEFLOOR")) {
									 TARGETFILEFLOOR =  HJsonData.getString("TARGETFILEFLOOR");
								 }
								 int DefNumber = FindDef(XMLDATA, SOURCENOTENAME);
								 if(DefNumber==0) {
									 DefNumber=1;
								 }
								 for(int k=0;k<DefNumber;k++){
									 j = (Seq-1);
									 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
									 //截取重表XML内容
									 String DefXML = subStringNum(XMLDATA,SOURCENOTENAME,k+1);
									 //创建XML
									 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
									 Integer DomCount = GetDomCount(excelToJsonArray, j);//下级
									 j++;
									 for(int dom =0; dom < DomCount; dom++,j++) {
										 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
										 if(!HJsonData.isNull("ISSUBLIST") && "2".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
											 LogUtil.printLog("二重表逻辑准备执行", Level.INFO);
											 int SEQY = HJsonData.getInt("SEQ");
											 String SOURCENOTENAMEY = "";
											 if(!HJsonData.isNull("SOURCENOTENAME")) {
												 SOURCENOTENAMEY = HJsonData.getString("SOURCENOTENAME");
											 }
											 String TARGETFILEFLOORY ="";
											 if(!HJsonData.isNull("TARGETFILEFLOOR")) {
												 TARGETFILEFLOORY =  HJsonData.getString("TARGETFILEFLOOR");
											 }
											 int DefNumberY = FindDef(DefXML, SOURCENOTENAMEY);
											 if(DefNumberY==0) {
												 DefNumberY=1;
											 }
											 for(int l=0;l<DefNumberY;l++,dom++){
												 j = (SEQY-1);
												 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
												 //截取重表XML内容
												 String DefXMLY = subStringNum(DefXML,SOURCENOTENAMEY,l+1);
												 Integer DomCountY = GetDomCount(excelToJsonArray, j);//下级
												//创建XML
												 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, DefXMLY,XMLDATA);
												 j++;
												 for(int domY =0; domY < DomCountY; domY++,j++) {
													 HJsonData = new JSONObject((HashMap)excelToJsonArray.get(j));
													 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, DefXMLY,XMLDATA);
												 }
												//由于循环J++ 最后一次循环J则不需要++
												 if(l+1 ==DefNumberY) j--;
											 }
										 }else {
											 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, DefXML,XMLDATA);
										 }
									 }
									//由于循环J++ 最后一次循环J则不需要++
									 if(k+1 ==DefNumber)j--;
								 }
								LogUtil.printLog("重表逻辑执行结束", Level.INFO);
							 }else{
								//创建XML
								 LogUtil.printLog("添加Dom节点", Level.INFO);
								 if(SysUtility.isEmpty(XMLDATA)) {
									 XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
								 }
								 AddXmlElementOracle(HJsonData, excelToJsonArray, j, AppearMap, XMLDATA,XMLDATA);
								 LogUtil.printLog("添加Dom节点完成", Level.INFO);
							 }
						}
						if(SysUtility.isNotEmpty(root)) {
							String createSourcePath ="";
							String SuccessPath = "";
							Document Doc  = new Document(root);
							XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
							String path =ConfigPathData.getString("TARGETFILEPATH");
							String Successpath =ConfigPathData.getString("SUCCESS_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
							String BACKPATH =ConfigPathData.getString("BACKPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
							String SOURCEBACKPATH =ConfigPathData.getString("SOURCE_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
							fileHandle(path);
							fileHandle(BACKPATH);
							fileHandle(SOURCEBACKPATH);
							fileHandle(Successpath);
							createSourcePath = path+File.separator+files[i].getName();
							SuccessPath = Successpath+File.separator+files[i].getName();
							String renameFileNameXmlToExs = FileUtility.renameFileNameXmlToExs(createSourcePath);
							FileOutputStream out = new FileOutputStream(renameFileNameXmlToExs);
							XMLOut.output(Doc, out);  
							out.close();
							Doc.clone();
							XMLOut.clone();
							FileUtility.copyFile(renameFileNameXmlToExs, SuccessPath);
							FileUtility.copyFile(files[i].getPath(), BACKPATH+File.separator+files[i].getName());
							FileUtility.deleteFile(files[i].getPath());
							if(!FileUtility.renameFileExsToXml(renameFileNameXmlToExs)) {
								FileUtility.copyFile(renameFileNameXmlToExs,createSourcePath);
							}
							long endTime=System.currentTimeMillis();
							LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName()+"运行时长:"+(endTime-startTime)+"毫秒", Level.WARN);
							
							String[] fileSplit = files[i].getName().split("_");
							if(fileSplit.length<4) {
								continue;
							}
							String regNo =  fileSplit[0];
							String fileType = fileSplit[1];
							String fileDate = fileSplit[2];
							String no =fileSplit[3];
							logMap.put("DATA_SOURCE", "XML转换");
		    				logMap.put("SERIAL_NO", regNo);
		    				logMap.put("TARGET_FILE_NAME", files[i].getName());
		    				logMap.put("FILE_PATH", createSourcePath);
		    				logMap.put("TRANSFORMATION_CODE", 2);
		    				logMap.put("TRANSFORMATION_NAME", "成功");
		    				logMap.put("TRANSFORMATION_TIME", "1");
		    				logMap.put("PROCESS_MSG", "转换完成");
		    				logMap.put("SUCCESS_BACK_PATH", SuccessPath);
							Util.AddMessLog(dataAccess, logMap);
						}else {
							throw new RuntimeException("根节点错误  请检查配置信息");
						}
												
					} catch (Exception e) {
						e.printStackTrace();
						String ErrSourcePath = ConfigPathData.getString("ERRORPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
						fileHandle(ErrSourcePath);
						FileUtility.copyFile(files[i].getPath(), ErrSourcePath+File.separator+files[i].getName());
						FileUtility.deleteFile(files[i].getPath());
						LogUtil.printLog("文件转换失败："+files[i].getPath()+"请检查"+ConfigPathData.getString("CONFIGNAME")+"Sheet相关配置！", Level.WARN);
						String[] fileSplit = files[i].getName().split("_");
						if(fileSplit.length<4) {
							continue;
						}
						String regNo =  fileSplit[0];
						String fileType = fileSplit[1];
						String fileDate = fileSplit[2];
						String no =fileSplit[3];
						HashMap logMap = new HashMap();
						logMap.put("DATA_SOURCE", "XML转换");
	    				logMap.put("SERIAL_NO", regNo);
	    				logMap.put("TARGET_FILE_NAME", files[i].getName());
	    				logMap.put("FILE_PATH", ErrSourcePath);
	    				logMap.put("TRANSFORMATION_CODE", 3);
	    				logMap.put("TRANSFORMATION_NAME", "失败");
	    				logMap.put("TRANSFORMATION_TIME", "1");
	    				logMap.put("PROCESS_MSG", e.getMessage());
						Util.AddMessLog(dataAccess, logMap);
					} 
				}
			
			} finally {
				System.gc();
			}
		
	}
		
	//文件处理,为了方便建议方法改成可以返回最终路径的字符串，以便调用
	public static void fileHandle(String path) {			
		fileHandle(path,"N");
	}
	public static void fileHandle(String path,String strDef) {
		try {		
			if(strDef.toUpperCase().equals("Y")){
				path = path+File.separator + SysUtility.getSysDateWithoutTime();//在原有的文件夹下面添加日期文件夹									
			}
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
	 public static void AddXmlElementOracle(JSONObject HJsonData,JSONArray excelToJsonArray,int i,Map<String, Integer> AppearMap,String xmlToString,String XMLDATA) throws JSONException{
			JSONObject object = HJsonData;
			if(!HJsonData.isNull("SOURCENOTENAME") && SysUtility.isEmpty(AppearMap.get(HJsonData.getString("SOURCENOTENAME")))){
				AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
			}
			String SOURCENOTENAMES ="";
			if(!HJsonData.isNull("SOURCENOTENAME")) {
				SOURCENOTENAMES = HJsonData.getString("SOURCENOTENAME");
			}
			 //创建XML
			 if("0".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& SysUtility.isEmpty(root)){
				 root = new Element(HJsonData.getString("TARGETCOLNAME")); 
			 }else if("1".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 value = isJsonFunctionValue(HJsonData,value,xmlToString);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 root.addContent(element1 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("2".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element1.addContent(element2 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("3".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element2.addContent(element3 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("4".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value =  FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element3.addContent(element4 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("5".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value =  FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element4.addContent(element5 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("6".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element5.addContent(element6 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("7".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element6.addContent(element7 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("8".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value =  FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element7.addContent(element8 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("9".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element8.addContent(element9 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("10".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element9.addContent(element0 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("11".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element0.addContent(element11 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("12".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element11.addContent(element12 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("13".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element12.addContent(element13 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("14".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element13.addContent(element14 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("15".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element14.addContent(element15 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("16".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element15.addContent(element16 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("17".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("SourceFileFloor"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element16.addContent(element17 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("18".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element17.addContent(element18 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("19".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element18.addContent(element19 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("20".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element19.addContent(element20 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }else if("21".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = FindDevlue(object,value);
				 String NextTARGETFILEFLOOR =null;
				 if((excelToJsonArray.length()>(i+1))){
					 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element20.addContent(new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }
		}
	 
	public static void AddXmlElement(JSONObject HJsonData,JSONArray excelToJsonArray,int i,Map<String, Integer> AppearMap,String xmlToString,String XMLDATA) throws JSONException{
		JSONObject object = HJsonData;
		if(SysUtility.isEmpty(AppearMap.get(HJsonData.getString("SOURCENOTENAME")))){
			AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
		}
		 //创建XML
		 if("0".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& SysUtility.isEmpty(root)){
			 root = new Element(HJsonData.getString("TARGETCOLNAME")); 
		 }else if("1".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 value = isJsonFunctionValue(HJsonData,value,value);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 root.addContent(element1 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("2".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element1.addContent(element2 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("3".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element2.addContent(element3 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("4".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element3.addContent(element4 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("5".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element4.addContent(element5 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("6".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element5.addContent(element6 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("7".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element6.addContent(element7 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("8".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element7.addContent(element8 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("9".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element8.addContent(element9 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("10".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element9.addContent(element0 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("11".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element0.addContent(element11 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("12".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element11.addContent(element12 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("13".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element12.addContent(element13 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("14".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element13.addContent(element14 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("15".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element14.addContent(element15 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("16".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element15.addContent(element16 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("17".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("SourceFileFloor"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element16.addContent(element17 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("18".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element17.addContent(element18 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("19".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element18.addContent(element19 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("20".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element19.addContent(element20 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
		 }else if("21".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(root)){
			 String value = FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), HJsonData.getString("SOURCENOTENAME"),object);
			 String NextTARGETFILEFLOOR =null;
			 if((excelToJsonArray.length()>(i+1))){
				 JSONObject nextjsonObject = (JSONObject) excelToJsonArray.get(i+1);
				 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
			 }
			 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
			 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
			 element20.addContent(new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
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
			theStr = theStr.trim();
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
	 
	 public static String FindDevlue(JSONObject jsonObject,String str) throws JSONException {
		 LogUtil.printLog("执行查询Value值初始化", Level.INFO);
	    	if(!jsonObject.isNull("DEFVALUE")&&SysUtility.isNotEmpty(jsonObject)&&!SysUtility.isEmpty(jsonObject.getString("DEFVALUE"))&&!"".equals(jsonObject.getString("DEFVALUE"))){
	    		return jsonObject.getString("DEFVALUE");
	    	}else {
	    		return str;
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
	
	 
	 public static String init(String TARGETFILEFLOOR,String NextTARGETFILEFLOOR,String value){
		  try {
			  if(Integer.parseInt(NextTARGETFILEFLOOR)>Integer.parseInt(TARGETFILEFLOOR)){
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
			 	theStr = theStr.trim();
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
				 if(!HJsonData.isNull("FUNCTION") && SysUtility.isNotEmpty(HJsonData.getString("FUNCTION"))) {
					 LogUtil.printLog("执行Function函数", Level.INFO);
					 if(SysUtility.isNotEmpty(value)) {
						 value = value.replaceAll(" ","");
						 value = value.replaceAll("\r","");
						 value = value.replaceAll("\n","");	 
					 }
					
					 String[] FunctionSplit = HJsonData.getString("FUNCTION").split(";");
					 if(FunctionSplit.length>0) {
						 if(FunctionSplit[0].toUpperCase().equals("GETUUID()")) {
							 value = SysUtility.GetUUID();
						 }else if(FunctionSplit[0].toUpperCase().equals("GETTIME()")) {
							 value = SysUtility.getSysDate();
						 }else if(FunctionSplit[0].toUpperCase().equals("SETUUID()")) {
							 String uuid = SysUtility.GetUUID();
							 JSON.put(HJsonData.getString("TARGETCOLNAME"), uuid);
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
				 
				 if(!HJsonData.isNull("MAPPING") && SysUtility.isNotEmpty(HJsonData.getString("MAPPING"))){
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
		 public static String subStringNum(String xml,String SOURCENOTENAME,int number){
			 int indexOf = 0;
			 int indexOutOf = 0;
			 String xmlBody="";
			 if(!SysUtility.isEmpty(SOURCENOTENAME)) {
				 SOURCENOTENAME = SOURCENOTENAME.trim();
			 }
			 try {
				 for(int i=0;i<number;i++){
					 indexOf = xml.indexOf("<"+SOURCENOTENAME+">",indexOf+1);
					 indexOutOf = xml.indexOf("</"+SOURCENOTENAME+">",indexOf);
					 if(indexOutOf == -1) {
						 xmlBody = "";
					 }else {
						 xmlBody = xml.substring(indexOf,indexOutOf+(3+SOURCENOTENAME.length()));
					 }
				 }
			} catch (Exception e) {
				return xmlBody;
			}
			 
			 return xmlBody;
		 }
	//获取本级节点	
	private static Integer GetThisDomCount(JSONArray XmlJsonArr,Integer number) throws JSONException {
			Integer DomCount = 1;
			Integer JsNumber = number;
			for(int i=JsNumber;i<XmlJsonArr.length();i++) {
				if(i+1 < XmlJsonArr.length()) {
					JSONObject jsonObject = null;
					if(XmlJsonArr.get(i) instanceof JSONObject) {
						jsonObject = XmlJsonArr.getJSONObject(i);
					}else if(XmlJsonArr.get(i) instanceof Map) {
						jsonObject = new JSONObject(XmlJsonArr.get(i).toString());
					}
					
					Integer TARGETFILEFLOOR = jsonObject.getInt("TARGETFILEFLOOR");
					if(XmlJsonArr.get(i+1) instanceof JSONObject) {
						jsonObject = XmlJsonArr.getJSONObject(i+1);
					}else if(XmlJsonArr.get(i+1) instanceof Map) {
						jsonObject = new JSONObject(XmlJsonArr.get(i+1).toString());
					}
					Integer TARGETFILEFLOOR2 = jsonObject.getInt("TARGETFILEFLOOR");
					if(TARGETFILEFLOOR2==TARGETFILEFLOOR) {
						DomCount++;
					}else {
						return DomCount;
					}
				}
			}
			return DomCount;
	}
	//获取下级
	private static Integer GetDomCount(JSONArray XmlJsonArr,Integer number) throws JSONException {
		Integer DomCount = 0;
		Integer JsNumber = number;
		Integer ThisJb = 0;
		if(XmlJsonArr.length()>number) {
			JSONObject jsonObject = null;
			if(XmlJsonArr.get(number) instanceof JSONObject) {
				jsonObject = XmlJsonArr.getJSONObject(number);
			}else if(XmlJsonArr.get(number) instanceof Map) {
				jsonObject = new JSONObject(XmlJsonArr.get(number).toString());
			}
			if(!jsonObject.isNull("TARGETFILEFLOOR") && !SysUtility.isEmpty(jsonObject.get("TARGETFILEFLOOR"))) {
				ThisJb = jsonObject.getInt("TARGETFILEFLOOR");
			}
		}
		
		for(int i=JsNumber;i<XmlJsonArr.length();i++) {
			if(i+1 < XmlJsonArr.length()) {
				JSONObject jsonObject = null;
				if(XmlJsonArr.get(i) instanceof JSONObject) {
					jsonObject = XmlJsonArr.getJSONObject(i);
				}else if(XmlJsonArr.get(i) instanceof Map) {
					jsonObject = new JSONObject(XmlJsonArr.get(i).toString());
				}
				if(XmlJsonArr.get(i+1) instanceof JSONObject) {
					jsonObject = XmlJsonArr.getJSONObject(i+1);
				}else if(XmlJsonArr.get(i+1) instanceof Map) {
					jsonObject = new JSONObject(XmlJsonArr.get(i+1).toString());
				}
				Integer TARGETFILEFLOOR2 = jsonObject.getInt("TARGETFILEFLOOR");
				if(TARGETFILEFLOOR2>ThisJb) {
					DomCount++;
				}else if(TARGETFILEFLOOR2<ThisJb || TARGETFILEFLOOR2 == ThisJb){
					return DomCount;
				}
			}
		}
		return DomCount;
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
		 LogUtil.printLog("初始化Dom节点完成", Level.INFO);
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
