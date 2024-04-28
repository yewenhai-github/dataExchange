package com.easy.app.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.app.constants.ExsConstants;
import com.easy.exception.LegendException;
import com.easy.utility.CacheUtility;
import com.easy.utility.FileUtility;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class ConvertUtility {
	
	//Xml转Json
	public static String xmlToJson(String xmlData) {
		try {
			InputStream is = new ByteArrayInputStream(xmlData.getBytes());
			String XmlRootName = FileUtility.GetExsFileRootName(xmlData);
			HashMap hmSourceData = FileUtility.xmlParse(is,XmlRootName);
			JSONObject jsTargetData = SysUtility.MapToJSONObject(hmSourceData);
			return jsTargetData.toString();
		} catch (LegendException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "xmlToJson Error";
	}
	
	//Json转Xml
	public static String jsonToXml(String JsonData,String Encoding) throws Exception{
		HashMap RequestMessage = new HashMap();
		JSONObject tempData = new JSONObject(JsonData);
		Iterator<?> keys = tempData.keys();
		String XmlRootName = keys.next().toString();
		RequestMessage = SysUtility.JSONObjectToHashMap(tempData, XmlRootName);
		
		return "<?xml version=\"1.0\" encoding=\""+Encoding+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(XmlRootName,RequestMessage, 0);
	}
	
	//Xml转EdiFact
	public static String xmlToEdiFact(String XmlData,String Encoding){
		if(SysUtility.isEmpty(XmlData) || SysUtility.isEmpty(Encoding)){
			return "";
		}
		try {
			/****************1.解析Xml*******************/
			InputStream is = new ByteArrayInputStream(XmlData.getBytes(Encoding));
			String XmlRootName = FileUtility.GetExsFileRootName(XmlData);
			HashMap hmSourceData = FileUtility.xmlParse(is,XmlRootName);
			/****************1.从Xml中读取相关的配置*******************/
			String RootName = FileUtility.getRootName(hmSourceData);
			HashMap parentMap = FileUtility.getParentMap(RootName,hmSourceData);
			/****************2.填充datas*******************/
			Datas datas = new Datas();
			datas.MapToDatas(ExsConstants.MessageHead, hmSourceData);
			String[] RootNames = RootName.toString().split(",");
			for (int i = 0; i < RootNames.length; i++) {
				datas.MapToDatas(RootNames[i],hmSourceData);
			}
			/****************3.生成ediFactData*******************/
			String messageType = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_TYPE);
			String messageId = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_ID);
			String messageTime = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_TIME);
			String messageSource = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SOURCE);
			String messageDest = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_DEST);
			String messageCategory = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_CATEGORY);
			String messageVersion = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_VERSION);
			String messageSignData = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.MESSAGE_SIGN_DATA);
			String signData = datas.GetTableValue(ExsConstants.MessageHead, ExsConstants.SIGN_DATA);
