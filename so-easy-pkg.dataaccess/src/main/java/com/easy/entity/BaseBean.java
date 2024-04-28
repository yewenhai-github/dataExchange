package com.easy.entity;

import java.util.Iterator;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class BaseBean {
	
	public JSONArray getChildData(JSONObject mainData,JSONArray childData,String pidName) throws JSONException{
		JSONArray rows = new JSONArray();
		for (int j = 0; j < childData.length(); j++) {
			JSONObject row = (JSONObject)childData.get(j);
			if(mainData.has(pidName) && row.has(pidName) && mainData.get(pidName).equals(row.get(pidName))){
				rows.put(row);
			}
		}
		return rows;
	}
	
	
	public void putMessage(JSONArray rows, String key, Object value) throws LegendException, JSONException{
		putMessage(rows, key, value, 0);
	}
	
    public void putMessage(JSONArray rows, String key, Object value, int RowIndex) throws LegendException, JSONException{
		if(rows != null){
			if(rows.length() > RowIndex){
				JSONObject row = rows.getJSONObject(RowIndex);
				row.put(key, value);
			}else{
				JSONObject row = new JSONObject();
				row.put(key, value);
				rows.put(row);
			}
		}
    }
    
    public void putObjectMessage(JSONObject rowData,String TableName,String FieldName, Object setValue) throws LegendException{
    	putObjectMessage(rowData, TableName, FieldName, setValue, 0);
	}
	
    public void putObjectMessage(JSONObject rowData,String TableName, String FieldName, Object setValue, int RowIndex) throws LegendException{
    	try {
    		TableName = SysUtility.ParseTableName(rowData, TableName);
			if(rowData.has(TableName) && rowData.get(TableName)!=null){
				JSONArray rows = rowData.getJSONArray(TableName);
				if(rows != null){
					if(rows.length() > RowIndex){
						JSONObject row = rows.getJSONObject(RowIndex);
						row.put(FieldName, setValue);
						Iterator<?> keys = row.keys();
			    		while(keys.hasNext()){
							String key = keys.next().toString();
							if(key.equalsIgnoreCase(FieldName) && !key.equals(FieldName)){
								row.remove(key);
								return;
							}
			    		}
					}else{
						JSONObject row = new JSONObject();
						row.put(FieldName, setValue);
						rows.put(row);
					}
				}
			}else{
				JSONArray rows = new JSONArray();
				JSONObject row = new JSONObject();
				row.put(FieldName, setValue);
				rows.put(row);
				rowData.put(TableName, rows);
			}
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
    }
    
}
