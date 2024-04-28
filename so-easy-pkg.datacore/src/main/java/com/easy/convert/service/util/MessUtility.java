package com.easy.convert.service.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Level;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.app.utility.ExsUtility;
import com.easy.convert.constants.ExsConstants;
import com.easy.convert.service.mess.Message_TransferSier;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.file.FileFilterHandle;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

import jxl.write.DateTime;


public class MessUtility {
	private static JSONObject CacheJson = new JSONObject();
	public static final int singleProcessCount = 10000;//单线程模式：单任务的总报文处理量。
	//报文转换统一中转站
	public static void MessToxml(ServicesBean bean, IDataAccess DataAccess) throws Exception {
		String serviceMode = bean.getServiceMode();
		if(ExsConstants.Local.toLowerCase().equals(serviceMode.toLowerCase())) {//本地
			ToXmlForLocal(bean, DataAccess);
		}else if(ExsConstants.Oracle.toLowerCase().equals(serviceMode.toLowerCase())) {//数据库版
			ToXmlForPool(bean, DataAccess);
		}else if(ExsConstants.DB.toLowerCase().equals(serviceMode.toLowerCase())) {//DB版
			ToXmlForDb(bean, DataAccess);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static void ToXmlForDb(ServicesBean bean,IDataAccess DataAccess) {
		try {
			List query4List = SQLExecUtils.query4List("SELECT * FROM EXS_HANDLE_SENDER WHERE nvl(MSG_FLAG,'0') = '0' AND MSG_TYPE=?", new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
						ps.setString(1, ExsConstants.DBTOXML);
				}
			});
			if(query4List.size() > 0) {
				for(int i=0; i < query4List.size(); i++) {
					HashMap Query4Map = null;
					try {
						Query4Map = (HashMap) query4List.get(i);
						String INDX = (String) Query4Map.get("INDX");
						String MSG_NO = (String) Query4Map.get("MSG_NO");
						String TECH_REG_CODE = (String) Query4Map.get("TECH_REG_CODE");
						String ATTRIBUTE1 = (String) Query4Map.get("ATTRIBUTE1");
						String ATTRIBUTE2 = (String) Query4Map.get("ATTRIBUTE2");
						//获取连接
						List query4PathList = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_path WHERE INDX='"+TECH_REG_CODE+"'");
						if(query4PathList.size() >0) {
							HashMap Que4PathMap = (HashMap) query4PathList.get(0);
							Connection Conn = null;
							try {
								Conn = Util.GetConn((String)Que4PathMap.get("SENDTYPE"), (String)Que4PathMap.get("SENDADRESS"), (String)Que4PathMap.get("SENDUSERNAME"), (String)Que4PathMap.get("SENDUSERPWD"));
							} catch (Exception e) {
								throw new RuntimeException("获取Connection 失败！");
							}
							if(Conn!= null) {
								StringBuffer sql = new StringBuffer();
								sql .append("SELECT * FROM ");
								sql .append(ATTRIBUTE1);
								sql .append(" WHERE ");
								sql .append(ATTRIBUTE2);
								sql .append(" = ");
								sql .append("'"+MSG_NO+"'");
								try {
									List TableDataList = SQLExecUtils.query4List(Conn, sql.toString());
									if(TableDataList .size() > 0) {
										//传入新方法 进行其他的逻辑
										DbToXml(Query4Map,TableDataList,Que4PathMap,DataAccess,Conn);
									}else {
										throw new RuntimeException(ATTRIBUTE1 + "：字段"+ATTRIBUTE2+"="+MSG_NO +"数据为空");
									}
								} finally {
									Conn.close();
								}
								
							}else {
								throw new RuntimeException("数据库连接失败 请检查数据库相关配置！");
							}
							Util.UpHanderFlatOk((String)Query4Map.get("INDX"), DataAccess);
						}else { //任务表的数据不存在
							throw new RuntimeException("相关数据不存在！");
						}
					} catch (RuntimeException e) {//人工抛出
						if(!SysUtility.isEmpty(Query4Map)) {
							String FILENAME = (String) Query4Map.get("EXTEND_XML");
							String PART_ID = (String) Query4Map.get("PART_ID");
							Map messLog = new HashMap();
							messLog.put("DATA_SOURCE", "DB");
							messLog.put("SERIAL_NO", PART_ID);
							messLog.put("TARGET_FILE_NAME",FILENAME);
							messLog.put("TRANSFORMATION_CODE", 3);
							messLog.put("TRANSFORMATION_NAME", "失败");
							messLog.put("PROCESS_MSG", e.getMessage());
							Util.AddMessLog(DataAccess, messLog);
							Util.UpHanderFlatErr((String)Query4Map.get("INDX"), DataAccess);
						}
						LogUtil.printLog(e.getMessage(), Level.ERROR);
					} catch (Exception e) {//系统异常
						if(!SysUtility.isEmpty(Query4Map)) {
							String FILENAME = (String) Query4Map.get("EXTEND_XML");
							String PART_ID = (String) Query4Map.get("PART_ID");
							Map messLog = new HashMap();
							messLog.put("DATA_SOURCE", "DB");
							messLog.put("SERIAL_NO", PART_ID);
							messLog.put("TARGET_FILE_NAME",FILENAME);
							messLog.put("TRANSFORMATION_CODE", 3);
							messLog.put("TRANSFORMATION_NAME", "失败");
							messLog.put("PROCESS_MSG", e.getMessage());
							Util.AddMessLog(DataAccess, messLog);
							Util.UpHanderFlatErr((String)Query4Map.get("INDX"), DataAccess);
						}
						LogUtil.printLog(e.getMessage(), Level.ERROR);
					}
				
				}
			}
		} catch (RuntimeException e){//人工抛出
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (Exception e) {//系统异常
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally {
			
		}
	}
	
	/**
	 *  DB转换XML文件
	 * @param HanderMap      任务表数据
	 * @param tableDataList  第三方表数据
	 * @param PathMap        Path表数据
	 * @param dataAccess     事物
	 * @throws LegendException 
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private static void DbToXml(final Map HanderMap , List tableDataList, Map PathMap , IDataAccess dataAccess ,Connection conn) throws LegendException {
		try {
			if(tableDataList .size() > 0) {
				for(int i=0; i< tableDataList.size(); i++) {
					//读取sheet页
					List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_name WHERE P_INDX=? ORDER BY to_number(exs_convert_config_name.SEQ)",new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1, (String)HanderMap.get("TECH_REG_CODE"));
						}
					});
					if(query4List.size()<=0){
						throw new RuntimeException("配置信息:映射配置信息不可为空");
					}
					JSONArray dbJsonArray = MessageUtil.getJSONArrayByList(query4List);
					HashMap  MainMap = null;
					if(tableDataList.get(i) instanceof Map) {
						 MainMap = (HashMap) Util.toUpperCaseMap((HashMap) tableDataList.get(i));
					}else {
						LogUtil.printLog("SQLExecUtils.query4List 方法映射不为Map 请及时更新代码!", Level.ERROR);
						throw new  RuntimeException("系统故障:系统故障");
					}
					initRoot();
					for(Integer j =0; j < dbJsonArray.length(); j++) {//循环配置表数据
						JSONObject HJsonData = new JSONObject((HashMap)dbJsonArray.get(j));
						 //重表逻辑
						 if(!HJsonData.isNull("ISSUBLIST") && "2".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
							 LogUtil.printLog("重表逻辑准备执行", Level.INFO);
							 if(HJsonData.isNull("SOURCENOTENAME")) {
									throw new  RuntimeException("配置信息:重节点源节点不可为空");
							 }
							 String SOURCENOTENAME = HJsonData.getString("SOURCENOTENAME");
							 int SEQ = HJsonData.getInt("SEQ");
							 j = (SEQ-1);
							 String[] split = SOURCENOTENAME.split("&");
							 if(split.length==1) {
								 throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAME+"配置信息有误!");
							 }
							 String[] split2 = split[1].split("\\|");
							 if(split2.length==1) {
								 throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAME+"配置信息有误!");
							 }
							 String name1 = split2[0];
							 String name2 = split2[1];//对应字段
							 String[] split3 = name1.split("\\.");
							 if(split3.length==1) {
							 	throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAME+"配置信息有误!");
							 }
							 String tablename1 = split3[0];//表名称
							 String fieid = split3[1];//对应字段
							 StringBuffer Sql = new StringBuffer();
							 Sql .append("SELECT * FROM ");
							 Sql .append(split[0]);
							 Sql .append(" WHERE ");
							 Sql .append(name2);
							 Sql .append(" = '");
							 Sql .append(MainMap.get(fieid.toUpperCase()));
							 Sql .append("'");
							 List query4List2 = SQLExecUtils.query4List(conn, Sql.toString());
							 Integer DomCount = GetDomCount(dbJsonArray, j);//下级
							 for(int k=0; k < query4List2.size(); k++) {//子表数据次数
								 HashMap MainMap2 = (HashMap) Util.toUpperCaseMap((HashMap) query4List2.get(k));
								 j = (SEQ-1);
								 HJsonData = new JSONObject((HashMap)dbJsonArray.get(j));
								 AddExcelElement(HJsonData, new JSONObject(MainMap));
								 j++;
								 for(int k1 =0; k1<DomCount; k1++,j++) {
									 HJsonData = new JSONObject((HashMap)dbJsonArray.get(j));
									 if(!HJsonData.isNull("ISSUBLIST") && "2".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
										 LogUtil.printLog("二重表逻辑准备执行", Level.INFO);
										 //TODO 待开发
									 }else {
										 AddExcelElement(HJsonData, new JSONObject(MainMap2));
									 }
								 }
								//由于循环J++ 最后一次循环J则不需要++
								 if(k+1 ==query4List2.size())j--;
							 }
						 }else {
							 LogUtil.printLog("添加Dom节点", Level.INFO);
							 AddExcelElement(HJsonData, new JSONObject(MainMap));
						 }
						
						
					}
					if(SysUtility.isNotEmpty(root)) {
						String TARGETFILEPATH = (String) PathMap.get("TARGETFILEPATH");
						String TARGETFILEPATHBACK = (String) PathMap.get("SUCCESS_BACK_PATH");
						String FILENAME = (String) HanderMap.get("EXTEND_XML");
						String PART_ID = (String) HanderMap.get("PART_ID");
						String renameFileNameXmlToExs = FileUtility.renameFileNameXmlToExs(TARGETFILEPATH + File.separator + FILENAME);
						Document Doc  = new Document(root);
						XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
						FileOutputStream out = new FileOutputStream(renameFileNameXmlToExs);
						XMLOut.output(Doc, out);  
						out.close();
						Doc.clone();
						XMLOut.clone();
						FileUtility.copyFile(renameFileNameXmlToExs, TARGETFILEPATHBACK + File.separator + FILENAME);
						if(!FileUtility.renameFileExsToXml(renameFileNameXmlToExs)) {
							FileUtility.copyFile(renameFileNameXmlToExs,TARGETFILEPATH + File .separator + FILENAME);
						}
						LogUtil.printLog("文件转换成功："+TARGETFILEPATH+File.separator+FILENAME, Level.WARN);
						Map messLog = new HashMap();
						messLog.put("DATA_SOURCE", "DB");
						messLog.put("SERIAL_NO", PART_ID);
						messLog.put("TARGET_FILE_NAME",FILENAME);
						messLog.put("FILE_PATH", TARGETFILEPATH + File.separator + FILENAME);
						messLog.put("SUCCESS_BACK_PATH", TARGETFILEPATHBACK + File.separator + FILENAME);
						messLog.put("TRANSFORMATION_CODE", 2);
						messLog.put("TRANSFORMATION_NAME", "成功");
						messLog.put("TRANSFORMATION_TIME", true);
						messLog.put("PROCESS_MSG", "转换成功");
						Util.AddMessLog(dataAccess, messLog);
					}
				}
			}
		} catch (RuntimeException e) {//人工抛出异常 
			String FILENAME = (String) HanderMap.get("EXTEND_XML");
			String PART_ID = (String) HanderMap.get("PART_ID");
			LogUtil.printLog("文件转换失败："+FILENAME, Level.WARN);
			Map messLog = new HashMap();
			messLog.put("DATA_SOURCE", "DB");
			messLog.put("SERIAL_NO", PART_ID);
			messLog.put("TARGET_FILE_NAME",FILENAME);
			messLog.put("TRANSFORMATION_CODE", 3);
			messLog.put("TRANSFORMATION_NAME", "失败");
			messLog.put("PROCESS_MSG", e.getMessage());
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (Exception e) {//系统异常
			String FILENAME = (String) HanderMap.get("EXTEND_XML");
			String PART_ID = (String) HanderMap.get("PART_ID");
			LogUtil.printLog("文件转换失败："+FILENAME, Level.WARN);
			Map messLog = new HashMap();
			messLog.put("DATA_SOURCE", "DB");
			messLog.put("SERIAL_NO", PART_ID);
			messLog.put("TARGET_FILE_NAME",FILENAME);
			messLog.put("TRANSFORMATION_CODE", 3);
			messLog.put("TRANSFORMATION_NAME", "失败");
			messLog.put("PROCESS_MSG", e.getMessage());
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} finally {
			
		}
 	}

	//转换XML数据库版 （目前只支持Oracle）
	@SuppressWarnings("rawtypes")
	public static void ToXmlForPool(ServicesBean bean,IDataAccess DataAccess) {
		try {
			List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_path WHERE nvl(ISENABLED,'1') = '1' AND FUNCTIONTYPE!=2 ORDER BY ORG_ID");
			if(query4List.size()>0) {
					JSONArray jsonArrayByList = getJSONArrayByList(query4List);
					JSONArray excelToJsonArray = jsonArrayByList;
					if(SysUtility.isEmpty(excelToJsonArray)) {
						throw new RuntimeException("配置信息不存在!");
					}
					for(int i=0;i<excelToJsonArray.length();i++) {
						long startTime=System.currentTimeMillis();   //获取开始时间
						JSONObject ConfigData = new JSONObject((HashMap)excelToJsonArray.get(i));
						//是否启用
						if(ConfigData.isNull("ISENABLED") || ConfigData.getString("ISENABLED").equals("0")) {
							LogUtil.printLog(ConfigData.getString("CONFIGNAME")+" 配置被禁用", Level.ERROR);
							excelToJsonArray.remove(i);
							i--;
							continue;
						}
						if(ConfigData.isNull("SOURCEFILETYPE") || ConfigData.isNull("TARGETFILETYPE")) {
							continue;
						}
						if(!SysUtility.isEmpty(ConfigData.get("SOURCEFILETYPE"))&&ConfigData.get("SOURCEFILETYPE").toString().toUpperCase().equals("1")&&ConfigData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
							//XML TO XML
							XmlToXmlForPool(ConfigData, DataAccess);
						}else if(!SysUtility.isEmpty(ConfigData.get("SOURCEFILETYPE"))&&ConfigData.get("SOURCEFILETYPE").toString().toUpperCase().equals("2")&&ConfigData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
							//按照excel格式读取 生成XML
							ExcelToXmlForPool(ConfigData,DataAccess);
						}
						long endTime=System.currentTimeMillis();   //获取结束时间
						LogUtil.printLog("扫描完毕：运行时长:"+(endTime-startTime)+"毫秒", Level.INFO);
					}
			}
		} catch (RuntimeException e){//人工抛出
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (Exception e) {//系统异常
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public static void ToXmlForLocal(ServicesBean bean,IDataAccess DataAccess) throws Exception {
		try {
			ExsUtility.InitServicesBeanPath(bean);
			JSONArray ExcelCaches = ExcelCache.getExcelToJsonArray();
			if(SysUtility.isEmpty(ExcelCaches) || ExcelCaches.length() < 1) {
				ExcelCache.setExcelToJsonArray(ExcelCaches = ExcelToJsonArrayUtil.ExcelToJsonArray(SysUtility.GetSetting("System", "ConfigPath"),"ConfigPath"));
			}
			for(int i=0;i<ExcelCaches.length();i++) {
				long startTime=System.currentTimeMillis();
				JSONObject ConfigData = null;
				if(ExcelCaches.get(i) instanceof Map) {
					ConfigData = new JSONObject((HashMap)ExcelCaches.get(i));
				}else if(ExcelCaches.get(i) instanceof JSONObject) {
					ConfigData =(JSONObject) ExcelCaches.get(i);
				}
				if(!ConfigData.isNull("ISENABLED")) {
					if(SysUtility.isEmpty(ConfigData.getString("ISENABLED")) || ConfigData.getString("ISENABLED").equals("0")) {
						LogUtil.printLog(ConfigData.getString("CONFIGNAME")+" 配置已被禁用", Level.WARN);
						ExcelCaches.remove(i);
						ExcelCache.setExcelToJsonArray(ExcelCaches);
						i--;
						continue;
					}
				}
				if(!SysUtility.isEmpty(ConfigData.get("SOURCEFILETYPE"))&&ConfigData.get("SOURCEFILETYPE").toString().toUpperCase().equals("1")&&ConfigData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
					//XML TO XML
					if(MutiUtil.IsMutiProcess(bean)){
						bean.setSearchParam(ConfigData);
						MutiUtil.MutiProcessToXmlLocal(bean, DataAccess);
						continue;
					}else {
						XmlToXmlForLocal(ConfigData);
					}
				}else if(!SysUtility.isEmpty(ConfigData.get("SOURCEFILETYPE"))&&ConfigData.get("SOURCEFILETYPE").toString().toUpperCase().equals("2")&&ConfigData.get("TARGETFILETYPE").toString().toUpperCase().equals("1")){
					//按照excel格式读取 生成XML
					ExcelToXmlForLocal(ConfigData);
				}
				long endTime=System.currentTimeMillis();   //获取结束时间
				LogUtil.printLog("扫描完毕：运行时长:"+(endTime-startTime)+"毫秒", Level.INFO);
			}
		} catch (RuntimeException e){//人工抛出
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (Exception e) {//系统异常
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		
	}
	//数据库XML转换XMLCore（目前支持Oracle）
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public static void XmlToXmlForPool(JSONObject ConfigData,IDataAccess DataAccess) {
		try {
			if(SysUtility.isEmpty(ConfigData.getString("SOURCEFILEPATH"))){
				throw new RuntimeException("配置信息源目录为空!");
			}
			File files[] = new File(ConfigData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
			if(SysUtility.isEmpty(files) || files.length<=0) {
				return;
			}
			for (int i = 0; i < files.length; i++) {
				if(i > singleProcessCount) {//预防内存爆炸 处理单次处理singleProcessCount 内存要求：4G以上
					return;
				}
				if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){//只处理XML文件 
					continue;
				}
				try {
					long startTime=System.currentTimeMillis();   //获取开始时间
					HashMap logMap = new HashMap();
					initRoot();
					List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_name WHERE P_INDX='"+ConfigData.getInt("INDX")+"' ORDER BY to_number(exs_convert_config_name.SEQ)");
					JSONArray PoolExcelJson = getJSONArrayByList(query4List);//数据格式转换
					if(PoolExcelJson.length()==0) {
		 				throw new RuntimeException("映射配置表为空无法转换文件");
		 			}
					String XMLDATA = FileUtility.readFile(files[i], false, "UTF-8");
					if(SysUtility.isEmpty(XMLDATA)) {
						 InputStream in = new FileInputStream(files[i]);
						 byte[] bytes = SysUtility.InputStreamToByte(in);
						 XMLDATA = new String(bytes,"UTF-8");
						 if(in != null) {
							 in.close();
						 }
					 }
					Map<String, Integer> AppearMap = new LinkedHashMap<String, Integer>();
					for(int j=0;j<PoolExcelJson.length();j++){
						JSONObject HJsonData = null;
						if(PoolExcelJson.get(j) instanceof Map) {
							HJsonData = new JSONObject((HashMap)PoolExcelJson.get(j));
						}else if(PoolExcelJson.get(j) instanceof JSONObject) {
							HJsonData = PoolExcelJson.getJSONObject(j);
						}else {
							HJsonData = new JSONObject(PoolExcelJson.get(j).toString());
						}
						if(!HJsonData.isNull("SOURCENOTENAME")) {
							 Integer cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
							 if(SysUtility.isEmpty(cout)){
								 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
							 }else{
								 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
							 }
						}
						if(HJsonData.isNull("SEQ") || SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
							 throw new RuntimeException(ConfigData.getString("CONFIGNAME")+"SEQ序号不准确");
						}
						if(!HJsonData.isNull("ISSUBLIST") && "2".equals(HJsonData.getString("ISSUBLIST").toUpperCase())){
							 LogUtil.printLog("重表逻辑准备执行", Level.INFO);
							 int SEQ = HJsonData.getInt("SEQ");
							 String SOURCENOTENAME = "";
							 if(!HJsonData.isNull("SOURCENOTENAME")) {
								 SOURCENOTENAME = HJsonData.getString("SOURCENOTENAME");
							 }
							 String TARGETFILEFLOOR ="";
							 if(!HJsonData.isNull("TARGETFILEFLOOR")) {
								 TARGETFILEFLOOR =  HJsonData.getString("TARGETFILEFLOOR");
							 }
							 int DEFNUMBER = FindDef(XMLDATA, SOURCENOTENAME);
							 if(DEFNUMBER==0) {
								 DEFNUMBER=1;
							 }
							 for(int k=0;k<DEFNUMBER;k++){
								 j = (SEQ-1);
								 if(PoolExcelJson.get(j) instanceof Map) {
									 HJsonData = new JSONObject((HashMap)PoolExcelJson.get(j));
								 }else if(PoolExcelJson.get(j) instanceof JSONObject) {
									 HJsonData = PoolExcelJson.getJSONObject(j);
								 }else {
									 HJsonData = new JSONObject(PoolExcelJson.get(j).toString());
								 }
								 String DEFXML = subStringNum(XMLDATA,SOURCENOTENAME,k+1);//截取重表内容
								 AddElementLocalXml(HJsonData, PoolExcelJson, j, AppearMap, DEFXML,XMLDATA);
								 Integer DOMCOUNT = GetDomCount(PoolExcelJson, j);//下级
								 j++;
								 for(int dom =0; dom < DOMCOUNT; dom++,j++) {
									 if(PoolExcelJson.get(j) instanceof Map) {
										 HJsonData = new JSONObject((HashMap)PoolExcelJson.get(j));
									 }else if(PoolExcelJson.get(j) instanceof JSONObject) {
										 HJsonData = PoolExcelJson.getJSONObject(j);
									 }else {
										 HJsonData = new JSONObject(PoolExcelJson.get(j).toString());
									 }
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
										 int DEFNUMBERY = FindDef(DEFXML, SOURCENOTENAMEY);//截取重表数
										 if(DEFNUMBERY==0) {
											 DEFNUMBERY=1;
										 }
										 for(int l=0;l<DEFNUMBERY;l++,dom++){
											 j = (SEQY-1);
											 if(PoolExcelJson.get(j) instanceof Map) {
												 HJsonData = new JSONObject((HashMap)PoolExcelJson.get(j));
											 }else if(PoolExcelJson.get(j) instanceof JSONObject) {
												 HJsonData = PoolExcelJson.getJSONObject(j);
											 }else {
												 HJsonData = new JSONObject(PoolExcelJson.get(j).toString());
											 }
											 String DEFXMLY = subStringNum(DEFXML,SOURCENOTENAMEY,l+1);//截取重表内容
											 Integer DOMCOUNTY = GetDomCount(PoolExcelJson, j);//下级数
											 AddElementLocalXml(HJsonData, PoolExcelJson, j, AppearMap, DEFXMLY,XMLDATA);
											 j++;
											 for(int domY =0; domY < DOMCOUNTY; domY++,j++) {
												 if(PoolExcelJson.get(j) instanceof Map) {
													 HJsonData = new JSONObject((HashMap)PoolExcelJson.get(j));
												 }else if(PoolExcelJson.get(j) instanceof JSONObject) {
													 HJsonData = PoolExcelJson.getJSONObject(j);
												 }else {
													 HJsonData = new JSONObject(PoolExcelJson.get(j).toString());
												 }
												 AddElementLocalXml(HJsonData, PoolExcelJson, j, AppearMap, DEFXMLY,XMLDATA);
											 }
											//由于循环J++ 最后一次循环J则不需要++
											 if(l+1 ==DEFNUMBERY) j--;
										 }
									 }else {
										 AddElementLocalXml(HJsonData, PoolExcelJson, j, AppearMap, DEFXML,XMLDATA);
									 }
								 }
								//由于循环J++ 最后一次循环J则不需要++
								 if(k+1 ==DEFNUMBER)j--;
							 }
							 LogUtil.printLog("重表逻辑执行结束", Level.INFO);
						}else {
							 AddElementLocalXml(HJsonData, PoolExcelJson, j, AppearMap, XMLDATA,XMLDATA);
						}
						if(SysUtility.isNotEmpty(root)) {
							String createSourcePath ="";
							String SuccessPath = "";
							Document Doc  = new Document(root);
							XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
							String path =ConfigData.getString("TARGETFILEPATH");
							String BACKPATH =  ConfigData.getString("BACKPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
							String Successpath =  ConfigData.getString("SUCCESS_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
							String SOURCEBACKPATH =  ConfigData.getString("SOURCE_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
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
							Util.AddMessLog(DataAccess, logMap);
						}else {
							throw new RuntimeException("根节点错误  请检查配置信息");
						}
					}
				} catch (RuntimeException e){//文件处理层人工抛出
					String ERRORPATH =  ConfigData.getString("ERRORPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
					fileHandle(ERRORPATH);
					LogUtil.printLog(e.getMessage(), Level.ERROR);
					FileUtility.copyFile(files[i].getPath(), ERRORPATH+File.separator+files[i].getName());
					FileUtility.deleteFile(files[i].getPath());
					LogUtil.printLog("文件转换失败："+files[i].getPath()+"请检查"+ConfigData.getString("CONFIGNAME")+"Sheet相关配置！", Level.WARN);
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
    				logMap.put("FILE_PATH", files[i].getPath());
    				logMap.put("TRANSFORMATION_CODE", 3);
    				logMap.put("TRANSFORMATION_NAME", "失败");
    				logMap.put("TRANSFORMATION_TIME", "1");
    				logMap.put("PROCESS_MSG", e.getMessage());
					Util.AddMessLog(DataAccess, logMap);
				} catch (Exception e) {//文件处理层系统异常
					String ERRORPATH =  ConfigData.getString("ERRORPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
					fileHandle(ERRORPATH);
					LogUtil.printLog(e.getMessage(), Level.ERROR);
					FileUtility.copyFile(files[i].getPath(), ERRORPATH+File.separator+files[i].getName());
					FileUtility.deleteFile(files[i].getPath());
					LogUtil.printLog("文件转换失败："+files[i].getPath()+"请检查"+ConfigData.getString("CONFIGNAME")+"Sheet相关配置！", Level.WARN);
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
    				logMap.put("FILE_PATH", files[i].getPath());
    				logMap.put("TRANSFORMATION_CODE", 3);
    				logMap.put("TRANSFORMATION_NAME", "失败");
    				logMap.put("TRANSFORMATION_TIME", "1");
    				logMap.put("PROCESS_MSG", e.getMessage());
					Util.AddMessLog(DataAccess, logMap);
				}
			}
		} catch (RuntimeException e){//人工抛出
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		} catch (Exception e) {//系统异常
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
	}
	
	public static void xmltoxmlLocal(JSONObject ConfigData,File file) throws JSONException {
		
		if(!file.getName().substring(file.getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){//只处理XML文件 
			return;
		}
		long startTime=System.currentTimeMillis();
		try {
			initRoot();
			HashMap<String, JSONArray> LocalExcelCacheMap = ExcelCache.getExcelSheetToMap();
			JSONArray LocalExcelJson = null;
			if(SysUtility.isEmpty(LocalExcelCacheMap)){
				LocalExcelCacheMap = new HashMap<String, JSONArray>();
				JSONArray SheetJson = ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigData.getString("EXCELPATH"), ConfigData.getString("CONFIGNAME"));
				LocalExcelCacheMap.put(ConfigData.getString("CONFIGNAME"),SheetJson);
				ExcelCache.setExcelSheetToMap(LocalExcelCacheMap);
				LocalExcelJson =LocalExcelCacheMap.get(ConfigData.getString("CONFIGNAME"));
			}else {
				LocalExcelJson = LocalExcelCacheMap.get(ConfigData.getString("CONFIGNAME"));
				if(SysUtility.isEmpty(LocalExcelJson)) {
					LocalExcelCacheMap.put(ConfigData.getString("CONFIGNAME"), ExcelToJsonArrayUtil.ExcelToJsonArray(ConfigData.getString("EXCELPATH"), ConfigData.getString("CONFIGNAME")));
	 				Message_TransferSier.setExcelSheetToMap(LocalExcelCacheMap);
	 				LocalExcelJson =LocalExcelCacheMap.get(ConfigData.getString("CONFIGNAME"));
				}
			}
			String XMLDATA  = FileUtility.readFile(file, false, "UTF-8");
			XMLDATA = XMLDATA.replaceAll("&amp;", "&");
			XMLDATA = XMLDATA.replaceAll("&", "&amp;");
			Map<String, Integer> AppearMap = new LinkedHashMap<String, Integer>();
			for(int j=0;j<LocalExcelJson.length();j++){
				JSONObject HJsonData = null;
				if(LocalExcelJson.get(j) instanceof Map) {
					HJsonData = new JSONObject((HashMap)LocalExcelJson.get(j));
				}else if(LocalExcelJson.get(j) instanceof JSONObject) {
					HJsonData = LocalExcelJson.getJSONObject(j);
				}else {
					HJsonData = new JSONObject(LocalExcelJson.get(j).toString());
				}
				
				if(!HJsonData.isNull("SOURCENOTENAME")) {
					 Integer cout = AppearMap.get(HJsonData.getString("SOURCENOTENAME"));
					 if(SysUtility.isEmpty(cout)){
						 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
					 }else{
						 AppearMap.put(HJsonData.getString("SOURCENOTENAME"), cout+1);
					 }
				}
				
				if(HJsonData.isNull("TARGETFILEFLOOR") || SysUtility.isEmpty(HJsonData.get("TARGETFILEFLOOR"))) {
					continue;
				}
				if(HJsonData.isNull("SEQ") || SysUtility.isEmpty(HJsonData.getString("SEQ")) || "".equals(HJsonData.getString("SEQ")) || !((j+1)+"").equals(HJsonData.getString("SEQ"))){
					 throw new RuntimeException(ConfigData.getString("CONFIGNAME")+"SEQ序号错误");
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
						 if(LocalExcelJson.get(j) instanceof Map) {
							HJsonData = new JSONObject((HashMap)LocalExcelJson.get(j));
						 }else if(LocalExcelJson.get(j) instanceof JSONObject) {
							HJsonData = LocalExcelJson.getJSONObject(j);
						 }else {
							HJsonData = new JSONObject(LocalExcelJson.get(j).toString());
						 }
						 //截取重表XML内容
						 String DefXML = subStringNum(XMLDATA,SOURCENOTENAME,k+1);
						//创建XML
						 AddElementLocalXml(HJsonData, LocalExcelJson, j, AppearMap, DefXML,XMLDATA);
						 Integer DomCount = GetDomCount(LocalExcelJson, j);//下级
						 j++;
						 for(int dom =0; dom < DomCount; dom++,j++) {
							 if(LocalExcelJson.get(j) instanceof Map) {
								 HJsonData = new JSONObject((HashMap)LocalExcelJson.get(j));
							 }else if(LocalExcelJson.get(j) instanceof JSONObject) {
								 HJsonData = LocalExcelJson.getJSONObject(j);
							 }else {
								 HJsonData = new JSONObject(LocalExcelJson.get(j).toString());
							 }
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
									 HJsonData = new JSONObject((HashMap)LocalExcelJson.get(j));
									 //截取重表XML内容
									 String DefXMLY = subStringNum(DefXML,SOURCENOTENAMEY,l+1);
									 Integer DomCountY = GetDomCount(LocalExcelJson, j);//下级
									 AddElementLocalXml(HJsonData, LocalExcelJson, j, AppearMap, DefXML,XMLDATA);
									 j++;
									 for(int domY =0; domY < DomCountY; domY++,j++) {
										 if(LocalExcelJson.get(j) instanceof Map) {
											 HJsonData = new JSONObject((HashMap)LocalExcelJson.get(j));
										 }else if(LocalExcelJson.get(j) instanceof JSONObject) {
											 HJsonData = LocalExcelJson.getJSONObject(j);
										 }else {
										 	 HJsonData = new JSONObject(LocalExcelJson.get(j).toString());
										 }
										 AddElementLocalXml(HJsonData, LocalExcelJson, j, AppearMap, DefXMLY,XMLDATA);
									 }
									//由于循环J++ 最后一次循环J则不需要++
									 if(l+1 ==DefNumberY) j--;
								 }
							 }else {
								 AddElementLocalXml(HJsonData, LocalExcelJson, j, AppearMap, DefXML,XMLDATA);
							 }
						 }
						//由于循环J++ 最后一次循环J则不需要++
						 if(k+1 ==DefNumber)j--;
					 }
				 }else {
					 LogUtil.printLog("添加Dom节点", Level.INFO);
					 if(SysUtility.isEmpty(XMLDATA)) {
						 InputStream in = new FileInputStream(file);
						 byte[] bytes = SysUtility.InputStreamToByte(in);
						 XMLDATA = new String(bytes,"UTF-8");
						 XMLDATA = XMLDATA.replaceAll("&amp;", "&");
						 XMLDATA = XMLDATA.replaceAll("&", "&amp;");
						 if(in != null) {
							 in.close();
						 }
					 }
					 AddElementLocalXml(HJsonData, LocalExcelJson, j, AppearMap, XMLDATA,XMLDATA);
				 }
			}
			if(SysUtility.isNotEmpty(root)) {
				Random random = new Random();
				//目录负载均衡
				String Folder = ConfigData.getString("TARGETFILEPATH");
				if(!ConfigData.isNull("SUBDIRECTORY")) {
					String SUBDIRECTORY = ConfigData.getString("SUBDIRECTORY");
					if(!SysUtility.isEmpty(SUBDIRECTORY)) {
						SUBDIRECTORY = SUBDIRECTORY.replace("，", ",");
						String[] split = SUBDIRECTORY.split(",");
						SUBDIRECTORY = split[random.nextInt(split.length)];
						if(!SysUtility.isEmpty(SUBDIRECTORY)) {
							Folder = Folder + File.separator + SUBDIRECTORY;
							fileHandle(Folder);
						}
					}
				}
				Folder +=File.separator + file.getName();
				Document Doc  = new Document(root);
				XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
				fileHandle(ConfigData.getString("TARGETFILEPATH"));
				String BACK_PATH = ConfigData.getString("BACKPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
				fileHandle(BACK_PATH);
				String renameFileNameXmlToExs = FileUtility.renameFileNameXmlToExs(Folder);
				FileOutputStream fileOutputStream = new FileOutputStream(renameFileNameXmlToExs);
				XMLOut.output(Doc, fileOutputStream);  
				if(fileOutputStream != null) {
					fileOutputStream.close();
				}
				if(Doc != null) {
					Doc.clone();
				}
				FileUtility.copyFile(file.getPath(), BACK_PATH+File.separator+file.getName());
				FileUtility.deleteFile(file.getPath());
				if(!FileUtility.renameFileExsToXml(renameFileNameXmlToExs)) {
					FileUtility.copyFile(renameFileNameXmlToExs,Folder);
				}
				long endTime=System.currentTimeMillis();
				LogUtil.printLog("文件转换成功："+Folder+"运行时长:"+(endTime-startTime)+"毫秒", Level.WARN);
			}else {
				LogUtil.printLog("文件转换失败：节点为空", Level.WARN);
				FileUtility.deleteFile(file.getPath());
			}
		} catch (Exception e) {//转换过程中错误 处理
			String ERR_PATH = ConfigData.getString("ERRORPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
			fileHandle(ERR_PATH);
			FileUtility.copyFile(file.getPath(), ERR_PATH+File.separator+file.getName());
			FileUtility.deleteFile(file.getPath());
			LogUtil.printLog("文件转换失败:"+e.getMessage(), Level.WARN);
		}
	}
	
	//本地版XML转换XMLCore
	@SuppressWarnings({ "rawtypes", "unused" })
	synchronized public static void XmlToXmlForLocal(JSONObject ConfigData) {
		try {
			int inProcessCount = 0;//正在处理的报文数量
			if(SysUtility.isEmpty(ConfigData.getString("SOURCEFILEPATH"))){
				throw new RuntimeException("配置信息源目录为空!");
			}
			File files[] = new File(ConfigData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
			if(SysUtility.isEmpty(files) || files.length<=0) {
				fileHandle(ConfigData.getString("SOURCEFILEPATH"));
				return;
			}
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()){
					inProcessCount++;
					if(inProcessCount > singleProcessCount){
						return;
					}
					xmltoxmlLocal(ConfigData,files[i]);
				}else {
					String folderName = files[i].getName();
					if("Temps".equals(folderName) ||"Backup".equals(folderName) || "Error".equals(folderName)){
						continue;
					}
					
					File files2[] = new File(ConfigData.getString("SOURCEFILEPATH")+File.separator+folderName).listFiles(new FileFilterHandle());
					for (int k = 0; k < files2.length; k++) {
						if (!files2[k].isDirectory()){
							inProcessCount++;
							if(inProcessCount > singleProcessCount){
								return;
							}
							xmltoxmlLocal(ConfigData,files2[k]);
						}
						
					}
					
				}
				
			}
		}catch (RuntimeException e) {
			LogUtil.printLog(e.getMessage(),Level.ERROR);
		}catch (Exception e) {
			LogUtil.printLog(e.getMessage(),Level.ERROR);
		}finally {
			
		}
		
	}
	//ExcelToXml数据库版(目前支持Oracle)
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public static void ExcelToXmlForPool(JSONObject ConfigData,IDataAccess DataAccess) {
		try {
			if(SysUtility.isEmpty(ConfigData.getString("SOURCEFILEPATH"))){
				throw new RuntimeException("配置信息源目录为空!");
			}
			File files[] = new File(ConfigData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
			if(SysUtility.isEmpty(files) || files.length<=0) {
				return;
			}
			for (int i = 0; i < files.length; i++) {
				if(i > singleProcessCount) {//预防内存爆炸 处理单次处理singleProcessCount 内存要求：4G以上
					return;
				}
				if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XLS") && !files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XLSX")){//只处理XLS文件 
					continue;
				}
				try {
					long startTime=System.currentTimeMillis();   //获取开始时间
					Map ExcelMap = new HashMap();
					Map ExcelSheetMap = new HashMap();
					Map LogMap = new HashMap();
					Map MainMap = null;
					final int Indx = ConfigData.getInt("INDX");
					//读取sheet页
					List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_name WHERE P_INDX=? ORDER BY to_number(exs_convert_config_name.SEQ)",new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setInt(1, Indx);
						}
					});
					if(query4List.size()<=0){
						throw new RuntimeException("配置信息:映射配置信息不可为空");
					}
					JSONArray excelToJsonArray = getJSONArrayByList(query4List);
					//获取主表信息
					List MainList = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_name WHERE P_INDX=? AND ISSUBLIST='1'  ORDER BY to_number(exs_convert_config_name.SEQ)",new Callback() {
						
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setInt(1, Indx);
						}
					});
					if(MainList.size()==0) {
						throw new  RuntimeException("配置信息:主表不可为空");
					}else if(MainList.size()!=1) {
						throw new  RuntimeException("配置信息:主表只能为1个");
					}
					ConfigData.put("SOURCEFILEPATHNAME", files[i].getPath());
					if(MainList.get(0) instanceof Map) {
						MainMap = (HashMap) MainList.get(0);
					}else {
						LogUtil.printLog("SQLExecUtils.query4List 方法映射不为Map 请及时更新代码!", Level.ERROR);
						throw new  RuntimeException("系统故障:系统故障");
					}
					if(SysUtility.isEmpty(MainMap.get("SOURCENOTENAME"))) {
						throw new  RuntimeException("配置信息:主表源节点配置不可为空 请配置相对于的Sheet页");
					}
					JSONArray MainExcelDATA = ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), (String)MainMap.get("SOURCENOTENAME"));//读取主表
					if(SysUtility.isEmpty(MainExcelDATA)) {
						throw new  RuntimeException("Excel文件:"+MainMap.get("SOURCENOTENAME")+"名称不存在或者为空(注意大小写)");
					}
					ExcelMap.put(MainMap.get("SOURCENOTENAME"), MainExcelDATA);
					for(int j = 0;j < MainExcelDATA.length(); j++) {//循环主表数据
						initRoot();
						
						JSONObject MainExcelJsonData = null;
						if(MainExcelDATA.get(j) instanceof JSONObject) {
							MainExcelJsonData = MainExcelDATA.getJSONObject(j);
						}else if(MainExcelDATA.get(j) instanceof Map) {
							MainExcelJsonData = new JSONObject(MainExcelDATA.get(j).toString());
						}else {
							MainExcelJsonData = new JSONObject(MainExcelDATA.get(j).toString());
						}
						ExcelSheetMap.put(MainMap.get("SOURCENOTENAME"), MainExcelJsonData);
						for(Integer k =0; k < excelToJsonArray.length(); k++) {//循环配置表数据
							int thisrepeatNum =GetThisDomCount(excelToJsonArray, k);//本级
							int repeatNum = GetDomCount(excelToJsonArray, k);//下级
							if(!new JSONObject(excelToJsonArray.get(k).toString()).isNull("ISSUBLIST") && new JSONObject(excelToJsonArray.get(k).toString()).getString("ISSUBLIST").equals("2")) {
								if(new JSONObject(excelToJsonArray.get(k).toString()).isNull("SOURCENOTENAME")) {
									throw new  RuntimeException("配置信息:重节点源节点不可为空");
								}
								String SOURCENOTENAME = new JSONObject(excelToJsonArray.get(k).toString()).getString("SOURCENOTENAME");
								int SEQ = new JSONObject(excelToJsonArray.get(k).toString()).getInt("SEQ");
								k = (SEQ-1);
								String[] split = SOURCENOTENAME.split("&");
								if(split.length==1) {
									throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAME+"配置信息有误!");
								}
								String[] split2 = split[1].split("\\|");
								if(split2.length==1) {
									throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAME+"配置信息有误!");
								}
								String name1 = split2[0];
								String name2 = split2[1];
								String[] split3 = name1.split("\\.");
								if(split3.length==1) {
									throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAME+"配置信息有误!");
								}
								String sheetname1 = split3[0];//sheet名称
								String fieid = split3[1];//对应字段
								JSONArray Excel2DATA = ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), split[0]);//重表	
								if(Excel2DATA.length()==0) {
									throw new  RuntimeException("Excel文件:"+split[0]+"名称不存在或者为空(注意大小写)");
								}
								JSONArray getSonData = GetSonData(Excel2DATA,((JSONObject)ExcelSheetMap.get(sheetname1)).getString(fieid.toUpperCase()), name2);
								ExcelMap.put(split[0], getSonData);
								for(int i1=0;i1<getSonData.length();i1++) {
									k = (SEQ-1);
									ExcelSheetMap.put(split[0], getSonData.getJSONObject(i1));
									AddExcelElement(new JSONObject(excelToJsonArray.get(k).toString()), Excel2DATA.getJSONObject(i1));
									k++;
								}
							}
						}
					}
					
				}catch (RuntimeException e) {//文件处理层
					LogUtil.printLog(e.getMessage(),Level.ERROR);
				}catch (Exception e) {//文件处理层
					LogUtil.printLog(e.getMessage(),Level.ERROR);
				}finally {
					
				}
			}
		}catch (RuntimeException e) {
			LogUtil.printLog(e.getMessage(),Level.ERROR);
		}catch (Exception e) {
			LogUtil.printLog(e.getMessage(),Level.ERROR);
		}finally {
			
		}
		
	}
	//ExcelToXml本地版(目前支持Oracle)
	public static void ExcelToXmlForLocal(JSONObject ConfigData) {
		try {
			
		}catch (RuntimeException e) {
			
		}catch (Exception e) {
			
		}finally {
			
		}
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
	
	
	@SuppressWarnings("rawtypes")
	public static void AddElementLocalXml(JSONObject HJsonData,JSONArray excelToJsonArray,int i,Map<String, Integer> AppearMap,String xmlToString,String XMLDATA) throws JSONException{
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
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
					 JSONObject nextjsonObject = null;
					 if(excelToJsonArray.get(i+1) instanceof Map) {
						 nextjsonObject =  new JSONObject((HashMap)excelToJsonArray.get(i+1)); 
					 }else if(excelToJsonArray.get(i+1) instanceof JSONObject) {
						 nextjsonObject = excelToJsonArray.getJSONObject(i+1);
					 }else {
						 nextjsonObject = new JSONObject(excelToJsonArray.get(i+1).toString());
					 }
					 NextTARGETFILEFLOOR =  nextjsonObject.getString("TARGETFILEFLOOR");
				 }
				 value = init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = isJsonFunctionValue(HJsonData,value, XMLDATA);
				 element20.addContent(new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }
		}
	 
		 private static void AddExcelElement(JSONObject HJsonData,JSONObject ExcelData) throws JSONException {
			 if("0".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& SysUtility.isEmpty(root)){
				 root = new Element(HJsonData.getString("TARGETCOLNAME")); 
			 }else if("1".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 root.addContent(element1 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("2".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element1.addContent(element2 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("3".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element2.addContent(element3 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("4".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element3.addContent(element4 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("5".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element4.addContent(element5 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("6".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element5.addContent(element6 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("7".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element6.addContent(element7 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("8".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element7.addContent(element8 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("9".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element8.addContent(element9 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }else if("10".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& !SysUtility.isEmpty(root)){
				 element9.addContent(element0 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(GetValue(ExcelData, HJsonData)));
			 }  
		 }
	 
	 
	    /**
		 * <x></x> 寻找X节点 的VALUE值
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
		 
		 //Excel函数库
		 public static String isJsonFunctionValueExcel(JSONObject HJsonData,String value) throws JSONException {
			 try {
				 if(!HJsonData.isNull("DEFVALUE") && !SysUtility.isEmpty(HJsonData.get("DEFVALUE"))) {
					 value = HJsonData.getString("DEFVALUE").trim();
				 }
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
							 CacheJson.put(HJsonData.getString("TARGETCOLNAME"), uuid);
							 value=uuid;
						 }else/* if(FunctionSplit[0].toUpperCase().substring(0,3).equals("GET")) {
							 value = 
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
						 }else*/{
							 try {
								 value = CacheJson.getString(FunctionSplit[0].substring(0,FunctionSplit[0].toUpperCase().indexOf("(")));
							 } catch (Exception e) {
								 LogUtil.printLog(HJsonData.getString("Seq")+"函数执行失败", Level.ERROR);
								 return value;
							 }
						 }
					 }
				 }
				 
