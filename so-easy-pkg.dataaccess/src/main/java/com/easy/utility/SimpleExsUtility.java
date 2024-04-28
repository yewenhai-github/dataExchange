package com.easy.utility;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.query.SQLHolder;
import com.easy.query.SQLMap;
import com.easy.query.SQLParser;
import com.easy.query.SimpleParamSetter;

public class SimpleExsUtility {
	
	
	public static void setExsConfigDbtoxmlSql(ServicesBean bean,final String Indx,Datas datas) throws LegendException{
		String SelectSQL = "SELECT S.* FROM exs_config_dbtoxml_sql S WHERE nvl(IS_ENABLED,'1') = '1' AND P_INDX = ? ORDER BY SEQ_NO";
		List SQLList = SQLExecUtils.query4List(SelectSQL, new Callback() {
			@Override
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(Indx));
			}
		});
		if(SysUtility.isEmpty(SQLList) || SQLList.size() == 0){
			LogUtil.printLog("exs_config_dbtoxml配置出错:exs_config_dbtoxml.INDX"+Indx, Level.INFO);
			return;
		}
		
		HashMap<String,String> SQLMap = new HashMap<String,String>();
		if("ComXml".equals(bean.getDataType())){
			String[] RootNames = bean.getRootNames().split(",");
			for (int j = 0; j < RootNames.length; j++) {
				HashMap temp = (HashMap)SQLList.get(j);
				StringBuffer xmlSql = new StringBuffer();
				xmlSql.append(temp.get("XML_SQL")).append(SysUtility.isEmpty(temp.get("XML_SQL1"))?"":temp.get("XML_SQL1")).append(SysUtility.isEmpty(temp.get("XML_SQL2"))?"":temp.get("XML_SQL2")).append(SysUtility.isEmpty(temp.get("XML_SQL3"))?"":temp.get("XML_SQL3"));
				if(j == 0){//主表SQL处理
					xmlSql.insert(0, "select x_.* from(");
					xmlSql.append(")x_,exs_handle_sender y_ where x_.").append(bean.getSerialName()).append(" = y_.msg_no and y_.msg_flag = '0' and y_.msg_type = '").append(bean.getMessageType()).append("' and rownum < 5000");
				}
				SQLMap.put(RootNames[j], xmlSql.toString());
			}
		}else if("AnyXml".equals(bean.getDataType())){//支持任意报文
			StringBuffer strRootName = new StringBuffer();
			for (int j = 0; j < SQLList.size(); j++) {
				HashMap temp = (HashMap)SQLList.get(j);
				String TableName = (String)temp.get("TABLE_NAME");
				String IndxName = (String)temp.get("INDX_NAME");
				String PIndxName = (String)temp.get("PINDX_NAME");
				
				if("3".equals(bean.getServiceType())){/********X-Link3.0 增加OpenApi模式***********/
					if(SysUtility.isEmpty(PIndxName)){//主表
						temp.put("XML_SQL", getOpenApiMainSql(bean, datas, temp));
					}else{//子表
						if(SysUtility.isNotEmpty(TableName)){
							temp.put("XML_SQL", "select * from "+TableName+" where "+PIndxName+" = #"+PIndxName+"#");
						}else if(SysUtility.isNotEmpty(temp.get("XML_SQL"))){
							temp.put("XML_SQL", ""+(SysUtility.isEmpty(temp.get("XML_SQL1"))?"":temp.get("XML_SQL1"))+(SysUtility.isEmpty(temp.get("XML_SQL2"))?"":temp.get("XML_SQL2"))+(SysUtility.isEmpty(temp.get("XML_SQL3"))?"":temp.get("XML_SQL3")));
						}
					}
					SQLMap.put((String)temp.get("XML_DOCUMENT_NAME"), (String)temp.get("XML_SQL"));
				}else if("2".equals(bean.getServiceType())){/********X-Link2.0 增加接口表模式***********/
					if(SysUtility.isEmpty(PIndxName)){
						temp.put("XML_SQL", "select c.* from "+TableName+" c ");
						temp.put("XML_DELETE_SQL", "delete from "+TableName+" c where "+IndxName+" = #"+IndxName+"#");
					}else{
						temp.put("XML_SQL", "select * from "+TableName+" where "+PIndxName+" = #"+PIndxName+"#");
						temp.put("XML_DELETE_SQL", "delete from "+TableName+" where "+PIndxName+" = #"+PIndxName+"#");
					}
					SQLMap.put((String)temp.get("XML_DOCUMENT_NAME"), (String)temp.get("XML_SQL"));
					SQLMap.put((String)temp.get("XML_DOCUMENT_NAME")+"_DELETE", (String)temp.get("XML_DELETE_SQL"));
				}else if(SysUtility.isNotEmpty(TableName)){/********X-Link1.1  增加Edi索引表模式***********/
					if(SysUtility.isEmpty(PIndxName) && "EdiItowNet".equals(bean.getServiceMode())){//主表,并从研发信城通Edi库生成报文
						temp.put("XML_SQL", "select c.*,e.edi_receive_index_id from "+TableName+" c,EDI_RECEIVE_INDEX_TABLE e where e.report_type='"+bean.getMessageType()+"' and e.status = '0' and e.table_name = '"+TableName+"'"+" and c."+IndxName+" = substr(e.condition,instr(e.condition,'''')+1,length(substr(e.condition,instr(e.condition,'''')+1))-1)");
					}else if(SysUtility.isEmpty(PIndxName) && SysUtility.isNotEmpty(bean.getPartId())){//主表，且以part_id区分配置及读取任务表
						temp.put("XML_SQL", "select c.* from "+TableName+" c,exs_handle_sender e where e.part_id = '"+bean.getPartId()+"' and e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName);
					}else if(SysUtility.isEmpty(PIndxName)){//主表
						temp.put("XML_SQL", "select c.* from "+TableName+" c,exs_handle_sender e where e.msg_type = '"+bean.getMessageType()+"' and e.msg_flag = 0 and e.msg_no = c."+IndxName);
				    }else{//子表
						temp.put("XML_SQL", "select * from "+TableName+" where "+PIndxName+" = #"+PIndxName+"#");
					}
					SQLMap.put((String)temp.get("XML_DOCUMENT_NAME"), (String)temp.get("XML_SQL"));
				}else if(SysUtility.isNotEmpty(temp.get("XML_SQL"))){/********X-Link1.0  自定义SQL***********/
					StringBuffer xmlSql = new StringBuffer();
					xmlSql.append(temp.get("XML_SQL")).append(SysUtility.isEmpty(temp.get("XML_SQL1"))?"":temp.get("XML_SQL1")).append(SysUtility.isEmpty(temp.get("XML_SQL2"))?"":temp.get("XML_SQL2")).append(SysUtility.isEmpty(temp.get("XML_SQL3"))?"":temp.get("XML_SQL3"));
					if(SysUtility.isEmpty(strRootName) && xmlSql.toString().toUpperCase().indexOf("exs_handle_sender") < 0 && !"EdiItowNet".equals(bean.getServiceMode())){
						//主表SQL处理
						xmlSql.insert(0, "select x_.* from(");
						xmlSql.append(")x_,exs_handle_sender y_ where x_.").append(bean.getSerialName()).append(" = y_.msg_no and y_.msg_flag = '0' and y_.msg_type = '").append(bean.getMessageType()).append("' and rownum < 5000");
					}
					SQLMap.put((String)temp.get("XML_DOCUMENT_NAME"), xmlSql.toString());
				}else{
					/********4.0 待扩展***********/
					continue;
				}
				strRootName.append(temp.get("XML_DOCUMENT_NAME")).append(",");
			}
			if(SysUtility.isNotEmpty(strRootName)){
				bean.setRootNames(strRootName.toString());//Edi模式，RootNames从子表拼接
			}
		}
		bean.setListSql(SQLList);
		bean.setMapSql(SQLMap);//取自定义配置的SQL
	}
	
	public static void setExsConfigDbtoxmlTest(ServicesBean bean,Datas datas) throws LegendException, JSONException{
		HashMap mainSqlMap = bean.getListSql().get(0);
		HashMap<String,String> SQLMap = bean.getMapSql();
		String mainRootName = (String)mainSqlMap.get("XML_DOCUMENT_NAME");//主表xml节点名称
		StringBuffer mainSQL = new StringBuffer();
		if(SysUtility.isNotEmpty(mainSqlMap.get("TABLE_NAME"))){
			mainSQL.append("select * from "+mainSqlMap.get("TABLE_NAME")+" where 1 = 1 ");
		}else{
			StringBuffer xmlSql = new StringBuffer();
			xmlSql.append(mainSqlMap.get("XML_SQL")).append(SysUtility.isEmpty(mainSqlMap.get("XML_SQL1"))?"":mainSqlMap.get("XML_SQL1")).append(SysUtility.isEmpty(mainSqlMap.get("XML_SQL2"))?"":mainSqlMap.get("XML_SQL2")).append(SysUtility.isEmpty(mainSqlMap.get("XML_SQL3"))?"":mainSqlMap.get("XML_SQL3"));
			mainSQL.append(xmlSql);
		}
		
		String receivedMode = datas.GetTableValue("MessageHead", "RECEIVED_MODE");
		if("1".equals(receivedMode)){
			mainSQL.append(" and rownum <= 20 ");
		}else if("2".equals(receivedMode)){
			mainSQL.append(" and rownum <= 100 ");
		}else{
			mainSQL.append(" and rownum = 1 ");
		}
		mainSqlMap.put("XML_SQL", mainSQL.toString());
		SQLMap.put(mainRootName, mainSQL.toString());
	}
	
	public static String getOpenApiMainSql(ServicesBean bean,Datas datas,HashMap temp){
		StringBuffer mainSQL = new StringBuffer();
		try {
			String dataFilterReg = bean.getDataFilterReg();
			String receivedMode = datas.GetTableValue("MessageHead", "RECEIVED_MODE");
			String messageSource = datas.GetTableValue("MessageHead", "MESSAGE_SOURCE");
			String messageType = datas.GetTableValue("MessageHead", "MESSAGE_TYPE");
			String serialName = bean.getSerialName();
			String TableName = (String)temp.get("TABLE_NAME");
			
			if(SysUtility.isNotEmpty(TableName)){
				mainSQL.append("select * from "+TableName+" where 1 = 1");
			}else{
				mainSQL.append(temp.get("XML_SQL")).append(SysUtility.isEmpty(temp.get("XML_SQL1"))?"":temp.get("XML_SQL1")).append(SysUtility.isEmpty(temp.get("XML_SQL2"))?"":temp.get("XML_SQL2")).append(SysUtility.isEmpty(temp.get("XML_SQL3"))?"":temp.get("XML_SQL3"));
			}
			mainSQL.append(" and is_enabled = '1' ");
			mainSQL.append(" and not exists(select 0 from exs_config_apitoserver_map where message_source = '"+messageSource+"' and message_type = '"+messageType+"' and msg_no = "+serialName+")");
			if("1".equals(receivedMode)){
				mainSQL.append(" and rownum <= 20 ");
			}else if("2".equals(receivedMode)){
				mainSQL.append(" and rownum <= 100 ");
			}else{
				mainSQL.append(" and rownum = 1 ");
			}
			if(SysUtility.isNotEmpty(dataFilterReg) && dataFilterReg.indexOf("EXP_MESSAGE_SOURCE") >= 0){
				mainSQL.append(" and exp_message_source = '"+datas.GetTableValue("MessageHead", "MESSAGE_SOURCE")+"'");
			}else{
				mainSQL.append(" and message_source = '"+datas.GetTableValue("MessageHead", "MESSAGE_SOURCE")+"'");
			}
			if(SysUtility.isNotEmpty(dataFilterReg)){//查询条件规则为空时候，直接拼接标准的接入方代码
				JSONObject mainData = datas.GetTable("SEARCH_TABLE");
				Iterator<?> keys = mainData.keys();
				while(keys.hasNext()){
					String key = keys.next().toString();
					if(SysUtility.isNotEmpty(dataFilterReg) && dataFilterReg.indexOf(key) >= 0){
						mainSQL.append(" and "+key+ " = '"+SysUtility.getJsonField(mainData, key)+"'");
					}
				}
			}
		} catch (LegendException e) {
			mainSQL.delete(0, mainSQL.length());
			LogUtil.printLog("OpenApi MainSql初始化出错！"+e.getMessage(), Level.ERROR);
		}
		return mainSQL.toString();
	}
	public static List GetCacheData(ServicesBean bean,IDataAccess DataAccess,HashMap childsMap) throws LegendException{
		List MainDatas = new ArrayList();
		
		String[] RootNames = bean.getRootNames().split(",");
		String MainNames = RootNames[0];
		HashMap<String,String> mapSql = bean.getMapSql();
		
		String MainSQL = SysUtility.isEmpty(mapSql.get(MainNames))?SQLMap.getSelect(MainNames):mapSql.get(MainNames);
		if(SysUtility.isEmpty(MainSQL)){
			return MainDatas;
		}
		JSONObject param = bean.getSearchParam();
		SQLHolder holder = SQLParser.parse(MainSQL, param);
		MainDatas.addAll(SQLExecUtils.query4List(holder.getSql(),new SimpleParamSetter(holder.getParamList()),bean.getBlobProcess()));
		if(SysUtility.isEmpty(MainDatas)){
			return MainDatas;
		}
		if("ComXml".equals(bean.getDataType())){
			childsMap.putAll(CacheChildData(bean, DataAccess, MainDatas));//二级节点场景，缓存子表数据。
		}
		return MainDatas;
	}
	
	public static final String INSERT_TEMP_ID5 = "INSERT INTO exs_temp_id T(ID,ID1,ID2,ID3,ID4,ID5) VALUES(?,?,?,?,?,?)";
	public static HashMap CacheChildData(ServicesBean bean, IDataAccess DataAccess,List MainDatas) throws LegendException{
		String[] RootNames = bean.getRootNames().split(",");
		HashMap<String,String> mapSql = bean.getMapSql();
		String Indx = bean.getIndxName();
		String Pindx = bean.getPindxName();
		
		DataAccess.BeginTrans();
		HashMap childsMap = new HashMap();
		for (int i = 0; i < MainDatas.size(); i++) {
			HashMap map = (HashMap)MainDatas.get(i);
			if(SysUtility.isEmpty(map.get(bean.getSerialName()))){
				LogUtil.printLog("主sql查询中中未返回关键字段："+bean.getSerialName()+"的值", Level.INFO);
				continue;
			}
			DataAccess.ExecSQL(INSERT_TEMP_ID5,new String[]{(String)map.get(Indx),(String)map.get("ID1"),(String)map.get("ID2"),(String)map.get("ID3"),(String)map.get("ID4"),(String)map.get("ID5")});
		}
		for (int i = 1; i < RootNames.length; i++) {
			String ChildSQL = mapSql.get(RootNames[i]);
			if(SysUtility.isEmpty(ChildSQL)){
				ChildSQL = SQLMap.getSelect(RootNames[i]);
			}
			if(SysUtility.isEmpty(ChildSQL)){
				continue;
			}
			if(ChildSQL.toUpperCase().indexOf("exs_temp_id") < 0){
				String p_indx = "p_indx";
				String PindxName = bean.getPindxName();
				if(SysUtility.isNotEmpty(PindxName) && PindxName.split(",").length >= RootNames.length - 1){
					p_indx = PindxName.split(",")[i-1];
				}
				ChildSQL = "select x_.* from("+ChildSQL+")x_,exs_temp_id g where g.id = x_."+p_indx;
			}
			List childList = SQLExecUtils.query4List(ChildSQL);
			if(childList.size() <= 0 ){
				continue;
			}
			HashMap childMap = new HashMap();
			groupChild(childList, childMap, Pindx);
			childsMap.put(RootNames[i], childMap);
		}
		return childsMap;
	}
	
	public static void groupChild(List childList, Map childMap,String P_INDX) {
		Iterator it = childList.iterator();
		while (it.hasNext()) {
			Map tmpMap = (Map) it.next();
			String pIndx = (String) tmpMap.get(P_INDX);
			if (childMap.containsKey(pIndx)) {
				((List) childMap.get(pIndx)).add(tmpMap);
			} else {
				List tmpLst = new ArrayList();
				tmpLst.add(tmpMap); 
				childMap.put(pIndx, tmpLst);
			}
		}
	}
	
	
	
	public static String GetAnyXmlData(ServicesBean bean,List MainDatas,HashMap childsMap) throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, UnsupportedEncodingException{
		if(SysUtility.isEmpty(MainDatas)){
			return "";
		}
		
		Map xmlRootMap = GetAnyXmlRootMap(bean, MainDatas, childsMap);
		
		if(SysUtility.isNotEmpty(bean.getRootDefault())){
			Map senderMap = GetSenderMap(bean, (HashMap)MainDatas.get(0));
			String messageTime = SysUtility.getSysDate();
			String messageSource = (String)senderMap.get("MESSAGE_SOURCE");
			String messageDest = (String)senderMap.get("MESSAGE_DEST");
			String messageVersion = (String)senderMap.get("MESSAGE_VERSION");
			String messageCategory = (String)senderMap.get("MESSAGE_CATEGORY");
			String signData = (String)senderMap.get("SIGN_DATA");
			String techRegCode = (String)senderMap.get("TECH_REG_CODE");
			String extendXml = (String)senderMap.get("EXTEND_XML");
			
			StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>").append("\n");
			xml.append("<"+bean.getRootDefault()+">").append("\n");
			xml.append("<MessageHead>").append("\n");
			xml.append("    <MESSAGE_ID>"+SysUtility.GetUUID()+"</MESSAGE_ID> ").append("\n");
			xml.append("    <MESSAGE_TYPE>"+bean.getMessageType()+"</MESSAGE_TYPE> ").append("\n");
			xml.append("    <MESSAGE_TIME>"+messageTime+"</MESSAGE_TIME> ").append("\n");
			xml.append("    <MESSAGE_SOURCE>"+bean.getMessageSource()+"</MESSAGE_SOURCE> ").append("\n");
			xml.append("    <MESSAGE_DEST>"+bean.getMessageDest()+"</MESSAGE_DEST> ").append("\n");
			if(SysUtility.isNotEmpty(messageVersion)){
				xml.append("    <MESSAGE_VERSION>"+messageVersion+"</MESSAGE_VERSION> ").append("\n");
			}
			if(SysUtility.isNotEmpty(messageCategory)){
				xml.append("    <MESSAGE_CATEGORY>"+messageCategory+"</MESSAGE_CATEGORY> ").append("\n");
			}
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
			
			List list = (List)xmlRootMap.get(bean.getXmlRoot());
			for (int i = 0; i < list.size(); i++) {
				HashMap RootMap = (HashMap)list.get(i);
				xml.append(FileUtility.hashMapToAnyXml(bean.getXmlRoot(),RootMap, 1));
			}
			
			xml.append("  </MessageBody>").append("\n");
			xml.append("</"+bean.getRootDefault()+">").append("\n");
			return xml.toString();
		}else{
			return "<?xml version=\"1.0\" encoding=\""+bean.getEncoding()+"\"?>"+ "\n" + FileUtility.hashMapToAnyXml(bean.getXmlRoot(),xmlRootMap, 0);
		}
	}
	
	public static Map GetAnyXmlRootMap(ServicesBean bean,List MainDatas,HashMap childsMap) throws LegendException, JSONException, SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, UnsupportedEncodingException{
		String[] RootNames = bean.getRootNames().split(",");
		List ListSql = bean.getListSql();
		HashMap SQLMap = bean.getMapSql();
		
		Map xmlRootMap = new HashMap();
		/**********************1  填充Root节点数据***************************************/
		for (int i = 0; i < MainDatas.size(); i++) {
			HashMap MainData = (HashMap)MainDatas.get(i);
			List xmlRootList = new ArrayList();
			String XmlRootName = "";
			for (int j = 0; j < ListSql.size(); j++) {
				HashMap sqlMap = (HashMap)ListSql.get(j);
				String SeqNo = (String)sqlMap.get("SEQ_NO");
				String XmlDocumentName = (String)sqlMap.get("XML_DOCUMENT_NAME");
				String XmlDisplayName = (String)sqlMap.get("XML_DISPLAY_NAME");
				String XmlParentDocument = (String)sqlMap.get("XML_PARENT_DOCUMENT");
				String HiddenStr = (String)sqlMap.get("XML_COULMN_HIDDEND");
				HashMap MappingMap = GetMappingMap(sqlMap);//获取Mapping字段集合
				String xmlSql = (String)SQLMap.get(XmlDocumentName);
				String DeleteSql = (String)SQLMap.get(XmlDocumentName+"_DELETE");
				
				if("0".equals(SeqNo) && SysUtility.isEmpty(XmlParentDocument)){//任意报文自定义Root，子表第一条配置为根节点,并默认读取出第2条配置取出组装主表数据。
					XmlRootName = XmlDocumentName;
					if(SysUtility.isEmpty(xmlRootMap.get(XmlRootName))){
						xmlRootMap.put(XmlRootName, xmlRootList);
					}else{
						xmlRootList = (List)xmlRootMap.get(XmlRootName);
					}
					HashMap mainMap = (HashMap)ListSql.get(++j);
					String mainXmlDocumentName = (String)mainMap.get("XML_DOCUMENT_NAME");
					HiddenStr = (String)mainMap.get("XML_COULMN_HIDDEND");
					MappingMap = GetMappingMap(mainMap);//获取Mapping字段集合
					
					List RootList = new ArrayList();
					HashMap RootMap = new HashMap();
					List HeadList = new ArrayList();
					HeadList.add(MainData);
					CoulmnHiddenInvoke(HeadList, HiddenStr);//字段Hidden
					CoulmnMappingInvoke(HeadList, MappingMap);//字段Mapping转换
					RootMap.put(mainXmlDocumentName, HeadList);
					xmlRootList.add(RootMap);
					DeleteMainData(bean, SQLMap, MainData);//接口表模式删除数据
				}else if("0".equals(SeqNo) && SysUtility.isNotEmpty(XmlParentDocument)){//任意报文自定义Root，子表第一条配置的XmlParentDocument为root名称并将组装主表数据
					XmlRootName = XmlParentDocument;
					if(SysUtility.isEmpty(xmlRootMap.get(XmlRootName))){
						xmlRootMap.put(XmlRootName, xmlRootList);
					}else{
						xmlRootList = (List)xmlRootMap.get(XmlRootName);
					}
					
					HashMap RootMap = new HashMap();
					List HeadList = new ArrayList();
					HeadList.add(MainData);
					CoulmnHiddenInvoke(HeadList, HiddenStr);//字段Hidden
					CoulmnMappingInvoke(HeadList, MappingMap);//字段Mapping转换
					RootMap.put(XmlDocumentName, HeadList);
					xmlRootList.add(RootMap);
					DeleteMainData(bean, SQLMap, MainData);//接口表模式删除数据
				}else if(SysUtility.isEmpty(XmlParentDocument)){//无父级节点逻辑
					XmlRootName = XmlDocumentName;
					if(SysUtility.isEmpty(xmlRootMap.get(XmlRootName))){
						xmlRootMap.put(XmlRootName, xmlRootList);
					}else{
						xmlRootList = (List)xmlRootMap.get(XmlRootName);
					}
					HashMap RootMap = new HashMap();
					if(XmlDocumentName.equals(RootNames[0])){//主表
						List HeadList = new ArrayList();
						HeadList.add(MainData);
						CoulmnHiddenInvoke(HeadList, HiddenStr);//字段Hidden
						CoulmnMappingInvoke(HeadList, MappingMap);//字段Mapping转换
						RootMap.put(XmlDocumentName, HeadList);
					}else{
						List tempList = getDataList(MainData,childsMap, RootNames, XmlDocumentName,XmlDisplayName, (String)MainData.get(bean.getIndxName()));
						if(SysUtility.isEmpty(tempList) && SysUtility.isNotEmpty(xmlSql)){
							JSONObject param2 = SysUtility.MapToJSONObject(MainData);
							SQLHolder holder2 = SQLParser.parse(xmlSql, param2);
							tempList = SQLExecUtils.query4List(holder2.getSql(),new SimpleParamSetter(holder2.getParamList()),bean.getBlobProcess());
						}
						CoulmnHiddenInvoke(tempList, HiddenStr);//字段Hidden
						CoulmnMappingInvoke(tempList, MappingMap);//字段Mapping转换
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
					DeleteMainData(bean, SQLMap, MainData);//接口表模式删除数据
				}else{//有父级节点逻辑
					/**********************3  填充二级以下节点数据***************************************/
					Object obj = getXmlParentDocumentObj(xmlRootList, XmlParentDocument);
					if(obj instanceof Map){
						Map ParentMap = (Map)obj;
						
					}else if(obj instanceof List){
						List ParentLst = (List)obj;
						for (int k = 0; k < ParentLst.size(); k++) {
							HashMap tempMap = (HashMap)ParentLst.get(k);
							List childList = getDataList(MainData,childsMap, RootNames, XmlDocumentName,XmlDisplayName, (String)MainData.get(bean.getIndxName()));
							if(SysUtility.isEmpty(childList) && SysUtility.isNotEmpty(xmlSql)){
								JSONObject param2 = SysUtility.MapToJSONObject(tempMap);
								SQLHolder holder2 = SQLParser.parse(xmlSql, param2);
								childList = SQLExecUtils.query4List(holder2.getSql(),new SimpleParamSetter(holder2.getParamList()),bean.getBlobProcess());
								DeleteChildData(bean, DeleteSql, param2);//接口表模式删除子表数据
							}
							CoulmnHiddenInvoke(childList, HiddenStr);//字段Hidden
							CoulmnMappingInvoke(childList, MappingMap);//字段Mapping转换
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
			bean.setXmlRoot(XmlRootName);
		}
		
		return xmlRootMap;
	}
	
	public static Map GetSenderMap(ServicesBean bean,HashMap MainData) throws LegendException, JSONException {
		Map senderMap = new HashMap();
		SQLBuild sqlBuild = SQLBuild.getInstance();
		if(SysUtility.isNotEmpty(bean.getMessageHeadSql())){
			JSONObject param = SysUtility.MapToJSONObject(MainData);
			SQLHolder holder = SQLParser.parse(bean.getMessageHeadSql(), param);
			senderMap = SQLExecUtils.query4Map(holder.getSql(),new SimpleParamSetter(holder.getParamList()));
		}else if("EdiItowNet".equals(bean.getServiceMode())){
			sqlBuild.append("select * from edi_receive_index_table where status = 0 ");
			sqlBuild.append("and report_type = ?", bean.getMessageType());
			sqlBuild.append("and EDI_RECEIVE_INDEX_ID  = ?",bean.getSerialNo());
			senderMap = sqlBuild.query4Map();
		}else if("2".equals(bean.getServiceType())){
			//TODO 接口表模式，待扩展
		}else{
			sqlBuild.append("select * from exs_handle_sender where msg_flag = 0 ");
			sqlBuild.append("and msg_type = ?", bean.getMessageType());
			sqlBuild.append("and msg_no = ?", bean.getSerialNo());
			senderMap = sqlBuild.query4Map();
		}
		String messageSource = (String)senderMap.get("MESSAGE_SOURCE");
		String messageDest = (String)senderMap.get("MESSAGE_DEST");
		if(SysUtility.isNotEmpty(messageSource)){
			bean.setMessageSource(messageSource);
		}
		if(SysUtility.isNotEmpty(messageDest)){
			bean.setMessageDest(messageDest);
		}
		return senderMap;
	}
	
	public static HashMap GetMappingMap(HashMap sqlMap){
		HashMap MappingMap = new HashMap();
		try {
			String XmlCoulmnMapping = (SysUtility.isEmpty(sqlMap.get("XML_COULMN_MAPPING"))?"":(String)sqlMap.get("XML_COULMN_MAPPING"))+(SysUtility.isEmpty(sqlMap.get("XML_COULMN_MAPPING1"))?"":(String)sqlMap.get("XML_COULMN_MAPPING1"))+(SysUtility.isEmpty(sqlMap.get("XML_COULMN_MAPPING2"))?"":(String)sqlMap.get("XML_COULMN_MAPPING2"));
			if(SysUtility.isNotEmpty(XmlCoulmnMapping)){
				String[] XmlCoulmnMappings = XmlCoulmnMapping.split(",");
				for (int i = 0; i < XmlCoulmnMappings.length; i++) {
					MappingMap.put(XmlCoulmnMappings[i].split("=")[0], XmlCoulmnMappings[i].split("=")[1]);
				}
			}
		} catch (Exception e) {
			LogUtil.printLog("Mapping Reg Error："+e.getMessage(), Level.ERROR);
		}
		return MappingMap;
	}
	
	public static void CoulmnHiddenInvoke(List dateList,String HiddenStr){
		if(SysUtility.isEmpty(dateList) || SysUtility.isEmpty(HiddenStr)){
			return;
		}
		String[] HiddenStrs = HiddenStr.split(",");
		
		for (int i = 0; i < dateList.size(); i++) {
			HashMap rowMap = (HashMap)dateList.get(i);
			for (int j = 0; j < HiddenStrs.length; j++) {
				rowMap.remove(HiddenStrs[j]);
			}
		}
	}
	
	public static void CoulmnMappingInvoke(List childList,HashMap MappingMap){
		if(SysUtility.isEmpty(childList) || SysUtility.isEmpty(MappingMap)){
			return;
		}
		
		for (int i = 0; i < childList.size(); i++) {
			HashMap rowMap = (HashMap)childList.get(i);
			HashMap tempMap = new HashMap();
			Set mapSet = rowMap.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
		        if(SysUtility.isNotEmpty(MappingMap.get(key))){
		        	tempMap.put(MappingMap.get(key), value);
		        }
			}
			//填充转换后的字段
			rowMap.putAll(tempMap);
			//移除转换前的字段
			Set mapSet2 = MappingMap.entrySet();
			for (Iterator it = mapSet2.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
				Object key = entry.getKey();
				rowMap.remove(key);
			}
		}
	}
	
	public static void DeleteMainData(ServicesBean bean,HashMap SQLMap,HashMap MainData) throws LegendException, JSONException {
		if(!"2".equals(bean.getServiceType())){
			return;
		}
		
		List ListSql = bean.getListSql();
		HashMap sqlMap2 = (HashMap)ListSql.get(0);
		String DeleteSql2 = (String)SQLMap.get(sqlMap2.get("XML_DOCUMENT_NAME")+"_DELETE");
		DeleteData(DeleteSql2, MainData);
	}
	
	public static boolean DeleteData(String DeleteSQL,Object obj) throws LegendException, JSONException {
		JSONObject root = null;
		if(obj instanceof JSONObject){
			root = (JSONObject)obj;
		}else if(obj instanceof Map){
			root = SysUtility.MapToJSONObject((HashMap)obj);
		}
		
		List pList = new  ArrayList();
		int i = 0;
		while(true){
			int bIndx = DeleteSQL.indexOf("#");
			if(bIndx > -1){
				int eIndx = DeleteSQL.indexOf("#", bIndx+1);
				String param = DeleteSQL.substring(bIndx+1, eIndx);
				if(SysUtility.isEmpty(SysUtility.getJsonField(root, param))){
					String partStr=DeleteSQL.substring(0,bIndx).trim();
					partStr=partStr.substring(0,  partStr.length()-1).trim();
					String field="";
					if(partStr.lastIndexOf(',')>-1){
						field=partStr.substring(partStr.lastIndexOf(',')+1).trim();
					}
					else{
						field=partStr.substring(partStr.lastIndexOf(' ')+1) ;
					}
					DeleteSQL = DeleteSQL.replaceAll("#"+param+"#", field);//如果回执报文中不存在这个字段需要的值就不更新
				}else{
					DeleteSQL = DeleteSQL.replaceAll("#"+param+"#", "?");
					pList.add(SysUtility.getJsonField(root, param));
				}
			}else{
				break;//没有需要替换的参数
			}
		}
		String[] params = new String[pList.size()];
		for (int j = 0; j < pList.size(); j++) {
			params[j] = (String)pList.get(j);
		}
		return SQLExecUtils.executeUpdate(SysUtility.getCurrentConnection(), DeleteSQL, params);
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
	
	public static void DeleteChildData(ServicesBean bean,String DeleteSql,JSONObject params) throws LegendException, JSONException {
		if(!"2".equals(bean.getServiceType())){
			return;
		}
		
		DeleteData(DeleteSql, params);
	}
	
	
}
