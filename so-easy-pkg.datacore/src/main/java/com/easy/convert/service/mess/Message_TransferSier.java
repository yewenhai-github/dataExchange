package com.easy.convert.service.mess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message_TransferSier implements Serializable{
	
	private static final long serialVersionUID = -7706760867203308471L;
	
	static JSONObject QuartzJson;
	static JSONArray excelToJsonArray;
	static HashMap<String, JSONArray> excelSheetToMap;
	
	public static JSONArray getExcelToJsonArray() {
		return excelToJsonArray;
	}

	public static void setExcelToJsonArray(JSONArray excelToJsonArray) {
		Message_TransferSier.excelToJsonArray = excelToJsonArray;
	}

	public static HashMap<String, JSONArray> getExcelSheetToMap() {
		return excelSheetToMap;
	}

	public static void setExcelSheetToMap(HashMap<String, JSONArray> excelSheetToMap) {
		Message_TransferSier.excelSheetToMap = excelSheetToMap;
	}

	public static JSONObject getQuartzJson() {
		return QuartzJson;
	}

	public static void setQuartzJson(JSONObject quartzJson) {
		Message_TransferSier.QuartzJson = quartzJson;
	}
	
	
	
	
	
	
}
