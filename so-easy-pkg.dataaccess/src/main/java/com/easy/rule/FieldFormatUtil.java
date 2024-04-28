package com.easy.rule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easy.access.Datas;
import com.easy.bizconfig.BizConfigFactory;
import com.easy.exception.LegendException;
import com.easy.query.SQLBuild;
import com.easy.utility.SysUtility;

public class FieldFormatUtil {
	private static HashMap<String,List> cacheFieldMap = new HashMap<String,List>();
	
	public static String checkFieldCode(Datas datas, String pointCode) throws LegendException{
		StringBuffer ErrorMsg = new StringBuffer();
		if(SysUtility.isEmpty(pointCode) || SysUtility.isEmpty(datas)){
			return "";
		}
		List fieldFormats = getFieldFormat(pointCode);
		for (int i = 0; i < fieldFormats.size(); i++) {
			HashMap<String,String> fieldFormatMap = (HashMap) fieldFormats.get(i);
			String tableName = fieldFormatMap.get("TABLE_NAME");
			String fieldCode = fieldFormatMap.get("FIELD_CODE");
			String fieldDataType = fieldFormatMap.get("FIELD_DATA_TYPE");
			//校验行数与列数是否合符字段格式里定义规则
			String isControlRow = fieldFormatMap.get("IS_CONTROL_ROW");
			String rowTotal = fieldFormatMap.get("ROW_TOTAL");
			String colTotal = fieldFormatMap.get("COL_TOTAL");
			//校验字段录入总字符数是否合符字段格式里定义规则
			String isControlTotal = fieldFormatMap.get("IS_CONTROL_TOTAL");
			String charTotal = fieldFormatMap.get("CHAR_TOTAL");
			String charTotalStart = fieldFormatMap.get("CHAR_TOTAL_START");
			//是否可以录入中文、数字
			String isChinese = fieldFormatMap.get("IS_CHINESE");
			//检验是否是必输的
			String isMustInput = fieldFormatMap.get("IS_MUSTINPUT");
			//运算符方式
			String operator = fieldFormatMap.get("OPERATOR");
			String fieldValue = fieldFormatMap.get("FIELD_VALUE");
			String fieldValueCn = fieldFormatMap.get("FIELD_VALUE_CN");
			String cselectFlag = fieldFormatMap.get("CSELECT_FLAG");

			if(datas.has(tableName)){
				for (int j = 0; j < datas.GetTableRows(tableName); j++) {
					Object left = datas.GetTableValue(tableName, fieldCode, j);
					StringBuffer TempMsg = new StringBuffer();
					TempMsg.append(checkRowColumnTotal(left,isControlRow,rowTotal,colTotal));
					TempMsg.append(checkCharTotal(left,isControlTotal,charTotal,charTotalStart));
					TempMsg.append(checkIsInputChinese(left, isChinese));
					TempMsg.append(checkIsMustInput(left, isMustInput));
					TempMsg.append(compareData(left,fieldValue,operator,fieldDataType,cselectFlag,fieldValueCn));
					if(SysUtility.isNotEmpty(TempMsg)){
						ErrorMsg.append("["+fieldFormatMap.get("WARNING_TYPE")+"]")
						        .append(fieldFormatMap.get("TABLE_NAME")+"."+fieldFormatMap.get("FIELD_CODE"))
						        .append(TempMsg).append("\n");
					}
				}
			}
		}
		return ErrorMsg.toString();
	}
	
	
	public static String checkField(HashMap CheckMap,String pointCode)throws Exception{
		String processMsg = "";
		/***必填项校验****/
		List list = FieldFormatUtil.checkFieldFormat(CheckMap, pointCode);
		if(SysUtility.isNotEmpty(list)){
			StringBuffer fieldRemark = new StringBuffer();
			for (int j = 0; j < list.size(); j++) {
				HashMap returnMap = (HashMap)list.get(j);
				if(j > 0){
					fieldRemark.append("\n");
				}
				//[警告]XXXX1001,件数 xmlDocumentName.qay 此字段是必输项；
				fieldRemark.append("["+returnMap.get("WARNING_TYPE")+"]");
				fieldRemark.append(returnMap.get("RULE_NO")+",");
				fieldRemark.append(returnMap.get("FIELD_NAME")+" "+returnMap.get("TABLE_NAME")+"."+returnMap.get("FIELD_CODE"));
				fieldRemark.append(returnMap.get("ERROR_MESSAGE"));
			}
			processMsg = fieldRemark.toString();
		}
		return processMsg;
	}
	
