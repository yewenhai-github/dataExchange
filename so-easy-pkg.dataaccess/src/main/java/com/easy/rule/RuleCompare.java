package com.easy.rule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easy.utility.SysUtility;
/**
 * so-easy private
 * 
 * @author yewh 2015-07-7
 * 
 * @version 7.0.0
 * 
 */
public class RuleCompare {
	static final String DELIMITER = ";"; // 分隔符

	/**操作符~HAVE:表达式左右值不能为空：rightValue在leftValue中存在，返回true；否则返回false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean containValue(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null) continue;
			
			String left = (String)obj;
			if(SysUtility.isEmpty(left)) return false;
			
			if(left.indexOf(rightValue) >= 0){
				return true;
			}
		}
		return false;
	}
	
	/**表达式左右值不能为空： rightValue配置2：leftValue只允许输入中文、非法字符 配置3：leftValue只允许输入数字
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean checkInputChinese(List leftValue, String rightValue){
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			String checkValue = (String)leftValue.get(i);
			if (SysUtility.isEmpty(checkValue))
				checkValue = "";
			// 英文
			if ("2".equalsIgnoreCase(rightValue)) {
				for(int j = 0 ; j < checkValue.length() ; j++){
					int temp = checkValue.charAt(j);
					if(temp < 10 
							|| temp > 10 && temp < 13
							|| temp > 13 && temp < 32
							|| temp > 126)
						return false;
				}
			}//数字 
			else if ("3".equalsIgnoreCase(rightValue)) {
				String pattern = "^[0-9]+$";
				Pattern p = Pattern.compile(pattern);
				Matcher regex = p.matcher(checkValue);
				if (!regex.find()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/** leftValue只能在rightValue的配置范围内：支持几行几列,根据换行符判断列
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean checkRowColumnTotal(List leftValue, String rightValue){
		if(SysUtility.isEmpty(rightValue)) return false;
		String[] rowColValues = rightValue.split(DELIMITER);
		if(rowColValues == null || rowColValues.length != 2) return false;
		
		int rowTotal = isNumber(rowColValues[0])? Integer.parseInt( rowColValues[0]) : Integer.MAX_VALUE;
		int colTotal = isNumber(rowColValues[1])? Integer.parseInt( rowColValues[1]) : Integer.MAX_VALUE;
		if(!isNumber(String.valueOf(rowTotal)) || !isNumber(String.valueOf(colTotal))){
			return false;
		}
		for(int i = 0;i < leftValue.size();i++){
			String checkValue = (String)leftValue.get(i);
			if(SysUtility.isEmpty(checkValue))
				checkValue = "";
			String split = getSplitString(checkValue);
			String [] rowArray = checkValue.split(split);
			if(rowArray.length > rowTotal){
				return false;
			}
			for(int j = 0; j < rowArray.length;j++){
				if(rowArray[j].length() > colTotal){
					return false;
				}
			}
		}		
		return true;
	}
	
	/**
	 * 替换tab键和换行符
	 * */
	private static String getSplitString(String value){
		if(value == null) value = "";
		String result = "\\r\\n";
		
		String pattern = "\\r\\n";  
        Pattern p = Pattern.compile(pattern);  
        Matcher regex = p.matcher(value);                  
        if(!regex.find()){
        	result = "\\n";
        }
		return result;
	}
	
	/**
	 * 判断是否是数字
	 * */
	public static boolean isNumber(String value) {
		if (value == null || value.length() < 1) return false;
		Pattern p = Pattern.compile("^(\\d*\\.)?\\d+$");
		Matcher m = p.matcher(value);
		return m.find();
	}
	
