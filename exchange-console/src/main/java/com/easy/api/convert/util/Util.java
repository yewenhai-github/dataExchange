package com.easy.api.convert.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
import com.easy.access.IDataAccess;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;

public class Util {
	public static void main(String[] args) {
//		AddMessLog(null, "1", "2", "3", "4", "5", "6");
	}
	
	public static boolean AddMessLog(IDataAccess access,Map dataMap) throws JSONException, LegendException {
		try {
		access.BeginTrans();
		java.util.Date a = new java.util.Date();
		JSONObject jsonObject =new JSONObject();
		String SERIAL_NO = "";
		String FILE_NAME = "";
		if(SysUtility.isNotEmpty(dataMap.get("DATA_SOURCE"))) {
			jsonObject.put("DATA_SOURCE", dataMap.get("DATA_SOURCE"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("SERIAL_NO"))) {
			jsonObject.put("SERIAL_NO", dataMap.get("SERIAL_NO"));
			SERIAL_NO = (String) dataMap.get("SERIAL_NO");
		}
		if(SysUtility.isNotEmpty(dataMap.get("TARGET_FILE_NAME"))) {
			jsonObject.put("TARGET_FILE_NAME", dataMap.get("TARGET_FILE_NAME"));
			FILE_NAME = (String) dataMap.get("TARGET_FILE_NAME");
		}
		if(SysUtility.isNotEmpty(dataMap.get("SOURCE_FILE_NAME"))) {
			jsonObject.put("SOURCE_FILE_NAME", dataMap.get("SOURCE_FILE_NAME"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("CONFIG_PATH_ID"))) {
			jsonObject.put("CONFIG_PATH_ID", dataMap.get("CONFIG_PATH_ID"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("FILE_PATH"))) {
			jsonObject.put("FILE_PATH", dataMap.get("FILE_PATH"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("PROCESS_MSG"))) {
			jsonObject.put("PROCESS_MSG", dataMap.get("PROCESS_MSG"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("TRANSFORMATION_CODE"))) {
			jsonObject.put("TRANSFORMATION_CODE", dataMap.get("TRANSFORMATION_CODE"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("TRANSFORMATION_NAME"))) {
			jsonObject.put("TRANSFORMATION_NAME", dataMap.get("TRANSFORMATION_NAME"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("SEND_CODE"))) {
			jsonObject.put("SEND_CODE", dataMap.get("SEND_CODE"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("SEND_NAME"))) {
			jsonObject.put("SEND_NAME", dataMap.get("SEND_NAME"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("SEND_TIME"))) {
			jsonObject.put("SEND_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		}
		if(SysUtility.isNotEmpty(dataMap.get("RECEIPT_RESULT"))) {
			jsonObject.put("RECEIPT_RESULT", dataMap.get("RECEIPT_RESULT"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("SOURCE_BACK_PATH"))) {
			jsonObject.put("SOURCE_BACK_PATH", dataMap.get("SOURCE_BACK_PATH"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("SUCCESS_BACK_PATH"))) {
			jsonObject.put("SUCCESS_BACK_PATH", dataMap.get("SUCCESS_BACK_PATH"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("REMARKS"))) {
			jsonObject.put("REMARKS", dataMap.get("REMARKS"));
		}
		if(SysUtility.isNotEmpty(dataMap.get("RECEIPT_TIME"))) {
			jsonObject.put("RECEIPT_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		}
		
		if(SysUtility.isNotEmpty(dataMap.get("TRANSFORMATION_TIME"))) {
			jsonObject.put("TRANSFORMATION_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		}
		
		String indx = "";
		String filename = "";
		String SOURCE_FILE_NAME = "";
		//查询是否存在 
		String sql = "SELECT INDX,SOURCE_FILE_NAME FROM exs_convert_log WHERE SERIAL_NO='"+SERIAL_NO+"' AND TARGET_FILE_NAME='"+FILE_NAME+"'";
		List query4List = SQLExecUtils.query4List(sql);
		if(query4List.size()>0) {
			indx = (String) ((HashMap)query4List.get(0)).get("INDX");
			filename = (String) ((HashMap)query4List.get(0)).get("SUCCESS_BACK_PATH");
			SOURCE_FILE_NAME = (String) ((HashMap)query4List.get(0)).get("SOURCE_FILE_NAME");
			if(SysUtility.isNotEmpty(filename)) {
				jsonObject.put("SUCCESS_BACK_PATH", filename+","+dataMap.get("SUCCESS_BACK_PATH"));
			}
			jsonObject.put("INDX", indx);
		}
		if(SysUtility.isEmpty(indx)) {
			if(SysUtility.isEmpty(dataMap.get("SOURCE_FILE_NAME"))) {
				if(SysUtility.isNotEmpty(SOURCE_FILE_NAME)) {
					jsonObject.put("SOURCE_FILE_NAME", SOURCE_FILE_NAME);
				}else {
					return false;
				}
			}
			return  access.Insert("exs_convert_log", jsonObject);
		}else {
			if(SysUtility.isEmpty(dataMap.get("SOURCE_FILE_NAME"))) {
				if(SysUtility.isNotEmpty(SOURCE_FILE_NAME)) {
					jsonObject.put("SOURCE_FILE_NAME", SOURCE_FILE_NAME);
				}else {
					return false;
				}
			}
			return  access.Update("exs_convert_log", jsonObject, "INDX");
		}
		
		
		} finally {
			access.ComitTrans();
		}
	}
	
	
	public static boolean UpHanderFlat(int indx,IDataAccess dataAccess) throws LegendException, JSONException {
		if(!SysUtility.isEmpty(dataAccess)) {
			dataAccess.BeginTrans();
			Datas datas = new Datas();
			datas.put("INDX", indx);
			boolean flat = dataAccess.Update("exs_handle_sender", datas);
			dataAccess.ComitTrans();
			return flat;
		}else {
			return false;
		}
		
	}
	
	

}