	public static List checkFieldFormat(HashMap CheckMap, String pointCode) throws LegendException{
		if(SysUtility.isEmpty(pointCode) || SysUtility.isEmpty(CheckMap)){
			return null;
		}
		/**规则定义检查数据加载*/
		List fieldFormats = getFieldFormat(pointCode);
		/**规则配置调用*/
		List result = new ArrayList();
		for (int i = 0; i < fieldFormats.size(); i++) {
			HashMap<String,String> fieldFormatMap = (HashMap) fieldFormats.get(i);
			String tableName = fieldFormatMap.get("TABLE_NAME");
			String fieldCode = fieldFormatMap.get("FIELD_CODE");
			String fieldDataType = fieldFormatMap.get("FIELD_DATA_TYPE");
			//校验行数与列数是否合符字段格式里定义规则
			String isControlRow = fieldFormatMap.get("IS_CONTROL_ROW");
			String rowTotal = fieldFormatMap.get("ROW_TOTAL");
			String colTotal = fieldFormatMap.get("COL_TOTAL");
			//校验字段录入总字符数是否合符字段格式里定义规则
			String isControlTotal = fieldFormatMap.get("IS_CONTROL_TOTAL");
			String charTotal = fieldFormatMap.get("CHAR_TOTAL");
			String charTotalStart = fieldFormatMap.get("CHAR_TOTAL_START");
			//是否可以录入中文、数字
			String isChinese = fieldFormatMap.get("IS_CHINESE");
			//检验是否是必输的
			String isMustInput = fieldFormatMap.get("IS_MUSTINPUT");
			//运算符方式
			String operator = fieldFormatMap.get("OPERATOR");
			String fieldValue = fieldFormatMap.get("FIELD_VALUE");
			String fieldValueCn = fieldFormatMap.get("FIELD_VALUE_CN");
			String cselectFlag = fieldFormatMap.get("CSELECT_FLAG");
			
			List leftValue = new ArrayList();
			getNameValue(CheckMap, fieldCode,tableName, leftValue);
			List errorList = new ArrayList();
			if(leftValue.size() == 0){
				errorList.add(checkIsMustInput(null, isMustInput));
			}else{
				for (int j = 0; j < leftValue.size(); j++) {
					Object left = leftValue.get(j);
					errorList.add(checkRowColumnTotal(left,isControlRow,rowTotal,colTotal));
					errorList.add(checkCharTotal(left,isControlTotal,charTotal,charTotalStart));
					errorList.add(checkIsInputChinese(left, isChinese));
					errorList.add(checkIsMustInput(left, isMustInput));
					errorList.add(compareData(left,fieldValue,operator,fieldDataType,cselectFlag,fieldValueCn));
				}
			}
			
			StringBuffer errorMessage = new StringBuffer();
			for (int k = 0; k < errorList.size(); k++) {
				String str = (String)errorList.get(k);
				if(SysUtility.isEmpty(str)){
					continue;
				}
				if(errorMessage.indexOf(str) >= 0){
					continue;
				}else if(errorMessage.length() > 0){
					errorMessage.append("\n"+str);
				}else{
					errorMessage.append(str);
				}
			}
			if (errorMessage.length() > 0) {
				HashMap returnMap = new HashMap();
				returnMap.put("RULE_NO", fieldFormatMap.get("RULE_NO"));
				returnMap.put("RULE_NAME", fieldFormatMap.get("RULE_NAME"));
				returnMap.put("WARNING_TYPE", fieldFormatMap.get("WARNING_TYPE"));
				returnMap.put("WARNING_INFO", fieldFormatMap.get("WARNING_INFO"));
				returnMap.put("ERROR_MESSAGE", errorMessage);
				returnMap.put("TABLE_NAME", tableName);
				returnMap.put("FIELD_CODE", fieldCode);
				returnMap.put("FIELD_NAME", fieldFormatMap.get("FIELD_NAME"));
				result.add(returnMap);
			}
		}
		return result;
	}
	
