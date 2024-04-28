package com.easy.query;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class SqlParamList {

	private LinkedList sqlParamList = new LinkedList();

	public int size() {
		return sqlParamList.size();
	}

	public void clear() {
		sqlParamList.clear();
	}

	public void addSqlParam(SqlParameter sqlParameter) {
		sqlParamList.add(sqlParameter);
	}

	public void addSqlParam(int paramType, String paramValue) {
		sqlParamList.add(new SqlParameter(paramType, paramValue));
	}

	public void addSqlParam(int index, int paramType, String paramValue) {
		sqlParamList.add(index, new SqlParameter(paramType, paramValue));
	}

	public Iterator listIterator() {
		return sqlParamList.listIterator();
	}

	public SqlParameter get(int index) {
		return (SqlParameter) sqlParamList.get(index);
	}

	public LinkedList getInnerList() {
		return new LinkedList(sqlParamList);
	}


	public void addAll(LinkedList sqlParamList) {
		this.sqlParamList.addAll(sqlParamList);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		int i = 1;
		for (Iterator it = sqlParamList.iterator(); it.hasNext();) {
			SqlParameter p = (SqlParameter) it.next();
			buf.append("param index : ").append(i++).append("\tsqlType : ").append(
					p.getSqlDataType()).append("\tvalue : ").append(p.getParamValue()).append("\n");
		}
		return buf.toString();
	}
}
