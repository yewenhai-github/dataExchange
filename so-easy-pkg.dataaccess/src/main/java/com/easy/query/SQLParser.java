package com.easy.query;

import java.lang.reflect.Array;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

import oracle.jdbc.OracleTypes;

public class SQLParser {
	private static final Pattern REPLACE_PATN = Pattern.compile("\\%\\w*\\%");
	//public static final Pattern D_SQL_PATN = Pattern.compile("\\s<<.*?#\\s*\\w*\\s*[\\s\\w:]*#[\\)\\,\\'\\w\\s\\:\\-\\+\\*/]*>>\\s");
	public static final Pattern D_SQL_PATN = Pattern.compile("\\s<<.*?#\\s*\\w*\\s*[\\s\\w:]*#[\\)\\,\\'\\w\\s\\:\\-\\+\\=\\*/]*>>\\s");
	public static final Pattern NAME_PATN = Pattern.compile("#\\s*\\w*\\s*[\\s\\w:]*#");
	private static final Map TYPE_MAP;
	static {
		TYPE_MAP = new HashMap();
		TYPE_MAP.put("BINARY", new Integer(Types.BINARY));
		TYPE_MAP.put("BIT", new Integer(Types.BIT));
		TYPE_MAP.put("BOOLEAN", new Integer(Types.BOOLEAN));
		TYPE_MAP.put("CHAR", new Integer(Types.CHAR));
		TYPE_MAP.put("VARCHAR", new Integer(Types.VARCHAR));
		TYPE_MAP.put("BIGINT", new Integer(Types.BIGINT));
		TYPE_MAP.put("INTEGER", new Integer(Types.INTEGER));
		TYPE_MAP.put("INT", new Integer(Types.INTEGER));
		TYPE_MAP.put("FLOAT", new Integer(Types.FLOAT));
		TYPE_MAP.put("DECIMAL", new Integer(Types.DECIMAL));
		TYPE_MAP.put("DOUBLE", new Integer(Types.DOUBLE));
		TYPE_MAP.put("NUMERIC", new Integer(Types.NUMERIC));
		TYPE_MAP.put("DATE", new Integer(Types.DATE));
		TYPE_MAP.put("TIMESTAMP", new Integer(Types.TIMESTAMP));
		TYPE_MAP.put("LONGVARCHAR", new Integer(Types.LONGVARCHAR));
		TYPE_MAP.put("CLOB", new Integer(Types.CLOB));
		TYPE_MAP.put("LONGVARBINARY", new Integer(Types.LONGVARBINARY));
		TYPE_MAP.put("BLOB", new Integer(Types.BLOB));
		TYPE_MAP.put("CURSOR", new Integer(OracleTypes.CURSOR));
	}
	
	/**
	 * 处理动态语句，返回SqlHolder
	 * 
	 * @param afterAclSql
	 * @param params
	 * @return
	 */
	public static SQLHolder parse(String SQL, Object jsonParams) {
		HashMap params = new HashMap();
		
		if(jsonParams instanceof JSONObject) {
			SysUtility.paramConvert(params, (JSONObject)jsonParams);
		}else if(jsonParams instanceof HashMap || jsonParams instanceof Map) {
			params = (HashMap)jsonParams;
		}
		
//		if(SysUtility.isEmpty(jsonParams) || SysUtility.isEmpty(params)){
//			SQLHolder holder = new SQLHolder();
//			holder.setSql(SQL);
//			return holder;
//		}
		for (Matcher m = REPLACE_PATN.matcher(SQL); m.find(); m = REPLACE_PATN.matcher(SQL)) {
			String group = m.group();
			String replace = (String) params.get(group.replaceAll("\\%", ""));
			SQL = m.replaceFirst(replace == null ? SysUtility.EMPTY : replace);
		}
		
		StringBuffer result = new StringBuffer(SQL.length());
		for (Matcher matcher = D_SQL_PATN.matcher(SQL); matcher.find(); matcher = D_SQL_PATN.matcher(SQL)) {
			int start = matcher.start() + 1;
			int end = matcher.end() - 1;
			result.append(SQL.substring(0, start));
			String group = matcher.group();
			Object value = params.get(getParaName(group));
			if (SysUtility.isNotEmpty(value)) {
				result.append(group.substring(3, group.length() - 3));
			}
			SQL = SQL.substring(end);
		}
		result.append(SQL);
		SQL = result.toString();
		
		SqlParamList paramList = new SqlParamList();
		StringBuffer realResult = new StringBuffer(SQL.length());
		for (Matcher matcher = NAME_PATN.matcher(SQL); matcher.find(); matcher = NAME_PATN.matcher(SQL)) {
			realResult.append(SQL.substring(0, matcher.start()));
			String group = matcher.group();
			Object value = params.get(getParaName(group));
			int sqlType = getSqlType(group);
			realResult.append(buildSpaceHolder(value, sqlType, paramList));
			SQL = SQL.substring(matcher.end());
		}
		realResult.append(SQL);
		String realSql = realResult.toString();
		LogUtil.printLog("parse realSql:" + realSql, LogUtil.INFO);
		
		SQLHolder holder = new SQLHolder();
		holder.setSql(realSql);
		holder.setParamList(paramList);
		return holder;
	}
	
