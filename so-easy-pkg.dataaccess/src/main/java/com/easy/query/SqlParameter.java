package com.easy.query;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class SqlParameter {
	private int sqlDataType;

	private String paramValue;

	public SqlParameter() {
		super();
	}

	public SqlParameter(int sqlDataType, String paramValue) {
		this.sqlDataType = sqlDataType;
		this.paramValue = paramValue;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public int getSqlDataType() {
		return sqlDataType;
	}

	public void setSqlDataType(int sqlDataType) {
		this.sqlDataType = sqlDataType;
	}

}
