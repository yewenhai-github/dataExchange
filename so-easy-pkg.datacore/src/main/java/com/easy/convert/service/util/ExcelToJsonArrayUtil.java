package com.easy.convert.service.util;

import java.io.File;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class ExcelToJsonArrayUtil {

	/*
	 * public static void main(String[] args) throws Exception { String filepath =
	 * "C:\\Users\\Administrator\\Desktop\\问题\\zhuanhuan\\西安.xls"; JSONArray
	 * excelToJsonArray = ExcelToJsonArray(filepath, 0);
	 * System.out.println(excelToJsonArray); }
	 */
	
	public static int isFlagExcelCol(String colName, Sheet excelTable) {
		int colIndx = -1;
		int clos = excelTable.getColumns();// 得到所有的列
		for (int i = 0; i < clos; i++) {
			if (excelTable.getCell(i, 0).getContents().equals(colName))
				colIndx = i;
		}
		return colIndx;
	}
	
	public static boolean isEmpty(String str) {
        return ("null".equalsIgnoreCase(str) || str == null || str.trim().equals(""));
    }
	/**
	 * 
	 * @param filepath Excel文件路径
	 * @param sheetName  sheet名称
	 * @return  如果出现excel文件不存在 以及sheet不存在 则会return null
	 * @throws Exception
	 */
	public static JSONArray ExcelToJsonArray(String filepath,String sheetName) throws Exception{
		try {
			WorkbookSettings ws = new WorkbookSettings();
			ws.setCellValidationDisabled(true);
			Workbook rwb = null;
			try {
				rwb = Workbook.getWorkbook(new File(filepath),ws);
			} catch (Exception e) {
				throw new RuntimeException(filepath+"不存在!");
			}
			Sheet rs = rwb.getSheet(sheetName);
			if(SysUtility.isEmpty(rs)){
				if(SysUtility.isNotEmpty(rwb)) {
					rwb.close();
				}
				throw new RuntimeException(sheetName+"  Sheet名称不存在!");
			}
			int clos = rs.getColumns();// 得到所有的列
			int rows = rs.getRows();// 得到所有的行
			JSONObject json = new JSONObject();
			JSONArray arrs =new JSONArray();
			int excelColIndx = -1;
			if(rows<=1){
	         	if(SysUtility.isNotEmpty(rwb)) {
					rwb.close();
				}
	         	throw new RuntimeException("导入的数据为空！");
	        }
			filepath = filepath.replace("\\", "/");
			int d_ = filepath.lastIndexOf(".");
			int p_ = filepath.lastIndexOf("/")+1;
			String fileName = filepath.substring(p_,d_);
			for (int i = 1; i < rows; i++) {//循环行
				for (int j = 0; j < 1; j++) {
					json = new JSONObject();
					// 判断表中是否存在空行
					int rowdataisnullcount = 0;
					for (int r = 0; r < clos; r++) {
						if (isEmpty(rs.getCell(r, i).getContents())) {
							rowdataisnullcount++;
						}
					}
					if(rowdataisnullcount<clos){
						json.put("EXCELPATH", filepath);
						json.put("FILENAME", fileName);
						for(int k=0;k<clos;k++){
							json.put(rs.getCell(k,0).getContents().toUpperCase(), rs.getCell(isFlagExcelCol(rs.getCell(k,0).getContents(), rs), i).getContents());
						}
						if(SysUtility.isNotEmpty(json)){
							arrs.put(json);
						}
					}
				}
			}
			if(SysUtility.isNotEmpty(rwb)) {
				rwb.close();
			}
			if(arrs.length()<1){
				return null;
			}else{
				return arrs;	
			}
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			return null;
		}
		
	}
	
	
	/**
	 * 
	 * @param filepath Excel文件路径
	 * @param sheetName  sheet名称
	 * @return  如果出现excel文件不存在 以及sheet不存在 则会return null
	 * @throws Exception
	 */
	public static JSONArray ExcelToJsonArray(String filepath,int sheetPage) throws Exception{
		String err;
		WorkbookSettings ws = new WorkbookSettings();
		ws.setCellValidationDisabled(true);
		Workbook rwb = null;
		try {
			rwb = Workbook.getWorkbook(new File(filepath),ws);
		} catch (Exception e) {
			err = filepath+"不存在!";
			return null;
		}
		Sheet rs  = null;
		try {
			rs = rwb.getSheet(sheetPage);
			if(SysUtility.isEmpty(rs)){
				err = "Sheet页不存在!";
				if(SysUtility.isNotEmpty(rwb)) {
					rwb.close();
				}
				
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		int clos = rs.getColumns();// 得到所有的列
		int rows = rs.getRows();// 得到所有的行
		LogUtil.printLog("读取:"+rs.getName() +"共 -->行:"+rows + "列:"+clos, Level.ERROR);

		JSONObject json = new JSONObject();
		JSONArray arrs =new JSONArray();
		int excelColIndx = -1;
		if(rows<=1){
         	err = "导入的数据为空！";
         	if(SysUtility.isNotEmpty(rwb)) {
				rwb.close();
			}
         	return null;
        }
		filepath = filepath.replace("\\", "/");
		int d_ = filepath.lastIndexOf(".");
		int p_ = filepath.lastIndexOf("/")+1;
		String fileName = filepath.substring(p_,d_);
		for (int i = 1; i < rows; i++) {//循环行
			for (int j = 0; j < 1; j++) {
				json = new JSONObject();
				// 判断表中是否存在空行
				int rowdataisnullcount = 0;
				for (int r = 0; r < clos; r++) {
					if (isEmpty(rs.getCell(r, i).getContents())) {
						rowdataisnullcount++;
					}
				}
				if(rowdataisnullcount<clos){
					json.put("EXCELPATH", filepath);
					json.put("FILENAME", fileName);
					for(int k=0;k<clos;k++){
						json.put(rs.getCell(k,0).getContents().toUpperCase(), rs.getCell(isFlagExcelCol(rs.getCell(k,0).getContents(), rs), i).getContents());
					}
					if(SysUtility.isNotEmpty(json)){
						arrs.put(json);
					}
				}
			}
		}
		if(SysUtility.isNotEmpty(rwb)) {
			rwb.close();
		}
		if(arrs.length()<1){
			return null;
		}else{
			return arrs;	
		}
	}
	
	
}