	/**
	 * 从配置中读取参数名
	 * 
	 * @param input
	 * @return
	 */
	private static String getParaName(String input) {
		Matcher m = NAME_PATN.matcher(input);
		if (m.find()) {
			String p = m.group();
			return p.substring(1, p.length() - 1).split(":")[0].trim();
		} else {
			return SysUtility.EMPTY;
		}
	}
	
	/**
	 * 从配置中解析参数的数据类型 具体参照TYPE_MAP表 如果无配置，则默认为 VARCHAR 类型
	 * 
	 * @param group
	 * @return
	 */
	private static int getSqlType(String group) {
		String[] s = group.substring(1, group.length() - 1).trim().split(":");
		if (s.length < 2) {
			return Types.VARCHAR;
		}
		Integer sqlType = (Integer) TYPE_MAP.get(s[1].toUpperCase());
		if (sqlType == null) {
			return Types.VARCHAR;
		} else {
			return sqlType.intValue();
		}
	}
	
	private static StringBuffer buildSpaceHolder(Object value, int sqlType, SqlParamList paramList) {
		StringBuffer spaceHolder = new StringBuffer();
		if (value == null) {
			spaceHolder.append("?");
			paramList.addSqlParam(sqlType, null);
			return spaceHolder;
		}
		Class clazz = value.getClass();
		if (clazz == String.class) {
			spaceHolder.append("?");
			paramList.addSqlParam(sqlType, (String) value);
		} else if (clazz == JSONArray.class) {
			try {
				JSONArray array = (JSONArray)value;
				for (int i = 0; i < array.length(); i++) {
					spaceHolder.append(",?");
					String v = (String) array.get(i);
					if (v == null) {
						paramList.addSqlParam(sqlType, null);
					} else {
						paramList.addSqlParam(sqlType, v);
					}
				}
				if (spaceHolder.length() > 0) {
					spaceHolder.deleteCharAt(0);
				}
			} catch (JSONException e) {
				LogUtil.printLog("SQLParser.buildSpaceHolder Error", Level.ERROR);
			}
		} else if (clazz.isArray()) {
			for (int i = 0, length = Array.getLength(value); i < length; i++) {
				spaceHolder.append(",?");
				String v = (String) Array.get(value, i);
				if (v == null) {
					paramList.addSqlParam(sqlType, null);
				} else {
					paramList.addSqlParam(sqlType, v);
				}
			}
			if (spaceHolder.length() > 0) {
				spaceHolder.deleteCharAt(0);
			}
		} else if (Collection.class.isAssignableFrom(clazz)) {
			for (Iterator it = ((Collection) value).iterator(); it.hasNext();) {
				spaceHolder.append(",?");
				paramList.addSqlParam(sqlType, (String) it.next());
			}
			if (spaceHolder.length() > 0) {
				spaceHolder.deleteCharAt(0);
			}
		} else if (Date.class.isAssignableFrom(clazz)) {
			spaceHolder.append("?");
			paramList.addSqlParam(sqlType, SysUtility.getDateFormat().format((Date) value));
		} else {
			spaceHolder.append("?");
			paramList.addSqlParam(sqlType, value.toString());
		}
		return spaceHolder;
	}
	
}