//			String SerialNo = datas.GetTableValue(RootNames[0], SerialName);
			messageSource = SysUtility.isEmpty(messageSource)?"":messageSource;
			messageDest = SysUtility.isEmpty(messageDest)?"":messageDest;
			messageTime = processDateValue(messageTime);
			messageCategory = SysUtility.isEmpty(messageCategory)?"11":messageVersion;
			messageVersion = SysUtility.isEmpty(messageVersion)?"1.0":messageVersion;
			if(SysUtility.isNotEmpty(signData)){
				messageSignData = SysUtility.EncryptKeys(messageSource, messageTime,signData);
			}
			StringBuffer EdiData = new StringBuffer();
			//UNH Message Header
			EdiData.append(ExsConstants.UNH+"+"+XmlRootName+"+"+messageType+"+"+Encoding+":"+messageId+":"+messageTime+":"+messageSource+":"+messageDest+":"+messageCategory+":"+messageVersion+":"+messageSignData+"'").append("\n");
			//Beginning of message
			EdiData.append(ExsConstants.BGM+"+0000"+"'").append("\n");
			//Free text
			EdiData.append(DatasToAnyEdi(datas, RootNames, parentMap, messageType));
			//Message trailer
			String line = datas.GetTableValue("tempTable", "LINE");
			int lineCount = 2 + Integer.parseInt(line);
			EdiData.append(ExsConstants.UNT+"+"+lineCount+"+"+XmlRootName+"'");
			return EdiData.toString();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (LegendException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	//EdiFact转Xml
	public static String ediFactToXml(InputStream is, String messageType) throws Exception{
		String Encoding = "UTF-8";
		//解析EdiFact数据
		StringBuffer RootName = new StringBuffer();
		StringBuffer RootDefault = new StringBuffer();
		HashMap RootNameMap = new HashMap();
		HashMap EdiFactMap = new HashMap();
		HashMap<String,String> MessageHead = new HashMap<String,String>();
		GetEdiFactConfig(is, Encoding, EdiFactMap, MessageHead, RootName, RootDefault, RootNameMap, messageType);
		
		//生成XmlMap对象
		String[] RootNames = RootName.toString().split(",");
		String XmlRootName = RootNames[0];
		
		Map xmlRootMap = new HashMap();
		List MainDatas = (List)EdiFactMap.get(XmlRootName);
		for (int i = 0; i < MainDatas.size(); i++) {
			HashMap MainData = (HashMap)MainDatas.get(i);
			List xmlRootList = new ArrayList();
			
			if(SysUtility.isEmpty(xmlRootMap.get(XmlRootName))){
				xmlRootMap.put(XmlRootName, xmlRootList);
			}else{
				xmlRootList = (List)xmlRootMap.get(XmlRootName);
			}
			HashMap RootMap = new HashMap();
			List HeadList = new ArrayList();
			HeadList.add(MainData);
			RootMap.put(XmlRootName, HeadList);
			xmlRootList.add(RootMap);
			
			for (int j = 1; j < RootNames.length; j++) {
				String XmlDocumentName = RootNames[j];
				String XmlParentDocument = (String)RootNameMap.get(XmlDocumentName);
				Object obj = ExsUtility.getXmlParentDocumentObj(xmlRootList, XmlParentDocument);
				if(obj instanceof List){
					List ParentLst = (List)obj;
					for (int k = 0; k < ParentLst.size(); k++) {
						HashMap tempMap = (HashMap)ParentLst.get(k);
						List childList = (List)EdiFactMap.get(XmlDocumentName);//需要筛选子表数据
						tempMap.put(XmlDocumentName, childList);
					}
				}
			}
		}
		//转成Xml
		StringBuffer XmlData = new StringBuffer();
		XmlData.append("<?xml version=\"1.0\" encoding=\""+Encoding+"\"?>").append("\n");
		XmlData.append("<"+RootDefault+">").append("\n");
		XmlData.append("<MessageHead>").append("\n");
		XmlData.append("    <MESSAGE_ID>"+MessageHead.get(ExsConstants.MESSAGE_ID)+"</MESSAGE_ID> ").append("\n");
		XmlData.append("    <MESSAGE_TYPE>"+MessageHead.get(ExsConstants.MESSAGE_TYPE)+"</MESSAGE_TYPE> ").append("\n");
		XmlData.append("    <MESSAGE_TIME>"+MessageHead.get(ExsConstants.MESSAGE_TIME)+"</MESSAGE_TIME> ").append("\n");
		XmlData.append("    <MESSAGE_SOURCE>"+MessageHead.get(ExsConstants.MESSAGE_SOURCE)+"</MESSAGE_SOURCE> ").append("\n");
		XmlData.append("    <MESSAGE_DEST>"+MessageHead.get(ExsConstants.MESSAGE_DEST)+"</MESSAGE_DEST> ").append("\n");
		if(SysUtility.isNotEmpty(MessageHead.get(ExsConstants.MESSAGE_VERSION))){
			XmlData.append("    <MESSAGE_VERSION>"+MessageHead.get(ExsConstants.MESSAGE_VERSION)+"</MESSAGE_VERSION> ").append("\n");
		}
		if(SysUtility.isNotEmpty(MessageHead.get(ExsConstants.MESSAGE_CATEGORY))){
			XmlData.append("    <MESSAGE_CATEGORY>"+MessageHead.get(ExsConstants.MESSAGE_CATEGORY)+"</MESSAGE_CATEGORY> ").append("\n");
		}
		if(SysUtility.isNotEmpty(MessageHead.get(ExsConstants.MESSAGE_SIGN_DATA))){
			XmlData.append("    <MESSAGE_SIGN_DATA>"+SysUtility.EncryptKeys((String)MessageHead.get(ExsConstants.MESSAGE_SOURCE), (String)MessageHead.get(ExsConstants.MESSAGE_TIME),(String)MessageHead.get(ExsConstants.MESSAGE_SIGN_DATA))+"</MESSAGE_SIGN_DATA> ").append("\n");
		}
		if(SysUtility.isNotEmpty(MessageHead.get(ExsConstants.TECH_REG_CODE))){
			XmlData.append("    <TECH_REG_CODE>"+MessageHead.get(ExsConstants.TECH_REG_CODE)+"</TECH_REG_CODE> ").append("\n");
		}
		XmlData.append("  </MessageHead>").append("\n");
		XmlData.append("  <MessageBody>").append("\n");
		HashMap MappingColumns = new HashMap();
		
		List list = (List)xmlRootMap.get(XmlRootName);
		for (int i = 0; i < list.size(); i++) {
			HashMap RootMap = (HashMap)list.get(i);
			XmlData.append(FileUtility.hashMapToAnyXml(XmlRootName, RootMap, 1, MappingColumns));
		}
		XmlData.append("  </MessageBody>").append("\n");
		XmlData.append("</"+RootDefault+">").append("\n");
		return XmlData.toString();
	}
	
	public static void GetEdiFactConfig(InputStream is,String Encoding, HashMap EdiFactMap,HashMap<String,String> MessageHead,StringBuffer RootName,StringBuffer RootDefault,HashMap RootNameMap, String messageType) throws Exception{
		InputStreamReader isRd = null;
		LineNumberReader reader = null;
		int errorCount = 0;
		String str = "";
		try {
			isRd = new InputStreamReader(is,Encoding);
			reader = new LineNumberReader(isRd);
			int nullCount = 0;//结束读写内容
			HashMap tempMap = new HashMap();
			String tempDocumentName = "";
			
			HashMap mappingCode = new HashMap();
			while(true){
				errorCount++;
				str = reader.readLine();
				if(SysUtility.isEmpty(str)){
					nullCount++;
					if(nullCount > 3)//遇到4个空格，表示结束
			    		break;
					continue;
				}
				nullCount = 0;
				//解析报文
				str = str.replace("'", "");
				if(str.startsWith("UNH")){
					//UNH+RequestMessage+EXIT_DECLARE+UTF-8:4ABEBF825C3326DDE050A8C0C8015793:2017-06-23 15:00:00:CBECClient:910000:11:1.0:d04a797bebd2b4fd1943b76e112ae186'
					String[] strs = str.split("\\+");
					String[] strHead = strs[3].split("\\:");
					RootDefault.append(strs[1]);
					MessageHead.put(ExsConstants.MESSAGE_TYPE, (strs.length > 2)?strs[2]:"");
					MessageHead.put(ExsConstants.MESSAGE_ID, (strHead.length > 1)?strHead[1]:"");
					MessageHead.put(ExsConstants.MESSAGE_TIME, (strHead.length > 2)?strHead[2]:"");
					MessageHead.put(ExsConstants.MESSAGE_SOURCE, (strHead.length > 3)?strHead[3]:"");
					MessageHead.put(ExsConstants.MESSAGE_DEST, (strHead.length > 4)?strHead[4]:"");
					MessageHead.put(ExsConstants.MESSAGE_VERSION, (strHead.length > 5)?strHead[5]:"");
					MessageHead.put(ExsConstants.MESSAGE_CATEGORY, (strHead.length > 6)?strHead[6]:"");
					MessageHead.put(ExsConstants.MESSAGE_SIGN_DATA, (strHead.length > 7)?strHead[7]:"");
					MessageHead.put(ExsConstants.TECH_REG_CODE, (strHead.length > 8)?strHead[8]:"");
				}if(str.startsWith("DMS")){
					//DMS+1+C_EXIT_DECLARE'
					//DMS+1+C_EXIT_DECLARE_ITEMS+C_EXIT_DECLARE'
					//DMS+2+C_EXIT_DECLARE_ITEMS+C_EXIT_DECLARE'
					String[] strs = str.split("\\+");
					//填充数据
					if(SysUtility.isNotEmpty(tempMap)){
						HashMap dataMap = new HashMap();
						dataMap.putAll(tempMap);
						if(SysUtility.isEmpty(EdiFactMap.get(tempDocumentName))){
							List dataList = new ArrayList();
							dataList.add(dataMap);
							EdiFactMap.put(tempDocumentName, dataList);
						}else{
							List dataList = (List)EdiFactMap.get(tempDocumentName);
							dataList.add(dataMap);
						}
						tempMap.clear();
					}
					tempDocumentName = strs[2];
					mappingCode = CacheUtility.GetMappingCode(ExsConstants.EdiFact, tempDocumentName, messageType);
					
					//填充RootName串
					if(RootName.indexOf(tempDocumentName) < 0){
						RootName.append(tempDocumentName).append(",");
					}
					//填充节点上下级
					String XmlDocumentName = strs[2];
					String XmlParentDocument = strs.length == 4?strs[3]:"";
					RootNameMap.put(XmlDocumentName, XmlParentDocument);
				}else if(str.startsWith("FTX")){
					//FTX+001:4ABEBF825C0F26DDE050A8C0C8015793'  FTX+001:'
					String[] strs = str.split("\\+");
					String[] strFtx = strs[1].split("\\:");
					String str1 = strFtx[0];
					String str2 = (strFtx.length == 2)?strFtx[1]:"";
					if(SysUtility.isNotEmpty(mappingCode.get(str1))){
						tempMap.put(mappingCode.get(str1), str2);
					}else{
						tempMap.put(str1, str2);
					}
				}
			}
			//填充最后一条数据
			if(SysUtility.isNotEmpty(tempMap)){
				HashMap dataMap = new HashMap();
				dataMap.putAll(tempMap);
				
				if(SysUtility.isEmpty(EdiFactMap.get(tempDocumentName))){
					List dataList = new ArrayList();
					dataList.add(dataMap);
					EdiFactMap.put(tempDocumentName, dataList);
				}else{
					List dataList = (List)EdiFactMap.get(tempDocumentName);
					dataList.add(dataMap);
				}
				tempMap.clear();
			}
		} catch (Exception e) {
			LogUtil.printLog("GetEdiFactMap Error at "+errorCount+" line:"+str, Level.ERROR);
		} finally{
			SysUtility.closeInputStreamReader(reader);
			SysUtility.closeLineNumberReader(isRd);
			SysUtility.closeInputStream(is);
		}
	}
	
	public static String DatasToAnyEdi(Datas datas,String[] RootNames,HashMap parentMap,String messageType) throws Exception{
		StringBuffer EdiData = new StringBuffer();
		
		int line = 1;
		for (int i = 0; i < RootNames.length; i++) {
			JSONArray rows = datas.GetTables(RootNames[i]);
			if(SysUtility.isEmpty(rows)){
				continue;
			}
			List list = CacheUtility.GetConfigMapping(ExsConstants.EdiFact, RootNames[i], messageType);
			int icount = 0;
			for (int j = 0; j < rows.length(); j++) {
				JSONObject row = (JSONObject)rows.get(j);
				icount ++;
				line++;
				EdiData.append(ExsConstants.DMS+"+"+icount+"+"+RootNames[i]).append(SysUtility.isNotEmpty(parentMap.get(RootNames[i]))?"+"+parentMap.get(RootNames[i]):"").append("'").append("\n");
				for (int k = 0; k < list.size(); k++) {
					HashMap map = (HashMap)list.get(k);
					String tag = (String)map.get("MAPP_TAG");
					String key = (String)map.get("MAPP_TARGET_CODE");
					String keySource = (String)map.get("MAPP_SOURCE_CODE");
					String value = SysUtility.getJsonField(row, key);
					if(ExsConstants.FTX.equals(tag)){
						EdiData.append(tag+"+"+keySource+":"+value+"'").append("\n");
					}else if(ExsConstants.DTM.equals(tag)){
						EdiData.append(tag+"+"+keySource+":"+processDateValue(value)+"'").append("\n");
					}else{
						EdiData.append(tag+"+"+keySource+":"+value+"'").append("\n");
					}
					line++;
				}
			}
		}
		datas.SetTableValue("tempTable", "LINE", line);
		return EdiData.toString();
	}
	
	public static boolean isDateValue(String str) throws Exception{
		if(SysUtility.isEmpty(str)){
			return false;
		}
		if(str.length() == 19 && str.indexOf("-") == 4 && str.lastIndexOf("-") == 7 && str.indexOf(":") == 13 && str.lastIndexOf(":") == 16){//2017-03-15 08:25:25
			return true;
		}
		return false;
	}
	
	public static String processDateValue(String str) throws Exception{
		if(SysUtility.isEmpty(str)){
			return "";
		}
		return str.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
	}
	
}
