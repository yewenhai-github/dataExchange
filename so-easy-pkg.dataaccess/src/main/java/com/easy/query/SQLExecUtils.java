package com.easy.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.access.Datas;
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
public class SQLExecUtils {
	private final static int fetchSize = 100;
	private final static int defaultBlobProcess = 0;
	
	public static Callback EMPTY_CALLBACK = new Callback() {
		public void doIn(PreparedStatement ps) throws SQLException {
		}
	};
	
	/** ----- query4JSONString ----- **/
	public static String query4JSONString(String TableName, String SQL) throws LegendException{
		return query4JSONString(SysUtility.getCurrentConnection(), TableName, SQL, EMPTY_CALLBACK, fetchSize);
	}
	
	public static String query4JSONString(String TableName, String SQL, Callback callback) throws LegendException{
		return query4JSONString(SysUtility.getCurrentConnection(), TableName, SQL, callback, fetchSize);
	}

	public static String query4JSONString(String TableName,String SQL, Callback callback, int fetchSize) throws LegendException{
		return query4JSONString(SysUtility.getCurrentConnection(), TableName, SQL, callback, fetchSize);
	}
	
	public static String query4JSONString(Connection conn, String TableName, String SQL, Callback callback) throws LegendException{
		return query4JSONString(conn, TableName, SQL, callback, fetchSize);
	}
	