				 if(!HJsonData.isNull("MAPPING") && SysUtility.isNotEmpty(HJsonData.getString("MAPPING"))){
					 LogUtil.printLog("执行Mapping映射", Level.INFO);
					 if(!HJsonData.isNull("MAPPING") && XmlToXmlUtil.isGoodJson(HJsonData.getString("MAPPING"))) {
						 JSONObject MapPingJson = new JSONObject(HJsonData.getString("MAPPING"));
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
		 //XML函数库
		 @SuppressWarnings({ "rawtypes", "unchecked" })
		public static String isJsonFunctionValue(JSONObject HJsonData,String value ,String XMLDATA) throws JSONException {
			 try {
				 if(value != null) {
					 value = value.replaceAll("\r","");
					 value = value.replaceAll("\n","");	
					 value = value.trim();
				 }
				
				 
				 if(!HJsonData.isNull("FUNCTION") && SysUtility.isNotEmpty(HJsonData.getString("FUNCTION"))) {
					 LogUtil.printLog("执行Function函数", Level.INFO);
					 String[] FunctionSplit = HJsonData.getString("FUNCTION").split(";");
					 if(FunctionSplit.length>0) {
						 if(FunctionSplit[0].toUpperCase().equals("GETUUID()")) {
							 value = SysUtility.GetUUID();
						 }else if(FunctionSplit[0].toUpperCase().equals("GETTIME()")) {
							 value = SysUtility.getSysDate();
						 }else if(FunctionSplit[0].toUpperCase().equals("SETUUID()")) {
							 String uuid = SysUtility.GetUUID();
							 CacheJson.put(HJsonData.getString("TARGETCOLNAME"), uuid);
							 value=uuid;
						 }else if(FunctionSplit[0].toUpperCase().substring(0,7).equals("SETTIME")) {
							 //时间格式转换
							 String[] Vl = FunctionSplit[0].split("\\(");
							 if(Vl.length>1) {
								 String[] vkey = Vl[1].split("\\)");
								 String key = vkey[0];//具体参数
								 key = key.replace("\"", "").replace("'", "").trim();//处理结束
								 String[] DateV = key.split(",");
								 if(DateV.length<1) {
									 LogUtil.printLog(HJsonData.get("SEQ")+"Function Err:SetTime有误！", Level.ERROR);
								 }else {
									 Date date = DateUtils.parseDate(value, DateV[0]);
									 String now = DateFormatUtils.format(date,DateV[1]);
									 value = now;
								 }
							 }
						 }else if(FunctionSplit[0].toUpperCase().substring(0,3).equals("GET")) {
							 value = FindTheLocation(XMLDATA, 1, FunctionSplit[0].substring(3,FunctionSplit[0].toUpperCase().indexOf("(")), null);
						 }else if(FunctionSplit[0].toUpperCase().split("\\(")[0].equals("ISNULL")){
							 if(SysUtility.isEmpty(value) || value.equals("\r\n") || value.equals("\n") ) {
								 String str = FindValue(FunctionSplit[0]);
								 if(SysUtility.isNotEmpty(str)) {
									 String[] StrSplit = str.split(",");
									 for(int i =0;i<StrSplit.length;i++) {
										 value = FindTheLocation(XMLDATA, 1, StrSplit[i].replaceAll(" ", "").replaceAll("\r\n", "").replaceAll("\r", ""), null);
										 if(SysUtility.isNotEmpty(value)) {
											 break;
										 }
									 }
									 
								 } 
							 }
						 }else{
							 try {
								 value = CacheJson.getString(FunctionSplit[0].substring(0,FunctionSplit[0].toUpperCase().indexOf("(")));
							 } catch (Exception e) {
								 LogUtil.printLog(HJsonData.get("SEQ")+"函数执行失败", Level.ERROR);
								 return value;
							 }
						 }
					 }
				 }
				 if(!HJsonData.isNull("MAPPING") && SysUtility.isNotEmpty(HJsonData.getString("MAPPING"))){
					 LogUtil.printLog("执行Mapping映射", Level.INFO);
					 if(SysUtility.isNotEmpty(value)) {
						 if(HJsonData.isNull("MAPPING")) {
							 return value;
						 }
						 String s =  HJsonData.getString("MAPPING");
						 if(s != null && s.length()>= 1) {
								if(s.substring(0,1).equals("{") && s.substring(s.length()-1,s.length()).equals("}")) {
									s = s.substring(1,s.length()-1);
								}
								String[] sSplit = s.split(",");
								HashMap mappingMap = new HashMap();
								for (String mapping : sSplit) {
									if(mapping != null) {
										String[] MapSplit = mapping.split(":");
										if(MapSplit.length == 2) {
											mappingMap.put(MapSplit[0], MapSplit[1]);
										}
									}
								}
								JSONObject MapPingJson = new JSONObject(mappingMap);
								value = MapPingJson.getString(value);
						 }
					 }
				 }
				 
				 
			 }catch (Exception e) {
				 return value;
			 }
			 
			 return value;
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
		 /**
		  * 验证JSON格式
		  */
		 public static boolean isGoodJson(String json) {    
			   try {    
			       new JSONObject(json);
			       return true;    
			   } catch (Exception e) {    
			       return false;    
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
		 
		 
		//获取下级
		@SuppressWarnings("rawtypes")
		public static Integer GetDomCount(JSONArray XmlJsonArr,Integer number) throws JSONException {
			Integer DomCount = 0;
			Integer JsNumber = number;
			Integer ThisJb = 0;
			if(XmlJsonArr.length()>number) {
				JSONObject jsonObject = null;
				if(XmlJsonArr.get(number) instanceof JSONObject) {
					jsonObject = XmlJsonArr.getJSONObject(number);
				}else if(XmlJsonArr.get(number) instanceof Map) {
					jsonObject = new JSONObject(((Map)XmlJsonArr.get(number)));
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
						
						jsonObject = new JSONObject(((Map)XmlJsonArr.get(i)));
					}
					if(XmlJsonArr.get(i+1) instanceof JSONObject) {
						jsonObject = XmlJsonArr.getJSONObject(i+1);
					}else if(XmlJsonArr.get(i+1) instanceof Map) {
						jsonObject = new JSONObject(((Map)XmlJsonArr.get(i+1)));
					}
					if(jsonObject.isNull("TARGETFILEFLOOR") || SysUtility.isEmpty(jsonObject.get("TARGETFILEFLOOR"))) {
						DomCount++;
						continue;
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
		
		
		public static Format FormatXML(){  
	        //格式化生成的xml文件，如果不进行格式化的话，生成的xml文件将会是很长的一行...  
	        Format format = Format.getCompactFormat();  
	        format.setEncoding("utf-8");  
	        format.setIndent("   ");  
	        return format;  
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
		
		/**
	     * 根据List获取到对应的JSONArray
	     * @param list
	     * @return
	     */
	    public static JSONArray getJSONArrayByList(List<?> list){
	        JSONArray jsonArray = new JSONArray();
	        if (list==null ||list.isEmpty()) {
	            return jsonArray;//nerver return null
	        }

	        for (Object object : list) {
	            jsonArray.put(object);
	        }
	        return jsonArray;
	    }
	    
	    
	    /**
		  * 
		  * @param SonArray 配置信息数据
		  * @param no1data  对比的字段数据（主表）
		  * @param body		负表数据
		  * @return
		  * @throws JSONException
		  */
		public static JSONArray GetSonData(JSONArray SonArray,Object no1data,String body) throws JSONException {
			if(SysUtility.isEmpty(SonArray)) {
					throw new  RuntimeException("Excel文件:读取子表数据失败 请检查源字段配置是否正确(区分大小写)");
			}
			JSONArray ReturnSonArr = new JSONArray();
			for(int i=0; i< SonArray.length(); i++) {
				JSONObject SonObj = SonArray.getJSONObject(i); 
				if(SonObj.getString(body.toUpperCase()).equals(no1data)) {
					ReturnSonArr.put(SonObj);
				}
			}
			return ReturnSonArr;
		}
		
		
		public static String GetValue(JSONObject ExcelData,JSONObject HJsonData){
			 String value =null;
			 try {
					 if(SysUtility.isNotEmpty(ExcelData.getString(HJsonData.getString("SOURCENOTENAME").toUpperCase()))){
						 value = ExcelData.getString(HJsonData.getString("SOURCENOTENAME").toUpperCase()) ;
					 }
					 if(!HJsonData.isNull("DEFVALUE") && SysUtility.isNotEmpty(HJsonData.get("DEFVALUE"))) {
						 value = HJsonData.getString("DEFVALUE");
					 }
					 value = isJsonFunctionValueExcel(HJsonData, value);
				 } catch (Exception e) {
					 try {
						value = isJsonFunctionValueExcel(HJsonData, value);
					} catch (JSONException e1) {
						value =null;
					}
					 
				 }
			 return value;
		 }
		
		private static Integer GetThisDomCount(JSONArray ExcelJsonArr,Integer number) throws JSONException {
			Integer DomCount = 1;
			Integer JsNumber = number;
			for(int i=JsNumber;i<ExcelJsonArr.length();i++) {
				if(i+1 < ExcelJsonArr.length()) {
					JSONObject jsonObject = null;
					if(ExcelJsonArr.get(i) instanceof JSONObject) {
						jsonObject = ExcelJsonArr.getJSONObject(i);
					}else if(ExcelJsonArr.get(i) instanceof Map) {
						jsonObject = new JSONObject(ExcelJsonArr.get(i).toString());
					}
					
					Integer TARGETFILEFLOOR = jsonObject.getInt("TARGETFILEFLOOR");
					if(ExcelJsonArr.get(i+1) instanceof JSONObject) {
						jsonObject = ExcelJsonArr.getJSONObject(i+1);
					}else if(ExcelJsonArr.get(i+1) instanceof Map) {
						jsonObject = new JSONObject(ExcelJsonArr.get(i+1).toString());
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
		
}
