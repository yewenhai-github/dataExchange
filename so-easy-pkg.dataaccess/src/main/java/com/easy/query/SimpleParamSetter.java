package com.easy.query;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Level;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public final class SimpleParamSetter implements Callback {
	private SqlParamList paramList;

	public SimpleParamSetter(SqlParamList paramList) {
		this.paramList = paramList;
	}

	public void doIn(PreparedStatement ps) throws SQLException {
		if(SysUtility.isEmpty(paramList)){
			return;
		}
		
		for (int i = 0; i < paramList.size(); i++) {
			SqlParameter param = paramList.get(i);
			int sqlType = param.getSqlDataType();
			String value = param.getParamValue();
			if (SysUtility.isEmpty(value) || "null".equals(value)) {
				ps.setNull(i + 1, sqlType);
				continue;
			}
			switch (sqlType) {
			case Types.BINARY:
				ps.setBytes(i + 1, value.getBytes());
				break;
			case Types.BIT:
				ps.setByte(i + 1, Byte.parseByte(value));
				break;
			case Types.BOOLEAN:
				ps.setBoolean(i + 1, Boolean.getBoolean(value));
				break;
			case Types.CHAR:
			case Types.VARCHAR:
				ps.setString(i + 1, value);
				LogUtil.printLog("参数"+(i+1) +"："+value, Level.INFO);
				break;
			case Types.BIGINT:
				ps.setLong(i + 1, Long.parseLong(value));
				break;
			case Types.INTEGER:
				ps.setInt(i + 1, Integer.parseInt(value));
				break;
			case Types.FLOAT:
			case Types.DECIMAL:
				ps.setFloat(i + 1, Float.parseFloat(value));
				break;
			case Types.DOUBLE:
			case Types.NUMERIC:
				ps.setDouble(i + 1, Double.parseDouble(value));
				break;
			case Types.TIMESTAMP:
				if (SysUtility.isNotEmpty(value)) {
					if (value.equals("%SYSTEM_DATE%") || "SYSDATE".equals(value.toUpperCase())) {
						java.util.Date now = new java.util.Date();
						ps.setTimestamp(i + 1, new Timestamp(now.getTime()));
					} else {
						try {
							Timestamp time = null;
							if (value.indexOf("to_date") >= 0 || value.indexOf("TO_DATE") >= 0) {
								String[] str = value.split("'");
								time = new Timestamp(SysUtility.str2Date(str[1]).getTime());
								ps.setTimestamp(i + 1, time);
							} else {
								time = new Timestamp(new SimpleDateFormat("y-M-d H:m:s.S").parse(
										value).getTime());
								ps.setTimestamp(i + 1, time);
							}
						} catch (ParseException e) {
						}
					}
				} else {
					ps.setNull(i + 1, Types.TIMESTAMP);
				}
				break;
			// -
			case Types.DATE:
				if (SysUtility.isNotEmpty(value)) {
					if (value.equals("%SYSTEM_DATE%") || "SYSDATE".equals(value.toUpperCase())) {
						java.util.Date now = new java.util.Date();
						ps.setTimestamp(i + 1, new Timestamp(now.getTime()));
					} else {
						try {
							Timestamp date = null;
							if (value.indexOf("to_date") >= 0 || value.indexOf("TO_DATE") >= 0) {
								String[] str = value.split("'");
								date = new Timestamp(SysUtility.str2Date(str[1]).getTime());
								ps.setTimestamp(i + 1, date);
							} else {
								date = new Timestamp(new SimpleDateFormat("y-M-d H:m:s").parse(
										value).getTime());
								ps.setTimestamp(i + 1, date);
							}
						} catch (ParseException e) {

						}
					}
				} else {
					ps.setNull(i + 1, Types.DATE);
				}
				break;
			case Types.LONGVARCHAR:
			case Types.CLOB:
				ps.setCharacterStream(i + 1, new StringReader(value), value.length());
				break;
			case Types.LONGVARBINARY:
			case Types.BLOB:
				byte[] bytes = value.getBytes();
				ps.setBinaryStream(i + 1, new ByteArrayInputStream(bytes), bytes.length);
				break;
			default:
				ps.setObject(i + 1, value, sqlType);
			}
		}
	}
}