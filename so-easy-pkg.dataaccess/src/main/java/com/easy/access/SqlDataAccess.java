package com.easy.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.exception.LegendException;
import com.easy.session.SessionManager;
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
public class SqlDataAccess extends AbstractDataAccess {
	
	public SqlDataAccess() {
		CurrentKey = "";
		Connection ActiveCN = SysUtility.getCurrentConnection();
		SessionManager.setAttribute(SysUtility.ActiveCN, ActiveCN);
	}
	
	public SqlDataAccess(String DBName) {
		CurrentKey = "";
		Connection ActiveCN = SysUtility.getCurrentConnection(DBName);
		SessionManager.setAttribute(SysUtility.ActiveCN, ActiveCN);
	}
	
	@Override
	public Connection GetActiveCN() throws LegendException{
		return super.GetActiveCN();
	}
	
	@Override
	public boolean BeginTrans() throws LegendException{
		return super.BeginTrans();
	}

	@Override
	public boolean ComitTrans() throws LegendException{
		return super.ComitTrans();
	}

	@Override
	public boolean RoolbackTrans() throws LegendException{
		return super.RoolbackTrans();
	}

	@Override
	public String GetTable(String SQL) throws LegendException{
		return super.GetTable(SQL);
	}
	
	@Override
	public String GetTable(String TableName, String SQL) throws LegendException{
		return super.GetTable(TableName, SQL);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms) throws LegendException{
		return super.GetTable(TableName, SQL, parms);
	}
	
	@Override
	public String GetTable(String SQL, int page, int pagesize) throws LegendException{
		return super.GetTable(SQL, page, pagesize);
	}

	@Override
	public String GetTable(String TableName, String SQL, int page, int pagesize) throws LegendException{
		return super.GetTable(TableName, SQL, page, pagesize);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms,int page, int pagesize) throws LegendException{
		return super.GetTable(TableName, SQL, parms, page, pagesize);
	}
	
	@Override
	public String GetTable(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return super.GetTable(TableName, SQL, page, pagesize, KeyField);
	}

