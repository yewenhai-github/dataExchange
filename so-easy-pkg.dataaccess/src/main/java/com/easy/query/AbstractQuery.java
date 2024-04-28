package com.easy.query;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Level;

import com.easy.exception.LegendException;
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
public abstract class AbstractQuery {
	public static final String ROW_NUM = "ROW_NUM__";
	private static final Pattern ORDER_BY = Pattern.compile("\\s*order\\s+by\\s*", Pattern.CASE_INSENSITIVE);
	
	private int page_size = 0;
	private int page_no = 0;
	private int totalCount = 0;
	protected String orderby = null;
	private int fetchSize;
	private SqlParamList sqlParamList = new SqlParamList();
	private Connection m_connection;
	
	public final List query4List() throws LegendException{
		List result = new ArrayList();
		String sql = getQuerySQL();
		if (isPageQuery()) {
			totalCount = executeCountQueryByParams(getPageCountQuerySQL(sql), sqlParamList);
			Map pageInfo = new HashMap();
			pageInfo.put("PAGE_NO", String.valueOf(getPage_no()));
			result.add(pageInfo);

			if (totalCount == 0) {
				pageInfo.put("TTL_COUNT", "0");
				return result;
			} else {
				pageInfo.put("TTL_COUNT", String.valueOf(totalCount));
			}
			sql = getPageQuerySQL(sql, sqlParamList);
		} else {
			sql = appendOrderBy(sql);
		}
		checkConnection();
		result.addAll(SQLExecUtils.query4List(m_connection, sql,new SimpleParamSetter(sqlParamList), fetchSize));
		return result;
	}
	
	private String getPageQuerySQL(String sql, final SqlParamList sqlParams) {
		sql = appendOrderBy(sql);
		StringBuffer buf = new StringBuffer();
		int start = getStartRecordIndex();
		if (hasOrderBy(sql)) {
			buf.append("select * from ( SELECT X_Z_Y__.* ,rownum as ").append(ROW_NUM)
					.append(" FROM (").append(sql).append(") X_Z_Y__) WHERE ").append(
							ROW_NUM).append(" between ? and ? ");
			sqlParams.addSqlParam(Types.INTEGER, String.valueOf(start));
			sqlParams.addSqlParam(Types.INTEGER, String.valueOf(getEndRecordIndex()));
		} else {
			if (start == 1) {
				buf.append("select * FROM (").append(sql).append(") WHERE rownum <= ?");
				sqlParams.addSqlParam(Types.INTEGER, String.valueOf(getEndRecordIndex()));
			} else {
				buf.append("select * from ( SELECT X_Z_Y__.* ,rownum as ").append(
						ROW_NUM).append(" FROM (").append(sql).append(
						") X_Z_Y__ where rownum <= ? ) WHERE ").append(ROW_NUM).append(
						">= ? ");
				sqlParams.addSqlParam(Types.INTEGER, String.valueOf(getEndRecordIndex()));
				sqlParams.addSqlParam(Types.INTEGER, String.valueOf(start));
			}
		}
		return buf.toString();
	}
		
	private int getEndRecordIndex() {
		return page_no * page_size;
	}
	
	private String appendOrderBy(String sql) {
		if (SysUtility.isNotEmpty(orderby)) {
			sql = sql + orderby;
		}
		return sql;
	}
	
	public static boolean hasOrderBy(String sql) {
		if (SysUtility.isEmpty(sql)) {
			return false;
		}
		sql = sql.replaceAll("('.*?')|(\".*?\")", "");
		return ORDER_BY.matcher(sql).find();
	}
	
	private int executeCountQueryByParams(String sql, final SqlParamList sqlParams)  {
		PreparedStatement ps = null;
		ResultSet rs = null;
		LogUtil.printLog(sql + "\n at" + this.getClass().getName(), LogUtil.DEBUG);
		int result = 0;
		try {
			checkConnection();
			ps = m_connection.prepareStatement(sql);
			bindParams(ps, sqlParams);
			long begtime = System.currentTimeMillis();
			rs = ps.executeQuery();
			rs.next();
			LogUtil.printLog("" + (System.currentTimeMillis() - begtime) + "ms",LogUtil.DEBUG);
			result = rs.getInt(1);
		} catch (SQLException e) {
			LogUtil.printLog("\n at AbstractQuery.excuteQuery()", LogUtil.DEBUG, e);
		} finally {
			SysUtility.closeResultSet(rs);
			SysUtility.closeStatement(ps);
		}
		return result;
	}
	
