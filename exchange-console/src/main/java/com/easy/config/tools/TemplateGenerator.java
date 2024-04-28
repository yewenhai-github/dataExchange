package com.easy.config.tools;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.easy.utility.SysUtility;

public class TemplateGenerator extends AbstractGenerator {

	public void generateMapping(String templatePackageName,String htmlPackageName,String packageName,String className) throws Exception {
		dataModel.put("columns", GetFileNameList(htmlPackageName));
		dataModel.put("packageName", packageName);
		dataModel.put("className", className);
		
		super.generateJava(packageName, className, templatePackageName+"/MappingModel.ftl");
	}
	
	

	public static List<String> GetFileNameList(String htmlPackageName) throws UnsupportedEncodingException {
		List<String> columns = new ArrayList<String>();
		String Path = java.net.URLDecoder.decode(new TemplateGenerator().getClass().getResource("/").getPath()+htmlPackageName,"UTF-8");
		List<String> list = new ArrayList<String>();
		GetFileNameList(list, Path, "");
		for (int i = 0; i < list.size(); i++) {
			String FileName = list.get(i);
			if(FileName.length() > 4 && FileName.substring(FileName.length() - 5, FileName.length()).equals(".html")) {
				String str = FileName.substring(0, FileName.length() - 5);
				if(!columns.contains(str)) {
					columns.add(str);
				}
			}
		}
		return columns;
	}
			
	public static void GetFileNameList(List<String> list,String sourcePath,String folderName){
		File files[] = new File(sourcePath).listFiles(new FileFilterHandle());
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()){
				if(SysUtility.isNotEmpty(folderName)) {
					list.add(folderName+"/"+files[i].getName());
				}else {
					list.add(files[i].getName());
				}
			}else{
				if(SysUtility.isNotEmpty(folderName)) {
					GetFileNameList(list, sourcePath + File.separator + files[i].getName(), folderName + "/" + files[i].getName());
				}else {
					GetFileNameList(list, sourcePath + File.separator + files[i].getName(), files[i].getName());
				}
			}
		}
	}

	public static final String FIELD_TYPE_STRING = "string";
	public static final String FIELD_TYPE_TEXT = "text";
	public static final String FIELD_TYPE_BYTES = "bytes";
	public static final String FIELD_TYPE_INT = "int";
	public static final String FIELD_TYPE_DOUBLE = "double";
	public static final String FIELD_TYPE_DATE = "date";
	public static final String FIELD_TYPE_DATETIME = "datetime";
	public static final String FIELD_TYPE_TIME = "time";
	public static final String FIELD_TYPE_MONTH = "month";
	public static final String FIELD_TYPE_SELECTCODE = "selectCode.";

	public static String dbNameToJavaName(String dbName, boolean firstCharUppered) {
		if (dbName == null || dbName.trim().length() == 0) {
			return "";
		}
		if (dbName.indexOf("_") >= 0 || dbName.equals(dbName.toUpperCase())) {
			dbName = dbName.toLowerCase();
		}
		String[] parts = dbName.split("_");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			if (part.length() == 0) {
				continue;
			}
			sb.append(part.substring(0, 1).toUpperCase());
			sb.append(part.substring(1));
		}
		if (firstCharUppered) {
			return sb.toString();
		} else {
			return sb.substring(0, 1).toLowerCase() + sb.substring(1);
		}
	}
}
