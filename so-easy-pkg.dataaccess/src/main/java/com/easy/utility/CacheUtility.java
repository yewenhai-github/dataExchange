package com.easy.utility;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;

public class CacheUtility {
	private static Map<String,Map> apiToServerMap = new HashMap();
	private static Map<String,Map> accessCustomerMap = new HashMap();
	private static Map<String,String> accessCustomerPemMap = new HashMap<String,String>();
	private static Map<String,Map> accessServerMap = new HashMap();
	private static Map<String,List> accessLimitMap = new HashMap();
	private static Map configMapping = new HashMap();
	
	static{
		loadAccessServerMap();
		loadAccessLimitList();
	}
	public static Map getApiToServerMap(final String MessageType){
		SysUtility.OutDate5MinuteReset(apiToServerMap);
		
		Map map = new HashMap();
		try {
			String key = MessageType;
			if(SysUtility.isNotEmpty(apiToServerMap.get(key))){
				map = (HashMap)apiToServerMap.get(key);
			}else{
				map = (HashMap) SQLExecUtils.query4Map("SELECT * FROM exs_config_api_polling WHERE IS_ENABLED = '1' AND Message_Type = ? and rownum = 1", new Callback() {
					@Override
					public void doIn(PreparedStatement ps) throws SQLException {
						ps.setString(1, MessageType);
					}
				});
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return map;
	}
	
	public static Map getCustomerMap(final String messageType,final String messageSource,final String techRegCode){
		SysUtility.OutDate5MinuteReset(accessCustomerMap);
		
		Map map = new HashMap();
		try {
			String key = messageType+"|"+messageSource+"|"+techRegCode;
			if(SysUtility.isEmpty(techRegCode)){
				key = messageType+messageSource;
			}
			
			if(SysUtility.isNotEmpty(accessCustomerMap.get(key))){
				map = (HashMap)accessCustomerMap.get(key);
			}else{
				if(SysUtility.isEmpty(techRegCode)){
					map = GetAccessCustomerMap(messageType,messageSource);
					if(SysUtility.isNotEmpty(map) && SysUtility.isEmpty(map.get("MESSAGE_TYPE"))){
						map.put("MESSAGE_TYPE", messageType);
					}
				}else{
					map = GetAccessCustomerMap(messageType,messageSource,techRegCode);
				}
				accessCustomerMap.put(key, map);
			}
		} catch (LegendException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return map;
	}
	
	public static String getPemMap(String sourceId){
		SysUtility.OutDate5MinuteReset(accessCustomerPemMap);
		
		if(SysUtility.isEmpty(accessCustomerPemMap.get(sourceId))){
			SQLBuild sqlBuild = SQLBuild.getInstance();
			sqlBuild.append("select pem from exs_access_customer_pem where source_id = ?", sourceId);
			accessCustomerPemMap.put(sourceId, sqlBuild.query4String());
		}
		return accessCustomerPemMap.get(sourceId);
	}
	
	public static Map getAccessServer(String messageType){
		if(SysUtility.OutDate5Minute()){
			loadAccessServerMap();
		}
		
		Map map = accessServerMap.get(messageType);
		if(SysUtility.isEmpty(map)){
			map = new HashMap();
		}
		return map;
	}
	
	public static List getAccessLimitList(String clientIp){
		if(SysUtility.OutDate5Minute()){
			loadAccessLimitList();
		}
		List list = accessLimitMap.get(clientIp);
		if(SysUtility.isEmpty(list)){
			list = new ArrayList();
		}
		return list;
	}
	
	public static Map GetAccessCustomerMap(String MessageType,String MessageSource) throws LegendException{
		List lst = GetAccessCustomerList(MessageType, MessageSource, "");
		if(SysUtility.isNotEmpty(lst) && lst.size() >= 1){
			return (HashMap)lst.get(0);
		}
		lst = GetAccessCustomerList("", MessageSource, "");
		if(SysUtility.isNotEmpty(lst) && lst.size() >= 1){
			return (HashMap)lst.get(0);
		}
		return new HashMap();
	}
	
	public static Map GetAccessCustomerMap(final String MessageType,final String MessageSource,String SourceId) throws LegendException{
		List lst = GetAccessCustomerList(MessageType, MessageSource, "");
		for (int i = 0; i < lst.size(); i++) {
			Map map = (HashMap)lst.get(i);
			String tempSourceId = (String)map.get("SOURCE_ID");
			if(SysUtility.isEmpty(tempSourceId) || tempSourceId.equals(SourceId)){
				return map;
			}
		}
		return new HashMap();
	}
	
	public static List GetAccessCustomerList(final String MessageType,final String MessageSource,String SourceId) throws LegendException{
		if(SysUtility.isEmpty(MessageType) && SysUtility.isEmpty(MessageSource) && SysUtility.isEmpty(SourceId)){
			return new ArrayList();
		}
		
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select * from exs_access_customer where nvl(IS_ENABLED,'1') = '1'");
		if(SysUtility.isNotEmpty(MessageType)){
			sqlBuild.append(" and message_type = ?",MessageType);
		}else{
			sqlBuild.append(" and message_type is null ");
		}
		if(SysUtility.isNotEmpty(MessageSource)){
			sqlBuild.append(" and message_source = ?",MessageSource);
		}
		if(SysUtility.isNotEmpty(SourceId)){
			sqlBuild.append(" and source_id = ?",SourceId);
		}
		return sqlBuild.query4List();
	}
	
	
	
	public static void loadAccessServerMap(){
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select * from exs_access_server where is_enabled = '1' ");
		List list = sqlBuild.query4List();
		for (int i = 0; i < list.size(); i++) {
			HashMap map = (HashMap)list.get(i);
			String messageType = (String)map.get("MESSAGE_TYPE");
			accessServerMap.put(messageType, map);
		}
	}
	
	public static void loadAccessLimitList(){
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select * from exs_access_limit where is_enabled = '1' and limit_flag = '1' ");
		List list = sqlBuild.query4List();
		for (int i = 0; i < list.size(); i++) {
			HashMap map = (HashMap)list.get(i);
			String clientIp = (String)map.get("CLIENT_IP");
			if(SysUtility.isNotEmpty(accessLimitMap.get(clientIp))){
				List tempList = accessLimitMap.get(clientIp);
				tempList.add(map);
			}else{
				List tempList = new ArrayList();
				tempList.add(map);
				accessLimitMap.put(clientIp, tempList);
			}
		}
	}
	
	public static HashMap GetMappingCode(String DataType,String DocumentName, String messageType){
		List list = GetConfigMapping(DataType, DocumentName, messageType);
		
		HashMap tempMap = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			HashMap map = (HashMap)list.get(i);
			String mappSourceCode = (String)map.get("MAPP_SOURCE_CODE");
			String mappTargetCode = (String)map.get("MAPP_TARGET_CODE");
			tempMap.put(mappSourceCode, mappTargetCode);
		}
		return tempMap;
	}
	
	public static List GetConfigMapping(String DataType,String DocumentName, String messageType){
		SysUtility.OutDate5MinuteReset(configMapping);
		
		String key = DataType+"|"+DocumentName;
		if(SysUtility.isNotEmpty(configMapping.get(key))){
			return (List)configMapping.get(key);
		}
		
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select * from exs_config_mapping where is_enabled = '1'");
		sqlBuild.append("and Data_Type = ?",DataType);
		sqlBuild.append("and Document_Name = ?",DocumentName);
		sqlBuild.append("and Message_Type = ?",messageType);
		sqlBuild.append(" order by mapp_seq_no");
		List list = sqlBuild.query4List();
		configMapping.put(key, list);
		return list;
	}
}
