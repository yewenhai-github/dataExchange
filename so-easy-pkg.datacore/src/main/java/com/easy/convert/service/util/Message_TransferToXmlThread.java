package com.easy.convert.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Level;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.convert.service.mess.Message_TransferSier;
import com.easy.entity.ServicesBean;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;


/**
 * 线程生成XML方法   
 * 私自类  不允许被继承   另一方面为了提升性能
 * @author Administrator
 *
 */
public  final class Message_TransferToXmlThread {
	
	
	
	public static void xmltoxmlLocalThread(ServicesBean bean) throws JSONException {
		JSONObject ConfigData = bean .getSearchParam();
		File file = bean.getFile();
		HashMap ElmentMap = bean.getTempMap();
		if(!file.getName().substring(file.getName().lastIndexOf(".")+1).toUpperCase().equals("XML")){//只处理XML文件 
			return;
		}
		long startTime=System.currentTimeMillis();
		try {
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
			String XMLDATA = FileUtility.readFile(file, false, "UTF-8");
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
					 int DefNumber = MessUtility.FindDef(XMLDATA, SOURCENOTENAME);
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
						 String DefXML =  MessUtility.subStringNum(XMLDATA,SOURCENOTENAME,k+1);
						//创建XML
						 AddElementLocalXml(ElmentMap,HJsonData, LocalExcelJson, j, AppearMap, DefXML,XMLDATA);
						 Integer DomCount =  MessUtility.GetDomCount(LocalExcelJson, j);//下级
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
								 int DefNumberY =  MessUtility.FindDef(DefXML, SOURCENOTENAMEY);
								 if(DefNumberY==0) {
									 DefNumberY=1;
								 }
								 for(int l=0;l<DefNumberY;l++,dom++){
									 j = (SEQY-1);
									 HJsonData = new JSONObject((HashMap)LocalExcelJson.get(j));
									 //截取重表XML内容
									 String DefXMLY =  MessUtility.subStringNum(DefXML,SOURCENOTENAMEY,l+1);
									 Integer DomCountY =  MessUtility.GetDomCount(LocalExcelJson, j);//下级
									 AddElementLocalXml(ElmentMap , HJsonData, LocalExcelJson, j, AppearMap, DefXML,XMLDATA);
									 j++;
									 for(int domY =0; domY < DomCountY; domY++,j++) {
										 if(LocalExcelJson.get(j) instanceof Map) {
											 HJsonData = new JSONObject((HashMap)LocalExcelJson.get(j));
										 }else if(LocalExcelJson.get(j) instanceof JSONObject) {
											 HJsonData = LocalExcelJson.getJSONObject(j);
										 }else {
										 	 HJsonData = new JSONObject(LocalExcelJson.get(j).toString());
										 }
										 AddElementLocalXml(ElmentMap , HJsonData, LocalExcelJson, j, AppearMap, DefXMLY,XMLDATA);
									 }
									//由于循环J++ 最后一次循环J则不需要++
									 if(l+1 ==DefNumberY) j--;
								 }
							 }else {
								 AddElementLocalXml(ElmentMap , HJsonData, LocalExcelJson, j, AppearMap, DefXML,XMLDATA);
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
						 if(in != null) {
							 in.close();
						 }
					 }
					 AddElementLocalXml(ElmentMap , HJsonData, LocalExcelJson, j, AppearMap, XMLDATA,XMLDATA);
				 }
			}
			if(SysUtility.isNotEmpty(ElmentMap.get("Element0"))) {
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
							 MessUtility.fileHandle(Folder);
						}
					}
				}
				Folder +=File.separator + file.getName();
				Document Doc  = new Document((Element)ElmentMap.get("Element0"));
				XMLOutputter XMLOut = new XMLOutputter( MessUtility.FormatXML()); 
				MessUtility.fileHandle(ConfigData.getString("TARGETFILEPATH"));
				MessUtility.fileHandle(ConfigData.getString("BACKPATH"));
				String renameFileNameXmlToExs = FileUtility.renameFileNameXmlToExs(Folder);
				FileOutputStream fileOutputStream = new FileOutputStream(renameFileNameXmlToExs);
				XMLOut.output(Doc, fileOutputStream);  
				if(fileOutputStream != null) {
					fileOutputStream.close();
				}
				bean.clone();
				if(Doc != null) {
					Doc.clone();
				}
				FileUtility.copyFile(file.getPath(), ConfigData.getString("BACKPATH")+File.separator+file.getName());
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
			 MessUtility.fileHandle(ConfigData.getString("ERRORPATH"));
			FileUtility.copyFile(file.getPath(), ConfigData.getString("ERRORPATH")+File.separator+file.getName());
			FileUtility.deleteFile(file.getPath());
			LogUtil.printLog("文件转换失败:"+e.getMessage(), Level.WARN);
		}
	}

	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void AddElementLocalXml(HashMap ElmentMap,JSONObject HJsonData,JSONArray excelToJsonArray,int i,Map<String, Integer> AppearMap,String xmlToString,String XMLDATA) throws JSONException{
			JSONObject object = HJsonData;
			if(!HJsonData.isNull("SOURCENOTENAME") && SysUtility.isEmpty(AppearMap.get(HJsonData.getString("SOURCENOTENAME")))){
				AppearMap.put(HJsonData.getString("SOURCENOTENAME"), 1);
			}
			String SOURCENOTENAMES ="";
			if(!HJsonData.isNull("SOURCENOTENAME")) {
				SOURCENOTENAMES = HJsonData.getString("SOURCENOTENAME");
			}
			 //创建XML
			 if("0".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&& SysUtility.isEmpty(ElmentMap.get("Element0"))){
				 Element o1 = ((Element)ElmentMap.get("Element0"));
				 o1 = new Element(HJsonData.getString("TARGETCOLNAME")); 
				 ElmentMap.put("Element0", o1);
			 }else if("1".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element0"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value,xmlToString);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element oo = ((Element)ElmentMap.get("Element0"));
				 Element o1 = ((Element)ElmentMap.get("Element1"));
				 oo.addContent(o1 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element0", oo);
				 ElmentMap.put("Element1", o1);
			 }else if("2".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element1"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value =MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o1 = ((Element)ElmentMap.get("Element1"));
				 Element o2 = ((Element)ElmentMap.get("Element2"));
				 o1.addContent(o2 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element1", o1);
				 ElmentMap.put("Element2", o2);
			 }else if("3".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element2"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o2 = ((Element)ElmentMap.get("Element2"));
				 Element o3 = ((Element)ElmentMap.get("Element3"));
				 o2.addContent(o3 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element2", o2);
				 ElmentMap.put("Element3", o3);
			 }else if("4".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element3"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value =  MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o3 = ((Element)ElmentMap.get("Element3"));
				 Element o4 = ((Element)ElmentMap.get("Element4"));
				 o3.addContent(o4 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element3", o3);
				 ElmentMap.put("Element4", o4);
			 }else if("5".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element4"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value =  MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o4 = ((Element)ElmentMap.get("Element4"));
				 Element o5 = ((Element)ElmentMap.get("Element5"));
				 o4.addContent(o5 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element4", o4);
				 ElmentMap.put("Element5", o5);
			 }else if("6".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element5"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o5 = ((Element)ElmentMap.get("Element5"));
				 Element o6 = ((Element)ElmentMap.get("Element6"));
				 o5.addContent(o6 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element5", o5);
				 ElmentMap.put("Element6", o6);
			 }else if("7".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element6"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o6 = ((Element)ElmentMap.get("Element6"));
				 Element o7 = ((Element)ElmentMap.get("Element7"));
				 o6.addContent(o7 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element6", o6);
				 ElmentMap.put("Element7", o7);
			 }else if("8".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element7"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value =  MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o7 = ((Element)ElmentMap.get("Element7"));
				 Element o8 = ((Element)ElmentMap.get("Element8"));
				 o7.addContent(o8 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element7", o7);
				 ElmentMap.put("Element8", o8);
			 }else if("9".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element8"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o8 = ((Element)ElmentMap.get("Element8"));
				 Element o9 = ((Element)ElmentMap.get("Element9"));
				 o8.addContent(o9 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element8", o8);
				 ElmentMap.put("Element9", o9);
			 }else if("10".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element9"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o9 = ((Element)ElmentMap.get("Element9"));
				 Element o10 = ((Element)ElmentMap.get("Element10"));
				 o9.addContent(o10 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element9", o9);
				 ElmentMap.put("Element10", o10);
			 }else if("11".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element10"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o10 = ((Element)ElmentMap.get("Element10"));
				 Element o11 = ((Element)ElmentMap.get("Element11"));
				 o10.addContent(o11 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element10", o10);
				 ElmentMap.put("Element11", o11);
			 }else if("12".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element11"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o11 = ((Element)ElmentMap.get("Element11"));
				 Element o12 = ((Element)ElmentMap.get("Element12"));
				 o11.addContent(o12 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element11", o11);
				 ElmentMap.put("Element12", o12);
			 }else if("13".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element12"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o12 = ((Element)ElmentMap.get("Element12"));
				 Element o13 = ((Element)ElmentMap.get("Element13"));
				 o12.addContent(o13 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element12", o12);
				 ElmentMap.put("Element13", o13);
			 }else if("14".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element13"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o13 = ((Element)ElmentMap.get("Element13"));
				 Element o14 = ((Element)ElmentMap.get("Element14"));
				 o13.addContent(o14 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element13", o13);
				 ElmentMap.put("Element14", o14);
			 }else if("15".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element14"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o14 = ((Element)ElmentMap.get("Element14"));
				 Element o15 = ((Element)ElmentMap.get("Element15"));
				 o14.addContent(o15 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element14", o14);
				 ElmentMap.put("Element15", o15);
			 }else if("16".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element15"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o15 = ((Element)ElmentMap.get("Element15"));
				 Element o16 = ((Element)ElmentMap.get("Element16"));
				 o15.addContent(o16 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element15", o15);
				 ElmentMap.put("Element16", o16);
			 }else if("17".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element16"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("SourceFileFloor"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o16 = ((Element)ElmentMap.get("Element16"));
				 Element o17 = ((Element)ElmentMap.get("Element17"));
				 o16.addContent(o17 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element16", o16);
				 ElmentMap.put("Element17", o17);
			 }else if("18".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element17"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value =  MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value =  MessUtility.FindDevlue(object,value);
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
				 value =  MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value =  MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o17 = ((Element)ElmentMap.get("Element17"));
				 Element o18 = ((Element)ElmentMap.get("Element18"));
				 o17.addContent(o18 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element17", o17);
				 ElmentMap.put("Element18", o18);
			 }else if("19".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element18"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o18 = ((Element)ElmentMap.get("Element18"));
				 Element o19 = ((Element)ElmentMap.get("Element19"));
				 o18.addContent(o19 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element18", o18);
				 ElmentMap.put("Element19", o19);
			 }else if("20".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element19"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value =MessUtility. FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o19 = ((Element)ElmentMap.get("Element19"));
				 Element o20 = ((Element)ElmentMap.get("Element20"));
				 o19.addContent(o20 = new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
				 ElmentMap.put("Element19", o19);
				 ElmentMap.put("Element20", o20);
			 }else if("21".equals(HJsonData.getString("TARGETFILEFLOOR"))&&!SysUtility.isEmpty(HJsonData.getString("TARGETCOLNAME"))&&!SysUtility.isEmpty(ElmentMap.get("Element20"))){
				 String value = ""; 
				 if(!HJsonData.isNull("SOURCENOTENAME")) {
					 value = MessUtility.FindTheLocation(xmlToString, AppearMap.get(HJsonData.getString("SOURCENOTENAME")), SOURCENOTENAMES,object);
				 }
				 value = MessUtility.FindDevlue(object,value);
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
				 value = MessUtility.init(HJsonData.getString("TARGETFILEFLOOR"), NextTARGETFILEFLOOR, value);
				 value = MessUtility.isJsonFunctionValue(HJsonData,value, XMLDATA);
				 Element o20 = ((Element)ElmentMap.get("Element20"));
				 o20.addContent(new Element(HJsonData.getString("TARGETCOLNAME")).setText(value));
			 }/**/
		}
}