	public static String query4JSONString(Connection conn, final String TableName, String SQL, 
			Callback callback, int fetchSize)throws LegendException{
		return (String) query(conn, SQL, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException,LegendException {
				return ResToString(TableName, rs).toString();
			}
		}, fetchSize);
	}
	
	/** ----- query4JSONObject ----- **/
	public static JSONObject query4JSONObject(String TableName, String SQL) throws LegendException{
		return query4JSONObject(SysUtility.getCurrentConnection(), TableName, SQL, EMPTY_CALLBACK, fetchSize);
	}
	
	public static JSONObject query4JSONObject(String TableName, String SQL, Callback callback) throws LegendException{
		return query4JSONObject(SysUtility.getCurrentConnection(), TableName, SQL, callback, fetchSize);
	}

	public static JSONObject query4JSONObject(String TableName,String SQL,  Callback callback, int fetchSize)throws LegendException{
		return query4JSONObject(SysUtility.getCurrentConnection(), TableName, SQL, callback, fetchSize);
	}
	
	public static JSONObject query4JSONObject(Connection conn, String TableName, String SQL, Callback callback)throws LegendException{
		return query4JSONObject(conn, TableName, SQL, callback, fetchSize);
	}
	
	public static JSONObject query4JSONObject(Connection conn, final String TableName, String SQL, 
			Callback callback, int fetchSize)throws LegendException{
		return (JSONObject) query(conn, SQL, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException,LegendException {
				return ResToJSON(TableName, rs);
			}
		}, fetchSize);
	}
	
	public static JSONObject query4JSONObject(Connection conn,final String TableName, String SQL, Callback callback,
			int page,int pagesize) throws LegendException{
		if(page < 1){
			page = 1;
		}
		JSONObject datajson = new JSONObject();
		try {
			String nSQL = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (" + SQL + ") A WHERE ROWNUM <= " + (page * pagesize ) + ") WHERE RN >= " +  ( (page-1) * pagesize + 1) ;
			LogUtil.printLog("SQL:" + nSQL, LogUtil.INFO);
			datajson = (JSONObject) query(conn, nSQL, callback, new ResultSetHandler() {
				public Object handle(ResultSet rs) throws SQLException,LegendException {
					return ResToJSON(TableName, rs);
				}
			}, fetchSize);
			String countSQL = "Select Count(0) as ROWCOUNT FROM (" + SQL + ") T";
			LogUtil.printLog("countSQL:" + countSQL, LogUtil.INFO);
			
			int rc = Integer.parseInt(query4String(conn, countSQL, callback));
			datajson.put("total", String.valueOf(rc%pagesize == 0 ? rc/pagesize : rc/pagesize + 1));
			datajson.put("page", String.valueOf(page));
			datajson.put("records", String.valueOf(rc));
		} catch (NumberFormatException e) {
			LogUtil.printLog("query4JSONObject分页查询出错："+e.getMessage(), Level.ERROR);
		} catch (JSONException e) {
			LogUtil.printLog("query4JSONObject分页查询出错："+e.getMessage(), Level.ERROR);
		}
		return datajson;
	}
	
	/** ----- query4Datas ----- **/
	public static Datas query4Datas(String TableName,String SQL)throws LegendException{
		return query4Datas(SysUtility.getCurrentConnection(), TableName, SQL, EMPTY_CALLBACK, fetchSize);
	}
	
	public static Datas query4Datas(String TableName,String SQL, Callback callback)throws LegendException{
		return query4Datas(SysUtility.getCurrentConnection(), TableName, SQL, callback, fetchSize);
	}
	
	public static Datas query4Datas(Connection conn,String TableName,String SQL, Callback callback)throws LegendException{
		return query4Datas(conn, TableName, SQL, callback, fetchSize);
	}
	
	public static Datas query4Datas(Connection conn,final String TableName,String SQL, Callback callback, int fetchSize)throws LegendException{
		return (Datas) query(conn, SQL, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException,LegendException {
				try {
					return new Datas(SysUtility.ResToJSON(TableName, rs).toString());
				} catch (JSONException e) {
					LogUtil.printLog(e.getMessage(), Level.ERROR);
				}
				return new Datas();
			}
		}, fetchSize);
	}
	
	public static Datas query4Datas(Connection conn,final String TableName, String SQL, Callback callback,int page,int pagesize) throws LegendException{
		return query4Datas(conn, TableName, SQL, callback, page, pagesize, false);
	}
	
	public static Datas query4Datas(Connection conn,final String TableName, String SQL, Callback callback,int page,int pagesize,boolean IsEasyUi) throws LegendException{
		return query4Datas(conn, TableName, SQL, callback, page, pagesize, IsEasyUi, 0);
	}
	
	public static Datas query4Datas(Connection conn,final String TableName, String SQL, Callback callback,int page,int pagesize,boolean IsEasyUi,final int BlobProcess) throws LegendException{
		if(page < 1){
			page = 1;
		}
		Datas datas = new Datas();
		try {
			String nSQL = "";
			if(SysUtility.IsMySqlDB()) {
				//nSQL = "SELECT * FROM (SELECT A.*, (@rowNum:=@rowNum+1)  as  RN FROM (" + SQL + ") A ,(Select (@rowNum :=0)) b) res  WHERE RN >= " +  ( (page-1) * pagesize + 1) +" and RN <= "+ (page * pagesize );
				nSQL = SQL + " limit  " + ( (page-1) * pagesize) +", " + pagesize ;
			}else {
				nSQL = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (" + SQL + ") A WHERE ROWNUM <= " + (page * pagesize ) + ") WHERE RN >= " +  ( (page-1) * pagesize + 1) ;
			}
			LogUtil.printLog("SQL:" + nSQL, LogUtil.INFO);
			
			datas = (Datas) query(conn, nSQL, callback, new ResultSetHandler() {
				public Object handle(ResultSet rs) throws SQLException,LegendException {
					try {
						return new Datas(SysUtility.ResToJSON(TableName, rs, BlobProcess).toString());
					} catch (JSONException e) {
						LogUtil.printLog(e.getMessage(), Level.ERROR);
					}
					return new Datas();
				}
			}, fetchSize);
			
			String countSQL = "Select Count(0) as ROWCOUNT FROM (" + SQL + ") T";
			LogUtil.printLog("countSQL:" + countSQL, LogUtil.INFO);
			
			int rc = Integer.parseInt(query4String(conn, countSQL, callback));
			JSONObject datajson = datas.getDataJSON();
			if(IsEasyUi){
				datajson.put("total", String.valueOf(rc));
			}else{
				datajson.put("total", String.valueOf(rc%pagesize == 0 ? rc/pagesize : rc/pagesize + 1));
				datajson.put("page", String.valueOf(page));
				datajson.put("records", String.valueOf(rc));
			}
		} catch (NumberFormatException e) {
			LogUtil.printLog("query4Datas分页查询出错："+e.getMessage(), Level.ERROR);
		} catch (JSONException e) {
			LogUtil.printLog("query4Datas分页查询出错："+e.getMessage(), Level.ERROR);
		}
		return datas;
	}
	/** ----- query4List ----- **/
	public static List query4List(String SQL) throws LegendException{
		return query4List(SQL, EMPTY_CALLBACK);
	}
	
	public static List query4List(String SQL, Callback callback) throws LegendException{
		return query4List(SQL, callback, defaultBlobProcess);
	}

	public static List query4List(String SQL, int blobProcess) throws LegendException{
		return query4List(SQL, EMPTY_CALLBACK, blobProcess);
	}
	
	public static List query4List(Connection conn,String SQL) throws LegendException{
		return query4List(conn, SQL, EMPTY_CALLBACK);
	}
	
	public static List query4List(String SQL,Callback callback, int blobProcess) throws LegendException{
		return query4List(SysUtility.getCurrentConnection(), SQL, callback, blobProcess);
	}
	
	public static List query4List(Connection conn, String SQL, Callback callback)throws LegendException{
		return query4List(conn, SQL, callback, defaultBlobProcess);
	}
	
	public static List query4List(Connection conn, String SQL, Callback callback, final int blobProcess)throws LegendException{
		return (List) query(conn, SQL, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException,LegendException {
				ResultSetMetaData md = rs.getMetaData(); 
				int columnCount = md.getColumnCount();
				List result = new ArrayList();
				while (rs.next()) {
					result.add(SysUtility.createRow(rs, md, columnCount, blobProcess));
				}
				return result;
			}
		}, fetchSize);
	}
	
	/** ----- query4Set ----- **/
	public static Set query4Set(String sql, Callback callback) throws LegendException {
		return query4Set(SysUtility.getCurrentConnection(), sql, callback);
	}
	
	public static Set query4Set(Connection conn, String sql) throws LegendException {
		return query4Set(conn, sql, EMPTY_CALLBACK);
	}

	public static Set query4Set(String sql) throws LegendException {
		return query4Set(SysUtility.getCurrentConnection(), sql, EMPTY_CALLBACK);
	}
	
	public static Set query4Set(Connection conn, String sql, Callback callback)throws LegendException {
		return query4Set(SysUtility.getCurrentConnection(), sql, EMPTY_CALLBACK,0);
	}
	public static Set query4Set(Connection conn, String sql, Callback callback,final int blobProcess)throws LegendException {
		return (Set) query(conn, sql, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException, LegendException {
				Set result = new HashSet();
				while (rs.next()) {
					result.add(SysUtility.getString(rs, 1, rs.getMetaData().getColumnType(1),blobProcess));
				}
				return result;
			}
		},fetchSize);
	}
	/** ----- query4Map ----- **/
	public static Map query4Map(String SQL) throws LegendException{
		return query4Map(SysUtility.getCurrentConnection(), SQL, EMPTY_CALLBACK);
	}
	
	public static Map query4Map(Connection conn, String SQL) throws LegendException{
		return query4Map(conn, SQL, EMPTY_CALLBACK);
	}
	
	public static Map query4Map(String SQL, Callback callback) throws LegendException{
		return query4Map(SysUtility.getCurrentConnection(), SQL, callback);
	}
	
	public static Map query4Map(Connection conn, String SQL, Callback callback) throws LegendException{
		return query4Map(conn, SQL, callback, 0);
	}
	
	public static Map query4Map(Connection conn, String SQL, Callback callback,final int blobProcess) throws LegendException{
		return (Map) query(conn, SQL, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException,LegendException {
				ResultSetMetaData md = rs.getMetaData();
				int columnCount = md.getColumnCount();
				if (rs.next()) {
					return SysUtility.createRow(rs, md, columnCount, blobProcess);
				} else {
					return new HashMap();
				}
			}
		}, fetchSize);
	}
	
	/** ----- query4String ----- **/
	public static String query4String(String SQL) throws LegendException{
		return query4String(SysUtility.getCurrentConnection(), SQL, EMPTY_CALLBACK);
	}
	
	public static String query4String(Connection conn, String SQL) throws LegendException{
		return query4String(conn, SQL, EMPTY_CALLBACK);
	}

	public static String query4String(String SQL, Callback callback) throws LegendException{
		return query4String(SysUtility.getCurrentConnection(), SQL, callback);
	}
	
	public static String query4String(Connection conn, String SQL, Callback callback) throws LegendException{
		return (String) query(conn, SQL, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException, LegendException {
				if (rs.next()) {
					return SysUtility.getString(rs, 1, rs.getMetaData().getColumnType(1),0);
				} else {
					return SysUtility.EMPTY;
				}
			}
		}, fetchSize);
	}
	
	public static List query4StringList(String SQL) throws LegendException{
		return query4StringList(SQL, EMPTY_CALLBACK);
	}
	
	public static List query4StringList(String SQL, Callback callback) throws LegendException{
		return (List) query(SysUtility.getCurrentConnection(), SQL, callback, new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException, LegendException {
				List rt = new ArrayList();
				while (rs.next()){
					rt.add(SysUtility.getString(rs, 1, rs.getMetaData().getColumnType(1),0));
				}
				return rt;
			}
		}, fetchSize);
	}
	/** ----- query4Wrap ----- **/
	public static ResultSetWrap query4Wrap(Connection conn, String sql, Callback callback, int fetchSize) throws LegendException {
		return (ResultSetWrap) query(conn, sql, callback, new ResultSetWrap(), fetchSize);
	}

	public static ResultSetWrap query4Wrap(Connection conn, String sql, Callback callback) throws LegendException {
		return query4Wrap(conn, sql, callback, fetchSize);
	}

	public static ResultSetWrap query4Wrap(String sql, Callback callback) throws LegendException {
		return query4Wrap(SysUtility.getCurrentConnection(), sql, callback, fetchSize);
	}

	public static ResultSetWrap query4Wrap(String sql, Callback callback, int fetchSize) throws LegendException {
		return query4Wrap(SysUtility.getCurrentConnection(), sql, callback, fetchSize);
	}
	
	public static ResultSetWrap query4Wrap(Connection conn, String sql) throws LegendException {
		return query4Wrap(conn, sql, EMPTY_CALLBACK, fetchSize);
	}

	public static ResultSetWrap query4Wrap(String sql) throws LegendException {
		return query4Wrap(SysUtility.getCurrentConnection(), sql, EMPTY_CALLBACK, fetchSize);
	}
	
	/** ----- executeUpdate ----- **/
	public static boolean executeUpdate(String SQL) throws LegendException {
		return executeUpdate(null, SQL, EMPTY_CALLBACK);
	}
	
	public static boolean executeUpdate(String SQL, Callback callback) throws LegendException {
		return executeUpdate(null, SQL, callback);
	}
	
	public static boolean executeUpdate(Connection conn, String SQL, Callback callback) throws LegendException {
		LogUtil.printLog(SQL, LogUtil.DEBUG);
		boolean result = false;
		PreparedStatement ps = null;
		try {
			conn = SysUtility.check(conn);
			if(SysUtility.isEmpty(conn)){
				throw LegendException.getLegendException("数据库连接无法获取");
			}
			ps = conn.prepareStatement(SQL);
			callback.doIn(getProxy(ps));
			ps.executeUpdate();
			result = true;
			LogUtil.printLog(SQL, Level.INFO);
		} catch (SQLException e) {
			LogUtil.printLog("error:"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeStatement(ps);
		}
		return result;
	}
	
	public static boolean executeUpdate(String SQL,Object params) throws LegendException{
		return executeUpdate(null, SQL, params);
	}
	
	public static boolean executeUpdate(Connection conn, String SQL,Object params) throws LegendException{
		LogUtil.printLog(SQL, Level.INFO);
		conn = SysUtility.check(conn);
		if(SysUtility.isEmpty(conn)){
			throw LegendException.getLegendException("数据库连接无法获取");
		}
		boolean result = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SQL);
			SysUtility.bindParams(ps, params);//绑定参数
			ps.executeUpdate();
			result = true;
		} catch (SQLException e) {
			LogUtil.printLog("error:"+e.getMessage(), Level.ERROR);
		} finally {
			SysUtility.closeResultSet(rs);
			SysUtility.closeStatement(ps);
		}
		return result;
	}
	
	public static Object query(Connection conn, String SQL, Callback callback,
							   ResultSetHandler rsHandler, int fetchSize)throws LegendException{
		LogUtil.printLog(SQL, LogUtil.INFO);
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = SysUtility.check(conn);
			if(SysUtility.isEmpty(conn)){
				throw LegendException.getLegendException("数据库连接无法获取");
			}
			ps = conn.prepareStatement(SQL);
			callback.doIn(getProxy(ps));
			long start = System.currentTimeMillis();
			rs = ps.executeQuery();
			rs.setFetchSize(fetchSize);
			LogUtil.printLog("new fetchsize : " + rs.getFetchSize(), LogUtil.INFO);
			Object o = rsHandler.handle(rs);
			LogUtil.printLog("cost time : " + (System.currentTimeMillis() - start) + " ms!",LogUtil.INFO);
			return o;
		} catch (SQLException e) {
			throw LegendException.getLegendException(e);
		} finally {
			SysUtility.closeResultSet(rs);
			SysUtility.closeStatement(ps);
		}
	}
	
	public static JSONObject ResToJSON(String TableName,ResultSet rs)throws LegendException	{
		JSONObject rtobj = new JSONObject();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			JSONArray rows = new JSONArray();
			try {
				while(rs.next()) {
					JSONObject row = new JSONObject();
					for(int c = 1 ; c <= rsmd.getColumnCount() ; c++)
					{
						Object value = rs.getObject(c);
						if(value == null){
							row.put(rsmd.getColumnLabel(c), "");
						}else{
							if(value instanceof Date){
//								row.put(rsmd.getColumnLabel(c), SysUtility.perfectTimestamp(rs.getTimestamp(c)));
								row.put(rsmd.getColumnLabel(c), rs.getString(c));
							}else{
								row.put(rsmd.getColumnLabel(c), rs.getString(c));
							}
						}
					}
					rows.put(row);
				}
				rtobj.put(TableName, rows);
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
			}
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e);
		}
		return rtobj;
	}
	
	public static StringBuilder ResToString(String TableName, ResultSet rs)throws LegendException{
		if(SysUtility.isEmpty(TableName)){
			return ResToString(rs);
		}
		StringBuilder rt = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			if(!TableName.isEmpty())
			  rt.append("{\"" + TableName + "\":[");
			else
				rt.append("[");
			String rlk = "";
			while(rs.next()) {
				String flk = "";
				rt.append(rlk + "{");
				for(int c = 1 ; c <= rsmd.getColumnCount() ; c++)
				{
					rt.append(flk + "\"");
					rt.append(rsmd.getColumnLabel(c));
					rt.append("\":\"");
					String v = rs.getString(c);
					if(v==null)
					{
						v = "";
					}
					rt.append(v.replace("\"", "\\\""));
					rt.append("\"");
					flk = ",";
				}
				rt.append("}");
				rlk = ",";
			}
			if(!TableName.isEmpty())
				  rt.append("]}");
				else
					rt.append("]");
			//rt.append("]}");
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e);
		}
		return rt;
	}
	
	public static StringBuilder ResToString(ResultSet rs)throws LegendException{
		StringBuilder rt = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			rt.append("[");
			String rlk = "";
			while (rs.next()) {
				String flk = "";
				rt.append(rlk + "{");
				for (int c = 1; c <= rsmd.getColumnCount(); c++) {
					rt.append(flk + "\"");
					rt.append(rsmd.getColumnLabel(c));
					rt.append("\":\"");
					String v = rs.getString(c);
					if (v == null) {
						v = "";
					}
					rt.append(v.replace("\"", "\\\""));
					rt.append("\"");
					flk = ",";
				}
				rt.append("}");
				rlk = ",";
			}
			  rt.append("]");
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e);
		}
		return rt;
	}
	
	public static List ResToList(ResultSet rs)throws LegendException{
		List rtlist = new ArrayList();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
				Map row = new HashMap();
				for(int c = 1 ; c <= rsmd.getColumnCount() ; c++){
					row.put(rsmd.getColumnLabel(c), rs.getString(c));
				}
				rtlist.add(row);
			}
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e);
		}
		return rtlist;
	}
	
	public static SimpleParamSetter parmsToSetter(Object parms){
		if(parms == null || "".equals(parms))
			return new SimpleParamSetter(new SqlParamList());
		
		SqlParamList paramList = new SqlParamList();
		
		if(parms instanceof String){
			paramList.addSqlParam(Types.VARCHAR,parms.toString());
		}else if(parms instanceof Integer){
			paramList.addSqlParam(Types.INTEGER,parms.toString());
		}else if(parms instanceof Long){
			paramList.addSqlParam(Types.NUMERIC,parms.toString());
		}else if(parms instanceof String[]){
			String[] strParms = (String[])parms;
			for (int i = 0; i < strParms.length; i++) {
				paramList.addSqlParam(Types.VARCHAR,strParms[i]);
			}
		}else if(parms instanceof Integer[]){
			Integer[] strParms = (Integer[])parms;
			for (int i = 0; i < strParms.length; i++) {
				paramList.addSqlParam(Types.INTEGER,strParms[i].toString());
			}
		}else if(parms instanceof Long[]){
			Long[] strParms = (Long[])parms;
			for (int i = 0; i < strParms.length; i++) {
				paramList.addSqlParam(Types.NUMERIC,strParms[i].toString());
			}
		}else if(parms instanceof Object[]){
			Object[] p = (Object[])parms;
			for(int i = 1; i <= p.length; i++){
				Object o = p[i-1];
				if(o instanceof String){
					paramList.addSqlParam(Types.VARCHAR,o.toString());
				}else if(o instanceof Integer){
					paramList.addSqlParam(Types.INTEGER,o.toString());
				}else if(o instanceof Long){
					paramList.addSqlParam(Types.NUMERIC,o.toString());
				}
			}
		}
		return new SimpleParamSetter(paramList);
	}
	
	public static PreparedStatement getProxy(PreparedStatement ps) {
		return new PreparedStatementHandler(ps).getProxy();
	}
	
	private static class PreparedStatementHandler implements InvocationHandler {
		private PreparedStatement ps;

		PreparedStatementHandler(PreparedStatement ps) {
			this.ps = ps;
		}
		PreparedStatement getProxy() {
			return (PreparedStatement) Proxy.newProxyInstance(ps.getClass().getClassLoader(),
					new Class[] { PreparedStatement.class }, this);
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object o = null;
			try {
				o = method.invoke(ps, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
			String methodName = method.getName();
			if (methodName.startsWith("set") && args.length == 2) {
				Integer index = (Integer) args[0];
				String type = methodName.substring(3);
				LogUtil.printLog("bind : " + index + " <" + type + "> " + args[1], LogUtil.DEBUG);
			}
			return o;
		}
	}
	
}
