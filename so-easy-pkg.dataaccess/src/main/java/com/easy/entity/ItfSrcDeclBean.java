package com.easy.entity;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class ItfSrcDeclBean extends BaseBean {
	public JSONObject MessageHead = new JSONObject();
	public JSONObject MessageBody = new JSONObject();

	public JSONArray ITF_SRC_DECL = new JSONArray();
	public JSONArray ITF_SRC_DECL_GOODSs = new JSONArray();
	
	public static ItfSrcDeclBean getInstance(){
		return new ItfSrcDeclBean();
	}
	
	public JSONObject getDeclMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {
			for (int i = 0; i < ITF_SRC_DECL.length(); i++) {
				JSONObject row = (JSONObject)ITF_SRC_DECL.get(i);
				if(SysUtility.isNotEmpty(ITF_SRC_DECL_GOODSs) && ITF_SRC_DECL_GOODSs.length() > 0){
					row.put("ITF_SRC_DECL_GOODS", getChildData(row, ITF_SRC_DECL_GOODSs, "Uuid"));
				}
			}
			MessageBody.put("ITF_SRC_DECL", ITF_SRC_DECL);
			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			Root.put(new JSONObject().put("MessageBody", MessageBody));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	}  
	
	public JSONObject getReceivedMessage(){
		JSONObject OBORMessage = new JSONObject();
		try {			
			JSONArray Root = new JSONArray();
			Root.put(new JSONObject().put("MessageHead", MessageHead));
			OBORMessage.put("OBORMessage", Root);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return OBORMessage;
	} 
	
	public JSONArray getDeclMessageNew(){
		JSONArray rows = new JSONArray();
		try {
			
			for (int i = 0; i < ITF_SRC_DECL.length(); i++) {
				JSONObject row = (JSONObject)ITF_SRC_DECL.get(i);
				if(SysUtility.isNotEmpty(ITF_SRC_DECL_GOODSs) && ITF_SRC_DECL_GOODSs.length() > 0){
					row.put("ITF_SRC_DECL_GOODS", getChildData(row, ITF_SRC_DECL_GOODSs, "Uuid"));
				}
			}
			MessageBody.put("ITF_SRC_DECL", ITF_SRC_DECL);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			rows.put(row); 
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	
	public JSONArray getReceivedMessageNew(){
		JSONArray rows = new JSONArray();
		try {			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
	 
}
