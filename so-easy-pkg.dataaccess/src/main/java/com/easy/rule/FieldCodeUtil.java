package com.easy.rule;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easy.access.Datas;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLBuild;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;

public class FieldCodeUtil {
	/** 表结构
	create table RULE_T_CODE_VALIDATE(
	  INDX         NUMBER(12),
	  POINT_CODE   VARCHAR2(50),
	  RULE_NO      VARCHAR2(50),
	  RULE_NAME    VARCHAR2(50),
	  WARNING_TYPE VARCHAR2(50),
	  WARNING_INFO VARCHAR2(50),
	  TABLE_NAME   VARCHAR2(50),
	  FIELD_NAME   VARCHAR2(50) default 'ITEM_NAME',
	  FIELD_CODE   VARCHAR2(50) default 'ITEM_CODE',
	  XML_DOCUMENT VARCHAR2(50),
	  XML_NAME     VARCHAR2(50),
	  XML_CODE     VARCHAR2(50),
	  XML_FILTER   VARCHAR2(50),
	  IS_ENABLED   CHAR(1) default '1',
	  PART_ID      VARCHAR2(30),
	  CREATEOR     NUMBER(12),
	  CREATE_TIME  DATE default SYSDATE,
	  MODIFYOR     NUMBER(12),
	  MODIFY_TIME  DATE,
	  REC_VER      NUMBER(8) default 0
	);
	配置使用说明：
	select t.indx,t.point_code,t.rule_no,t.rule_name,t.warning_type,t.warning_info,t.table_name,t.xml_document,t.xml_code from rule_t_code_validate t for update;  
	INDX	POINT_CODE	RULE_NO	RULE_NAME	WARNING_TYPE	WARNING_INFO	TABLE_NAME	XML_DOCUMENT	XML_CODE	XML_FILTER
	1	(1)	Code004	代码004	INFO	运输方式代码不存在.	S_ECIQ_TRANS_MODE	ITF_DCL_IO_DECL	DespTransModeCodePortCode	
	2	(1)	Code005	代码005	INFO	世界口岸代码不存在.	S_ECIQ_WORLDPORT	ITF_DCL_IO_DECL	DespPortCode	ITF_DCL_IO_DECL.AplKind=I
	3	(1)	Code006	代码006	INFO	国内口岸代码不存在.	S_ECIQ_PORTCODE	ITF_DCL_IO_DECL	DespPortCode	ITF_DCL_IO_DECL.AplKind=O
	* */
	
	public static String checkFieldCode(Datas datas, String pointCode) throws LegendException{
		StringBuffer ErrorMsg = new StringBuffer();
		if(SysUtility.isEmpty(pointCode) || SysUtility.isEmpty(datas)){
			return "";
		}
		List FieldCodes = getFieldCode(pointCode);
		for (int i = 0; i < FieldCodes.size(); i++) {
			HashMap<String,String> codeMap = (HashMap) FieldCodes.get(i);
			String tableName = codeMap.get("TABLE_NAME");
			String fieldName = SysUtility.isEmpty(codeMap.get("FIELD_NAME"))?"ITEM_NAME":codeMap.get("FIELD_NAME");
			String fieldCode = SysUtility.isEmpty(codeMap.get("FIELD_CODE"))?"ITEM_CODE":codeMap.get("FIELD_CODE");
			String tableCondition = codeMap.get("TABLE_CONDITION");
			String xmlDocument = codeMap.get("XML_DOCUMENT");
			String xmlName = codeMap.get("XML_NAME");
			String xmlCode = codeMap.get("XML_CODE");
			String xmlFilter = codeMap.get("XML_FILTER");
			String warningInfo = codeMap.get("WARNING_INFO");
			
			if(datas.has(xmlDocument)){
				for (int j = 0; j < datas.GetTableRows(xmlDocument); j++) {
					final String xmlCodeVal = datas.GetTableValue(xmlDocument, xmlCode, j);
					if(SysUtility.isEmpty(xmlCodeVal)){
						continue;//报文中不存在的字段，不做基础数据校验
					}
					if(SysUtility.isNotEmpty(xmlFilter)){
						String[] arr1 = xmlFilter.split("\\=");
						String[] arr2 = arr1[0].split("\\.");
						if(!datas.GetTableValue(arr2[0], arr2[1], 0).equals(arr1[1])){
							continue;//过滤非出境或入境的配置。
						}
					}
					
					final String xmlNameVal = datas.GetTableValue(xmlDocument, xmlName, j);
					StringBuffer SQL = new StringBuffer();
					SQL.append("select "+fieldCode);
					if(SysUtility.isNotEmpty(fieldName)){
						SQL.append(","+fieldName);
					}
					SQL.append(" from "+tableName);
					SQL.append(" where is_enabled = '1' and "+fieldCode+" = ?");
					if(SysUtility.isNotEmpty(tableCondition)){
						SQL.append(" and "+tableCondition);
					}
					Map map = SQLExecUtils.query4Map(SQL.toString(), new Callback() {
						@Override
						public void doIn(PreparedStatement ps) throws SQLException {
							ps.setString(1, xmlCodeVal);
						}
					});
					String mapCodeVal = (String)map.get(fieldCode.toUpperCase());
					String mapNameVal = (String)map.get(fieldName.toUpperCase());
					
					if(!xmlCodeVal.equals(mapCodeVal)){
						String tempStr = xmlDocument+"."+xmlCode+"="+xmlCodeVal+warningInfo;
						if(ErrorMsg.indexOf(tempStr) < 0){
							ErrorMsg.append("["+codeMap.get("WARNING_TYPE")+"]").append(tempStr).append("\n");
						}
					}
					if(SysUtility.isNotEmpty(xmlName) && !xmlNameVal.equals(mapNameVal)){
						ErrorMsg.append("["+codeMap.get("WARNING_TYPE")+"]").append(xmlName+warningInfo).append("\n");
					}
				}
			}
		}
		return ErrorMsg.toString();
	}
	
	private static HashMap<String,List> cacheFieldMap = new HashMap<String,List>();
	private static List getFieldCode(final String pointCode) throws LegendException {
		SysUtility.OutDate5MinuteReset(cacheFieldMap);
		
		List list = cacheFieldMap.get(pointCode);
		if(SysUtility.isNotEmpty(list) || SysUtility.isEmpty(pointCode)){
			return list;
		}
		
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select *");
		sqlBuild.append("  from rule_t_code_validate");
		sqlBuild.append(" where is_enabled = '1'");
		sqlBuild.append("point_code", "in", pointCode.split(","));
		sqlBuild.append(" order by rule_no");
		list = sqlBuild.query4List();
		cacheFieldMap.put(pointCode, list);
		return list;
	}
	
}
