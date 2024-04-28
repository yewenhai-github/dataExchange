package com.easy.convert.service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Level;
import org.jaxen.function.ext.EndsWithFunction;
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
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


public class ExcelToXmlUtil {
	
	private final static Integer FILECOUNT = 10000;//单个线程最大处理报文数
	static JSONObject JSON = new JSONObject();
	
	static {
		 
	}
	public static void ExcelToXmlTo(JSONObject ConfigPathData,IDataAccess access) throws Exception{
		if(ConfigPathData.isNull("SOURCEFILEPATH")){
			return;
		}
		File files[] = new File(ConfigPathData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
		if(SysUtility.isEmpty(files)) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if(i > FILECOUNT) {//预防内存爆炸 处理单次处理FILECOUNT 内存要求：4G以上
				return;
			}
			if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XLS")){
				//只处理XLS文件 
				continue;
			}
			Map LogMap = new HashMap();
			try {
				Map ExcelMap = new HashMap();
				Map ExcelSheetMap = new HashMap();
				long startTime=System.currentTimeMillis();   //获取开始时间
				final int Indx = ConfigPathData.getInt("INDX");
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
				JSONArray excelToJsonArray = MessageUtil.getJSONArrayByList(query4List);
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
				ConfigPathData.put("SOURCEFILEPATHNAME", files[i].getPath());
				HashMap  MainMap = null;
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
					LogUtil.printLog("正在处理"+j + "总 :"+MainExcelDATA.length() + "当前时间"+SysUtility.DataFormatStr(new Date()), Level.WARN);
					initRoot();
					JSONObject MainExcelJsonData = MainExcelDATA.getJSONObject(j);
					ExcelSheetMap.put(MainMap.get("SOURCENOTENAME"), MainExcelJsonData);
					for(Integer k =0; k < excelToJsonArray.length(); k++) {//循环配置表数据
						int thisrepeatNum =GetThisDomCount(excelToJsonArray, k);//本级
						int repeatNum = GetDomCount(excelToJsonArray, k);//下级
						if(thisrepeatNum==1) {
//							System.err.println("我是单标签"+new JSONObject(excelToJsonArray.get(k).toString()).getString("TARGETCOLNAME"));
						}
						if(repeatNum>0) {
							if(!new JSONObject((Map)excelToJsonArray.get(k)).isNull("ISSUBLIST") && new JSONObject((Map)excelToJsonArray.get(k)).getString("ISSUBLIST").equals("2")) {
								if(new JSONObject((Map)excelToJsonArray.get(k)).isNull("SOURCENOTENAME")) {
									System.out.println(excelToJsonArray.get(k).toString());
									throw new  RuntimeException("配置信息:重节点源节点不可为空");
								}
								String SOURCENOTENAME = new JSONObject((Map)excelToJsonArray.get(k)).getString("SOURCENOTENAME");
								int SEQ = new JSONObject((Map)excelToJsonArray.get(k)).getInt("SEQ");
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
								
								if(getSonData.length() == 0) {
									AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), null);
									k = k+repeatNum;
									continue;
								}
								for(int i1=0;i1<getSonData.length();i1++) {
									k = (SEQ-1);
									ExcelSheetMap.put(split[0], getSonData.getJSONObject(i1));
										AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), Excel2DATA.getJSONObject(i1));
										k++;
										for(int j1=0;j1<repeatNum;j1++) {
											
											if(!new JSONObject((Map)excelToJsonArray.get(k)).isNull("ISSUBLIST") && new JSONObject((Map)excelToJsonArray.get(k)).getString("ISSUBLIST").equals("2")) {
												if(new JSONObject((Map)excelToJsonArray.get(k)).isNull("SOURCENOTENAME")) {
													throw new  RuntimeException("配置信息:重节点源节点不可为空");
												}
												
												String SOURCENOTENAMEY = new JSONObject((Map)excelToJsonArray.get(k)).getString("SOURCENOTENAME");
												int SEQY = new JSONObject((Map)excelToJsonArray.get(k)).getInt("SEQ");
												k = (SEQY-1);
												//Sheet节点名&sheet名称.对应字段|对应字段
												String[] splitY = SOURCENOTENAMEY.split("&");
												if(splitY.length==1) {
													throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAMEY+"配置信息有误!");
												}
												String[] split2Y = splitY[1].split("\\|");
												if(split2Y.length==1) {
													throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAMEY+"配置信息有误!");
												}
												String name1Y = split2Y[0];
												String name2Y = split2Y[1];
												String[] split3Y = name1Y.split("\\.");
												if(split3.length==1) {
													throw new  RuntimeException("配置信息:重节点"+SOURCENOTENAMEY+"配置信息有误!");
												}
												String sheetname1Y = split3Y[0];//sheet名称
												String fieidY = split3Y[1];//对应字段
												JSONArray Excel2DATAY = ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), splitY[0]);//二级重表	
												if(Excel2DATA.length()==0) {
													throw new  RuntimeException("Excel文件:"+splitY[0]+"名称不存在或者为空(注意大小写)");
												}
												JSONArray getSonDataY = GetSonData(Excel2DATAY,((JSONObject)ExcelSheetMap.get(sheetname1Y)).getString(fieidY.toUpperCase()), name2Y);
												ExcelMap.put(splitY[0], getSonDataY);
												
