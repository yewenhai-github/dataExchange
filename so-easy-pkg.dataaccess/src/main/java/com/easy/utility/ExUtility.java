package com.easy.utility;

import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.IDataAccess;
import com.easy.exception.LegendException;

public class ExUtility {
	
	public static String addHandleReceived(IDataAccess DataAccess, String msg_no, String msg_biz_type, String msg_code,String msg_name,String msg_time,String msg_desc)throws JSONException, LegendException{
		return addHandleReceived(DataAccess, msg_no, msg_biz_type, msg_code, msg_name, msg_time, msg_desc, "", "", "", "");
	}
	
	public static String addHandleReceived(IDataAccess DataAccess, String msg_no, String msg_biz_type, String msg_code,String msg_name,String msg_time,String msg_desc,
			String attribute1,String attribute2,String attribute3,String attribute4)throws JSONException, LegendException{
		JSONObject data = new JSONObject();
		
		if(SysUtility.isNotEmpty(msg_desc) && msg_desc.getBytes().length >= 2000){
			msg_desc = msg_desc.substring(0, 1500)+"...";
		}
		String uuid = SysUtility.GetUUID();
		data.put("indx", uuid);
		data.put("msg_no", msg_no);
		data.put("msg_biz_type", msg_biz_type);
		data.put("msg_code", msg_code);
		data.put("msg_name", msg_name);
		data.put("msg_desc", msg_desc);
		data.put("msg_time", msg_time);
		data.put("attribute1", attribute1);
		data.put("attribute2", attribute2);
		data.put("attribute3", attribute3);
		data.put("attribute4", attribute4);
		DataAccess.Insert("exs_handle_received", data, "indx");
		return uuid;
	}
	
	public static void addHandleSender(IDataAccess DataAccess, String msg_no, String msg_type, String message_source, String message_dest) throws LegendException, JSONException {
		JSONObject obj = new JSONObject();
		if(SysUtility.IsOracleDB()) {
			obj.put("indx", SysUtility.GetUUID());
		}
		obj.put("msg_no", msg_no);
		obj.put("msg_flag", "0");
		obj.put("msg_type", msg_type);
		obj.put("message_source", message_source);
		obj.put("message_dest", message_dest);
		DataAccess.Insert("exs_handle_sender", obj);
	}
}
