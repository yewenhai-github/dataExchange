package com.easy.app.utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.entity.ServicesBean;
import com.easy.utility.FileUtility;
import com.easy.utility.SysUtility;

import java.util.Set;

public class ConversionUtility {
	
	
	public static void ConversionXmlCore(ServicesBean bean,InputStream is,IDataAccess DataAccess) throws Exception{
		
		/****************1 读取转换配置********************/
		
		
		/****************2 转换前数据处理********************/
		String XmlRootNamePre = FileUtility.GetExsFileRootName(bean.getFile());
		HashMap hmSourceData = FileUtility.xmlParse(is,XmlRootNamePre);
		String[] RootNamesPre = null;//TODO
		Datas datas = new Datas();
		datas.MapToDatas(ExsConstants.MessageHead, hmSourceData);
		for (int i = 0; i < RootNamesPre.length; i++) {
			datas.MapToDatas(RootNamesPre[i],hmSourceData, new HashMap());
		}
		/****************2 转换后数据处理********************/
		
		String[] RootNames = bean.getRootNames().split(",");
		List ListSql = bean.getListSql();
		HashMap SQLMap = bean.getMapSql();
		
		/**********************1  填充Root节点数据***************************************/
		HashMap childsMap = new HashMap();
		
		Map xmlRootMap = new HashMap();
		List xmlRootList = new ArrayList();
		String XmlRootName = "";
		for (int j = 0; j < ListSql.size(); j++) {
			HashMap sqlMap = (HashMap)ListSql.get(j);
			String SeqNo = (String)sqlMap.get("SEQ_NO");
			String XmlDocumentName = (String)sqlMap.get("XML_DOCUMENT_NAME");
			String XmlDisplayName = (String)sqlMap.get("XML_DISPLAY_NAME");
			String XmlParentDocument = (String)sqlMap.get("XML_PARENT_DOCUMENT");
			String HiddenStr = (String)sqlMap.get("XML_COULMN_HIDDEND");
			String xmlSql = (String)SQLMap.get(XmlDocumentName);
			
			if(SysUtility.isEmpty(XmlParentDocument)){//无父级节点逻辑
				XmlRootName = XmlDocumentName;
				xmlRootMap.put(XmlRootName, xmlRootList);
				HashMap RootMap = new HashMap();
				if(XmlDocumentName.equals(RootNames[0])){//主表
					List HeadList = new ArrayList();
					HeadList.add(datas.GetTableMap(XmlDocumentName));
					RootMap.put(XmlDocumentName, HeadList);
				}else{
					List tempList = datas.GetTableList(XmlDocumentName);
					if(SysUtility.isNotEmpty(XmlDisplayName)){
						HashMap tempChildMap = new HashMap();
						tempChildMap.put(XmlDisplayName, tempList);
						RootMap.put(XmlDocumentName, tempChildMap);
					}else{
						RootMap.put(XmlDocumentName, tempList);
					}
				}
				/**********************2  填充一级节点数据***************************************/
				xmlRootList.add(RootMap);
			}else{//有父级节点逻辑
				/**********************3  填充二级以下节点数据***************************************/
				Object obj = getXmlParentDocumentObj(xmlRootList, XmlParentDocument);
				if(obj instanceof Map){
					Map ParentMap = (Map)obj;
					
				}else if(obj instanceof List){
					List ParentLst = (List)obj;
					for (int k = 0; k < ParentLst.size(); k++) {
						HashMap tempMap = (HashMap)ParentLst.get(k);
						List childList = datas.GetTableList(XmlDocumentName);
						if(SysUtility.isEmpty(childList)){//填充空集合
							HashMap childMap = new HashMap();
							childList.add(childMap);
						}
						if(SysUtility.isNotEmpty(XmlDisplayName)){
							HashMap tempChildMap = new HashMap();
							tempChildMap.put(XmlDocumentName, childList);
							tempMap.put(XmlDisplayName, tempChildMap);
						}else{
							tempMap.put(XmlDocumentName, childList);
						}
					}
				}
			}
		}
		/**********************4  拼接标准的MessageHead***************************************/
		if(SysUtility.isNotEmpty(bean.getRootDefault())){
			Map senderMap = new HashMap();
			
			String messageTime = SysUtility.getSysDate();
			String messageSource = (String)senderMap.get("MESSAGE_SOURCE");
			String messageDest = (String)senderMap.get("MESSAGE_DEST");
			String signData = (String)senderMap.get("SIGN_DATA");
			String techRegCode = (String)senderMap.get("TECH_REG_CODE");
			String extendXml = (String)senderMap.get("EXTEND_XML");
			if(SysUtility.isNotEmpty(messageSource)){
				bean.setMessageSource(messageSource);
			}
			if(SysUtility.isNotEmpty(messageDest)){
				bean.setMessageDest(messageDest);
			}
			
			StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>").append("\n");
			xml.append("<"+bean.getRootDefault()+">").append("\n");
			xml.append("<MessageHead>").append("\n");
			xml.append("    <MESSAGE_ID>"+SysUtility.GetUUID()+"</MESSAGE_ID> ").append("\n");
			xml.append("    <MESSAGE_TYPE>"+bean.getMessageType()+"</MESSAGE_TYPE> ").append("\n");
			xml.append("    <MESSAGE_TIME>"+messageTime+"</MESSAGE_TIME> ").append("\n");
			xml.append("    <MESSAGE_SOURCE>"+bean.getMessageSource()+"</MESSAGE_SOURCE> ").append("\n");
			xml.append("    <MESSAGE_DEST>"+bean.getMessageDest()+"</MESSAGE_DEST> ").append("\n");
			xml.append("    <MESSAGE_VERSION>11</MESSAGE_VERSION> ").append("\n");
			xml.append("    <MESSAGE_CATEGORY>1.0</MESSAGE_CATEGORY> ").append("\n");
			if(SysUtility.isNotEmpty(signData)){
				xml.append("    <MESSAGE_SIGN_DATA>"+SysUtility.EncryptKeys(messageSource, messageTime,signData)+"</MESSAGE_SIGN_DATA> ").append("\n");
			}
			if(SysUtility.isNotEmpty(techRegCode)){
				xml.append("    <TECH_REG_CODE>"+techRegCode+"</TECH_REG_CODE> ").append("\n");
			}
			if(SysUtility.isNotEmpty(extendXml)){
				xml.append(extendXml).append("\n");
			}
			xml.append("  </MessageHead>").append("\n");
			xml.append("  <MessageBody>").append("\n");
			HashMap RootMap = new HashMap();
			if(xmlRootList.size() > 0){
				RootMap = (HashMap)xmlRootList.get(0);
			}
			xml.append(FileUtility.hashMapToAnyXml(XmlRootName,RootMap, 1));
			xml.append("  </MessageBody>").append("\n");
			xml.append("</"+bean.getRootDefault()+">").append("\n");
			
		}else{
		    String xml = "<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(XmlRootName,xmlRootMap, 0);
		}
		
		
		
	}
	
