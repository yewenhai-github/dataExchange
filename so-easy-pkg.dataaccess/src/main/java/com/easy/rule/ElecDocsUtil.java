package com.easy.rule;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;

import com.easy.bizconfig.BizConfigFactory;
import com.easy.constants.Constants;
import com.easy.exception.ERR;
import com.easy.exception.LegendException;
import com.easy.query.Callback;
import com.easy.query.SQLExecUtils;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
/**
 * so-easy private
 * 
 * @author yewh 2015-07-07
 * 
 * @version 7.0.0
 * 
 */
public class ElecDocsUtil {
	public static HashMap pubCache = new HashMap();
	public static void checkRule(HashMap CheckMap, String MappingPointCode, String RulePointCode) throws LegendException {
		if(SysUtility.isEmpty(MappingPointCode) || SysUtility.isEmpty(RulePointCode) || SysUtility.isEmpty(CheckMap)){
			return;
		}
		SysUtility.BeginTrans();
		/**1.规则定义映射配置加载*/
		List ruleList = getRuleMapping(MappingPointCode);
		HashMap codeNameMap = getFieldCodeName(ruleList);//key value
		HashMap fieldMap = getFieldMap(ruleList);//key map
		/**2.规则定义检查数据加载*/
		List ruleChecks = getRuleCheck(RulePointCode);
		for (int i = 0; i < ruleChecks.size(); i++) {
			HashMap map = (HashMap)ruleChecks.get(i);
			final String indx = map.get("INDX").toString();
			SQLExecUtils.executeUpdate(SysUtility.exs_temp_id, new Callback() {
				@Override
				public void doIn(PreparedStatement ps) throws SQLException {
					ps.setString(1, indx);
				}
			});
		}
		/**3.规则定义检查数据-条件维护遍历*/
		List checkConList = SQLExecUtils.query4List("select c.* from rule_t_check_con c,exs_temp_id e where c.p_indx = e.id");
		HashMap checkConMap = new HashMap();
		SysUtility.GroupChildByPindx(checkConList, checkConMap);
		/**4.规则定义检查数据-结论维护遍历*/
		List checkResultList = SQLExecUtils.query4List("select c.* from rule_t_check_result c,exs_temp_id e where c.p_indx = e.id order by c.p_indx,result_level");
		HashMap checkResultMap = new HashMap();
		SysUtility.GroupChildByPindx(checkResultList, checkResultMap);
		/**5.规则配置轮询解析*/
		List hitRuleList = new ArrayList();
		HashMap SkipI = new HashMap();
		for (int i = 0; i < ruleChecks.size(); i++) {
			HashMap ruleMap = (HashMap) ruleChecks.get(i);
			String ruleNo = (String)ruleMap.get("RULE_NO");
			SkipI.put(ruleNo, i);
		}
		for (int i = 0; i < ruleChecks.size(); i++) {
			HashMap ruleMap = (HashMap) ruleChecks.get(i);
			try {
				String indx = (String)ruleMap.get("INDX");
				/**6.规则条件*/
				List ruleConList = (List)checkConMap.get(indx);
				if(SysUtility.isEmpty(ruleConList) || SysUtility.isEmpty((String)ruleMap.get("RULE_HIT_CON"))){//条件维护--表达式
					continue;//规则没有配置条件或表达式为空，跳过规则
				}
				ruleMap.put("RULE_HIT_CX", ruleConList);
				/**6.结论维护*/
				List ruleResultList = (List)checkResultMap.get(indx);
				if(SysUtility.isEmpty(ruleResultList)){
					continue;//规则没有配置结论，跳过规则
				}
				ruleMap.put("RULE_HIT_EX", ruleResultList);
				/**7.判断规则是否已被执行过*/
				if(hitRuleList.contains(ruleMap.get("RULE_NO"))){
					return;//规则已执行过，不能重复执行
				}else{
					hitRuleList.add(ruleMap.get("RULE_NO"));
				}
				/**7.调用规则引擎核心方法*/
				boolean rt = checkRule(CheckMap, ruleMap,codeNameMap,fieldMap);
				/**8.正向跳转、反向跳转逻辑处理*/
				String ForwardSkip = (String)ruleMap.get("FORWARD_SKIP");//正向跳转
				String ReverseSkip = (String)ruleMap.get("REVERSE_SKIP");//反向跳转
				Integer ski = (Integer)SkipI.get(ruleMap.get("RULE_NO"));
				if(rt && SysUtility.isNotEmpty(ForwardSkip)){//正向结论
					if(SysUtility.isNotEmpty(ski) && ski > i){
						i = ski;
					}
				}else if(!rt && SysUtility.isNotEmpty(ReverseSkip)){//反向结论
					if(SysUtility.isNotEmpty(ski) && ski < i){
						i = ski;
					}
				}
			} catch (Exception e) {
				LogUtil.printLog("规则执行错误："+ruleMap.get("RULE_NO")+","+ruleMap.get("RULE_HIT_CON"), Level.ERROR);
			}
		}
	}
	
