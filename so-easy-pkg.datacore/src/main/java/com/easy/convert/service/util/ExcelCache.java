package com.easy.convert.service.util;

import java.io.File;
import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ps: 本地Excel读取数据缓存存储
 * @author wangk
 *
 */
public class ExcelCache implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1777887888144518829L;
	
	private static JSONObject QuartzJson;
	private static JSONArray excelToJsonArray;
	private static HashMap<String, JSONArray> excelSheetToMap;
	private static HashMap<String,Deque<File>> FileCache;
	public static JSONObject getQuartzJson() {
		return QuartzJson;
	}
	public static void setQuartzJson(JSONObject quartzJson) {
		QuartzJson = quartzJson;
	}
	public static JSONArray getExcelToJsonArray() {
		return excelToJsonArray;
	}
	public static void setExcelToJsonArray(JSONArray excelToJsonArray) {
		ExcelCache.excelToJsonArray = excelToJsonArray;
	}
	public static HashMap<String, JSONArray> getExcelSheetToMap() {
		return excelSheetToMap;
	}
	public static void setExcelSheetToMap(HashMap<String, JSONArray> excelSheetToMap) {
		ExcelCache.excelSheetToMap = excelSheetToMap;
	}
	public static HashMap<String, Deque<File>> getFileCache() {
		return FileCache;
	}
	public static void setFileCache(HashMap<String, Deque<File>> fileCache) {
		FileCache = fileCache;
	}
	
	

}