	/**leftValue长度大于rightValue长度，返回false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean lessEqualLength(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				continue;
			String left = (String)obj;
			if(!isNumber(rightValue)) return false;
			
			if(left.length() > Integer.valueOf(rightValue).intValue())
			   return false;
		}
		return true;
	}
	
	/**leftValue长度大于等于rightValue长度，返回false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean lessLength(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				continue;
			String left = (String)obj;
			if(!isNumber(rightValue)) return false;
			
			if(left.length() >= Integer.valueOf(rightValue).intValue())
			   return false;
		}
		return true;
	}
	
	/**leftValue长度不等于rightValue长度，返回false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean EqualLength(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				continue;
			String left = (String)obj;
			if(!isNumber(rightValue)) return false;
			
			if(left.length() != Integer.valueOf(rightValue).intValue())
			   return false;
		}
		return true;
	}
	
	/**leftValue长度小于rightValue长度，返回false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean moreEqualLength(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				continue;
			String left = (String)obj;
			if(!isNumber(rightValue)) return false;
			
			if(left.length() < Integer.valueOf(rightValue).intValue())
			   return false;
		}
		return true;
	}
	
	/**leftValue长度小于等于rightValue长度，返回false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean moreLength(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				continue;
			String left = (String)obj;
			if(!isNumber(rightValue)) return false;
			
			if(left.length() <= Integer.valueOf(rightValue).intValue())
			   return false;
		}
		return true;
	}
	
	/**leftValue在rightValue配置值中，返回true
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean includeCodition(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "";
			String left = (String)obj;
			String[] rightArray = rightValue.split(DELIMITER);
			for (int j = 0; j < rightArray.length; j++) {
				if(left.equals(rightArray[j]))
					return true;
			}
		}
		return false;
	}			
	
	/**leftValue在rightValue配置值中，返回true
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean includeCodition(List leftValue, List rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "";
			String left = (String)obj;
			String[] rightArray=new String[rightValue.size()];
			for(int x=0,y=rightValue.size();x<y;x++){
				rightArray[x]=(String) rightValue.get(x);
			}
			for (int j = 0; j < rightArray.length; j++) {
				if(left.equals(rightArray[j]))
					return true;
			}
		}
		return false;
	}	
	
	/**leftValue包含rightValue配置的值，返回true，否则false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean likeValue(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "";
			String left = (String)obj;			
			String[] rightArray = rightValue.split(DELIMITER);
			for (int j = 0; j < rightArray.length; j++) {
				if(left.indexOf(rightArray[j]) >= 0 )
					return true;
			}
		}
		return false;
	}
	
	/**leftValue不能包含rightValue配置的值，返回true，否则false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean notIncludeCodition(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "";
			String left = (String)obj;			
			String[] rightArray = rightValue.split(DELIMITER);
			for (int j = 0; j < rightArray.length; j++) {
				if(left.equals((rightArray[j])))
					return false;
			}
		}
		return true;
	}
	
	/**leftValue不能包含rightValue配置的值，返回true，否则false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean notIncludeCodition(List leftValue, List rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "";
			String left = (String)obj;
			String[] rightArray=new String[rightValue.size()];
			for(int x=0,y=rightValue.size();x<y;x++){
				rightArray[x]=(String) rightValue.get(x);
			}
			for (int j = 0; j < rightArray.length; j++) {
				if(left.equals(rightArray[j]))
					return false;
			}
		}
		return true;
	}
		
	
	/**leftValue不能包含rightValue配置的值，返回true，否则false
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean notLikeValue(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "";
			String left = (String)obj;
			String[] rightArray = rightValue.split(DELIMITER);
			for (int j = 0; j < rightArray.length; j++) {
				if(left.indexOf(rightArray[j]) >= 0 )
					return false;
			}
		}
		return true;
	}
	
	/***
	 * 待扩展 
	 * */
	public static boolean startWiths(List leftValue, String rightValue) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "";
			String left = (String)obj;
			if(!isNumber(rightValue)) return false;
			