												for(int i2=0;i2<getSonDataY.length();i2++) {
													k = (SEQY-1);
													int xny = (k+1);
													int thisrepeatNumY =GetThisDomCount(excelToJsonArray, xny);//本级
													int repeatNumY =GetDomCount(excelToJsonArray, k);//下级
													/*if(thisrepeatNumY == repeatNumY) {*/
														AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), Excel2DATAY.getJSONObject(i2));
														k++;
														for(int j2=0;j2<repeatNumY;j2++) {
															AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), Excel2DATAY.getJSONObject(i2));
															k++;
														}
													/*}else {
														AddXmlElement(new JSONObject(excelToJsonArray.get(k).toString()), Excel2DATAY.getJSONObject(i2));
														k++;
														if(repeatNumY ==0 && i2+1 == getSonDataY.length()) {
															int seqs = new JSONObject(excelToJsonArray.get(k).toString()).getInt("SEQ");
															int seq = SEQ;
															k = k-(seqs-seq);
														}
														for(int j2=0;j2<repeatNumY;j2++) {
															AddXmlElement(new JSONObject(excelToJsonArray.get(k).toString()), Excel2DATAY.getJSONObject(i2));
															k++;
															if(i2+1 ==getSonDataY.length()) {
																k = k-getSonDataY.length();
															}else if(j2+1 == repeatNumY) {
																k = k-repeatNumY-1;
															}
														}
													}*/
												}
												
												
												
												
											}else {
												AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), Excel2DATA.getJSONObject(i1));
												k++;
												if(j1+1 == repeatNum) {
													k--;
												}
											}
										}
									
								}
								
							}else if(!new JSONObject((Map)excelToJsonArray.get(k)).isNull("ISSUBLIST") && new JSONObject((Map)excelToJsonArray.get(k)).getString("ISSUBLIST").equals("3")){//表头
								if(new JSONObject(excelToJsonArray.get(k).toString()).isNull("SOURCENOTENAME")) {
									throw new  RuntimeException("配置信息:表头源节点不可为空");
								}
								String SOURCENOTENAME = new JSONObject((Map)excelToJsonArray.get(k)).getString("SOURCENOTENAME");
								JSONArray Excel2DATA = ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), SOURCENOTENAME);//重表
								if(Excel2DATA.length()==0) {
									throw new  RuntimeException("Excel文件:"+SOURCENOTENAME+"名称不存在或者为空(注意大小写)");
								}
								AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), Excel2DATA.getJSONObject(0));
								k++;
								thisrepeatNum =GetThisDomCount(excelToJsonArray, k);//本级
								for(int n=0;n<thisrepeatNum;n++,k++) {
									AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), Excel2DATA.getJSONObject(0));
								}
							}else{
								AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), MainExcelJsonData);
							}
							
							
						}else {
							AddXmlElement(new JSONObject((Map)excelToJsonArray.get(k)), MainExcelJsonData);
						}
						
						
					}
					if(SysUtility.isNotEmpty(root)) {
//						Random random = new Random();
//						//目录负载均衡
						String Folder = ConfigPathData.getString("TARGETFILEPATH");
//						String SUBDIRECTORY = "auto1,auto2,auto3,auto4,auto5,auto6,auto7,auto8,auto9,auto10,auto11,auto12,auto13,auto14,auto15,auto16,auto17,auto18,auto19,auto20";
//						if(!SysUtility.isEmpty(SUBDIRECTORY)) {
//							SUBDIRECTORY = SUBDIRECTORY.replace("，", ",");
//							String[] split = SUBDIRECTORY.split(",");
//							SUBDIRECTORY = split[random.nextInt(split.length)];
//							if(!SysUtility.isEmpty(SUBDIRECTORY)) {
//								Folder = Folder + File.separator + SUBDIRECTORY;
//								fileHandle(Folder);
//							}
//						}
						Folder =Folder + File.separator;
	 					String createSourcePath ="";
 	 					Document Doc  = new Document(root);
		 				XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
		 				String SuccessPath =  ConfigPathData.get("SUCCESS_BACK_PATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
		 				String BACKPATH =  ConfigPathData.get("BACKPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
		 				fileHandle(ConfigPathData.getString("TARGETFILEPATH"));
		 				fileHandle(BACKPATH);
						fileHandle(SuccessPath);
		 				String path =ConfigPathData.getString("TARGETFILEPATH");
		 				createSourcePath  = Folder+File.separator+MainExcelJsonData.getString("FILENAME")+"_"+j+".xml";
//		 				createSourcePath  = path+File.separator+MainExcelJsonData.getString("FILENAME")+"_"+j+".xml";
						SuccessPath = SuccessPath+File.separator+MainExcelJsonData.getString("FILENAME")+"_"+j+".xml";
//						String renameFileNameXmlToExs = FileUtility.renameFileNameXmlToExs(createSourcePath);
						String renameFileNameXmlToExs = FileUtility.renameFileNameXmlToExs(createSourcePath);
		 				FileOutputStream out = new FileOutputStream(renameFileNameXmlToExs);
						XMLOut.output(Doc, out);  
//		 				XMLOut.output(Doc, new FileOutputStream(Successpath = Successpath+File.separator+MainExcelJsonData.getString("FILENAME")+"_"+j+".xml"));  
						out.close();
						Doc.clone();
						XMLOut.clone();
						FileUtility.copyFile(renameFileNameXmlToExs, SuccessPath);
						if(!FileUtility.renameFileExsToXml(renameFileNameXmlToExs)) {
							FileUtility.copyFile(renameFileNameXmlToExs,createSourcePath);
//							FileUtility.copyFile(renameFileNameXmlToExs,createSourcePath);
						}
		 				LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName(), Level.WARN);
		 				String filepath = files[i].getPath().replace("\\", "/").replace("\\", "/");
			 			int d_ = filepath.lastIndexOf(".");
			 			int p_ = filepath.lastIndexOf("/")+1;
			 			String fileName = filepath.substring(p_,d_);
			 			String TargetName = new File(SuccessPath).getName();
						String[] fileSplit = files[i].getName().split("_");
						if(fileSplit.length<4) {
							continue;
						}
						String regNo =  fileSplit[0];
						String fileType = fileSplit[1];
						String fileDate = fileSplit[2];
						String no =fileSplit[3];
						
						LogMap.put("SOURCE_BACK_PATH",BACKPATH+File.separator+files[i].getName());
						LogMap.put("CONFIG_PATH_ID",Indx);
						LogMap.put("DATA_SOURCE", "Excel转换");
						LogMap.put("SERIAL_NO", regNo);
						LogMap.put("TARGET_FILE_NAME",files[i].getName());
						LogMap.put("FILE_PATH", createSourcePath);
						LogMap.put("SUCCESS_BACK_PATH", SuccessPath);
						LogMap.put("TRANSFORMATION_CODE", 2);
						LogMap.put("TRANSFORMATION_NAME", "成功");
						LogMap.put("TRANSFORMATION_TIME", true);
						LogMap.put("PROCESS_MSG", "转换成功");
						Util.AddMessLog(access,LogMap);
	 				}else {
	 					throw new  RuntimeException("根节点错误 请检查配置问题");
	 				}
				}
				String filepath = files[i].getPath().replace("\\", "/");
				int d_ = filepath.length();
				int p_ = filepath.lastIndexOf("/")+1;
				String fileName = filepath.substring(p_,d_);
				String BACKPATH = ConfigPathData.getString("BACKPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();
				SQLExecUtils.executeUpdate("DELETE exs_convert_log WHERE TARGET_FILE_NAME='"+fileName+"' AND PROGRESS_CODE = '0' AND DATA_SOURCE LIKE '%服务器%'");
				FileUtility.copyFile(files[i].getPath(), BACKPATH + File.separator+files[i].getName());
				FileUtility.deleteFile(files[i].getPath());
			}catch (Exception e) {
				String ERRORPATH = ConfigPathData.getString("ERRORPATH")+File.separator+SysUtility.getSysDateWithoutTime()+File.separator+SysUtility.getHourOfDay();;
				fileHandle(ERRORPATH);
	 			String filepath = files[i].getPath().replace("\\", "/").replace("\\", "/");
	 			int d_ = filepath.lastIndexOf(".");
	 			int p_ = filepath.lastIndexOf("/")+1;
	 			String fileName = filepath.substring(p_,d_);
	 			FileUtility.copyFile(files[i].getPath(), ERRORPATH+File.separator+fileName+".xls");
				FileUtility.deleteFile(files[i].getPath());
 				LogUtil.printLog("文件转换失败："+ERRORPATH+File.separator+files[i].getName()  +"\t错误信息:" + e.getMessage(), Level.ERROR);
 				String[] fileSplit = files[i].getName().split("_");
				if(fileSplit.length<4) {
					return;
				}
				String regNo =  fileSplit[0];
				String fileType = fileSplit[1];
				String fileDate = fileSplit[2];
				String no =fileSplit[3];
//				LogMap.put("SOURCE_BACK_PATH",ConfigPathData.getString("BACKPATH")+File.separator+files[i].getName());
				LogMap.put("CONFIG_PATH_ID",ConfigPathData.getInt("INDX"));
				LogMap.put("DATA_SOURCE", "Excel转换");
				LogMap.put("SERIAL_NO", regNo);
				LogMap.put("TARGET_FILE_NAME", files[i].getName());
				LogMap.put("FILE_PATH", ERRORPATH);
				LogMap.put("TRANSFORMATION_CODE", 3);
				LogMap.put("TRANSFORMATION_NAME", "失败");
				LogMap.put("PROCESS_MSG", "错误信息:"+e.getMessage());
				Util.AddMessLog(access,LogMap);
				List query4List = SQLExecUtils.query4List("SELECT COUNT(0) as COUNT FROM exs_convert_log WHERE SERIAL_NO = '"+regNo +"' AND TARGET_FILE_NAME LIKE '%"+fileName+"%'");
				if (query4List.size() > 0 ) {
					HashMap CountMap = (HashMap) query4List.get(0);
					Object COUNT =  CountMap.get("COUNT");
					if (!SysUtility.isEmpty(COUNT)) {
						if(Integer.parseInt((String) COUNT)>1) {
							SQLExecUtils.executeUpdate("DELETE exs_convert_log WHERE TARGET_FILE_NAME='"+filepath.substring(p_,filepath.length())+"' AND PROGRESS_CODE = '0' AND DATA_SOURCE LIKE '%服务器%'");
						}
					}
					
				}
			}
		}
	}
	
	
	//递归写法  repeat=重表 标记
	public static void AddXml2(File file,Integer k,JSONArray JSONArray,JSONObject ExcelData,boolean repeat,Map ExcelMap,Map ExcelSheetMap) throws Exception {
		int thisrepeatNum =GetThisDomCount(JSONArray, k);//本级
		int repeatNum = GetDomCount(JSONArray, k);//下级
		if(thisrepeatNum==1) {
			System.err.println("我是单标签"+new JSONObject(JSONArray.get(k).toString()).getString("TARGETCOLNAME"));
		}
		if(repeatNum>0) {
			if(!new JSONObject(JSONArray.get(k).toString()).isNull("ISSUBLIST") && new JSONObject(JSONArray.get(k).toString()).getString("ISSUBLIST").equals("2")) {
				if(new JSONObject(JSONArray.get(k).toString()).isNull("SOURCENOTENAME")) {
					throw new  RuntimeException("配置信息:重节点源节点不可为空");
				}
				String SOURCENOTENAME = new JSONObject(JSONArray.get(k).toString()).getString("SOURCENOTENAME");
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
				JSONArray Excel2DATA = ExcelToJsonArrayUtil.ExcelToJsonArray(file.getPath(), split[0]);//重表	
				if(Excel2DATA.length()==0) {
					throw new  RuntimeException("Excel文件:"+split[0]+"名称不存在或者为空(注意大小写)");
				}
				JSONArray getSonData = GetSonData(Excel2DATA,((JSONObject)ExcelSheetMap.get(sheetname1)).getString(fieid), name2);
				for(int i=0;i<getSonData.length();i++) {
					int xn = (k+1);
					thisrepeatNum =GetThisDomCount(JSONArray, xn);//本级
					repeatNum =GetDomCount(JSONArray, k);//下级
					if(thisrepeatNum == repeatNum) {
						AddXmlElement(new JSONObject(JSONArray.get(k).toString()), Excel2DATA.getJSONObject(i));
						k++;
						for(int j=0;j<repeatNum;j++) {
							AddXmlElement(new JSONObject(JSONArray.get(k).toString()), Excel2DATA.getJSONObject(i));
							k++;
							if(i+1 ==getSonData.length()) {
								
							}else if(j+1 == repeatNum) {
								k = k-repeatNum-1;
							}
						}
					}else {
						//待添加 TODO
					}
				}
				
			}
			if(!repeat) {
				AddXmlElement(new JSONObject(JSONArray.get(k).toString()), ExcelData);
			}else {
				AddXmlElement(new JSONObject(JSONArray.get(k).toString()), ExcelData);
			}
			
		}else {
			AddXmlElement(new JSONObject(JSONArray.get(k).toString()), ExcelData);
		}
	}
	
	
	public static void ExcelToXml(JSONObject ConfigPathData,IDataAccess access) throws Exception{
		
		if(SysUtility.isEmpty(ConfigPathData.getString("SOURCEFILEPATH"))){
			return;
		}
		File files[] = new File(ConfigPathData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
		if(SysUtility.isEmpty(files)) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if(i > FILECOUNT) {
				return;
			}
			
			if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XLS")){
				//只处理XLS文件 
				continue;
			}
			Map LogMap = new HashMap();
			try {
				Map sheetMap = new HashMap();
				long startTime=System.currentTimeMillis();   //获取开始时间
				initRoot();
				//读取sheet页
				List query4List = SQLExecUtils.query4List("SELECT * FROM exs_convert_config_name WHERE P_INDX='"+ConfigPathData.getInt("INDX")+"' ORDER BY to_number(exs_convert_config_name.SEQ)");
				//读取sheet页
				JSONArray excelToJsonArray = MessageUtil.getJSONArrayByList(query4List);
	 			
				if(excelToJsonArray.length()<=0){
					return;
				}
				
				ConfigPathData.put("SOURCEFILEPATHNAME", files[i].getPath());
				String SteetName = new JSONObject(excelToJsonArray.get(0).toString()).getString("SOURCENOTENAME");
				if(SysUtility.isEmpty(SteetName)) {
					throw new  RuntimeException("读取主表数据失败 请检查目标层为0的第一行数据是否存在Sheet页名字忘记填写!");
				}
				JSONArray MainExcelDATA = ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), SteetName);//读取主表
				
				//读取sheet页
	 			if(SysUtility.isEmpty(MainExcelDATA)){//如果Excel配置数据为空 则直接中断
	 				throw new  RuntimeException("读取主表数据失败 请检查目标层为0的第一行数据是否存在Sheet页名字写错!(区分大小写)");
				}
	 			//由于Excel可以配置N条数据信息 所以在此可以读取多个
	 			for(int a=0;a<MainExcelDATA.length();a++){
	 				initRoot();
	 				JSONObject ExcelData = MainExcelDATA.getJSONObject(a);//每一条数据
	 				sheetMap.put(SteetName, ExcelData);
	 				
	 				
	 				for(int b=0;b<excelToJsonArray.length();b++){
	 					JSONObject HjsonData = null;
	 					if(excelToJsonArray.get(b) instanceof JSONObject) {
	 						HjsonData = excelToJsonArray.getJSONObject(b);
	 					}else if(excelToJsonArray.get(b) instanceof Map) {
	 						HjsonData = new JSONObject(excelToJsonArray.get(b).toString());
	 					}
	 					 
 						 if(!HjsonData.isNull("ISSUBLIST") && "Y".equals(HjsonData.getString("ISSUBLIST").toUpperCase())){
 							 //获取字表
 							String seq =Integer.valueOf(b+1).toString();
 							if(!HjsonData.isNull("SEQ")) {
 								seq = HjsonData.getString("SEQ");
 							}
 							String SOURCENOTENAME = HjsonData.getString("SOURCENOTENAME");
 							if(SysUtility.isEmpty(SOURCENOTENAME)) {
 								throw new  RuntimeException("读取子表数据失败 请检查"+HjsonData.get("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 							}
 							String[] split = SOURCENOTENAME.split("&");
 							if(split.length==1) {
 								throw new  RuntimeException("读取子表数据失败 请检查"+HjsonData.get("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 							}
 							String[] split2 = split[1].split("\\|");
 							if(split2.length==1) {
 								throw new  RuntimeException("读取子表数据失败 请检查"+HjsonData.get("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 							}
 							String name1 = split2[0];
 							String name2 = split2[1];
 							String[] split3 = name1.split("\\.");
 							if(split3.length==1) {
 								throw new  RuntimeException("读取子表数据失败 请检查"+HjsonData.get("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 							}
 							String sheetname1 = split3[0];
 							String fieid = split3[1];
 							
 							Integer getDomCount = GetDomCount(excelToJsonArray,b);
 							
 							JSONArray SonData = GetSonData(ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), split[0]),((JSONObject)sheetMap.get(sheetname1)).getString(fieid.toUpperCase()),name2);
 							if(SonData.length()==0) {
 								throw new  RuntimeException("读取子表数据为空 请检查"+HjsonData.get("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 							}
 							for(int S=0;S<SonData.length();S++) {
 								if(SysUtility.isEmpty(sheetMap.get(split[0]))) {
 	 								sheetMap.put(split[0], ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), split[0]).get(S));
 								}
 								String SourceName = "";
 	 							while(SourceName!="!") {
 	 								
									AddXmlElement(new JSONObject(excelToJsonArray.get(b).toString()), SonData.getJSONObject(S));
									b++;
 	 								for(int f=0; f<getDomCount; f++) {
 	 									if(!new JSONObject(excelToJsonArray.get(b).toString()).isNull("ISSUBLIST") && (new JSONObject(excelToJsonArray.get(b).toString()).getString("ISSUBLIST").toUpperCase().indexOf("Y")!=-1)) {
 	 										String SOURCENOTENAMEY = "";
 	 										if(!new JSONObject(excelToJsonArray.get(b).toString()).isNull("SOURCENOTENAME")) {
 	 											SOURCENOTENAMEY =new JSONObject(excelToJsonArray.get(b).toString()).getString("SOURCENOTENAME");
 	 										}
 	 										
 	 			 							if(SysUtility.isEmpty(SOURCENOTENAMEY)) {
 	 			 								throw new  RuntimeException("读取子表数据失败 请检查"+new JSONObject(excelToJsonArray.get(b).toString()).getString("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 	 			 							}
 	 			 							String[] splitY = SOURCENOTENAMEY.split("&");
 	 			 							if(splitY.length==1) {
 	 			 								throw new  RuntimeException("读取子表数据失败 请检查"+new JSONObject(excelToJsonArray.get(b).toString()).getString("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 	 			 							}
 	 			 							String[] split2Y = splitY[1].split("\\|");
 	 			 							if(split2Y.length==1) {
 	 			 								throw new  RuntimeException("读取子表数据失败 请检查"+new JSONObject(excelToJsonArray.get(b).toString()).getString("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 	 			 							}
 	 			 							String name1Y = split2Y[0];
 	 			 							String name2Y = split2Y[1];
 	 			 							String[] split3Y = name1Y.split("\\.");
 	 			 							if(split3Y.length==1) {
 	 			 								throw new  RuntimeException("读取子表数据失败 请检查"+new JSONObject(excelToJsonArray.get(b).toString()).getString("TARGETCOLNAME")+"的源字段配置是否正确(区分大小写)");
 	 			 							}
 	 			 							String sheetname1Y = split3Y[0];
 	 			 							String fieidY = split3Y[1];
	 	 										Integer DomCount = GetDomCount(excelToJsonArray,b);
	 	 			 							JSONArray YSonData = GetSonData(ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), splitY[0]),((JSONObject)sheetMap.get(sheetname1Y)).getString(fieidY.toUpperCase()),name2Y);
	 	 			 							for(int S1=0;S1<YSonData.length();S1++) {
	 	 			 								String SourceNameY = "";
	 	 			 								while(SourceNameY!="!") {
 	 			 										AddXmlElement(new JSONObject(excelToJsonArray.get(b).toString()), YSonData.getJSONObject(S1));
 	 			 										b++;
	 	 			 									for(int f1=0; f1<DomCount; f1++) {
	 	 			 										AddXmlElement(new JSONObject(excelToJsonArray.get(b).toString()), YSonData.getJSONObject(S1));
 	 	 													f++;
 	 	 													b++;
	 	 			 									}
	 	 			 									if(S1+1!=YSonData.length()) {
	 	 			 										SourceNameY="!";
	 	 			 										f= f-DomCount;
	 	 			 										b= b-DomCount-1;
	 	 			 									}else if(S1+1==YSonData.length()) {
	 	 			 										SourceNameY="!";
	 	 			 									}
	 	 			 								}
	 	 			 							}
	 	 									}
	 	 									if(excelToJsonArray.length()>b) {
	 											AddXmlElement(new JSONObject(excelToJsonArray.get(b).toString()), SonData.getJSONObject(S));
	 	 									}
 											if(f+1 >= getDomCount) {
 												
 												if(S+1 == SonData.length()) {
 													SourceName = "!";
 												}else {
 													SourceName = "!";
 													b = Integer.parseInt(seq)-1;
 												}
 											}else {
 												b++;
 											}
 	 								}
 	 							}
 	 							
 							}
 							
 						 }else{
 							 
 							AddXmlElement(HjsonData, ExcelData);
 							 
 						 }
	 					
	 					
	 				}
	 				if(SysUtility.isNotEmpty(root)) {
	 					String createSourcePath ="";
						String SuccessPath = "";
 	 					Document Doc  = new Document(root);
		 				XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
		 				fileHandle(ConfigPathData.getString("TARGETFILEPATH"));
		 				fileHandle(ConfigPathData.getString("BACKPATH"));
		 				String Successpath =ConfigPathData.getString("SUCCESS_BACK_PATH");
		 				String path =ConfigPathData.getString("TARGETFILEPATH");
		 				XMLOut.output(Doc, new FileOutputStream(createSourcePath = path+File.separator+ExcelData.getString("FILENAME")+".xml"));  
		 				XMLOut.output(Doc, new FileOutputStream(Successpath = path+File.separator+ExcelData.getString("FILENAME")+".xml"));  
		 				LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName(), Level.WARN);
		 				String filepath = files[i].getPath().replace("\\", "/").replace("\\", "/");
			 			int d_ = filepath.lastIndexOf(".");
			 			int p_ = filepath.lastIndexOf("/")+1;
			 			String fileName = filepath.substring(p_,d_);
			 			FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("BACKPATH")+File.separator+fileName+".xls");
						FileUtility.deleteFile(files[i].getPath());
						String[] fileSplit = files[i].getName().split("_");
						if(fileSplit.length<4) {
							continue;
						}
						String regNo =  fileSplit[0];
						String fileType = fileSplit[1];
						String fileDate = fileSplit[2];
						String no =fileSplit[3];
						LogMap.put("DATA_SOURCE", "Excel转换");
						LogMap.put("SERIAL_NO", regNo);
						LogMap.put("TARGET_FILE_NAME", files[i].getName());
						LogMap.put("FILE_PATH", createSourcePath);
						LogMap.put("SUCCESS_BACK_PATH", SuccessPath);
						LogMap.put("TRANSFORMATION_CODE", 2);
						LogMap.put("TRANSFORMATION_NAME", "成功");
						LogMap.put("TRANSFORMATION_TIME", true);
						LogMap.put("PROCESS_MSG", "转换成功");
						Util.AddMessLog(access,LogMap);
	 				}else {
	 					throw new  RuntimeException("根节点错误 请检查配置问题");
	 				}
	 			}
	 			
	 		} catch (Exception e) {
	 			
	 			fileHandle(ConfigPathData.getString("ERRORPATH"));
	 			String filepath = files[i].getPath().replace("\\", "/").replace("\\", "/");
	 			int d_ = filepath.lastIndexOf(".");
	 			int p_ = filepath.lastIndexOf("/")+1;
	 			String fileName = filepath.substring(p_,d_);
	 			FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("ERRORPATH")+File.separator+fileName+".xls");
				FileUtility.deleteFile(files[i].getPath());
 				LogUtil.printLog("文件转换失败："+ConfigPathData.getString("ERRORPATH")+File.separator+files[i].getName()  +"\t错误信息:" + e.getMessage(), Level.ERROR);
 				String[] fileSplit = files[i].getName().split("_");
				if(fileSplit.length<4) {
					return;
				}
				String regNo =  fileSplit[0];
				String fileType = fileSplit[1];
				String fileDate = fileSplit[2];
				String no =fileSplit[3];
				LogMap.put("DATA_SOURCE", "Excel转换");
				LogMap.put("SERIAL_NO", regNo);
				LogMap.put("TARGET_FILE_NAME", files[i].getName());
				LogMap.put("FILE_PATH", ConfigPathData.getString("ERRORPATH"));
				LogMap.put("TRANSFORMATION_CODE", 3);
				LogMap.put("TRANSFORMATION_NAME", "失败");
				LogMap.put("TRANSFORMATION_TIME", true);
				LogMap.put("PROCESS_MSG", e.getMessage());
				Util.AddMessLog(access,LogMap);
			}
		}
		
	}
	//老版
	public static void ExcelToXml(JSONObject ConfigPathData) throws Exception{
		if(SysUtility.isEmpty(ConfigPathData.getString("SOURCEFILEPATH"))){
			return;
		}
		File files[] = new File(ConfigPathData.getString("SOURCEFILEPATH")).listFiles(new FileFilterHandle());
		if(SysUtility.isEmpty(files)) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if(i > FILECOUNT) {
				return;
			}
			if(!files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toUpperCase().equals("XLS")){
				//只处理XLS文件 
				continue;
			}
			long startTime=System.currentTimeMillis();   //获取开始时间
			LogUtil.printLog("开始获取开始时间完成"+startTime, Level.INFO);
			initRoot();
			//开始处理
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
 			
			if(SysUtility.isEmpty(excelToJsonArray)){
				return;
			}
			
			ConfigPathData.put("SOURCEFILEPATHNAME", files[i].getPath());
			JSONArray MainExcelDATA = ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), 0);//读取主表
			
			//读取sheet页
 			if(SysUtility.isEmpty(MainExcelDATA)){//如果Excel配置数据为空 则直接中断
				return;
			}
 			try {
 				
	 			//由于Excel可以配置N条数据信息 所以在此可以读取多个
	 			for(int a=0;a<MainExcelDATA.length();a++){
	 				initRoot();
	 				JSONObject ExcelData = MainExcelDATA.getJSONObject(a);//每一条数据
	 				
	 				String No1Indx = "";
	 				if(!ExcelData.isNull("NO1INDX")) {
	 					No1Indx = ExcelData.getString("NO1INDX");
	 				}else if(!ExcelData.isNull("no1indx")) {
	 					No1Indx = ExcelData.getString("no1indx");
	 				}
	 				
	 				for(int b=0;b<excelToJsonArray.length();b++){
	 					
	 					
	 					JSONObject HjsonData = excelToJsonArray.getJSONObject(b);
	 					if(SysUtility.isEmpty(No1Indx)) {//单表处理
	 						
	 						AddXmlElement(HjsonData, ExcelData);
	 						
	 					}else {//多表处理
	 						 if(!HjsonData.isNull("ISSUBLIST") && "Y".equals(HjsonData.getString("ISSUBLIST").toUpperCase())){
	 							 //获取字表
	 							String seq = HjsonData.getString("SEQ");
	 							Integer getDomCount = GetDomCount(excelToJsonArray,b);
	 							JSONArray SonData = null;//GetSonData(ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), 1),No1Indx);
	 							for(int S=0;S<SonData.length();S++) {
	 								String no2indx = "";
	 								if(!SonData.getJSONObject(S).isNull("INDX")) {
	 									no2indx = SonData.getJSONObject(S).getString("INDX");
	 								}else if(!SonData.getJSONObject(S).isNull("indx")) {
	 									no2indx = SonData.getJSONObject(S).getString("indx");
	 								}
	 								String SourceName = "";
	 	 							while(SourceName!="!") {
 										AddXmlElement(excelToJsonArray.getJSONObject(b), SonData.getJSONObject(S));
 										b++;
	 	 								for(int f=0; f<getDomCount; f++) {
	 	 									if(!excelToJsonArray.getJSONObject(b).isNull("ISSUBLIST") && (excelToJsonArray.getJSONObject(b).getString("ISSUBLIST").toUpperCase().indexOf("Y")!=-1) && f!=0) {
		 	 										Integer DomCount = GetDomCount(excelToJsonArray,b);
	 	 	 			 							JSONArray YSonData = GetSonDataY(ExcelToJsonArrayUtil.ExcelToJsonArray(files[i].getPath(), 2),no2indx);
		 	 			 							for(int S1=0;S1<YSonData.length();S1++) {
		 	 			 								String SourceNameY = "";
		 	 			 								while(SourceNameY!="!") {
	 	 			 										AddXmlElement(excelToJsonArray.getJSONObject(b), YSonData.getJSONObject(S1));
	 	 			 										b++;
		 	 			 									for(int f1=0; f1<DomCount; f1++) {
		 	 			 										AddXmlElement(excelToJsonArray.getJSONObject(b), YSonData.getJSONObject(S1));
	 	 	 													f++;
	 	 	 													b++;
		 	 			 									}
		 	 			 									if(S1+1!=YSonData.length()) {
		 	 			 										SourceNameY="!";
		 	 			 										f= f-DomCount;
		 	 			 										b= b-DomCount-1;
		 	 			 									}else if(S1+1==YSonData.length()) {
		 	 			 										SourceNameY="!";
		 	 			 									}
		 	 			 								}
		 	 			 							}
		 	 									}
		 	 									if(excelToJsonArray.length()>b) {
		 											AddXmlElement(excelToJsonArray.getJSONObject(b), SonData.getJSONObject(S));
		 	 									}
	 											if(f+1 >= getDomCount) {
	 												
	 												if(S+1 == SonData.length()) {
	 													SourceName = "!";
	 												}else {
	 													SourceName = "!";
	 													b = Integer.parseInt(seq)-1;
	 												}
	 											}else {
	 												b++;
	 											}
	 	 								}
	 	 							}
	 	 							
	 							}
	 							
	 						 }else{
	 							 
	 							AddXmlElement(HjsonData, ExcelData);
	 							 
	 						 }
	 					}
	 					
	 					
	 				}
	 				if(SysUtility.isNotEmpty(root)) {
	 					Document Doc  = new Document(root);
		 				XMLOutputter XMLOut = new XMLOutputter(FormatXML()); 
		 				fileHandle(ConfigPathData.getString("TARGETFILEPATH"));
		 				fileHandle(ConfigPathData.getString("BACKPATH"));
		 				String path =ConfigPathData.getString("TARGETFILEPATH");
		 				XMLOut.output(Doc, new FileOutputStream(path+File.separator+ExcelData.getString("FILENAME")+".xml"));  
		 				LogUtil.printLog("文件转换成功："+path+File.separator+files[i].getName(), Level.WARN);
		 				String filepath = files[i].getPath().replace("\\", "/").replace("\\", "/");
			 			int d_ = filepath.lastIndexOf(".");
			 			int p_ = filepath.lastIndexOf("/")+1;
			 			String fileName = filepath.substring(p_,d_);
			 			FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("BACKPATH")+File.separator+fileName+".xls");
						FileUtility.deleteFile(files[i].getPath());
	 				}else {
	 					throw new  Exception("根节点错误 请检查配置问题");
	 				}
	 			}
	 			
	 		} catch (Exception e) {
	 			fileHandle(ConfigPathData.getString("ERRORPATH"));
	 			String filepath = files[i].getPath().replace("\\", "/").replace("\\", "/");
	 			int d_ = filepath.lastIndexOf(".");
	 			int p_ = filepath.lastIndexOf("/")+1;
	 			String fileName = filepath.substring(p_,d_);
	 			FileUtility.copyFile(files[i].getPath(), ConfigPathData.getString("ERRORPATH")+File.separator+fileName+".xls");
				FileUtility.deleteFile(files[i].getPath());
 				LogUtil.printLog("文件转换失败："+ConfigPathData.getString("ERRORPATH")+File.separator+files[i].getName(), Level.ERROR);

			}
		}
		
	}

	
	
	
	 private static void AddXmlElement(JSONObject HJsonData,JSONObject ExcelData) throws JSONException {
		 
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
	 
	 public static String GetValue(JSONObject ExcelData,JSONObject HJsonData) throws JSONException{
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
				 if(!HJsonData.isNull("DEFVALUE") && SysUtility.isNotEmpty(HJsonData.get("DEFVALUE"))) {
					 value = HJsonData.getString("DEFVALUE");
				 }
				 value = isJsonFunctionValueExcel(HJsonData, value);
				 if(SysUtility.isEmpty(value)) {
					 value =null;
				 }
			 }
		 return value;
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
	
	public static JSONArray GetSonDataY(JSONArray SonArray,String no2Indx) throws JSONException {
		JSONArray ReturnSonArr = new JSONArray();
		for(int i=0; i< SonArray.length(); i++) {
			JSONObject SonObj = SonArray.getJSONObject(i);
			if(SonObj.getString("NO3INDX").equals(no2Indx)) {
				ReturnSonArr.put(SonObj);
			}
		}
		return ReturnSonArr;
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
					jsonObject = new JSONObject((Map)ExcelJsonArr.get(i));
				}
				
				Integer TARGETFILEFLOOR = jsonObject.getInt("TARGETFILEFLOOR");
				if(ExcelJsonArr.get(i+1) instanceof JSONObject) {
					jsonObject = ExcelJsonArr.getJSONObject(i+1);
				}else if(ExcelJsonArr.get(i+1) instanceof Map) {
					jsonObject = new JSONObject((Map)ExcelJsonArr.get(i+1));
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
	
	
	private static Integer GetDomCount(JSONArray ExcelJsonArr,Integer number) throws JSONException {
		Integer DomCount = 0;
		Integer JsNumber = number;
		for(int i=JsNumber;i<ExcelJsonArr.length();i++) {
			if(i+1 < ExcelJsonArr.length()) {
				JSONObject jsonObject = null;
				if(ExcelJsonArr.get(i) instanceof JSONObject) {
					jsonObject = ExcelJsonArr.getJSONObject(i);
				}else if(ExcelJsonArr.get(i) instanceof Map) {
					jsonObject = new JSONObject((Map)ExcelJsonArr.get(i));
				}
				
				Integer TARGETFILEFLOOR = jsonObject.getInt("TARGETFILEFLOOR");
				if(ExcelJsonArr.get(i+1) instanceof JSONObject) {
					jsonObject = ExcelJsonArr.getJSONObject(i+1);
				}else if(ExcelJsonArr.get(i+1) instanceof Map) {
					jsonObject = new JSONObject((Map)ExcelJsonArr.get(i+1));
				}
				Integer TARGETFILEFLOOR2 = jsonObject.getInt("TARGETFILEFLOOR");
				if(TARGETFILEFLOOR2>TARGETFILEFLOOR || TARGETFILEFLOOR2==TARGETFILEFLOOR) {
					DomCount++;
				}else if(TARGETFILEFLOOR2<TARGETFILEFLOOR){
					return DomCount;
				}
			}
		}
		return DomCount;
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

    
    public static String isJsonFunctionValueExcel(JSONObject HJsonData,String value) throws JSONException {
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
    
	public static void initRoot(){
    	 root     = null;
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