	public static List getDataList(HashMap HeadMap,HashMap childsMap,String[] RootNames,String xmlDocumentName,String XmlDisplayName,String IndxValue){
		if(RootNames[0].equals(xmlDocumentName) || RootNames[0].equals(XmlDisplayName)){
			List lst = new ArrayList();
			lst.add(HeadMap);
			return lst;
		}
		for (int k = 1; k < RootNames.length; k++) {
			if(RootNames[k].equals(xmlDocumentName) || RootNames[k].equals(XmlDisplayName)){
				if(SysUtility.isEmpty(childsMap.get(RootNames[k]))){
					break;
				}
				HashMap childMap = (HashMap)childsMap.get(RootNames[k]);
				return (List)childMap.get(IndxValue);
			}
		}
		return new ArrayList();
	}
	
	public static Object getXmlParentDocumentObj(List xmlRootList,String XmlParentDocument){
		Object obj = new Object();
		for (int i = 0; i < xmlRootList.size(); i++) {
			HashMap RootMap = (HashMap)xmlRootList.get(i);
			Set mapSet = RootMap.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
		        Object key = entry.getKey();
		        Object value = entry.getValue();
				if(key.equals(XmlParentDocument)){
					if(value instanceof Map){
						return (Map)value;
					}else if(value instanceof List){
						return (List)value;
					}
				}
				if(value instanceof Map) {
					HashMap map = (HashMap)value;
					Set mapSet2 = map.entrySet();
					for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
						Entry entry2 = (Entry)it2.next();
						Object key2 = entry2.getKey();
				        Object value2 = entry2.getValue();
				        if(key2.equals(XmlParentDocument) && value2 instanceof List){
				        	return (List)value2;
				        }
					}
				}else if(value instanceof List) {
					obj = getXmlParentDocumentObj((List)value, XmlParentDocument);
					if(obj instanceof Map){
						return (Map)obj;
					}else if(obj instanceof List){
						return (List)obj;
					}
				}
			}
		}
		return obj;
	}
	
	
	
}
