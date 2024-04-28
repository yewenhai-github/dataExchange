package com.easy.tools;

import com.easy.file.FileFilterHandle;
import com.easy.utility.SysUtility;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public class TemplateGenerator extends AbstractGenerator {

	public void generateMapping(String templatePackageName,String htmlPackageName,String packageName,String className,String applicationName) throws Exception {
		dataModel.put("columns", GetFileNameList(htmlPackageName));
		dataModel.put("packageName", packageName);
		dataModel.put("className", className);
		
		super.generateJava(packageName, className, applicationName,templatePackageName+"/MappingModel.ftl");
	}
	
	public void generateModel(String packageName, String tableName,String applicationName) throws Exception {
		String className = dbNameToJavaName(tableName, true);
		createDataModel(packageName, className, tableName);
		super.generateJava(packageName + ".model", className + "Model", applicationName,"/template/Model.ftl");
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
	
	private void createDataModel(String packageName, String className, String tableName) throws Exception {
		try {
			if (tableName.equals(dataModel.get("tableName"))) {
				return;
			}

			String varName = className.substring(0, 1).toLowerCase() + className.substring(1);

			dataModel.clear();
			dataModel.put("tableName", tableName);
			dataModel.put("packageName", packageName);
			dataModel.put("mapperPackageName", packageName.substring(0, packageName.length() - 6));
			dataModel.put("className", className);
			dataModel.put("varName", varName);

			Connection conn = SysUtility.getCurrentConnection();
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = null;
			List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
			boolean implementsOperationLog = false;
			boolean hasDateColumns = false;
			boolean hasLobColumns = false;
			boolean hasVersionColumn = false;
			try {
				List<String> pks = new ArrayList<String>();
				rs = meta.getPrimaryKeys(null, meta.getUserName().toUpperCase(), tableName);
				while (rs.next()) {
					pks.add(rs.getString("COLUMN_NAME"));
				}
				rs.close();
				if (pks.size() == 0) {
					log.info("Table with no PK not supported. Use the first column as default PK");
				}
				if (pks.size() > 1) {
					log.info("Composite PK not supported. Use the first column as default PK");
					pks.clear();
				}

				String tableRemarks = null;
				rs = meta.getTables(null, meta.getUserName().toUpperCase(), tableName, null);
				if (rs.next()) {
					tableRemarks = rs.getString("REMARKS");
				}
				rs.close();

				String tableLabel = className;
				List<String> tableRemarkLinesList = new ArrayList<String>();
				if (tableRemarks != null && tableRemarks.trim().length() != 0) {
					StringTokenizer st = new StringTokenizer(tableRemarks, " 　,:;，：；\t\n");
					tableLabel = st.nextToken();
					tableRemarks = tableRemarks.substring(tableLabel.length());
					tableRemarkLinesList.addAll(Arrays.asList(StringUtils.tokenizeToStringArray(tableRemarks, "\n")));
				}
				dataModel.put("label", tableLabel);
				dataModel.put("remarkLines", tableRemarkLinesList);

				List<String> fieldNames = new ArrayList<String>();

				rs = meta.getColumns(null, meta.getUserName().toUpperCase(), tableName, null);
				while (rs.next()) {
					Map<String, Object> column = new HashMap<String, Object>();
					String columnName = rs.getString("COLUMN_NAME");
					String fieldName = dbNameToJavaName(columnName, false);
					String getterMethodName = "get" + dbNameToJavaName(columnName, true);
					String setterMethodName = "set" + dbNameToJavaName(columnName, true);

					if (pks.size() == 0) {
						pks.add(columnName);
					}

					fieldNames.add(fieldName);

					column.put("columnName", columnName);
					column.put("fieldName", fieldName);
					column.put("getterMethodName", getterMethodName);
					column.put("setterMethodName", setterMethodName);

					int columnSize = rs.getInt("COLUMN_SIZE");
					int decimalDigits = rs.getInt("DECIMAL_DIGITS");

					int sqlType = rs.getInt("DATA_TYPE");
					String columnType;
					String fieldType;
					String uiFieldType;
					int length = 0;
					int precision = 0;
					int scale = 0;
					int width = 0;
					if (sqlType == Types.DECIMAL) {
						columnType = "DECIMAL";
						if (rs.getInt("DECIMAL_DIGITS") == 0) {
							if (columnSize < 10) {
								fieldType = "Integer";
							} else {
								fieldType = "Long";
							}
							uiFieldType = FIELD_TYPE_INT;
							precision = columnSize;
							width = columnSize;
						} else {
							fieldType = "Double";
							uiFieldType = FIELD_TYPE_DOUBLE;
							precision = columnSize;
							scale = decimalDigits;
							width = columnSize + 1;
						}
					} else if (sqlType == Types.CHAR) {
						columnType = "CHAR";
						fieldType = "String";
						uiFieldType = FIELD_TYPE_STRING;
						length = columnSize;
						width = columnSize;
					} else if (sqlType == Types.VARCHAR) {
						columnType = "VARCHAR";
						fieldType = "String";
						uiFieldType = FIELD_TYPE_STRING;
						length = columnSize;
						width = columnSize;
					} else if (sqlType == Types.TIMESTAMP || sqlType == Types.DATE || sqlType == Types.TIME) {
						columnType = "TIMESTAMP";
						fieldType = "Date";
						hasDateColumns = true;
						if (fieldName.toLowerCase().endsWith("time")) {
							uiFieldType = FIELD_TYPE_DATETIME;
							width = 19;
						} else if (fieldName.toLowerCase().endsWith("month")) {
							uiFieldType = FIELD_TYPE_MONTH;
							width = 10;
						} else {
							uiFieldType = FIELD_TYPE_DATE;
							width = 10;
						}
					} else if (sqlType == Types.BLOB) {
						columnType = "BLOB";
						fieldType = "byte[]";
						hasLobColumns = true;
						uiFieldType = FIELD_TYPE_BYTES;
					} else if (sqlType == Types.CLOB) {
						columnType = "CLOB";
						fieldType = "String";
						hasLobColumns = true;
						uiFieldType = FIELD_TYPE_TEXT;
						width = 20;
					} else {
						log.info(tableName + "." + columnName + ", not supported column type: " + sqlType
								+ ". Use String as default");
						columnType = "VARCHAR";
						fieldType = "String";
						uiFieldType = FIELD_TYPE_STRING;
						length = columnSize;
						width = columnSize;
					}
					column.put("columnType", columnType);
					column.put("fieldType", fieldType);
					column.put("length", Integer.toString(length));
					column.put("precision", Integer.toString(precision));
					column.put("scale", Integer.toString(scale));

					if (pks.contains(columnName)) {
						column.put("isPK", true);
						dataModel.put("pkColumnName", columnName);
						dataModel.put("pkFieldName", fieldName);
						dataModel.put("pkFieldType", fieldType);
						dataModel.put("pkFieldLength", Integer.toString(length));
					} else {
						column.put("isPK", false);
					}

					if ("REC_VER".equalsIgnoreCase(columnName)) {
						column.put("isVersion", true);
						hasVersionColumn = true;
					} else {
						column.put("isVersion", false);
					}

					String remarks = rs.getString("REMARKS");
					String label = fieldName;
					List<String> remarkLinesList = new ArrayList<String>();
					if (remarks != null && remarks.trim().length() != 0) {
						StringTokenizer st = new StringTokenizer(remarks, " 　,:;，：；\t\n");
						label = st.nextToken();
						remarks = remarks.substring(label.length());
						remarkLinesList.addAll(Arrays.asList(StringUtils.tokenizeToStringArray(remarks, "\n")));
					}
					column.put("label", label);
					column.put("remarkLines", remarkLinesList);

					String[] remarkParts = StringUtils.tokenizeToStringArray(remarks, " 　,:;，：；\t\n");
					if (remarkParts != null) {
						for (String part : remarkParts) {
							if (part.startsWith(FIELD_TYPE_SELECTCODE)) {
								uiFieldType = part;
								width = 20;
								break;
							}
						}
					}

					column.put("uiFieldType", uiFieldType);

					if (width < 10) {
						width = 10;
					}
					if (width > 20) {
						width = 20;
					}
					width *= 10;
					column.put("width", Integer.toString(width));

					column.put("nullable", "YES".equalsIgnoreCase(rs.getString("IS_NULLABLE")) ? "true" : "false");
					column.put("sortable", sqlType != Types.BLOB && sqlType != Types.CLOB ? "true" : "false");

					columns.add(column);
				}

				if (fieldNames.containsAll(Arrays.asList("creator", "createTime", "modifier", "modifyTime"))) {
					implementsOperationLog = true;
				}

			} finally {
				try {
					rs.close();
				} catch (Exception ex) {
				}
			}

			dataModel.put("columns", columns);
			dataModel.put("implementsOperationLog", implementsOperationLog);
			dataModel.put("hasDateColumns", hasDateColumns);
			dataModel.put("hasLobColumns", hasLobColumns);
			dataModel.put("hasVersionColumn", hasVersionColumn);
		} catch (Exception ex) {
			dataModel.clear();
			throw ex;
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
