package com.easy.access;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.exception.ERR;
import com.easy.exception.LegendException;
import com.easy.query.SQLExecUtils;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

import oracle.jdbc.OracleTypes;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public abstract class AbstractDataAccess implements IDataAccess{
	public String CurrentKey;//全局变量，单例模式下才能使用此值
	public JSONObject CurrentUser;//全局变量，单例模式下才能使用此值
	public String DBName ;//全局变量，单例模式下才能使用此值
	public boolean LogCopHistoryFlag = false;//全局变量，单例模式下才能使用此值
	
	@Override
	public Connection GetActiveCN() throws LegendException{
		if(SysUtility.isEmpty(DBName)){
			return SysUtility.getCurrentConnection();
		}
		else{
			return SysUtility.getCurrentConnection(DBName);
		}
	}
	
	@Override
	public boolean BeginTrans() throws LegendException{
		if(GetActiveCN() != null){
			try {
				GetActiveCN().setAutoCommit(false);
				return true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return false;
	}

	@Override
	public boolean ComitTrans() throws LegendException{
		if(GetActiveCN() != null){
			try {
				GetActiveCN().commit();
				GetActiveCN().setAutoCommit(false);
				return true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return false;
	}

	@Override
	public boolean RoolbackTrans() throws LegendException{
		if(GetActiveCN() != null){
			try {
				GetActiveCN().rollback();
				return true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return false;
	}

	/*********************Begin*******************/
	@Override
	public String GetTable(Connection ActiveCN,String TableName, String SQL, Object parms) throws LegendException{
		Datas data = GetTableDatas(ActiveCN,TableName, SQL, parms);
		return data.getData();
	}
	@Override
	public Datas GetTableDatas(Connection ActiveCN,String TableName, String SQL, Object parms) throws LegendException{
		return ExecQuerySQL(ActiveCN, TableName, SQL, parms);
	}
	/*********************End*******************/
	
	@Override
	public String GetTable(String SQL) throws LegendException{
		Datas datas = GetTableDatas("rows", SQL);
		String rt = "";
		try {
			rt = datas.getDataJSON().getJSONArray("rows").toString();
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rt;
	}
	
	@Override
	public String GetTable(String TableName, String SQL) throws LegendException{
		return GetTable(TableName, SQL, null);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms) throws LegendException{
		Datas data = GetTableDatas(TableName, SQL, parms);
		return data.getData();
	}
	
	@Override
	public String GetTable(String SQL, int page, int pagesize) throws LegendException{
		Datas datas = GetTableDatas("rows",SQL,page,pagesize);
		String rt = "";
		try {
			rt = datas.getJSONObject("rows").toString();
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rt;
	}

	@Override
	public String GetTable(String TableName, String SQL, int page, int pagesize) throws LegendException{
		return GetTable(TableName, SQL, null, page, pagesize);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms,int page, int pagesize) throws LegendException{
		return GetTable(TableName, SQL, parms, page, pagesize, SysUtility.KeyFieldDefault);
	}
	
	@Override
	public String GetTable(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return GetTable(TableName, SQL, null, page, pagesize, KeyField);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		Datas data = GetTableDatas(TableName, SQL, parms, page, pagesize, KeyField);
		return data.getData();
	}
		
	@Override
	public JSONObject GetTableJSON(String TableName, String SQL) throws LegendException{
		return GetTableJSON(TableName, SQL, null);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, Object parms) throws LegendException{
		Datas data = GetTableDatas(TableName, SQL, parms);
		return data.getDataJSON();
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, int page,int pagesize) throws LegendException{
		return GetTableJSON(TableName, SQL, null, page, pagesize);
	}
	
	@Override
	public JSONObject GetTableJSON(String TableName, String SQL,Object parms, int page, int pagesize) throws LegendException{
		return GetTableJSON(TableName, SQL, parms, page, pagesize, SysUtility.KeyFieldDefault);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return GetTableJSON(TableName, SQL, null, page, pagesize, KeyField);
	}
	
	@Override
	public JSONObject GetTableJSON(String TableName, String SQL,Object parms, int page, int pagesize, String KeyField) throws LegendException{
		Datas data = GetTableDatas(TableName, SQL, parms, page, pagesize, KeyField);
		return data.getDataJSON();
	}
	
	@Override
	public JSONObject GetTableJSONUI(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return GetTableJSONUI(TableName, SQL, null, page, pagesize, KeyField);
	}
	
	@Override
	public JSONObject GetTableJSONUI(String TableName, String SQL,Object parms, int page, int pagesize, String KeyField) throws LegendException{
		Datas data = GetTableDatasUI(TableName, SQL, parms, page, pagesize, KeyField);
		return data.getDataJSON();
	}
	
	@Override
	public JSONObject GetTableJSONUI(String TableName, String SQL, int page, int pagesize, String KeyField,JSONArray footer) throws LegendException{
		Datas data = GetTableDatasUI(TableName, SQL, null, page, pagesize, KeyField,footer);
		return data.getDataJSON();
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL) throws LegendException{
		return GetTableDatas(TableName, SQL, null);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, Object parms) throws LegendException{
		return GetTableDatas(GetActiveCN(), TableName, SQL, parms);
	}
	
	@Override
	public Datas GetTableDatas(String TableName,String SQL,int page,int pagesize) throws LegendException{
		return GetTableDatas(TableName, SQL, null, page, pagesize);
	}
	
	@Override
	public Datas GetTableDatas(String TableName,String SQL,Object parms,int page,int pagesize) throws LegendException{
		return GetTableDatas(TableName, SQL, parms, page, pagesize, SysUtility.KeyFieldDefault);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return GetTableDatas(TableName, SQL, null, page, pagesize, KeyField);
	}

	@Override //默认为Oracle实现
	public Datas GetTableDatas(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		if(page < 1){
			page = 1;
		}
		String nSQL = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (" + SQL + ") A WHERE ROWNUM <= " + ( page * pagesize ) + ") WHERE RN >= " +  ( (page-1) * pagesize + 1) ;
		Datas data = GetTableDatas(TableName,nSQL,parms);
		JSONObject rt = data.getDataJSON();
		int rc = GetRowCount(SQL,parms);
		try {
//			rt.put("ROWCOUNT", String.valueOf(rc));
			rt.put("total", String.valueOf(rc%pagesize == 0 ? rc/pagesize : rc/pagesize + 1));
			rt.put("page", String.valueOf(page));
			rt.put("records", String.valueOf(rc));
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());	
		}
		return data;
	}
	
	public Datas GetTableDatasUI(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		return GetTableDatasUI(GetActiveCN(), TableName, SQL, parms, page, pagesize, KeyField);
	}
	
	@Override //默认为Oracle实现
	public Datas GetTableDatasUI(Connection conn,String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		String nSQL = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (" + SQL + ") A WHERE ROWNUM <= " + ( (page + 1 ) * pagesize ) + ") WHERE RN >= " +  ( page * pagesize + 1) ;
		Datas data = GetTableDatas(conn,TableName,nSQL,parms);
		JSONObject rt = data.getDataJSON();
		int rc = GetRowCount(conn,SQL,parms);
		try {
			rt.put("total", String.valueOf(rc));
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());	
		}
		return data;
	}
	
	@Override
	public Datas GetTableDatasUI(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField,JSONArray footer) throws LegendException{
		Datas data = GetTableDatasUI(GetActiveCN(), TableName, SQL, parms, page, pagesize, KeyField);
		try {
			data.put("footer", footer);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());	
		}
		return data;
	}
	
	@Override
	public int GetRowCount(String SQL) throws LegendException{
		return GetRowCount(SQL,null);
	}

	
	public int GetRowCount(String SQL, Object parms) throws LegendException{
		return GetRowCount(GetActiveCN(), SQL, parms);
	}

	@Override //默认为Oracle实现
	public int GetRowCount(Connection conn,String SQL, Object parms) throws LegendException{
		String countSQL = "Select Count(0) as ROWCOUNT FROM (" + SQL + ") T";
		Datas data = GetTableDatas(conn,"R", countSQL, parms);
		String rc = data.GetTableValue("R.ROWCOUNT");
		return Integer.parseInt(rc);
	}
	
	@Override
	public boolean Exists(String SQL) throws LegendException{
		return Exists(SQL, null);
	}
	
	@Override
	public boolean Exists(String SQL, Object parms) throws LegendException{
		String rt = SQLExecUtils.query4String(GetActiveCN(), SQL, SQLExecUtils.parmsToSetter(parms));
		if(SysUtility.isNotEmpty(rt))
			return true;
		return false;
	}
	
	@Override
	public boolean Insert(String TableName, Object objs) throws LegendException{
		return Insert(TableName, objs, SysUtility.KeyFieldDefault);
	}

	@Override
	public boolean Insert(String TableName, Object objs, String KeyField) throws LegendException{
		return Insert(TableName, objs, KeyField, GetActiveCN());
	}
	
	@Override //默认为Oracle实现
	public boolean Insert(String TableName, Object objs, String KeyField,Connection conn) throws LegendException{
		boolean rt = false;
		if(SysUtility.isEmpty(KeyField)){
			KeyField = SysUtility.KeyFieldDefault;
		}
//		String chcheTableName = TableName;
//		try {
//			chcheTableName = conn.getMetaData().getURL()+TableName;
//		} catch (SQLException throwables) {
//			throwables.printStackTrace();
//		}

		List<String> ColumnNames = SysUtility.getTableColumnNames(conn, TableName);
		HashMap<String,Integer> ColumnTypes = SysUtility.getTableColumnTypes(conn, TableName);
		
		JSONArray Datas = new JSONArray();
		if(objs instanceof JSONArray){
			Datas = (JSONArray)objs;
		}else if(objs instanceof JSONObject){
			Datas.put((JSONObject)objs);
		}
		
		for(int i = 0 ; i<Datas.length();i++){
			try {
				JSONObject row = Datas.getJSONObject(i);
				SetInsertDefault(row);
				
				StringBuilder sql = new StringBuilder();
				StringBuilder fields = new StringBuilder();
				ArrayList<Object> parms = new ArrayList<Object>();
				sql.append("Insert Into ");
				sql.append(TableName);
				sql.append("(");
				Iterator<?> keys =row.keys();
				String sp = "";
				while(keys.hasNext()){
					String key = keys.next().toString();
					if(!ColumnNames.contains(key.toUpperCase())){
						continue;//传入列在数据库表中不存在，自动忽略
					}
					if(!row.get(key).equals("")){
						sql.append(sp + key);
						int columnType = ColumnTypes.get(key.toUpperCase());
						Object value = row.get(key);
						if(value.equals("")){
							fields.append(sp + "null");	
						}else{
							if(columnType == Types.TIMESTAMP || columnType == Types.DATE){
								value=value.toString().replaceAll("T"," ");
								if(SysUtility.IsMySqlDB(conn)) {
									fields.append(sp + "str_to_date(?,'%Y-%m-%d %H:%i:%s')");
								}
								else {
									fields.append(sp + "to_date(?,'yyyy-mm-dd hh24:mi:ss')");
								}
							}else{
								fields.append(sp + "?");	
							}
							if(columnType == Types.BLOB){
								if(value instanceof byte[]){
									parms.add((byte[])value);
								}else{//base64字符串
									parms.add(value.toString().getBytes());
								}
							}else{
								parms.add(value.toString());
							}
						}
						sp = ",";
					}
				}
				if(SysUtility.isEmpty(fields) || "".equals(fields.toString())){
					return false;//没有需要插入的值
				}
				StringBuffer KeyValue = new StringBuffer();
				/**增加sequence自动取值逻辑，sequence名称：seq_表名*/
				if(!row.has(KeyField) || SysUtility.isEmpty(row.get(KeyField))){
					int columnType = ColumnTypes.get(KeyField.toUpperCase());
					
					if(SysUtility.IsOracleDB(conn) && columnType != Types.VARCHAR) {
						sql.insert(("Insert Into "+TableName+"(").length(),KeyField+",");
						fields.insert(0, "seq_"+TableName+".nextval,");
					}else if(SysUtility.IsOracleDB(conn) && columnType == Types.VARCHAR) {
						sql.insert(("Insert Into "+TableName+"(").length(),KeyField+",");
						KeyValue = new StringBuffer(SysUtility.GetUUID());
						fields.insert(0, "'"+KeyValue.toString()+"',");
					}else if(SysUtility.IsMySqlDB(conn) &&  columnType == Types.VARCHAR){
						sql.insert(("Insert Into "+TableName+"(").length(),KeyField+",");
						KeyValue = new StringBuffer(SysUtility.GetUUID());
						fields.insert(0, "'"+KeyValue.toString()+"',");
					}
				}
				sql.append(") Values (");
				sql.append(fields.toString());
				sql.append(")");
				Object[] params = parms.toArray(new Object[parms.size()]);
				
				rt = ExecSQL(sql.toString(), params, KeyField, KeyValue, conn);
				row.put(KeyField, KeyValue.toString());
				
				LogTableHistory(SysUtility.INSERT, rt, TableName, KeyField, null, row);
				
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return rt;
	}

	@Override
	public boolean Update(String TableName, Object objs) throws LegendException{
		return Update(TableName, objs, SysUtility.KeyFieldDefault);
	}
	
	@Override
	public boolean Update(String TableName, Object objs, String KeyField) throws LegendException{
		return Update(TableName, objs, KeyField,GetActiveCN());
	}

	@Override //默认为Oracle实现
	public boolean Update(String TableName, Object objs, String KeyField, Connection conn) throws LegendException{
		boolean rt = false;
		List<String> ColumnNames = SysUtility.getTableColumnNames(conn, TableName);
		HashMap<String,Integer> ColumnTypes = SysUtility.getTableColumnTypes(conn, TableName);
		
		JSONArray Datas = new JSONArray();
		if(objs instanceof JSONArray){
			Datas = (JSONArray)objs;
		}else if(objs instanceof JSONObject){
			Datas.put((JSONObject)objs);
		}
		
		for(int i = 0 ; i<Datas.length();i++){
			try {
				JSONObject row = Datas.getJSONObject(i);
				
				
				Iterator<?> keys2 = row.keys();
				while(keys2.hasNext()){
					String key = keys2.next().toString();
					if(KeyField.equalsIgnoreCase(key)){
						Object value = row.get(key);
						if(SysUtility.isEmpty(value)) {
							SetInsertDefault(row);
						}else {
							SetUpdateDefault(row);
						}
						break;
					}
				}
				
				StringBuilder updatesql = new StringBuilder();
				StringBuilder insertsql = new StringBuilder();
				StringBuilder fields = new StringBuilder();
				StringBuilder wstr = new StringBuilder();
				ArrayList<Object> uparms = new ArrayList<Object>();
				ArrayList<Object> iparms = new ArrayList<Object>();
				
				ArrayList<String> wparms = new ArrayList<String>();
				updatesql.append("Update ");
				updatesql.append(TableName);
				updatesql.append(" Set ");
				
				insertsql.append("Insert Into ");
				insertsql.append(TableName);
				insertsql.append("(");

				
				Iterator<?> keys = row.keys();
				String usp = "";
				String isp = "";
				String wlk = "";
				boolean isinsert = true;
				String rec_ver = "";
				
				while(keys.hasNext()){
					String key = keys.next().toString();
					if(!ColumnNames.contains(key.toUpperCase())){
						continue;//传入列在数据库表中不存在，自动忽略
					}
					if("REC_VER".equals(key.toUpperCase())){
						rec_ver = row.get(key).toString();
						continue;//版本号控制，特殊处理。
					}
					int columnType = ColumnTypes.get(key.toUpperCase());
					Object value = row.get(key);
					if(KeyField.equalsIgnoreCase(key)){
						if(SysUtility.isEmpty(value)){
							value = SysUtility.getJsonField(row, KeyField);
						}
						KeyField = key;
					}
					
					if(!KeyField.equals(key)){
						if(value.equals("")){
							updatesql.append(usp + key + "=null");							
						}else{
							if(columnType == Types.TIMESTAMP || columnType == Types.DATE){
								updatesql.append(usp + key + "=to_date(?,'yyyy-mm-dd hh24:mi:ss')");
							}else{
								updatesql.append(usp + key + "=?");
							}
							if(columnType == Types.BLOB){
								if(value instanceof byte[]){
									uparms.add((byte[])value);
								}else{//base64字符串
									uparms.add(value.toString().getBytes());
								}
							}else{
								uparms.add(value.toString());
							}
							//uparms.add(value.toString());
						}
						usp = ",";
					}else{
						if(value.equals("")){
							wstr.append(wlk + key + " is null");
						}else{
							if(columnType == Types.TIMESTAMP || columnType == Types.DATE){
								wstr.append(wlk + key + "=to_date(?,'yyyy-mm-dd hh24:mi:ss')");
							}else{
								wstr.append(wlk + key + "=?");
							}
							wparms.add(value.toString());
						}
						wlk = " AND ";
						if(value!=null && !value.equals("")){
							isinsert = false;
						}
					}
					if(!row.get(key).equals("")){
						insertsql.append(isp + key);
						if(columnType == Types.TIMESTAMP || columnType == Types.DATE){
							fields.append(isp + "to_date(?,'yyyy-mm-dd hh24:mi:ss')");
						}else{
							fields.append(isp + "?");	
						}
						if(columnType == Types.BLOB){
							iparms.add(value.toString().getBytes());
						}else{
							iparms.add(value.toString());
						}
//						iparms.add(value.toString());
						isp = ",";
					}
				}
				
				if(isinsert){
					/**增加sequence自动取值逻辑，sequence名称：seq_表名*/
					if(!row.has(KeyField) || SysUtility.isEmpty(row.get(KeyField))){
						if(SysUtility.IsOracleDB()){
							insertsql.insert(("Insert Into "+TableName+"(").length(),KeyField+",");
							if(TableName.length()<=26){
								fields.insert(0, "seq_"+TableName+".nextval,");
							}else{
								fields.insert(0,"seq_"+KeyField+".nextval,");
							}
						}
					}
					insertsql.append(") Values (");
					insertsql.append(fields.toString());
					insertsql.append(")");
					StringBuffer KeyValue = new StringBuffer();
					rt = ExecSQL(insertsql.toString(),(Object[]) iparms.toArray(new Object[iparms.size()]),KeyField,KeyValue,conn);
					row.put(KeyField, KeyValue.toString());
					
					LogTableHistory(SysUtility.INSERT, rt, TableName, KeyField, null, row);
				}else{
					uparms.addAll(wparms);
					if(SysUtility.isNotEmpty(rec_ver)){
						updatesql.append(",REC_VER = REC_VER + 1");
						updatesql.append(" Where ");
						updatesql.append(wstr);
						updatesql.append(" and REC_VER = "+rec_ver);
					}else{
						updatesql.append(" Where ");
						updatesql.append(wstr);
					}
					
					Datas datas = LogTableOldValue(TableName, KeyField, row);
					rt = ExecSQL(updatesql.toString(),(Object[]) uparms.toArray(new Object[uparms.size()]),null,new StringBuffer(),conn);
					LogTableHistory(SysUtility.UPDATE, rt, TableName, KeyField, datas, row);
					if(!rt){
						throw LegendException.getLegendException(ERR.DB_DATA_CONFLICT);
					}
				}
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			} catch(Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());				
			}
		}
		return rt;		
	}

	@Override
	public boolean Delete(String TableName, Object objs) throws LegendException{
		return Delete(TableName, objs, SysUtility.KeyFieldDefault);
	}
	
	@Override
	public boolean Delete(String TableName, Object objs, String KeyField) throws LegendException{
		return Delete(TableName, objs, KeyField, GetActiveCN());
	}

	@Override
	public boolean Delete(String TableName, Object objs, String KeyField, Connection conn) throws LegendException{
		boolean rt = false;
		
		JSONArray Datas = new JSONArray();
		if(objs instanceof JSONArray){
			Datas = (JSONArray)objs;
		}else if(objs instanceof JSONObject){
			Datas.put((JSONObject)objs);
		}
		
		for(int i = 0 ; i<Datas.length();i++){
			try {
				JSONObject row = Datas.getJSONObject(i);
				Iterator<?> keys =row.keys();
				while(keys.hasNext()){
					String key = keys.next().toString();
					if(key.equalsIgnoreCase(KeyField)){
						Datas datas = null;
						if(LogCopHistoryFlag){
							String SQL = "";
							if("indx".equalsIgnoreCase(key)){
								SQL = "SELECT * FROM "+TableName+" WHERE "+key+" = "+row.get(key)+"";
							}else{
								SQL = "SELECT * FROM "+TableName+" WHERE "+key+" = '"+row.get(key)+"'";
							}
							datas = GetTableDatas(TableName, SQL);
						}
						
						String deletesql = "";
						if("indx".equalsIgnoreCase(key)){
							deletesql = "Delete From "+TableName+" WHERE "+key+" = "+row.get(key)+"";
						}else{
							deletesql = "Delete From "+TableName+" WHERE "+key+" = '"+row.get(key)+"'";
						}
						rt = ExecSQL(deletesql, null, null, new StringBuffer(), conn);
						rt = true;
						
						if(LogCopHistoryFlag){					
							LogTableHistory(SysUtility.DELETE, rt, TableName, KeyField, datas, row);
						}
					}
				}
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			} catch (Exception e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());				
			}
		}
		return rt;		
	}
	
	@Override
	public boolean ExecSQL(String SQL) throws LegendException{
		return ExecSQL(SQL, null);
	}
	
	@Override
	public boolean ExecSQL(String SQL,Object parms) throws LegendException{
		return ExecSQL(SQL, parms, null);
	}

	public boolean ExecSQL(String SQL,Object parms,String KeyField) throws LegendException{
		return ExecSQL(SQL, parms, KeyField, new StringBuffer());
	}
	
	/** 默认为Oracle实现方式
	 * 执行SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @return 成功失败标识
	 * */
	public boolean ExecSQL(String SQL,Object parms,String KeyField,StringBuffer KeyValue) throws LegendException{
		return ExecSQL(SQL, parms, KeyField, KeyValue, GetActiveCN());
	}
	
	public boolean ExecSQL(String SQL,Object parms,String KeyField,StringBuffer KeyValue,Connection conn) throws LegendException{
		LogUtil.printLog(SQL, Level.INFO);
		long start = System.currentTimeMillis();
		
		PreparedStatement stmt = null;
		ResultSet keyset = null;
		int rec_count = 0;
		if(conn!=null){
			try {
				if(SysUtility.isEmpty(KeyField)){//更新或删除
					stmt = conn.prepareStatement(SQL);
					SysUtility.bindParams(stmt, parms);//绑定参数
					rec_count = stmt.executeUpdate();
				}else{//新增
					stmt = conn.prepareStatement(SQL, new String[] { KeyField.toUpperCase() });
					SysUtility.bindParams(stmt, parms);//绑定参数
					rec_count = stmt.executeUpdate();
					if(SysUtility.isEmpty(KeyValue)){
						try {
							keyset = stmt.getGeneratedKeys();
							if (keyset != null && keyset.next()) {
								KeyValue.append(keyset.getString(1));
								SetCurrentKey(keyset.getString(1).toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						SetCurrentKey(KeyValue.toString());
					}
				}
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage()+"\n"+"sql:"+SQL+"\n"+SysUtility.getBindParams(parms), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			} finally {
				SysUtility.closeResultSet(keyset);
				SysUtility.closeStatement(stmt);
			}
		}
		LogUtil.printLog("ExecSQL执行耗时 ：" + (System.currentTimeMillis() - start) + " ms!",Level.INFO);
		if(rec_count > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public Datas Exec(String SQL,Object[] params) throws LegendException{
		long start = System.currentTimeMillis();
		Datas datas = new Datas();
		CallableStatement ocs = null;
		try {
			String[] allParams = SQL.substring(SQL.indexOf("(")+1, SQL.indexOf(")")).split(",");
			for (int i = 0; i < allParams.length; i++) {
				String p = allParams[i];
				if(!p.trim().equals("?")){
					SQL = SQL.replace(p, "?");
				}
			}
			SQL = "{call " + SQL + "}";
			LogUtil.printLog(SQL, Level.INFO);
			
			ocs = (CallableStatement)GetActiveCN().prepareCall(SQL);
			
			int inParamsIndex = 0;
			for (int i = 1; i <= allParams.length; i++) {
				String p = allParams[i - 1];
				if(p.trim().equals("?")){
					ocs.setObject(i, params[inParamsIndex]);
					inParamsIndex++;
				}else{
					ocs.registerOutParameter(i, OracleTypes.CURSOR);
				}
			}
			ocs.execute();
			
			JSONObject dataJSON = new JSONObject();
			for (int i = 1; i <= allParams.length; i++) {
				String p = allParams[i - 1];
				if(!p.trim().equals("?")){
					ResultSet rs = (ResultSet) ocs.getObject(i);
					JSONObject tableData = SysUtility.ResToJSON(allParams[i-1].toString(), rs);
					Iterator<?> keys = tableData.keys();
					if(keys.hasNext()){
						String key = keys.next().toString();
						dataJSON.put(key, tableData.get(key));
					}
					SysUtility.closeResultSet(rs);
				}
			}
			datas.setDataJSON(dataJSON);
		} catch (SQLException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} finally {
			SysUtility.closeCallableStatement(ocs);
		}
		LogUtil.printLog("Exec执行耗时 ：" + (System.currentTimeMillis() - start) + " ms!",Level.INFO);
		return datas;
	}

	@Override
	public void Despose() throws LegendException{
		if(GetActiveCN() != null){
			try {
				GetActiveCN().close();
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
			SessionManager.setAttribute(SysUtility.ActiveCN, null);
		}
	}

	@Override
	public void setCurrentUser(JSONObject currentUser) {
		CurrentUser = currentUser;
	}
	
	@Override
	public void LogCopHistory(boolean Flag) {
		this.LogCopHistoryFlag = Flag;
	}
	/*******************************************************************/
	@Override
	public String GetCurrentKey() {
		return CurrentKey;
	}
	@Override
	public void SetCurrentKey(String CurrentKey) {
		this.CurrentKey = CurrentKey;
	}
	
	/************************私有方法不会被继承，子类无法使用*********************************/
	private Datas ExecQuerySQL(Connection ActiveCN,String TableName,String SQL,Object parms) throws LegendException{
		LogUtil.printLog(SQL, Level.INFO);
		long start = System.currentTimeMillis();
		Datas data = null;
		try {
			data = SQLExecUtils.query4Datas(ActiveCN, TableName, SQL, SQLExecUtils.parmsToSetter(parms));
		} catch (Exception e) {
			LogUtil.printLog("ExecQuerySQL执行报错："+e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} 
		LogUtil.printLog("ExecQuerySQL执行耗时 ：" + (System.currentTimeMillis() - start) + " ms!",Level.INFO);
		return data;
	}
	
	/*************************公共方法，继承给子类使用，子类按需重写************************************/
	/**
	 * 获取用户修改前的数据
	 * */
	public Datas LogTableOldValue(String TableName, String KeyField,JSONObject row) throws LegendException{
		if(!LogCopHistoryFlag){
			return null;
		}
		try {
			Iterator<?> keys = row.keys();
			Long indx = null;
			StringBuffer coulmns = new StringBuffer();
			while(keys.hasNext()){
				String key = keys.next().toString();
				coulmns.append(","+key);
				if(key.equalsIgnoreCase(KeyField)){
					indx = Long.valueOf(row.get(key).toString());
				}
			}
			String SQL = "SELECT "+coulmns.toString().substring(1)+" FROM "+TableName+" WHERE "+KeyField+" = ?";
			return GetTableDatas(TableName, SQL, new Long[]{indx});
		} catch (NumberFormatException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
	}
	
	/**
	 * 将修改的列拼接并保存到COP_HISTORY表中
	 * */
	public void LogTableHistory(String opType, boolean rt,String TableName, String KeyField,Datas datas, JSONObject row) throws LegendException{
		if(SysUtility.isEmpty(TableName) || !rt || !LogCopHistoryFlag){
			return;
		}
		try {
			if(opType.equals(SysUtility.UPDATE)){
				Long indx = Long.valueOf(row.get(KeyField).toString());
				//主键为空的数据不记录
				if(indx == null){
					return;
				}
				
				StringBuffer contentValue = new StringBuffer();
				contentValue.append("{\"rows\":[");
				//contentValue.append("{");
				String flk = "";
				boolean changeCoulmn = false;
				
				Iterator<?> rk = row.keys();
				while(rk.hasNext()){
					String key = rk.next().toString();
					Object obj = row.get(key);
					String newValue = "";
					if(obj instanceof Date){
						newValue = SysUtility.DataFormatStr((Date)obj);
					}else{
						newValue = obj.toString();
					}
					String oldValue = datas.GetTableValue(TableName, key);
					if(!oldValue.equals(newValue)){
						changeCoulmn = true;
						contentValue.append(flk + "{\"");
						contentValue.append("field");
						contentValue.append("\":\"");
						contentValue.append(key.replace("\"", "\\\""));
						contentValue.append("\"");
						flk = ",";
						contentValue.append(flk + "\"");
						contentValue.append("old");
						contentValue.append("\":\"");
						contentValue.append(oldValue.replace("\"", "\\\""));
						contentValue.append("\"");
						flk = ",";
						contentValue.append(flk + "\"");
						contentValue.append("new");
						contentValue.append("\":\"");
						contentValue.append(newValue.replace("\"", "\\\""));
						contentValue.append("\"}");
					}
				}
				//contentValue.append("}");
				contentValue.append("]}");
				if(changeCoulmn){
					JSONObject copHistory = new JSONObject();
					copHistory.put("TABLE_NAME", TableName);
					copHistory.put("TABLE_INDX", indx);
					copHistory.put("OP_TYPE", opType);
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    String time=format.format(new Date());
					copHistory.put("OP_DATE", time);
					if(null != CurrentUser && CurrentUser.has("User_Id")){
						copHistory.put("User_Id", CurrentUser.get("User_Id").toString());
					}
					if(null != CurrentUser && CurrentUser.has("User_Name")){
						copHistory.put("User_Name", CurrentUser.get("User_Name").toString());
					}
					int contentCount = contentValue.length() / 1000;
					for (int j = 1; j <= contentCount; j++) {
						String subValue = contentValue.substring(1000 * (j - 1), 1000 * j);
						copHistory.put("OP_CONTENT" + j, subValue);
					}
					copHistory.put("OP_CONTENT" + (contentCount + 1), contentValue.substring(contentCount * 1000, contentValue.length()));
					
					JSONArray copRows = new JSONArray();
					copRows.put(copHistory);
					LogCopHistory(false);
					Insert("COP_HISTORY", copRows);
				}
			}else if(opType.equals(SysUtility.DELETE)){
				//TODO 待扩展				
				Long indx = Long.valueOf(row.get(KeyField).toString());
				//主键为空的数据不记录
				if(indx == null){
					return;
				}
				
				StringBuffer contentValue = new StringBuffer();
				contentValue.append("{\"rows\":[");
				//contentValue.append("{");
				String flk = "";
				boolean changeCoulmn = false;
				
				JSONArray ls = datas.getDataJSON().getJSONArray(TableName);
				for(int i=0;i<ls.length();i++)
				{
					JSONObject jobj = (JSONObject)ls.get(i);  
					Iterator<?> rk = jobj.keys();
					while(rk.hasNext()){
						String key = rk.next().toString();
						Object obj = datas.GetTableValue(TableName, key);
						String Value = "";
						if(obj instanceof Date){
							Value = SysUtility.DataFormatStr((Date)obj);
						}else{
							Value = obj.toString();
						}
						changeCoulmn = true;	
						contentValue.append(flk + "{\"");
						contentValue.append("field");
						contentValue.append("\":\"");
						contentValue.append(key.replace("\"", "\\\""));
						contentValue.append("\"");
						flk = ",";
						contentValue.append(flk + "\"");
						contentValue.append("old");
						contentValue.append("\":\"");
						contentValue.append(Value.replace("\"", "\\\""));
						contentValue.append("\"}");
						flk = ",";					
					}
				}
				//contentValue.append("}");
				contentValue.append("]}");
				if(changeCoulmn){
					JSONObject copHistory = new JSONObject();
					copHistory.put("TABLE_NAME", TableName);
					copHistory.put("TABLE_INDX", indx);
					copHistory.put("OP_TYPE", opType);
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    String time=format.format(new Date());
					copHistory.put("OP_DATE", time);
					if(null != CurrentUser && CurrentUser.has("User_Id")){
						copHistory.put("User_Id", CurrentUser.get("User_Id").toString());
					}
					if(null != CurrentUser && CurrentUser.has("User_Name")){
						copHistory.put("User_Name", CurrentUser.get("User_Name").toString());
					}
					int contentCount = contentValue.length() / 1000;
					for (int j = 1; j <= contentCount; j++) {
						String subValue = contentValue.substring(1000 * (j - 1), 1000 * j);
						copHistory.put("OP_CONTENT" + j, subValue);
					}
					copHistory.put("OP_CONTENT" + (contentCount + 1), contentValue.substring(contentCount * 1000, contentValue.length()));
					
					JSONArray copRows = new JSONArray();
					copRows.put(copHistory);
					LogCopHistory(false);
					Insert("COP_HISTORY", copRows);
				}
			}else if(opType.equals(SysUtility.INSERT)){
				//TODO 待扩展
				Long indx = Long.valueOf(row.get(KeyField).toString());
				//主键为空的数据不记录
				if(indx == null){
					return;
				}
				
				StringBuffer contentValue = new StringBuffer();
				contentValue.append("{\"rows\":[");
				//contentValue.append("{");
				String flk = "";
				boolean changeCoulmn = false;
				
				Iterator<?> rk = row.keys();
				while(rk.hasNext()){
					String key = rk.next().toString();
					Object obj = row.get(key);
					String Value = "";
					if(obj instanceof Date){
						Value = SysUtility.DataFormatStr((Date)obj);
					}else{
						Value = obj.toString();
					}
					changeCoulmn = true;
					contentValue.append(flk + "{\"");
					contentValue.append("field");
					contentValue.append("\":\"");
					contentValue.append(key.replace("\"", "\\\""));
					contentValue.append("\"");
					flk = ",";
					contentValue.append(flk + "\"");
					contentValue.append("new");
					contentValue.append("\":\"");
					contentValue.append(Value.replace("\"", "\\\""));
					contentValue.append("\"}");
					flk = ",";
				}
				//contentValue.append("}");
				contentValue.append("]}");
				if(changeCoulmn){
					JSONObject copHistory = new JSONObject();
					copHistory.put("TABLE_NAME", TableName);
					copHistory.put("TABLE_INDX", indx);
					copHistory.put("OP_TYPE", opType);
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    String time=format.format(new Date());
					copHistory.put("OP_DATE", time);
					if(null != CurrentUser && CurrentUser.has("User_Id")){
						copHistory.put("User_Id", CurrentUser.get("User_Id").toString());
					}
					if(null != CurrentUser && CurrentUser.has("User_Name")){
						copHistory.put("User_Name", CurrentUser.get("User_Name").toString());
					}
					int contentCount = contentValue.length() / 1000;
					for (int j = 1; j <= contentCount; j++) {
						String subValue = contentValue.substring(1000 * (j - 1), 1000 * j);
						copHistory.put("OP_CONTENT" + j, subValue);
					}
					copHistory.put("OP_CONTENT" + (contentCount + 1), contentValue.substring(contentCount * 1000, contentValue.length()));
					
					JSONArray copRows = new JSONArray();
					copRows.put(copHistory);
					LogCopHistory(false);
					Insert("COP_HISTORY", copRows);
				}
			}
		} catch (NumberFormatException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		} catch (Exception e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
	}
	
	public JSONArray ToTree(JSONArray data, String idfield,String pidfield) {
		JSONArray rt = new JSONArray();
		JSONObject rows = new JSONObject();
		try {
			for (int i = 0; i < data.length(); i++) {
				JSONObject row = data.getJSONObject(i);
				JSONObject item = new JSONObject();
				Iterator itt = row.keys();  		      
				while (itt.hasNext()) {  					    	
				    String key = itt.next().toString();  
				    String value = row.getString(key);
				    item.put(key, value);
			    }
				if (row.has(pidfield)) {
					String pid = row.getString(pidfield);
					if (rows.has(pid)) {
						JSONObject p = rows.getJSONObject(pid);
						JSONArray children = null;
						if (p != null) {
							if (!p.has("children")) {
								children = new JSONArray();
								p.put("children", children);
							} else {
								children = p.getJSONArray("children");
							}
						}
						if (children != null) {
							children.put(item);
						}
					}
					else{
						rt.put(item);
					}
				} else {
					rt.put(item);
				}
				rows.put(row.getString(idfield), item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		rows = null;

		return rt;
	}
	public JSONArray ToTree(JSONArray data, String idfield,String pidfield1,String pidfield2) {
		JSONArray rt = new JSONArray();
		JSONObject rows = new JSONObject();
		try {
			for (int i = 0; i < data.length(); i++) {
				JSONObject row = data.getJSONObject(i);
				JSONObject item = new JSONObject();
				Iterator itt = row.keys();  		      
				while (itt.hasNext()) {  					    	
				    String key = itt.next().toString();  
				    String value = row.getString(key);
				    item.put(key, value);
			    }
				if (row.has(pidfield1)) {
					String pid = row.getString(pidfield1);
					if (rows.has(pid)) {
						JSONObject p = rows.getJSONObject(pid);
						JSONArray children = null;
						if (p != null) {
							if (!p.has("children")) {
								if (row.has(pidfield2)) {
									String pid1 = row.getString(pidfield2);
									if (rows.has(pid1)) {
										JSONObject p1 = rows.getJSONObject(pid1);
										JSONArray childrens = null;
										if (p1 != null) {
											if (!p1.has("children")) {
												childrens = new JSONArray();
												p1.put("children", childrens);
											} else {
												childrens = p1.getJSONArray("children");
											}
										}
										if (childrens != null) {
											childrens.put(item);
										}
									}
								} 
								children = new JSONArray();
								p.put("children", children);
							} else {
								children = p.getJSONArray("children");
							}
						}
						if (children != null) {
							children.put(item);
						}
					}
					else{
						rt.put(item);
					}
				} else {
					rt.put(item);
				}
				rows.put(row.getString(idfield), item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		rows = null;

		return rt;
	}
	
	public void SetInsertDefault(JSONObject row) {
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "is_enabled")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "IS_ENABLED"))) {
			SysUtility.putJsonField(row, "is_enabled", "1");
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "part_id")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "PART_ID"))) {
			SysUtility.putJsonField(row, "part_id", SysUtility.getCurrentPartId());
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "creator")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "CREATOR"))) {
			SysUtility.putJsonField(row, "creator", SysUtility.getCurrentUserIndx());
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "created_by_name")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "CREATED_BY_NAME"))) {
			SysUtility.putJsonField(row, "created_by_name", SysUtility.getCurrentUserName());
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "create_time")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "CREATE_TIME"))) {
			SysUtility.putJsonField(row, "create_time", SysUtility.getSysDate());
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "modifyor")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "MODIFYOR"))) {
			SysUtility.putJsonField(row, "modifyor", SysUtility.getCurrentUserIndx());
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "modify_time")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "MODIFY_TIME"))) {
			SysUtility.putJsonField(row, "modify_time", SysUtility.getSysDate());
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "rec_ver")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "REC_VER"))) {
			SysUtility.putJsonField(row, "rec_ver", "0");
		}
	}
	
	public void SetUpdateDefault(JSONObject row) {
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "modifyor")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "MODIFYOR"))) {
			SysUtility.putJsonField(row, "modifyor", SysUtility.getCurrentUserIndx());
		}
		if(SysUtility.isEmpty(SysUtility.getJsonField(row, "modify_time")) && SysUtility.isEmpty(SysUtility.getJsonField(row, "MODIFY_TIME"))) {
			SysUtility.putJsonField(row, "modify_time", SysUtility.getSysDate());
		}
	}
}