			String[] rightArray = rightValue.split(DELIMITER);
			for (int j = 0; j < rightArray.length; j++) {
				
			}
		}
		return true;
	}
	
	/**数字：leftValue与rightValue比对
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean compareNumber(List leftValue, String rightValue,String operate) {
		if(SysUtility.isEmpty(rightValue)) rightValue = "0";
		
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null)
				obj = "0";
			String left = (String)obj;
			if(!isNumber(rightValue)) return false;
			
			double leftvalue = Double.parseDouble(left);
			double right = Double.parseDouble(rightValue);
			
			if (operate.trim().equals(">") && !(leftvalue > right)) {
				  return false;
			} else if (operate.trim().equals("<") && !(leftvalue < right)) {
				return false;
			} else if (operate.trim().equals(">=") && !(leftvalue >= right)) {
				return false;
			} else if (operate.trim().equals("<=") && !(leftvalue <= right)) {
				return false;
			} else if (operate.trim().equals("==") && !(leftvalue == right)) {
				return false;
			} else if (operate.trim().equals("!=") && !(leftvalue != right)) {
				return false;
			} 
		}
		return true;
	}
	
	/**时间：leftValue与rightValue比对
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * @param dataType 待扩展
	 * **/
	public static boolean compareDate(List leftValue, String rightValue,String operate) {
		if(SysUtility.isEmpty(rightValue)) return false;
		
		if ("SYSDATE".equals(rightValue)) {//TD39063
			rightValue=SysUtility.getSysDate();
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null) 
				return false;
			Date leftvalue = null;
			try {
				if (obj instanceof Date) {
					leftvalue = (Date) obj;
				} else {
					String left = (String)obj;
					if (SysUtility.isEmpty(left)) return false;
					leftvalue = dateFormat.parse(left);
				}
				if (operate.trim().equals(">") && !(leftvalue.compareTo(dateFormat.parse(rightValue)) > 0)) {
					return false;
				} else if (operate.trim().equals("<") && !(leftvalue.compareTo(dateFormat.parse(rightValue)) < 0)) {
					return false;
				} else if (operate.trim().equals(">=") && !(leftvalue.compareTo(dateFormat.parse(rightValue)) >= 0)) {
					return false;
				} else if (operate.trim().equals("<=") && !(leftvalue.compareTo(dateFormat.parse(rightValue)) <= 0)) {
					return false;
				} else if (operate.trim().equals("==") && !(leftvalue.compareTo(dateFormat.parse(rightValue)) == 0)) {
					return false;
				} else if (operate.trim().equals("!=") && !(leftvalue.compareTo(dateFormat.parse(rightValue)) != 0)) {
					return false;
				}
			} catch (Exception ex) {
				
			}
		}
		return true;
	}
	
	/**字符串(包含了数字处理)：leftValue与rightValue比对
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * **/
	public static boolean compareString(String leftCompareType,List leftValue, String rightValue,String operate) {
		boolean rt = false;
		operate = operate.trim();
		if(SysUtility.isEmpty(rightValue)){
			rightValue = "";
		}
		if("1".equals(leftCompareType) || SysUtility.isEmpty(leftCompareType)){//单项比对
			for (int i = 0; i < leftValue.size(); i++) {
				Object obj = leftValue.get(i);
				String left = SysUtility.isEmpty(obj)?"":(String)obj;
				rt = compareLeftRight(left, rightValue, operate);
				if(rt == true){
					break;
				}
			}
		}else if("2".equals(leftCompareType)){//整单比对
			for (int i = 0; i < leftValue.size(); i++) {
				Object obj = leftValue.get(i);
				String left = SysUtility.isEmpty(obj)?"":(String)obj;
				rt = compareLeftRight(left, rightValue, operate);
				if(rt == false){
					break;
				}
			}
		}else if("3".equals(leftCompareType)){//整单求和
			double leftTotalVal = 0;
			for (int i = 0; i < leftValue.size(); i++) {
				Object obj = leftValue.get(i);
				String left = SysUtility.isEmpty(obj)?"":(String)obj;
				if(isNumeric(left) && !".".equals(left)){
					double leftVal = Double.valueOf(left).doubleValue();
					leftTotalVal += leftVal;
				}
			}
			rt = compareLeftRight(String.valueOf(leftTotalVal), rightValue, operate);
		}else if("4".equals(leftCompareType)){//整单平均
			double leftTotalVal = 0;
			for (int i = 0; i < leftValue.size(); i++) {
				Object obj = leftValue.get(i);
				String left = SysUtility.isEmpty(obj)?"":(String)obj;
				if(isNumeric(left) && !".".equals(left)){
					double leftVal = Double.valueOf(left).doubleValue();
					leftTotalVal += leftVal;
				}
			}
			rt = compareLeftRight(String.valueOf(leftTotalVal/leftValue.size()+leftTotalVal%leftValue.size()), rightValue, operate);
		}
		return rt;
	}
	
	public static boolean compareLeftRight(String left,String rightValue,String operate){
		boolean rt = false;
		if(isNumeric(left) && isNumeric(rightValue) && !".".equals(left)){
			if(SysUtility.isEmpty(left))  left = "0";
			if(SysUtility.isEmpty(rightValue))  rightValue = "0";
			double leftVal = Double.valueOf(left).doubleValue();
			double rightVal = Double.valueOf(rightValue).doubleValue();
			if (operate.equalsIgnoreCase(">") && (leftVal > rightVal)) {
				rt = true;
			}else if (operate.equalsIgnoreCase(">=") && (leftVal >= rightVal)) {
				rt = true;
			}else if (operate.equalsIgnoreCase("<") && (leftVal < rightVal)) {
				rt = true;
			}else if (operate.equalsIgnoreCase("<=") && (leftVal <= rightVal)) {
				rt = true;
			}if (operate.equalsIgnoreCase("!=") && (leftVal != rightVal)) {
				rt = true;
			} else if (operate.equalsIgnoreCase("==") && (leftVal == rightVal)) {
				rt = true;
			}
		}else{
			if (operate.equalsIgnoreCase(">") && (left.compareTo(rightValue) > 0)) {
				rt = true;
			}else if (operate.equalsIgnoreCase(">=") && (left.compareTo(rightValue) >= 0)) {
				rt = true;
			}if (operate.equalsIgnoreCase("<") && (left.compareTo(rightValue) < 0)) {
				rt = true;
			}if (operate.equalsIgnoreCase("<=") && (left.compareTo(rightValue) <= 0)) {
				rt = true;
			}else if (operate.equalsIgnoreCase("!=") && (left.compareTo(rightValue) != 0)) {
				rt = true;
			} else if (operate.equalsIgnoreCase("==") && (left.compareTo(rightValue) == 0)) {
				rt = true;
			}
		}
		return rt;
	}
	
	
	/**字符串(包含了数字处理)：leftValue与rightValue比对
	 * @param leftValue
	 * @param rightValue
	 * @param operate
	 * **/
	public static boolean BelongString(List leftValue, List rightValues) {
		if(SysUtility.isEmpty(rightValues)){
			rightValues = new ArrayList();
		}
		for (int i = 0; i < leftValue.size(); i++) {
			Object obj = leftValue.get(i);
			if(obj == null) 
				obj = "";
			if(rightValues.contains(obj)){
				return true;
			}
		}
		return false;
	}
	
	/**判断数字
	 * @param str
	 * **/
	public static boolean isNumeric(String str){       
	    Pattern pattern = Pattern.compile("[0-9.]*");       
	    return pattern.matcher(str).matches();          
	}
	
}