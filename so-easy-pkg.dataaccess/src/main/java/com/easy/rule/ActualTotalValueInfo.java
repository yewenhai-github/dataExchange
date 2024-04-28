package com.easy.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.easy.exception.LegendException;
/**
 * so-easy private
 * 
 * @author yewh 2015-07-7
 * 
 * @version 7.0.0
 * 
 */
public class ActualTotalValueInfo implements IValueInfo{

	public List getValue(HashMap tableMap,String fieldName,String tableName) throws LegendException {
		List leftValue = new ArrayList();
		/***模式：0 表示统计具体的一个值  1：表示统计一共几行数据，如箱量 2:表示统计具体的一个值,但要折半，如货物排除小计的信息 */
		HashMap db_fieldMap = new HashMap();
		db_fieldMap.put("CONSIGNCARGOTOTAL", new String[]{"0","FCCI_QUANTITY"});//货物件数总计
		db_fieldMap.put("CONTAINERTOTAL", new String[]{"1","FCTI_CNT_NO"});//实装总箱量（FCTI_CNT_NO：前台传入的有此字段即可）
		db_fieldMap.put("CONTAINERPREPARETOTAL", new String[]{"2","FCTP_CONSIGN_ID"});//预配总箱量
		
		String model[] = (String[])db_fieldMap.get(fieldName);
		ElecDocsUtil.getNameValue(tableMap,model[1],tableName,leftValue);
		if("0".equals(model[0])){
			int total = 0;
			for(int i = 0 ; i < leftValue.size() ; i++){
				String temp = (String)leftValue.get(i);
				total += Integer.valueOf(temp).intValue();
			}
			leftValue.clear();
			leftValue.add(String.valueOf(total));
		}else if("1".equals(model[0])){
			String size = String.valueOf(leftValue.size());
			leftValue.clear();
			leftValue.add(size);
			return leftValue;
		}else if("2".equals(model[0])){
			int total = 0;
			for(int i = 0 ; i < leftValue.size() ; i++){
				String temp = (String)leftValue.get(i);
				total += Integer.valueOf(temp).intValue();
			}
			BigDecimal bigTotal = new BigDecimal(total);
			double result = bigTotal.divide(new BigDecimal(2),2,BigDecimal.ROUND_HALF_UP).doubleValue();
			leftValue.clear();
			leftValue.add(String.valueOf(result));
		}
		//如果无法取得数据，返回空
		if(ElecDocsUtil.isEmpty(leftValue)){
			leftValue.add("");
			return leftValue;
		}
		return leftValue;
	}

	
}