	/**
	 * 规则执行逻辑
	 * @param conditionExpress
	 * @param CheckMap
	 * @param fieldCodeNameMap
	 * @param fieldMap
	 * @return
	 */
	private static boolean checkRule(HashMap CheckMap,HashMap ruleMap,HashMap codeNameMap,HashMap fieldMap)throws LegendException {
		HashMap resultMaps = new HashMap();
		HashMap rowMaps = new HashMap();
		StringBuffer ruleAlias = new StringBuffer();
		
		List ruleConList = (List)ruleMap.get("RULE_HIT_CX");//条件集合
		String ruleHitCon = (String)ruleMap.get("RULE_HIT_CON");//replaceCodition();//条件表达式
		for (int i = 0; i < ruleConList.size(); i++) {
			HashMap tempMap = (HashMap)ruleConList.get(i);
			String conFlag = (String)tempMap.get("CON_FLAG");//条件标识
			String leftCompareType = (String)tempMap.get("CON_COMPARE_TYPE");//左边类型
			String leftItemsName = (String)tempMap.get("CON_ITEMS_NAME");//左边项名称
			String compareOperator = (String)tempMap.get("CON_OPERATORS");//操作符
			String rightType = (String)tempMap.get("CON_RIGHT_TYPE");//右边类型代码
			String rightItemsName = (String)tempMap.get("CON_RIGHT_ITEMS_NAME");//右边项名称
			
			if(ruleHitCon.indexOf(conFlag) < 0){
				continue;//条件表达式未被使用
			}
			String condition = "["+leftItemsName+"]";
			if("1".equals(rightType)){//字符串
				condition = condition + " "+compareOperator + " "+rightItemsName;
			}else if("2".equals(rightType)){//别名结果集
				condition = condition + " "+ compareOperator + " "+ "#"+ rightItemsName + "#";
				ruleAlias.append(rightItemsName).append(";");
			}else if("3".equals(rightType)){//比对项
				condition = condition +  " "+compareOperator + " "+ "["+rightItemsName+"]";
			}else if("4".equals(rightType)){//SQL
				condition = condition + " "+ compareOperator + " "+ "@"+ rightItemsName + "@";
			}else{//空默认为字符串处理方式
				condition = condition + " "+compareOperator + " "+ rightItemsName;
			}
			StringBuffer operator = new StringBuffer();
			String[] coditions = splitCondition(condition,operator);
			
			String fieldName = leftItemsName;
			String fieldCode = (String) codeNameMap.get(fieldName);
			if(SysUtility.isEmpty(fieldCode)){
				throw LegendException.getLegendException(ERR.NOT_FOUND_FIELD_MAPPING,new String[]{fieldName});
			}
			HashMap iFieldMap = (HashMap)fieldMap.get(fieldName);
			String tableName = (String)iFieldMap.get("TABLE_NAME");
			String tableCondition = (String)iFieldMap.get("TABLE_CONDITION");//过滤不需要校验的数据
			
			/************************递归处理校验数据,组装处理结果********************************/
			HashMap tableMap = getMinuteCheckMap(CheckMap, tableName, tableCondition);
			Set mapSet = tableMap.entrySet();
			for (Iterator it = mapSet.iterator(); it.hasNext();) {
				Entry entry = (Entry)it.next();
		        Object key = entry.getKey();
		        Object value = entry.getValue();
		        if(value instanceof Map){
		        	HashMap row = (HashMap)value;
		        	CompareLeftRightValues(row,ruleMap,codeNameMap,fieldMap,fieldName,fieldCode,coditions,conFlag,operator,leftCompareType,resultMaps,rowMaps);
		        }else if(value instanceof List){
		        	List rows = (List) value;
		        	for (int j = 0; j < rows.size(); j++) {
		        		HashMap row = (HashMap)rows.get(j);
		        		CompareLeftRightValues(row,ruleMap,codeNameMap,fieldMap,fieldName,fieldCode,coditions,conFlag,operator,leftCompareType,resultMaps,rowMaps);
					}
		        }
			}
			/************************递归处理校验数据,组装处理结果********************************/
		}
		/*******************使用运算符进行计算表达式计算,解析处理结果*******************************/
		Set mapSet = resultMaps.entrySet();
		boolean checkResult = false;
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
	        String ruleId = (String)entry.getKey();
	        HashMap conResult = (HashMap)entry.getValue();
	        checkResult = CalcConditionExpress(conResult, ruleHitCon);
	        if(!checkResult){
	        	continue;//return checkResult;//条件表达式解析结果未不通过，判定此规则为未命中。
	        }
	        List ruleResultList = (List)ruleMap.get("RULE_HIT_EX");
	        if(SysUtility.isEmpty(ruleResultList)){
	        	return false;//没有配置结论，判定此规则为未命中。
	        }
	        HashMap row = (HashMap)rowMaps.get(ruleId);//业务数据行，精确到子表的某一行
	        /****************遍历结论：填充规则明细****************************/
        	float chkhitlong = GetChkHitPercent(ruleMap);
        	float roundPercent = Math.round(Math.random() * 100 * 10000);
        	boolean hitFlag = false;
        	if(chkhitlong >= roundPercent){
        		hitFlag = true;
			}
	        if(hitFlag){//命中结论
	        	checkResult = true;
	        	for (int j = 0; j < ruleResultList.size(); j++) {
		        	HashMap tempMap = (HashMap)ruleResultList.get(j);
		        	tempMap.put("CHK_HIT_PERCENT", ruleMap.get("CHK_HIT_PERCENT"));
		        	tempMap.put("CR_RATE", ruleMap.get("CR_RATE"));
		        	tempMap.put("RULE_NO", ruleMap.get("RULE_NO"));
		        	tempMap.put("POINT_CODE", ruleMap.get("POINT_CODE"));
		        	tempMap.put(tempMap.get("RESULT_TYPE_CODE")+"_ALIAS", ruleAlias.toString());
		        	tempMap.remove("INDX");
		        	tempMap.remove("P_INDX");
		        	tempMap.remove("IS_ENABLED");
		        	tempMap.remove("ORG_ID");
		        	tempMap.remove("CREATE_TIME");
		        	tempMap.remove("CREATOR");
		        	tempMap.remove("MODIFY_TIME");
		        	tempMap.remove("MODIFYOR");
		        	tempMap.remove("REC_VER");
		        	//String ruleHitEx = (String)tempMap.get("RESULT_TYPE_CODE");//结论类别代码
				}
	        	List tmpList = (ArrayList)row.get("HIT_RULE");
	        	if(SysUtility.isNotEmpty(tmpList)){
	        		tmpList.addAll(ruleResultList);
	        		row.put("HIT_RULE", tmpList);
	        	}else{
	        		row.put("HIT_RULE", ruleResultList);
	        	}
	        }else{//如果百分比未命中，强制将规则设置为不通过
	        	checkResult = false;
	        }
		}
		/*******************使用运算符进行计算表达式计算,解析处理结果*******************************/
		return checkResult;
	}
	
	private static void CompareLeftRightValues(HashMap row,HashMap ruleMap,HashMap codeNameMap,HashMap fieldMap,
			String fieldName,String fieldCode,String[] coditions,String condition,StringBuffer operator,
			String leftCompareType,HashMap resultMaps,HashMap rowMaps) throws LegendException{
		String rowTmepName = "RULE_TEMP_ID";
    	String ruleId = (String)ruleMap.get(rowTmepName);
    	if(SysUtility.isEmpty(ruleId)){
    		ruleId = SysUtility.getMilliSeconds();
    		row.put(rowTmepName, ruleId);
    		ruleMap.put(rowTmepName, ruleId);
    	}
		
		/********获取左边项值的集合***************/
		List leftValue = GetLeftValue(row,ruleMap,codeNameMap,fieldMap,fieldName,fieldCode);
		/********获取右边项值的集合***************/
		List rightValue = GetRightValue(row,ruleMap,codeNameMap,fieldMap,fieldName,fieldCode,coditions);
		/********左边项与右边项比对**************************/
		HashMap iFieldMap = (HashMap)fieldMap.get(fieldName);
		String dataType = (String)iFieldMap.get("FIELD_DATA_TYPE");//字段在数据库中的类型
		boolean result = compareData(coditions,leftCompareType,leftValue,rightValue, operator.toString(), dataType);
		
		HashMap conResult = new HashMap();
		if(!conditionFilter(condition, conResult)){
			if (result){
				conResult.put(condition, "1");
			}else{
				conResult.put(condition, "0");
			}
		}
		//填充返回结果
		HashMap tempRt = (HashMap)resultMaps.get(ruleId);
    	if(SysUtility.isEmpty(tempRt)){
    		resultMaps.put(ruleId, conResult);
    		rowMaps.put(ruleId, row);
		}else{
			tempRt.putAll(conResult);
		}
	}
	
	private static boolean conditionFilter(String condition,HashMap conResult){
		boolean rt = false;
		if(SysUtility.isEmpty(condition)){
			return true;//默认跳过
		}
		//恒真恒假处理
		if("t".equalsIgnoreCase(condition)){
			conResult.put(condition, "1");
		}else if("f".equalsIgnoreCase(condition)){
			conResult.put(condition, "0");
		}
		/*
		if(condition.trim().startsWith("1") && condition.trim().endsWith("1")){
			if("==".equals(condition.trim().substring(1, condition.trim().length() - 1).trim())
					|| "=".equals(condition.trim().substring(1, condition.trim().length() - 1).trim())){
				conResult.put(condition, "1");
			}else if("!=".equals(condition.trim().substring(1, condition.trim().length() - 1).trim())){
				conResult.put(condition, "0");
			}
			return true;
		}*/
		return rt;
	}
	
	/***
	 * 替换表达式
	 * */
	private static String replaceCodition(String conditionExpress)throws LegendException{
		//替换且和或
		conditionExpress = conditionExpress.replaceAll("且", "AND")
						                   .replaceAll("或", "OR")
						                   .replaceAll("\\|\\|", "OR")
						                   .replaceAll("\\&\\&", "AND");
		// 替换右括号
		conditionExpress = conditionExpress.replaceAll("\\)+\\s*OR", " OR")
								   		   .replaceAll("\\)+\\s*AND", " AND")
								   		   .replaceAll("\\)+\\s*$", "");
		// 替换左括号
		conditionExpress = conditionExpress.replaceAll("\\s*OR\\s*\\(+"," OR")
		                           		   .replaceAll("\\s*AND\\s*\\(+", " AND")
		                           		   .replaceAll("^\\(+\\s*", "");
		// 替换 OR 与AND
		conditionExpress = conditionExpress.replaceAll("\\s*OR\\s*\\[","#OR#[")
		                           		   .replaceAll("\\s*OR\\s*\\(", "#OR#(")
		                           		   .replaceAll("\\s*AND\\s*\\[", "#AND#[")
		                           		   .replaceAll("\\s*AND\\s*\\(","#AND#(");
		// 替换操作符用于拆解子表达式
		conditionExpress = conditionExpress.replaceAll("#OR#", "#;#")
		                           		   .replaceAll("#AND#", "#;#");
		return conditionExpress;
	}
	
	private static List GetRightValue(HashMap tableMap,HashMap ruleMap,HashMap codeNameMap,HashMap fieldMap,String fieldName,String fieldCode,String[] coditions) throws LegendException{
		HashMap iFieldMap = (HashMap)fieldMap.get(fieldName);
		String tableName = (String)iFieldMap.get("TABLE_NAME");
		String tableCondition = (String)iFieldMap.get("TABLE_CONDITION");//过滤不需要校验的数据
		List rightValue = new ArrayList();
		if(SysUtility.isEmpty(coditions[1])){
			return rightValue;
		}
		String Value = coditions[1].trim();
		if(Value.startsWith("#") && Value.endsWith("#")) {//双#号为处理别名结果集
			final String processName = Value.substring(1,Value.length() - 1);
			try {
				StringBuffer SQL = new StringBuffer();
				SQL.append("select b.coulmn_value from RULE_T_ALIAS a,RULE_T_ALIAS_DETAIL b where a.indx = b.p_indx and a.is_enabled = '1' and a.process_name = ?");
				List lst = SQLExecUtils.query4List(SQL.toString(),new Callback() {
					public void doIn(PreparedStatement ps) throws SQLException {
						ps.setString(1, processName);
					}
				});
				for (int j = 0; j < lst.size(); j++) {
					HashMap temp = (HashMap)lst.get(j);
					rightValue.add(((String) temp.get("COULMN_VALUE")).substring(0,temp.get("COULMN_VALUE").toString().toString().length()-2));
				}
			} catch (Exception e) {
				LogUtil.printLog("处理别名结果集出错:"+processName+"，"+e.getMessage(), Level.ERROR);
			}
		}else if(Value.startsWith("[") && Value.endsWith("]")){//右边为关键字
			getNameValue(tableMap,fieldCode,tableName,rightValue);/**从报文读取左边值所有的值，值为空时，默认执行为0操作。*/
		}else if(Value.startsWith("@") && Value.endsWith("@")){//右边为SQL				
			String SQL = Value.substring(1,Value.length() - 1);
			String reg=SQL.substring(SQL.indexOf('#'), SQL.lastIndexOf('#')+1);
			String para=SQL.substring(SQL.indexOf('#')+1, SQL.lastIndexOf('#'));
			if(SysUtility.isNotEmpty(reg)){
				SQL=SQL.replaceAll(reg, "'"+(tableMap.get(para)==null?"":tableMap.get(para.toUpperCase()).toString())+"'");
			}
			try {
				rightValue = SQLExecUtils.query4StringList(SQL);
			} catch (Exception e) {
				LogUtil.printLog("处理SQL出错:"+SQL+"，"+e.getMessage(), Level.ERROR);
			}
		}else{//字符串
			if (coditions[1].trim().startsWith("'") && coditions[1].trim().endsWith("'")) {
				rightValue.add(coditions[1].trim().substring(1,coditions[1].trim().length() - 1));
			} else {
				rightValue.add(coditions[1].trim());
			}
		}
		return rightValue;
	}
			
	
	private static List GetLeftValue(HashMap tableMap,HashMap ruleMap,HashMap codeNameMap,HashMap fieldMap,String fieldName,String fieldCode) throws LegendException{
		HashMap iFieldMap = (HashMap)fieldMap.get(fieldName);
		String tableName = (String)iFieldMap.get("TABLE_NAME");
		String leftProcessType = (String)iFieldMap.get("PROCESS_TYPE");//数据处理类型
		
		List leftValue = new ArrayList();
		/**数据处理类型0表示从前台数据取数、1表示从SQL语句查询获取的值 、2表示自己定义类的取值、3从缓存表取、4从*/
		if("1".equals(leftProcessType)) {
			try {
				AccessDbBySql accessDbBysql = new AccessDbBySql();
				List lst = accessDbBysql.getValueBySql(tableMap, fieldName,codeNameMap,fieldMap);
				StringBuffer hitMsg = new StringBuffer();
				for (int k = 0; k < lst.size(); k++) {
					HashMap map = (HashMap)lst.get(k);
					Set mapSet = map.entrySet();
					int j = 0;
					for (Iterator it = mapSet.iterator(); it.hasNext();) {
						Entry entry = (Entry)it.next();
						if(j == 0 && SysUtility.isNotEmpty(entry.getValue())){
							leftValue.add(entry.getValue());
						}else if(j == 1 && SysUtility.isNotEmpty(entry.getValue())){
							hitMsg.append(entry.getValue());
						}
						j++;
					}
				}
				if(SysUtility.isNotEmpty(hitMsg)){
					ruleMap.put("WARNING_INFO", hitMsg);
				}
			} catch (Exception e) {
				throw LegendException.getLegendException(ERR.RULE_INVALID_002,new String[]{fieldName});
			}
		}else if ("2".equals(leftProcessType)) {
			try {
				String className = (String)iFieldMap.get("PROCESS_METHOD");//对应的处理类
				className = "com.easy.rule.ActualTotalValueInfo";//统一接口类处理
				IValueInfo valueInfo = (IValueInfo) Class.forName(className).newInstance();
				leftValue = valueInfo.getValue(tableMap, fieldName, tableName);
			} catch (Exception ex) {
				throw LegendException.getLegendException(ERR.RULE_INVALID_002,new String[]{fieldName});
			}
		}else if ("3".equals(leftProcessType)) {
			HashSet chche = (HashSet)pubCache.get(tableName);
			getNameValue(tableMap,fieldCode,tableName,leftValue);
			HashSet hitValueSet = new HashSet();
			for (int j = 0; j < leftValue.size(); j++) {
				String left = (String)leftValue.get(j);
				if(SysUtility.isNotEmpty(chche) && chche.contains(left)){
					hitValueSet.add(left);
				}
			}
			if(hitValueSet.size() > 0){
				leftValue.clear();
				leftValue.add("true");
			}
			ruleMap.put("HIT_VALUE", hitValueSet);
		}else if ("4".equals(leftProcessType)) {
			try {
				AccessDbBySql accessDbBysql = new AccessDbBySql();
				List lst = accessDbBysql.getValueBySql2(tableMap, fieldName,codeNameMap,fieldMap);
				StringBuffer hitMsg = new StringBuffer();
				for (int k = 0; k < lst.size(); k++) {
					HashMap map = (HashMap)lst.get(k);
					Set mapSet = map.entrySet();
					int j = 0;
					for (Iterator it = mapSet.iterator(); it.hasNext();) {
						Entry entry = (Entry)it.next();
						if(j == 0 && SysUtility.isNotEmpty(entry.getValue())){
							leftValue.add(entry.getValue());
						}else if(j == 1 && SysUtility.isNotEmpty(entry.getValue())){
							hitMsg.append(entry.getValue());
						}
						j++;
					}
				}
				if(SysUtility.isNotEmpty(hitMsg)){
					ruleMap.put("WARNING_INFO", hitMsg);
				}
			} catch (Exception e) {
				throw LegendException.getLegendException(ERR.RULE_INVALID_002,new String[]{fieldName});
			}
		}else{
			getNameValue(tableMap,fieldCode,tableName,leftValue);/**从报文读取左边值所有的值，值为空时，默认执行为0操作。*/
		}
		return leftValue;
	}
	
	
	/**
	 * 返回tableName级别的校验数据
	 * @param CheckMap
	 * @param tableName
	 * @return
	 */
	private static HashMap getMinuteCheckMap(Map CheckMap,String tableName,String tableCondition) {
		HashMap tableMap = new HashMap();
		Set mapSet = CheckMap.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
			Entry entry = (Entry)it.next();
	        Object key = entry.getKey();
	        Object value = entry.getValue();
			if (key.toString().equalsIgnoreCase(tableName)) {
				Object tmpObj = (Object)CheckMap.get(key);
				if (tmpObj instanceof List) {
					if(SysUtility.isNotEmpty(tableCondition)){
						List list = (List) tmpObj;
						for(int i = 0 ; i < list.size() ; i++){
							HashMap temp = (HashMap) list.get(i);
		   			    	String[] condtions = tableCondition.split("=");
		   			    	String compareString = SysUtility.isEmpty((String)temp.get(condtions[0].toLowerCase())) 
		   			    	                         ? (String)temp.get(condtions[0].toUpperCase()) : (String)temp.get(condtions[0].toLowerCase());
							if(condtions[1].equals(compareString)){
								if(tableMap.get(tableName) == null){
									List tempList = new ArrayList();
									tempList.add(temp);
									tableMap.put(tableName,tempList);
								}else{
									List tempList = (List)tableMap.get(tableName);
									tempList.add(temp);
								}
		   				 	}
						}
					}else{
						tableMap.put(tableName, tmpObj);
					}
				} else if (tmpObj instanceof Map) {
					List tmpList = new ArrayList();
					tmpList.add(tmpObj);
					tableMap.put(tableName, tmpList);
				}
			}else if(value instanceof Map){
				HashMap map2 = (HashMap)value;
   			    Set mapSet2 = map2.entrySet();
   			    for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
   			    	Entry entry2 =  (Entry)it2.next();
   	                Object key2 = entry2.getKey();
   	                if (key2.toString().equalsIgnoreCase(tableName)) {
   	                	Object tmpObj = (Object)map2.get(key2);
   					    if (tmpObj instanceof List) {
   					    	tableMap.put(tableName, tmpObj);
   					    } else if (tmpObj instanceof Map) {
   					    	List tmpList = new ArrayList();
   						    tmpList.add(tmpObj);
   						    tableMap.put(tableName, tmpList);
   					     }
   				    }
   	            }
			 }else if(value instanceof List){
				 List list = (List) value;
   			     for(int i = 0 ; i < list.size() ; i++){
   			    	Object ob = list.get(i);
   			    	if(ob instanceof HashMap){
   			    		HashMap temp = (HashMap) list.get(i);
   					    HashMap tempResult = getMinuteCheckMap(temp, tableName, tableCondition);
   			     	    tableMap.putAll(tempResult);
   			    	}else{
   			    		continue;
   			    	}
   			     }
			 }
		}
		return tableMap;
	}
	
	/**
	 * 获取字段值
	 * param: CheckMap:校验的数据
	 * param: strKey：取值的key   
	 * return String
	 * */
	protected static void getNameValue(HashMap CheckMap, String strKey,String tableName,Object result)throws LegendException {
		if(strKey == null) return;
		boolean msg_flag=false;
		if(strKey.equals("HS_CODES")||strKey.equals("HSCODES")){
			strKey=strKey.substring(0, strKey.length()-1);
			msg_flag=true;
		}
		Set mapSet = CheckMap.entrySet();
		for (Iterator it = mapSet.iterator(); it.hasNext();) {
		    Entry entry =  (Entry)it.next();
		    Object key = entry.getKey();
		    Object value = entry.getValue();
		    if(strKey.equals(key)){
		    	if(result instanceof List) {
		    		if("".equals(value)){
		    			value = null;
		    		}else if(msg_flag) {
		               //hs编码总共十位,前八位为主码后两位为附加码
						value=value.toString().substring(0,value.toString().toString().length()-2);
		    			//value=value.toString().substring(0,value.toString().toString().length());
					}
		    		((List)result).add(value);
		    	}
		    }else if(value instanceof Map){
		    	HashMap map2 = (HashMap)value;
				Set mapSet2 = map2.entrySet();
				if(key.equals(tableName)){
					for (Iterator it2 = mapSet2.iterator(); it2.hasNext();) {
			            Entry entry2 =  (Entry)it2.next();
			            Object key2 = entry2.getKey();
			            Object value2 = entry2.getValue();
			            if(strKey.equals(key2)){
			            	if(result instanceof List) {
			            		if("".equals(value)){
			            			value = null;
			            		}
			            		((List)result).add(value2);
			            	}
			            }
			        }
				}
		    }else if(value instanceof List){
		    	List list = (List) value;
				for(int i = 0 ; i < list.size() ; i++){
					HashMap temp = (HashMap) list.get(i);
					if(key.equals(tableName))
						getNameValue(temp, strKey,tableName,result);
				}
		    }
		}
	}
	/**
	 * 表达式计算
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType
	 * @return
	 */
	private static boolean compareData(String[] coditions,String leftCompareType,List leftValue, List rightValues,String operate, String dataType) {
		if (isEmpty(leftValue)){
			leftValue.add("");
		}
		String Value=coditions[1].trim();
		
		if (isEmpty(rightValues)&&"NOTIN".equals(operate.trim())) {
			return true;
		}else if(isEmpty(rightValues)){
			return false;
		}
		boolean result = false;
		String rightValue = (String)rightValues.get(0);
		if (" ~HAVE ".equalsIgnoreCase(operate)) {
			result = RuleCompare.containValue(leftValue, rightValue);
		} else if (" ~IS_CN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.checkInputChinese(leftValue, rightValue);
		} else if (" ~R_C ".equalsIgnoreCase(operate)) {
			result = RuleCompare.checkRowColumnTotal(leftValue, rightValue);
		} else if (" <=LEN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.lessEqualLength(leftValue, rightValue);
		}else if (" <LEN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.lessLength(leftValue, rightValue);
		} else if (" =LEN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.EqualLength(leftValue, rightValue);
		} else if (" >=LEN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.moreEqualLength(leftValue, rightValue);
		} else if (" >LEN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.moreLength(leftValue, rightValue);
		} else if (" IN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.includeCodition(leftValue, rightValues);
		} else if (" LIKE ".equalsIgnoreCase(operate)) {
			result = RuleCompare.likeValue(leftValue, rightValue);
		} else if (" NOTIN ".equalsIgnoreCase(operate)) {
			result = RuleCompare.notIncludeCodition(leftValue, rightValues);
		} else if (" NOTLIKE ".equalsIgnoreCase(operate)) {
			result = RuleCompare.notLikeValue(leftValue, rightValue);
		} else if (" ^ ".equalsIgnoreCase(operate)) {
			result = RuleCompare.startWiths(leftValue, rightValue);
		} else if (" ∈ ".equalsIgnoreCase(operate)) {
			result = RuleCompare.BelongString(leftValue, rightValues);
		} else if((Value.startsWith("#") && Value.endsWith("#"))||(coditions[1].trim().startsWith("#") && coditions[1].trim().endsWith("#"))) {//双#号为处理别名结果集
			result = RuleCompare.BelongString(leftValue, rightValues);
		} else{
			if (dataType.equals(Constants.DECIMAL)){// number
				result = RuleCompare.compareNumber(leftValue, rightValue,operate);
			} else if (dataType.equals(Constants.DATE)){// date
				result = RuleCompare.compareDate(leftValue, rightValue, operate);
			} else {//String,number
				result = RuleCompare.compareString(leftCompareType ,leftValue, rightValue, operate);
			}
		}
		return result;
	}
	
	/**
	 * 判断是否包含关键字
	 * 
	 * @param condition
	 * @return
	 */
	protected static String getKeyWord(List exp) {
		String rightValue = (String)exp.get(0);
		int leftIndex = rightValue.indexOf("[");
		int rightIndex = rightValue.indexOf("]");
		if (leftIndex >= 0 && rightIndex >= 0) {
			return rightValue.substring(leftIndex, rightIndex + 1);
		}
		return "";
	}
	
	/**
	 * 判断是否包含关键字
	 * 
	 * @param condition
	 * @return
	 */
	protected static String getKeyWord(String rightValue) {
		int leftIndex = rightValue.indexOf("[");
		int rightIndex = rightValue.indexOf("]");
		if(-1 == leftIndex || -1 == rightIndex){
			return "";
		}
		String result = rightValue.substring(leftIndex, rightIndex + 1);
		if (leftIndex >= 0 && rightIndex >= 0) {
			return result;
		}
		return "";
	}
	
	/**
	 * 从前台数据中获取船公司的值
	 * @param map
	 * @param moduleName
	 * @return
	 */
	private static String getCarrier(HashMap CheckMap,String bizConfigType,HashMap fieldCodeNameMap)throws LegendException {
		List result = new ArrayList();
		String mainTableName="";
		if("FF".equals(bizConfigType)){
			mainTableName = "CONSIGN";
		}else if("SA".equals(bizConfigType)){
			mainTableName = "BOOKINGINFO";
		}
		getNameValue(CheckMap, (String)fieldCodeNameMap.get("船公司"),mainTableName,result);
		if(result.size() > 0)
			return (String)result.get(0);
		return "";
	}
	
	/**
	 * 获取字段映射关系
	 * param:
	 *       fieldList:字段映射集合
	 * return
	 *       HashMap:字段：编码、名称
	 * */
	private static HashMap getFieldCodeName(List fieldList) throws LegendException {
		HashMap fieldCodeName = new HashMap();
		for(int i = 0 ; i < fieldList.size(); i++){
			HashMap temp = (HashMap)fieldList.get(i);
			fieldCodeName.put(temp.get("FIELD_NAME"), temp.get("FIELD_CODE"));
		}
		return fieldCodeName;
	}
	
	/**
	 * 获取字段映射关系
	 * param:
	 *       fieldList:字段映射集合
	 * return
	 *       HashMap:字段名称、集合
	 * */
	private static HashMap getFieldMap(List fieldList) throws LegendException {
		HashMap fieldMap = new HashMap();
		for(int i = 0 ; i < fieldList.size(); i++){
			HashMap temp = (HashMap)fieldList.get(i);
			fieldMap.put(temp.get("FIELD_NAME"), temp);
		}
		return fieldMap;
	}
	
	/**
	 * 拆解子表达式，如把字段+操作符+值，拆解成字段，值的数组
	 * 
	 * @param condition
	 * @return
	 */
	private static String[] splitCondition(String condition,StringBuffer operators) {
		operators.delete(0, operators.toString().length());
		String operator = "";
		if (condition.toUpperCase().indexOf(" == ") >= 0) {
			operator = " == ";
		} else if (condition.toUpperCase().indexOf("==") >= 0) {
			operator = "==";
		} else if (condition.toUpperCase().indexOf(" >LEN ") >= 0) {
			operator = " >LEN ";
		} else if (condition.toUpperCase().indexOf(" <LEN ") >= 0) {
			operator = " <LEN ";
		} else if (condition.toUpperCase().indexOf(" >=LEN ") >= 0) {
			operator = " >=LEN ";
		} else if (condition.toUpperCase().indexOf(" <=LEN ") >= 0) {
			operator = " <=LEN ";
		} else if (condition.toUpperCase().indexOf(" =LEN ") >= 0) {
			operator = " =LEN ";
		} else if (condition.toUpperCase().indexOf(" >= ") >= 0) {
			operator = " >= ";
		} else if (condition.toUpperCase().indexOf(" != ") >= 0) {
			operator = " != ";
		} else if (condition.toUpperCase().indexOf(" <= ") >= 0) {
			operator = " <= ";
		} else if (condition.toUpperCase().indexOf(" < ") >= 0) {
			operator = " < ";
		}else if (condition.toUpperCase().indexOf(" > ") >= 0) {
			operator = " > ";
		} else if (condition.toUpperCase().indexOf(" ~HAVE ") >= 0) {
			operator = " ~HAVE ";
		} else if (condition.toUpperCase().indexOf(" NOTIN ") >= 0) {
			operator = " NOTIN ";
		} else if (condition.toUpperCase().indexOf(" IN ") >= 0) {
			operator = " IN ";
		} else if (condition.toUpperCase().indexOf(" ^ ") >= 0) {
			operator = " ^ ";
		} else if (condition.toUpperCase().indexOf(" LIKE ") >= 0) {
			operator = " LIKE ";
		} else if (condition.toUpperCase().indexOf(" NOTLIKE ") >= 0) {
			operator = " NOTLIKE ";
		} else if (condition.toUpperCase().indexOf(" ~R_C ") >= 0) {
			operator = " ~R_C ";
		} else if (condition.toUpperCase().indexOf(" ~IS_CN ") >= 0) {
			operator = " ~IS_CN ";
		}else if (condition.toUpperCase().indexOf(" ∈ ") >= 0) {
			operator = " ∈ ";
		}
		operators.append(operator);
		String[] conditions = condition.toUpperCase().split(operator);
		return conditions;
	}
	
	/**
	 * 根据子条件比较结果的表达式，计算表达式的结果，计算结果大于0则表达式计算结果为true
	 * 
	 * @param hsConditionResult
	 * @param condition
	 * @return
	 */
	private static boolean CalcConditionExpress(Map hsConditionResult,String condition) throws LegendException {
		String finalCondition = replaceCoditionResult(hsConditionResult,condition);
		finalCondition = finalCondition.replaceAll("OR", "+").replaceAll("\\|\\|", "+").replaceAll("或", "+");
		finalCondition = finalCondition.replaceAll("AND", "*").replaceAll("\\&\\&", "*").replaceAll("且", "*");
		Eval eval = new Eval();
		int result = 0;
		try {
			result = eval.eval(finalCondition);
		} catch (LegendException ex) {
			throw ex;
		}
		return result > 0;
	}
	/**
	 * 根据从传入子条件结果MAP替换条件中的表达式，并返回替换之后的表达式; 子条件格式为字段 + 操作数 + 数值
	 */
	private static String replaceCoditionResult(Map perConditioinCompareResult,String condition) {
		String replaceCondition = condition;
		Set keys = perConditioinCompareResult.keySet();
		Iterator keyIter = keys.iterator();
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();
			String value = (String) perConditioinCompareResult.get(key);
			key = key.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]")
			         .replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)")
			         .replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}")
			         .replaceAll("\\'", "\\\\'").replaceAll("\\|", "\\\\|");
			replaceCondition = replaceCondition.replaceAll(key, value);
		}
		return replaceCondition;
	}
	
	/**
	 * 判断集合是否为空
	 * @param list
	 * @return List
	 */
	protected static boolean isEmpty(List list){
		if(null == list || list.size() < 1 )
			return true;
		return false;
	}
	
	/**
	 * 判断集合所有元素是否都为空
	 * @param list
	 * @return List
	 */
	protected static boolean isAllEmpty(List list){
		if(isEmpty(list)) return true;
		
		int count = 0;
		for(int i = 0 ; i < list.size() ; i++){
			Object obj = list.get(i);
			if(null == obj || "".equals(obj))
				count++;
		}
		if(count == list.size()) return true;
		return false;
	}
	
	private static List getRuleCheck(final String pointCode) throws LegendException {
		List list = new ArrayList();
		
		final String appRecVer = BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentPartId());
		StringBuffer sql = new StringBuffer();
		sql.append("select r.indx,r.point_code,r.rule_no,r.rule_name,r.chk_hit_percent,r.chk_level,r.forward_skip,r.reverse_skip,r.rule_hit_con,r.cr_rate");
		sql.append("  from rule_t_check r");
		sql.append(" where is_enabled = '1'");
		sql.append("   and (active_time_begin is null or active_time_begin <= sysdate)");
		sql.append("   and (active_time_end is null or active_time_end >= sysdate)");
		sql.append("   and r.point_code = ?");
		if(SysUtility.isNotEmpty(appRecVer)){
			sql.append("   and app_rec_ver = ?");
		}
		sql.append("   order by r.chk_level");
		list = SQLExecUtils.query4List(sql.toString(), new Callback() {
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, pointCode);
				if(SysUtility.isNotEmpty(appRecVer)){
					ps.setString(2, appRecVer);
				}
			}
		});
		return list;
	}
	
	private static List getRuleMapping(final String pointCode) throws LegendException {
		List list = new ArrayList();
		if(SysUtility.isEmpty(pointCode))  
			return list;
		
		final String appRecVer = BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentPartId());
		StringBuffer sql = new StringBuffer();
		sql.append("select table_name,");
		sql.append("       field_name,");
		sql.append("       field_code,");
		sql.append("       field_data_type,");
		sql.append("       process_type,");
		sql.append("       process_method,");
		sql.append("       filter_condition");
		sql.append("  from RULE_B_MAPPING");
		sql.append(" where point_code = ?");
		if(SysUtility.isNotEmpty(appRecVer)){
			sql.append("   and app_rec_ver = ?");
		}
		list = SQLExecUtils.query4List(sql.toString(), new Callback() {
			public void doIn(PreparedStatement ps) throws SQLException {
				ps.setString(1, pointCode);
				if(SysUtility.isNotEmpty(appRecVer)){
					ps.setString(2, appRecVer);
				}
			}
		});
		return list;
	}
	
	public static void autoCacheTable() throws LegendException {
		StringBuffer sql = new StringBuffer();
		sql.append("select cache_table_name,cache_field_code,cache_condition,table_name from RULE_B_MAPPING where process_type = 3");
		List list = SQLExecUtils.query4List(sql.toString(), new Callback() {
			public void doIn(PreparedStatement ps) throws SQLException {
			}
		});
		for(int i = 0 ; i < list.size() ; i++){
			HashMap map = (HashMap)list.get(i);
			String cacheTableName = (String)map.get("CACHE_TABLE_NAME");
			String cacheFieldCode = (String)map.get("CACHE_FIELD_CODE");
			String tableName = (String)map.get("TABLE_NAME");
			String cacheCondition = (String)map.get("CACHE_CONDITION");
			String cacheSql = "select "+cacheFieldCode+" from "+cacheTableName+" "+cacheCondition;
			Set set = SQLExecUtils.query4Set(cacheSql.toString());
			pubCache.put(tableName, set);
		}
	}
	
	public static float GetChkHitPercent(HashMap ruleMap) throws LegendException {
		String chkHitPercent = (String)ruleMap.get("CHK_HIT_PERCENT");
		chkHitPercent = chkHitPercent.replaceAll("%", "");
		double chkhitlong = 0;
		try {
			if(SysUtility.isNotEmpty(chkHitPercent)){
				chkhitlong = Double.parseDouble(chkHitPercent);
			}else{
				chkhitlong = 100;
			}
		} catch (Exception e) {
			LogUtil.printLog("概率设置错误！,小数位限制4位，范围：0--100"+e.getMessage(), Level.ERROR);
		}
		String tstr = chkhitlong * 10000+"";
		return Float.valueOf(tstr).floatValue();
	}
	
	
	
}