	/**
	 * 校验行数与列数是否合符字段格式里定义规则
	 * @param value
	 * @return
	 */
	private static String checkRowColumnTotal(Object value,String isControlRow,String rowTotal,String colTotal){
		String result = "";
		if(SysUtility.isEmpty(value) || SysUtility.isEmpty(isControlRow)|| "N".equalsIgnoreCase(isControlRow))
			return result;
		
		String checkValue = (String) value;
		if(checkValue.length() < 1)
			return result;
		
		String split = getSplitString(checkValue);
		String [] rowArray = checkValue.split(split);
		
		if(rowArray.length > Integer.valueOf(rowTotal).intValue())
			result = "此字段只能输入" + rowTotal + "行，现在已输入" + String.valueOf(rowArray.length) + "行；";
		
		for(int i = 0; i < rowArray.length;i++){
			if(rowArray[i].length() > Integer.valueOf(colTotal).intValue()){
				if(result.length() > 0){
					result = result + "\r\n"+ "此字段只能输入"+ colTotal + "列，现在已输入" + rowArray[i].length() + "列；";;
				} else {
					result = "此字段只能输入"+ colTotal + "列，现在已输入" + rowArray[i].length() + "列；";
				}
				break;
			}
		}
		return result;
	}
	
	/**
	 * 校验字段录入总字符数是否合符字段格式里定义规则
	 * @return
	 */
	public static String checkCharTotal(Object value,String isControlTotal,String charTotal,String charTotalStart){
		String result = "";
		if(SysUtility.isEmpty(isControlTotal) || "N".equalsIgnoreCase(isControlTotal))
			return result;
		String checkValue = "";
		if(SysUtility.isNotEmpty(value)){
			checkValue = value.toString();
		}
		long icharTotal = Long.valueOf(charTotal).longValue();
		long icharTotalStart = Long.valueOf(charTotalStart).longValue();
		String split = getSplitString(checkValue);
		long valueCharTotal = SysUtility.getStrLength(checkValue.replaceAll(split,""));
		if(valueCharTotal > icharTotal || valueCharTotal < icharTotalStart){
			result = "此字段只能输入从" + charTotalStart + "到" + charTotal + "个字符，现在已输入" + valueCharTotal + "个字符；";	
		}
		return result;
	}
	
	/**
	 * 是否可以录入中文、数字
	 * @return
	 */
	public static String checkIsInputChinese(Object value,String isChinese){
		String result = "";
		if(SysUtility.isEmpty(value))
			return result;
		
		String checkValue = (String) value;
		if(SysUtility.isEmpty(isChinese) || checkValue.length() < 1)
			return result;
		//英文
		if(isChinese.equalsIgnoreCase("2") && isChinese(checkValue)){
	        result = "此字段不能录入中文；";
		}else if(isChinese.equalsIgnoreCase("3")){
			String pattern="^[0-9]+$";  
	        Pattern p=Pattern.compile(pattern);  
	        Matcher regex=p.matcher(checkValue); 
	        if(!regex.find()){
	        	result = "此字段只能输入数字；";
	        }
		}
		return result;
	}
	
	/**
	 * 检验是否是必输的
	 * @return
	 */
	public static String checkIsMustInput(Object value,String isMustInput){
		String result = "";
		if("Y".equalsIgnoreCase(isMustInput) && SysUtility.isEmpty(value)){
			result = "此字段是必输项；";	
		}		
		return result;
	}
	
