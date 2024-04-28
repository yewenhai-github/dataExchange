package com.easy.query;

public class SQLHolder {
	private String sql;

	private SqlParamList paramList;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public SqlParamList getParamList() {
		return paramList;
	}

	public void setParamList(SqlParamList paramList) {
		this.paramList = paramList;
	}

	public void addParam(int sqlType, String value) {
		paramList.addSqlParam(sqlType, value);
	}
}