	private static void bindParams(final PreparedStatement pstmt, final SqlParamList sqlParams)
			throws SQLException {
		SqlParameter sqlParam = null;
		for (int columnIndex = 0; columnIndex < sqlParams.size(); columnIndex++) {
			sqlParam = sqlParams.get(columnIndex);
			String value = sqlParam.getParamValue();
			int dataType=sqlParam.getSqlDataType();
			if(dataType==4){
				 if(Long.parseLong(value)>Integer.MAX_VALUE){
					 dataType=2;
				 }
			}
			switch (dataType) {
			// -
			case Types.VARCHAR:
				if (SysUtility.isNotEmpty(value)) {
					pstmt.setString(columnIndex + 1, value);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <String> " + value,LogUtil.DEBUG);
				} else {
					pstmt.setNull(columnIndex + 1, Types.VARCHAR);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <String> null",LogUtil.DEBUG);
				}
				break;
			// -
			case Types.TIMESTAMP:
				if (SysUtility.isNotEmpty(value)) {
					if (value.equals("%SYSTEM_DATE%") || "SYSDATE".equals(value.toUpperCase())) {
						java.util.Date now = new java.util.Date();
						pstmt.setTimestamp(columnIndex + 1, new Timestamp(now.getTime()));
						LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Timestamp> " + now,LogUtil.DEBUG);
					} else {
						try {
							Timestamp time = null;
							if (value.indexOf("to_date") >= 0 || value.indexOf("TO_DATE") >= 0) {
								String[] str = value.split("'");
								time = new Timestamp(SysUtility.str2Date(str[1]).getTime());
								pstmt.setTimestamp(columnIndex + 1, time);
							} else {
								time = new Timestamp(new SimpleDateFormat("y-M-d H:m:s.S").parse(value).getTime());
								pstmt.setTimestamp(columnIndex + 1, time);
							}
							LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Timestamp> "+ time, LogUtil.DEBUG);
						} catch (ParseException e) {
						}
					}
				} else {
					pstmt.setNull(columnIndex + 1, Types.TIMESTAMP);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Timestamp> null",LogUtil.DEBUG);
				}
				break;
			// -
			case Types.DATE:
				if (SysUtility.isNotEmpty(value)) {
					if (value.equals("%SYSTEM_DATE%") || "SYSDATE".equals(value.toUpperCase())) {
						java.util.Date now = new java.util.Date();
						pstmt.setDate(columnIndex + 1, new Date(now.getTime()));
						LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Date> " + now,LogUtil.DEBUG);
					} else {
						try {
							Date date = null;
							if (value.indexOf("to_date") >= 0 || value.indexOf("TO_DATE") >= 0) {
								String[] str = value.split("'");
								date = new Date(SysUtility.str2Date(str[1]).getTime());
								pstmt.setDate(columnIndex + 1, date);
							} else {
								date = new Date(new SimpleDateFormat("y-M-d H:m:s").parse(value).getTime());
								pstmt.setDate(columnIndex + 1, date);
							}
							LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Date> " + date,LogUtil.DEBUG);
						} catch (ParseException e) {
							LogUtil.printLog("bind param :" + e.getMessage(),LogUtil.DEBUG);
						}
					}
				} else {
					pstmt.setNull(columnIndex + 1, Types.DATE);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Date> null",LogUtil.DEBUG);
				}
				break;
			// -
			case Types.CLOB:
			case Types.LONGVARCHAR:
				if (SysUtility.isNotEmpty(value)) {
					pstmt.setCharacterStream(columnIndex + 1, new StringReader(value), value.length());
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Colb> " + value,LogUtil.DEBUG);
				} else {
					pstmt.setNull(columnIndex + 1, Types.LONGVARCHAR);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Colb> null ",LogUtil.DEBUG);
				}
				break;
			// --
			case Types.DECIMAL:
			case Types.FLOAT:
				if (SysUtility.isNotEmpty(value)) {
					pstmt.setFloat(columnIndex + 1, Float.parseFloat(value));
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Float> " + value,LogUtil.DEBUG);
				} else {
					pstmt.setNull(columnIndex + 1, Types.NUMERIC);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Float> null ",LogUtil.DEBUG);
				}
				break;
			// --
			case Types.INTEGER:
				if (SysUtility.isNotEmpty(value)) {
					pstmt.setInt(columnIndex + 1, Integer.parseInt(value));
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Integer> " + value,LogUtil.DEBUG);
				} else {
					pstmt.setNull(columnIndex + 1, Types.INTEGER);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Integer> null ",LogUtil.DEBUG);
				}
				break;
			case Types.NUMERIC:
				if(SysUtility.isNotEmpty(value)){
				pstmt.setLong(columnIndex + 1, Long.parseLong(value));
				LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Long> " + value,LogUtil.DEBUG);
				}else{
					pstmt.setNull(columnIndex + 1, Types.NUMERIC);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <long> null ",LogUtil.DEBUG);
				}
				break;		
			case Types.BIGINT:
				if (SysUtility.isNotEmpty(value)) {
					pstmt.setInt(columnIndex + 1, Integer.parseInt(value));
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Bigint> " + value,LogUtil.DEBUG);
				} else {
					pstmt.setNull(columnIndex + 1, Types.BIGINT);
					LogUtil.printLog("bind param :" + (columnIndex + 1) + " <Bigint> null ",LogUtil.DEBUG);
				}
				break;
			default:
				break;
			}
		}
	}
	
	private String getPageCountQuerySQL(String sql) {
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("SELECT count(1) as totalcount FROM ").append("(").append(sql).append(")");
		return sqlBuff.toString();
	}
		
	protected abstract String getQuerySQL() ;
	
	private final void checkConnection()  {
		try {
			if (m_connection == null || m_connection.isClosed()) {
				m_connection = SysUtility.getCurrentConnection();
			}
		} catch (SQLException e) {
			LogUtil.printLog("error:"+e.getMessage(), Level.ERROR);
		}
	}
	
	private int getStartRecordIndex() {
		return (page_no - 1) * page_size + 1;
	}
	
	private boolean isPageQuery() {
		return getStartRecordIndex() > 0 && page_size > 0;
	}
	
	public int getPage_no() {
		if (totalCount < getStartRecordIndex()) {
			return -1;
		}
		return page_no;
	}
	
}