	/**
	 * 比较子表达
	 * @return
	 */
	private static String compareData(Object leftValue, String rightValue,String operate, String dataType,
			String cselectFlag, String fieldValueCn) {
		if(SysUtility.isEmpty(operate))
			return "";
		
		StringBuffer result = new StringBuffer();
		if (operate.equalsIgnoreCase("^")) {
			boolean checkResult = startWiths(leftValue, rightValue);
			if(!checkResult){			
				result.append("字段的值必须包含在" + rightValue + "中；");			
			}
		} else if (operate.equalsIgnoreCase("in")) {
			boolean checkResult = includeCodition(leftValue, rightValue);
			if(!checkResult){
				if(cselectFlag.equalsIgnoreCase("Y")){
					result.append("字段的值不包含在" + fieldValueCn + "中；");
				}else{
					result.append("字段的值不包含在" + rightValue + "中；");
				}
			}
		} else if (operate.equalsIgnoreCase("notin")) {
			boolean checkResult = notIncludeCodition(leftValue, rightValue);
			if(!checkResult){
				if(cselectFlag.equalsIgnoreCase("Y")){
					result.append("字段的值不能包含在" + fieldValueCn + "中；");
				}else{
					result.append("字段的值不能包含在" + rightValue + "中；");
				}
			}
		} else if (operate.equalsIgnoreCase("like")) {
			boolean checkResult = likeValue(leftValue, rightValue);
			if(!checkResult){
				if(cselectFlag.equalsIgnoreCase("Y")){
					result.append("字段的值不包含在" + fieldValueCn + "中；");
				}else{
					result.append("字段的值不包含在" + rightValue + "中；");
				}
			}
		} else if (operate.equalsIgnoreCase("notlike")) {
			boolean checkResult = notLikeValue(leftValue, rightValue);
			if(!checkResult){
				if(cselectFlag.equalsIgnoreCase("Y")){
					result.append("字段的值不能包含在" + fieldValueCn + "中；");
				}else{
					result.append("字段的值不能包含在" + rightValue + "中；");
				}
			}
		} else {
			if (dataType.equalsIgnoreCase("NUMBER")){// number
				if (operate.equalsIgnoreCase( ">")) {
					boolean checkResult = numberMoreThan(leftValue, rightValue);
					if(!checkResult){
						result.append("字段的值不能小于等于" + rightValue + "的值；");
					}
				} else if (operate.equalsIgnoreCase(  "<")) {
					boolean checkResult = numberLessThan(leftValue, rightValue);
					if(!checkResult){
						result.append("字段的值不能大于等于" + rightValue + "的值；");
					}
				} else if (operate.equalsIgnoreCase( ">=")) {
					boolean checkResult = numberMoreThanEqaul(leftValue,rightValue);
					if(!checkResult){
						result.append("字段的值不能小于" + rightValue + "的值；");
					}
				} else if (operate.equalsIgnoreCase("<=")) {
					boolean checkResult = numberLessThanEqaul(leftValue,rightValue);
					if(!checkResult){
						result.append("字段的值不能大于" + rightValue + "的值；");
					}
				} else {
					boolean checkResult = numberIsEqual(leftValue, rightValue);
					if(!checkResult){
						result.append("字段的值不等于" + rightValue + "的值；");
					}
				}
			} else if (dataType.equalsIgnoreCase("DATE")){
				if (operate.equalsIgnoreCase(">")) {
					boolean checkResult = dateMoreThan(leftValue, rightValue);
					if(!checkResult){
						result.append("字段的值不能小于等于" + rightValue + "的值；");
					}
				} else if (operate.equalsIgnoreCase("<")) {
					boolean checkResult = dateLessThan(leftValue, rightValue);
					if(!checkResult){
						result.append("字段的值不能大于等于" + rightValue + "的值；");
					}
				} else if (operate.equalsIgnoreCase(">=")) {
					boolean checkResult = dateMoreThanEqaul(leftValue,rightValue);
					if(!checkResult){
						result.append("字段的值不能小于" + rightValue + "的值；");
					}
				} else if (operate.equalsIgnoreCase("<=")) {
					boolean checkResult = dateLessThanEqaul(leftValue,rightValue);
					if(!checkResult){
						result.append("字段的值不能大于" + rightValue + "的值；");
					}
				} else {
					boolean checkResult = dateIsEqual(leftValue, rightValue);
					if(!checkResult){
						result.append("字段的值不等于" + rightValue + "的值；");
					}
				}
			} else {
				if (operate.equalsIgnoreCase("!=")) {
					boolean checkResult = stringNotEqual(leftValue, rightValue);
					if(!checkResult){
						if(cselectFlag.equalsIgnoreCase("Y")){
							result.append("字段的值不能等于" + fieldValueCn + "的值；");
						}else{
							result.append("字段的值不能等于" + rightValue + "的值；");
						}
					}
				} else {
					boolean checkResult = stringIsEqual(leftValue, rightValue);
					if(!checkResult) {
						if(cselectFlag.equalsIgnoreCase("Y")) {
							result.append("字段的值不等于" + fieldValueCn + "的值；");
						} else {
							result.append("字段的值不等于" + rightValue + "的值；");
						}
					}
				}
			}
		}
		return result.toString();
	}
	