	@Override
	public String GetTable(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException{
		return super.GetTable(TableName, SQL, parms, page, pagesize, KeyField);
	}
		
	@Override
	public JSONObject GetTableJSON(String TableName, String SQL) throws LegendException{
		return super.GetTableJSON(TableName, SQL);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, Object parms) throws LegendException{
		return super.GetTableJSON(TableName, SQL, parms);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, int page,int pagesize) throws LegendException{
		return super.GetTableJSON(TableName, SQL, page, pagesize);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL,Object parms, int page, int pagesize) throws LegendException{
		return super.GetTableJSON(TableName, SQL, page, pagesize);
	}

	@Override
	public JSONObject GetTableJSON(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return super.GetTableJSON(TableName, SQL, page, pagesize, KeyField);
	}
	
	@Override
	public JSONObject GetTableJSON(String TableName, String SQL,Object parms, int page, int pagesize, String KeyField) throws LegendException{
		return super.GetTableJSON(TableName, SQL, parms, page, pagesize, KeyField);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL) throws LegendException{
		return super.GetTableDatas(TableName, SQL);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, Object parms) throws LegendException{
		return super.GetTableDatas(TableName, SQL, parms);
	}
	
	@Override
	public Datas GetTableDatas(String TableName,String SQL,int page,int pagesize) throws LegendException{
		return super.GetTableDatas(TableName, SQL, page, pagesize);
	}
	
	@Override
	public Datas GetTableDatas(String TableName,String SQL,Object parms,int page,int pagesize) throws LegendException{
		return super.GetTableDatas(TableName, SQL, parms, page, pagesize);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, int page,int pagesize, String KeyField) throws LegendException{
		return super.GetTableDatas(TableName, SQL, page, pagesize, KeyField);
	}
	
	@Override
	public Datas GetTableDatasUI(String TableName, String SQL, Object parms,int page, int pagesize, String KeyField,JSONArray footer) throws LegendException{
		return super.GetTableDatasUI(TableName, SQL, parms, page,  pagesize,  KeyField, footer);
	}
	
	@Override
	public Datas GetTableDatas(String TableName, String SQL, Object parms, int page, int pagesize, String key) throws LegendException{
		String nSQL = "select * from (select top " + ((page + 1) * pagesize) + " * from (" + SQL + ") T ) t2 where not " + key + " in (select " + key + " from (select top " + (page * pagesize) + " * from (" + SQL + ") T ) T1)";;
		Object nparms = SysUtility.paramDouble(parms);
		Datas data = GetTableDatas(TableName,nSQL,nparms);
		JSONObject rt = data.getDataJSON();
		int rc = GetRowCount(SQL,parms);
		try {
			rt.put("ROWCOUNT", rc);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
			throw LegendException.getLegendException(e.getMessage());
		}
		return data;
	}
	
	@Override
	public int GetRowCount(String SQL) throws LegendException{
		return super.GetRowCount(SQL);
	}

	@Override
	public int GetRowCount(String SQL, Object parms) throws LegendException{
		String countSQL = "Select count(*) as [ROWCOUNT] from (" + SQL + ") as T";
		Datas data = GetTableDatas("R", countSQL, parms);
		String rc = data.GetTableValue("R.ROWCOUNT");
		return Integer.parseInt(rc);
	}
	
	@Override
	public boolean Exists(String SQL)throws LegendException{
		return super.Exists(SQL);
	}
	
	@Override
	public boolean Exists(String SQL, Object parms)throws LegendException{
		return super.Exists(SQL, parms);
	}

	@Override
	public boolean Insert(String TableName, Object objs) throws LegendException {
		return super.Insert(TableName, objs);
	}

	@Override
	public boolean Insert(String TableName, Object objs, String KeyField) throws LegendException {
		return super.Insert(TableName, objs, KeyField);
	}

	@Override
	public boolean Insert(String TableName, Object objs, String KeyField, Connection conn) throws LegendException{
		boolean rt = false;
		if (SysUtility.isEmpty(KeyField)) {
			KeyField = SysUtility.KeyFieldDefault;
		}
		JSONArray Datas = new JSONArray();
		if(objs instanceof JSONArray){
			Datas = (JSONArray)objs;
		}else if(objs instanceof JSONObject){
			Datas.put((JSONObject)objs);
		}
		List<String> ColumnNames = SysUtility.getTableColumnNames(GetActiveCN(), TableName);
		for (int i = 0; i < Datas.length(); i++) {
			try {
				JSONObject row = Datas.getJSONObject(i);
				StringBuilder sql = new StringBuilder();
				StringBuilder fields = new StringBuilder();
				ArrayList<String> parms = new ArrayList<String>();
				sql.append("Insert Into ");
				sql.append(TableName);
				sql.append("(");
				Iterator<?> keys = row.keys();
				String sp = "";
				while (keys.hasNext()) {
					String key = keys.next().toString();
					if(!ColumnNames.contains(key.toUpperCase())){
						continue;//传入列在数据库表中不存在，自动忽略
					}
					if (SysUtility.isNotEmpty(row.getString(key))) {
						sql.append(sp + key);
						Object value = row.get(key);
						if (value.equals("")) {
							fields.append(sp + "null");
						} else {
							fields.append(sp + "?");
							if (value instanceof Date) {
								parms.add((new SimpleDateFormat(
										"yyyy-MM-dd hh:mm:ss")).format(value));
							} else {
								parms.add(value.toString());
							}
						}
						sp = ",";
					}
				}

				sql.append(") Values (");
				sql.append(fields.toString());
				sql.append(")");
				/**增加sequence自动取值逻辑，sequence名称：seq_表名*/
				if(!row.has(KeyField) || SysUtility.isEmpty(row.getString(KeyField))){
					sql.append(" Select IDENT_CURRENT('" + TableName + "')");
				}

				rt = ExecSQL(sql.toString(),(String[]) parms.toArray(new String[parms.size()]),null);
				if (row.has(KeyField)) {
					row.put(KeyField, CurrentKey);
				}
			} catch (JSONException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			}
		}
		return rt;
	}

	@Override
	public boolean Update(String TableName, Object objs) throws LegendException{
		return super.Update(TableName, objs);
	}
	
	@Override
	public boolean Update(String TableName, Object objs,String KeyField) throws LegendException{
		return super.Update(TableName, objs, KeyField);
	}

	@Override
	public boolean Update(String TableName, Object objs, String KeyField, Connection conn) throws LegendException{
		boolean rt = false;
		List<String> ColumnNames = SysUtility.getTableColumnNames(GetActiveCN(), TableName);
		
		JSONArray Datas = new JSONArray();
		if(objs instanceof JSONArray){
			Datas = (JSONArray)objs;
		}else if(objs instanceof JSONObject){
			Datas.put((JSONObject)objs);
		}
		
		for (int i = 0; i < Datas.length(); i++) {
			try {
				JSONObject row = Datas.getJSONObject(i);
				StringBuilder updatesql = new StringBuilder();
				StringBuilder insertsql = new StringBuilder();
				StringBuilder fields = new StringBuilder();
				StringBuilder wstr = new StringBuilder();
				ArrayList<String> uparms = new ArrayList<String>();
				ArrayList<String> iparms = new ArrayList<String>();

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
				while (keys.hasNext()) {
					String key = keys.next().toString();
					if(!ColumnNames.contains(key.toUpperCase())){
						continue;//传入列在数据库表中不存在，自动忽略
					}
					Object value = row.get(key);
					if(KeyField.equalsIgnoreCase(key)){
						KeyField = key;
					}
					
					if(!KeyField.equals(key)){
						if (value.equals("")) {
							updatesql.append(usp + key + "=null");
						} else {
							updatesql.append(usp + key + "=?");
							if (value instanceof Date) {
								uparms.add((new SimpleDateFormat(
										"yyyy-MM-dd hh:mm:ss")).format(value));
							} else {
								uparms.add(value.toString());
							}
						}
						usp = ",";
					} else {
						if (value.equals("")) {
							wstr.append(wlk + key + " is null");
						} else {
							wstr.append(wlk + key + "=?");
							if (value instanceof Date) {
								wparms.add((new SimpleDateFormat(
										"yyyy-MM-dd hh:mm:ss")).format(value));
							} else {
								wparms.add(value.toString());
							}
						}
						wlk = " AND ";
						if (value != null && !value.equals("")) {
							isinsert = false;
						}
						CurrentKey = value.toString();
					}
					if (!row.getString(key).equals("")) {
						insertsql.append(isp + key);
						fields.append(isp + "?");
						if (value instanceof Date) {
							iparms.add((new SimpleDateFormat(
									"yyyy-MM-dd hh:mm:ss")).format(value));
						} else {
							iparms.add(value.toString());
						}
						isp = ",";
					}
				}
				if (isinsert) {
					insertsql.append(") Values (");
					insertsql.append(fields.toString());
					insertsql.append(")");

					if (!row.has(KeyField) || SysUtility.isEmpty(row.getString(KeyField))) {
						insertsql.append(" Select IDENT_CURRENT('" + TableName + "')");
					}
					rt = ExecSQL(insertsql.toString(),(String[]) iparms.toArray(new String[iparms.size()]),null);
					row.put(KeyField, CurrentKey);
				} else {
					uparms.addAll(wparms);
					updatesql.append(" Where ");
					updatesql.append(wstr);
					Datas datas = LogTableOldValue(TableName, KeyField, row);
					rt = ExecSQL(updatesql.toString(),(String[]) uparms.toArray(new String[uparms.size()]));
					LogTableHistory(SysUtility.UPDATE, rt, TableName, KeyField, datas, row);
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
	public boolean Delete(String TableName, Object objs) throws LegendException{
		return Delete(TableName, objs, SysUtility.KeyFieldDefault);
	}
	
	@Override
	public boolean Delete(String TableName, Object objs, String KeyField) throws LegendException{
		return Delete(TableName, objs, KeyField, GetActiveCN());
	}

	@Override
	public boolean Delete(String TableName, Object objs, String KeyField, Connection conn) throws LegendException{
		return super.Delete(TableName, objs, KeyField, conn);
	}
	
	@Override
	public boolean ExecSQL(String SQL) throws LegendException{
		return super.ExecSQL(SQL);
	}
	
	@Override
	public boolean ExecSQL(String SQL,Object parms) throws LegendException{
		return super.ExecSQL(SQL, parms);
	}

	@Override
	public Datas Exec(String SQL,Object[] params) throws LegendException{
		return super.Exec(SQL, params);
	}

	@Override
	public void Despose() throws LegendException{
		super.Despose();
	}

	@Override
	public void setCurrentUser(JSONObject currentUser) {
		super.setCurrentUser(currentUser);
	}
	
	@Override
	public void LogCopHistory(boolean Flag) {
		super.LogCopHistory(Flag);
	}
	
	@Override
	public String GetCurrentKey() {
		return super.GetCurrentKey();
	}
	
	/**
	 * 执行SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @return 成功失败标识
	 * */
	public boolean ExecSQL(String SQL,Object parms,String KeyField) throws LegendException{
		LogUtil.printLog("SQL:"+SQL, Level.INFO);
		boolean rt = false;
		PreparedStatement stmt = null;
		ResultSet keyset = null;
		if (GetActiveCN() != null) {
			try {
				stmt = GetActiveCN().prepareStatement(SQL,Statement.RETURN_GENERATED_KEYS);
				SysUtility.bindParams(stmt, parms);//绑定参数
				rt = stmt.execute();
				keyset = stmt.getGeneratedKeys();
				if (keyset != null) {
					keyset.next();
					CurrentKey = keyset.getString(1);
				}
				rt = true;
			} catch (SQLException e) {
				LogUtil.printLog(e.getMessage(), Level.ERROR);
				throw LegendException.getLegendException(e.getMessage());
			} finally {
				SysUtility.closeResultSet(keyset);
				SysUtility.closeStatement(stmt);
			}
		}
		return rt;
	}
}
