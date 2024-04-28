package com.easy.app.utility;

import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;

public class ExpUtility {
	
	public static void setRowsDefault(JSONObject rows) throws LegendException{
		String TableName = "rows";
		for (int i = 0; i < SysUtility.GetTableRows(rows, TableName); i++) {
			JSONObject row = SysUtility.GetTable(rows, TableName, i);
			setStatusDefault(row, SysUtility.GetTableValue(rows, TableName, "MODIFY_TIME", i));
			setYesNoDefault(row, "IS_ENABLED");
			setEmptyDefault(row, "ENCODING", "UTF-8");
			setEmptyDefault(row, "MQ_TYPE", "ActiveMQ");
			setMqModeDefault(row);
			if("0".equals(SysUtility.GetTableValue(rows, TableName, "QUARTZ_TYPE", i))){
				SysUtility.putJsonField(row, "QUARTZ_CRON", SysUtility.GetTableValue(rows, TableName, "QUARTZ_CRON", i)+"秒");
			}
			
			
		}
	}
	
	public static void setStatusDefault(JSONObject row,String modifyTime){
		String FieldValue = "0";
		if(SysUtility.isEmpty(modifyTime)){
			SysUtility.putJsonField(row, "STATUS", FieldValue);
			return;
		}
		
		String tempModifyTime = SysUtility.getSysDate(modifyTime, 1800);
		String currentTime = SysUtility.getSysDate();
		if(currentTime.compareTo(tempModifyTime) < 0){
			FieldValue = "1";
		}
		SysUtility.putJsonField(row, "STATUS", FieldValue);
	}
	
	public static void setEmptyDefault(JSONObject row,String FieldName,String FieldValue){
		String tempStr = SysUtility.getJsonField(row, FieldName);
		if(SysUtility.isEmpty(tempStr)){
			SysUtility.putJsonField(row, FieldName, FieldValue);
		}
	}
	
	public static void setYesNoDefault(JSONObject row,String FieldName){
		String tempStr = SysUtility.getJsonField(row, FieldName);
		if("1".equals(tempStr)){
			SysUtility.putJsonField(row, FieldName, "是");
		}else{
			SysUtility.putJsonField(row, FieldName, "否");
		}
	}
	
	//空=TextMessage 1ObjectMessage 2MapMessage
	public static void setMqModeDefault(JSONObject row){
		String FieldName = "ACTIVEMQ_MODE";
		String tempStr = SysUtility.getJsonField(row, FieldName);
		if("1".equals(tempStr)){
			SysUtility.putJsonField(row, FieldName, "Object");
		}else if("2".equals(tempStr)){
			SysUtility.putJsonField(row, FieldName, "Map");
		}else{
			SysUtility.putJsonField(row, FieldName, "Text");
		}
	}
}
