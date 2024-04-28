package com.easy.query;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;

/**
 * jdbc-ResultSet 包装器
 * 
 * @author snow
 * 
 */
public class ResultSetWrap implements ResultSetHandler, XmlConverter {

	public static final ResultSetWrap EMPTY = new ResultSetWrap();

	/*
	 * 字段名Map，key表示字段名，value表示字段下标
	 */
	private Map fieldNameMap;

	/*
	 * 数据(二维List)
	 */
	private List rows;

	/*
	 * 当前游标
	 */
	private int currentIndex;

	/*
	 * 是否为分页查询
	 */
	private boolean isPageQuery;

	private int pageNo;

	private int totalCount;
	
	private String resultSetName;

	public String getResultSetName() {
		return resultSetName;
	}

	public void setResultSetName(String resultSetName) {
		this.resultSetName = resultSetName;
	}

	public ResultSetWrap() {
		currentIndex = -1;
		rows = new ArrayList();
	}

	/**
	 * 结果集处理
	 */
	public Object handle(ResultSet rs) throws SQLException, LegendException {
		ResultSetMetaData md = rs.getMetaData();
		// 把jdbc的数据 放入包装器里
		for (; rs.next();) {
			List row = new ArrayList();
			for (int i = 1; i <= md.getColumnCount(); i++) {
				String columnName = md.getColumnName(i);
				if (SysUtility.ROWNUM.equals(columnName)) {
					continue;
				}
				int sqlType = md.getColumnType(i);
				row.add(SysUtility.getString(rs, i, sqlType,0));
			}
			rows.add(row);
		}
		int columnCount = 0;
		// 初始化字段列表Map
		fieldNameMap = new HashMap(md.getColumnCount());
		for (int i = 1; i <= md.getColumnCount(); i++) {
			String columnName = md.getColumnName(i);
			if (SysUtility.ROWNUM.equals(columnName)) {
				continue;
			}
			fieldNameMap.put(columnName, new Integer(columnCount++));
		}
		return this;
	}

	/**
	 * 判断结果集是否包含该列
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String fieldName) {
		return fieldNameMap.containsKey(fieldName);
	}

	/**
	 * 动态增加列
	 * 
	 * @param name
	 *            列名
	 * @param value
	 *            值
	 */
	public void add(String name, String value) {
		List row = (List) rows.get(this.currentIndex);
		if (row == null) {
			return;
		}
		Integer columnIndex = (Integer) fieldNameMap.get(name);
		if (columnIndex == null) {
			columnIndex = new Integer(fieldNameMap.size());
			fieldNameMap.put(name, columnIndex);
		}
		if (columnIndex.intValue() < row.size()) {
			row.set(columnIndex.intValue(), value);
		} else {
			for (int n = row.size(); n < columnIndex.intValue(); n++) {
				row.add(null);
			}
			row.add(value);
		}

	}

	/**
	 * 动态增加列 (多列)
	 * 
	 * @param name
	 *            列名
	 * @param value
	 *            值
	 */
	public void addAll(Map map) {
		if (map == null) {
			return;
		}
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			add(key, value);
		}
	}

	/**
	 * 动态增加行
	 * 
	 * @param name
	 *            列名
	 * @param value
	 *            值
	 */
	public void addRows(ResultSetWrap rsWrap) {
		if (rsWrap == null || rsWrap.isEmpty()) {
			return;
		}

		if (this.fieldNameMap == null || this.fieldNameMap.isEmpty()) {
			this.fieldNameMap = new HashMap(rsWrap.fieldNameMap);
		}

		if (!this.fieldNameMap.keySet().equals(rsWrap.fieldNameMap.keySet())) {
			return;
		}

		int[] index = new int[fieldNameMap.size()];
		for (Iterator it = fieldNameMap.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			Integer value1 = (Integer) entry.getValue();
			Integer value2 = (Integer) rsWrap.fieldNameMap.get(entry.getKey());
			index[value1.intValue()] = value2.intValue();
		}

		for (Iterator it = rsWrap.rows.iterator(); it.hasNext();) {
			List e = (List) it.next();
			List list = new ArrayList(e.size());
			for (int i = 0; i < index.length; i++) {
				if (index[i] < e.size()) {
					list.add(e.get(index[i]));
				} else {
					list.add(null);
				}
			}
			rows.add(list);
		}

	}

	public int size() {
		return rows.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean next() {
		return (++currentIndex) < rows.size();
	}

	public boolean hasNext() {
		return max(currentIndex, 0) < rows.size();
	}

	private int max(int i, int j) {
		return i > j ? i : j;
	}
	
	/**
	 * 移除某行(不适用于带分页的查询;同List,需要外部i--)
	 * @param i
	 * @example
	 * for (int i = 0; i < rsWrap.size(); i++) {
	 *     rsWrap.next();
	 *     if(true){
	 *         rsWrap.removeItem(i);
	 *         i--;//*,抵消FOR中的i++
	 *     }
	 * }
	 */
	public void removeItem(int i){
		if(isPageQuery){
			return;
		}
		if (i >= rows.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.rows.remove(i);
		this.currentIndex = this.currentIndex - 1; 
	}
	
	public ResultSetWrap goTo(int i) {
		if (i >= rows.size()) {
			throw new IndexOutOfBoundsException();
		}
		this.currentIndex = i;
		return this;
	}

	public StringBuffer data2Xml(String entityName) {
		if (SysUtility.isNotEmpty(this.resultSetName)){
			entityName = this.resultSetName;
		}
		StringBuffer buf = new StringBuffer();
		if (isPageQuery) {
			buf.append("<_PAGE_INFOS>");
			buf.append("<PAGE_NO>").append(String.valueOf(pageNo)).append("</PAGE_NO>");
			buf.append("<TTL_COUNT>").append(String.valueOf(totalCount)).append("</TTL_COUNT>");
			buf.append("</_PAGE_INFOS>\n");
		}
		currentIndex = -1;
		String[] fields = null;
		if (hasNext()) {
			fields = createFieldArr();
		}

		while (next()) {
			buf.append("<").append(entityName).append(">");
			List row = (List) rows.get(currentIndex);
			for (int i = 0; i < fields.length; i++) {
				if (i >= row.size()) {
					break;
				}
				String value = (String) row.get(i);
				if (value == null) {
					continue;
				}

				buf.append("<").append(fields[i]).append(">");
				buf.append(SysUtility.parseXMLCData(value));
				buf.append("</").append(fields[i]).append(">\n");
			}
			buf.append("</").append(entityName).append(">\n");
		}
		return buf;
	}
	
	private String[] createFieldArr() {
		String[] fields = new String[fieldNameMap.size()];
		for (Iterator it = fieldNameMap.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			String fieldName = (String) entry.getKey();
			int cIndex = ((Integer) entry.getValue()).intValue();
			fields[cIndex] = fieldName;
		}
		return fields;
	}

	public String get(String key) {
		Integer index = (Integer) fieldNameMap.get(key);
		if (index == null) {
			return null;
		}
		List row = (List) rows.get(this.currentIndex);
		if (row.size() > index.intValue()) {
			return (String) row.get(index.intValue());
		} else {
			return null;
		}
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setPageQuery(boolean isPageQuery) {
		this.isPageQuery = isPageQuery;
	}
}