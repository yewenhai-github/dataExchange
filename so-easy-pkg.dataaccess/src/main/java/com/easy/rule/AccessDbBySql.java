package com.easy.rule;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.easy.constants.Constants;
import com.easy.exception.ERR;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.query.SimpleParamSetter;
import com.easy.query.SqlParamList;
import com.easy.query.SqlParameter;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

import java.util.Set;
/**
 * so-easy private
 * 
 * @author yewh 2015-07-7
 * 
 * @version 7.0.0
 * 
 */
public class AccessDbBySql {

	/**
	 * 根据SQL语句查询数据库获取值
	 * 
	 * @param map
	 * @return
	 */
	public List getValueBySql(HashMap tableMap,String fieldName,HashMap fieldCodeNameMap,HashMap fieldMap) throws LegendException {
		List result = new ArrayList();
		SqlParamList sqlParamList = new SqlParamList();
		HashMap iFieldMap = (HashMap)fieldMap.get(fieldName);
		String sql = (String)iFieldMap.get("PROCESS_METHOD");
		try {
			if(SysUtility.isEmpty(sql)){
				sql = "";
			}
			sql = structureSql(sql, fieldCodeNameMap, fieldMap, tableMap, sqlParamList);
		} catch (Exception ex) {
			throw LegendException.getLegendException(ERR.RULE_INVALID_002);
		}
		if (SysUtility.isEmpty(sql)){
			return result;
		}
		LogUtil.printLog("\n" + sql + "\n at " + this.getClass().getName(),LogUtil.INFO);
		return SQLExecUtils.query4List(sql,new SimpleParamSetter(sqlParamList));
	}

	/**
	 * 构造带参的SQL语句
	 * 
	 * @param map
	 * @param fieldName
	 * @param sqlParamList
	 * @return
	 * @throws LegendException
	 */
	private String structureSql(String execSql,HashMap fieldCodeNameMap,HashMap fieldMap,
			HashMap tableMap,SqlParamList sqlParamList) throws LegendException{
		String keywords = ElecDocsUtil.getKeyWord(execSql);
		try {
			if(SysUtility.isNotEmpty(keywords)) {
				String tmpFieldName = keywords.substring(1, keywords.length() - 1).trim();
				String tmpFieldCode = (String)fieldCodeNameMap.get(tmpFieldName);
				HashMap tmpFieldMap = (HashMap) fieldMap.get(tmpFieldName);
				if(SysUtility.isEmpty(tmpFieldMap)){
					return "";
				}
				String tableName = (String)tmpFieldMap.get("TABLE_NAME");
				String dataType = (String)tmpFieldMap.get("FIELD_DATA_TYPE");
				String paramsMode = (String)tmpFieldMap.get("PROCESS_PARAMS_MODE");
				
				List leftValue = new ArrayList();
				ElecDocsUtil.getNameValue(tableMap,tmpFieldCode,tableName,leftValue);
				if(SysUtility.isEmpty(leftValue)){
					leftValue.add("Null");
				}
				StringBuffer sqlParam = new StringBuffer();
				for (int i = 0; i < leftValue.size(); i++) {
					if (dataType.equals(Constants.DATE)) {
						sqlParamList.addSqlParam(Types.DATE, (String) leftValue.get(i));
					} else if (dataType.equals(Constants.DECIMAL)) {
						sqlParamList.addSqlParam(Types.DECIMAL,(String) leftValue.get(i));
					} else {
						String str = "";
						if("1".equals(paramsMode)){
							str = "%"+leftValue.get(i);
						}else if("2".equals(paramsMode)){
							str = leftValue.get(i)+"%";
						}else if("3".equals(paramsMode)){
							str = "%"+leftValue.get(i)+"%";
						}else{
							str = (String)leftValue.get(i);
						}
						sqlParamList.addSqlParam(Types.VARCHAR,str);
					}
					if (sqlParam.length() < 1)
						sqlParam.append("?");
					else
						sqlParam.append(",?");
				}
				execSql = execSql.replaceFirst("\\[" + tmpFieldName + "\\]", sqlParam.toString());
				execSql = structureSql(execSql, fieldCodeNameMap, fieldMap, tableMap, sqlParamList);
			}
		} catch (Exception ex) {
			throw LegendException.getLegendException(ERR.RULE_INVALID_002);
		}
		return execSql;
	}
	
	
	
	/**
	 * 根据SQL语句查询数据库获取值
	 * 
	 * @param map
	 * @return
	 */
	public List getValueBySql2(HashMap tableMap,String fieldName,HashMap fieldCodeNameMap,HashMap fieldMap) throws LegendException {
		List result = new ArrayList();
		
		HashMap iFieldMap = (HashMap)fieldMap.get(fieldName);
		String sql = (String)iFieldMap.get("PROCESS_METHOD");
		try {
			List paramsList = new ArrayList();
			sql = structureSql2(sql, paramsList, fieldCodeNameMap, fieldMap, tableMap);
			
			String[] st = (String[])paramsList.get(0);
			for (int i = 0; i < st.length; i++) {
				SqlParamList sqlParamList = new SqlParamList();
				for (int j = 0; j < paramsList.size(); j++) {
					String[] strs = (String[])paramsList.get(j);
					sqlParamList.addSqlParam(Types.VARCHAR,strs[i]);
				}
				List list = SQLExecUtils.query4List(sql,new SimpleParamSetter(sqlParamList));
				if(SysUtility.isNotEmpty(list)){
					result.addAll(list);
				}
			}
		} catch (Exception ex) {
			throw LegendException.getLegendException(ERR.RULE_INVALID_002);
		}
		return result;
	}

	/**
	 * 构造带参的SQL语句
	 * 
	 * @param map
	 * @param fieldName
	 * @param sqlParamList
	 * @return
	 * @throws LegendException
	 */
	private String structureSql2(String execSql,List paramsList,HashMap fieldCodeNameMap,HashMap fieldMap,
			HashMap tableMap) throws LegendException{
		String keywords = ElecDocsUtil.getKeyWord(execSql);
		try {
			if(SysUtility.isNotEmpty(keywords)) {
				String tmpFieldName = keywords.substring(1, keywords.length() - 1).trim();
				String tmpFieldCode = (String)fieldCodeNameMap.get(tmpFieldName);
				HashMap tmpFieldMap = (HashMap) fieldMap.get(tmpFieldName);
				if(SysUtility.isEmpty(tmpFieldMap)){
					return "";
				}
				String tableName = (String)tmpFieldMap.get("TABLE_NAME");
				
				List leftValue = new ArrayList();
				ElecDocsUtil.getNameValue(tableMap,tmpFieldCode,tableName,leftValue);
				if(SysUtility.isEmpty(leftValue)){
					leftValue.add("Null");
				}
				StringBuffer sqlParam = new StringBuffer();
				String[] tempP = new String[leftValue.size()];
				for (int i = 0; i < leftValue.size(); i++) {
					tempP[i] = (String)leftValue.get(i);
					if (sqlParam.length() < 1){
						sqlParam.append("?");
					}
				}
				paramsList.add(tempP);
				execSql = execSql.replaceFirst("\\[" + tmpFieldName + "\\]", sqlParam.toString());
				execSql = structureSql2(execSql, paramsList,fieldCodeNameMap, fieldMap, tableMap);
			}
		} catch (Exception ex) {
			throw LegendException.getLegendException(ERR.RULE_INVALID_002);
		}
		return execSql;
	}
}