	private static boolean isChinese(String value){
		if(SysUtility.isEmpty(value) || value.length() < 1)
			return false;
		String pattern="[\u4e00-\u9fa5]+";  
        Pattern p=Pattern.compile(pattern);  
        Matcher regex=p.matcher(value);                  
        return regex.find();
	}
	
	private static String getSplitString(String value){
		if(value == null) 
			value = "";
		String result = "\\r\\n";
		String pattern="\\r\\n";  
        Pattern p=Pattern.compile(pattern);  
        Matcher regex=p.matcher(value);                  
        if(!regex.find()){
        	result = "\\n";
        }		
		return result;
	}
	
	/**
	 * 获取字段值
	 * param: CheckMap:校验的数据
	 * param: strKey：取值的key   
	 * return String
	 * */
	protected static void getNameValue(HashMap CheckMap, String strKey,String tableName,Object result)throws LegendException {
		if(strKey == null) return;
		
		Set mapSet = CheckMap.entrySet();
        for (Iterator it = mapSet.iterator(); it.hasNext();) {
            Entry entry =  (Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof List){
            	List list = (List) value;
    			for(int i = 0 ; i < list.size() ; i++){
    				HashMap temp = (HashMap) list.get(i);
    				if(key.equals(tableName) || key.equals(tableName+"Information")){
    					getCoulmnValue(temp, strKey, tableName, result);
    				}else{
    					Set set2 = temp.entrySet();
    					for (Iterator it2 = set2.iterator(); it2.hasNext();) {
    			            Entry entry2 =  (Entry)it2.next();
    			            Object key2 = entry2.getKey();
    			            Object value2 = entry2.getValue();
    			            if(key2.equals(tableName)){
    			            	if(value2 instanceof List){
    			            		List list2 = (List) value2;
    			            		for(int j = 0 ; j < list2.size() ; j++){
    			            			HashMap temp2 = (HashMap) list2.get(i);
    			            			if(key2.toString().endsWith("Head") || key2.toString().endsWith("Information")){
    			            				getCoulmnValue(temp2, strKey, tableName, result);
    			            			}else{
    			            				getNameValue(temp2, strKey,tableName,result);
    			            			}
    			            		}
    			            	}
    			            }
    					}
    				}
    			}
            }
        }
	}
	
	
	protected static void getCoulmnValue(HashMap CheckMap, String strKey,String tableName,Object result)
			throws LegendException {
		if(strKey == null) return;
		
		String value = "";
		Set mapSet = CheckMap.entrySet();
        for (Iterator it = mapSet.iterator(); it.hasNext();) {
            Entry entry =  (Entry)it.next();
            Object key = entry.getKey();
            if(strKey.equals(key)){
            	value = entry.getValue().toString();
            }
        }
        
        if(result instanceof List) {
    		if("".equals(value)){
    			value = null;
    		}
    		((List)result).add(value);
    	}
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
	
	
	static final String DELIMITER = ";"; // 分隔符

	public static boolean stringNotEqual(Object left, String right) {
		if (left == null || right == null)
			return false;
		String leftValue = (String) left;
		return !leftValue.equals(right);
	}

	public static boolean stringIsEqual(Object left, String right) {
		if (left == null || right == null)
			return false;
		String leftValue = (String) left;
		return leftValue.equals(right);
	}

	public static boolean numberLessThanEqaul(Object left,
			String right) {
		if (left == null || right == null)
			return false;
		String checkValue = (String) left;
		if (checkValue.length() < 1)
			return false;
		String replaceRight = right.replaceAll("'", "");
		if (!isNumber(replaceRight) || !isNumber(checkValue))
			return false;

		return Double.parseDouble(replaceRight) <= Double
				.parseDouble(checkValue);
	}

	public static boolean numberMoreThanEqaul(Object left, String right) {
		if (left == null || right == null)
			return false;
		String checkValue = (String) left;
		if (checkValue.length() < 1)
			return false;
		String replaceRight = right.replaceAll("'", "");
		if (!isNumber(replaceRight) || !isNumber(checkValue))
			return false;

		return Double.parseDouble(replaceRight) >= Double
				.parseDouble(checkValue);
	}

	public static boolean numberLessThan(Object left, String right) {
		if (left == null || right == null)
			return false;
		String checkValue = (String) left;
		if (checkValue.length() < 1)
			return false;
		String replaceRight = right.replaceAll("'", "");
		if (!isNumber(replaceRight) || !isNumber(checkValue))
			return false;

		return Double.parseDouble(replaceRight) < Double
				.parseDouble(checkValue);
	}

	public static boolean numberMoreThan(Object left, String right) {
		if (left == null || right == null)
			return false;
		String checkValue = (String) left;
		if (checkValue.length() < 1)
			return false;
		String replaceRight = right.replaceAll("'", "");
		if (!isNumber(replaceRight) || !isNumber(checkValue))
			return false;

		return Double.parseDouble(replaceRight) > Double
				.parseDouble(checkValue);
	}

	public static boolean numberIsEqual(Object left, String right) {
		if (left == null || right == null)
			return false;
		String checkValue = (String) left;
		if (checkValue.length() < 1)
			return false;
		String replaceRight = right.replaceAll("'", "");
		if (!isNumber(replaceRight) || !isNumber(checkValue))
			return false;

		return Double.parseDouble(replaceRight) == Double
				.parseDouble(checkValue);
	}

	public static boolean dateNotEqual(Object leftValue, String rightValue) {
		boolean result = false;

		if (leftValue == null || rightValue == null)
			return false;

		String checkValue = (String) leftValue;
		if (checkValue.length() < 1 || rightValue.length() < 1)
			return false;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date left = dateFormat.parse((String) checkValue);
			Date right = dateFormat.parse((String) rightValue);
			result = left.compareTo(right) != 0;
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean dateIsEqual(Object leftValue, String rightValue) {
		boolean result = false;

		if (leftValue == null || rightValue == null)
			return false;

		String checkValue = (String) leftValue;
		if (checkValue.length() < 1 || rightValue.length() < 1)
			return false;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date left = dateFormat.parse((String) checkValue);
			Date right = dateFormat.parse((String) rightValue);
			result = left.compareTo(right) == 0;
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean dateLessThan(Object leftValue, String rightValue) {
		boolean result = false;

		if (leftValue == null || rightValue == null)
			return false;

		String checkValue = (String) leftValue;
		if (checkValue.length() < 1 || rightValue.length() < 1)
			return false;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date left = dateFormat.parse((String) checkValue);
			Date right = dateFormat.parse((String) rightValue);
			result = left.compareTo(right) < 0;
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean dateMoreThan(Object leftValue, String rightValue) {
		boolean result = false;

		if (leftValue == null || rightValue == null)
			return false;

		String checkValue = (String) leftValue;
		if (checkValue.length() < 1 || rightValue.length() < 1)
			return false;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date left = dateFormat.parse((String) checkValue);
			Date right = dateFormat.parse((String) rightValue);
			result = left.compareTo(right) > 0;
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean dateLessThanEqaul(Object leftValue, String rightValue) {
		boolean result = false;

		if (leftValue == null || rightValue == null)
			return false;

		String checkValue = (String) leftValue;
		if (checkValue.length() < 1 || rightValue.length() < 1)
			return false;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date left = dateFormat.parse((String) checkValue);
			Date right = dateFormat.parse((String) rightValue);
			result = left.compareTo(right) <= 0;
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean dateMoreThanEqaul(Object leftValue, String rightValue) {
		boolean result = false;

		if (leftValue == null || rightValue == null)
			return false;

		String checkValue = (String) leftValue;
		if (checkValue.length() < 1 || rightValue.length() < 1)
			return false;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date left = dateFormat.parse((String) checkValue);
			Date right = dateFormat.parse((String) rightValue);
			result = left.compareTo(right) >= 0;
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean startWiths(Object leftValue, String rightValue) {
		boolean result = false;
		if (leftValue == null || rightValue == null)
			return false;
		String checkValue = (String) leftValue;
		if (checkValue.length() < 1)
			return false;

		String[] rightArray = rightValue.split(DELIMITER);
		for (int i = 0; i < rightArray.length; i++) {
			int length = rightArray[i].length();
			if (checkValue.length() >= length) {
				result = checkValue.substring(0, length).equals(rightArray[i]);
			}
			if (result)
				break;
		}
		return result;
	}

	public static boolean notIncludeCodition(Object leftValue, String rightValue) {
		if (leftValue == null || rightValue == null)
			return false;
		String checkValue = (String) leftValue;
		if (checkValue.length() < 1)
			return false;
		return !includeCodition(leftValue, rightValue);
	}

	public static boolean includeCodition(Object left, String right) {
		boolean result = false;
		if (left == null || right == null)
			return false;

		String[] rightArray = right.split(DELIMITER);
		for (int i = 0; i < rightArray.length; i++) {
			result = stringIsEqual(left, rightArray[i]);
			if (result)
				break;
		}
		return result;
	}
	
	public static boolean likeValue(Object left, String right) {
		boolean result = false;
		if (left == null || right == null)
			return false;
		String leftValue = String.valueOf(left);
		String[] rightArray = right.split(DELIMITER);
		for (int i = 0; i < rightArray.length; i++) {
			result = leftValue.indexOf(rightArray[i]) >= 0;
			if (result)
				break;
		}
		return result;
	}
	
	public static boolean notLikeValue(Object left, String right) {
		boolean result = false;
		if (left == null || right == null)
			return false;
		String leftValue = String.valueOf(left);
		String[] rightArray = right.split(DELIMITER);
		for (int i = 0; i < rightArray.length; i++) {
			result = leftValue.indexOf(rightArray[i]) < 0;
			if (!result)
				break;
		}
		return result;
	}

	public static boolean isNumber(String value) {
		if (value == null || value.length() < 1)
			return false;
		Pattern p = Pattern.compile("^(\\d*\\.)?\\d+$");
		Matcher m = p.matcher(value);
		return m.find();
	}
	
	private static List getFieldFormat(final String pointCode) throws LegendException {
		SysUtility.OutDate5MinuteReset(cacheFieldMap);
		
		List list = cacheFieldMap.get(pointCode);
		if(SysUtility.isNotEmpty(list) || SysUtility.isEmpty(pointCode)){
			return list;
		}
		final String appRecVer = BizConfigFactory.getCfgValue("APP_REC_VER",SysUtility.getCurrentPartId());
		
		SQLBuild sqlBuild = SQLBuild.getInstance();
		sqlBuild.append("select rule_no,");
		sqlBuild.append("       rule_name,");
		sqlBuild.append("       warning_type,");
		sqlBuild.append("       warning_info,");
		sqlBuild.append("       table_name,");
		sqlBuild.append("       field_name,");
		sqlBuild.append("       field_code,");
		sqlBuild.append("       field_data_type,");
		sqlBuild.append("       is_control_row,");
		sqlBuild.append("       row_total,");
		sqlBuild.append("       col_total,");
		sqlBuild.append("       is_control_total,");
		sqlBuild.append("       char_total,");
		sqlBuild.append("       is_chinese,");
		sqlBuild.append("       is_mustinput,");
		sqlBuild.append("       operator,");
		sqlBuild.append("       field_value,");
		sqlBuild.append("       field_value_cn,");
		sqlBuild.append("       char_total_start");
		sqlBuild.append("  from rule_t_field");
		sqlBuild.append(" where is_enabled = '1'");
		sqlBuild.append("point_code", "in", pointCode.split(","));
		if(SysUtility.isNotEmpty(appRecVer)){
			sqlBuild.append("app_rec_ver", "=", appRecVer);
		}
		sqlBuild.append(" order by rule_no");
		list = sqlBuild.query4List();
		cacheFieldMap.put(pointCode, list);
		return list;
	}
}
