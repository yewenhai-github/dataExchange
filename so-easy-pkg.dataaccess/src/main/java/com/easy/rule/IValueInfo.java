package com.easy.rule;
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
public interface IValueInfo {

	/**
	 * 自定义类来取值
	 * @param map
	 * @param fieldName
	 * @return
	 */
	List getValue(HashMap tableMap, String fieldName,String tableName)throws LegendException;		
	
}
